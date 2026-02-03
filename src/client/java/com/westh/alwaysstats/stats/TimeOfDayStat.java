package com.westh.alwaysstats.stats;

import net.minecraft.client.Minecraft;

public class TimeOfDayStat implements StatProvider {

    @Override
    public String getConfigKey() {
        return "timeOfDay";
    }

    @Override
    public String getConfigName() {
        return "Time";
    }

    @Override
    public String getDisplayText(Minecraft client) {
        long timeOfDay = client.level.getDayTime() % 24000;

        // Convert to hours and minutes (0 ticks = 6:00 AM in Minecraft)
        int hours = (int) ((timeOfDay / 1000 + 6) % 24);
        int minutes = (int) ((timeOfDay % 1000) * 60 / 1000);

        // Player can sleep between tick 12542 and 23460
        boolean canSleep = timeOfDay >= 12542 && timeOfDay <= 23460;
        String sleepIndicator = canSleep ? " *" : "";

        return String.format("%s: %02d:%02d%s", getConfigName(), hours, minutes, sleepIndicator);
    }
}
