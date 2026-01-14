package com.westh.alwaysstats.stats;

import net.minecraft.client.Minecraft;

public class CoordStat implements StatProvider {
    
    @Override
    public String getDisplayText(Minecraft client) {
        if (client.level == null || client.player == null) {
            return null;
        }
        
        var pos = client.player.blockPosition();
        return "Coords: " + pos.getX() + ", " + pos.getY() + ", " + pos.getZ();
    }
}
