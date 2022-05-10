package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.chars.AbstractCharBidirectionalIterator;
import it.unimi.dsi.fastutil.chars.CharBigListIterator;

public abstract class AbstractCharBigListIterator
extends AbstractCharBidirectionalIterator
implements CharBigListIterator {
    protected AbstractCharBigListIterator() {
    }

    @Override
    public void set(Character ok2) {
        this.set(ok2.charValue());
    }

    @Override
    public void add(Character ok2) {
        this.add(ok2.charValue());
    }

    @Override
    public void set(char k2) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void add(char k2) {
        throw new UnsupportedOperationException();
    }

    @Override
    public long skip(long n2) {
        long i2 = n2;
        while (i2-- != 0L && this.hasNext()) {
            this.nextChar();
        }
        return n2 - i2 - 1L;
    }

    public long back(long n2) {
        long i2 = n2;
        while (i2-- != 0L && this.hasPrevious()) {
            this.previousChar();
        }
        return n2 - i2 - 1L;
    }
}

