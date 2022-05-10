package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.chars.AbstractCharBidirectionalIterator;
import it.unimi.dsi.fastutil.chars.CharListIterator;

public abstract class AbstractCharListIterator
extends AbstractCharBidirectionalIterator
implements CharListIterator {
    protected AbstractCharListIterator() {
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
}

