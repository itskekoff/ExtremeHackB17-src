package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.bytes.AbstractByteList;
import it.unimi.dsi.fastutil.bytes.ByteCollection;
import it.unimi.dsi.fastutil.bytes.ByteCollections;
import it.unimi.dsi.fastutil.bytes.ByteIterator;
import it.unimi.dsi.fastutil.bytes.ByteIterators;
import it.unimi.dsi.fastutil.bytes.ByteList;
import it.unimi.dsi.fastutil.bytes.ByteListIterator;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Random;

public class ByteLists {
    public static final EmptyList EMPTY_LIST = new EmptyList();

    private ByteLists() {
    }

    public static ByteList shuffle(ByteList l2, Random random) {
        int i2 = l2.size();
        while (i2-- != 0) {
            int p2 = random.nextInt(i2 + 1);
            byte t2 = l2.getByte(i2);
            l2.set(i2, l2.getByte(p2));
            l2.set(p2, t2);
        }
        return l2;
    }

    public static ByteList singleton(byte element) {
        return new Singleton(element);
    }

    public static ByteList singleton(Object element) {
        return new Singleton((Byte)element);
    }

    public static ByteList synchronize(ByteList l2) {
        return new SynchronizedList(l2);
    }

    public static ByteList synchronize(ByteList l2, Object sync) {
        return new SynchronizedList(l2, sync);
    }

    public static ByteList unmodifiable(ByteList l2) {
        return new UnmodifiableList(l2);
    }

    public static class UnmodifiableList
    extends ByteCollections.UnmodifiableCollection
    implements ByteList,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final ByteList list;

        protected UnmodifiableList(ByteList l2) {
            super(l2);
            this.list = l2;
        }

        @Override
        public byte getByte(int i2) {
            return this.list.getByte(i2);
        }

        @Override
        public byte set(int i2, byte k2) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void add(int i2, byte k2) {
            throw new UnsupportedOperationException();
        }

        @Override
        public byte removeByte(int i2) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int indexOf(byte k2) {
            return this.list.indexOf(k2);
        }

        @Override
        public int lastIndexOf(byte k2) {
            return this.list.lastIndexOf(k2);
        }

        @Override
        public boolean addAll(int index, Collection<? extends Byte> c2) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void getElements(int from, byte[] a2, int offset, int length) {
            this.list.getElements(from, a2, offset, length);
        }

        @Override
        public void removeElements(int from, int to2) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void addElements(int index, byte[] a2, int offset, int length) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void addElements(int index, byte[] a2) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void size(int size) {
            this.list.size(size);
        }

        @Override
        public ByteListIterator iterator() {
            return this.listIterator();
        }

        @Override
        public ByteListIterator listIterator() {
            return ByteIterators.unmodifiable(this.list.listIterator());
        }

        @Override
        public ByteListIterator listIterator(int i2) {
            return ByteIterators.unmodifiable(this.list.listIterator(i2));
        }

        @Override
        @Deprecated
        public ByteListIterator byteListIterator() {
            return this.listIterator();
        }

        @Override
        @Deprecated
        public ByteListIterator byteListIterator(int i2) {
            return this.listIterator(i2);
        }

        @Override
        public ByteList subList(int from, int to2) {
            return ByteLists.unmodifiable(this.list.subList(from, to2));
        }

        @Override
        @Deprecated
        public ByteList byteSubList(int from, int to2) {
            return this.subList(from, to2);
        }

        @Override
        public boolean equals(Object o2) {
            return this.collection.equals(o2);
        }

        @Override
        public int hashCode() {
            return this.collection.hashCode();
        }

        @Override
        public int compareTo(List<? extends Byte> o2) {
            return this.list.compareTo(o2);
        }

        @Override
        public boolean addAll(int index, ByteCollection c2) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(ByteList l2) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(int index, ByteList l2) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Byte get(int i2) {
            return (Byte)this.list.get(i2);
        }

        @Override
        public void add(int i2, Byte k2) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Byte set(int index, Byte k2) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Byte remove(int i2) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int indexOf(Object o2) {
            return this.list.indexOf(o2);
        }

