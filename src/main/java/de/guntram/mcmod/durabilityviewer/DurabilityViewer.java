package de.guntram.mcmod.durabilityviewer;

import de.guntram.mcmod.durabilityviewer.client.gui.GuiItemDurability;
import de.guntram.mcmod.durabilityviewer.config.Configs;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_H;

public class DurabilityViewer implements ClientModInitializer {
    public static final String MODID = "durabilityviewer";
    public static final String MODNAME = "Durability Viewer";

    public static DurabilityViewer instance;
    private static String changedWindowTitle;
    private KeyBinding showHide;
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
        if (showHide.wasPressed()) {
            GuiItemDurability.toggleVisibility();
        }
    }

    public void setKeyBindings() {
        final KeyBinding.Category category = KeyBinding.Category.create(Identifier.of("key.categories.durabilityviewer")); //FIXME 1.21.10
        KeyBindingHelper.registerKeyBinding(showHide = new KeyBinding("key.durabilityviewer.showhide", InputUtil.Type.KEYSYM, GLFW_KEY_H, category));
        ClientTickEvents.END_CLIENT_TICK.register(e -> processKeyBinds());
    }
}
