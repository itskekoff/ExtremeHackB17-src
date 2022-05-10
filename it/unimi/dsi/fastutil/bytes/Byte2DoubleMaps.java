package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.bytes.Byte2DoubleFunctions;
import it.unimi.dsi.fastutil.bytes.Byte2DoubleMap;
import it.unimi.dsi.fastutil.bytes.ByteSet;
import it.unimi.dsi.fastutil.bytes.ByteSets;
import it.unimi.dsi.fastutil.doubles.DoubleCollection;
import it.unimi.dsi.fastutil.doubles.DoubleCollections;
import it.unimi.dsi.fastutil.doubles.DoubleSets;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectSets;
import java.io.Serializable;
import java.util.Map;

public class Byte2DoubleMaps {
    public static final EmptyMap EMPTY_MAP = new EmptyMap();

    private Byte2DoubleMaps() {
    }

    public static Byte2DoubleMap singleton(byte key, double value) {
        return new Singleton(key, value);
    }

    public static Byte2DoubleMap singleton(Byte key, Double value) {
        return new Singleton(key, value);
    }

    public static Byte2DoubleMap synchronize(Byte2DoubleMap m2) {
        return new SynchronizedMap(m2);
    }

    public static Byte2DoubleMap synchronize(Byte2DoubleMap m2, Object sync) {
        return new SynchronizedMap(m2, sync);
    }

    public static Byte2DoubleMap unmodifiable(Byte2DoubleMap m2) {
        return new UnmodifiableMap(m2);
    }

    public static class UnmodifiableMap
    extends Byte2DoubleFunctions.UnmodifiableFunction
    implements Byte2DoubleMap,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final Byte2DoubleMap map;
        protected transient ObjectSet<Byte2DoubleMap.Entry> entries;
        protected transient ByteSet keys;
        protected transient DoubleCollection values;

        protected UnmodifiableMap(Byte2DoubleMap m2) {
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
        public boolean containsValue(double v2) {
            return this.map.containsValue(v2);
        }

        @Override
        public double defaultReturnValue() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void defaultReturnValue(double defRetValue) {
            throw new UnsupportedOperationException();
        }

        @Override
        public double put(byte k2, double v2) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void putAll(Map<? extends Byte, ? extends Double> m2) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ObjectSet<Byte2DoubleMap.Entry> byte2DoubleEntrySet() {
            if (this.entries == null) {
                this.entries = ObjectSets.unmodifiable(this.map.byte2DoubleEntrySet());
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
        public DoubleCollection values() {
            if (this.values == null) {
                return DoubleCollections.unmodifiable(this.map.values());
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
        public Double put(Byte k2, Double v2) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public double remove(byte k2) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public double get(byte k2) {
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
        public ObjectSet<Map.Entry<Byte, Double>> entrySet() {
            return ObjectSets.unmodifiable(this.map.entrySet());
        }
    }

    public static class SynchronizedMap
    extends Byte2DoubleFunctions.SynchronizedFunction
    implements Byte2DoubleMap,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final Byte2DoubleMap map;
        protected transient ObjectSet<Byte2DoubleMap.Entry> entries;
        protected transient ByteSet keys;
        protected transient DoubleCollection values;

        protected SynchronizedMap(Byte2DoubleMap m2, Object sync) {
            super(m2, sync);
            this.map = m2;
        }

        protected SynchronizedMap(Byte2DoubleMap m2) {
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
        public boolean containsValue(double v2) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.containsValue(v2);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public double defaultReturnValue() {
            Object object = this.sync;
            synchronized (object) {
                return this.map.defaultReturnValue();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void defaultReturnValue(double defRetValue) {
            Object object = this.sync;
            synchronized (object) {
                this.map.defaultReturnValue(defRetValue);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public double put(byte k2, double v2) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.put(k2, v2);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void putAll(Map<? extends Byte, ? extends Double> m2) {
            Object object = this.sync;
            synchronized (object) {
                this.map.putAll(m2);
            }
        }

        @Override
        public ObjectSet<Byte2DoubleMap.Entry> byte2DoubleEntrySet() {
            if (this.entries == null) {
                this.entries = ObjectSets.synchronize(this.map.byte2DoubleEntrySet(), this.sync);
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
        public DoubleCollection values() {
            if (this.values == null) {
                return DoubleCollections.synchronize(this.map.values(), this.sync);
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
        public Double put(Byte k2, Double v2) {
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
        public double remove(byte k2) {
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
        public double get(byte k2) {
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
        public ObjectSet<Map.Entry<Byte, Double>> entrySet() {
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
    extends Byte2DoubleFunctions.Singleton
    implements Byte2DoubleMap,
    Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected transient ObjectSet<Byte2DoubleMap.Entry> entries;
        protected transient ByteSet keys;
        protected transient DoubleCollection values;

        protected Singleton(byte key, double value) {
            super(key, value);
        }

        @Override
        public boolean containsValue(double v2) {
            return this.value == v2;
        }

        @Override
        public boolean containsValue(Object ov) {
            return (Double)ov == this.value;
        }

        @Override
        public void putAll(Map<? extends Byte, ? extends Double> m2) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ObjectSet<Byte2DoubleMap.Entry> byte2DoubleEntrySet() {
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
        public DoubleCollection values() {
            if (this.values == null) {
                this.values = DoubleSets.singleton(this.value);
            }
            return this.values;
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public ObjectSet<Map.Entry<Byte, Double>> entrySet() {
            return this.byte2DoubleEntrySet();
        }

        @Override
        public int hashCode() {
            return this.key ^ HashCommon.double2int(this.value);
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
        implements Byte2DoubleMap.Entry,
        Map.Entry<Byte, Double> {
            protected SingletonEntry() {
            }

            @Override
            @Deprecated
            public Byte getKey() {
                return Singleton.this.key;
            }

            @Override
            @Deprecated
            public Double getValue() {
                return Singleton.this.value;
            }

            @Override
            public byte getByteKey() {
                return Singleton.this.key;
            }

            @Override
            public double getDoubleValue() {
                return Singleton.this.value;
            }

            @Override
            public double setValue(double value) {
                throw new UnsupportedOperationException();
            }

            @Override
            public Double setValue(Double value) {
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
                if (e2.getValue() == null || !(e2.getValue() instanceof Double)) {
                    return false;
                }
                return Singleton.this.key == (Byte)e2.getKey() && Singleton.this.value == (Double)e2.getValue();
            }

            @Override
            public int hashCode() {
                return Singleton.this.key ^ HashCommon.double2int(Singleton.this.value);
            }

            public String toString() {
                return Singleton.this.key + "->" + Singleton.this.value;
            }
        }
    }

    public static class EmptyMap
    extends Byte2DoubleFunctions.EmptyFunction
    implements Byte2DoubleMap,
    Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;

        protected EmptyMap() {
        }

        @Override
        public boolean containsValue(double v2) {
            return false;
        }

        @Override
        public void putAll(Map<? extends Byte, ? extends Double> m2) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ObjectSet<Byte2DoubleMap.Entry> byte2DoubleEntrySet() {
            return ObjectSets.EMPTY_SET;
        }

        @Override
        public ByteSet keySet() {
            return ByteSets.EMPTY_SET;
        }

        @Override
        public DoubleCollection values() {
            return DoubleSets.EMPTY_SET;
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
        public ObjectSet<Map.Entry<Byte, Double>> entrySet() {
            return this.byte2DoubleEntrySet();
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

