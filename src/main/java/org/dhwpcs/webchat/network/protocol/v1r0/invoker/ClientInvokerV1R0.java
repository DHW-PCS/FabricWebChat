package org.dhwpcs.webchat.network.protocol.v1r0.invoker;

import net.minecraft.text.Text;
import org.dhwpcs.webchat.network.protocol.invoker.ClientNetworkInvoker;
import org.dhwpcs.webchat.network.protocol.invoker.ClientNetworkInvokerBase;

import java.util.UUID;

public class ClientInvokerV1R0 extends ClientNetworkInvokerBase implements ClientNetworkInvoker {

    @Override
    public void login(String account, String pwd) {

    }

    @Override
    public void sendMessage(Text text, long sendTime) {

    }

    @Override
    public void logout() {

    }

    @Override
    public void resume(UUID sessionId) {

    }

    @Override
    public void disconnect() {

    }
}
