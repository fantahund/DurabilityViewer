package de.guntram.mcmod.durabilityviewer;

import com.mojang.blaze3d.platform.InputConstants;
import de.guntram.mcmod.durabilityviewer.client.gui.GuiItemDurability;
import de.guntram.mcmod.durabilityviewer.config.Configs;
import de.voxelmap.voxelconfig.ConfigScreen;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Loader independent core of the mod. The Fabric, Forge and NeoForge modules
 * each call {@link #init} and then wire their own events to the hooks here and
 * in {@link ClientHooks}.
 */
public class DurabilityViewer {
    public static final String MODID = "durabilityviewer";
    public static final String MODNAME = "Durability Viewer";
    public static final Logger LOGGER = LogManager.getLogger("DurabilityViewer");

    private static Platform platform;
    private static String changedWindowTitle;
    private static KeyMapping.Category keyCategory;
    private static KeyMapping showHide;
    private static KeyMapping openConfig;

    /** Called once by the loader module at client startup. */
    public static void init(Platform platform) {
        DurabilityViewer.platform = platform;
        changedWindowTitle = null;
        Configs.loadFromFile();
    }

    public static Platform platform() {
        if (platform == null) {
            throw new IllegalStateException("DurabilityViewer.init() has not been called by the loader module");
        }
        return platform;
    }

    public static void setWindowTitle(String s) {
        changedWindowTitle = s;
    }

    public static String getWindowTitle() {
        return changedWindowTitle;
    }

    /**
     * Creates the key mappings and their category. The loader module has to
     * register {@link #showHideKey()} and {@link #openConfigKey()} afterwards,
     * because that part differs per loader.
     */
    public static void createKeyMappings() {
        // The category's label is Identifier.toLanguageKey("key.category"),
        // i.e. key.category.durabilityviewer.controls.title
        keyCategory = KeyMapping.Category.register(Identifier.fromNamespaceAndPath(MODID, "controls.title"));
        showHide = new KeyMapping("key.durabilityviewer.showhide", InputConstants.getKey("key.keyboard.h").getValue(), keyCategory);
        // Unbound by default so it cannot collide with anything; the mod list
        // entry is not available on every loader and version.
        openConfig = new KeyMapping("key.durabilityviewer.openconfig", InputConstants.UNKNOWN.getValue(), keyCategory);
    }

    /** NeoForge and Forge additionally register the category through their key mapping event. */
    public static KeyMapping.Category keyCategory() {
        return keyCategory;
    }

    public static KeyMapping showHideKey() {
        return showHide;
    }

    public static KeyMapping openConfigKey() {
        return openConfig;
    }

    /** Called by the loader module once per client tick. */
    public static void onClientTick() {
        if (showHide != null && showHide.consumeClick()) {
            GuiItemDurability.toggleVisibility();
        }
        if (openConfig != null && openConfig.consumeClick()) {
            Minecraft.getInstance().gui.setScreen(createConfigScreen(null));
        }
    }

    /** Builds the settings screen. Also used by the loader specific mod list integrations. */
    public static Screen createConfigScreen(Screen parent) {
        return ConfigScreen.create(
                Component.translatable("durabilityviewer.gui.title.configs"),
                Configs.INSTANCE,
                parent,
                Configs::saveToFile);
    }
}
