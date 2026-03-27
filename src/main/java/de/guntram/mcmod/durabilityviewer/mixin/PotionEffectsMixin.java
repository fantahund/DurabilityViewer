package de.guntram.mcmod.durabilityviewer.mixin;

import de.guntram.mcmod.durabilityviewer.client.gui.GuiItemDurability;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphicsExtractor;
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

    @Inject(method = "extractEffects", at = @At("RETURN"))
    private void afterRenderStatusEffects(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        if (gui == null)
            gui = new GuiItemDurability();
        gui.afterRenderStatusEffects(graphics, 0);
    }

    @Inject(method = "extractHotbarAndDecorations", at = @At(value = "RETURN", opcode = Opcodes.GETFIELD, args = {"log=false"}))
    private void beforeRenderDebugScreen(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        if (gui == null)
            gui = new GuiItemDurability();
        gui.onRenderGameOverlayPost(graphics, 0);
    }
}
