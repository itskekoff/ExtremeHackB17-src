package it.unimi.dsi.fastutil.booleans;

import it.unimi.dsi.fastutil.booleans.BooleanIterator;

public abstract class AbstractBooleanIterator
implements BooleanIterator {
    protected AbstractBooleanIterator() {
    }

    @Override
    public boolean nextBoolean() {
        return this.next();
    }

    @Override
    @Deprecated
    public Boolean next() {
        return this.nextBoolean();
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int skip(int n2) {
        int i2 = n2;
        while (i2-- != 0 && this.hasNext()) {
            this.nextBoolean();
        }
        return n2 - i2 - 1;
    }
}

