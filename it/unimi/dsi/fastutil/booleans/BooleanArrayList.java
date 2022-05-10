package it.unimi.dsi.fastutil.booleans;

import it.unimi.dsi.fastutil.Arrays;
import it.unimi.dsi.fastutil.booleans.AbstractBooleanList;
import it.unimi.dsi.fastutil.booleans.AbstractBooleanListIterator;
import it.unimi.dsi.fastutil.booleans.BooleanArrays;
import it.unimi.dsi.fastutil.booleans.BooleanCollection;
import it.unimi.dsi.fastutil.booleans.BooleanIterator;
import it.unimi.dsi.fastutil.booleans.BooleanIterators;
import it.unimi.dsi.fastutil.booleans.BooleanList;
import it.unimi.dsi.fastutil.booleans.BooleanListIterator;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.RandomAccess;

public class BooleanArrayList
extends AbstractBooleanList
implements RandomAccess,
Cloneable,
Serializable {
    private static final long serialVersionUID = -7046029254386353130L;
    public static final int DEFAULT_INITIAL_CAPACITY = 16;
    protected transient boolean[] a;
    protected int size;
    private static final boolean ASSERTS = false;

    protected BooleanArrayList(boolean[] a2, boolean dummy) {
        this.a = a2;
    }

    public BooleanArrayList(int capacity) {
        if (capacity < 0) {
            throw new IllegalArgumentException("Initial capacity (" + capacity + ") is negative");
        }
        this.a = new boolean[capacity];
    }

    public BooleanArrayList() {
        this(16);
    }

    public BooleanArrayList(Collection<? extends Boolean> c2) {
        this(c2.size());
        this.size = BooleanIterators.unwrap(BooleanIterators.asBooleanIterator(c2.iterator()), this.a);
    }

    public BooleanArrayList(BooleanCollection c2) {
        this(c2.size());
        this.size = BooleanIterators.unwrap(c2.iterator(), this.a);
    }

    public BooleanArrayList(BooleanList l2) {
        this(l2.size());
        this.size = l2.size();
        l2.getElements(0, this.a, 0, this.size);
    }

    public BooleanArrayList(boolean[] a2) {
        this(a2, 0, a2.length);
    }

    public BooleanArrayList(boolean[] a2, int offset, int length) {
        this(length);
        System.arraycopy(a2, offset, this.a, 0, length);
        this.size = length;
    }

    public BooleanArrayList(Iterator<? extends Boolean> i2) {
        this();
        while (i2.hasNext()) {
            this.add(i2.next());
        }
    }

    public BooleanArrayList(BooleanIterator i2) {
        this();
        while (i2.hasNext()) {
            this.add(i2.nextBoolean());
        }
    }

    public boolean[] elements() {
        return this.a;
    }

    public static BooleanArrayList wrap(boolean[] a2, int length) {
        if (length > a2.length) {
            throw new IllegalArgumentException("The specified length (" + length + ") is greater than the array size (" + a2.length + ")");
        }
        BooleanArrayList l2 = new BooleanArrayList(a2, false);
        l2.size = length;
        return l2;
    }

    public static BooleanArrayList wrap(boolean[] a2) {
        return BooleanArrayList.wrap(a2, a2.length);
    }

    public void ensureCapacity(int capacity) {
        this.a = BooleanArrays.ensureCapacity(this.a, capacity, this.size);
    }

    private void grow(int capacity) {
        this.a = BooleanArrays.grow(this.a, capacity, this.size);
    }

    @Override
    public void add(int index, boolean k2) {
        this.ensureIndex(index);
        this.grow(this.size + 1);
        if (index != this.size) {
            System.arraycopy(this.a, index, this.a, index + 1, this.size - index);
        }
        this.a[index] = k2;
        ++this.size;
    }

    @Override
    public boolean add(boolean k2) {
        this.grow(this.size + 1);
        this.a[this.size++] = k2;
        return true;
    }

    @Override
    public boolean getBoolean(int index) {
        if (index >= this.size) {
            throw new IndexOutOfBoundsException("Index (" + index + ") is greater than or equal to list size (" + this.size + ")");
        }
        return this.a[index];
    }

    @Override
    public int indexOf(boolean k2) {
        for (int i2 = 0; i2 < this.size; ++i2) {
            if (k2 != this.a[i2]) continue;
            return i2;
        }
        return -1;
    }

    @Override
    public int lastIndexOf(boolean k2) {
        int i2 = this.size;
        while (i2-- != 0) {
            if (k2 != this.a[i2]) continue;
            return i2;
        }
        return -1;
    }

    @Override
    public boolean removeBoolean(int index) {
        if (index >= this.size) {
            throw new IndexOutOfBoundsException("Index (" + index + ") is greater than or equal to list size (" + this.size + ")");
        }
        boolean old = this.a[index];
        --this.size;
        if (index != this.size) {
            System.arraycopy(this.a, index + 1, this.a, index, this.size - index);
        }
        return old;
    }

    @Override
    public boolean rem(boolean k2) {
        int index = this.indexOf(k2);
        if (index == -1) {
            return false;
        }
        this.removeBoolean(index);
        return true;
    }

    @Override
    public boolean set(int index, boolean k2) {
        if (index >= this.size) {
            throw new IndexOutOfBoundsException("Index (" + index + ") is greater than or equal to list size (" + this.size + ")");
        }
        boolean old = this.a[index];
        this.a[index] = k2;
        return old;
    }

    @Override
    public void clear() {
        this.size = 0;
    }

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public void size(int size) {
        if (size > this.a.length) {
            this.ensureCapacity(size);
        }
        if (size > this.size) {
            java.util.Arrays.fill(this.a, this.size, size, false);
        }
        this.size = size;
    }

    @Override
    public boolean isEmpty() {
        return this.size == 0;
    }

    public void trim() {
        this.trim(0);
    }

    public void trim(int n2) {
        if (n2 >= this.a.length || this.size == this.a.length) {
            return;
        }
        boolean[] t2 = new boolean[Math.max(n2, this.size)];
        System.arraycopy(this.a, 0, t2, 0, this.size);
        this.a = t2;
    }

    @Override
    public void getElements(int from, boolean[] a2, int offset, int length) {
        BooleanArrays.ensureOffsetLength(a2, offset, length);
        System.arraycopy(this.a, from, a2, offset, length);
    }

    @Override
    public void removeElements(int from, int to2) {
        Arrays.ensureFromTo(this.size, from, to2);
        System.arraycopy(this.a, to2, this.a, from, this.size - to2);
        this.size -= to2 - from;
    }

    @Override
    public void addElements(int index, boolean[] a2, int offset, int length) {
        this.ensureIndex(index);
        BooleanArrays.ensureOffsetLength(a2, offset, length);
        this.grow(this.size + length);
        System.arraycopy(this.a, index, this.a, index + length, this.size - index);
        System.arraycopy(a2, offset, this.a, index, length);
        this.size += length;
    }

    @Override
    public boolean[] toBooleanArray(boolean[] a2) {
        if (a2 == null || a2.length < this.size) {
            a2 = new boolean[this.size];
        }
        System.arraycopy(this.a, 0, a2, 0, this.size);
        return a2;
    }

    @Override
    public boolean addAll(int index, BooleanCollection c2) {
        this.ensureIndex(index);
        int n2 = c2.size();
        if (n2 == 0) {
            return false;
        }
        this.grow(this.size + n2);
        if (index != this.size) {
            System.arraycopy(this.a, index, this.a, index + n2, this.size - index);
        }
        BooleanIterator i2 = c2.iterator();
        this.size += n2;
        while (n2-- != 0) {
            this.a[index++] = i2.nextBoolean();
        }
        return true;
    }

    @Override
    public boolean addAll(int index, BooleanList l2) {
        this.ensureIndex(index);
        int n2 = l2.size();
        if (n2 == 0) {
            return false;
        }
        this.grow(this.size + n2);
        if (index != this.size) {
            System.arraycopy(this.a, index, this.a, index + n2, this.size - index);
        }
        l2.getElements(0, this.a, index, n2);
        this.size += n2;
        return true;
    }

    @Override
    public boolean removeAll(BooleanCollection c2) {
        boolean[] a2 = this.a;
        int j2 = 0;
        for (int i2 = 0; i2 < this.size; ++i2) {
            if (c2.contains(a2[i2])) continue;
            a2[j2++] = a2[i2];
        }
        boolean modified = this.size != j2;
        this.size = j2;
        return modified;
    }

    @Override
    public boolean removeAll(Collection<?> c2) {
        boolean[] a2 = this.a;
        int j2 = 0;
        for (int i2 = 0; i2 < this.size; ++i2) {
            if (c2.contains(a2[i2])) continue;
            a2[j2++] = a2[i2];
        }
        boolean modified = this.size != j2;
        this.size = j2;
        return modified;
    }

    @Override
    public BooleanListIterator listIterator(final int index) {
        this.ensureIndex(index);
        return new AbstractBooleanListIterator(){
            int pos;
            int last;
            {
                this.pos = index;
                this.last = -1;
            }

            @Override
            public boolean hasNext() {
                return this.pos < BooleanArrayList.this.size;
            }

            @Override
            public boolean hasPrevious() {
                return this.pos > 0;
            }

            @Override
            public boolean nextBoolean() {
                if (!this.hasNext()) {
                    throw new NoSuchElementException();
                }
                this.last = this.pos++;
                return BooleanArrayList.this.a[this.last];
            }

            @Override
            public boolean previousBoolean() {
                if (!this.hasPrevious()) {
                    throw new NoSuchElementException();
                }
                this.last = --this.pos;
                return BooleanArrayList.this.a[this.pos];
            }

            @Override
            public int nextIndex() {
                return this.pos;
            }

            @Override
            public int previousIndex() {
                return this.pos - 1;
            }

            @Override
            public void add(boolean k2) {
                BooleanArrayList.this.add(this.pos++, k2);
                this.last = -1;
            }

            @Override
            public void set(boolean k2) {
                if (this.last == -1) {
                    throw new IllegalStateException();
                }
                BooleanArrayList.this.set(this.last, k2);
            }

            @Override
            public void remove() {
                if (this.last == -1) {
                    throw new IllegalStateException();
                }
                BooleanArrayList.this.removeBoolean(this.last);
                if (this.last < this.pos) {
                    --this.pos;
                }
                this.last = -1;
            }
        };
    }

    public BooleanArrayList clone() {
        BooleanArrayList c2 = new BooleanArrayList(this.size);
        System.arraycopy(this.a, 0, c2.a, 0, this.size);
        c2.size = this.size;
        return c2;
    }

    public boolean equals(BooleanArrayList l2) {
        if (l2 == this) {
            return true;
        }
        int s2 = this.size();
        if (s2 != l2.size()) {
            return false;
        }
        boolean[] a1 = this.a;
        boolean[] a2 = l2.a;
        while (s2-- != 0) {
            if (a1[s2] == a2[s2]) continue;
            return false;
        }
        return true;
    }

    @Override
    public int compareTo(BooleanArrayList l2) {
        int i2;
        int s1 = this.size();
        int s2 = l2.size();
        boolean[] a1 = this.a;
        boolean[] a2 = l2.a;
        for (i2 = 0; i2 < s1 && i2 < s2; ++i2) {
            boolean e1 = a1[i2];
            boolean e2 = a2[i2];
            int r2 = Boolean.compare(e1, e2);
            if (r2 == 0) continue;
            return r2;
        }
        return i2 < s2 ? -1 : (i2 < s1 ? 1 : 0);
    }

    private void writeObject(ObjectOutputStream s2) throws IOException {
        s2.defaultWriteObject();
        for (int i2 = 0; i2 < this.size; ++i2) {
            s2.writeBoolean(this.a[i2]);
        }
    }

    private void readObject(ObjectInputStream s2) throws IOException, ClassNotFoundException {
        s2.defaultReadObject();
        this.a = new boolean[this.size];
        for (int i2 = 0; i2 < this.size; ++i2) {
            this.a[i2] = s2.readBoolean();
        }
    }
}

