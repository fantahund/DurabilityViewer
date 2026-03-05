package de.guntram.mcmod.durabilityviewer.config;

import fi.dy.masa.malilib.config.IConfigOptionListEntry;
import fi.dy.masa.malilib.util.StringUtils;

public class SoundCategory implements IConfigOptionListEntry
{
    private final static net.minecraft.sounds.SoundSource[] allValues = net.minecraft.sounds.SoundSource.values();

    private final String configString;
    private final String translationKey;
    private final net.minecraft.sounds.SoundSource minecraft;

    private SoundCategory(net.minecraft.sounds.SoundSource minecraft)
    {
        this.minecraft = minecraft;
        this.configString = this.minecraft.getName();
        this.translationKey = "soundCategory." + this.configString;
    }

    public net.minecraft.sounds.SoundSource getInternal()
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
        return new SoundCategory(net.minecraft.sounds.SoundSource.PLAYERS);
    }

    public static SoundCategory fromStringStatic(String name)
    {
        for (net.minecraft.sounds.SoundSource mode : allValues) {
            if (mode.getName().equalsIgnoreCase(name)) {
                return new SoundCategory(mode);
            }
        }

        return getDefault();
    }
}
