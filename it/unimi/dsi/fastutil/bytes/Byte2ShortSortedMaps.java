package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.bytes.Byte2ShortMap;
import it.unimi.dsi.fastutil.bytes.Byte2ShortMaps;
import it.unimi.dsi.fastutil.bytes.Byte2ShortSortedMap;
import it.unimi.dsi.fastutil.bytes.ByteComparator;
import it.unimi.dsi.fastutil.bytes.ByteSortedSet;
import it.unimi.dsi.fastutil.bytes.ByteSortedSets;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import it.unimi.dsi.fastutil.objects.ObjectSortedSets;
import java.io.Serializable;
import java.util.Comparator;
import java.util.Map;
import java.util.NoSuchElementException;

public class Byte2ShortSortedMaps {
    public static final EmptySortedMap EMPTY_MAP = new EmptySortedMap();

    private Byte2ShortSortedMaps() {
    }

    public static Comparator<? super Map.Entry<Byte, ?>> entryComparator(final ByteComparator comparator) {
        return new Comparator<Map.Entry<Byte, ?>>(){

            @Override
            public int compare(Map.Entry<Byte, ?> x2, Map.Entry<Byte, ?> y2) {
                return comparator.compare(x2.getKey(), y2.getKey());
            }
        };
    }

    public static Byte2ShortSortedMap singleton(Byte key, Short value) {
        return new Singleton(key, value);
    }

    public static Byte2ShortSortedMap singleton(Byte key, Short value, ByteComparator comparator) {
        return new Singleton(key, value, comparator);
    }

    public static Byte2ShortSortedMap singleton(byte key, short value) {
        return new Singleton(key, value);
    }

    public static Byte2ShortSortedMap singleton(byte key, short value, ByteComparator comparator) {
        return new Singleton(key, value, comparator);
    }

    public static Byte2ShortSortedMap synchronize(Byte2ShortSortedMap m2) {
        return new SynchronizedSortedMap(m2);
    }

    public static Byte2ShortSortedMap synchronize(Byte2ShortSortedMap m2, Object sync) {
        return new SynchronizedSortedMap(m2, sync);
    }

    public static Byte2ShortSortedMap unmodifiable(Byte2ShortSortedMap m2) {
        return new UnmodifiableSortedMap(m2);
    }

    public static class UnmodifiableSortedMap
    extends Byte2ShortMaps.UnmodifiableMap
    implements Byte2ShortSortedMap,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final Byte2ShortSortedMap sortedMap;

        protected UnmodifiableSortedMap(Byte2ShortSortedMap m2) {
            super(m2);
            this.sortedMap = m2;
        }

        @Override
        public ByteComparator comparator() {
            return this.sortedMap.comparator();
        }

        @Override
        public ObjectSortedSet<Byte2ShortMap.Entry> byte2ShortEntrySet() {
            if (this.entries == null) {
                this.entries = ObjectSortedSets.unmodifiable(this.sortedMap.byte2ShortEntrySet());
            }
            return (ObjectSortedSet)this.entries;
        }

        @Override
        public ObjectSortedSet<Map.Entry<Byte, Short>> entrySet() {
            return this.byte2ShortEntrySet();
        }

        @Override
        public ByteSortedSet keySet() {
            if (this.keys == null) {
                this.keys = ByteSortedSets.unmodifiable(this.sortedMap.keySet());
            }
            return (ByteSortedSet)this.keys;
        }

        @Override
        public Byte2ShortSortedMap subMap(byte from, byte to2) {
            return new UnmodifiableSortedMap(this.sortedMap.subMap(from, to2));
        }

        @Override
        public Byte2ShortSortedMap headMap(byte to2) {
            return new UnmodifiableSortedMap(this.sortedMap.headMap(to2));
        }

        @Override
        public Byte2ShortSortedMap tailMap(byte from) {
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
        public Byte2ShortSortedMap subMap(Byte from, Byte to2) {
            return new UnmodifiableSortedMap(this.sortedMap.subMap(from, to2));
        }

        @Override
        public Byte2ShortSortedMap headMap(Byte to2) {
            return new UnmodifiableSortedMap(this.sortedMap.headMap(to2));
        }

        @Override
        public Byte2ShortSortedMap tailMap(Byte from) {
            return new UnmodifiableSortedMap(this.sortedMap.tailMap(from));
        }
    }

    public static class SynchronizedSortedMap
    extends Byte2ShortMaps.SynchronizedMap
    implements Byte2ShortSortedMap,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final Byte2ShortSortedMap sortedMap;

        protected SynchronizedSortedMap(Byte2ShortSortedMap m2, Object sync) {
            super(m2, sync);
            this.sortedMap = m2;
        }

