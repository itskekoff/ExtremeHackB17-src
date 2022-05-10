package io.netty.util.internal.shaded.org.jctools.queues;

import io.netty.util.internal.shaded.org.jctools.queues.MpscArrayQueueL2Pad;
import io.netty.util.internal.shaded.org.jctools.util.UnsafeAccess;

abstract class MpscArrayQueueConsumerField<E>
extends MpscArrayQueueL2Pad<E> {
    private static final long C_INDEX_OFFSET;
    protected long consumerIndex;

    public MpscArrayQueueConsumerField(int capacity) {
        super(capacity);
    }

    protected final long lpConsumerIndex() {
        return this.consumerIndex;
    }

    @Override
    public final long lvConsumerIndex() {
        return UnsafeAccess.UNSAFE.getLongVolatile(this, C_INDEX_OFFSET);
    }

    protected void soConsumerIndex(long l2) {
        UnsafeAccess.UNSAFE.putOrderedLong(this, C_INDEX_OFFSET, l2);
    }

    static {
        try {
            C_INDEX_OFFSET = UnsafeAccess.UNSAFE.objectFieldOffset(MpscArrayQueueConsumerField.class.getDeclaredField("consumerIndex"));
        }
        catch (NoSuchFieldException e2) {
            throw new RuntimeException(e2);
        }
    }
}

