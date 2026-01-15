package com.westh.alwaysstats.config;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;

@Config(name = "alwaysstats")
public class StatsConfig implements ConfigData {
    public ScreenCorner corner = ScreenCorner.TOP_LEFT;

    public static StatsConfig get() {
        return AutoConfig.getConfigHolder(StatsConfig.class).getConfig();
    }

    public static void save() {
        AutoConfig.getConfigHolder(StatsConfig.class).save();
    }
}

