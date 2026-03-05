package de.guntram.mcmod.durabilityviewer.config;

import com.google.common.collect.ImmutableList;
import fi.dy.masa.malilib.config.IConfigOptionList;
import fi.dy.masa.malilib.config.IConfigOptionListEntry;
import fi.dy.masa.malilib.util.StringUtils;

public enum WarnMode implements IConfigOptionListEntry {
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

    @Override
    public String getStringValue()
    {
        return this.configString;
    }

    @Override
    public String getDisplayName()
    {
        return StringUtils.translate(this.translationKey);
    }

    @Override
    public IConfigOptionListEntry cycle(boolean forward)
    {
        int id = this.ordinal();

        if (forward)
        {
            if (++id >= values().length)
            {
                id = 0;
            }
        }
        else
        {
            if (--id < 0)
            {
                id = values().length - 1;
            }
        }

        return values()[id % values().length];
    }

    @Override
    public WarnMode fromString(String name)
    {
        return fromStringStatic(name);
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
