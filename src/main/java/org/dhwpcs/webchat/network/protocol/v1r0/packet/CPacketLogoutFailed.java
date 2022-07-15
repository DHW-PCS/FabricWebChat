package org.dhwpcs.webchat.network.protocol.v1r0.packet;

import org.dhwpcs.webchat.session.LogoutFailedReason;

public class CPacketLogoutFailed {
    public static final String NOT_LOGIN = "not_login";

    private final String reason;

    public CPacketLogoutFailed(LogoutFailedReason reason) {
        this.reason = switch (reason) {
            case NOT_LOGGED_IN -> NOT_LOGIN;
        };
    }
}
