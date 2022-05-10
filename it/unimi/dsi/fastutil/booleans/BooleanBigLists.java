package it.unimi.dsi.fastutil.booleans;

import it.unimi.dsi.fastutil.BigList;
import it.unimi.dsi.fastutil.booleans.AbstractBooleanBigList;
import it.unimi.dsi.fastutil.booleans.BooleanBigArrays;
import it.unimi.dsi.fastutil.booleans.BooleanBigList;
import it.unimi.dsi.fastutil.booleans.BooleanBigListIterator;
import it.unimi.dsi.fastutil.booleans.BooleanBigListIterators;
import it.unimi.dsi.fastutil.booleans.BooleanCollection;
import it.unimi.dsi.fastutil.booleans.BooleanCollections;
import it.unimi.dsi.fastutil.booleans.BooleanList;
import java.io.Serializable;
import java.util.Collection;
import java.util.Random;

public class BooleanBigLists {
    public static final EmptyBigList EMPTY_BIG_LIST = new EmptyBigList();

    private BooleanBigLists() {
    }

    public static BooleanBigList shuffle(BooleanBigList l2, Random random) {
        long i2 = l2.size64();
        while (i2-- != 0L) {
            long p2 = (random.nextLong() & Long.MAX_VALUE) % (i2 + 1L);
            boolean t2 = l2.getBoolean(i2);
            l2.set(i2, l2.getBoolean(p2));
            l2.set(p2, t2);
        }
        return l2;
    }

    public static BooleanBigList singleton(boolean element) {
        return new Singleton(element);
    }

    public static BooleanBigList singleton(Object element) {
        return new Singleton((Boolean)element);
    }

    public static BooleanBigList synchronize(BooleanBigList l2) {
        return new SynchronizedBigList(l2);
    }

    public static BooleanBigList synchronize(BooleanBigList l2, Object sync) {
        return new SynchronizedBigList(l2, sync);
    }

    public static BooleanBigList unmodifiable(BooleanBigList l2) {
        return new UnmodifiableBigList(l2);
    }

    public static BooleanBigList asBigList(BooleanList list) {
        return new ListBigList(list);
    }

    public static class ListBigList
    extends AbstractBooleanBigList
    implements Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        private final BooleanList list;

        protected ListBigList(BooleanList list) {
            this.list = list;
        }

        private int intIndex(long index) {
            if (index >= Integer.MAX_VALUE) {
                throw new IndexOutOfBoundsException("This big list is restricted to 32-bit indices");
            }
            return (int)index;
        }

        @Override
        public long size64() {
            return this.list.size();
        }

        @Override
        @Deprecated
        public int size() {
            return this.list.size();
        }

        @Override
        public void size(long size) {
            this.list.size(this.intIndex(size));
        }

        @Override
        public BooleanBigListIterator iterator() {
            return BooleanBigListIterators.asBigListIterator(this.list.iterator());
        }

        @Override
        public BooleanBigListIterator listIterator() {
            return BooleanBigListIterators.asBigListIterator(this.list.listIterator());
        }

        @Override
        public boolean addAll(long index, Collection<? extends Boolean> c2) {
            return this.list.addAll(this.intIndex(index), c2);
        }

        @Override
        public BooleanBigListIterator listIterator(long index) {
            return BooleanBigListIterators.asBigListIterator(this.list.listIterator(this.intIndex(index)));
        }

        @Override
        public BooleanBigList subList(long from, long to2) {
            return new ListBigList(this.list.subList(this.intIndex(from), this.intIndex(to2)));
        }

        @Override
        public boolean contains(boolean key) {
            return this.list.contains(key);
        }

        @Override
        public boolean[] toBooleanArray() {
            return this.list.toBooleanArray();
        }

        @Override
        public void removeElements(long from, long to2) {
            this.list.removeElements(this.intIndex(from), this.intIndex(to2));
        }

        @Override
        public boolean[] toBooleanArray(boolean[] a2) {
            return this.list.toBooleanArray(a2);
        }

        @Override
        public void add(long index, boolean key) {
            this.list.add(this.intIndex(index), key);
        }

        @Override
        public boolean addAll(long index, BooleanCollection c2) {
            return this.list.addAll(this.intIndex(index), c2);
        }

        @Override
        public boolean addAll(long index, BooleanBigList c2) {
            return this.list.addAll(this.intIndex(index), c2);
        }

        @Override
        public boolean add(boolean key) {
            return this.list.add(key);
        }

        @Override
        public boolean addAll(BooleanBigList c2) {
            return this.list.addAll(c2);
        }

