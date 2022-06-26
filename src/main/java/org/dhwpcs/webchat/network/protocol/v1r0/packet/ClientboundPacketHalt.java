package org.dhwpcs.webchat.network.protocol.v1r0.packet;

import com.google.gson.JsonObject;
import org.dhwpcs.webchat.network.protocol.packet.OutboundPacket;
import org.dhwpcs.webchat.session.DisconnectReason;

public class ClientboundPacketHalt implements OutboundPacket {

    public static final String SERVER_STOPPING = "server_stopping";

    private final String type;
    private final String extra;

    public ClientboundPacketHalt(String type, String extra) {
        this.type = type;
        this.extra = extra;
    }

    public ClientboundPacketHalt(String type) {
        this.type = type;
        this.extra = null;
    }

    public ClientboundPacketHalt(DisconnectReason reason) {
        this(switch (reason) {
            case SERVER_STOPPING -> SERVER_STOPPING;
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
