package org.dhwpcs.webchat;

import com.google.common.base.Preconditions;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketFrameAggregator;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;
import org.dhwpcs.webchat.data.AccountInfo;
import org.dhwpcs.webchat.data.AccountInfoSerde;
import org.dhwpcs.webchat.network.Handshaker;
import org.dhwpcs.webchat.network.PacketCodec;
import org.dhwpcs.webchat.server.ChatListener;
import org.dhwpcs.webchat.server.GameServerApi;
import org.dhwpcs.webchat.session.ClientConnection;
import org.dhwpcs.webchat.session.SessionManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;

public class WebChat {

    public static WebChat INSTANCE;
    public static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(AccountInfo.class, new AccountInfoSerde())
            .create();

    private final MinecraftServer mcServer;

    private boolean available = false;
    private Channel httpServer;

    private final SessionManager sessions = new SessionManager();
    private final ScheduledExecutorService asyncSingle = Executors.newSingleThreadScheduledExecutor(r -> new Thread(r, "WebChat Worker Thread"));

    private GameServerApi gameServerApi;
    private Future<Map<String, AccountInfo>> userRegistry;

    private WebChat(MinecraftServer server) {
        this.mcServer = server;
    }

    public static void initialize(MinecraftServer server, GameServerApi prod) {
        INSTANCE = new WebChat(server);
        INSTANCE.start();
        prod.registerChatListener(new WebchatChatListener(INSTANCE));
        prod.registerTickRunnable(INSTANCE.sessions::update);
        INSTANCE.gameServerApi = prod;
    }

    public void start() {
        Class<? extends ServerSocketChannel> channelClass;
        if(Epoll.isAvailable() && mcServer.isUsingNativeTransport()) {
            channelClass = EpollServerSocketChannel.class;
        } else {
            channelClass = NioServerSocketChannel.class;
        }

        this.httpServer = new ServerBootstrap().channel(channelClass).childHandler(new ChannelInitializer<ServerSocketChannel>() {
            @Override
            protected void initChannel(ServerSocketChannel channel) throws Exception {
                ChannelPipeline pipeline = channel.pipeline();
                ClientConnection connection = new ClientConnection();
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
            try (BufferedReader registries = Files.newBufferedReader(pth)) {
                return GSON.fromJson(registries, new TypeToken<Map<String, AccountInfo>>(){}.getType());
            } catch (IOException e) {
                throw new RuntimeException("Could not read user registries!", e);
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
            try {
                INSTANCE.httpServer.close().sync();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            INSTANCE.available = false;
        }
    }

    public SessionManager getSessions() {
        return sessions;
    }

    public Future<Map<String, AccountInfo>> getUserRegistry() {
        return userRegistry;
    }

    static class WebchatChatListener implements ChatListener {

        private final WebChat parent;

        public WebchatChatListener(WebChat parent) {
            this.parent = parent;
        }

        @Override
        public void onMessage(UUID sender, Text message) {
            parent.asyncSingle.submit(() -> parent.getSessions().broadcast(sender, message));
        }
    }
}
