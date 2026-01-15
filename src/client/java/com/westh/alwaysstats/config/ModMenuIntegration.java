package com.westh.alwaysstats.config;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
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

            general.addEntry(entryBuilder.startEnumSelector(
                            Component.literal("HUD Position"),
                            ScreenCorner.class,
                            config.corner)
                    .setDefaultValue(ScreenCorner.TOP_LEFT)
                    .setSaveConsumer(newValue -> config.corner = newValue)
                    .build());

            builder.setSavingRunnable(() -> {
                StatsConfig.save();
            });

            return builder.build();
        };
    }
}
