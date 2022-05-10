package it.unimi.dsi.fastutil.booleans;

import it.unimi.dsi.fastutil.booleans.AbstractBooleanList;
import it.unimi.dsi.fastutil.booleans.BooleanCollection;
import it.unimi.dsi.fastutil.booleans.BooleanCollections;
import it.unimi.dsi.fastutil.booleans.BooleanIterator;
import it.unimi.dsi.fastutil.booleans.BooleanIterators;
import it.unimi.dsi.fastutil.booleans.BooleanList;
import it.unimi.dsi.fastutil.booleans.BooleanListIterator;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Random;

public class BooleanLists {
    public static final EmptyList EMPTY_LIST = new EmptyList();

    private BooleanLists() {
    }

    public static BooleanList shuffle(BooleanList l2, Random random) {
        int i2 = l2.size();
        while (i2-- != 0) {
            int p2 = random.nextInt(i2 + 1);
            boolean t2 = l2.getBoolean(i2);
            l2.set(i2, l2.getBoolean(p2));
            l2.set(p2, t2);
        }
        return l2;
    }

    public static BooleanList singleton(boolean element) {
        return new Singleton(element);
    }

    public static BooleanList singleton(Object element) {
        return new Singleton((Boolean)element);
    }

    public static BooleanList synchronize(BooleanList l2) {
        return new SynchronizedList(l2);
    }

    public static BooleanList synchronize(BooleanList l2, Object sync) {
        return new SynchronizedList(l2, sync);
    }

    public static BooleanList unmodifiable(BooleanList l2) {
        return new UnmodifiableList(l2);
    }

    public static class UnmodifiableList
    extends BooleanCollections.UnmodifiableCollection
    implements BooleanList,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final BooleanList list;

        protected UnmodifiableList(BooleanList l2) {
            super(l2);
            this.list = l2;
        }

        @Override
        public boolean getBoolean(int i2) {
            return this.list.getBoolean(i2);
        }

        @Override
        public boolean set(int i2, boolean k2) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void add(int i2, boolean k2) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean removeBoolean(int i2) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int indexOf(boolean k2) {
            return this.list.indexOf(k2);
        }

        @Override
        public int lastIndexOf(boolean k2) {
            return this.list.lastIndexOf(k2);
        }

        @Override
        public boolean addAll(int index, Collection<? extends Boolean> c2) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void getElements(int from, boolean[] a2, int offset, int length) {
            this.list.getElements(from, a2, offset, length);
        }

        @Override
        public void removeElements(int from, int to2) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void addElements(int index, boolean[] a2, int offset, int length) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void addElements(int index, boolean[] a2) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void size(int size) {
            this.list.size(size);
        }

        @Override
        public BooleanListIterator iterator() {
            return this.listIterator();
        }

        @Override
        public BooleanListIterator listIterator() {
            return BooleanIterators.unmodifiable(this.list.listIterator());
        }

        @Override
        public BooleanListIterator listIterator(int i2) {
            return BooleanIterators.unmodifiable(this.list.listIterator(i2));
        }

        @Override
        @Deprecated
        public BooleanListIterator booleanListIterator() {
            return this.listIterator();
        }

        @Override
        @Deprecated
        public BooleanListIterator booleanListIterator(int i2) {
            return this.listIterator(i2);
        }

        @Override
        public BooleanList subList(int from, int to2) {
            return BooleanLists.unmodifiable(this.list.subList(from, to2));
        }

