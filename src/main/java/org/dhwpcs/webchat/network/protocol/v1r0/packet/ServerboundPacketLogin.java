package org.dhwpcs.webchat.network.protocol.v1r0.packet;

import com.google.gson.JsonObject;
import org.dhwpcs.webchat.network.exception.NetworkException;
import org.dhwpcs.webchat.network.protocol.invoker.ClientNetworkInvoker;
import org.dhwpcs.webchat.network.protocol.packet.InboundPacket;

public class ServerboundPacketLogin implements InboundPacket {

    private String account;
    private String password;

    @Override
    public void deserialize(JsonObject payload) throws NetworkException {
        account = payload.getAsJsonPrimitive("account").getAsString();
        password = payload.getAsJsonPrimitive("password").getAsString();
    }

    @Override
    public void handle(ClientNetworkInvoker invoker) {
        invoker.login(account, password);
    }

}
