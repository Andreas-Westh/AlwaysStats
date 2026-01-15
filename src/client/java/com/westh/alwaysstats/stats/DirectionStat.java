package com.westh.alwaysstats.stats;

import com.westh.alwaysstats.config.StatsConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;

public class DirectionStat implements StatProvider {

    @Override
    public String getConfigKey() {
        return "direction";
    }

    @Override
    public String getConfigName() {
        return "Direction";
    }

    @Override
    public String getDisplayText(Minecraft client) {
        Direction direction = Direction.fromYRot(client.player.getYRot());
        String directionName = direction.getName();

        if (StatsConfig.get().directionDetails) {
            float yaw = Mth.wrapDegrees(client.player.getYRot());
            return getConfigName() + ": " + directionName + " (" + String.format("%.1f", yaw) + "°)";
        }

        return getConfigName() + ": " + directionName;
    }
}