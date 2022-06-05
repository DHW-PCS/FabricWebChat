package org.dhwpcs.webchat.network.protocol;

import com.google.gson.JsonElement;
import org.dhwpcs.webchat.network.exception.NetworkException;
import org.dhwpcs.webchat.network.protocol.packet.InboundPacket;
import org.dhwpcs.webchat.network.protocol.packet.OutboundPacket;
import org.dhwpcs.webchat.network.protocol.packet.Packet;

public interface Protocol {
    JsonElement serialize(OutboundPacket pkt) throws NetworkException;
    InboundPacket deserialize(JsonElement je) throws NetworkException;
}
