 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.bytes.Byte2ObjectAVLTreeMap$Submap.SubmapIterator
 */
package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.bytes.AbstractByte2ObjectSortedMap;
import it.unimi.dsi.fastutil.bytes.Byte2ObjectAVLTreeMap;
import it.unimi.dsi.fastutil.bytes.Byte2ObjectMap;
import it.unimi.dsi.fastutil.bytes.Byte2ObjectSortedMap;
import it.unimi.dsi.fastutil.bytes.ByteBidirectionalIterator;
import it.unimi.dsi.fastutil.bytes.ByteComparator;
import it.unimi.dsi.fastutil.bytes.ByteListIterator;
import it.unimi.dsi.fastutil.bytes.ByteSortedSet;
import it.unimi.dsi.fastutil.objects.AbstractObjectCollection;
import it.unimi.dsi.fastutil.objects.AbstractObjectSortedSet;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectCollection;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Comparator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.SortedMap;

public class Byte2ObjectAVLTreeMap<V>
extends AbstractByte2ObjectSortedMap<V>
implements Serializable,
Cloneable {
    protected transient Entry<V> tree;
    protected int count;
    protected transient Entry<V> firstEntry;
    protected transient Entry<V> lastEntry;
    protected transient ObjectSortedSet<Byte2ObjectMap.Entry<V>> entries;
    protected transient ByteSortedSet keys;
    protected transient ObjectCollection<V> values;
    protected transient boolean modified;
    protected Comparator<? super Byte> storedComparator;
    protected transient ByteComparator actualComparator;
    private static final long serialVersionUID = -7046029254386353129L;
    private static final boolean ASSERTS = false;
    private transient boolean[] dirPath;

    public Byte2ObjectAVLTreeMap() {
        this.allocatePaths();
        this.tree = null;
        this.count = 0;
    }

    private void setActualComparator() {
        this.actualComparator = this.storedComparator == null || this.storedComparator instanceof ByteComparator ? (ByteComparator)this.storedComparator : new ByteComparator(){

            @Override
            public int compare(byte k1, byte k2) {
                return Byte2ObjectAVLTreeMap.this.storedComparator.compare((Byte)k1, (Byte)k2);
            }

            @Override
            public int compare(Byte ok1, Byte ok2) {
                return Byte2ObjectAVLTreeMap.this.storedComparator.compare(ok1, ok2);
            }
        };
    }

    public Byte2ObjectAVLTreeMap(Comparator<? super Byte> c2) {
        this();
        this.storedComparator = c2;
    }

    public Byte2ObjectAVLTreeMap(Map<? extends Byte, ? extends V> m2) {
        this();
        this.putAll((Map<Byte, ? extends V>)m2);
    }

    public Byte2ObjectAVLTreeMap(SortedMap<Byte, V> m2) {
        this(m2.comparator());
        this.putAll((Map<Byte, V>)m2);
    }

    public Byte2ObjectAVLTreeMap(Byte2ObjectMap<? extends V> m2) {
        this();
        this.putAll(m2);
    }

    public Byte2ObjectAVLTreeMap(Byte2ObjectSortedMap<V> m2) {
        this(m2.comparator());
        this.putAll(m2);
    }

    public Byte2ObjectAVLTreeMap(byte[] k2, V[] v2, Comparator<? super Byte> c2) {
        this(c2);
        if (k2.length != v2.length) {
            throw new IllegalArgumentException("The key array and the value array have different lengths (" + k2.length + " and " + v2.length + ")");
        }
        for (int i2 = 0; i2 < k2.length; ++i2) {
            this.put(k2[i2], v2[i2]);
        }
    }

    public Byte2ObjectAVLTreeMap(byte[] k2, V[] v2) {
        this(k2, v2, null);
    }

    final int compare(byte k1, byte k2) {
        return this.actualComparator == null ? Byte.compare(k1, k2) : this.actualComparator.compare(k1, k2);
    }

    final Entry<V> findKey(byte k2) {
        int cmp;
        Entry<V> e2 = this.tree;
        while (e2 != null && (cmp = this.compare(k2, e2.key)) != 0) {
            e2 = cmp < 0 ? e2.left() : e2.right();
        }
        return e2;
    }

    final Entry<V> locateKey(byte k2) {
        Entry<V> e2 = this.tree;
        Entry<V> last = this.tree;
        int cmp = 0;
        while (e2 != null && (cmp = this.compare(k2, e2.key)) != 0) {
            last = e2;
            e2 = cmp < 0 ? e2.left() : e2.right();
        }
        return cmp == 0 ? e2 : last;
    }

    private void allocatePaths() {
        this.dirPath = new boolean[48];
    }

    @Override
    public V put(byte k2, V v2) {
        Entry<V> e2 = this.add(k2);
        Object oldValue = e2.value;
        e2.value = v2;
        return oldValue;
    }

    private Entry<V> add(byte k2) {
        this.modified = false;
        Entry<Object> e2 = null;
        if (this.tree == null) {
            ++this.count;
            this.firstEntry = new Entry<Object>(k2, this.defRetValue);
            this.lastEntry = this.firstEntry;
            this.tree = this.firstEntry;
            e2 = this.firstEntry;
            this.modified = true;
        } else {
            Entry<Object> p2 = this.tree;
            Entry<Object> q2 = null;
            Entry y2 = this.tree;
            Entry<Object> z2 = null;
            Entry w2 = null;
            int i2 = 0;
            while (true) {
                int cmp;
                if ((cmp = this.compare(k2, p2.key)) == 0) {
                    return p2;
                }
                if (p2.balance() != 0) {
                    i2 = 0;
                    z2 = q2;
                    y2 = p2;
                }
                if (this.dirPath[i2++] = cmp > 0) {
                    if (p2.succ()) {
                        ++this.count;
                        e2 = new Entry<Object>(k2, this.defRetValue);
                        this.modified = true;
                        if (p2.right == null) {
                            this.lastEntry = e2;
                        }
                        e2.left = p2;
                        e2.right = p2.right;
                        p2.right(e2);
                        break;
                    }
                    q2 = p2;
                    p2 = p2.right;
                    continue;
                }
                if (p2.pred()) {
                    ++this.count;
                    e2 = new Entry<Object>(k2, this.defRetValue);
                    this.modified = true;
                    if (p2.left == null) {
                        this.firstEntry = e2;
                    }
                    e2.right = p2;
                    e2.left = p2.left;
                    p2.left(e2);
                    break;
                }
                q2 = p2;
                p2 = p2.left;
            }
            p2 = y2;
            i2 = 0;
            while (p2 != e2) {
                if (this.dirPath[i2]) {
                    p2.incBalance();
                } else {
                    p2.decBalance();
                }
                p2 = this.dirPath[i2++] ? p2.right : p2.left;
            }
            if (y2.balance() == -2) {
                Entry x2 = y2.left;
                if (x2.balance() == -1) {
                    w2 = x2;
                    if (x2.succ()) {
                        x2.succ(false);
                        y2.pred(x2);
                    } else {
                        y2.left = x2.right;
                    }
                    x2.right = y2;
                    x2.balance(0);
                    y2.balance(0);
                } else {
                    w2 = x2.right;
                    x2.right = w2.left;
                    w2.left = x2;
                    y2.left = w2.right;
                    w2.right = y2;
                    if (w2.balance() == -1) {
                        x2.balance(0);
                        y2.balance(1);
                    } else if (w2.balance() == 0) {
                        x2.balance(0);
                        y2.balance(0);
                    } else {
                        x2.balance(-1);
                        y2.balance(0);
                    }
                    w2.balance(0);
                    if (w2.pred()) {
                        x2.succ(w2);
                        w2.pred(false);
                    }
                    if (w2.succ()) {
                        y2.pred(w2);
                        w2.succ(false);
                    }
                }
            } else if (y2.balance() == 2) {
                Entry x3 = y2.right;
                if (x3.balance() == 1) {
                    w2 = x3;
                    if (x3.pred()) {
                        x3.pred(false);
                        y2.succ(x3);
                    } else {
                        y2.right = x3.left;
                    }
                    x3.left = y2;
                    x3.balance(0);
                    y2.balance(0);
                } else {
                    w2 = x3.left;
                    x3.left = w2.right;
                    w2.right = x3;
                    y2.right = w2.left;
                    w2.left = y2;
                    if (w2.balance() == 1) {
                        x3.balance(0);
                        y2.balance(-1);
                    } else if (w2.balance() == 0) {
                        x3.balance(0);
                        y2.balance(0);
                    } else {
                        x3.balance(1);
                        y2.balance(0);
                    }
                    w2.balance(0);
                    if (w2.pred()) {
                        y2.succ(w2);
                        w2.pred(false);
                    }
                    if (w2.succ()) {
                        x3.pred(w2);
                        w2.succ(false);
                    }
                }
            } else {
                return e2;
            }
            if (z2 == null) {
                this.tree = w2;
            } else if (z2.left == y2) {
                z2.left = w2;
            } else {
                z2.right = w2;
            }
        }
        return e2;
    }

    private Entry<V> parent(Entry<V> e2) {
        Entry<V> y2;
        if (e2 == this.tree) {
            return null;
        }
        Entry<V> x2 = y2 = e2;
        while (true) {
            if (y2.succ()) {
                Entry p2 = y2.right;
                if (p2 == null || p2.left != e2) {
                    while (!x2.pred()) {
                        x2 = x2.left;
                    }
                    p2 = x2.left;
                }
                return p2;
            }
            if (x2.pred()) {
                Entry p3 = x2.left;
                if (p3 == null || p3.right != e2) {
                    while (!y2.succ()) {
                        y2 = y2.right;
                    }
                    p3 = y2.right;
                }
                return p3;
            }
            x2 = x2.left;
            y2 = y2.right;
        }
    }

    @Override
    public V remove(byte k2) {
        int cmp;
        this.modified = false;
        if (this.tree == null) {
            return (V)this.defRetValue;
        }
        Entry p2 = this.tree;
        Entry q2 = null;
        boolean dir = false;
        byte kk2 = k2;
        while ((cmp = this.compare(kk2, p2.key)) != 0) {
            dir = cmp > 0;
            if (dir) {
                q2 = p2;
                if ((p2 = p2.right()) != null) continue;
                return (V)this.defRetValue;
            }
            q2 = p2;
            if ((p2 = p2.left()) != null) continue;
            return (V)this.defRetValue;
        }
        if (p2.left == null) {
            this.firstEntry = p2.next();
        }
        if (p2.right == null) {
            this.lastEntry = p2.prev();
        }
        if (p2.succ()) {
            if (p2.pred()) {
                if (q2 != null) {
                    if (dir) {
                        q2.succ(p2.right);
                    } else {
                        q2.pred(p2.left);
                    }
                } else {
                    this.tree = dir ? p2.right : p2.left;
                }
            } else {
                p2.prev().right = p2.right;
                if (q2 != null) {
                    if (dir) {
                        q2.right = p2.left;
                    } else {
                        q2.left = p2.left;
                    }
                } else {
                    this.tree = p2.left;
                }
            }
        } else {
            Entry r2 = p2.right;
            if (r2.pred()) {
                r2.left = p2.left;
                r2.pred(p2.pred());
                if (!r2.pred()) {
                    r2.prev().right = r2;
                }
                if (q2 != null) {
                    if (dir) {
                        q2.right = r2;
                    } else {
                        q2.left = r2;
                    }
                } else {
                    this.tree = r2;
                }
                r2.balance(p2.balance());
                q2 = r2;
                dir = true;
            } else {
                Entry s2;
                while (!(s2 = r2.left).pred()) {
                    r2 = s2;
                }
                if (s2.succ()) {
                    r2.pred(s2);
                } else {
                    r2.left = s2.right;
                }
                s2.left = p2.left;
                if (!p2.pred()) {
                    p2.prev().right = s2;
                    s2.pred(false);
                }
                s2.right = p2.right;
                s2.succ(false);
                if (q2 != null) {
                    if (dir) {
                        q2.right = s2;
                    } else {
                        q2.left = s2;
                    }
                } else {
                    this.tree = s2;
                }
                s2.balance(p2.balance());
                q2 = r2;
                dir = false;
            }
        }
        while (q2 != null) {
            Entry w2;
            Entry x2;
            Entry y2 = q2;
            q2 = this.parent(y2);
            if (!dir) {
                dir = q2 != null && q2.left != y2;
                y2.incBalance();
                if (y2.balance() == 1) break;
                if (y2.balance() != 2) continue;
                x2 = y2.right;
                if (x2.balance() == -1) {
                    w2 = x2.left;
                    x2.left = w2.right;
                    w2.right = x2;
                    y2.right = w2.left;
                    w2.left = y2;
                    if (w2.balance() == 1) {
                        x2.balance(0);
                        y2.balance(-1);
                    } else if (w2.balance() == 0) {
                        x2.balance(0);
                        y2.balance(0);
                    } else {
                        x2.balance(1);
                        y2.balance(0);
                    }
                    w2.balance(0);
                    if (w2.pred()) {
                        y2.succ(w2);
                        w2.pred(false);
                    }
                    if (w2.succ()) {
                        x2.pred(w2);
                        w2.succ(false);
                    }
                    if (q2 != null) {
                        if (dir) {
                            q2.right = w2;
                            continue;
                        }
                        q2.left = w2;
                        continue;
                    }
                    this.tree = w2;
                    continue;
                }
                if (q2 != null) {
                    if (dir) {
                        q2.right = x2;
                    } else {
                        q2.left = x2;
                    }
                } else {
                    this.tree = x2;
                }
                if (x2.balance() == 0) {
                    y2.right = x2.left;
                    x2.left = y2;
                    x2.balance(-1);
                    y2.balance(1);
                    break;
                }
                if (x2.pred()) {
                    y2.succ(true);
                    x2.pred(false);
                } else {
                    y2.right = x2.left;
                }
                x2.left = y2;
                y2.balance(0);
                x2.balance(0);
                continue;
            }
            dir = q2 != null && q2.left != y2;
            y2.decBalance();
            if (y2.balance() == -1) break;
            if (y2.balance() != -2) continue;
            x2 = y2.left;
            if (x2.balance() == 1) {
                w2 = x2.right;
                x2.right = w2.left;
                w2.left = x2;
                y2.left = w2.right;
                w2.right = y2;
                if (w2.balance() == -1) {
                    x2.balance(0);
                    y2.balance(1);
                } else if (w2.balance() == 0) {
                    x2.balance(0);
                    y2.balance(0);
                } else {
                    x2.balance(-1);
                    y2.balance(0);
                }
                w2.balance(0);
                if (w2.pred()) {
                    x2.succ(w2);
                    w2.pred(false);
                }
                if (w2.succ()) {
                    y2.pred(w2);
                    w2.succ(false);
                }
                if (q2 != null) {
                    if (dir) {
                        q2.right = w2;
                        continue;
                    }
                    q2.left = w2;
                    continue;
                }
                this.tree = w2;
                continue;
            }
            if (q2 != null) {
                if (dir) {
                    q2.right = x2;
                } else {
                    q2.left = x2;
                }
            } else {
                this.tree = x2;
            }
            if (x2.balance() == 0) {
                y2.left = x2.right;
                x2.right = y2;
                x2.balance(1);
                y2.balance(-1);
                break;
            }
            if (x2.succ()) {
                y2.pred(true);
                x2.succ(false);
            } else {
                y2.left = x2.right;
            }
            x2.right = y2;
            y2.balance(0);
            x2.balance(0);
        }
        this.modified = true;
        --this.count;
        return p2.value;
    }

    @Override
    @Deprecated
    public V put(Byte ok2, V ov) {
        V oldValue = this.put((byte)ok2, ov);
        return (V)(this.modified ? this.defRetValue : oldValue);
    }

    @Override
    @Deprecated
    public V remove(Object ok2) {
        V oldValue = this.remove((Byte)ok2);
        return (V)(this.modified ? oldValue : this.defRetValue);
    }

    @Override
    public boolean containsValue(Object v2) {
        ValueIterator i2 = new ValueIterator();
        int j2 = this.count;
        while (j2-- != 0) {
            Object ev2 = i2.next();
            if (!(ev2 == null ? v2 == null : ev2.equals(v2))) continue;
            return true;
        }
        return false;
    }

    @Override
    public void clear() {
        this.count = 0;
        this.tree = null;
        this.entries = null;
        this.values = null;
        this.keys = null;
        this.lastEntry = null;
        this.firstEntry = null;
    }

    @Override
    public boolean containsKey(byte k2) {
        return this.findKey(k2) != null;
    }

    @Override
    public int size() {
        return this.count;
    }

    @Override
    public boolean isEmpty() {
        return this.count == 0;
    }

    @Override
    public V get(byte k2) {
        Entry<V> e2 = this.findKey(k2);
        return (V)(e2 == null ? this.defRetValue : e2.value);
    }

    @Override
    public byte firstByteKey() {
        if (this.tree == null) {
            throw new NoSuchElementException();
        }
        return this.firstEntry.key;
    }

    @Override
    public byte lastByteKey() {
        if (this.tree == null) {
            throw new NoSuchElementException();
        }
        return this.lastEntry.key;
    }

    @Override
    public ObjectSortedSet<Byte2ObjectMap.Entry<V>> byte2ObjectEntrySet() {
        if (this.entries == null) {
            this.entries = new AbstractObjectSortedSet<Byte2ObjectMap.Entry<V>>(){
                final Comparator<? super Byte2ObjectMap.Entry<V>> comparator = new Comparator<Byte2ObjectMap.Entry<V>>(){

                    @Override
                    public int compare(Byte2ObjectMap.Entry<V> x2, Byte2ObjectMap.Entry<V> y2) {
                        return Byte2ObjectAVLTreeMap.this.actualComparator.compare(x2.getByteKey(), y2.getByteKey());
                    }
                };

                @Override
                public Comparator<? super Byte2ObjectMap.Entry<V>> comparator() {
                    return this.comparator;
                }

                @Override
                public ObjectBidirectionalIterator<Byte2ObjectMap.Entry<V>> iterator() {
                    return new EntryIterator();
                }

                @Override
                public ObjectBidirectionalIterator<Byte2ObjectMap.Entry<V>> iterator(Byte2ObjectMap.Entry<V> from) {
                    return new EntryIterator(from.getByteKey());
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
                    Entry f2 = Byte2ObjectAVLTreeMap.this.findKey((Byte)e2.getKey());
                    return e2.equals(f2);
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
                    Entry f2 = Byte2ObjectAVLTreeMap.this.findKey((Byte)e2.getKey());
                    if (f2 != null) {
                        Byte2ObjectAVLTreeMap.this.remove(f2.key);
                    }
                    return f2 != null;
                }

                @Override
                public int size() {
                    return Byte2ObjectAVLTreeMap.this.count;
                }

                @Override
                public void clear() {
                    Byte2ObjectAVLTreeMap.this.clear();
                }

                @Override
                public Byte2ObjectMap.Entry<V> first() {
                    return Byte2ObjectAVLTreeMap.this.firstEntry;
                }

                @Override
                public Byte2ObjectMap.Entry<V> last() {
                    return Byte2ObjectAVLTreeMap.this.lastEntry;
                }

                @Override
                public ObjectSortedSet<Byte2ObjectMap.Entry<V>> subSet(Byte2ObjectMap.Entry<V> from, Byte2ObjectMap.Entry<V> to2) {
                    return Byte2ObjectAVLTreeMap.this.subMap(from.getByteKey(), to2.getByteKey()).byte2ObjectEntrySet();
                }

                @Override
                public ObjectSortedSet<Byte2ObjectMap.Entry<V>> headSet(Byte2ObjectMap.Entry<V> to2) {
                    return Byte2ObjectAVLTreeMap.this.headMap(to2.getByteKey()).byte2ObjectEntrySet();
                }

                @Override
                public ObjectSortedSet<Byte2ObjectMap.Entry<V>> tailSet(Byte2ObjectMap.Entry<V> from) {
                    return Byte2ObjectAVLTreeMap.this.tailMap(from.getByteKey()).byte2ObjectEntrySet();
                }
            };
        }
        return this.entries;
    }

    @Override
    public ByteSortedSet keySet() {
        if (this.keys == null) {
            this.keys = new KeySet();
        }
        return this.keys;
    }

    @Override
    public ObjectCollection<V> values() {
        if (this.values == null) {
            this.values = new AbstractObjectCollection<V>(){

                @Override
                public ObjectIterator<V> iterator() {
                    return new ValueIterator();
                }

                @Override
                public boolean contains(Object k2) {
                    return Byte2ObjectAVLTreeMap.this.containsValue(k2);
                }

                @Override
                public int size() {
                    return Byte2ObjectAVLTreeMap.this.count;
                }

                @Override
                public void clear() {
                    Byte2ObjectAVLTreeMap.this.clear();
                }
            };
        }
        return this.values;
    }

    @Override
    public ByteComparator comparator() {
        return this.actualComparator;
    }

    @Override
    public Byte2ObjectSortedMap<V> headMap(byte to2) {
        return new Submap(0, true, to2, false);
    }

    @Override
    public Byte2ObjectSortedMap<V> tailMap(byte from) {
        return new Submap(from, false, 0, true);
    }

    @Override
    public Byte2ObjectSortedMap<V> subMap(byte from, byte to2) {
        return new Submap(from, false, to2, false);
    }

    public Byte2ObjectAVLTreeMap<V> clone() {
        Byte2ObjectAVLTreeMap c2;
        try {
            c2 = (Byte2ObjectAVLTreeMap)super.clone();
        }
        catch (CloneNotSupportedException cantHappen) {
            throw new InternalError();
        }
        c2.keys = null;
        c2.values = null;
        c2.entries = null;
        c2.allocatePaths();
        if (this.count != 0) {
            Entry<V> rp2 = new Entry<V>();
            Entry rq = new Entry();
            Entry<V> p2 = rp2;
            rp2.left(this.tree);
            Entry q2 = rq;
            rq.pred(null);
            while (true) {
                Object e2;
                if (!p2.pred()) {
                    e2 = p2.left.clone();
                    ((Entry)e2).pred(q2.left);
                    ((Entry)e2).succ(q2);
                    q2.left(e2);
                    p2 = p2.left;
                    q2 = q2.left;
                } else {
                    while (p2.succ()) {
                        p2 = p2.right;
                        if (p2 == null) {
                            q2.right = null;
                            c2.tree = rq.left;
                            c2.firstEntry = c2.tree;
                            while (c2.firstEntry.left != null) {
                                c2.firstEntry = c2.firstEntry.left;
                            }
                            c2.lastEntry = c2.tree;
                            while (c2.lastEntry.right != null) {
                                c2.lastEntry = c2.lastEntry.right;
                            }
                            return c2;
                        }
                        q2 = q2.right;
                    }
                    p2 = p2.right;
                    q2 = q2.right;
                }
                if (p2.succ()) continue;
                e2 = p2.right.clone();
                ((Entry)e2).succ(q2.right);
                ((Entry)e2).pred(q2);
                q2.right(e2);
            }
        }
        return c2;
    }

    private void writeObject(ObjectOutputStream s2) throws IOException {
        int n2 = this.count;
        EntryIterator i2 = new EntryIterator();
        s2.defaultWriteObject();
        while (n2-- != 0) {
            Entry e2 = i2.nextEntry();
            s2.writeByte(e2.key);
            s2.writeObject(e2.value);
        }
    }

    private Entry<V> readTree(ObjectInputStream s2, int n2, Entry<V> pred, Entry<V> succ) throws IOException, ClassNotFoundException {
        if (n2 == 1) {
            Entry<Object> top = new Entry<Object>(s2.readByte(), s2.readObject());
            top.pred(pred);
            top.succ(succ);
            return top;
        }
        if (n2 == 2) {
            Entry<Object> top = new Entry<Object>(s2.readByte(), s2.readObject());
            top.right(new Entry<Object>(s2.readByte(), s2.readObject()));
            top.right.pred(top);
            top.balance(1);
            top.pred(pred);
            top.right.succ(succ);
            return top;
        }
        int rightN = n2 / 2;
        int leftN = n2 - rightN - 1;
        Entry top = new Entry();
        top.left(this.readTree(s2, leftN, pred, top));
        top.key = s2.readByte();
        top.value = s2.readObject();
        top.right(this.readTree(s2, rightN, top, succ));
        if (n2 == (n2 & -n2)) {
            top.balance(1);
        }
        return top;
    }

    private void readObject(ObjectInputStream s2) throws IOException, ClassNotFoundException {
        s2.defaultReadObject();
        this.setActualComparator();
        this.allocatePaths();
        if (this.count != 0) {
            this.tree = this.readTree(s2, this.count, null, null);
            Entry<V> e2 = this.tree;
            while (e2.left() != null) {
                e2 = e2.left();
            }
            this.firstEntry = e2;
            e2 = this.tree;
            while (e2.right() != null) {
                e2 = e2.right();
            }
            this.lastEntry = e2;
        }
    }

    private static <V> int checkTree(Entry<V> e2) {
        return 0;
    }

    private final class Submap
    extends AbstractByte2ObjectSortedMap<V>
    implements Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        byte from;
        byte to;
        boolean bottom;
        boolean top;
        protected transient ObjectSortedSet<Byte2ObjectMap.Entry<V>> entries;
        protected transient ByteSortedSet keys;
        protected transient ObjectCollection<V> values;

        public Submap(byte from, boolean bottom, byte to2, boolean top) {
            if (!bottom && !top && Byte2ObjectAVLTreeMap.this.compare(from, to2) > 0) {
                throw new IllegalArgumentException("Start key (" + from + ") is larger than end key (" + to2 + ")");
            }
            this.from = from;
            this.bottom = bottom;
            this.to = to2;
            this.top = top;
            this.defRetValue = Byte2ObjectAVLTreeMap.this.defRetValue;
        }

        @Override
        public void clear() {
            SubmapIterator i2 = new SubmapIterator();
            while (i2.hasNext()) {
                i2.nextEntry();
                i2.remove();
            }
        }

        final boolean in(byte k2) {
            return !(!this.bottom && Byte2ObjectAVLTreeMap.this.compare(k2, this.from) < 0 || !this.top && Byte2ObjectAVLTreeMap.this.compare(k2, this.to) >= 0);
        }

        @Override
        public ObjectSortedSet<Byte2ObjectMap.Entry<V>> byte2ObjectEntrySet() {
            if (this.entries == null) {
                this.entries = new AbstractObjectSortedSet<Byte2ObjectMap.Entry<V>>(){

                    @Override
                    public ObjectBidirectionalIterator<Byte2ObjectMap.Entry<V>> iterator() {
                        return new SubmapEntryIterator();
                    }

                    @Override
                    public ObjectBidirectionalIterator<Byte2ObjectMap.Entry<V>> iterator(Byte2ObjectMap.Entry<V> from) {
                        return new SubmapEntryIterator(from.getByteKey());
                    }

                    @Override
                    public Comparator<? super Byte2ObjectMap.Entry<V>> comparator() {
                        return Byte2ObjectAVLTreeMap.this.entrySet().comparator();
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
                        Entry f2 = Byte2ObjectAVLTreeMap.this.findKey((Byte)e2.getKey());
                        return f2 != null && Submap.this.in(f2.key) && e2.equals(f2);
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
                        Entry f2 = Byte2ObjectAVLTreeMap.this.findKey((Byte)e2.getKey());
                        if (f2 != null && Submap.this.in(f2.key)) {
                            Submap.this.remove(f2.key);
                        }
                        return f2 != null;
                    }

                    @Override
                    public int size() {
                        int c2 = 0;
                        ObjectIterator i2 = this.iterator();
                        while (i2.hasNext()) {
                            ++c2;
                            i2.next();
                        }
                        return c2;
                    }

                    @Override
                    public boolean isEmpty() {
                        return !new SubmapIterator().hasNext();
                    }

                    @Override
                    public void clear() {
                        Submap.this.clear();
                    }

                    @Override
                    public Byte2ObjectMap.Entry<V> first() {
                        return Submap.this.firstEntry();
                    }

                    @Override
                    public Byte2ObjectMap.Entry<V> last() {
                        return Submap.this.lastEntry();
                    }

                    @Override
                    public ObjectSortedSet<Byte2ObjectMap.Entry<V>> subSet(Byte2ObjectMap.Entry<V> from, Byte2ObjectMap.Entry<V> to2) {
                        return Submap.this.subMap(from.getByteKey(), to2.getByteKey()).byte2ObjectEntrySet();
                    }

                    @Override
                    public ObjectSortedSet<Byte2ObjectMap.Entry<V>> headSet(Byte2ObjectMap.Entry<V> to2) {
                        return Submap.this.headMap(to2.getByteKey()).byte2ObjectEntrySet();
                    }

                    @Override
                    public ObjectSortedSet<Byte2ObjectMap.Entry<V>> tailSet(Byte2ObjectMap.Entry<V> from) {
                        return Submap.this.tailMap(from.getByteKey()).byte2ObjectEntrySet();
                    }
                };
            }
            return this.entries;
        }

        @Override
        public ByteSortedSet keySet() {
            if (this.keys == null) {
                this.keys = new KeySet();
            }
            return this.keys;
        }

        @Override
        public ObjectCollection<V> values() {
            if (this.values == null) {
                this.values = new AbstractObjectCollection<V>(){

                    @Override
                    public ObjectIterator<V> iterator() {
                        return new SubmapValueIterator();
                    }

                    @Override
                    public boolean contains(Object k2) {
                        return Submap.this.containsValue(k2);
                    }

                    @Override
                    public int size() {
                        return Submap.this.size();
                    }

                    @Override
                    public void clear() {
                        Submap.this.clear();
                    }
                };
            }
            return this.values;
        }

        @Override
        public boolean containsKey(byte k2) {
            return this.in(k2) && Byte2ObjectAVLTreeMap.this.containsKey(k2);
        }

        @Override
        public boolean containsValue(Object v2) {
            SubmapIterator i2 = new SubmapIterator();
            while (i2.hasNext()) {
                Object ev2 = i2.nextEntry().value;
                if (!(ev2 == null ? v2 == null : ev2.equals(v2))) continue;
                return true;
            }
            return false;
        }

        @Override
        public V get(byte k2) {
            Entry e2;
            byte kk2 = k2;
            return this.in(kk2) && (e2 = Byte2ObjectAVLTreeMap.this.findKey(kk2)) != null ? e2.value : this.defRetValue;
        }

        @Override
        public V put(byte k2, V v2) {
            Byte2ObjectAVLTreeMap.this.modified = false;
            if (!this.in(k2)) {
                throw new IllegalArgumentException("Key (" + k2 + ") out of range [" + (this.bottom ? "-" : String.valueOf(this.from)) + ", " + (this.top ? "-" : String.valueOf(this.to)) + ")");
            }
            Object oldValue = Byte2ObjectAVLTreeMap.this.put(k2, v2);
            return Byte2ObjectAVLTreeMap.this.modified ? this.defRetValue : oldValue;
        }

        @Override
        @Deprecated
        public V put(Byte ok2, V ov) {
            Object oldValue = this.put((byte)ok2, (V)ov);
            return Byte2ObjectAVLTreeMap.this.modified ? this.defRetValue : oldValue;
        }

        @Override
        public V remove(byte k2) {
            Byte2ObjectAVLTreeMap.this.modified = false;
            if (!this.in(k2)) {
                return this.defRetValue;
            }
            Object oldValue = Byte2ObjectAVLTreeMap.this.remove(k2);
            return Byte2ObjectAVLTreeMap.this.modified ? oldValue : this.defRetValue;
        }

        @Override
        @Deprecated
        public V remove(Object ok2) {
            Object oldValue = this.remove((Byte)ok2);
            return Byte2ObjectAVLTreeMap.this.modified ? oldValue : this.defRetValue;
        }

        @Override
        public int size() {
            SubmapIterator i2 = new SubmapIterator();
            int n2 = 0;
            while (i2.hasNext()) {
                ++n2;
                i2.nextEntry();
            }
            return n2;
        }

        @Override
        public boolean isEmpty() {
            return !new SubmapIterator().hasNext();
        }

        @Override
        public ByteComparator comparator() {
            return Byte2ObjectAVLTreeMap.this.actualComparator;
        }

        @Override
        public Byte2ObjectSortedMap<V> headMap(byte to2) {
            if (this.top) {
                return new Submap(this.from, this.bottom, to2, false);
            }
            return Byte2ObjectAVLTreeMap.this.compare(to2, this.to) < 0 ? new Submap(this.from, this.bottom, to2, false) : this;
        }

        @Override
        public Byte2ObjectSortedMap<V> tailMap(byte from) {
            if (this.bottom) {
                return new Submap(from, false, this.to, this.top);
            }
            return Byte2ObjectAVLTreeMap.this.compare(from, this.from) > 0 ? new Submap(from, false, this.to, this.top) : this;
        }

        @Override
        public Byte2ObjectSortedMap<V> subMap(byte from, byte to2) {
            if (this.top && this.bottom) {
                return new Submap(from, false, to2, false);
            }
            if (!this.top) {
                byte by2 = to2 = Byte2ObjectAVLTreeMap.this.compare(to2, this.to) < 0 ? to2 : this.to;
            }
            if (!this.bottom) {
                byte by3 = from = Byte2ObjectAVLTreeMap.this.compare(from, this.from) > 0 ? from : this.from;
            }
            if (!this.top && !this.bottom && from == this.from && to2 == this.to) {
                return this;
            }
            return new Submap(from, false, to2, false);
        }

        public Entry<V> firstEntry() {
            Entry e2;
            if (Byte2ObjectAVLTreeMap.this.tree == null) {
                return null;
            }
            if (this.bottom) {
                e2 = Byte2ObjectAVLTreeMap.this.firstEntry;
            } else {
                e2 = Byte2ObjectAVLTreeMap.this.locateKey(this.from);
                if (Byte2ObjectAVLTreeMap.this.compare(e2.key, this.from) < 0) {
                    e2 = e2.next();
                }
            }
            if (e2 == null || !this.top && Byte2ObjectAVLTreeMap.this.compare(e2.key, this.to) >= 0) {
                return null;
            }
            return e2;
        }

        public Entry<V> lastEntry() {
            Entry e2;
            if (Byte2ObjectAVLTreeMap.this.tree == null) {
                return null;
            }
            if (this.top) {
                e2 = Byte2ObjectAVLTreeMap.this.lastEntry;
            } else {
                e2 = Byte2ObjectAVLTreeMap.this.locateKey(this.to);
                if (Byte2ObjectAVLTreeMap.this.compare(e2.key, this.to) >= 0) {
                    e2 = e2.prev();
                }
            }
            if (e2 == null || !this.bottom && Byte2ObjectAVLTreeMap.this.compare(e2.key, this.from) < 0) {
                return null;
            }
            return e2;
        }

        @Override
        public byte firstByteKey() {
            Entry e2 = this.firstEntry();
            if (e2 == null) {
                throw new NoSuchElementException();
            }
            return e2.key;
        }

        @Override
        public byte lastByteKey() {
            Entry e2 = this.lastEntry();
            if (e2 == null) {
                throw new NoSuchElementException();
            }
            return e2.key;
        }

        @Override
        @Deprecated
        public Byte firstKey() {
            Entry e2 = this.firstEntry();
            if (e2 == null) {
                throw new NoSuchElementException();
            }
            return e2.getKey();
        }

        @Override
        @Deprecated
        public Byte lastKey() {
            Entry e2 = this.lastEntry();
            if (e2 == null) {
                throw new NoSuchElementException();
            }
            return e2.getKey();
        }

        private final class SubmapValueIterator
        extends it.unimi.dsi.fastutil.bytes.Byte2ObjectAVLTreeMap$Submap.SubmapIterator
        implements ObjectListIterator<V> {
            private SubmapValueIterator() {
            }

            @Override
            public V next() {
                return this.nextEntry().value;
            }

            @Override
            public V previous() {
                return this.previousEntry().value;
            }

            @Override
            public void set(V v2) {
                throw new UnsupportedOperationException();
            }

            @Override
            public void add(V v2) {
                throw new UnsupportedOperationException();
            }
        }

        private final class SubmapKeyIterator
        extends it.unimi.dsi.fastutil.bytes.Byte2ObjectAVLTreeMap$Submap.SubmapIterator
        implements ByteListIterator {
            public SubmapKeyIterator() {
            }

            public SubmapKeyIterator(byte from) {
                super(from);
            }

            @Override
            public byte nextByte() {
                return this.nextEntry().key;
            }

            @Override
            public byte previousByte() {
                return this.previousEntry().key;
            }

            @Override
            public void set(byte k2) {
                throw new UnsupportedOperationException();
            }

            @Override
            public void add(byte k2) {
                throw new UnsupportedOperationException();
            }

            @Override
            public Byte next() {
                return this.nextEntry().key;
            }

            @Override
            public Byte previous() {
                return this.previousEntry().key;
            }

            @Override
            public void set(Byte ok2) {
                throw new UnsupportedOperationException();
            }

            @Override
            public void add(Byte ok2) {
                throw new UnsupportedOperationException();
            }
        }

        private class SubmapEntryIterator
        extends it.unimi.dsi.fastutil.bytes.Byte2ObjectAVLTreeMap$Submap.SubmapIterator
        implements ObjectListIterator<Byte2ObjectMap.Entry<V>> {
            SubmapEntryIterator() {
            }

            SubmapEntryIterator(byte k2) {
                super(k2);
            }

            @Override
            public Byte2ObjectMap.Entry<V> next() {
                return this.nextEntry();
            }

            @Override
            public Byte2ObjectMap.Entry<V> previous() {
                return this.previousEntry();
            }

            @Override
            public void set(Byte2ObjectMap.Entry<V> ok2) {
                throw new UnsupportedOperationException();
            }

            @Override
            public void add(Byte2ObjectMap.Entry<V> ok2) {
                throw new UnsupportedOperationException();
            }
        }

        private class SubmapIterator
        extends TreeIterator {
            SubmapIterator() {
                this.next = Submap.this.firstEntry();
            }

            /*
             * Enabled aggressive block sorting
             */
            SubmapIterator(byte k2) {
                this();
                if (this.next == null) return;
                if (!submap.bottom && submap.Byte2ObjectAVLTreeMap.this.compare(k2, this.next.key) < 0) {
                    this.prev = null;
                    return;
                }
                if (!submap.top) {
                    this.prev = submap.lastEntry();
                    if (submap.Byte2ObjectAVLTreeMap.this.compare(k2, this.prev.key) >= 0) {
                        this.next = null;
                        return;
                    }
                }
                this.next = submap.Byte2ObjectAVLTreeMap.this.locateKey(k2);
                if (submap.Byte2ObjectAVLTreeMap.this.compare(this.next.key, k2) <= 0) {
                    this.prev = this.next;
                    this.next = this.next.next();
                    return;
                }
                this.prev = this.next.prev();
            }

            @Override
            void updatePrevious() {
                this.prev = this.prev.prev();
                if (!Submap.this.bottom && this.prev != null && Byte2ObjectAVLTreeMap.this.compare(this.prev.key, Submap.this.from) < 0) {
                    this.prev = null;
                }
            }

            @Override
            void updateNext() {
                this.next = this.next.next();
                if (!Submap.this.top && this.next != null && Byte2ObjectAVLTreeMap.this.compare(this.next.key, Submap.this.to) >= 0) {
                    this.next = null;
                }
            }
        }

        private class KeySet
        extends AbstractByte2ObjectSortedMap.KeySet {
            private KeySet() {
            }

            @Override
            public ByteBidirectionalIterator iterator() {
                return new SubmapKeyIterator();
            }

            @Override
            public ByteBidirectionalIterator iterator(byte from) {
                return new SubmapKeyIterator(from);
            }
        }
    }

    private final class ValueIterator
    extends TreeIterator
    implements ObjectListIterator<V> {
        private ValueIterator() {
        }

        @Override
        public V next() {
            return this.nextEntry().value;
        }

        @Override
        public V previous() {
            return this.previousEntry().value;
        }

        @Override
        public void set(V v2) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void add(V v2) {
            throw new UnsupportedOperationException();
        }
    }

    private class KeySet
    extends AbstractByte2ObjectSortedMap.KeySet {
        private KeySet() {
        }

        @Override
        public ByteBidirectionalIterator iterator() {
            return new KeyIterator();
        }

        @Override
        public ByteBidirectionalIterator iterator(byte from) {
            return new KeyIterator(from);
        }
    }

    private final class KeyIterator
    extends TreeIterator
    implements ByteListIterator {
        public KeyIterator() {
        }

        public KeyIterator(byte k2) {
            super(k2);
        }

        @Override
        public byte nextByte() {
            return this.nextEntry().key;
        }

        @Override
        public byte previousByte() {
            return this.previousEntry().key;
        }

        @Override
        public void set(byte k2) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void add(byte k2) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Byte next() {
            return this.nextEntry().key;
        }

        @Override
        public Byte previous() {
            return this.previousEntry().key;
        }

        @Override
        public void set(Byte ok2) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void add(Byte ok2) {
            throw new UnsupportedOperationException();
        }
    }

    private class EntryIterator
    extends TreeIterator
    implements ObjectListIterator<Byte2ObjectMap.Entry<V>> {
        EntryIterator() {
        }

        EntryIterator(byte k2) {
            super(k2);
        }

        @Override
        public Byte2ObjectMap.Entry<V> next() {
            return this.nextEntry();
        }

        @Override
        public Byte2ObjectMap.Entry<V> previous() {
            return this.previousEntry();
        }

        @Override
        public void set(Byte2ObjectMap.Entry<V> ok2) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void add(Byte2ObjectMap.Entry<V> ok2) {
            throw new UnsupportedOperationException();
        }
    }

    private class TreeIterator {
        Entry<V> prev;
        Entry<V> next;
        Entry<V> curr;
        int index = 0;

        TreeIterator() {
            this.next = Byte2ObjectAVLTreeMap.this.firstEntry;
        }

        TreeIterator(byte k2) {
            this.next = Byte2ObjectAVLTreeMap.this.locateKey(k2);
            if (this.next != null) {
                if (Byte2ObjectAVLTreeMap.this.compare(this.next.key, k2) <= 0) {
                    this.prev = this.next;
                    this.next = this.next.next();
                } else {
                    this.prev = this.next.prev();
                }
            }
        }

        public boolean hasNext() {
            return this.next != null;
        }

        public boolean hasPrevious() {
            return this.prev != null;
        }

        void updateNext() {
            this.next = this.next.next();
        }

        Entry<V> nextEntry() {
            if (!this.hasNext()) {
                throw new NoSuchElementException();
            }
            this.prev = this.next;
            this.curr = this.prev;
            ++this.index;
            this.updateNext();
            return this.curr;
        }

        void updatePrevious() {
            this.prev = this.prev.prev();
        }

        Entry<V> previousEntry() {
            if (!this.hasPrevious()) {
                throw new NoSuchElementException();
            }
            this.next = this.prev;
            this.curr = this.next;
            --this.index;
            this.updatePrevious();
            return this.curr;
        }

        public int nextIndex() {
            return this.index;
        }

        public int previousIndex() {
            return this.index - 1;
        }

        public void remove() {
            if (this.curr == null) {
                throw new IllegalStateException();
            }
            if (this.curr == this.prev) {
                --this.index;
            }
            this.prev = this.curr;
            this.next = this.prev;
            this.updatePrevious();
            this.updateNext();
            Byte2ObjectAVLTreeMap.this.remove(this.curr.key);
            this.curr = null;
        }

        public int skip(int n2) {
            int i2 = n2;
            while (i2-- != 0 && this.hasNext()) {
                this.nextEntry();
            }
            return n2 - i2 - 1;
        }

        public int back(int n2) {
            int i2 = n2;
            while (i2-- != 0 && this.hasPrevious()) {
                this.previousEntry();
            }
            return n2 - i2 - 1;
        }
    }

    private static final class Entry<V>
    implements Cloneable,
    Byte2ObjectMap.Entry<V> {
        private static final int SUCC_MASK = Integer.MIN_VALUE;
        private static final int PRED_MASK = 0x40000000;
        private static final int BALANCE_MASK = 255;
        byte key;
        V value;
        Entry<V> left;
        Entry<V> right;
        int info;

        Entry() {
        }

        Entry(byte k2, V v2) {
            this.key = k2;
            this.value = v2;
            this.info = -1073741824;
        }

        Entry<V> left() {
            return (this.info & 0x40000000) != 0 ? null : this.left;
        }

        Entry<V> right() {
            return (this.info & Integer.MIN_VALUE) != 0 ? null : this.right;
        }

        boolean pred() {
            return (this.info & 0x40000000) != 0;
        }

        boolean succ() {
            return (this.info & Integer.MIN_VALUE) != 0;
        }

        void pred(boolean pred) {
            this.info = pred ? (this.info |= 0x40000000) : (this.info &= 0xBFFFFFFF);
        }

        void succ(boolean succ) {
            this.info = succ ? (this.info |= Integer.MIN_VALUE) : (this.info &= Integer.MAX_VALUE);
        }

        void pred(Entry<V> pred) {
            this.info |= 0x40000000;
            this.left = pred;
        }

        void succ(Entry<V> succ) {
            this.info |= Integer.MIN_VALUE;
            this.right = succ;
        }

        void left(Entry<V> left) {
            this.info &= 0xBFFFFFFF;
            this.left = left;
        }

        void right(Entry<V> right) {
            this.info &= Integer.MAX_VALUE;
            this.right = right;
        }

        int balance() {
            return (byte)this.info;
        }

        void balance(int level) {
            this.info &= 0xFFFFFF00;
            this.info |= level & 0xFF;
        }

        void incBalance() {
            this.info = this.info & 0xFFFFFF00 | (byte)this.info + 1 & 0xFF;
        }

        protected void decBalance() {
            this.info = this.info & 0xFFFFFF00 | (byte)this.info - 1 & 0xFF;
        }

        Entry<V> next() {
            Entry<V> next = this.right;
            if ((this.info & Integer.MIN_VALUE) == 0) {
                while ((next.info & 0x40000000) == 0) {
                    next = next.left;
                }
            }
            return next;
        }

        Entry<V> prev() {
            Entry<V> prev = this.left;
            if ((this.info & 0x40000000) == 0) {
                while ((prev.info & Integer.MIN_VALUE) == 0) {
                    prev = prev.right;
                }
            }
            return prev;
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
            V oldValue = this.value;
            this.value = value;
            return oldValue;
        }

        public Entry<V> clone() {
            Entry c2;
            try {
                c2 = (Entry)super.clone();
            }
            catch (CloneNotSupportedException cantHappen) {
                throw new InternalError();
            }
            c2.key = this.key;
            c2.value = this.value;
            c2.info = this.info;
            return c2;
        }

        @Override
        public boolean equals(Object o2) {
            if (!(o2 instanceof Map.Entry)) {
                return false;
            }
            Map.Entry e2 = (Map.Entry)o2;
            return this.key == (Byte)e2.getKey() && (this.value == null ? e2.getValue() == null : this.value.equals(e2.getValue()));
        }

        @Override
        public int hashCode() {
            return this.key ^ (this.value == null ? 0 : this.value.hashCode());
        }

        public String toString() {
            return this.key + "=>" + this.value;
        }
    }
}

