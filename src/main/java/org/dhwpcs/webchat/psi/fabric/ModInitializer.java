package org.dhwpcs.webchat.psi.fabric;

import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import org.dhwpcs.webchat.WebChat;

public class ModInitializer implements DedicatedServerModInitializer {
    @Override
    public void onInitializeServer() {
        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            WebChat.initialize(server, FabricGameApi.initialize(server));
        });

        ServerLifecycleEvents.SERVER_STOPPING.register(WebChat::stop);
    }
}
