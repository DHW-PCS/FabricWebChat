package org.dhwpcs.webchat.network.protocol.v1r0.packet;

import com.google.gson.JsonObject;
import org.dhwpcs.webchat.network.protocol.packet.OutboundPacket;
import org.dhwpcs.webchat.session.SendFailedReason;

public class CPacketSendFailed implements OutboundPacket {

    public static final String NOT_LOGIN = "not_login";

    private final String reason;

    public CPacketSendFailed(SendFailedReason reason) {
        this.reason = switch (reason) {
            case NOT_LOGGED_IN -> NOT_LOGIN;
        };
    }

    @Override
    public void serialize(JsonObject payload) {
        payload.addProperty("reason", reason);
    }
}
