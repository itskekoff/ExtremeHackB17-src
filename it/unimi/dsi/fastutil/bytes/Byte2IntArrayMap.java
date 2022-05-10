package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.bytes.AbstractByte2IntMap;
import it.unimi.dsi.fastutil.bytes.Byte2IntMap;
import it.unimi.dsi.fastutil.bytes.ByteArraySet;
import it.unimi.dsi.fastutil.bytes.ByteArrays;
import it.unimi.dsi.fastutil.bytes.ByteSet;
import it.unimi.dsi.fastutil.ints.IntArraySet;
import it.unimi.dsi.fastutil.ints.IntArrays;
import it.unimi.dsi.fastutil.ints.IntCollection;
import it.unimi.dsi.fastutil.ints.IntCollections;
import it.unimi.dsi.fastutil.objects.AbstractObjectIterator;
import it.unimi.dsi.fastutil.objects.AbstractObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;
import java.util.NoSuchElementException;

public class Byte2IntArrayMap
extends AbstractByte2IntMap
implements Serializable,
Cloneable {
    private static final long serialVersionUID = 1L;
    private transient byte[] key;
    private transient int[] value;
    private int size;

    public Byte2IntArrayMap(byte[] key, int[] value) {
        this.key = key;
        this.value = value;
        this.size = key.length;
        if (key.length != value.length) {
            throw new IllegalArgumentException("Keys and values have different lengths (" + key.length + ", " + value.length + ")");
        }
    }

    public Byte2IntArrayMap() {
        this.key = ByteArrays.EMPTY_ARRAY;
        this.value = IntArrays.EMPTY_ARRAY;
    }

    public Byte2IntArrayMap(int capacity) {
        this.key = new byte[capacity];
        this.value = new int[capacity];
    }

    public Byte2IntArrayMap(Byte2IntMap m2) {
        this(m2.size());
        this.putAll(m2);
    }

    public Byte2IntArrayMap(Map<? extends Byte, ? extends Integer> m2) {
        this(m2.size());
        this.putAll(m2);
    }

    public Byte2IntArrayMap(byte[] key, int[] value, int size) {
        this.key = key;
        this.value = value;
        this.size = size;
        if (key.length != value.length) {
            throw new IllegalArgumentException("Keys and values have different lengths (" + key.length + ", " + value.length + ")");
        }
        if (size > key.length) {
            throw new IllegalArgumentException("The provided size (" + size + ") is larger than or equal to the backing-arrays size (" + key.length + ")");
        }
    }

    public Byte2IntMap.FastEntrySet byte2IntEntrySet() {
        return new EntrySet();
    }

    private int findKey(byte k2) {
        byte[] key = this.key;
        int i2 = this.size;
        while (i2-- != 0) {
            if (key[i2] != k2) continue;
            return i2;
        }
        return -1;
    }

    @Override
    public int get(byte k2) {
        byte[] key = this.key;
        int i2 = this.size;
        while (i2-- != 0) {
            if (key[i2] != k2) continue;
            return this.value[i2];
        }
        return this.defRetValue;
    }

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public void clear() {
        this.size = 0;
    }

    @Override
    public boolean containsKey(byte k2) {
        return this.findKey(k2) != -1;
    }

    @Override
    public boolean containsValue(int v2) {
        int i2 = this.size;
        while (i2-- != 0) {
            if (this.value[i2] != v2) continue;
            return true;
        }
        return false;
    }

    @Override
    public boolean isEmpty() {
        return this.size == 0;
    }

    @Override
    public int put(byte k2, int v2) {
        int oldKey = this.findKey(k2);
        if (oldKey != -1) {
            int oldValue = this.value[oldKey];
            this.value[oldKey] = v2;
            return oldValue;
        }
        if (this.size == this.key.length) {
            byte[] newKey = new byte[this.size == 0 ? 2 : this.size * 2];
            int[] newValue = new int[this.size == 0 ? 2 : this.size * 2];
            int i2 = this.size;
            while (i2-- != 0) {
                newKey[i2] = this.key[i2];
                newValue[i2] = this.value[i2];
            }
            this.key = newKey;
            this.value = newValue;
        }
        this.key[this.size] = k2;
        this.value[this.size] = v2;
        ++this.size;
        return this.defRetValue;
    }

    @Override
    public int remove(byte k2) {
        int oldPos = this.findKey(k2);
        if (oldPos == -1) {
            return this.defRetValue;
        }
        int oldValue = this.value[oldPos];
        int tail = this.size - oldPos - 1;
        System.arraycopy(this.key, oldPos + 1, this.key, oldPos, tail);
        System.arraycopy(this.value, oldPos + 1, this.value, oldPos, tail);
        --this.size;
        return oldValue;
    }

    @Override
    public ByteSet keySet() {
        return new ByteArraySet(this.key, this.size);
    }

    @Override
    public IntCollection values() {
        return IntCollections.unmodifiable(new IntArraySet(this.value, this.size));
    }

    public Byte2IntArrayMap clone() {
        Byte2IntArrayMap c2;
        try {
            c2 = (Byte2IntArrayMap)super.clone();
        }
        catch (CloneNotSupportedException cantHappen) {
            throw new InternalError();
        }
        c2.key = (byte[])this.key.clone();
        c2.value = (int[])this.value.clone();
        return c2;
    }

    private void writeObject(ObjectOutputStream s2) throws IOException {
        s2.defaultWriteObject();
        for (int i2 = 0; i2 < this.size; ++i2) {
            s2.writeByte(this.key[i2]);
            s2.writeInt(this.value[i2]);
        }
    }

    private void readObject(ObjectInputStream s2) throws IOException, ClassNotFoundException {
        s2.defaultReadObject();
        this.key = new byte[this.size];
        this.value = new int[this.size];
        for (int i2 = 0; i2 < this.size; ++i2) {
            this.key[i2] = s2.readByte();
            this.value[i2] = s2.readInt();
        }
    }

    private final class EntrySet
    extends AbstractObjectSet<Byte2IntMap.Entry>
    implements Byte2IntMap.FastEntrySet {
        private EntrySet() {
        }

        @Override
        public ObjectIterator<Byte2IntMap.Entry> iterator() {
            return new AbstractObjectIterator<Byte2IntMap.Entry>(){
                int curr = -1;
                int next = 0;

                @Override
                public boolean hasNext() {
                    return this.next < Byte2IntArrayMap.this.size;
                }

                @Override
                public Byte2IntMap.Entry next() {
                    if (!this.hasNext()) {
                        throw new NoSuchElementException();
                    }
                    this.curr = this.next;
                    return new AbstractByte2IntMap.BasicEntry(Byte2IntArrayMap.this.key[this.curr], Byte2IntArrayMap.this.value[this.next++]);
                }

                @Override
                public void remove() {
                    if (this.curr == -1) {
                        throw new IllegalStateException();
                    }
                    this.curr = -1;
                    int tail = Byte2IntArrayMap.this.size-- - this.next--;
                    System.arraycopy(Byte2IntArrayMap.this.key, this.next + 1, Byte2IntArrayMap.this.key, this.next, tail);
                    System.arraycopy(Byte2IntArrayMap.this.value, this.next + 1, Byte2IntArrayMap.this.value, this.next, tail);
                }
            };
        }

        @Override
        public ObjectIterator<Byte2IntMap.Entry> fastIterator() {
            return new AbstractObjectIterator<Byte2IntMap.Entry>(){
                int next = 0;
                int curr = -1;
                final AbstractByte2IntMap.BasicEntry entry = new AbstractByte2IntMap.BasicEntry(0, 0);

                @Override
                public boolean hasNext() {
                    return this.next < Byte2IntArrayMap.this.size;
                }

                @Override
                public Byte2IntMap.Entry next() {
                    if (!this.hasNext()) {
                        throw new NoSuchElementException();
                    }
                    this.curr = this.next;
                    this.entry.key = Byte2IntArrayMap.this.key[this.curr];
                    this.entry.value = Byte2IntArrayMap.this.value[this.next++];
                    return this.entry;
                }

                @Override
                public void remove() {
                    if (this.curr == -1) {
                        throw new IllegalStateException();
                    }
                    this.curr = -1;
                    int tail = Byte2IntArrayMap.this.size-- - this.next--;
                    System.arraycopy(Byte2IntArrayMap.this.key, this.next + 1, Byte2IntArrayMap.this.key, this.next, tail);
                    System.arraycopy(Byte2IntArrayMap.this.value, this.next + 1, Byte2IntArrayMap.this.value, this.next, tail);
                }
            };
        }

        @Override
        public int size() {
            return Byte2IntArrayMap.this.size;
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
            return Byte2IntArrayMap.this.containsKey(k2) && Byte2IntArrayMap.this.get(k2) == ((Integer)e2.getValue()).intValue();
        }

        @Override
        public boolean remove(Object o2) {
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
            int oldPos = Byte2IntArrayMap.this.findKey(k2);
            if (oldPos == -1 || v2 != Byte2IntArrayMap.this.value[oldPos]) {
                return false;
            }
            int tail = Byte2IntArrayMap.this.size - oldPos - 1;
            System.arraycopy(Byte2IntArrayMap.this.key, oldPos + 1, Byte2IntArrayMap.this.key, oldPos, tail);
            System.arraycopy(Byte2IntArrayMap.this.value, oldPos + 1, Byte2IntArrayMap.this.value, oldPos, tail);
            Byte2IntArrayMap.this.size--;
            return true;
        }
    }
}

