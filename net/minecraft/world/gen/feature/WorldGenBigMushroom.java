package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockHugeMushroom;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

public class WorldGenBigMushroom
extends WorldGenerator {
    private final Block mushroomType;

    public WorldGenBigMushroom(Block p_i46449_1_) {
        super(true);
        this.mushroomType = p_i46449_1_;
    }

    public WorldGenBigMushroom() {
        super(false);
        this.mushroomType = null;
    }

    @Override
    public boolean generate(World worldIn, Random rand, BlockPos position) {
        Block block = this.mushroomType;
        if (block == null) {
            block = rand.nextBoolean() ? Blocks.BROWN_MUSHROOM_BLOCK : Blocks.RED_MUSHROOM_BLOCK;
        }
        int i2 = rand.nextInt(3) + 4;
        if (rand.nextInt(12) == 0) {
            i2 *= 2;
        }
        boolean flag = true;
        if (position.getY() >= 1 && position.getY() + i2 + 1 < 256) {
            for (int j2 = position.getY(); j2 <= position.getY() + 1 + i2; ++j2) {
                int k2 = 3;
                if (j2 <= position.getY() + 3) {
                    k2 = 0;
                }
                BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();
                for (int l2 = position.getX() - k2; l2 <= position.getX() + k2 && flag; ++l2) {
                    for (int i1 = position.getZ() - k2; i1 <= position.getZ() + k2 && flag; ++i1) {
                        if (j2 >= 0 && j2 < 256) {
                            Material material = worldIn.getBlockState(blockpos$mutableblockpos.setPos(l2, j2, i1)).getMaterial();
                            if (material == Material.AIR || material == Material.LEAVES) continue;
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
            Block block1 = worldIn.getBlockState(position.down()).getBlock();
            if (block1 != Blocks.DIRT && block1 != Blocks.GRASS && block1 != Blocks.MYCELIUM) {
                return false;
            }
            int k2 = position.getY() + i2;
            if (block == Blocks.RED_MUSHROOM_BLOCK) {
                k2 = position.getY() + i2 - 3;
            }
            for (int l2 = k2; l2 <= position.getY() + i2; ++l2) {
                int j3 = 1;
                if (l2 < position.getY() + i2) {
                    ++j3;
                }
                if (block == Blocks.BROWN_MUSHROOM_BLOCK) {
                    j3 = 3;
                }
                int k3 = position.getX() - j3;
                int l3 = position.getX() + j3;
                int j1 = position.getZ() - j3;
                int k1 = position.getZ() + j3;
                for (int l1 = k3; l1 <= l3; ++l1) {
                    for (int i22 = j1; i22 <= k1; ++i22) {
                        BlockPos blockpos;
                        int j2 = 5;
                        if (l1 == k3) {
                            --j2;
                        } else if (l1 == l3) {
                            ++j2;
                        }
                        if (i22 == j1) {
                            j2 -= 3;
                        } else if (i22 == k1) {
                            j2 += 3;
                        }
                        BlockHugeMushroom.EnumType blockhugemushroom$enumtype = BlockHugeMushroom.EnumType.byMetadata(j2);
                        if (block == Blocks.BROWN_MUSHROOM_BLOCK || l2 < position.getY() + i2) {
                            if ((l1 == k3 || l1 == l3) && (i22 == j1 || i22 == k1)) continue;
                            if (l1 == position.getX() - (j3 - 1) && i22 == j1) {
                                blockhugemushroom$enumtype = BlockHugeMushroom.EnumType.NORTH_WEST;
                            }
                            if (l1 == k3 && i22 == position.getZ() - (j3 - 1)) {
                                blockhugemushroom$enumtype = BlockHugeMushroom.EnumType.NORTH_WEST;
                            }
                            if (l1 == position.getX() + (j3 - 1) && i22 == j1) {
                                blockhugemushroom$enumtype = BlockHugeMushroom.EnumType.NORTH_EAST;
                            }
                            if (l1 == l3 && i22 == position.getZ() - (j3 - 1)) {
                                blockhugemushroom$enumtype = BlockHugeMushroom.EnumType.NORTH_EAST;
                            }
                            if (l1 == position.getX() - (j3 - 1) && i22 == k1) {
                                blockhugemushroom$enumtype = BlockHugeMushroom.EnumType.SOUTH_WEST;
                            }
                            if (l1 == k3 && i22 == position.getZ() + (j3 - 1)) {
                                blockhugemushroom$enumtype = BlockHugeMushroom.EnumType.SOUTH_WEST;
                            }
                            if (l1 == position.getX() + (j3 - 1) && i22 == k1) {
                                blockhugemushroom$enumtype = BlockHugeMushroom.EnumType.SOUTH_EAST;
                            }
                            if (l1 == l3 && i22 == position.getZ() + (j3 - 1)) {
                                blockhugemushroom$enumtype = BlockHugeMushroom.EnumType.SOUTH_EAST;
                            }
                        }
                        if (blockhugemushroom$enumtype == BlockHugeMushroom.EnumType.CENTER && l2 < position.getY() + i2) {
                            blockhugemushroom$enumtype = BlockHugeMushroom.EnumType.ALL_INSIDE;
                        }
                        if (position.getY() < position.getY() + i2 - 1 && blockhugemushroom$enumtype == BlockHugeMushroom.EnumType.ALL_INSIDE || worldIn.getBlockState(blockpos = new BlockPos(l1, l2, i22)).isFullBlock()) continue;
                        this.setBlockAndNotifyAdequately(worldIn, blockpos, block.getDefaultState().withProperty(BlockHugeMushroom.VARIANT, blockhugemushroom$enumtype));
                    }
                }
            }
            for (int i3 = 0; i3 < i2; ++i3) {
                IBlockState iblockstate = worldIn.getBlockState(position.up(i3));
                if (iblockstate.isFullBlock()) continue;
                this.setBlockAndNotifyAdequately(worldIn, position.up(i3), block.getDefaultState().withProperty(BlockHugeMushroom.VARIANT, BlockHugeMushroom.EnumType.STEM));
            }
            return true;
        }
        return false;
    }
}
