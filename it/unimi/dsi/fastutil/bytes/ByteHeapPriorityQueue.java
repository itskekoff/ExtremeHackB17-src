package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.bytes.AbstractBytePriorityQueue;
import it.unimi.dsi.fastutil.bytes.ByteArrays;
import it.unimi.dsi.fastutil.bytes.ByteCollection;
import it.unimi.dsi.fastutil.bytes.ByteComparator;
import it.unimi.dsi.fastutil.bytes.ByteHeaps;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class ByteHeapPriorityQueue
extends AbstractBytePriorityQueue
implements Serializable {
    private static final long serialVersionUID = 1L;
    protected transient byte[] heap = ByteArrays.EMPTY_ARRAY;
    protected int size;
    protected ByteComparator c;

    public ByteHeapPriorityQueue(int capacity, ByteComparator c2) {
        if (capacity > 0) {
            this.heap = new byte[capacity];
        }
        this.c = c2;
    }

    public ByteHeapPriorityQueue(int capacity) {
        this(capacity, null);
    }

    public ByteHeapPriorityQueue(ByteComparator c2) {
        this(0, c2);
    }

    public ByteHeapPriorityQueue() {
        this(0, null);
    }

    public ByteHeapPriorityQueue(byte[] a2, int size, ByteComparator c2) {
        this(c2);
        this.heap = a2;
        this.size = size;
        ByteHeaps.makeHeap(a2, size, c2);
    }

    public ByteHeapPriorityQueue(byte[] a2, ByteComparator c2) {
        this(a2, a2.length, c2);
    }

    public ByteHeapPriorityQueue(byte[] a2, int size) {
        this(a2, size, null);
    }

    public ByteHeapPriorityQueue(byte[] a2) {
        this(a2, a2.length);
    }

    public ByteHeapPriorityQueue(ByteCollection collection, ByteComparator c2) {
        this(collection.toByteArray(), c2);
    }

    public ByteHeapPriorityQueue(ByteCollection collection) {
        this(collection, (ByteComparator)null);
    }

    public ByteHeapPriorityQueue(Collection<? extends Byte> collection, ByteComparator c2) {
        this(collection.size(), c2);
        Iterator<? extends Byte> iterator = collection.iterator();
        int size = collection.size();
        for (int i2 = 0; i2 < size; ++i2) {
            this.heap[i2] = iterator.next();
        }
    }

    public ByteHeapPriorityQueue(Collection<? extends Byte> collection) {
        this(collection, null);
    }

    @Override
    public void enqueue(byte x2) {
        if (this.size == this.heap.length) {
            this.heap = ByteArrays.grow(this.heap, this.size + 1);
        }
        this.heap[this.size++] = x2;
        ByteHeaps.upHeap(this.heap, this.size, this.size - 1, this.c);
    }

    @Override
    public byte dequeueByte() {
        if (this.size == 0) {
            throw new NoSuchElementException();
        }
        byte result = this.heap[0];
        this.heap[0] = this.heap[--this.size];
        if (this.size != 0) {
            ByteHeaps.downHeap(this.heap, this.size, 0, this.c);
        }
        return result;
    }

    @Override
    public byte firstByte() {
        if (this.size == 0) {
            throw new NoSuchElementException();
        }
        return this.heap[0];
    }

    @Override
    public void changed() {
        ByteHeaps.downHeap(this.heap, this.size, 0, this.c);
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
        this.heap = ByteArrays.trim(this.heap, this.size);
    }

    @Override
    public ByteComparator comparator() {
        return this.c;
    }

    private void writeObject(ObjectOutputStream s2) throws IOException {
        s2.defaultWriteObject();
        s2.writeInt(this.heap.length);
        for (int i2 = 0; i2 < this.size; ++i2) {
            s2.writeByte(this.heap[i2]);
        }
    }

    private void readObject(ObjectInputStream s2) throws IOException, ClassNotFoundException {
        s2.defaultReadObject();
        this.heap = new byte[s2.readInt()];
        for (int i2 = 0; i2 < this.size; ++i2) {
            this.heap[i2] = s2.readByte();
        }
    }
}

