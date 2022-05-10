package it.unimi.dsi.fastutil.booleans;

import it.unimi.dsi.fastutil.booleans.AbstractBooleanSet;
import it.unimi.dsi.fastutil.booleans.BooleanCollection;
import it.unimi.dsi.fastutil.booleans.BooleanCollections;
import it.unimi.dsi.fastutil.booleans.BooleanIterators;
import it.unimi.dsi.fastutil.booleans.BooleanListIterator;
import it.unimi.dsi.fastutil.booleans.BooleanSet;
import java.io.Serializable;
import java.util.Collection;
import java.util.Set;

public class BooleanSets {
    public static final EmptySet EMPTY_SET = new EmptySet();

    private BooleanSets() {
    }

    public static BooleanSet singleton(boolean element) {
        return new Singleton(element);
    }

    public static BooleanSet singleton(Boolean element) {
        return new Singleton(element);
    }

    public static BooleanSet synchronize(BooleanSet s2) {
        return new SynchronizedSet(s2);
    }

    public static BooleanSet synchronize(BooleanSet s2, Object sync) {
        return new SynchronizedSet(s2, sync);
    }

    public static BooleanSet unmodifiable(BooleanSet s2) {
        return new UnmodifiableSet(s2);
    }

    public static class UnmodifiableSet
    extends BooleanCollections.UnmodifiableCollection
    implements BooleanSet,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;

        protected UnmodifiableSet(BooleanSet s2) {
            super(s2);
        }

        @Override
        public boolean remove(boolean k2) {
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
    extends BooleanCollections.SynchronizedCollection
    implements BooleanSet,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;

        protected SynchronizedSet(BooleanSet s2, Object sync) {
            super(s2, sync);
        }

        protected SynchronizedSet(BooleanSet s2) {
            super(s2);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean remove(boolean k2) {
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
    extends AbstractBooleanSet
    implements Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final boolean element;

        protected Singleton(boolean element) {
            this.element = element;
        }

        @Override
        public boolean add(boolean k2) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean contains(boolean k2) {
            return k2 == this.element;
        }

        @Override
        public boolean addAll(Collection<? extends Boolean> c2) {
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
        public boolean[] toBooleanArray() {
            boolean[] a2 = new boolean[]{this.element};
            return a2;
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
        public BooleanListIterator iterator() {
            return BooleanIterators.singleton(this.element);
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
    extends BooleanCollections.EmptyCollection
    implements BooleanSet,
    Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;

        protected EmptySet() {
        }

        @Override
        public boolean remove(boolean ok2) {
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

