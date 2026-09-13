package de.guntram.mcmod.durabilityviewer.client.gui;

import net.minecraft.network.chat.Component;

public enum Corner {
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

    public String getStringValue()
    {
        return this.configString;
    }

    public Component getDisplayName()
    {
        return Component.translatable(this.translationKey);
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
