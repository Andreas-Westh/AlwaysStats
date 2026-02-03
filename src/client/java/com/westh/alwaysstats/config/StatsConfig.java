package com.westh.alwaysstats.config;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;

import net.minecraft.client.Minecraft;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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
    public boolean lastDeathAutoRefresh = false;

    // Custom position and scale (used when corner == CUSTOM)
    public int customX = 5;
    public int customY = 5;
    public float customScale = 0.75f; // Default to MEDIUM scale

    // Death stats per world/server
    public Map<String, DeathInfo> deathPoints = new HashMap<>();

    public static class DeathInfo {
        public int x;
        public int y;
        public int z;
        public String dimension;

        public DeathInfo() {}

        public DeathInfo(int x, int y, int z, String dimension) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.dimension = dimension;
        }
    }

    public static String getWorldKey() {
        Minecraft client = Minecraft.getInstance();
        if (client.getSingleplayerServer() != null) {
            return "local/" + client.getSingleplayerServer().getWorldData().getLevelName();
        }
        if (client.getCurrentServer() != null) {
            return "server/" + client.getCurrentServer().ip;
        }
        return "unknown";
    }

    public Set<String> enabledStats = new HashSet<>();
    public List<String> statOrder = new ArrayList<>();

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

        // Initialize default stat order if empty
        if (statOrder.isEmpty()) {
            statOrder.addAll(List.of("fps", "biome", "coords", "direction",
                                      "lightLevel", "target", "timeOfDay", "lastDeath"));
        }

        // Ensure any stats missing from statOrder are added
        for (var stat : com.westh.alwaysstats.render.StatsRenderer.getAllStats()) {
            if (!statOrder.contains(stat.getConfigKey())) {
                statOrder.add(stat.getConfigKey());
            }
        }
    }

    public static StatsConfig get() {
        return AutoConfig.getConfigHolder(StatsConfig.class).getConfig();
    }

    public static void save() {
        AutoConfig.getConfigHolder(StatsConfig.class).save();
    }
}