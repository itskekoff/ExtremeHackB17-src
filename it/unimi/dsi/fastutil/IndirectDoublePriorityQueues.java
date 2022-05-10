package it.unimi.dsi.fastutil;

import it.unimi.dsi.fastutil.IndirectDoublePriorityQueue;
import it.unimi.dsi.fastutil.IndirectPriorityQueues;
import java.util.Comparator;
import java.util.NoSuchElementException;

public class IndirectDoublePriorityQueues {
    public static final EmptyIndirectDoublePriorityQueue EMPTY_QUEUE = new EmptyIndirectDoublePriorityQueue();

    private IndirectDoublePriorityQueues() {
    }

    public static <K> IndirectDoublePriorityQueue<K> synchronize(IndirectDoublePriorityQueue<K> q2) {
        return new SynchronizedIndirectDoublePriorityQueue<K>(q2);
    }

    public static <K> IndirectDoublePriorityQueue<K> synchronize(IndirectDoublePriorityQueue<K> q2, Object sync) {
        return new SynchronizedIndirectDoublePriorityQueue<K>(q2, sync);
    }

    public static class SynchronizedIndirectDoublePriorityQueue<K>
    implements IndirectDoublePriorityQueue<K> {
        public static final long serialVersionUID = -7046029254386353129L;
        protected final IndirectDoublePriorityQueue<K> q;
        protected final Object sync;

        protected SynchronizedIndirectDoublePriorityQueue(IndirectDoublePriorityQueue<K> q2, Object sync) {
            this.q = q2;
            this.sync = sync;
        }

        protected SynchronizedIndirectDoublePriorityQueue(IndirectDoublePriorityQueue<K> q2) {
            this.q = q2;
            this.sync = this;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void enqueue(int index) {
            Object object = this.sync;
            synchronized (object) {
                this.q.enqueue(index);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public int dequeue() {
            Object object = this.sync;
            synchronized (object) {
                return this.q.dequeue();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public int first() {
            Object object = this.sync;
            synchronized (object) {
                return this.q.first();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public int last() {
            Object object = this.sync;
            synchronized (object) {
                return this.q.last();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean contains(int index) {
            Object object = this.sync;
            synchronized (object) {
                return this.q.contains(index);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public int secondaryFirst() {
            Object object = this.sync;
            synchronized (object) {
                return this.q.secondaryFirst();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public int secondaryLast() {
            Object object = this.sync;
            synchronized (object) {
                return this.q.secondaryLast();
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
        public void allChanged() {
            Object object = this.sync;
            synchronized (object) {
                this.q.allChanged();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void changed(int i2) {
            Object object = this.sync;
            synchronized (object) {
                this.q.changed(i2);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean remove(int i2) {
            Object object = this.sync;
            synchronized (object) {
                return this.q.remove(i2);
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

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public Comparator<? super K> secondaryComparator() {
            Object object = this.sync;
            synchronized (object) {
                return this.q.secondaryComparator();
            }
        }

        @Override
        public int secondaryFront(int[] a2) {
            return this.q.secondaryFront(a2);
        }

        @Override
        public int front(int[] a2) {
            return this.q.front(a2);
        }
    }

    public static class EmptyIndirectDoublePriorityQueue
    extends IndirectPriorityQueues.EmptyIndirectPriorityQueue {
        protected EmptyIndirectDoublePriorityQueue() {
        }

        public int secondaryFirst() {
            throw new NoSuchElementException();
        }

        public int secondaryLast() {
            throw new NoSuchElementException();
        }

        public Comparator<?> secondaryComparator() {
            return null;
        }
    }
}

