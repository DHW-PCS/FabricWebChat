package org.dhwpcs.webchat.network.protocol.v1r0.packet;

import com.google.gson.JsonObject;
import org.dhwpcs.webchat.network.protocol.packet.InboundPacket;
import org.dhwpcs.webchat.network.connection.ClientConnection;

public class SPacketLogout implements InboundPacket {
    @Override
    public void deserialize(JsonObject payload) {}

    @Override
    public void handle(ClientConnection connection) {
        connection.getSession().logout();
        connection.setSession(null);
    }
}
