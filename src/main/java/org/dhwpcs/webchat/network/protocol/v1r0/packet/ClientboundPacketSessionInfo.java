package org.dhwpcs.webchat.network.protocol.v1r0.packet;

import com.google.gson.JsonObject;
import org.dhwpcs.webchat.network.exception.NetworkException;
import org.dhwpcs.webchat.network.protocol.packet.OutboundPacket;

import java.util.UUID;

public class ClientboundPacketSessionInfo implements OutboundPacket {

    private final UUID uuid;

    public ClientboundPacketSessionInfo(UUID uuid) {
        this.uuid = uuid;
    }

    @Override
    public void serialize(JsonObject payload) throws NetworkException {
        payload.addProperty("uuid", uuid.toString());
    }
}
