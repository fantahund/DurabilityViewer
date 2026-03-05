package de.guntram.mcmod.durabilityviewer;

import de.guntram.mcmod.durabilityviewer.config.Configs;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;

public class Events {

    public Events() {
    }

    public void init() {
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            if (!Configs.Settings.SetWindowTitle.getBooleanValue())
                return;
            Minecraft mc = Minecraft.getInstance();
            ServerData serverData = mc.getCurrentServer();
            String serverName;
            if (serverData == null)
                serverName = "local game";
            else
                serverName = serverData.name;
            if (serverName == null)
                serverName = "unknown server";
            //DurabilityViewer.setWindowTitle(mc.getUser().getName() + " on " + serverName); //FIXME
        });

        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
            if (!Configs.Settings.SetWindowTitle.getBooleanValue())
                return;
            Minecraft mc = Minecraft.getInstance();
            //DurabilityViewer.setWindowTitle(mc.getUser().getName() + " not connected"); //FIXME
        });
    }
}
