package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.booleans.AbstractBooleanCollection;
import it.unimi.dsi.fastutil.booleans.BooleanCollection;
import it.unimi.dsi.fastutil.booleans.BooleanIterator;
import it.unimi.dsi.fastutil.chars.AbstractChar2BooleanMap;
import it.unimi.dsi.fastutil.chars.AbstractCharSet;
import it.unimi.dsi.fastutil.chars.Char2BooleanMap;
import it.unimi.dsi.fastutil.chars.CharArrayList;
import it.unimi.dsi.fastutil.chars.CharHash;
import it.unimi.dsi.fastutil.chars.CharIterator;
import it.unimi.dsi.fastutil.chars.CharSet;
import it.unimi.dsi.fastutil.objects.AbstractObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Map;
import java.util.NoSuchElementException;

public class Char2BooleanOpenCustomHashMap
extends AbstractChar2BooleanMap
implements Serializable,
Cloneable,
Hash {
    private static final long serialVersionUID = 0L;
    private static final boolean ASSERTS = false;
    protected transient char[] key;
    protected transient boolean[] value;
    protected transient int mask;
    protected transient boolean containsNullKey;
    protected CharHash.Strategy strategy;
    protected transient int n;
    protected transient int maxFill;
    protected int size;
    protected final float f;
    protected transient Char2BooleanMap.FastEntrySet entries;
    protected transient CharSet keys;
    protected transient BooleanCollection values;

    public Char2BooleanOpenCustomHashMap(int expected, float f2, CharHash.Strategy strategy) {
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
        this.key = new char[this.n + 1];
        this.value = new boolean[this.n + 1];
    }

    public Char2BooleanOpenCustomHashMap(int expected, CharHash.Strategy strategy) {
        this(expected, 0.75f, strategy);
    }

    public Char2BooleanOpenCustomHashMap(CharHash.Strategy strategy) {
        this(16, 0.75f, strategy);
    }

    public Char2BooleanOpenCustomHashMap(Map<? extends Character, ? extends Boolean> m2, float f2, CharHash.Strategy strategy) {
        this(m2.size(), f2, strategy);
        this.putAll(m2);
    }

    public Char2BooleanOpenCustomHashMap(Map<? extends Character, ? extends Boolean> m2, CharHash.Strategy strategy) {
        this(m2, 0.75f, strategy);
    }

    public Char2BooleanOpenCustomHashMap(Char2BooleanMap m2, float f2, CharHash.Strategy strategy) {
        this(m2.size(), f2, strategy);
        this.putAll(m2);
    }

    public Char2BooleanOpenCustomHashMap(Char2BooleanMap m2, CharHash.Strategy strategy) {
        this(m2, 0.75f, strategy);
    }

    public Char2BooleanOpenCustomHashMap(char[] k2, boolean[] v2, float f2, CharHash.Strategy strategy) {
        this(k2.length, f2, strategy);
        if (k2.length != v2.length) {
            throw new IllegalArgumentException("The key array and the value array have different lengths (" + k2.length + " and " + v2.length + ")");
        }
        for (int i2 = 0; i2 < k2.length; ++i2) {
            this.put(k2[i2], v2[i2]);
        }
    }

    public Char2BooleanOpenCustomHashMap(char[] k2, boolean[] v2, CharHash.Strategy strategy) {
        this(k2, v2, 0.75f, strategy);
    }

    public CharHash.Strategy strategy() {
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

    private boolean removeEntry(int pos) {
        boolean oldValue = this.value[pos];
        --this.size;
        this.shiftKeys(pos);
        if (this.size < this.maxFill / 4 && this.n > 16) {
            this.rehash(this.n / 2);
        }
        return oldValue;
    }

    private boolean removeNullEntry() {
        this.containsNullKey = false;
        boolean oldValue = this.value[this.n];
        --this.size;
        if (this.size < this.maxFill / 4 && this.n > 16) {
            this.rehash(this.n / 2);
        }
        return oldValue;
    }

    @Override
    public void putAll(Map<? extends Character, ? extends Boolean> m2) {
        if ((double)this.f <= 0.5) {
            this.ensureCapacity(m2.size());
        } else {
            this.tryCapacity(this.size() + m2.size());
        }
        super.putAll(m2);
    }

    private int insert(char k2, boolean v2) {
        int pos;
        if (this.strategy.equals(k2, '\u0000')) {
            if (this.containsNullKey) {
                return this.n;
            }
            this.containsNullKey = true;
            pos = this.n;
        } else {
            char[] key = this.key;
            pos = HashCommon.mix(this.strategy.hashCode(k2)) & this.mask;
            char curr = key[pos];
            if (curr != '\u0000') {
                if (this.strategy.equals(curr, k2)) {
                    return pos;
                }
                while ((curr = key[pos = pos + 1 & this.mask]) != '\u0000') {
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
    public boolean put(char k2, boolean v2) {
        int pos = this.insert(k2, v2);
        if (pos < 0) {
            return this.defRetValue;
        }
        boolean oldValue = this.value[pos];
        this.value[pos] = v2;
        return oldValue;
    }

    @Override
    @Deprecated
    public Boolean put(Character ok2, Boolean ov) {
        boolean v2 = ov;
        int pos = this.insert(ok2.charValue(), v2);
        if (pos < 0) {
            return null;
        }
        boolean oldValue = this.value[pos];
        this.value[pos] = v2;
        return oldValue;
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
                int slot = HashCommon.mix(this.strategy.hashCode(curr)) & this.mask;
                if (last <= pos ? last >= slot || slot > pos : last >= slot && slot > pos) break;
                pos = pos + 1 & this.mask;
            }
            key[last] = curr;
            this.value[last] = this.value[pos];
        }
    }

    @Override
    public boolean remove(char k2) {
        if (this.strategy.equals(k2, '\u0000')) {
            if (this.containsNullKey) {
                return this.removeNullEntry();
            }
            return this.defRetValue;
        }
        char[] key = this.key;
        int pos = HashCommon.mix(this.strategy.hashCode(k2)) & this.mask;
        char curr = key[pos];
        if (curr == '\u0000') {
            return this.defRetValue;
        }
        if (this.strategy.equals(k2, curr)) {
            return this.removeEntry(pos);
        }
        do {
            if ((curr = key[pos = pos + 1 & this.mask]) != '\u0000') continue;
            return this.defRetValue;
        } while (!this.strategy.equals(k2, curr));
        return this.removeEntry(pos);
    }

    @Override
    @Deprecated
    public Boolean remove(Object ok2) {
        char k2 = ((Character)ok2).charValue();
        if (this.strategy.equals(k2, '\u0000')) {
            if (this.containsNullKey) {
                return this.removeNullEntry();
            }
            return null;
        }
        char[] key = this.key;
        int pos = HashCommon.mix(this.strategy.hashCode(k2)) & this.mask;
        char curr = key[pos];
        if (curr == '\u0000') {
            return null;
        }
        if (this.strategy.equals(curr, k2)) {
            return this.removeEntry(pos);
        }
        do {
            if ((curr = key[pos = pos + 1 & this.mask]) != '\u0000') continue;
            return null;
        } while (!this.strategy.equals(curr, k2));
        return this.removeEntry(pos);
    }

    @Deprecated
    public Boolean get(Character ok2) {
        if (ok2 == null) {
            return null;
        }
        char k2 = ok2.charValue();
        if (this.strategy.equals(k2, '\u0000')) {
            return this.containsNullKey ? Boolean.valueOf(this.value[this.n]) : null;
        }
        char[] key = this.key;
        int pos = HashCommon.mix(this.strategy.hashCode(k2)) & this.mask;
        char curr = key[pos];
        if (curr == '\u0000') {
            return null;
        }
        if (this.strategy.equals(k2, curr)) {
            return this.value[pos];
        }
        do {
            if ((curr = key[pos = pos + 1 & this.mask]) != '\u0000') continue;
            return null;
        } while (!this.strategy.equals(k2, curr));
        return this.value[pos];
    }

    @Override
    public boolean get(char k2) {
        if (this.strategy.equals(k2, '\u0000')) {
            return this.containsNullKey ? this.value[this.n] : this.defRetValue;
        }
        char[] key = this.key;
        int pos = HashCommon.mix(this.strategy.hashCode(k2)) & this.mask;
        char curr = key[pos];
        if (curr == '\u0000') {
            return this.defRetValue;
        }
        if (this.strategy.equals(k2, curr)) {
            return this.value[pos];
        }
        do {
            if ((curr = key[pos = pos + 1 & this.mask]) != '\u0000') continue;
            return this.defRetValue;
        } while (!this.strategy.equals(k2, curr));
        return this.value[pos];
    }

    @Override
    public boolean containsKey(char k2) {
        if (this.strategy.equals(k2, '\u0000')) {
            return this.containsNullKey;
        }
        char[] key = this.key;
        int pos = HashCommon.mix(this.strategy.hashCode(k2)) & this.mask;
        char curr = key[pos];
        if (curr == '\u0000') {
            return false;
        }
        if (this.strategy.equals(k2, curr)) {
            return true;
        }
        do {
            if ((curr = key[pos = pos + 1 & this.mask]) != '\u0000') continue;
            return false;
        } while (!this.strategy.equals(k2, curr));
        return true;
    }

    @Override
    public boolean containsValue(boolean v2) {
        boolean[] value = this.value;
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

    public Char2BooleanMap.FastEntrySet char2BooleanEntrySet() {
        if (this.entries == null) {
            this.entries = new MapEntrySet();
        }
        return this.entries;
    }

    @Override
    public CharSet keySet() {
        if (this.keys == null) {
            this.keys = new KeySet();
        }
        return this.keys;
    }

    @Override
    public BooleanCollection values() {
        if (this.values == null) {
            this.values = new AbstractBooleanCollection(){

                @Override
                public BooleanIterator iterator() {
                    return new ValueIterator();
                }

                @Override
                public int size() {
                    return Char2BooleanOpenCustomHashMap.this.size;
                }

                @Override
                public boolean contains(boolean v2) {
                    return Char2BooleanOpenCustomHashMap.this.containsValue(v2);
                }

                @Override
                public void clear() {
                    Char2BooleanOpenCustomHashMap.this.clear();
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
        boolean[] value = this.value;
        int mask = newN - 1;
        char[] newKey = new char[newN + 1];
        boolean[] newValue = new boolean[newN + 1];
        int i2 = this.n;
        int j2 = this.realSize();
        while (j2-- != 0) {
            while (key[--i2] == '\u0000') {
            }
            int pos = HashCommon.mix(this.strategy.hashCode(key[i2])) & mask;
            if (newKey[pos] != '\u0000') {
                while (newKey[pos = pos + 1 & mask] != '\u0000') {
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

    public Char2BooleanOpenCustomHashMap clone() {
        Char2BooleanOpenCustomHashMap c2;
        try {
            c2 = (Char2BooleanOpenCustomHashMap)super.clone();
        }
        catch (CloneNotSupportedException cantHappen) {
            throw new InternalError();
        }
        c2.keys = null;
        c2.values = null;
        c2.entries = null;
        c2.containsNullKey = this.containsNullKey;
        c2.key = (char[])this.key.clone();
        c2.value = (boolean[])this.value.clone();
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
            while (this.key[i2] == '\u0000') {
                ++i2;
            }
            t2 = this.strategy.hashCode(this.key[i2]);
            h2 += (t2 ^= this.value[i2] ? 1231 : 1237);
            ++i2;
        }
        if (this.containsNullKey) {
            h2 += this.value[this.n] ? 1231 : 1237;
        }
        return h2;
    }

    private void writeObject(ObjectOutputStream s2) throws IOException {
        char[] key = this.key;
        boolean[] value = this.value;
        MapIterator i2 = new MapIterator();
        s2.defaultWriteObject();
        int j2 = this.size;
        while (j2-- != 0) {
            int e2 = i2.nextEntry();
            s2.writeChar(key[e2]);
            s2.writeBoolean(value[e2]);
        }
    }

    private void readObject(ObjectInputStream s2) throws IOException, ClassNotFoundException {
        s2.defaultReadObject();
        this.n = HashCommon.arraySize(this.size, this.f);
        this.maxFill = HashCommon.maxFill(this.n, this.f);
        this.mask = this.n - 1;
        this.key = new char[this.n + 1];
        char[] key = this.key;
        this.value = new boolean[this.n + 1];
        boolean[] value = this.value;
        int i2 = this.size;
        while (i2-- != 0) {
            int pos;
            char k2 = s2.readChar();
            boolean v2 = s2.readBoolean();
            if (this.strategy.equals(k2, '\u0000')) {
                pos = this.n;
                this.containsNullKey = true;
            } else {
                pos = HashCommon.mix(this.strategy.hashCode(k2)) & this.mask;
                while (key[pos] != '\u0000') {
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
    implements BooleanIterator {
        @Override
        public boolean nextBoolean() {
            return Char2BooleanOpenCustomHashMap.this.value[this.nextEntry()];
        }

        @Override
        @Deprecated
        public Boolean next() {
            return Char2BooleanOpenCustomHashMap.this.value[this.nextEntry()];
        }
    }

    private final class KeySet
    extends AbstractCharSet {
        private KeySet() {
        }

        @Override
        public CharIterator iterator() {
            return new KeyIterator();
        }

        @Override
        public int size() {
            return Char2BooleanOpenCustomHashMap.this.size;
        }

        @Override
        public boolean contains(char k2) {
            return Char2BooleanOpenCustomHashMap.this.containsKey(k2);
        }

        @Override
        public boolean rem(char k2) {
            int oldSize = Char2BooleanOpenCustomHashMap.this.size;
            Char2BooleanOpenCustomHashMap.this.remove(k2);
            return Char2BooleanOpenCustomHashMap.this.size != oldSize;
        }

        @Override
        public void clear() {
            Char2BooleanOpenCustomHashMap.this.clear();
        }
    }

    private final class KeyIterator
    extends MapIterator
    implements CharIterator {
        @Override
        public char nextChar() {
            return Char2BooleanOpenCustomHashMap.this.key[this.nextEntry()];
        }

        @Override
        public Character next() {
            return Character.valueOf(Char2BooleanOpenCustomHashMap.this.key[this.nextEntry()]);
        }
    }

    private final class MapEntrySet
    extends AbstractObjectSet<Char2BooleanMap.Entry>
    implements Char2BooleanMap.FastEntrySet {
        private MapEntrySet() {
        }

        @Override
        public ObjectIterator<Char2BooleanMap.Entry> iterator() {
            return new EntryIterator();
        }

        @Override
        public ObjectIterator<Char2BooleanMap.Entry> fastIterator() {
            return new FastEntryIterator();
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
            if (e2.getValue() == null || !(e2.getValue() instanceof Boolean)) {
                return false;
            }
            char k2 = ((Character)e2.getKey()).charValue();
            boolean v2 = (Boolean)e2.getValue();
            if (Char2BooleanOpenCustomHashMap.this.strategy.equals(k2, '\u0000')) {
                return Char2BooleanOpenCustomHashMap.this.containsNullKey && Char2BooleanOpenCustomHashMap.this.value[Char2BooleanOpenCustomHashMap.this.n] == v2;
            }
            char[] key = Char2BooleanOpenCustomHashMap.this.key;
            int pos = HashCommon.mix(Char2BooleanOpenCustomHashMap.this.strategy.hashCode(k2)) & Char2BooleanOpenCustomHashMap.this.mask;
            char curr = key[pos];
            if (curr == '\u0000') {
                return false;
            }
            if (Char2BooleanOpenCustomHashMap.this.strategy.equals(k2, curr)) {
                return Char2BooleanOpenCustomHashMap.this.value[pos] == v2;
            }
            do {
                if ((curr = key[pos = pos + 1 & Char2BooleanOpenCustomHashMap.this.mask]) != '\u0000') continue;
                return false;
            } while (!Char2BooleanOpenCustomHashMap.this.strategy.equals(k2, curr));
            return Char2BooleanOpenCustomHashMap.this.value[pos] == v2;
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
            if (e2.getValue() == null || !(e2.getValue() instanceof Boolean)) {
                return false;
            }
            char k2 = ((Character)e2.getKey()).charValue();
            boolean v2 = (Boolean)e2.getValue();
            if (Char2BooleanOpenCustomHashMap.this.strategy.equals(k2, '\u0000')) {
                if (Char2BooleanOpenCustomHashMap.this.containsNullKey && Char2BooleanOpenCustomHashMap.this.value[Char2BooleanOpenCustomHashMap.this.n] == v2) {
                    Char2BooleanOpenCustomHashMap.this.removeNullEntry();
                    return true;
                }
                return false;
            }
            char[] key = Char2BooleanOpenCustomHashMap.this.key;
            int pos = HashCommon.mix(Char2BooleanOpenCustomHashMap.this.strategy.hashCode(k2)) & Char2BooleanOpenCustomHashMap.this.mask;
            char curr = key[pos];
            if (curr == '\u0000') {
                return false;
            }
            if (Char2BooleanOpenCustomHashMap.this.strategy.equals(curr, k2)) {
                if (Char2BooleanOpenCustomHashMap.this.value[pos] == v2) {
                    Char2BooleanOpenCustomHashMap.this.removeEntry(pos);
                    return true;
                }
                return false;
            }
            do {
                if ((curr = key[pos = pos + 1 & Char2BooleanOpenCustomHashMap.this.mask]) != '\u0000') continue;
                return false;
            } while (!Char2BooleanOpenCustomHashMap.this.strategy.equals(curr, k2) || Char2BooleanOpenCustomHashMap.this.value[pos] != v2);
            Char2BooleanOpenCustomHashMap.this.removeEntry(pos);
            return true;
        }

        @Override
        public int size() {
            return Char2BooleanOpenCustomHashMap.this.size;
        }

        @Override
        public void clear() {
            Char2BooleanOpenCustomHashMap.this.clear();
        }
    }

    private class FastEntryIterator
    extends MapIterator
    implements ObjectIterator<Char2BooleanMap.Entry> {
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
    implements ObjectIterator<Char2BooleanMap.Entry> {
        private MapEntry entry;

        private EntryIterator() {
        }

        @Override
        public Char2BooleanMap.Entry next() {
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
        CharArrayList wrapped;

        private MapIterator() {
            this.pos = Char2BooleanOpenCustomHashMap.this.n;
            this.last = -1;
            this.c = Char2BooleanOpenCustomHashMap.this.size;
            this.mustReturnNullKey = Char2BooleanOpenCustomHashMap.this.containsNullKey;
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
                this.last = Char2BooleanOpenCustomHashMap.this.n;
                return this.last;
            }
            char[] key = Char2BooleanOpenCustomHashMap.this.key;
            do {
                if (--this.pos >= 0) continue;
                this.last = Integer.MIN_VALUE;
                char k2 = this.wrapped.getChar(-this.pos - 1);
                int p2 = HashCommon.mix(Char2BooleanOpenCustomHashMap.this.strategy.hashCode(k2)) & Char2BooleanOpenCustomHashMap.this.mask;
                while (!Char2BooleanOpenCustomHashMap.this.strategy.equals(k2, key[p2])) {
                    p2 = p2 + 1 & Char2BooleanOpenCustomHashMap.this.mask;
                }
                return p2;
            } while (key[this.pos] == '\u0000');
            this.last = this.pos;
            return this.last;
        }

        private final void shiftKeys(int pos) {
            char[] key = Char2BooleanOpenCustomHashMap.this.key;
            while (true) {
                char curr;
                int last = pos;
                pos = last + 1 & Char2BooleanOpenCustomHashMap.this.mask;
                while (true) {
                    if ((curr = key[pos]) == '\u0000') {
                        key[last] = '\u0000';
                        return;
                    }
                    int slot = HashCommon.mix(Char2BooleanOpenCustomHashMap.this.strategy.hashCode(curr)) & Char2BooleanOpenCustomHashMap.this.mask;
                    if (last <= pos ? last >= slot || slot > pos : last >= slot && slot > pos) break;
                    pos = pos + 1 & Char2BooleanOpenCustomHashMap.this.mask;
                }
                if (pos < last) {
                    if (this.wrapped == null) {
                        this.wrapped = new CharArrayList(2);
                    }
                    this.wrapped.add(key[pos]);
                }
                key[last] = curr;
                Char2BooleanOpenCustomHashMap.this.value[last] = Char2BooleanOpenCustomHashMap.this.value[pos];
            }
        }

        public void remove() {
            if (this.last == -1) {
                throw new IllegalStateException();
            }
            if (this.last == Char2BooleanOpenCustomHashMap.this.n) {
                Char2BooleanOpenCustomHashMap.this.containsNullKey = false;
            } else if (this.pos >= 0) {
                this.shiftKeys(this.last);
            } else {
                Char2BooleanOpenCustomHashMap.this.remove(this.wrapped.getChar(-this.pos - 1));
                this.last = -1;
                return;
            }
            --Char2BooleanOpenCustomHashMap.this.size;
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
    implements Char2BooleanMap.Entry,
    Map.Entry<Character, Boolean> {
        int index;

        MapEntry(int index) {
            this.index = index;
        }

        MapEntry() {
        }

        @Override
        @Deprecated
        public Character getKey() {
            return Character.valueOf(Char2BooleanOpenCustomHashMap.this.key[this.index]);
        }

        @Override
        public char getCharKey() {
            return Char2BooleanOpenCustomHashMap.this.key[this.index];
        }

        @Override
        @Deprecated
        public Boolean getValue() {
            return Char2BooleanOpenCustomHashMap.this.value[this.index];
        }

        @Override
        public boolean getBooleanValue() {
            return Char2BooleanOpenCustomHashMap.this.value[this.index];
        }

        @Override
        public boolean setValue(boolean v2) {
            boolean oldValue = Char2BooleanOpenCustomHashMap.this.value[this.index];
            Char2BooleanOpenCustomHashMap.this.value[this.index] = v2;
            return oldValue;
        }

        @Override
        public Boolean setValue(Boolean v2) {
            return this.setValue((boolean)v2);
        }

        @Override
        public boolean equals(Object o2) {
            if (!(o2 instanceof Map.Entry)) {
                return false;
            }
            Map.Entry e2 = (Map.Entry)o2;
            return Char2BooleanOpenCustomHashMap.this.strategy.equals(Char2BooleanOpenCustomHashMap.this.key[this.index], ((Character)e2.getKey()).charValue()) && Char2BooleanOpenCustomHashMap.this.value[this.index] == (Boolean)e2.getValue();
        }

        @Override
        public int hashCode() {
            return Char2BooleanOpenCustomHashMap.this.strategy.hashCode(Char2BooleanOpenCustomHashMap.this.key[this.index]) ^ (Char2BooleanOpenCustomHashMap.this.value[this.index] ? 1231 : 1237);
        }

        public String toString() {
            return Char2BooleanOpenCustomHashMap.this.key[this.index] + "=>" + Char2BooleanOpenCustomHashMap.this.value[this.index];
        }
    }
}

