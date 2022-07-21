package org.dhwpcs.webchat.network.protocol.v1r0.packet;

import com.google.gson.JsonObject;
import org.dhwpcs.webchat.server.data.AccountInfo;
import org.dhwpcs.webchat.network.exception.NetworkException;
import org.dhwpcs.webchat.network.protocol.packet.OutboundPacket;

public class CPacketSession implements OutboundPacket {

    private final AccountInfo info;

    public CPacketSession(AccountInfo info) {
        this.info = info;
    }

    @Override
    public void serialize(JsonObject payload) throws NetworkException {
        payload.addProperty("uuid", info.uid().toString());
        payload.addProperty("name", info.name());
    }
}
