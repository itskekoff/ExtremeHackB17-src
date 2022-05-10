package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.bytes.Byte2IntFunctions;
import it.unimi.dsi.fastutil.bytes.Byte2IntMap;
import it.unimi.dsi.fastutil.bytes.ByteSet;
import it.unimi.dsi.fastutil.bytes.ByteSets;
import it.unimi.dsi.fastutil.ints.IntCollection;
import it.unimi.dsi.fastutil.ints.IntCollections;
import it.unimi.dsi.fastutil.ints.IntSets;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectSets;
import java.io.Serializable;
import java.util.Map;

public class Byte2IntMaps {
    public static final EmptyMap EMPTY_MAP = new EmptyMap();

    private Byte2IntMaps() {
    }

    public static Byte2IntMap singleton(byte key, int value) {
        return new Singleton(key, value);
    }

    public static Byte2IntMap singleton(Byte key, Integer value) {
        return new Singleton(key, value);
    }

    public static Byte2IntMap synchronize(Byte2IntMap m2) {
        return new SynchronizedMap(m2);
    }

    public static Byte2IntMap synchronize(Byte2IntMap m2, Object sync) {
        return new SynchronizedMap(m2, sync);
    }

    public static Byte2IntMap unmodifiable(Byte2IntMap m2) {
        return new UnmodifiableMap(m2);
    }

    public static class UnmodifiableMap
    extends Byte2IntFunctions.UnmodifiableFunction
    implements Byte2IntMap,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final Byte2IntMap map;
        protected transient ObjectSet<Byte2IntMap.Entry> entries;
        protected transient ByteSet keys;
        protected transient IntCollection values;

        protected UnmodifiableMap(Byte2IntMap m2) {
            super(m2);
            this.map = m2;
        }

        @Override
        public int size() {
            return this.map.size();
        }

        @Override
        public boolean containsKey(byte k2) {
            return this.map.containsKey(k2);
        }

        @Override
        public boolean containsValue(int v2) {
            return this.map.containsValue(v2);
        }

        @Override
        public int defaultReturnValue() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void defaultReturnValue(int defRetValue) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int put(byte k2, int v2) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void putAll(Map<? extends Byte, ? extends Integer> m2) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ObjectSet<Byte2IntMap.Entry> byte2IntEntrySet() {
            if (this.entries == null) {
                this.entries = ObjectSets.unmodifiable(this.map.byte2IntEntrySet());
            }
            return this.entries;
        }

        @Override
        public ByteSet keySet() {
            if (this.keys == null) {
                this.keys = ByteSets.unmodifiable(this.map.keySet());
            }
            return this.keys;
        }

