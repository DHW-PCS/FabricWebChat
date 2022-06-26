package org.dhwpcs.webchat.session;

import net.minecraft.text.BaseText;
import org.dhwpcs.webchat.WebChat;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class SessionManager {
    private final Set<ChatSession> sessions = new HashSet<>();
    private final WebChat webChat;

    public SessionManager(WebChat webChat) {
        this.webChat = webChat;
    }

    public void addSession(ChatSession session) {
        session.setManager(this);
        sessions.add(session);
    }

    public void discardSession(ChatSession session, DisconnectReason reason) {
        session.setManager(null);
        session.discard(reason);
        sessions.remove(session);
    }

    public ChatSession acquireSession(UUID uid) {
        for(ChatSession session : sessions) {
            if(session.getInfo() != null && session.getInfo().account().equals(uid)) {
                return session;
            }
        }
        return null;
    }

    public void broadcast(UUID sender, BaseText text) {
        sessions.forEach(session -> session.pushMessage(sender, text));
    }

    public WebChat getWebChat() {
        return webChat;
    }

    public void terminate() {

    }
}
