package io.netty.buffer;

import io.netty.buffer.PoolArenaMetric;
import io.netty.buffer.PoolChunk;
import io.netty.buffer.PoolChunkList;
import io.netty.buffer.PoolChunkListMetric;
import io.netty.buffer.PoolChunkMetric;
import io.netty.buffer.PoolSubpage;
import io.netty.buffer.PoolSubpageMetric;
import io.netty.buffer.PoolThreadCache;
import io.netty.buffer.PooledByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.buffer.PooledDirectByteBuf;
import io.netty.buffer.PooledHeapByteBuf;
import io.netty.buffer.PooledUnsafeDirectByteBuf;
import io.netty.buffer.PooledUnsafeHeapByteBuf;
import io.netty.util.internal.LongCounter;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.StringUtil;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

abstract class PoolArena<T>
implements PoolArenaMetric {
    static final boolean HAS_UNSAFE = PlatformDependent.hasUnsafe();
    static final int numTinySubpagePools = 32;
    final PooledByteBufAllocator parent;
    private final int maxOrder;
    final int pageSize;
    final int pageShifts;
    final int chunkSize;
    final int subpageOverflowMask;
    final int numSmallSubpagePools;
    final int directMemoryCacheAlignment;
    final int directMemoryCacheAlignmentMask;
    private final PoolSubpage<T>[] tinySubpagePools;
    private final PoolSubpage<T>[] smallSubpagePools;
    private final PoolChunkList<T> q050;
    private final PoolChunkList<T> q025;
    private final PoolChunkList<T> q000;
    private final PoolChunkList<T> qInit;
    private final PoolChunkList<T> q075;
    private final PoolChunkList<T> q100;
    private final List<PoolChunkListMetric> chunkListMetrics;
    private long allocationsNormal;
    private final LongCounter allocationsTiny = PlatformDependent.newLongCounter();
    private final LongCounter allocationsSmall = PlatformDependent.newLongCounter();
    private final LongCounter allocationsHuge = PlatformDependent.newLongCounter();
    private final LongCounter activeBytesHuge = PlatformDependent.newLongCounter();
    private long deallocationsTiny;
    private long deallocationsSmall;
    private long deallocationsNormal;
    private final LongCounter deallocationsHuge = PlatformDependent.newLongCounter();
    final AtomicInteger numThreadCaches = new AtomicInteger();

    protected PoolArena(PooledByteBufAllocator parent, int pageSize, int maxOrder, int pageShifts, int chunkSize, int cacheAlignment) {
        int i2;
        this.parent = parent;
        this.pageSize = pageSize;
        this.maxOrder = maxOrder;
        this.pageShifts = pageShifts;
        this.chunkSize = chunkSize;
        this.directMemoryCacheAlignment = cacheAlignment;
        this.directMemoryCacheAlignmentMask = cacheAlignment - 1;
        this.subpageOverflowMask = ~(pageSize - 1);
        this.tinySubpagePools = this.newSubpagePoolArray(32);
        for (i2 = 0; i2 < this.tinySubpagePools.length; ++i2) {
            this.tinySubpagePools[i2] = this.newSubpagePoolHead(pageSize);
        }
        this.numSmallSubpagePools = pageShifts - 9;
        this.smallSubpagePools = this.newSubpagePoolArray(this.numSmallSubpagePools);
        for (i2 = 0; i2 < this.smallSubpagePools.length; ++i2) {
            this.smallSubpagePools[i2] = this.newSubpagePoolHead(pageSize);
        }
        this.q100 = new PoolChunkList(this, null, 100, Integer.MAX_VALUE, chunkSize);
        this.q075 = new PoolChunkList<T>(this, this.q100, 75, 100, chunkSize);
        this.q050 = new PoolChunkList<T>(this, this.q075, 50, 100, chunkSize);
        this.q025 = new PoolChunkList<T>(this, this.q050, 25, 75, chunkSize);
        this.q000 = new PoolChunkList<T>(this, this.q025, 1, 50, chunkSize);
        this.qInit = new PoolChunkList<T>(this, this.q000, Integer.MIN_VALUE, 25, chunkSize);
        this.q100.prevList(this.q075);
        this.q075.prevList(this.q050);
        this.q050.prevList(this.q025);
        this.q025.prevList(this.q000);
        this.q000.prevList(null);
        this.qInit.prevList(this.qInit);
        ArrayList<PoolChunkList<T>> metrics = new ArrayList<PoolChunkList<T>>(6);
        metrics.add(this.qInit);
        metrics.add(this.q000);
        metrics.add(this.q025);
        metrics.add(this.q050);
        metrics.add(this.q075);
        metrics.add(this.q100);
        this.chunkListMetrics = Collections.unmodifiableList(metrics);
    }

    private PoolSubpage<T> newSubpagePoolHead(int pageSize) {
        PoolSubpage head = new PoolSubpage(pageSize);
        head.prev = head;
        head.next = head;
        return head;
    }

    private PoolSubpage<T>[] newSubpagePoolArray(int size) {
        return new PoolSubpage[size];
    }

    abstract boolean isDirect();

    PooledByteBuf<T> allocate(PoolThreadCache cache, int reqCapacity, int maxCapacity) {
        PooledByteBuf<T> buf2 = this.newByteBuf(maxCapacity);
        this.allocate(cache, buf2, reqCapacity);
        return buf2;
    }

    static int tinyIdx(int normCapacity) {
        return normCapacity >>> 4;
    }

    static int smallIdx(int normCapacity) {
        int tableIdx = 0;
        int i2 = normCapacity >>> 10;
        while (i2 != 0) {
            i2 >>>= 1;
            ++tableIdx;
        }
        return tableIdx;
    }

    boolean isTinyOrSmall(int normCapacity) {
        return (normCapacity & this.subpageOverflowMask) == 0;
    }

    static boolean isTiny(int normCapacity) {
        return (normCapacity & 0xFFFFFE00) == 0;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void allocate(PoolThreadCache cache, PooledByteBuf<T> buf2, int reqCapacity) {
        int normCapacity = this.normalizeCapacity(reqCapacity);
        if (this.isTinyOrSmall(normCapacity)) {
            PoolSubpage<T>[] table;
            int tableIdx;
            boolean tiny = PoolArena.isTiny(normCapacity);
            if (tiny) {
                if (cache.allocateTiny(this, buf2, reqCapacity, normCapacity)) {
                    return;
                }
                tableIdx = PoolArena.tinyIdx(normCapacity);
                table = this.tinySubpagePools;
            } else {
                if (cache.allocateSmall(this, buf2, reqCapacity, normCapacity)) {
                    return;
                }
                tableIdx = PoolArena.smallIdx(normCapacity);
                table = this.smallSubpagePools;
            }
            PoolSubpage<T> head = table[tableIdx];
            Object object = head;
            synchronized (object) {
                PoolSubpage s2 = head.next;
                if (s2 != head) {
                    assert (s2.doNotDestroy && s2.elemSize == normCapacity);
                    long handle = s2.allocate();
                    assert (handle >= 0L);
                    s2.chunk.initBufWithSubpage(buf2, handle, reqCapacity);
                    this.incTinySmallAllocation(tiny);
                    return;
                }
            }
            object = this;
            synchronized (object) {
                this.allocateNormal(buf2, reqCapacity, normCapacity);
            }
            this.incTinySmallAllocation(tiny);
            return;
        }
        if (normCapacity <= this.chunkSize) {
            if (cache.allocateNormal(this, buf2, reqCapacity, normCapacity)) {
                return;
            }
            PoolArena poolArena = this;
            synchronized (poolArena) {
                this.allocateNormal(buf2, reqCapacity, normCapacity);
                ++this.allocationsNormal;
            }
        } else {
            this.allocateHuge(buf2, reqCapacity);
        }
    }

    private void allocateNormal(PooledByteBuf<T> buf2, int reqCapacity, int normCapacity) {
        if (this.q050.allocate(buf2, reqCapacity, normCapacity) || this.q025.allocate(buf2, reqCapacity, normCapacity) || this.q000.allocate(buf2, reqCapacity, normCapacity) || this.qInit.allocate(buf2, reqCapacity, normCapacity) || this.q075.allocate(buf2, reqCapacity, normCapacity)) {
            return;
        }
        PoolChunk<T> c2 = this.newChunk(this.pageSize, this.maxOrder, this.pageShifts, this.chunkSize);
        long handle = c2.allocate(normCapacity);
        assert (handle > 0L);
        c2.initBuf(buf2, handle, reqCapacity);
        this.qInit.add(c2);
    }

    private void incTinySmallAllocation(boolean tiny) {
        if (tiny) {
            this.allocationsTiny.increment();
        } else {
            this.allocationsSmall.increment();
        }
    }

    private void allocateHuge(PooledByteBuf<T> buf2, int reqCapacity) {
        PoolChunk<T> chunk = this.newUnpooledChunk(reqCapacity);
        this.activeBytesHuge.add(chunk.chunkSize());
        buf2.initUnpooled(chunk, reqCapacity);
        this.allocationsHuge.increment();
    }

    void free(PoolChunk<T> chunk, long handle, int normCapacity, PoolThreadCache cache) {
        if (chunk.unpooled) {
            int size = chunk.chunkSize();
            this.destroyChunk(chunk);
            this.activeBytesHuge.add(-size);
            this.deallocationsHuge.increment();
        } else {
            SizeClass sizeClass = this.sizeClass(normCapacity);
            if (cache != null && cache.add(this, chunk, handle, normCapacity, sizeClass)) {
                return;
            }
            this.freeChunk(chunk, handle, sizeClass);
        }
    }

    private SizeClass sizeClass(int normCapacity) {
        if (!this.isTinyOrSmall(normCapacity)) {
            return SizeClass.Normal;
        }
        return PoolArena.isTiny(normCapacity) ? SizeClass.Tiny : SizeClass.Small;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    void freeChunk(PoolChunk<T> chunk, long handle, SizeClass sizeClass) {
        boolean destroyChunk;
        PoolArena poolArena = this;
        synchronized (poolArena) {
            switch (sizeClass) {
                case Normal: {
                    ++this.deallocationsNormal;
                    break;
                }
                case Small: {
                    ++this.deallocationsSmall;
                    break;
                }
                case Tiny: {
                    ++this.deallocationsTiny;
                    break;
                }
                default: {
                    throw new Error();
                }
            }
            destroyChunk = !chunk.parent.free(chunk, handle);
        }
        if (destroyChunk) {
            this.destroyChunk(chunk);
        }
    }

    PoolSubpage<T> findSubpagePoolHead(int elemSize) {
        PoolSubpage<T>[] table;
        int tableIdx;
        if (PoolArena.isTiny(elemSize)) {
            tableIdx = elemSize >>> 4;
            table = this.tinySubpagePools;
        } else {
            tableIdx = 0;
            elemSize >>>= 10;
            while (elemSize != 0) {
                elemSize >>>= 1;
                ++tableIdx;
            }
            table = this.smallSubpagePools;
        }
        return table[tableIdx];
    }

    int normalizeCapacity(int reqCapacity) {
        if (reqCapacity < 0) {
            throw new IllegalArgumentException("capacity: " + reqCapacity + " (expected: 0+)");
        }
        if (reqCapacity >= this.chunkSize) {
            return this.directMemoryCacheAlignment == 0 ? reqCapacity : this.alignCapacity(reqCapacity);
        }
        if (!PoolArena.isTiny(reqCapacity)) {
            int normalizedCapacity = reqCapacity;
            --normalizedCapacity;
            normalizedCapacity |= normalizedCapacity >>> 1;
            normalizedCapacity |= normalizedCapacity >>> 2;
            normalizedCapacity |= normalizedCapacity >>> 4;
            normalizedCapacity |= normalizedCapacity >>> 8;
            normalizedCapacity |= normalizedCapacity >>> 16;
            if (++normalizedCapacity < 0) {
                normalizedCapacity >>>= 1;
            }
            assert (this.directMemoryCacheAlignment == 0 || (normalizedCapacity & this.directMemoryCacheAlignmentMask) == 0);
            return normalizedCapacity;
        }
        if (this.directMemoryCacheAlignment > 0) {
            return this.alignCapacity(reqCapacity);
        }
        if ((reqCapacity & 0xF) == 0) {
            return reqCapacity;
        }
        return (reqCapacity & 0xFFFFFFF0) + 16;
    }

    int alignCapacity(int reqCapacity) {
        int delta = reqCapacity & this.directMemoryCacheAlignmentMask;
        return delta == 0 ? reqCapacity : reqCapacity + this.directMemoryCacheAlignment - delta;
    }

    void reallocate(PooledByteBuf<T> buf2, int newCapacity, boolean freeOldMemory) {
        if (newCapacity < 0 || newCapacity > buf2.maxCapacity()) {
            throw new IllegalArgumentException("newCapacity: " + newCapacity);
        }
        int oldCapacity = buf2.length;
        if (oldCapacity == newCapacity) {
            return;
        }
        PoolChunk oldChunk = buf2.chunk;
        long oldHandle = buf2.handle;
        Object oldMemory = buf2.memory;
        int oldOffset = buf2.offset;
        int oldMaxLength = buf2.maxLength;
        int readerIndex = buf2.readerIndex();
        int writerIndex = buf2.writerIndex();
        this.allocate(this.parent.threadCache(), buf2, newCapacity);
        if (newCapacity > oldCapacity) {
            this.memoryCopy(oldMemory, oldOffset, buf2.memory, buf2.offset, oldCapacity);
        } else if (newCapacity < oldCapacity) {
            if (readerIndex < newCapacity) {
                if (writerIndex > newCapacity) {
                    writerIndex = newCapacity;
                }
                this.memoryCopy(oldMemory, oldOffset + readerIndex, buf2.memory, buf2.offset + readerIndex, writerIndex - readerIndex);
            } else {
                readerIndex = writerIndex = newCapacity;
            }
        }
        buf2.setIndex(readerIndex, writerIndex);
        if (freeOldMemory) {
            this.free(oldChunk, oldHandle, oldMaxLength, buf2.cache);
        }
    }

    @Override
    public int numThreadCaches() {
        return this.numThreadCaches.get();
    }

    @Override
    public int numTinySubpages() {
        return this.tinySubpagePools.length;
    }

    @Override
    public int numSmallSubpages() {
        return this.smallSubpagePools.length;
    }

    @Override
    public int numChunkLists() {
        return this.chunkListMetrics.size();
    }

    @Override
    public List<PoolSubpageMetric> tinySubpages() {
        return PoolArena.subPageMetricList(this.tinySubpagePools);
    }

    @Override
    public List<PoolSubpageMetric> smallSubpages() {
        return PoolArena.subPageMetricList(this.smallSubpagePools);
    }

    @Override
    public List<PoolChunkListMetric> chunkLists() {
        return this.chunkListMetrics;
    }

    private static List<PoolSubpageMetric> subPageMetricList(PoolSubpage<?>[] pages) {
        ArrayList<PoolSubpageMetric> metrics = new ArrayList<PoolSubpageMetric>();
        for (PoolSubpage<?> head : pages) {
            if (head.next == head) continue;
            PoolSubpage s2 = head.next;
            do {
                metrics.add(s2);
            } while ((s2 = s2.next) != head);
        }
        return metrics;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public long numAllocations() {
        long allocsNormal;
        PoolArena poolArena = this;
        synchronized (poolArena) {
            allocsNormal = this.allocationsNormal;
        }
        return this.allocationsTiny.value() + this.allocationsSmall.value() + allocsNormal + this.allocationsHuge.value();
    }

    @Override
    public long numTinyAllocations() {
        return this.allocationsTiny.value();
    }

    @Override
    public long numSmallAllocations() {
        return this.allocationsSmall.value();
    }

    @Override
    public synchronized long numNormalAllocations() {
        return this.allocationsNormal;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public long numDeallocations() {
        long deallocs;
        PoolArena poolArena = this;
        synchronized (poolArena) {
            deallocs = this.deallocationsTiny + this.deallocationsSmall + this.deallocationsNormal;
        }
        return deallocs + this.deallocationsHuge.value();
    }

    @Override
    public synchronized long numTinyDeallocations() {
        return this.deallocationsTiny;
    }

    @Override
    public synchronized long numSmallDeallocations() {
        return this.deallocationsSmall;
    }

    @Override
    public synchronized long numNormalDeallocations() {
        return this.deallocationsNormal;
    }

    @Override
    public long numHugeAllocations() {
        return this.allocationsHuge.value();
    }

    @Override
    public long numHugeDeallocations() {
        return this.deallocationsHuge.value();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public long numActiveAllocations() {
        long val = this.allocationsTiny.value() + this.allocationsSmall.value() + this.allocationsHuge.value() - this.deallocationsHuge.value();
        PoolArena poolArena = this;
        synchronized (poolArena) {
        }
        return Math.max(val += this.allocationsNormal - (this.deallocationsTiny + this.deallocationsSmall + this.deallocationsNormal), 0L);
    }

    @Override
    public long numActiveTinyAllocations() {
        return Math.max(this.numTinyAllocations() - this.numTinyDeallocations(), 0L);
    }

    @Override
    public long numActiveSmallAllocations() {
        return Math.max(this.numSmallAllocations() - this.numSmallDeallocations(), 0L);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public long numActiveNormalAllocations() {
        long val;
        PoolArena poolArena = this;
        synchronized (poolArena) {
            val = this.allocationsNormal - this.deallocationsNormal;
        }
        return Math.max(val, 0L);
    }

    @Override
    public long numActiveHugeAllocations() {
        return Math.max(this.numHugeAllocations() - this.numHugeDeallocations(), 0L);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public long numActiveBytes() {
        long val = this.activeBytesHuge.value();
        PoolArena poolArena = this;
        synchronized (poolArena) {
            for (int i2 = 0; i2 < this.chunkListMetrics.size(); ++i2) {
                for (PoolChunkMetric m2 : this.chunkListMetrics.get(i2)) {
                    val += (long)m2.chunkSize();
                }
            }
        }
        return Math.max(0L, val);
    }

    protected abstract PoolChunk<T> newChunk(int var1, int var2, int var3, int var4);

    protected abstract PoolChunk<T> newUnpooledChunk(int var1);

    protected abstract PooledByteBuf<T> newByteBuf(int var1);

    protected abstract void memoryCopy(T var1, int var2, T var3, int var4, int var5);

    protected abstract void destroyChunk(PoolChunk<T> var1);

    public synchronized String toString() {
        StringBuilder buf2 = new StringBuilder().append("Chunk(s) at 0~25%:").append(StringUtil.NEWLINE).append(this.qInit).append(StringUtil.NEWLINE).append("Chunk(s) at 0~50%:").append(StringUtil.NEWLINE).append(this.q000).append(StringUtil.NEWLINE).append("Chunk(s) at 25~75%:").append(StringUtil.NEWLINE).append(this.q025).append(StringUtil.NEWLINE).append("Chunk(s) at 50~100%:").append(StringUtil.NEWLINE).append(this.q050).append(StringUtil.NEWLINE).append("Chunk(s) at 75~100%:").append(StringUtil.NEWLINE).append(this.q075).append(StringUtil.NEWLINE).append("Chunk(s) at 100%:").append(StringUtil.NEWLINE).append(this.q100).append(StringUtil.NEWLINE).append("tiny subpages:");
        PoolArena.appendPoolSubPages(buf2, this.tinySubpagePools);
        buf2.append(StringUtil.NEWLINE).append("small subpages:");
        PoolArena.appendPoolSubPages(buf2, this.smallSubpagePools);
        buf2.append(StringUtil.NEWLINE);
        return buf2.toString();
    }

    private static void appendPoolSubPages(StringBuilder buf2, PoolSubpage<?>[] subpages) {
        for (int i2 = 0; i2 < subpages.length; ++i2) {
            PoolSubpage<?> head = subpages[i2];
            if (head.next == head) continue;
            buf2.append(StringUtil.NEWLINE).append(i2).append(": ");
            PoolSubpage s2 = head.next;
            do {
                buf2.append(s2);
            } while ((s2 = s2.next) != head);
        }
    }

    protected final void finalize() throws Throwable {
        try {
            super.finalize();
        }
        catch (Throwable throwable) {
            PoolArena.destroyPoolSubPages(this.smallSubpagePools);
            PoolArena.destroyPoolSubPages(this.tinySubpagePools);
            this.destroyPoolChunkLists(this.qInit, this.q000, this.q025, this.q050, this.q075, this.q100);
            throw throwable;
        }
        PoolArena.destroyPoolSubPages(this.smallSubpagePools);
        PoolArena.destroyPoolSubPages(this.tinySubpagePools);
        this.destroyPoolChunkLists(this.qInit, this.q000, this.q025, this.q050, this.q075, this.q100);
    }

    private static void destroyPoolSubPages(PoolSubpage<?>[] pages) {
        for (PoolSubpage<?> page : pages) {
            page.destroy();
        }
    }

    private void destroyPoolChunkLists(PoolChunkList<T> ... chunkLists) {
        for (PoolChunkList<T> chunkList : chunkLists) {
            chunkList.destroy(this);
        }
    }

    static final class DirectArena
    extends PoolArena<ByteBuffer> {
        DirectArena(PooledByteBufAllocator parent, int pageSize, int maxOrder, int pageShifts, int chunkSize, int directMemoryCacheAlignment) {
            super(parent, pageSize, maxOrder, pageShifts, chunkSize, directMemoryCacheAlignment);
        }

        @Override
        boolean isDirect() {
            return true;
        }

        private int offsetCacheLine(ByteBuffer memory) {
            return HAS_UNSAFE ? (int)(PlatformDependent.directBufferAddress(memory) & (long)this.directMemoryCacheAlignmentMask) : 0;
        }

        @Override
        protected PoolChunk<ByteBuffer> newChunk(int pageSize, int maxOrder, int pageShifts, int chunkSize) {
            if (this.directMemoryCacheAlignment == 0) {
                return new PoolChunk<ByteBuffer>(this, DirectArena.allocateDirect(chunkSize), pageSize, maxOrder, pageShifts, chunkSize, 0);
            }
            ByteBuffer memory = DirectArena.allocateDirect(chunkSize + this.directMemoryCacheAlignment);
            return new PoolChunk<ByteBuffer>(this, memory, pageSize, maxOrder, pageShifts, chunkSize, this.offsetCacheLine(memory));
        }

        @Override
        protected PoolChunk<ByteBuffer> newUnpooledChunk(int capacity) {
            if (this.directMemoryCacheAlignment == 0) {
                return new PoolChunk<ByteBuffer>(this, DirectArena.allocateDirect(capacity), capacity, 0);
            }
            ByteBuffer memory = DirectArena.allocateDirect(capacity + this.directMemoryCacheAlignment);
            return new PoolChunk<ByteBuffer>(this, memory, capacity, this.offsetCacheLine(memory));
        }

        private static ByteBuffer allocateDirect(int capacity) {
            return PlatformDependent.useDirectBufferNoCleaner() ? PlatformDependent.allocateDirectNoCleaner(capacity) : ByteBuffer.allocateDirect(capacity);
        }

        @Override
        protected void destroyChunk(PoolChunk<ByteBuffer> chunk) {
            if (PlatformDependent.useDirectBufferNoCleaner()) {
                PlatformDependent.freeDirectNoCleaner((ByteBuffer)chunk.memory);
            } else {
                PlatformDependent.freeDirectBuffer((ByteBuffer)chunk.memory);
            }
        }

        @Override
        protected PooledByteBuf<ByteBuffer> newByteBuf(int maxCapacity) {
            if (HAS_UNSAFE) {
                return PooledUnsafeDirectByteBuf.newInstance(maxCapacity);
            }
            return PooledDirectByteBuf.newInstance(maxCapacity);
        }

        @Override
        protected void memoryCopy(ByteBuffer src, int srcOffset, ByteBuffer dst, int dstOffset, int length) {
            if (length == 0) {
                return;
            }
            if (HAS_UNSAFE) {
                PlatformDependent.copyMemory(PlatformDependent.directBufferAddress(src) + (long)srcOffset, PlatformDependent.directBufferAddress(dst) + (long)dstOffset, length);
            } else {
                src = src.duplicate();
                dst = dst.duplicate();
                src.position(srcOffset).limit(srcOffset + length);
                dst.position(dstOffset);
                dst.put(src);
            }
        }
    }

    static final class HeapArena
    extends PoolArena<byte[]> {
        HeapArena(PooledByteBufAllocator parent, int pageSize, int maxOrder, int pageShifts, int chunkSize, int directMemoryCacheAlignment) {
            super(parent, pageSize, maxOrder, pageShifts, chunkSize, directMemoryCacheAlignment);
        }

        @Override
        boolean isDirect() {
            return false;
        }

        @Override
        protected PoolChunk<byte[]> newChunk(int pageSize, int maxOrder, int pageShifts, int chunkSize) {
            return new PoolChunk<byte[]>(this, new byte[chunkSize], pageSize, maxOrder, pageShifts, chunkSize, 0);
        }

        @Override
        protected PoolChunk<byte[]> newUnpooledChunk(int capacity) {
            return new PoolChunk<byte[]>(this, new byte[capacity], capacity, 0);
        }

        @Override
        protected void destroyChunk(PoolChunk<byte[]> chunk) {
        }

        @Override
        protected PooledByteBuf<byte[]> newByteBuf(int maxCapacity) {
            return HAS_UNSAFE ? PooledUnsafeHeapByteBuf.newUnsafeInstance(maxCapacity) : PooledHeapByteBuf.newInstance(maxCapacity);
        }

        @Override
        protected void memoryCopy(byte[] src, int srcOffset, byte[] dst, int dstOffset, int length) {
            if (length == 0) {
                return;
            }
            System.arraycopy(src, srcOffset, dst, dstOffset, length);
        }
    }

    static enum SizeClass {
        Tiny,
        Small,
        Normal;

    }
}

