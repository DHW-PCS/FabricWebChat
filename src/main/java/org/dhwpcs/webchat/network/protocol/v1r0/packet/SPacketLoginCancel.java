package org.dhwpcs.webchat.network.protocol.v1r0.packet;

import com.google.gson.JsonObject;
import org.dhwpcs.webchat.network.exception.NetworkException;
import org.dhwpcs.webchat.network.protocol.packet.InboundPacket;
import org.dhwpcs.webchat.network.protocol.v1r0.tasks.LoginTask;
import org.dhwpcs.webchat.network.connection.ClientConnection;

import java.util.Optional;

public class SPacketLoginCancel implements InboundPacket {

    @Override
    public void deserialize(JsonObject payload) throws NetworkException {}

    @Override
    public void handle(ClientConnection connection) {
        Optional<LoginTask> tsk = connection.getTaskHandler().querySingleton(LoginTask.class);
        if(tsk.isPresent()) {
            LoginTask exact = tsk.get();
            if(!exact.isDone()) {
                exact.cancel();
            }
        }
    }
}
