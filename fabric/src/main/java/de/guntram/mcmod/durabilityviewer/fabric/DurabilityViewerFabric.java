package de.guntram.mcmod.durabilityviewer.fabric;

import de.guntram.mcmod.durabilityviewer.ClientHooks;
import de.guntram.mcmod.durabilityviewer.DurabilityViewer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resources.Identifier;

public class DurabilityViewerFabric implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        DurabilityViewer.init(() -> FabricLoader.getInstance().getConfigDir());

        DurabilityViewer.createKeyMappings();
        KeyMappingHelper.registerKeyMapping(DurabilityViewer.showHideKey());
        KeyMappingHelper.registerKeyMapping(DurabilityViewer.openConfigKey());
        ClientTickEvents.END_CLIENT_TICK.register(client -> DurabilityViewer.onClientTick());

        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> ClientHooks.onJoinWorld());
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> ClientHooks.onDisconnect());

        HudElementRegistry.attachElementAfter(
                VanillaHudElements.MOB_EFFECTS,
                Identifier.fromNamespaceAndPath(DurabilityViewer.MODID, "durabilityeffecthudlayer"),
                (context, tickCounter) -> ClientHooks.renderAfterStatusEffects(context));

        HudElementRegistry.attachElementAfter(
                VanillaHudElements.HOTBAR,
                Identifier.fromNamespaceAndPath(DurabilityViewer.MODID, "durabilityhotbarhudlayer"),
                (context, tickCounter) -> ClientHooks.renderAfterHotbar(context));
    }
}
