package org.dhwpcs.webchat.network.protocol.v1r0;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.dhwpcs.webchat.network.exception.NetworkException;
import org.dhwpcs.webchat.network.protocol.packet.InboundPacket;
import org.dhwpcs.webchat.network.protocol.packet.OutboundPacket;
import org.dhwpcs.webchat.network.protocol.packet.Packet;
import org.dhwpcs.webchat.network.protocol.ProtocolBase;

public class ProtocolV1R0 extends ProtocolBase {
    @Override
    public JsonElement serialize(OutboundPacket pkt) throws NetworkException {
        String id = queryId(pkt.getClass());
        if (id == null) {
            throw new IllegalArgumentException("Not supported packet type: " + pkt.getClass().getName());
        }
        JsonObject jo = new JsonObject();
        jo.addProperty("action", id);
        JsonObject payload = new JsonObject();
        pkt.serialize(payload);
        jo.add("payload", payload);
        return jo;
    }

    @Override
    public InboundPacket deserialize(JsonElement je) throws NetworkException {
        if(je instanceof JsonObject $) {
            if($.has("action") && $.has("payload")) {
                String action = $.getAsJsonPrimitive("action").getAsString();
                JsonObject payload = $.getAsJsonObject("payload");
                Class<? extends Packet> clz = queryPacket(action);
                if(!InboundPacket.class.isAssignableFrom(clz)) {
                    throw new NetworkException("Trying to deserialize an non-inbound packet!");
                }
                InboundPacket pkt = (InboundPacket) createPacket(action);
                pkt.deserialize(payload);
                return pkt;
            }
        }
        throw new NetworkException("Not supported packet format: "+je.toString());
    }
}
