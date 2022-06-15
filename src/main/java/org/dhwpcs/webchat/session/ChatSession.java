package org.dhwpcs.webchat.session;

import net.minecraft.text.BaseText;
import org.dhwpcs.webchat.network.protocol.v1r0.packet.ClientboundPacketMessage;

import java.util.UUID;

public class ChatSession {

    private ClientConnection connection;
    private UUID accountUID;

    public void pushMessage(UUID sender, BaseText text) {
        if(connection.)
        connection.sendPacket(new ClientboundPacketMessage(sender, text));
    }

    public void connect(ClientConnection connection) {
        this.connection = connection;
    }

    public void disconnect() {
        this.connection = null;
    }

}
