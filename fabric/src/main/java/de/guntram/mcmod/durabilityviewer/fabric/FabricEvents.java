package de.guntram.mcmod.durabilityviewer.fabric;

import de.guntram.mcmod.durabilityviewer.DurabilityViewer;
import de.guntram.mcmod.durabilityviewer.DurabilityViewerConstants;
import de.guntram.mcmod.durabilityviewer.Events;
import de.guntram.mcmod.durabilityviewer.config.Configs;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;

public class FabricEvents implements Events {

    public FabricEvents() {
    }

    @Override
    public void initEvents(DurabilityViewer map) {
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> DurabilityViewerConstants.setWindowTitle());
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> DurabilityViewerConstants.setWindowTitle());
        ClientTickEvents.END_CLIENT_TICK.register(e -> DurabilityViewerConstants.getDurabilityViewerInstance().processKeyBinds());
    }
}
