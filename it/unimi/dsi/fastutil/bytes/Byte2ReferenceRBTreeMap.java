 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.bytes.Byte2ReferenceRBTreeMap$Submap.SubmapIterator
 */
package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.bytes.AbstractByte2ReferenceSortedMap;
import it.unimi.dsi.fastutil.bytes.Byte2ReferenceMap;
import it.unimi.dsi.fastutil.bytes.Byte2ReferenceRBTreeMap;
import it.unimi.dsi.fastutil.bytes.Byte2ReferenceSortedMap;
import it.unimi.dsi.fastutil.bytes.ByteBidirectionalIterator;
import it.unimi.dsi.fastutil.bytes.ByteComparator;
import it.unimi.dsi.fastutil.bytes.ByteListIterator;
import it.unimi.dsi.fastutil.bytes.ByteSortedSet;
import it.unimi.dsi.fastutil.objects.AbstractObjectSortedSet;
import it.unimi.dsi.fastutil.objects.AbstractReferenceCollection;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import it.unimi.dsi.fastutil.objects.ReferenceCollection;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Comparator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.SortedMap;

public class Byte2ReferenceRBTreeMap<V>
extends AbstractByte2ReferenceSortedMap<V>
implements Serializable,
Cloneable {
    protected transient Entry<V> tree;
    protected int count;
    protected transient Entry<V> firstEntry;
    protected transient Entry<V> lastEntry;
    protected transient ObjectSortedSet<Byte2ReferenceMap.Entry<V>> entries;
    protected transient ByteSortedSet keys;
    protected transient ReferenceCollection<V> values;
    protected transient boolean modified;
    protected Comparator<? super Byte> storedComparator;
    protected transient ByteComparator actualComparator;
    private static final long serialVersionUID = -7046029254386353129L;
    private static final boolean ASSERTS = false;
    private transient boolean[] dirPath;
    private transient Entry<V>[] nodePath;

    public Byte2ReferenceRBTreeMap() {
        this.allocatePaths();
        this.tree = null;
        this.count = 0;
    }

    private void setActualComparator() {
        this.actualComparator = this.storedComparator == null || this.storedComparator instanceof ByteComparator ? (ByteComparator)this.storedComparator : new ByteComparator(){

            @Override
            public int compare(byte k1, byte k2) {
                return Byte2ReferenceRBTreeMap.this.storedComparator.compare((Byte)k1, (Byte)k2);
            }

            @Override
            public int compare(Byte ok1, Byte ok2) {
                return Byte2ReferenceRBTreeMap.this.storedComparator.compare(ok1, ok2);
            }
        };
    }

    public Byte2ReferenceRBTreeMap(Comparator<? super Byte> c2) {
        this();
        this.storedComparator = c2;
    }

    public Byte2ReferenceRBTreeMap(Map<? extends Byte, ? extends V> m2) {
        this();
        this.putAll((Map<Byte, ? extends V>)m2);
    }

    public Byte2ReferenceRBTreeMap(SortedMap<Byte, V> m2) {
        this(m2.comparator());
        this.putAll((Map<Byte, V>)m2);
    }

    public Byte2ReferenceRBTreeMap(Byte2ReferenceMap<? extends V> m2) {
        this();
        this.putAll(m2);
    }

    public Byte2ReferenceRBTreeMap(Byte2ReferenceSortedMap<V> m2) {
        this(m2.comparator());
        this.putAll(m2);
    }

    public Byte2ReferenceRBTreeMap(byte[] k2, V[] v2, Comparator<? super Byte> c2) {
        this(c2);
        if (k2.length != v2.length) {
            throw new IllegalArgumentException("The key array and the value array have different lengths (" + k2.length + " and " + v2.length + ")");
        }
        for (int i2 = 0; i2 < k2.length; ++i2) {
            this.put(k2[i2], v2[i2]);
        }
    }

    public Byte2ReferenceRBTreeMap(byte[] k2, V[] v2) {
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
        this.dirPath = new boolean[64];
        this.nodePath = new Entry[64];
    }

    @Override
    public V put(byte k2, V v2) {
        Entry<V> e2 = this.add(k2);
        Object oldValue = e2.value;
        e2.value = v2;
        return oldValue;
    }

    private Entry<V> add(byte k2) {
        Entry<Object> e2;
        this.modified = false;
        int maxDepth = 0;
        if (this.tree == null) {
            ++this.count;
            this.firstEntry = new Entry<Object>(k2, this.defRetValue);
            this.lastEntry = this.firstEntry;
            this.tree = this.firstEntry;
            e2 = this.firstEntry;
        } else {
            Entry<Object> p2 = this.tree;
            int i2 = 0;
            while (true) {
                int cmp;
                if ((cmp = this.compare(k2, p2.key)) == 0) {
                    while (i2-- != 0) {
                        this.nodePath[i2] = null;
                    }
                    return p2;
                }
                this.nodePath[i2] = p2;
                this.dirPath[i2++] = cmp > 0;
                if (this.dirPath[i2++]) {
                    if (p2.succ()) {
                        ++this.count;
                        e2 = new Entry<Object>(k2, this.defRetValue);
                        if (p2.right == null) {
                            this.lastEntry = e2;
                        }
                        e2.left = p2;
                        e2.right = p2.right;
                        p2.right(e2);
                        break;
                    }
                    p2 = p2.right;
                    continue;
                }
                if (p2.pred()) {
                    ++this.count;
                    e2 = new Entry<Object>(k2, this.defRetValue);
                    if (p2.left == null) {
                        this.firstEntry = e2;
                    }
                    e2.right = p2;
                    e2.left = p2.left;
                    p2.left(e2);
                    break;
                }
                p2 = p2.left;
            }
            this.modified = true;
            maxDepth = i2--;
            while (i2 > 0 && !this.nodePath[i2].black()) {
                Entry x2;
                Entry y2;
                if (!this.dirPath[i2 - 1]) {
                    y2 = this.nodePath[i2 - 1].right;
                    if (!this.nodePath[i2 - 1].succ() && !y2.black()) {
                        this.nodePath[i2].black(true);
                        y2.black(true);
                        this.nodePath[i2 - 1].black(false);
                        i2 -= 2;
                        continue;
                    }
                    if (!this.dirPath[i2]) {
                        y2 = this.nodePath[i2];
                    } else {
                        x2 = this.nodePath[i2];
                        y2 = x2.right;
                        x2.right = y2.left;
                        y2.left = x2;
                        this.nodePath[i2 - 1].left = y2;
                        if (y2.pred()) {
                            y2.pred(false);
                            x2.succ(y2);
                        }
                    }
                    x2 = this.nodePath[i2 - 1];
                    x2.black(false);
                    y2.black(true);
                    x2.left = y2.right;
                    y2.right = x2;
                    if (i2 < 2) {
                        this.tree = y2;
                    } else if (this.dirPath[i2 - 2]) {
                        this.nodePath[i2 - 2].right = y2;
                    } else {
                        this.nodePath[i2 - 2].left = y2;
                    }
                    if (!y2.succ()) break;
                    y2.succ(false);
                    x2.pred(y2);
                    break;
                }
                y2 = this.nodePath[i2 - 1].left;
                if (!this.nodePath[i2 - 1].pred() && !y2.black()) {
                    this.nodePath[i2].black(true);
                    y2.black(true);
                    this.nodePath[i2 - 1].black(false);
                    i2 -= 2;
                    continue;
                }
                if (this.dirPath[i2]) {
                    y2 = this.nodePath[i2];
                } else {
                    x2 = this.nodePath[i2];
                    y2 = x2.left;
                    x2.left = y2.right;
                    y2.right = x2;
                    this.nodePath[i2 - 1].right = y2;
                    if (y2.succ()) {
                        y2.succ(false);
                        x2.pred(y2);
                    }
                }
                x2 = this.nodePath[i2 - 1];
                x2.black(false);
                y2.black(true);
                x2.right = y2.left;
                y2.left = x2;
                if (i2 < 2) {
                    this.tree = y2;
                } else if (this.dirPath[i2 - 2]) {
                    this.nodePath[i2 - 2].right = y2;
                } else {
                    this.nodePath[i2 - 2].left = y2;
                }
                if (!y2.pred()) break;
                y2.pred(false);
                x2.succ(y2);
                break;
            }
        }
        this.tree.black(true);
        while (maxDepth-- != 0) {
            this.nodePath[maxDepth] = null;
        }
        return e2;
    }

    /*
     * Unable to fully structure code
     * Enabled aggressive block sorting
     * Lifted jumps to return sites
     */
    @Override
    public V remove(byte k) {
        block69: {
            block66: {
                block68: {
                    block64: {
                        block67: {
                            block65: {
                                block62: {
                                    block63: {
                                        this.modified = false;
                                        if (this.tree == null) {
                                            return (V)this.defRetValue;
                                        }
                                        p = this.tree;
                                        i = 0;
                                        kk = k;
                                        do lbl-1000:
                                        // 3 sources

                                        {
                                            if ((cmp = this.compare(kk, p.key)) == 0) {
                                                if (p.left != null) break block62;
                                                break block63;
                                            }
                                            this.dirPath[i] = cmp > 0;
                                            this.nodePath[i] = p;
                                            if (!this.dirPath[i++]) continue;
                                            if ((p = p.right()) != null) ** GOTO lbl-1000
                                            while (i-- != 0) {
                                                this.nodePath[i] = null;
                                            }
                                            return (V)this.defRetValue;
                                        } while ((p = p.left()) != null);
                                        while (i-- != 0) {
                                            this.nodePath[i] = null;
                                        }
                                        return (V)this.defRetValue;
                                    }
                                    this.firstEntry = p.next();
                                }
                                if (p.right == null) {
                                    this.lastEntry = p.prev();
                                }
                                if (!p.succ()) break block65;
                                if (p.pred()) {
                                    if (i == 0) {
                                        this.tree = p.left;
                                    } else if (this.dirPath[i - 1]) {
                                        this.nodePath[i - 1].succ(p.right);
                                    } else {
                                        this.nodePath[i - 1].pred(p.left);
                                    }
                                } else {
                                    p.prev().right = p.right;
                                    if (i == 0) {
                                        this.tree = p.left;
                                    } else if (this.dirPath[i - 1]) {
                                        this.nodePath[i - 1].right = p.left;
                                    } else {
                                        this.nodePath[i - 1].left = p.left;
                                    }
                                }
                                break block66;
                            }
                            r = p.right;
                            if (!r.pred()) break block67;
                            r.left = p.left;
                            r.pred(p.pred());
                            if (!r.pred()) {
                                r.prev().right = r;
                            }
                            if (i == 0) {
                                this.tree = r;
                            } else if (this.dirPath[i - 1]) {
                                this.nodePath[i - 1].right = r;
                            } else {
                                this.nodePath[i - 1].left = r;
                            }
                            color = r.black();
                            r.black(p.black());
                            p.black(color);
                            this.dirPath[i] = true;
                            this.nodePath[i++] = r;
                            break block66;
                        }
                        j = i++;
                        while (true) {
                            this.dirPath[i] = false;
                            this.nodePath[i++] = r;
                            s = r.left;
                            if (s.pred()) {
                                this.dirPath[j] = true;
                                this.nodePath[j] = s;
                                if (s.succ()) {
                                    break;
                                }
                                break block64;
                            }
                            r = s;
                        }
                        r.pred(s);
                        break block68;
                    }
                    r.left = s.right;
                }
                s.left = p.left;
                if (!p.pred()) {
                    p.prev().right = s;
                    s.pred(false);
                }
                s.right(p.right);
                color = s.black();
                s.black(p.black());
                p.black(color);
                if (j == 0) {
                    this.tree = s;
                } else if (this.dirPath[j - 1]) {
                    this.nodePath[j - 1].right = s;
                } else {
                    this.nodePath[j - 1].left = s;
                }
            }
            maxDepth = i;
            if (!p.black()) break block69;
            while (i > 0) {
                if (this.dirPath[i - 1] && !this.nodePath[i - 1].succ() || !this.dirPath[i - 1] && !this.nodePath[i - 1].pred()) {
                    v0 = x = this.dirPath[i - 1] != false ? this.nodePath[i - 1].right : this.nodePath[i - 1].left;
                    if (!x.black()) {
                        x.black(true);
                        break;
                    }
                }
                if (this.dirPath[i - 1]) ** GOTO lbl161
                w = this.nodePath[i - 1].right;
                if (!w.black()) {
                    w.black(true);
                    this.nodePath[i - 1].black(false);
                    this.nodePath[i - 1].right = w.left;
                    w.left = this.nodePath[i - 1];
                    if (i < 2) {
                        this.tree = w;
                    } else if (this.dirPath[i - 2]) {
                        this.nodePath[i - 2].right = w;
                    } else {
                        this.nodePath[i - 2].left = w;
                    }
                    this.nodePath[i] = this.nodePath[i - 1];
                    this.dirPath[i] = false;
                    this.nodePath[i - 1] = w;
                    if (maxDepth == i++) {
                        ++maxDepth;
                    }
                    w = this.nodePath[i - 1].right;
                }
                if ((w.pred() || w.left.black()) && (w.succ() || w.right.black())) {
                    w.black(false);
                } else {
                    if (w.succ() || w.right.black()) {
                        y = w.left;
                        y.black(true);
                        w.black(false);
                        w.left = y.right;
                        y.right = w;
                        this.nodePath[i - 1].right = y;
                        w = this.nodePath[i - 1].right;
                        if (w.succ()) {
                            w.succ(false);
                            w.right.pred(w);
                        }
                    }
                    w.black(this.nodePath[i - 1].black());
                    this.nodePath[i - 1].black(true);
                    w.right.black(true);
                    this.nodePath[i - 1].right = w.left;
                    w.left = this.nodePath[i - 1];
                    if (i < 2) {
                        this.tree = w;
                    } else if (this.dirPath[i - 2]) {
                        this.nodePath[i - 2].right = w;
                    } else {
                        this.nodePath[i - 2].left = w;
                    }
                    if (!w.pred()) break;
                    w.pred(false);
                    this.nodePath[i - 1].succ(w);
                    break;
lbl161:
                    // 1 sources

                    w = this.nodePath[i - 1].left;
                    if (!w.black()) {
                        w.black(true);
                        this.nodePath[i - 1].black(false);
                        this.nodePath[i - 1].left = w.right;
                        w.right = this.nodePath[i - 1];
                        if (i < 2) {
                            this.tree = w;
                        } else if (this.dirPath[i - 2]) {
                            this.nodePath[i - 2].right = w;
                        } else {
                            this.nodePath[i - 2].left = w;
                        }
                        this.nodePath[i] = this.nodePath[i - 1];
                        this.dirPath[i] = true;
                        this.nodePath[i - 1] = w;
                        if (maxDepth == i++) {
                            ++maxDepth;
                        }
                        w = this.nodePath[i - 1].left;
                    }
                    if ((w.pred() || w.left.black()) && (w.succ() || w.right.black())) {
                        w.black(false);
                    } else {
                        if (w.pred() || w.left.black()) {
                            y = w.right;
                            y.black(true);
                            w.black(false);
                            w.right = y.left;
                            y.left = w;
                            this.nodePath[i - 1].left = y;
                            w = this.nodePath[i - 1].left;
                            if (w.pred()) {
                                w.pred(false);
                                w.left.succ(w);
                            }
                        }
                        w.black(this.nodePath[i - 1].black());
                        this.nodePath[i - 1].black(true);
                        w.left.black(true);
                        this.nodePath[i - 1].left = w.right;
                        w.right = this.nodePath[i - 1];
                        if (i < 2) {
                            this.tree = w;
                        } else if (this.dirPath[i - 2]) {
                            this.nodePath[i - 2].right = w;
                        } else {
                            this.nodePath[i - 2].left = w;
                        }
                        if (!w.succ()) break;
                        w.succ(false);
                        this.nodePath[i - 1].pred(w);
                        break;
                    }
                }
                --i;
            }
            if (this.tree != null) {
                this.tree.black(true);
            }
        }
        this.modified = true;
        --this.count;
        while (maxDepth-- != 0) {
            this.nodePath[maxDepth] = null;
        }
        return p.value;
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
            if (ev2 != v2) continue;
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
    public ObjectSortedSet<Byte2ReferenceMap.Entry<V>> byte2ReferenceEntrySet() {
        if (this.entries == null) {
            this.entries = new AbstractObjectSortedSet<Byte2ReferenceMap.Entry<V>>(){
                final Comparator<? super Byte2ReferenceMap.Entry<V>> comparator = new Comparator<Byte2ReferenceMap.Entry<V>>(){

                    @Override
                    public int compare(Byte2ReferenceMap.Entry<V> x2, Byte2ReferenceMap.Entry<V> y2) {
                        return Byte2ReferenceRBTreeMap.this.actualComparator.compare(x2.getByteKey(), y2.getByteKey());
                    }
                };

                @Override
                public Comparator<? super Byte2ReferenceMap.Entry<V>> comparator() {
                    return this.comparator;
                }

                @Override
                public ObjectBidirectionalIterator<Byte2ReferenceMap.Entry<V>> iterator() {
                    return new EntryIterator();
                }

                @Override
                public ObjectBidirectionalIterator<Byte2ReferenceMap.Entry<V>> iterator(Byte2ReferenceMap.Entry<V> from) {
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
                    Entry f2 = Byte2ReferenceRBTreeMap.this.findKey((Byte)e2.getKey());
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
                    Entry f2 = Byte2ReferenceRBTreeMap.this.findKey((Byte)e2.getKey());
                    if (f2 != null) {
                        Byte2ReferenceRBTreeMap.this.remove(f2.key);
                    }
                    return f2 != null;
                }

                @Override
                public int size() {
                    return Byte2ReferenceRBTreeMap.this.count;
                }

                @Override
                public void clear() {
                    Byte2ReferenceRBTreeMap.this.clear();
                }

                @Override
                public Byte2ReferenceMap.Entry<V> first() {
                    return Byte2ReferenceRBTreeMap.this.firstEntry;
                }

                @Override
                public Byte2ReferenceMap.Entry<V> last() {
                    return Byte2ReferenceRBTreeMap.this.lastEntry;
                }

                @Override
                public ObjectSortedSet<Byte2ReferenceMap.Entry<V>> subSet(Byte2ReferenceMap.Entry<V> from, Byte2ReferenceMap.Entry<V> to2) {
                    return Byte2ReferenceRBTreeMap.this.subMap(from.getByteKey(), to2.getByteKey()).byte2ReferenceEntrySet();
                }

                @Override
                public ObjectSortedSet<Byte2ReferenceMap.Entry<V>> headSet(Byte2ReferenceMap.Entry<V> to2) {
                    return Byte2ReferenceRBTreeMap.this.headMap(to2.getByteKey()).byte2ReferenceEntrySet();
                }

                @Override
                public ObjectSortedSet<Byte2ReferenceMap.Entry<V>> tailSet(Byte2ReferenceMap.Entry<V> from) {
                    return Byte2ReferenceRBTreeMap.this.tailMap(from.getByteKey()).byte2ReferenceEntrySet();
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
    public ReferenceCollection<V> values() {
        if (this.values == null) {
            this.values = new AbstractReferenceCollection<V>(){

                @Override
                public ObjectIterator<V> iterator() {
                    return new ValueIterator();
                }

                @Override
                public boolean contains(Object k2) {
                    return Byte2ReferenceRBTreeMap.this.containsValue(k2);
                }

                @Override
                public int size() {
                    return Byte2ReferenceRBTreeMap.this.count;
                }

                @Override
                public void clear() {
                    Byte2ReferenceRBTreeMap.this.clear();
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
    public Byte2ReferenceSortedMap<V> headMap(byte to2) {
        return new Submap(0, true, to2, false);
    }

    @Override
    public Byte2ReferenceSortedMap<V> tailMap(byte from) {
        return new Submap(from, false, 0, true);
    }

    @Override
    public Byte2ReferenceSortedMap<V> subMap(byte from, byte to2) {
        return new Submap(from, false, to2, false);
    }

    public Byte2ReferenceRBTreeMap<V> clone() {
        Byte2ReferenceRBTreeMap c2;
        try {
            c2 = (Byte2ReferenceRBTreeMap)super.clone();
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
            top.black(true);
            return top;
        }
        if (n2 == 2) {
            Entry<Object> top = new Entry<Object>(s2.readByte(), s2.readObject());
            top.black(true);
            top.right(new Entry<Object>(s2.readByte(), s2.readObject()));
            top.right.pred(top);
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
        top.black(true);
        top.right(this.readTree(s2, rightN, top, succ));
        if (n2 + 2 == (n2 + 2 & -(n2 + 2))) {
            top.right.black(false);
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

    private void checkNodePath() {
    }

    private static <V> int checkTree(Entry<V> e2, int d2, int D) {
        return 0;
    }

    private final class Submap
    extends AbstractByte2ReferenceSortedMap<V>
    implements Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        byte from;
        byte to;
        boolean bottom;
        boolean top;
        protected transient ObjectSortedSet<Byte2ReferenceMap.Entry<V>> entries;
        protected transient ByteSortedSet keys;
        protected transient ReferenceCollection<V> values;

        public Submap(byte from, boolean bottom, byte to2, boolean top) {
            if (!bottom && !top && Byte2ReferenceRBTreeMap.this.compare(from, to2) > 0) {
                throw new IllegalArgumentException("Start key (" + from + ") is larger than end key (" + to2 + ")");
            }
            this.from = from;
            this.bottom = bottom;
            this.to = to2;
            this.top = top;
            this.defRetValue = Byte2ReferenceRBTreeMap.this.defRetValue;
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
            return !(!this.bottom && Byte2ReferenceRBTreeMap.this.compare(k2, this.from) < 0 || !this.top && Byte2ReferenceRBTreeMap.this.compare(k2, this.to) >= 0);
        }

        @Override
        public ObjectSortedSet<Byte2ReferenceMap.Entry<V>> byte2ReferenceEntrySet() {
            if (this.entries == null) {
                this.entries = new AbstractObjectSortedSet<Byte2ReferenceMap.Entry<V>>(){

                    @Override
                    public ObjectBidirectionalIterator<Byte2ReferenceMap.Entry<V>> iterator() {
                        return new SubmapEntryIterator();
                    }

                    @Override
                    public ObjectBidirectionalIterator<Byte2ReferenceMap.Entry<V>> iterator(Byte2ReferenceMap.Entry<V> from) {
                        return new SubmapEntryIterator(from.getByteKey());
                    }

                    @Override
                    public Comparator<? super Byte2ReferenceMap.Entry<V>> comparator() {
                        return Byte2ReferenceRBTreeMap.this.byte2ReferenceEntrySet().comparator();
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
                        Entry f2 = Byte2ReferenceRBTreeMap.this.findKey((Byte)e2.getKey());
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
                        Entry f2 = Byte2ReferenceRBTreeMap.this.findKey((Byte)e2.getKey());
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
                    public Byte2ReferenceMap.Entry<V> first() {
                        return Submap.this.firstEntry();
                    }

                    @Override
                    public Byte2ReferenceMap.Entry<V> last() {
                        return Submap.this.lastEntry();
                    }

                    @Override
                    public ObjectSortedSet<Byte2ReferenceMap.Entry<V>> subSet(Byte2ReferenceMap.Entry<V> from, Byte2ReferenceMap.Entry<V> to2) {
                        return Submap.this.subMap(from.getByteKey(), to2.getByteKey()).byte2ReferenceEntrySet();
                    }

                    @Override
                    public ObjectSortedSet<Byte2ReferenceMap.Entry<V>> headSet(Byte2ReferenceMap.Entry<V> to2) {
                        return Submap.this.headMap(to2.getByteKey()).byte2ReferenceEntrySet();
                    }

                    @Override
                    public ObjectSortedSet<Byte2ReferenceMap.Entry<V>> tailSet(Byte2ReferenceMap.Entry<V> from) {
                        return Submap.this.tailMap(from.getByteKey()).byte2ReferenceEntrySet();
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
        public ReferenceCollection<V> values() {
            if (this.values == null) {
                this.values = new AbstractReferenceCollection<V>(){

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
            return this.in(k2) && Byte2ReferenceRBTreeMap.this.containsKey(k2);
        }

        @Override
        public boolean containsValue(Object v2) {
            SubmapIterator i2 = new SubmapIterator();
            while (i2.hasNext()) {
                Object ev2 = i2.nextEntry().value;
                if (ev2 != v2) continue;
                return true;
            }
            return false;
        }

        @Override
        public V get(byte k2) {
            Entry e2;
            byte kk2 = k2;
            return this.in(kk2) && (e2 = Byte2ReferenceRBTreeMap.this.findKey(kk2)) != null ? e2.value : this.defRetValue;
        }

        @Override
        public V put(byte k2, V v2) {
            Byte2ReferenceRBTreeMap.this.modified = false;
            if (!this.in(k2)) {
                throw new IllegalArgumentException("Key (" + k2 + ") out of range [" + (this.bottom ? "-" : String.valueOf(this.from)) + ", " + (this.top ? "-" : String.valueOf(this.to)) + ")");
            }
            Object oldValue = Byte2ReferenceRBTreeMap.this.put(k2, v2);
            return Byte2ReferenceRBTreeMap.this.modified ? this.defRetValue : oldValue;
        }

        @Override
        @Deprecated
        public V put(Byte ok2, V ov) {
            Object oldValue = this.put((byte)ok2, (V)ov);
            return Byte2ReferenceRBTreeMap.this.modified ? this.defRetValue : oldValue;
        }

        @Override
        public V remove(byte k2) {
            Byte2ReferenceRBTreeMap.this.modified = false;
            if (!this.in(k2)) {
                return this.defRetValue;
            }
            Object oldValue = Byte2ReferenceRBTreeMap.this.remove(k2);
            return Byte2ReferenceRBTreeMap.this.modified ? oldValue : this.defRetValue;
        }

        @Override
        @Deprecated
        public V remove(Object ok2) {
            Object oldValue = this.remove((Byte)ok2);
            return Byte2ReferenceRBTreeMap.this.modified ? oldValue : this.defRetValue;
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
            return Byte2ReferenceRBTreeMap.this.actualComparator;
        }

        @Override
        public Byte2ReferenceSortedMap<V> headMap(byte to2) {
            if (this.top) {
                return new Submap(this.from, this.bottom, to2, false);
            }
            return Byte2ReferenceRBTreeMap.this.compare(to2, this.to) < 0 ? new Submap(this.from, this.bottom, to2, false) : this;
        }

        @Override
        public Byte2ReferenceSortedMap<V> tailMap(byte from) {
            if (this.bottom) {
                return new Submap(from, false, this.to, this.top);
            }
            return Byte2ReferenceRBTreeMap.this.compare(from, this.from) > 0 ? new Submap(from, false, this.to, this.top) : this;
        }

        @Override
        public Byte2ReferenceSortedMap<V> subMap(byte from, byte to2) {
            if (this.top && this.bottom) {
                return new Submap(from, false, to2, false);
            }
            if (!this.top) {
                byte by2 = to2 = Byte2ReferenceRBTreeMap.this.compare(to2, this.to) < 0 ? to2 : this.to;
            }
            if (!this.bottom) {
                byte by3 = from = Byte2ReferenceRBTreeMap.this.compare(from, this.from) > 0 ? from : this.from;
            }
            if (!this.top && !this.bottom && from == this.from && to2 == this.to) {
                return this;
            }
            return new Submap(from, false, to2, false);
        }

        public Entry<V> firstEntry() {
            Entry e2;
            if (Byte2ReferenceRBTreeMap.this.tree == null) {
                return null;
            }
            if (this.bottom) {
                e2 = Byte2ReferenceRBTreeMap.this.firstEntry;
            } else {
                e2 = Byte2ReferenceRBTreeMap.this.locateKey(this.from);
                if (Byte2ReferenceRBTreeMap.this.compare(e2.key, this.from) < 0) {
                    e2 = e2.next();
                }
            }
            if (e2 == null || !this.top && Byte2ReferenceRBTreeMap.this.compare(e2.key, this.to) >= 0) {
                return null;
            }
            return e2;
        }

        public Entry<V> lastEntry() {
            Entry e2;
            if (Byte2ReferenceRBTreeMap.this.tree == null) {
                return null;
            }
            if (this.top) {
                e2 = Byte2ReferenceRBTreeMap.this.lastEntry;
            } else {
                e2 = Byte2ReferenceRBTreeMap.this.locateKey(this.to);
                if (Byte2ReferenceRBTreeMap.this.compare(e2.key, this.to) >= 0) {
                    e2 = e2.prev();
                }
            }
            if (e2 == null || !this.bottom && Byte2ReferenceRBTreeMap.this.compare(e2.key, this.from) < 0) {
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
        extends it.unimi.dsi.fastutil.bytes.Byte2ReferenceRBTreeMap$Submap.SubmapIterator
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
        extends it.unimi.dsi.fastutil.bytes.Byte2ReferenceRBTreeMap$Submap.SubmapIterator
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
        extends it.unimi.dsi.fastutil.bytes.Byte2ReferenceRBTreeMap$Submap.SubmapIterator
        implements ObjectListIterator<Byte2ReferenceMap.Entry<V>> {
            SubmapEntryIterator() {
            }

            SubmapEntryIterator(byte k2) {
                super(k2);
            }

            @Override
            public Byte2ReferenceMap.Entry<V> next() {
                return this.nextEntry();
            }

            @Override
            public Byte2ReferenceMap.Entry<V> previous() {
                return this.previousEntry();
            }

            @Override
            public void set(Byte2ReferenceMap.Entry<V> ok2) {
                throw new UnsupportedOperationException();
            }

            @Override
            public void add(Byte2ReferenceMap.Entry<V> ok2) {
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
                if (!submap.bottom && submap.Byte2ReferenceRBTreeMap.this.compare(k2, this.next.key) < 0) {
                    this.prev = null;
                    return;
                }
                if (!submap.top) {
                    this.prev = submap.lastEntry();
                    if (submap.Byte2ReferenceRBTreeMap.this.compare(k2, this.prev.key) >= 0) {
                        this.next = null;
                        return;
                    }
                }
                this.next = submap.Byte2ReferenceRBTreeMap.this.locateKey(k2);
                if (submap.Byte2ReferenceRBTreeMap.this.compare(this.next.key, k2) <= 0) {
                    this.prev = this.next;
                    this.next = this.next.next();
                    return;
                }
                this.prev = this.next.prev();
            }

            @Override
            void updatePrevious() {
                this.prev = this.prev.prev();
                if (!Submap.this.bottom && this.prev != null && Byte2ReferenceRBTreeMap.this.compare(this.prev.key, Submap.this.from) < 0) {
                    this.prev = null;
                }
            }

            @Override
            void updateNext() {
                this.next = this.next.next();
                if (!Submap.this.top && this.next != null && Byte2ReferenceRBTreeMap.this.compare(this.next.key, Submap.this.to) >= 0) {
                    this.next = null;
                }
            }
        }

        private class KeySet
        extends AbstractByte2ReferenceSortedMap.KeySet {
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
    extends AbstractByte2ReferenceSortedMap.KeySet {
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
    implements ObjectListIterator<Byte2ReferenceMap.Entry<V>> {
        EntryIterator() {
        }

        EntryIterator(byte k2) {
            super(k2);
        }

        @Override
        public Byte2ReferenceMap.Entry<V> next() {
            return this.nextEntry();
        }

        @Override
        public Byte2ReferenceMap.Entry<V> previous() {
            return this.previousEntry();
        }

        @Override
        public void set(Byte2ReferenceMap.Entry<V> ok2) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void add(Byte2ReferenceMap.Entry<V> ok2) {
            throw new UnsupportedOperationException();
        }
    }

    private class TreeIterator {
        Entry<V> prev;
        Entry<V> next;
        Entry<V> curr;
        int index = 0;

        TreeIterator() {
            this.next = Byte2ReferenceRBTreeMap.this.firstEntry;
        }

        TreeIterator(byte k2) {
            this.next = Byte2ReferenceRBTreeMap.this.locateKey(k2);
            if (this.next != null) {
                if (Byte2ReferenceRBTreeMap.this.compare(this.next.key, k2) <= 0) {
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
            Byte2ReferenceRBTreeMap.this.remove(this.curr.key);
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
    Byte2ReferenceMap.Entry<V> {
        private static final int BLACK_MASK = 1;
        private static final int SUCC_MASK = Integer.MIN_VALUE;
        private static final int PRED_MASK = 0x40000000;
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

        boolean black() {
            return (this.info & 1) != 0;
        }

        void black(boolean black) {
            this.info = black ? (this.info |= 1) : (this.info &= 0xFFFFFFFE);
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
            return this.key == (Byte)e2.getKey() && this.value == e2.getValue();
        }

        @Override
        public int hashCode() {
            return this.key ^ (this.value == null ? 0 : System.identityHashCode(this.value));
        }

        public String toString() {
            return this.key + "=>" + this.value;
        }
    }
}