        @Override
        public int lastIndexOf(Object o2) {
            return this.list.lastIndexOf(o2);
        }
    }

    public static class SynchronizedList
    extends ByteCollections.SynchronizedCollection
    implements ByteList,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final ByteList list;

        protected SynchronizedList(ByteList l2, Object sync) {
            super(l2, sync);
            this.list = l2;
        }

        protected SynchronizedList(ByteList l2) {
            super(l2);
            this.list = l2;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public byte getByte(int i2) {
            Object object = this.sync;
            synchronized (object) {
                return this.list.getByte(i2);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public byte set(int i2, byte k2) {
            Object object = this.sync;
            synchronized (object) {
                return this.list.set(i2, k2);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void add(int i2, byte k2) {
            Object object = this.sync;
            synchronized (object) {
                this.list.add(i2, k2);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public byte removeByte(int i2) {
            Object object = this.sync;
            synchronized (object) {
                return this.list.removeByte(i2);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public int indexOf(byte k2) {
            Object object = this.sync;
            synchronized (object) {
                return this.list.indexOf(k2);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public int lastIndexOf(byte k2) {
            Object object = this.sync;
            synchronized (object) {
                return this.list.lastIndexOf(k2);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean addAll(int index, Collection<? extends Byte> c2) {
            Object object = this.sync;
            synchronized (object) {
                return this.list.addAll(index, c2);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void getElements(int from, byte[] a2, int offset, int length) {
            Object object = this.sync;
            synchronized (object) {
                this.list.getElements(from, a2, offset, length);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void removeElements(int from, int to2) {
            Object object = this.sync;
            synchronized (object) {
                this.list.removeElements(from, to2);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void addElements(int index, byte[] a2, int offset, int length) {
            Object object = this.sync;
            synchronized (object) {
                this.list.addElements(index, a2, offset, length);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void addElements(int index, byte[] a2) {
            Object object = this.sync;
            synchronized (object) {
                this.list.addElements(index, a2);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void size(int size) {
            Object object = this.sync;
            synchronized (object) {
                this.list.size(size);
            }
        }

        @Override
        public ByteListIterator iterator() {
            return this.list.listIterator();
        }

        @Override
        public ByteListIterator listIterator() {
            return this.list.listIterator();
        }

        @Override
        public ByteListIterator listIterator(int i2) {
            return this.list.listIterator(i2);
        }

        @Override
        @Deprecated
        public ByteListIterator byteListIterator() {
            return this.listIterator();
        }

        @Override
        @Deprecated
        public ByteListIterator byteListIterator(int i2) {
            return this.listIterator(i2);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public ByteList subList(int from, int to2) {
            Object object = this.sync;
            synchronized (object) {
                return ByteLists.synchronize(this.list.subList(from, to2), this.sync);
            }
        }

        @Override
        @Deprecated
        public ByteList byteSubList(int from, int to2) {
            return this.subList(from, to2);
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

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public int compareTo(List<? extends Byte> o2) {
            Object object = this.sync;
            synchronized (object) {
                return this.list.compareTo(o2);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean addAll(int index, ByteCollection c2) {
            Object object = this.sync;
            synchronized (object) {
                return this.list.addAll(index, c2);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean addAll(int index, ByteList l2) {
            Object object = this.sync;
            synchronized (object) {
                return this.list.addAll(index, l2);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean addAll(ByteList l2) {
            Object object = this.sync;
            synchronized (object) {
                return this.list.addAll(l2);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public Byte get(int i2) {
            Object object = this.sync;
            synchronized (object) {
                return (Byte)this.list.get(i2);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void add(int i2, Byte k2) {
            Object object = this.sync;
            synchronized (object) {
                this.list.add(i2, k2);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public Byte set(int index, Byte k2) {
            Object object = this.sync;
            synchronized (object) {
                return this.list.set(index, k2);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public Byte remove(int i2) {
            Object object = this.sync;
            synchronized (object) {
                return (Byte)this.list.remove(i2);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public int indexOf(Object o2) {
            Object object = this.sync;
            synchronized (object) {
                return this.list.indexOf(o2);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public int lastIndexOf(Object o2) {
            Object object = this.sync;
            synchronized (object) {
                return this.list.lastIndexOf(o2);
            }
        }
    }

    public static class Singleton
    extends AbstractByteList
    implements Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;
        private final byte element;

        private Singleton(byte element) {
            this.element = element;
        }

        @Override
        public byte getByte(int i2) {
            if (i2 == 0) {
                return this.element;
            }
            throw new IndexOutOfBoundsException();
        }

        @Override
        public byte removeByte(int i2) {
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
        public boolean addAll(int i2, Collection<? extends Byte> c2) {
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
        public ByteListIterator listIterator() {
            return ByteIterators.singleton(this.element);
        }

        @Override
        public ByteListIterator iterator() {
            return this.listIterator();
        }

        @Override
        public ByteListIterator listIterator(int i2) {
            if (i2 > 1 || i2 < 0) {
                throw new IndexOutOfBoundsException();
            }
            ByteListIterator l2 = this.listIterator();
            if (i2 == 1) {
                l2.next();
            }
            return l2;
        }

        @Override
        public ByteList subList(int from, int to2) {
            this.ensureIndex(from);
            this.ensureIndex(to2);
            if (from > to2) {
                throw new IndexOutOfBoundsException("Start index (" + from + ") is greater than end index (" + to2 + ")");
            }
            if (from != 0 || to2 != 1) {
                return EMPTY_LIST;
            }
            return this;
        }

        @Override
        public int size() {
            return 1;
        }

        @Override
        public void size(int size) {
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
        public boolean addAll(int i2, ByteCollection c2) {
            throw new UnsupportedOperationException();
        }
    }

    public static class EmptyList
    extends ByteCollections.EmptyCollection
    implements ByteList,
    Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;

        protected EmptyList() {
        }

        @Override
        public void add(int index, byte k2) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean add(byte k2) {
            throw new UnsupportedOperationException();
        }

        @Override
        public byte removeByte(int i2) {
            throw new UnsupportedOperationException();
        }

        @Override
        public byte set(int index, byte k2) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int indexOf(byte k2) {
            return -1;
        }

        @Override
        public int lastIndexOf(byte k2) {
            return -1;
        }

        @Override
        public boolean addAll(Collection<? extends Byte> c2) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(int i2, Collection<? extends Byte> c2) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean removeAll(Collection<?> c2) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Byte get(int i2) {
            throw new IndexOutOfBoundsException();
        }

        @Override
        public boolean addAll(ByteCollection c2) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(ByteList c2) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(int i2, ByteCollection c2) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(int i2, ByteList c2) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void add(int index, Byte k2) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean add(Byte k2) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Byte set(int index, Byte k2) {
            throw new UnsupportedOperationException();
        }

        @Override
        public byte getByte(int i2) {
            throw new IndexOutOfBoundsException();
        }

        @Override
        public Byte remove(int k2) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int indexOf(Object k2) {
            return -1;
        }

        @Override
        public int lastIndexOf(Object k2) {
            return -1;
        }

        @Override
        @Deprecated
        public ByteIterator byteIterator() {
            return ByteIterators.EMPTY_ITERATOR;
        }

        @Override
        public ByteListIterator listIterator() {
            return ByteIterators.EMPTY_ITERATOR;
        }

        @Override
        public ByteListIterator iterator() {
            return ByteIterators.EMPTY_ITERATOR;
        }

        @Override
        public ByteListIterator listIterator(int i2) {
            if (i2 == 0) {
                return ByteIterators.EMPTY_ITERATOR;
            }
            throw new IndexOutOfBoundsException(String.valueOf(i2));
        }

        @Override
        @Deprecated
        public ByteListIterator byteListIterator() {
            return this.listIterator();
        }

        @Override
        @Deprecated
        public ByteListIterator byteListIterator(int i2) {
            return this.listIterator(i2);
        }

        @Override
        public ByteList subList(int from, int to2) {
            if (from == 0 && to2 == 0) {
                return this;
            }
            throw new IndexOutOfBoundsException();
        }

        @Override
        @Deprecated
        public ByteList byteSubList(int from, int to2) {
            return this.subList(from, to2);
        }

        @Override
        public void getElements(int from, byte[] a2, int offset, int length) {
            if (from == 0 && length == 0 && offset >= 0 && offset <= a2.length) {
                return;
            }
            throw new IndexOutOfBoundsException();
        }

        @Override
        public void removeElements(int from, int to2) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void addElements(int index, byte[] a2, int offset, int length) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void addElements(int index, byte[] a2) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void size(int s2) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int compareTo(List<? extends Byte> o2) {
            if (o2 == this) {
                return 0;
            }
            return o2.isEmpty() ? 0 : -1;
        }

        private Object readResolve() {
            return EMPTY_LIST;
        }

        public Object clone() {
            return EMPTY_LIST;
        }

        @Override
        public int hashCode() {
            return 1;
        }

        @Override
        public boolean equals(Object o2) {
            return o2 instanceof List && ((List)o2).isEmpty();
        }

        @Override
        public String toString() {
            return "[]";
        }
    }
}

