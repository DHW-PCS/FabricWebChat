package org.dhwpcs.webchat.network.protocol.invoker;

import org.dhwpcs.webchat.session.ClientConnection;

public abstract class ClientNetworkInvokerBase implements ClientNetworkInvoker {
    protected ClientConnection connection;

    public ClientNetworkInvokerBase bindConnection(ClientConnection connection) {
        this.connection = connection;
        return this;
    }

    @Override
    public ClientConnection getConnection() {
        return connection;
    }
}
