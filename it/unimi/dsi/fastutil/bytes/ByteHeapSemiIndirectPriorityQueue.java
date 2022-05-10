package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.AbstractIndirectPriorityQueue;
import it.unimi.dsi.fastutil.bytes.ByteComparator;
import it.unimi.dsi.fastutil.bytes.ByteIndirectPriorityQueue;
import it.unimi.dsi.fastutil.bytes.ByteSemiIndirectHeaps;
import it.unimi.dsi.fastutil.ints.IntArrays;
import java.util.NoSuchElementException;

public class ByteHeapSemiIndirectPriorityQueue
extends AbstractIndirectPriorityQueue<Byte>
implements ByteIndirectPriorityQueue {
    protected final byte[] refArray;
    protected int[] heap = IntArrays.EMPTY_ARRAY;
    protected int size;
    protected ByteComparator c;

    public ByteHeapSemiIndirectPriorityQueue(byte[] refArray, int capacity, ByteComparator c2) {
        if (capacity > 0) {
            this.heap = new int[capacity];
        }
        this.refArray = refArray;
        this.c = c2;
    }

    public ByteHeapSemiIndirectPriorityQueue(byte[] refArray, int capacity) {
        this(refArray, capacity, null);
    }

    public ByteHeapSemiIndirectPriorityQueue(byte[] refArray, ByteComparator c2) {
        this(refArray, refArray.length, c2);
    }

    public ByteHeapSemiIndirectPriorityQueue(byte[] refArray) {
        this(refArray, refArray.length, null);
    }

    public ByteHeapSemiIndirectPriorityQueue(byte[] refArray, int[] a2, int size, ByteComparator c2) {
        this(refArray, 0, c2);
        this.heap = a2;
        this.size = size;
        ByteSemiIndirectHeaps.makeHeap(refArray, a2, size, c2);
    }

    public ByteHeapSemiIndirectPriorityQueue(byte[] refArray, int[] a2, ByteComparator c2) {
        this(refArray, a2, a2.length, c2);
    }

    public ByteHeapSemiIndirectPriorityQueue(byte[] refArray, int[] a2, int size) {
        this(refArray, a2, size, null);
    }

    public ByteHeapSemiIndirectPriorityQueue(byte[] refArray, int[] a2) {
        this(refArray, a2, a2.length);
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
        if (this.size == this.heap.length) {
            this.heap = IntArrays.grow(this.heap, this.size + 1);
        }
        this.heap[this.size++] = x2;
        ByteSemiIndirectHeaps.upHeap(this.refArray, this.heap, this.size, this.size - 1, this.c);
    }

    @Override
    public int dequeue() {
        if (this.size == 0) {
            throw new NoSuchElementException();
        }
        int result = this.heap[0];
        this.heap[0] = this.heap[--this.size];
        if (this.size != 0) {
            ByteSemiIndirectHeaps.downHeap(this.refArray, this.heap, this.size, 0, this.c);
        }
        return result;
    }

    @Override
    public int first() {
        if (this.size == 0) {
            throw new NoSuchElementException();
        }
        return this.heap[0];
    }

    @Override
    public void changed() {
        ByteSemiIndirectHeaps.downHeap(this.refArray, this.heap, this.size, 0, this.c);
    }

    @Override
    public void allChanged() {
        ByteSemiIndirectHeaps.makeHeap(this.refArray, this.heap, this.size, this.c);
    }

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public void clear() {
        this.size = 0;
    }

    public void trim() {
        this.heap = IntArrays.trim(this.heap, this.size);
    }

    @Override
    public ByteComparator comparator() {
        return this.c;
    }

    @Override
    public int front(int[] a2) {
        return this.c == null ? ByteSemiIndirectHeaps.front(this.refArray, this.heap, this.size, a2) : ByteSemiIndirectHeaps.front(this.refArray, this.heap, this.size, a2, this.c);
    }

    public String toString() {
        StringBuffer s2 = new StringBuffer();
        s2.append("[");
        for (int i2 = 0; i2 < this.size; ++i2) {
            if (i2 != 0) {
                s2.append(", ");
            }
            s2.append(this.refArray[this.heap[i2]]);
        }
        s2.append("]");
        return s2.toString();
    }
}

