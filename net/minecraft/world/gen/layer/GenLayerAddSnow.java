package net.minecraft.world.gen.layer;

import net.minecraft.world.gen.layer.GenLayer;
import net.minecraft.world.gen.layer.IntCache;

public class GenLayerAddSnow
extends GenLayer {
    public GenLayerAddSnow(long p_i2121_1_, GenLayer p_i2121_3_) {
        super(p_i2121_1_);
        this.parent = p_i2121_3_;
    }

    @Override
    public int[] getInts(int areaX, int areaY, int areaWidth, int areaHeight) {
        int i2 = areaX - 1;
        int j2 = areaY - 1;
        int k2 = areaWidth + 2;
        int l2 = areaHeight + 2;
        int[] aint = this.parent.getInts(i2, j2, k2, l2);
        int[] aint1 = IntCache.getIntCache(areaWidth * areaHeight);
        for (int i1 = 0; i1 < areaHeight; ++i1) {
            for (int j1 = 0; j1 < areaWidth; ++j1) {
                int k1 = aint[j1 + 1 + (i1 + 1) * k2];
                this.initChunkSeed(j1 + areaX, i1 + areaY);
                if (k1 == 0) {
                    aint1[j1 + i1 * areaWidth] = 0;
                    continue;
                }
                int l1 = this.nextInt(6);
                l1 = l1 == 0 ? 4 : (l1 <= 1 ? 3 : 1);
                aint1[j1 + i1 * areaWidth] = l1;
            }
        }
        return aint1;
    }
}

