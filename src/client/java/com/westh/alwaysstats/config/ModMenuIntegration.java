package com.westh.alwaysstats.config;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import com.westh.alwaysstats.render.StatsRenderer;
import com.westh.alwaysstats.screen.RepositionScreen;
import com.westh.alwaysstats.stats.StatProvider;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

public class ModMenuIntegration implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> {
            StatsConfig config = StatsConfig.get();

            ConfigBuilder builder = ConfigBuilder.create()
                    .setParentScreen(parent)
                    .setTitle(Component.literal("AlwaysStats Settings"));

            ConfigEntryBuilder entryBuilder = builder.entryBuilder();

            ConfigCategory general = builder.getOrCreateCategory(Component.literal("General"));

            // Only show preset positions, not CUSTOM (which is set via reposition screen)
            ScreenCorner displayCorner = config.corner == ScreenCorner.CUSTOM ? ScreenCorner.TOP_LEFT : config.corner;

            general.addEntry(entryBuilder.startEnumSelector(
                            Component.literal("HUD Position"),
                            ScreenCorner.class,
                            displayCorner)
                    .setDefaultValue(ScreenCorner.TOP_LEFT)
                    .setEnumNameProvider(corner -> {
                        // Hide CUSTOM from the dropdown display
                        if (corner == ScreenCorner.CUSTOM) {
                            return Component.literal("Custom (use Reposition)");
                        }
                        return Component.literal(corner.name().replace("_", " "));
                    })
                    .setSaveConsumer(newValue -> config.corner = newValue)
                    .build());

            // Add reposition button (uses a toggle that acts as a button)
            general.addEntry(entryBuilder.startBooleanToggle(
                            Component.literal("Reposition HUD..."),
                            false)
                    .setDefaultValue(false)
                    .setTooltip(Component.literal("Click to drag the HUD to a custom position"))
                    .setSaveConsumer(clicked -> {
                        if (clicked) {
                            // Open the reposition screen
                            Minecraft.getInstance().execute(() -> {
                                Minecraft.getInstance().setScreen(new RepositionScreen(parent));
                            });
                        }
                    })
                    .build());

            general.addEntry(entryBuilder.startEnumSelector(
                            Component.literal("Font Size"),
                            FontSize.class,
                            config.fontSize)
                    .setDefaultValue(FontSize.MEDIUM)
                    .setSaveConsumer(newValue -> config.fontSize = newValue)
                    .build());

            general.addEntry(entryBuilder.startBooleanToggle(
                            Component.literal("Show Background"),
                            config.showBackground)
                    .setDefaultValue(true)
                    .setSaveConsumer(newValue -> config.showBackground = newValue)
                    .build());

            general.addEntry(entryBuilder.startBooleanToggle(
                            Component.literal("Align Right"),
                            config.alignRight)
                    .setDefaultValue(false)
                    .setTooltip(Component.literal("Align text to the right side of the HUD"))
                    .setSaveConsumer(newValue -> config.alignRight = newValue)
                    .build());

            ConfigCategory statsCategory = builder.getOrCreateCategory(Component.literal("Stats"));

            for (StatProvider stat : StatsRenderer.getAllStats()) {
                statsCategory.addEntry(entryBuilder.startBooleanToggle(
                                Component.literal(stat.getConfigName()),
                                config.enabledStats.contains(stat.getConfigKey()))
                        .setDefaultValue(true)
                        .setSaveConsumer(enabled -> {
                            if (enabled) {
                                config.enabledStats.add(stat.getConfigKey());
                            } else {
                                config.enabledStats.remove(stat.getConfigKey());
                            }
                        })
                        .build());

                if (stat.getConfigKey().equals("biome")) {
                    statsCategory.addEntry(entryBuilder.startBooleanToggle(
                                    Component.literal("  └ Details"),
                                    config.biomeDetails)
                            .setDefaultValue(false)
                            .setTooltip(Component.literal("Show temperature (e.g. plains (0.80°))"))
                            .setSaveConsumer(newValue -> config.biomeDetails = newValue)
                            .build());
                } else if (stat.getConfigKey().equals("direction")) {
                    statsCategory.addEntry(entryBuilder.startBooleanToggle(
                                    Component.literal("  └ Details"),
                                    config.directionDetails)
                            .setDefaultValue(false)
                            .setTooltip(Component.literal("Show degrees (e.g. north (45.0°))"))
                            .setSaveConsumer(newValue -> config.directionDetails = newValue)
                            .build());
                } else if (stat.getConfigKey().equals("target")) {
                    statsCategory.addEntry(entryBuilder.startBooleanToggle(
                                    Component.literal("  └ Details"),
                                    config.targetDetails)
                            .setDefaultValue(false)
                            .setTooltip(Component.literal("Show variant info (e.g. cat (tabby), villager (farmer))"))
                            .setSaveConsumer(newValue -> config.targetDetails = newValue)
                            .build());
                }
            }
                    
            builder.setSavingRunnable(() -> {
                StatsConfig.save();
            });

            return builder.build();
        };
    }
}
