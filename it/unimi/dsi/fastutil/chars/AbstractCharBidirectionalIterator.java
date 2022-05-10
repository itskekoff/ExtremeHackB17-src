package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.chars.AbstractCharIterator;
import it.unimi.dsi.fastutil.chars.CharBidirectionalIterator;

public abstract class AbstractCharBidirectionalIterator
extends AbstractCharIterator
implements CharBidirectionalIterator {
    protected AbstractCharBidirectionalIterator() {
    }

    @Override
    public char previousChar() {
        return this.previous().charValue();
    }

    @Override
    public Character previous() {
        return Character.valueOf(this.previousChar());
    }

    @Override
    public int back(int n2) {
        int i2 = n2;
        while (i2-- != 0 && this.hasPrevious()) {
            this.previousChar();
        }
        return n2 - i2 - 1;
    }
}

