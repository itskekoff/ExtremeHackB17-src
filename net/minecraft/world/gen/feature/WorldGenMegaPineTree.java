package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDirt;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockOldLeaf;
import net.minecraft.block.BlockOldLog;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenHugeTrees;

public class WorldGenMegaPineTree
extends WorldGenHugeTrees {
    private static final IBlockState TRUNK = Blocks.LOG.getDefaultState().withProperty(BlockOldLog.VARIANT, BlockPlanks.EnumType.SPRUCE);
    private static final IBlockState LEAF = Blocks.LEAVES.getDefaultState().withProperty(BlockOldLeaf.VARIANT, BlockPlanks.EnumType.SPRUCE).withProperty(BlockLeaves.CHECK_DECAY, false);
    private static final IBlockState PODZOL = Blocks.DIRT.getDefaultState().withProperty(BlockDirt.VARIANT, BlockDirt.DirtType.PODZOL);
    private final boolean useBaseHeight;

    public WorldGenMegaPineTree(boolean notify, boolean p_i45457_2_) {
        super(notify, 13, 15, TRUNK, LEAF);
        this.useBaseHeight = p_i45457_2_;
    }

    @Override
    public boolean generate(World worldIn, Random rand, BlockPos position) {
        int i2 = this.getHeight(rand);
        if (!this.ensureGrowable(worldIn, rand, position, i2)) {
            return false;
        }
        this.createCrown(worldIn, position.getX(), position.getZ(), position.getY() + i2, 0, rand);
        for (int j2 = 0; j2 < i2; ++j2) {
            IBlockState iblockstate = worldIn.getBlockState(position.up(j2));
            if (iblockstate.getMaterial() == Material.AIR || iblockstate.getMaterial() == Material.LEAVES) {
                this.setBlockAndNotifyAdequately(worldIn, position.up(j2), this.woodMetadata);
            }
            if (j2 >= i2 - 1) continue;
            iblockstate = worldIn.getBlockState(position.add(1, j2, 0));
            if (iblockstate.getMaterial() == Material.AIR || iblockstate.getMaterial() == Material.LEAVES) {
                this.setBlockAndNotifyAdequately(worldIn, position.add(1, j2, 0), this.woodMetadata);
            }
            if ((iblockstate = worldIn.getBlockState(position.add(1, j2, 1))).getMaterial() == Material.AIR || iblockstate.getMaterial() == Material.LEAVES) {
                this.setBlockAndNotifyAdequately(worldIn, position.add(1, j2, 1), this.woodMetadata);
            }
            if ((iblockstate = worldIn.getBlockState(position.add(0, j2, 1))).getMaterial() != Material.AIR && iblockstate.getMaterial() != Material.LEAVES) continue;
            this.setBlockAndNotifyAdequately(worldIn, position.add(0, j2, 1), this.woodMetadata);
        }
        return true;
    }

    private void createCrown(World worldIn, int x2, int z2, int y2, int p_150541_5_, Random rand) {
        int i2 = rand.nextInt(5) + (this.useBaseHeight ? this.baseHeight : 3);
        int j2 = 0;
        for (int k2 = y2 - i2; k2 <= y2; ++k2) {
            int l2 = y2 - k2;
            int i1 = p_150541_5_ + MathHelper.floor((float)l2 / (float)i2 * 3.5f);
            this.growLeavesLayerStrict(worldIn, new BlockPos(x2, k2, z2), i1 + (l2 > 0 && i1 == j2 && (k2 & 1) == 0 ? 1 : 0));
            j2 = i1;
        }
    }

    @Override
    public void generateSaplings(World worldIn, Random random, BlockPos pos) {
        this.placePodzolCircle(worldIn, pos.west().north());
        this.placePodzolCircle(worldIn, pos.east(2).north());
        this.placePodzolCircle(worldIn, pos.west().south(2));
        this.placePodzolCircle(worldIn, pos.east(2).south(2));
        for (int i2 = 0; i2 < 5; ++i2) {
            int j2 = random.nextInt(64);
            int k2 = j2 % 8;
            int l2 = j2 / 8;
            if (k2 != 0 && k2 != 7 && l2 != 0 && l2 != 7) continue;
            this.placePodzolCircle(worldIn, pos.add(-3 + k2, 0, -3 + l2));
        }
    }

    private void placePodzolCircle(World worldIn, BlockPos center) {
        for (int i2 = -2; i2 <= 2; ++i2) {
            for (int j2 = -2; j2 <= 2; ++j2) {
                if (Math.abs(i2) == 2 && Math.abs(j2) == 2) continue;
                this.placePodzolAt(worldIn, center.add(i2, 0, j2));
            }
        }
    }

    private void placePodzolAt(World worldIn, BlockPos pos) {
        for (int i2 = 2; i2 >= -3; --i2) {
            BlockPos blockpos = pos.up(i2);
            IBlockState iblockstate = worldIn.getBlockState(blockpos);
            Block block = iblockstate.getBlock();
            if (block == Blocks.GRASS || block == Blocks.DIRT) {
                this.setBlockAndNotifyAdequately(worldIn, blockpos, PODZOL);
                break;
            }
            if (iblockstate.getMaterial() != Material.AIR && i2 < 0) break;
        }
    }
}

