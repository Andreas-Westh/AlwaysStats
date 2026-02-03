package com.westh.alwaysstats.mixin.client;

import com.westh.alwaysstats.config.StatsConfig;
import net.minecraft.client.gui.screens.DeathScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DeathScreen.class)
public class DeathMixin {

    @Inject(method = "<init>", at = @At("TAIL"))
    private void onDeath(Component causeOfDeath, boolean hardcore, LocalPlayer player, CallbackInfo ci) {
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
