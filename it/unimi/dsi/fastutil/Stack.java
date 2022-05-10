package it.unimi.dsi.fastutil;

public interface Stack<K> {
    public void push(K var1);

    public K pop();

    public boolean isEmpty();

    public K top();

    public K peek(int var1);
}