        protected SynchronizedSortedMap(Byte2ShortSortedMap m2) {
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
        public ObjectSortedSet<Byte2ShortMap.Entry> byte2ShortEntrySet() {
            if (this.entries == null) {
                this.entries = ObjectSortedSets.synchronize(this.sortedMap.byte2ShortEntrySet(), this.sync);
            }
            return (ObjectSortedSet)this.entries;
        }

        @Override
        public ObjectSortedSet<Map.Entry<Byte, Short>> entrySet() {
            return this.byte2ShortEntrySet();
        }

        @Override
        public ByteSortedSet keySet() {
            if (this.keys == null) {
                this.keys = ByteSortedSets.synchronize(this.sortedMap.keySet(), this.sync);
            }
            return (ByteSortedSet)this.keys;
        }

        @Override
        public Byte2ShortSortedMap subMap(byte from, byte to2) {
            return new SynchronizedSortedMap(this.sortedMap.subMap(from, to2), this.sync);
        }

        @Override
        public Byte2ShortSortedMap headMap(byte to2) {
            return new SynchronizedSortedMap(this.sortedMap.headMap(to2), this.sync);
        }

        @Override
        public Byte2ShortSortedMap tailMap(byte from) {
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
        public Byte2ShortSortedMap subMap(Byte from, Byte to2) {
            return new SynchronizedSortedMap(this.sortedMap.subMap(from, to2), this.sync);
        }

        @Override
        public Byte2ShortSortedMap headMap(Byte to2) {
            return new SynchronizedSortedMap(this.sortedMap.headMap(to2), this.sync);
        }

        @Override
        public Byte2ShortSortedMap tailMap(Byte from) {
            return new SynchronizedSortedMap(this.sortedMap.tailMap(from), this.sync);
        }
    }

    public static class Singleton
    extends Byte2ShortMaps.Singleton
    implements Byte2ShortSortedMap,
    Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final ByteComparator comparator;

        protected Singleton(byte key, short value, ByteComparator comparator) {
            super(key, value);
            this.comparator = comparator;
        }

        protected Singleton(byte key, short value) {
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
        public ObjectSortedSet<Byte2ShortMap.Entry> byte2ShortEntrySet() {
            if (this.entries == null) {
                this.entries = ObjectSortedSets.singleton(new Byte2ShortMaps.Singleton.SingletonEntry(), Byte2ShortSortedMaps.entryComparator(this.comparator));
            }
            return (ObjectSortedSet)this.entries;
        }

        @Override
        public ObjectSortedSet<Map.Entry<Byte, Short>> entrySet() {
            return this.byte2ShortEntrySet();
        }

        @Override
        public ByteSortedSet keySet() {
            if (this.keys == null) {
                this.keys = ByteSortedSets.singleton(this.key, this.comparator);
            }
            return (ByteSortedSet)this.keys;
        }

        @Override
        public Byte2ShortSortedMap subMap(byte from, byte to2) {
            if (this.compare(from, this.key) <= 0 && this.compare(this.key, to2) < 0) {
                return this;
            }
            return EMPTY_MAP;
        }

        @Override
        public Byte2ShortSortedMap headMap(byte to2) {
            if (this.compare(this.key, to2) < 0) {
                return this;
            }
            return EMPTY_MAP;
        }

        @Override
        public Byte2ShortSortedMap tailMap(byte from) {
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
        public Byte2ShortSortedMap headMap(Byte oto) {
            return this.headMap((byte)oto);
        }

        @Override
        @Deprecated
        public Byte2ShortSortedMap tailMap(Byte ofrom) {
            return this.tailMap((byte)ofrom);
        }

        @Override
        @Deprecated
        public Byte2ShortSortedMap subMap(Byte ofrom, Byte oto) {
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
    extends Byte2ShortMaps.EmptyMap
    implements Byte2ShortSortedMap,
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
        public ObjectSortedSet<Byte2ShortMap.Entry> byte2ShortEntrySet() {
            return ObjectSortedSets.EMPTY_SET;
        }

        @Override
        public ObjectSortedSet<Map.Entry<Byte, Short>> entrySet() {
            return ObjectSortedSets.EMPTY_SET;
        }

        @Override
        public ByteSortedSet keySet() {
            return ByteSortedSets.EMPTY_SET;
        }

        @Override
        public Byte2ShortSortedMap subMap(byte from, byte to2) {
            return EMPTY_MAP;
        }

        @Override
        public Byte2ShortSortedMap headMap(byte to2) {
            return EMPTY_MAP;
        }

        @Override
        public Byte2ShortSortedMap tailMap(byte from) {
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
        public Byte2ShortSortedMap headMap(Byte oto) {
            return this.headMap((byte)oto);
        }

        @Override
        @Deprecated
        public Byte2ShortSortedMap tailMap(Byte ofrom) {
            return this.tailMap((byte)ofrom);
        }

        @Override
        @Deprecated
        public Byte2ShortSortedMap subMap(Byte ofrom, Byte oto) {
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

