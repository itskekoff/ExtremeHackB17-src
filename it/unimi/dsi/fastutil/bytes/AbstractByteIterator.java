package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.bytes.ByteIterator;

public abstract class AbstractByteIterator
implements ByteIterator {
    protected AbstractByteIterator() {
    }

    @Override
    public byte nextByte() {
        return this.next();
    }

    @Override
    @Deprecated
    public Byte next() {
        return this.nextByte();
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int skip(int n2) {
        int i2 = n2;
        while (i2-- != 0 && this.hasNext()) {
            this.nextByte();
        }
        return n2 - i2 - 1;
    }
}

