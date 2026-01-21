package com.westh.alwaysstats.render;

import com.westh.alwaysstats.config.FontSize;
import com.westh.alwaysstats.config.ScreenCorner;
import com.westh.alwaysstats.config.StatsConfig;
import com.westh.alwaysstats.stats.BiomeStat;
import com.westh.alwaysstats.stats.CoordStat;
import com.westh.alwaysstats.stats.DirectionStat;
import com.westh.alwaysstats.stats.FpsStat;
import com.westh.alwaysstats.stats.LightLevelStat;
import com.westh.alwaysstats.stats.StatProvider;
import com.westh.alwaysstats.stats.TargetStat;
import com.westh.alwaysstats.stats.TimeOfDayStat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;

import java.util.ArrayList;
import java.util.List;

public class StatsRenderer {
    private static final int BASE_LINE_HEIGHT = 11;
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
        new TargetStat(),
        new TimeOfDayStat()
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

        // Get font scale from config
        float scale = config.fontSize.getScale();
        int lineHeight = Math.round(BASE_LINE_HEIGHT * scale);

        // Calculate dimensions at the scaled size
        int maxWidth = 0;
        for (String line : lines) {
            maxWidth = Math.max(maxWidth, Math.round(client.font.width(line) * scale));
        }

        int boxWidth = maxWidth + (PADDING * 2);
        int boxHeight = (lines.size() * lineHeight) + PADDING;

        int screenWidth = client.getWindow().getGuiScaledWidth();
        int screenHeight = client.getWindow().getGuiScaledHeight();

        ScreenCorner corner = config.corner;
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

        // Draw background at screen coordinates (no scaling) if enabled
        if (config.showBackground) {
            guiGraphics.fill(boxX, boxY, boxX + boxWidth, boxY + boxHeight, BACKGROUND_COLOR);
        }

        // Draw text with scaling applied using JOML Matrix3x2fStack (Minecraft 1.21+ API)
        guiGraphics.pose().pushMatrix();
        guiGraphics.pose().translate(x, y);
        guiGraphics.pose().scale(scale, scale);

        int currentY = 0;
        for (String line : lines) {
            guiGraphics.drawString(client.font, line, 0, currentY, TEXT_COLOR);
            currentY += BASE_LINE_HEIGHT;
        }

        guiGraphics.pose().popMatrix();
    }
}
