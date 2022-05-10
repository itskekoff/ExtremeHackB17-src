package net.minecraft.world.gen;

import java.util.Random;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkPrimer;

public class MapGenBase {
    protected int range = 8;
    protected Random rand = new Random();
    protected World worldObj;

    public void generate(World worldIn, int x2, int z2, ChunkPrimer primer) {
        int i2 = this.range;
        this.worldObj = worldIn;
        this.rand.setSeed(worldIn.getSeed());
        long j2 = this.rand.nextLong();
        long k2 = this.rand.nextLong();
        for (int l2 = x2 - i2; l2 <= x2 + i2; ++l2) {
            for (int i1 = z2 - i2; i1 <= z2 + i2; ++i1) {
                long j1 = (long)l2 * j2;
                long k1 = (long)i1 * k2;
                this.rand.setSeed(j1 ^ k1 ^ worldIn.getSeed());
                this.recursiveGenerate(worldIn, l2, i1, x2, z2, primer);
            }
        }
    }

    public static void func_191068_a(long p_191068_0_, Random p_191068_2_, int p_191068_3_, int p_191068_4_) {
        p_191068_2_.setSeed(p_191068_0_);
        long i2 = p_191068_2_.nextLong();
        long j2 = p_191068_2_.nextLong();
        long k2 = (long)p_191068_3_ * i2;
        long l2 = (long)p_191068_4_ * j2;
        p_191068_2_.setSeed(k2 ^ l2 ^ p_191068_0_);
    }

    protected void recursiveGenerate(World worldIn, int chunkX, int chunkZ, int p_180701_4_, int p_180701_5_, ChunkPrimer chunkPrimerIn) {
    }
}

