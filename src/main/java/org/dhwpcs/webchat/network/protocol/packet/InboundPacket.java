package org.dhwpcs.webchat.network.protocol.packet;

import com.google.gson.JsonObject;
import org.dhwpcs.webchat.network.exception.NetworkException;
import org.dhwpcs.webchat.network.connection.ClientConnection;

public interface InboundPacket extends Packet {
    void deserialize(JsonObject payload) throws NetworkException;
    void handle(ClientConnection connection);
}
