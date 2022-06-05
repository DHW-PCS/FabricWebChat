package org.dhwpcs.webchat.network;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import org.dhwpcs.webchat.network.protocol.packet.Packet;
import org.dhwpcs.webchat.network.protocol.v1r0.ProtocolV1R0;

import java.util.List;


public class PacketCodec extends MessageToMessageCodec<WebSocketFrame, Packet> {
    private final Validator validator = new Validator();

    public static final PacketCodec INSTANCE = new PacketCodec();

    private PacketCodec() {
        validator.register("1.0", ProtocolV1R0::new);
    }

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Packet packet, List<Object> list) throws Exception {

    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, WebSocketFrame webSocketFrame, List<Object> list) throws Exception {

    }

    @Override
    public boolean isSharable() {
        return true;
    }
}
