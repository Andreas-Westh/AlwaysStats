package com.westh.alwaysstats.stats;

import com.westh.alwaysstats.config.StatsConfig;
import net.minecraft.client.Minecraft;

public class LastDeathStat implements StatProvider {
    @Override
    public String getConfigKey() { return "lastDeath"; }

    @Override
    public String getConfigName() { return "Last Death";}

    @Override
    public String getDisplayText(Minecraft client) {
        StatsConfig config = StatsConfig.get();
        String worldKey = StatsConfig.getWorldKey();
        StatsConfig.DeathInfo death = config.deathPoints.get(worldKey);
        if (death == null) return getConfigName() + ": None :)";

        if (config.lastDeathAutoRefresh && client.player != null) {
            int px = client.player.blockPosition().getX();
            int py = client.player.blockPosition().getY();
            int pz = client.player.blockPosition().getZ();
            if (px == death.x && py == death.y && pz == death.z) {
                config.deathPoints.remove(worldKey);
                StatsConfig.save();
                return getConfigName() + ": None :)";
            }
        }

        return getConfigName() + ": " + death.x + ", "
            + death.y + ", " + death.z + " (" + death.dimension + ")";
    }
}
