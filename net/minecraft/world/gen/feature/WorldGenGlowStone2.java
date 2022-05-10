package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

public class WorldGenGlowStone2
extends WorldGenerator {
    @Override
    public boolean generate(World worldIn, Random rand, BlockPos position) {
        if (!worldIn.isAirBlock(position)) {
            return false;
        }
        if (worldIn.getBlockState(position.up()).getBlock() != Blocks.NETHERRACK) {
            return false;
        }
        worldIn.setBlockState(position, Blocks.GLOWSTONE.getDefaultState(), 2);
        for (int i2 = 0; i2 < 1500; ++i2) {
            BlockPos blockpos = position.add(rand.nextInt(8) - rand.nextInt(8), -rand.nextInt(12), rand.nextInt(8) - rand.nextInt(8));
            if (worldIn.getBlockState(blockpos).getMaterial() != Material.AIR) continue;
            int j2 = 0;
            for (EnumFacing enumfacing : EnumFacing.values()) {
                if (worldIn.getBlockState(blockpos.offset(enumfacing)).getBlock() == Blocks.GLOWSTONE) {
                    ++j2;
                }
                if (j2 > 1) break;
            }
            if (j2 != true) continue;
            worldIn.setBlockState(blockpos, Blocks.GLOWSTONE.getDefaultState(), 2);
        }
        return true;
    }
}

