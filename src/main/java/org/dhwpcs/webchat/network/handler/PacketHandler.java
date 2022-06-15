package org.dhwpcs.webchat.network.handler;

import org.dhwpcs.webchat.session.ClientConnection;

public interface PacketHandler {
    void disconnect();
    HandlerStage getStage();
    PacketHandler nextStage();

    ClientConnection getConnection();
}
