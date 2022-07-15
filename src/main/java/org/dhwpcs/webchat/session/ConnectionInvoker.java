package org.dhwpcs.webchat.session;

import io.netty.channel.ChannelFuture;
import org.dhwpcs.webchat.network.protocol.packet.OutboundPacket;

public class ConnectionInvoker {
    private final ClientConnection connection;

    public ConnectionInvoker(ClientConnection connection) {
        this.connection = connection;
    }

    public ChannelFuture sendPacket(OutboundPacket packet) {
        return connection.sendPacket(packet);
    }
}
