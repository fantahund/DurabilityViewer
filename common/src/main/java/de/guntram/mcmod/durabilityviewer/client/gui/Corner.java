package de.guntram.mcmod.durabilityviewer.client.gui;

import fi.dy.masa.malilib.config.IConfigOptionListEntry;
import fi.dy.masa.malilib.util.StringUtils;

public enum Corner implements IConfigOptionListEntry {
    BOTTOM_RIGHT("Bottom-Right", "durabilityviewer.config.bottom_right"),
    BOTTOM_LEFT("Bottom-Left", "durabilityviewer.config.bottom_left"),
    TOP_RIGHT("Top-Right", "durabilityviewer.config.top_right"),
    TOP_LEFT("Top-Left", "durabilityviewer.config.top_left");

    public boolean isLeft() {
        return this == TOP_LEFT || this == BOTTOM_LEFT;
    }

    public boolean isRight() {
        return this == TOP_RIGHT || this == BOTTOM_RIGHT;
    }

    public boolean isTop() {
        return this == TOP_LEFT || this == TOP_RIGHT;
    }

    public boolean isBottom() {
        return this == BOTTOM_LEFT || this == BOTTOM_RIGHT;
    }

    private final String configString;
    private final String translationKey;

    private Corner(String configString, String translationKey) {
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
    public Corner fromString(String name)
    {
        return fromStringStatic(name);
    }

    public static Corner fromStringStatic(String name)
    {
        for (Corner mode : Corner.values())
        {
            if (mode.configString.equalsIgnoreCase(name))
            {
                return mode;
            }
        }

        return Corner.BOTTOM_RIGHT;
    }
}
