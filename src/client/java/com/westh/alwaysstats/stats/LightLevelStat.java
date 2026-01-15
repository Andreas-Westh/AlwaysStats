package com.westh.alwaysstats.stats;

import net.minecraft.client.Minecraft;

public class LightLevelStat implements StatProvider {
    
    @Override
    public String getConfigKey() {
        return "lightLevel";
    }

    @Override
    public String getConfigName() {
        return "Light Level";
    }

    @Override
    public String getDisplayText(Minecraft client) {
        var pos = client.player.blockPosition();
        var lightLevel = client.level.getMaxLocalRawBrightness(pos);
        return getConfigName() + ": " + lightLevel;
    }
}
