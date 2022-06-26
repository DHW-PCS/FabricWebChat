package org.dhwpcs.webchat.session;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import org.dhwpcs.webchat.network.protocol.Protocol;
import org.dhwpcs.webchat.network.protocol.invoker.ClientNetworkInvoker;
import org.dhwpcs.webchat.network.protocol.invoker.ServerNetworkInvoker;
import org.dhwpcs.webchat.network.protocol.packet.InboundPacket;
import org.dhwpcs.webchat.network.protocol.packet.OutboundPacket;

public class ClientConnection extends SimpleChannelInboundHandler<InboundPacket> {
    private ChatSession session;
    private Channel channel;
    private ConnectionState state;
    private ClientNetworkInvoker client;
    private ServerNetworkInvoker server;

    public void setProtocol(Protocol proto) {
        client = proto.createClientInvoker(this);
        server = proto.createServerInvoker(this);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        channel = ctx.channel();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        channel = null;
    }

    public void sendPacket(OutboundPacket packet, GenericFutureListener<? extends Future<? super Void>> listener) {
        channel.writeAndFlush(packet).addListener(listener);
    }

    public void sendPacket(OutboundPacket packet) {
        channel.writeAndFlush(packet);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, InboundPacket inbound) throws Exception {
        inbound.handle(client);
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

    public void setSession(ChatSession session) {
        this.session = session;
    }

    public ServerNetworkInvoker serverInvoker() {
        return server;
    }

    public Channel getChannel() {
        return channel;
    }
}
