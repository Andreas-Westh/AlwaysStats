package com.westh.alwaysstats.render;

import com.westh.alwaysstats.config.ScreenCorner;
import com.westh.alwaysstats.config.StatsConfig;
import com.westh.alwaysstats.stats.BiomeStat;
import com.westh.alwaysstats.stats.CoordStat;
import com.westh.alwaysstats.stats.DirectionStat;
import com.westh.alwaysstats.stats.FpsStat;
import com.westh.alwaysstats.stats.LightLevelStat;
import com.westh.alwaysstats.stats.StatProvider;
import com.westh.alwaysstats.stats.TargetBlockStat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;

import java.util.ArrayList;
import java.util.List;

public class StatsRenderer {
    private static final int LINE_HEIGHT = 11;
    private static final int PADDING = 2;
    private static final int MARGIN = 5;
    private static final int BACKGROUND_COLOR = 0x90000000;
    private static final int TEXT_COLOR = 0xFFFFFFFF;

    private static final List<StatProvider> ALL_STATS = List.of(
        new FpsStat(),
        new BiomeStat(),
        new CoordStat(),
        new DirectionStat(),
        new LightLevelStat(),
        new TargetBlockStat()
    );

    public static List<StatProvider> getAllStats() {
        return ALL_STATS;
    }

    public static void render(GuiGraphics guiGraphics, Minecraft client) {
        List<String> lines = new ArrayList<>();

        StatsConfig config = StatsConfig.get(); 

        for (StatProvider provider : ALL_STATS) {
            if (!config.enabledStats.contains(provider.getConfigKey())) {
                continue;
            }
            
            String text = provider.getDisplayText(client);
            if (text != null) {
                lines.add(text);
            }
        }

        if (lines.isEmpty()) {
            return;
        }

        int maxWidth = 0;
        for (String line : lines) {
            maxWidth = Math.max(maxWidth, client.font.width(line));
        }

        int boxWidth = maxWidth + (PADDING * 2);
        int boxHeight = (lines.size() * LINE_HEIGHT) + PADDING;

        int screenWidth = client.getWindow().getGuiScaledWidth();
        int screenHeight = client.getWindow().getGuiScaledHeight();

        ScreenCorner corner = StatsConfig.get().corner;
        int x, y;

        switch (corner) {
            case TOP_RIGHT -> {
                x = screenWidth - maxWidth - MARGIN;
                y = MARGIN;
            }
            case BOTTOM_LEFT -> {
                x = MARGIN;
                y = screenHeight - boxHeight - MARGIN + PADDING;
            }
            case BOTTOM_RIGHT -> {
                x = screenWidth - maxWidth - MARGIN;
                y = screenHeight - boxHeight - MARGIN + PADDING;
            }
            default -> { // TOP_LEFT
                x = MARGIN;
                y = MARGIN;
            }
        }

        int boxX = x - PADDING;
        int boxY = y - PADDING;

        guiGraphics.fill(boxX, boxY, boxX + boxWidth, boxY + boxHeight, BACKGROUND_COLOR);

        int currentY = y;
        for (String line : lines) {
            guiGraphics.drawString(client.font, line, x, currentY, TEXT_COLOR);
            currentY += LINE_HEIGHT;
        }
    }
}
