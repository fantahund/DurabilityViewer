package de.guntram.mcmod.durabilityviewer;

import de.guntram.mcmod.durabilityviewer.client.gui.GuiItemDurability;
import de.guntram.mcmod.durabilityviewer.config.Configs;
import de.tobi.voxelconfig.ConfigScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mojang.blaze3d.platform.InputConstants;

public class DurabilityViewer implements ClientModInitializer {
    public static final String MODID = "durabilityviewer";
    public static final String MODNAME = "Durability Viewer";

    public static DurabilityViewer instance;
    private static String changedWindowTitle;
    private KeyMapping showHide;
    private KeyMapping openConfig;
    public static final Logger LOGGER = LogManager.getLogger("DurabilityViewer");

    @Override
    public void onInitializeClient() {
        setKeyBindings();
        changedWindowTitle = null;

        Configs.loadFromFile();

        new Events().init(); //init Fabric Events
    }

    public static void setWindowTitle(String s) {
        changedWindowTitle = s;
    }

    public static String getWindowTitle() {
        return changedWindowTitle;
    }

    public void processKeyBinds() {
        if (showHide.consumeClick()) {
            GuiItemDurability.toggleVisibility();
        }
        if (openConfig.consumeClick()) {
            openConfigScreen(null);
        }
    }

    /** Opens the settings screen. Also used by the ModMenu integration, which passes the screen to return to. */
    public static Screen createConfigScreen(Screen parent) {
        return ConfigScreen.create(
                Component.translatable("durabilityviewer.gui.title.configs"),
                Configs.INSTANCE,
                parent,
                Configs::saveToFile);
    }

    private static void openConfigScreen(Screen parent) {
        Minecraft minecraft = Minecraft.getInstance();
        minecraft.gui.setScreen(createConfigScreen(parent));
    }

    public void setKeyBindings() {
        // The category's label is Identifier.toLanguageKey("key.category"), i.e. key.category.durabilityviewer.controls.title
        final KeyMapping.Category category = KeyMapping.Category.register(Identifier.fromNamespaceAndPath(MODID, "controls.title"));
        KeyMappingHelper.registerKeyMapping(showHide = new KeyMapping("key.durabilityviewer.showhide", InputConstants.getKey("key.keyboard.h").getValue(), category));
        // Unbound by default so it cannot collide with anything; ModMenu is not always available
        // for the current Minecraft version, and then this is the only way into the settings.
        KeyMappingHelper.registerKeyMapping(openConfig = new KeyMapping("key.durabilityviewer.openconfig", InputConstants.UNKNOWN.getValue(), category));
        ClientTickEvents.END_CLIENT_TICK.register(e -> processKeyBinds());
    }
}
