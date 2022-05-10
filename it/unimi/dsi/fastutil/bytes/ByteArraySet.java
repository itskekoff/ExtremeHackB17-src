package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.bytes.AbstractByteIterator;
import it.unimi.dsi.fastutil.bytes.AbstractByteSet;
import it.unimi.dsi.fastutil.bytes.ByteArrays;
import it.unimi.dsi.fastutil.bytes.ByteCollection;
import it.unimi.dsi.fastutil.bytes.ByteIterator;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.NoSuchElementException;

public class ByteArraySet
extends AbstractByteSet
implements Serializable,
Cloneable {
    private static final long serialVersionUID = 1L;
    private transient byte[] a;
    private int size;

    public ByteArraySet(byte[] a2) {
        this.a = a2;
        this.size = a2.length;
    }

    public ByteArraySet() {
        this.a = ByteArrays.EMPTY_ARRAY;
    }

    public ByteArraySet(int capacity) {
        this.a = new byte[capacity];
    }

    public ByteArraySet(ByteCollection c2) {
        this(c2.size());
        this.addAll(c2);
    }

    public ByteArraySet(Collection<? extends Byte> c2) {
        this(c2.size());
        this.addAll(c2);
    }

    public ByteArraySet(byte[] a2, int size) {
        this.a = a2;
        this.size = size;
        if (size > a2.length) {
            throw new IllegalArgumentException("The provided size (" + size + ") is larger than or equal to the array size (" + a2.length + ")");
        }
    }

    private int findKey(byte o2) {
        int i2 = this.size;
        while (i2-- != 0) {
            if (this.a[i2] != o2) continue;
            return i2;
        }
        return -1;
    }

    @Override
    public ByteIterator iterator() {
        return new AbstractByteIterator(){
            int next = 0;

            @Override
            public boolean hasNext() {
                return this.next < ByteArraySet.this.size;
            }

            @Override
            public byte nextByte() {
                if (!this.hasNext()) {
                    throw new NoSuchElementException();
                }
                return ByteArraySet.this.a[this.next++];
            }

            @Override
            public void remove() {
                int tail = ByteArraySet.this.size-- - this.next--;
                System.arraycopy(ByteArraySet.this.a, this.next + 1, ByteArraySet.this.a, this.next, tail);
            }
        };
    }

    @Override
    public boolean contains(byte k2) {
        return this.findKey(k2) != -1;
    }

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public boolean rem(byte k2) {
        int pos = this.findKey(k2);
        if (pos == -1) {
            return false;
        }
        int tail = this.size - pos - 1;
        for (int i2 = 0; i2 < tail; ++i2) {
            this.a[pos + i2] = this.a[pos + i2 + 1];
        }
        --this.size;
        return true;
    }

    @Override
    public boolean add(byte k2) {
        int pos = this.findKey(k2);
        if (pos != -1) {
            return false;
        }
        if (this.size == this.a.length) {
            byte[] b2 = new byte[this.size == 0 ? 2 : this.size * 2];
            int i2 = this.size;
            while (i2-- != 0) {
                b2[i2] = this.a[i2];
            }
            this.a = b2;
        }
        this.a[this.size++] = k2;
        return true;
    }

    @Override
    public void clear() {
        this.size = 0;
    }

    @Override
    public boolean isEmpty() {
        return this.size == 0;
    }

    public ByteArraySet clone() {
        ByteArraySet c2;
        try {
            c2 = (ByteArraySet)super.clone();
        }
        catch (CloneNotSupportedException cantHappen) {
            throw new InternalError();
        }
        c2.a = (byte[])this.a.clone();
        return c2;
    }

    private void writeObject(ObjectOutputStream s2) throws IOException {
        s2.defaultWriteObject();
        for (int i2 = 0; i2 < this.size; ++i2) {
            s2.writeByte(this.a[i2]);
        }
    }

    private void readObject(ObjectInputStream s2) throws IOException, ClassNotFoundException {
        s2.defaultReadObject();
        this.a = new byte[this.size];
        for (int i2 = 0; i2 < this.size; ++i2) {
            this.a[i2] = s2.readByte();
        }
    }
}

