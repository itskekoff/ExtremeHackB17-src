package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.bytes.AbstractByte2ObjectMap;
import it.unimi.dsi.fastutil.bytes.Byte2ObjectMap;
import it.unimi.dsi.fastutil.bytes.ByteArraySet;
import it.unimi.dsi.fastutil.bytes.ByteArrays;
import it.unimi.dsi.fastutil.bytes.ByteSet;
import it.unimi.dsi.fastutil.objects.AbstractObjectIterator;
import it.unimi.dsi.fastutil.objects.AbstractObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import it.unimi.dsi.fastutil.objects.ObjectArrays;
import it.unimi.dsi.fastutil.objects.ObjectCollection;
import it.unimi.dsi.fastutil.objects.ObjectCollections;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;
import java.util.NoSuchElementException;

public class Byte2ObjectArrayMap<V>
extends AbstractByte2ObjectMap<V>
implements Serializable,
Cloneable {
    private static final long serialVersionUID = 1L;
    private transient byte[] key;
    private transient Object[] value;
    private int size;

    public Byte2ObjectArrayMap(byte[] key, Object[] value) {
        this.key = key;
        this.value = value;
        this.size = key.length;
        if (key.length != value.length) {
            throw new IllegalArgumentException("Keys and values have different lengths (" + key.length + ", " + value.length + ")");
        }
    }

    public Byte2ObjectArrayMap() {
        this.key = ByteArrays.EMPTY_ARRAY;
        this.value = ObjectArrays.EMPTY_ARRAY;
    }

    public Byte2ObjectArrayMap(int capacity) {
        this.key = new byte[capacity];
        this.value = new Object[capacity];
    }

    public Byte2ObjectArrayMap(Byte2ObjectMap<V> m2) {
        this(m2.size());
        this.putAll(m2);
    }

    public Byte2ObjectArrayMap(Map<? extends Byte, ? extends V> m2) {
        this(m2.size());
        this.putAll(m2);
    }

    public Byte2ObjectArrayMap(byte[] key, Object[] value, int size) {
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

    public Byte2ObjectMap.FastEntrySet<V> byte2ObjectEntrySet() {
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
    public V get(byte k2) {
        byte[] key = this.key;
        int i2 = this.size;
        while (i2-- != 0) {
            if (key[i2] != k2) continue;
            return (V)this.value[i2];
        }
        return (V)this.defRetValue;
    }

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public void clear() {
        int i2 = this.size;
        while (i2-- != 0) {
            this.value[i2] = null;
        }
        this.size = 0;
    }

    @Override
    public boolean containsKey(byte k2) {
        return this.findKey(k2) != -1;
    }

    @Override
    public boolean containsValue(Object v2) {
        int i2 = this.size;
        while (i2-- != 0) {
            if (!(this.value[i2] == null ? v2 == null : this.value[i2].equals(v2))) continue;
            return true;
        }
        return false;
    }

    @Override
    public boolean isEmpty() {
        return this.size == 0;
    }

    @Override
    public V put(byte k2, V v2) {
        int oldKey = this.findKey(k2);
        if (oldKey != -1) {
            Object oldValue = this.value[oldKey];
            this.value[oldKey] = v2;
            return (V)oldValue;
        }
        if (this.size == this.key.length) {
            byte[] newKey = new byte[this.size == 0 ? 2 : this.size * 2];
            Object[] newValue = new Object[this.size == 0 ? 2 : this.size * 2];
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
        return (V)this.defRetValue;
    }

    @Override
    public V remove(byte k2) {
        int oldPos = this.findKey(k2);
        if (oldPos == -1) {
            return (V)this.defRetValue;
        }
        Object oldValue = this.value[oldPos];
        int tail = this.size - oldPos - 1;
        System.arraycopy(this.key, oldPos + 1, this.key, oldPos, tail);
        System.arraycopy(this.value, oldPos + 1, this.value, oldPos, tail);
        --this.size;
        this.value[this.size] = null;
        return (V)oldValue;
    }

    @Override
    public ByteSet keySet() {
        return new ByteArraySet(this.key, this.size);
    }

    @Override
    public ObjectCollection<V> values() {
        return ObjectCollections.unmodifiable(new ObjectArraySet(this.value, this.size));
    }

    public Byte2ObjectArrayMap<V> clone() {
        Byte2ObjectArrayMap c2;
        try {
            c2 = (Byte2ObjectArrayMap)super.clone();
        }
        catch (CloneNotSupportedException cantHappen) {
            throw new InternalError();
        }
        c2.key = (byte[])this.key.clone();
        c2.value = (Object[])this.value.clone();
        return c2;
    }

    private void writeObject(ObjectOutputStream s2) throws IOException {
        s2.defaultWriteObject();
        for (int i2 = 0; i2 < this.size; ++i2) {
            s2.writeByte(this.key[i2]);
            s2.writeObject(this.value[i2]);
        }
    }

    private void readObject(ObjectInputStream s2) throws IOException, ClassNotFoundException {
        s2.defaultReadObject();
        this.key = new byte[this.size];
        this.value = new Object[this.size];
        for (int i2 = 0; i2 < this.size; ++i2) {
            this.key[i2] = s2.readByte();
            this.value[i2] = s2.readObject();
        }
    }

    private final class EntrySet
    extends AbstractObjectSet<Byte2ObjectMap.Entry<V>>
    implements Byte2ObjectMap.FastEntrySet<V> {
        private EntrySet() {
        }

        @Override
        public ObjectIterator<Byte2ObjectMap.Entry<V>> iterator() {
            return new AbstractObjectIterator<Byte2ObjectMap.Entry<V>>(){
                int curr = -1;
                int next = 0;

                @Override
                public boolean hasNext() {
                    return this.next < Byte2ObjectArrayMap.this.size;
                }

                @Override
                public Byte2ObjectMap.Entry<V> next() {
                    if (!this.hasNext()) {
                        throw new NoSuchElementException();
                    }
                    this.curr = this.next;
                    return new AbstractByte2ObjectMap.BasicEntry<Object>(Byte2ObjectArrayMap.this.key[this.curr], Byte2ObjectArrayMap.this.value[this.next++]);
                }

                @Override
                public void remove() {
                    if (this.curr == -1) {
                        throw new IllegalStateException();
                    }
                    this.curr = -1;
                    int tail = Byte2ObjectArrayMap.this.size-- - this.next--;
                    System.arraycopy(Byte2ObjectArrayMap.this.key, this.next + 1, Byte2ObjectArrayMap.this.key, this.next, tail);
                    System.arraycopy(Byte2ObjectArrayMap.this.value, this.next + 1, Byte2ObjectArrayMap.this.value, this.next, tail);
                    ((Byte2ObjectArrayMap)Byte2ObjectArrayMap.this).value[((Byte2ObjectArrayMap)Byte2ObjectArrayMap.this).size] = null;
                }
            };
        }

        @Override
        public ObjectIterator<Byte2ObjectMap.Entry<V>> fastIterator() {
            return new AbstractObjectIterator<Byte2ObjectMap.Entry<V>>(){
                int next = 0;
                int curr = -1;
                final AbstractByte2ObjectMap.BasicEntry<V> entry = new AbstractByte2ObjectMap.BasicEntry<Object>(0, null);

                @Override
                public boolean hasNext() {
                    return this.next < Byte2ObjectArrayMap.this.size;
                }

                @Override
                public Byte2ObjectMap.Entry<V> next() {
                    if (!this.hasNext()) {
                        throw new NoSuchElementException();
                    }
                    this.curr = this.next;
                    this.entry.key = Byte2ObjectArrayMap.this.key[this.curr];
                    this.entry.value = Byte2ObjectArrayMap.this.value[this.next++];
                    return this.entry;
                }

                @Override
                public void remove() {
                    if (this.curr == -1) {
                        throw new IllegalStateException();
                    }
                    this.curr = -1;
                    int tail = Byte2ObjectArrayMap.this.size-- - this.next--;
                    System.arraycopy(Byte2ObjectArrayMap.this.key, this.next + 1, Byte2ObjectArrayMap.this.key, this.next, tail);
                    System.arraycopy(Byte2ObjectArrayMap.this.value, this.next + 1, Byte2ObjectArrayMap.this.value, this.next, tail);
                    ((Byte2ObjectArrayMap)Byte2ObjectArrayMap.this).value[((Byte2ObjectArrayMap)Byte2ObjectArrayMap.this).size] = null;
                }
            };
        }

        @Override
        public int size() {
            return Byte2ObjectArrayMap.this.size;
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
            byte k2 = (Byte)e2.getKey();
            return Byte2ObjectArrayMap.this.containsKey(k2) && (Byte2ObjectArrayMap.this.get(k2) == null ? e2.getValue() == null : Byte2ObjectArrayMap.this.get(k2).equals(e2.getValue()));
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
            byte k2 = (Byte)e2.getKey();
            Object v2 = e2.getValue();
            int oldPos = Byte2ObjectArrayMap.this.findKey(k2);
            if (oldPos == -1 || !(v2 != null ? v2.equals(Byte2ObjectArrayMap.this.value[oldPos]) : Byte2ObjectArrayMap.this.value[oldPos] == null)) {
                return false;
            }
            int tail = Byte2ObjectArrayMap.this.size - oldPos - 1;
            System.arraycopy(Byte2ObjectArrayMap.this.key, oldPos + 1, Byte2ObjectArrayMap.this.key, oldPos, tail);
            System.arraycopy(Byte2ObjectArrayMap.this.value, oldPos + 1, Byte2ObjectArrayMap.this.value, oldPos, tail);
            Byte2ObjectArrayMap.this.size--;
            ((Byte2ObjectArrayMap)Byte2ObjectArrayMap.this).value[((Byte2ObjectArrayMap)Byte2ObjectArrayMap.this).size] = null;
            return true;
        }
    }
}

