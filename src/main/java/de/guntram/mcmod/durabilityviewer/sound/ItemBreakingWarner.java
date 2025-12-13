/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.guntram.mcmod.durabilityviewer.sound;

import de.guntram.mcmod.durabilityviewer.DurabilityViewer;
import de.guntram.mcmod.durabilityviewer.config.Configs;
import de.guntram.mcmod.durabilityviewer.config.SoundCategory;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;


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
            location = Identifier.of(DurabilityViewer.MODID, "tool_breaking");
            sound = SoundEvent.of(location);
        }
    }

    public boolean checkBreaks(ItemStack stack) {
        lastStack = stack;
        if (stack == null || !stack.isDamageable())
            return false;
        int newDurability = stack.getMaxDamage() - stack.getDamage();
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
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null) {
            return;
        }
        ClientWorld world = MinecraftClient.getInstance().world;
        if (world != null) {
            world.playSound(player, player.getBlockPos(), sound, ((SoundCategory) Configs.Settings.SoundCategory.getOptionListValue()).getInternal(), 100, 100);
        }

    }
}
