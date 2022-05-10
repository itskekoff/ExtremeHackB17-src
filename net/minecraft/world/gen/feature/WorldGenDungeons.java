package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraft.world.storage.loot.LootTableList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class WorldGenDungeons
extends WorldGenerator {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final ResourceLocation[] SPAWNERTYPES = new ResourceLocation[]{EntityList.func_191306_a(EntitySkeleton.class), EntityList.func_191306_a(EntityZombie.class), EntityList.func_191306_a(EntityZombie.class), EntityList.func_191306_a(EntitySpider.class)};

    @Override
    public boolean generate(World worldIn, Random rand, BlockPos position) {
        int i2 = 3;
        int j2 = rand.nextInt(2) + 2;
        int k2 = -j2 - 1;
        int l2 = j2 + 1;
        int i1 = -1;
        int j1 = 4;
        int k1 = rand.nextInt(2) + 2;
        int l1 = -k1 - 1;
        int i22 = k1 + 1;
        int j22 = 0;
        for (int k22 = k2; k22 <= l2; ++k22) {
            for (int l22 = -1; l22 <= 4; ++l22) {
                for (int i3 = l1; i3 <= i22; ++i3) {
                    BlockPos blockpos = position.add(k22, l22, i3);
                    Material material = worldIn.getBlockState(blockpos).getMaterial();
                    boolean flag = material.isSolid();
                    if (l22 == -1 && !flag) {
                        return false;
                    }
                    if (l22 == 4 && !flag) {
                        return false;
                    }
                    if (k22 != k2 && k22 != l2 && i3 != l1 && i3 != i22 || l22 != 0 || !worldIn.isAirBlock(blockpos) || !worldIn.isAirBlock(blockpos.up())) continue;
                    ++j22;
                }
            }
        }
        if (j22 >= 1 && j22 <= 5) {
            for (int k3 = k2; k3 <= l2; ++k3) {
                for (int i4 = 3; i4 >= -1; --i4) {
                    for (int k4 = l1; k4 <= i22; ++k4) {
                        BlockPos blockpos1 = position.add(k3, i4, k4);
                        if (k3 != k2 && i4 != -1 && k4 != l1 && k3 != l2 && i4 != 4 && k4 != i22) {
                            if (worldIn.getBlockState(blockpos1).getBlock() == Blocks.CHEST) continue;
                            worldIn.setBlockToAir(blockpos1);
                            continue;
                        }
                        if (blockpos1.getY() >= 0 && !worldIn.getBlockState(blockpos1.down()).getMaterial().isSolid()) {
                            worldIn.setBlockToAir(blockpos1);
                            continue;
                        }
                        if (!worldIn.getBlockState(blockpos1).getMaterial().isSolid() || worldIn.getBlockState(blockpos1).getBlock() == Blocks.CHEST) continue;
                        if (i4 == -1 && rand.nextInt(4) != 0) {
                            worldIn.setBlockState(blockpos1, Blocks.MOSSY_COBBLESTONE.getDefaultState(), 2);
                            continue;
                        }
                        worldIn.setBlockState(blockpos1, Blocks.COBBLESTONE.getDefaultState(), 2);
                    }
                }
            }
            block6: for (int l3 = 0; l3 < 2; ++l3) {
                for (int j4 = 0; j4 < 3; ++j4) {
                    int j5;
                    int i5;
                    int l4 = position.getX() + rand.nextInt(j2 * 2 + 1) - j2;
                    BlockPos blockpos2 = new BlockPos(l4, i5 = position.getY(), j5 = position.getZ() + rand.nextInt(k1 * 2 + 1) - k1);
                    if (!worldIn.isAirBlock(blockpos2)) continue;
                    int j3 = 0;
                    for (EnumFacing enumfacing : EnumFacing.Plane.HORIZONTAL) {
                        if (!worldIn.getBlockState(blockpos2.offset(enumfacing)).getMaterial().isSolid()) continue;
                        ++j3;
                    }
                    if (j3 != true) continue;
                    worldIn.setBlockState(blockpos2, Blocks.CHEST.correctFacing(worldIn, blockpos2, Blocks.CHEST.getDefaultState()), 2);
                    TileEntity tileentity1 = worldIn.getTileEntity(blockpos2);
                    if (!(tileentity1 instanceof TileEntityChest)) continue block6;
                    ((TileEntityChest)tileentity1).setLootTable(LootTableList.CHESTS_SIMPLE_DUNGEON, rand.nextLong());
                    continue block6;
                }
            }
            worldIn.setBlockState(position, Blocks.MOB_SPAWNER.getDefaultState(), 2);
            TileEntity tileentity = worldIn.getTileEntity(position);
            if (tileentity instanceof TileEntityMobSpawner) {
                ((TileEntityMobSpawner)tileentity).getSpawnerBaseLogic().func_190894_a(this.pickMobSpawner(rand));
            } else {
                LOGGER.error("Failed to fetch mob spawner entity at ({}, {}, {})", (Object)position.getX(), (Object)position.getY(), (Object)position.getZ());
            }
            return true;
        }
        return false;
    }

    private ResourceLocation pickMobSpawner(Random rand) {
        return SPAWNERTYPES[rand.nextInt(SPAWNERTYPES.length)];
    }
}

