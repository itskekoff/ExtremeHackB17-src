package io.netty.util.internal;

import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PriorityQueue;
import io.netty.util.internal.PriorityQueueNode;
import java.util.AbstractQueue;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;

public final class DefaultPriorityQueue<T extends PriorityQueueNode>
extends AbstractQueue<T>
implements PriorityQueue<T> {
    private static final PriorityQueueNode[] EMPTY_ARRAY = new PriorityQueueNode[0];
    private final Comparator<T> comparator;
    private T[] queue;
    private int size;

    public DefaultPriorityQueue(Comparator<T> comparator, int initialSize) {
        this.comparator = ObjectUtil.checkNotNull(comparator, "comparator");
        this.queue = initialSize != 0 ? new PriorityQueueNode[initialSize] : EMPTY_ARRAY;
    }

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public boolean isEmpty() {
        return this.size == 0;
    }

    @Override
    public boolean contains(Object o2) {
        if (!(o2 instanceof PriorityQueueNode)) {
            return false;
        }
        PriorityQueueNode node = (PriorityQueueNode)o2;
        return this.contains(node, node.priorityQueueIndex(this));
    }

    @Override
    public boolean containsTyped(T node) {
        return this.contains((PriorityQueueNode)node, node.priorityQueueIndex(this));
    }

    @Override
    public void clear() {
        for (int i2 = 0; i2 < this.size; ++i2) {
            T node = this.queue[i2];
            if (node == null) continue;
            node.priorityQueueIndex(this, -1);
            this.queue[i2] = null;
        }
        this.size = 0;
    }

    @Override
    public boolean offer(T e2) {
        if (e2.priorityQueueIndex(this) != -1) {
            throw new IllegalArgumentException("e.priorityQueueIndex(): " + e2.priorityQueueIndex(this) + " (expected: " + -1 + ") + e: " + e2);
        }
        if (this.size >= this.queue.length) {
            this.queue = (PriorityQueueNode[])Arrays.copyOf(this.queue, this.queue.length + (this.queue.length < 64 ? this.queue.length + 2 : this.queue.length >>> 1));
        }
        this.bubbleUp(this.size++, e2);
        return true;
    }

    @Override
    public T poll() {
        if (this.size == 0) {
            return null;
        }
        T result = this.queue[0];
        result.priorityQueueIndex(this, -1);
        T last = this.queue[--this.size];
        this.queue[this.size] = null;
        if (this.size != 0) {
            this.bubbleDown(0, last);
        }
        return result;
    }

    @Override
    public T peek() {
        return this.size == 0 ? null : (T)this.queue[0];
    }

    @Override
    public boolean remove(Object o2) {
        PriorityQueueNode node;
        try {
            node = (PriorityQueueNode)o2;
        }
        catch (ClassCastException e2) {
            return false;
        }
        return this.removeTyped((T)node);
    }

    @Override
    public boolean removeTyped(T node) {
        int i2 = node.priorityQueueIndex(this);
        if (!this.contains((PriorityQueueNode)node, i2)) {
            return false;
        }
        node.priorityQueueIndex(this, -1);
        if (--this.size == 0 || this.size == i2) {
            this.queue[i2] = null;
            return true;
        }
        T moved = this.queue[i2] = this.queue[this.size];
        this.queue[this.size] = null;
        if (this.comparator.compare(node, moved) < 0) {
            this.bubbleDown(i2, moved);
        } else {
            this.bubbleUp(i2, moved);
        }
        return true;
    }

    @Override
    public void priorityChanged(T node) {
        int i2 = node.priorityQueueIndex(this);
        if (!this.contains((PriorityQueueNode)node, i2)) {
            return;
        }
        if (i2 == 0) {
            this.bubbleDown(i2, node);
        } else {
            int iParent = i2 - 1 >>> 1;
            T parent = this.queue[iParent];
            if (this.comparator.compare(node, parent) < 0) {
                this.bubbleUp(i2, node);
            } else {
                this.bubbleDown(i2, node);
            }
        }
    }

    @Override
    public Object[] toArray() {
        return Arrays.copyOf(this.queue, this.size);
    }

    @Override
    public <X> X[] toArray(X[] a2) {
        if (a2.length < this.size) {
            return Arrays.copyOf(this.queue, this.size, a2.getClass());
        }
        System.arraycopy(this.queue, 0, a2, 0, this.size);
        if (a2.length > this.size) {
            a2[this.size] = null;
        }
        return a2;
    }

    @Override
    public Iterator<T> iterator() {
        return new PriorityQueueIterator();
    }

    private boolean contains(PriorityQueueNode node, int i2) {
        return i2 >= 0 && i2 < this.size && node.equals(this.queue[i2]);
    }

    private void bubbleDown(int k2, T node) {
        int half = this.size >>> 1;
        while (k2 < half) {
            int iChild = (k2 << 1) + 1;
            T child = this.queue[iChild];
            int rightChild = iChild + 1;
            if (rightChild < this.size && this.comparator.compare(child, this.queue[rightChild]) > 0) {
                iChild = rightChild;
                child = this.queue[iChild];
            }
            if (this.comparator.compare(node, child) <= 0) break;
            this.queue[k2] = child;
            child.priorityQueueIndex(this, k2);
            k2 = iChild;
        }
        this.queue[k2] = node;
        node.priorityQueueIndex(this, k2);
    }

    private void bubbleUp(int k2, T node) {
        int iParent;
        T parent;
        while (k2 > 0 && this.comparator.compare(node, parent = this.queue[iParent = k2 - 1 >>> 1]) < 0) {
            this.queue[k2] = parent;
            parent.priorityQueueIndex(this, k2);
            k2 = iParent;
        }
        this.queue[k2] = node;
        node.priorityQueueIndex(this, k2);
    }

    private final class PriorityQueueIterator
    implements Iterator<T> {
        private int index;

        private PriorityQueueIterator() {
        }

        @Override
        public boolean hasNext() {
            return this.index < DefaultPriorityQueue.this.size;
        }

        @Override
        public T next() {
            if (this.index >= DefaultPriorityQueue.this.size) {
                throw new NoSuchElementException();
            }
            return DefaultPriorityQueue.this.queue[this.index++];
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("remove");
        }
    }
}

