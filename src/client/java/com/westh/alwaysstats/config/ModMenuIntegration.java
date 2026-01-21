package com.westh.alwaysstats.config;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.network.chat.Component;
import com.westh.alwaysstats.render.StatsRenderer;
import com.westh.alwaysstats.stats.StatProvider;

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

            general.addEntry(entryBuilder.startEnumSelector(
                            Component.literal("HUD Position"),
                            ScreenCorner.class,
                            config.corner)
                    .setDefaultValue(ScreenCorner.TOP_LEFT)
                    .setSaveConsumer(newValue -> config.corner = newValue)
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

                if (stat.getConfigKey().equals("direction")) {
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
