package de.guntram.mcmod.durabilityviewer.client.gui;

import com.google.common.collect.Ordering;
import com.mojang.blaze3d.systems.RenderSystem;
import de.guntram.mcmod.durabilityviewer.config.Configs;
import de.guntram.mcmod.durabilityviewer.config.WarnMode;
import de.guntram.mcmod.durabilityviewer.itemindicator.ColytraDamageIndicator;
import de.guntram.mcmod.durabilityviewer.itemindicator.InventorySlotsIndicator;
import de.guntram.mcmod.durabilityviewer.itemindicator.ItemCountIndicator;
import de.guntram.mcmod.durabilityviewer.itemindicator.ItemDamageIndicator;
import de.guntram.mcmod.durabilityviewer.itemindicator.ItemIndicator;
import de.guntram.mcmod.durabilityviewer.itemindicator.TREnergyIndicator;
import de.guntram.mcmod.durabilityviewer.sound.ColytraBreakingWarner;
import de.guntram.mcmod.durabilityviewer.sound.ItemBreakingWarner;
import dev.emi.trinkets.api.TrinketComponent;
import dev.emi.trinkets.api.TrinketsApi;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArrowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.RangedWeaponItem;
import net.minecraft.util.Arm;
import net.minecraft.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joml.Matrix4fStack;
import team.reborn.energy.api.EnergyStorage;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;


public class GuiItemDurability {

    private static final Logger LOGGER = LogManager.getLogger();
    private final MinecraftClient minecraft;
    private static boolean visible;
    private final TextRenderer fontRenderer;
    private final ItemRenderer itemRenderer;

    private long lastWarningTime;
    private ItemStack lastWarningItem;

    private static final int iconWidth = 16;
    private static final int iconHeight = 16;
    private static final int spacing = 2;

    private static boolean haveTrinketsApi = false;
    private static boolean haveTRCore = false;

    private final ItemBreakingWarner mainHandWarner, offHandWarner, helmetWarner, chestWarner, pantsWarner, bootsWarner;
    private final ItemBreakingWarner colytraWarner;
    private ItemBreakingWarner[] trinketWarners;

    public static void toggleVisibility() {
        visible = !visible;
    }

    public GuiItemDurability() {
        minecraft = MinecraftClient.getInstance();
        fontRenderer = minecraft.textRenderer;
        itemRenderer = minecraft.getItemRenderer();
        visible = true;

        mainHandWarner = new ItemBreakingWarner();
        offHandWarner = new ItemBreakingWarner();
        helmetWarner = new ItemBreakingWarner();
        chestWarner = new ItemBreakingWarner();
        pantsWarner = new ItemBreakingWarner();
        bootsWarner = new ItemBreakingWarner();
        colytraWarner = new ColytraBreakingWarner();

        try {
            Class.forName("dev.emi.trinkets.api.TrinketsApi");
            LOGGER.info("Using trinkets in DurabilityViewer");
            haveTrinketsApi = true;
            trinketWarners = new ItemBreakingWarner[getTrinketSlotCount(minecraft.player)];
            for (int i = 0; i < trinketWarners.length; i++) {
                trinketWarners[i] = new ItemBreakingWarner();
            }
        } catch (ClassNotFoundException ex) {
            LOGGER.info("DurabilityViewer did not find Trinkets API");
            trinketWarners = new ItemBreakingWarner[0];
        }
        try {
            Class.forName("team.reborn.energy.api.EnergyStorage");
            haveTRCore = true;
        } catch (ClassNotFoundException ex) {
            LOGGER.info("DurabilityViewer did not find Tech Reborn");
        }
    }

    private int getInventoryArrowCount() {
        int arrows = 0;
        for (final ItemStack stack : minecraft.player.getInventory().main) {
            if (isArrow(stack)) {
                arrows += stack.getCount();
            }
        }
        return arrows;
    }

