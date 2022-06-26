package org.dhwpcs.webchat.network;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketCloseStatus;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import org.dhwpcs.webchat.network.protocol.Protocol;
import org.dhwpcs.webchat.session.ClientConnection;

public class Handshaker extends SimpleChannelInboundHandler<WebSocketFrame> {

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
    protected void channelRead0(ChannelHandlerContext ctx, WebSocketFrame frame) {
        if(!established) {
            if(frame instanceof TextWebSocketFrame txt) {
                String json = txt.text();
                JsonElement element = JsonParser.parseString(json);
                protocol = validator.validate(element);
                if(protocol == null) {
                    ctx.writeAndFlush(new CloseWebSocketFrame(WebSocketCloseStatus.ABNORMAL_CLOSURE,
                            "UnsupportedProtocolVersion"))
                            .addListener(ChannelFutureListener.CLOSE);
                }
                ctx.writeAndFlush(new TextWebSocketFrame(protocol.onHandshakeSuccess().toString()));
                established = true;
            }
        } else {
            ctx.fireChannelRead(frame);
        }
    }
}
