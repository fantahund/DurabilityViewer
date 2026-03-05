package de.guntram.mcmod.durabilityviewer.config;

import com.google.common.collect.ImmutableList;
import de.guntram.mcmod.durabilityviewer.DurabilityViewerConstants;
import fi.dy.masa.malilib.config.IConfigBase;
import fi.dy.masa.malilib.gui.GuiConfigsBase;
import fi.dy.masa.malilib.gui.button.ButtonBase;
import fi.dy.masa.malilib.gui.button.ButtonGeneric;
import fi.dy.masa.malilib.gui.button.IButtonActionListener;
import fi.dy.masa.malilib.util.StringUtils;

import java.util.Collections;
import java.util.List;

public class ConfigGui extends GuiConfigsBase { //GuiBase.openGui(new ConfigGui()); <-- Open GUI
    private static ConfigGuiTab tab = ConfigGuiTab.SETTINGS;

    public ConfigGui() {
        super(10, 50, DurabilityViewerConstants.MOD_ID, null, "durabilityviewer.gui.title.configs");
    }

    @Override
    public void initGui() {
        super.initGui();
        this.clearOptions();

        int x = 10;
        int y = 26;

        for (ConfigGuiTab tab : ConfigGuiTab.VALUES) {
            x += this.createButton(x, y, -1, tab);
        }
    }

    private int createButton(int x, int y, int width, ConfigGuiTab tab) {
        ButtonGeneric button = new ButtonGeneric(x, y, width, 20, tab.getDisplayName());
        button.setEnabled(ConfigGui.tab != tab);
        this.addButton(button, new ButtonListener(tab, this));

        return button.getWidth() + 2;
    }

    @Override
    protected int getConfigWidth() {
        ConfigGuiTab tab = ConfigGui.tab;

        /*if (tab == ConfigGuiTab.GENERIC || tab == ConfigGuiTab.CHUNKLOADING) {
            return 100;
        }*/

        return super.getConfigWidth();
    }

    @Override
    public List<ConfigOptionWrapper> getConfigs() {
        List<? extends IConfigBase> configs;
        ConfigGuiTab tab = ConfigGui.tab;

        if (tab == ConfigGuiTab.SETTINGS) {
            configs = Configs.Settings.SETTINGS;
        }  else {
            return Collections.emptyList();
        }

        return ConfigOptionWrapper.createFor(configs);
    }

    @Override
    public void removed() {
        if (this.getListWidget().wereConfigsModified()) {
            this.getListWidget().applyPendingModifications();
            this.onSettingsChanged();
            this.getListWidget().clearConfigsModifiedFlag();
            Configs.saveToFile();
        }

        Configs.saveToFile();
        //this.client.keyboard.setRepeatEvents(false);
    }


    private static class ButtonListener implements IButtonActionListener {
        private final ConfigGui parent;
        private final ConfigGuiTab tab;

        public ButtonListener(ConfigGuiTab tab, ConfigGui parent) {
            this.tab = tab;
            this.parent = parent;
        }

        @Override
        public void actionPerformedWithButton(ButtonBase button, int mouseButton) {
            ConfigGui.tab = this.tab;

            this.parent.reCreateListWidget(); // apply the new config width
            this.parent.getListWidget().resetScrollbarPosition();
            this.parent.initGui();
        }
    }

    public enum ConfigGuiTab {
        SETTINGS("durabilityviewer.gui.button.config_gui.settings");

        private final String translationKey;

        public static final ImmutableList<ConfigGuiTab> VALUES = ImmutableList.copyOf(values());

        ConfigGuiTab(String translationKey) {
            this.translationKey = translationKey;
        }

        public String getDisplayName() {
            return StringUtils.translate(this.translationKey);
        }
    }
}
