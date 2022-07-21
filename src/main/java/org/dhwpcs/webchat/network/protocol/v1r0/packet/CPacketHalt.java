package org.dhwpcs.webchat.network.protocol.v1r0.packet;

import com.google.gson.JsonObject;
import org.dhwpcs.webchat.network.protocol.packet.OutboundPacket;
import org.dhwpcs.webchat.server.session.DisconnectReason;

public class CPacketHalt implements OutboundPacket {

    public static final String SERVER_STOPPING = "server_stopping";
    public static final String SERVER_OPERATION = "server_operation";

    private final String type;
    private final String extra;

    public CPacketHalt(String type, String extra) {
        this.type = type;
        this.extra = extra;
    }

    public CPacketHalt(String type) {
        this.type = type;
        this.extra = null;
    }

    public CPacketHalt(DisconnectReason reason) {
        this(switch (reason) {
            case SERVER_STOPPING -> SERVER_STOPPING;
            case SERVER_OPERATION -> SERVER_OPERATION;
        });
    }

    @Override
    public void serialize(JsonObject payload) {
        payload.addProperty("type", type);
        if(extra != null) {
            payload.addProperty("extra", extra);
        }
    }
}
