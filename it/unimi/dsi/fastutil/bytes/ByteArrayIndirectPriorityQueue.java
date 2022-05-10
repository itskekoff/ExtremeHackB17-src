package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.AbstractIndirectPriorityQueue;
import it.unimi.dsi.fastutil.bytes.ByteComparator;
import it.unimi.dsi.fastutil.bytes.ByteIndirectPriorityQueue;
import it.unimi.dsi.fastutil.ints.IntArrays;
import java.util.NoSuchElementException;

public class ByteArrayIndirectPriorityQueue
extends AbstractIndirectPriorityQueue<Byte>
implements ByteIndirectPriorityQueue {
    protected byte[] refArray;
    protected int[] array = IntArrays.EMPTY_ARRAY;
    protected int size;
    protected ByteComparator c;
    protected int firstIndex;
    protected boolean firstIndexValid;

    public ByteArrayIndirectPriorityQueue(byte[] refArray, int capacity, ByteComparator c2) {
        if (capacity > 0) {
            this.array = new int[capacity];
        }
        this.refArray = refArray;
        this.c = c2;
    }

    public ByteArrayIndirectPriorityQueue(byte[] refArray, int capacity) {
        this(refArray, capacity, null);
    }

    public ByteArrayIndirectPriorityQueue(byte[] refArray, ByteComparator c2) {
        this(refArray, refArray.length, c2);
    }

    public ByteArrayIndirectPriorityQueue(byte[] refArray) {
        this(refArray, refArray.length, null);
    }

    public ByteArrayIndirectPriorityQueue(byte[] refArray, int[] a2, int size, ByteComparator c2) {
        this(refArray, 0, c2);
        this.array = a2;
        this.size = size;
    }

    public ByteArrayIndirectPriorityQueue(byte[] refArray, int[] a2, ByteComparator c2) {
        this(refArray, a2, a2.length, c2);
    }

    public ByteArrayIndirectPriorityQueue(byte[] refArray, int[] a2, int size) {
        this(refArray, a2, size, null);
    }

    public ByteArrayIndirectPriorityQueue(byte[] refArray, int[] a2) {
        this(refArray, a2, a2.length);
    }

    private int findFirst() {
        if (this.firstIndexValid) {
            return this.firstIndex;
        }
        this.firstIndexValid = true;
        int i2 = this.size;
        int firstIndex = --i2;
        byte first = this.refArray[this.array[firstIndex]];
        if (this.c == null) {
            while (i2-- != 0) {
                if (this.refArray[this.array[i2]] >= first) continue;
                firstIndex = i2;
                first = this.refArray[this.array[firstIndex]];
            }
        } else {
            while (i2-- != 0) {
                if (this.c.compare(this.refArray[this.array[i2]], first) >= 0) continue;
                firstIndex = i2;
                first = this.refArray[this.array[firstIndex]];
            }
        }
        this.firstIndex = firstIndex;
        return this.firstIndex;
    }

    private int findLast() {
        int i2 = this.size;
        int lastIndex = --i2;
        byte last = this.refArray[this.array[lastIndex]];
        if (this.c == null) {
            while (i2-- != 0) {
                if (last >= this.refArray[this.array[i2]]) continue;
                lastIndex = i2;
                last = this.refArray[this.array[lastIndex]];
            }
        } else {
            while (i2-- != 0) {
                if (this.c.compare(last, this.refArray[this.array[i2]]) >= 0) continue;
                lastIndex = i2;
                last = this.refArray[this.array[lastIndex]];
            }
        }
        return lastIndex;
    }

    protected final void ensureNonEmpty() {
        if (this.size == 0) {
            throw new NoSuchElementException();
        }
    }

    protected void ensureElement(int index) {
        if (index < 0) {
            throw new IndexOutOfBoundsException("Index (" + index + ") is negative");
        }
        if (index >= this.refArray.length) {
            throw new IndexOutOfBoundsException("Index (" + index + ") is larger than or equal to reference array size (" + this.refArray.length + ")");
        }
    }

    @Override
    public void enqueue(int x2) {
        this.ensureElement(x2);
        if (this.size == this.array.length) {
            this.array = IntArrays.grow(this.array, this.size + 1);
        }
        if (this.firstIndexValid) {
            if (this.c == null) {
                if (this.refArray[x2] < this.refArray[this.array[this.firstIndex]]) {
                    this.firstIndex = this.size;
                }
            } else if (this.c.compare(this.refArray[x2], this.refArray[this.array[this.firstIndex]]) < 0) {
                this.firstIndex = this.size;
            }
        } else {
            this.firstIndexValid = false;
        }
        this.array[this.size++] = x2;
    }

    @Override
    public int dequeue() {
        this.ensureNonEmpty();
        int firstIndex = this.findFirst();
        int result = this.array[firstIndex];
        if (--this.size != 0) {
            System.arraycopy(this.array, firstIndex + 1, this.array, firstIndex, this.size - firstIndex);
        }
        this.firstIndexValid = false;
        return result;
    }

    @Override
    public int first() {
        this.ensureNonEmpty();
        return this.array[this.findFirst()];
    }

    @Override
    public int last() {
        this.ensureNonEmpty();
        return this.array[this.findLast()];
    }

    @Override
    public void changed() {
        this.ensureNonEmpty();
        this.firstIndexValid = false;
    }

    @Override
    public void changed(int index) {
        this.ensureElement(index);
        if (index == this.firstIndex) {
            this.firstIndexValid = false;
        }
    }

    @Override
    public void allChanged() {
        this.firstIndexValid = false;
    }

    @Override
    public boolean remove(int index) {
        this.ensureElement(index);
        int[] a2 = this.array;
        int i2 = this.size;
        while (i2-- != 0 && a2[i2] != index) {
        }
        if (i2 < 0) {
            return false;
        }
        this.firstIndexValid = false;
        if (--this.size != 0) {
            System.arraycopy(a2, i2 + 1, a2, i2, this.size - i2);
        }
        return true;
    }

    @Override
    public int front(int[] a2) {
        byte top = this.refArray[this.array[this.findFirst()]];
        int i2 = this.size;
        int c2 = 0;
        while (i2-- != 0) {
            if (top != this.refArray[this.array[i2]]) continue;
            a2[c2++] = this.array[i2];
        }
        return c2;
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
        this.array = IntArrays.trim(this.array, this.size);
    }

    @Override
    public ByteComparator comparator() {
        return this.c;
    }

    public String toString() {
        StringBuffer s2 = new StringBuffer();
        s2.append("[");
        for (int i2 = 0; i2 < this.size; ++i2) {
            if (i2 != 0) {
                s2.append(", ");
            }
            s2.append(this.refArray[this.array[i2]]);
        }
        s2.append("]");
        return s2.toString();
    }
}

