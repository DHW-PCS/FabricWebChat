package org.dhwpcs.webchat.session;

import io.netty.channel.ChannelFuture;
import net.minecraft.text.Text;
import org.dhwpcs.webchat.data.AccountInfo;

import java.util.Deque;
import java.util.LinkedList;
import java.util.UUID;
import java.util.function.Supplier;

public class ChatSession {

    private ClientConnection connection;
    private AccountInfo info;
    private final SessionManager manager;
    private Deque<Supplier<ChannelFuture>> actions = new LinkedList<>();

    public ChatSession(SessionManager manager, AccountInfo info) {
        this.manager = manager;
        this.info = info;
    }

    public void sendInfo() {
        if(connection != null) {
            actions.add(() -> connection.serverInvoker().sendInfo(info));
        } else throw new IllegalStateException("Not connected");
    }

    public void pushMessage(UUID sender, Text text) {
        actions.addLast(() -> connection.serverInvoker().pushMessage(sender, text));
    }

    public void connect(ClientConnection connection) {
        if(connection == null) {
            throw new IllegalStateException();
        }
        this.connection = connection;
        if(this.info != null) {
            manager.endCountdown(this);
        }
    }

    public void executeQueuedActions() {
        for(Supplier<ChannelFuture> element : actions) {
            ChannelFuture future = element.get();
            future.addListener(it -> {
                if(!it.isSuccess()) {
                    actions.add(element);
                }
            });
        }
    }

    /**
     * Called by the server to halt the session.
     */
    public void halt() {
        this.connection = null;
        this.info = null;
    }

    /**
     * Called by the connection to notify the session.
     * It would not discard the connection.
     * Rather it counts 5min and discard the session.
     */
    public void onDisconnect() {
        this.connection = null;
        manager.beginCountdown(this);
    }

    /**
     * Called by the connection to notify the session
     * that it is to close.
     * It would discard the SessionInfo instantly.
     */
    public void logout() {
        this.connection = null;
        this.info = null;
    }

    /**
     * Time's up!
     */
    public void discardCd() {
        this.info = null;
    }

    public AccountInfo getInfo() {
        return info;
    }

    public ClientConnection getConnection() {
        return connection;
    }

    public SessionManager getManager() {
        return manager;
    }

    public boolean isAlive() {
        return connection != null;
    }

    public boolean isKeeping() {
        return connection == null && info != null;
    }

    public boolean isDead() {
        return connection == null && info == null;
    }
}
