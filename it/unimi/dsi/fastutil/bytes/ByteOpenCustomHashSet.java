package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.bytes.AbstractByteIterator;
import it.unimi.dsi.fastutil.bytes.AbstractByteSet;
import it.unimi.dsi.fastutil.bytes.ByteArrayList;
import it.unimi.dsi.fastutil.bytes.ByteArrays;
import it.unimi.dsi.fastutil.bytes.ByteCollection;
import it.unimi.dsi.fastutil.bytes.ByteHash;
import it.unimi.dsi.fastutil.bytes.ByteIterator;
import it.unimi.dsi.fastutil.bytes.ByteIterators;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class ByteOpenCustomHashSet
extends AbstractByteSet
implements Serializable,
Cloneable,
Hash {
    private static final long serialVersionUID = 0L;
    private static final boolean ASSERTS = false;
    protected transient byte[] key;
    protected transient int mask;
    protected transient boolean containsNull;
    protected ByteHash.Strategy strategy;
    protected transient int n;
    protected transient int maxFill;
    protected int size;
    protected final float f;

    public ByteOpenCustomHashSet(int expected, float f2, ByteHash.Strategy strategy) {
        this.strategy = strategy;
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
        this.key = new byte[this.n + 1];
    }

    public ByteOpenCustomHashSet(int expected, ByteHash.Strategy strategy) {
        this(expected, 0.75f, strategy);
    }

    public ByteOpenCustomHashSet(ByteHash.Strategy strategy) {
        this(16, 0.75f, strategy);
    }

    public ByteOpenCustomHashSet(Collection<? extends Byte> c2, float f2, ByteHash.Strategy strategy) {
        this(c2.size(), f2, strategy);
        this.addAll(c2);
    }

    public ByteOpenCustomHashSet(Collection<? extends Byte> c2, ByteHash.Strategy strategy) {
        this(c2, 0.75f, strategy);
    }

    public ByteOpenCustomHashSet(ByteCollection c2, float f2, ByteHash.Strategy strategy) {
        this(c2.size(), f2, strategy);
        this.addAll(c2);
    }

    public ByteOpenCustomHashSet(ByteCollection c2, ByteHash.Strategy strategy) {
        this(c2, 0.75f, strategy);
    }

    public ByteOpenCustomHashSet(ByteIterator i2, float f2, ByteHash.Strategy strategy) {
        this(16, f2, strategy);
        while (i2.hasNext()) {
            this.add(i2.nextByte());
        }
    }

    public ByteOpenCustomHashSet(ByteIterator i2, ByteHash.Strategy strategy) {
        this(i2, 0.75f, strategy);
    }

    public ByteOpenCustomHashSet(Iterator<?> i2, float f2, ByteHash.Strategy strategy) {
        this(ByteIterators.asByteIterator(i2), f2, strategy);
    }

    public ByteOpenCustomHashSet(Iterator<?> i2, ByteHash.Strategy strategy) {
        this(ByteIterators.asByteIterator(i2), strategy);
    }

    public ByteOpenCustomHashSet(byte[] a2, int offset, int length, float f2, ByteHash.Strategy strategy) {
        this(length < 0 ? 0 : length, f2, strategy);
        ByteArrays.ensureOffsetLength(a2, offset, length);
        for (int i2 = 0; i2 < length; ++i2) {
            this.add(a2[offset + i2]);
        }
    }

    public ByteOpenCustomHashSet(byte[] a2, int offset, int length, ByteHash.Strategy strategy) {
        this(a2, offset, length, 0.75f, strategy);
    }

    public ByteOpenCustomHashSet(byte[] a2, float f2, ByteHash.Strategy strategy) {
        this(a2, 0, a2.length, f2, strategy);
    }

    public ByteOpenCustomHashSet(byte[] a2, ByteHash.Strategy strategy) {
        this(a2, 0.75f, strategy);
    }

    public ByteHash.Strategy strategy() {
        return this.strategy;
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
    public boolean addAll(ByteCollection c2) {
        if ((double)this.f <= 0.5) {
            this.ensureCapacity(c2.size());
        } else {
            this.tryCapacity(this.size() + c2.size());
        }
        return super.addAll(c2);
    }

    @Override
    public boolean addAll(Collection<? extends Byte> c2) {
        if ((double)this.f <= 0.5) {
            this.ensureCapacity(c2.size());
        } else {
            this.tryCapacity(this.size() + c2.size());
        }
        return super.addAll(c2);
    }

    @Override
    public boolean add(byte k2) {
        if (this.strategy.equals(k2, (byte)0)) {
            if (this.containsNull) {
                return false;
            }
            this.containsNull = true;
            this.key[this.n] = k2;
        } else {
            byte[] key = this.key;
            int pos = HashCommon.mix(this.strategy.hashCode(k2)) & this.mask;
            byte curr = key[pos];
            if (curr != 0) {
                if (this.strategy.equals(curr, k2)) {
                    return false;
                }
                while ((curr = key[pos = pos + 1 & this.mask]) != 0) {
                    if (!this.strategy.equals(curr, k2)) continue;
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
        byte[] key = this.key;
        while (true) {
            byte curr;
            int last = pos;
            pos = last + 1 & this.mask;
            while (true) {
                if ((curr = key[pos]) == 0) {
                    key[last] = 0;
                    return;
                }
                int slot = HashCommon.mix(this.strategy.hashCode(curr)) & this.mask;
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
        this.key[this.n] = 0;
        --this.size;
        if (this.size < this.maxFill / 4 && this.n > 16) {
            this.rehash(this.n / 2);
        }
        return true;
    }

    @Override
    public boolean rem(byte k2) {
        if (this.strategy.equals(k2, (byte)0)) {
            if (this.containsNull) {
                return this.removeNullEntry();
            }
            return false;
        }
        byte[] key = this.key;
        int pos = HashCommon.mix(this.strategy.hashCode(k2)) & this.mask;
        byte curr = key[pos];
        if (curr == 0) {
            return false;
        }
        if (this.strategy.equals(k2, curr)) {
            return this.removeEntry(pos);
        }
        do {
            if ((curr = key[pos = pos + 1 & this.mask]) != 0) continue;
            return false;
        } while (!this.strategy.equals(k2, curr));
        return this.removeEntry(pos);
    }

    @Override
    public boolean contains(byte k2) {
        if (this.strategy.equals(k2, (byte)0)) {
            return this.containsNull;
        }
        byte[] key = this.key;
        int pos = HashCommon.mix(this.strategy.hashCode(k2)) & this.mask;
        byte curr = key[pos];
        if (curr == 0) {
            return false;
        }
        if (this.strategy.equals(k2, curr)) {
            return true;
        }
        do {
            if ((curr = key[pos = pos + 1 & this.mask]) != 0) continue;
            return false;
        } while (!this.strategy.equals(k2, curr));
        return true;
    }

    @Override
    public void clear() {
        if (this.size == 0) {
            return;
        }
        this.size = 0;
        this.containsNull = false;
        Arrays.fill(this.key, (byte)0);
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
    public ByteIterator iterator() {
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
        byte[] key = this.key;
        int mask = newN - 1;
        byte[] newKey = new byte[newN + 1];
        int i2 = this.n;
        int j2 = this.realSize();
        while (j2-- != 0) {
            while (key[--i2] == 0) {
            }
            int pos = HashCommon.mix(this.strategy.hashCode(key[i2])) & mask;
            if (newKey[pos] != 0) {
                while (newKey[pos = pos + 1 & mask] != 0) {
                }
            }
            newKey[pos] = key[i2];
        }
        this.n = newN;
        this.mask = mask;
        this.maxFill = HashCommon.maxFill(this.n, this.f);
        this.key = newKey;
    }

    public ByteOpenCustomHashSet clone() {
        ByteOpenCustomHashSet c2;
        try {
            c2 = (ByteOpenCustomHashSet)super.clone();
        }
        catch (CloneNotSupportedException cantHappen) {
            throw new InternalError();
        }
        c2.key = (byte[])this.key.clone();
        c2.containsNull = this.containsNull;
        c2.strategy = this.strategy;
        return c2;
    }

    @Override
    public int hashCode() {
        int h2 = 0;
        int j2 = this.realSize();
        int i2 = 0;
        while (j2-- != 0) {
            while (this.key[i2] == 0) {
                ++i2;
            }
            h2 += this.strategy.hashCode(this.key[i2]);
            ++i2;
        }
        return h2;
    }

    private void writeObject(ObjectOutputStream s2) throws IOException {
        ByteIterator i2 = this.iterator();
        s2.defaultWriteObject();
        int j2 = this.size;
        while (j2-- != 0) {
            s2.writeByte(i2.nextByte());
        }
    }

    private void readObject(ObjectInputStream s2) throws IOException, ClassNotFoundException {
        s2.defaultReadObject();
        this.n = HashCommon.arraySize(this.size, this.f);
        this.maxFill = HashCommon.maxFill(this.n, this.f);
        this.mask = this.n - 1;
        this.key = new byte[this.n + 1];
        byte[] key = this.key;
        int i2 = this.size;
        while (i2-- != 0) {
            int pos;
            byte k2 = s2.readByte();
            if (this.strategy.equals(k2, (byte)0)) {
                pos = this.n;
                this.containsNull = true;
            } else {
                pos = HashCommon.mix(this.strategy.hashCode(k2)) & this.mask;
                if (key[pos] != 0) {
                    while (key[pos = pos + 1 & this.mask] != 0) {
                    }
                }
            }
            key[pos] = k2;
        }
    }

    private void checkTable() {
    }

    private class SetIterator
    extends AbstractByteIterator {
        int pos;
        int last;
        int c;
        boolean mustReturnNull;
        ByteArrayList wrapped;

        private SetIterator() {
            this.pos = ByteOpenCustomHashSet.this.n;
            this.last = -1;
            this.c = ByteOpenCustomHashSet.this.size;
            this.mustReturnNull = ByteOpenCustomHashSet.this.containsNull;
        }

        @Override
        public boolean hasNext() {
            return this.c != 0;
        }

        @Override
        public byte nextByte() {
            if (!this.hasNext()) {
                throw new NoSuchElementException();
            }
            --this.c;
            if (this.mustReturnNull) {
                this.mustReturnNull = false;
                this.last = ByteOpenCustomHashSet.this.n;
                return ByteOpenCustomHashSet.this.key[ByteOpenCustomHashSet.this.n];
            }
            byte[] key = ByteOpenCustomHashSet.this.key;
            do {
                if (--this.pos >= 0) continue;
                this.last = Integer.MIN_VALUE;
                return this.wrapped.getByte(-this.pos - 1);
            } while (key[this.pos] == 0);
            this.last = this.pos;
            return key[this.last];
        }

        private final void shiftKeys(int pos) {
            byte[] key = ByteOpenCustomHashSet.this.key;
            while (true) {
                byte curr;
                int last = pos;
                pos = last + 1 & ByteOpenCustomHashSet.this.mask;
                while (true) {
                    if ((curr = key[pos]) == 0) {
                        key[last] = 0;
                        return;
                    }
                    int slot = HashCommon.mix(ByteOpenCustomHashSet.this.strategy.hashCode(curr)) & ByteOpenCustomHashSet.this.mask;
                    if (last <= pos ? last >= slot || slot > pos : last >= slot && slot > pos) break;
                    pos = pos + 1 & ByteOpenCustomHashSet.this.mask;
                }
                if (pos < last) {
                    if (this.wrapped == null) {
                        this.wrapped = new ByteArrayList(2);
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
            if (this.last == ByteOpenCustomHashSet.this.n) {
                ByteOpenCustomHashSet.this.containsNull = false;
                ByteOpenCustomHashSet.this.key[ByteOpenCustomHashSet.this.n] = 0;
            } else if (this.pos >= 0) {
                this.shiftKeys(this.last);
            } else {
                ByteOpenCustomHashSet.this.rem(this.wrapped.getByte(-this.pos - 1));
                this.last = -1;
                return;
            }
            --ByteOpenCustomHashSet.this.size;
            this.last = -1;
        }
    }
}

