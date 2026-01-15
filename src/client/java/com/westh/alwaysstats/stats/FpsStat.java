package com.westh.alwaysstats.stats;

import net.minecraft.client.Minecraft;

public class FpsStat implements StatProvider {
    
    @Override
    public String getConfigKey() {
        return "fps";
    }

    @Override
    public String getConfigName() {
        return "FPS";
    }

    @Override 
    public String getDisplayText(Minecraft client) {
        return getConfigName() + ": " + client.getFps();
    }
}
