package org.dhwpcs.webchat.network.protocol.invoker;

import net.minecraft.text.Text;
import org.dhwpcs.webchat.session.ClientConnection;
import org.dhwpcs.webchat.session.DisconnectReason;

import java.util.UUID;

public interface ServerNetworkInvoker {
    ClientConnection getConnection();
    boolean sendInfo();
    boolean pushMessage(UUID sender, Text text);

    void disconnect(DisconnectReason reason);
}
