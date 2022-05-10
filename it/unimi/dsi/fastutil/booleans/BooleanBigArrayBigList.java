package it.unimi.dsi.fastutil.booleans;

import it.unimi.dsi.fastutil.BigArrays;
import it.unimi.dsi.fastutil.booleans.AbstractBooleanBigList;
import it.unimi.dsi.fastutil.booleans.AbstractBooleanBigListIterator;
import it.unimi.dsi.fastutil.booleans.BooleanBigArrays;
import it.unimi.dsi.fastutil.booleans.BooleanBigList;
import it.unimi.dsi.fastutil.booleans.BooleanBigListIterator;
import it.unimi.dsi.fastutil.booleans.BooleanCollection;
import it.unimi.dsi.fastutil.booleans.BooleanIterator;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.RandomAccess;

public class BooleanBigArrayBigList
extends AbstractBooleanBigList
implements RandomAccess,
Cloneable,
Serializable {
    private static final long serialVersionUID = -7046029254386353130L;
    public static final int DEFAULT_INITIAL_CAPACITY = 16;
    protected transient boolean[][] a;
    protected long size;
    private static final boolean ASSERTS = false;

    protected BooleanBigArrayBigList(boolean[][] a2, boolean dummy) {
        this.a = a2;
    }

    public BooleanBigArrayBigList(long capacity) {
        if (capacity < 0L) {
            throw new IllegalArgumentException("Initial capacity (" + capacity + ") is negative");
        }
        this.a = BooleanBigArrays.newBigArray(capacity);
    }

    public BooleanBigArrayBigList() {
        this(16L);
    }

    public BooleanBigArrayBigList(BooleanCollection c2) {
        this(c2.size());
        BooleanIterator i2 = c2.iterator();
        while (i2.hasNext()) {
            this.add(i2.nextBoolean());
        }
    }

    public BooleanBigArrayBigList(BooleanBigList l2) {
        this(l2.size64());
        this.size = l2.size64();
        l2.getElements(0L, this.a, 0L, this.size);
    }

    public BooleanBigArrayBigList(boolean[][] a2) {
        this(a2, 0L, BooleanBigArrays.length(a2));
    }

    public BooleanBigArrayBigList(boolean[][] a2, long offset, long length) {
        this(length);
        BooleanBigArrays.copy(a2, offset, this.a, 0L, length);
        this.size = length;
    }

    public BooleanBigArrayBigList(Iterator<? extends Boolean> i2) {
        this();
        while (i2.hasNext()) {
            this.add(i2.next());
        }
    }

    public BooleanBigArrayBigList(BooleanIterator i2) {
        this();
        while (i2.hasNext()) {
            this.add(i2.nextBoolean());
        }
    }

    public boolean[][] elements() {
        return this.a;
    }

    public static BooleanBigArrayBigList wrap(boolean[][] a2, long length) {
        if (length > BooleanBigArrays.length(a2)) {
            throw new IllegalArgumentException("The specified length (" + length + ") is greater than the array size (" + BooleanBigArrays.length(a2) + ")");
        }
        BooleanBigArrayBigList l2 = new BooleanBigArrayBigList(a2, false);
        l2.size = length;
        return l2;
    }

    public static BooleanBigArrayBigList wrap(boolean[][] a2) {
        return BooleanBigArrayBigList.wrap(a2, BooleanBigArrays.length(a2));
    }

    public void ensureCapacity(long capacity) {
        this.a = BooleanBigArrays.ensureCapacity(this.a, capacity, this.size);
    }

    private void grow(long capacity) {
        this.a = BooleanBigArrays.grow(this.a, capacity, this.size);
    }

    @Override
    public void add(long index, boolean k2) {
        this.ensureIndex(index);
        this.grow(this.size + 1L);
        if (index != this.size) {
            BooleanBigArrays.copy(this.a, index, this.a, index + 1L, this.size - index);
        }
        BooleanBigArrays.set(this.a, index, k2);
        ++this.size;
    }

    @Override
    public boolean add(boolean k2) {
        this.grow(this.size + 1L);
        BooleanBigArrays.set(this.a, this.size++, k2);
        return true;
    }

    @Override
    public boolean getBoolean(long index) {
        if (index >= this.size) {
            throw new IndexOutOfBoundsException("Index (" + index + ") is greater than or equal to list size (" + this.size + ")");
        }
        return BooleanBigArrays.get(this.a, index);
    }

    @Override
    public long indexOf(boolean k2) {
        for (long i2 = 0L; i2 < this.size; ++i2) {
            if (k2 != BooleanBigArrays.get(this.a, i2)) continue;
            return i2;
        }
        return -1L;
    }

    @Override
    public long lastIndexOf(boolean k2) {
        long i2 = this.size;
        while (i2-- != 0L) {
            if (k2 != BooleanBigArrays.get(this.a, i2)) continue;
            return i2;
        }
        return -1L;
    }

    @Override
    public boolean removeBoolean(long index) {
        if (index >= this.size) {
            throw new IndexOutOfBoundsException("Index (" + index + ") is greater than or equal to list size (" + this.size + ")");
        }
        boolean old = BooleanBigArrays.get(this.a, index);
        --this.size;
        if (index != this.size) {
            BooleanBigArrays.copy(this.a, index + 1L, this.a, index, this.size - index);
        }
        return old;
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
    public boolean set(long index, boolean k2) {
        if (index >= this.size) {
            throw new IndexOutOfBoundsException("Index (" + index + ") is greater than or equal to list size (" + this.size + ")");
        }
        boolean old = BooleanBigArrays.get(this.a, index);
        BooleanBigArrays.set(this.a, index, k2);
        return old;
    }

    @Override
    public boolean removeAll(BooleanCollection c2) {
        boolean[] s2 = null;
        boolean[] d2 = null;
        int ss2 = -1;
        int sd2 = 0x8000000;
        int ds2 = -1;
        int dd2 = 0x8000000;
        for (long i2 = 0L; i2 < this.size; ++i2) {
            if (sd2 == 0x8000000) {
                sd2 = 0;
                s2 = this.a[++ss2];
            }
            if (!c2.contains((boolean)s2[sd2])) {
                if (dd2 == 0x8000000) {
                    d2 = this.a[++ds2];
                    dd2 = 0;
                }
                d2[dd2++] = s2[sd2];
            }
            ++sd2;
        }
        long j2 = BigArrays.index(ds2, dd2);
        boolean modified = this.size != j2;
        this.size = j2;
        return modified;
    }

    @Override
    public boolean removeAll(Collection<?> c2) {
        boolean[] s2 = null;
        boolean[] d2 = null;
        int ss2 = -1;
        int sd2 = 0x8000000;
        int ds2 = -1;
        int dd2 = 0x8000000;
        for (long i2 = 0L; i2 < this.size; ++i2) {
            if (sd2 == 0x8000000) {
                sd2 = 0;
                s2 = this.a[++ss2];
            }
            if (!c2.contains((boolean)s2[sd2])) {
                if (dd2 == 0x8000000) {
                    d2 = this.a[++ds2];
                    dd2 = 0;
                }
                d2[dd2++] = s2[sd2];
            }
            ++sd2;
        }
        long j2 = BigArrays.index(ds2, dd2);
        boolean modified = this.size != j2;
        this.size = j2;
        return modified;
    }

    @Override
    public void clear() {
        this.size = 0L;
    }

    @Override
    public long size64() {
        return this.size;
    }

    @Override
    public void size(long size) {
        if (size > BooleanBigArrays.length(this.a)) {
            this.ensureCapacity(size);
        }
        if (size > this.size) {
            BooleanBigArrays.fill(this.a, this.size, size, false);
        }
        this.size = size;
    }

    @Override
    public boolean isEmpty() {
        return this.size == 0L;
    }

    public void trim() {
        this.trim(0L);
    }

    public void trim(long n2) {
        long arrayLength = BooleanBigArrays.length(this.a);
        if (n2 >= arrayLength || this.size == arrayLength) {
            return;
        }
        this.a = BooleanBigArrays.trim(this.a, Math.max(n2, this.size));
    }

    public void getElements(int from, boolean[][] a2, long offset, long length) {
        BooleanBigArrays.copy(this.a, from, a2, offset, length);
    }

    public void removeElements(int from, int to2) {
        BigArrays.ensureFromTo(this.size, from, to2);
        BooleanBigArrays.copy(this.a, to2, this.a, from, this.size - (long)to2);
        this.size -= (long)(to2 - from);
    }

    public void addElements(int index, boolean[][] a2, long offset, long length) {
        this.ensureIndex(index);
        BooleanBigArrays.ensureOffsetLength(a2, offset, length);
        this.grow(this.size + length);
        BooleanBigArrays.copy(this.a, index, this.a, (long)index + length, this.size - (long)index);
        BooleanBigArrays.copy(a2, offset, this.a, index, length);
        this.size += length;
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
                return this.pos < BooleanBigArrayBigList.this.size;
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
                return BooleanBigArrays.get(BooleanBigArrayBigList.this.a, this.last);
            }

            @Override
            public boolean previousBoolean() {
                if (!this.hasPrevious()) {
                    throw new NoSuchElementException();
                }
                this.last = --this.pos;
                return BooleanBigArrays.get(BooleanBigArrayBigList.this.a, this.pos);
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
                BooleanBigArrayBigList.this.add(this.pos++, k2);
                this.last = -1L;
            }

            @Override
            public void set(boolean k2) {
                if (this.last == -1L) {
                    throw new IllegalStateException();
                }
                BooleanBigArrayBigList.this.set(this.last, k2);
            }

            @Override
            public void remove() {
                if (this.last == -1L) {
                    throw new IllegalStateException();
                }
                BooleanBigArrayBigList.this.removeBoolean(this.last);
                if (this.last < this.pos) {
                    --this.pos;
                }
                this.last = -1L;
            }
        };
    }

    public BooleanBigArrayBigList clone() {
        BooleanBigArrayBigList c2 = new BooleanBigArrayBigList(this.size);
        BooleanBigArrays.copy(this.a, 0L, c2.a, 0L, this.size);
        c2.size = this.size;
        return c2;
    }

    public boolean equals(BooleanBigArrayBigList l2) {
        if (l2 == this) {
            return true;
        }
        long s2 = this.size64();
        if (s2 != l2.size64()) {
            return false;
        }
        boolean[][] a1 = this.a;
        boolean[][] a2 = l2.a;
        while (s2-- != 0L) {
            if (BooleanBigArrays.get(a1, s2) == BooleanBigArrays.get(a2, s2)) continue;
            return false;
        }
        return true;
    }

    @Override
    public int compareTo(BooleanBigArrayBigList l2) {
        long s1 = this.size64();
        long s2 = l2.size64();
        boolean[][] a1 = this.a;
        boolean[][] a2 = l2.a;
        int i2 = 0;
        while ((long)i2 < s1 && (long)i2 < s2) {
            boolean e2;
            boolean e1 = BooleanBigArrays.get(a1, i2);
            int r2 = Boolean.compare(e1, e2 = BooleanBigArrays.get(a2, i2));
            if (r2 != 0) {
                return r2;
            }
            ++i2;
        }
        return (long)i2 < s2 ? -1 : ((long)i2 < s1 ? 1 : 0);
    }

    private void writeObject(ObjectOutputStream s2) throws IOException {
        s2.defaultWriteObject();
        int i2 = 0;
        while ((long)i2 < this.size) {
            s2.writeBoolean(BooleanBigArrays.get(this.a, i2));
            ++i2;
        }
    }

    private void readObject(ObjectInputStream s2) throws IOException, ClassNotFoundException {
        s2.defaultReadObject();
        this.a = BooleanBigArrays.newBigArray(this.size);
        int i2 = 0;
        while ((long)i2 < this.size) {
            BooleanBigArrays.set(this.a, i2, s2.readBoolean());
            ++i2;
        }
    }
}

