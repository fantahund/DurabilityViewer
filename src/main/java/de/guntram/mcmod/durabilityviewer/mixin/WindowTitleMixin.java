/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.guntram.mcmod.durabilityviewer.mixin;

import de.guntram.mcmod.durabilityviewer.DurabilityViewer;
import de.guntram.mcmod.durabilityviewer.config.Configs;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * @author gbl
 */
@Mixin(MinecraftClient.class)
public class WindowTitleMixin {

    @Inject(method = "getWindowTitle()Ljava/lang/String;", at = @At("HEAD"), cancellable = true)
    public final void getWindowTitle(CallbackInfoReturnable<String> cir) {
        if (Configs.Settings.SetWindowTitle.getBooleanValue()) {
            if (DurabilityViewer.getWindowTitle() != null) {
                cir.setReturnValue(DurabilityViewer.getWindowTitle());
            }
        }
    }


    /*@Shadow
    @Final
    private long handle;
    @Overwrite
    public void setTitle(String title) {
        if (DurabilityViewer.getWindowTitle() != null) {
            title = DurabilityViewer.getWindowTitle();
        }
        GLFW.glfwSetWindowTitle(this.handle, title);
    }*/
}
