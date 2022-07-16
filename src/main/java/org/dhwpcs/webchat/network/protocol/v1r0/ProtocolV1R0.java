package org.dhwpcs.webchat.network.protocol.v1r0;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.dhwpcs.webchat.network.exception.NetworkException;
import org.dhwpcs.webchat.network.protocol.invoker.ServerNetworkInvoker;
import org.dhwpcs.webchat.network.protocol.packet.InboundPacket;
import org.dhwpcs.webchat.network.protocol.packet.OutboundPacket;
import org.dhwpcs.webchat.network.protocol.packet.Packet;
import org.dhwpcs.webchat.network.protocol.ProtocolBase;
import org.dhwpcs.webchat.network.protocol.v1r0.invoker.ServerInvokerV1R0;
import org.dhwpcs.webchat.network.protocol.v1r0.packet.*;
import org.dhwpcs.webchat.network.connection.ClientConnection;

public class ProtocolV1R0 extends ProtocolBase {

    public ProtocolV1R0() {
        registerInbound("lifecycle/login", SPacketLogin.class, SPacketLogin::new);
        registerInbound("lifecycle/login_confirm", SPacketLoginConfirm.class, SPacketLoginConfirm::new);
        registerInbound("chat/send", SPacketSend.class, SPacketSend::new);
        registerInbound("lifecycle/resume", SPacketResume.class, SPacketResume::new);
        registerInbound("lifecycle/logout", SPacketLogout.class, SPacketLogout::new);
        registerInbound("lifecycle/login_cancel", SPacketLoginCancel.class, SPacketLoginCancel::new);

        registerOutbound("lifecycle/halt", CPacketHalt.class);
        registerOutbound("lifecycle/login/failed", CPacketLoginFailed.class);
        registerOutbound("lifecycle/login/success", CPacketLoginSuccess.class);
        registerOutbound("lifecycle/logout/failed", CPacketLogoutFailed.class);
        registerOutbound("lifecycle/logout/success", CPacketLogoutSuccess.class);
        registerOutbound("lifecycle/resume/failed", CPacketResumeFailed.class);
        registerOutbound("lifecycle/resume/success", CPacketResumeSuccess.class);
        registerOutbound("chat/recv", CPacketMessage.class);
        registerOutbound("chat/send/failed", CPacketSendFailed.class);
        registerOutbound("chat/send/success", CPacketSendSuccess.class);
        registerOutbound("lifecycle/session_confirm", CPacketSession.class);
    }

    @Override
    public JsonElement serialize(OutboundPacket pkt) throws NetworkException {
        String id = queryId(pkt.getClass());
        JsonObject jo = new JsonObject();
        jo.addProperty("action", id);
        JsonObject payload = new JsonObject();
        pkt.serialize(payload);
        jo.add("payload", payload);
        return jo;
    }

    @Override
    public InboundPacket deserialize(JsonElement je) throws NetworkException {
        if(je instanceof JsonObject $) {
            if($.has("action") && $.has("payload")) {
                String action = $.getAsJsonPrimitive("action").getAsString();
                JsonObject payload = $.getAsJsonObject("payload");
                Class<? extends Packet> clz = queryPacket(action);
                if(!InboundPacket.class.isAssignableFrom(clz)) {
                    throw new NetworkException("Trying to deserialize an non-inbound packet!");
                }
                InboundPacket pkt = (InboundPacket) createPacket(action);
                pkt.deserialize(payload);
                return pkt;
            }
        }
        throw new NetworkException("Not supported packet format: "+je.toString());
    }

    @Override
    public ServerNetworkInvoker createServerInvoker(ClientConnection connection) {
        return new ServerInvokerV1R0().bindConnection(connection);
    }
}
