package de.guntram.mcmod.durabilityviewer.config;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import de.guntram.mcmod.durabilityviewer.DurabilityViewer;
import de.guntram.mcmod.durabilityviewer.client.gui.Corner;
import fi.dy.masa.malilib.config.ConfigUtils;
import fi.dy.masa.malilib.config.IConfigBase;
import fi.dy.masa.malilib.config.IConfigHandler;
import fi.dy.masa.malilib.config.options.ConfigBoolean;
import fi.dy.masa.malilib.config.options.ConfigColor;
import fi.dy.masa.malilib.config.options.ConfigInteger;
import fi.dy.masa.malilib.config.options.ConfigOptionList;
import fi.dy.masa.malilib.util.FileUtils;
import fi.dy.masa.malilib.util.JsonUtils;
import net.minecraft.text.Text;

import java.io.File;

public class Configs implements IConfigHandler {

    private static final String CONFIG_FILE_NAME = "durability-viewer.json";
    private static final int CONFIG_VERSION = 1;

    public static class Settings {
        public static final ConfigBoolean ArmorAroundHotbar = new ConfigBoolean("ArmorAroundHotbar", false, Text.translatable("durabilityviewer.config.tt.armorhotbar").getString());
        public static final ConfigOptionList HUDCorner = new ConfigOptionList("HUDCorner", Corner.BOTTOM_RIGHT, Text.translatable("durabilityviewer.config.tt.corner").getString());
        public static final ConfigBoolean EffectDuration = new ConfigBoolean("EffectDuration", true, Text.translatable("durabilityviewer.config.tt.effectduration").getString());
        public static final ConfigInteger HideDamageOverPercent = new ConfigInteger("HideDamageOverPercent", 100, 0, 100, true, Text.translatable("durabilityviewer.config.tt.hidedamagepercent").getString());
        public static final ConfigInteger SoundBelowDurability = new ConfigInteger("SoundBelowDurability", 100, 1, 1500, true, Text.translatable("durabilityviewer.config.tt.mindurability").getString());
        public static final ConfigInteger SoundBelowPercent = new ConfigInteger("SoundBelowPercent", 10, 1, 100, true, Text.translatable("durabilityviewer.config.tt.minpercent").getString());
        public static final ConfigBoolean Percentages = new ConfigBoolean("Percentages", false, Text.translatable("durabilityviewer.config.tt.percentvalues").getString());
        public static final ConfigBoolean SetWindowTitle = new ConfigBoolean("SetWindowTitle", true, Text.translatable("durabilityviewer.config.tt.setwindowtitle").getString());
        public static final ConfigBoolean ShowAllTrinkets = new ConfigBoolean("ShowAllTrinkets", true, Text.translatable("durabilityviewer.config.tt.showalltrinkets").getString());
        public static final ConfigInteger PercentToShowDamage = new ConfigInteger("PercentToShowDamage", 80, 0, 100, true, Text.translatable("durabilityviewer.config.tt.showdamagepercent").getString());
        public static final ConfigBoolean ShowFreeInventorySlots = new ConfigBoolean("ShowFreeInventorySlots", true, Text.translatable("durabilityviewer.config.tt.showfreeslots").getString());
        public static final ConfigColor TooltipColor = new ConfigColor("TooltipColor", "#54FB54", Text.translatable("durabilityviewer.config.tt.tooltipcolor").getString());
        public static final ConfigOptionList WarningMode = new ConfigOptionList("WarningMode", WarnMode.SOUND, Text.translatable("durabilityviewer.config.tt.warnmode").getString());
        public static final ConfigOptionList SoundCategory = new ConfigOptionList("SoundCategory", de.guntram.mcmod.durabilityviewer.config.SoundCategory.getDefault(), Text.translatable("durabilityviewer.config.tt.soundcategory").getString());

        public static final ImmutableList<IConfigBase> SETTINGS = ImmutableList.of(
                ArmorAroundHotbar,
                HUDCorner,
                EffectDuration,
                HideDamageOverPercent,
                SoundBelowDurability,
                SoundBelowPercent,
                Percentages,
                SetWindowTitle,
                ShowAllTrinkets,
                PercentToShowDamage,
                ShowFreeInventorySlots,
                TooltipColor,
                WarningMode,
                SoundCategory
        );
    }

    public static void loadFromFile() {
        File configFile = new File(FileUtils.getConfigDirectoryAsPath().toFile(), CONFIG_FILE_NAME);

        if (!configFile.exists()) {
            saveToFile();
        }

        if (configFile.exists() && configFile.isFile() && configFile.canRead()) {
            JsonElement element = JsonUtils.parseJsonFile(configFile);

            if (element != null && element.isJsonObject()) {
                JsonObject root = element.getAsJsonObject();
                ConfigUtils.readConfigBase(root, "Settings", Settings.SETTINGS);
            }
        }
    }

    public static void saveToFile() {
        File dir = FileUtils.getConfigDirectoryAsPath().toFile();

        if ((dir.exists() && dir.isDirectory()) || dir.mkdirs()) {
            JsonObject root = new JsonObject();

            ConfigUtils.writeConfigBase(root, "Settings", Settings.SETTINGS);

            root.add("config_version", new JsonPrimitive(CONFIG_VERSION));

            JsonUtils.writeJsonToFile(root, new File(dir, CONFIG_FILE_NAME));

            DurabilityViewer.LOGGER.info("[DurabilityViewer] Config Saved");
        }
    }

    @Override
    public void load() {
        loadFromFile();
    }

    @Override
    public void save() {
        saveToFile();
    }
}
