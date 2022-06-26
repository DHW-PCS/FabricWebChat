package org.dhwpcs.webchat.network.protocol.invoker;

import net.minecraft.text.Text;
import org.dhwpcs.webchat.session.ClientConnection;

import java.util.UUID;

public interface ClientNetworkInvoker {
    ClientConnection getConnection();
    void login(String account, String pwd);
    void sendMessage(Text text, long sendTime);
    void logout();
    void resume(UUID sessionId);
    void disconnect();
}
