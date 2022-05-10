package io.netty.buffer;

import io.netty.buffer.AbstractByteBufAllocator;
import io.netty.buffer.AbstractReferenceCountedByteBuf;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufAllocatorMetricProvider;
import io.netty.buffer.PoolArena;
import io.netty.buffer.PoolArenaMetric;
import io.netty.buffer.PoolThreadCache;
import io.netty.buffer.PooledByteBufAllocatorMetric;
import io.netty.buffer.UnpooledDirectByteBuf;
import io.netty.buffer.UnpooledHeapByteBuf;
import io.netty.buffer.UnpooledUnsafeHeapByteBuf;
import io.netty.buffer.UnsafeByteBufUtil;
import io.netty.util.concurrent.FastThreadLocal;
import io.netty.util.concurrent.FastThreadLocalThread;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.StringUtil;
import io.netty.util.internal.SystemPropertyUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PooledByteBufAllocator
extends AbstractByteBufAllocator
implements ByteBufAllocatorMetricProvider {
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(PooledByteBufAllocator.class);
    private static final int DEFAULT_NUM_HEAP_ARENA;
    private static final int DEFAULT_NUM_DIRECT_ARENA;
    private static final int DEFAULT_PAGE_SIZE;
    private static final int DEFAULT_MAX_ORDER;
    private static final int DEFAULT_TINY_CACHE_SIZE;
    private static final int DEFAULT_SMALL_CACHE_SIZE;
    private static final int DEFAULT_NORMAL_CACHE_SIZE;
    private static final int DEFAULT_MAX_CACHED_BUFFER_CAPACITY;
    private static final int DEFAULT_CACHE_TRIM_INTERVAL;
    private static final boolean DEFAULT_USE_CACHE_FOR_ALL_THREADS;
    private static final int DEFAULT_DIRECT_MEMORY_CACHE_ALIGNMENT;
    private static final int MIN_PAGE_SIZE = 4096;
    private static final int MAX_CHUNK_SIZE = 0x40000000;
    public static final PooledByteBufAllocator DEFAULT;
    private final PoolArena<byte[]>[] heapArenas;
    private final PoolArena<ByteBuffer>[] directArenas;
    private final int tinyCacheSize;
    private final int smallCacheSize;
    private final int normalCacheSize;
    private final List<PoolArenaMetric> heapArenaMetrics;
    private final List<PoolArenaMetric> directArenaMetrics;
    private final PoolThreadLocalCache threadCache;
    private final int chunkSize;
    private final PooledByteBufAllocatorMetric metric;

    public PooledByteBufAllocator() {
        this(false);
    }

    public PooledByteBufAllocator(boolean preferDirect) {
        this(preferDirect, DEFAULT_NUM_HEAP_ARENA, DEFAULT_NUM_DIRECT_ARENA, DEFAULT_PAGE_SIZE, DEFAULT_MAX_ORDER);
    }

    public PooledByteBufAllocator(int nHeapArena, int nDirectArena, int pageSize, int maxOrder) {
        this(false, nHeapArena, nDirectArena, pageSize, maxOrder);
    }

    @Deprecated
    public PooledByteBufAllocator(boolean preferDirect, int nHeapArena, int nDirectArena, int pageSize, int maxOrder) {
        this(preferDirect, nHeapArena, nDirectArena, pageSize, maxOrder, DEFAULT_TINY_CACHE_SIZE, DEFAULT_SMALL_CACHE_SIZE, DEFAULT_NORMAL_CACHE_SIZE);
    }

    @Deprecated
    public PooledByteBufAllocator(boolean preferDirect, int nHeapArena, int nDirectArena, int pageSize, int maxOrder, int tinyCacheSize, int smallCacheSize, int normalCacheSize) {
        this(preferDirect, nHeapArena, nDirectArena, pageSize, maxOrder, tinyCacheSize, smallCacheSize, normalCacheSize, DEFAULT_USE_CACHE_FOR_ALL_THREADS, DEFAULT_DIRECT_MEMORY_CACHE_ALIGNMENT);
    }

    public PooledByteBufAllocator(boolean preferDirect, int nHeapArena, int nDirectArena, int pageSize, int maxOrder, int tinyCacheSize, int smallCacheSize, int normalCacheSize, boolean useCacheForAllThreads) {
        this(preferDirect, nHeapArena, nDirectArena, pageSize, maxOrder, tinyCacheSize, smallCacheSize, normalCacheSize, useCacheForAllThreads, DEFAULT_DIRECT_MEMORY_CACHE_ALIGNMENT);
    }

    public PooledByteBufAllocator(boolean preferDirect, int nHeapArena, int nDirectArena, int pageSize, int maxOrder, int tinyCacheSize, int smallCacheSize, int normalCacheSize, boolean useCacheForAllThreads, int directMemoryCacheAlignment) {
        super(preferDirect);
        PoolArena arena;
        int i2;
        ArrayList<PoolArena.HeapArena> metrics;
        this.threadCache = new PoolThreadLocalCache(useCacheForAllThreads);
        this.tinyCacheSize = tinyCacheSize;
        this.smallCacheSize = smallCacheSize;
        this.normalCacheSize = normalCacheSize;
        this.chunkSize = PooledByteBufAllocator.validateAndCalculateChunkSize(pageSize, maxOrder);
        if (nHeapArena < 0) {
            throw new IllegalArgumentException("nHeapArena: " + nHeapArena + " (expected: >= 0)");
        }
        if (nDirectArena < 0) {
            throw new IllegalArgumentException("nDirectArea: " + nDirectArena + " (expected: >= 0)");
        }
        if (directMemoryCacheAlignment < 0) {
            throw new IllegalArgumentException("directMemoryCacheAlignment: " + directMemoryCacheAlignment + " (expected: >= 0)");
        }
        if (directMemoryCacheAlignment > 0 && !PooledByteBufAllocator.isDirectMemoryCacheAlignmentSupported()) {
            throw new IllegalArgumentException("directMemoryCacheAlignment is not supported");
        }
        if ((directMemoryCacheAlignment & -directMemoryCacheAlignment) != directMemoryCacheAlignment) {
            throw new IllegalArgumentException("directMemoryCacheAlignment: " + directMemoryCacheAlignment + " (expected: power of two)");
        }
        int pageShifts = PooledByteBufAllocator.validateAndCalculatePageShifts(pageSize);
        if (nHeapArena > 0) {
            this.heapArenas = PooledByteBufAllocator.newArenaArray(nHeapArena);
            metrics = new ArrayList<PoolArena.HeapArena>(this.heapArenas.length);
            for (i2 = 0; i2 < this.heapArenas.length; ++i2) {
                this.heapArenas[i2] = arena = new PoolArena.HeapArena(this, pageSize, maxOrder, pageShifts, this.chunkSize, directMemoryCacheAlignment);
                metrics.add((PoolArena.HeapArena)arena);
            }
            this.heapArenaMetrics = Collections.unmodifiableList(metrics);
        } else {
            this.heapArenas = null;
            this.heapArenaMetrics = Collections.emptyList();
        }
        if (nDirectArena > 0) {
            this.directArenas = PooledByteBufAllocator.newArenaArray(nDirectArena);
            metrics = new ArrayList(this.directArenas.length);
            for (i2 = 0; i2 < this.directArenas.length; ++i2) {
                this.directArenas[i2] = arena = new PoolArena.DirectArena(this, pageSize, maxOrder, pageShifts, this.chunkSize, directMemoryCacheAlignment);
                metrics.add((PoolArena.HeapArena)arena);
            }
            this.directArenaMetrics = Collections.unmodifiableList(metrics);
        } else {
            this.directArenas = null;
            this.directArenaMetrics = Collections.emptyList();
        }
        this.metric = new PooledByteBufAllocatorMetric(this);
    }

    private static <T> PoolArena<T>[] newArenaArray(int size) {
        return new PoolArena[size];
    }

    private static int validateAndCalculatePageShifts(int pageSize) {
        if (pageSize < 4096) {
            throw new IllegalArgumentException("pageSize: " + pageSize + " (expected: " + 4096 + ")");
        }
        if ((pageSize & pageSize - 1) != 0) {
            throw new IllegalArgumentException("pageSize: " + pageSize + " (expected: power of 2)");
        }
        return 31 - Integer.numberOfLeadingZeros(pageSize);
    }

    private static int validateAndCalculateChunkSize(int pageSize, int maxOrder) {
        if (maxOrder > 14) {
            throw new IllegalArgumentException("maxOrder: " + maxOrder + " (expected: 0-14)");
        }
        int chunkSize = pageSize;
        for (int i2 = maxOrder; i2 > 0; --i2) {
            if (chunkSize > 0x20000000) {
                throw new IllegalArgumentException(String.format("pageSize (%d) << maxOrder (%d) must not exceed %d", pageSize, maxOrder, 0x40000000));
            }
            chunkSize <<= 1;
        }
        return chunkSize;
    }

    @Override
    protected ByteBuf newHeapBuffer(int initialCapacity, int maxCapacity) {
        PoolThreadCache cache = (PoolThreadCache)this.threadCache.get();
        PoolArena<byte[]> heapArena = cache.heapArena;
        AbstractReferenceCountedByteBuf buf2 = heapArena != null ? heapArena.allocate(cache, initialCapacity, maxCapacity) : (PlatformDependent.hasUnsafe() ? new UnpooledUnsafeHeapByteBuf((ByteBufAllocator)this, initialCapacity, maxCapacity) : new UnpooledHeapByteBuf((ByteBufAllocator)this, initialCapacity, maxCapacity));
        return PooledByteBufAllocator.toLeakAwareBuffer(buf2);
    }

    @Override
    protected ByteBuf newDirectBuffer(int initialCapacity, int maxCapacity) {
        PoolThreadCache cache = (PoolThreadCache)this.threadCache.get();
        PoolArena<ByteBuffer> directArena = cache.directArena;
        AbstractReferenceCountedByteBuf buf2 = directArena != null ? directArena.allocate(cache, initialCapacity, maxCapacity) : (PlatformDependent.hasUnsafe() ? UnsafeByteBufUtil.newUnsafeDirectByteBuf(this, initialCapacity, maxCapacity) : new UnpooledDirectByteBuf((ByteBufAllocator)this, initialCapacity, maxCapacity));
        return PooledByteBufAllocator.toLeakAwareBuffer(buf2);
    }

    public static int defaultNumHeapArena() {
        return DEFAULT_NUM_HEAP_ARENA;
    }

    public static int defaultNumDirectArena() {
        return DEFAULT_NUM_DIRECT_ARENA;
    }

    public static int defaultPageSize() {
        return DEFAULT_PAGE_SIZE;
    }

    public static int defaultMaxOrder() {
        return DEFAULT_MAX_ORDER;
    }

    public static int defaultTinyCacheSize() {
        return DEFAULT_TINY_CACHE_SIZE;
    }

    public static int defaultSmallCacheSize() {
        return DEFAULT_SMALL_CACHE_SIZE;
    }

    public static int defaultNormalCacheSize() {
        return DEFAULT_NORMAL_CACHE_SIZE;
    }

    public static boolean isDirectMemoryCacheAlignmentSupported() {
        return PlatformDependent.hasUnsafe();
    }

    @Override
    public boolean isDirectBufferPooled() {
        return this.directArenas != null;
    }

    @Deprecated
    public boolean hasThreadLocalCache() {
        return this.threadCache.isSet();
    }

    @Deprecated
    public void freeThreadLocalCache() {
        this.threadCache.remove();
    }

    @Override
    public PooledByteBufAllocatorMetric metric() {
        return this.metric;
    }

    @Deprecated
    public int numHeapArenas() {
        return this.heapArenaMetrics.size();
    }

    @Deprecated
    public int numDirectArenas() {
        return this.directArenaMetrics.size();
    }

    @Deprecated
    public List<PoolArenaMetric> heapArenas() {
        return this.heapArenaMetrics;
    }

    @Deprecated
    public List<PoolArenaMetric> directArenas() {
        return this.directArenaMetrics;
    }

    @Deprecated
    public int numThreadLocalCaches() {
        PoolArena<Object>[] arenas;
        PoolArena<Object>[] arrpoolArena = arenas = this.heapArenas != null ? this.heapArenas : this.directArenas;
        if (arenas == null) {
            return 0;
        }
        int total = 0;
        for (PoolArena<Object> arena : arenas) {
            total += arena.numThreadCaches.get();
        }
        return total;
    }

    @Deprecated
    public int tinyCacheSize() {
        return this.tinyCacheSize;
    }

    @Deprecated
    public int smallCacheSize() {
        return this.smallCacheSize;
    }

    @Deprecated
    public int normalCacheSize() {
        return this.normalCacheSize;
    }

    @Deprecated
    public final int chunkSize() {
        return this.chunkSize;
    }

    final long usedHeapMemory() {
        return PooledByteBufAllocator.usedMemory(this.heapArenas);
    }

    final long usedDirectMemory() {
        return PooledByteBufAllocator.usedMemory(this.directArenas);
    }

    private static long usedMemory(PoolArena<?> ... arenas) {
        if (arenas == null) {
            return -1L;
        }
        long used = 0L;
        for (PoolArena<?> arena : arenas) {
            if ((used += arena.numActiveBytes()) >= 0L) continue;
            return Long.MAX_VALUE;
        }
        return used;
    }

    final PoolThreadCache threadCache() {
        return (PoolThreadCache)this.threadCache.get();
    }

    public String dumpStats() {
        int heapArenasLen = this.heapArenas == null ? 0 : this.heapArenas.length;
        StringBuilder buf2 = new StringBuilder(512).append(heapArenasLen).append(" heap arena(s):").append(StringUtil.NEWLINE);
        if (heapArenasLen > 0) {
            for (PoolArena<byte[]> a2 : this.heapArenas) {
                buf2.append(a2);
            }
        }
        int directArenasLen = this.directArenas == null ? 0 : this.directArenas.length;
        buf2.append(directArenasLen).append(" direct arena(s):").append(StringUtil.NEWLINE);
        if (directArenasLen > 0) {
            for (PoolArena<ByteBuffer> a3 : this.directArenas) {
                buf2.append(a3);
            }
        }
        return buf2.toString();
    }

    static {
        int defaultPageSize = SystemPropertyUtil.getInt("io.netty.allocator.pageSize", 8192);
        Throwable pageSizeFallbackCause = null;
        try {
            PooledByteBufAllocator.validateAndCalculatePageShifts(defaultPageSize);
        }
        catch (Throwable t2) {
            pageSizeFallbackCause = t2;
            defaultPageSize = 8192;
        }
        DEFAULT_PAGE_SIZE = defaultPageSize;
        int defaultMaxOrder = SystemPropertyUtil.getInt("io.netty.allocator.maxOrder", 11);
        Throwable maxOrderFallbackCause = null;
        try {
            PooledByteBufAllocator.validateAndCalculateChunkSize(DEFAULT_PAGE_SIZE, defaultMaxOrder);
        }
        catch (Throwable t3) {
            maxOrderFallbackCause = t3;
            defaultMaxOrder = 11;
        }
        DEFAULT_MAX_ORDER = defaultMaxOrder;
        Runtime runtime = Runtime.getRuntime();
        int defaultMinNumArena = runtime.availableProcessors() * 2;
        int defaultChunkSize = DEFAULT_PAGE_SIZE << DEFAULT_MAX_ORDER;
        DEFAULT_NUM_HEAP_ARENA = Math.max(0, SystemPropertyUtil.getInt("io.netty.allocator.numHeapArenas", (int)Math.min((long)defaultMinNumArena, runtime.maxMemory() / (long)defaultChunkSize / 2L / 3L)));
        DEFAULT_NUM_DIRECT_ARENA = Math.max(0, SystemPropertyUtil.getInt("io.netty.allocator.numDirectArenas", (int)Math.min((long)defaultMinNumArena, PlatformDependent.maxDirectMemory() / (long)defaultChunkSize / 2L / 3L)));
        DEFAULT_TINY_CACHE_SIZE = SystemPropertyUtil.getInt("io.netty.allocator.tinyCacheSize", 512);
        DEFAULT_SMALL_CACHE_SIZE = SystemPropertyUtil.getInt("io.netty.allocator.smallCacheSize", 256);
        DEFAULT_NORMAL_CACHE_SIZE = SystemPropertyUtil.getInt("io.netty.allocator.normalCacheSize", 64);
        DEFAULT_MAX_CACHED_BUFFER_CAPACITY = SystemPropertyUtil.getInt("io.netty.allocator.maxCachedBufferCapacity", 32768);
        DEFAULT_CACHE_TRIM_INTERVAL = SystemPropertyUtil.getInt("io.netty.allocator.cacheTrimInterval", 8192);
        DEFAULT_USE_CACHE_FOR_ALL_THREADS = SystemPropertyUtil.getBoolean("io.netty.allocator.useCacheForAllThreads", true);
        DEFAULT_DIRECT_MEMORY_CACHE_ALIGNMENT = SystemPropertyUtil.getInt("io.netty.allocator.directMemoryCacheAlignment", 0);
        if (logger.isDebugEnabled()) {
            logger.debug("-Dio.netty.allocator.numHeapArenas: {}", (Object)DEFAULT_NUM_HEAP_ARENA);
            logger.debug("-Dio.netty.allocator.numDirectArenas: {}", (Object)DEFAULT_NUM_DIRECT_ARENA);
            if (pageSizeFallbackCause == null) {
                logger.debug("-Dio.netty.allocator.pageSize: {}", (Object)DEFAULT_PAGE_SIZE);
            } else {
                logger.debug("-Dio.netty.allocator.pageSize: {}", (Object)DEFAULT_PAGE_SIZE, (Object)pageSizeFallbackCause);
            }
            if (maxOrderFallbackCause == null) {
                logger.debug("-Dio.netty.allocator.maxOrder: {}", (Object)DEFAULT_MAX_ORDER);
            } else {
                logger.debug("-Dio.netty.allocator.maxOrder: {}", (Object)DEFAULT_MAX_ORDER, (Object)maxOrderFallbackCause);
            }
            logger.debug("-Dio.netty.allocator.chunkSize: {}", (Object)(DEFAULT_PAGE_SIZE << DEFAULT_MAX_ORDER));
            logger.debug("-Dio.netty.allocator.tinyCacheSize: {}", (Object)DEFAULT_TINY_CACHE_SIZE);
            logger.debug("-Dio.netty.allocator.smallCacheSize: {}", (Object)DEFAULT_SMALL_CACHE_SIZE);
            logger.debug("-Dio.netty.allocator.normalCacheSize: {}", (Object)DEFAULT_NORMAL_CACHE_SIZE);
            logger.debug("-Dio.netty.allocator.maxCachedBufferCapacity: {}", (Object)DEFAULT_MAX_CACHED_BUFFER_CAPACITY);
            logger.debug("-Dio.netty.allocator.cacheTrimInterval: {}", (Object)DEFAULT_CACHE_TRIM_INTERVAL);
            logger.debug("-Dio.netty.allocator.useCacheForAllThreads: {}", (Object)DEFAULT_USE_CACHE_FOR_ALL_THREADS);
        }
        DEFAULT = new PooledByteBufAllocator(PlatformDependent.directBufferPreferred());
    }

    final class PoolThreadLocalCache
    extends FastThreadLocal<PoolThreadCache> {
        private final boolean useCacheForAllThreads;

        PoolThreadLocalCache(boolean useCacheForAllThreads) {
            this.useCacheForAllThreads = useCacheForAllThreads;
        }

        @Override
        protected synchronized PoolThreadCache initialValue() {
            PoolArena<byte[]> heapArena = this.leastUsedArena(PooledByteBufAllocator.this.heapArenas);
            PoolArena<ByteBuffer> directArena = this.leastUsedArena(PooledByteBufAllocator.this.directArenas);
            if (this.useCacheForAllThreads || Thread.currentThread() instanceof FastThreadLocalThread) {
                return new PoolThreadCache(heapArena, directArena, PooledByteBufAllocator.this.tinyCacheSize, PooledByteBufAllocator.this.smallCacheSize, PooledByteBufAllocator.this.normalCacheSize, DEFAULT_MAX_CACHED_BUFFER_CAPACITY, DEFAULT_CACHE_TRIM_INTERVAL);
            }
            return new PoolThreadCache(heapArena, directArena, 0, 0, 0, 0, 0);
        }

        @Override
        protected void onRemoval(PoolThreadCache threadCache) {
            threadCache.free();
        }

        private <T> PoolArena<T> leastUsedArena(PoolArena<T>[] arenas) {
            if (arenas == null || arenas.length == 0) {
                return null;
            }
            PoolArena<T> minArena = arenas[0];
            for (int i2 = 1; i2 < arenas.length; ++i2) {
                PoolArena<T> arena = arenas[i2];
                if (arena.numThreadCaches.get() >= minArena.numThreadCaches.get()) continue;
                minArena = arena;
            }
            return minArena;
        }
    }
}

