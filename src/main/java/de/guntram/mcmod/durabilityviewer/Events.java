package de.guntram.mcmod.durabilityviewer;

import de.guntram.mcmod.durabilityviewer.client.gui.GuiItemDurability;
import de.guntram.mcmod.durabilityviewer.config.Configs;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.resources.Identifier;

public class Events {

    private static GuiItemDurability gui;

    public Events() {
    }

    public void init() {
        System.out.println("Test1");
        System.out.println("Test1");
        System.out.println("Test1");
        System.out.println("Test1");
        System.out.println("Test1");
        System.out.println("Test1");
        System.out.println("Test1");
        System.out.println("Test1");
        System.out.println("Test1");
        System.out.println("Test1");
        System.out.println("Test1");
        System.out.println("Test1");
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
            DurabilityViewer.setWindowTitle(mc.getUser().getName() + " on " + serverName);
        });

        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
            if (!Configs.Settings.SetWindowTitle.getBooleanValue())
                return;
            Minecraft mc = Minecraft.getInstance();
            DurabilityViewer.setWindowTitle(mc.getUser().getName() + " not connected");
        });

        Identifier durabilityMobEffectHudLayer = Identifier.fromNamespaceAndPath(DurabilityViewer.MODID, "durabilityeffecthudlayer");
        HudElementRegistry.attachElementAfter(VanillaHudElements.MOB_EFFECTS, durabilityMobEffectHudLayer, (context, tickCounter) -> {
            if (gui == null)
                gui = new GuiItemDurability();
            gui.afterRenderStatusEffects(context, 0);
        });

        Identifier durabilityHotbarHudLayer = Identifier.fromNamespaceAndPath(DurabilityViewer.MODID, "durabilityhotbarhudlayer");
        HudElementRegistry.attachElementAfter(VanillaHudElements.HOTBAR, durabilityHotbarHudLayer, (context, tickCounter) -> {
            if (gui == null)
                gui = new GuiItemDurability();
            gui.onRenderGameOverlayPost(context, 0);
        });
    }
}
