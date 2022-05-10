package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.bytes.AbstractByteIterator;
import it.unimi.dsi.fastutil.bytes.ByteBidirectionalIterator;

public abstract class AbstractByteBidirectionalIterator
extends AbstractByteIterator
implements ByteBidirectionalIterator {
    protected AbstractByteBidirectionalIterator() {
    }

    @Override
    public byte previousByte() {
        return this.previous();
    }

    @Override
    public Byte previous() {
        return this.previousByte();
    }

    @Override
    public int back(int n2) {
        int i2 = n2;
        while (i2-- != 0 && this.hasPrevious()) {
            this.previousByte();
        }
        return n2 - i2 - 1;
    }
}

