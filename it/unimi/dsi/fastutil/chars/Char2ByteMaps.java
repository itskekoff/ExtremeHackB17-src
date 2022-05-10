package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.bytes.ByteCollection;
import it.unimi.dsi.fastutil.bytes.ByteCollections;
import it.unimi.dsi.fastutil.bytes.ByteSets;
import it.unimi.dsi.fastutil.chars.Char2ByteFunctions;
import it.unimi.dsi.fastutil.chars.Char2ByteMap;
import it.unimi.dsi.fastutil.chars.CharSet;
import it.unimi.dsi.fastutil.chars.CharSets;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectSets;
import java.io.Serializable;
import java.util.Map;

public class Char2ByteMaps {
    public static final EmptyMap EMPTY_MAP = new EmptyMap();

    private Char2ByteMaps() {
    }

    public static Char2ByteMap singleton(char key, byte value) {
        return new Singleton(key, value);
    }

    public static Char2ByteMap singleton(Character key, Byte value) {
        return new Singleton(key.charValue(), value);
    }

    public static Char2ByteMap synchronize(Char2ByteMap m2) {
        return new SynchronizedMap(m2);
    }

    public static Char2ByteMap synchronize(Char2ByteMap m2, Object sync) {
        return new SynchronizedMap(m2, sync);
    }

    public static Char2ByteMap unmodifiable(Char2ByteMap m2) {
        return new UnmodifiableMap(m2);
    }

    public static class UnmodifiableMap
    extends Char2ByteFunctions.UnmodifiableFunction
    implements Char2ByteMap,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final Char2ByteMap map;
        protected transient ObjectSet<Char2ByteMap.Entry> entries;
        protected transient CharSet keys;
        protected transient ByteCollection values;

        protected UnmodifiableMap(Char2ByteMap m2) {
            super(m2);
            this.map = m2;
        }

        @Override
        public int size() {
            return this.map.size();
        }

        @Override
        public boolean containsKey(char k2) {
            return this.map.containsKey(k2);
        }

        @Override
        public boolean containsValue(byte v2) {
            return this.map.containsValue(v2);
        }

        @Override
        public byte defaultReturnValue() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void defaultReturnValue(byte defRetValue) {
            throw new UnsupportedOperationException();
        }

        @Override
        public byte put(char k2, byte v2) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void putAll(Map<? extends Character, ? extends Byte> m2) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ObjectSet<Char2ByteMap.Entry> char2ByteEntrySet() {
            if (this.entries == null) {
                this.entries = ObjectSets.unmodifiable(this.map.char2ByteEntrySet());
            }
            return this.entries;
        }

        @Override
        public CharSet keySet() {
            if (this.keys == null) {
                this.keys = CharSets.unmodifiable(this.map.keySet());
            }
            return this.keys;
        }

        @Override
        public ByteCollection values() {
            if (this.values == null) {
                return ByteCollections.unmodifiable(this.map.values());
            }
            return this.values;
        }

        @Override
        public void clear() {
            throw new UnsupportedOperationException();
        }

        @Override
        public String toString() {
            return this.map.toString();
        }

        @Override
        @Deprecated
        public Byte put(Character k2, Byte v2) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public byte remove(char k2) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public byte get(char k2) {
            return this.map.get(k2);
        }

        @Override
        public boolean containsKey(Object ok2) {
            return this.map.containsKey(ok2);
        }

        @Override
        public boolean containsValue(Object ov) {
            return this.map.containsValue(ov);
        }

        @Override
        public boolean isEmpty() {
            return this.map.isEmpty();
        }

