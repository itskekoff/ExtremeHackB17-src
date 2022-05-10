package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

public class WorldGenEndIsland
extends WorldGenerator {
    @Override
    public boolean generate(World worldIn, Random rand, BlockPos position) {
        float f2 = rand.nextInt(3) + 4;
        int i2 = 0;
        while (f2 > 0.5f) {
            for (int j2 = MathHelper.floor(-f2); j2 <= MathHelper.ceil(f2); ++j2) {
                for (int k2 = MathHelper.floor(-f2); k2 <= MathHelper.ceil(f2); ++k2) {
                    if (!((float)(j2 * j2 + k2 * k2) <= (f2 + 1.0f) * (f2 + 1.0f))) continue;
                    this.setBlockAndNotifyAdequately(worldIn, position.add(j2, i2, k2), Blocks.END_STONE.getDefaultState());
                }
            }
            f2 = (float)((double)f2 - ((double)rand.nextInt(2) + 0.5));
            --i2;
        }
        return true;
    }
}

