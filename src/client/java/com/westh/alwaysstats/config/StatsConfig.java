package com.westh.alwaysstats.config;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;

import java.util.HashSet;
import java.util.Set;

@Config(name = "alwaysstats")
public class StatsConfig implements ConfigData {
    public ScreenCorner corner = ScreenCorner.TOP_LEFT;
    public FontSize fontSize = FontSize.MEDIUM;
    public boolean showBackground = true;
    public boolean alignRight = false;
    public boolean biomeDetails = false;
    public boolean directionDetails = false;
    public boolean targetDetails = false;

    // Custom position and scale (used when corner == CUSTOM)
    public int customX = 5;
    public int customY = 5;
    public float customScale = 0.75f; // Default to MEDIUM scale

    public Set<String> enabledStats = new HashSet<>();
    
    @Override
    public void validatePostLoad() {
        if (enabledStats.isEmpty()) {
            enabledStats.add("fps");
            enabledStats.add("biome");
            enabledStats.add("coords");
            enabledStats.add("direction");
            enabledStats.add("lightLevel");
            enabledStats.add("target");
        }
    }

    public static StatsConfig get() {
        return AutoConfig.getConfigHolder(StatsConfig.class).getConfig();
    }

    public static void save() {
        AutoConfig.getConfigHolder(StatsConfig.class).save();
    }
}