        @Override
        public ObjectSet<Map.Entry<Character, Byte>> entrySet() {
            return ObjectSets.unmodifiable(this.map.entrySet());
        }
    }

    public static class SynchronizedMap
    extends Char2ByteFunctions.SynchronizedFunction
    implements Char2ByteMap,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final Char2ByteMap map;
        protected transient ObjectSet<Char2ByteMap.Entry> entries;
        protected transient CharSet keys;
        protected transient ByteCollection values;

        protected SynchronizedMap(Char2ByteMap m2, Object sync) {
            super(m2, sync);
            this.map = m2;
        }

        protected SynchronizedMap(Char2ByteMap m2) {
            super(m2);
            this.map = m2;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public int size() {
            Object object = this.sync;
            synchronized (object) {
                return this.map.size();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean containsKey(char k2) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.containsKey(k2);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean containsValue(byte v2) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.containsValue(v2);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public byte defaultReturnValue() {
            Object object = this.sync;
            synchronized (object) {
                return this.map.defaultReturnValue();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void defaultReturnValue(byte defRetValue) {
            Object object = this.sync;
            synchronized (object) {
                this.map.defaultReturnValue(defRetValue);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public byte put(char k2, byte v2) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.put(k2, v2);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void putAll(Map<? extends Character, ? extends Byte> m2) {
            Object object = this.sync;
            synchronized (object) {
                this.map.putAll(m2);
            }
        }

        @Override
        public ObjectSet<Char2ByteMap.Entry> char2ByteEntrySet() {
            if (this.entries == null) {
                this.entries = ObjectSets.synchronize(this.map.char2ByteEntrySet(), this.sync);
            }
            return this.entries;
        }

        @Override
        public CharSet keySet() {
            if (this.keys == null) {
                this.keys = CharSets.synchronize(this.map.keySet(), this.sync);
            }
            return this.keys;
        }

        @Override
        public ByteCollection values() {
            if (this.values == null) {
                return ByteCollections.synchronize(this.map.values(), this.sync);
            }
            return this.values;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void clear() {
            Object object = this.sync;
            synchronized (object) {
                this.map.clear();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public String toString() {
            Object object = this.sync;
            synchronized (object) {
                return this.map.toString();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        @Deprecated
        public Byte put(Character k2, Byte v2) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.put(k2, v2);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        @Deprecated
        public byte remove(char k2) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.remove(k2);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        @Deprecated
        public byte get(char k2) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.get(k2);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean containsKey(Object ok2) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.containsKey(ok2);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        @Deprecated
        public boolean containsValue(Object ov) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.containsValue(ov);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean isEmpty() {
            Object object = this.sync;
            synchronized (object) {
                return this.map.isEmpty();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public ObjectSet<Map.Entry<Character, Byte>> entrySet() {
            Object object = this.sync;
            synchronized (object) {
                return this.map.entrySet();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public int hashCode() {
            Object object = this.sync;
            synchronized (object) {
                return this.map.hashCode();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean equals(Object o2) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.equals(o2);
            }
        }
    }

    public static class Singleton
    extends Char2ByteFunctions.Singleton
    implements Char2ByteMap,
    Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected transient ObjectSet<Char2ByteMap.Entry> entries;
        protected transient CharSet keys;
        protected transient ByteCollection values;

        protected Singleton(char key, byte value) {
            super(key, value);
        }

        @Override
        public boolean containsValue(byte v2) {
            return this.value == v2;
        }

        @Override
        public boolean containsValue(Object ov) {
            return (Byte)ov == this.value;
        }

        @Override
        public void putAll(Map<? extends Character, ? extends Byte> m2) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ObjectSet<Char2ByteMap.Entry> char2ByteEntrySet() {
            if (this.entries == null) {
                this.entries = ObjectSets.singleton(new SingletonEntry());
            }
            return this.entries;
        }

        @Override
        public CharSet keySet() {
            if (this.keys == null) {
                this.keys = CharSets.singleton(this.key);
            }
            return this.keys;
        }

        @Override
        public ByteCollection values() {
            if (this.values == null) {
                this.values = ByteSets.singleton(this.value);
            }
            return this.values;
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public ObjectSet<Map.Entry<Character, Byte>> entrySet() {
            return this.char2ByteEntrySet();
        }

        @Override
        public int hashCode() {
            return this.key ^ this.value;
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
            if (m2.size() != 1) {
                return false;
            }
            return ((Map.Entry)this.entrySet().iterator().next()).equals(m2.entrySet().iterator().next());
        }

        public String toString() {
            return "{" + this.key + "=>" + this.value + "}";
        }

        protected class SingletonEntry
        implements Char2ByteMap.Entry,
        Map.Entry<Character, Byte> {
            protected SingletonEntry() {
            }

            @Override
            @Deprecated
            public Character getKey() {
                return Character.valueOf(Singleton.this.key);
            }

            @Override
            @Deprecated
            public Byte getValue() {
                return Singleton.this.value;
            }

            @Override
            public char getCharKey() {
                return Singleton.this.key;
            }

            @Override
            public byte getByteValue() {
                return Singleton.this.value;
            }

            @Override
            public byte setValue(byte value) {
                throw new UnsupportedOperationException();
            }

            @Override
            public Byte setValue(Byte value) {
                throw new UnsupportedOperationException();
            }

            @Override
            public boolean equals(Object o2) {
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
                return Singleton.this.key == ((Character)e2.getKey()).charValue() && Singleton.this.value == (Byte)e2.getValue();
            }

            @Override
            public int hashCode() {
                return Singleton.this.key ^ Singleton.this.value;
            }

            public String toString() {
                return Singleton.this.key + "->" + Singleton.this.value;
            }
        }
    }

    public static class EmptyMap
    extends Char2ByteFunctions.EmptyFunction
    implements Char2ByteMap,
    Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;

        protected EmptyMap() {
        }

        @Override
        public boolean containsValue(byte v2) {
            return false;
        }

        @Override
        public void putAll(Map<? extends Character, ? extends Byte> m2) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ObjectSet<Char2ByteMap.Entry> char2ByteEntrySet() {
            return ObjectSets.EMPTY_SET;
        }

        @Override
        public CharSet keySet() {
            return CharSets.EMPTY_SET;
        }

        @Override
        public ByteCollection values() {
            return ByteSets.EMPTY_SET;
        }

        @Override
        public boolean containsValue(Object ov) {
            return false;
        }

        private Object readResolve() {
            return EMPTY_MAP;
        }

        @Override
        public Object clone() {
            return EMPTY_MAP;
        }

        @Override
        public boolean isEmpty() {
            return true;
        }

        @Override
        public ObjectSet<Map.Entry<Character, Byte>> entrySet() {
            return this.char2ByteEntrySet();
        }

        @Override
        public int hashCode() {
            return 0;
        }

        @Override
        public boolean equals(Object o2) {
            if (!(o2 instanceof Map)) {
                return false;
            }
            return ((Map)o2).isEmpty();
        }

        public String toString() {
            return "{}";
        }
    }
}

