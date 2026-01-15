package com.westh.alwaysstats.stats;

import net.minecraft.client.Minecraft;
import net.minecraft.core.Direction;

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
        return getConfigName() + ": " + direction.getName();
    }
}