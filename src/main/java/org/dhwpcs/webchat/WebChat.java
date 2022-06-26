package org.dhwpcs.webchat;

import com.google.common.base.Preconditions;
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
import org.checkerframework.checker.units.qual.C;
import org.dhwpcs.webchat.network.Handshaker;
import org.dhwpcs.webchat.network.PacketCodec;
import org.dhwpcs.webchat.session.ClientConnection;
import org.dhwpcs.webchat.session.SessionManager;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WebChat {

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

        available = true;
    }

    public void stop() throws InterruptedException {
        Preconditions.checkState(available);
        sessions.terminate();
        httpServer.close().sync();
    }
}
