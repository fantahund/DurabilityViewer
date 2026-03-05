package de.guntram.mcmod.durabilityviewer.fabric;

import de.guntram.mcmod.durabilityviewer.DurabilityViewerConstants;
import de.guntram.mcmod.durabilityviewer.ModApiBridge;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;

public class FabricModApiBridge implements ModApiBridge {

    @Override
    public void setKeyBindings() {
        KeyBindingHelper.registerKeyBinding(DurabilityViewerConstants.getKeyMapping());
    }
}
