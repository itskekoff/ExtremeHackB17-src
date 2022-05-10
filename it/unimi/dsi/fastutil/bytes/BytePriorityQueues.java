package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.bytes.ByteComparator;
import it.unimi.dsi.fastutil.bytes.BytePriorityQueue;

public class BytePriorityQueues {
    private BytePriorityQueues() {
    }

    public static BytePriorityQueue synchronize(BytePriorityQueue q2) {
        return new SynchronizedPriorityQueue(q2);
    }

    public static BytePriorityQueue synchronize(BytePriorityQueue q2, Object sync) {
        return new SynchronizedPriorityQueue(q2, sync);
    }

    public static class SynchronizedPriorityQueue
    implements BytePriorityQueue {
        protected final BytePriorityQueue q;
        protected final Object sync;

        protected SynchronizedPriorityQueue(BytePriorityQueue q2, Object sync) {
            this.q = q2;
            this.sync = sync;
        }

        protected SynchronizedPriorityQueue(BytePriorityQueue q2) {
            this.q = q2;
            this.sync = this;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void enqueue(byte x2) {
            Object object = this.sync;
            synchronized (object) {
                this.q.enqueue(x2);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public byte dequeueByte() {
            Object object = this.sync;
            synchronized (object) {
                return this.q.dequeueByte();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public byte firstByte() {
            Object object = this.sync;
            synchronized (object) {
                return this.q.firstByte();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public byte lastByte() {
            Object object = this.sync;
            synchronized (object) {
                return this.q.lastByte();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean isEmpty() {
            Object object = this.sync;
            synchronized (object) {
                return this.q.isEmpty();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public int size() {
            Object object = this.sync;
            synchronized (object) {
                return this.q.size();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void clear() {
            Object object = this.sync;
            synchronized (object) {
                this.q.clear();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void changed() {
            Object object = this.sync;
            synchronized (object) {
                this.q.changed();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public ByteComparator comparator() {
            Object object = this.sync;
            synchronized (object) {
                return this.q.comparator();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void enqueue(Byte x2) {
            Object object = this.sync;
            synchronized (object) {
                this.q.enqueue(x2);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public Byte dequeue() {
            Object object = this.sync;
            synchronized (object) {
                return (Byte)this.q.dequeue();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public Byte first() {
            Object object = this.sync;
            synchronized (object) {
                return (Byte)this.q.first();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public Byte last() {
            Object object = this.sync;
            synchronized (object) {
                return (Byte)this.q.last();
            }
        }
    }
}

