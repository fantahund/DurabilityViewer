package de.guntram.mcmod.durabilityviewer.fabric;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import de.guntram.mcmod.durabilityviewer.DurabilityViewer;

public class ModMenuImpl implements ModMenuApi {
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return DurabilityViewer::createConfigScreen;
    }
}
