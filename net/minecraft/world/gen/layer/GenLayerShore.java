package net.minecraft.world.gen.layer;

import net.minecraft.init.Biomes;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeJungle;
import net.minecraft.world.biome.BiomeMesa;
import net.minecraft.world.gen.layer.GenLayer;
import net.minecraft.world.gen.layer.IntCache;

public class GenLayerShore
extends GenLayer {
    public GenLayerShore(long p_i2130_1_, GenLayer p_i2130_3_) {
        super(p_i2130_1_);
        this.parent = p_i2130_3_;
    }

    @Override
    public int[] getInts(int areaX, int areaY, int areaWidth, int areaHeight) {
        int[] aint = this.parent.getInts(areaX - 1, areaY - 1, areaWidth + 2, areaHeight + 2);
        int[] aint1 = IntCache.getIntCache(areaWidth * areaHeight);
        for (int i2 = 0; i2 < areaHeight; ++i2) {
            for (int j2 = 0; j2 < areaWidth; ++j2) {
                this.initChunkSeed(j2 + areaX, i2 + areaY);
                int k2 = aint[j2 + 1 + (i2 + 1) * (areaWidth + 2)];
                Biome biome = Biome.getBiome(k2);
                if (k2 == Biome.getIdForBiome(Biomes.MUSHROOM_ISLAND)) {
                    int j22 = aint[j2 + 1 + (i2 + 1 - 1) * (areaWidth + 2)];
                    int i3 = aint[j2 + 1 + 1 + (i2 + 1) * (areaWidth + 2)];
                    int l3 = aint[j2 + 1 - 1 + (i2 + 1) * (areaWidth + 2)];
                    int k4 = aint[j2 + 1 + (i2 + 1 + 1) * (areaWidth + 2)];
                    if (j22 != Biome.getIdForBiome(Biomes.OCEAN) && i3 != Biome.getIdForBiome(Biomes.OCEAN) && l3 != Biome.getIdForBiome(Biomes.OCEAN) && k4 != Biome.getIdForBiome(Biomes.OCEAN)) {
                        aint1[j2 + i2 * areaWidth] = k2;
                        continue;
                    }
                    aint1[j2 + i2 * areaWidth] = Biome.getIdForBiome(Biomes.MUSHROOM_ISLAND_SHORE);
                    continue;
                }
                if (biome != null && biome.getBiomeClass() == BiomeJungle.class) {
                    int i22 = aint[j2 + 1 + (i2 + 1 - 1) * (areaWidth + 2)];
                    int l2 = aint[j2 + 1 + 1 + (i2 + 1) * (areaWidth + 2)];
                    int k3 = aint[j2 + 1 - 1 + (i2 + 1) * (areaWidth + 2)];
                    int j4 = aint[j2 + 1 + (i2 + 1 + 1) * (areaWidth + 2)];
                    if (this.isJungleCompatible(i22) && this.isJungleCompatible(l2) && this.isJungleCompatible(k3) && this.isJungleCompatible(j4)) {
                        if (!(GenLayerShore.isBiomeOceanic(i22) || GenLayerShore.isBiomeOceanic(l2) || GenLayerShore.isBiomeOceanic(k3) || GenLayerShore.isBiomeOceanic(j4))) {
                            aint1[j2 + i2 * areaWidth] = k2;
                            continue;
                        }
                        aint1[j2 + i2 * areaWidth] = Biome.getIdForBiome(Biomes.BEACH);
                        continue;
                    }
                    aint1[j2 + i2 * areaWidth] = Biome.getIdForBiome(Biomes.JUNGLE_EDGE);
                    continue;
                }
                if (k2 != Biome.getIdForBiome(Biomes.EXTREME_HILLS) && k2 != Biome.getIdForBiome(Biomes.EXTREME_HILLS_WITH_TREES) && k2 != Biome.getIdForBiome(Biomes.EXTREME_HILLS_EDGE)) {
                    if (biome != null && biome.isSnowyBiome()) {
                        this.replaceIfNeighborOcean(aint, aint1, j2, i2, areaWidth, k2, Biome.getIdForBiome(Biomes.COLD_BEACH));
                        continue;
                    }
                    if (k2 != Biome.getIdForBiome(Biomes.MESA) && k2 != Biome.getIdForBiome(Biomes.MESA_ROCK)) {
                        if (k2 != Biome.getIdForBiome(Biomes.OCEAN) && k2 != Biome.getIdForBiome(Biomes.DEEP_OCEAN) && k2 != Biome.getIdForBiome(Biomes.RIVER) && k2 != Biome.getIdForBiome(Biomes.SWAMPLAND)) {
                            int l1 = aint[j2 + 1 + (i2 + 1 - 1) * (areaWidth + 2)];
                            int k22 = aint[j2 + 1 + 1 + (i2 + 1) * (areaWidth + 2)];
                            int j3 = aint[j2 + 1 - 1 + (i2 + 1) * (areaWidth + 2)];
                            int i4 = aint[j2 + 1 + (i2 + 1 + 1) * (areaWidth + 2)];
                            if (!(GenLayerShore.isBiomeOceanic(l1) || GenLayerShore.isBiomeOceanic(k22) || GenLayerShore.isBiomeOceanic(j3) || GenLayerShore.isBiomeOceanic(i4))) {
                                aint1[j2 + i2 * areaWidth] = k2;
                                continue;
                            }
                            aint1[j2 + i2 * areaWidth] = Biome.getIdForBiome(Biomes.BEACH);
                            continue;
                        }
                        aint1[j2 + i2 * areaWidth] = k2;
                        continue;
                    }
                    int l2 = aint[j2 + 1 + (i2 + 1 - 1) * (areaWidth + 2)];
                    int i1 = aint[j2 + 1 + 1 + (i2 + 1) * (areaWidth + 2)];
                    int j1 = aint[j2 + 1 - 1 + (i2 + 1) * (areaWidth + 2)];
                    int k1 = aint[j2 + 1 + (i2 + 1 + 1) * (areaWidth + 2)];
                    if (!(GenLayerShore.isBiomeOceanic(l2) || GenLayerShore.isBiomeOceanic(i1) || GenLayerShore.isBiomeOceanic(j1) || GenLayerShore.isBiomeOceanic(k1))) {
                        if (this.isMesa(l2) && this.isMesa(i1) && this.isMesa(j1) && this.isMesa(k1)) {
                            aint1[j2 + i2 * areaWidth] = k2;
                            continue;
                        }
                        aint1[j2 + i2 * areaWidth] = Biome.getIdForBiome(Biomes.DESERT);
                        continue;
                    }
                    aint1[j2 + i2 * areaWidth] = k2;
                    continue;
                }
                this.replaceIfNeighborOcean(aint, aint1, j2, i2, areaWidth, k2, Biome.getIdForBiome(Biomes.STONE_BEACH));
            }
        }
        return aint1;
    }

    private void replaceIfNeighborOcean(int[] p_151632_1_, int[] p_151632_2_, int p_151632_3_, int p_151632_4_, int p_151632_5_, int p_151632_6_, int p_151632_7_) {
        if (GenLayerShore.isBiomeOceanic(p_151632_6_)) {
            p_151632_2_[p_151632_3_ + p_151632_4_ * p_151632_5_] = p_151632_6_;
        } else {
            int i2 = p_151632_1_[p_151632_3_ + 1 + (p_151632_4_ + 1 - 1) * (p_151632_5_ + 2)];
            int j2 = p_151632_1_[p_151632_3_ + 1 + 1 + (p_151632_4_ + 1) * (p_151632_5_ + 2)];
            int k2 = p_151632_1_[p_151632_3_ + 1 - 1 + (p_151632_4_ + 1) * (p_151632_5_ + 2)];
            int l2 = p_151632_1_[p_151632_3_ + 1 + (p_151632_4_ + 1 + 1) * (p_151632_5_ + 2)];
            p_151632_2_[p_151632_3_ + p_151632_4_ * p_151632_5_] = !GenLayerShore.isBiomeOceanic(i2) && !GenLayerShore.isBiomeOceanic(j2) && !GenLayerShore.isBiomeOceanic(k2) && !GenLayerShore.isBiomeOceanic(l2) ? p_151632_6_ : p_151632_7_;
        }
    }

    private boolean isJungleCompatible(int p_151631_1_) {
        if (Biome.getBiome(p_151631_1_) != null && Biome.getBiome(p_151631_1_).getBiomeClass() == BiomeJungle.class) {
            return true;
        }
        return p_151631_1_ == Biome.getIdForBiome(Biomes.JUNGLE_EDGE) || p_151631_1_ == Biome.getIdForBiome(Biomes.JUNGLE) || p_151631_1_ == Biome.getIdForBiome(Biomes.JUNGLE_HILLS) || p_151631_1_ == Biome.getIdForBiome(Biomes.FOREST) || p_151631_1_ == Biome.getIdForBiome(Biomes.TAIGA) || GenLayerShore.isBiomeOceanic(p_151631_1_);
    }

    private boolean isMesa(int p_151633_1_) {
        return Biome.getBiome(p_151633_1_) instanceof BiomeMesa;
    }
}

