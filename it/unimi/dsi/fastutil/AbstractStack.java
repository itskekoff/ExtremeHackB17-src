package it.unimi.dsi.fastutil;

import it.unimi.dsi.fastutil.Stack;

public abstract class AbstractStack<K>
implements Stack<K> {
    @Override
    public K top() {
        return this.peek(0);
    }

    @Override
    public K peek(int i2) {
        throw new UnsupportedOperationException();
    }
}

