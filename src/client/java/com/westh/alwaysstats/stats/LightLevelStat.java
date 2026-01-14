package com.westh.alwaysstats.stats;

import net.minecraft.client.Minecraft;

public class LightLevelStat implements StatProvider {
    
    @Override
    public String getDisplayText(Minecraft client) {
        var pos = client.player.blockPosition();
        var lightLevel = client.level.getMaxLocalRawBrightness(pos);
        return "Light Level: " + lightLevel;
    }
}
