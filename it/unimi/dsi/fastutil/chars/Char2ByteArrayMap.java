package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.bytes.ByteArraySet;
import it.unimi.dsi.fastutil.bytes.ByteArrays;
import it.unimi.dsi.fastutil.bytes.ByteCollection;
import it.unimi.dsi.fastutil.bytes.ByteCollections;
import it.unimi.dsi.fastutil.chars.AbstractChar2ByteMap;
import it.unimi.dsi.fastutil.chars.Char2ByteMap;
import it.unimi.dsi.fastutil.chars.CharArraySet;
import it.unimi.dsi.fastutil.chars.CharArrays;
import it.unimi.dsi.fastutil.chars.CharSet;
import it.unimi.dsi.fastutil.objects.AbstractObjectIterator;
import it.unimi.dsi.fastutil.objects.AbstractObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;
import java.util.NoSuchElementException;

public class Char2ByteArrayMap
extends AbstractChar2ByteMap
implements Serializable,
Cloneable {
    private static final long serialVersionUID = 1L;
    private transient char[] key;
    private transient byte[] value;
    private int size;

    public Char2ByteArrayMap(char[] key, byte[] value) {
        this.key = key;
        this.value = value;
        this.size = key.length;
        if (key.length != value.length) {
            throw new IllegalArgumentException("Keys and values have different lengths (" + key.length + ", " + value.length + ")");
        }
    }

    public Char2ByteArrayMap() {
        this.key = CharArrays.EMPTY_ARRAY;
        this.value = ByteArrays.EMPTY_ARRAY;
    }

    public Char2ByteArrayMap(int capacity) {
        this.key = new char[capacity];
        this.value = new byte[capacity];
    }

    public Char2ByteArrayMap(Char2ByteMap m2) {
        this(m2.size());
        this.putAll(m2);
    }

    public Char2ByteArrayMap(Map<? extends Character, ? extends Byte> m2) {
        this(m2.size());
        this.putAll(m2);
    }

    public Char2ByteArrayMap(char[] key, byte[] value, int size) {
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

    public Char2ByteMap.FastEntrySet char2ByteEntrySet() {
        return new EntrySet();
    }

    private int findKey(char k2) {
        char[] key = this.key;
        int i2 = this.size;
        while (i2-- != 0) {
            if (key[i2] != k2) continue;
            return i2;
        }
        return -1;
    }

    @Override
    public byte get(char k2) {
        char[] key = this.key;
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
    public boolean containsKey(char k2) {
        return this.findKey(k2) != -1;
    }

    @Override
    public boolean containsValue(byte v2) {
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
    public byte put(char k2, byte v2) {
        int oldKey = this.findKey(k2);
        if (oldKey != -1) {
            byte oldValue = this.value[oldKey];
            this.value[oldKey] = v2;
            return oldValue;
        }
        if (this.size == this.key.length) {
            char[] newKey = new char[this.size == 0 ? 2 : this.size * 2];
            byte[] newValue = new byte[this.size == 0 ? 2 : this.size * 2];
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
    public byte remove(char k2) {
        int oldPos = this.findKey(k2);
        if (oldPos == -1) {
            return this.defRetValue;
        }
        byte oldValue = this.value[oldPos];
        int tail = this.size - oldPos - 1;
        System.arraycopy(this.key, oldPos + 1, this.key, oldPos, tail);
        System.arraycopy(this.value, oldPos + 1, this.value, oldPos, tail);
        --this.size;
        return oldValue;
    }

    @Override
    public CharSet keySet() {
        return new CharArraySet(this.key, this.size);
    }

    @Override
    public ByteCollection values() {
        return ByteCollections.unmodifiable(new ByteArraySet(this.value, this.size));
    }

    public Char2ByteArrayMap clone() {
        Char2ByteArrayMap c2;
        try {
            c2 = (Char2ByteArrayMap)super.clone();
        }
        catch (CloneNotSupportedException cantHappen) {
            throw new InternalError();
        }
        c2.key = (char[])this.key.clone();
        c2.value = (byte[])this.value.clone();
        return c2;
    }

    private void writeObject(ObjectOutputStream s2) throws IOException {
        s2.defaultWriteObject();
        for (int i2 = 0; i2 < this.size; ++i2) {
            s2.writeChar(this.key[i2]);
            s2.writeByte(this.value[i2]);
        }
    }

    private void readObject(ObjectInputStream s2) throws IOException, ClassNotFoundException {
        s2.defaultReadObject();
        this.key = new char[this.size];
        this.value = new byte[this.size];
        for (int i2 = 0; i2 < this.size; ++i2) {
            this.key[i2] = s2.readChar();
            this.value[i2] = s2.readByte();
        }
    }

    private final class EntrySet
    extends AbstractObjectSet<Char2ByteMap.Entry>
    implements Char2ByteMap.FastEntrySet {
        private EntrySet() {
        }

        @Override
        public ObjectIterator<Char2ByteMap.Entry> iterator() {
            return new AbstractObjectIterator<Char2ByteMap.Entry>(){
                int curr = -1;
                int next = 0;

                @Override
                public boolean hasNext() {
                    return this.next < Char2ByteArrayMap.this.size;
                }

                @Override
                public Char2ByteMap.Entry next() {
                    if (!this.hasNext()) {
                        throw new NoSuchElementException();
                    }
                    this.curr = this.next;
                    return new AbstractChar2ByteMap.BasicEntry(Char2ByteArrayMap.this.key[this.curr], Char2ByteArrayMap.this.value[this.next++]);
                }

                @Override
                public void remove() {
                    if (this.curr == -1) {
                        throw new IllegalStateException();
                    }
                    this.curr = -1;
                    int tail = Char2ByteArrayMap.this.size-- - this.next--;
                    System.arraycopy(Char2ByteArrayMap.this.key, this.next + 1, Char2ByteArrayMap.this.key, this.next, tail);
                    System.arraycopy(Char2ByteArrayMap.this.value, this.next + 1, Char2ByteArrayMap.this.value, this.next, tail);
                }
            };
        }

        @Override
        public ObjectIterator<Char2ByteMap.Entry> fastIterator() {
            return new AbstractObjectIterator<Char2ByteMap.Entry>(){
                int next = 0;
                int curr = -1;
                final AbstractChar2ByteMap.BasicEntry entry = new AbstractChar2ByteMap.BasicEntry('\u0000', 0);

                @Override
                public boolean hasNext() {
                    return this.next < Char2ByteArrayMap.this.size;
                }

                @Override
                public Char2ByteMap.Entry next() {
                    if (!this.hasNext()) {
                        throw new NoSuchElementException();
                    }
                    this.curr = this.next;
                    this.entry.key = Char2ByteArrayMap.this.key[this.curr];
                    this.entry.value = Char2ByteArrayMap.this.value[this.next++];
                    return this.entry;
                }

                @Override
                public void remove() {
                    if (this.curr == -1) {
                        throw new IllegalStateException();
                    }
                    this.curr = -1;
                    int tail = Char2ByteArrayMap.this.size-- - this.next--;
                    System.arraycopy(Char2ByteArrayMap.this.key, this.next + 1, Char2ByteArrayMap.this.key, this.next, tail);
                    System.arraycopy(Char2ByteArrayMap.this.value, this.next + 1, Char2ByteArrayMap.this.value, this.next, tail);
                }
            };
        }

        @Override
        public int size() {
            return Char2ByteArrayMap.this.size;
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
            return Char2ByteArrayMap.this.containsKey(k2) && Char2ByteArrayMap.this.get(k2) == ((Byte)e2.getValue()).byteValue();
        }

        @Override
        public boolean remove(Object o2) {
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
            int oldPos = Char2ByteArrayMap.this.findKey(k2);
            if (oldPos == -1 || v2 != Char2ByteArrayMap.this.value[oldPos]) {
                return false;
            }
            int tail = Char2ByteArrayMap.this.size - oldPos - 1;
            System.arraycopy(Char2ByteArrayMap.this.key, oldPos + 1, Char2ByteArrayMap.this.key, oldPos, tail);
            System.arraycopy(Char2ByteArrayMap.this.value, oldPos + 1, Char2ByteArrayMap.this.value, oldPos, tail);
            Char2ByteArrayMap.this.size--;
            return true;
        }
    }
}

