package org.dhwpcs.webchat.network.protocol.invoker;

import io.netty.channel.ChannelFuture;
import net.minecraft.text.Text;
import org.dhwpcs.webchat.data.AccountInfo;
import org.dhwpcs.webchat.session.DisconnectReason;

import java.util.UUID;

public interface ServerNetworkInvoker {
    ChannelFuture sendInfo(AccountInfo info);
    ChannelFuture pushMessage(UUID sender, Text text);

    void disconnect(DisconnectReason reason);
}
