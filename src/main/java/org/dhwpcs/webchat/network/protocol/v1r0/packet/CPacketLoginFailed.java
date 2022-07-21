package org.dhwpcs.webchat.network.protocol.v1r0.packet;

import com.google.gson.JsonObject;
import org.dhwpcs.webchat.network.exception.NetworkException;
import org.dhwpcs.webchat.network.protocol.packet.OutboundPacket;
import org.dhwpcs.webchat.server.session.LoginFailedReason;

public class CPacketLoginFailed implements OutboundPacket {

    public static final String WRONG_AC_OR_PWD = "wrong_account_or_pwd";
    public static final String ALREADY_LOGGED_IN = "already_logged_in";
    public static final String ALREADY_LOGGING_IN = "already_logging_in";
    public static final String NOT_LOGGING_IN = "not_logging_in";
    public static final String ACCOUNT_NOT_SPECIFIED = "account_not_specified";

    private final String reason;

    public CPacketLoginFailed(LoginFailedReason reason) {
        this.reason = switch (reason) {
            case WRONG_ACCOUNT_OR_PASSWORD -> WRONG_AC_OR_PWD;
            case ALREADY_LOGGING_IN -> ALREADY_LOGGING_IN;
            case NOT_LOGGING_IN -> NOT_LOGGING_IN;
            case ALREADY_LOGGED_IN -> ALREADY_LOGGED_IN;
            case ACCOUNT_NOT_SPECIFIED -> ACCOUNT_NOT_SPECIFIED;
            case UNKNOWN -> "我不知道";
        };
    }

    @Override
    public void serialize(JsonObject payload) throws NetworkException {
        payload.addProperty("reason", reason);
    }
}
