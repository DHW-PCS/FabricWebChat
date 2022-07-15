package org.dhwpcs.webchat.network.protocol.v1r0.packet;

import com.google.gson.JsonObject;
import org.dhwpcs.webchat.network.exception.NetworkException;
import org.dhwpcs.webchat.network.protocol.packet.InboundPacket;
import org.dhwpcs.webchat.session.ClientConnection;
import org.dhwpcs.webchat.network.protocol.v1r0.tasks.LoginTask;
import org.dhwpcs.webchat.session.LoginFailedReason;

public class SPacketLogin implements InboundPacket {

    private String account;
    private String password;

    @Override
    public void deserialize(JsonObject payload) throws NetworkException {
        account = payload.getAsJsonPrimitive("account").getAsString();
        password = payload.getAsJsonPrimitive("password").getAsString();
    }

    @Override
    public void handle(ClientConnection connection) {
        if(connection.getSession() != null) {
            connection.sendPacket(new CPacketLoginFailed(LoginFailedReason.ALREADY_LOGGED_IN));
            return;
        }
        LoginTask tsk = new LoginTask();
        if(!connection.getTaskHandler().registerSingleton(tsk)) {
            connection.sendPacket(new CPacketLoginFailed(LoginFailedReason.ALREADY_LOGGING_IN));
        }
        switch (tsk.account(account, password)) {
            case WRONG_PASSWORD_OR_ACCOUNT -> connection.sendPacket(new CPacketLoginFailed(LoginFailedReason.WRONG_ACCOUNT_OR_PASSWORD));
            case SUCCESS -> connection.sendPacket(new CPacketSession(tsk.getAccountInfo()));
        }
    }

}
