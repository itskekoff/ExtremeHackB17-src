package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

public class WorldGenWaterlily
extends WorldGenerator {
    @Override
    public boolean generate(World worldIn, Random rand, BlockPos position) {
        for (int i2 = 0; i2 < 10; ++i2) {
            int l2;
            int k2;
            int j2 = position.getX() + rand.nextInt(8) - rand.nextInt(8);
            if (!worldIn.isAirBlock(new BlockPos(j2, k2 = position.getY() + rand.nextInt(4) - rand.nextInt(4), l2 = position.getZ() + rand.nextInt(8) - rand.nextInt(8))) || !Blocks.WATERLILY.canPlaceBlockAt(worldIn, new BlockPos(j2, k2, l2))) continue;
            worldIn.setBlockState(new BlockPos(j2, k2, l2), Blocks.WATERLILY.getDefaultState(), 2);
        }
        return true;
    }
}

