package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.BigArrays;
import it.unimi.dsi.fastutil.bytes.AbstractByteBigList;
import it.unimi.dsi.fastutil.bytes.AbstractByteBigListIterator;
import it.unimi.dsi.fastutil.bytes.ByteBigArrays;
import it.unimi.dsi.fastutil.bytes.ByteBigList;
import it.unimi.dsi.fastutil.bytes.ByteBigListIterator;
import it.unimi.dsi.fastutil.bytes.ByteCollection;
import it.unimi.dsi.fastutil.bytes.ByteIterator;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.RandomAccess;

public class ByteBigArrayBigList
extends AbstractByteBigList
implements RandomAccess,
Cloneable,
Serializable {
    private static final long serialVersionUID = -7046029254386353130L;
    public static final int DEFAULT_INITIAL_CAPACITY = 16;
    protected transient byte[][] a;
    protected long size;
    private static final boolean ASSERTS = false;

    protected ByteBigArrayBigList(byte[][] a2, boolean dummy) {
        this.a = a2;
    }

    public ByteBigArrayBigList(long capacity) {
        if (capacity < 0L) {
            throw new IllegalArgumentException("Initial capacity (" + capacity + ") is negative");
        }
        this.a = ByteBigArrays.newBigArray(capacity);
    }

    public ByteBigArrayBigList() {
        this(16L);
    }

    public ByteBigArrayBigList(ByteCollection c2) {
        this(c2.size());
        ByteIterator i2 = c2.iterator();
        while (i2.hasNext()) {
            this.add(i2.nextByte());
        }
    }

    public ByteBigArrayBigList(ByteBigList l2) {
        this(l2.size64());
        this.size = l2.size64();
        l2.getElements(0L, this.a, 0L, this.size);
    }

    public ByteBigArrayBigList(byte[][] a2) {
        this(a2, 0L, ByteBigArrays.length(a2));
    }

    public ByteBigArrayBigList(byte[][] a2, long offset, long length) {
        this(length);
        ByteBigArrays.copy(a2, offset, this.a, 0L, length);
        this.size = length;
    }

    public ByteBigArrayBigList(Iterator<? extends Byte> i2) {
        this();
        while (i2.hasNext()) {
            this.add(i2.next());
        }
    }

    public ByteBigArrayBigList(ByteIterator i2) {
        this();
        while (i2.hasNext()) {
            this.add(i2.nextByte());
        }
    }

    public byte[][] elements() {
        return this.a;
    }

    public static ByteBigArrayBigList wrap(byte[][] a2, long length) {
        if (length > ByteBigArrays.length(a2)) {
            throw new IllegalArgumentException("The specified length (" + length + ") is greater than the array size (" + ByteBigArrays.length(a2) + ")");
        }
        ByteBigArrayBigList l2 = new ByteBigArrayBigList(a2, false);
        l2.size = length;
        return l2;
    }

    public static ByteBigArrayBigList wrap(byte[][] a2) {
        return ByteBigArrayBigList.wrap(a2, ByteBigArrays.length(a2));
    }

    public void ensureCapacity(long capacity) {
        this.a = ByteBigArrays.ensureCapacity(this.a, capacity, this.size);
    }

    private void grow(long capacity) {
        this.a = ByteBigArrays.grow(this.a, capacity, this.size);
    }

    @Override
    public void add(long index, byte k2) {
        this.ensureIndex(index);
        this.grow(this.size + 1L);
        if (index != this.size) {
            ByteBigArrays.copy(this.a, index, this.a, index + 1L, this.size - index);
        }
        ByteBigArrays.set(this.a, index, k2);
        ++this.size;
    }

    @Override
    public boolean add(byte k2) {
        this.grow(this.size + 1L);
        ByteBigArrays.set(this.a, this.size++, k2);
        return true;
    }

    @Override
    public byte getByte(long index) {
        if (index >= this.size) {
            throw new IndexOutOfBoundsException("Index (" + index + ") is greater than or equal to list size (" + this.size + ")");
        }
        return ByteBigArrays.get(this.a, index);
    }

    @Override
    public long indexOf(byte k2) {
        for (long i2 = 0L; i2 < this.size; ++i2) {
            if (k2 != ByteBigArrays.get(this.a, i2)) continue;
            return i2;
        }
        return -1L;
    }

    @Override
    public long lastIndexOf(byte k2) {
        long i2 = this.size;
        while (i2-- != 0L) {
            if (k2 != ByteBigArrays.get(this.a, i2)) continue;
            return i2;
        }
        return -1L;
    }

    @Override
    public byte removeByte(long index) {
        if (index >= this.size) {
            throw new IndexOutOfBoundsException("Index (" + index + ") is greater than or equal to list size (" + this.size + ")");
        }
        byte old = ByteBigArrays.get(this.a, index);
        --this.size;
        if (index != this.size) {
            ByteBigArrays.copy(this.a, index + 1L, this.a, index, this.size - index);
        }
        return old;
    }

    @Override
    public boolean rem(byte k2) {
        long index = this.indexOf(k2);
        if (index == -1L) {
            return false;
        }
        this.removeByte(index);
        return true;
    }

    @Override
    public byte set(long index, byte k2) {
        if (index >= this.size) {
            throw new IndexOutOfBoundsException("Index (" + index + ") is greater than or equal to list size (" + this.size + ")");
        }
        byte old = ByteBigArrays.get(this.a, index);
        ByteBigArrays.set(this.a, index, k2);
        return old;
    }

    @Override
    public boolean removeAll(ByteCollection c2) {
        byte[] s2 = null;
        byte[] d2 = null;
        int ss2 = -1;
        int sd2 = 0x8000000;
        int ds2 = -1;
        int dd2 = 0x8000000;
        for (long i2 = 0L; i2 < this.size; ++i2) {
            if (sd2 == 0x8000000) {
                sd2 = 0;
                s2 = this.a[++ss2];
            }
            if (!c2.contains((byte)s2[sd2])) {
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
        byte[] s2 = null;
        byte[] d2 = null;
        int ss2 = -1;
        int sd2 = 0x8000000;
        int ds2 = -1;
        int dd2 = 0x8000000;
        for (long i2 = 0L; i2 < this.size; ++i2) {
            if (sd2 == 0x8000000) {
                sd2 = 0;
                s2 = this.a[++ss2];
            }
            if (!c2.contains((byte)s2[sd2])) {
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
        if (size > ByteBigArrays.length(this.a)) {
            this.ensureCapacity(size);
        }
        if (size > this.size) {
            ByteBigArrays.fill(this.a, this.size, size, (byte)0);
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
        long arrayLength = ByteBigArrays.length(this.a);
        if (n2 >= arrayLength || this.size == arrayLength) {
            return;
        }
        this.a = ByteBigArrays.trim(this.a, Math.max(n2, this.size));
    }

    public void getElements(int from, byte[][] a2, long offset, long length) {
        ByteBigArrays.copy(this.a, from, a2, offset, length);
    }

    public void removeElements(int from, int to2) {
        BigArrays.ensureFromTo(this.size, from, to2);
        ByteBigArrays.copy(this.a, to2, this.a, from, this.size - (long)to2);
        this.size -= (long)(to2 - from);
    }

    public void addElements(int index, byte[][] a2, long offset, long length) {
        this.ensureIndex(index);
        ByteBigArrays.ensureOffsetLength(a2, offset, length);
        this.grow(this.size + length);
        ByteBigArrays.copy(this.a, index, this.a, (long)index + length, this.size - (long)index);
        ByteBigArrays.copy(a2, offset, this.a, index, length);
        this.size += length;
    }

    @Override
    public ByteBigListIterator listIterator(final long index) {
        this.ensureIndex(index);
        return new AbstractByteBigListIterator(){
            long pos;
            long last;
            {
                this.pos = index;
                this.last = -1L;
            }

            @Override
            public boolean hasNext() {
                return this.pos < ByteBigArrayBigList.this.size;
            }

            @Override
            public boolean hasPrevious() {
                return this.pos > 0L;
            }

            @Override
            public byte nextByte() {
                if (!this.hasNext()) {
                    throw new NoSuchElementException();
                }
                this.last = this.pos++;
                return ByteBigArrays.get(ByteBigArrayBigList.this.a, this.last);
            }

            @Override
            public byte previousByte() {
                if (!this.hasPrevious()) {
                    throw new NoSuchElementException();
                }
                this.last = --this.pos;
                return ByteBigArrays.get(ByteBigArrayBigList.this.a, this.pos);
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
            public void add(byte k2) {
                ByteBigArrayBigList.this.add(this.pos++, k2);
                this.last = -1L;
            }

            @Override
            public void set(byte k2) {
                if (this.last == -1L) {
                    throw new IllegalStateException();
                }
                ByteBigArrayBigList.this.set(this.last, k2);
            }

            @Override
            public void remove() {
                if (this.last == -1L) {
                    throw new IllegalStateException();
                }
                ByteBigArrayBigList.this.removeByte(this.last);
                if (this.last < this.pos) {
                    --this.pos;
                }
                this.last = -1L;
            }
        };
    }

    public ByteBigArrayBigList clone() {
        ByteBigArrayBigList c2 = new ByteBigArrayBigList(this.size);
        ByteBigArrays.copy(this.a, 0L, c2.a, 0L, this.size);
        c2.size = this.size;
        return c2;
    }

    public boolean equals(ByteBigArrayBigList l2) {
        if (l2 == this) {
            return true;
        }
        long s2 = this.size64();
        if (s2 != l2.size64()) {
            return false;
        }
        byte[][] a1 = this.a;
        byte[][] a2 = l2.a;
        while (s2-- != 0L) {
            if (ByteBigArrays.get(a1, s2) == ByteBigArrays.get(a2, s2)) continue;
            return false;
        }
        return true;
    }

    @Override
    public int compareTo(ByteBigArrayBigList l2) {
        long s1 = this.size64();
        long s2 = l2.size64();
        byte[][] a1 = this.a;
        byte[][] a2 = l2.a;
        int i2 = 0;
        while ((long)i2 < s1 && (long)i2 < s2) {
            byte e2;
            byte e1 = ByteBigArrays.get(a1, i2);
            int r2 = Byte.compare(e1, e2 = ByteBigArrays.get(a2, i2));
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
            s2.writeByte(ByteBigArrays.get(this.a, i2));
            ++i2;
        }
    }

    private void readObject(ObjectInputStream s2) throws IOException, ClassNotFoundException {
        s2.defaultReadObject();
        this.a = ByteBigArrays.newBigArray(this.size);
        int i2 = 0;
        while ((long)i2 < this.size) {
            ByteBigArrays.set(this.a, i2, s2.readByte());
            ++i2;
        }
    }
}

