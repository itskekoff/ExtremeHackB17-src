package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.bytes.AbstractByte2IntMap;
import it.unimi.dsi.fastutil.bytes.AbstractByteSet;
import it.unimi.dsi.fastutil.bytes.Byte2IntMap;
import it.unimi.dsi.fastutil.bytes.ByteArrayList;
import it.unimi.dsi.fastutil.bytes.ByteIterator;
import it.unimi.dsi.fastutil.bytes.ByteSet;
import it.unimi.dsi.fastutil.ints.AbstractIntCollection;
import it.unimi.dsi.fastutil.ints.IntCollection;
import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.objects.AbstractObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Map;
import java.util.NoSuchElementException;

public class Byte2IntOpenHashMap
extends AbstractByte2IntMap
implements Serializable,
Cloneable,
Hash {
    private static final long serialVersionUID = 0L;
    private static final boolean ASSERTS = false;
    protected transient byte[] key;
    protected transient int[] value;
    protected transient int mask;
    protected transient boolean containsNullKey;
    protected transient int n;
    protected transient int maxFill;
    protected int size;
    protected final float f;
    protected transient Byte2IntMap.FastEntrySet entries;
    protected transient ByteSet keys;
    protected transient IntCollection values;

    public Byte2IntOpenHashMap(int expected, float f2) {
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
        this.value = new int[this.n + 1];
    }

    public Byte2IntOpenHashMap(int expected) {
        this(expected, 0.75f);
    }

    public Byte2IntOpenHashMap() {
        this(16, 0.75f);
    }

    public Byte2IntOpenHashMap(Map<? extends Byte, ? extends Integer> m2, float f2) {
        this(m2.size(), f2);
        this.putAll(m2);
    }

    public Byte2IntOpenHashMap(Map<? extends Byte, ? extends Integer> m2) {
        this(m2, 0.75f);
    }

    public Byte2IntOpenHashMap(Byte2IntMap m2, float f2) {
        this(m2.size(), f2);
        this.putAll(m2);
    }

    public Byte2IntOpenHashMap(Byte2IntMap m2) {
        this(m2, 0.75f);
    }

    public Byte2IntOpenHashMap(byte[] k2, int[] v2, float f2) {
        this(k2.length, f2);
        if (k2.length != v2.length) {
            throw new IllegalArgumentException("The key array and the value array have different lengths (" + k2.length + " and " + v2.length + ")");
        }
        for (int i2 = 0; i2 < k2.length; ++i2) {
            this.put(k2[i2], v2[i2]);
        }
    }

    public Byte2IntOpenHashMap(byte[] k2, int[] v2) {
        this(k2, v2, 0.75f);
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

    private int removeEntry(int pos) {
        int oldValue = this.value[pos];
        --this.size;
        this.shiftKeys(pos);
        if (this.size < this.maxFill / 4 && this.n > 16) {
            this.rehash(this.n / 2);
        }
        return oldValue;
    }

    private int removeNullEntry() {
        this.containsNullKey = false;
        int oldValue = this.value[this.n];
        --this.size;
        if (this.size < this.maxFill / 4 && this.n > 16) {
            this.rehash(this.n / 2);
        }
        return oldValue;
    }

    @Override
    public void putAll(Map<? extends Byte, ? extends Integer> m2) {
        if ((double)this.f <= 0.5) {
            this.ensureCapacity(m2.size());
        } else {
            this.tryCapacity(this.size() + m2.size());
        }
        super.putAll(m2);
    }

    private int insert(byte k2, int v2) {
        int pos;
        if (k2 == 0) {
            if (this.containsNullKey) {
                return this.n;
            }
            this.containsNullKey = true;
            pos = this.n;
        } else {
            byte[] key = this.key;
            pos = HashCommon.mix(k2) & this.mask;
            byte curr = key[pos];
            if (curr != 0) {
                if (curr == k2) {
                    return pos;
                }
                while ((curr = key[pos = pos + 1 & this.mask]) != 0) {
                    if (curr != k2) continue;
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
    public int put(byte k2, int v2) {
        int pos = this.insert(k2, v2);
        if (pos < 0) {
            return this.defRetValue;
        }
        int oldValue = this.value[pos];
        this.value[pos] = v2;
        return oldValue;
    }

    @Override
    @Deprecated
    public Integer put(Byte ok2, Integer ov) {
        int v2 = ov;
        int pos = this.insert(ok2, v2);
        if (pos < 0) {
            return null;
        }
        int oldValue = this.value[pos];
        this.value[pos] = v2;
        return oldValue;
    }

    private int addToValue(int pos, int incr) {
        int oldValue = this.value[pos];
        this.value[pos] = oldValue + incr;
        return oldValue;
    }

    public int addTo(byte k2, int incr) {
        int pos;
        if (k2 == 0) {
            if (this.containsNullKey) {
                return this.addToValue(this.n, incr);
            }
            pos = this.n;
            this.containsNullKey = true;
        } else {
            byte[] key = this.key;
            pos = HashCommon.mix(k2) & this.mask;
            byte curr = key[pos];
            if (curr != 0) {
                if (curr == k2) {
                    return this.addToValue(pos, incr);
                }
                while ((curr = key[pos = pos + 1 & this.mask]) != 0) {
                    if (curr != k2) continue;
                    return this.addToValue(pos, incr);
                }
            }
        }
        this.key[pos] = k2;
        this.value[pos] = this.defRetValue + incr;
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
                int slot = HashCommon.mix(curr) & this.mask;
                if (last <= pos ? last >= slot || slot > pos : last >= slot && slot > pos) break;
                pos = pos + 1 & this.mask;
            }
            key[last] = curr;
            this.value[last] = this.value[pos];
        }
    }

    @Override
    public int remove(byte k2) {
        if (k2 == 0) {
            if (this.containsNullKey) {
                return this.removeNullEntry();
            }
            return this.defRetValue;
        }
        byte[] key = this.key;
        int pos = HashCommon.mix(k2) & this.mask;
        byte curr = key[pos];
        if (curr == 0) {
            return this.defRetValue;
        }
        if (k2 == curr) {
            return this.removeEntry(pos);
        }
        do {
            if ((curr = key[pos = pos + 1 & this.mask]) != 0) continue;
            return this.defRetValue;
        } while (k2 != curr);
        return this.removeEntry(pos);
    }

    @Override
    @Deprecated
    public Integer remove(Object ok2) {
        byte k2 = (Byte)ok2;
        if (k2 == 0) {
            if (this.containsNullKey) {
                return this.removeNullEntry();
            }
            return null;
        }
        byte[] key = this.key;
        int pos = HashCommon.mix(k2) & this.mask;
        byte curr = key[pos];
        if (curr == 0) {
            return null;
        }
        if (curr == k2) {
            return this.removeEntry(pos);
        }
        do {
            if ((curr = key[pos = pos + 1 & this.mask]) != 0) continue;
            return null;
        } while (curr != k2);
        return this.removeEntry(pos);
    }

    @Deprecated
    public Integer get(Byte ok2) {
        if (ok2 == null) {
            return null;
        }
        byte k2 = ok2;
        if (k2 == 0) {
            return this.containsNullKey ? Integer.valueOf(this.value[this.n]) : null;
        }
        byte[] key = this.key;
        int pos = HashCommon.mix(k2) & this.mask;
        byte curr = key[pos];
        if (curr == 0) {
            return null;
        }
        if (k2 == curr) {
            return this.value[pos];
        }
        do {
            if ((curr = key[pos = pos + 1 & this.mask]) != 0) continue;
            return null;
        } while (k2 != curr);
        return this.value[pos];
    }

    @Override
    public int get(byte k2) {
        if (k2 == 0) {
            return this.containsNullKey ? this.value[this.n] : this.defRetValue;
        }
        byte[] key = this.key;
        int pos = HashCommon.mix(k2) & this.mask;
        byte curr = key[pos];
        if (curr == 0) {
            return this.defRetValue;
        }
        if (k2 == curr) {
            return this.value[pos];
        }
        do {
            if ((curr = key[pos = pos + 1 & this.mask]) != 0) continue;
            return this.defRetValue;
        } while (k2 != curr);
        return this.value[pos];
    }

    @Override
    public boolean containsKey(byte k2) {
        if (k2 == 0) {
            return this.containsNullKey;
        }
        byte[] key = this.key;
        int pos = HashCommon.mix(k2) & this.mask;
        byte curr = key[pos];
        if (curr == 0) {
            return false;
        }
        if (k2 == curr) {
            return true;
        }
        do {
            if ((curr = key[pos = pos + 1 & this.mask]) != 0) continue;
            return false;
        } while (k2 != curr);
        return true;
    }

    @Override
    public boolean containsValue(int v2) {
        int[] value = this.value;
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

    public Byte2IntMap.FastEntrySet byte2IntEntrySet() {
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
    public IntCollection values() {
        if (this.values == null) {
            this.values = new AbstractIntCollection(){

                @Override
                public IntIterator iterator() {
                    return new ValueIterator();
                }

                @Override
                public int size() {
                    return Byte2IntOpenHashMap.this.size;
                }

                @Override
                public boolean contains(int v2) {
                    return Byte2IntOpenHashMap.this.containsValue(v2);
                }

                @Override
                public void clear() {
                    Byte2IntOpenHashMap.this.clear();
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
        int[] value = this.value;
        int mask = newN - 1;
        byte[] newKey = new byte[newN + 1];
        int[] newValue = new int[newN + 1];
        int i2 = this.n;
        int j2 = this.realSize();
        while (j2-- != 0) {
            while (key[--i2] == 0) {
            }
            int pos = HashCommon.mix(key[i2]) & mask;
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

    public Byte2IntOpenHashMap clone() {
        Byte2IntOpenHashMap c2;
        try {
            c2 = (Byte2IntOpenHashMap)super.clone();
        }
        catch (CloneNotSupportedException cantHappen) {
            throw new InternalError();
        }
        c2.keys = null;
        c2.values = null;
        c2.entries = null;
        c2.containsNullKey = this.containsNullKey;
        c2.key = (byte[])this.key.clone();
        c2.value = (int[])this.value.clone();
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
            t2 = this.key[i2];
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
        int[] value = this.value;
        MapIterator i2 = new MapIterator();
        s2.defaultWriteObject();
        int j2 = this.size;
        while (j2-- != 0) {
            int e2 = i2.nextEntry();
            s2.writeByte(key[e2]);
            s2.writeInt(value[e2]);
        }
    }

    private void readObject(ObjectInputStream s2) throws IOException, ClassNotFoundException {
        s2.defaultReadObject();
        this.n = HashCommon.arraySize(this.size, this.f);
        this.maxFill = HashCommon.maxFill(this.n, this.f);
        this.mask = this.n - 1;
        this.key = new byte[this.n + 1];
        byte[] key = this.key;
        this.value = new int[this.n + 1];
        int[] value = this.value;
        int i2 = this.size;
        while (i2-- != 0) {
            int pos;
            byte k2 = s2.readByte();
            int v2 = s2.readInt();
            if (k2 == 0) {
                pos = this.n;
                this.containsNullKey = true;
            } else {
                pos = HashCommon.mix(k2) & this.mask;
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
    implements IntIterator {
        @Override
        public int nextInt() {
            return Byte2IntOpenHashMap.this.value[this.nextEntry()];
        }

        @Override
        @Deprecated
        public Integer next() {
            return Byte2IntOpenHashMap.this.value[this.nextEntry()];
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
            return Byte2IntOpenHashMap.this.size;
        }

        @Override
        public boolean contains(byte k2) {
            return Byte2IntOpenHashMap.this.containsKey(k2);
        }

        @Override
        public boolean rem(byte k2) {
            int oldSize = Byte2IntOpenHashMap.this.size;
            Byte2IntOpenHashMap.this.remove(k2);
            return Byte2IntOpenHashMap.this.size != oldSize;
        }

        @Override
        public void clear() {
            Byte2IntOpenHashMap.this.clear();
        }
    }

    private final class KeyIterator
    extends MapIterator
    implements ByteIterator {
        @Override
        public byte nextByte() {
            return Byte2IntOpenHashMap.this.key[this.nextEntry()];
        }

        @Override
        public Byte next() {
            return Byte2IntOpenHashMap.this.key[this.nextEntry()];
        }
    }

    private final class MapEntrySet
    extends AbstractObjectSet<Byte2IntMap.Entry>
    implements Byte2IntMap.FastEntrySet {
        private MapEntrySet() {
        }

        @Override
        public ObjectIterator<Byte2IntMap.Entry> iterator() {
            return new EntryIterator();
        }

        @Override
        public ObjectIterator<Byte2IntMap.Entry> fastIterator() {
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
            if (e2.getValue() == null || !(e2.getValue() instanceof Integer)) {
                return false;
            }
            byte k2 = (Byte)e2.getKey();
            int v2 = (Integer)e2.getValue();
            if (k2 == 0) {
                return Byte2IntOpenHashMap.this.containsNullKey && Byte2IntOpenHashMap.this.value[Byte2IntOpenHashMap.this.n] == v2;
            }
            byte[] key = Byte2IntOpenHashMap.this.key;
            int pos = HashCommon.mix(k2) & Byte2IntOpenHashMap.this.mask;
            byte curr = key[pos];
            if (curr == 0) {
                return false;
            }
            if (k2 == curr) {
                return Byte2IntOpenHashMap.this.value[pos] == v2;
            }
            do {
                if ((curr = key[pos = pos + 1 & Byte2IntOpenHashMap.this.mask]) != 0) continue;
                return false;
            } while (k2 != curr);
            return Byte2IntOpenHashMap.this.value[pos] == v2;
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
            if (e2.getValue() == null || !(e2.getValue() instanceof Integer)) {
                return false;
            }
            byte k2 = (Byte)e2.getKey();
            int v2 = (Integer)e2.getValue();
            if (k2 == 0) {
                if (Byte2IntOpenHashMap.this.containsNullKey && Byte2IntOpenHashMap.this.value[Byte2IntOpenHashMap.this.n] == v2) {
                    Byte2IntOpenHashMap.this.removeNullEntry();
                    return true;
                }
                return false;
            }
            byte[] key = Byte2IntOpenHashMap.this.key;
            int pos = HashCommon.mix(k2) & Byte2IntOpenHashMap.this.mask;
            byte curr = key[pos];
            if (curr == 0) {
                return false;
            }
            if (curr == k2) {
                if (Byte2IntOpenHashMap.this.value[pos] == v2) {
                    Byte2IntOpenHashMap.this.removeEntry(pos);
                    return true;
                }
                return false;
            }
            do {
                if ((curr = key[pos = pos + 1 & Byte2IntOpenHashMap.this.mask]) != 0) continue;
                return false;
            } while (curr != k2 || Byte2IntOpenHashMap.this.value[pos] != v2);
            Byte2IntOpenHashMap.this.removeEntry(pos);
            return true;
        }

        @Override
        public int size() {
            return Byte2IntOpenHashMap.this.size;
        }

        @Override
        public void clear() {
            Byte2IntOpenHashMap.this.clear();
        }
    }

    private class FastEntryIterator
    extends MapIterator
    implements ObjectIterator<Byte2IntMap.Entry> {
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
    implements ObjectIterator<Byte2IntMap.Entry> {
        private MapEntry entry;

        private EntryIterator() {
        }

        @Override
        public Byte2IntMap.Entry next() {
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
            this.pos = Byte2IntOpenHashMap.this.n;
            this.last = -1;
            this.c = Byte2IntOpenHashMap.this.size;
            this.mustReturnNullKey = Byte2IntOpenHashMap.this.containsNullKey;
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
                this.last = Byte2IntOpenHashMap.this.n;
                return this.last;
            }
            byte[] key = Byte2IntOpenHashMap.this.key;
            do {
                if (--this.pos >= 0) continue;
                this.last = Integer.MIN_VALUE;
                byte k2 = this.wrapped.getByte(-this.pos - 1);
                int p2 = HashCommon.mix(k2) & Byte2IntOpenHashMap.this.mask;
                while (k2 != key[p2]) {
                    p2 = p2 + 1 & Byte2IntOpenHashMap.this.mask;
                }
                return p2;
            } while (key[this.pos] == 0);
            this.last = this.pos;
            return this.last;
        }

        private final void shiftKeys(int pos) {
            byte[] key = Byte2IntOpenHashMap.this.key;
            while (true) {
                byte curr;
                int last = pos;
                pos = last + 1 & Byte2IntOpenHashMap.this.mask;
                while (true) {
                    if ((curr = key[pos]) == 0) {
                        key[last] = 0;
                        return;
                    }
                    int slot = HashCommon.mix(curr) & Byte2IntOpenHashMap.this.mask;
                    if (last <= pos ? last >= slot || slot > pos : last >= slot && slot > pos) break;
                    pos = pos + 1 & Byte2IntOpenHashMap.this.mask;
                }
                if (pos < last) {
                    if (this.wrapped == null) {
                        this.wrapped = new ByteArrayList(2);
                    }
                    this.wrapped.add(key[pos]);
                }
                key[last] = curr;
                Byte2IntOpenHashMap.this.value[last] = Byte2IntOpenHashMap.this.value[pos];
            }
        }

        public void remove() {
            if (this.last == -1) {
                throw new IllegalStateException();
            }
            if (this.last == Byte2IntOpenHashMap.this.n) {
                Byte2IntOpenHashMap.this.containsNullKey = false;
            } else if (this.pos >= 0) {
                this.shiftKeys(this.last);
            } else {
                Byte2IntOpenHashMap.this.remove(this.wrapped.getByte(-this.pos - 1));
                this.last = -1;
                return;
            }
            --Byte2IntOpenHashMap.this.size;
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
    implements Byte2IntMap.Entry,
    Map.Entry<Byte, Integer> {
        int index;

        MapEntry(int index) {
            this.index = index;
        }

        MapEntry() {
        }

        @Override
        @Deprecated
        public Byte getKey() {
            return Byte2IntOpenHashMap.this.key[this.index];
        }

        @Override
        public byte getByteKey() {
            return Byte2IntOpenHashMap.this.key[this.index];
        }

        @Override
        @Deprecated
        public Integer getValue() {
            return Byte2IntOpenHashMap.this.value[this.index];
        }

        @Override
        public int getIntValue() {
            return Byte2IntOpenHashMap.this.value[this.index];
        }

        @Override
        public int setValue(int v2) {
            int oldValue = Byte2IntOpenHashMap.this.value[this.index];
            Byte2IntOpenHashMap.this.value[this.index] = v2;
            return oldValue;
        }

        @Override
        public Integer setValue(Integer v2) {
            return this.setValue((int)v2);
        }

        @Override
        public boolean equals(Object o2) {
            if (!(o2 instanceof Map.Entry)) {
                return false;
            }
            Map.Entry e2 = (Map.Entry)o2;
            return Byte2IntOpenHashMap.this.key[this.index] == (Byte)e2.getKey() && Byte2IntOpenHashMap.this.value[this.index] == (Integer)e2.getValue();
        }

        @Override
        public int hashCode() {
            return Byte2IntOpenHashMap.this.key[this.index] ^ Byte2IntOpenHashMap.this.value[this.index];
        }

        public String toString() {
            return Byte2IntOpenHashMap.this.key[this.index] + "=>" + Byte2IntOpenHashMap.this.value[this.index];
        }
    }
}

