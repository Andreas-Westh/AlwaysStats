package com.westh.alwaysstats.stats;

import com.westh.alwaysstats.config.StatsConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.axolotl.Axolotl;
import net.minecraft.world.entity.animal.cow.MushroomCow;
import net.minecraft.world.entity.animal.cow.Cow;
import net.minecraft.world.entity.animal.feline.Cat;
import net.minecraft.world.entity.animal.fox.Fox;
import net.minecraft.world.entity.animal.frog.Frog;
import net.minecraft.world.entity.animal.parrot.Parrot;
import net.minecraft.world.entity.animal.rabbit.Rabbit;
import net.minecraft.world.entity.animal.sheep.Sheep;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

public class TargetStat implements StatProvider {

    private static final String BLOCK_PREFIX = "block.minecraft.";
    private static final String ENTITY_PREFIX = "entity.minecraft.";

    @Override
    public String getConfigKey() {
        return "target";
    }

    @Override
    public String getConfigName() {
        return "Target";
    }

    @Override
    public String getDisplayText(Minecraft client) {
        HitResult hitResult = client.hitResult;

        if (hitResult == null) {
            return getConfigName() + ": ";
        }

        // Check for entity first (takes priority when looking at entity on a block)
        if (hitResult.getType() == HitResult.Type.ENTITY) {
            EntityHitResult entityHit = (EntityHitResult) hitResult;
            Entity entity = entityHit.getEntity();
            String entityName = entity.getType().getDescriptionId();

            // Convert "entity.minecraft.pig" -> "pig"
            if (entityName.startsWith(ENTITY_PREFIX)) {
                entityName = entityName.substring(ENTITY_PREFIX.length());
            }

            if (StatsConfig.get().targetDetails) {
                String variant = getEntityVariant(entity);
                if (variant != null) {
                    return getConfigName() + ": " + entityName + " (" + variant + ")";
                }
            }

            return getConfigName() + ": " + entityName;
        }

        // Then check for block
        if (hitResult.getType() == HitResult.Type.BLOCK) {
            BlockHitResult blockHit = (BlockHitResult) hitResult;
            var blockState = client.level.getBlockState(blockHit.getBlockPos());
            String blockName = blockState.getBlock().getDescriptionId();

            // Convert "block.minecraft.stone" -> "stone"
            if (blockName.startsWith(BLOCK_PREFIX)) {
                blockName = blockName.substring(BLOCK_PREFIX.length());
            }

            return getConfigName() + ": " + blockName;
        }

        // Not looking at anything (MISS)
        return getConfigName() + ": ";
    }

    private String getEntityVariant(Entity entity) {
        return switch (entity) {
            case Cow cow -> extractHolderName(cow.getVariant());
            case Cat cat -> extractHolderName(cat.getVariant());
            case Frog frog -> extractHolderName(frog.getVariant());
            case Axolotl axolotl -> axolotl.getVariant().getName();
            case Parrot parrot -> parrot.getVariant().name().toLowerCase();
            case Rabbit rabbit -> rabbit.getVariant().name().toLowerCase();
            case Fox fox -> fox.getVariant().name().toLowerCase();
            case MushroomCow mooshroom -> mooshroom.getVariant().name().toLowerCase();
            case Sheep sheep -> sheep.getColor().getName();
            case Villager villager -> extractHolderName(villager.getVillagerData().profession());
            default -> null;
        };
    }

    private String extractHolderName(Holder<?> holder) {
        return holder.getRegisteredName().replaceFirst("^minecraft:", "");
    }
}
