package org.dhwpcs.webchat.session;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.text.Text;
import org.dhwpcs.webchat.data.AccountInfo;
import org.dhwpcs.webchat.util.Tickable;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class SessionManager implements Tickable {
    private final Set<ChatSession> sessions = new HashSet<>();
    private final Object2IntMap<ChatSession> countdown = new Object2IntOpenHashMap<>();

    public SessionManager() {}

    public ChatSession createSession(AccountInfo info) {
        ChatSession session = new ChatSession(this, info);
        sessions.add(session);
        return session;
    }

    public void discardSession(ChatSession session, DisconnectReason reason) {
        if(!sessions.contains(session)) {
            throw new IllegalArgumentException("The session is not contained in this manager!");
        }
        if(session.isDead()) {
            return;
        }
        if(session.isAlive()){
            session.onDisconnect();
            session.getConnection().serverInvoker().disconnect(reason);
        } else if(session.isKeeping()) {
            endCountdown(session);
            session.discardCd();
        }
        sessions.remove(session);
    }

    public ChatSession acquireSession(UUID uid) {
        for(ChatSession session : sessions) {
            if(session.getInfo() != null && session.getInfo().uid().equals(uid)) {
                return session;
            }
        }
        return null;
    }

    public void broadcast(UUID sender, Text text) {
        sessions.forEach(session -> {
            if(!session.isDead()) session.pushMessage(sender, text);
        });
    }

    public void terminate() {
        sessions.forEach(it -> discardSession(it, DisconnectReason.SERVER_STOPPING));
    }

    /**
     * Count 5min and delete the session.
     * @param session
     */
    public void beginCountdown(ChatSession session) {
        countdown.put(session, 5*60*20);
    }

    public void tick() {
        for(ChatSession session : sessions) {
            if(countdown.containsKey(session)) {
                int res = countdown.getInt(session);
                if (--res == 0) {
                    countdown.removeInt(session);
                    session.discardCd();
                    sessions.remove(session);
                }
            }
            if(session.isDead()) {
                sessions.remove(session);
            }
        }
    }

    /**
     * Cancel the countdown.
     * @param session
     */
    public void endCountdown(ChatSession session) {
        countdown.removeInt(session);
    }
}
