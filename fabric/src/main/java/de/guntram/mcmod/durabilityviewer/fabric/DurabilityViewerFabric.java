package de.guntram.mcmod.durabilityviewer.fabric;

import com.mojang.blaze3d.platform.InputConstants;
import de.guntram.mcmod.durabilityviewer.Events;
import de.guntram.mcmod.durabilityviewer.client.gui.GuiItemDurability;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_H;

public class DurabilityViewerFabric implements ClientModInitializer {

    public static final String MODID = "durabilityviewer";
    public static final String MODNAME = "Durability Viewer";

    private static String changedWindowTitle;
    private KeyMapping showHide;
    public static final Logger LOGGER = LogManager.getLogger("DurabilityViewer");

    @Override
    public void onInitializeClient() {
        setKeyBindings();
        changedWindowTitle = null;

        //Configs.loadFromFile(); //FIXME

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
    }

    public void setKeyBindings() {
        final KeyMapping.Category category = KeyMapping.Category.register(Identifier.parse("key.categories.durabilityviewer")); //FIXME 1.21.10
        KeyBindingHelper.registerKeyBinding(showHide = new KeyMapping("key.durabilityviewer.showhide", InputConstants.Type.KEYSYM, GLFW_KEY_H, category));
        ClientTickEvents.END_CLIENT_TICK.register(e -> processKeyBinds());
    }
}