        @Override
        public boolean getBoolean(long index) {
            return this.list.getBoolean(this.intIndex(index));
        }

        @Override
        public long indexOf(boolean k2) {
            return this.list.indexOf(k2);
        }

        @Override
        public long lastIndexOf(boolean k2) {
            return this.list.lastIndexOf(k2);
        }

        @Override
        public boolean removeBoolean(long index) {
            return this.list.removeBoolean(this.intIndex(index));
        }

        @Override
        public boolean set(long index, boolean k2) {
            return this.list.set(this.intIndex(index), k2);
        }

        @Override
        public boolean addAll(BooleanCollection c2) {
            return this.list.addAll(c2);
        }

        @Override
        public boolean containsAll(BooleanCollection c2) {
            return this.list.containsAll(c2);
        }

        @Override
        public boolean removeAll(BooleanCollection c2) {
            return this.list.removeAll(c2);
        }

        @Override
        public boolean retainAll(BooleanCollection c2) {
            return this.list.retainAll(c2);
        }

        @Override
        public boolean isEmpty() {
            return this.list.isEmpty();
        }

        @Override
        public <T> T[] toArray(T[] a2) {
            return this.list.toArray(a2);
        }

        @Override
        public boolean containsAll(Collection<?> c2) {
            return this.list.containsAll(c2);
        }

        @Override
        public boolean addAll(Collection<? extends Boolean> c2) {
            return this.list.addAll(c2);
        }

        @Override
        public boolean removeAll(Collection<?> c2) {
            return this.list.removeAll(c2);
        }

        @Override
        public boolean retainAll(Collection<?> c2) {
            return this.list.retainAll(c2);
        }

        @Override
        public void clear() {
            this.list.clear();
        }

