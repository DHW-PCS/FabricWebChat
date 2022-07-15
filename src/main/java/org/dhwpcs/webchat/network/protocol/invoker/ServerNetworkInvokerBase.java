package org.dhwpcs.webchat.network.protocol.invoker;

import org.dhwpcs.webchat.session.ClientConnection;

public abstract class ServerNetworkInvokerBase implements ServerNetworkInvoker {
    protected ClientConnection connection;

    public ServerNetworkInvokerBase bindConnection(ClientConnection connection) {
        this.connection = connection;
        return this;
    }
}
