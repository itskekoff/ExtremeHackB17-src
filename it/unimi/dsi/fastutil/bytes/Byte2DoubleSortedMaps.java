package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.bytes.Byte2DoubleMap;
import it.unimi.dsi.fastutil.bytes.Byte2DoubleMaps;
import it.unimi.dsi.fastutil.bytes.Byte2DoubleSortedMap;
import it.unimi.dsi.fastutil.bytes.ByteComparator;
import it.unimi.dsi.fastutil.bytes.ByteSortedSet;
import it.unimi.dsi.fastutil.bytes.ByteSortedSets;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import it.unimi.dsi.fastutil.objects.ObjectSortedSets;
import java.io.Serializable;
import java.util.Comparator;
import java.util.Map;
import java.util.NoSuchElementException;

public class Byte2DoubleSortedMaps {
    public static final EmptySortedMap EMPTY_MAP = new EmptySortedMap();

    private Byte2DoubleSortedMaps() {
    }

    public static Comparator<? super Map.Entry<Byte, ?>> entryComparator(final ByteComparator comparator) {
        return new Comparator<Map.Entry<Byte, ?>>(){

            @Override
            public int compare(Map.Entry<Byte, ?> x2, Map.Entry<Byte, ?> y2) {
                return comparator.compare(x2.getKey(), y2.getKey());
            }
        };
    }

    public static Byte2DoubleSortedMap singleton(Byte key, Double value) {
        return new Singleton(key, value);
    }

    public static Byte2DoubleSortedMap singleton(Byte key, Double value, ByteComparator comparator) {
        return new Singleton(key, value, comparator);
    }

    public static Byte2DoubleSortedMap singleton(byte key, double value) {
        return new Singleton(key, value);
    }

    public static Byte2DoubleSortedMap singleton(byte key, double value, ByteComparator comparator) {
        return new Singleton(key, value, comparator);
    }

    public static Byte2DoubleSortedMap synchronize(Byte2DoubleSortedMap m2) {
        return new SynchronizedSortedMap(m2);
    }

    public static Byte2DoubleSortedMap synchronize(Byte2DoubleSortedMap m2, Object sync) {
        return new SynchronizedSortedMap(m2, sync);
    }

    public static Byte2DoubleSortedMap unmodifiable(Byte2DoubleSortedMap m2) {
        return new UnmodifiableSortedMap(m2);
    }

    public static class UnmodifiableSortedMap
    extends Byte2DoubleMaps.UnmodifiableMap
    implements Byte2DoubleSortedMap,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final Byte2DoubleSortedMap sortedMap;

        protected UnmodifiableSortedMap(Byte2DoubleSortedMap m2) {
            super(m2);
            this.sortedMap = m2;
        }

        @Override
        public ByteComparator comparator() {
            return this.sortedMap.comparator();
        }

        @Override
        public ObjectSortedSet<Byte2DoubleMap.Entry> byte2DoubleEntrySet() {
            if (this.entries == null) {
                this.entries = ObjectSortedSets.unmodifiable(this.sortedMap.byte2DoubleEntrySet());
            }
            return (ObjectSortedSet)this.entries;
        }

        @Override
        public ObjectSortedSet<Map.Entry<Byte, Double>> entrySet() {
            return this.byte2DoubleEntrySet();
        }

        @Override
        public ByteSortedSet keySet() {
            if (this.keys == null) {
                this.keys = ByteSortedSets.unmodifiable(this.sortedMap.keySet());
            }
            return (ByteSortedSet)this.keys;
        }

        @Override
        public Byte2DoubleSortedMap subMap(byte from, byte to2) {
            return new UnmodifiableSortedMap(this.sortedMap.subMap(from, to2));
        }

        @Override
        public Byte2DoubleSortedMap headMap(byte to2) {
            return new UnmodifiableSortedMap(this.sortedMap.headMap(to2));
        }

        @Override
        public Byte2DoubleSortedMap tailMap(byte from) {
            return new UnmodifiableSortedMap(this.sortedMap.tailMap(from));
        }

        @Override
        public byte firstByteKey() {
            return this.sortedMap.firstByteKey();
        }

        @Override
        public byte lastByteKey() {
            return this.sortedMap.lastByteKey();
        }

