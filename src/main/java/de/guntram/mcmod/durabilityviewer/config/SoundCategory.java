package de.guntram.mcmod.durabilityviewer.config;

import fi.dy.masa.malilib.config.IConfigOptionListEntry;
import fi.dy.masa.malilib.util.StringUtils;

public class SoundCategory implements IConfigOptionListEntry
{
    private final static net.minecraft.sound.SoundCategory[] allValues = net.minecraft.sound.SoundCategory.values();

    private final String configString;
    private final String translationKey;
    private final net.minecraft.sound.SoundCategory minecraft;

    private SoundCategory(net.minecraft.sound.SoundCategory minecraft)
    {
        this.minecraft = minecraft;
        this.configString = this.minecraft.getName();
        this.translationKey = "soundCategory." + this.configString;
    }

    public net.minecraft.sound.SoundCategory getInternal()
    {
        return minecraft;
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
        int id = minecraft.ordinal();

        if (forward) {
            if (++id >= allValues.length) {
                id = 0;
            }
        } else {
            if (--id < 0) {
                id = allValues.length - 1;
            }
        }

        return new SoundCategory(allValues[id % allValues.length]);
    }

    @Override
    public SoundCategory fromString(String name)
    {
        return fromStringStatic(name);
    }

    public static SoundCategory getDefault()
    {
        return new SoundCategory(net.minecraft.sound.SoundCategory.PLAYERS);
    }

    public static SoundCategory fromStringStatic(String name)
    {
        for (net.minecraft.sound.SoundCategory mode : allValues) {
            if (mode.getName().equalsIgnoreCase(name)) {
                return new SoundCategory(mode);
            }
        }

        return getDefault();
    }
}
