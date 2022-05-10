package it.unimi.dsi.fastutil.booleans;

import it.unimi.dsi.fastutil.booleans.AbstractBooleanBidirectionalIterator;
import it.unimi.dsi.fastutil.booleans.BooleanBigListIterator;

public abstract class AbstractBooleanBigListIterator
extends AbstractBooleanBidirectionalIterator
implements BooleanBigListIterator {
    protected AbstractBooleanBigListIterator() {
    }

    @Override
    public void set(Boolean ok2) {
        this.set((boolean)ok2);
    }

    @Override
    public void add(Boolean ok2) {
        this.add((boolean)ok2);
    }

    @Override
    public void set(boolean k2) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void add(boolean k2) {
        throw new UnsupportedOperationException();
    }

    @Override
    public long skip(long n2) {
        long i2 = n2;
        while (i2-- != 0L && this.hasNext()) {
            this.nextBoolean();
        }
        return n2 - i2 - 1L;
    }

    public long back(long n2) {
        long i2 = n2;
        while (i2-- != 0L && this.hasPrevious()) {
            this.previousBoolean();
        }
        return n2 - i2 - 1L;
    }
}

