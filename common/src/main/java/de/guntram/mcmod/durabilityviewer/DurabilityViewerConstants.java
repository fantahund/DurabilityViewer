package de.guntram.mcmod.durabilityviewer;

import com.mojang.blaze3d.platform.InputConstants;
import de.guntram.mcmod.durabilityviewer.config.Configs;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.resources.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_H;

public class DurabilityViewerConstants {
    private static final Logger LOGGER = LogManager.getLogger("DurabilityViewer");
    public static final String MOD_ID = "durabilityviewer";

    private static final DurabilityViewer DURABILITY_VIEWER_INSTANCE = new DurabilityViewer();

    private static String changedWindowTitle;
    private static Events events;
    private static ModApiBridge modApiBridge;
    private static final KeyMapping.Category category = KeyMapping.Category.register(Identifier.fromNamespaceAndPath(DurabilityViewerConstants.MOD_ID, "key.categories.durabilityviewer"));
    private static final KeyMapping keyMapping = new KeyMapping("key.durabilityviewer.showhide", InputConstants.Type.KEYSYM, GLFW_KEY_H, category);

    private DurabilityViewerConstants() {
    }

    public static Logger getLOGGER() {
        return LOGGER;
    }

    public static DurabilityViewer getDurabilityViewerInstance() {
        return DURABILITY_VIEWER_INSTANCE;
    }

    public static void setEvents(Events events) {
        DurabilityViewerConstants.events = events;
        DurabilityViewerConstants.getDurabilityViewerInstance().onEventsSet(events);
    }

    public static Events getEvents() {
        return events;
    }

    public static void setWindowTitle() {
        if (!Configs.Settings.SetWindowTitle.getBooleanValue()) {
            return;
        }

        Minecraft mc = Minecraft.getInstance();
        ServerData serverData = mc.getCurrentServer();
        String serverName;
        if (serverData == null) {
            serverName = "local game";
        } else {
            serverName = serverData.name;
        }

        if (serverName == null) {
            serverName = "unknown server";
        }

        changedWindowTitle = serverName;
    }

    public static String getWindowTitle() {
        return changedWindowTitle;
    }

    public static void setModApiBride(ModApiBridge modApiBridge) {
        DurabilityViewerConstants.modApiBridge = modApiBridge;
    }

    public static KeyMapping.Category getCategory() {
        return category;
    }

    public static KeyMapping getKeyMapping() {
        return keyMapping;
    }

    public static ModApiBridge getModApiBridge() {
        return modApiBridge;
    }
}
