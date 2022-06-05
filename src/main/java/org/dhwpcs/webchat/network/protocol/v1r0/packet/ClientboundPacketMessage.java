package org.dhwpcs.webchat.network.protocol.v1r0.packet;

import com.google.gson.JsonObject;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.BaseText;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import org.dhwpcs.webchat.network.exception.NetworkException;
import org.dhwpcs.webchat.network.protocol.packet.OutboundPacket;

import java.util.UUID;

public class ClientboundPacketMessage implements OutboundPacket {

    private Entity sender;
    private final BaseText text;
    private final UUID uuid;

    public ClientboundPacketMessage(PlayerEntity sender, BaseText text) {
        this.sender = sender;
        this.text = text;
        this.uuid = sender.getUuid();
    }

    public ClientboundPacketMessage(UUID sender, BaseText text) {
        this.uuid = sender;
        this.text = text;
    }

    @Override
    public void serialize(JsonObject payload) throws NetworkException {
        payload.addProperty("sender", uuid.toString());
        try {
            Text parsed = Texts.parse(sender == null ? null : sender.getCommandSource(), text, sender, 0);
            payload.add("message", Text.Serializer.toJsonTree(parsed));
        } catch (CommandSyntaxException exception) {
            throw new NetworkException("Failed to parse the chat message!", exception);
        }
    }

}
