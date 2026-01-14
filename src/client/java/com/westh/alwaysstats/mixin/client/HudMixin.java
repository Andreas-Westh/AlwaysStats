package com.westh.alwaysstats.mixin.client;

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
        
        if (client.level == null || client.player == null) {
            return;
        }

        int fps = client.getFps();
        String fpsText = "FPS: " + fps;

        // Get biome at player's position
        var pos = client.player.blockPosition();
        var biomeHolder = client.level.getBiome(pos);
        String biomeName = biomeHolder.getRegisteredName();
        if (biomeName.startsWith("minecraft:")) {
            biomeName = biomeName.substring(10);
        }
        String biomeText = "Biome: " + biomeName;
        
        // Draw background (sized to fit both lines)
        int maxWidth = Math.max(client.font.width(fpsText), client.font.width(biomeText));
        guiGraphics.fill(3, 3, 7 + maxWidth, 28, 0x90000000);
        
        // Draw text
        guiGraphics.drawString(client.font, fpsText, 5, 5, 0xFFFFFFFF);
        guiGraphics.drawString(client.font, biomeText, 5, 16, 0xFFFFFFFF);
    }
}
