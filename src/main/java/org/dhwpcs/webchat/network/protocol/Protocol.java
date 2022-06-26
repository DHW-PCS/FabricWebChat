package org.dhwpcs.webchat.network.protocol;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.dhwpcs.webchat.network.exception.NetworkException;
import org.dhwpcs.webchat.network.protocol.invoker.ClientNetworkInvoker;
import org.dhwpcs.webchat.network.protocol.invoker.ServerNetworkInvoker;
import org.dhwpcs.webchat.network.protocol.packet.InboundPacket;
import org.dhwpcs.webchat.network.protocol.packet.OutboundPacket;
import org.dhwpcs.webchat.network.protocol.packet.Packet;
import org.dhwpcs.webchat.session.ClientConnection;

import java.util.function.Supplier;

public interface Protocol {

    JsonElement serialize(OutboundPacket pkt) throws NetworkException;
    InboundPacket deserialize(JsonElement je) throws NetworkException;

    ClientNetworkInvoker createClientInvoker(ClientConnection connection);
    ServerNetworkInvoker createServerInvoker(ClientConnection connection);

    default JsonElement onHandshakeSuccess() {
        JsonObject $ = new JsonObject();
        $.addProperty("handshake", "success");
        return $;
    }
}
