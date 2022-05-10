package io.netty.channel.nio;

import java.nio.channels.SelectionKey;
import java.util.AbstractSet;
import java.util.Arrays;
import java.util.Iterator;

final class SelectedSelectionKeySet
extends AbstractSet<SelectionKey> {
    SelectionKey[] keys = new SelectionKey[1024];
    int size;

    SelectedSelectionKeySet() {
    }

    @Override
    public boolean add(SelectionKey o2) {
        if (o2 == null) {
            return false;
        }
        this.keys[this.size++] = o2;
        if (this.size == this.keys.length) {
            this.increaseCapacity();
        }
        return true;
    }

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public boolean remove(Object o2) {
        return false;
    }

    @Override
    public boolean contains(Object o2) {
        return false;
    }

    @Override
    public Iterator<SelectionKey> iterator() {
        throw new UnsupportedOperationException();
    }

    void reset() {
        this.reset(0);
    }

    void reset(int start) {
        Arrays.fill(this.keys, start, this.size, null);
        this.size = 0;
    }

    private void increaseCapacity() {
        SelectionKey[] newKeys = new SelectionKey[this.keys.length << 1];
        System.arraycopy(this.keys, 0, newKeys, 0, this.size);
        this.keys = newKeys;
    }
}

