package net.minecraft.world.biome;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import java.util.List;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeProvider;

public class BiomeCache {
    private final BiomeProvider chunkManager;
    private long lastCleanupTime;
    private final Long2ObjectMap<Block> cacheMap = new Long2ObjectOpenHashMap<Block>(4096);
    private final List<Block> cache = Lists.newArrayList();

    public BiomeCache(BiomeProvider chunkManagerIn) {
        this.chunkManager = chunkManagerIn;
    }

    public Block getBiomeCacheBlock(int x2, int z2) {
        long i2 = (long)(x2 >>= 4) & 0xFFFFFFFFL | ((long)(z2 >>= 4) & 0xFFFFFFFFL) << 32;
        Block biomecache$block = (Block)this.cacheMap.get(i2);
        if (biomecache$block == null) {
            biomecache$block = new Block(x2, z2);
            this.cacheMap.put(i2, biomecache$block);
            this.cache.add(biomecache$block);
        }
        biomecache$block.lastAccessTime = MinecraftServer.getCurrentTimeMillis();
        return biomecache$block;
    }

    public Biome getBiome(int x2, int z2, Biome defaultValue) {
        Biome biome = this.getBiomeCacheBlock(x2, z2).getBiome(x2, z2);
        return biome == null ? defaultValue : biome;
    }

    public void cleanupCache() {
        long i2 = MinecraftServer.getCurrentTimeMillis();
        long j2 = i2 - this.lastCleanupTime;
        if (j2 > 7500L || j2 < 0L) {
            this.lastCleanupTime = i2;
            for (int k2 = 0; k2 < this.cache.size(); ++k2) {
                Block biomecache$block = this.cache.get(k2);
                long l2 = i2 - biomecache$block.lastAccessTime;
                if (l2 <= 30000L && l2 >= 0L) continue;
                this.cache.remove(k2--);
                long i1 = (long)biomecache$block.xPosition & 0xFFFFFFFFL | ((long)biomecache$block.zPosition & 0xFFFFFFFFL) << 32;
                this.cacheMap.remove(i1);
            }
        }
    }

    public Biome[] getCachedBiomes(int x2, int z2) {
        return this.getBiomeCacheBlock((int)x2, (int)z2).biomes;
    }

    public class Block {
        public Biome[] biomes = new Biome[256];
        public int xPosition;
        public int zPosition;
        public long lastAccessTime;

        public Block(int x2, int z2) {
            this.xPosition = x2;
            this.zPosition = z2;
            BiomeCache.this.chunkManager.getBiomes(this.biomes, x2 << 4, z2 << 4, 16, 16, false);
        }

        public Biome getBiome(int x2, int z2) {
            return this.biomes[x2 & 0xF | (z2 & 0xF) << 4];
        }
    }
}

