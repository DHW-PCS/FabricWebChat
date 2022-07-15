package org.dhwpcs.webchat.network.protocol.v1r0.packet;

import com.google.gson.JsonObject;
import org.dhwpcs.webchat.network.exception.NetworkException;
import org.dhwpcs.webchat.network.protocol.packet.OutboundPacket;

public class CPacketSendSuccess implements OutboundPacket {
    @Override
    public void serialize(JsonObject payload) throws NetworkException {}
}
