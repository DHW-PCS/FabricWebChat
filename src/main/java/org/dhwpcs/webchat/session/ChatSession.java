package org.dhwpcs.webchat.session;

import net.minecraft.text.BaseText;

import java.util.Deque;
import java.util.LinkedList;
import java.util.UUID;
import java.util.function.BooleanSupplier;

public class ChatSession {

    private ClientConnection connection;
    private SessionInfo info;
    private SessionManager manager;
    private Deque<BooleanSupplier> actions = new LinkedList<>();

    public void pushMessage(UUID sender, BaseText text) {
        actions.addLast(() -> connection.serverInvoker().pushMessage(sender, text));
    }

    public void connect(ClientConnection connection) {
        this.connection = connection;
    }

    public void executeQueuedActions() {
        while(!actions.isEmpty()) {
            if(actions.peekFirst().getAsBoolean()) {
                actions.poll();
            } else break;
        }
    }

    public void discard(DisconnectReason reason) {
        connection.serverInvoker().disconnect(reason);
        this.connection = null;
        this.info = null;
    }

    public void setManager(SessionManager manager) {
        this.manager = manager;
    }

    public SessionInfo getInfo() {
        return info;
    }
}
