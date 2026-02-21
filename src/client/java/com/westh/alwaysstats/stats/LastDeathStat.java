package com.westh.alwaysstats.stats;

import com.westh.alwaysstats.config.StatsConfig;
import net.minecraft.client.Minecraft;

public class LastDeathStat implements StatProvider {

    // Distance in blocks to consider the player has left the death area (pre respawm)
    // Without this, death point is cleared before the player even respawns
    private static final int LEAVE_DISTANCE = 5;
    private boolean hasLeftDeathArea = false;

    @Override
    public String getConfigKey() { return "lastDeath"; }

    @Override
    public String getConfigName() { return "Last Death";}

    @Override
    public String getDisplayText(Minecraft client) {
        StatsConfig config = StatsConfig.get();
        String worldKey = StatsConfig.getWorldKey();
        StatsConfig.DeathInfo death = config.deathPoints.get(worldKey);
        if (death == null) {
            hasLeftDeathArea = false;
            return getConfigName() + ": None :)";
        }

        if (config.lastDeathAutoRefresh && client.player != null) {
            int px = client.player.blockPosition().getX();
            int py = client.player.blockPosition().getY();
            int pz = client.player.blockPosition().getZ();
            int dx = px - death.x;
            int dy = py - death.y;
            int dz = pz - death.z;
            double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);

            if (!hasLeftDeathArea && distance > LEAVE_DISTANCE) {
                hasLeftDeathArea = true;
            }

            if (hasLeftDeathArea && px == death.x && py == death.y && pz == death.z) {
                config.deathPoints.remove(worldKey);
                StatsConfig.save();
                hasLeftDeathArea = false;
                return getConfigName() + ": None :)";
            }
        }

        return getConfigName() + ": " + death.x + ", "
            + death.y + ", " + death.z + " (" + death.dimension + ")";
    }
}
