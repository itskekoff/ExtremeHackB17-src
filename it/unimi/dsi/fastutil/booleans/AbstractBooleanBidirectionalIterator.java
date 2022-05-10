package it.unimi.dsi.fastutil.booleans;

import it.unimi.dsi.fastutil.booleans.AbstractBooleanIterator;
import it.unimi.dsi.fastutil.booleans.BooleanBidirectionalIterator;

public abstract class AbstractBooleanBidirectionalIterator
extends AbstractBooleanIterator
implements BooleanBidirectionalIterator {
    protected AbstractBooleanBidirectionalIterator() {
    }

    @Override
    public boolean previousBoolean() {
        return this.previous();
    }

    @Override
    public Boolean previous() {
        return this.previousBoolean();
    }

    @Override
    public int back(int n2) {
        int i2 = n2;
        while (i2-- != 0 && this.hasPrevious()) {
            this.previousBoolean();
        }
        return n2 - i2 - 1;
    }
}

