package com.westh.alwaysstats.mixin.client;

import com.westh.alwaysstats.render.StatsRenderer;
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
    private void renderStatsOverlay(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        Minecraft client = Minecraft.getInstance();
        
        if (client.getDebugOverlay().showDebugScreen()) {
            return;
        }
        
        if (client.level == null || client.player == null) {
            return;
        }

        StatsRenderer.render(guiGraphics, client);
    }
}
