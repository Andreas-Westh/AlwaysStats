package com.westh.alwaysstats.mixin.client;

import com.westh.alwaysstats.config.StatsConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.DeathScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DeathScreen.class)
public class DeathMixin {

    @Inject(method = "init", at = @At("HEAD"))
    private void onDeath(CallbackInfo ci) {
        var player = Minecraft.getInstance().player;
        if (player == null) return;

        StatsConfig config = StatsConfig.get();
        String worldKey = StatsConfig.getWorldKey();
        config.deathPoints.put(worldKey, new StatsConfig.DeathInfo(
            player.blockPosition().getX(),
            player.blockPosition().getY(),
            player.blockPosition().getZ(),
            player.level().dimension().identifier().getPath()
        ));
        StatsConfig.save();
    }
}
