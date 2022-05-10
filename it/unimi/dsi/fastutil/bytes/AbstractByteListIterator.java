package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.bytes.AbstractByteBidirectionalIterator;
import it.unimi.dsi.fastutil.bytes.ByteListIterator;

public abstract class AbstractByteListIterator
extends AbstractByteBidirectionalIterator
implements ByteListIterator {
    protected AbstractByteListIterator() {
    }

    @Override
    public void set(Byte ok2) {
        this.set((byte)ok2);
    }

    @Override
    public void add(Byte ok2) {
        this.add((byte)ok2);
    }

    @Override
    public void set(byte k2) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void add(byte k2) {
        throw new UnsupportedOperationException();
    }
}

