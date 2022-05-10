package io.netty.util.internal.shaded.org.jctools.queues.atomic;

import io.netty.util.internal.shaded.org.jctools.queues.QueueProgressIndicators;
import io.netty.util.internal.shaded.org.jctools.queues.atomic.AtomicReferenceArrayQueue;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReferenceArray;

public final class MpscAtomicArrayQueue<E>
extends AtomicReferenceArrayQueue<E>
implements QueueProgressIndicators {
    private final AtomicLong consumerIndex = new AtomicLong();
    private final AtomicLong producerIndex = new AtomicLong();
    private volatile long headCache;

    public MpscAtomicArrayQueue(int capacity) {
        super(capacity);
    }

    @Override
    public boolean offer(E e2) {
        long currentProducerIndex;
        if (null == e2) {
            throw new NullPointerException();
        }
        int mask = this.mask;
        long capacity = mask + 1;
        long consumerIndexCache = this.lvConsumerIndexCache();
        do {
            long wrapPoint;
            if (consumerIndexCache > (wrapPoint = (currentProducerIndex = this.lvProducerIndex()) - capacity)) continue;
            long currHead = this.lvConsumerIndex();
            if (currHead <= wrapPoint) {
                return false;
            }
            this.svConsumerIndexCache(currHead);
            consumerIndexCache = currHead;
        } while (!this.casProducerIndex(currentProducerIndex, currentProducerIndex + 1L));
        int offset = this.calcElementOffset(currentProducerIndex, mask);
        this.soElement(offset, e2);
        return true;
    }

    public final int weakOffer(E e2) {
        long wrapPoint;
        if (null == e2) {
            throw new NullPointerException("Null is not a valid element");
        }
        int mask = this.mask;
        long capacity = mask + 1;
        long currentTail = this.lvProducerIndex();
        long consumerIndexCache = this.lvConsumerIndexCache();
        if (consumerIndexCache <= (wrapPoint = currentTail - capacity)) {
            long currHead = this.lvConsumerIndex();
            if (currHead <= wrapPoint) {
                return 1;
            }
            this.svConsumerIndexCache(currHead);
        }
        if (!this.casProducerIndex(currentTail, currentTail + 1L)) {
            return -1;
        }
        int offset = this.calcElementOffset(currentTail, mask);
        this.soElement(offset, e2);
        return 0;
    }

    @Override
    public E poll() {
        AtomicReferenceArray buffer = this.buffer;
        long consumerIndex = this.lvConsumerIndex();
        int offset = this.calcElementOffset(consumerIndex);
        Object e2 = this.lvElement(buffer, offset);
        if (null == e2) {
            if (consumerIndex != this.lvProducerIndex()) {
                while ((e2 = this.lvElement(buffer, offset)) == null) {
                }
            } else {
                return null;
            }
        }
        this.spElement(buffer, offset, null);
        this.soConsumerIndex(consumerIndex + 1L);
        return e2;
    }

    @Override
    public E peek() {
        AtomicReferenceArray buffer = this.buffer;
        long consumerIndex = this.lvConsumerIndex();
        int offset = this.calcElementOffset(consumerIndex);
        Object e2 = this.lvElement(buffer, offset);
        if (null == e2) {
            if (consumerIndex != this.lvProducerIndex()) {
                while ((e2 = this.lvElement(buffer, offset)) == null) {
                }
            } else {
                return null;
            }
        }
        return e2;
    }

    @Override
    public int size() {
        long currentProducerIndex;
        long before;
        long after = this.lvConsumerIndex();
        do {
            before = after;
            currentProducerIndex = this.lvProducerIndex();
        } while (before != (after = this.lvConsumerIndex()));
        return (int)(currentProducerIndex - after);
    }

    @Override
    public boolean isEmpty() {
        return this.lvConsumerIndex() == this.lvProducerIndex();
    }

    @Override
    public long currentProducerIndex() {
        return this.lvProducerIndex();
    }

    @Override
    public long currentConsumerIndex() {
        return this.lvConsumerIndex();
    }

    private long lvConsumerIndex() {
        return this.consumerIndex.get();
    }

    private long lvProducerIndex() {
        return this.producerIndex.get();
    }

    protected final long lvConsumerIndexCache() {
        return this.headCache;
    }

    protected final void svConsumerIndexCache(long v2) {
        this.headCache = v2;
    }

    protected final boolean casProducerIndex(long expect, long newValue) {
        return this.producerIndex.compareAndSet(expect, newValue);
    }

    protected void soConsumerIndex(long l2) {
        this.consumerIndex.lazySet(l2);
    }
}

