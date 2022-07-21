package org.dhwpcs.webchat.server.psi;

import net.minecraft.text.Text;
import org.dhwpcs.webchat.server.data.AccountInfo;
import org.dhwpcs.webchat.util.Tickable;

public interface GameServerApi {
    void onMessage(AccountInfo info, Text txt);
    void registerChatListener(ChatListener listener);
    void registerTickable(Tickable tickable);
}
