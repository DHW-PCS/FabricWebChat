package org.dhwpcs.webchat.network.handler;

import net.minecraft.text.Text;

public interface LifecyclePacketHandler extends PacketHandler {
    void sendMessage(Text text);
    void logout();

    default HandlerStage getStage() {
        return HandlerStage.LIFECYCLE;
    }
}
