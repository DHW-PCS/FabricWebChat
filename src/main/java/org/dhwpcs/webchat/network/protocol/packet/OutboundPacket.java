package org.dhwpcs.webchat.network.protocol.packet;

import com.google.gson.JsonObject;
import org.dhwpcs.webchat.network.exception.NetworkException;

public interface OutboundPacket extends Packet{
    void serialize(JsonObject payload) throws NetworkException;
}
