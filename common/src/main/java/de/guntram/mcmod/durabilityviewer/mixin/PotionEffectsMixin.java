package de.guntram.mcmod.durabilityviewer.mixin;

import de.guntram.mcmod.durabilityviewer.client.gui.GuiItemDurability;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public abstract class PotionEffectsMixin {

    @Unique
    private static GuiItemDurability gui;

    @Inject(method = "renderEffects", at = @At("RETURN"))
    private void afterRenderStatusEffects(GuiGraphics context, DeltaTracker tickCounter, CallbackInfo ci) {
        if (gui == null)
            gui = new GuiItemDurability();
        gui.afterRenderStatusEffects(context, 0);
    }

    @Inject(method = "renderHotbarAndDecorations", at = @At(value = "RETURN", opcode = Opcodes.GETFIELD, args = {"log=false"}))
    private void beforeRenderDebugScreen(GuiGraphics context, DeltaTracker tickCounter, CallbackInfo ci) {
        if (gui == null)
            gui = new GuiItemDurability();
        gui.onRenderGameOverlayPost(context, 0);
    }
}
