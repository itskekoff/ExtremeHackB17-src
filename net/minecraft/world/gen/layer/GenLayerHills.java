package net.minecraft.world.gen.layer;

import net.minecraft.init.Biomes;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.layer.GenLayer;
import net.minecraft.world.gen.layer.IntCache;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GenLayerHills
extends GenLayer {
    private static final Logger LOGGER = LogManager.getLogger();
    private final GenLayer riverLayer;

    public GenLayerHills(long p_i45479_1_, GenLayer p_i45479_3_, GenLayer p_i45479_4_) {
        super(p_i45479_1_);
        this.parent = p_i45479_3_;
        this.riverLayer = p_i45479_4_;
    }

    @Override
    public int[] getInts(int areaX, int areaY, int areaWidth, int areaHeight) {
        int[] aint = this.parent.getInts(areaX - 1, areaY - 1, areaWidth + 2, areaHeight + 2);
        int[] aint1 = this.riverLayer.getInts(areaX - 1, areaY - 1, areaWidth + 2, areaHeight + 2);
        int[] aint2 = IntCache.getIntCache(areaWidth * areaHeight);
        for (int i2 = 0; i2 < areaHeight; ++i2) {
            for (int j2 = 0; j2 < areaWidth; ++j2) {
                Biome biome;
                boolean flag1;
                boolean flag;
                this.initChunkSeed(j2 + areaX, i2 + areaY);
                int k2 = aint[j2 + 1 + (i2 + 1) * (areaWidth + 2)];
                int l2 = aint1[j2 + 1 + (i2 + 1) * (areaWidth + 2)];
                boolean bl2 = flag = (l2 - 2) % 29 == 0;
                if (k2 > 255) {
                    LOGGER.debug("old! {}", (Object)k2);
                }
                boolean bl3 = flag1 = (biome = Biome.getBiomeForId(k2)) != null && biome.isMutation();
                if (k2 != 0 && l2 >= 2 && (l2 - 2) % 29 == 1 && !flag1) {
                    Biome biome3 = Biome.getMutationForBiome(biome);
                    aint2[j2 + i2 * areaWidth] = biome3 == null ? k2 : Biome.getIdForBiome(biome3);
                    continue;
                }
                if (this.nextInt(3) != 0 && !flag) {
                    aint2[j2 + i2 * areaWidth] = k2;
                    continue;
                }
                Biome biome1 = biome;
                if (biome == Biomes.DESERT) {
                    biome1 = Biomes.DESERT_HILLS;
                } else if (biome == Biomes.FOREST) {
                    biome1 = Biomes.FOREST_HILLS;
                } else if (biome == Biomes.BIRCH_FOREST) {
                    biome1 = Biomes.BIRCH_FOREST_HILLS;
                } else if (biome == Biomes.ROOFED_FOREST) {
                    biome1 = Biomes.PLAINS;
                } else if (biome == Biomes.TAIGA) {
                    biome1 = Biomes.TAIGA_HILLS;
                } else if (biome == Biomes.REDWOOD_TAIGA) {
                    biome1 = Biomes.REDWOOD_TAIGA_HILLS;
                } else if (biome == Biomes.COLD_TAIGA) {
                    biome1 = Biomes.COLD_TAIGA_HILLS;
                } else if (biome == Biomes.PLAINS) {
                    biome1 = this.nextInt(3) == 0 ? Biomes.FOREST_HILLS : Biomes.FOREST;
                } else if (biome == Biomes.ICE_PLAINS) {
                    biome1 = Biomes.ICE_MOUNTAINS;
                } else if (biome == Biomes.JUNGLE) {
                    biome1 = Biomes.JUNGLE_HILLS;
                } else if (biome == Biomes.OCEAN) {
                    biome1 = Biomes.DEEP_OCEAN;
                } else if (biome == Biomes.EXTREME_HILLS) {
                    biome1 = Biomes.EXTREME_HILLS_WITH_TREES;
                } else if (biome == Biomes.SAVANNA) {
                    biome1 = Biomes.SAVANNA_PLATEAU;
                } else if (GenLayerHills.biomesEqualOrMesaPlateau(k2, Biome.getIdForBiome(Biomes.MESA_ROCK))) {
                    biome1 = Biomes.MESA;
                } else if (biome == Biomes.DEEP_OCEAN && this.nextInt(3) == 0) {
                    int i1 = this.nextInt(2);
                    biome1 = i1 == 0 ? Biomes.PLAINS : Biomes.FOREST;
                }
                int j22 = Biome.getIdForBiome(biome1);
                if (flag && j22 != k2) {
                    Biome biome2 = Biome.getMutationForBiome(biome1);
                    int n2 = j22 = biome2 == null ? k2 : Biome.getIdForBiome(biome2);
                }
                if (j22 == k2) {
                    aint2[j2 + i2 * areaWidth] = k2;
                    continue;
                }
                int k22 = aint[j2 + 1 + (i2 + 0) * (areaWidth + 2)];
                int j1 = aint[j2 + 2 + (i2 + 1) * (areaWidth + 2)];
                int k1 = aint[j2 + 0 + (i2 + 1) * (areaWidth + 2)];
                int l1 = aint[j2 + 1 + (i2 + 2) * (areaWidth + 2)];
                int i22 = 0;
                if (GenLayerHills.biomesEqualOrMesaPlateau(k22, k2)) {
                    ++i22;
                }
                if (GenLayerHills.biomesEqualOrMesaPlateau(j1, k2)) {
                    ++i22;
                }
                if (GenLayerHills.biomesEqualOrMesaPlateau(k1, k2)) {
                    ++i22;
                }
                if (GenLayerHills.biomesEqualOrMesaPlateau(l1, k2)) {
                    ++i22;
                }
                aint2[j2 + i2 * areaWidth] = i22 >= 3 ? j22 : k2;
            }
        }
        return aint2;
    }
}

