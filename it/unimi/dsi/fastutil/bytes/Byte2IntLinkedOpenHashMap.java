package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.bytes.AbstractByte2IntSortedMap;
import it.unimi.dsi.fastutil.bytes.AbstractByteSortedSet;
import it.unimi.dsi.fastutil.bytes.Byte2IntMap;
import it.unimi.dsi.fastutil.bytes.Byte2IntSortedMap;
import it.unimi.dsi.fastutil.bytes.ByteComparator;
import it.unimi.dsi.fastutil.bytes.ByteListIterator;
import it.unimi.dsi.fastutil.bytes.ByteSortedSet;
import it.unimi.dsi.fastutil.ints.AbstractIntCollection;
import it.unimi.dsi.fastutil.ints.IntCollection;
import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.ints.IntListIterator;
import it.unimi.dsi.fastutil.objects.AbstractObjectSortedSet;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;
import java.util.NoSuchElementException;

public class Byte2IntLinkedOpenHashMap
extends AbstractByte2IntSortedMap
implements Serializable,
Cloneable,
Hash {
    private static final long serialVersionUID = 0L;
    private static final boolean ASSERTS = false;
    protected transient byte[] key;
    protected transient int[] value;
    protected transient int mask;
    protected transient boolean containsNullKey;
    protected transient int first = -1;
    protected transient int last = -1;
    protected transient long[] link;
    protected transient int n;
    protected transient int maxFill;
    protected int size;
    protected final float f;
    protected transient Byte2IntSortedMap.FastSortedEntrySet entries;
    protected transient ByteSortedSet keys;
    protected transient IntCollection values;

    public Byte2IntLinkedOpenHashMap(int expected, float f2) {
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
        this.link = new long[this.n + 1];
    }

    public Byte2IntLinkedOpenHashMap(int expected) {
        this(expected, 0.75f);
    }

    public Byte2IntLinkedOpenHashMap() {
        this(16, 0.75f);
    }

    public Byte2IntLinkedOpenHashMap(Map<? extends Byte, ? extends Integer> m2, float f2) {
        this(m2.size(), f2);
        this.putAll(m2);
    }

    public Byte2IntLinkedOpenHashMap(Map<? extends Byte, ? extends Integer> m2) {
        this(m2, 0.75f);
    }

    public Byte2IntLinkedOpenHashMap(Byte2IntMap m2, float f2) {
        this(m2.size(), f2);
        this.putAll(m2);
    }

    public Byte2IntLinkedOpenHashMap(Byte2IntMap m2) {
        this(m2, 0.75f);
    }

    public Byte2IntLinkedOpenHashMap(byte[] k2, int[] v2, float f2) {
        this(k2.length, f2);
        if (k2.length != v2.length) {
            throw new IllegalArgumentException("The key array and the value array have different lengths (" + k2.length + " and " + v2.length + ")");
        }
        for (int i2 = 0; i2 < k2.length; ++i2) {
            this.put(k2[i2], v2[i2]);
        }
    }

    public Byte2IntLinkedOpenHashMap(byte[] k2, int[] v2) {
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
        this.fixPointers(pos);
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
        this.fixPointers(this.n);
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
        if (this.size == 0) {
            this.first = this.last = pos;
            this.link[pos] = -1L;
        } else {
            int n2 = this.last;
            this.link[n2] = this.link[n2] ^ (this.link[this.last] ^ (long)pos & 0xFFFFFFFFL) & 0xFFFFFFFFL;
            this.link[pos] = ((long)this.last & 0xFFFFFFFFL) << 32 | 0xFFFFFFFFL;
            this.last = pos;
        }
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
        if (this.size == 0) {
            this.first = this.last = pos;
            this.link[pos] = -1L;
        } else {
            int n2 = this.last;
            this.link[n2] = this.link[n2] ^ (this.link[this.last] ^ (long)pos & 0xFFFFFFFFL) & 0xFFFFFFFFL;
            this.link[pos] = ((long)this.last & 0xFFFFFFFFL) << 32 | 0xFFFFFFFFL;
            this.last = pos;
        }
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
            this.fixPointers(pos, last);
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

    private int setValue(int pos, int v2) {
        int oldValue = this.value[pos];
        this.value[pos] = v2;
        return oldValue;
    }

    public int removeFirstInt() {
        if (this.size == 0) {
            throw new NoSuchElementException();
        }
        int pos = this.first;
        this.first = (int)this.link[pos];
        if (0 <= this.first) {
            int n2 = this.first;
            this.link[n2] = this.link[n2] | 0xFFFFFFFF00000000L;
        }
        --this.size;
        int v2 = this.value[pos];
        if (pos == this.n) {
            this.containsNullKey = false;
        } else {
            this.shiftKeys(pos);
        }
        if (this.size < this.maxFill / 4 && this.n > 16) {
            this.rehash(this.n / 2);
        }
        return v2;
    }

    public int removeLastInt() {
        if (this.size == 0) {
            throw new NoSuchElementException();
        }
        int pos = this.last;
        this.last = (int)(this.link[pos] >>> 32);
        if (0 <= this.last) {
            int n2 = this.last;
            this.link[n2] = this.link[n2] | 0xFFFFFFFFL;
        }
        --this.size;
        int v2 = this.value[pos];
        if (pos == this.n) {
            this.containsNullKey = false;
        } else {
            this.shiftKeys(pos);
        }
        if (this.size < this.maxFill / 4 && this.n > 16) {
            this.rehash(this.n / 2);
        }
        return v2;
    }

    private void moveIndexToFirst(int i2) {
        if (this.size == 1 || this.first == i2) {
            return;
        }
        if (this.last == i2) {
            int n2 = this.last = (int)(this.link[i2] >>> 32);
            this.link[n2] = this.link[n2] | 0xFFFFFFFFL;
        } else {
            long linki = this.link[i2];
            int prev = (int)(linki >>> 32);
            int next = (int)linki;
            int n3 = prev;
            this.link[n3] = this.link[n3] ^ (this.link[prev] ^ linki & 0xFFFFFFFFL) & 0xFFFFFFFFL;
            int n4 = next;
            this.link[n4] = this.link[n4] ^ (this.link[next] ^ linki & 0xFFFFFFFF00000000L) & 0xFFFFFFFF00000000L;
        }
        int n5 = this.first;
        this.link[n5] = this.link[n5] ^ (this.link[this.first] ^ ((long)i2 & 0xFFFFFFFFL) << 32) & 0xFFFFFFFF00000000L;
        this.link[i2] = 0xFFFFFFFF00000000L | (long)this.first & 0xFFFFFFFFL;
        this.first = i2;
    }

    private void moveIndexToLast(int i2) {
        if (this.size == 1 || this.last == i2) {
            return;
        }
        if (this.first == i2) {
            int n2 = this.first = (int)this.link[i2];
            this.link[n2] = this.link[n2] | 0xFFFFFFFF00000000L;
        } else {
            long linki = this.link[i2];
            int prev = (int)(linki >>> 32);
            int next = (int)linki;
            int n3 = prev;
            this.link[n3] = this.link[n3] ^ (this.link[prev] ^ linki & 0xFFFFFFFFL) & 0xFFFFFFFFL;
            int n4 = next;
            this.link[n4] = this.link[n4] ^ (this.link[next] ^ linki & 0xFFFFFFFF00000000L) & 0xFFFFFFFF00000000L;
        }
        int n5 = this.last;
        this.link[n5] = this.link[n5] ^ (this.link[this.last] ^ (long)i2 & 0xFFFFFFFFL) & 0xFFFFFFFFL;
        this.link[i2] = ((long)this.last & 0xFFFFFFFFL) << 32 | 0xFFFFFFFFL;
        this.last = i2;
    }

    public int getAndMoveToFirst(byte k2) {
        if (k2 == 0) {
            if (this.containsNullKey) {
                this.moveIndexToFirst(this.n);
                return this.value[this.n];
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
            this.moveIndexToFirst(pos);
            return this.value[pos];
        }
        do {
            if ((curr = key[pos = pos + 1 & this.mask]) != 0) continue;
            return this.defRetValue;
        } while (k2 != curr);
        this.moveIndexToFirst(pos);
        return this.value[pos];
    }

    public int getAndMoveToLast(byte k2) {
        if (k2 == 0) {
            if (this.containsNullKey) {
                this.moveIndexToLast(this.n);
                return this.value[this.n];
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
            this.moveIndexToLast(pos);
            return this.value[pos];
        }
        do {
            if ((curr = key[pos = pos + 1 & this.mask]) != 0) continue;
            return this.defRetValue;
        } while (k2 != curr);
        this.moveIndexToLast(pos);
        return this.value[pos];
    }

    public int putAndMoveToFirst(byte k2, int v2) {
        int pos;
        if (k2 == 0) {
            if (this.containsNullKey) {
                this.moveIndexToFirst(this.n);
                return this.setValue(this.n, v2);
            }
            this.containsNullKey = true;
            pos = this.n;
        } else {
            byte[] key = this.key;
            pos = HashCommon.mix(k2) & this.mask;
            byte curr = key[pos];
            if (curr != 0) {
                if (curr == k2) {
                    this.moveIndexToFirst(pos);
                    return this.setValue(pos, v2);
                }
                while ((curr = key[pos = pos + 1 & this.mask]) != 0) {
                    if (curr != k2) continue;
                    this.moveIndexToFirst(pos);
                    return this.setValue(pos, v2);
                }
            }
        }
        this.key[pos] = k2;
        this.value[pos] = v2;
        if (this.size == 0) {
            this.first = this.last = pos;
            this.link[pos] = -1L;
        } else {
            int n2 = this.first;
            this.link[n2] = this.link[n2] ^ (this.link[this.first] ^ ((long)pos & 0xFFFFFFFFL) << 32) & 0xFFFFFFFF00000000L;
            this.link[pos] = 0xFFFFFFFF00000000L | (long)this.first & 0xFFFFFFFFL;
            this.first = pos;
        }
        if (this.size++ >= this.maxFill) {
            this.rehash(HashCommon.arraySize(this.size, this.f));
        }
        return this.defRetValue;
    }

    public int putAndMoveToLast(byte k2, int v2) {
        int pos;
        if (k2 == 0) {
            if (this.containsNullKey) {
                this.moveIndexToLast(this.n);
                return this.setValue(this.n, v2);
            }
            this.containsNullKey = true;
            pos = this.n;
        } else {
            byte[] key = this.key;
            pos = HashCommon.mix(k2) & this.mask;
            byte curr = key[pos];
            if (curr != 0) {
                if (curr == k2) {
                    this.moveIndexToLast(pos);
                    return this.setValue(pos, v2);
                }
                while ((curr = key[pos = pos + 1 & this.mask]) != 0) {
                    if (curr != k2) continue;
                    this.moveIndexToLast(pos);
                    return this.setValue(pos, v2);
                }
            }
        }
        this.key[pos] = k2;
        this.value[pos] = v2;
        if (this.size == 0) {
            this.first = this.last = pos;
            this.link[pos] = -1L;
        } else {
            int n2 = this.last;
            this.link[n2] = this.link[n2] ^ (this.link[this.last] ^ (long)pos & 0xFFFFFFFFL) & 0xFFFFFFFFL;
            this.link[pos] = ((long)this.last & 0xFFFFFFFFL) << 32 | 0xFFFFFFFFL;
            this.last = pos;
        }
        if (this.size++ >= this.maxFill) {
            this.rehash(HashCommon.arraySize(this.size, this.f));
        }
        return this.defRetValue;
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
        this.last = -1;
        this.first = -1;
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

    protected void fixPointers(int i2) {
        if (this.size == 0) {
            this.last = -1;
            this.first = -1;
            return;
        }
        if (this.first == i2) {
            this.first = (int)this.link[i2];
            if (0 <= this.first) {
                int n2 = this.first;
                this.link[n2] = this.link[n2] | 0xFFFFFFFF00000000L;
            }
            return;
        }
        if (this.last == i2) {
            this.last = (int)(this.link[i2] >>> 32);
            if (0 <= this.last) {
                int n3 = this.last;
                this.link[n3] = this.link[n3] | 0xFFFFFFFFL;
            }
            return;
        }
        long linki = this.link[i2];
        int prev = (int)(linki >>> 32);
        int next = (int)linki;
        int n4 = prev;
        this.link[n4] = this.link[n4] ^ (this.link[prev] ^ linki & 0xFFFFFFFFL) & 0xFFFFFFFFL;
        int n5 = next;
        this.link[n5] = this.link[n5] ^ (this.link[next] ^ linki & 0xFFFFFFFF00000000L) & 0xFFFFFFFF00000000L;
    }

    protected void fixPointers(int s2, int d2) {
        if (this.size == 1) {
            this.first = this.last = d2;
            this.link[d2] = -1L;
            return;
        }
        if (this.first == s2) {
            this.first = d2;
            int n2 = (int)this.link[s2];
            this.link[n2] = this.link[n2] ^ (this.link[(int)this.link[s2]] ^ ((long)d2 & 0xFFFFFFFFL) << 32) & 0xFFFFFFFF00000000L;
            this.link[d2] = this.link[s2];
            return;
        }
        if (this.last == s2) {
            this.last = d2;
            int n3 = (int)(this.link[s2] >>> 32);
            this.link[n3] = this.link[n3] ^ (this.link[(int)(this.link[s2] >>> 32)] ^ (long)d2 & 0xFFFFFFFFL) & 0xFFFFFFFFL;
            this.link[d2] = this.link[s2];
            return;
        }
        long links = this.link[s2];
        int prev = (int)(links >>> 32);
        int next = (int)links;
        int n4 = prev;
        this.link[n4] = this.link[n4] ^ (this.link[prev] ^ (long)d2 & 0xFFFFFFFFL) & 0xFFFFFFFFL;
        int n5 = next;
        this.link[n5] = this.link[n5] ^ (this.link[next] ^ ((long)d2 & 0xFFFFFFFFL) << 32) & 0xFFFFFFFF00000000L;
        this.link[d2] = links;
    }

    @Override
    public byte firstByteKey() {
        if (this.size == 0) {
            throw new NoSuchElementException();
        }
        return this.key[this.first];
    }

    @Override
    public byte lastByteKey() {
        if (this.size == 0) {
            throw new NoSuchElementException();
        }
        return this.key[this.last];
    }

    @Override
    public ByteComparator comparator() {
        return null;
    }

    @Override
    public Byte2IntSortedMap tailMap(byte from) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Byte2IntSortedMap headMap(byte to2) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Byte2IntSortedMap subMap(byte from, byte to2) {
        throw new UnsupportedOperationException();
    }

    public Byte2IntSortedMap.FastSortedEntrySet byte2IntEntrySet() {
        if (this.entries == null) {
            this.entries = new MapEntrySet();
        }
        return this.entries;
    }

    @Override
    public ByteSortedSet keySet() {
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
                    return Byte2IntLinkedOpenHashMap.this.size;
                }

                @Override
                public boolean contains(int v2) {
                    return Byte2IntLinkedOpenHashMap.this.containsValue(v2);
                }

                @Override
                public void clear() {
                    Byte2IntLinkedOpenHashMap.this.clear();
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
        int i2 = this.first;
        int prev = -1;
        int newPrev = -1;
        long[] link = this.link;
        long[] newLink = new long[newN + 1];
        this.first = -1;
        int j2 = this.size;
        while (j2-- != 0) {
            int pos;
            if (key[i2] == 0) {
                pos = newN;
            } else {
                pos = HashCommon.mix(key[i2]) & mask;
                while (newKey[pos] != 0) {
                    pos = pos + 1 & mask;
                }
            }
            newKey[pos] = key[i2];
            newValue[pos] = value[i2];
            if (prev != -1) {
                int n2 = newPrev;
                newLink[n2] = newLink[n2] ^ (newLink[newPrev] ^ (long)pos & 0xFFFFFFFFL) & 0xFFFFFFFFL;
                int n3 = pos;
                newLink[n3] = newLink[n3] ^ (newLink[pos] ^ ((long)newPrev & 0xFFFFFFFFL) << 32) & 0xFFFFFFFF00000000L;
                newPrev = pos;
            } else {
                newPrev = this.first = pos;
                newLink[pos] = -1L;
            }
            int t2 = i2;
            i2 = (int)link[i2];
            prev = t2;
        }
        this.link = newLink;
        this.last = newPrev;
        if (newPrev != -1) {
            int n4 = newPrev;
            newLink[n4] = newLink[n4] | 0xFFFFFFFFL;
        }
        this.n = newN;
        this.mask = mask;
        this.maxFill = HashCommon.maxFill(this.n, this.f);
        this.key = newKey;
        this.value = newValue;
    }

    public Byte2IntLinkedOpenHashMap clone() {
        Byte2IntLinkedOpenHashMap c2;
        try {
            c2 = (Byte2IntLinkedOpenHashMap)super.clone();
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
        c2.link = (long[])this.link.clone();
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
        this.link = new long[this.n + 1];
        long[] link = this.link;
        int prev = -1;
        this.last = -1;
        this.first = -1;
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
            if (this.first != -1) {
                int n2 = prev;
                link[n2] = link[n2] ^ (link[prev] ^ (long)pos & 0xFFFFFFFFL) & 0xFFFFFFFFL;
                int n3 = pos;
                link[n3] = link[n3] ^ (link[pos] ^ ((long)prev & 0xFFFFFFFFL) << 32) & 0xFFFFFFFF00000000L;
                prev = pos;
                continue;
            }
            prev = this.first = pos;
            int n4 = pos;
            link[n4] = link[n4] | 0xFFFFFFFF00000000L;
        }
        this.last = prev;
        if (prev != -1) {
            int n5 = prev;
            link[n5] = link[n5] | 0xFFFFFFFFL;
        }
    }

    private void checkTable() {
    }

    private final class ValueIterator
    extends MapIterator
    implements IntListIterator {
        @Override
        public int previousInt() {
            return Byte2IntLinkedOpenHashMap.this.value[this.previousEntry()];
        }

        @Override
        public Integer previous() {
            return Byte2IntLinkedOpenHashMap.this.value[this.previousEntry()];
        }

        @Override
        public void set(Integer ok2) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void add(Integer ok2) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void set(int v2) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void add(int v2) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int nextInt() {
            return Byte2IntLinkedOpenHashMap.this.value[this.nextEntry()];
        }

        @Override
        @Deprecated
        public Integer next() {
            return Byte2IntLinkedOpenHashMap.this.value[this.nextEntry()];
        }
    }

    private final class KeySet
    extends AbstractByteSortedSet {
        private KeySet() {
        }

        @Override
        public ByteListIterator iterator(byte from) {
            return new KeyIterator(from);
        }

        @Override
        public ByteListIterator iterator() {
            return new KeyIterator();
        }

        @Override
        public int size() {
            return Byte2IntLinkedOpenHashMap.this.size;
        }

        @Override
        public boolean contains(byte k2) {
            return Byte2IntLinkedOpenHashMap.this.containsKey(k2);
        }

        @Override
        public boolean rem(byte k2) {
            int oldSize = Byte2IntLinkedOpenHashMap.this.size;
            Byte2IntLinkedOpenHashMap.this.remove(k2);
            return Byte2IntLinkedOpenHashMap.this.size != oldSize;
        }

        @Override
        public void clear() {
            Byte2IntLinkedOpenHashMap.this.clear();
        }

        @Override
        public byte firstByte() {
            if (Byte2IntLinkedOpenHashMap.this.size == 0) {
                throw new NoSuchElementException();
            }
            return Byte2IntLinkedOpenHashMap.this.key[Byte2IntLinkedOpenHashMap.this.first];
        }

        @Override
        public byte lastByte() {
            if (Byte2IntLinkedOpenHashMap.this.size == 0) {
                throw new NoSuchElementException();
            }
            return Byte2IntLinkedOpenHashMap.this.key[Byte2IntLinkedOpenHashMap.this.last];
        }

        @Override
        public ByteComparator comparator() {
            return null;
        }

        @Override
        public final ByteSortedSet tailSet(byte from) {
            throw new UnsupportedOperationException();
        }

        @Override
        public final ByteSortedSet headSet(byte to2) {
            throw new UnsupportedOperationException();
        }

        @Override
        public final ByteSortedSet subSet(byte from, byte to2) {
            throw new UnsupportedOperationException();
        }
    }

    private final class KeyIterator
    extends MapIterator
    implements ByteListIterator {
        public KeyIterator(byte k2) {
            super(k2);
        }

        @Override
        public byte previousByte() {
            return Byte2IntLinkedOpenHashMap.this.key[this.previousEntry()];
        }

        @Override
        public void set(byte k2) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void add(byte k2) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Byte previous() {
            return Byte2IntLinkedOpenHashMap.this.key[this.previousEntry()];
        }

        @Override
        public void set(Byte ok2) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void add(Byte ok2) {
            throw new UnsupportedOperationException();
        }

        public KeyIterator() {
        }

        @Override
        public byte nextByte() {
            return Byte2IntLinkedOpenHashMap.this.key[this.nextEntry()];
        }

        @Override
        public Byte next() {
            return Byte2IntLinkedOpenHashMap.this.key[this.nextEntry()];
        }
    }

    private final class MapEntrySet
    extends AbstractObjectSortedSet<Byte2IntMap.Entry>
    implements Byte2IntSortedMap.FastSortedEntrySet {
        private MapEntrySet() {
        }

        @Override
        public ObjectBidirectionalIterator<Byte2IntMap.Entry> iterator() {
            return new EntryIterator();
        }

        @Override
        public Comparator<? super Byte2IntMap.Entry> comparator() {
            return null;
        }

        @Override
        public ObjectSortedSet<Byte2IntMap.Entry> subSet(Byte2IntMap.Entry fromElement, Byte2IntMap.Entry toElement) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ObjectSortedSet<Byte2IntMap.Entry> headSet(Byte2IntMap.Entry toElement) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ObjectSortedSet<Byte2IntMap.Entry> tailSet(Byte2IntMap.Entry fromElement) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Byte2IntMap.Entry first() {
            if (Byte2IntLinkedOpenHashMap.this.size == 0) {
                throw new NoSuchElementException();
            }
            return new MapEntry(Byte2IntLinkedOpenHashMap.this.first);
        }

        @Override
        public Byte2IntMap.Entry last() {
            if (Byte2IntLinkedOpenHashMap.this.size == 0) {
                throw new NoSuchElementException();
            }
            return new MapEntry(Byte2IntLinkedOpenHashMap.this.last);
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
                return Byte2IntLinkedOpenHashMap.this.containsNullKey && Byte2IntLinkedOpenHashMap.this.value[Byte2IntLinkedOpenHashMap.this.n] == v2;
            }
            byte[] key = Byte2IntLinkedOpenHashMap.this.key;
            int pos = HashCommon.mix(k2) & Byte2IntLinkedOpenHashMap.this.mask;
            byte curr = key[pos];
            if (curr == 0) {
                return false;
            }
            if (k2 == curr) {
                return Byte2IntLinkedOpenHashMap.this.value[pos] == v2;
            }
            do {
                if ((curr = key[pos = pos + 1 & Byte2IntLinkedOpenHashMap.this.mask]) != 0) continue;
                return false;
            } while (k2 != curr);
            return Byte2IntLinkedOpenHashMap.this.value[pos] == v2;
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
                if (Byte2IntLinkedOpenHashMap.this.containsNullKey && Byte2IntLinkedOpenHashMap.this.value[Byte2IntLinkedOpenHashMap.this.n] == v2) {
                    Byte2IntLinkedOpenHashMap.this.removeNullEntry();
                    return true;
                }
                return false;
            }
            byte[] key = Byte2IntLinkedOpenHashMap.this.key;
            int pos = HashCommon.mix(k2) & Byte2IntLinkedOpenHashMap.this.mask;
            byte curr = key[pos];
            if (curr == 0) {
                return false;
            }
            if (curr == k2) {
                if (Byte2IntLinkedOpenHashMap.this.value[pos] == v2) {
                    Byte2IntLinkedOpenHashMap.this.removeEntry(pos);
                    return true;
                }
                return false;
            }
            do {
                if ((curr = key[pos = pos + 1 & Byte2IntLinkedOpenHashMap.this.mask]) != 0) continue;
                return false;
            } while (curr != k2 || Byte2IntLinkedOpenHashMap.this.value[pos] != v2);
            Byte2IntLinkedOpenHashMap.this.removeEntry(pos);
            return true;
        }

        @Override
        public int size() {
            return Byte2IntLinkedOpenHashMap.this.size;
        }

        @Override
        public void clear() {
            Byte2IntLinkedOpenHashMap.this.clear();
        }

        @Override
        public ObjectBidirectionalIterator<Byte2IntMap.Entry> iterator(Byte2IntMap.Entry from) {
            return new EntryIterator(from.getByteKey());
        }

        public ObjectBidirectionalIterator<Byte2IntMap.Entry> fastIterator() {
            return new FastEntryIterator();
        }

        @Override
        public ObjectBidirectionalIterator<Byte2IntMap.Entry> fastIterator(Byte2IntMap.Entry from) {
            return new FastEntryIterator(from.getByteKey());
        }
    }

    private class FastEntryIterator
    extends MapIterator
    implements ObjectListIterator<Byte2IntMap.Entry> {
        final MapEntry entry;

        public FastEntryIterator() {
            this.entry = new MapEntry();
        }

        public FastEntryIterator(byte from) {
            super(from);
            this.entry = new MapEntry();
        }

        @Override
        public MapEntry next() {
            this.entry.index = this.nextEntry();
            return this.entry;
        }

        @Override
        public MapEntry previous() {
            this.entry.index = this.previousEntry();
            return this.entry;
        }

        @Override
        public void set(Byte2IntMap.Entry ok2) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void add(Byte2IntMap.Entry ok2) {
            throw new UnsupportedOperationException();
        }
    }

    private class EntryIterator
    extends MapIterator
    implements ObjectListIterator<Byte2IntMap.Entry> {
        private MapEntry entry;

        public EntryIterator() {
        }

        public EntryIterator(byte from) {
            super(from);
        }

        @Override
        public MapEntry next() {
            this.entry = new MapEntry(this.nextEntry());
            return this.entry;
        }

        @Override
        public MapEntry previous() {
            this.entry = new MapEntry(this.previousEntry());
            return this.entry;
        }

        @Override
        public void remove() {
            super.remove();
            this.entry.index = -1;
        }

        @Override
        public void set(Byte2IntMap.Entry ok2) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void add(Byte2IntMap.Entry ok2) {
            throw new UnsupportedOperationException();
        }
    }

    private class MapIterator {
        int prev = -1;
        int next = -1;
        int curr = -1;
        int index = -1;

        private MapIterator() {
            this.next = Byte2IntLinkedOpenHashMap.this.first;
            this.index = 0;
        }

        private MapIterator(byte from) {
            if (from == 0) {
                if (Byte2IntLinkedOpenHashMap.this.containsNullKey) {
                    this.next = (int)Byte2IntLinkedOpenHashMap.this.link[Byte2IntLinkedOpenHashMap.this.n];
                    this.prev = Byte2IntLinkedOpenHashMap.this.n;
                    return;
                }
                throw new NoSuchElementException("The key " + from + " does not belong to this map.");
            }
            if (Byte2IntLinkedOpenHashMap.this.key[Byte2IntLinkedOpenHashMap.this.last] == from) {
                this.prev = Byte2IntLinkedOpenHashMap.this.last;
                this.index = Byte2IntLinkedOpenHashMap.this.size;
                return;
            }
            int pos = HashCommon.mix(from) & Byte2IntLinkedOpenHashMap.this.mask;
            while (Byte2IntLinkedOpenHashMap.this.key[pos] != 0) {
                if (Byte2IntLinkedOpenHashMap.this.key[pos] == from) {
                    this.next = (int)Byte2IntLinkedOpenHashMap.this.link[pos];
                    this.prev = pos;
                    return;
                }
                pos = pos + 1 & Byte2IntLinkedOpenHashMap.this.mask;
            }
            throw new NoSuchElementException("The key " + from + " does not belong to this map.");
        }

        public boolean hasNext() {
            return this.next != -1;
        }

        public boolean hasPrevious() {
            return this.prev != -1;
        }

        private final void ensureIndexKnown() {
            if (this.index >= 0) {
                return;
            }
            if (this.prev == -1) {
                this.index = 0;
                return;
            }
            if (this.next == -1) {
                this.index = Byte2IntLinkedOpenHashMap.this.size;
                return;
            }
            int pos = Byte2IntLinkedOpenHashMap.this.first;
            this.index = 1;
            while (pos != this.prev) {
                pos = (int)Byte2IntLinkedOpenHashMap.this.link[pos];
                ++this.index;
            }
        }

        public int nextIndex() {
            this.ensureIndexKnown();
            return this.index;
        }

        public int previousIndex() {
            this.ensureIndexKnown();
            return this.index - 1;
        }

        public int nextEntry() {
            if (!this.hasNext()) {
                throw new NoSuchElementException();
            }
            this.curr = this.next;
            this.next = (int)Byte2IntLinkedOpenHashMap.this.link[this.curr];
            this.prev = this.curr;
            if (this.index >= 0) {
                ++this.index;
            }
            return this.curr;
        }

        public int previousEntry() {
            if (!this.hasPrevious()) {
                throw new NoSuchElementException();
            }
            this.curr = this.prev;
            this.prev = (int)(Byte2IntLinkedOpenHashMap.this.link[this.curr] >>> 32);
            this.next = this.curr;
            if (this.index >= 0) {
                --this.index;
            }
            return this.curr;
        }

        public void remove() {
            this.ensureIndexKnown();
            if (this.curr == -1) {
                throw new IllegalStateException();
            }
            if (this.curr == this.prev) {
                --this.index;
                this.prev = (int)(Byte2IntLinkedOpenHashMap.this.link[this.curr] >>> 32);
            } else {
                this.next = (int)Byte2IntLinkedOpenHashMap.this.link[this.curr];
            }
            --Byte2IntLinkedOpenHashMap.this.size;
            if (this.prev == -1) {
                Byte2IntLinkedOpenHashMap.this.first = this.next;
            } else {
                int n2 = this.prev;
                Byte2IntLinkedOpenHashMap.this.link[n2] = Byte2IntLinkedOpenHashMap.this.link[n2] ^ (Byte2IntLinkedOpenHashMap.this.link[this.prev] ^ (long)this.next & 0xFFFFFFFFL) & 0xFFFFFFFFL;
            }
            if (this.next == -1) {
                Byte2IntLinkedOpenHashMap.this.last = this.prev;
            } else {
                int n3 = this.next;
                Byte2IntLinkedOpenHashMap.this.link[n3] = Byte2IntLinkedOpenHashMap.this.link[n3] ^ (Byte2IntLinkedOpenHashMap.this.link[this.next] ^ ((long)this.prev & 0xFFFFFFFFL) << 32) & 0xFFFFFFFF00000000L;
            }
            int pos = this.curr;
            this.curr = -1;
            if (pos != Byte2IntLinkedOpenHashMap.this.n) {
                byte[] key = Byte2IntLinkedOpenHashMap.this.key;
                while (true) {
                    byte curr;
                    int last = pos;
                    pos = last + 1 & Byte2IntLinkedOpenHashMap.this.mask;
                    while (true) {
                        if ((curr = key[pos]) == 0) {
                            key[last] = 0;
                            return;
                        }
                        int slot = HashCommon.mix(curr) & Byte2IntLinkedOpenHashMap.this.mask;
                        if (last <= pos ? last >= slot || slot > pos : last >= slot && slot > pos) break;
                        pos = pos + 1 & Byte2IntLinkedOpenHashMap.this.mask;
                    }
                    key[last] = curr;
                    Byte2IntLinkedOpenHashMap.this.value[last] = Byte2IntLinkedOpenHashMap.this.value[pos];
                    if (this.next == pos) {
                        this.next = last;
                    }
                    if (this.prev == pos) {
                        this.prev = last;
                    }
                    Byte2IntLinkedOpenHashMap.this.fixPointers(pos, last);
                }
            }
            Byte2IntLinkedOpenHashMap.this.containsNullKey = false;
        }

        public int skip(int n2) {
            int i2 = n2;
            while (i2-- != 0 && this.hasNext()) {
                this.nextEntry();
            }
            return n2 - i2 - 1;
        }

        public int back(int n2) {
            int i2 = n2;
            while (i2-- != 0 && this.hasPrevious()) {
                this.previousEntry();
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
            return Byte2IntLinkedOpenHashMap.this.key[this.index];
        }

        @Override
        public byte getByteKey() {
            return Byte2IntLinkedOpenHashMap.this.key[this.index];
        }

        @Override
        @Deprecated
        public Integer getValue() {
            return Byte2IntLinkedOpenHashMap.this.value[this.index];
        }

        @Override
        public int getIntValue() {
            return Byte2IntLinkedOpenHashMap.this.value[this.index];
        }

        @Override
        public int setValue(int v2) {
            int oldValue = Byte2IntLinkedOpenHashMap.this.value[this.index];
            Byte2IntLinkedOpenHashMap.this.value[this.index] = v2;
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
            return Byte2IntLinkedOpenHashMap.this.key[this.index] == (Byte)e2.getKey() && Byte2IntLinkedOpenHashMap.this.value[this.index] == (Integer)e2.getValue();
        }

        @Override
        public int hashCode() {
            return Byte2IntLinkedOpenHashMap.this.key[this.index] ^ Byte2IntLinkedOpenHashMap.this.value[this.index];
        }

        public String toString() {
            return Byte2IntLinkedOpenHashMap.this.key[this.index] + "=>" + Byte2IntLinkedOpenHashMap.this.value[this.index];
        }
    }
}

