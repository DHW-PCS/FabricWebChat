package org.dhwpcs.webchat.psi.fabric.mixin;

import net.minecraft.network.MessageType;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import org.dhwpcs.webchat.psi.fabric.ChatEventDispatcher;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.UUID;
import java.util.function.Function;

@Mixin(ServerPlayNetworkHandler.class)
public abstract class ServerPlayNetworkHandlerMixin {

    @Redirect(method = "handleMessage",
    at = @At(value = "INVOKE", target = "Lnet/minecraft/server/PlayerManager;broadcast(Lnet/minecraft/text/Text;Ljava/util/function/Function;Lnet/minecraft/network/MessageType;Ljava/util/UUID;)V"))
    private void afterBroadcast(PlayerManager instance, Text serverMessage, Function<ServerPlayerEntity, Text> playerMessageFactory, MessageType type, UUID sender) {
        instance.getServer().getPlayerManager().broadcast(serverMessage, playerMessageFactory, type, sender);
        TranslatableText text = (TranslatableText)serverMessage;
        Text rawText = (Text) text.getArgs()[1];
        ((ChatEventDispatcher)instance.getServer()).dispatch(sender, rawText);
    }
}
