package com.westh.alwaysstats.stats;

import net.minecraft.client.Minecraft;

public class BiomeStat implements StatProvider {
    
    @Override
    public String getConfigKey() {
        return "biome";
    }

    @Override
    public String getConfigName() {
        return "Biome";
    }

    @Override
    public String getDisplayText(Minecraft client) {
        var pos = client.player.blockPosition();
        var biomeHolder = client.level.getBiome(pos);
        String biomeName = biomeHolder.getRegisteredName();
        
        if (biomeName.startsWith("minecraft:")) {
            biomeName = biomeName.substring(10);
        }
        
        return getConfigName() + ": " + biomeName;
    }
}
