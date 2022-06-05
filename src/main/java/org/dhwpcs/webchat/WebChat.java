package org.dhwpcs.webchat;

import com.mojang.authlib.*;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.authlib.yggdrasil.YggdrasilEnvironment;
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
import org.dhwpcs.webchat.network.PacketCodec;
import org.dhwpcs.webchat.session.ChatSession;
import org.dhwpcs.webchat.session.SessionManager;

import java.io.IOException;
import java.net.Proxy;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WebChat {

    private final Environment yggdrasilEnv = YggdrasilEnvironment.PROD.getEnvironment();
    private final AuthenticationService yggdrasilAuth = new YggdrasilAuthenticationService(Proxy.NO_PROXY);
    private final GameProfileRepository yggdrasilRepo = yggdrasilAuth.createProfileRepository();
    private final UserAuthentication yggdrasilUser = yggdrasilAuth.createUserAuthentication(Agent.MINECRAFT);

    private final MinecraftServer mcServer;

    private final ExecutorService executor = Executors.newFixedThreadPool(4);
    private boolean available = false;
    private Channel httpServer;

    private final SessionManager sessions = new SessionManager(this);

    public WebChat(MinecraftServer server) throws IOException {
        this.mcServer = server;
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
                pipeline.addLast("http-codec", new HttpServerCodec());
                pipeline.addLast("chunked-write", new ChunkedWriteHandler());
                pipeline.addLast("aggregator", new HttpObjectAggregator(65536));
                pipeline.addLast("log-handler", new LoggingHandler(LogLevel.DEBUG));
                pipeline.addLast("websocket-handler", new WebSocketServerProtocolHandler("/chat/backend"));
                pipeline.addLast("websocket-aggregator", new WebSocketFrameAggregator(65536));
                pipeline.addLast("packet-codec", PacketCodec.INSTANCE);
                pipeline.addLast("session-handler", new ChatSession());
            }
        }).bind(80).syncUninterruptibly().channel();

        available = true;
    }
}
