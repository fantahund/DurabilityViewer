/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.guntram.mcmod.durabilityviewer.itemindicator;

import de.guntram.mcmod.durabilityviewer.config.Configs;
import net.minecraft.item.ItemStack;
import team.reborn.energy.api.EnergyStorage;
import team.reborn.energy.api.base.SimpleEnergyItem;

/**
 * @author gbl
 */
public class TREnergyIndicator implements ItemIndicator {

    private final ItemStack stack;
    private final double maxEnergy;

    public TREnergyIndicator(ItemStack stack) {
        this.stack = stack;
        if (stack.getItem() instanceof EnergyStorage) {
            maxEnergy = ((EnergyStorage) stack.getItem()).getCapacity();
        } else {
            maxEnergy = 0;
        }
    }

    @Override
    public String getDisplayValue() {
        double energy = 0.0;
        /*if (stack.getNbt() != null) {
           energy = stack.getNbt().getDouble("energy");
        }*/
        energy = SimpleEnergyItem.getStoredEnergyUnchecked(stack);
        if (Configs.Settings.Percentages.getBooleanValue() && maxEnergy > 0) {
            return String.format("§o%.1f%%", energy / maxEnergy * 100);
        }
        if (energy > 10_000_000) {
            return "§o" + ((int) (energy / 1000)) + "M";
        } else if (energy > 10_000) {
            return "§o" + ((int) (energy / 1000)) + "k";
        } else {
            return "§o" + (int) energy;
        }
    }

    @Override
    public int getDisplayColor() {
        double energy = 0.0;//stack.getNbt().getDouble("energy");
        energy = SimpleEnergyItem.getStoredEnergyUnchecked(stack);
        if (energy > maxEnergy * 0.2) {
            return color_green;
        } else if (energy > maxEnergy * 0.1) {
            return color_yellow;
        } else {
            return color_red;
        }
    }

    @Override
    public boolean isEmpty() {
        return stack.isEmpty();
    }

    @Override
    public boolean isItemStackDamageable() {
        return true;            // it is not, but we want to be displayed
    }

    @Override
    public ItemStack getItemStack() {
        return stack;
    }
}
