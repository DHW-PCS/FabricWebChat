package org.dhwpcs.webchat.network.protocol.v1r0.packet;

import com.google.gson.JsonObject;
import org.dhwpcs.webchat.network.protocol.packet.InboundPacket;
import org.dhwpcs.webchat.network.protocol.v1r0.tasks.LoginState;
import org.dhwpcs.webchat.network.protocol.v1r0.tasks.LoginTask;
import org.dhwpcs.webchat.session.ChatSession;
import org.dhwpcs.webchat.session.ClientConnection;
import org.dhwpcs.webchat.session.LoginFailedReason;

import java.util.Optional;

public class SPacketLoginConfirm implements InboundPacket {
    @Override
    public void deserialize(JsonObject payload) {}

    @Override
    public void handle(ClientConnection connection) {
        Optional<LoginTask> tsk = connection.getTaskHandler().querySingleton(LoginTask.class);
        if(tsk.isPresent()) {
            LoginTask task = tsk.get();
            if(task.isDone()) {
                connection.sendPacket(new CPacketLoginFailed(LoginFailedReason.ALREADY_LOGGED_IN));
            } else if(task.getState() != LoginState.ACCOUNT_SET) {
                connection.sendPacket(new CPacketLoginFailed(LoginFailedReason.ACCOUNT_NOT_SPECIFIED));
            }
            ChatSession session = task.confirm();
            if(session == null) {
                connection.sendPacket(new CPacketLoginFailed(LoginFailedReason.UNKNOWN));
            } else {
                connection.sendPacket(new CPacketLoginSuccess());
                session.sendInfo();
            }
        } else {
            connection.sendPacket(new CPacketLoginFailed(LoginFailedReason.NOT_LOGGING_IN));
        }
    }
}
