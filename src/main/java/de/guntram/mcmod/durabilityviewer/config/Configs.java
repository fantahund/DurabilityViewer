package de.guntram.mcmod.durabilityviewer.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.guntram.mcmod.durabilityviewer.DurabilityViewer;
import de.guntram.mcmod.durabilityviewer.client.gui.Corner;
import de.tobi.voxelconfig.ConfigFile;
import de.tobi.voxelconfig.ConfigProvider;
import de.tobi.voxelconfig.SettingsCategory;
import de.tobi.voxelconfig.SettingsGroup;
import de.tobi.voxelconfig.SettingsOption;
import java.io.BufferedReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;

public class Configs implements ConfigProvider {

    public static final Configs INSTANCE = new Configs();

    private static final String CONFIG_FILE_NAME = "durabilityviewer.properties";
    private static final String LEGACY_CONFIG_FILE_NAME = "durability-viewer.json";

    public static class Settings {
        public static boolean armorAroundHotbar = false;
        public static Corner hudCorner = Corner.BOTTOM_RIGHT;
        public static boolean effectDuration = true;
        public static int hideDamageOverPercent = 100;
        public static int soundBelowDurability = 100;
        public static int soundBelowPercent = 10;
        public static boolean percentages = false;
        public static boolean setWindowTitle = true;
        public static boolean showAllTrinkets = true;
        public static int percentToShowDamage = 80;
        public static boolean showFreeInventorySlots = true;
        public static String tooltipColor = "#54FB54";
        public static WarnMode warningMode = WarnMode.SOUND;
        public static SoundSource soundCategory = SoundSource.PLAYERS;
    }

    private static Path configPath(String fileName) {
        return FabricLoader.getInstance().getConfigDir().resolve(fileName);
    }

    public static void loadFromFile() {
        Path path = configPath(CONFIG_FILE_NAME);

        if (!Files.exists(path) && loadLegacyJson()) {
            saveToFile();
            return;
        }

        new ConfigFile(path).load(reader -> {
            Settings.armorAroundHotbar = reader.getBoolean("ArmorAroundHotbar", Settings.armorAroundHotbar);
            Settings.hudCorner = Corner.fromStringStatic(reader.getString("HUDCorner", Settings.hudCorner.getStringValue()));
            Settings.effectDuration = reader.getBoolean("EffectDuration", Settings.effectDuration);
            Settings.hideDamageOverPercent = reader.getInt("HideDamageOverPercent", Settings.hideDamageOverPercent, 0, 100);
            Settings.soundBelowDurability = reader.getInt("SoundBelowDurability", Settings.soundBelowDurability, 1, 1500);
            Settings.soundBelowPercent = reader.getInt("SoundBelowPercent", Settings.soundBelowPercent, 1, 100);
            Settings.percentages = reader.getBoolean("Percentages", Settings.percentages);
            Settings.setWindowTitle = reader.getBoolean("SetWindowTitle", Settings.setWindowTitle);
            Settings.showAllTrinkets = reader.getBoolean("ShowAllTrinkets", Settings.showAllTrinkets);
            Settings.percentToShowDamage = reader.getInt("PercentToShowDamage", Settings.percentToShowDamage, 0, 100);
            Settings.showFreeInventorySlots = reader.getBoolean("ShowFreeInventorySlots", Settings.showFreeInventorySlots);
            Settings.tooltipColor = reader.getString("TooltipColor", Settings.tooltipColor);
            Settings.warningMode = WarnMode.fromStringStatic(reader.getString("WarningMode", Settings.warningMode.getStringValue()));
            Settings.soundCategory = soundSourceFromString(reader.getString("SoundCategory", Settings.soundCategory.getName()));
        });
    }

    public static void saveToFile() {
        new ConfigFile(configPath(CONFIG_FILE_NAME)).save(writer -> {
            writer.comment("Durability Viewer settings");
            writer.blank();
            writer.put("ArmorAroundHotbar", Settings.armorAroundHotbar);
            writer.put("HUDCorner", Settings.hudCorner.getStringValue());
            writer.put("EffectDuration", Settings.effectDuration);
            writer.put("HideDamageOverPercent", Settings.hideDamageOverPercent);
            writer.put("SoundBelowDurability", Settings.soundBelowDurability);
            writer.put("SoundBelowPercent", Settings.soundBelowPercent);
            writer.put("Percentages", Settings.percentages);
            writer.put("SetWindowTitle", Settings.setWindowTitle);
            writer.put("ShowAllTrinkets", Settings.showAllTrinkets);
            writer.put("PercentToShowDamage", Settings.percentToShowDamage);
            writer.put("ShowFreeInventorySlots", Settings.showFreeInventorySlots);
            writer.put("TooltipColor", Settings.tooltipColor);
            writer.put("WarningMode", Settings.warningMode.getStringValue());
            writer.put("SoundCategory", Settings.soundCategory.getName());
        });

        DurabilityViewer.LOGGER.info("[DurabilityViewer] Config Saved");
    }

