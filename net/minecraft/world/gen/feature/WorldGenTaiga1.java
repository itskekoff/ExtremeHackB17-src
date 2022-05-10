package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockOldLeaf;
import net.minecraft.block.BlockOldLog;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;

public class WorldGenTaiga1
extends WorldGenAbstractTree {
    private static final IBlockState TRUNK = Blocks.LOG.getDefaultState().withProperty(BlockOldLog.VARIANT, BlockPlanks.EnumType.SPRUCE);
    private static final IBlockState LEAF = Blocks.LEAVES.getDefaultState().withProperty(BlockOldLeaf.VARIANT, BlockPlanks.EnumType.SPRUCE).withProperty(BlockLeaves.CHECK_DECAY, false);

    public WorldGenTaiga1() {
        super(false);
    }

    @Override
    public boolean generate(World worldIn, Random rand, BlockPos position) {
        int i2 = rand.nextInt(5) + 7;
        int j2 = i2 - rand.nextInt(2) - 3;
        int k2 = i2 - j2;
        int l2 = 1 + rand.nextInt(k2 + 1);
        if (position.getY() >= 1 && position.getY() + i2 + 1 <= 256) {
            boolean flag = true;
            for (int i1 = position.getY(); i1 <= position.getY() + 1 + i2 && flag; ++i1) {
                int j1 = 1;
                j1 = i1 - position.getY() < j2 ? 0 : l2;
                BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();
                for (int k1 = position.getX() - j1; k1 <= position.getX() + j1 && flag; ++k1) {
                    for (int l1 = position.getZ() - j1; l1 <= position.getZ() + j1 && flag; ++l1) {
                        if (i1 >= 0 && i1 < 256) {
                            if (this.canGrowInto(worldIn.getBlockState(blockpos$mutableblockpos.setPos(k1, i1, l1)).getBlock())) continue;
                            flag = false;
                            continue;
                        }
                        flag = false;
                    }
                }
            }
            if (!flag) {
                return false;
            }
            Block block = worldIn.getBlockState(position.down()).getBlock();
            if ((block == Blocks.GRASS || block == Blocks.DIRT) && position.getY() < 256 - i2 - 1) {
                this.setDirtAt(worldIn, position.down());
                int k22 = 0;
                for (int l22 = position.getY() + i2; l22 >= position.getY() + j2; --l22) {
                    for (int j3 = position.getX() - k22; j3 <= position.getX() + k22; ++j3) {
                        int k3 = j3 - position.getX();
                        for (int i22 = position.getZ() - k22; i22 <= position.getZ() + k22; ++i22) {
                            BlockPos blockpos;
                            int j22 = i22 - position.getZ();
                            if (Math.abs(k3) == k22 && Math.abs(j22) == k22 && k22 > 0 || worldIn.getBlockState(blockpos = new BlockPos(j3, l22, i22)).isFullBlock()) continue;
                            this.setBlockAndNotifyAdequately(worldIn, blockpos, LEAF);
                        }
                    }
                    if (k22 >= 1 && l22 == position.getY() + j2 + 1) {
                        --k22;
                        continue;
                    }
                    if (k22 >= l2) continue;
                    ++k22;
                }
                for (int i3 = 0; i3 < i2 - 1; ++i3) {
                    Material material = worldIn.getBlockState(position.up(i3)).getMaterial();
                    if (material != Material.AIR && material != Material.LEAVES) continue;
                    this.setBlockAndNotifyAdequately(worldIn, position.up(i3), TRUNK);
                }
                return true;
            }
            return false;
        }
        return false;
    }
}

