package com.westh.alwaysstats.stats;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

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
}
