package org.dhwpcs.webchat.psi.fabric;

import net.minecraft.text.Text;
import org.dhwpcs.webchat.data.AccountInfo;
import org.dhwpcs.webchat.server.ChatListener;

import java.util.UUID;

public interface ChatEventDispatcher {
    void register(ChatListener listener);
    void dispatch(UUID sender, Text message);
    void webSend(AccountInfo sender, Text message);
}
