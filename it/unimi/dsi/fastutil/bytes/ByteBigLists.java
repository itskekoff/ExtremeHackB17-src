package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.BigList;
import it.unimi.dsi.fastutil.bytes.AbstractByteBigList;
import it.unimi.dsi.fastutil.bytes.ByteBigArrays;
import it.unimi.dsi.fastutil.bytes.ByteBigList;
import it.unimi.dsi.fastutil.bytes.ByteBigListIterator;
import it.unimi.dsi.fastutil.bytes.ByteBigListIterators;
import it.unimi.dsi.fastutil.bytes.ByteCollection;
import it.unimi.dsi.fastutil.bytes.ByteCollections;
import it.unimi.dsi.fastutil.bytes.ByteList;
import java.io.Serializable;
import java.util.Collection;
import java.util.Random;

public class ByteBigLists {
    public static final EmptyBigList EMPTY_BIG_LIST = new EmptyBigList();

    private ByteBigLists() {
    }

    public static ByteBigList shuffle(ByteBigList l2, Random random) {
        long i2 = l2.size64();
        while (i2-- != 0L) {
            long p2 = (random.nextLong() & Long.MAX_VALUE) % (i2 + 1L);
            byte t2 = l2.getByte(i2);
            l2.set(i2, l2.getByte(p2));
            l2.set(p2, t2);
        }
        return l2;
    }

    public static ByteBigList singleton(byte element) {
        return new Singleton(element);
    }

    public static ByteBigList singleton(Object element) {
        return new Singleton((Byte)element);
    }

    public static ByteBigList synchronize(ByteBigList l2) {
        return new SynchronizedBigList(l2);
    }

    public static ByteBigList synchronize(ByteBigList l2, Object sync) {
        return new SynchronizedBigList(l2, sync);
    }

    public static ByteBigList unmodifiable(ByteBigList l2) {
        return new UnmodifiableBigList(l2);
    }

    public static ByteBigList asBigList(ByteList list) {
        return new ListBigList(list);
    }

    public static class ListBigList
    extends AbstractByteBigList
    implements Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        private final ByteList list;

