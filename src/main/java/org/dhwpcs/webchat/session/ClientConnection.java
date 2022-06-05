package org.dhwpcs.webchat.session;

import com.google.common.base.Preconditions;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.dhwpcs.webchat.network.protocol.packet.OutboundPacket;
import org.dhwpcs.webchat.network.protocol.packet.Packet;
import org.dhwpcs.webchat.network.protocol.v1r0.packet.ServerboundPacketLogin;

public class ClientConnection extends SimpleChannelInboundHandler<Packet> {
    private final SessionManager manager;
    private Channel channel;
    private SessionState state;

    public ClientConnection(SessionManager manager) {
        this.manager = manager;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        channel = ctx.channel();
        state = SessionState.IDLE;
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        channel = null;
        state = SessionState.TERMINATE_WAIT;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Packet packet) throws Exception {
        if(packet instanceof ServerboundPacketLogin login) {
            login.
        }
    }

    public void sendPacket(OutboundPacket packet) {
        Preconditions.checkState(channel != null, "Underlying connection has been terminated.");
        if(channel.eventLoop().inEventLoop()) {
            channel.writeAndFlush(packet);
        } else channel.eventLoop().execute(() -> channel.writeAndFlush(packet));
    }
}
