package org.dhwpcs.webchat.network.handler;

import org.dhwpcs.webchat.session.ClientConnection;

import java.util.UUID;

public class ServerAuthPacketHandler extends AbstractPacketHandler implements AuthPacketHandler{

    public ServerAuthPacketHandler(ClientConnection connection) {
        super(connection);
    }

    @Override
    public void login(String account, String password) {

    }

    @Override
    public void selectProfile(UUID profile) {

    }

    @Override
    public void resume(UUID session) {

    }


    @Override
    public void disconnect() {

    }

    @Override
    public PacketHandler nextStage() {
        return new ServerLifecyclePacketHandler(connection);
    }
}
