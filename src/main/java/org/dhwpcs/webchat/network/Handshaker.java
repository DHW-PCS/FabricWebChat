package org.dhwpcs.webchat.network;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketCloseStatus;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import org.dhwpcs.webchat.network.protocol.Protocol;
import org.dhwpcs.webchat.network.connection.ClientConnection;

public class Handshaker extends ChannelInboundHandlerAdapter {

    private final PacketCodec codec;
    private final ClientConnection connection;
    private final Validator validator = Validator.DEFAULT;
    private Protocol protocol;
    private boolean established = false;

    public Handshaker(PacketCodec codec, ClientConnection connection) {
        this.codec = codec;
        this.connection = connection;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object frame) {
        System.out.println(frame);
        if(!(frame instanceof WebSocketFrame)) {
            throw new IllegalArgumentException();
        }
        if(!established) {
            try {
                if (frame instanceof TextWebSocketFrame txt) {
                    String json = txt.text();
                    JsonElement element = JsonParser.parseString(json);
                    protocol = validator.validate(element);
                    if (protocol == null) {
                        ctx.writeAndFlush(new CloseWebSocketFrame(WebSocketCloseStatus.ABNORMAL_CLOSURE,
                                        "UnsupportedProtocolVersion"))
                                .addListener(ChannelFutureListener.CLOSE);
                    }
                    ctx.writeAndFlush(new TextWebSocketFrame(protocol.onHandshakeSuccess().toString()));
                    codec.setProtocol(protocol);
                    connection.setProtocol(protocol);
                    established = true;
                } else {
                    ctx.writeAndFlush(new TextWebSocketFrame(protocol.onHandshakeFailed("TextWebSocketFrame is required.").toString()));
                }
            } catch (Exception ex) {
                ctx.writeAndFlush(new TextWebSocketFrame(protocol.onHandshakeFailed(ex.getMessage()).toString()));
            }
        } else {
            ctx.fireChannelRead(frame);
        }
    }
}
