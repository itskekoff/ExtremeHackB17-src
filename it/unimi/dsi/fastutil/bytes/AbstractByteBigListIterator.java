package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.bytes.AbstractByteBidirectionalIterator;
import it.unimi.dsi.fastutil.bytes.ByteBigListIterator;

public abstract class AbstractByteBigListIterator
extends AbstractByteBidirectionalIterator
implements ByteBigListIterator {
    protected AbstractByteBigListIterator() {
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

    @Override
    public long skip(long n2) {
        long i2 = n2;
        while (i2-- != 0L && this.hasNext()) {
            this.nextByte();
        }
        return n2 - i2 - 1L;
    }

    public long back(long n2) {
        long i2 = n2;
        while (i2-- != 0L && this.hasPrevious()) {
            this.previousByte();
        }
        return n2 - i2 - 1L;
    }
}

