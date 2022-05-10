package io.netty.util.internal;

import io.netty.util.internal.EmptyArrays;
import io.netty.util.internal.PriorityQueue;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.NoSuchElementException;

public final class EmptyPriorityQueue<T>
implements PriorityQueue<T> {
    private static final PriorityQueue<Object> INSTANCE = new EmptyPriorityQueue<Object>();

    private EmptyPriorityQueue() {
    }

    public static <V> EmptyPriorityQueue<V> instance() {
        return (EmptyPriorityQueue)INSTANCE;
    }

    @Override
    public boolean removeTyped(T node) {
        return false;
    }

    @Override
    public boolean containsTyped(T node) {
        return false;
    }

    @Override
    public void priorityChanged(T node) {
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public boolean isEmpty() {
        return true;
    }

    @Override
    public boolean contains(Object o2) {
        return false;
    }

    @Override
    public Iterator<T> iterator() {
        return Collections.emptyList().iterator();
    }

    @Override
    public Object[] toArray() {
        return EmptyArrays.EMPTY_OBJECTS;
    }

    @Override
    public <T1> T1[] toArray(T1[] a2) {
        if (a2.length > 0) {
            a2[0] = null;
        }
        return a2;
    }

    @Override
    public boolean add(T t2) {
        return false;
    }

    @Override
    public boolean remove(Object o2) {
        return false;
    }

    @Override
    public boolean containsAll(Collection<?> c2) {
        return false;
    }

    @Override
    public boolean addAll(Collection<? extends T> c2) {
        return false;
    }

    @Override
    public boolean removeAll(Collection<?> c2) {
        return false;
    }

    @Override
    public boolean retainAll(Collection<?> c2) {
        return false;
    }

    @Override
    public void clear() {
    }

    @Override
    public boolean equals(Object o2) {
        return o2 instanceof PriorityQueue && ((PriorityQueue)o2).isEmpty();
    }

    @Override
    public int hashCode() {
        return 0;
    }

    @Override
    public boolean offer(T t2) {
        return false;
    }

    @Override
    public T remove() {
        throw new NoSuchElementException();
    }

    @Override
    public T poll() {
        return null;
    }

    @Override
    public T element() {
        throw new NoSuchElementException();
    }

    @Override
    public T peek() {
        return null;
    }

    public String toString() {
        return EmptyPriorityQueue.class.getSimpleName();
    }
}

