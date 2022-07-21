package org.dhwpcs.webchat.server;

import com.google.common.base.Preconditions;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketFrameAggregator;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.dhwpcs.webchat.server.data.AccountInfo;
import org.dhwpcs.webchat.server.data.AccountInfoSerde;
import org.dhwpcs.webchat.network.Handshaker;
import org.dhwpcs.webchat.network.PacketCodec;
import org.dhwpcs.webchat.network.connection.ConnectionManager;
import org.dhwpcs.webchat.network.connection.ClientConnection;
import org.dhwpcs.webchat.server.psi.ChatListener;
import org.dhwpcs.webchat.server.psi.GameServerApi;
import org.dhwpcs.webchat.server.session.SessionManager;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Function;

public class WebChatServer {

    public static WebChatServer INSTANCE;
    public static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(AccountInfo.class, new AccountInfoSerde())
            .create();

    private static final AtomicInteger bossCount = new AtomicInteger(0);
    private static final AtomicInteger childCount = new AtomicInteger(0);

    public static final ThreadFactory BOSS = r -> new Thread(r, "WebChat-Netty-BossGroup-"+bossCount.getAndIncrement());
    public static final ThreadFactory CHILD = r -> new Thread(r, "WebChat-Netty-WorkerGroup-"+childCount.getAndIncrement());
    public static final ThreadFactory EVENT_LOOP = r -> new Thread(r, "WebChat-EventExecutor");

    private final MinecraftServer mcServer;

    private boolean available = false;
    private Channel httpServer;

    private final SessionManager sessions = new SessionManager();
    private final ConnectionManager connections = new ConnectionManager();
    private final ScheduledExecutorService asyncSingle = Executors.newSingleThreadScheduledExecutor(EVENT_LOOP);

    private GameServerApi gameServerApi;
    private Future<Map<String, AccountInfo>> userRegistry;
    private Future<?> userRegistrySaving;

    private WebChatServer(MinecraftServer server) {
        this.mcServer = server;
    }

    public static void initialize(MinecraftServer server, GameServerApi prod) {
        INSTANCE = new WebChatServer(server);
        INSTANCE.start();
        prod.registerChatListener(new WebchatChatListener(INSTANCE));
        prod.registerTickable(INSTANCE.sessions);
        prod.registerTickable(INSTANCE.connections);
        INSTANCE.gameServerApi = prod;
    }

    public void start() {
        Class<? extends ServerSocketChannel> channelClass;
        if(Epoll.isAvailable() && mcServer.isUsingNativeTransport()) {
            channelClass = EpollServerSocketChannel.class;
        } else {
            channelClass = NioServerSocketChannel.class;
        }

        this.httpServer = new ServerBootstrap()
                .group(new NioEventLoopGroup(BOSS), new NioEventLoopGroup(CHILD))
                .channel(channelClass)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
            @Override
            protected void initChannel(NioSocketChannel channel) {
                ChannelPipeline pipeline = channel.pipeline();
                ClientConnection connection = connections.createConnection();
                PacketCodec codec = new PacketCodec();
                Handshaker handshaker = new Handshaker(codec, connection);
                pipeline.addLast("http-codec", new HttpServerCodec());
                pipeline.addLast("chunked-write", new ChunkedWriteHandler());
                pipeline.addLast("aggregator", new HttpObjectAggregator(65536));
                pipeline.addLast("log-handler", new LoggingHandler(LogLevel.DEBUG));
                pipeline.addLast("websocket-handler", new WebSocketServerProtocolHandler("/chat/backend"));
                pipeline.addLast("websocket-aggregator", new WebSocketFrameAggregator(65536));
                pipeline.addLast("handshaker", handshaker);
                pipeline.addLast("packet-codec", codec);
                pipeline.addLast("connection-handler", connection);
            }
        }).bind(80).syncUninterruptibly().channel();
        userRegistry = asyncSingle.submit(() -> {
            Path pth = mcServer.getFile("webchat_registries.json").toPath();
            if(!Files.exists(pth)) {
                Files.writeString(pth, "{}");
                return new HashMap<>();
            } else {
                try (BufferedReader registries = Files.newBufferedReader(pth)) {
                    return GSON.fromJson(registries, new TypeToken<Map<String, AccountInfo>>() {
                    }.getType());
                } catch (IOException e) {
                    throw new RuntimeException("Could not read user registries!", e);
                }
            }
        });
        available = true;
    }

    public static void stop(MinecraftServer unused) {
        if(INSTANCE == null) {
            throw new IllegalStateException("Server is not booted");
        } else {
            Preconditions.checkState(INSTANCE.available);
            INSTANCE.sessions.terminate();
            INSTANCE.httpServer.close();
            INSTANCE.available = false;
        }
    }

    public SessionManager getSessions() {
        return sessions;
    }

    public Future<Map<String, AccountInfo>> getUserRegistry() {
        return userRegistry;
    }

    public GameServerApi getGame() {
        return gameServerApi;
    }

    static class WebchatChatListener implements ChatListener {

        private final WebChatServer parent;

        public WebchatChatListener(WebChatServer parent) {
            this.parent = parent;
        }

        @Override
        public void onMessage(UUID sender, Text message) {
            parent.asyncSingle.submit(() -> parent.getSessions().broadcast(sender, message));
        }
    }

    public<T> T run(Function<WebChatServer, T> runner) {
        return runner.apply(this);
    }
}
