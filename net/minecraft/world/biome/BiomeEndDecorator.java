package net.minecraft.world.biome;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ContiguousSet;
import com.google.common.collect.DiscreteDomain;
import com.google.common.collect.Lists;
import com.google.common.collect.Range;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeDecorator;
import net.minecraft.world.gen.feature.WorldGenSpikes;

public class BiomeEndDecorator
extends BiomeDecorator {
    private static final LoadingCache<Long, WorldGenSpikes.EndSpike[]> SPIKE_CACHE = CacheBuilder.newBuilder().expireAfterWrite(5L, TimeUnit.MINUTES).build(new SpikeCacheLoader());
    private final WorldGenSpikes spikeGen = new WorldGenSpikes();

    @Override
    protected void genDecorations(Biome biomeIn, World worldIn, Random random) {
        WorldGenSpikes.EndSpike[] aworldgenspikes$endspike;
        this.generateOres(worldIn, random);
        WorldGenSpikes.EndSpike[] arrendSpike = aworldgenspikes$endspike = BiomeEndDecorator.getSpikesForWorld(worldIn);
        int n2 = aworldgenspikes$endspike.length;
        for (int i2 = 0; i2 < n2; ++i2) {
            WorldGenSpikes.EndSpike worldgenspikes$endspike = arrendSpike[i2];
            if (!worldgenspikes$endspike.doesStartInChunk(this.chunkPos)) continue;
            this.spikeGen.setSpike(worldgenspikes$endspike);
            this.spikeGen.generate(worldIn, random, new BlockPos(worldgenspikes$endspike.getCenterX(), 45, worldgenspikes$endspike.getCenterZ()));
        }
    }

    public static WorldGenSpikes.EndSpike[] getSpikesForWorld(World p_185426_0_) {
        Random random = new Random(p_185426_0_.getSeed());
        long i2 = random.nextLong() & 0xFFFFL;
        return SPIKE_CACHE.getUnchecked(i2);
    }

    static class SpikeCacheLoader
    extends CacheLoader<Long, WorldGenSpikes.EndSpike[]> {
        private SpikeCacheLoader() {
        }

        @Override
        public WorldGenSpikes.EndSpike[] load(Long p_load_1_) throws Exception {
            ArrayList<Integer> list = Lists.newArrayList(ContiguousSet.create(Range.closedOpen(0, 10), DiscreteDomain.integers()));
            Collections.shuffle(list, new Random(p_load_1_));
            WorldGenSpikes.EndSpike[] aworldgenspikes$endspike = new WorldGenSpikes.EndSpike[10];
            for (int i2 = 0; i2 < 10; ++i2) {
                int j2 = (int)(42.0 * Math.cos(2.0 * (-Math.PI + 0.3141592653589793 * (double)i2)));
                int k2 = (int)(42.0 * Math.sin(2.0 * (-Math.PI + 0.3141592653589793 * (double)i2)));
                int l2 = (Integer)list.get(i2);
                int i1 = 2 + l2 / 3;
                int j1 = 76 + l2 * 3;
                boolean flag = l2 == 1 || l2 == 2;
                aworldgenspikes$endspike[i2] = new WorldGenSpikes.EndSpike(j2, k2, i1, j1, flag);
            }
            return aworldgenspikes$endspike;
        }
    }
}

