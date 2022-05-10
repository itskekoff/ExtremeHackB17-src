package net.minecraft.world.biome;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.biome.Biome;

public class BiomeColorHelper {
    private static final ColorResolver GRASS_COLOR = new ColorResolver(){

        @Override
        public int getColorAtPos(Biome biome, BlockPos blockPosition) {
            return biome.getGrassColorAtPos(blockPosition);
        }
    };
    private static final ColorResolver FOLIAGE_COLOR = new ColorResolver(){

        @Override
        public int getColorAtPos(Biome biome, BlockPos blockPosition) {
            return biome.getFoliageColorAtPos(blockPosition);
        }
    };
    private static final ColorResolver WATER_COLOR = new ColorResolver(){

        @Override
        public int getColorAtPos(Biome biome, BlockPos blockPosition) {
            return biome.getWaterColor();
        }
    };

    private static int getColorAtPos(IBlockAccess blockAccess, BlockPos pos, ColorResolver colorResolver) {
        int i2 = 0;
        int j2 = 0;
        int k2 = 0;
        for (BlockPos.MutableBlockPos blockpos$mutableblockpos : BlockPos.getAllInBoxMutable(pos.add(-1, 0, -1), pos.add(1, 0, 1))) {
            int l2 = colorResolver.getColorAtPos(blockAccess.getBiome(blockpos$mutableblockpos), blockpos$mutableblockpos);
            i2 += (l2 & 0xFF0000) >> 16;
            j2 += (l2 & 0xFF00) >> 8;
            k2 += l2 & 0xFF;
        }
        return (i2 / 9 & 0xFF) << 16 | (j2 / 9 & 0xFF) << 8 | k2 / 9 & 0xFF;
    }

    public static int getGrassColorAtPos(IBlockAccess blockAccess, BlockPos pos) {
        return BiomeColorHelper.getColorAtPos(blockAccess, pos, GRASS_COLOR);
    }

    public static int getFoliageColorAtPos(IBlockAccess blockAccess, BlockPos pos) {
        return BiomeColorHelper.getColorAtPos(blockAccess, pos, FOLIAGE_COLOR);
    }

    public static int getWaterColorAtPos(IBlockAccess blockAccess, BlockPos pos) {
        return BiomeColorHelper.getColorAtPos(blockAccess, pos, WATER_COLOR);
    }

    static interface ColorResolver {
        public int getColorAtPos(Biome var1, BlockPos var2);
    }
}