        @Override
        @Deprecated
        public BooleanList booleanSubList(int from, int to2) {
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
        public int compareTo(List<? extends Boolean> o2) {
            return this.list.compareTo(o2);
        }

        @Override
        public boolean addAll(int index, BooleanCollection c2) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(BooleanList l2) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(int index, BooleanList l2) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Boolean get(int i2) {
            return (Boolean)this.list.get(i2);
        }

        @Override
        public void add(int i2, Boolean k2) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Boolean set(int index, Boolean k2) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Boolean remove(int i2) {
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
    extends BooleanCollections.SynchronizedCollection
    implements BooleanList,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final BooleanList list;

        protected SynchronizedList(BooleanList l2, Object sync) {
            super(l2, sync);
            this.list = l2;
        }

        protected SynchronizedList(BooleanList l2) {
            super(l2);
            this.list = l2;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean getBoolean(int i2) {
            Object object = this.sync;
            synchronized (object) {
                return this.list.getBoolean(i2);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean set(int i2, boolean k2) {
            Object object = this.sync;
            synchronized (object) {
                return this.list.set(i2, k2);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void add(int i2, boolean k2) {
            Object object = this.sync;
            synchronized (object) {
                this.list.add(i2, k2);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean removeBoolean(int i2) {
            Object object = this.sync;
            synchronized (object) {
                return this.list.removeBoolean(i2);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public int indexOf(boolean k2) {
            Object object = this.sync;
            synchronized (object) {
                return this.list.indexOf(k2);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public int lastIndexOf(boolean k2) {
            Object object = this.sync;
            synchronized (object) {
                return this.list.lastIndexOf(k2);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean addAll(int index, Collection<? extends Boolean> c2) {
            Object object = this.sync;
            synchronized (object) {
                return this.list.addAll(index, c2);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void getElements(int from, boolean[] a2, int offset, int length) {
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
        public void addElements(int index, boolean[] a2, int offset, int length) {
            Object object = this.sync;
            synchronized (object) {
                this.list.addElements(index, a2, offset, length);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void addElements(int index, boolean[] a2) {
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
        public BooleanListIterator iterator() {
            return this.list.listIterator();
        }

        @Override
        public BooleanListIterator listIterator() {
            return this.list.listIterator();
        }

        @Override
        public BooleanListIterator listIterator(int i2) {
            return this.list.listIterator(i2);
        }

        @Override
        @Deprecated
        public BooleanListIterator booleanListIterator() {
            return this.listIterator();
        }

        @Override
        @Deprecated
        public BooleanListIterator booleanListIterator(int i2) {
            return this.listIterator(i2);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public BooleanList subList(int from, int to2) {
            Object object = this.sync;
            synchronized (object) {
                return BooleanLists.synchronize(this.list.subList(from, to2), this.sync);
            }
        }

        @Override
        @Deprecated
        public BooleanList booleanSubList(int from, int to2) {
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
        public int compareTo(List<? extends Boolean> o2) {
            Object object = this.sync;
            synchronized (object) {
                return this.list.compareTo(o2);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean addAll(int index, BooleanCollection c2) {
            Object object = this.sync;
            synchronized (object) {
                return this.list.addAll(index, c2);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean addAll(int index, BooleanList l2) {
            Object object = this.sync;
            synchronized (object) {
                return this.list.addAll(index, l2);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean addAll(BooleanList l2) {
            Object object = this.sync;
            synchronized (object) {
                return this.list.addAll(l2);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public Boolean get(int i2) {
            Object object = this.sync;
            synchronized (object) {
                return (Boolean)this.list.get(i2);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void add(int i2, Boolean k2) {
            Object object = this.sync;
            synchronized (object) {
                this.list.add(i2, k2);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public Boolean set(int index, Boolean k2) {
            Object object = this.sync;
            synchronized (object) {
                return this.list.set(index, k2);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public Boolean remove(int i2) {
            Object object = this.sync;
            synchronized (object) {
                return (Boolean)this.list.remove(i2);
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
    extends AbstractBooleanList
    implements Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;
        private final boolean element;

        private Singleton(boolean element) {
            this.element = element;
        }

        @Override
        public boolean getBoolean(int i2) {
            if (i2 == 0) {
                return this.element;
            }
            throw new IndexOutOfBoundsException();
        }

        @Override
        public boolean removeBoolean(int i2) {
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
        public boolean addAll(int i2, Collection<? extends Boolean> c2) {
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
        public BooleanListIterator listIterator() {
            return BooleanIterators.singleton(this.element);
        }

        @Override
        public BooleanListIterator iterator() {
            return this.listIterator();
        }

        @Override
        public BooleanListIterator listIterator(int i2) {
            if (i2 > 1 || i2 < 0) {
                throw new IndexOutOfBoundsException();
            }
            BooleanListIterator l2 = this.listIterator();
            if (i2 == 1) {
                l2.next();
            }
            return l2;
        }

        @Override
        public BooleanList subList(int from, int to2) {
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
        public boolean rem(boolean k2) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(BooleanCollection c2) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(int i2, BooleanCollection c2) {
            throw new UnsupportedOperationException();
        }
    }

    public static class EmptyList
    extends BooleanCollections.EmptyCollection
    implements BooleanList,
    Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;

        protected EmptyList() {
        }

        @Override
        public void add(int index, boolean k2) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean add(boolean k2) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean removeBoolean(int i2) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean set(int index, boolean k2) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int indexOf(boolean k2) {
            return -1;
        }

        @Override
        public int lastIndexOf(boolean k2) {
            return -1;
        }

        @Override
        public boolean addAll(Collection<? extends Boolean> c2) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(int i2, Collection<? extends Boolean> c2) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean removeAll(Collection<?> c2) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Boolean get(int i2) {
            throw new IndexOutOfBoundsException();
        }

        @Override
        public boolean addAll(BooleanCollection c2) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(BooleanList c2) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(int i2, BooleanCollection c2) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(int i2, BooleanList c2) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void add(int index, Boolean k2) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean add(Boolean k2) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Boolean set(int index, Boolean k2) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean getBoolean(int i2) {
            throw new IndexOutOfBoundsException();
        }

        @Override
        public Boolean remove(int k2) {
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
        public BooleanIterator booleanIterator() {
            return BooleanIterators.EMPTY_ITERATOR;
        }

        @Override
        public BooleanListIterator listIterator() {
            return BooleanIterators.EMPTY_ITERATOR;
        }

        @Override
        public BooleanListIterator iterator() {
            return BooleanIterators.EMPTY_ITERATOR;
        }

        @Override
        public BooleanListIterator listIterator(int i2) {
            if (i2 == 0) {
                return BooleanIterators.EMPTY_ITERATOR;
            }
            throw new IndexOutOfBoundsException(String.valueOf(i2));
        }

        @Override
        @Deprecated
        public BooleanListIterator booleanListIterator() {
            return this.listIterator();
        }

        @Override
        @Deprecated
        public BooleanListIterator booleanListIterator(int i2) {
            return this.listIterator(i2);
        }

        @Override
        public BooleanList subList(int from, int to2) {
            if (from == 0 && to2 == 0) {
                return this;
            }
            throw new IndexOutOfBoundsException();
        }

        @Override
        @Deprecated
        public BooleanList booleanSubList(int from, int to2) {
            return this.subList(from, to2);
        }

        @Override
        public void getElements(int from, boolean[] a2, int offset, int length) {
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
        public void addElements(int index, boolean[] a2, int offset, int length) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void addElements(int index, boolean[] a2) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void size(int s2) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int compareTo(List<? extends Boolean> o2) {
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

