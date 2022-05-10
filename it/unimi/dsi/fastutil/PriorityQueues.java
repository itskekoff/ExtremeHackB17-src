package it.unimi.dsi.fastutil;

import it.unimi.dsi.fastutil.AbstractPriorityQueue;
import it.unimi.dsi.fastutil.PriorityQueue;
import java.util.Comparator;
import java.util.NoSuchElementException;

public class PriorityQueues {
    public static final EmptyPriorityQueue EMPTY_QUEUE = new EmptyPriorityQueue();

    private PriorityQueues() {
    }

    public static <K> PriorityQueue<K> synchronize(PriorityQueue<K> q2) {
        return new SynchronizedPriorityQueue<K>(q2);
    }

    public static <K> PriorityQueue<K> synchronize(PriorityQueue<K> q2, Object sync) {
        return new SynchronizedPriorityQueue<K>(q2, sync);
    }

    public static class SynchronizedPriorityQueue<K>
    implements PriorityQueue<K> {
        public static final long serialVersionUID = -7046029254386353129L;
        protected final PriorityQueue<K> q;
        protected final Object sync;

        protected SynchronizedPriorityQueue(PriorityQueue<K> q2, Object sync) {
            this.q = q2;
            this.sync = sync;
        }

        protected SynchronizedPriorityQueue(PriorityQueue<K> q2) {
            this.q = q2;
            this.sync = this;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void enqueue(K x2) {
            Object object = this.sync;
            synchronized (object) {
                this.q.enqueue(x2);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public K dequeue() {
            Object object = this.sync;
            synchronized (object) {
                return this.q.dequeue();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public K first() {
            Object object = this.sync;
            synchronized (object) {
                return this.q.first();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public K last() {
            Object object = this.sync;
            synchronized (object) {
                return this.q.last();
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
        public Comparator<? super K> comparator() {
            Object object = this.sync;
            synchronized (object) {
                return this.q.comparator();
            }
        }
    }

    public static class EmptyPriorityQueue
    extends AbstractPriorityQueue {
        protected EmptyPriorityQueue() {
        }

        @Override
        public void enqueue(Object o2) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Object dequeue() {
            throw new NoSuchElementException();
        }

        @Override
        public boolean isEmpty() {
            return true;
        }

        @Override
        public int size() {
            return 0;
        }

        @Override
        public void clear() {
        }

        @Override
        public Object first() {
            throw new NoSuchElementException();
        }

        @Override
        public Object last() {
            throw new NoSuchElementException();
        }

        @Override
        public void changed() {
            throw new NoSuchElementException();
        }

        @Override
        public Comparator<?> comparator() {
            return null;
        }
    }
}

