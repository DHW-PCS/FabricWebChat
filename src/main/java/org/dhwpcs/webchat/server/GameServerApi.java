package org.dhwpcs.webchat.server;

import net.minecraft.text.Text;
import org.dhwpcs.webchat.session.SessionInfo;

public interface GameServerApi {
    void onMessage(SessionInfo info, Text txt);
    void registerChatListener(ChatListener listener);
}
