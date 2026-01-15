package com.westh.alwaysstats.stats;

import net.minecraft.client.Minecraft;

public class CoordStat implements StatProvider {
    
    @Override
    public String getConfigKey() {
        return "coords";
    }

    @Override
    public String getConfigName() {
        return "Coords";
    }

    @Override
    public String getDisplayText(Minecraft client) {
        return getConfigName() + ": " + client.player.blockPosition().getX() + ", " + client.player.blockPosition().getY() + ", " + client.player.blockPosition().getZ();
    }
}
