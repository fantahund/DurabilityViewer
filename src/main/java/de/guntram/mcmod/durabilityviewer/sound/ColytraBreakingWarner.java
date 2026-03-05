package de.guntram.mcmod.durabilityviewer.sound;

import de.guntram.mcmod.durabilityviewer.config.Configs;
import net.minecraft.world.item.ItemStack;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * @author gbl
 */
public class ColytraBreakingWarner extends ItemBreakingWarner {

    private static int elytraMaxDamage;

    int lastDurability;

    @Override
    public boolean checkBreaks(ItemStack stack) {
        return false;
        /*if (stack.getNbt() == null || !stack.getNbt().contains("colytra:ElytraUpgrade")) {
            return false;
        }

        int damage;
        try {
            damage = 0;//stack.getNbt().getCompound("colytra:ElytraUpgrade").getCompound("tag").getInt("Damage");
        } catch (Exception ex) {
            return false;
        }

        if (elytraMaxDamage == 0) {
            elytraMaxDamage = new ItemStack(Items.ELYTRA).getMaxDamage();
        }

        int newDurability = elytraMaxDamage - damage;
        if (newDurability < lastDurability
                && newDurability < Configs.Settings.SoundBelowDurability.getIntegerValue()
                && newDurability * 100 / Configs.Settings.SoundBelowPercent.getIntegerValue() < elytraMaxDamage) {
            lastDurability = newDurability;
            return true;
        }
        lastDurability = newDurability;
        return false;*/
    }
}
