package org.dhwpcs.webchat.server;

import net.minecraft.text.Text;

import java.util.UUID;

public interface ChatListener {
    void onMessage(UUID sender, Text message);
}
