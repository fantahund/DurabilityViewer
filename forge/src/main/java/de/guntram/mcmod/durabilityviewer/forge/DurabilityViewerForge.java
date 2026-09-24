package de.guntram.mcmod.durabilityviewer.forge;

import de.guntram.mcmod.durabilityviewer.ClientHooks;
import de.guntram.mcmod.durabilityviewer.DurabilityViewer;
import net.minecraft.resources.Identifier;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.client.event.AddGuiOverlayLayersEvent;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.gui.overlay.ForgeLayeredDraw;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;

@Mod(DurabilityViewer.MODID)
public class DurabilityViewerForge {

    public DurabilityViewerForge(FMLJavaModLoadingContext context) {
        DurabilityViewer.init(() -> FMLPaths.CONFIGDIR.get());
        DurabilityViewer.createKeyMappings();

        RegisterKeyMappingsEvent.BUS.addListener(event -> {
            event.register(DurabilityViewer.showHideKey());
            event.register(DurabilityViewer.openConfigKey());
        });

        AddGuiOverlayLayersEvent.BUS.addListener(event -> {
            ForgeLayeredDraw layers = event.getLayeredDraw();
            layers.addAbove(ForgeLayeredDraw.POTION_EFFECTS,
                    Identifier.fromNamespaceAndPath(DurabilityViewer.MODID, "durabilityeffecthudlayer"),
                    (graphics, deltaTracker) -> ClientHooks.renderAfterStatusEffects(graphics));
            layers.addAbove(ForgeLayeredDraw.ITEM_HOTBAR,
                    Identifier.fromNamespaceAndPath(DurabilityViewer.MODID, "durabilityhotbarhudlayer"),
                    (graphics, deltaTracker) -> ClientHooks.renderAfterHotbar(graphics));
        });

        TickEvent.ClientTickEvent.Post.BUS.addListener(event -> DurabilityViewer.onClientTick());
        ClientPlayerNetworkEvent.LoggingIn.BUS.addListener(event -> ClientHooks.onJoinWorld());
        ClientPlayerNetworkEvent.LoggingOut.BUS.addListener(event -> ClientHooks.onDisconnect());

        // Opens the settings straight from the mod list, the Forge counterpart
        // of the Fabric ModMenu integration.
        ModLoadingContext.get().registerExtensionPoint(
                ConfigScreenHandler.ConfigScreenFactory.class,
                () -> new ConfigScreenHandler.ConfigScreenFactory(DurabilityViewer::createConfigScreen));
    }
}
