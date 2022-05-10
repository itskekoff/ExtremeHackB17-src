package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.bytes.AbstractByte2CharSortedMap;
import it.unimi.dsi.fastutil.bytes.Byte2CharMap;
import it.unimi.dsi.fastutil.bytes.Byte2CharSortedMap;
import it.unimi.dsi.fastutil.bytes.ByteBidirectionalIterator;
import it.unimi.dsi.fastutil.bytes.ByteComparator;
import it.unimi.dsi.fastutil.bytes.ByteListIterator;
import it.unimi.dsi.fastutil.bytes.ByteSortedSet;
import it.unimi.dsi.fastutil.chars.AbstractCharCollection;
import it.unimi.dsi.fastutil.chars.CharCollection;
import it.unimi.dsi.fastutil.chars.CharIterator;
import it.unimi.dsi.fastutil.chars.CharListIterator;
import it.unimi.dsi.fastutil.objects.AbstractObjectSortedSet;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
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

public class Byte2CharRBTreeMap
extends AbstractByte2CharSortedMap
implements Serializable,
Cloneable {
    protected transient Entry tree;
    protected int count;
    protected transient Entry firstEntry;
    protected transient Entry lastEntry;
    protected transient ObjectSortedSet<Byte2CharMap.Entry> entries;
    protected transient ByteSortedSet keys;
    protected transient CharCollection values;
    protected transient boolean modified;
    protected Comparator<? super Byte> storedComparator;
    protected transient ByteComparator actualComparator;
    private static final long serialVersionUID = -7046029254386353129L;
    private static final boolean ASSERTS = false;
    private transient boolean[] dirPath;
    private transient Entry[] nodePath;

    public Byte2CharRBTreeMap() {
        this.allocatePaths();
        this.tree = null;
        this.count = 0;
    }

    private void setActualComparator() {
        this.actualComparator = this.storedComparator == null || this.storedComparator instanceof ByteComparator ? (ByteComparator)this.storedComparator : new ByteComparator(){

            @Override
            public int compare(byte k1, byte k2) {
                return Byte2CharRBTreeMap.this.storedComparator.compare((Byte)k1, (Byte)k2);
            }

            @Override
            public int compare(Byte ok1, Byte ok2) {
                return Byte2CharRBTreeMap.this.storedComparator.compare(ok1, ok2);
            }
        };
    }

    public Byte2CharRBTreeMap(Comparator<? super Byte> c2) {
        this();
        this.storedComparator = c2;
        this.setActualComparator();
    }

    public Byte2CharRBTreeMap(Map<? extends Byte, ? extends Character> m2) {
        this();
        this.putAll(m2);
    }

    public Byte2CharRBTreeMap(SortedMap<Byte, Character> m2) {
        this(m2.comparator());
        this.putAll((Map<? extends Byte, ? extends Character>)m2);
    }

    public Byte2CharRBTreeMap(Byte2CharMap m2) {
        this();
        this.putAll(m2);
    }

    public Byte2CharRBTreeMap(Byte2CharSortedMap m2) {
        this(m2.comparator());
        this.putAll(m2);
    }

    public Byte2CharRBTreeMap(byte[] k2, char[] v2, Comparator<? super Byte> c2) {
        this(c2);
        if (k2.length != v2.length) {
            throw new IllegalArgumentException("The key array and the value array have different lengths (" + k2.length + " and " + v2.length + ")");
        }
        for (int i2 = 0; i2 < k2.length; ++i2) {
            this.put(k2[i2], v2[i2]);
        }
    }

    public Byte2CharRBTreeMap(byte[] k2, char[] v2) {
        this(k2, v2, null);
    }

    final int compare(byte k1, byte k2) {
        return this.actualComparator == null ? Byte.compare(k1, k2) : this.actualComparator.compare(k1, k2);
    }

    final Entry findKey(byte k2) {
        int cmp;
        Entry e2 = this.tree;
        while (e2 != null && (cmp = this.compare(k2, e2.key)) != 0) {
            e2 = cmp < 0 ? e2.left() : e2.right();
        }
        return e2;
    }

    final Entry locateKey(byte k2) {
        Entry e2 = this.tree;
        Entry last = this.tree;
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

    public char addTo(byte k2, char incr) {
        Entry e2 = this.add(k2);
        char oldValue = e2.value;
        e2.value = (char)(e2.value + incr);
        return oldValue;
    }

    @Override
    public char put(byte k2, char v2) {
        Entry e2 = this.add(k2);
        char oldValue = e2.value;
        e2.value = v2;
        return oldValue;
    }

    private Entry add(byte k2) {
        Entry e2;
        this.modified = false;
        int maxDepth = 0;
        if (this.tree == null) {
            ++this.count;
            this.lastEntry = this.firstEntry = new Entry(k2, this.defRetValue);
            this.tree = this.firstEntry;
            e2 = this.firstEntry;
        } else {
            Entry p2 = this.tree;
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
                        e2 = new Entry(k2, this.defRetValue);
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
                    e2 = new Entry(k2, this.defRetValue);
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
    public char remove(byte k) {
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
                                            return this.defRetValue;
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
                                            return this.defRetValue;
                                        } while ((p = p.left()) != null);
                                        while (i-- != 0) {
                                            this.nodePath[i] = null;
                                        }
                                        return this.defRetValue;
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
                if (this.dirPath[i - 1]) ** GOTO lbl160
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
                        w = this.nodePath[i - 1].right = y;
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
lbl160:
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
                            w = this.nodePath[i - 1].left = y;
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
    public Character put(Byte ok2, Character ov) {
        char oldValue = this.put((byte)ok2, ov.charValue());
        return this.modified ? null : Character.valueOf(oldValue);
    }

    @Override
    @Deprecated
    public Character remove(Object ok2) {
        char oldValue = this.remove((Byte)ok2);
        return this.modified ? Character.valueOf(oldValue) : null;
    }

    @Override
    public boolean containsValue(char v2) {
        ValueIterator i2 = new ValueIterator();
        int j2 = this.count;
        while (j2-- != 0) {
            char ev2 = i2.nextChar();
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
    public char get(byte k2) {
        Entry e2 = this.findKey(k2);
        return e2 == null ? this.defRetValue : e2.value;
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
    public ObjectSortedSet<Byte2CharMap.Entry> byte2CharEntrySet() {
        if (this.entries == null) {
            this.entries = new AbstractObjectSortedSet<Byte2CharMap.Entry>(){
                final Comparator<? super Byte2CharMap.Entry> comparator = new Comparator<Byte2CharMap.Entry>(){

                    @Override
                    public int compare(Byte2CharMap.Entry x2, Byte2CharMap.Entry y2) {
                        return Byte2CharRBTreeMap.this.actualComparator.compare(x2.getByteKey(), y2.getByteKey());
                    }
                };

                @Override
                public Comparator<? super Byte2CharMap.Entry> comparator() {
                    return this.comparator;
                }

                @Override
                public ObjectBidirectionalIterator<Byte2CharMap.Entry> iterator() {
                    return new EntryIterator();
                }

                @Override
                public ObjectBidirectionalIterator<Byte2CharMap.Entry> iterator(Byte2CharMap.Entry from) {
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
                    if (e2.getValue() == null || !(e2.getValue() instanceof Character)) {
                        return false;
                    }
                    Entry f2 = Byte2CharRBTreeMap.this.findKey((Byte)e2.getKey());
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
                    if (e2.getValue() == null || !(e2.getValue() instanceof Character)) {
                        return false;
                    }
                    Entry f2 = Byte2CharRBTreeMap.this.findKey((Byte)e2.getKey());
                    if (f2 != null) {
                        Byte2CharRBTreeMap.this.remove(f2.key);
                    }
                    return f2 != null;
                }

                @Override
                public int size() {
                    return Byte2CharRBTreeMap.this.count;
                }

                @Override
                public void clear() {
                    Byte2CharRBTreeMap.this.clear();
                }

                @Override
                public Byte2CharMap.Entry first() {
                    return Byte2CharRBTreeMap.this.firstEntry;
                }

                @Override
                public Byte2CharMap.Entry last() {
                    return Byte2CharRBTreeMap.this.lastEntry;
                }

                @Override
                public ObjectSortedSet<Byte2CharMap.Entry> subSet(Byte2CharMap.Entry from, Byte2CharMap.Entry to2) {
                    return Byte2CharRBTreeMap.this.subMap(from.getByteKey(), to2.getByteKey()).byte2CharEntrySet();
                }

                @Override
                public ObjectSortedSet<Byte2CharMap.Entry> headSet(Byte2CharMap.Entry to2) {
                    return Byte2CharRBTreeMap.this.headMap(to2.getByteKey()).byte2CharEntrySet();
                }

                @Override
                public ObjectSortedSet<Byte2CharMap.Entry> tailSet(Byte2CharMap.Entry from) {
                    return Byte2CharRBTreeMap.this.tailMap(from.getByteKey()).byte2CharEntrySet();
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
    public CharCollection values() {
        if (this.values == null) {
            this.values = new AbstractCharCollection(){

                @Override
                public CharIterator iterator() {
                    return new ValueIterator();
                }

                @Override
                public boolean contains(char k2) {
                    return Byte2CharRBTreeMap.this.containsValue(k2);
                }

                @Override
                public int size() {
                    return Byte2CharRBTreeMap.this.count;
                }

                @Override
                public void clear() {
                    Byte2CharRBTreeMap.this.clear();
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
    public Byte2CharSortedMap headMap(byte to2) {
        return new Submap(0, true, to2, false);
    }

    @Override
    public Byte2CharSortedMap tailMap(byte from) {
        return new Submap(from, false, 0, true);
    }

    @Override
    public Byte2CharSortedMap subMap(byte from, byte to2) {
        return new Submap(from, false, to2, false);
    }

    public Byte2CharRBTreeMap clone() {
        Byte2CharRBTreeMap c2;
        try {
            c2 = (Byte2CharRBTreeMap)super.clone();
        }
        catch (CloneNotSupportedException cantHappen) {
            throw new InternalError();
        }
        c2.keys = null;
        c2.values = null;
        c2.entries = null;
        c2.allocatePaths();
        if (this.count != 0) {
            Entry rp2 = new Entry();
            Entry rq = new Entry();
            Entry p2 = rp2;
            rp2.left(this.tree);
            Entry q2 = rq;
            rq.pred(null);
            while (true) {
                Entry e2;
                if (!p2.pred()) {
                    e2 = p2.left.clone();
                    e2.pred(q2.left);
                    e2.succ(q2);
                    q2.left(e2);
                    p2 = p2.left;
                    q2 = q2.left;
                } else {
                    while (p2.succ()) {
                        p2 = p2.right;
                        if (p2 == null) {
                            q2.right = null;
                            c2.firstEntry = c2.tree = rq.left;
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
                e2.succ(q2.right);
                e2.pred(q2);
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
            s2.writeChar(e2.value);
        }
    }

    private Entry readTree(ObjectInputStream s2, int n2, Entry pred, Entry succ) throws IOException, ClassNotFoundException {
        if (n2 == 1) {
            Entry top = new Entry(s2.readByte(), s2.readChar());
            top.pred(pred);
            top.succ(succ);
            top.black(true);
            return top;
        }
        if (n2 == 2) {
            Entry top = new Entry(s2.readByte(), s2.readChar());
            top.black(true);
            top.right(new Entry(s2.readByte(), s2.readChar()));
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
        top.value = s2.readChar();
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
            Entry e2 = this.tree = this.readTree(s2, this.count, null, null);
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

    private static int checkTree(Entry e2, int d2, int D) {
        return 0;
    }

    private final class Submap
    extends AbstractByte2CharSortedMap
    implements Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        byte from;
        byte to;
        boolean bottom;
        boolean top;
        protected transient ObjectSortedSet<Byte2CharMap.Entry> entries;
        protected transient ByteSortedSet keys;
        protected transient CharCollection values;

        public Submap(byte from, boolean bottom, byte to2, boolean top) {
            if (!bottom && !top && Byte2CharRBTreeMap.this.compare(from, to2) > 0) {
                throw new IllegalArgumentException("Start key (" + from + ") is larger than end key (" + to2 + ")");
            }
            this.from = from;
            this.bottom = bottom;
            this.to = to2;
            this.top = top;
            this.defRetValue = Byte2CharRBTreeMap.this.defRetValue;
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
            return !(!this.bottom && Byte2CharRBTreeMap.this.compare(k2, this.from) < 0 || !this.top && Byte2CharRBTreeMap.this.compare(k2, this.to) >= 0);
        }

        @Override
        public ObjectSortedSet<Byte2CharMap.Entry> byte2CharEntrySet() {
            if (this.entries == null) {
                this.entries = new AbstractObjectSortedSet<Byte2CharMap.Entry>(){

                    @Override
                    public ObjectBidirectionalIterator<Byte2CharMap.Entry> iterator() {
                        return new SubmapEntryIterator();
                    }

                    @Override
                    public ObjectBidirectionalIterator<Byte2CharMap.Entry> iterator(Byte2CharMap.Entry from) {
                        return new SubmapEntryIterator(from.getByteKey());
                    }

                    @Override
                    public Comparator<? super Byte2CharMap.Entry> comparator() {
                        return Byte2CharRBTreeMap.this.byte2CharEntrySet().comparator();
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
                        if (e2.getValue() == null || !(e2.getValue() instanceof Character)) {
                            return false;
                        }
                        Entry f2 = Byte2CharRBTreeMap.this.findKey((Byte)e2.getKey());
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
                        if (e2.getValue() == null || !(e2.getValue() instanceof Character)) {
                            return false;
                        }
                        Entry f2 = Byte2CharRBTreeMap.this.findKey((Byte)e2.getKey());
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
                    public Byte2CharMap.Entry first() {
                        return Submap.this.firstEntry();
                    }

                    @Override
                    public Byte2CharMap.Entry last() {
                        return Submap.this.lastEntry();
                    }

                    @Override
                    public ObjectSortedSet<Byte2CharMap.Entry> subSet(Byte2CharMap.Entry from, Byte2CharMap.Entry to2) {
                        return Submap.this.subMap(from.getByteKey(), to2.getByteKey()).byte2CharEntrySet();
                    }

                    @Override
                    public ObjectSortedSet<Byte2CharMap.Entry> headSet(Byte2CharMap.Entry to2) {
                        return Submap.this.headMap(to2.getByteKey()).byte2CharEntrySet();
                    }

                    @Override
                    public ObjectSortedSet<Byte2CharMap.Entry> tailSet(Byte2CharMap.Entry from) {
                        return Submap.this.tailMap(from.getByteKey()).byte2CharEntrySet();
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
        public CharCollection values() {
            if (this.values == null) {
                this.values = new AbstractCharCollection(){

                    @Override
                    public CharIterator iterator() {
                        return new SubmapValueIterator();
                    }

                    @Override
                    public boolean contains(char k2) {
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
            return this.in(k2) && Byte2CharRBTreeMap.this.containsKey(k2);
        }

        @Override
        public boolean containsValue(char v2) {
            SubmapIterator i2 = new SubmapIterator();
            while (i2.hasNext()) {
                char ev2 = i2.nextEntry().value;
                if (ev2 != v2) continue;
                return true;
            }
            return false;
        }

        @Override
        public char get(byte k2) {
            Entry e2;
            byte kk2 = k2;
            return this.in(kk2) && (e2 = Byte2CharRBTreeMap.this.findKey(kk2)) != null ? e2.value : this.defRetValue;
        }

        @Override
        public char put(byte k2, char v2) {
            Byte2CharRBTreeMap.this.modified = false;
            if (!this.in(k2)) {
                throw new IllegalArgumentException("Key (" + k2 + ") out of range [" + (this.bottom ? "-" : String.valueOf(this.from)) + ", " + (this.top ? "-" : String.valueOf(this.to)) + ")");
            }
            char oldValue = Byte2CharRBTreeMap.this.put(k2, v2);
            return Byte2CharRBTreeMap.this.modified ? this.defRetValue : oldValue;
        }

        @Override
        @Deprecated
        public Character put(Byte ok2, Character ov) {
            char oldValue = this.put((byte)ok2, ov.charValue());
            return Byte2CharRBTreeMap.this.modified ? null : Character.valueOf(oldValue);
        }

        @Override
        public char remove(byte k2) {
            Byte2CharRBTreeMap.this.modified = false;
            if (!this.in(k2)) {
                return this.defRetValue;
            }
            char oldValue = Byte2CharRBTreeMap.this.remove(k2);
            return Byte2CharRBTreeMap.this.modified ? oldValue : this.defRetValue;
        }

        @Override
        @Deprecated
        public Character remove(Object ok2) {
            char oldValue = this.remove((Byte)ok2);
            return Byte2CharRBTreeMap.this.modified ? Character.valueOf(oldValue) : null;
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
            return Byte2CharRBTreeMap.this.actualComparator;
        }

        @Override
        public Byte2CharSortedMap headMap(byte to2) {
            if (this.top) {
                return new Submap(this.from, this.bottom, to2, false);
            }
            return Byte2CharRBTreeMap.this.compare(to2, this.to) < 0 ? new Submap(this.from, this.bottom, to2, false) : this;
        }

        @Override
        public Byte2CharSortedMap tailMap(byte from) {
            if (this.bottom) {
                return new Submap(from, false, this.to, this.top);
            }
            return Byte2CharRBTreeMap.this.compare(from, this.from) > 0 ? new Submap(from, false, this.to, this.top) : this;
        }

        @Override
        public Byte2CharSortedMap subMap(byte from, byte to2) {
            if (this.top && this.bottom) {
                return new Submap(from, false, to2, false);
            }
            if (!this.top) {
                byte by2 = to2 = Byte2CharRBTreeMap.this.compare(to2, this.to) < 0 ? to2 : this.to;
            }
            if (!this.bottom) {
                byte by3 = from = Byte2CharRBTreeMap.this.compare(from, this.from) > 0 ? from : this.from;
            }
            if (!this.top && !this.bottom && from == this.from && to2 == this.to) {
                return this;
            }
            return new Submap(from, false, to2, false);
        }

        public Entry firstEntry() {
            Entry e2;
            if (Byte2CharRBTreeMap.this.tree == null) {
                return null;
            }
            if (this.bottom) {
                e2 = Byte2CharRBTreeMap.this.firstEntry;
            } else {
                e2 = Byte2CharRBTreeMap.this.locateKey(this.from);
                if (Byte2CharRBTreeMap.this.compare(e2.key, this.from) < 0) {
                    e2 = e2.next();
                }
            }
            if (e2 == null || !this.top && Byte2CharRBTreeMap.this.compare(e2.key, this.to) >= 0) {
                return null;
            }
            return e2;
        }

        public Entry lastEntry() {
            Entry e2;
            if (Byte2CharRBTreeMap.this.tree == null) {
                return null;
            }
            if (this.top) {
                e2 = Byte2CharRBTreeMap.this.lastEntry;
            } else {
                e2 = Byte2CharRBTreeMap.this.locateKey(this.to);
                if (Byte2CharRBTreeMap.this.compare(e2.key, this.to) >= 0) {
                    e2 = e2.prev();
                }
            }
            if (e2 == null || !this.bottom && Byte2CharRBTreeMap.this.compare(e2.key, this.from) < 0) {
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
        extends SubmapIterator
        implements CharListIterator {
            private SubmapValueIterator() {
            }

            @Override
            public char nextChar() {
                return this.nextEntry().value;
            }

            @Override
            public char previousChar() {
                return this.previousEntry().value;
            }

            @Override
            public void set(char v2) {
                throw new UnsupportedOperationException();
            }

            @Override
            public void add(char v2) {
                throw new UnsupportedOperationException();
            }

            @Override
            public Character next() {
                return Character.valueOf(this.nextEntry().value);
            }

            @Override
            public Character previous() {
                return Character.valueOf(this.previousEntry().value);
            }

            @Override
            public void set(Character ok2) {
                throw new UnsupportedOperationException();
            }

            @Override
            public void add(Character ok2) {
                throw new UnsupportedOperationException();
            }
        }

        private final class SubmapKeyIterator
        extends SubmapIterator
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
        extends SubmapIterator
        implements ObjectListIterator<Byte2CharMap.Entry> {
            SubmapEntryIterator() {
            }

            SubmapEntryIterator(byte k2) {
                super(k2);
            }

            @Override
            public Byte2CharMap.Entry next() {
                return this.nextEntry();
            }

            @Override
            public Byte2CharMap.Entry previous() {
                return this.previousEntry();
            }

            @Override
            public void set(Byte2CharMap.Entry ok2) {
                throw new UnsupportedOperationException();
            }

            @Override
            public void add(Byte2CharMap.Entry ok2) {
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
                if (!submap.bottom && submap.Byte2CharRBTreeMap.this.compare(k2, this.next.key) < 0) {
                    this.prev = null;
                    return;
                }
                if (!submap.top) {
                    this.prev = submap.lastEntry();
                    if (submap.Byte2CharRBTreeMap.this.compare(k2, this.prev.key) >= 0) {
                        this.next = null;
                        return;
                    }
                }
                this.next = submap.Byte2CharRBTreeMap.this.locateKey(k2);
                if (submap.Byte2CharRBTreeMap.this.compare(this.next.key, k2) <= 0) {
                    this.prev = this.next;
                    this.next = this.next.next();
                    return;
                }
                this.prev = this.next.prev();
            }

            @Override
            void updatePrevious() {
                this.prev = this.prev.prev();
                if (!Submap.this.bottom && this.prev != null && Byte2CharRBTreeMap.this.compare(this.prev.key, Submap.this.from) < 0) {
                    this.prev = null;
                }
            }

            @Override
            void updateNext() {
                this.next = this.next.next();
                if (!Submap.this.top && this.next != null && Byte2CharRBTreeMap.this.compare(this.next.key, Submap.this.to) >= 0) {
                    this.next = null;
                }
            }
        }

        private class KeySet
        extends AbstractByte2CharSortedMap.KeySet {
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
    implements CharListIterator {
        private ValueIterator() {
        }

        @Override
        public char nextChar() {
            return this.nextEntry().value;
        }

        @Override
        public char previousChar() {
            return this.previousEntry().value;
        }

        @Override
        public void set(char v2) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void add(char v2) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Character next() {
            return Character.valueOf(this.nextEntry().value);
        }

        @Override
        public Character previous() {
            return Character.valueOf(this.previousEntry().value);
        }

        @Override
        public void set(Character ok2) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void add(Character ok2) {
            throw new UnsupportedOperationException();
        }
    }

    private class KeySet
    extends AbstractByte2CharSortedMap.KeySet {
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
    implements ObjectListIterator<Byte2CharMap.Entry> {
        EntryIterator() {
        }

        EntryIterator(byte k2) {
            super(k2);
        }

        @Override
        public Byte2CharMap.Entry next() {
            return this.nextEntry();
        }

        @Override
        public Byte2CharMap.Entry previous() {
            return this.previousEntry();
        }

        @Override
        public void set(Byte2CharMap.Entry ok2) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void add(Byte2CharMap.Entry ok2) {
            throw new UnsupportedOperationException();
        }
    }

    private class TreeIterator {
        Entry prev;
        Entry next;
        Entry curr;
        int index = 0;

        TreeIterator() {
            this.next = Byte2CharRBTreeMap.this.firstEntry;
        }

        TreeIterator(byte k2) {
            this.next = Byte2CharRBTreeMap.this.locateKey(k2);
            if (this.next != null) {
                if (Byte2CharRBTreeMap.this.compare(this.next.key, k2) <= 0) {
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

        Entry nextEntry() {
            if (!this.hasNext()) {
                throw new NoSuchElementException();
            }
            this.curr = this.prev = this.next;
            ++this.index;
            this.updateNext();
            return this.curr;
        }

        void updatePrevious() {
            this.prev = this.prev.prev();
        }

        Entry previousEntry() {
            if (!this.hasPrevious()) {
                throw new NoSuchElementException();
            }
            this.curr = this.next = this.prev;
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
            this.next = this.prev = this.curr;
            this.updatePrevious();
            this.updateNext();
            Byte2CharRBTreeMap.this.remove(this.curr.key);
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

    private static final class Entry
    implements Cloneable,
    Byte2CharMap.Entry {
        private static final int BLACK_MASK = 1;
        private static final int SUCC_MASK = Integer.MIN_VALUE;
        private static final int PRED_MASK = 0x40000000;
        byte key;
        char value;
        Entry left;
        Entry right;
        int info;

        Entry() {
        }

        Entry(byte k2, char v2) {
            this.key = k2;
            this.value = v2;
            this.info = -1073741824;
        }

        Entry left() {
            return (this.info & 0x40000000) != 0 ? null : this.left;
        }

        Entry right() {
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

        void pred(Entry pred) {
            this.info |= 0x40000000;
            this.left = pred;
        }

        void succ(Entry succ) {
            this.info |= Integer.MIN_VALUE;
            this.right = succ;
        }

        void left(Entry left) {
            this.info &= 0xBFFFFFFF;
            this.left = left;
        }

        void right(Entry right) {
            this.info &= Integer.MAX_VALUE;
            this.right = right;
        }

        boolean black() {
            return (this.info & 1) != 0;
        }

        void black(boolean black) {
            this.info = black ? (this.info |= 1) : (this.info &= 0xFFFFFFFE);
        }

        Entry next() {
            Entry next = this.right;
            if ((this.info & Integer.MIN_VALUE) == 0) {
                while ((next.info & 0x40000000) == 0) {
                    next = next.left;
                }
            }
            return next;
        }

        Entry prev() {
            Entry prev = this.left;
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
        @Deprecated
        public Character getValue() {
            return Character.valueOf(this.value);
        }

        @Override
        public char getCharValue() {
            return this.value;
        }

        @Override
        public char setValue(char value) {
            char oldValue = this.value;
            this.value = value;
            return oldValue;
        }

        @Override
        public Character setValue(Character value) {
            return Character.valueOf(this.setValue(value.charValue()));
        }

        public Entry clone() {
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
            return this.key == (Byte)e2.getKey() && this.value == ((Character)e2.getValue()).charValue();
        }

        @Override
        public int hashCode() {
            return this.key ^ this.value;
        }

        public String toString() {
            return this.key + "=>" + this.value;
        }
    }
}

