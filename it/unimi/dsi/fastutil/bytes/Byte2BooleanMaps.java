package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.booleans.BooleanCollection;
import it.unimi.dsi.fastutil.booleans.BooleanCollections;
import it.unimi.dsi.fastutil.booleans.BooleanSets;
import it.unimi.dsi.fastutil.bytes.Byte2BooleanFunctions;
import it.unimi.dsi.fastutil.bytes.Byte2BooleanMap;
import it.unimi.dsi.fastutil.bytes.ByteSet;
import it.unimi.dsi.fastutil.bytes.ByteSets;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectSets;
import java.io.Serializable;
import java.util.Map;

public class Byte2BooleanMaps {
    public static final EmptyMap EMPTY_MAP = new EmptyMap();

    private Byte2BooleanMaps() {
    }

    public static Byte2BooleanMap singleton(byte key, boolean value) {
        return new Singleton(key, value);
    }

    public static Byte2BooleanMap singleton(Byte key, Boolean value) {
        return new Singleton(key, value);
    }

    public static Byte2BooleanMap synchronize(Byte2BooleanMap m2) {
        return new SynchronizedMap(m2);
    }

    public static Byte2BooleanMap synchronize(Byte2BooleanMap m2, Object sync) {
        return new SynchronizedMap(m2, sync);
    }

    public static Byte2BooleanMap unmodifiable(Byte2BooleanMap m2) {
        return new UnmodifiableMap(m2);
    }

    public static class UnmodifiableMap
    extends Byte2BooleanFunctions.UnmodifiableFunction
    implements Byte2BooleanMap,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final Byte2BooleanMap map;
        protected transient ObjectSet<Byte2BooleanMap.Entry> entries;
        protected transient ByteSet keys;
        protected transient BooleanCollection values;

        protected UnmodifiableMap(Byte2BooleanMap m2) {
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
        public boolean containsValue(boolean v2) {
            return this.map.containsValue(v2);
        }

        @Override
        public boolean defaultReturnValue() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void defaultReturnValue(boolean defRetValue) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean put(byte k2, boolean v2) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void putAll(Map<? extends Byte, ? extends Boolean> m2) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ObjectSet<Byte2BooleanMap.Entry> byte2BooleanEntrySet() {
            if (this.entries == null) {
                this.entries = ObjectSets.unmodifiable(this.map.byte2BooleanEntrySet());
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
        public BooleanCollection values() {
            if (this.values == null) {
                return BooleanCollections.unmodifiable(this.map.values());
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
        public Boolean put(Byte k2, Boolean v2) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public boolean remove(byte k2) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public boolean get(byte k2) {
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
        public ObjectSet<Map.Entry<Byte, Boolean>> entrySet() {
            return ObjectSets.unmodifiable(this.map.entrySet());
        }
    }

    public static class SynchronizedMap
    extends Byte2BooleanFunctions.SynchronizedFunction
    implements Byte2BooleanMap,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final Byte2BooleanMap map;
        protected transient ObjectSet<Byte2BooleanMap.Entry> entries;
        protected transient ByteSet keys;
        protected transient BooleanCollection values;

        protected SynchronizedMap(Byte2BooleanMap m2, Object sync) {
            super(m2, sync);
            this.map = m2;
        }

        protected SynchronizedMap(Byte2BooleanMap m2) {
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
        public boolean containsValue(boolean v2) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.containsValue(v2);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean defaultReturnValue() {
            Object object = this.sync;
            synchronized (object) {
                return this.map.defaultReturnValue();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void defaultReturnValue(boolean defRetValue) {
            Object object = this.sync;
            synchronized (object) {
                this.map.defaultReturnValue(defRetValue);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean put(byte k2, boolean v2) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.put(k2, v2);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void putAll(Map<? extends Byte, ? extends Boolean> m2) {
            Object object = this.sync;
            synchronized (object) {
                this.map.putAll(m2);
            }
        }

        @Override
        public ObjectSet<Byte2BooleanMap.Entry> byte2BooleanEntrySet() {
            if (this.entries == null) {
                this.entries = ObjectSets.synchronize(this.map.byte2BooleanEntrySet(), this.sync);
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
        public BooleanCollection values() {
            if (this.values == null) {
                return BooleanCollections.synchronize(this.map.values(), this.sync);
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
        public Boolean put(Byte k2, Boolean v2) {
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
        public boolean remove(byte k2) {
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
        public boolean get(byte k2) {
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
        public ObjectSet<Map.Entry<Byte, Boolean>> entrySet() {
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
    extends Byte2BooleanFunctions.Singleton
    implements Byte2BooleanMap,
    Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected transient ObjectSet<Byte2BooleanMap.Entry> entries;
        protected transient ByteSet keys;
        protected transient BooleanCollection values;

        protected Singleton(byte key, boolean value) {
            super(key, value);
        }

        @Override
        public boolean containsValue(boolean v2) {
            return this.value == v2;
        }

        @Override
        public boolean containsValue(Object ov) {
            return (Boolean)ov == this.value;
        }

        @Override
        public void putAll(Map<? extends Byte, ? extends Boolean> m2) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ObjectSet<Byte2BooleanMap.Entry> byte2BooleanEntrySet() {
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
        public BooleanCollection values() {
            if (this.values == null) {
                this.values = BooleanSets.singleton(this.value);
            }
            return this.values;
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public ObjectSet<Map.Entry<Byte, Boolean>> entrySet() {
            return this.byte2BooleanEntrySet();
        }

        @Override
        public int hashCode() {
            return this.key ^ (this.value ? 1231 : 1237);
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
        implements Byte2BooleanMap.Entry,
        Map.Entry<Byte, Boolean> {
            protected SingletonEntry() {
            }

            @Override
            @Deprecated
            public Byte getKey() {
                return Singleton.this.key;
            }

            @Override
            @Deprecated
            public Boolean getValue() {
                return Singleton.this.value;
            }

            @Override
            public byte getByteKey() {
                return Singleton.this.key;
            }

            @Override
            public boolean getBooleanValue() {
                return Singleton.this.value;
            }

            @Override
            public boolean setValue(boolean value) {
                throw new UnsupportedOperationException();
            }

            @Override
            public Boolean setValue(Boolean value) {
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
                if (e2.getValue() == null || !(e2.getValue() instanceof Boolean)) {
                    return false;
                }
                return Singleton.this.key == (Byte)e2.getKey() && Singleton.this.value == (Boolean)e2.getValue();
            }

            @Override
            public int hashCode() {
                return Singleton.this.key ^ (Singleton.this.value ? 1231 : 1237);
            }

            public String toString() {
                return Singleton.this.key + "->" + Singleton.this.value;
            }
        }
    }

    public static class EmptyMap
    extends Byte2BooleanFunctions.EmptyFunction
    implements Byte2BooleanMap,
    Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;

        protected EmptyMap() {
        }

        @Override
        public boolean containsValue(boolean v2) {
            return false;
        }

        @Override
        public void putAll(Map<? extends Byte, ? extends Boolean> m2) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ObjectSet<Byte2BooleanMap.Entry> byte2BooleanEntrySet() {
            return ObjectSets.EMPTY_SET;
        }

        @Override
        public ByteSet keySet() {
            return ByteSets.EMPTY_SET;
        }

        @Override
        public BooleanCollection values() {
            return BooleanSets.EMPTY_SET;
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
        public ObjectSet<Map.Entry<Byte, Boolean>> entrySet() {
            return this.byte2BooleanEntrySet();
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