        @Override
        public int hashCode() {
            return this.list.hashCode();
        }
    }

    public static class UnmodifiableBigList
    extends BooleanCollections.UnmodifiableCollection
    implements BooleanBigList,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final BooleanBigList list;

        protected UnmodifiableBigList(BooleanBigList l2) {
            super(l2);
            this.list = l2;
        }

        @Override
        public boolean getBoolean(long i2) {
            return this.list.getBoolean(i2);
        }

        @Override
        public boolean set(long i2, boolean k2) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void add(long i2, boolean k2) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean removeBoolean(long i2) {
            throw new UnsupportedOperationException();
        }

        @Override
        public long indexOf(boolean k2) {
            return this.list.indexOf(k2);
        }

        @Override
        public long lastIndexOf(boolean k2) {
            return this.list.lastIndexOf(k2);
        }

        @Override
        public boolean addAll(long index, Collection<? extends Boolean> c2) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void getElements(long from, boolean[][] a2, long offset, long length) {
            this.list.getElements(from, a2, offset, length);
        }

        @Override
        public void removeElements(long from, long to2) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void addElements(long index, boolean[][] a2, long offset, long length) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void addElements(long index, boolean[][] a2) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void size(long size) {
            this.list.size(size);
        }

        @Override
        public long size64() {
            return this.list.size64();
        }

        @Override
        public BooleanBigListIterator iterator() {
            return this.listIterator();
        }

        @Override
        public BooleanBigListIterator listIterator() {
            return BooleanBigListIterators.unmodifiable(this.list.listIterator());
        }

        @Override
        public BooleanBigListIterator listIterator(long i2) {
            return BooleanBigListIterators.unmodifiable(this.list.listIterator(i2));
        }

        @Override
        public BooleanBigList subList(long from, long to2) {
            return BooleanBigLists.unmodifiable(this.list.subList(from, to2));
        }

        @Override
        public boolean equals(Object o2) {
            return this.list.equals(o2);
        }

        @Override
        public int hashCode() {
            return this.list.hashCode();
        }

        @Override
        public int compareTo(BigList<? extends Boolean> o2) {
            return this.list.compareTo(o2);
        }

        @Override
        public boolean addAll(long index, BooleanCollection c2) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(BooleanBigList l2) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(long index, BooleanBigList l2) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Boolean get(long i2) {
            return (Boolean)this.list.get(i2);
        }

        @Override
        public void add(long i2, Boolean k2) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Boolean set(long index, Boolean k2) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Boolean remove(long i2) {
            throw new UnsupportedOperationException();
        }

        @Override
        public long indexOf(Object o2) {
            return this.list.indexOf(o2);
        }

        @Override
        public long lastIndexOf(Object o2) {
            return this.list.lastIndexOf(o2);
        }
    }

    public static class SynchronizedBigList
    extends BooleanCollections.SynchronizedCollection
    implements BooleanBigList,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final BooleanBigList list;

        protected SynchronizedBigList(BooleanBigList l2, Object sync) {
            super(l2, sync);
            this.list = l2;
        }

        protected SynchronizedBigList(BooleanBigList l2) {
            super(l2);
            this.list = l2;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean getBoolean(long i2) {
            Object object = this.sync;
            synchronized (object) {
                return this.list.getBoolean(i2);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean set(long i2, boolean k2) {
            Object object = this.sync;
            synchronized (object) {
                return this.list.set(i2, k2);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void add(long i2, boolean k2) {
            Object object = this.sync;
            synchronized (object) {
                this.list.add(i2, k2);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean removeBoolean(long i2) {
            Object object = this.sync;
            synchronized (object) {
                return this.list.removeBoolean(i2);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public long indexOf(boolean k2) {
            Object object = this.sync;
            synchronized (object) {
                return this.list.indexOf(k2);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public long lastIndexOf(boolean k2) {
            Object object = this.sync;
            synchronized (object) {
                return this.list.lastIndexOf(k2);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean addAll(long index, Collection<? extends Boolean> c2) {
            Object object = this.sync;
            synchronized (object) {
                return this.list.addAll(index, c2);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void getElements(long from, boolean[][] a2, long offset, long length) {
            Object object = this.sync;
            synchronized (object) {
                this.list.getElements(from, a2, offset, length);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void removeElements(long from, long to2) {
            Object object = this.sync;
            synchronized (object) {
                this.list.removeElements(from, to2);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void addElements(long index, boolean[][] a2, long offset, long length) {
            Object object = this.sync;
            synchronized (object) {
                this.list.addElements(index, a2, offset, length);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void addElements(long index, boolean[][] a2) {
            Object object = this.sync;
            synchronized (object) {
                this.list.addElements(index, a2);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void size(long size) {
            Object object = this.sync;
            synchronized (object) {
                this.list.size(size);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public long size64() {
            Object object = this.sync;
            synchronized (object) {
                return this.list.size64();
            }
        }

        @Override
        public BooleanBigListIterator iterator() {
            return this.list.listIterator();
        }

        @Override
        public BooleanBigListIterator listIterator() {
            return this.list.listIterator();
        }

        @Override
        public BooleanBigListIterator listIterator(long i2) {
            return this.list.listIterator(i2);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public BooleanBigList subList(long from, long to2) {
            Object object = this.sync;
            synchronized (object) {
                return BooleanBigLists.synchronize(this.list.subList(from, to2), this.sync);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean equals(Object o2) {
            Object object = this.sync;
            synchronized (object) {
                return this.list.equals(o2);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public int hashCode() {
            Object object = this.sync;
            synchronized (object) {
                return this.list.hashCode();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public int compareTo(BigList<? extends Boolean> o2) {
            Object object = this.sync;
            synchronized (object) {
                return this.list.compareTo(o2);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean addAll(long index, BooleanCollection c2) {
            Object object = this.sync;
            synchronized (object) {
                return this.list.addAll(index, c2);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean addAll(long index, BooleanBigList l2) {
            Object object = this.sync;
            synchronized (object) {
                return this.list.addAll(index, l2);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean addAll(BooleanBigList l2) {
            Object object = this.sync;
            synchronized (object) {
                return this.list.addAll(l2);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public Boolean get(long i2) {
            Object object = this.sync;
            synchronized (object) {
                return (Boolean)this.list.get(i2);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void add(long i2, Boolean k2) {
            Object object = this.sync;
            synchronized (object) {
                this.list.add(i2, k2);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public Boolean set(long index, Boolean k2) {
            Object object = this.sync;
            synchronized (object) {
                return this.list.set(index, k2);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public Boolean remove(long i2) {
            Object object = this.sync;
            synchronized (object) {
                return (Boolean)this.list.remove(i2);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public long indexOf(Object o2) {
            Object object = this.sync;
            synchronized (object) {
                return this.list.indexOf(o2);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public long lastIndexOf(Object o2) {
            Object object = this.sync;
            synchronized (object) {
                return this.list.lastIndexOf(o2);
            }
        }
    }

    public static class Singleton
    extends AbstractBooleanBigList
    implements Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;
        private final boolean element;

        private Singleton(boolean element) {
            this.element = element;
        }

        @Override
        public boolean getBoolean(long i2) {
            if (i2 == 0L) {
                return this.element;
            }
            throw new IndexOutOfBoundsException();
        }

        @Override
        public boolean removeBoolean(long i2) {
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
        public boolean addAll(long i2, Collection<? extends Boolean> c2) {
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
        public BooleanBigListIterator listIterator() {
            return BooleanBigListIterators.singleton(this.element);
        }

        @Override
        public BooleanBigListIterator iterator() {
            return this.listIterator();
        }

        @Override
        public BooleanBigListIterator listIterator(long i2) {
            if (i2 > 1L || i2 < 0L) {
                throw new IndexOutOfBoundsException();
            }
            BooleanBigListIterator l2 = this.listIterator();
            if (i2 == 1L) {
                l2.next();
            }
            return l2;
        }

        @Override
        public BooleanBigList subList(long from, long to2) {
            this.ensureIndex(from);
            this.ensureIndex(to2);
            if (from > to2) {
                throw new IndexOutOfBoundsException("Start index (" + from + ") is greater than end index (" + to2 + ")");
            }
            if (from != 0L || to2 != 1L) {
                return EMPTY_BIG_LIST;
            }
            return this;
        }

        @Override
        @Deprecated
        public int size() {
            return 1;
        }

        @Override
        public long size64() {
            return 1L;
        }

        @Override
        public void size(long size) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void clear() {
            throw new UnsupportedOperationException();
        }

        public Object clone() {
            return this;
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
        public boolean addAll(long i2, BooleanCollection c2) {
            throw new UnsupportedOperationException();
        }
    }

    public static class EmptyBigList
    extends BooleanCollections.EmptyCollection
    implements BooleanBigList,
    Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;

        protected EmptyBigList() {
        }

        @Override
        public void add(long index, boolean k2) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean add(boolean k2) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean removeBoolean(long i2) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean set(long index, boolean k2) {
            throw new UnsupportedOperationException();
        }

        @Override
        public long indexOf(boolean k2) {
            return -1L;
        }

        @Override
        public long lastIndexOf(boolean k2) {
            return -1L;
        }

        @Override
        public boolean addAll(Collection<? extends Boolean> c2) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(long i2, Collection<? extends Boolean> c2) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean removeAll(Collection<?> c2) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Boolean get(long i2) {
            throw new IndexOutOfBoundsException();
        }

        @Override
        public boolean addAll(BooleanCollection c2) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(BooleanBigList c2) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(long i2, BooleanCollection c2) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(long i2, BooleanBigList c2) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void add(long index, Boolean k2) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean add(Boolean k2) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Boolean set(long index, Boolean k2) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean getBoolean(long i2) {
            throw new IndexOutOfBoundsException();
        }

        @Override
        public Boolean remove(long k2) {
            throw new UnsupportedOperationException();
        }

        @Override
        public long indexOf(Object k2) {
            return -1L;
        }

        @Override
        public long lastIndexOf(Object k2) {
            return -1L;
        }

        @Override
        public BooleanBigListIterator listIterator() {
            return BooleanBigListIterators.EMPTY_BIG_LIST_ITERATOR;
        }

        @Override
        public BooleanBigListIterator iterator() {
            return BooleanBigListIterators.EMPTY_BIG_LIST_ITERATOR;
        }

        @Override
        public BooleanBigListIterator listIterator(long i2) {
            if (i2 == 0L) {
                return BooleanBigListIterators.EMPTY_BIG_LIST_ITERATOR;
            }
            throw new IndexOutOfBoundsException(String.valueOf(i2));
        }

        @Override
        public BooleanBigList subList(long from, long to2) {
            if (from == 0L && to2 == 0L) {
                return this;
            }
            throw new IndexOutOfBoundsException();
        }

        @Override
        public void getElements(long from, boolean[][] a2, long offset, long length) {
            BooleanBigArrays.ensureOffsetLength(a2, offset, length);
            if (from != 0L) {
                throw new IndexOutOfBoundsException();
            }
        }

        @Override
        public void removeElements(long from, long to2) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void addElements(long index, boolean[][] a2, long offset, long length) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void addElements(long index, boolean[][] a2) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void size(long s2) {
            throw new UnsupportedOperationException();
        }

        @Override
        public long size64() {
            return 0L;
        }

        @Override
        public int compareTo(BigList<? extends Boolean> o2) {
            if (o2 == this) {
                return 0;
            }
            return o2.isEmpty() ? 0 : -1;
        }

        private Object readResolve() {
            return EMPTY_BIG_LIST;
        }

        public Object clone() {
            return EMPTY_BIG_LIST;
        }

        @Override
        public int hashCode() {
            return 1;
        }

        @Override
        public boolean equals(Object o2) {
            return o2 instanceof BigList && ((BigList)o2).isEmpty();
        }

        @Override
        public String toString() {
            return "[]";
        }
    }
}

