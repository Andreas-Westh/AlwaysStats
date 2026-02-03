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
import com.westh.alwaysstats.stats.LastDeathStat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

public class StatsRenderer {
    public static final int BASE_LINE_HEIGHT = 11;
    public static final int PADDING = 2;
    public static final int MARGIN = 5;
    private static final int BACKGROUND_COLOR = 0x90000000;
    private static final int TEXT_COLOR = 0xFFFFFFFF;

    public record StatBounds(String key, int y, int height) {}
    public record PreviewResult(int width, int height, List<StatBounds> statBounds) {}

    private static final List<StatProvider> ALL_STATS = List.of(
        new FpsStat(),
        new BiomeStat(),
        new CoordStat(),
        new DirectionStat(),
        new LightLevelStat(),
        new TargetStat(),
        new TimeOfDayStat(),
        new LastDeathStat()
    );

    public static List<StatProvider> getAllStats() {
        return ALL_STATS;
    }

    private static StatProvider getStatByKey(String key) {
        for (StatProvider stat : ALL_STATS) {
            if (stat.getConfigKey().equals(key)) {
                return stat;
            }
        }
        return null;
    }

    public static void render(GuiGraphics guiGraphics, Minecraft client) {
        StatsConfig config = StatsConfig.get();
        List<Component> lines = getDisplayLines(client);

        if (lines.isEmpty()) {
            return;
        }

        // Get font scale from config (use custom scale for CUSTOM position)
        float scale = config.corner == ScreenCorner.CUSTOM ? config.customScale : config.fontSize.getScale();
        int lineHeight = Math.round(BASE_LINE_HEIGHT * scale);

        // Calculate dimensions at the scaled size
        int maxWidth = 0;
        for (Component line : lines) {
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
            case CUSTOM -> {
                x = config.customX;
                y = config.customY;
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

        // Check if text should be right-aligned
        boolean rightAligned = config.alignRight;

        // Draw text with scaling applied using JOML Matrix3x2fStack (Minecraft 1.21+ API)
        guiGraphics.pose().pushMatrix();
        guiGraphics.pose().translate(x, y);
        guiGraphics.pose().scale(scale, scale);

        // maxWidth at scale 1.0 for text positioning
        int unscaledMaxWidth = Math.round(maxWidth / scale);

        int currentY = 0;
        for (Component line : lines) {
            int textX = 0;
            if (rightAligned) {
                int lineWidth = client.font.width(line);
                textX = unscaledMaxWidth - lineWidth;
            }
            guiGraphics.drawString(client.font, line, textX, currentY, TEXT_COLOR);
            currentY += BASE_LINE_HEIGHT;
        }

        guiGraphics.pose().popMatrix();
    }

    /**
     * Calculate the position for a given corner setting.
     * Returns int[2] with {x, y}.
     */
    public static int[] calculatePosition(Minecraft client, ScreenCorner corner) {
        StatsConfig config = StatsConfig.get();
        float scale = config.fontSize.getScale();

        List<Component> lines = getDisplayLines(client);
        if (lines.isEmpty()) {
            return new int[]{MARGIN, MARGIN};
        }

        int maxWidth = 0;
        for (Component line : lines) {
            maxWidth = Math.max(maxWidth, Math.round(client.font.width(line) * scale));
        }

        int lineHeight = Math.round(BASE_LINE_HEIGHT * scale);
        int boxHeight = (lines.size() * lineHeight) + PADDING;

        int screenWidth = client.getWindow().getGuiScaledWidth();
        int screenHeight = client.getWindow().getGuiScaledHeight();

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
            case CUSTOM -> {
                x = config.customX;
                y = config.customY;
            }
            default -> { // TOP_LEFT
                x = MARGIN;
                y = MARGIN;
            }
        }
        return new int[]{x, y};
    }

    /**
     * Render a preview of the stats at a specific position with a given scale.
     * Returns PreviewResult with width, height, and stat bounds for hit detection.
     */
    public static PreviewResult renderPreview(GuiGraphics guiGraphics, Minecraft client, int x, int y, float scale) {
        return renderPreview(guiGraphics, client, x, y, scale, null);
    }

    /**
     * Render a preview with optional highlighted stat (for drag feedback).
     */
    public static PreviewResult renderPreview(GuiGraphics guiGraphics, Minecraft client, int x, int y, float scale, String highlightedKey) {
        List<Component> lines = getDisplayLines(client);
        List<String> statKeys = getEnabledStatKeys(client);

        if (lines.isEmpty()) {
            return new PreviewResult(0, 0, List.of());
        }

        StatsConfig config = StatsConfig.get();
        int lineHeight = Math.round(BASE_LINE_HEIGHT * scale);

        int maxWidth = 0;
        for (Component line : lines) {
            maxWidth = Math.max(maxWidth, Math.round(client.font.width(line) * scale));
        }

        int boxWidth = maxWidth + (PADDING * 2);
        int boxHeight = (lines.size() * lineHeight) + PADDING;

        int boxX = x - PADDING;
        int boxY = y - PADDING;

        // Draw background if enabled
        if (config.showBackground) {
            guiGraphics.fill(boxX, boxY, boxX + boxWidth, boxY + boxHeight, BACKGROUND_COLOR);
        }

        // Draw border to show draggable area
        guiGraphics.fill(boxX, boxY, boxX + boxWidth, boxY + 1, 0xFFFFFFFF);
        guiGraphics.fill(boxX, boxY + boxHeight - 1, boxX + boxWidth, boxY + boxHeight, 0xFFFFFFFF);
        guiGraphics.fill(boxX, boxY, boxX + 1, boxY + boxHeight, 0xFFFFFFFF);
        guiGraphics.fill(boxX + boxWidth - 1, boxY, boxX + boxWidth, boxY + boxHeight, 0xFFFFFFFF);

        // Draw resize handle in bottom-right corner
        int handleSize = 6;
        int handleX = boxX + boxWidth - handleSize;
        int handleY = boxY + boxHeight - handleSize;
        guiGraphics.fill(handleX, handleY, boxX + boxWidth, boxY + boxHeight, 0xFFFFFF00); // Yellow handle

        // Check if text should be right-aligned
        boolean rightAligned = config.alignRight;

        // maxWidth at scale 1.0 for text positioning
        int unscaledMaxWidth = Math.round(maxWidth / scale);

        // Build stat bounds for hit detection
        List<StatBounds> statBounds = new ArrayList<>();
        int currentYOffset = 0;
        for (int i = 0; i < lines.size(); i++) {
            String key = statKeys.get(i);
            int statY = boxY + PADDING + currentYOffset;
            statBounds.add(new StatBounds(key, statY, lineHeight));
            currentYOffset += lineHeight;
        }

        // Draw highlight behind the dragged stat
        if (highlightedKey != null) {
            for (StatBounds bounds : statBounds) {
                if (bounds.key().equals(highlightedKey)) {
                    guiGraphics.fill(boxX + 1, bounds.y(), boxX + boxWidth - 1, bounds.y() + bounds.height(), 0x40FFFFFF);
                    break;
                }
            }
        }

        // Draw text with scaling
        guiGraphics.pose().pushMatrix();
        guiGraphics.pose().translate(x, y);
        guiGraphics.pose().scale(scale, scale);

        int currentY = 0;
        for (int i = 0; i < lines.size(); i++) {
            Component line = lines.get(i);
            String key = statKeys.get(i);
            int textX = 0;
            if (rightAligned) {
                int lineWidth = client.font.width(line);
                textX = unscaledMaxWidth - lineWidth;
            }
            // Use yellow tint for the highlighted/dragged stat
            int color = (highlightedKey != null && key.equals(highlightedKey)) ? 0xFFFFFF55 : TEXT_COLOR;
            guiGraphics.drawString(client.font, line, textX, currentY, color);
            currentY += BASE_LINE_HEIGHT;
        }

        guiGraphics.pose().popMatrix();

        return new PreviewResult(boxWidth, boxHeight, statBounds);
    }

    private static List<Component> getDisplayLines(Minecraft client) {
        List<Component> lines = new ArrayList<>();
        StatsConfig config = StatsConfig.get();

        // Iterate in configured order
        for (String key : config.statOrder) {
            if (!config.enabledStats.contains(key)) {
                continue;
            }

            StatProvider provider = getStatByKey(key);
            if (provider != null) {
                Component component = provider.getDisplayComponent(client);
                if (component != null) {
                    lines.add(component);
                }
            }
        }

        return lines;
    }

    private static List<String> getEnabledStatKeys(Minecraft client) {
        List<String> keys = new ArrayList<>();
        StatsConfig config = StatsConfig.get();

        for (String key : config.statOrder) {
            if (!config.enabledStats.contains(key)) {
                continue;
            }

            StatProvider provider = getStatByKey(key);
            if (provider != null) {
                Component component = provider.getDisplayComponent(client);
                if (component != null) {
                    keys.add(key);
                }
            }
        }

        return keys;
    }
}
