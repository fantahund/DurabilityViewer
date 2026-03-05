package de.guntram.mcmod.durabilityviewer;

import de.guntram.mcmod.durabilityviewer.client.gui.GuiItemDurability;
import de.guntram.mcmod.durabilityviewer.config.Configs;

public class DurabilityViewer {



    public void init() {
        DurabilityViewerConstants.getModApiBridge().setKeyBindings();
        Configs.loadFromFile();
    }

    public void processKeyBinds() {
        if (DurabilityViewerConstants.getKeyMapping().consumeClick()) {
            GuiItemDurability.toggleVisibility();
        }
    }

    public void onEventsSet(Events events) {
        events.initEvents(this);
    }
}
