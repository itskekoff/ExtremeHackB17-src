package it.unimi.dsi.fastutil.booleans;

import it.unimi.dsi.fastutil.booleans.AbstractBooleanBidirectionalIterator;
import it.unimi.dsi.fastutil.booleans.BooleanListIterator;

public abstract class AbstractBooleanListIterator
extends AbstractBooleanBidirectionalIterator
implements BooleanListIterator {
    protected AbstractBooleanListIterator() {
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
}

