package com.westh.alwaysstats.stats;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import com.westh.alwaysstats.config.StatsConfig;

public interface StatProvider {
    String getConfigKey();

    String getConfigName();

    default String getDisplayText(Minecraft client) {
        return null;
    }

    default Component getDisplayComponent(Minecraft client) {
        String text = getDisplayText(client);
        return text != null ? Component.literal(text) : null;
    }

    default Component render(Minecraft client, StatsConfig config) {
        return getDisplayComponent(client);
    }
}
