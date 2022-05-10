package net.minecraft.world.gen.layer;

import net.minecraft.init.Biomes;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.ChunkGeneratorSettings;
import net.minecraft.world.gen.layer.GenLayer;
import net.minecraft.world.gen.layer.IntCache;

public class GenLayerBiome
extends GenLayer {
    private Biome[] warmBiomes = new Biome[]{Biomes.DESERT, Biomes.DESERT, Biomes.DESERT, Biomes.SAVANNA, Biomes.SAVANNA, Biomes.PLAINS};
    private final Biome[] mediumBiomes = new Biome[]{Biomes.FOREST, Biomes.ROOFED_FOREST, Biomes.EXTREME_HILLS, Biomes.PLAINS, Biomes.BIRCH_FOREST, Biomes.SWAMPLAND};
    private final Biome[] coldBiomes = new Biome[]{Biomes.FOREST, Biomes.EXTREME_HILLS, Biomes.TAIGA, Biomes.PLAINS};
    private final Biome[] iceBiomes = new Biome[]{Biomes.ICE_PLAINS, Biomes.ICE_PLAINS, Biomes.ICE_PLAINS, Biomes.COLD_TAIGA};
    private final ChunkGeneratorSettings settings;

    public GenLayerBiome(long p_i45560_1_, GenLayer p_i45560_3_, WorldType p_i45560_4_, ChunkGeneratorSettings p_i45560_5_) {
        super(p_i45560_1_);
        this.parent = p_i45560_3_;
        if (p_i45560_4_ == WorldType.DEFAULT_1_1) {
            this.warmBiomes = new Biome[]{Biomes.DESERT, Biomes.FOREST, Biomes.EXTREME_HILLS, Biomes.SWAMPLAND, Biomes.PLAINS, Biomes.TAIGA};
            this.settings = null;
        } else {
            this.settings = p_i45560_5_;
        }
    }

    @Override
    public int[] getInts(int areaX, int areaY, int areaWidth, int areaHeight) {
        int[] aint = this.parent.getInts(areaX, areaY, areaWidth, areaHeight);
        int[] aint1 = IntCache.getIntCache(areaWidth * areaHeight);
        for (int i2 = 0; i2 < areaHeight; ++i2) {
            for (int j2 = 0; j2 < areaWidth; ++j2) {
                this.initChunkSeed(j2 + areaX, i2 + areaY);
                int k2 = aint[j2 + i2 * areaWidth];
                int l2 = (k2 & 0xF00) >> 8;
                k2 &= 0xFFFFF0FF;
                if (this.settings != null && this.settings.fixedBiome >= 0) {
                    aint1[j2 + i2 * areaWidth] = this.settings.fixedBiome;
                    continue;
                }
                if (GenLayerBiome.isBiomeOceanic(k2)) {
                    aint1[j2 + i2 * areaWidth] = k2;
                    continue;
                }
                if (k2 == Biome.getIdForBiome(Biomes.MUSHROOM_ISLAND)) {
                    aint1[j2 + i2 * areaWidth] = k2;
                    continue;
                }
                if (k2 == 1) {
                    if (l2 > 0) {
                        if (this.nextInt(3) == 0) {
                            aint1[j2 + i2 * areaWidth] = Biome.getIdForBiome(Biomes.MESA_CLEAR_ROCK);
                            continue;
                        }
                        aint1[j2 + i2 * areaWidth] = Biome.getIdForBiome(Biomes.MESA_ROCK);
                        continue;
                    }
                    aint1[j2 + i2 * areaWidth] = Biome.getIdForBiome(this.warmBiomes[this.nextInt(this.warmBiomes.length)]);
                    continue;
                }
                if (k2 == 2) {
                    if (l2 > 0) {
                        aint1[j2 + i2 * areaWidth] = Biome.getIdForBiome(Biomes.JUNGLE);
                        continue;
                    }
                    aint1[j2 + i2 * areaWidth] = Biome.getIdForBiome(this.mediumBiomes[this.nextInt(this.mediumBiomes.length)]);
                    continue;
                }
                if (k2 == 3) {
                    if (l2 > 0) {
                        aint1[j2 + i2 * areaWidth] = Biome.getIdForBiome(Biomes.REDWOOD_TAIGA);
                        continue;
                    }
                    aint1[j2 + i2 * areaWidth] = Biome.getIdForBiome(this.coldBiomes[this.nextInt(this.coldBiomes.length)]);
                    continue;
                }
                aint1[j2 + i2 * areaWidth] = k2 == 4 ? Biome.getIdForBiome(this.iceBiomes[this.nextInt(this.iceBiomes.length)]) : Biome.getIdForBiome(Biomes.MUSHROOM_ISLAND);
            }
        }
        return aint1;
    }
}

