package net.minecraft.util;

import javax.annotation.Nullable;

public class IntHashMap<V> {
    private transient Entry<V>[] slots = new Entry[16];
    private transient int count;
    private int threshold = 12;
    private final float growFactor = 0.75f;

    private static int computeHash(int integer) {
        integer = integer ^ integer >>> 20 ^ integer >>> 12;
        return integer ^ integer >>> 7 ^ integer >>> 4;
    }

    private static int getSlotIndex(int hash, int slotCount) {
        return hash & slotCount - 1;
    }

    @Nullable
    public V lookup(int hashEntry) {
        int i2 = IntHashMap.computeHash(hashEntry);
        Entry<V> entry = this.slots[IntHashMap.getSlotIndex(i2, this.slots.length)];
        while (entry != null) {
            if (entry.hashEntry == hashEntry) {
                return entry.valueEntry;
            }
            entry = entry.nextEntry;
        }
        return null;
    }

    public boolean containsItem(int hashEntry) {
        return this.lookupEntry(hashEntry) != null;
    }

    @Nullable
    final Entry<V> lookupEntry(int hashEntry) {
        int i2 = IntHashMap.computeHash(hashEntry);
        Entry<V> entry = this.slots[IntHashMap.getSlotIndex(i2, this.slots.length)];
        while (entry != null) {
            if (entry.hashEntry == hashEntry) {
                return entry;
            }
            entry = entry.nextEntry;
        }
        return null;
    }

    public void addKey(int hashEntry, V valueEntry) {
        int i2 = IntHashMap.computeHash(hashEntry);
        int j2 = IntHashMap.getSlotIndex(i2, this.slots.length);
        Entry<V> entry = this.slots[j2];
        while (entry != null) {
            if (entry.hashEntry == hashEntry) {
                entry.valueEntry = valueEntry;
                return;
            }
            entry = entry.nextEntry;
        }
        this.insert(i2, hashEntry, valueEntry, j2);
    }

    private void grow(int p_76047_1_) {
        Entry<V>[] entry = this.slots;
        int i2 = entry.length;
        if (i2 == 0x40000000) {
            this.threshold = Integer.MAX_VALUE;
        } else {
            Entry[] entry1 = new Entry[p_76047_1_];
            this.copyTo(entry1);
            this.slots = entry1;
            this.threshold = (int)((float)p_76047_1_ * 0.75f);
        }
    }

    private void copyTo(Entry<V>[] p_76048_1_) {
        Entry<V>[] entry = this.slots;
        int i2 = p_76048_1_.length;
        for (int j2 = 0; j2 < entry.length; ++j2) {
            Entry entry2;
            Entry<V> entry1 = entry[j2];
            if (entry1 == null) continue;
            entry[j2] = null;
            do {
                entry2 = entry1.nextEntry;
                int k2 = IntHashMap.getSlotIndex(entry1.slotHash, i2);
                entry1.nextEntry = p_76048_1_[k2];
                p_76048_1_[k2] = entry1;
                entry1 = entry2;
            } while (entry2 != null);
        }
    }

    @Nullable
    public V removeObject(int p_76049_1_) {
        Entry<V> entry = this.removeEntry(p_76049_1_);
        return entry == null ? null : (V)entry.valueEntry;
    }

    @Nullable
    final Entry<V> removeEntry(int p_76036_1_) {
        Entry<V> entry;
        int i2 = IntHashMap.computeHash(p_76036_1_);
        int j2 = IntHashMap.getSlotIndex(i2, this.slots.length);
        Entry<V> entry1 = entry = this.slots[j2];
        while (entry1 != null) {
            Entry entry2 = entry1.nextEntry;
            if (entry1.hashEntry == p_76036_1_) {
                --this.count;
                if (entry == entry1) {
                    this.slots[j2] = entry2;
                } else {
                    entry.nextEntry = entry2;
                }
                return entry1;
            }
            entry = entry1;
            entry1 = entry2;
        }
        return entry1;
    }

    public void clearMap() {
        Entry<V>[] entry = this.slots;
        for (int i2 = 0; i2 < entry.length; ++i2) {
            entry[i2] = null;
        }
        this.count = 0;
    }

    private void insert(int p_76040_1_, int p_76040_2_, V p_76040_3_, int p_76040_4_) {
        Entry<V> entry = this.slots[p_76040_4_];
        this.slots[p_76040_4_] = new Entry<V>(p_76040_1_, p_76040_2_, p_76040_3_, entry);
        if (this.count++ >= this.threshold) {
            this.grow(2 * this.slots.length);
        }
    }

    static class Entry<V> {
        final int hashEntry;
        V valueEntry;
        Entry<V> nextEntry;
        final int slotHash;

        Entry(int p_i1552_1_, int p_i1552_2_, V p_i1552_3_, Entry<V> p_i1552_4_) {
            this.valueEntry = p_i1552_3_;
            this.nextEntry = p_i1552_4_;
            this.hashEntry = p_i1552_2_;
            this.slotHash = p_i1552_1_;
        }

        public final int getHash() {
            return this.hashEntry;
        }

        public final V getValue() {
            return this.valueEntry;
        }

        public final boolean equals(Object p_equals_1_) {
            V object1;
            V object;
            if (!(p_equals_1_ instanceof Entry)) {
                return false;
            }
            Entry entry = (Entry)p_equals_1_;
            return this.hashEntry == entry.hashEntry && ((object = this.getValue()) == (object1 = entry.getValue()) || object != null && object.equals(object1));
        }

        public final int hashCode() {
            return IntHashMap.computeHash(this.hashEntry);
        }

        public final String toString() {
            return String.valueOf(this.getHash()) + "=" + this.getValue();
        }
    }
}

