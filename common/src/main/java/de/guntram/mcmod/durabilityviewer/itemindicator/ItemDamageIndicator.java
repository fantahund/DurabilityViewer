package de.guntram.mcmod.durabilityviewer.itemindicator;

import de.guntram.mcmod.durabilityviewer.config.Configs;
import net.minecraft.world.item.ItemStack;

public class ItemDamageIndicator implements ItemIndicator {

    final ItemStack stack;
    boolean alwaysAssumeDamageable;

    public ItemDamageIndicator(ItemStack stack) {
        this(stack, false);
    }

    public ItemDamageIndicator(ItemStack stack, boolean alwaysDamageable) {
        this.stack = stack;
        this.alwaysAssumeDamageable = alwaysDamageable;
    }

    @Override
    public String getDisplayValue() {
        if (!(stack.isDamageableItem())) {
            return "";
        }
        return calculateDisplayValue(stack.getMaxDamage(), stack.getDamageValue());
    }

    public static String calculateDisplayValue(int max, int dam) {
        int cur = max - dam;

        int shown;
        if (cur > max * Configs.Settings.PercentToShowDamage.getIntegerValue() / 100) {
            shown = -dam;
        } else {
            shown = cur;
        }
        if (Configs.Settings.Percentages.getBooleanValue()) {
            return String.format("%.1f%%", shown * 100.0 / max);
        }
        return String.valueOf(shown);
    }

    @Override
    public int getDisplayColor() {
        int max = stack.getMaxDamage();
        int cur = stack.getDamageValue();
        return calculateDisplayColor(max, cur);
    }

    public static int calculateDisplayColor(int max, int cur) {
        if (cur < max / 5)
            return color_green;
        if (cur > max * 9 / 10 && cur > max - 100)
            return color_red;
        if (cur > max * 4 / 5 && cur > max - 200)
            return color_yellow;
        return color_white;
    }

    @Override
    public boolean isEmpty() {
        return stack.isEmpty() || (stack.getMaxDamage() - stack.getDamageValue() > stack.getMaxDamage() * Configs.Settings.HideDamageOverPercent.getIntegerValue() / 100);
    }

    @Override
    public boolean isItemStackDamageable() {
        return alwaysAssumeDamageable || stack.isDamageableItem();
    }

    @Override
    public ItemStack getItemStack() {
        return stack;
    }
}
