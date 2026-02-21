package com.westh.alwaysstats.config;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import com.westh.alwaysstats.render.StatsRenderer;
import com.westh.alwaysstats.screen.RepositionScreen;
import com.westh.alwaysstats.stats.StatProvider;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.api.Requirement;
import me.shedaniel.clothconfig2.gui.entries.BooleanListEntry;
import me.shedaniel.clothconfig2.impl.builders.SubCategoryBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

import java.util.HashMap;
import java.util.Map;

public class ModMenuIntegration implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> {
            StatsConfig config = StatsConfig.get();

            ConfigBuilder builder = ConfigBuilder.create()
                    .setParentScreen(parent)
                    .setTitle(Component.literal("AlwaysStats Settings"));

            ConfigEntryBuilder entryBuilder = builder.entryBuilder();

            // === General Category ===
            ConfigCategory general = builder.getOrCreateCategory(Component.literal("General"));

            // --- Position sub-category ---
            SubCategoryBuilder positionGroup = entryBuilder.startSubCategory(Component.literal("Position"))
                    .setExpanded(true);

            // Only show preset positions, not CUSTOM (which is set via reposition screen)
            ScreenCorner displayCorner = config.corner == ScreenCorner.CUSTOM ? ScreenCorner.TOP_LEFT : config.corner;

            positionGroup.add(entryBuilder.startEnumSelector(
                            Component.literal("HUD Position"),
                            ScreenCorner.class,
                            displayCorner)
                    .setDefaultValue(ScreenCorner.TOP_LEFT)
                    .setEnumNameProvider(corner -> {
                        if (corner == ScreenCorner.CUSTOM) {
                            return Component.literal("Custom (use Reposition)");
                        }
                        return Component.literal(corner.name().replace("_", " "));
                    })
                    .setSaveConsumer(newValue -> config.corner = newValue)
                    .build());

            // Use a boolean toggle styled as a button — opens the screen immediately on click
            boolean inGame = Minecraft.getInstance().level != null;
            final boolean[] repositionOpened = {false};
            positionGroup.add(entryBuilder.startBooleanToggle(
                            Component.literal("Open Reposition Screen"),
                            false)
                    .setDefaultValue(false)
                    .setTooltip(Component.literal(inGame
                            ? "Drag the HUD to any position, resize it, and reorder stats"
                            : "Join a world first to reposition the HUD"))
                    .setYesNoTextSupplier(value -> {
                        if (inGame && value && !repositionOpened[0]) {
                            repositionOpened[0] = true;
                            Minecraft.getInstance().execute(() ->
                                    Minecraft.getInstance().setScreen(new RepositionScreen(parent)));
                        }
                        return Component.literal(inGame ? "Open ▶" : "In-game Only");
                    })
                    .setSaveConsumer(value -> {})
                    .build());

            general.addEntry(positionGroup.build());

            // --- Appearance sub-category ---
            SubCategoryBuilder appearanceGroup = entryBuilder.startSubCategory(Component.literal("Appearance"))
                    .setExpanded(true);

            appearanceGroup.add(entryBuilder.startEnumSelector(
                            Component.literal("Font Size"),
                            FontSize.class,
                            config.fontSize)
                    .setDefaultValue(FontSize.MEDIUM)
                    .setSaveConsumer(newValue -> config.fontSize = newValue)
                    .build());

            appearanceGroup.add(entryBuilder.startBooleanToggle(
                            Component.literal("Show Background"),
                            config.showBackground)
                    .setDefaultValue(true)
                    .setSaveConsumer(newValue -> config.showBackground = newValue)
                    .build());

            appearanceGroup.add(entryBuilder.startBooleanToggle(
                            Component.literal("Align Right"),
                            config.alignRight)
                    .setDefaultValue(false)
                    .setTooltip(Component.literal("Align text to the right side of the HUD"))
                    .setSaveConsumer(newValue -> config.alignRight = newValue)
                    .build());

            general.addEntry(appearanceGroup.build());

            // === Stats Category ===
            ConfigCategory statsCategory = builder.getOrCreateCategory(Component.literal("Stats"));

            // Build a lookup map for StatProvider by config key
            Map<String, StatProvider> statsByKey = new HashMap<>();
            for (StatProvider stat : StatsRenderer.getAllStats()) {
                statsByKey.put(stat.getConfigKey(), stat);
            }

