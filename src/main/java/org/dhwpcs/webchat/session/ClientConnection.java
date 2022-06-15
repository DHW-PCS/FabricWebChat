package org.dhwpcs.webchat.session;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.dhwpcs.webchat.network.handler.PacketHandler;
import org.dhwpcs.webchat.network.handler.ServerAuthPacketHandler;
import org.dhwpcs.webchat.network.protocol.packet.InboundPacket;
import org.dhwpcs.webchat.network.protocol.packet.OutboundPacket;

import java.util.LinkedList;
import java.util.List;

public class ClientConnection extends SimpleChannelInboundHandler<InboundPacket> {
    private ChatSession session;
    private Channel channel;
    private ConnectionState state;
    private PacketHandler handler;
    private boolean writable = false;
    private List<OutboundPacket> traffic = new LinkedList<>();

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        channel = ctx.channel();
        handler = new ServerAuthPacketHandler(this);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        channel = null;
        handler.disconnect();
    }

    @Override
    public void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception {
        writable = ctx.channel().isWritable();
    }

    public void sendPacket(OutboundPacket packet) {
        if(!writable) {
            traffic.add(packet);
        } else channel.eventLoop().execute(() -> this.writeOrCache(packet));
    }

    private void writeOrCache(OutboundPacket packet) {
        if(channel.eventLoop().inEventLoop()) {
            if(channel.isWritable()) {
                channel.writeAndFlush(packet);
            } else {
                traffic.add(packet);
            }
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, InboundPacket inbound) throws Exception {
        inbound.handle(handler);
    }

    public ConnectionState getState() {
        return state;
    }

    public void setState(ConnectionState state) {
        this.state = state;
    }

    public ChatSession getSession() {
        return session;
    }

    public void bindSession(ChatSession session) {
        this.session = session;
        this.state = ConnectionState.ESTABLISHED;
    }

    public void unbind() {
        if(this.session != null) {
            this.session.disconnect();
            this.session = null;
            this.state = ConnectionState.NOT_AUTHENTICATED;
        }
    }

    public void halt() {
        if(this.session != null) {
            this.state = ConnectionState.WAIT;
        } else {
            this.state = ConnectionState.TERMINATED;
        }
    }
}
