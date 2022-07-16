package org.dhwpcs.webchat.network.protocol.v1r0.packet;

import com.google.gson.JsonObject;
import org.dhwpcs.webchat.WebChat;
import org.dhwpcs.webchat.network.protocol.packet.InboundPacket;
import org.dhwpcs.webchat.session.ChatSession;
import org.dhwpcs.webchat.network.connection.ClientConnection;
import org.dhwpcs.webchat.session.ResumeFailedReason;

import java.util.UUID;

public class SPacketResume implements InboundPacket {

    private UUID session = null;

    @Override
    public void deserialize(JsonObject payload) {
        session = UUID.fromString(payload.getAsJsonPrimitive("session").getAsString());
    }

    @Override
    public void handle(ClientConnection connection) {
        if(session != null) {
            ChatSession chatSession = WebChat.INSTANCE.getSessions().acquireSession(session);
            if(chatSession == null || chatSession.isDead()) {
                connection.sendPacket(new CPacketResumeFailed(ResumeFailedReason.SESSION_EXPIRED));
            } else if(chatSession.isKeeping()) {
                chatSession.connect(connection);
                connection.sendPacket(new CPacketResumeSuccess());
            } else if(chatSession.isAlive()) {
                connection.sendPacket(new CPacketResumeFailed(ResumeFailedReason.SESSION_OCCUPIED));
            }
        }
    }
}