            // Iterate in configured display order (config.statOrder)
            for (String key : config.statOrder) {
                StatProvider stat = statsByKey.get(key);
                if (stat == null) continue;

                boolean hasSubOptions = key.equals("biome") || key.equals("direction")
                        || key.equals("target") || key.equals("lightLevel") || key.equals("lastDeath");

                // Build the stat toggle entry
                BooleanListEntry statToggle = entryBuilder.startBooleanToggle(
                                Component.literal(hasSubOptions ? "Enabled" : stat.getConfigName()),
                                config.enabledStats.contains(key))
                        .setDefaultValue(true)
                        .setTooltip(Component.literal(getStatTooltip(key)))
                        .setSaveConsumer(enabled -> {
                            if (enabled) {
                                config.enabledStats.add(key);
                            } else {
                                config.enabledStats.remove(key);
                            }
                        })
                        .build();

                if (hasSubOptions) {
                    // Wrap stat + sub-options in a collapsible group
                    SubCategoryBuilder group = entryBuilder.startSubCategory(
                            Component.literal(stat.getConfigName()));
                    group.add(statToggle);

                    if (key.equals("biome")) {
                        group.add(entryBuilder.startBooleanToggle(
                                        Component.literal("Show Temperature"),
                                        config.biomeDetails)
                                .setDefaultValue(false)
                                .setTooltip(Component.literal("Show temperature (e.g. plains (0.80°))"))
                                .setSaveConsumer(newValue -> config.biomeDetails = newValue)
                                .build());
                    } else if (key.equals("direction")) {
                        group.add(entryBuilder.startBooleanToggle(
                                        Component.literal("Show Degrees"),
                                        config.directionDetails)
                                .setDefaultValue(false)
                                .setTooltip(Component.literal("Show degrees (e.g. north (45.0°))"))
                                .setSaveConsumer(newValue -> config.directionDetails = newValue)
                                .build());
                    } else if (key.equals("target")) {
                        group.add(entryBuilder.startBooleanToggle(
                                        Component.literal("Show Variants"),
                                        config.targetDetails)
                                .setDefaultValue(false)
                                .setTooltip(Component.literal("Show variant info (e.g. cat (tabby), villager (farmer))"))
                                .setSaveConsumer(newValue -> config.targetDetails = newValue)
                                .build());
                    } else if (key.equals("lightLevel")) {
                        group.add(entryBuilder.startBooleanToggle(
                                        Component.literal("Show Block/Sky Split"),
                                        config.lightLevelSplit)
                                .setDefaultValue(false)
                                .setTooltip(Component.literal("Show block light and sky light separately"))
                                .setSaveConsumer(newValue -> config.lightLevelSplit = newValue)
                                .build());
                        group.add(entryBuilder.startBooleanToggle(
                                        Component.literal("Show Mob Spawn Warning"),
                                        config.lightLevelDetails)
                                .setDefaultValue(false)
                                .setTooltip(Component.literal("Show warning when mobs can spawn (light level ≤ 7)"))
                                .setSaveConsumer(newValue -> config.lightLevelDetails = newValue)
                                .build());
                    } else if (key.equals("lastDeath")) {
                        group.add(entryBuilder.startBooleanToggle(
                                        Component.literal("Auto-clear at Death Location"),
                                        config.lastDeathAutoRefresh)
                                .setDefaultValue(false)
                                .setTooltip(Component.literal("Clear death point when you reach the death coords"))
                                .setSaveConsumer(newValue -> config.lastDeathAutoRefresh = newValue)
                                .build());
                    }

                    statsCategory.addEntry(group.build());
                } else {
                    // Simple stat with no sub-options — just a toggle
                    statsCategory.addEntry(statToggle);
                }
            }

            builder.setSavingRunnable(StatsConfig::save);

            return builder.build();
        };
    }

    private static String getStatTooltip(String key) {
        return switch (key) {
            case "fps" -> "Shows your current frames per second";
            case "biome" -> "Shows the biome you're standing in";
            case "coords" -> "Shows your X, Y, Z block coordinates";
            case "direction" -> "Shows the cardinal direction you're facing (N/S/E/W)";
            case "lightLevel" -> "Shows the light level at your position (0-15)";
            case "target" -> "Shows the block or entity you're looking at";
            case "timeOfDay" -> "Shows the in-game time of day (24h format)";
            case "lastDeath" -> "Shows where you last died in this world";
            default -> "";
        };
    }
}
