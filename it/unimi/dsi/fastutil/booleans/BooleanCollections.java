package it.unimi.dsi.fastutil.booleans;

import it.unimi.dsi.fastutil.booleans.AbstractBooleanCollection;
import it.unimi.dsi.fastutil.booleans.BooleanArrays;
import it.unimi.dsi.fastutil.booleans.BooleanBidirectionalIterator;
import it.unimi.dsi.fastutil.booleans.BooleanCollection;
import it.unimi.dsi.fastutil.booleans.BooleanIterable;
import it.unimi.dsi.fastutil.booleans.BooleanIterator;
import it.unimi.dsi.fastutil.booleans.BooleanIterators;
import it.unimi.dsi.fastutil.objects.ObjectArrays;
import java.io.Serializable;
import java.util.Collection;

public class BooleanCollections {
    private BooleanCollections() {
    }

    public static BooleanCollection synchronize(BooleanCollection c2) {
        return new SynchronizedCollection(c2);
    }

    public static BooleanCollection synchronize(BooleanCollection c2, Object sync) {
        return new SynchronizedCollection(c2, sync);
    }

    public static BooleanCollection unmodifiable(BooleanCollection c2) {
        return new UnmodifiableCollection(c2);
    }

    public static BooleanCollection asCollection(BooleanIterable iterable) {
        if (iterable instanceof BooleanCollection) {
            return (BooleanCollection)iterable;
        }
        return new IterableCollection(iterable);
    }

    public static class IterableCollection
    extends AbstractBooleanCollection
    implements Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final BooleanIterable iterable;

        protected IterableCollection(BooleanIterable iterable) {
            if (iterable == null) {
                throw new NullPointerException();
            }
            this.iterable = iterable;
        }

        @Override
        public int size() {
            int c2 = 0;
            BooleanIterator iterator = this.iterator();
            while (iterator.hasNext()) {
                iterator.next();
                ++c2;
            }
            return c2;
        }

        @Override
        public boolean isEmpty() {
            return !this.iterable.iterator().hasNext();
        }

        @Override
        public BooleanIterator iterator() {
            return this.iterable.iterator();
        }

