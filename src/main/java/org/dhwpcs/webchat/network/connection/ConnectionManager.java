package org.dhwpcs.webchat.network.connection;

import org.dhwpcs.webchat.util.Tickable;

import java.util.HashSet;
import java.util.Set;

public class ConnectionManager implements Tickable {
    private final Set<ClientConnection> connections = new HashSet<>();
    public ClientConnection createConnection() {
        ClientConnection connection = new ClientConnection();
        connections.add(connection);
        return connection;
    }

    @Override
    public void tick() {
        for(ClientConnection connection : connections) {
            connection.tick();
        }
    }
}
