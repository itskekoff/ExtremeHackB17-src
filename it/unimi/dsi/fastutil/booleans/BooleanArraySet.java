package it.unimi.dsi.fastutil.booleans;

import it.unimi.dsi.fastutil.booleans.AbstractBooleanIterator;
import it.unimi.dsi.fastutil.booleans.AbstractBooleanSet;
import it.unimi.dsi.fastutil.booleans.BooleanArrays;
import it.unimi.dsi.fastutil.booleans.BooleanCollection;
import it.unimi.dsi.fastutil.booleans.BooleanIterator;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.NoSuchElementException;

public class BooleanArraySet
extends AbstractBooleanSet
implements Serializable,
Cloneable {
    private static final long serialVersionUID = 1L;
    private transient boolean[] a;
    private int size;

    public BooleanArraySet(boolean[] a2) {
        this.a = a2;
        this.size = a2.length;
    }

    public BooleanArraySet() {
        this.a = BooleanArrays.EMPTY_ARRAY;
    }

    public BooleanArraySet(int capacity) {
        this.a = new boolean[capacity];
    }

    public BooleanArraySet(BooleanCollection c2) {
        this(c2.size());
        this.addAll(c2);
    }

    public BooleanArraySet(Collection<? extends Boolean> c2) {
        this(c2.size());
        this.addAll(c2);
    }

    public BooleanArraySet(boolean[] a2, int size) {
        this.a = a2;
        this.size = size;
        if (size > a2.length) {
            throw new IllegalArgumentException("The provided size (" + size + ") is larger than or equal to the array size (" + a2.length + ")");
        }
    }

    private int findKey(boolean o2) {
        int i2 = this.size;
        while (i2-- != 0) {
            if (this.a[i2] != o2) continue;
            return i2;
        }
        return -1;
    }

    @Override
    public BooleanIterator iterator() {
        return new AbstractBooleanIterator(){
            int next = 0;

            @Override
            public boolean hasNext() {
                return this.next < BooleanArraySet.this.size;
            }

            @Override
            public boolean nextBoolean() {
                if (!this.hasNext()) {
                    throw new NoSuchElementException();
                }
                return BooleanArraySet.this.a[this.next++];
            }

            @Override
            public void remove() {
                int tail = BooleanArraySet.this.size-- - this.next--;
                System.arraycopy(BooleanArraySet.this.a, this.next + 1, BooleanArraySet.this.a, this.next, tail);
            }
        };
    }

    @Override
    public boolean contains(boolean k2) {
        return this.findKey(k2) != -1;
    }

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public boolean rem(boolean k2) {
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
    public boolean add(boolean k2) {
        int pos = this.findKey(k2);
        if (pos != -1) {
            return false;
        }
        if (this.size == this.a.length) {
            boolean[] b2 = new boolean[this.size == 0 ? 2 : this.size * 2];
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

    public BooleanArraySet clone() {
        BooleanArraySet c2;
        try {
            c2 = (BooleanArraySet)super.clone();
        }
        catch (CloneNotSupportedException cantHappen) {
            throw new InternalError();
        }
        c2.a = (boolean[])this.a.clone();
        return c2;
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

