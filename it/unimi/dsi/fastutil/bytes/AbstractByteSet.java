package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.bytes.AbstractByteCollection;
import it.unimi.dsi.fastutil.bytes.ByteIterator;
import it.unimi.dsi.fastutil.bytes.ByteSet;
import java.util.Set;

public abstract class AbstractByteSet
extends AbstractByteCollection
implements Cloneable,
ByteSet {
    protected AbstractByteSet() {
    }

    @Override
    public abstract ByteIterator iterator();

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
        ByteIterator i2 = this.iterator();
        while (n2-- != 0) {
            byte k2 = i2.nextByte();
            h2 += k2;
        }
        return h2;
    }

    @Override
    public boolean remove(byte k2) {
        return this.rem(k2);
    }
}

