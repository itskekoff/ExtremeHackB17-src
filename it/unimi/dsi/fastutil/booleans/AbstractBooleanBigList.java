package it.unimi.dsi.fastutil.booleans;

import it.unimi.dsi.fastutil.BigList;
import it.unimi.dsi.fastutil.BigListIterator;
import it.unimi.dsi.fastutil.booleans.AbstractBooleanBigListIterator;
import it.unimi.dsi.fastutil.booleans.AbstractBooleanCollection;
import it.unimi.dsi.fastutil.booleans.BooleanBigArrays;
import it.unimi.dsi.fastutil.booleans.BooleanBigList;
import it.unimi.dsi.fastutil.booleans.BooleanBigListIterator;
import it.unimi.dsi.fastutil.booleans.BooleanCollection;
import it.unimi.dsi.fastutil.booleans.BooleanList;
import it.unimi.dsi.fastutil.booleans.BooleanStack;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

public abstract class AbstractBooleanBigList
extends AbstractBooleanCollection
implements BooleanBigList,
BooleanStack {
    protected AbstractBooleanBigList() {
    }

    protected void ensureIndex(long index) {
        if (index < 0L) {
            throw new IndexOutOfBoundsException("Index (" + index + ") is negative");
        }
        if (index > this.size64()) {
            throw new IndexOutOfBoundsException("Index (" + index + ") is greater than list size (" + this.size64() + ")");
        }
    }

    protected void ensureRestrictedIndex(long index) {
        if (index < 0L) {
            throw new IndexOutOfBoundsException("Index (" + index + ") is negative");
        }
        if (index >= this.size64()) {
            throw new IndexOutOfBoundsException("Index (" + index + ") is greater than or equal to list size (" + this.size64() + ")");
        }
    }

    @Override
    public void add(long index, boolean k2) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean add(boolean k2) {
        this.add(this.size64(), k2);
        return true;
    }

    @Override
    public boolean removeBoolean(long i2) {
        throw new UnsupportedOperationException();
    }

    public boolean removeBoolean(int i2) {
        return this.removeBoolean((long)i2);
    }

    @Override
    public boolean set(long index, boolean k2) {
        throw new UnsupportedOperationException();
    }

    public boolean set(int index, boolean k2) {
        return this.set((long)index, k2);
    }

    @Override
    public boolean addAll(long index, Collection<? extends Boolean> c2) {
        this.ensureIndex(index);
        int n2 = c2.size();
        if (n2 == 0) {
            return false;
        }
        Iterator<? extends Boolean> i2 = c2.iterator();
        while (n2-- != 0) {
            this.add(index++, i2.next());
        }
        return true;
    }

    public boolean addAll(int index, Collection<? extends Boolean> c2) {
        return this.addAll((long)index, c2);
    }

    @Override
    public boolean addAll(Collection<? extends Boolean> c2) {
        return this.addAll(this.size64(), c2);
    }

    @Override
    public BooleanBigListIterator iterator() {
        return this.listIterator();
    }

    @Override
    public BooleanBigListIterator listIterator() {
        return this.listIterator(0L);
    }

    @Override
    public BooleanBigListIterator listIterator(final long index) {
        this.ensureIndex(index);
        return new AbstractBooleanBigListIterator(){
            long pos;
            long last;
            {
                this.pos = index;
                this.last = -1L;
            }

            @Override
            public boolean hasNext() {
                return this.pos < AbstractBooleanBigList.this.size64();
            }

            @Override
            public boolean hasPrevious() {
                return this.pos > 0L;
            }

            @Override
            public boolean nextBoolean() {
                if (!this.hasNext()) {
                    throw new NoSuchElementException();
                }
                this.last = this.pos++;
                return AbstractBooleanBigList.this.getBoolean(this.last);
            }

            @Override
            public boolean previousBoolean() {
                if (!this.hasPrevious()) {
                    throw new NoSuchElementException();
                }
                this.last = --this.pos;
                return AbstractBooleanBigList.this.getBoolean(this.pos);
            }

            @Override
            public long nextIndex() {
                return this.pos;
            }

            @Override
            public long previousIndex() {
                return this.pos - 1L;
            }

            @Override
            public void add(boolean k2) {
                AbstractBooleanBigList.this.add(this.pos++, k2);
                this.last = -1L;
            }

            @Override
            public void set(boolean k2) {
                if (this.last == -1L) {
                    throw new IllegalStateException();
                }
                AbstractBooleanBigList.this.set(this.last, k2);
            }

            @Override
            public void remove() {
                if (this.last == -1L) {
                    throw new IllegalStateException();
                }
                AbstractBooleanBigList.this.removeBoolean(this.last);
                if (this.last < this.pos) {
                    --this.pos;
                }
                this.last = -1L;
            }
        };
    }

    public BooleanBigListIterator listIterator(int index) {
        return this.listIterator((long)index);
    }

    @Override
    public boolean contains(boolean k2) {
        return this.indexOf(k2) >= 0L;
    }

    @Override
    public long indexOf(boolean k2) {
        BooleanBigListIterator i2 = this.listIterator();
        while (i2.hasNext()) {
            boolean e2 = i2.nextBoolean();
            if (k2 != e2) continue;
            return i2.previousIndex();
        }
        return -1L;
    }

    @Override
    public long lastIndexOf(boolean k2) {
        BooleanBigListIterator i2 = this.listIterator(this.size64());
        while (i2.hasPrevious()) {
            boolean e2 = i2.previousBoolean();
            if (k2 != e2) continue;
            return i2.nextIndex();
        }
        return -1L;
    }

    @Override
    public void size(long size) {
        long i2 = this.size64();
        if (size > i2) {
            while (i2++ < size) {
                this.add(false);
            }
        } else {
            while (i2-- != size) {
                this.remove(i2);
            }
        }
    }

    public void size(int size) {
        this.size((long)size);
    }

    @Override
    public BooleanBigList subList(long from, long to2) {
        this.ensureIndex(from);
        this.ensureIndex(to2);
        if (from > to2) {
            throw new IndexOutOfBoundsException("Start index (" + from + ") is greater than end index (" + to2 + ")");
        }
        return new BooleanSubList(this, from, to2);
    }

    @Override
    public void removeElements(long from, long to2) {
        this.ensureIndex(to2);
        BooleanBigListIterator i2 = this.listIterator(from);
        long n2 = to2 - from;
        if (n2 < 0L) {
            throw new IllegalArgumentException("Start index (" + from + ") is greater than end index (" + to2 + ")");
        }
        while (n2-- != 0L) {
            i2.nextBoolean();
            i2.remove();
        }
    }

    @Override
    public void addElements(long index, boolean[][] a2, long offset, long length) {
        this.ensureIndex(index);
        BooleanBigArrays.ensureOffsetLength(a2, offset, length);
        while (length-- != 0L) {
            this.add(index++, BooleanBigArrays.get(a2, offset++));
        }
    }

    @Override
    public void addElements(long index, boolean[][] a2) {
        this.addElements(index, a2, 0L, BooleanBigArrays.length(a2));
    }

    @Override
    public void getElements(long from, boolean[][] a2, long offset, long length) {
        BooleanBigListIterator i2 = this.listIterator(from);
        BooleanBigArrays.ensureOffsetLength(a2, offset, length);
        if (from + length > this.size64()) {
            throw new IndexOutOfBoundsException("End index (" + (from + length) + ") is greater than list size (" + this.size64() + ")");
        }
        while (length-- != 0L) {
            BooleanBigArrays.set(a2, offset++, i2.nextBoolean());
        }
    }

    @Override
    @Deprecated
    public int size() {
        return (int)Math.min(Integer.MAX_VALUE, this.size64());
    }

    private boolean valEquals(Object a2, Object b2) {
        return a2 == null ? b2 == null : a2.equals(b2);
    }

    @Override
    public boolean equals(Object o2) {
        if (o2 == this) {
            return true;
        }
        if (!(o2 instanceof BigList)) {
            return false;
        }
        BigList l2 = (BigList)o2;
        long s2 = this.size64();
        if (s2 != l2.size64()) {
            return false;
        }
        if (l2 instanceof BooleanBigList) {
            BooleanBigListIterator i1 = this.listIterator();
            BooleanBigListIterator i2 = ((BooleanBigList)l2).listIterator();
            while (s2-- != 0L) {
                if (i1.nextBoolean() == i2.nextBoolean()) continue;
                return false;
            }
            return true;
        }
        BooleanBigListIterator i1 = this.listIterator();
        BigListIterator i2 = l2.listIterator();
        while (s2-- != 0L) {
            if (this.valEquals(i1.next(), i2.next())) continue;
            return false;
        }
        return true;
    }

    @Override
    public int compareTo(BigList<? extends Boolean> l2) {
        if (l2 == this) {
            return 0;
        }
        if (l2 instanceof BooleanBigList) {
            BooleanBigListIterator i1 = this.listIterator();
            BooleanBigListIterator i2 = ((BooleanBigList)l2).listIterator();
            while (i1.hasNext() && i2.hasNext()) {
                boolean e2;
                boolean e1 = i1.nextBoolean();
                int r2 = Boolean.compare(e1, e2 = i2.nextBoolean());
                if (r2 == 0) continue;
                return r2;
            }
            return i2.hasNext() ? -1 : (i1.hasNext() ? 1 : 0);
        }
        BooleanBigListIterator i1 = this.listIterator();
        BigListIterator<? extends Boolean> i2 = l2.listIterator();
        while (i1.hasNext() && i2.hasNext()) {
            int r3 = ((Comparable)i1.next()).compareTo(i2.next());
            if (r3 == 0) continue;
            return r3;
        }
        return i2.hasNext() ? -1 : (i1.hasNext() ? 1 : 0);
    }

    @Override
    public int hashCode() {
        BooleanBigListIterator i2 = this.iterator();
        int h2 = 1;
        long s2 = this.size64();
        while (s2-- != 0L) {
            boolean k2 = i2.nextBoolean();
            h2 = 31 * h2 + (k2 ? 1231 : 1237);
        }
        return h2;
    }

    @Override
    public void push(boolean o2) {
        this.add(o2);
    }

    @Override
    public boolean popBoolean() {
        if (this.isEmpty()) {
            throw new NoSuchElementException();
        }
        return this.removeBoolean(this.size64() - 1L);
    }

    @Override
    public boolean topBoolean() {
        if (this.isEmpty()) {
            throw new NoSuchElementException();
        }
        return this.getBoolean(this.size64() - 1L);
    }

    @Override
    public boolean peekBoolean(int i2) {
        return this.getBoolean(this.size64() - 1L - (long)i2);
    }

    public boolean getBoolean(int index) {
        return this.getBoolean((long)index);
    }

    @Override
    public boolean rem(boolean k2) {
        long index = this.indexOf(k2);
        if (index == -1L) {
            return false;
        }
        this.removeBoolean(index);
        return true;
    }

    @Override
    public boolean addAll(long index, BooleanCollection c2) {
        return this.addAll(index, (Collection<? extends Boolean>)c2);
    }

    @Override
    public boolean addAll(long index, BooleanBigList l2) {
        return this.addAll(index, (BooleanCollection)l2);
    }

    @Override
    public boolean addAll(BooleanCollection c2) {
        return this.addAll(this.size64(), c2);
    }

    @Override
    public boolean addAll(BooleanBigList l2) {
        return this.addAll(this.size64(), l2);
    }

    @Override
    @Deprecated
    public void add(long index, Boolean ok2) {
        this.add(index, (boolean)ok2);
    }

    @Override
    @Deprecated
    public Boolean set(long index, Boolean ok2) {
        return this.set(index, (boolean)ok2);
    }

    @Override
    @Deprecated
    public Boolean get(long index) {
        return this.getBoolean(index);
    }

    @Override
    @Deprecated
    public long indexOf(Object ok2) {
        return this.indexOf((Boolean)ok2);
    }

    @Override
    @Deprecated
    public long lastIndexOf(Object ok2) {
        return this.lastIndexOf((Boolean)ok2);
    }

    @Deprecated
    public Boolean remove(int index) {
        return this.removeBoolean(index);
    }

    @Override
    @Deprecated
    public Boolean remove(long index) {
        return this.removeBoolean(index);
    }

    @Override
    @Deprecated
    public void push(Boolean o2) {
        this.push((boolean)o2);
    }

    @Override
    @Deprecated
    public Boolean pop() {
        return this.popBoolean();
    }

    @Override
    @Deprecated
    public Boolean top() {
        return this.topBoolean();
    }

    @Override
    @Deprecated
    public Boolean peek(int i2) {
        return this.peekBoolean(i2);
    }

    @Override
    public String toString() {
        StringBuilder s2 = new StringBuilder();
        BooleanBigListIterator i2 = this.iterator();
        long n2 = this.size64();
        boolean first = true;
        s2.append("[");
        while (n2-- != 0L) {
            if (first) {
                first = false;
            } else {
                s2.append(", ");
            }
            boolean k2 = i2.nextBoolean();
            s2.append(String.valueOf(k2));
        }
        s2.append("]");
        return s2.toString();
    }

    public static class BooleanSubList
    extends AbstractBooleanBigList
    implements Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final BooleanBigList l;
        protected final long from;
        protected long to;
        private static final boolean ASSERTS = false;

        public BooleanSubList(BooleanBigList l2, long from, long to2) {
            this.l = l2;
            this.from = from;
            this.to = to2;
        }

        private void assertRange() {
        }

        @Override
        public boolean add(boolean k2) {
            this.l.add(this.to, k2);
            ++this.to;
            return true;
        }

        @Override
        public void add(long index, boolean k2) {
            this.ensureIndex(index);
            this.l.add(this.from + index, k2);
            ++this.to;
        }

        @Override
        public boolean addAll(long index, Collection<? extends Boolean> c2) {
            this.ensureIndex(index);
            this.to += (long)c2.size();
            return this.l.addAll(this.from + index, c2);
        }

        @Override
        public boolean getBoolean(long index) {
            this.ensureRestrictedIndex(index);
            return this.l.getBoolean(this.from + index);
        }

        @Override
        public boolean removeBoolean(long index) {
            this.ensureRestrictedIndex(index);
            --this.to;
            return this.l.removeBoolean(this.from + index);
        }

        @Override
        public boolean set(long index, boolean k2) {
            this.ensureRestrictedIndex(index);
            return this.l.set(this.from + index, k2);
        }

        @Override
        public void clear() {
            this.removeElements(0L, this.size64());
        }

        @Override
        public long size64() {
            return this.to - this.from;
        }

        @Override
        public void getElements(long from, boolean[][] a2, long offset, long length) {
            this.ensureIndex(from);
            if (from + length > this.size64()) {
                throw new IndexOutOfBoundsException("End index (" + from + length + ") is greater than list size (" + this.size64() + ")");
            }
            this.l.getElements(this.from + from, a2, offset, length);
        }

        @Override
        public void removeElements(long from, long to2) {
            this.ensureIndex(from);
            this.ensureIndex(to2);
            this.l.removeElements(this.from + from, this.from + to2);
            this.to -= to2 - from;
        }

        @Override
        public void addElements(long index, boolean[][] a2, long offset, long length) {
            this.ensureIndex(index);
            this.l.addElements(this.from + index, a2, offset, length);
            this.to += length;
        }

        @Override
        public BooleanBigListIterator listIterator(final long index) {
            this.ensureIndex(index);
            return new AbstractBooleanBigListIterator(){
                long pos;
                long last;
                {
                    this.pos = index;
                    this.last = -1L;
                }

                @Override
                public boolean hasNext() {
                    return this.pos < BooleanSubList.this.size64();
                }

                @Override
                public boolean hasPrevious() {
                    return this.pos > 0L;
                }

                @Override
                public boolean nextBoolean() {
                    if (!this.hasNext()) {
                        throw new NoSuchElementException();
                    }
                    this.last = this.pos++;
                    return BooleanSubList.this.l.getBoolean(BooleanSubList.this.from + this.last);
                }

                @Override
                public boolean previousBoolean() {
                    if (!this.hasPrevious()) {
                        throw new NoSuchElementException();
                    }
                    this.last = --this.pos;
                    return BooleanSubList.this.l.getBoolean(BooleanSubList.this.from + this.pos);
                }

                @Override
                public long nextIndex() {
                    return this.pos;
                }

                @Override
                public long previousIndex() {
                    return this.pos - 1L;
                }

                @Override
                public void add(boolean k2) {
                    if (this.last == -1L) {
                        throw new IllegalStateException();
                    }
                    BooleanSubList.this.add(this.pos++, k2);
                    this.last = -1L;
                }

                @Override
                public void set(boolean k2) {
                    if (this.last == -1L) {
                        throw new IllegalStateException();
                    }
                    BooleanSubList.this.set(this.last, k2);
                }

                @Override
                public void remove() {
                    if (this.last == -1L) {
                        throw new IllegalStateException();
                    }
                    BooleanSubList.this.removeBoolean(this.last);
                    if (this.last < this.pos) {
                        --this.pos;
                    }
                    this.last = -1L;
                }
            };
        }

        @Override
        public BooleanBigList subList(long from, long to2) {
            this.ensureIndex(from);
            this.ensureIndex(to2);
            if (from > to2) {
                throw new IllegalArgumentException("Start index (" + from + ") is greater than end index (" + to2 + ")");
            }
            return new BooleanSubList(this, from, to2);
        }

        @Override
        public boolean rem(boolean k2) {
            long index = this.indexOf(k2);
            if (index == -1L) {
                return false;
            }
            --this.to;
            this.l.removeBoolean(this.from + index);
            return true;
        }

        @Override
        public boolean remove(Object o2) {
            return this.rem((Boolean)o2);
        }

        @Override
        public boolean addAll(long index, BooleanCollection c2) {
            this.ensureIndex(index);
            this.to += (long)c2.size();
            return this.l.addAll(this.from + index, c2);
        }

        public boolean addAll(long index, BooleanList l2) {
            this.ensureIndex(index);
            this.to += (long)l2.size();
            return this.l.addAll(this.from + index, l2);
        }
    }
}