    /**
     * Reads the malilib era {@code durability-viewer.json} once so users keep their settings
     * when updating. Returns whether anything was imported.
     */
    private static boolean loadLegacyJson() {
        Path legacy = configPath(LEGACY_CONFIG_FILE_NAME);

        if (!Files.isReadable(legacy)) {
            return false;
        }

        JsonObject settings;

        try (BufferedReader in = Files.newBufferedReader(legacy, StandardCharsets.UTF_8)) {
            JsonElement root = JsonParser.parseReader(in);

            if (!root.isJsonObject() || !root.getAsJsonObject().has("Settings")) {
                return false;
            }

            settings = root.getAsJsonObject().getAsJsonObject("Settings");
        } catch (Exception e) {
            DurabilityViewer.LOGGER.warn("[DurabilityViewer] Could not read {}", LEGACY_CONFIG_FILE_NAME, e);
            return false;
        }

        Settings.armorAroundHotbar = legacyBoolean(settings, "ArmorAroundHotbar", Settings.armorAroundHotbar);
        Settings.hudCorner = Corner.fromStringStatic(legacyString(settings, "HUDCorner", Settings.hudCorner.getStringValue()));
        Settings.effectDuration = legacyBoolean(settings, "EffectDuration", Settings.effectDuration);
        Settings.hideDamageOverPercent = legacyInt(settings, "HideDamageOverPercent", Settings.hideDamageOverPercent, 0, 100);
        Settings.soundBelowDurability = legacyInt(settings, "SoundBelowDurability", Settings.soundBelowDurability, 1, 1500);
        Settings.soundBelowPercent = legacyInt(settings, "SoundBelowPercent", Settings.soundBelowPercent, 1, 100);
        Settings.percentages = legacyBoolean(settings, "Percentages", Settings.percentages);
        Settings.setWindowTitle = legacyBoolean(settings, "SetWindowTitle", Settings.setWindowTitle);
        Settings.showAllTrinkets = legacyBoolean(settings, "ShowAllTrinkets", Settings.showAllTrinkets);
        Settings.percentToShowDamage = legacyInt(settings, "PercentToShowDamage", Settings.percentToShowDamage, 0, 100);
        Settings.showFreeInventorySlots = legacyBoolean(settings, "ShowFreeInventorySlots", Settings.showFreeInventorySlots);
        Settings.tooltipColor = legacyString(settings, "TooltipColor", Settings.tooltipColor);
        Settings.warningMode = WarnMode.fromStringStatic(legacyString(settings, "WarningMode", Settings.warningMode.getStringValue()));
        Settings.soundCategory = soundSourceFromString(legacyString(settings, "SoundCategory", Settings.soundCategory.getName()));

        DurabilityViewer.LOGGER.info("[DurabilityViewer] Imported settings from {}", LEGACY_CONFIG_FILE_NAME);
        return true;
    }

    private static boolean legacyBoolean(JsonObject settings, String key, boolean defaultValue) {
        JsonElement element = settings.get(key);
        return element != null && element.isJsonPrimitive() ? element.getAsBoolean() : defaultValue;
    }

