package it.unimi.dsi.fastutil;

import java.util.Comparator;

public interface PriorityQueue<K> {
    public void enqueue(K var1);

    public K dequeue();

    public boolean isEmpty();

    public int size();

    public void clear();

    public K first();

    public K last();

    public void changed();

    public Comparator<? super K> comparator();
}