        @Override
        public IntCollection values() {
            if (this.values == null) {
                return IntCollections.unmodifiable(this.map.values());
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
        public Integer put(Byte k2, Integer v2) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public int remove(byte k2) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public int get(byte k2) {
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
        public ObjectSet<Map.Entry<Byte, Integer>> entrySet() {
            return ObjectSets.unmodifiable(this.map.entrySet());
        }
    }

    public static class SynchronizedMap
    extends Byte2IntFunctions.SynchronizedFunction
    implements Byte2IntMap,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final Byte2IntMap map;
        protected transient ObjectSet<Byte2IntMap.Entry> entries;
        protected transient ByteSet keys;
        protected transient IntCollection values;

        protected SynchronizedMap(Byte2IntMap m2, Object sync) {
            super(m2, sync);
            this.map = m2;
        }

        protected SynchronizedMap(Byte2IntMap m2) {
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
        public boolean containsKey(byte k2) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.containsKey(k2);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean containsValue(int v2) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.containsValue(v2);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public int defaultReturnValue() {
            Object object = this.sync;
            synchronized (object) {
                return this.map.defaultReturnValue();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void defaultReturnValue(int defRetValue) {
            Object object = this.sync;
            synchronized (object) {
                this.map.defaultReturnValue(defRetValue);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public int put(byte k2, int v2) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.put(k2, v2);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void putAll(Map<? extends Byte, ? extends Integer> m2) {
            Object object = this.sync;
            synchronized (object) {
                this.map.putAll(m2);
            }
        }

        @Override
        public ObjectSet<Byte2IntMap.Entry> byte2IntEntrySet() {
            if (this.entries == null) {
                this.entries = ObjectSets.synchronize(this.map.byte2IntEntrySet(), this.sync);
            }
            return this.entries;
        }

        @Override
        public ByteSet keySet() {
            if (this.keys == null) {
                this.keys = ByteSets.synchronize(this.map.keySet(), this.sync);
            }
            return this.keys;
        }

        @Override
        public IntCollection values() {
            if (this.values == null) {
                return IntCollections.synchronize(this.map.values(), this.sync);
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
        public Integer put(Byte k2, Integer v2) {
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
        public int remove(byte k2) {
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
        public int get(byte k2) {
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
        public ObjectSet<Map.Entry<Byte, Integer>> entrySet() {
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
    extends Byte2IntFunctions.Singleton
    implements Byte2IntMap,
    Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected transient ObjectSet<Byte2IntMap.Entry> entries;
        protected transient ByteSet keys;
        protected transient IntCollection values;

        protected Singleton(byte key, int value) {
            super(key, value);
        }

        @Override
        public boolean containsValue(int v2) {
            return this.value == v2;
        }

        @Override
        public boolean containsValue(Object ov) {
            return (Integer)ov == this.value;
        }

        @Override
        public void putAll(Map<? extends Byte, ? extends Integer> m2) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ObjectSet<Byte2IntMap.Entry> byte2IntEntrySet() {
            if (this.entries == null) {
                this.entries = ObjectSets.singleton(new SingletonEntry());
            }
            return this.entries;
        }

        @Override
        public ByteSet keySet() {
            if (this.keys == null) {
                this.keys = ByteSets.singleton(this.key);
            }
            return this.keys;
        }

        @Override
        public IntCollection values() {
            if (this.values == null) {
                this.values = IntSets.singleton(this.value);
            }
            return this.values;
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public ObjectSet<Map.Entry<Byte, Integer>> entrySet() {
            return this.byte2IntEntrySet();
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
        implements Byte2IntMap.Entry,
        Map.Entry<Byte, Integer> {
            protected SingletonEntry() {
            }

            @Override
            @Deprecated
            public Byte getKey() {
                return Singleton.this.key;
            }

            @Override
            @Deprecated
            public Integer getValue() {
                return Singleton.this.value;
            }

            @Override
            public byte getByteKey() {
                return Singleton.this.key;
            }

            @Override
            public int getIntValue() {
                return Singleton.this.value;
            }

            @Override
            public int setValue(int value) {
                throw new UnsupportedOperationException();
            }

            @Override
            public Integer setValue(Integer value) {
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
                if (e2.getValue() == null || !(e2.getValue() instanceof Integer)) {
                    return false;
                }
                return Singleton.this.key == (Byte)e2.getKey() && Singleton.this.value == (Integer)e2.getValue();
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
    extends Byte2IntFunctions.EmptyFunction
    implements Byte2IntMap,
    Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;

        protected EmptyMap() {
        }

        @Override
        public boolean containsValue(int v2) {
            return false;
        }

        @Override
        public void putAll(Map<? extends Byte, ? extends Integer> m2) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ObjectSet<Byte2IntMap.Entry> byte2IntEntrySet() {
            return ObjectSets.EMPTY_SET;
        }

        @Override
        public ByteSet keySet() {
            return ByteSets.EMPTY_SET;
        }

        @Override
        public IntCollection values() {
            return IntSets.EMPTY_SET;
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
        public ObjectSet<Map.Entry<Byte, Integer>> entrySet() {
            return this.byte2IntEntrySet();
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

