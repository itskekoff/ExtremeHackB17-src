package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.bytes.AbstractByte2ShortMap;
import it.unimi.dsi.fastutil.bytes.AbstractByteSet;
import it.unimi.dsi.fastutil.bytes.Byte2ShortMap;
import it.unimi.dsi.fastutil.bytes.ByteArrayList;
import it.unimi.dsi.fastutil.bytes.ByteHash;
import it.unimi.dsi.fastutil.bytes.ByteIterator;
import it.unimi.dsi.fastutil.bytes.ByteSet;
import it.unimi.dsi.fastutil.objects.AbstractObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.shorts.AbstractShortCollection;
import it.unimi.dsi.fastutil.shorts.ShortCollection;
import it.unimi.dsi.fastutil.shorts.ShortIterator;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Map;
import java.util.NoSuchElementException;

public class Byte2ShortOpenCustomHashMap
extends AbstractByte2ShortMap
implements Serializable,
Cloneable,
Hash {
    private static final long serialVersionUID = 0L;
    private static final boolean ASSERTS = false;
    protected transient byte[] key;
    protected transient short[] value;
    protected transient int mask;
    protected transient boolean containsNullKey;
    protected ByteHash.Strategy strategy;
    protected transient int n;
    protected transient int maxFill;
    protected int size;
    protected final float f;
    protected transient Byte2ShortMap.FastEntrySet entries;
    protected transient ByteSet keys;
    protected transient ShortCollection values;

    public Byte2ShortOpenCustomHashMap(int expected, float f2, ByteHash.Strategy strategy) {
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
        this.value = new short[this.n + 1];
    }

    public Byte2ShortOpenCustomHashMap(int expected, ByteHash.Strategy strategy) {
        this(expected, 0.75f, strategy);
    }

    public Byte2ShortOpenCustomHashMap(ByteHash.Strategy strategy) {
        this(16, 0.75f, strategy);
    }

    public Byte2ShortOpenCustomHashMap(Map<? extends Byte, ? extends Short> m2, float f2, ByteHash.Strategy strategy) {
        this(m2.size(), f2, strategy);
        this.putAll(m2);
    }

    public Byte2ShortOpenCustomHashMap(Map<? extends Byte, ? extends Short> m2, ByteHash.Strategy strategy) {
        this(m2, 0.75f, strategy);
    }

    public Byte2ShortOpenCustomHashMap(Byte2ShortMap m2, float f2, ByteHash.Strategy strategy) {
        this(m2.size(), f2, strategy);
        this.putAll(m2);
    }

    public Byte2ShortOpenCustomHashMap(Byte2ShortMap m2, ByteHash.Strategy strategy) {
        this(m2, 0.75f, strategy);
    }

    public Byte2ShortOpenCustomHashMap(byte[] k2, short[] v2, float f2, ByteHash.Strategy strategy) {
        this(k2.length, f2, strategy);
        if (k2.length != v2.length) {
            throw new IllegalArgumentException("The key array and the value array have different lengths (" + k2.length + " and " + v2.length + ")");
        }
        for (int i2 = 0; i2 < k2.length; ++i2) {
            this.put(k2[i2], v2[i2]);
        }
    }

    public Byte2ShortOpenCustomHashMap(byte[] k2, short[] v2, ByteHash.Strategy strategy) {
        this(k2, v2, 0.75f, strategy);
    }

    public ByteHash.Strategy strategy() {
        return this.strategy;
    }

    private int realSize() {
        return this.containsNullKey ? this.size - 1 : this.size;
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

    private short removeEntry(int pos) {
        short oldValue = this.value[pos];
        --this.size;
        this.shiftKeys(pos);
        if (this.size < this.maxFill / 4 && this.n > 16) {
            this.rehash(this.n / 2);
        }
        return oldValue;
    }

    private short removeNullEntry() {
        this.containsNullKey = false;
        short oldValue = this.value[this.n];
        --this.size;
        if (this.size < this.maxFill / 4 && this.n > 16) {
            this.rehash(this.n / 2);
        }
        return oldValue;
    }

    @Override
    public void putAll(Map<? extends Byte, ? extends Short> m2) {
        if ((double)this.f <= 0.5) {
            this.ensureCapacity(m2.size());
        } else {
            this.tryCapacity(this.size() + m2.size());
        }
        super.putAll(m2);
    }

    private int insert(byte k2, short v2) {
        int pos;
        if (this.strategy.equals(k2, (byte)0)) {
            if (this.containsNullKey) {
                return this.n;
            }
            this.containsNullKey = true;
            pos = this.n;
        } else {
            byte[] key = this.key;
            pos = HashCommon.mix(this.strategy.hashCode(k2)) & this.mask;
            byte curr = key[pos];
            if (curr != 0) {
                if (this.strategy.equals(curr, k2)) {
                    return pos;
                }
                while ((curr = key[pos = pos + 1 & this.mask]) != 0) {
                    if (!this.strategy.equals(curr, k2)) continue;
                    return pos;
                }
            }
        }
        this.key[pos] = k2;
        this.value[pos] = v2;
        if (this.size++ >= this.maxFill) {
            this.rehash(HashCommon.arraySize(this.size + 1, this.f));
        }
        return -1;
    }

    @Override
    public short put(byte k2, short v2) {
        int pos = this.insert(k2, v2);
        if (pos < 0) {
            return this.defRetValue;
        }
        short oldValue = this.value[pos];
        this.value[pos] = v2;
        return oldValue;
    }

    @Override
    @Deprecated
    public Short put(Byte ok2, Short ov) {
        short v2 = ov;
        int pos = this.insert(ok2, v2);
        if (pos < 0) {
            return null;
        }
        short oldValue = this.value[pos];
        this.value[pos] = v2;
        return oldValue;
    }

    private short addToValue(int pos, short incr) {
        short oldValue = this.value[pos];
        this.value[pos] = (short)(oldValue + incr);
        return oldValue;
    }

    public short addTo(byte k2, short incr) {
        int pos;
        if (this.strategy.equals(k2, (byte)0)) {
            if (this.containsNullKey) {
                return this.addToValue(this.n, incr);
            }
            pos = this.n;
            this.containsNullKey = true;
        } else {
            byte[] key = this.key;
            pos = HashCommon.mix(this.strategy.hashCode(k2)) & this.mask;
            byte curr = key[pos];
            if (curr != 0) {
                if (this.strategy.equals(curr, k2)) {
                    return this.addToValue(pos, incr);
                }
                while ((curr = key[pos = pos + 1 & this.mask]) != 0) {
                    if (!this.strategy.equals(curr, k2)) continue;
                    return this.addToValue(pos, incr);
                }
            }
        }
        this.key[pos] = k2;
        this.value[pos] = (short)(this.defRetValue + incr);
        if (this.size++ >= this.maxFill) {
            this.rehash(HashCommon.arraySize(this.size + 1, this.f));
        }
        return this.defRetValue;
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
            this.value[last] = this.value[pos];
        }
    }

    @Override
    public short remove(byte k2) {
        if (this.strategy.equals(k2, (byte)0)) {
            if (this.containsNullKey) {
                return this.removeNullEntry();
            }
            return this.defRetValue;
        }
        byte[] key = this.key;
        int pos = HashCommon.mix(this.strategy.hashCode(k2)) & this.mask;
        byte curr = key[pos];
        if (curr == 0) {
            return this.defRetValue;
        }
        if (this.strategy.equals(k2, curr)) {
            return this.removeEntry(pos);
        }
        do {
            if ((curr = key[pos = pos + 1 & this.mask]) != 0) continue;
            return this.defRetValue;
        } while (!this.strategy.equals(k2, curr));
        return this.removeEntry(pos);
    }

    @Override
    @Deprecated
    public Short remove(Object ok2) {
        byte k2 = (Byte)ok2;
        if (this.strategy.equals(k2, (byte)0)) {
            if (this.containsNullKey) {
                return this.removeNullEntry();
            }
            return null;
        }
        byte[] key = this.key;
        int pos = HashCommon.mix(this.strategy.hashCode(k2)) & this.mask;
        byte curr = key[pos];
        if (curr == 0) {
            return null;
        }
        if (this.strategy.equals(curr, k2)) {
            return this.removeEntry(pos);
        }
        do {
            if ((curr = key[pos = pos + 1 & this.mask]) != 0) continue;
            return null;
        } while (!this.strategy.equals(curr, k2));
        return this.removeEntry(pos);
    }

    @Deprecated
    public Short get(Byte ok2) {
        if (ok2 == null) {
            return null;
        }
        byte k2 = ok2;
        if (this.strategy.equals(k2, (byte)0)) {
            return this.containsNullKey ? Short.valueOf(this.value[this.n]) : null;
        }
        byte[] key = this.key;
        int pos = HashCommon.mix(this.strategy.hashCode(k2)) & this.mask;
        byte curr = key[pos];
        if (curr == 0) {
            return null;
        }
        if (this.strategy.equals(k2, curr)) {
            return this.value[pos];
        }
        do {
            if ((curr = key[pos = pos + 1 & this.mask]) != 0) continue;
            return null;
        } while (!this.strategy.equals(k2, curr));
        return this.value[pos];
    }

    @Override
    public short get(byte k2) {
        if (this.strategy.equals(k2, (byte)0)) {
            return this.containsNullKey ? this.value[this.n] : this.defRetValue;
        }
        byte[] key = this.key;
        int pos = HashCommon.mix(this.strategy.hashCode(k2)) & this.mask;
        byte curr = key[pos];
        if (curr == 0) {
            return this.defRetValue;
        }
        if (this.strategy.equals(k2, curr)) {
            return this.value[pos];
        }
        do {
            if ((curr = key[pos = pos + 1 & this.mask]) != 0) continue;
            return this.defRetValue;
        } while (!this.strategy.equals(k2, curr));
        return this.value[pos];
    }

    @Override
    public boolean containsKey(byte k2) {
        if (this.strategy.equals(k2, (byte)0)) {
            return this.containsNullKey;
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
    public boolean containsValue(short v2) {
        short[] value = this.value;
        byte[] key = this.key;
        if (this.containsNullKey && value[this.n] == v2) {
            return true;
        }
        int i2 = this.n;
        while (i2-- != 0) {
            if (key[i2] == 0 || value[i2] != v2) continue;
            return true;
        }
        return false;
    }

    @Override
    public void clear() {
        if (this.size == 0) {
            return;
        }
        this.size = 0;
        this.containsNullKey = false;
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

    public Byte2ShortMap.FastEntrySet byte2ShortEntrySet() {
        if (this.entries == null) {
            this.entries = new MapEntrySet();
        }
        return this.entries;
    }

    @Override
    public ByteSet keySet() {
        if (this.keys == null) {
            this.keys = new KeySet();
        }
        return this.keys;
    }

    @Override
    public ShortCollection values() {
        if (this.values == null) {
            this.values = new AbstractShortCollection(){

                @Override
                public ShortIterator iterator() {
                    return new ValueIterator();
                }

                @Override
                public int size() {
                    return Byte2ShortOpenCustomHashMap.this.size;
                }

                @Override
                public boolean contains(short v2) {
                    return Byte2ShortOpenCustomHashMap.this.containsValue(v2);
                }

                @Override
                public void clear() {
                    Byte2ShortOpenCustomHashMap.this.clear();
                }
            };
        }
        return this.values;
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
        short[] value = this.value;
        int mask = newN - 1;
        byte[] newKey = new byte[newN + 1];
        short[] newValue = new short[newN + 1];
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
            newValue[pos] = value[i2];
        }
        newValue[newN] = value[this.n];
        this.n = newN;
        this.mask = mask;
        this.maxFill = HashCommon.maxFill(this.n, this.f);
        this.key = newKey;
        this.value = newValue;
    }

    public Byte2ShortOpenCustomHashMap clone() {
        Byte2ShortOpenCustomHashMap c2;
        try {
            c2 = (Byte2ShortOpenCustomHashMap)super.clone();
        }
        catch (CloneNotSupportedException cantHappen) {
            throw new InternalError();
        }
        c2.keys = null;
        c2.values = null;
        c2.entries = null;
        c2.containsNullKey = this.containsNullKey;
        c2.key = (byte[])this.key.clone();
        c2.value = (short[])this.value.clone();
        c2.strategy = this.strategy;
        return c2;
    }

    @Override
    public int hashCode() {
        int h2 = 0;
        int j2 = this.realSize();
        int i2 = 0;
        int t2 = 0;
        while (j2-- != 0) {
            while (this.key[i2] == 0) {
                ++i2;
            }
            t2 = this.strategy.hashCode(this.key[i2]);
            h2 += (t2 ^= this.value[i2]);
            ++i2;
        }
        if (this.containsNullKey) {
            h2 += this.value[this.n];
        }
        return h2;
    }

    private void writeObject(ObjectOutputStream s2) throws IOException {
        byte[] key = this.key;
        short[] value = this.value;
        MapIterator i2 = new MapIterator();
        s2.defaultWriteObject();
        int j2 = this.size;
        while (j2-- != 0) {
            int e2 = i2.nextEntry();
            s2.writeByte(key[e2]);
            s2.writeShort(value[e2]);
        }
    }

    private void readObject(ObjectInputStream s2) throws IOException, ClassNotFoundException {
        s2.defaultReadObject();
        this.n = HashCommon.arraySize(this.size, this.f);
        this.maxFill = HashCommon.maxFill(this.n, this.f);
        this.mask = this.n - 1;
        this.key = new byte[this.n + 1];
        byte[] key = this.key;
        this.value = new short[this.n + 1];
        short[] value = this.value;
        int i2 = this.size;
        while (i2-- != 0) {
            int pos;
            byte k2 = s2.readByte();
            short v2 = s2.readShort();
            if (this.strategy.equals(k2, (byte)0)) {
                pos = this.n;
                this.containsNullKey = true;
            } else {
                pos = HashCommon.mix(this.strategy.hashCode(k2)) & this.mask;
                while (key[pos] != 0) {
                    pos = pos + 1 & this.mask;
                }
            }
            key[pos] = k2;
            value[pos] = v2;
        }
    }

    private void checkTable() {
    }

    private final class ValueIterator
    extends MapIterator
    implements ShortIterator {
        @Override
        public short nextShort() {
            return Byte2ShortOpenCustomHashMap.this.value[this.nextEntry()];
        }

        @Override
        @Deprecated
        public Short next() {
            return Byte2ShortOpenCustomHashMap.this.value[this.nextEntry()];
        }
    }

    private final class KeySet
    extends AbstractByteSet {
        private KeySet() {
        }

        @Override
        public ByteIterator iterator() {
            return new KeyIterator();
        }

        @Override
        public int size() {
            return Byte2ShortOpenCustomHashMap.this.size;
        }

        @Override
        public boolean contains(byte k2) {
            return Byte2ShortOpenCustomHashMap.this.containsKey(k2);
        }

        @Override
        public boolean rem(byte k2) {
            int oldSize = Byte2ShortOpenCustomHashMap.this.size;
            Byte2ShortOpenCustomHashMap.this.remove(k2);
            return Byte2ShortOpenCustomHashMap.this.size != oldSize;
        }

        @Override
        public void clear() {
            Byte2ShortOpenCustomHashMap.this.clear();
        }
    }

    private final class KeyIterator
    extends MapIterator
    implements ByteIterator {
        @Override
        public byte nextByte() {
            return Byte2ShortOpenCustomHashMap.this.key[this.nextEntry()];
        }

        @Override
        public Byte next() {
            return Byte2ShortOpenCustomHashMap.this.key[this.nextEntry()];
        }
    }

    private final class MapEntrySet
    extends AbstractObjectSet<Byte2ShortMap.Entry>
    implements Byte2ShortMap.FastEntrySet {
        private MapEntrySet() {
        }

        @Override
        public ObjectIterator<Byte2ShortMap.Entry> iterator() {
            return new EntryIterator();
        }

        @Override
        public ObjectIterator<Byte2ShortMap.Entry> fastIterator() {
            return new FastEntryIterator();
        }

        @Override
        public boolean contains(Object o2) {
            if (!(o2 instanceof Map.Entry)) {
                return false;
            }
            Map.Entry e2 = (Map.Entry)o2;
            if (e2.getKey() == null || !(e2.getKey() instanceof Byte)) {
                return false;
            }
            if (e2.getValue() == null || !(e2.getValue() instanceof Short)) {
                return false;
            }
            byte k2 = (Byte)e2.getKey();
            short v2 = (Short)e2.getValue();
            if (Byte2ShortOpenCustomHashMap.this.strategy.equals(k2, (byte)0)) {
                return Byte2ShortOpenCustomHashMap.this.containsNullKey && Byte2ShortOpenCustomHashMap.this.value[Byte2ShortOpenCustomHashMap.this.n] == v2;
            }
            byte[] key = Byte2ShortOpenCustomHashMap.this.key;
            int pos = HashCommon.mix(Byte2ShortOpenCustomHashMap.this.strategy.hashCode(k2)) & Byte2ShortOpenCustomHashMap.this.mask;
            byte curr = key[pos];
            if (curr == 0) {
                return false;
            }
            if (Byte2ShortOpenCustomHashMap.this.strategy.equals(k2, curr)) {
                return Byte2ShortOpenCustomHashMap.this.value[pos] == v2;
            }
            do {
                if ((curr = key[pos = pos + 1 & Byte2ShortOpenCustomHashMap.this.mask]) != 0) continue;
                return false;
            } while (!Byte2ShortOpenCustomHashMap.this.strategy.equals(k2, curr));
            return Byte2ShortOpenCustomHashMap.this.value[pos] == v2;
        }

        @Override
        public boolean rem(Object o2) {
            if (!(o2 instanceof Map.Entry)) {
                return false;
            }
            Map.Entry e2 = (Map.Entry)o2;
            if (e2.getKey() == null || !(e2.getKey() instanceof Byte)) {
                return false;
            }
            if (e2.getValue() == null || !(e2.getValue() instanceof Short)) {
                return false;
            }
            byte k2 = (Byte)e2.getKey();
            short v2 = (Short)e2.getValue();
            if (Byte2ShortOpenCustomHashMap.this.strategy.equals(k2, (byte)0)) {
                if (Byte2ShortOpenCustomHashMap.this.containsNullKey && Byte2ShortOpenCustomHashMap.this.value[Byte2ShortOpenCustomHashMap.this.n] == v2) {
                    Byte2ShortOpenCustomHashMap.this.removeNullEntry();
                    return true;
                }
                return false;
            }
            byte[] key = Byte2ShortOpenCustomHashMap.this.key;
            int pos = HashCommon.mix(Byte2ShortOpenCustomHashMap.this.strategy.hashCode(k2)) & Byte2ShortOpenCustomHashMap.this.mask;
            byte curr = key[pos];
            if (curr == 0) {
                return false;
            }
            if (Byte2ShortOpenCustomHashMap.this.strategy.equals(curr, k2)) {
                if (Byte2ShortOpenCustomHashMap.this.value[pos] == v2) {
                    Byte2ShortOpenCustomHashMap.this.removeEntry(pos);
                    return true;
                }
                return false;
            }
            do {
                if ((curr = key[pos = pos + 1 & Byte2ShortOpenCustomHashMap.this.mask]) != 0) continue;
                return false;
            } while (!Byte2ShortOpenCustomHashMap.this.strategy.equals(curr, k2) || Byte2ShortOpenCustomHashMap.this.value[pos] != v2);
            Byte2ShortOpenCustomHashMap.this.removeEntry(pos);
            return true;
        }

        @Override
        public int size() {
            return Byte2ShortOpenCustomHashMap.this.size;
        }

        @Override
        public void clear() {
            Byte2ShortOpenCustomHashMap.this.clear();
        }
    }

    private class FastEntryIterator
    extends MapIterator
    implements ObjectIterator<Byte2ShortMap.Entry> {
        private final MapEntry entry;

        private FastEntryIterator() {
            this.entry = new MapEntry();
        }

        @Override
        public MapEntry next() {
            this.entry.index = this.nextEntry();
            return this.entry;
        }
    }

    private class EntryIterator
    extends MapIterator
    implements ObjectIterator<Byte2ShortMap.Entry> {
        private MapEntry entry;

        private EntryIterator() {
        }

        @Override
        public Byte2ShortMap.Entry next() {
            this.entry = new MapEntry(this.nextEntry());
            return this.entry;
        }

        @Override
        public void remove() {
            super.remove();
            this.entry.index = -1;
        }
    }

    private class MapIterator {
        int pos;
        int last;
        int c;
        boolean mustReturnNullKey;
        ByteArrayList wrapped;

        private MapIterator() {
            this.pos = Byte2ShortOpenCustomHashMap.this.n;
            this.last = -1;
            this.c = Byte2ShortOpenCustomHashMap.this.size;
            this.mustReturnNullKey = Byte2ShortOpenCustomHashMap.this.containsNullKey;
        }

        public boolean hasNext() {
            return this.c != 0;
        }

        public int nextEntry() {
            if (!this.hasNext()) {
                throw new NoSuchElementException();
            }
            --this.c;
            if (this.mustReturnNullKey) {
                this.mustReturnNullKey = false;
                this.last = Byte2ShortOpenCustomHashMap.this.n;
                return this.last;
            }
            byte[] key = Byte2ShortOpenCustomHashMap.this.key;
            do {
                if (--this.pos >= 0) continue;
                this.last = Integer.MIN_VALUE;
                byte k2 = this.wrapped.getByte(-this.pos - 1);
                int p2 = HashCommon.mix(Byte2ShortOpenCustomHashMap.this.strategy.hashCode(k2)) & Byte2ShortOpenCustomHashMap.this.mask;
                while (!Byte2ShortOpenCustomHashMap.this.strategy.equals(k2, key[p2])) {
                    p2 = p2 + 1 & Byte2ShortOpenCustomHashMap.this.mask;
                }
                return p2;
            } while (key[this.pos] == 0);
            this.last = this.pos;
            return this.last;
        }

        private final void shiftKeys(int pos) {
            byte[] key = Byte2ShortOpenCustomHashMap.this.key;
            while (true) {
                byte curr;
                int last = pos;
                pos = last + 1 & Byte2ShortOpenCustomHashMap.this.mask;
                while (true) {
                    if ((curr = key[pos]) == 0) {
                        key[last] = 0;
                        return;
                    }
                    int slot = HashCommon.mix(Byte2ShortOpenCustomHashMap.this.strategy.hashCode(curr)) & Byte2ShortOpenCustomHashMap.this.mask;
                    if (last <= pos ? last >= slot || slot > pos : last >= slot && slot > pos) break;
                    pos = pos + 1 & Byte2ShortOpenCustomHashMap.this.mask;
                }
                if (pos < last) {
                    if (this.wrapped == null) {
                        this.wrapped = new ByteArrayList(2);
                    }
                    this.wrapped.add(key[pos]);
                }
                key[last] = curr;
                Byte2ShortOpenCustomHashMap.this.value[last] = Byte2ShortOpenCustomHashMap.this.value[pos];
            }
        }

        public void remove() {
            if (this.last == -1) {
                throw new IllegalStateException();
            }
            if (this.last == Byte2ShortOpenCustomHashMap.this.n) {
                Byte2ShortOpenCustomHashMap.this.containsNullKey = false;
            } else if (this.pos >= 0) {
                this.shiftKeys(this.last);
            } else {
                Byte2ShortOpenCustomHashMap.this.remove(this.wrapped.getByte(-this.pos - 1));
                this.last = -1;
                return;
            }
            --Byte2ShortOpenCustomHashMap.this.size;
            this.last = -1;
        }

        public int skip(int n2) {
            int i2 = n2;
            while (i2-- != 0 && this.hasNext()) {
                this.nextEntry();
            }
            return n2 - i2 - 1;
        }
    }

    final class MapEntry
    implements Byte2ShortMap.Entry,
    Map.Entry<Byte, Short> {
        int index;

        MapEntry(int index) {
            this.index = index;
        }

        MapEntry() {
        }

        @Override
        @Deprecated
        public Byte getKey() {
            return Byte2ShortOpenCustomHashMap.this.key[this.index];
        }

        @Override
        public byte getByteKey() {
            return Byte2ShortOpenCustomHashMap.this.key[this.index];
        }

        @Override
        @Deprecated
        public Short getValue() {
            return Byte2ShortOpenCustomHashMap.this.value[this.index];
        }

        @Override
        public short getShortValue() {
            return Byte2ShortOpenCustomHashMap.this.value[this.index];
        }

        @Override
        public short setValue(short v2) {
            short oldValue = Byte2ShortOpenCustomHashMap.this.value[this.index];
            Byte2ShortOpenCustomHashMap.this.value[this.index] = v2;
            return oldValue;
        }

        @Override
        public Short setValue(Short v2) {
            return this.setValue((short)v2);
        }

        @Override
        public boolean equals(Object o2) {
            if (!(o2 instanceof Map.Entry)) {
                return false;
            }
            Map.Entry e2 = (Map.Entry)o2;
            return Byte2ShortOpenCustomHashMap.this.strategy.equals(Byte2ShortOpenCustomHashMap.this.key[this.index], (Byte)e2.getKey()) && Byte2ShortOpenCustomHashMap.this.value[this.index] == (Short)e2.getValue();
        }

        @Override
        public int hashCode() {
            return Byte2ShortOpenCustomHashMap.this.strategy.hashCode(Byte2ShortOpenCustomHashMap.this.key[this.index]) ^ Byte2ShortOpenCustomHashMap.this.value[this.index];
        }

        public String toString() {
            return Byte2ShortOpenCustomHashMap.this.key[this.index] + "=>" + Byte2ShortOpenCustomHashMap.this.value[this.index];
        }
    }
}

