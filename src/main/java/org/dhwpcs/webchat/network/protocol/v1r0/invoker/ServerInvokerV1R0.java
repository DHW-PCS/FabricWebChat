package org.dhwpcs.webchat.network.protocol.v1r0.invoker;

import io.netty.channel.ChannelFutureListener;
import net.minecraft.text.Text;
import org.dhwpcs.webchat.network.protocol.invoker.ServerNetworkInvoker;
import org.dhwpcs.webchat.network.protocol.invoker.ServerNetworkInvokerBase;
import org.dhwpcs.webchat.network.protocol.v1r0.packet.ClientboundPacketHalt;
import org.dhwpcs.webchat.session.ConnectionState;
import org.dhwpcs.webchat.session.DisconnectReason;

import java.util.UUID;

public class ServerInvokerV1R0 extends ServerNetworkInvokerBase implements ServerNetworkInvoker {
    @Override
    public boolean sendInfo() {
        return false;
    }

    @Override
    public boolean pushMessage(UUID sender, Text text) {
        return false;
    }

    @Override
    public void disconnect(DisconnectReason reason) {
        connection.sendPacket(new ClientboundPacketHalt(reason), ChannelFutureListener.CLOSE);
        connection.setState(ConnectionState.TERMINATED);
    }
}
