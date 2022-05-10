package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.chars.AbstractCharCollection;
import it.unimi.dsi.fastutil.chars.CharIterator;
import it.unimi.dsi.fastutil.chars.CharSet;
import java.util.Set;

public abstract class AbstractCharSet
extends AbstractCharCollection
implements Cloneable,
CharSet {
    protected AbstractCharSet() {
    }

    @Override
    public abstract CharIterator iterator();

    @Override
    public boolean equals(Object o2) {
        if (o2 == this) {
            return true;
        }
        if (!(o2 instanceof Set)) {
            return false;
        }
        Set s2 = (Set)o2;
        if (s2.size() != this.size()) {
            return false;
        }
        return this.containsAll(s2);
    }

    @Override
    public int hashCode() {
        int h2 = 0;
        int n2 = this.size();
        CharIterator i2 = this.iterator();
        while (n2-- != 0) {
            char k2 = i2.nextChar();
            h2 += k2;
        }
        return h2;
    }

    @Override
    public boolean remove(char k2) {
        return this.rem(k2);
    }
}

