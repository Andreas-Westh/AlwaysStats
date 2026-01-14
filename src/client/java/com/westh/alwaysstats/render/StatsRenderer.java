package com.westh.alwaysstats.render;

import com.westh.alwaysstats.config.StatsConfig;
import com.westh.alwaysstats.stats.BiomeStat;
import com.westh.alwaysstats.stats.CoordStat;
import com.westh.alwaysstats.stats.DirectionStat;
import com.westh.alwaysstats.stats.FpsStat;
import com.westh.alwaysstats.stats.LightLevelStat;
import com.westh.alwaysstats.stats.StatProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;

import java.util.ArrayList;
import java.util.List;

public class StatsRenderer {
    private static final int LINE_HEIGHT = 11;
    private static final int PADDING = 2;
    private static final int BACKGROUND_COLOR = 0x90000000;
    private static final int TEXT_COLOR = 0xFFFFFFFF;
    
    private static final List<StatProvider> statProviders = new ArrayList<>();
    
    static {
        statProviders.add(new FpsStat());
        statProviders.add(new BiomeStat());
        statProviders.add(new CoordStat());
        statProviders.add(new DirectionStat());
        statProviders.add(new LightLevelStat());
    }
    
    public static void render(GuiGraphics guiGraphics, Minecraft client) {
        List<String> lines = new ArrayList<>();
        
        for (StatProvider provider : statProviders) {
            String text = provider.getDisplayText(client);
            if (text != null) {
                lines.add(text);
            }
        }
        
        if (lines.isEmpty()) {
            return;
        }
        
        int x = StatsConfig.getPosX();
        int y = StatsConfig.getPosY();
        
        int maxWidth = 0;
        for (String line : lines) {
            maxWidth = Math.max(maxWidth, client.font.width(line));
        }
        
        int boxWidth = maxWidth + (PADDING * 2);
        int boxHeight = (lines.size() * LINE_HEIGHT) + PADDING;
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
