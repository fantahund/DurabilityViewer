package de.guntram.mcmod.durabilityviewer;

import de.guntram.mcmod.durabilityviewer.client.gui.GuiItemDurability;
import de.guntram.mcmod.durabilityviewer.config.Configs;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.multiplayer.ServerData;

/**
 * The callbacks a loader module has to drive: two HUD render passes and the
 * connection events that keep the window title in sync.
 */
public final class ClientHooks {

    private static GuiItemDurability gui;

    private ClientHooks() {
    }

    public static void onJoinWorld() {
        if (!Configs.Settings.setWindowTitle) {
            return;
        }
        Minecraft mc = Minecraft.getInstance();
        ServerData serverData = mc.getCurrentServer();
        String serverName = serverData == null ? "local game" : serverData.name;
        if (serverName == null) {
            serverName = "unknown server";
        }
        DurabilityViewer.setWindowTitle(mc.getUser().getName() + " on " + serverName);
    }

    public static void onDisconnect() {
        if (!Configs.Settings.setWindowTitle) {
            return;
        }
        DurabilityViewer.setWindowTitle(Minecraft.getInstance().getUser().getName() + " not connected");
    }

    /** Render pass directly after the vanilla mob effect display. */
    public static void renderAfterStatusEffects(GuiGraphicsExtractor context) {
        gui().afterRenderStatusEffects(context, 0);
    }

    /** Render pass directly after the vanilla hotbar. */
    public static void renderAfterHotbar(GuiGraphicsExtractor context) {
        gui().onRenderGameOverlayPost(context, 0);
    }

    private static GuiItemDurability gui() {
        if (gui == null) {
            gui = new GuiItemDurability();
        }
        return gui;
    }
}
