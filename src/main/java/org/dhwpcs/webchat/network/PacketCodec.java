package org.dhwpcs.webchat.network;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import org.dhwpcs.webchat.network.protocol.Protocol;
import org.dhwpcs.webchat.network.protocol.packet.OutboundPacket;
import org.dhwpcs.webchat.network.protocol.packet.Packet;

import java.util.List;
public class PacketCodec extends MessageToMessageCodec<WebSocketFrame, Packet> {

    private Protocol protocol;

    public void setProtocol(Protocol protocol) {
        this.protocol = protocol;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, Packet packet, List<Object> list) throws Exception {
        if(packet instanceof OutboundPacket outbound) {
            list.add(new TextWebSocketFrame(protocol.serialize(outbound).toString()));
        }
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, WebSocketFrame frame, List<Object> list) throws Exception {
        if(frame instanceof TextWebSocketFrame txt) {
            String content = txt.text();
            JsonElement je = JsonParser.parseString(content);
            list.add(protocol.deserialize(je));
        }
    }

}
