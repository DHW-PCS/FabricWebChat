package org.dhwpcs.webchat.server.psi.fabric;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;
import org.dhwpcs.webchat.server.data.AccountInfo;
import org.dhwpcs.webchat.server.psi.ChatListener;
import org.dhwpcs.webchat.server.psi.GameServerApi;
import org.dhwpcs.webchat.util.Tickable;

public class FabricGameApi implements GameServerApi {

    public static FabricGameApi FABRIC;
    private final MinecraftServer server;

    public FabricGameApi(MinecraftServer server) {
        this.server = server;
    }

    @Override
    public void onMessage(AccountInfo info, Text txt) {
        ((ChatEventDispatcher)server).webSend(info, txt);
    }

    @Override
    public void registerChatListener(ChatListener listener) {
        ((ChatEventDispatcher)server).register(listener);
    }

    @Override
    public void registerTickable(Tickable runnable) {
        ServerTickEvents.END_SERVER_TICK.register(it -> it.execute(runnable::tick));
    }

    public static GameServerApi initialize(MinecraftServer server) {
        FABRIC = new FabricGameApi(server);
        return FABRIC;
    }
}
