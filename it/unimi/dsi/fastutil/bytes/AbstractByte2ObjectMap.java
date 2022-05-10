package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.bytes.AbstractByte2ObjectFunction;
import it.unimi.dsi.fastutil.bytes.AbstractByteIterator;
import it.unimi.dsi.fastutil.bytes.AbstractByteSet;
import it.unimi.dsi.fastutil.bytes.Byte2ObjectMap;
import it.unimi.dsi.fastutil.bytes.ByteIterator;
import it.unimi.dsi.fastutil.bytes.ByteSet;
import it.unimi.dsi.fastutil.objects.AbstractObjectCollection;
import it.unimi.dsi.fastutil.objects.AbstractObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectCollection;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;

public abstract class AbstractByte2ObjectMap<V>
extends AbstractByte2ObjectFunction<V>
implements Byte2ObjectMap<V>,
Serializable {
    private static final long serialVersionUID = -4940583368468432370L;

    protected AbstractByte2ObjectMap() {
    }

    @Override
    public boolean containsValue(Object v2) {
        return this.values().contains(v2);
    }

    @Override
    public boolean containsKey(byte k2) {
        return this.keySet().contains(k2);
    }

    @Override
    public void putAll(Map<? extends Byte, ? extends V> m2) {
        int n2 = m2.size();
        Iterator<Map.Entry<Byte, V>> i2 = m2.entrySet().iterator();
        if (m2 instanceof Byte2ObjectMap) {
            while (n2-- != 0) {
                Byte2ObjectMap.Entry e2 = (Byte2ObjectMap.Entry)i2.next();
                this.put(e2.getByteKey(), e2.getValue());
            }
        } else {
            while (n2-- != 0) {
                Map.Entry<Byte, V> e3 = i2.next();
                this.put(e3.getKey(), e3.getValue());
            }
        }
    }

    @Override
    public boolean isEmpty() {
        return this.size() == 0;
    }

    @Override
    public ByteSet keySet() {
        return new AbstractByteSet(){

            @Override
            public boolean contains(byte k2) {
                return AbstractByte2ObjectMap.this.containsKey(k2);
            }

            @Override
            public int size() {
                return AbstractByte2ObjectMap.this.size();
            }

            @Override
            public void clear() {
                AbstractByte2ObjectMap.this.clear();
            }

            @Override
            public ByteIterator iterator() {
                return new AbstractByteIterator(){
                    final ObjectIterator<Map.Entry<Byte, V>> i;
                    {
                        this.i = AbstractByte2ObjectMap.this.entrySet().iterator();
                    }

                    @Override
                    public byte nextByte() {
                        return ((Byte2ObjectMap.Entry)this.i.next()).getByteKey();
                    }

                    @Override
                    public boolean hasNext() {
                        return this.i.hasNext();
                    }

                    @Override
                    public void remove() {
                        this.i.remove();
                    }
                };
            }
        };
    }

    @Override
    public ObjectCollection<V> values() {
        return new AbstractObjectCollection<V>(){

            @Override
            public boolean contains(Object k2) {
                return AbstractByte2ObjectMap.this.containsValue(k2);
            }

            @Override
            public int size() {
                return AbstractByte2ObjectMap.this.size();
            }

            @Override
            public void clear() {
                AbstractByte2ObjectMap.this.clear();
            }

            @Override
            public ObjectIterator<V> iterator() {
                return new AbstractObjectIterator<V>(){
                    final ObjectIterator<Map.Entry<Byte, V>> i;
                    {
                        this.i = AbstractByte2ObjectMap.this.entrySet().iterator();
                    }

                    @Override
                    @Deprecated
                    public V next() {
                        return ((Byte2ObjectMap.Entry)this.i.next()).getValue();
                    }

                    @Override
                    public boolean hasNext() {
                        return this.i.hasNext();
                    }
                };
            }
        };
    }

    @Override
    public ObjectSet<Map.Entry<Byte, V>> entrySet() {
        return this.byte2ObjectEntrySet();
    }

    @Override
    public int hashCode() {
        int h2 = 0;
        int n2 = this.size();
        ObjectIterator i2 = this.entrySet().iterator();
        while (n2-- != 0) {
            h2 += ((Map.Entry)i2.next()).hashCode();
        }
        return h2;
    }

    @Override
    public boolean equals(Object o2) {
        if (o2 == this) {
            return true;
        }
        if (!(o2 instanceof Map)) {
            return false;
        }
        Map m2 = (Map)o2;
        if (m2.size() != this.size()) {
            return false;
        }
        return this.entrySet().containsAll(m2.entrySet());
    }

    public String toString() {
        StringBuilder s2 = new StringBuilder();
        ObjectIterator i2 = this.entrySet().iterator();
        int n2 = this.size();
        boolean first = true;
        s2.append("{");
        while (n2-- != 0) {
            if (first) {
                first = false;
            } else {
                s2.append(", ");
            }
            Byte2ObjectMap.Entry e2 = (Byte2ObjectMap.Entry)i2.next();
            s2.append(String.valueOf(e2.getByteKey()));
            s2.append("=>");
            if (this == e2.getValue()) {
                s2.append("(this map)");
                continue;
            }
            s2.append(String.valueOf(e2.getValue()));
        }
        s2.append("}");
        return s2.toString();
    }

    public static class BasicEntry<V>
    implements Byte2ObjectMap.Entry<V> {
        protected byte key;
        protected V value;

        public BasicEntry(Byte key, V value) {
            this.key = key;
            this.value = value;
        }

        public BasicEntry(byte key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        @Deprecated
        public Byte getKey() {
            return this.key;
        }

        @Override
        public byte getByteKey() {
            return this.key;
        }

        @Override
        public V getValue() {
            return this.value;
        }

        @Override
        public V setValue(V value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean equals(Object o2) {
            if (!(o2 instanceof Map.Entry)) {
                return false;
            }
            Map.Entry e2 = (Map.Entry)o2;
            if (e2.getKey() == null || !(e2.getKey() instanceof Byte)) {
                return false;
            }
            return this.key == (Byte)e2.getKey() && (this.value == null ? e2.getValue() == null : this.value.equals(e2.getValue()));
        }

        @Override
        public int hashCode() {
            return this.key ^ (this.value == null ? 0 : this.value.hashCode());
        }

        public String toString() {
            return this.key + "->" + this.value;
        }
    }
}

