package com.westh.alwaysstats.stats;

import net.minecraft.client.Minecraft;
import net.minecraft.core.Direction;

public class DirectionStat implements StatProvider {
    
    @Override
    public String getDisplayText(Minecraft client) {
        Direction direction = Direction.fromYRot(client.player.getYRot());
        return "Direction: " + direction.getName();
    }
}