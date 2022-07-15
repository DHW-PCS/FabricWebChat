package org.dhwpcs.webchat.network.protocol.v1r0.packet;

import com.google.gson.JsonObject;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.dhwpcs.webchat.network.protocol.packet.InboundPacket;
import org.dhwpcs.webchat.session.ChatSession;
import org.dhwpcs.webchat.session.ClientConnection;

public class SPacketSend implements InboundPacket {

    private MutableText content;

    @Override
    public void deserialize(JsonObject payload) {
        JsonObject text = payload.getAsJsonObject("message");
        content = Text.Serializer.fromJson(text);
    }

    @Override
    public void handle(ClientConnection connection) {
        ChatSession session = connection.getSession();
        session.getManager().broadcast(session.getInfo().uid(), content);
        connection.sendPacket(new CPacketSendSuccess());
    }
}
