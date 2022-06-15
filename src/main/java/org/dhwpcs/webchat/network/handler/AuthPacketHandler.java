package org.dhwpcs.webchat.network.handler;

import java.util.UUID;

public interface AuthPacketHandler extends PacketHandler {
    void login(String account, String password);
    void selectProfile(UUID profile);
    void resume(UUID session);

    default HandlerStage getStage() {
        return HandlerStage.AUTH;
    }
}
