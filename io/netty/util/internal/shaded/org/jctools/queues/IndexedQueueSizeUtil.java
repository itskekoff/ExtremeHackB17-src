package io.netty.util.internal.shaded.org.jctools.queues;

final class IndexedQueueSizeUtil {
    private IndexedQueueSizeUtil() {
    }

    static int size(IndexedQueue iq2) {
        long currentProducerIndex;
        long before;
        long after = iq2.lvConsumerIndex();
        do {
            before = after;
            currentProducerIndex = iq2.lvProducerIndex();
        } while (before != (after = iq2.lvConsumerIndex()));
        long size = currentProducerIndex - after;
        if (size > Integer.MAX_VALUE) {
            return Integer.MAX_VALUE;
        }
        return (int)size;
    }

    static boolean isEmpty(IndexedQueue iq2) {
        return iq2.lvConsumerIndex() == iq2.lvProducerIndex();
    }

    protected static interface IndexedQueue {
        public long lvConsumerIndex();

        public long lvProducerIndex();
    }
}

