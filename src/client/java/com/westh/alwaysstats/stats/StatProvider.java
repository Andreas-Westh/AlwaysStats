package com.westh.alwaysstats.stats;

import net.minecraft.client.Minecraft;

public interface StatProvider {
    String getDisplayText(Minecraft client);

    String getConfigKey(); 

    String getConfigName(); 
}
