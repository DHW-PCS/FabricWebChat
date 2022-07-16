package org.dhwpcs.webchat.network.protocol;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.dhwpcs.webchat.network.exception.NetworkException;
import org.dhwpcs.webchat.network.protocol.invoker.ServerNetworkInvoker;
import org.dhwpcs.webchat.network.protocol.packet.InboundPacket;
import org.dhwpcs.webchat.network.protocol.packet.OutboundPacket;
import org.dhwpcs.webchat.network.connection.ClientConnection;

public interface Protocol {

    JsonElement serialize(OutboundPacket pkt) throws NetworkException;
    InboundPacket deserialize(JsonElement je) throws NetworkException;

    ServerNetworkInvoker createServerInvoker(ClientConnection connection);

    default JsonElement onHandshakeSuccess() {
        JsonObject $ = new JsonObject();
        $.addProperty("handshake", "success");
        return $;
    }

    default JsonElement onHandshakeFailed(String reason) {
        JsonObject $ = new JsonObject();
        $.addProperty("handshake", "failed");
        $.addProperty("reason", reason);
        return $;
    }
}
