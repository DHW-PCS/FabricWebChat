package org.dhwpcs.webchat.network.protocol.v1r0.packet;

import com.google.gson.JsonObject;
import org.dhwpcs.webchat.network.protocol.packet.OutboundPacket;
import org.dhwpcs.webchat.session.ResumeFailedReason;

public class CPacketResumeFailed implements OutboundPacket {

    private static final String SESSION_OCCUPIED = "session_occupied";
    private static final String SESSION_EXPIRED = "session_expired";

    private final String reason;

    public CPacketResumeFailed(ResumeFailedReason reason) {
        this.reason = switch (reason) {
            case SESSION_EXPIRED -> SESSION_EXPIRED;
            case SESSION_OCCUPIED -> SESSION_OCCUPIED;
        };
    }

    @Override
    public void serialize(JsonObject payload) {
        payload.addProperty("reason", reason);
    }
}
