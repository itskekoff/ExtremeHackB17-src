package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.bytes.AbstractByteSet;
import it.unimi.dsi.fastutil.bytes.ByteCollection;
import it.unimi.dsi.fastutil.bytes.ByteCollections;
import it.unimi.dsi.fastutil.bytes.ByteIterators;
import it.unimi.dsi.fastutil.bytes.ByteListIterator;
import it.unimi.dsi.fastutil.bytes.ByteSet;
import java.io.Serializable;
import java.util.Collection;
import java.util.Set;

public class ByteSets {
    public static final EmptySet EMPTY_SET = new EmptySet();

    private ByteSets() {
    }

    public static ByteSet singleton(byte element) {
        return new Singleton(element);
    }

    public static ByteSet singleton(Byte element) {
        return new Singleton(element);
    }

    public static ByteSet synchronize(ByteSet s2) {
        return new SynchronizedSet(s2);
    }

    public static ByteSet synchronize(ByteSet s2, Object sync) {
        return new SynchronizedSet(s2, sync);
    }

    public static ByteSet unmodifiable(ByteSet s2) {
        return new UnmodifiableSet(s2);
    }

    public static class UnmodifiableSet
    extends ByteCollections.UnmodifiableCollection
    implements ByteSet,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;

        protected UnmodifiableSet(ByteSet s2) {
            super(s2);
        }

        @Override
        public boolean remove(byte k2) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean equals(Object o2) {
            return this.collection.equals(o2);
        }

        @Override
        public int hashCode() {
            return this.collection.hashCode();
        }
    }

    public static class SynchronizedSet
    extends ByteCollections.SynchronizedCollection
    implements ByteSet,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;

        protected SynchronizedSet(ByteSet s2, Object sync) {
            super(s2, sync);
        }

        protected SynchronizedSet(ByteSet s2) {
            super(s2);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean remove(byte k2) {
            Object object = this.sync;
            synchronized (object) {
                return this.collection.remove(k2);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean equals(Object o2) {
            Object object = this.sync;
            synchronized (object) {
                return this.collection.equals(o2);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public int hashCode() {
            Object object = this.sync;
            synchronized (object) {
                return this.collection.hashCode();
            }
        }
    }

    public static class Singleton
    extends AbstractByteSet
    implements Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final byte element;

        protected Singleton(byte element) {
            this.element = element;
        }

        @Override
        public boolean add(byte k2) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean contains(byte k2) {
            return k2 == this.element;
        }

        @Override
        public boolean addAll(Collection<? extends Byte> c2) {
            throw new UnsupportedOperationException();
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
        public byte[] toByteArray() {
            byte[] a2 = new byte[]{this.element};
            return a2;
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
        public ByteListIterator iterator() {
            return ByteIterators.singleton(this.element);
        }

        @Override
        public int size() {
            return 1;
        }

        public Object clone() {
            return this;
        }
    }

    public static class EmptySet
    extends ByteCollections.EmptyCollection
    implements ByteSet,
    Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;

        protected EmptySet() {
        }

        @Override
        public boolean remove(byte ok2) {
            throw new UnsupportedOperationException();
        }

        public Object clone() {
            return EMPTY_SET;
        }

        @Override
        public boolean equals(Object o2) {
            return o2 instanceof Set && ((Set)o2).isEmpty();
        }

        private Object readResolve() {
            return EMPTY_SET;
        }
    }
}

