package io.netty.util.internal.shaded.org.jctools.queues;

import io.netty.util.internal.shaded.org.jctools.queues.BaseMpscLinkedArrayQueueColdProducerFields;
import io.netty.util.internal.shaded.org.jctools.queues.BaseMpscLinkedArrayQueueConsumerFields;
import io.netty.util.internal.shaded.org.jctools.queues.BaseMpscLinkedArrayQueueProducerFields;
import io.netty.util.internal.shaded.org.jctools.queues.CircularArrayOffsetCalculator;
import io.netty.util.internal.shaded.org.jctools.queues.MessagePassingQueue;
import io.netty.util.internal.shaded.org.jctools.queues.MpmcArrayQueue;
import io.netty.util.internal.shaded.org.jctools.queues.QueueProgressIndicators;
import io.netty.util.internal.shaded.org.jctools.util.Pow2;
import io.netty.util.internal.shaded.org.jctools.util.UnsafeAccess;
import io.netty.util.internal.shaded.org.jctools.util.UnsafeRefArrayAccess;
import java.lang.reflect.Field;
import java.util.Iterator;

public abstract class BaseMpscLinkedArrayQueue<E>
extends BaseMpscLinkedArrayQueueColdProducerFields<E>
implements MessagePassingQueue<E>,
QueueProgressIndicators {
    private static final long P_INDEX_OFFSET;
    private static final long C_INDEX_OFFSET;
    private static final long P_LIMIT_OFFSET;
    private static final Object JUMP;

    public BaseMpscLinkedArrayQueue(int initialCapacity) {
        if (initialCapacity < 2) {
            throw new IllegalArgumentException("Initial capacity must be 2 or more");
        }
        int p2capacity = Pow2.roundToPowerOfTwo(initialCapacity);
        long mask = p2capacity - 1 << 1;
        E[] buffer = CircularArrayOffsetCalculator.allocate(p2capacity + 1);
        this.producerBuffer = buffer;
        this.producerMask = mask;
        this.consumerBuffer = buffer;
        this.consumerMask = mask;
        this.soProducerLimit(mask);
    }

    @Override
    public final Iterator<E> iterator() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String toString() {
        return this.getClass().getName();
    }

    @Override
    public boolean offer(E e2) {
        Object[] buffer;
        long mask;
        long pIndex;
        if (null == e2) {
            throw new NullPointerException();
        }
        block6: while (true) {
            long producerLimit = this.lvProducerLimit();
            pIndex = this.lvProducerIndex();
            if ((pIndex & 1L) == 1L) continue;
            mask = this.producerMask;
            buffer = this.producerBuffer;
            if (producerLimit <= pIndex) {
                int result = this.offerSlowPath(mask, pIndex, producerLimit);
                switch (result) {
                    case 0: {
                        break;
                    }
                    case 1: {
                        continue block6;
                    }
                    case 2: {
                        return false;
                    }
                    case 3: {
                        this.resize(mask, buffer, pIndex, e2);
                        return true;
                    }
                }
            }
            if (this.casProducerIndex(pIndex, pIndex + 2L)) break;
        }
        long offset = BaseMpscLinkedArrayQueue.modifiedCalcElementOffset(pIndex, mask);
        UnsafeRefArrayAccess.soElement(buffer, offset, e2);
        return true;
    }

    private int offerSlowPath(long mask, long pIndex, long producerLimit) {
        long cIndex = this.lvConsumerIndex();
        long bufferCapacity = this.getCurrentBufferCapacity(mask);
        int result = 0;
        if (cIndex + bufferCapacity > pIndex) {
            if (!this.casProducerLimit(producerLimit, cIndex + bufferCapacity)) {
                result = 1;
            }
        } else {
            result = this.availableInQueue(pIndex, cIndex) <= 0L ? 2 : (this.casProducerIndex(pIndex, pIndex + 1L) ? 3 : 1);
        }
        return result;
    }

    protected abstract long availableInQueue(long var1, long var3);

    private static long modifiedCalcElementOffset(long index, long mask) {
        return UnsafeRefArrayAccess.REF_ARRAY_BASE + ((index & mask) << UnsafeRefArrayAccess.REF_ELEMENT_SHIFT - 1);
    }

    @Override
    public E poll() {
        Object[] buffer = this.consumerBuffer;
        long index = this.consumerIndex;
        long mask = this.consumerMask;
        long offset = BaseMpscLinkedArrayQueue.modifiedCalcElementOffset(index, mask);
        Object e2 = UnsafeRefArrayAccess.lvElement(buffer, offset);
        if (e2 == null) {
            if (index != this.lvProducerIndex()) {
                while ((e2 = UnsafeRefArrayAccess.lvElement(buffer, offset)) == null) {
                }
            } else {
                return null;
            }
        }
        if (e2 == JUMP) {
            Object[] nextBuffer = this.getNextBuffer(buffer, mask);
            return (E)this.newBufferPoll(nextBuffer, index);
        }
        UnsafeRefArrayAccess.soElement(buffer, offset, null);
        this.soConsumerIndex(index + 2L);
        return (E)e2;
    }

    @Override
    public E peek() {
        Object[] buffer = this.consumerBuffer;
        long index = this.consumerIndex;
        long mask = this.consumerMask;
        long offset = BaseMpscLinkedArrayQueue.modifiedCalcElementOffset(index, mask);
        Object e2 = UnsafeRefArrayAccess.lvElement(buffer, offset);
        if (e2 == null && index != this.lvProducerIndex()) {
            while ((e2 = UnsafeRefArrayAccess.lvElement(buffer, offset)) == null) {
            }
        }
        if (e2 == JUMP) {
            return (E)this.newBufferPeek(this.getNextBuffer(buffer, mask), index);
        }
        return (E)e2;
    }

    private E[] getNextBuffer(E[] buffer, long mask) {
        long nextArrayOffset = this.nextArrayOffset(mask);
        Object[] nextBuffer = (Object[])UnsafeRefArrayAccess.lvElement(buffer, nextArrayOffset);
        UnsafeRefArrayAccess.soElement(buffer, nextArrayOffset, null);
        return nextBuffer;
    }

    private long nextArrayOffset(long mask) {
        return BaseMpscLinkedArrayQueue.modifiedCalcElementOffset(mask + 2L, Long.MAX_VALUE);
    }

    private E newBufferPoll(E[] nextBuffer, long index) {
        long offsetInNew = this.newBufferAndOffset(nextBuffer, index);
        E n2 = UnsafeRefArrayAccess.lvElement(nextBuffer, offsetInNew);
        if (n2 == null) {
            throw new IllegalStateException("new buffer must have at least one element");
        }
        UnsafeRefArrayAccess.soElement(nextBuffer, offsetInNew, null);
        this.soConsumerIndex(index + 2L);
        return n2;
    }

    private E newBufferPeek(E[] nextBuffer, long index) {
        long offsetInNew = this.newBufferAndOffset(nextBuffer, index);
        E n2 = UnsafeRefArrayAccess.lvElement(nextBuffer, offsetInNew);
        if (null == n2) {
            throw new IllegalStateException("new buffer must have at least one element");
        }
        return n2;
    }

    private long newBufferAndOffset(E[] nextBuffer, long index) {
        this.consumerBuffer = nextBuffer;
        this.consumerMask = nextBuffer.length - 2 << 1;
        long offsetInNew = BaseMpscLinkedArrayQueue.modifiedCalcElementOffset(index, this.consumerMask);
        return offsetInNew;
    }

    @Override
    public final int size() {
        long currentProducerIndex;
        long before;
        long after = this.lvConsumerIndex();
        do {
            before = after;
            currentProducerIndex = this.lvProducerIndex();
        } while (before != (after = this.lvConsumerIndex()));
        long size = currentProducerIndex - after >> 1;
        if (size > Integer.MAX_VALUE) {
            return Integer.MAX_VALUE;
        }
        return (int)size;
    }

    @Override
    public final boolean isEmpty() {
        return this.lvConsumerIndex() == this.lvProducerIndex();
    }

    private long lvProducerIndex() {
        return UnsafeAccess.UNSAFE.getLongVolatile(this, P_INDEX_OFFSET);
    }

    private long lvConsumerIndex() {
        return UnsafeAccess.UNSAFE.getLongVolatile(this, C_INDEX_OFFSET);
    }

    private void soProducerIndex(long v2) {
        UnsafeAccess.UNSAFE.putOrderedLong(this, P_INDEX_OFFSET, v2);
    }

    private boolean casProducerIndex(long expect, long newValue) {
        return UnsafeAccess.UNSAFE.compareAndSwapLong(this, P_INDEX_OFFSET, expect, newValue);
    }

    private void soConsumerIndex(long v2) {
        UnsafeAccess.UNSAFE.putOrderedLong(this, C_INDEX_OFFSET, v2);
    }

    private long lvProducerLimit() {
        return this.producerLimit;
    }

    private boolean casProducerLimit(long expect, long newValue) {
        return UnsafeAccess.UNSAFE.compareAndSwapLong(this, P_LIMIT_OFFSET, expect, newValue);
    }

    private void soProducerLimit(long v2) {
        UnsafeAccess.UNSAFE.putOrderedLong(this, P_LIMIT_OFFSET, v2);
    }

    @Override
    public long currentProducerIndex() {
        return this.lvProducerIndex() / 2L;
    }

    @Override
    public long currentConsumerIndex() {
        return this.lvConsumerIndex() / 2L;
    }

    @Override
    public abstract int capacity();

    @Override
    public boolean relaxedOffer(E e2) {
        return this.offer(e2);
    }

    @Override
    public E relaxedPoll() {
        Object[] buffer = this.consumerBuffer;
        long index = this.consumerIndex;
        long mask = this.consumerMask;
        long offset = BaseMpscLinkedArrayQueue.modifiedCalcElementOffset(index, mask);
        Object e2 = UnsafeRefArrayAccess.lvElement(buffer, offset);
        if (e2 == null) {
            return null;
        }
        if (e2 == JUMP) {
            Object[] nextBuffer = this.getNextBuffer(buffer, mask);
            return (E)this.newBufferPoll(nextBuffer, index);
        }
        UnsafeRefArrayAccess.soElement(buffer, offset, null);
        this.soConsumerIndex(index + 2L);
        return (E)e2;
    }

    @Override
    public E relaxedPeek() {
        Object[] buffer = this.consumerBuffer;
        long index = this.consumerIndex;
        long mask = this.consumerMask;
        long offset = BaseMpscLinkedArrayQueue.modifiedCalcElementOffset(index, mask);
        Object e2 = UnsafeRefArrayAccess.lvElement(buffer, offset);
        if (e2 == JUMP) {
            return (E)this.newBufferPeek(this.getNextBuffer(buffer, mask), index);
        }
        return (E)e2;
    }

    @Override
    public int fill(MessagePassingQueue.Supplier<E> s2, int batchSize) {
        long batchIndex;
        Object[] buffer;
        long mask;
        long pIndex;
        block5: while (true) {
            long producerLimit = this.lvProducerLimit();
            pIndex = this.lvProducerIndex();
            if ((pIndex & 1L) == 1L) continue;
            mask = this.producerMask;
            buffer = this.producerBuffer;
            batchIndex = Math.min(producerLimit, pIndex + (long)(2 * batchSize));
            if (pIndex == producerLimit || producerLimit < batchIndex) {
                int result = this.offerSlowPath(mask, pIndex, producerLimit);
                switch (result) {
                    case 1: {
                        continue block5;
                    }
                    case 2: {
                        return 0;
                    }
                    case 3: {
                        this.resize(mask, buffer, pIndex, s2.get());
                        return 1;
                    }
                }
            }
            if (this.casProducerIndex(pIndex, batchIndex)) break;
        }
        int claimedSlots = (int)((batchIndex - pIndex) / 2L);
        int i2 = 0;
        for (i2 = 0; i2 < claimedSlots; ++i2) {
            long offset = BaseMpscLinkedArrayQueue.modifiedCalcElementOffset(pIndex + (long)(2 * i2), mask);
            UnsafeRefArrayAccess.soElement(buffer, offset, s2.get());
        }
        return claimedSlots;
    }

    private void resize(long oldMask, E[] oldBuffer, long pIndex, E e2) {
        int newBufferLength = this.getNextBufferSize(oldBuffer);
        E[] newBuffer = CircularArrayOffsetCalculator.allocate(newBufferLength);
        this.producerBuffer = newBuffer;
        int newMask = newBufferLength - 2 << 1;
        this.producerMask = newMask;
        long offsetInOld = BaseMpscLinkedArrayQueue.modifiedCalcElementOffset(pIndex, oldMask);
        long offsetInNew = BaseMpscLinkedArrayQueue.modifiedCalcElementOffset(pIndex, newMask);
        UnsafeRefArrayAccess.soElement(newBuffer, offsetInNew, e2);
        UnsafeRefArrayAccess.soElement(oldBuffer, this.nextArrayOffset(oldMask), newBuffer);
        long cIndex = this.lvConsumerIndex();
        long availableInQueue = this.availableInQueue(pIndex, cIndex);
        if (availableInQueue <= 0L) {
            throw new IllegalStateException();
        }
        this.soProducerLimit(pIndex + Math.min((long)newMask, availableInQueue));
        this.soProducerIndex(pIndex + 2L);
        UnsafeRefArrayAccess.soElement(oldBuffer, offsetInOld, JUMP);
    }

    protected abstract int getNextBufferSize(E[] var1);

    protected abstract long getCurrentBufferCapacity(long var1);

    @Override
    public int fill(MessagePassingQueue.Supplier<E> s2) {
        int filled;
        long result = 0L;
        int capacity = this.capacity();
        do {
            if ((filled = this.fill(s2, MpmcArrayQueue.RECOMENDED_OFFER_BATCH)) != 0) continue;
            return (int)result;
        } while ((result += (long)filled) <= (long)capacity);
        return (int)result;
    }

    @Override
    public void fill(MessagePassingQueue.Supplier<E> s2, MessagePassingQueue.WaitStrategy w2, MessagePassingQueue.ExitCondition exit) {
        while (exit.keepRunning()) {
            while (this.fill(s2, MpmcArrayQueue.RECOMENDED_OFFER_BATCH) != 0 && exit.keepRunning()) {
            }
            int idleCounter = 0;
            while (exit.keepRunning() && this.fill(s2, MpmcArrayQueue.RECOMENDED_OFFER_BATCH) == 0) {
                idleCounter = w2.idle(idleCounter);
            }
        }
    }

    @Override
    public void drain(MessagePassingQueue.Consumer<E> c2, MessagePassingQueue.WaitStrategy w2, MessagePassingQueue.ExitCondition exit) {
        int idleCounter = 0;
        while (exit.keepRunning()) {
            E e2 = this.relaxedPoll();
            if (e2 == null) {
                idleCounter = w2.idle(idleCounter);
                continue;
            }
            idleCounter = 0;
            c2.accept(e2);
        }
    }

    @Override
    public int drain(MessagePassingQueue.Consumer<E> c2) {
        return this.drain(c2, this.capacity());
    }

    @Override
    public int drain(MessagePassingQueue.Consumer<E> c2, int limit) {
        E m2;
        int i2;
        for (i2 = 0; i2 < limit && (m2 = this.relaxedPoll()) != null; ++i2) {
            c2.accept(m2);
        }
        return i2;
    }

    static {
        Field iField;
        try {
            iField = BaseMpscLinkedArrayQueueProducerFields.class.getDeclaredField("producerIndex");
            P_INDEX_OFFSET = UnsafeAccess.UNSAFE.objectFieldOffset(iField);
        }
        catch (NoSuchFieldException e2) {
            throw new RuntimeException(e2);
        }
        try {
            iField = BaseMpscLinkedArrayQueueConsumerFields.class.getDeclaredField("consumerIndex");
            C_INDEX_OFFSET = UnsafeAccess.UNSAFE.objectFieldOffset(iField);
        }
        catch (NoSuchFieldException e3) {
            throw new RuntimeException(e3);
        }
        try {
            iField = BaseMpscLinkedArrayQueueColdProducerFields.class.getDeclaredField("producerLimit");
            P_LIMIT_OFFSET = UnsafeAccess.UNSAFE.objectFieldOffset(iField);
        }
        catch (NoSuchFieldException e4) {
            throw new RuntimeException(e4);
        }
        JUMP = new Object();
    }
}

