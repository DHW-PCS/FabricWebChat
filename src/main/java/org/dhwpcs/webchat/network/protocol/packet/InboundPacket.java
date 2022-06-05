package org.dhwpcs.webchat.network.protocol.packet;

import com.google.gson.JsonObject;
import org.dhwpcs.webchat.network.exception.NetworkException;

public interface InboundPacket extends Packet {
    void deserialize(JsonObject payload) throws NetworkException;
}
