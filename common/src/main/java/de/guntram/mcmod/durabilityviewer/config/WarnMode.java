package de.guntram.mcmod.durabilityviewer.config;

import net.minecraft.network.chat.Component;

public enum WarnMode {
    NONE("None", "durabilityviewer.config.warnmode.none"),
    SOUND("Sound", "durabilityviewer.config.warnmode.sound"),
    VISUAL("Visual", "durabilityviewer.config.warnmode.visual"),
    BOTH("Both", "durabilityviewer.config.warnmode.both");

    private final String configString;
    private final String translationKey;

    private WarnMode(String configString, String translationKey) {
        this.configString = configString;
        this.translationKey = translationKey;
    }

    public String getStringValue()
    {
        return this.configString;
    }

    public Component getDisplayName()
    {
        return Component.translatable(this.translationKey);
    }

    public static WarnMode fromStringStatic(String name)
    {
        for (WarnMode mode : WarnMode.values())
        {
            if (mode.configString.equalsIgnoreCase(name))
            {
                return mode;
            }
        }

        return WarnMode.SOUND;
    }
}
