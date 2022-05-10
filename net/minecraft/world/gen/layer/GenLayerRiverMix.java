package net.minecraft.world.gen.layer;

import net.minecraft.init.Biomes;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.layer.GenLayer;
import net.minecraft.world.gen.layer.IntCache;

public class GenLayerRiverMix
extends GenLayer {
    private final GenLayer biomePatternGeneratorChain;
    private final GenLayer riverPatternGeneratorChain;

    public GenLayerRiverMix(long p_i2129_1_, GenLayer p_i2129_3_, GenLayer p_i2129_4_) {
        super(p_i2129_1_);
        this.biomePatternGeneratorChain = p_i2129_3_;
        this.riverPatternGeneratorChain = p_i2129_4_;
    }

    @Override
    public void initWorldGenSeed(long seed) {
        this.biomePatternGeneratorChain.initWorldGenSeed(seed);
        this.riverPatternGeneratorChain.initWorldGenSeed(seed);
        super.initWorldGenSeed(seed);
    }

    @Override
    public int[] getInts(int areaX, int areaY, int areaWidth, int areaHeight) {
        int[] aint = this.biomePatternGeneratorChain.getInts(areaX, areaY, areaWidth, areaHeight);
        int[] aint1 = this.riverPatternGeneratorChain.getInts(areaX, areaY, areaWidth, areaHeight);
        int[] aint2 = IntCache.getIntCache(areaWidth * areaHeight);
        for (int i2 = 0; i2 < areaWidth * areaHeight; ++i2) {
            if (aint[i2] != Biome.getIdForBiome(Biomes.OCEAN) && aint[i2] != Biome.getIdForBiome(Biomes.DEEP_OCEAN)) {
                if (aint1[i2] == Biome.getIdForBiome(Biomes.RIVER)) {
                    if (aint[i2] == Biome.getIdForBiome(Biomes.ICE_PLAINS)) {
                        aint2[i2] = Biome.getIdForBiome(Biomes.FROZEN_RIVER);
                        continue;
                    }
                    if (aint[i2] != Biome.getIdForBiome(Biomes.MUSHROOM_ISLAND) && aint[i2] != Biome.getIdForBiome(Biomes.MUSHROOM_ISLAND_SHORE)) {
                        aint2[i2] = aint1[i2] & 0xFF;
                        continue;
                    }
                    aint2[i2] = Biome.getIdForBiome(Biomes.MUSHROOM_ISLAND_SHORE);
                    continue;
                }
                aint2[i2] = aint[i2];
                continue;
            }
            aint2[i2] = aint[i2];
        }
        return aint2;
    }
}

