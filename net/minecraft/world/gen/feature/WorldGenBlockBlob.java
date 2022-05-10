package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

public class WorldGenBlockBlob
extends WorldGenerator {
    private final Block block;
    private final int startRadius;

    public WorldGenBlockBlob(Block blockIn, int startRadiusIn) {
        super(false);
        this.block = blockIn;
        this.startRadius = startRadiusIn;
    }

    @Override
    public boolean generate(World worldIn, Random rand, BlockPos position) {
        while (true) {
            Block block;
            if (position.getY() <= 3 || !worldIn.isAirBlock(position.down()) && ((block = worldIn.getBlockState(position.down()).getBlock()) == Blocks.GRASS || block == Blocks.DIRT || block == Blocks.STONE)) {
                if (position.getY() <= 3) {
                    return false;
                }
                int i1 = this.startRadius;
                for (int i2 = 0; i1 >= 0 && i2 < 3; ++i2) {
                    int j2 = i1 + rand.nextInt(2);
                    int k2 = i1 + rand.nextInt(2);
                    int l2 = i1 + rand.nextInt(2);
                    float f2 = (float)(j2 + k2 + l2) * 0.333f + 0.5f;
                    for (BlockPos blockpos : BlockPos.getAllInBox(position.add(-j2, -k2, -l2), position.add(j2, k2, l2))) {
                        if (!(blockpos.distanceSq(position) <= (double)(f2 * f2))) continue;
                        worldIn.setBlockState(blockpos, this.block.getDefaultState(), 4);
                    }
                    position = position.add(-(i1 + 1) + rand.nextInt(2 + i1 * 2), 0 - rand.nextInt(2), -(i1 + 1) + rand.nextInt(2 + i1 * 2));
                }
                return true;
            }
            position = position.down();
        }
    }
}

