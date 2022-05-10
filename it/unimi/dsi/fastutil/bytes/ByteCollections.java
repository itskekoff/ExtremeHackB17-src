package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.bytes.AbstractByteCollection;
import it.unimi.dsi.fastutil.bytes.ByteArrays;
import it.unimi.dsi.fastutil.bytes.ByteBidirectionalIterator;
import it.unimi.dsi.fastutil.bytes.ByteCollection;
import it.unimi.dsi.fastutil.bytes.ByteIterable;
import it.unimi.dsi.fastutil.bytes.ByteIterator;
import it.unimi.dsi.fastutil.bytes.ByteIterators;
import it.unimi.dsi.fastutil.objects.ObjectArrays;
import java.io.Serializable;
import java.util.Collection;

public class ByteCollections {
    private ByteCollections() {
    }

    public static ByteCollection synchronize(ByteCollection c2) {
        return new SynchronizedCollection(c2);
    }

    public static ByteCollection synchronize(ByteCollection c2, Object sync) {
        return new SynchronizedCollection(c2, sync);
    }

    public static ByteCollection unmodifiable(ByteCollection c2) {
        return new UnmodifiableCollection(c2);
    }

    public static ByteCollection asCollection(ByteIterable iterable) {
        if (iterable instanceof ByteCollection) {
            return (ByteCollection)iterable;
        }
        return new IterableCollection(iterable);
    }

    public static class IterableCollection
    extends AbstractByteCollection
    implements Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final ByteIterable iterable;

        protected IterableCollection(ByteIterable iterable) {
            if (iterable == null) {
                throw new NullPointerException();
            }
            this.iterable = iterable;
        }

        @Override
        public int size() {
            int c2 = 0;
            ByteIterator iterator = this.iterator();
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
        public ByteIterator iterator() {
            return this.iterable.iterator();
        }

        @Override
        @Deprecated
        public ByteIterator byteIterator() {
            return this.iterator();
        }
    }

    public static class UnmodifiableCollection
    implements ByteCollection,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final ByteCollection collection;

        protected UnmodifiableCollection(ByteCollection c2) {
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
        public boolean contains(byte o2) {
            return this.collection.contains(o2);
        }

        @Override
        public ByteIterator iterator() {
            return ByteIterators.unmodifiable(this.collection.iterator());
        }

        @Override
        @Deprecated
        public ByteIterator byteIterator() {
            return this.iterator();
        }

        @Override
        public boolean add(byte k2) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean remove(Object ok2) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(Collection<? extends Byte> c2) {
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
        public byte[] toByteArray() {
            return this.collection.toByteArray();
        }

        @Override
        public byte[] toByteArray(byte[] a2) {
            return this.collection.toByteArray(a2);
        }

        @Override
        public byte[] toArray(byte[] a2) {
            return this.collection.toArray(a2);
        }

        @Override
        public boolean rem(byte k2) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(ByteCollection c2) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean containsAll(ByteCollection c2) {
            return this.collection.containsAll(c2);
        }

        @Override
        public boolean removeAll(ByteCollection c2) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean retainAll(ByteCollection c2) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean add(Byte k2) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean contains(Object k2) {
            return this.collection.contains(k2);
        }
    }

    public static class SynchronizedCollection
    implements ByteCollection,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final ByteCollection collection;
        protected final Object sync;

        protected SynchronizedCollection(ByteCollection c2, Object sync) {
            if (c2 == null) {
                throw new NullPointerException();
            }
            this.collection = c2;
            this.sync = sync;
        }

        protected SynchronizedCollection(ByteCollection c2) {
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
        public boolean contains(byte o2) {
            Object object = this.sync;
            synchronized (object) {
                return this.collection.contains(o2);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public byte[] toByteArray() {
            Object object = this.sync;
            synchronized (object) {
                return this.collection.toByteArray();
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
        public byte[] toByteArray(byte[] a2) {
            Object object = this.sync;
            synchronized (object) {
                return this.collection.toByteArray(a2);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public byte[] toArray(byte[] a2) {
            Object object = this.sync;
            synchronized (object) {
                return this.collection.toByteArray(a2);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean addAll(ByteCollection c2) {
            Object object = this.sync;
            synchronized (object) {
                return this.collection.addAll(c2);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean containsAll(ByteCollection c2) {
            Object object = this.sync;
            synchronized (object) {
                return this.collection.containsAll(c2);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean removeAll(ByteCollection c2) {
            Object object = this.sync;
            synchronized (object) {
                return this.collection.removeAll(c2);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean retainAll(ByteCollection c2) {
            Object object = this.sync;
            synchronized (object) {
                return this.collection.retainAll(c2);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean add(Byte k2) {
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
        public ByteIterator iterator() {
            return this.collection.iterator();
        }

        @Override
        @Deprecated
        public ByteIterator byteIterator() {
            return this.iterator();
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean add(byte k2) {
            Object object = this.sync;
            synchronized (object) {
                return this.collection.add(k2);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean rem(byte k2) {
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
        public boolean addAll(Collection<? extends Byte> c2) {
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
    extends AbstractByteCollection {
        protected EmptyCollection() {
        }

        @Override
        public boolean add(byte k2) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean contains(byte k2) {
            return false;
        }

        @Override
        public Object[] toArray() {
            return ObjectArrays.EMPTY_ARRAY;
        }

        @Override
        public byte[] toByteArray(byte[] a2) {
            return a2;
        }

        @Override
        public byte[] toByteArray() {
            return ByteArrays.EMPTY_ARRAY;
        }

        @Override
        public boolean rem(byte k2) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(ByteCollection c2) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean removeAll(ByteCollection c2) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean retainAll(ByteCollection c2) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean containsAll(ByteCollection c2) {
            return c2.isEmpty();
        }

        @Override
        public ByteBidirectionalIterator iterator() {
            return ByteIterators.EMPTY_ITERATOR;
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

