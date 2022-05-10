package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

public class WorldGenIceSpike
extends WorldGenerator {
    @Override
    public boolean generate(World worldIn, Random rand, BlockPos position) {
        while (worldIn.isAirBlock(position) && position.getY() > 2) {
            position = position.down();
        }
        if (worldIn.getBlockState(position).getBlock() != Blocks.SNOW) {
            return false;
        }
        position = position.up(rand.nextInt(4));
        int i2 = rand.nextInt(4) + 7;
        int j2 = i2 / 4 + rand.nextInt(2);
        if (j2 > 1 && rand.nextInt(60) == 0) {
            position = position.up(10 + rand.nextInt(30));
        }
        for (int k2 = 0; k2 < i2; ++k2) {
            float f2 = (1.0f - (float)k2 / (float)i2) * (float)j2;
            int l2 = MathHelper.ceil(f2);
            for (int i1 = -l2; i1 <= l2; ++i1) {
                float f1 = (float)MathHelper.abs(i1) - 0.25f;
                for (int j1 = -l2; j1 <= l2; ++j1) {
                    float f22 = (float)MathHelper.abs(j1) - 0.25f;
                    if ((i1 != 0 || j1 != 0) && !(f1 * f1 + f22 * f22 <= f2 * f2) || (i1 == -l2 || i1 == l2 || j1 == -l2 || j1 == l2) && !(rand.nextFloat() <= 0.75f)) continue;
                    IBlockState iblockstate = worldIn.getBlockState(position.add(i1, k2, j1));
                    Block block = iblockstate.getBlock();
                    if (iblockstate.getMaterial() == Material.AIR || block == Blocks.DIRT || block == Blocks.SNOW || block == Blocks.ICE) {
                        this.setBlockAndNotifyAdequately(worldIn, position.add(i1, k2, j1), Blocks.PACKED_ICE.getDefaultState());
                    }
                    if (k2 == 0 || l2 <= 1) continue;
                    iblockstate = worldIn.getBlockState(position.add(i1, -k2, j1));
                    block = iblockstate.getBlock();
                    if (iblockstate.getMaterial() != Material.AIR && block != Blocks.DIRT && block != Blocks.SNOW && block != Blocks.ICE) continue;
                    this.setBlockAndNotifyAdequately(worldIn, position.add(i1, -k2, j1), Blocks.PACKED_ICE.getDefaultState());
                }
            }
        }
        int k1 = j2 - 1;
        if (k1 < 0) {
            k1 = 0;
        } else if (k1 > 1) {
            k1 = 1;
        }
        for (int l1 = -k1; l1 <= k1; ++l1) {
            block5: for (int i22 = -k1; i22 <= k1; ++i22) {
                BlockPos blockpos = position.add(l1, -1, i22);
                int j22 = 50;
                if (Math.abs(l1) == 1 && Math.abs(i22) == 1) {
                    j22 = rand.nextInt(5);
                }
                while (blockpos.getY() > 50) {
                    IBlockState iblockstate1 = worldIn.getBlockState(blockpos);
                    Block block1 = iblockstate1.getBlock();
                    if (iblockstate1.getMaterial() != Material.AIR && block1 != Blocks.DIRT && block1 != Blocks.SNOW && block1 != Blocks.ICE && block1 != Blocks.PACKED_ICE) continue block5;
                    this.setBlockAndNotifyAdequately(worldIn, blockpos, Blocks.PACKED_ICE.getDefaultState());
                    blockpos = blockpos.down();
                    if (--j22 > 0) continue;
                    blockpos = blockpos.down(rand.nextInt(5) + 1);
                    j22 = rand.nextInt(5);
                }
            }
        }
        return true;
    }
}