    private static int legacyInt(JsonObject settings, String key, int defaultValue, int min, int max) {
        JsonElement element = settings.get(key);

        if (element == null || !element.isJsonPrimitive()) {
            return defaultValue;
        }

        try {
            return Math.clamp(element.getAsInt(), min, max);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    private static String legacyString(JsonObject settings, String key, String defaultValue) {
        JsonElement element = settings.get(key);
        return element != null && element.isJsonPrimitive() ? element.getAsString() : defaultValue;
    }

    private static SoundSource soundSourceFromString(String name) {
        for (SoundSource source : SoundSource.values()) {
            if (source.getName().equalsIgnoreCase(name)) {
                return source;
            }
        }

        return SoundSource.PLAYERS;
    }

    @Override
    public List<SettingsCategory> categories() {
        return List.of(new SettingsCategory("settings", "durabilityviewer.gui.button.config_gui.settings", List.of(
                new SettingsGroup("durabilityviewer.config.group.display", List.of(
                        SettingsOption.choice("HUDCorner", "durabilityviewer.config.corner", "durabilityviewer.config.tt.corner",
                                () -> Settings.hudCorner, value -> Settings.hudCorner = value,
                                choices(Corner.values(), Corner::getDisplayName)),
                        SettingsOption.toggle("ArmorAroundHotbar", "durabilityviewer.config.armorhotbar", "durabilityviewer.config.tt.armorhotbar",
                                () -> Settings.armorAroundHotbar, value -> Settings.armorAroundHotbar = value),
                        SettingsOption.toggle("Percentages", "durabilityviewer.config.percentvalues", "durabilityviewer.config.tt.percentvalues",
                                () -> Settings.percentages, value -> Settings.percentages = value),
                        SettingsOption.toggle("EffectDuration", "durabilityviewer.config.effectduration", "durabilityviewer.config.tt.effectduration",
                                () -> Settings.effectDuration, value -> Settings.effectDuration = value),
                        SettingsOption.toggle("ShowFreeInventorySlots", "durabilityviewer.config.showfreeslots", "durabilityviewer.config.tt.showfreeslots",
                                () -> Settings.showFreeInventorySlots, value -> Settings.showFreeInventorySlots = value),
                        SettingsOption.toggle("ShowAllTrinkets", "durabilityviewer.config.showalltrinkets", "durabilityviewer.config.tt.showalltrinkets",
                                () -> Settings.showAllTrinkets, value -> Settings.showAllTrinkets = value),
                        SettingsOption.text("TooltipColor", "durabilityviewer.config.tooltipcolor", "durabilityviewer.config.tt.tooltipcolor",
                                () -> Settings.tooltipColor, value -> Settings.tooltipColor = value,
                                () -> true, Component::empty))),
                new SettingsGroup("durabilityviewer.config.group.durability", List.of(
                        percentSlider("PercentToShowDamage", "durabilityviewer.config.showdamagepercent", "durabilityviewer.config.tt.showdamagepercent",
                                () -> Settings.percentToShowDamage, value -> Settings.percentToShowDamage = value, 0, 100),
                        percentSlider("HideDamageOverPercent", "durabilityviewer.config.hidedamagepercent", "durabilityviewer.config.tt.hidedamagepercent",
                                () -> Settings.hideDamageOverPercent, value -> Settings.hideDamageOverPercent = value, 0, 100))),
                new SettingsGroup("durabilityviewer.config.group.warning", List.of(
                        SettingsOption.choice("WarningMode", "durabilityviewer.config.warnmode", "durabilityviewer.config.tt.warnmode",
                                () -> Settings.warningMode, value -> Settings.warningMode = value,
                                choices(WarnMode.values(), WarnMode::getDisplayName)),
                        SettingsOption.slider("SoundBelowDurability", "durabilityviewer.config.mindurability", "durabilityviewer.config.tt.mindurability",
                                () -> (double) Settings.soundBelowDurability, value -> Settings.soundBelowDurability = value.intValue(),
                                1, 1500, 1, value -> Component.literal(Integer.toString(value.intValue())),
                                Configs::warnsWithSound, soundDisabled(), 1),
                        SettingsOption.slider("SoundBelowPercent", "durabilityviewer.config.minpercent", "durabilityviewer.config.tt.minpercent",
                                () -> (double) Settings.soundBelowPercent, value -> Settings.soundBelowPercent = value.intValue(),
                                1, 100, 1, Configs::formatPercent,
                                Configs::warnsWithSound, soundDisabled(), 1),
                        SettingsOption.choice("SoundCategory", "durabilityviewer.config.soundcategory", "durabilityviewer.config.tt.soundcategory",
                                () -> Settings.soundCategory, value -> Settings.soundCategory = value,
                                choices(SoundSource.values(), source -> Component.translatable("soundCategory." + source.getName())),
                                Configs::warnsWithSound, soundDisabled(), 1))),
                new SettingsGroup("durabilityviewer.config.group.misc", List.of(
                        SettingsOption.toggle("SetWindowTitle", "durabilityviewer.config.setwindowtitle", "durabilityviewer.config.tt.setwindowtitle",
                                () -> Settings.setWindowTitle, value -> Settings.setWindowTitle = value))))));
    }

    private static boolean warnsWithSound() {
        return Settings.warningMode == WarnMode.SOUND || Settings.warningMode == WarnMode.BOTH;
    }

    private static Supplier<Component> soundDisabled() {
        return () -> Component.translatable("durabilityviewer.config.requires.sound");
    }

    private static Component formatPercent(Double value) {
        return Component.literal(value.intValue() + "%");
    }

    private static SettingsOption<Double> percentSlider(String id, String nameKey, String tooltipKey,
            Supplier<Integer> getter, java.util.function.IntConsumer setter, int minimum, int maximum) {
        return SettingsOption.slider(id, nameKey, tooltipKey,
                () -> (double) getter.get(), value -> setter.accept(value.intValue()),
                minimum, maximum, 1, Configs::formatPercent, () -> true, Component::empty, 0);
    }

    private static <T> List<SettingsOption.Choice<T>> choices(T[] values, java.util.function.Function<T, Component> label) {
        List<SettingsOption.Choice<T>> choices = new ArrayList<>(values.length);

        for (T value : values) {
            choices.add(new SettingsOption.Choice<>(value, label.apply(value)));
        }

        return List.copyOf(choices);
    }
}
