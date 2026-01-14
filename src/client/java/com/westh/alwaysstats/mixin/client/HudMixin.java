package com.westh.alwaysstats.mixin.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.DeltaTracker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public class HudMixin {

    @Inject(method = "render", at = @At("TAIL"))
    private void renderFpsOverlay(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        Minecraft client = Minecraft.getInstance();
        
        if (client.getDebugOverlay().showDebugScreen()) {
            return;
        }

        int fps = client.getFps();
        String text = "FPS: " + fps;
        
        // Draw background for visibility
        int textWidth = client.font.width(text);
        guiGraphics.fill(3, 3, 7 + textWidth, 14, 0x90000000);
        
        // Draw text (color needs full alpha: 0xAARRGGBB)
        guiGraphics.drawString(client.font, text, 5, 5, 0xFFFFFFFF);
    }
}
