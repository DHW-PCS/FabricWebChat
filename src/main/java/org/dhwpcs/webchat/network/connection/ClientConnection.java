package org.dhwpcs.webchat.network.connection;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.dhwpcs.webchat.network.protocol.Protocol;
import org.dhwpcs.webchat.network.protocol.invoker.ServerNetworkInvoker;
import org.dhwpcs.webchat.network.protocol.packet.InboundPacket;
import org.dhwpcs.webchat.network.protocol.packet.OutboundPacket;
import org.dhwpcs.webchat.session.ChatSession;
import org.dhwpcs.webchat.task.SimpleTaskHandler;
import org.dhwpcs.webchat.task.TaskHandler;
import org.dhwpcs.webchat.util.Tickable;

public class ClientConnection extends SimpleChannelInboundHandler<InboundPacket> implements Tickable {
    private ChatSession session;
    private Channel channel;
    private ServerNetworkInvoker server;
    private TaskHandler tasks;
    private boolean tickable;
    private final ConnectionInvoker invoker = new ConnectionInvoker(this);

    public void setProtocol(Protocol proto) {
        tasks = new SimpleTaskHandler();
        server = proto.createServerInvoker(this);
        tickable = true;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        channel = ctx.channel();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        tickable = false;
        tasks.shutdown();
        channel = null;
        session = null;
    }

    public ChannelFuture sendPacket(OutboundPacket packet) {
        return channel.writeAndFlush(packet);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, InboundPacket inbound) {
        inbound.handle(this);
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

    public TaskHandler getTaskHandler() {
        return tasks;
    }

    public ConnectionInvoker getInvoker() {
        return invoker;
    }

    public void tick() {
        if(tickable) {
            if (channel.eventLoop().inEventLoop()) {
                tasks.tick();
            } else channel.eventLoop().execute(tasks::tick);
        }
    }
}
