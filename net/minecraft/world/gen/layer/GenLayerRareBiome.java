package net.minecraft.world.gen.layer;

import net.minecraft.init.Biomes;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.layer.GenLayer;
import net.minecraft.world.gen.layer.IntCache;

public class GenLayerRareBiome
extends GenLayer {
    public GenLayerRareBiome(long p_i45478_1_, GenLayer p_i45478_3_) {
        super(p_i45478_1_);
        this.parent = p_i45478_3_;
    }

    @Override
    public int[] getInts(int areaX, int areaY, int areaWidth, int areaHeight) {
        int[] aint = this.parent.getInts(areaX - 1, areaY - 1, areaWidth + 2, areaHeight + 2);
        int[] aint1 = IntCache.getIntCache(areaWidth * areaHeight);
        for (int i2 = 0; i2 < areaHeight; ++i2) {
            for (int j2 = 0; j2 < areaWidth; ++j2) {
                this.initChunkSeed(j2 + areaX, i2 + areaY);
                int k2 = aint[j2 + 1 + (i2 + 1) * (areaWidth + 2)];
                if (this.nextInt(57) == 0) {
                    if (k2 == Biome.getIdForBiome(Biomes.PLAINS)) {
                        aint1[j2 + i2 * areaWidth] = Biome.getIdForBiome(Biomes.MUTATED_PLAINS);
                        continue;
                    }
                    aint1[j2 + i2 * areaWidth] = k2;
                    continue;
                }
                aint1[j2 + i2 * areaWidth] = k2;
            }
        }
        return aint1;
    }
}

