package com.westh.alwaysstats.stats;

import net.minecraft.client.Minecraft;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

public class TargetBlockStat implements StatProvider {
    
    @Override
    public String getConfigKey() {
        return "targetBlock";
    }

    @Override 
    public String getConfigName() {
        return "Target Block";
    }

    @Override
    public String getDisplayText(Minecraft client) {
        HitResult hitResult = client.hitResult;
        
        // Only show when looking at a block (not air/entity/miss)
        if (hitResult == null || hitResult.getType() != HitResult.Type.BLOCK) {
            return null; // Returns null to hide the stat when not looking at a block
        }
        
        BlockHitResult blockHit = (BlockHitResult) hitResult;
        var blockState = client.level.getBlockState(blockHit.getBlockPos());
        String blockName = blockState.getBlock().getDescriptionId();
        
        // Convert "block.minecraft.stone" -> "stone"
        if (blockName.startsWith("block.minecraft.")) {
            blockName = blockName.substring(16);
        }
        
        return getConfigName() + ": " + blockName;
    }
}
