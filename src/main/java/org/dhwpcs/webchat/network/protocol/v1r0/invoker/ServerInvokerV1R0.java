package org.dhwpcs.webchat.network.protocol.v1r0.invoker;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import net.minecraft.text.Text;
import org.dhwpcs.webchat.data.AccountInfo;
import org.dhwpcs.webchat.network.protocol.invoker.ServerNetworkInvoker;
import org.dhwpcs.webchat.network.protocol.invoker.ServerNetworkInvokerBase;
import org.dhwpcs.webchat.network.protocol.v1r0.packet.CPacketHalt;
import org.dhwpcs.webchat.network.protocol.v1r0.packet.CPacketMessage;
import org.dhwpcs.webchat.network.protocol.v1r0.packet.CPacketSession;
import org.dhwpcs.webchat.session.DisconnectReason;

import java.util.UUID;

public class ServerInvokerV1R0 extends ServerNetworkInvokerBase implements ServerNetworkInvoker {

    @Override
    public ChannelFuture sendInfo(AccountInfo info) {
        return connection.sendPacket(new CPacketSession(info));
    }

    @Override
    public ChannelFuture pushMessage(UUID sender, Text text) {
        return connection.sendPacket(new CPacketMessage(sender, text));
    }

    @Override
    public void disconnect(DisconnectReason reason) {
        connection.sendPacket(new CPacketHalt(reason)).addListener(ChannelFutureListener.CLOSE);
    }
}
