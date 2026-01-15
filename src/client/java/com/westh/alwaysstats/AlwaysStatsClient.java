package com.westh.alwaysstats;

import com.westh.alwaysstats.config.StatsConfig;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ClientModInitializer;

public class AlwaysStatsClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        AutoConfig.register(StatsConfig.class, GsonConfigSerializer::new);
    }
}
