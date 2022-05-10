package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.bytes.AbstractBytePriorityQueue;
import it.unimi.dsi.fastutil.bytes.ByteArrays;
import it.unimi.dsi.fastutil.bytes.ByteComparator;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.NoSuchElementException;

public class ByteArrayPriorityQueue
extends AbstractBytePriorityQueue
implements Serializable {
    private static final long serialVersionUID = 1L;
    protected transient byte[] array = ByteArrays.EMPTY_ARRAY;
    protected int size;
    protected ByteComparator c;
    protected transient int firstIndex;
    protected transient boolean firstIndexValid;

    public ByteArrayPriorityQueue(int capacity, ByteComparator c2) {
        if (capacity > 0) {
            this.array = new byte[capacity];
        }
        this.c = c2;
    }

    public ByteArrayPriorityQueue(int capacity) {
        this(capacity, null);
    }

    public ByteArrayPriorityQueue(ByteComparator c2) {
        this(0, c2);
    }

    public ByteArrayPriorityQueue() {
        this(0, null);
    }

    public ByteArrayPriorityQueue(byte[] a2, int size, ByteComparator c2) {
        this(c2);
        this.array = a2;
        this.size = size;
    }

    public ByteArrayPriorityQueue(byte[] a2, ByteComparator c2) {
        this(a2, a2.length, c2);
    }

    public ByteArrayPriorityQueue(byte[] a2, int size) {
        this(a2, size, null);
    }

    public ByteArrayPriorityQueue(byte[] a2) {
        this(a2, a2.length);
    }

    private int findFirst() {
        if (this.firstIndexValid) {
            return this.firstIndex;
        }
        this.firstIndexValid = true;
        int i2 = this.size;
        int firstIndex = --i2;
        byte first = this.array[firstIndex];
        if (this.c == null) {
            while (i2-- != 0) {
                if (this.array[i2] >= first) continue;
                firstIndex = i2;
                first = this.array[firstIndex];
            }
        } else {
            while (i2-- != 0) {
                if (this.c.compare(this.array[i2], first) >= 0) continue;
                firstIndex = i2;
                first = this.array[firstIndex];
            }
        }
        this.firstIndex = firstIndex;
        return this.firstIndex;
    }

    private void ensureNonEmpty() {
        if (this.size == 0) {
            throw new NoSuchElementException();
        }
    }

    @Override
    public void enqueue(byte x2) {
        if (this.size == this.array.length) {
            this.array = ByteArrays.grow(this.array, this.size + 1);
        }
        if (this.firstIndexValid) {
            if (this.c == null) {
                if (x2 < this.array[this.firstIndex]) {
                    this.firstIndex = this.size;
                }
            } else if (this.c.compare(x2, this.array[this.firstIndex]) < 0) {
                this.firstIndex = this.size;
            }
        } else {
            this.firstIndexValid = false;
        }
        this.array[this.size++] = x2;
    }

    @Override
    public byte dequeueByte() {
        this.ensureNonEmpty();
        int first = this.findFirst();
        byte result = this.array[first];
        System.arraycopy(this.array, first + 1, this.array, first, --this.size - first);
        this.firstIndexValid = false;
        return result;
    }

    @Override
    public byte firstByte() {
        this.ensureNonEmpty();
        return this.array[this.findFirst()];
    }

    @Override
    public void changed() {
        this.ensureNonEmpty();
        this.firstIndexValid = false;
    }

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public void clear() {
        this.size = 0;
        this.firstIndexValid = false;
    }

    public void trim() {
        this.array = ByteArrays.trim(this.array, this.size);
    }

    @Override
    public ByteComparator comparator() {
        return this.c;
    }

    private void writeObject(ObjectOutputStream s2) throws IOException {
        s2.defaultWriteObject();
        s2.writeInt(this.array.length);
        for (int i2 = 0; i2 < this.size; ++i2) {
            s2.writeByte(this.array[i2]);
        }
    }

    private void readObject(ObjectInputStream s2) throws IOException, ClassNotFoundException {
        s2.defaultReadObject();
        this.array = new byte[s2.readInt()];
        for (int i2 = 0; i2 < this.size; ++i2) {
            this.array[i2] = s2.readByte();
        }
    }
}

