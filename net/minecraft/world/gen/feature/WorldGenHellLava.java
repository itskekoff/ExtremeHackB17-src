package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

public class WorldGenHellLava
extends WorldGenerator {
    private final Block block;
    private final boolean insideRock;

    public WorldGenHellLava(Block blockIn, boolean insideRockIn) {
        this.block = blockIn;
        this.insideRock = insideRockIn;
    }

    @Override
    public boolean generate(World worldIn, Random rand, BlockPos position) {
        if (worldIn.getBlockState(position.up()).getBlock() != Blocks.NETHERRACK) {
            return false;
        }
        if (worldIn.getBlockState(position).getMaterial() != Material.AIR && worldIn.getBlockState(position).getBlock() != Blocks.NETHERRACK) {
            return false;
        }
        int i2 = 0;
        if (worldIn.getBlockState(position.west()).getBlock() == Blocks.NETHERRACK) {
            ++i2;
        }
        if (worldIn.getBlockState(position.east()).getBlock() == Blocks.NETHERRACK) {
            ++i2;
        }
        if (worldIn.getBlockState(position.north()).getBlock() == Blocks.NETHERRACK) {
            ++i2;
        }
        if (worldIn.getBlockState(position.south()).getBlock() == Blocks.NETHERRACK) {
            ++i2;
        }
        if (worldIn.getBlockState(position.down()).getBlock() == Blocks.NETHERRACK) {
            ++i2;
        }
        int j2 = 0;
        if (worldIn.isAirBlock(position.west())) {
            ++j2;
        }
        if (worldIn.isAirBlock(position.east())) {
            ++j2;
        }
        if (worldIn.isAirBlock(position.north())) {
            ++j2;
        }
        if (worldIn.isAirBlock(position.south())) {
            ++j2;
        }
        if (worldIn.isAirBlock(position.down())) {
            ++j2;
        }
        if (!this.insideRock && i2 == 4 && j2 == 1 || i2 == 5) {
            IBlockState iblockstate = this.block.getDefaultState();
            worldIn.setBlockState(position, iblockstate, 2);
            worldIn.immediateBlockTick(position, iblockstate, rand);
        }
        return true;
    }
}

