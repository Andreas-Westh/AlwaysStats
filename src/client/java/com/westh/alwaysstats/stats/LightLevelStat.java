package com.westh.alwaysstats.stats;

import com.westh.alwaysstats.config.StatsConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.world.level.LightLayer;

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

        StatsConfig config = StatsConfig.get();
        String text = getConfigName() + ": " + lightLevel;

        if (config.lightLevelDetails && lightLevel <= 7) {
            text += "!";
        }

        if (config.lightLevelSplit) {
            int blockLight = client.level.getBrightness(LightLayer.BLOCK, pos);
            int skyLight = client.level.getBrightness(LightLayer.SKY, pos);
            text += " (Block: " + blockLight + ", Sky: " + skyLight + ")";
        }

        return text;
    }
}
