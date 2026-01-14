package com.westh.alwaysstats.stats;

import net.minecraft.client.Minecraft;

public class FpsStat implements StatProvider {
    
    @Override
    public String getDisplayText(Minecraft client) {
        return "FPS: " + client.getFps();
    }
}
