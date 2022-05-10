package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.bytes.AbstractByteCollection;
import it.unimi.dsi.fastutil.bytes.ByteCollection;
import it.unimi.dsi.fastutil.bytes.ByteIterator;
import it.unimi.dsi.fastutil.bytes.ByteListIterator;
import it.unimi.dsi.fastutil.chars.AbstractChar2ByteSortedMap;
import it.unimi.dsi.fastutil.chars.AbstractCharSortedSet;
import it.unimi.dsi.fastutil.chars.Char2ByteMap;
import it.unimi.dsi.fastutil.chars.Char2ByteSortedMap;
import it.unimi.dsi.fastutil.chars.CharComparator;
import it.unimi.dsi.fastutil.chars.CharListIterator;
import it.unimi.dsi.fastutil.chars.CharSortedSet;
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

public class Char2ByteLinkedOpenHashMap
extends AbstractChar2ByteSortedMap
implements Serializable,
Cloneable,
Hash {
    private static final long serialVersionUID = 0L;
    private static final boolean ASSERTS = false;
    protected transient char[] key;
    protected transient byte[] value;
    protected transient int mask;
    protected transient boolean containsNullKey;
    protected transient int first = -1;
    protected transient int last = -1;
    protected transient long[] link;
    protected transient int n;
    protected transient int maxFill;
    protected int size;
    protected final float f;
    protected transient Char2ByteSortedMap.FastSortedEntrySet entries;
    protected transient CharSortedSet keys;
    protected transient ByteCollection values;

    public Char2ByteLinkedOpenHashMap(int expected, float f2) {
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
        this.key = new char[this.n + 1];
        this.value = new byte[this.n + 1];
        this.link = new long[this.n + 1];
    }

    public Char2ByteLinkedOpenHashMap(int expected) {
        this(expected, 0.75f);
    }

    public Char2ByteLinkedOpenHashMap() {
        this(16, 0.75f);
    }

    public Char2ByteLinkedOpenHashMap(Map<? extends Character, ? extends Byte> m2, float f2) {
        this(m2.size(), f2);
        this.putAll(m2);
    }

    public Char2ByteLinkedOpenHashMap(Map<? extends Character, ? extends Byte> m2) {
        this(m2, 0.75f);
    }

    public Char2ByteLinkedOpenHashMap(Char2ByteMap m2, float f2) {
        this(m2.size(), f2);
        this.putAll(m2);
    }

    public Char2ByteLinkedOpenHashMap(Char2ByteMap m2) {
        this(m2, 0.75f);
    }

    public Char2ByteLinkedOpenHashMap(char[] k2, byte[] v2, float f2) {
        this(k2.length, f2);
        if (k2.length != v2.length) {
            throw new IllegalArgumentException("The key array and the value array have different lengths (" + k2.length + " and " + v2.length + ")");
        }
        for (int i2 = 0; i2 < k2.length; ++i2) {
            this.put(k2[i2], v2[i2]);
        }
    }

    public Char2ByteLinkedOpenHashMap(char[] k2, byte[] v2) {
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

    private byte removeEntry(int pos) {
        byte oldValue = this.value[pos];
        --this.size;
        this.fixPointers(pos);
        this.shiftKeys(pos);
        if (this.size < this.maxFill / 4 && this.n > 16) {
            this.rehash(this.n / 2);
        }
        return oldValue;
    }

    private byte removeNullEntry() {
        this.containsNullKey = false;
        byte oldValue = this.value[this.n];
        --this.size;
        this.fixPointers(this.n);
        if (this.size < this.maxFill / 4 && this.n > 16) {
            this.rehash(this.n / 2);
        }
        return oldValue;
    }

    @Override
    public void putAll(Map<? extends Character, ? extends Byte> m2) {
        if ((double)this.f <= 0.5) {
            this.ensureCapacity(m2.size());
        } else {
            this.tryCapacity(this.size() + m2.size());
        }
        super.putAll(m2);
    }

    private int insert(char k2, byte v2) {
        int pos;
        if (k2 == '\u0000') {
            if (this.containsNullKey) {
                return this.n;
            }
            this.containsNullKey = true;
            pos = this.n;
        } else {
            char[] key = this.key;
            pos = HashCommon.mix(k2) & this.mask;
            char curr = key[pos];
            if (curr != '\u0000') {
                if (curr == k2) {
                    return pos;
                }
                while ((curr = key[pos = pos + 1 & this.mask]) != '\u0000') {
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
    public byte put(char k2, byte v2) {
        int pos = this.insert(k2, v2);
        if (pos < 0) {
            return this.defRetValue;
        }
        byte oldValue = this.value[pos];
        this.value[pos] = v2;
        return oldValue;
    }

    @Override
    @Deprecated
    public Byte put(Character ok2, Byte ov) {
        byte v2 = ov;
        int pos = this.insert(ok2.charValue(), v2);
        if (pos < 0) {
            return null;
        }
        byte oldValue = this.value[pos];
        this.value[pos] = v2;
        return oldValue;
    }

    private byte addToValue(int pos, byte incr) {
        byte oldValue = this.value[pos];
        this.value[pos] = (byte)(oldValue + incr);
        return oldValue;
    }

    public byte addTo(char k2, byte incr) {
        int pos;
        if (k2 == '\u0000') {
            if (this.containsNullKey) {
                return this.addToValue(this.n, incr);
            }
            pos = this.n;
            this.containsNullKey = true;
        } else {
            char[] key = this.key;
            pos = HashCommon.mix(k2) & this.mask;
            char curr = key[pos];
            if (curr != '\u0000') {
                if (curr == k2) {
                    return this.addToValue(pos, incr);
                }
                while ((curr = key[pos = pos + 1 & this.mask]) != '\u0000') {
                    if (curr != k2) continue;
                    return this.addToValue(pos, incr);
                }
            }
        }
        this.key[pos] = k2;
        this.value[pos] = (byte)(this.defRetValue + incr);
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
        char[] key = this.key;
        while (true) {
            char curr;
            int last = pos;
            pos = last + 1 & this.mask;
            while (true) {
                if ((curr = key[pos]) == '\u0000') {
                    key[last] = '\u0000';
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
    public byte remove(char k2) {
        if (k2 == '\u0000') {
            if (this.containsNullKey) {
                return this.removeNullEntry();
            }
            return this.defRetValue;
        }
        char[] key = this.key;
        int pos = HashCommon.mix(k2) & this.mask;
        char curr = key[pos];
        if (curr == '\u0000') {
            return this.defRetValue;
        }
        if (k2 == curr) {
            return this.removeEntry(pos);
        }
        do {
            if ((curr = key[pos = pos + 1 & this.mask]) != '\u0000') continue;
            return this.defRetValue;
        } while (k2 != curr);
        return this.removeEntry(pos);
    }

    @Override
    @Deprecated
    public Byte remove(Object ok2) {
        char k2 = ((Character)ok2).charValue();
        if (k2 == '\u0000') {
            if (this.containsNullKey) {
                return this.removeNullEntry();
            }
            return null;
        }
        char[] key = this.key;
        int pos = HashCommon.mix(k2) & this.mask;
        char curr = key[pos];
        if (curr == '\u0000') {
            return null;
        }
        if (curr == k2) {
            return this.removeEntry(pos);
        }
        do {
            if ((curr = key[pos = pos + 1 & this.mask]) != '\u0000') continue;
            return null;
        } while (curr != k2);
        return this.removeEntry(pos);
    }

    private byte setValue(int pos, byte v2) {
        byte oldValue = this.value[pos];
        this.value[pos] = v2;
        return oldValue;
    }

    public byte removeFirstByte() {
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
        byte v2 = this.value[pos];
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

    public byte removeLastByte() {
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
        byte v2 = this.value[pos];
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

    public byte getAndMoveToFirst(char k2) {
        if (k2 == '\u0000') {
            if (this.containsNullKey) {
                this.moveIndexToFirst(this.n);
                return this.value[this.n];
            }
            return this.defRetValue;
        }
        char[] key = this.key;
        int pos = HashCommon.mix(k2) & this.mask;
        char curr = key[pos];
        if (curr == '\u0000') {
            return this.defRetValue;
        }
        if (k2 == curr) {
            this.moveIndexToFirst(pos);
            return this.value[pos];
        }
        do {
            if ((curr = key[pos = pos + 1 & this.mask]) != '\u0000') continue;
            return this.defRetValue;
        } while (k2 != curr);
        this.moveIndexToFirst(pos);
        return this.value[pos];
    }

    public byte getAndMoveToLast(char k2) {
        if (k2 == '\u0000') {
            if (this.containsNullKey) {
                this.moveIndexToLast(this.n);
                return this.value[this.n];
            }
            return this.defRetValue;
        }
        char[] key = this.key;
        int pos = HashCommon.mix(k2) & this.mask;
        char curr = key[pos];
        if (curr == '\u0000') {
            return this.defRetValue;
        }
        if (k2 == curr) {
            this.moveIndexToLast(pos);
            return this.value[pos];
        }
        do {
            if ((curr = key[pos = pos + 1 & this.mask]) != '\u0000') continue;
            return this.defRetValue;
        } while (k2 != curr);
        this.moveIndexToLast(pos);
        return this.value[pos];
    }

    public byte putAndMoveToFirst(char k2, byte v2) {
        int pos;
        if (k2 == '\u0000') {
            if (this.containsNullKey) {
                this.moveIndexToFirst(this.n);
                return this.setValue(this.n, v2);
            }
            this.containsNullKey = true;
            pos = this.n;
        } else {
            char[] key = this.key;
            pos = HashCommon.mix(k2) & this.mask;
            char curr = key[pos];
            if (curr != '\u0000') {
                if (curr == k2) {
                    this.moveIndexToFirst(pos);
                    return this.setValue(pos, v2);
                }
                while ((curr = key[pos = pos + 1 & this.mask]) != '\u0000') {
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

    public byte putAndMoveToLast(char k2, byte v2) {
        int pos;
        if (k2 == '\u0000') {
            if (this.containsNullKey) {
                this.moveIndexToLast(this.n);
                return this.setValue(this.n, v2);
            }
            this.containsNullKey = true;
            pos = this.n;
        } else {
            char[] key = this.key;
            pos = HashCommon.mix(k2) & this.mask;
            char curr = key[pos];
            if (curr != '\u0000') {
                if (curr == k2) {
                    this.moveIndexToLast(pos);
                    return this.setValue(pos, v2);
                }
                while ((curr = key[pos = pos + 1 & this.mask]) != '\u0000') {
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
    public Byte get(Character ok2) {
        if (ok2 == null) {
            return null;
        }
        char k2 = ok2.charValue();
        if (k2 == '\u0000') {
            return this.containsNullKey ? Byte.valueOf(this.value[this.n]) : null;
        }
        char[] key = this.key;
        int pos = HashCommon.mix(k2) & this.mask;
        char curr = key[pos];
        if (curr == '\u0000') {
            return null;
        }
        if (k2 == curr) {
            return this.value[pos];
        }
        do {
            if ((curr = key[pos = pos + 1 & this.mask]) != '\u0000') continue;
            return null;
        } while (k2 != curr);
        return this.value[pos];
    }

    @Override
    public byte get(char k2) {
        if (k2 == '\u0000') {
            return this.containsNullKey ? this.value[this.n] : this.defRetValue;
        }
        char[] key = this.key;
        int pos = HashCommon.mix(k2) & this.mask;
        char curr = key[pos];
        if (curr == '\u0000') {
            return this.defRetValue;
        }
        if (k2 == curr) {
            return this.value[pos];
        }
        do {
            if ((curr = key[pos = pos + 1 & this.mask]) != '\u0000') continue;
            return this.defRetValue;
        } while (k2 != curr);
        return this.value[pos];
    }

    @Override
    public boolean containsKey(char k2) {
        if (k2 == '\u0000') {
            return this.containsNullKey;
        }
        char[] key = this.key;
        int pos = HashCommon.mix(k2) & this.mask;
        char curr = key[pos];
        if (curr == '\u0000') {
            return false;
        }
        if (k2 == curr) {
            return true;
        }
        do {
            if ((curr = key[pos = pos + 1 & this.mask]) != '\u0000') continue;
            return false;
        } while (k2 != curr);
        return true;
    }

    @Override
    public boolean containsValue(byte v2) {
        byte[] value = this.value;
        char[] key = this.key;
        if (this.containsNullKey && value[this.n] == v2) {
            return true;
        }
        int i2 = this.n;
        while (i2-- != 0) {
            if (key[i2] == '\u0000' || value[i2] != v2) continue;
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
        Arrays.fill(this.key, '\u0000');
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
    public char firstCharKey() {
        if (this.size == 0) {
            throw new NoSuchElementException();
        }
        return this.key[this.first];
    }

    @Override
    public char lastCharKey() {
        if (this.size == 0) {
            throw new NoSuchElementException();
        }
        return this.key[this.last];
    }

    @Override
    public CharComparator comparator() {
        return null;
    }

    @Override
    public Char2ByteSortedMap tailMap(char from) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Char2ByteSortedMap headMap(char to2) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Char2ByteSortedMap subMap(char from, char to2) {
        throw new UnsupportedOperationException();
    }

    public Char2ByteSortedMap.FastSortedEntrySet char2ByteEntrySet() {
        if (this.entries == null) {
            this.entries = new MapEntrySet();
        }
        return this.entries;
    }

    @Override
    public CharSortedSet keySet() {
        if (this.keys == null) {
            this.keys = new KeySet();
        }
        return this.keys;
    }

    @Override
    public ByteCollection values() {
        if (this.values == null) {
            this.values = new AbstractByteCollection(){

                @Override
                public ByteIterator iterator() {
                    return new ValueIterator();
                }

                @Override
                public int size() {
                    return Char2ByteLinkedOpenHashMap.this.size;
                }

                @Override
                public boolean contains(byte v2) {
                    return Char2ByteLinkedOpenHashMap.this.containsValue(v2);
                }

                @Override
                public void clear() {
                    Char2ByteLinkedOpenHashMap.this.clear();
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
        char[] key = this.key;
        byte[] value = this.value;
        int mask = newN - 1;
        char[] newKey = new char[newN + 1];
        byte[] newValue = new byte[newN + 1];
        int i2 = this.first;
        int prev = -1;
        int newPrev = -1;
        long[] link = this.link;
        long[] newLink = new long[newN + 1];
        this.first = -1;
        int j2 = this.size;
        while (j2-- != 0) {
            int pos;
            if (key[i2] == '\u0000') {
                pos = newN;
            } else {
                pos = HashCommon.mix(key[i2]) & mask;
                while (newKey[pos] != '\u0000') {
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

    public Char2ByteLinkedOpenHashMap clone() {
        Char2ByteLinkedOpenHashMap c2;
        try {
            c2 = (Char2ByteLinkedOpenHashMap)super.clone();
        }
        catch (CloneNotSupportedException cantHappen) {
            throw new InternalError();
        }
        c2.keys = null;
        c2.values = null;
        c2.entries = null;
        c2.containsNullKey = this.containsNullKey;
        c2.key = (char[])this.key.clone();
        c2.value = (byte[])this.value.clone();
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
            while (this.key[i2] == '\u0000') {
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
        char[] key = this.key;
        byte[] value = this.value;
        MapIterator i2 = new MapIterator();
        s2.defaultWriteObject();
        int j2 = this.size;
        while (j2-- != 0) {
            int e2 = i2.nextEntry();
            s2.writeChar(key[e2]);
            s2.writeByte(value[e2]);
        }
    }

    private void readObject(ObjectInputStream s2) throws IOException, ClassNotFoundException {
        s2.defaultReadObject();
        this.n = HashCommon.arraySize(this.size, this.f);
        this.maxFill = HashCommon.maxFill(this.n, this.f);
        this.mask = this.n - 1;
        this.key = new char[this.n + 1];
        char[] key = this.key;
        this.value = new byte[this.n + 1];
        byte[] value = this.value;
        this.link = new long[this.n + 1];
        long[] link = this.link;
        int prev = -1;
        this.last = -1;
        this.first = -1;
        int i2 = this.size;
        while (i2-- != 0) {
            int pos;
            char k2 = s2.readChar();
            byte v2 = s2.readByte();
            if (k2 == '\u0000') {
                pos = this.n;
                this.containsNullKey = true;
            } else {
                pos = HashCommon.mix(k2) & this.mask;
                while (key[pos] != '\u0000') {
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
    implements ByteListIterator {
        @Override
        public byte previousByte() {
            return Char2ByteLinkedOpenHashMap.this.value[this.previousEntry()];
        }

        @Override
        public Byte previous() {
            return Char2ByteLinkedOpenHashMap.this.value[this.previousEntry()];
        }

        @Override
        public void set(Byte ok2) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void add(Byte ok2) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void set(byte v2) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void add(byte v2) {
            throw new UnsupportedOperationException();
        }

        @Override
        public byte nextByte() {
            return Char2ByteLinkedOpenHashMap.this.value[this.nextEntry()];
        }

        @Override
        @Deprecated
        public Byte next() {
            return Char2ByteLinkedOpenHashMap.this.value[this.nextEntry()];
        }
    }

    private final class KeySet
    extends AbstractCharSortedSet {
        private KeySet() {
        }

        @Override
        public CharListIterator iterator(char from) {
            return new KeyIterator(from);
        }

        @Override
        public CharListIterator iterator() {
            return new KeyIterator();
        }

        @Override
        public int size() {
            return Char2ByteLinkedOpenHashMap.this.size;
        }

        @Override
        public boolean contains(char k2) {
            return Char2ByteLinkedOpenHashMap.this.containsKey(k2);
        }

        @Override
        public boolean rem(char k2) {
            int oldSize = Char2ByteLinkedOpenHashMap.this.size;
            Char2ByteLinkedOpenHashMap.this.remove(k2);
            return Char2ByteLinkedOpenHashMap.this.size != oldSize;
        }

        @Override
        public void clear() {
            Char2ByteLinkedOpenHashMap.this.clear();
        }

        @Override
        public char firstChar() {
            if (Char2ByteLinkedOpenHashMap.this.size == 0) {
                throw new NoSuchElementException();
            }
            return Char2ByteLinkedOpenHashMap.this.key[Char2ByteLinkedOpenHashMap.this.first];
        }

        @Override
        public char lastChar() {
            if (Char2ByteLinkedOpenHashMap.this.size == 0) {
                throw new NoSuchElementException();
            }
            return Char2ByteLinkedOpenHashMap.this.key[Char2ByteLinkedOpenHashMap.this.last];
        }

        @Override
        public CharComparator comparator() {
            return null;
        }

        @Override
        public final CharSortedSet tailSet(char from) {
            throw new UnsupportedOperationException();
        }

        @Override
        public final CharSortedSet headSet(char to2) {
            throw new UnsupportedOperationException();
        }

        @Override
        public final CharSortedSet subSet(char from, char to2) {
            throw new UnsupportedOperationException();
        }
    }

    private final class KeyIterator
    extends MapIterator
    implements CharListIterator {
        public KeyIterator(char k2) {
            super(k2);
        }

        @Override
        public char previousChar() {
            return Char2ByteLinkedOpenHashMap.this.key[this.previousEntry()];
        }

        @Override
        public void set(char k2) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void add(char k2) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Character previous() {
            return Character.valueOf(Char2ByteLinkedOpenHashMap.this.key[this.previousEntry()]);
        }

        @Override
        public void set(Character ok2) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void add(Character ok2) {
            throw new UnsupportedOperationException();
        }

        public KeyIterator() {
        }

        @Override
        public char nextChar() {
            return Char2ByteLinkedOpenHashMap.this.key[this.nextEntry()];
        }

        @Override
        public Character next() {
            return Character.valueOf(Char2ByteLinkedOpenHashMap.this.key[this.nextEntry()]);
        }
    }

    private final class MapEntrySet
    extends AbstractObjectSortedSet<Char2ByteMap.Entry>
    implements Char2ByteSortedMap.FastSortedEntrySet {
        private MapEntrySet() {
        }

        @Override
        public ObjectBidirectionalIterator<Char2ByteMap.Entry> iterator() {
            return new EntryIterator();
        }

        @Override
        public Comparator<? super Char2ByteMap.Entry> comparator() {
            return null;
        }

        @Override
        public ObjectSortedSet<Char2ByteMap.Entry> subSet(Char2ByteMap.Entry fromElement, Char2ByteMap.Entry toElement) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ObjectSortedSet<Char2ByteMap.Entry> headSet(Char2ByteMap.Entry toElement) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ObjectSortedSet<Char2ByteMap.Entry> tailSet(Char2ByteMap.Entry fromElement) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Char2ByteMap.Entry first() {
            if (Char2ByteLinkedOpenHashMap.this.size == 0) {
                throw new NoSuchElementException();
            }
            return new MapEntry(Char2ByteLinkedOpenHashMap.this.first);
        }

        @Override
        public Char2ByteMap.Entry last() {
            if (Char2ByteLinkedOpenHashMap.this.size == 0) {
                throw new NoSuchElementException();
            }
            return new MapEntry(Char2ByteLinkedOpenHashMap.this.last);
        }

        @Override
        public boolean contains(Object o2) {
            if (!(o2 instanceof Map.Entry)) {
                return false;
            }
            Map.Entry e2 = (Map.Entry)o2;
            if (e2.getKey() == null || !(e2.getKey() instanceof Character)) {
                return false;
            }
            if (e2.getValue() == null || !(e2.getValue() instanceof Byte)) {
                return false;
            }
            char k2 = ((Character)e2.getKey()).charValue();
            byte v2 = (Byte)e2.getValue();
            if (k2 == '\u0000') {
                return Char2ByteLinkedOpenHashMap.this.containsNullKey && Char2ByteLinkedOpenHashMap.this.value[Char2ByteLinkedOpenHashMap.this.n] == v2;
            }
            char[] key = Char2ByteLinkedOpenHashMap.this.key;
            int pos = HashCommon.mix(k2) & Char2ByteLinkedOpenHashMap.this.mask;
            char curr = key[pos];
            if (curr == '\u0000') {
                return false;
            }
            if (k2 == curr) {
                return Char2ByteLinkedOpenHashMap.this.value[pos] == v2;
            }
            do {
                if ((curr = key[pos = pos + 1 & Char2ByteLinkedOpenHashMap.this.mask]) != '\u0000') continue;
                return false;
            } while (k2 != curr);
            return Char2ByteLinkedOpenHashMap.this.value[pos] == v2;
        }

        @Override
        public boolean rem(Object o2) {
            if (!(o2 instanceof Map.Entry)) {
                return false;
            }
            Map.Entry e2 = (Map.Entry)o2;
            if (e2.getKey() == null || !(e2.getKey() instanceof Character)) {
                return false;
            }
            if (e2.getValue() == null || !(e2.getValue() instanceof Byte)) {
                return false;
            }
            char k2 = ((Character)e2.getKey()).charValue();
            byte v2 = (Byte)e2.getValue();
            if (k2 == '\u0000') {
                if (Char2ByteLinkedOpenHashMap.this.containsNullKey && Char2ByteLinkedOpenHashMap.this.value[Char2ByteLinkedOpenHashMap.this.n] == v2) {
                    Char2ByteLinkedOpenHashMap.this.removeNullEntry();
                    return true;
                }
                return false;
            }
            char[] key = Char2ByteLinkedOpenHashMap.this.key;
            int pos = HashCommon.mix(k2) & Char2ByteLinkedOpenHashMap.this.mask;
            char curr = key[pos];
            if (curr == '\u0000') {
                return false;
            }
            if (curr == k2) {
                if (Char2ByteLinkedOpenHashMap.this.value[pos] == v2) {
                    Char2ByteLinkedOpenHashMap.this.removeEntry(pos);
                    return true;
                }
                return false;
            }
            do {
                if ((curr = key[pos = pos + 1 & Char2ByteLinkedOpenHashMap.this.mask]) != '\u0000') continue;
                return false;
            } while (curr != k2 || Char2ByteLinkedOpenHashMap.this.value[pos] != v2);
            Char2ByteLinkedOpenHashMap.this.removeEntry(pos);
            return true;
        }

        @Override
        public int size() {
            return Char2ByteLinkedOpenHashMap.this.size;
        }

        @Override
        public void clear() {
            Char2ByteLinkedOpenHashMap.this.clear();
        }

        @Override
        public ObjectBidirectionalIterator<Char2ByteMap.Entry> iterator(Char2ByteMap.Entry from) {
            return new EntryIterator(from.getCharKey());
        }

        public ObjectBidirectionalIterator<Char2ByteMap.Entry> fastIterator() {
            return new FastEntryIterator();
        }

        @Override
        public ObjectBidirectionalIterator<Char2ByteMap.Entry> fastIterator(Char2ByteMap.Entry from) {
            return new FastEntryIterator(from.getCharKey());
        }
    }

    private class FastEntryIterator
    extends MapIterator
    implements ObjectListIterator<Char2ByteMap.Entry> {
        final MapEntry entry;

        public FastEntryIterator() {
            this.entry = new MapEntry();
        }

        public FastEntryIterator(char from) {
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
        public void set(Char2ByteMap.Entry ok2) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void add(Char2ByteMap.Entry ok2) {
            throw new UnsupportedOperationException();
        }
    }

    private class EntryIterator
    extends MapIterator
    implements ObjectListIterator<Char2ByteMap.Entry> {
        private MapEntry entry;

        public EntryIterator() {
        }

        public EntryIterator(char from) {
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
        public void set(Char2ByteMap.Entry ok2) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void add(Char2ByteMap.Entry ok2) {
            throw new UnsupportedOperationException();
        }
    }

    private class MapIterator {
        int prev = -1;
        int next = -1;
        int curr = -1;
        int index = -1;

        private MapIterator() {
            this.next = Char2ByteLinkedOpenHashMap.this.first;
            this.index = 0;
        }

        private MapIterator(char from) {
            if (from == '\u0000') {
                if (Char2ByteLinkedOpenHashMap.this.containsNullKey) {
                    this.next = (int)Char2ByteLinkedOpenHashMap.this.link[Char2ByteLinkedOpenHashMap.this.n];
                    this.prev = Char2ByteLinkedOpenHashMap.this.n;
                    return;
                }
                throw new NoSuchElementException("The key " + from + " does not belong to this map.");
            }
            if (Char2ByteLinkedOpenHashMap.this.key[Char2ByteLinkedOpenHashMap.this.last] == from) {
                this.prev = Char2ByteLinkedOpenHashMap.this.last;
                this.index = Char2ByteLinkedOpenHashMap.this.size;
                return;
            }
            int pos = HashCommon.mix(from) & Char2ByteLinkedOpenHashMap.this.mask;
            while (Char2ByteLinkedOpenHashMap.this.key[pos] != '\u0000') {
                if (Char2ByteLinkedOpenHashMap.this.key[pos] == from) {
                    this.next = (int)Char2ByteLinkedOpenHashMap.this.link[pos];
                    this.prev = pos;
                    return;
                }
                pos = pos + 1 & Char2ByteLinkedOpenHashMap.this.mask;
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
                this.index = Char2ByteLinkedOpenHashMap.this.size;
                return;
            }
            int pos = Char2ByteLinkedOpenHashMap.this.first;
            this.index = 1;
            while (pos != this.prev) {
                pos = (int)Char2ByteLinkedOpenHashMap.this.link[pos];
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
            this.next = (int)Char2ByteLinkedOpenHashMap.this.link[this.curr];
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
            this.prev = (int)(Char2ByteLinkedOpenHashMap.this.link[this.curr] >>> 32);
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
                this.prev = (int)(Char2ByteLinkedOpenHashMap.this.link[this.curr] >>> 32);
            } else {
                this.next = (int)Char2ByteLinkedOpenHashMap.this.link[this.curr];
            }
            --Char2ByteLinkedOpenHashMap.this.size;
            if (this.prev == -1) {
                Char2ByteLinkedOpenHashMap.this.first = this.next;
            } else {
                int n2 = this.prev;
                Char2ByteLinkedOpenHashMap.this.link[n2] = Char2ByteLinkedOpenHashMap.this.link[n2] ^ (Char2ByteLinkedOpenHashMap.this.link[this.prev] ^ (long)this.next & 0xFFFFFFFFL) & 0xFFFFFFFFL;
            }
            if (this.next == -1) {
                Char2ByteLinkedOpenHashMap.this.last = this.prev;
            } else {
                int n3 = this.next;
                Char2ByteLinkedOpenHashMap.this.link[n3] = Char2ByteLinkedOpenHashMap.this.link[n3] ^ (Char2ByteLinkedOpenHashMap.this.link[this.next] ^ ((long)this.prev & 0xFFFFFFFFL) << 32) & 0xFFFFFFFF00000000L;
            }
            int pos = this.curr;
            this.curr = -1;
            if (pos != Char2ByteLinkedOpenHashMap.this.n) {
                char[] key = Char2ByteLinkedOpenHashMap.this.key;
                while (true) {
                    char curr;
                    int last = pos;
                    pos = last + 1 & Char2ByteLinkedOpenHashMap.this.mask;
                    while (true) {
                        if ((curr = key[pos]) == '\u0000') {
                            key[last] = '\u0000';
                            return;
                        }
                        int slot = HashCommon.mix(curr) & Char2ByteLinkedOpenHashMap.this.mask;
                        if (last <= pos ? last >= slot || slot > pos : last >= slot && slot > pos) break;
                        pos = pos + 1 & Char2ByteLinkedOpenHashMap.this.mask;
                    }
                    key[last] = curr;
                    Char2ByteLinkedOpenHashMap.this.value[last] = Char2ByteLinkedOpenHashMap.this.value[pos];
                    if (this.next == pos) {
                        this.next = last;
                    }
                    if (this.prev == pos) {
                        this.prev = last;
                    }
                    Char2ByteLinkedOpenHashMap.this.fixPointers(pos, last);
                }
            }
            Char2ByteLinkedOpenHashMap.this.containsNullKey = false;
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
    implements Char2ByteMap.Entry,
    Map.Entry<Character, Byte> {
        int index;

        MapEntry(int index) {
            this.index = index;
        }

        MapEntry() {
        }

        @Override
        @Deprecated
        public Character getKey() {
            return Character.valueOf(Char2ByteLinkedOpenHashMap.this.key[this.index]);
        }

        @Override
        public char getCharKey() {
            return Char2ByteLinkedOpenHashMap.this.key[this.index];
        }

        @Override
        @Deprecated
        public Byte getValue() {
            return Char2ByteLinkedOpenHashMap.this.value[this.index];
        }

        @Override
        public byte getByteValue() {
            return Char2ByteLinkedOpenHashMap.this.value[this.index];
        }

        @Override
        public byte setValue(byte v2) {
            byte oldValue = Char2ByteLinkedOpenHashMap.this.value[this.index];
            Char2ByteLinkedOpenHashMap.this.value[this.index] = v2;
            return oldValue;
        }

        @Override
        public Byte setValue(Byte v2) {
            return this.setValue((byte)v2);
        }

        @Override
        public boolean equals(Object o2) {
            if (!(o2 instanceof Map.Entry)) {
                return false;
            }
            Map.Entry e2 = (Map.Entry)o2;
            return Char2ByteLinkedOpenHashMap.this.key[this.index] == ((Character)e2.getKey()).charValue() && Char2ByteLinkedOpenHashMap.this.value[this.index] == (Byte)e2.getValue();
        }

        @Override
        public int hashCode() {
            return Char2ByteLinkedOpenHashMap.this.key[this.index] ^ Char2ByteLinkedOpenHashMap.this.value[this.index];
        }

        public String toString() {
            return Char2ByteLinkedOpenHashMap.this.key[this.index] + "=>" + Char2ByteLinkedOpenHashMap.this.value[this.index];
        }
    }
}

