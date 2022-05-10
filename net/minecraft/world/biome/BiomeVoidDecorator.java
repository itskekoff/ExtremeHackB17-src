package net.minecraft.world.biome;

import java.util.Random;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeDecorator;

public class BiomeVoidDecorator
extends BiomeDecorator {
    @Override
    public void decorate(World worldIn, Random random, Biome biome, BlockPos pos) {
        BlockPos blockpos = worldIn.getSpawnPoint();
        int i2 = 16;
        double d0 = blockpos.distanceSq(pos.add(8, blockpos.getY(), 8));
        if (d0 <= 1024.0) {
            BlockPos blockpos1 = new BlockPos(blockpos.getX() - 16, blockpos.getY() - 1, blockpos.getZ() - 16);
            BlockPos blockpos2 = new BlockPos(blockpos.getX() + 16, blockpos.getY() - 1, blockpos.getZ() + 16);
            BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos(blockpos1);
            for (int j2 = pos.getZ(); j2 < pos.getZ() + 16; ++j2) {
                for (int k2 = pos.getX(); k2 < pos.getX() + 16; ++k2) {
                    if (j2 < blockpos1.getZ() || j2 > blockpos2.getZ() || k2 < blockpos1.getX() || k2 > blockpos2.getX()) continue;
                    blockpos$mutableblockpos.setPos(k2, blockpos$mutableblockpos.getY(), j2);
                    if (blockpos.getX() == k2 && blockpos.getZ() == j2) {
                        worldIn.setBlockState(blockpos$mutableblockpos, Blocks.COBBLESTONE.getDefaultState(), 2);
                        continue;
                    }
                    worldIn.setBlockState(blockpos$mutableblockpos, Blocks.STONE.getDefaultState(), 2);
                }
            }
        }
    }
}

