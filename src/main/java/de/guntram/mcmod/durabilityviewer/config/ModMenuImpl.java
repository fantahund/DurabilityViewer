package de.guntram.mcmod.durabilityviewer.config;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import de.tobi.voxelconfig.ConfigScreen;
import net.minecraft.network.chat.Component;

public class ModMenuImpl implements ModMenuApi {
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return (screen) -> ConfigScreen.create(
                Component.translatable("durabilityviewer.gui.title.configs"),
                Configs.INSTANCE,
                screen,
                Configs::saveToFile);
    }
}
