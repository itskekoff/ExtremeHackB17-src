package net.minecraft.world.gen.structure;

import com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeProvider;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.ChunkGeneratorOverworld;
import net.minecraft.world.gen.structure.MapGenStructure;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.StructureStart;
import net.minecraft.world.gen.structure.WoodlandMansionPieces;

public class WoodlandMansion
extends MapGenStructure {
    private final int field_191073_b = 80;
    private final int field_191074_d = 20;
    public static final List<Biome> field_191072_a = Arrays.asList(Biomes.ROOFED_FOREST, Biomes.MUTATED_ROOFED_FOREST);
    private final ChunkGeneratorOverworld field_191075_h;

    public WoodlandMansion(ChunkGeneratorOverworld p_i47240_1_) {
        this.field_191075_h = p_i47240_1_;
    }

    @Override
    public String getStructureName() {
        return "Mansion";
    }

    @Override
    protected boolean canSpawnStructureAtCoords(int chunkX, int chunkZ) {
        boolean flag;
        int i2 = chunkX;
        int j2 = chunkZ;
        if (chunkX < 0) {
            i2 = chunkX - 79;
        }
        if (chunkZ < 0) {
            j2 = chunkZ - 79;
        }
        int k2 = i2 / 80;
        int l2 = j2 / 80;
        Random random = this.worldObj.setRandomSeed(k2, l2, 10387319);
        k2 *= 80;
        l2 *= 80;
        return chunkX == (k2 += (random.nextInt(60) + random.nextInt(60)) / 2) && chunkZ == (l2 += (random.nextInt(60) + random.nextInt(60)) / 2) && (flag = this.worldObj.getBiomeProvider().areBiomesViable(chunkX * 16 + 8, chunkZ * 16 + 8, 32, field_191072_a));
    }

    @Override
    public BlockPos getClosestStrongholdPos(World worldIn, BlockPos pos, boolean p_180706_3_) {
        this.worldObj = worldIn;
        BiomeProvider biomeprovider = worldIn.getBiomeProvider();
        return biomeprovider.func_190944_c() && biomeprovider.func_190943_d() != Biomes.ROOFED_FOREST ? null : WoodlandMansion.func_191069_a(worldIn, this, pos, 80, 20, 10387319, true, 100, p_180706_3_);
    }

    @Override
    protected StructureStart getStructureStart(int chunkX, int chunkZ) {
        return new Start(this.worldObj, this.field_191075_h, this.rand, chunkX, chunkZ);
    }

    public static class Start
    extends StructureStart {
        private boolean field_191093_c;

        public Start() {
        }

        public Start(World p_i47235_1_, ChunkGeneratorOverworld p_i47235_2_, Random p_i47235_3_, int p_i47235_4_, int p_i47235_5_) {
            super(p_i47235_4_, p_i47235_5_);
            this.func_191092_a(p_i47235_1_, p_i47235_2_, p_i47235_3_, p_i47235_4_, p_i47235_5_);
        }

        private void func_191092_a(World p_191092_1_, ChunkGeneratorOverworld p_191092_2_, Random p_191092_3_, int p_191092_4_, int p_191092_5_) {
            Rotation rotation = Rotation.values()[p_191092_3_.nextInt(Rotation.values().length)];
            ChunkPrimer chunkprimer = new ChunkPrimer();
            p_191092_2_.setBlocksInChunk(p_191092_4_, p_191092_5_, chunkprimer);
            int i2 = 5;
            int j2 = 5;
            if (rotation == Rotation.CLOCKWISE_90) {
                i2 = -5;
            } else if (rotation == Rotation.CLOCKWISE_180) {
                i2 = -5;
                j2 = -5;
            } else if (rotation == Rotation.COUNTERCLOCKWISE_90) {
                j2 = -5;
            }
            int k2 = chunkprimer.findGroundBlockIdx(7, 7);
            int l2 = chunkprimer.findGroundBlockIdx(7, 7 + j2);
            int i1 = chunkprimer.findGroundBlockIdx(7 + i2, 7);
            int j1 = chunkprimer.findGroundBlockIdx(7 + i2, 7 + j2);
            int k1 = Math.min(Math.min(k2, l2), Math.min(i1, j1));
            if (k1 < 60) {
                this.field_191093_c = false;
            } else {
                BlockPos blockpos = new BlockPos(p_191092_4_ * 16 + 8, k1 + 1, p_191092_5_ * 16 + 8);
                LinkedList<WoodlandMansionPieces.MansionTemplate> list = Lists.newLinkedList();
                WoodlandMansionPieces.func_191152_a(p_191092_1_.getSaveHandler().getStructureTemplateManager(), blockpos, rotation, list, p_191092_3_);
                this.components.addAll(list);
                this.updateBoundingBox();
                this.field_191093_c = true;
            }
        }

        @Override
        public void generateStructure(World worldIn, Random rand, StructureBoundingBox structurebb) {
            super.generateStructure(worldIn, rand, structurebb);
            int i2 = this.boundingBox.minY;
            for (int j2 = structurebb.minX; j2 <= structurebb.maxX; ++j2) {
                block1: for (int k2 = structurebb.minZ; k2 <= structurebb.maxZ; ++k2) {
                    BlockPos blockpos = new BlockPos(j2, i2, k2);
                    if (worldIn.isAirBlock(blockpos) || !this.boundingBox.isVecInside(blockpos)) continue;
                    boolean flag = false;
                    for (StructureComponent structurecomponent : this.components) {
                        if (!structurecomponent.boundingBox.isVecInside(blockpos)) continue;
                        flag = true;
                        break;
                    }
                    if (!flag) continue;
                    for (int l2 = i2 - 1; l2 > 1; --l2) {
                        BlockPos blockpos1 = new BlockPos(j2, l2, k2);
                        if (!worldIn.isAirBlock(blockpos1) && !worldIn.getBlockState(blockpos1).getMaterial().isLiquid()) continue block1;
                        worldIn.setBlockState(blockpos1, Blocks.COBBLESTONE.getDefaultState(), 2);
                    }
                }
            }
        }

        @Override
        public boolean isSizeableStructure() {
            return this.field_191093_c;
        }
    }
}

