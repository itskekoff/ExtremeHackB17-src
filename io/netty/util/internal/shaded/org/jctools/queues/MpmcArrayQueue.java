package io.netty.util.internal.shaded.org.jctools.queues;

import io.netty.util.internal.shaded.org.jctools.queues.MessagePassingQueue;
import io.netty.util.internal.shaded.org.jctools.queues.MpmcArrayQueueConsumerField;
import io.netty.util.internal.shaded.org.jctools.queues.QueueProgressIndicators;
import io.netty.util.internal.shaded.org.jctools.util.JvmInfo;
import io.netty.util.internal.shaded.org.jctools.util.UnsafeRefArrayAccess;

public class MpmcArrayQueue<E>
extends MpmcArrayQueueConsumerField<E>
implements QueueProgressIndicators {
    long p01;
    long p02;
    long p03;
    long p04;
    long p05;
    long p06;
    long p07;
    long p10;
    long p11;
    long p12;
    long p13;
    long p14;
    long p15;
    long p16;
    long p17;
    static final int RECOMENDED_POLL_BATCH = JvmInfo.CPUs * 4;
    static final int RECOMENDED_OFFER_BATCH = JvmInfo.CPUs * 4;

    public MpmcArrayQueue(int capacity) {
        super(MpmcArrayQueue.validateCapacity(capacity));
    }

    private static int validateCapacity(int capacity) {
        if (capacity < 2) {
            throw new IllegalArgumentException("Minimum size is 2");
        }
        return capacity;
    }

    @Override
    public boolean offer(E e2) {
        long seqOffset;
        long pIndex;
        long seq;
        if (null == e2) {
            throw new NullPointerException();
        }
        long mask = this.mask;
        long capacity = mask + 1L;
        long[] sBuffer = this.sequenceBuffer;
        long cIndex = Long.MAX_VALUE;
        do {
            if ((seq = this.lvSequence(sBuffer, seqOffset = MpmcArrayQueue.calcSequenceOffset(pIndex = this.lvProducerIndex(), mask))) >= pIndex) continue;
            if (pIndex - capacity <= cIndex && pIndex - capacity <= (cIndex = this.lvConsumerIndex())) {
                return false;
            }
            seq = pIndex + 1L;
        } while (seq > pIndex || !this.casProducerIndex(pIndex, pIndex + 1L));
        assert (null == UnsafeRefArrayAccess.lpElement(this.buffer, MpmcArrayQueue.calcElementOffset(pIndex, mask)));
        UnsafeRefArrayAccess.soElement(this.buffer, MpmcArrayQueue.calcElementOffset(pIndex, mask), e2);
        this.soSequence(sBuffer, seqOffset, pIndex + 1L);
        return true;
    }

    @Override
    public E poll() {
        long seqOffset;
        long cIndex;
        long expectedSeq;
        long seq;
        long[] sBuffer = this.sequenceBuffer;
        long mask = this.mask;
        long pIndex = -1L;
        do {
            if ((seq = this.lvSequence(sBuffer, seqOffset = MpmcArrayQueue.calcSequenceOffset(cIndex = this.lvConsumerIndex(), mask))) >= (expectedSeq = cIndex + 1L)) continue;
            if (cIndex >= pIndex && cIndex == (pIndex = this.lvProducerIndex())) {
                return null;
            }
            seq = expectedSeq + 1L;
        } while (seq > expectedSeq || !this.casConsumerIndex(cIndex, cIndex + 1L));
        long offset = MpmcArrayQueue.calcElementOffset(cIndex, mask);
        Object e2 = UnsafeRefArrayAccess.lpElement(this.buffer, offset);
        assert (e2 != null);
        UnsafeRefArrayAccess.soElement(this.buffer, offset, null);
        this.soSequence(sBuffer, seqOffset, cIndex + mask + 1L);
        return (E)e2;
    }

    @Override
    public E peek() {
        long cIndex;
        Object e2;
        while ((e2 = UnsafeRefArrayAccess.lpElement(this.buffer, this.calcElementOffset(cIndex = this.lvConsumerIndex()))) == null && cIndex != this.lvProducerIndex()) {
        }
        return (E)e2;
    }

    @Override
    public boolean relaxedOffer(E e2) {
        long seqOffset;
        long pIndex;
        long seq;
        if (null == e2) {
            throw new NullPointerException();
        }
        long mask = this.mask;
        long[] sBuffer = this.sequenceBuffer;
        do {
            if ((seq = this.lvSequence(sBuffer, seqOffset = MpmcArrayQueue.calcSequenceOffset(pIndex = this.lvProducerIndex(), mask))) >= pIndex) continue;
            return false;
        } while (seq > pIndex || !this.casProducerIndex(pIndex, pIndex + 1L));
        UnsafeRefArrayAccess.soElement(this.buffer, MpmcArrayQueue.calcElementOffset(pIndex, mask), e2);
        this.soSequence(sBuffer, seqOffset, pIndex + 1L);
        return true;
    }

    @Override
    public E relaxedPoll() {
        long seqOffset;
        long cIndex;
        long expectedSeq;
        long seq;
        long[] sBuffer = this.sequenceBuffer;
        long mask = this.mask;
        do {
            if ((seq = this.lvSequence(sBuffer, seqOffset = MpmcArrayQueue.calcSequenceOffset(cIndex = this.lvConsumerIndex(), mask))) >= (expectedSeq = cIndex + 1L)) continue;
            return null;
        } while (seq > expectedSeq || !this.casConsumerIndex(cIndex, cIndex + 1L));
        long offset = MpmcArrayQueue.calcElementOffset(cIndex, mask);
        Object e2 = UnsafeRefArrayAccess.lpElement(this.buffer, offset);
        UnsafeRefArrayAccess.soElement(this.buffer, offset, null);
        this.soSequence(sBuffer, seqOffset, cIndex + mask + 1L);
        return (E)e2;
    }

    @Override
    public E relaxedPeek() {
        long currConsumerIndex = this.lvConsumerIndex();
        return (E)UnsafeRefArrayAccess.lpElement(this.buffer, this.calcElementOffset(currConsumerIndex));
    }

    @Override
    public int drain(MessagePassingQueue.Consumer<E> c2) {
        int sum;
        int drained;
        int capacity = this.capacity();
        for (sum = 0; sum < capacity; sum += drained) {
            drained = 0;
            drained = this.drain(c2, RECOMENDED_POLL_BATCH);
            if (drained == 0) break;
        }
        return sum;
    }

    @Override
    public int fill(MessagePassingQueue.Supplier<E> s2) {
        int filled;
        long result = 0L;
        int capacity = this.capacity();
        do {
            if ((filled = this.fill(s2, RECOMENDED_OFFER_BATCH)) != 0) continue;
            return (int)result;
        } while ((result += (long)filled) <= (long)capacity);
        return (int)result;
    }

    @Override
    public int drain(MessagePassingQueue.Consumer<E> c2, int limit) {
        long[] sBuffer = this.sequenceBuffer;
        long mask = this.mask;
        Object[] buffer = this.buffer;
        for (int i2 = 0; i2 < limit; ++i2) {
            long seqOffset;
            long cIndex;
            long expectedSeq;
            long seq;
            do {
                if ((seq = this.lvSequence(sBuffer, seqOffset = MpmcArrayQueue.calcSequenceOffset(cIndex = this.lvConsumerIndex(), mask))) >= (expectedSeq = cIndex + 1L)) continue;
                return i2;
            } while (seq > expectedSeq || !this.casConsumerIndex(cIndex, cIndex + 1L));
            long offset = MpmcArrayQueue.calcElementOffset(cIndex, mask);
            Object e2 = UnsafeRefArrayAccess.lpElement(buffer, offset);
            UnsafeRefArrayAccess.soElement(buffer, offset, null);
            this.soSequence(sBuffer, seqOffset, cIndex + mask + 1L);
            c2.accept(e2);
        }
        return limit;
    }

    @Override
    public int fill(MessagePassingQueue.Supplier<E> s2, int limit) {
        long[] sBuffer = this.sequenceBuffer;
        long mask = this.mask;
        Object[] buffer = this.buffer;
        for (int i2 = 0; i2 < limit; ++i2) {
            long seqOffset;
            long pIndex;
            long seq;
            do {
                if ((seq = this.lvSequence(sBuffer, seqOffset = MpmcArrayQueue.calcSequenceOffset(pIndex = this.lvProducerIndex(), mask))) >= pIndex) continue;
                return i2;
            } while (seq > pIndex || !this.casProducerIndex(pIndex, pIndex + 1L));
            UnsafeRefArrayAccess.soElement(buffer, MpmcArrayQueue.calcElementOffset(pIndex, mask), s2.get());
            this.soSequence(sBuffer, seqOffset, pIndex + 1L);
        }
        return limit;
    }

    @Override
    public void drain(MessagePassingQueue.Consumer<E> c2, MessagePassingQueue.WaitStrategy w2, MessagePassingQueue.ExitCondition exit) {
        int idleCounter = 0;
        while (exit.keepRunning()) {
            if (this.drain(c2, RECOMENDED_POLL_BATCH) == 0) {
                idleCounter = w2.idle(idleCounter);
                continue;
            }
            idleCounter = 0;
        }
    }

    @Override
    public void fill(MessagePassingQueue.Supplier<E> s2, MessagePassingQueue.WaitStrategy w2, MessagePassingQueue.ExitCondition exit) {
        int idleCounter = 0;
        while (exit.keepRunning()) {
            if (this.fill(s2, RECOMENDED_OFFER_BATCH) == 0) {
                idleCounter = w2.idle(idleCounter);
                continue;
            }
            idleCounter = 0;
        }
    }
}

