/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.guntram.mcmod.durabilityviewer.sound;

import de.guntram.mcmod.durabilityviewer.config.Configs;
import de.guntram.mcmod.durabilityviewer.config.SoundCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.ItemStack;


/**
 * @author gbl
 */
public class ItemBreakingWarner {
    private int lastDurability;
    private ItemStack lastStack;
    private static SoundEvent sound;

    public ItemBreakingWarner() {
        lastDurability = 1000;
        lastStack = null;
        Identifier location;

        if (sound == null) {
            location = Identifier.fromNamespaceAndPath("DurabilityViewer.MODID", "tool_breaking"); //FIOXME
            sound = SoundEvent.createVariableRangeEvent(location);
        }
    }

    public boolean checkBreaks(ItemStack stack) {
        lastStack = stack;
        if (stack == null || !stack.isDamageableItem())
            return false;
        int newDurability = stack.getMaxDamage() - stack.getDamageValue();
        if (newDurability < lastDurability
                && newDurability < Configs.Settings.SoundBelowDurability.getIntegerValue()
                && newDurability * 100 / Configs.Settings.SoundBelowPercent.getIntegerValue() < stack.getMaxDamage()) {
            lastDurability = newDurability;
            return true;
        }
        lastDurability = newDurability;
        return false;
    }

    public static void playWarningSound() {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) {
            return;
        }
        ClientLevel world = Minecraft.getInstance().level;
        if (world != null) {
            world.playSound(player, player.blockPosition(), sound, ((SoundCategory) Configs.Settings.SoundCategory.getOptionListValue()).getInternal(), 100, 100);
        }
    }
}