    private ItemStack getFirstArrowStack() {
        if (isArrow(minecraft.player.getOffHandStack())) {
            return minecraft.player.getOffHandStack();
        }
        if (isArrow(minecraft.player.getMainHandStack())) {
            return minecraft.player.getMainHandStack();
        }
        int size = minecraft.player.getInventory().size();
        for (int i = 0; i < size; ++i) {
            final ItemStack itemstack = minecraft.player.getInventory().getStack(i);
            if (this.isArrow(itemstack)) {
                return itemstack;
            }
        }
        return null;
    }

    private boolean isArrow(final ItemStack stack) {
        return !stack.isEmpty() && stack.getItem() instanceof ArrowItem;
    }

    private static class RenderSize {
        int width;
        int height;

        RenderSize(int w, int h) {
            width = w;
            height = h;
        }
    }

    private enum RenderPos {
        left, over, right;
    }

    public void onRenderGameOverlayPost(DrawContext context, float partialTicks) {

        PlayerEntity player = minecraft.player;
        ItemStack needToWarn = null;

        ItemIndicator mainHand, offHand;
        mainHand = damageOrEnergy(player, EquipmentSlot.MAINHAND);
        offHand = damageOrEnergy(player, EquipmentSlot.OFFHAND);

        ItemStack chestItem = player.getEquippedStack(EquipmentSlot.CHEST);
        ItemIndicator colytra = null;
        /*if (chestItem != null && chestItem.getNbt() != null && chestItem.getNbt().contains("colytra:ElytraUpgrade")) {
            colytra = new ColytraDamageIndicator(chestItem);
        }*/

        ItemIndicator boots = new ItemDamageIndicator(player.getEquippedStack(EquipmentSlot.FEET));
        ItemIndicator leggings = new ItemDamageIndicator(player.getEquippedStack(EquipmentSlot.LEGS));
        ItemIndicator chestplate = new ItemDamageIndicator(chestItem);
        ItemIndicator helmet = new ItemDamageIndicator(player.getEquippedStack(EquipmentSlot.HEAD));
        ItemIndicator arrows = null;
        ItemIndicator invSlots = (Configs.Settings.ShowFreeInventorySlots.getBooleanValue() ? new InventorySlotsIndicator(minecraft.player.getInventory()) : null);

        if (mainHandWarner.checkBreaks(player.getEquippedStack(EquipmentSlot.MAINHAND)))
            needToWarn = player.getEquippedStack(EquipmentSlot.MAINHAND);
        if (needToWarn == null && offHandWarner.checkBreaks(player.getEquippedStack(EquipmentSlot.OFFHAND)))
            needToWarn = player.getEquippedStack(EquipmentSlot.OFFHAND);
        if (needToWarn == null && bootsWarner.checkBreaks(player.getEquippedStack(EquipmentSlot.FEET)))
            needToWarn = player.getEquippedStack(EquipmentSlot.FEET);
        if (needToWarn == null && pantsWarner.checkBreaks(player.getEquippedStack(EquipmentSlot.LEGS)))
            needToWarn = player.getEquippedStack(EquipmentSlot.LEGS);
        if (needToWarn == null && chestWarner.checkBreaks(chestItem)) needToWarn = chestItem;
        if (needToWarn == null && helmetWarner.checkBreaks(player.getEquippedStack(EquipmentSlot.HEAD)))
            needToWarn = player.getEquippedStack(EquipmentSlot.HEAD);
        if (needToWarn == null && colytraWarner.checkBreaks(chestItem)) needToWarn = chestItem;

        ItemIndicator[] trinkets;
        if (haveTrinketsApi) {
            List<ItemStack> equipped = getTrinkets(player);
            if (equipped == null) {
                equipped = Collections.emptyList();
            }

            trinkets = new ItemIndicator[equipped.size()];
            if (trinkets.length > trinketWarners.length) {
                // Apparently this can happen when joining a server that defines 
                // more trinkets than the client?
                trinketWarners = new ItemBreakingWarner[trinkets.length];
                for (int i = 0; i < trinketWarners.length; i++) {
                    trinketWarners[i] = new ItemBreakingWarner();
                }
            }
            LOGGER.debug("know about " + trinkets.length + " trinkets, invSize is " + equipped.size() + ", have " + trinketWarners.length + " warners");
            for (int i = 0; i < trinkets.length; i++) {
                trinkets[i] = new ItemDamageIndicator(equipped.get(i), Configs.Settings.ShowAllTrinkets.getBooleanValue());
                if (needToWarn == null && trinketWarners[i].checkBreaks(equipped.get(i))) {
                    needToWarn = equipped.get(i);
                }
                LOGGER.debug("trinket position " + i + " has item " + equipped.get(i).getItem().toString());
            }
        } else {
            trinkets = new ItemIndicator[0];
        }

        WarnMode warnMode = (WarnMode) Configs.Settings.WarningMode.getOptionListValue();
        if (needToWarn != null) {
            if (warnMode == WarnMode.SOUND || warnMode == WarnMode.BOTH) {
                ItemBreakingWarner.playWarningSound();
            }
            lastWarningTime = System.currentTimeMillis();
            lastWarningItem = needToWarn;
        }

        long timeSinceLastWarning = System.currentTimeMillis() - lastWarningTime;
        if (timeSinceLastWarning < 1000 && (warnMode == WarnMode.VISUAL || warnMode == WarnMode.BOTH)) {
            renderItemBreakingOverlay(context, lastWarningItem, timeSinceLastWarning);
        }

        // Moved this check to down here, in order to play the 
        // warning sound / do the visible 
        if (!visible || minecraft.getDebugHud().shouldShowDebugHud()) {
            return;
        }


        if (mainHand.getItemStack().getItem() instanceof RangedWeaponItem
                || offHand.getItemStack().getItem() instanceof RangedWeaponItem) {
            arrows = new ItemCountIndicator(getFirstArrowStack(), getInventoryArrowCount());
        }

        Window mainWindow = MinecraftClient.getInstance().getWindow();
        RenderSize armorSize, toolsSize, trinketsSize;
        if (Configs.Settings.ArmorAroundHotbar.getBooleanValue()) {
            armorSize = new RenderSize(0, 0);
        } else {
            armorSize = this.renderItems(context, 0, 0, false, RenderPos.left, 0, boots, leggings, colytra, chestplate, helmet);
        }
        toolsSize = this.renderItems(context, 0, 0, false, RenderPos.right, 0, invSlots, mainHand, offHand, arrows);
        trinketsSize = this.renderItems(context, 0, 0, false, RenderPos.left, 0, trinkets);

        int totalHeight = (Math.max(toolsSize.height, armorSize.height));
        if (trinketsSize.height > totalHeight) {
            totalHeight = trinketsSize.height;
        }
        if (trinketsSize.width == 0 && trinkets.length > 0 && Configs.Settings.ShowAllTrinkets.getBooleanValue()) {
            trinketsSize.width = iconWidth + spacing * 2;
        }
        int xposArmor, xposTools, xposTrinkets, ypos;

        Corner corner = (Corner) Configs.Settings.HUDCorner.getOptionListValue();
        switch (corner) {
            case TOP_LEFT -> {
                xposArmor = 5;
                xposTools = 5 + armorSize.width;
                xposTrinkets = 5 + armorSize.width + trinketsSize.width;
                ypos = 5;
            }
            case TOP_RIGHT -> {
                xposArmor = mainWindow.getScaledWidth() - 5 - armorSize.width;
                xposTools = xposArmor - toolsSize.width;
                xposTrinkets = xposTools - trinketsSize.width;
                ypos = 60;   // below buff/debuff effects
            }
            case BOTTOM_LEFT -> {
                xposArmor = 5;
                xposTools = 5 + armorSize.width;
                xposTrinkets = 5 + armorSize.width + trinketsSize.width;
                ypos = mainWindow.getScaledHeight() - 5 - totalHeight;
            }
            case BOTTOM_RIGHT -> {
                xposArmor = mainWindow.getScaledWidth() - 5 - armorSize.width;
                xposTools = mainWindow.getScaledWidth() - 5 - armorSize.width - toolsSize.width;
                xposTrinkets = xposTools - trinketsSize.width;
                ypos = mainWindow.getScaledHeight() - 5 - totalHeight;
            }
            default -> {
                return;
            }
        }

        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);

