package io.netty.util.internal.shaded.org.jctools.queues;

import io.netty.util.internal.shaded.org.jctools.queues.MessagePassingQueue;
import io.netty.util.internal.shaded.org.jctools.queues.MpmcArrayQueue;
import io.netty.util.internal.shaded.org.jctools.queues.MpscArrayQueueConsumerField;
import io.netty.util.internal.shaded.org.jctools.queues.QueueProgressIndicators;
import io.netty.util.internal.shaded.org.jctools.util.UnsafeRefArrayAccess;

public class MpscArrayQueue<E>
extends MpscArrayQueueConsumerField<E>
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

    public MpscArrayQueue(int capacity) {
        super(capacity);
    }

    public boolean offerIfBelowThreshold(E e2, int threshold) {
        long pIndex;
        if (null == e2) {
            throw new NullPointerException();
        }
        long mask = this.mask;
        long capacity = mask + 1L;
        long producerLimit = this.lvProducerLimit();
        do {
            long available;
            long size;
            if ((size = capacity - (available = producerLimit - (pIndex = this.lvProducerIndex()))) < (long)threshold) continue;
            long cIndex = this.lvConsumerIndex();
            size = pIndex - cIndex;
            if (size >= (long)threshold) {
                return false;
            }
            producerLimit = cIndex + capacity;
            this.soProducerLimit(producerLimit);
        } while (!this.casProducerIndex(pIndex, pIndex + 1L));
        long offset = MpscArrayQueue.calcElementOffset(pIndex, mask);
        UnsafeRefArrayAccess.soElement(this.buffer, offset, e2);
        return true;
    }

    @Override
    public boolean offer(E e2) {
        long pIndex;
        if (null == e2) {
            throw new NullPointerException();
        }
        long mask = this.mask;
        long producerLimit = this.lvProducerLimit();
        do {
            if ((pIndex = this.lvProducerIndex()) < producerLimit) continue;
            long cIndex = this.lvConsumerIndex();
            producerLimit = cIndex + mask + 1L;
            if (pIndex >= producerLimit) {
                return false;
            }
            this.soProducerLimit(producerLimit);
        } while (!this.casProducerIndex(pIndex, pIndex + 1L));
        long offset = MpscArrayQueue.calcElementOffset(pIndex, mask);
        UnsafeRefArrayAccess.soElement(this.buffer, offset, e2);
        return true;
    }

    public final int failFastOffer(E e2) {
        long producerLimit;
        if (null == e2) {
            throw new NullPointerException();
        }
        long mask = this.mask;
        long capacity = mask + 1L;
        long pIndex = this.lvProducerIndex();
        if (pIndex >= (producerLimit = this.lvProducerLimit())) {
            long cIndex = this.lvConsumerIndex();
            producerLimit = cIndex + capacity;
            if (pIndex >= producerLimit) {
                return 1;
            }
            this.soProducerLimit(producerLimit);
        }
        if (!this.casProducerIndex(pIndex, pIndex + 1L)) {
            return -1;
        }
        long offset = MpscArrayQueue.calcElementOffset(pIndex, mask);
        UnsafeRefArrayAccess.soElement(this.buffer, offset, e2);
        return 0;
    }

    @Override
    public E poll() {
        Object[] buffer = this.buffer;
        long cIndex = this.lpConsumerIndex();
        long offset = this.calcElementOffset(cIndex);
        Object e2 = UnsafeRefArrayAccess.lvElement(buffer, offset);
        if (null == e2) {
            if (cIndex != this.lvProducerIndex()) {
                while ((e2 = UnsafeRefArrayAccess.lvElement(buffer, offset)) == null) {
                }
            } else {
                return null;
            }
        }
        UnsafeRefArrayAccess.spElement(buffer, offset, null);
        this.soConsumerIndex(cIndex + 1L);
        return (E)e2;
    }

    @Override
    public E peek() {
        Object[] buffer = this.buffer;
        long cIndex = this.lpConsumerIndex();
        long offset = this.calcElementOffset(cIndex);
        Object e2 = UnsafeRefArrayAccess.lvElement(buffer, offset);
        if (null == e2) {
            if (cIndex != this.lvProducerIndex()) {
                while ((e2 = UnsafeRefArrayAccess.lvElement(buffer, offset)) == null) {
                }
            } else {
                return null;
            }
        }
        return (E)e2;
    }

    @Override
    public boolean relaxedOffer(E e2) {
        return this.offer(e2);
    }

    @Override
    public E relaxedPoll() {
        Object[] buffer = this.buffer;
        long cIndex = this.lpConsumerIndex();
        long offset = this.calcElementOffset(cIndex);
        Object e2 = UnsafeRefArrayAccess.lvElement(buffer, offset);
        if (null == e2) {
            return null;
        }
        UnsafeRefArrayAccess.spElement(buffer, offset, null);
        this.soConsumerIndex(cIndex + 1L);
        return (E)e2;
    }

    @Override
    public E relaxedPeek() {
        Object[] buffer = this.buffer;
        long mask = this.mask;
        long cIndex = this.lpConsumerIndex();
        return (E)UnsafeRefArrayAccess.lvElement(buffer, MpscArrayQueue.calcElementOffset(cIndex, mask));
    }

    @Override
    public int drain(MessagePassingQueue.Consumer<E> c2) {
        return this.drain(c2, this.capacity());
    }

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
    public int drain(MessagePassingQueue.Consumer<E> c2, int limit) {
        Object[] buffer = this.buffer;
        long mask = this.mask;
        long cIndex = this.lpConsumerIndex();
        for (int i2 = 0; i2 < limit; ++i2) {
            long index = cIndex + (long)i2;
            long offset = MpscArrayQueue.calcElementOffset(index, mask);
            Object e2 = UnsafeRefArrayAccess.lvElement(buffer, offset);
            if (null == e2) {
                return i2;
            }
            UnsafeRefArrayAccess.spElement(buffer, offset, null);
            this.soConsumerIndex(index + 1L);
            c2.accept(e2);
        }
        return limit;
    }

    @Override
    public int fill(MessagePassingQueue.Supplier<E> s2, int limit) {
        long available;
        long pIndex;
        long mask = this.mask;
        long capacity = mask + 1L;
        long producerLimit = this.lvProducerLimit();
        int actualLimit = 0;
        do {
            if ((available = producerLimit - (pIndex = this.lvProducerIndex())) > 0L) continue;
            long cIndex = this.lvConsumerIndex();
            producerLimit = cIndex + capacity;
            available = producerLimit - pIndex;
            if (available <= 0L) {
                return 0;
            }
            this.soProducerLimit(producerLimit);
        } while (!this.casProducerIndex(pIndex, pIndex + (long)(actualLimit = Math.min((int)available, limit))));
        Object[] buffer = this.buffer;
        for (int i2 = 0; i2 < actualLimit; ++i2) {
            long offset = MpscArrayQueue.calcElementOffset(pIndex + (long)i2, mask);
            UnsafeRefArrayAccess.soElement(buffer, offset, s2.get());
        }
        return actualLimit;
    }

    @Override
    public void drain(MessagePassingQueue.Consumer<E> c2, MessagePassingQueue.WaitStrategy w2, MessagePassingQueue.ExitCondition exit) {
        Object[] buffer = this.buffer;
        long mask = this.mask;
        long cIndex = this.lpConsumerIndex();
        int counter = 0;
        while (exit.keepRunning()) {
            for (int i2 = 0; i2 < 4096; ++i2) {
                long offset = MpscArrayQueue.calcElementOffset(cIndex, mask);
                Object e2 = UnsafeRefArrayAccess.lvElement(buffer, offset);
                if (null == e2) {
                    counter = w2.idle(counter);
                    continue;
                }
                counter = 0;
                UnsafeRefArrayAccess.spElement(buffer, offset, null);
                this.soConsumerIndex(++cIndex);
                c2.accept(e2);
            }
        }
    }

    @Override
    public void fill(MessagePassingQueue.Supplier<E> s2, MessagePassingQueue.WaitStrategy w2, MessagePassingQueue.ExitCondition exit) {
        int idleCounter = 0;
        while (exit.keepRunning()) {
            if (this.fill(s2, MpmcArrayQueue.RECOMENDED_OFFER_BATCH) == 0) {
                idleCounter = w2.idle(idleCounter);
                continue;
            }
            idleCounter = 0;
        }
    }
}

