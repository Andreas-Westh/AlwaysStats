package com.westh.alwaysstats.stats;

import com.westh.alwaysstats.config.StatsConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;

public class BiomeStat implements StatProvider {

    private static final int COLOR_HOT = 0xFF5555;   // Red - snowmen melt
    private static final int COLOR_COLD = 0x55FFFF;  // Cyan - water freezes

    @Override
    public String getConfigKey() {
        return "biome";
    }

    @Override
    public String getConfigName() {
        return "Biome";
    }

    @Override
    public Component getDisplayComponent(Minecraft client) {
        var pos = client.player.blockPosition();
        var biomeHolder = client.level.getBiome(pos);
        String biomeName = biomeHolder.getRegisteredName();

        if (biomeName.startsWith("minecraft:")) {
            biomeName = biomeName.substring(10);
        }

        if (StatsConfig.get().biomeDetails) {
            float baseTemp = biomeHolder.value().getBaseTemperature();
            // Temperature decreases with altitude above Y=64
            float heightAdjustment = Math.max(0, pos.getY() - 64) * 0.00166667f;
            float temperature = baseTemp - heightAdjustment;

            MutableComponent result = Component.literal(getConfigName() + ": " + biomeName + " (");

            // Color the temperature based on thresholds
            String tempStr = String.format("%.2f", temperature) + "°";
            MutableComponent tempComponent = Component.literal(tempStr);

            if (temperature >= 1.0f) {
                // Hot - snowmen melt
                tempComponent.setStyle(Style.EMPTY.withColor(TextColor.fromRgb(COLOR_HOT)));
            } else if (temperature < 0.15f) {
                // Cold - water freezes, snow forms
                tempComponent.setStyle(Style.EMPTY.withColor(TextColor.fromRgb(COLOR_COLD)));
            }

            result.append(tempComponent);
            result.append(")");

            return result;
        }

        return Component.literal(getConfigName() + ": " + biomeName);
    }
}
