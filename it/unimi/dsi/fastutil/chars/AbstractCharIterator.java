package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.chars.CharIterator;

public abstract class AbstractCharIterator
implements CharIterator {
    protected AbstractCharIterator() {
    }

    @Override
    public char nextChar() {
        return this.next().charValue();
    }

    @Override
    @Deprecated
    public Character next() {
        return Character.valueOf(this.nextChar());
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int skip(int n2) {
        int i2 = n2;
        while (i2-- != 0 && this.hasNext()) {
            this.nextChar();
        }
        return n2 - i2 - 1;
    }
}

