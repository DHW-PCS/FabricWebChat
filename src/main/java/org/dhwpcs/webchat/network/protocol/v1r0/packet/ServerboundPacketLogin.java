package org.dhwpcs.webchat.network.protocol.v1r0.packet;

import com.google.gson.JsonObject;
import org.dhwpcs.webchat.network.exception.NetworkException;
import org.dhwpcs.webchat.network.handler.HandlerStage;
import org.dhwpcs.webchat.network.handler.PacketHandler;
import org.dhwpcs.webchat.network.protocol.packet.InboundPacket;

public class ServerboundPacketLogin implements InboundPacket {
    @Override
    public void deserialize(JsonObject payload) throws NetworkException {

    }

    @Override
    public void handle(PacketHandler handler) {
        if(handler.getStage() == HandlerStage.AUTH) {

        }
    }

}