        if (Configs.Settings.ArmorAroundHotbar.getBooleanValue()) {
            int leftOffset = -120;
            int rightOffset = 100;
            if (!player.getEquippedStack(EquipmentSlot.OFFHAND).isEmpty()) {
                if (minecraft.options.getMainArm().getValue() == Arm.RIGHT) {
                    leftOffset -= 20;
                } else {
                    rightOffset += 20;
                }
            }
            int helmetTextWidth = fontRenderer.getWidth(helmet.getDisplayValue());
            int chestTextWidth = fontRenderer.getWidth(chestplate.getDisplayValue());
            this.renderItems(context, mainWindow.getScaledWidth() / 2 + leftOffset - helmetTextWidth, mainWindow.getScaledHeight() - iconHeight * 2 - 2, true, RenderPos.left, helmetTextWidth + iconWidth + spacing, helmet);
            this.renderItems(context, mainWindow.getScaledWidth() / 2 + leftOffset - chestTextWidth, mainWindow.getScaledHeight() - iconHeight - 2, true, RenderPos.left, chestTextWidth + iconWidth + spacing, chestplate);
            if (colytra != null) {
                int colytraTextWidth = fontRenderer.getWidth(colytra.getDisplayValue());
                this.renderItems(context, mainWindow.getScaledWidth() / 2 + leftOffset - chestTextWidth - colytraTextWidth - iconWidth, mainWindow.getScaledHeight() - iconHeight - 2, true, RenderPos.left, colytraTextWidth + iconWidth + spacing, colytra);
            }
            this.renderItems(context, mainWindow.getScaledWidth() / 2 + rightOffset, mainWindow.getScaledHeight() - iconHeight * 2 - 2, true, RenderPos.right, armorSize.width, leggings);
            this.renderItems(context, mainWindow.getScaledWidth() / 2 + rightOffset, mainWindow.getScaledHeight() - iconHeight - 2, true, RenderPos.right, armorSize.width, boots);
            if (corner.isRight()) {
                xposTools += armorSize.width;
            } else {
                xposTools -= armorSize.width;
            }
        } else {
            this.renderItems(context, xposArmor, ypos, true, corner.isLeft() ? RenderPos.left : RenderPos.right, armorSize.width, helmet, chestplate, colytra, leggings, boots);
        }
        this.renderItems(context, xposTools, ypos, true, corner.isRight() ? RenderPos.right : RenderPos.left, toolsSize.width, invSlots, mainHand, offHand, arrows);
        this.renderItems(context, xposTrinkets, ypos, true, corner.isRight() ? RenderPos.right : RenderPos.left, trinketsSize.width, trinkets);
    }

    private ItemIndicator damageOrEnergy(PlayerEntity player, EquipmentSlot slot) {
        ItemStack stack = player.getEquippedStack(slot);
        if (stack.isDamageable()) {
            return new ItemDamageIndicator(stack);
        } else if (haveTRCore) {
            /*if (stack.getItem() instanceof EnergyHolder && stack.getNbt() != null && stack.getNbt().contains("energy", 6)) {
                return new TREnergyIndicator(stack);
            }*/
        }
        return new ItemDamageIndicator(stack);
    }

    private void renderItemBreakingOverlay(DrawContext context, ItemStack itemStack, long timeDelta) {
        Window mainWindow = MinecraftClient.getInstance().getWindow();
        float alpha = 1.0f - ((float) timeDelta / 1000.0f);
        float xWarn = mainWindow.getScaledWidth() / 2f;
        float yWarn = mainWindow.getScaledHeight() / 2f;
        float scale = 5.0f;

        context.fill(0, 0, mainWindow.getScaledWidth(), mainWindow.getScaledHeight(), 0xff0000 + ((int) (alpha * 128) << 24));

        Matrix4fStack stack = RenderSystem.getModelViewStack();
        stack.pushMatrix();
        stack.scale(scale, scale, scale);
        RenderSystem.applyModelViewMatrix();

        context.drawItem(itemStack, (int) ((xWarn) / scale - 8), (int) ((yWarn) / scale - 8));

        stack.popMatrix();
        RenderSystem.applyModelViewMatrix();

        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
    }

    public void afterRenderStatusEffects(DrawContext context, float partialTicks) {
        if (Configs.Settings.EffectDuration.getBooleanValue()) {
            // a lot of this is copied from net/minecraft/client/gui/GuiIngame.java
            Window mainWindow = MinecraftClient.getInstance().getWindow();
            Collection<StatusEffectInstance> collection = minecraft.player.getStatusEffects();
            int posGood = 0, posBad = 0;
            for (StatusEffectInstance potioneffect : Ordering.natural().reverse().sortedCopy(collection)) {
                if (potioneffect.shouldShowIcon()) {
                    StatusEffect potion = potioneffect.getEffectType().value();
                    int xpos = mainWindow.getScaledWidth();
                    int ypos;
                    if (potion.isBeneficial()) {     // isBeneficial
                        posGood += 25;
                        xpos -= posGood;
                        ypos = 15;
                    } else {
                        posBad += 25;
                        xpos -= posBad;
                        ypos = 41;
                    }
                    int duration = potioneffect.getDuration();
                    String show;
                    if (duration > 1200)
                        show = (duration / 1200) + "m";
                    else
                        show = (duration / 20) + "s";
                    context.drawText(fontRenderer, show, xpos + 2, ypos, ItemIndicator.color_yellow, true);
                }
            }
        }
    }

    private RenderSize renderItems(DrawContext context, int xpos, int ypos, boolean reallyDraw, RenderPos numberPos, int maxWidth, ItemIndicator... items) {
        RenderSize result = new RenderSize(0, 0);

        for (ItemIndicator item : items) {
            if (item != null && !item.isEmpty() && item.isItemStackDamageable()) {
                String displayString = item.getDisplayValue();
                int width = fontRenderer.getWidth(displayString);
                if (width > result.width)
                    result.width = width;
                if (reallyDraw) {
                    int color = item.getDisplayColor();
                    context.drawItem(item.getItemStack(), numberPos == RenderPos.left ? xpos + maxWidth - iconWidth - spacing : xpos, ypos + result.height);
                    context.drawText(fontRenderer, displayString, numberPos != RenderPos.right ? xpos : xpos + iconWidth + spacing, (int) (ypos + result.height + fontRenderer.fontHeight / 2f + (numberPos == RenderPos.over ? 10 : 0)), color, true);
                }
                result.height += 16;
            }
        }
        if (result.width != 0)
            result.width += iconWidth + spacing * 2;
        return result;
    }

    public int getTrinketSlotCount(LivingEntity player) {
        Optional<TrinketComponent> component = TrinketsApi.getTrinketComponent(player);
        return 0;//component.map(trinketComponent -> trinketComponent.getEquipped(prdct -> true).size()).orElse(0);
    }

    public List<ItemStack> getTrinkets(LivingEntity player) {
        Optional<TrinketComponent> component = TrinketsApi.getTrinketComponent(player);
        return null;//component.map(trinketComponent -> trinketComponent.getEquipped(prdct -> true).stream().map(Pair::getRight).toList()).orElse(null);
    }
}
