package org.dhwpcs.webchat.server.psi.fabric.mixin;

import net.minecraft.network.MessageType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;
import org.dhwpcs.webchat.server.data.AccountInfo;
import org.dhwpcs.webchat.server.psi.ChatListener;
import org.dhwpcs.webchat.server.psi.fabric.ChatEventDispatcher;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin implements ChatEventDispatcher {

    @Shadow public abstract PlayerManager getPlayerManager();

    private Set<ChatListener> listeners = new HashSet<>();

    @Override
    public void register(ChatListener listener) {
        listeners.add(listener);
    }

    @Override
    public void dispatch(UUID sender, Text message) {
        listeners.forEach(it -> it.onMessage(sender, message));
    }

    @Override
    public void webSend(AccountInfo sender, Text message) {
        MutableText displayName = new LiteralText(String.format("[WEB]<%s>", sender.name()));
        displayName.styled(style -> style.withColor(Formatting.GREEN));
        Text formattedText = new TranslatableText("chat.type.text", message.shallowCopy(), displayName);
        getPlayerManager().broadcast(formattedText, MessageType.CHAT, Util.NIL_UUID);
    }
}