        @Override
        @Deprecated
        public BooleanIterator booleanIterator() {
            return this.iterator();
        }
    }

    public static class UnmodifiableCollection
    implements BooleanCollection,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final BooleanCollection collection;

        protected UnmodifiableCollection(BooleanCollection c2) {
            if (c2 == null) {
                throw new NullPointerException();
            }
            this.collection = c2;
        }

        @Override
        public int size() {
            return this.collection.size();
        }

        @Override
        public boolean isEmpty() {
            return this.collection.isEmpty();
        }

        @Override
        public boolean contains(boolean o2) {
            return this.collection.contains(o2);
        }

        @Override
        public BooleanIterator iterator() {
            return BooleanIterators.unmodifiable(this.collection.iterator());
        }

        @Override
        @Deprecated
        public BooleanIterator booleanIterator() {
            return this.iterator();
        }

        @Override
        public boolean add(boolean k2) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean remove(Object ok2) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(Collection<? extends Boolean> c2) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean containsAll(Collection<?> c2) {
            return this.collection.containsAll(c2);
        }

        @Override
        public boolean removeAll(Collection<?> c2) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean retainAll(Collection<?> c2) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void clear() {
            throw new UnsupportedOperationException();
        }

        public String toString() {
            return this.collection.toString();
        }

        @Override
        public <T> T[] toArray(T[] a2) {
            return this.collection.toArray(a2);
        }

        @Override
        public Object[] toArray() {
            return this.collection.toArray();
        }

        @Override
        public boolean[] toBooleanArray() {
            return this.collection.toBooleanArray();
        }

        @Override
        public boolean[] toBooleanArray(boolean[] a2) {
            return this.collection.toBooleanArray(a2);
        }

        @Override
        public boolean[] toArray(boolean[] a2) {
            return this.collection.toArray(a2);
        }

        @Override
        public boolean rem(boolean k2) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(BooleanCollection c2) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean containsAll(BooleanCollection c2) {
            return this.collection.containsAll(c2);
        }

        @Override
        public boolean removeAll(BooleanCollection c2) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean retainAll(BooleanCollection c2) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean add(Boolean k2) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean contains(Object k2) {
            return this.collection.contains(k2);
        }
    }

    public static class SynchronizedCollection
    implements BooleanCollection,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final BooleanCollection collection;
        protected final Object sync;

        protected SynchronizedCollection(BooleanCollection c2, Object sync) {
            if (c2 == null) {
                throw new NullPointerException();
            }
            this.collection = c2;
            this.sync = sync;
        }

        protected SynchronizedCollection(BooleanCollection c2) {
            if (c2 == null) {
                throw new NullPointerException();
            }
            this.collection = c2;
            this.sync = this;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public int size() {
            Object object = this.sync;
            synchronized (object) {
                return this.collection.size();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean isEmpty() {
            Object object = this.sync;
            synchronized (object) {
                return this.collection.isEmpty();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean contains(boolean o2) {
            Object object = this.sync;
            synchronized (object) {
                return this.collection.contains(o2);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean[] toBooleanArray() {
            Object object = this.sync;
            synchronized (object) {
                return this.collection.toBooleanArray();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public Object[] toArray() {
            Object object = this.sync;
            synchronized (object) {
                return this.collection.toArray();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean[] toBooleanArray(boolean[] a2) {
            Object object = this.sync;
            synchronized (object) {
                return this.collection.toBooleanArray(a2);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean[] toArray(boolean[] a2) {
            Object object = this.sync;
            synchronized (object) {
                return this.collection.toBooleanArray(a2);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean addAll(BooleanCollection c2) {
            Object object = this.sync;
            synchronized (object) {
                return this.collection.addAll(c2);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean containsAll(BooleanCollection c2) {
            Object object = this.sync;
            synchronized (object) {
                return this.collection.containsAll(c2);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean removeAll(BooleanCollection c2) {
            Object object = this.sync;
            synchronized (object) {
                return this.collection.removeAll(c2);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean retainAll(BooleanCollection c2) {
            Object object = this.sync;
            synchronized (object) {
                return this.collection.retainAll(c2);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean add(Boolean k2) {
            Object object = this.sync;
            synchronized (object) {
                return this.collection.add(k2);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean contains(Object k2) {
            Object object = this.sync;
            synchronized (object) {
                return this.collection.contains(k2);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public <T> T[] toArray(T[] a2) {
            Object object = this.sync;
            synchronized (object) {
                return this.collection.toArray(a2);
            }
        }

        @Override
        public BooleanIterator iterator() {
            return this.collection.iterator();
        }

        @Override
        @Deprecated
        public BooleanIterator booleanIterator() {
            return this.iterator();
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean add(boolean k2) {
            Object object = this.sync;
            synchronized (object) {
                return this.collection.add(k2);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean rem(boolean k2) {
            Object object = this.sync;
            synchronized (object) {
                return this.collection.rem(k2);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean remove(Object ok2) {
            Object object = this.sync;
            synchronized (object) {
                return this.collection.remove(ok2);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean addAll(Collection<? extends Boolean> c2) {
            Object object = this.sync;
            synchronized (object) {
                return this.collection.addAll(c2);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean containsAll(Collection<?> c2) {
            Object object = this.sync;
            synchronized (object) {
                return this.collection.containsAll(c2);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean removeAll(Collection<?> c2) {
            Object object = this.sync;
            synchronized (object) {
                return this.collection.removeAll(c2);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean retainAll(Collection<?> c2) {
            Object object = this.sync;
            synchronized (object) {
                return this.collection.retainAll(c2);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void clear() {
            Object object = this.sync;
            synchronized (object) {
                this.collection.clear();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public String toString() {
            Object object = this.sync;
            synchronized (object) {
                return this.collection.toString();
            }
        }
    }

    public static abstract class EmptyCollection
    extends AbstractBooleanCollection {
        protected EmptyCollection() {
        }

        @Override
        public boolean add(boolean k2) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean contains(boolean k2) {
            return false;
        }

        @Override
        public Object[] toArray() {
            return ObjectArrays.EMPTY_ARRAY;
        }

        @Override
        public boolean[] toBooleanArray(boolean[] a2) {
            return a2;
        }

        @Override
        public boolean[] toBooleanArray() {
            return BooleanArrays.EMPTY_ARRAY;
        }

        @Override
        public boolean rem(boolean k2) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(BooleanCollection c2) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean removeAll(BooleanCollection c2) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean retainAll(BooleanCollection c2) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean containsAll(BooleanCollection c2) {
            return c2.isEmpty();
        }

        @Override
        public BooleanBidirectionalIterator iterator() {
            return BooleanIterators.EMPTY_ITERATOR;
        }

        @Override
        public int size() {
            return 0;
        }

        @Override
        public void clear() {
        }

        @Override
        public int hashCode() {
            return 0;
        }

        @Override
        public boolean equals(Object o2) {
            if (o2 == this) {
                return true;
            }
            if (!(o2 instanceof Collection)) {
                return false;
            }
            return ((Collection)o2).isEmpty();
        }
    }
}