        @Override
        public Byte firstKey() {
            return (Byte)this.sortedMap.firstKey();
        }

        @Override
        public Byte lastKey() {
            return (Byte)this.sortedMap.lastKey();
        }

        @Override
        public Byte2DoubleSortedMap subMap(Byte from, Byte to2) {
            return new UnmodifiableSortedMap(this.sortedMap.subMap(from, to2));
        }

        @Override
        public Byte2DoubleSortedMap headMap(Byte to2) {
            return new UnmodifiableSortedMap(this.sortedMap.headMap(to2));
        }

        @Override
        public Byte2DoubleSortedMap tailMap(Byte from) {
            return new UnmodifiableSortedMap(this.sortedMap.tailMap(from));
        }
    }

    public static class SynchronizedSortedMap
    extends Byte2DoubleMaps.SynchronizedMap
    implements Byte2DoubleSortedMap,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final Byte2DoubleSortedMap sortedMap;

        protected SynchronizedSortedMap(Byte2DoubleSortedMap m2, Object sync) {
            super(m2, sync);
            this.sortedMap = m2;
        }

        protected SynchronizedSortedMap(Byte2DoubleSortedMap m2) {
            super(m2);
            this.sortedMap = m2;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public ByteComparator comparator() {
            Object object = this.sync;
            synchronized (object) {
                return this.sortedMap.comparator();
            }
        }

        @Override
        public ObjectSortedSet<Byte2DoubleMap.Entry> byte2DoubleEntrySet() {
            if (this.entries == null) {
                this.entries = ObjectSortedSets.synchronize(this.sortedMap.byte2DoubleEntrySet(), this.sync);
            }
            return (ObjectSortedSet)this.entries;
        }

        @Override
        public ObjectSortedSet<Map.Entry<Byte, Double>> entrySet() {
            return this.byte2DoubleEntrySet();
        }

        @Override
        public ByteSortedSet keySet() {
            if (this.keys == null) {
                this.keys = ByteSortedSets.synchronize(this.sortedMap.keySet(), this.sync);
            }
            return (ByteSortedSet)this.keys;
        }

        @Override
        public Byte2DoubleSortedMap subMap(byte from, byte to2) {
            return new SynchronizedSortedMap(this.sortedMap.subMap(from, to2), this.sync);
        }

        @Override
        public Byte2DoubleSortedMap headMap(byte to2) {
            return new SynchronizedSortedMap(this.sortedMap.headMap(to2), this.sync);
        }

        @Override
        public Byte2DoubleSortedMap tailMap(byte from) {
            return new SynchronizedSortedMap(this.sortedMap.tailMap(from), this.sync);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public byte firstByteKey() {
            Object object = this.sync;
            synchronized (object) {
                return this.sortedMap.firstByteKey();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public byte lastByteKey() {
            Object object = this.sync;
            synchronized (object) {
                return this.sortedMap.lastByteKey();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public Byte firstKey() {
            Object object = this.sync;
            synchronized (object) {
                return (Byte)this.sortedMap.firstKey();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public Byte lastKey() {
            Object object = this.sync;
            synchronized (object) {
                return (Byte)this.sortedMap.lastKey();
            }
        }

        @Override
        public Byte2DoubleSortedMap subMap(Byte from, Byte to2) {
            return new SynchronizedSortedMap(this.sortedMap.subMap(from, to2), this.sync);
        }

        @Override
        public Byte2DoubleSortedMap headMap(Byte to2) {
            return new SynchronizedSortedMap(this.sortedMap.headMap(to2), this.sync);
        }

        @Override
        public Byte2DoubleSortedMap tailMap(Byte from) {
            return new SynchronizedSortedMap(this.sortedMap.tailMap(from), this.sync);
        }
    }

    public static class Singleton
    extends Byte2DoubleMaps.Singleton
    implements Byte2DoubleSortedMap,
    Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final ByteComparator comparator;

        protected Singleton(byte key, double value, ByteComparator comparator) {
            super(key, value);
            this.comparator = comparator;
        }

        protected Singleton(byte key, double value) {
            this(key, value, null);
        }

        final int compare(byte k1, byte k2) {
            return this.comparator == null ? Byte.compare(k1, k2) : this.comparator.compare(k1, k2);
        }

        @Override
        public ByteComparator comparator() {
            return this.comparator;
        }

        @Override
        public ObjectSortedSet<Byte2DoubleMap.Entry> byte2DoubleEntrySet() {
            if (this.entries == null) {
                this.entries = ObjectSortedSets.singleton(new Byte2DoubleMaps.Singleton.SingletonEntry(), Byte2DoubleSortedMaps.entryComparator(this.comparator));
            }
            return (ObjectSortedSet)this.entries;
        }

        @Override
        public ObjectSortedSet<Map.Entry<Byte, Double>> entrySet() {
            return this.byte2DoubleEntrySet();
        }

        @Override
        public ByteSortedSet keySet() {
            if (this.keys == null) {
                this.keys = ByteSortedSets.singleton(this.key, this.comparator);
            }
            return (ByteSortedSet)this.keys;
        }

        @Override
        public Byte2DoubleSortedMap subMap(byte from, byte to2) {
            if (this.compare(from, this.key) <= 0 && this.compare(this.key, to2) < 0) {
                return this;
            }
            return EMPTY_MAP;
        }

        @Override
        public Byte2DoubleSortedMap headMap(byte to2) {
            if (this.compare(this.key, to2) < 0) {
                return this;
            }
            return EMPTY_MAP;
        }

        @Override
        public Byte2DoubleSortedMap tailMap(byte from) {
            if (this.compare(from, this.key) <= 0) {
                return this;
            }
            return EMPTY_MAP;
        }

        @Override
        public byte firstByteKey() {
            return this.key;
        }

        @Override
        public byte lastByteKey() {
            return this.key;
        }

        @Override
        @Deprecated
        public Byte2DoubleSortedMap headMap(Byte oto) {
            return this.headMap((byte)oto);
        }

        @Override
        @Deprecated
        public Byte2DoubleSortedMap tailMap(Byte ofrom) {
            return this.tailMap((byte)ofrom);
        }

        @Override
        @Deprecated
        public Byte2DoubleSortedMap subMap(Byte ofrom, Byte oto) {
            return this.subMap((byte)ofrom, (byte)oto);
        }

        @Override
        @Deprecated
        public Byte firstKey() {
            return this.firstByteKey();
        }

        @Override
        @Deprecated
        public Byte lastKey() {
            return this.lastByteKey();
        }
    }

    public static class EmptySortedMap
    extends Byte2DoubleMaps.EmptyMap
    implements Byte2DoubleSortedMap,
    Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;

        protected EmptySortedMap() {
        }

        @Override
        public ByteComparator comparator() {
            return null;
        }

        @Override
        public ObjectSortedSet<Byte2DoubleMap.Entry> byte2DoubleEntrySet() {
            return ObjectSortedSets.EMPTY_SET;
        }

        @Override
        public ObjectSortedSet<Map.Entry<Byte, Double>> entrySet() {
            return ObjectSortedSets.EMPTY_SET;
        }

        @Override
        public ByteSortedSet keySet() {
            return ByteSortedSets.EMPTY_SET;
        }

        @Override
        public Byte2DoubleSortedMap subMap(byte from, byte to2) {
            return EMPTY_MAP;
        }

        @Override
        public Byte2DoubleSortedMap headMap(byte to2) {
            return EMPTY_MAP;
        }

        @Override
        public Byte2DoubleSortedMap tailMap(byte from) {
            return EMPTY_MAP;
        }

        @Override
        public byte firstByteKey() {
            throw new NoSuchElementException();
        }

        @Override
        public byte lastByteKey() {
            throw new NoSuchElementException();
        }

        @Override
        @Deprecated
        public Byte2DoubleSortedMap headMap(Byte oto) {
            return this.headMap((byte)oto);
        }

        @Override
        @Deprecated
        public Byte2DoubleSortedMap tailMap(Byte ofrom) {
            return this.tailMap((byte)ofrom);
        }

        @Override
        @Deprecated
        public Byte2DoubleSortedMap subMap(Byte ofrom, Byte oto) {
            return this.subMap((byte)ofrom, (byte)oto);
        }

        @Override
        @Deprecated
        public Byte firstKey() {
            return this.firstByteKey();
        }

        @Override
        @Deprecated
        public Byte lastKey() {
            return this.lastByteKey();
        }
    }
}

