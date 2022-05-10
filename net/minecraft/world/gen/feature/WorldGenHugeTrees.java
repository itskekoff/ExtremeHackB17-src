package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;

public abstract class WorldGenHugeTrees
extends WorldGenAbstractTree {
    protected final int baseHeight;
    protected final IBlockState woodMetadata;
    protected final IBlockState leavesMetadata;
    protected int extraRandomHeight;

    public WorldGenHugeTrees(boolean notify, int baseHeightIn, int extraRandomHeightIn, IBlockState woodMetadataIn, IBlockState leavesMetadataIn) {
        super(notify);
        this.baseHeight = baseHeightIn;
        this.extraRandomHeight = extraRandomHeightIn;
        this.woodMetadata = woodMetadataIn;
        this.leavesMetadata = leavesMetadataIn;
    }

    protected int getHeight(Random rand) {
        int i2 = rand.nextInt(3) + this.baseHeight;
        if (this.extraRandomHeight > 1) {
            i2 += rand.nextInt(this.extraRandomHeight);
        }
        return i2;
    }

    private boolean isSpaceAt(World worldIn, BlockPos leavesPos, int height) {
        boolean flag = true;
        if (leavesPos.getY() >= 1 && leavesPos.getY() + height + 1 <= 256) {
            for (int i2 = 0; i2 <= 1 + height; ++i2) {
                int j2 = 2;
                if (i2 == 0) {
                    j2 = 1;
                } else if (i2 >= 1 + height - 2) {
                    j2 = 2;
                }
                for (int k2 = -j2; k2 <= j2 && flag; ++k2) {
                    for (int l2 = -j2; l2 <= j2 && flag; ++l2) {
                        if (leavesPos.getY() + i2 >= 0 && leavesPos.getY() + i2 < 256 && this.canGrowInto(worldIn.getBlockState(leavesPos.add(k2, i2, l2)).getBlock())) continue;
                        flag = false;
                    }
                }
            }
            return flag;
        }
        return false;
    }

    private boolean ensureDirtsUnderneath(BlockPos pos, World worldIn) {
        BlockPos blockpos = pos.down();
        Block block = worldIn.getBlockState(blockpos).getBlock();
        if ((block == Blocks.GRASS || block == Blocks.DIRT) && pos.getY() >= 2) {
            this.setDirtAt(worldIn, blockpos);
            this.setDirtAt(worldIn, blockpos.east());
            this.setDirtAt(worldIn, blockpos.south());
            this.setDirtAt(worldIn, blockpos.south().east());
            return true;
        }
        return false;
    }

    protected boolean ensureGrowable(World worldIn, Random rand, BlockPos treePos, int p_175929_4_) {
        return this.isSpaceAt(worldIn, treePos, p_175929_4_) && this.ensureDirtsUnderneath(treePos, worldIn);
    }

    protected void growLeavesLayerStrict(World worldIn, BlockPos layerCenter, int width) {
        int i2 = width * width;
        for (int j2 = -width; j2 <= width + 1; ++j2) {
            for (int k2 = -width; k2 <= width + 1; ++k2) {
                BlockPos blockpos;
                Material material;
                int l2 = j2 - 1;
                int i1 = k2 - 1;
                if (j2 * j2 + k2 * k2 > i2 && l2 * l2 + i1 * i1 > i2 && j2 * j2 + i1 * i1 > i2 && l2 * l2 + k2 * k2 > i2 || (material = worldIn.getBlockState(blockpos = layerCenter.add(j2, 0, k2)).getMaterial()) != Material.AIR && material != Material.LEAVES) continue;
                this.setBlockAndNotifyAdequately(worldIn, blockpos, this.leavesMetadata);
            }
        }
    }

    protected void growLeavesLayer(World worldIn, BlockPos layerCenter, int width) {
        int i2 = width * width;
        for (int j2 = -width; j2 <= width; ++j2) {
            for (int k2 = -width; k2 <= width; ++k2) {
                BlockPos blockpos;
                Material material;
                if (j2 * j2 + k2 * k2 > i2 || (material = worldIn.getBlockState(blockpos = layerCenter.add(j2, 0, k2)).getMaterial()) != Material.AIR && material != Material.LEAVES) continue;
                this.setBlockAndNotifyAdequately(worldIn, blockpos, this.leavesMetadata);
            }
        }
    }
}

