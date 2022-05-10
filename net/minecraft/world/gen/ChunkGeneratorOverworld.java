package net.minecraft.world.gen;

import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.WorldEntitySpawner;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.ChunkGeneratorSettings;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.MapGenBase;
import net.minecraft.world.gen.MapGenCaves;
import net.minecraft.world.gen.MapGenRavine;
import net.minecraft.world.gen.NoiseGeneratorOctaves;
import net.minecraft.world.gen.NoiseGeneratorPerlin;
import net.minecraft.world.gen.feature.WorldGenDungeons;
import net.minecraft.world.gen.feature.WorldGenLakes;
import net.minecraft.world.gen.structure.MapGenMineshaft;
import net.minecraft.world.gen.structure.MapGenScatteredFeature;
import net.minecraft.world.gen.structure.MapGenStronghold;
import net.minecraft.world.gen.structure.MapGenVillage;
import net.minecraft.world.gen.structure.StructureOceanMonument;
import net.minecraft.world.gen.structure.WoodlandMansion;

public class ChunkGeneratorOverworld
implements IChunkGenerator {
    protected static final IBlockState STONE = Blocks.STONE.getDefaultState();
    private final Random rand;
    private final NoiseGeneratorOctaves minLimitPerlinNoise;
    private final NoiseGeneratorOctaves maxLimitPerlinNoise;
    private final NoiseGeneratorOctaves mainPerlinNoise;
    private final NoiseGeneratorPerlin surfaceNoise;
    public NoiseGeneratorOctaves scaleNoise;
    public NoiseGeneratorOctaves depthNoise;
    public NoiseGeneratorOctaves forestNoise;
    private final World worldObj;
    private final boolean mapFeaturesEnabled;
    private final WorldType terrainType;
    private final double[] heightMap;
    private final float[] biomeWeights;
    private ChunkGeneratorSettings settings;
    private IBlockState oceanBlock = Blocks.WATER.getDefaultState();
    private double[] depthBuffer = new double[256];
    private final MapGenBase caveGenerator = new MapGenCaves();
    private final MapGenStronghold strongholdGenerator = new MapGenStronghold();
    private final MapGenVillage villageGenerator = new MapGenVillage();
    private final MapGenMineshaft mineshaftGenerator = new MapGenMineshaft();
    private final MapGenScatteredFeature scatteredFeatureGenerator = new MapGenScatteredFeature();
    private final MapGenBase ravineGenerator = new MapGenRavine();
    private final StructureOceanMonument oceanMonumentGenerator = new StructureOceanMonument();
    private final WoodlandMansion field_191060_C = new WoodlandMansion(this);
    private Biome[] biomesForGeneration;
    double[] mainNoiseRegion;
    double[] minLimitRegion;
    double[] maxLimitRegion;
    double[] depthRegion;

    public ChunkGeneratorOverworld(World worldIn, long seed, boolean mapFeaturesEnabledIn, String p_i46668_5_) {
        this.worldObj = worldIn;
        this.mapFeaturesEnabled = mapFeaturesEnabledIn;
        this.terrainType = worldIn.getWorldInfo().getTerrainType();
        this.rand = new Random(seed);
        this.minLimitPerlinNoise = new NoiseGeneratorOctaves(this.rand, 16);
        this.maxLimitPerlinNoise = new NoiseGeneratorOctaves(this.rand, 16);
        this.mainPerlinNoise = new NoiseGeneratorOctaves(this.rand, 8);
        this.surfaceNoise = new NoiseGeneratorPerlin(this.rand, 4);
        this.scaleNoise = new NoiseGeneratorOctaves(this.rand, 10);
        this.depthNoise = new NoiseGeneratorOctaves(this.rand, 16);
        this.forestNoise = new NoiseGeneratorOctaves(this.rand, 8);
        this.heightMap = new double[825];
        this.biomeWeights = new float[25];
        for (int i2 = -2; i2 <= 2; ++i2) {
            for (int j2 = -2; j2 <= 2; ++j2) {
                float f2;
                this.biomeWeights[i2 + 2 + (j2 + 2) * 5] = f2 = 10.0f / MathHelper.sqrt((float)(i2 * i2 + j2 * j2) + 0.2f);
            }
        }
        if (p_i46668_5_ != null) {
            this.settings = ChunkGeneratorSettings.Factory.jsonToFactory(p_i46668_5_).build();
            this.oceanBlock = this.settings.useLavaOceans ? Blocks.LAVA.getDefaultState() : Blocks.WATER.getDefaultState();
            worldIn.setSeaLevel(this.settings.seaLevel);
        }
    }

    public void setBlocksInChunk(int x2, int z2, ChunkPrimer primer) {
        this.biomesForGeneration = this.worldObj.getBiomeProvider().getBiomesForGeneration(this.biomesForGeneration, x2 * 4 - 2, z2 * 4 - 2, 10, 10);
        this.generateHeightmap(x2 * 4, 0, z2 * 4);
        for (int i2 = 0; i2 < 4; ++i2) {
            int j2 = i2 * 5;
            int k2 = (i2 + 1) * 5;
            for (int l2 = 0; l2 < 4; ++l2) {
                int i1 = (j2 + l2) * 33;
                int j1 = (j2 + l2 + 1) * 33;
                int k1 = (k2 + l2) * 33;
                int l1 = (k2 + l2 + 1) * 33;
                for (int i22 = 0; i22 < 32; ++i22) {
                    double d0 = 0.125;
                    double d1 = this.heightMap[i1 + i22];
                    double d2 = this.heightMap[j1 + i22];
                    double d3 = this.heightMap[k1 + i22];
                    double d4 = this.heightMap[l1 + i22];
                    double d5 = (this.heightMap[i1 + i22 + 1] - d1) * 0.125;
                    double d6 = (this.heightMap[j1 + i22 + 1] - d2) * 0.125;
                    double d7 = (this.heightMap[k1 + i22 + 1] - d3) * 0.125;
                    double d8 = (this.heightMap[l1 + i22 + 1] - d4) * 0.125;
                    for (int j22 = 0; j22 < 8; ++j22) {
                        double d9 = 0.25;
                        double d10 = d1;
                        double d11 = d2;
                        double d12 = (d3 - d1) * 0.25;
                        double d13 = (d4 - d2) * 0.25;
                        for (int k22 = 0; k22 < 4; ++k22) {
                            double d14 = 0.25;
                            double d16 = (d11 - d10) * 0.25;
                            double lvt_45_1_ = d10 - d16;
                            for (int l22 = 0; l22 < 4; ++l22) {
                                double d15;
                                lvt_45_1_ += d16;
                                if (d15 > 0.0) {
                                    primer.setBlockState(i2 * 4 + k22, i22 * 8 + j22, l2 * 4 + l22, STONE);
                                    continue;
                                }
                                if (i22 * 8 + j22 >= this.settings.seaLevel) continue;
                                primer.setBlockState(i2 * 4 + k22, i22 * 8 + j22, l2 * 4 + l22, this.oceanBlock);
                            }
                            d10 += d12;
                            d11 += d13;
                        }
                        d1 += d5;
                        d2 += d6;
                        d3 += d7;
                        d4 += d8;
                    }
                }
            }
        }
    }

    public void replaceBiomeBlocks(int x2, int z2, ChunkPrimer primer, Biome[] biomesIn) {
        double d0 = 0.03125;
        this.depthBuffer = this.surfaceNoise.getRegion(this.depthBuffer, x2 * 16, z2 * 16, 16, 16, 0.0625, 0.0625, 1.0);
        for (int i2 = 0; i2 < 16; ++i2) {
            for (int j2 = 0; j2 < 16; ++j2) {
                Biome biome = biomesIn[j2 + i2 * 16];
                biome.genTerrainBlocks(this.worldObj, this.rand, primer, x2 * 16 + i2, z2 * 16 + j2, this.depthBuffer[j2 + i2 * 16]);
            }
        }
    }

    @Override
    public Chunk provideChunk(int x2, int z2) {
        this.rand.setSeed((long)x2 * 341873128712L + (long)z2 * 132897987541L);
        ChunkPrimer chunkprimer = new ChunkPrimer();
        this.setBlocksInChunk(x2, z2, chunkprimer);
        this.biomesForGeneration = this.worldObj.getBiomeProvider().getBiomes(this.biomesForGeneration, x2 * 16, z2 * 16, 16, 16);
        this.replaceBiomeBlocks(x2, z2, chunkprimer, this.biomesForGeneration);
        if (this.settings.useCaves) {
            this.caveGenerator.generate(this.worldObj, x2, z2, chunkprimer);
        }
        if (this.settings.useRavines) {
            this.ravineGenerator.generate(this.worldObj, x2, z2, chunkprimer);
        }
        if (this.mapFeaturesEnabled) {
            if (this.settings.useMineShafts) {
                this.mineshaftGenerator.generate(this.worldObj, x2, z2, chunkprimer);
            }
            if (this.settings.useVillages) {
                this.villageGenerator.generate(this.worldObj, x2, z2, chunkprimer);
            }
            if (this.settings.useStrongholds) {
                this.strongholdGenerator.generate(this.worldObj, x2, z2, chunkprimer);
            }
            if (this.settings.useTemples) {
                this.scatteredFeatureGenerator.generate(this.worldObj, x2, z2, chunkprimer);
            }
            if (this.settings.useMonuments) {
                this.oceanMonumentGenerator.generate(this.worldObj, x2, z2, chunkprimer);
            }
            if (this.settings.field_191077_z) {
                this.field_191060_C.generate(this.worldObj, x2, z2, chunkprimer);
            }
        }
        Chunk chunk = new Chunk(this.worldObj, chunkprimer, x2, z2);
        byte[] abyte = chunk.getBiomeArray();
        for (int i2 = 0; i2 < abyte.length; ++i2) {
            abyte[i2] = (byte)Biome.getIdForBiome(this.biomesForGeneration[i2]);
        }
        chunk.generateSkylightMap();
        return chunk;
    }

    private void generateHeightmap(int p_185978_1_, int p_185978_2_, int p_185978_3_) {
        this.depthRegion = this.depthNoise.generateNoiseOctaves(this.depthRegion, p_185978_1_, p_185978_3_, 5, 5, this.settings.depthNoiseScaleX, this.settings.depthNoiseScaleZ, this.settings.depthNoiseScaleExponent);
        float f2 = this.settings.coordinateScale;
        float f1 = this.settings.heightScale;
        this.mainNoiseRegion = this.mainPerlinNoise.generateNoiseOctaves(this.mainNoiseRegion, p_185978_1_, p_185978_2_, p_185978_3_, 5, 33, 5, f2 / this.settings.mainNoiseScaleX, f1 / this.settings.mainNoiseScaleY, f2 / this.settings.mainNoiseScaleZ);
        this.minLimitRegion = this.minLimitPerlinNoise.generateNoiseOctaves(this.minLimitRegion, p_185978_1_, p_185978_2_, p_185978_3_, 5, 33, 5, f2, f1, f2);
        this.maxLimitRegion = this.maxLimitPerlinNoise.generateNoiseOctaves(this.maxLimitRegion, p_185978_1_, p_185978_2_, p_185978_3_, 5, 33, 5, f2, f1, f2);
        int i2 = 0;
        int j2 = 0;
        for (int k2 = 0; k2 < 5; ++k2) {
            for (int l2 = 0; l2 < 5; ++l2) {
                float f22 = 0.0f;
                float f3 = 0.0f;
                float f4 = 0.0f;
                int i1 = 2;
                Biome biome = this.biomesForGeneration[k2 + 2 + (l2 + 2) * 10];
                for (int j1 = -2; j1 <= 2; ++j1) {
                    for (int k1 = -2; k1 <= 2; ++k1) {
                        Biome biome1 = this.biomesForGeneration[k2 + j1 + 2 + (l2 + k1 + 2) * 10];
                        float f5 = this.settings.biomeDepthOffSet + biome1.getBaseHeight() * this.settings.biomeDepthWeight;
                        float f6 = this.settings.biomeScaleOffset + biome1.getHeightVariation() * this.settings.biomeScaleWeight;
                        if (this.terrainType == WorldType.AMPLIFIED && f5 > 0.0f) {
                            f5 = 1.0f + f5 * 2.0f;
                            f6 = 1.0f + f6 * 4.0f;
                        }
                        float f7 = this.biomeWeights[j1 + 2 + (k1 + 2) * 5] / (f5 + 2.0f);
                        if (biome1.getBaseHeight() > biome.getBaseHeight()) {
                            f7 /= 2.0f;
                        }
                        f22 += f6 * f7;
                        f3 += f5 * f7;
                        f4 += f7;
                    }
                }
                f22 /= f4;
                f3 /= f4;
                f22 = f22 * 0.9f + 0.1f;
                f3 = (f3 * 4.0f - 1.0f) / 8.0f;
                double d7 = this.depthRegion[j2] / 8000.0;
                if (d7 < 0.0) {
                    d7 = -d7 * 0.3;
                }
                if ((d7 = d7 * 3.0 - 2.0) < 0.0) {
                    if ((d7 /= 2.0) < -1.0) {
                        d7 = -1.0;
                    }
                    d7 /= 1.4;
                    d7 /= 2.0;
                } else {
                    if (d7 > 1.0) {
                        d7 = 1.0;
                    }
                    d7 /= 8.0;
                }
                ++j2;
                double d8 = f3;
                double d9 = f22;
                d8 += d7 * 0.2;
                d8 = d8 * (double)this.settings.baseSize / 8.0;
                double d0 = (double)this.settings.baseSize + d8 * 4.0;
                for (int l1 = 0; l1 < 33; ++l1) {
                    double d1 = ((double)l1 - d0) * (double)this.settings.stretchY * 128.0 / 256.0 / d9;
                    if (d1 < 0.0) {
                        d1 *= 4.0;
                    }
                    double d2 = this.minLimitRegion[i2] / (double)this.settings.lowerLimitScale;
                    double d3 = this.maxLimitRegion[i2] / (double)this.settings.upperLimitScale;
                    double d4 = (this.mainNoiseRegion[i2] / 10.0 + 1.0) / 2.0;
                    double d5 = MathHelper.clampedLerp(d2, d3, d4) - d1;
                    if (l1 > 29) {
                        double d6 = (float)(l1 - 29) / 3.0f;
                        d5 = d5 * (1.0 - d6) + -10.0 * d6;
                    }
                    this.heightMap[i2] = d5;
                    ++i2;
                }
            }
        }
    }

    @Override
    public void populate(int x2, int z2) {
        BlockFalling.fallInstantly = true;
        int i2 = x2 * 16;
        int j2 = z2 * 16;
        BlockPos blockpos = new BlockPos(i2, 0, j2);
        Biome biome = this.worldObj.getBiome(blockpos.add(16, 0, 16));
        this.rand.setSeed(this.worldObj.getSeed());
        long k2 = this.rand.nextLong() / 2L * 2L + 1L;
        long l2 = this.rand.nextLong() / 2L * 2L + 1L;
        this.rand.setSeed((long)x2 * k2 + (long)z2 * l2 ^ this.worldObj.getSeed());
        boolean flag = false;
        ChunkPos chunkpos = new ChunkPos(x2, z2);
        if (this.mapFeaturesEnabled) {
            if (this.settings.useMineShafts) {
                this.mineshaftGenerator.generateStructure(this.worldObj, this.rand, chunkpos);
            }
            if (this.settings.useVillages) {
                flag = this.villageGenerator.generateStructure(this.worldObj, this.rand, chunkpos);
            }
            if (this.settings.useStrongholds) {
                this.strongholdGenerator.generateStructure(this.worldObj, this.rand, chunkpos);
            }
            if (this.settings.useTemples) {
                this.scatteredFeatureGenerator.generateStructure(this.worldObj, this.rand, chunkpos);
            }
            if (this.settings.useMonuments) {
                this.oceanMonumentGenerator.generateStructure(this.worldObj, this.rand, chunkpos);
            }
            if (this.settings.field_191077_z) {
                this.field_191060_C.generateStructure(this.worldObj, this.rand, chunkpos);
            }
        }
        if (biome != Biomes.DESERT && biome != Biomes.DESERT_HILLS && this.settings.useWaterLakes && !flag && this.rand.nextInt(this.settings.waterLakeChance) == 0) {
            int i1 = this.rand.nextInt(16) + 8;
            int j1 = this.rand.nextInt(256);
            int k1 = this.rand.nextInt(16) + 8;
            new WorldGenLakes(Blocks.WATER).generate(this.worldObj, this.rand, blockpos.add(i1, j1, k1));
        }
        if (!flag && this.rand.nextInt(this.settings.lavaLakeChance / 10) == 0 && this.settings.useLavaLakes) {
            int i22 = this.rand.nextInt(16) + 8;
            int l22 = this.rand.nextInt(this.rand.nextInt(248) + 8);
            int k3 = this.rand.nextInt(16) + 8;
            if (l22 < this.worldObj.getSeaLevel() || this.rand.nextInt(this.settings.lavaLakeChance / 8) == 0) {
                new WorldGenLakes(Blocks.LAVA).generate(this.worldObj, this.rand, blockpos.add(i22, l22, k3));
            }
        }
        if (this.settings.useDungeons) {
            for (int j22 = 0; j22 < this.settings.dungeonChance; ++j22) {
                int i3 = this.rand.nextInt(16) + 8;
                int l3 = this.rand.nextInt(256);
                int l1 = this.rand.nextInt(16) + 8;
                new WorldGenDungeons().generate(this.worldObj, this.rand, blockpos.add(i3, l3, l1));
            }
        }
        biome.decorate(this.worldObj, this.rand, new BlockPos(i2, 0, j2));
        WorldEntitySpawner.performWorldGenSpawning(this.worldObj, biome, i2 + 8, j2 + 8, 16, 16, this.rand);
        blockpos = blockpos.add(8, 0, 8);
        for (int k22 = 0; k22 < 16; ++k22) {
            for (int j3 = 0; j3 < 16; ++j3) {
                BlockPos blockpos1 = this.worldObj.getPrecipitationHeight(blockpos.add(k22, 0, j3));
                BlockPos blockpos2 = blockpos1.down();
                if (this.worldObj.canBlockFreezeWater(blockpos2)) {
                    this.worldObj.setBlockState(blockpos2, Blocks.ICE.getDefaultState(), 2);
                }
                if (!this.worldObj.canSnowAt(blockpos1, true)) continue;
                this.worldObj.setBlockState(blockpos1, Blocks.SNOW_LAYER.getDefaultState(), 2);
            }
        }
        BlockFalling.fallInstantly = false;
    }

    @Override
    public boolean generateStructures(Chunk chunkIn, int x2, int z2) {
        boolean flag = false;
        if (this.settings.useMonuments && this.mapFeaturesEnabled && chunkIn.getInhabitedTime() < 3600L) {
            flag |= this.oceanMonumentGenerator.generateStructure(this.worldObj, this.rand, new ChunkPos(x2, z2));
        }
        return flag;
    }

    @Override
    public List<Biome.SpawnListEntry> getPossibleCreatures(EnumCreatureType creatureType, BlockPos pos) {
        Biome biome = this.worldObj.getBiome(pos);
        if (this.mapFeaturesEnabled) {
            if (creatureType == EnumCreatureType.MONSTER && this.scatteredFeatureGenerator.isSwampHut(pos)) {
                return this.scatteredFeatureGenerator.getScatteredFeatureSpawnList();
            }
            if (creatureType == EnumCreatureType.MONSTER && this.settings.useMonuments && this.oceanMonumentGenerator.isPositionInStructure(this.worldObj, pos)) {
                return this.oceanMonumentGenerator.getScatteredFeatureSpawnList();
            }
        }
        return biome.getSpawnableList(creatureType);
    }

    @Override
    public boolean func_193414_a(World p_193414_1_, String p_193414_2_, BlockPos p_193414_3_) {
        if (!this.mapFeaturesEnabled) {
            return false;
        }
        if ("Stronghold".equals(p_193414_2_) && this.strongholdGenerator != null) {
            return this.strongholdGenerator.isInsideStructure(p_193414_3_);
        }
        if ("Mansion".equals(p_193414_2_) && this.field_191060_C != null) {
            return this.field_191060_C.isInsideStructure(p_193414_3_);
        }
        if ("Monument".equals(p_193414_2_) && this.oceanMonumentGenerator != null) {
            return this.oceanMonumentGenerator.isInsideStructure(p_193414_3_);
        }
        if ("Village".equals(p_193414_2_) && this.villageGenerator != null) {
            return this.villageGenerator.isInsideStructure(p_193414_3_);
        }
        if ("Mineshaft".equals(p_193414_2_) && this.mineshaftGenerator != null) {
            return this.mineshaftGenerator.isInsideStructure(p_193414_3_);
        }
        return "Temple".equals(p_193414_2_) && this.scatteredFeatureGenerator != null ? this.scatteredFeatureGenerator.isInsideStructure(p_193414_3_) : false;
    }

    @Override
    @Nullable
    public BlockPos getStrongholdGen(World worldIn, String structureName, BlockPos position, boolean p_180513_4_) {
        if (!this.mapFeaturesEnabled) {
            return null;
        }
        if ("Stronghold".equals(structureName) && this.strongholdGenerator != null) {
            return this.strongholdGenerator.getClosestStrongholdPos(worldIn, position, p_180513_4_);
        }
        if ("Mansion".equals(structureName) && this.field_191060_C != null) {
            return this.field_191060_C.getClosestStrongholdPos(worldIn, position, p_180513_4_);
        }
        if ("Monument".equals(structureName) && this.oceanMonumentGenerator != null) {
            return this.oceanMonumentGenerator.getClosestStrongholdPos(worldIn, position, p_180513_4_);
        }
        if ("Village".equals(structureName) && this.villageGenerator != null) {
            return this.villageGenerator.getClosestStrongholdPos(worldIn, position, p_180513_4_);
        }
        if ("Mineshaft".equals(structureName) && this.mineshaftGenerator != null) {
            return this.mineshaftGenerator.getClosestStrongholdPos(worldIn, position, p_180513_4_);
        }
        return "Temple".equals(structureName) && this.scatteredFeatureGenerator != null ? this.scatteredFeatureGenerator.getClosestStrongholdPos(worldIn, position, p_180513_4_) : null;
    }

    @Override
    public void recreateStructures(Chunk chunkIn, int x2, int z2) {
        if (this.mapFeaturesEnabled) {
            if (this.settings.useMineShafts) {
                this.mineshaftGenerator.generate(this.worldObj, x2, z2, null);
            }
            if (this.settings.useVillages) {
                this.villageGenerator.generate(this.worldObj, x2, z2, null);
            }
            if (this.settings.useStrongholds) {
                this.strongholdGenerator.generate(this.worldObj, x2, z2, null);
            }
            if (this.settings.useTemples) {
                this.scatteredFeatureGenerator.generate(this.worldObj, x2, z2, null);
            }
            if (this.settings.useMonuments) {
                this.oceanMonumentGenerator.generate(this.worldObj, x2, z2, null);
            }
            if (this.settings.field_191077_z) {
                this.field_191060_C.generate(this.worldObj, x2, z2, null);
            }
        }
    }
}

