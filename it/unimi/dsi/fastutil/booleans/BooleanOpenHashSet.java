package it.unimi.dsi.fastutil.booleans;

import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.booleans.AbstractBooleanIterator;
import it.unimi.dsi.fastutil.booleans.AbstractBooleanSet;
import it.unimi.dsi.fastutil.booleans.BooleanArrayList;
import it.unimi.dsi.fastutil.booleans.BooleanArrays;
import it.unimi.dsi.fastutil.booleans.BooleanCollection;
import it.unimi.dsi.fastutil.booleans.BooleanIterator;
import it.unimi.dsi.fastutil.booleans.BooleanIterators;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class BooleanOpenHashSet
extends AbstractBooleanSet
implements Serializable,
Cloneable,
Hash {
    private static final long serialVersionUID = 0L;
    private static final boolean ASSERTS = false;
    protected transient boolean[] key;
    protected transient int mask;
    protected transient boolean containsNull;
    protected transient int n;
    protected transient int maxFill;
    protected int size;
    protected final float f;

    public BooleanOpenHashSet(int expected, float f2) {
        if (f2 <= 0.0f || f2 > 1.0f) {
            throw new IllegalArgumentException("Load factor must be greater than 0 and smaller than or equal to 1");
        }
        if (expected < 0) {
            throw new IllegalArgumentException("The expected number of elements must be nonnegative");
        }
        this.f = f2;
        this.n = HashCommon.arraySize(expected, f2);
        this.mask = this.n - 1;
        this.maxFill = HashCommon.maxFill(this.n, f2);
        this.key = new boolean[this.n + 1];
    }

    public BooleanOpenHashSet(int expected) {
        this(expected, 0.75f);
    }

    public BooleanOpenHashSet() {
        this(16, 0.75f);
    }

    public BooleanOpenHashSet(Collection<? extends Boolean> c2, float f2) {
        this(c2.size(), f2);
        this.addAll(c2);
    }

    public BooleanOpenHashSet(Collection<? extends Boolean> c2) {
        this(c2, 0.75f);
    }

    public BooleanOpenHashSet(BooleanCollection c2, float f2) {
        this(c2.size(), f2);
        this.addAll(c2);
    }

    public BooleanOpenHashSet(BooleanCollection c2) {
        this(c2, 0.75f);
    }

    public BooleanOpenHashSet(BooleanIterator i2, float f2) {
        this(16, f2);
        while (i2.hasNext()) {
            this.add(i2.nextBoolean());
        }
    }

    public BooleanOpenHashSet(BooleanIterator i2) {
        this(i2, 0.75f);
    }

    public BooleanOpenHashSet(Iterator<?> i2, float f2) {
        this(BooleanIterators.asBooleanIterator(i2), f2);
    }

    public BooleanOpenHashSet(Iterator<?> i2) {
        this(BooleanIterators.asBooleanIterator(i2));
    }

    public BooleanOpenHashSet(boolean[] a2, int offset, int length, float f2) {
        this(length < 0 ? 0 : length, f2);
        BooleanArrays.ensureOffsetLength(a2, offset, length);
        for (int i2 = 0; i2 < length; ++i2) {
            this.add(a2[offset + i2]);
        }
    }

    public BooleanOpenHashSet(boolean[] a2, int offset, int length) {
        this(a2, offset, length, 0.75f);
    }

    public BooleanOpenHashSet(boolean[] a2, float f2) {
        this(a2, 0, a2.length, f2);
    }

    public BooleanOpenHashSet(boolean[] a2) {
        this(a2, 0.75f);
    }

    private int realSize() {
        return this.containsNull ? this.size - 1 : this.size;
    }

    private void ensureCapacity(int capacity) {
        int needed = HashCommon.arraySize(capacity, this.f);
        if (needed > this.n) {
            this.rehash(needed);
        }
    }

    private void tryCapacity(long capacity) {
        int needed = (int)Math.min(0x40000000L, Math.max(2L, HashCommon.nextPowerOfTwo((long)Math.ceil((float)capacity / this.f))));
        if (needed > this.n) {
            this.rehash(needed);
        }
    }

    @Override
    public boolean addAll(BooleanCollection c2) {
        if ((double)this.f <= 0.5) {
            this.ensureCapacity(c2.size());
        } else {
            this.tryCapacity(this.size() + c2.size());
        }
        return super.addAll(c2);
    }

    @Override
    public boolean addAll(Collection<? extends Boolean> c2) {
        if ((double)this.f <= 0.5) {
            this.ensureCapacity(c2.size());
        } else {
            this.tryCapacity(this.size() + c2.size());
        }
        return super.addAll(c2);
    }

    @Override
    public boolean add(boolean k2) {
        if (!k2) {
            if (this.containsNull) {
                return false;
            }
            this.containsNull = true;
        } else {
            boolean[] key = this.key;
            int pos = (k2 ? 262886248 : -878682501) & this.mask;
            boolean curr = key[pos];
            if (curr) {
                if (curr == k2) {
                    return false;
                }
                while (curr = key[pos = pos + 1 & this.mask]) {
                    if (curr != k2) continue;
                    return false;
                }
            }
            key[pos] = k2;
        }
        if (this.size++ >= this.maxFill) {
            this.rehash(HashCommon.arraySize(this.size + 1, this.f));
        }
        return true;
    }

    protected final void shiftKeys(int pos) {
        boolean[] key = this.key;
        while (true) {
            boolean curr;
            int last = pos;
            pos = last + 1 & this.mask;
            while (true) {
                if (!(curr = key[pos])) {
                    key[last] = false;
                    return;
                }
                int slot = (curr ? 262886248 : -878682501) & this.mask;
                if (last <= pos ? last >= slot || slot > pos : last >= slot && slot > pos) break;
                pos = pos + 1 & this.mask;
            }
            key[last] = curr;
        }
    }

    private boolean removeEntry(int pos) {
        --this.size;
        this.shiftKeys(pos);
        if (this.size < this.maxFill / 4 && this.n > 16) {
            this.rehash(this.n / 2);
        }
        return true;
    }

    private boolean removeNullEntry() {
        this.containsNull = false;
        this.key[this.n] = false;
        --this.size;
        if (this.size < this.maxFill / 4 && this.n > 16) {
            this.rehash(this.n / 2);
        }
        return true;
    }

    @Override
    public boolean rem(boolean k2) {
        if (!k2) {
            if (this.containsNull) {
                return this.removeNullEntry();
            }
            return false;
        }
        boolean[] key = this.key;
        int pos = (k2 ? 262886248 : -878682501) & this.mask;
        boolean curr = key[pos];
        if (!curr) {
            return false;
        }
        if (k2 == curr) {
            return this.removeEntry(pos);
        }
        do {
            if (curr = key[pos = pos + 1 & this.mask]) continue;
            return false;
        } while (k2 != curr);
        return this.removeEntry(pos);
    }

    @Override
    public boolean contains(boolean k2) {
        if (!k2) {
            return this.containsNull;
        }
        boolean[] key = this.key;
        int pos = (k2 ? 262886248 : -878682501) & this.mask;
        boolean curr = key[pos];
        if (!curr) {
            return false;
        }
        if (k2 == curr) {
            return true;
        }
        do {
            if (curr = key[pos = pos + 1 & this.mask]) continue;
            return false;
        } while (k2 != curr);
        return true;
    }

    @Override
    public void clear() {
        if (this.size == 0) {
            return;
        }
        this.size = 0;
        this.containsNull = false;
        Arrays.fill(this.key, false);
    }

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public boolean isEmpty() {
        return this.size == 0;
    }

    @Deprecated
    public void growthFactor(int growthFactor) {
    }

    @Deprecated
    public int growthFactor() {
        return 16;
    }

    @Override
    public BooleanIterator iterator() {
        return new SetIterator();
    }

    @Deprecated
    public boolean rehash() {
        return true;
    }

    public boolean trim() {
        int l2 = HashCommon.arraySize(this.size, this.f);
        if (l2 >= this.n || this.size > HashCommon.maxFill(l2, this.f)) {
            return true;
        }
        try {
            this.rehash(l2);
        }
        catch (OutOfMemoryError cantDoIt) {
            return false;
        }
        return true;
    }

    public boolean trim(int n2) {
        int l2 = HashCommon.nextPowerOfTwo((int)Math.ceil((float)n2 / this.f));
        if (l2 >= n2 || this.size > HashCommon.maxFill(l2, this.f)) {
            return true;
        }
        try {
            this.rehash(l2);
        }
        catch (OutOfMemoryError cantDoIt) {
            return false;
        }
        return true;
    }

    protected void rehash(int newN) {
        boolean[] key = this.key;
        int mask = newN - 1;
        boolean[] newKey = new boolean[newN + 1];
        int i2 = this.n;
        int j2 = this.realSize();
        while (j2-- != 0) {
            while (!key[--i2]) {
            }
            int pos = (key[i2] ? 262886248 : -878682501) & mask;
            if (newKey[pos]) {
                while (newKey[pos = pos + 1 & mask]) {
                }
            }
            newKey[pos] = key[i2];
        }
        this.n = newN;
        this.mask = mask;
        this.maxFill = HashCommon.maxFill(this.n, this.f);
        this.key = newKey;
    }

    public BooleanOpenHashSet clone() {
        BooleanOpenHashSet c2;
        try {
            c2 = (BooleanOpenHashSet)super.clone();
        }
        catch (CloneNotSupportedException cantHappen) {
            throw new InternalError();
        }
        c2.key = (boolean[])this.key.clone();
        c2.containsNull = this.containsNull;
        return c2;
    }

    @Override
    public int hashCode() {
        int h2 = 0;
        int j2 = this.realSize();
        int i2 = 0;
        while (j2-- != 0) {
            while (!this.key[i2]) {
                ++i2;
            }
            h2 += this.key[i2] ? 1231 : 1237;
            ++i2;
        }
        return h2;
    }

    private void writeObject(ObjectOutputStream s2) throws IOException {
        BooleanIterator i2 = this.iterator();
        s2.defaultWriteObject();
        int j2 = this.size;
        while (j2-- != 0) {
            s2.writeBoolean(i2.nextBoolean());
        }
    }

    private void readObject(ObjectInputStream s2) throws IOException, ClassNotFoundException {
        s2.defaultReadObject();
        this.n = HashCommon.arraySize(this.size, this.f);
        this.maxFill = HashCommon.maxFill(this.n, this.f);
        this.mask = this.n - 1;
        this.key = new boolean[this.n + 1];
        boolean[] key = this.key;
        int i2 = this.size;
        while (i2-- != 0) {
            int pos;
            boolean k2 = s2.readBoolean();
            if (!k2) {
                pos = this.n;
                this.containsNull = true;
            } else {
                pos = (k2 ? 262886248 : -878682501) & this.mask;
                if (key[pos]) {
                    while (key[pos = pos + 1 & this.mask]) {
                    }
                }
            }
            key[pos] = k2;
        }
    }

    private void checkTable() {
    }

    private class SetIterator
    extends AbstractBooleanIterator {
        int pos;
        int last;
        int c;
        boolean mustReturnNull;
        BooleanArrayList wrapped;

        private SetIterator() {
            this.pos = BooleanOpenHashSet.this.n;
            this.last = -1;
            this.c = BooleanOpenHashSet.this.size;
            this.mustReturnNull = BooleanOpenHashSet.this.containsNull;
        }

        @Override
        public boolean hasNext() {
            return this.c != 0;
        }

        @Override
        public boolean nextBoolean() {
            if (!this.hasNext()) {
                throw new NoSuchElementException();
            }
            --this.c;
            if (this.mustReturnNull) {
                this.mustReturnNull = false;
                this.last = BooleanOpenHashSet.this.n;
                return BooleanOpenHashSet.this.key[BooleanOpenHashSet.this.n];
            }
            boolean[] key = BooleanOpenHashSet.this.key;
            do {
                if (--this.pos >= 0) continue;
                this.last = Integer.MIN_VALUE;
                return this.wrapped.getBoolean(-this.pos - 1);
            } while (!key[this.pos]);
            this.last = this.pos;
            return key[this.last];
        }

        private final void shiftKeys(int pos) {
            boolean[] key = BooleanOpenHashSet.this.key;
            while (true) {
                boolean curr;
                int last = pos;
                pos = last + 1 & BooleanOpenHashSet.this.mask;
                while (true) {
                    if (!(curr = key[pos])) {
                        key[last] = false;
                        return;
                    }
                    int slot = (curr ? 262886248 : -878682501) & BooleanOpenHashSet.this.mask;
                    if (last <= pos ? last >= slot || slot > pos : last >= slot && slot > pos) break;
                    pos = pos + 1 & BooleanOpenHashSet.this.mask;
                }
                if (pos < last) {
                    if (this.wrapped == null) {
                        this.wrapped = new BooleanArrayList(2);
                    }
                    this.wrapped.add(key[pos]);
                }
                key[last] = curr;
            }
        }

        @Override
        public void remove() {
            if (this.last == -1) {
                throw new IllegalStateException();
            }
            if (this.last == BooleanOpenHashSet.this.n) {
                BooleanOpenHashSet.this.containsNull = false;
                BooleanOpenHashSet.this.key[BooleanOpenHashSet.this.n] = false;
            } else if (this.pos >= 0) {
                this.shiftKeys(this.last);
            } else {
                BooleanOpenHashSet.this.rem(this.wrapped.getBoolean(-this.pos - 1));
                this.last = -1;
                return;
            }
            --BooleanOpenHashSet.this.size;
            this.last = -1;
        }
    }
}