        protected ListBigList(ByteList list) {
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
        public ByteBigListIterator iterator() {
            return ByteBigListIterators.asBigListIterator(this.list.iterator());
        }

        @Override
        public ByteBigListIterator listIterator() {
            return ByteBigListIterators.asBigListIterator(this.list.listIterator());
        }

        @Override
        public boolean addAll(long index, Collection<? extends Byte> c2) {
            return this.list.addAll(this.intIndex(index), c2);
        }

        @Override
        public ByteBigListIterator listIterator(long index) {
            return ByteBigListIterators.asBigListIterator(this.list.listIterator(this.intIndex(index)));
        }

        @Override
        public ByteBigList subList(long from, long to2) {
            return new ListBigList(this.list.subList(this.intIndex(from), this.intIndex(to2)));
        }

        @Override
        public boolean contains(byte key) {
            return this.list.contains(key);
        }

        @Override
        public byte[] toByteArray() {
            return this.list.toByteArray();
        }

        @Override
        public void removeElements(long from, long to2) {
            this.list.removeElements(this.intIndex(from), this.intIndex(to2));
        }

        @Override
        public byte[] toByteArray(byte[] a2) {
            return this.list.toByteArray(a2);
        }

        @Override
        public void add(long index, byte key) {
            this.list.add(this.intIndex(index), key);
        }

        @Override
        public boolean addAll(long index, ByteCollection c2) {
            return this.list.addAll(this.intIndex(index), c2);
        }

        @Override
        public boolean addAll(long index, ByteBigList c2) {
            return this.list.addAll(this.intIndex(index), c2);
        }

        @Override
        public boolean add(byte key) {
            return this.list.add(key);
        }

        @Override
        public boolean addAll(ByteBigList c2) {
            return this.list.addAll(c2);
        }

        @Override
        public byte getByte(long index) {
            return this.list.getByte(this.intIndex(index));
        }

        @Override
        public long indexOf(byte k2) {
            return this.list.indexOf(k2);
        }

        @Override
        public long lastIndexOf(byte k2) {
            return this.list.lastIndexOf(k2);
        }

        @Override
        public byte removeByte(long index) {
            return this.list.removeByte(this.intIndex(index));
        }

        @Override
        public byte set(long index, byte k2) {
            return this.list.set(this.intIndex(index), k2);
        }

        @Override
        public boolean addAll(ByteCollection c2) {
            return this.list.addAll(c2);
        }

        @Override
        public boolean containsAll(ByteCollection c2) {
            return this.list.containsAll(c2);
        }

        @Override
        public boolean removeAll(ByteCollection c2) {
            return this.list.removeAll(c2);
        }

        @Override
        public boolean retainAll(ByteCollection c2) {
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
        public boolean addAll(Collection<? extends Byte> c2) {
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
    extends ByteCollections.UnmodifiableCollection
    implements ByteBigList,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final ByteBigList list;

        protected UnmodifiableBigList(ByteBigList l2) {
            super(l2);
            this.list = l2;
        }

        @Override
        public byte getByte(long i2) {
            return this.list.getByte(i2);
        }

        @Override
        public byte set(long i2, byte k2) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void add(long i2, byte k2) {
            throw new UnsupportedOperationException();
        }

        @Override
        public byte removeByte(long i2) {
            throw new UnsupportedOperationException();
        }

        @Override
        public long indexOf(byte k2) {
            return this.list.indexOf(k2);
        }

        @Override
        public long lastIndexOf(byte k2) {
            return this.list.lastIndexOf(k2);
        }

        @Override
        public boolean addAll(long index, Collection<? extends Byte> c2) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void getElements(long from, byte[][] a2, long offset, long length) {
            this.list.getElements(from, a2, offset, length);
        }

        @Override
        public void removeElements(long from, long to2) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void addElements(long index, byte[][] a2, long offset, long length) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void addElements(long index, byte[][] a2) {
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
        public ByteBigListIterator iterator() {
            return this.listIterator();
        }

        @Override
        public ByteBigListIterator listIterator() {
            return ByteBigListIterators.unmodifiable(this.list.listIterator());
        }

        @Override
        public ByteBigListIterator listIterator(long i2) {
            return ByteBigListIterators.unmodifiable(this.list.listIterator(i2));
        }

        @Override
        public ByteBigList subList(long from, long to2) {
            return ByteBigLists.unmodifiable(this.list.subList(from, to2));
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
        public int compareTo(BigList<? extends Byte> o2) {
            return this.list.compareTo(o2);
        }

        @Override
        public boolean addAll(long index, ByteCollection c2) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(ByteBigList l2) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(long index, ByteBigList l2) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Byte get(long i2) {
            return (Byte)this.list.get(i2);
        }

        @Override
        public void add(long i2, Byte k2) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Byte set(long index, Byte k2) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Byte remove(long i2) {
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
    extends ByteCollections.SynchronizedCollection
    implements ByteBigList,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final ByteBigList list;

        protected SynchronizedBigList(ByteBigList l2, Object sync) {
            super(l2, sync);
            this.list = l2;
        }

        protected SynchronizedBigList(ByteBigList l2) {
            super(l2);
            this.list = l2;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public byte getByte(long i2) {
            Object object = this.sync;
            synchronized (object) {
                return this.list.getByte(i2);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public byte set(long i2, byte k2) {
            Object object = this.sync;
            synchronized (object) {
                return this.list.set(i2, k2);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void add(long i2, byte k2) {
            Object object = this.sync;
            synchronized (object) {
                this.list.add(i2, k2);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public byte removeByte(long i2) {
            Object object = this.sync;
            synchronized (object) {
                return this.list.removeByte(i2);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public long indexOf(byte k2) {
            Object object = this.sync;
            synchronized (object) {
                return this.list.indexOf(k2);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public long lastIndexOf(byte k2) {
            Object object = this.sync;
            synchronized (object) {
                return this.list.lastIndexOf(k2);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean addAll(long index, Collection<? extends Byte> c2) {
            Object object = this.sync;
            synchronized (object) {
                return this.list.addAll(index, c2);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void getElements(long from, byte[][] a2, long offset, long length) {
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
        public void addElements(long index, byte[][] a2, long offset, long length) {
            Object object = this.sync;
            synchronized (object) {
                this.list.addElements(index, a2, offset, length);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void addElements(long index, byte[][] a2) {
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
        public ByteBigListIterator iterator() {
            return this.list.listIterator();
        }

        @Override
        public ByteBigListIterator listIterator() {
            return this.list.listIterator();
        }

        @Override
        public ByteBigListIterator listIterator(long i2) {
            return this.list.listIterator(i2);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public ByteBigList subList(long from, long to2) {
            Object object = this.sync;
            synchronized (object) {
                return ByteBigLists.synchronize(this.list.subList(from, to2), this.sync);
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
        public int compareTo(BigList<? extends Byte> o2) {
            Object object = this.sync;
            synchronized (object) {
                return this.list.compareTo(o2);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean addAll(long index, ByteCollection c2) {
            Object object = this.sync;
            synchronized (object) {
                return this.list.addAll(index, c2);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean addAll(long index, ByteBigList l2) {
            Object object = this.sync;
            synchronized (object) {
                return this.list.addAll(index, l2);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean addAll(ByteBigList l2) {
            Object object = this.sync;
            synchronized (object) {
                return this.list.addAll(l2);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public Byte get(long i2) {
            Object object = this.sync;
            synchronized (object) {
                return (Byte)this.list.get(i2);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void add(long i2, Byte k2) {
            Object object = this.sync;
            synchronized (object) {
                this.list.add(i2, k2);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public Byte set(long index, Byte k2) {
            Object object = this.sync;
            synchronized (object) {
                return this.list.set(index, k2);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public Byte remove(long i2) {
            Object object = this.sync;
            synchronized (object) {
                return (Byte)this.list.remove(i2);
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
    extends AbstractByteBigList
    implements Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;
        private final byte element;

        private Singleton(byte element) {
            this.element = element;
        }

        @Override
        public byte getByte(long i2) {
            if (i2 == 0L) {
                return this.element;
            }
            throw new IndexOutOfBoundsException();
        }

        @Override
        public byte removeByte(long i2) {
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
        public boolean addAll(long i2, Collection<? extends Byte> c2) {
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
        public ByteBigListIterator listIterator() {
            return ByteBigListIterators.singleton(this.element);
        }

        @Override
        public ByteBigListIterator iterator() {
            return this.listIterator();
        }

        @Override
        public ByteBigListIterator listIterator(long i2) {
            if (i2 > 1L || i2 < 0L) {
                throw new IndexOutOfBoundsException();
            }
            ByteBigListIterator l2 = this.listIterator();
            if (i2 == 1L) {
                l2.next();
            }
            return l2;
        }

        @Override
        public ByteBigList subList(long from, long to2) {
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
        public boolean rem(byte k2) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(ByteCollection c2) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(long i2, ByteCollection c2) {
            throw new UnsupportedOperationException();
        }
    }

    public static class EmptyBigList
    extends ByteCollections.EmptyCollection
    implements ByteBigList,
    Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;

        protected EmptyBigList() {
        }

        @Override
        public void add(long index, byte k2) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean add(byte k2) {
            throw new UnsupportedOperationException();
        }

        @Override
        public byte removeByte(long i2) {
            throw new UnsupportedOperationException();
        }

        @Override
        public byte set(long index, byte k2) {
            throw new UnsupportedOperationException();
        }

        @Override
        public long indexOf(byte k2) {
            return -1L;
        }

        @Override
        public long lastIndexOf(byte k2) {
            return -1L;
        }

        @Override
        public boolean addAll(Collection<? extends Byte> c2) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(long i2, Collection<? extends Byte> c2) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean removeAll(Collection<?> c2) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Byte get(long i2) {
            throw new IndexOutOfBoundsException();
        }

        @Override
        public boolean addAll(ByteCollection c2) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(ByteBigList c2) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(long i2, ByteCollection c2) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(long i2, ByteBigList c2) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void add(long index, Byte k2) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean add(Byte k2) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Byte set(long index, Byte k2) {
            throw new UnsupportedOperationException();
        }

        @Override
        public byte getByte(long i2) {
            throw new IndexOutOfBoundsException();
        }

        @Override
        public Byte remove(long k2) {
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
        public ByteBigListIterator listIterator() {
            return ByteBigListIterators.EMPTY_BIG_LIST_ITERATOR;
        }

        @Override
        public ByteBigListIterator iterator() {
            return ByteBigListIterators.EMPTY_BIG_LIST_ITERATOR;
        }

        @Override
        public ByteBigListIterator listIterator(long i2) {
            if (i2 == 0L) {
                return ByteBigListIterators.EMPTY_BIG_LIST_ITERATOR;
            }
            throw new IndexOutOfBoundsException(String.valueOf(i2));
        }

        @Override
        public ByteBigList subList(long from, long to2) {
            if (from == 0L && to2 == 0L) {
                return this;
            }
            throw new IndexOutOfBoundsException();
        }

        @Override
        public void getElements(long from, byte[][] a2, long offset, long length) {
            ByteBigArrays.ensureOffsetLength(a2, offset, length);
            if (from != 0L) {
                throw new IndexOutOfBoundsException();
            }
        }

        @Override
        public void removeElements(long from, long to2) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void addElements(long index, byte[][] a2, long offset, long length) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void addElements(long index, byte[][] a2) {
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
        public int compareTo(BigList<? extends Byte> o2) {
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

