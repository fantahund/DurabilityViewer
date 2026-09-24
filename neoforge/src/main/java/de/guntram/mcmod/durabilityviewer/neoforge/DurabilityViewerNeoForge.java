package de.guntram.mcmod.durabilityviewer.neoforge;

import de.guntram.mcmod.durabilityviewer.ClientHooks;
import de.guntram.mcmod.durabilityviewer.DurabilityViewer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.minecraft.resources.Identifier;
import net.neoforged.fml.loading.FMLPaths;

@Mod(value = DurabilityViewer.MODID, dist = net.neoforged.api.distmarker.Dist.CLIENT)
public class DurabilityViewerNeoForge {

    public DurabilityViewerNeoForge(IEventBus modEventBus, ModContainer modContainer) {
        DurabilityViewer.init(() -> FMLPaths.CONFIGDIR.get());
        DurabilityViewer.createKeyMappings();

        modEventBus.addListener(DurabilityViewerNeoForge::onRegisterKeyMappings);
        modEventBus.addListener(DurabilityViewerNeoForge::onRegisterGuiLayers);

        // Opens the settings straight from the mod list, the NeoForge counterpart
        // of the Fabric ModMenu integration.
        modContainer.registerExtensionPoint(IConfigScreenFactory.class,
                (container, parent) -> DurabilityViewer.createConfigScreen(parent));

        net.neoforged.neoforge.common.NeoForge.EVENT_BUS.register(DurabilityViewerNeoForge.class);
    }

    private static void onRegisterKeyMappings(RegisterKeyMappingsEvent event) {
        event.registerCategory(DurabilityViewer.keyCategory());
        event.register(DurabilityViewer.showHideKey());
        event.register(DurabilityViewer.openConfigKey());
    }

    private static void onRegisterGuiLayers(RegisterGuiLayersEvent event) {
        event.registerAbove(VanillaGuiLayers.EFFECTS,
                Identifier.fromNamespaceAndPath(DurabilityViewer.MODID, "durabilityeffecthudlayer"),
                (graphics, deltaTracker) -> ClientHooks.renderAfterStatusEffects(graphics));

        event.registerAbove(VanillaGuiLayers.HOTBAR,
                Identifier.fromNamespaceAndPath(DurabilityViewer.MODID, "durabilityhotbarhudlayer"),
                (graphics, deltaTracker) -> ClientHooks.renderAfterHotbar(graphics));
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        DurabilityViewer.onClientTick();
    }

    @SubscribeEvent
    public static void onLoggingIn(ClientPlayerNetworkEvent.LoggingIn event) {
        ClientHooks.onJoinWorld();
    }

    @SubscribeEvent
    public static void onLoggingOut(ClientPlayerNetworkEvent.LoggingOut event) {
        ClientHooks.onDisconnect();
    }
}
