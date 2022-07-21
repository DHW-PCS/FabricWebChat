package org.dhwpcs.webchat.server.psi;

import net.minecraft.text.Text;

import java.util.UUID;

public interface ChatListener {
    void onMessage(UUID sender, Text message);
}
