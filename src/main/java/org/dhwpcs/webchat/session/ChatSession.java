package org.dhwpcs.webchat.session;

import net.minecraft.text.BaseText;
import org.dhwpcs.webchat.network.protocol.packet.OutboundPacket;
import org.dhwpcs.webchat.network.protocol.packet.Packet;
import org.dhwpcs.webchat.network.protocol.v1r0.packet.ClientboundPacketMessage;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public class ChatSession {

    private List<OutboundPacket> traffic = new LinkedList<>();
    private ClientConnection connection;
    private UUID uid;

    public void pushMessage(UUID sender, BaseText text) {
        if(connection.)
        connection.sendPacket(new ClientboundPacketMessage(sender, text));
    }

    public void connect(ClientConnection connection) {
        this.connection = connection;
        if(traffic != null) {
            traffic.forEach(connection::sendPacket);
        }
    }

}
