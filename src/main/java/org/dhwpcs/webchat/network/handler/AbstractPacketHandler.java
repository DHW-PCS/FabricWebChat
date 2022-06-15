package org.dhwpcs.webchat.network.handler;

import org.dhwpcs.webchat.session.ClientConnection;

public abstract class AbstractPacketHandler implements PacketHandler {

    protected final ClientConnection connection;

    public AbstractPacketHandler(ClientConnection connection) {
        this.connection = connection;
    }

    @Override
    public ClientConnection getConnection() {
        return connection;
    }
}
