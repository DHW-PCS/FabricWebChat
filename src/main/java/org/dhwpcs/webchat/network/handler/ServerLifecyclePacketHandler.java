package org.dhwpcs.webchat.network.handler;

import net.minecraft.text.Text;
import org.dhwpcs.webchat.network.protocol.v1r0.packet.ClientboundPacketMessage;
import org.dhwpcs.webchat.session.ClientConnection;
import org.dhwpcs.webchat.session.ConnectionState;

public class ServerLifecyclePacketHandler extends AbstractPacketHandler implements LifecyclePacketHandler {

    public ServerLifecyclePacketHandler(ClientConnection connection) {
        super(connection);
    }

    @Override
    public void sendMessage(Text text) {
        connection.sendPacket(new ClientboundPacketMessage(connection.getSession()., text));
    }

    @Override
    public void logout() {
        connection.unbind();
    }

    @Override
    public void disconnect() {
        connection.halt();
    }

    @Override
    public PacketHandler nextStage() {
        return null;
    }
}