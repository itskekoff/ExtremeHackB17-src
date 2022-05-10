package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenTrees;

public class WorldGenShrub
extends WorldGenTrees {
    private final IBlockState leavesMetadata;
    private final IBlockState woodMetadata;

    public WorldGenShrub(IBlockState p_i46450_1_, IBlockState p_i46450_2_) {
        super(false);
        this.woodMetadata = p_i46450_1_;
        this.leavesMetadata = p_i46450_2_;
    }

    @Override
    public boolean generate(World worldIn, Random rand, BlockPos position) {
        IBlockState iblockstate = worldIn.getBlockState(position);
        while ((iblockstate.getMaterial() == Material.AIR || iblockstate.getMaterial() == Material.LEAVES) && position.getY() > 0) {
            position = position.down();
            iblockstate = worldIn.getBlockState(position);
        }
        Block block = worldIn.getBlockState(position).getBlock();
        if (block == Blocks.DIRT || block == Blocks.GRASS) {
            position = position.up();
            this.setBlockAndNotifyAdequately(worldIn, position, this.woodMetadata);
            for (int i2 = position.getY(); i2 <= position.getY() + 2; ++i2) {
                int j2 = i2 - position.getY();
                int k2 = 2 - j2;
                for (int l2 = position.getX() - k2; l2 <= position.getX() + k2; ++l2) {
                    int i1 = l2 - position.getX();
                    for (int j1 = position.getZ() - k2; j1 <= position.getZ() + k2; ++j1) {
                        BlockPos blockpos;
                        Material material;
                        int k1 = j1 - position.getZ();
                        if (Math.abs(i1) == k2 && Math.abs(k1) == k2 && rand.nextInt(2) == 0 || (material = worldIn.getBlockState(blockpos = new BlockPos(l2, i2, j1)).getMaterial()) != Material.AIR && material != Material.LEAVES) continue;
                        this.setBlockAndNotifyAdequately(worldIn, blockpos, this.leavesMetadata);
                    }
                }
            }
        }
        return true;
    }
}

