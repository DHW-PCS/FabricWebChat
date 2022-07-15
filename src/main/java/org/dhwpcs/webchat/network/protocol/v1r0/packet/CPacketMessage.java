package org.dhwpcs.webchat.network.protocol.v1r0.packet;

import com.google.gson.JsonObject;
import net.minecraft.text.Text;
import org.dhwpcs.webchat.network.protocol.packet.OutboundPacket;

import java.util.UUID;

public class CPacketMessage implements OutboundPacket {

    private final Text text;
    private final UUID uuid;

    public CPacketMessage(UUID sender, Text text) {
        this.uuid = sender;
        this.text = text;
    }

    @Override
    public void serialize(JsonObject payload) {
        payload.addProperty("sender", uuid.toString());
        payload.add("message", Text.Serializer.toJsonTree(text));
    }
}
