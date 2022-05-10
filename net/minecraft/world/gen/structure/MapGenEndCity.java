package net.minecraft.world.gen.structure;

import java.util.Random;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.ChunkGeneratorEnd;
import net.minecraft.world.gen.structure.MapGenStructure;
import net.minecraft.world.gen.structure.StructureEndCityPieces;
import net.minecraft.world.gen.structure.StructureStart;

public class MapGenEndCity
extends MapGenStructure {
    private final int citySpacing = 20;
    private final int minCitySeparation = 11;
    private final ChunkGeneratorEnd endProvider;

    public MapGenEndCity(ChunkGeneratorEnd p_i46665_1_) {
        this.endProvider = p_i46665_1_;
    }

    @Override
    public String getStructureName() {
        return "EndCity";
    }

    @Override
    protected boolean canSpawnStructureAtCoords(int chunkX, int chunkZ) {
        int i2 = chunkX;
        int j2 = chunkZ;
        if (chunkX < 0) {
            chunkX -= 19;
        }
        if (chunkZ < 0) {
            chunkZ -= 19;
        }
        int k2 = chunkX / 20;
        int l2 = chunkZ / 20;
        Random random = this.worldObj.setRandomSeed(k2, l2, 10387313);
        k2 *= 20;
        l2 *= 20;
        if (i2 == (k2 += (random.nextInt(9) + random.nextInt(9)) / 2) && j2 == (l2 += (random.nextInt(9) + random.nextInt(9)) / 2) && this.endProvider.isIslandChunk(i2, j2)) {
            int i1 = MapGenEndCity.func_191070_b(i2, j2, this.endProvider);
            return i1 >= 60;
        }
        return false;
    }

    @Override
    protected StructureStart getStructureStart(int chunkX, int chunkZ) {
        return new Start(this.worldObj, this.endProvider, this.rand, chunkX, chunkZ);
    }

    @Override
    public BlockPos getClosestStrongholdPos(World worldIn, BlockPos pos, boolean p_180706_3_) {
        this.worldObj = worldIn;
        return MapGenEndCity.func_191069_a(worldIn, this, pos, 20, 11, 10387313, true, 100, p_180706_3_);
    }

    private static int func_191070_b(int p_191070_0_, int p_191070_1_, ChunkGeneratorEnd p_191070_2_) {
        Random random = new Random(p_191070_0_ + p_191070_1_ * 10387313);
        Rotation rotation = Rotation.values()[random.nextInt(Rotation.values().length)];
        ChunkPrimer chunkprimer = new ChunkPrimer();
        p_191070_2_.setBlocksInChunk(p_191070_0_, p_191070_1_, chunkprimer);
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
        return k1;
    }

    public static class Start
    extends StructureStart {
        private boolean isSizeable;

        public Start() {
        }

        public Start(World worldIn, ChunkGeneratorEnd chunkProvider, Random random, int chunkX, int chunkZ) {
            super(chunkX, chunkZ);
            this.create(worldIn, chunkProvider, random, chunkX, chunkZ);
        }

        private void create(World worldIn, ChunkGeneratorEnd chunkProvider, Random rnd, int chunkX, int chunkZ) {
            Random random = new Random(chunkX + chunkZ * 10387313);
            Rotation rotation = Rotation.values()[random.nextInt(Rotation.values().length)];
            int i2 = MapGenEndCity.func_191070_b(chunkX, chunkZ, chunkProvider);
            if (i2 < 60) {
                this.isSizeable = false;
            } else {
                BlockPos blockpos = new BlockPos(chunkX * 16 + 8, i2, chunkZ * 16 + 8);
                StructureEndCityPieces.func_191087_a(worldIn.getSaveHandler().getStructureTemplateManager(), blockpos, rotation, this.components, rnd);
                this.updateBoundingBox();
                this.isSizeable = true;
            }
        }

        @Override
        public boolean isSizeableStructure() {
            return this.isSizeable;
        }
    }
}

