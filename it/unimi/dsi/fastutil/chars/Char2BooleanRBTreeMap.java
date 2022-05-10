package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.booleans.AbstractBooleanCollection;
import it.unimi.dsi.fastutil.booleans.BooleanCollection;
import it.unimi.dsi.fastutil.booleans.BooleanIterator;
import it.unimi.dsi.fastutil.booleans.BooleanListIterator;
import it.unimi.dsi.fastutil.chars.AbstractChar2BooleanSortedMap;
import it.unimi.dsi.fastutil.chars.Char2BooleanMap;
import it.unimi.dsi.fastutil.chars.Char2BooleanSortedMap;
import it.unimi.dsi.fastutil.chars.CharBidirectionalIterator;
import it.unimi.dsi.fastutil.chars.CharComparator;
import it.unimi.dsi.fastutil.chars.CharListIterator;
import it.unimi.dsi.fastutil.chars.CharSortedSet;
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

public class Char2BooleanRBTreeMap
extends AbstractChar2BooleanSortedMap
implements Serializable,
Cloneable {
    protected transient Entry tree;
    protected int count;
    protected transient Entry firstEntry;
    protected transient Entry lastEntry;
    protected transient ObjectSortedSet<Char2BooleanMap.Entry> entries;
    protected transient CharSortedSet keys;
    protected transient BooleanCollection values;
    protected transient boolean modified;
    protected Comparator<? super Character> storedComparator;
    protected transient CharComparator actualComparator;
    private static final long serialVersionUID = -7046029254386353129L;
    private static final boolean ASSERTS = false;
    private transient boolean[] dirPath;
    private transient Entry[] nodePath;

    public Char2BooleanRBTreeMap() {
        this.allocatePaths();
        this.tree = null;
        this.count = 0;
    }

    private void setActualComparator() {
        this.actualComparator = this.storedComparator == null || this.storedComparator instanceof CharComparator ? (CharComparator)this.storedComparator : new CharComparator(){

            @Override
            public int compare(char k1, char k2) {
                return Char2BooleanRBTreeMap.this.storedComparator.compare(Character.valueOf(k1), Character.valueOf(k2));
            }

            @Override
            public int compare(Character ok1, Character ok2) {
                return Char2BooleanRBTreeMap.this.storedComparator.compare(ok1, ok2);
            }
        };
    }

    public Char2BooleanRBTreeMap(Comparator<? super Character> c2) {
        this();
        this.storedComparator = c2;
        this.setActualComparator();
    }

    public Char2BooleanRBTreeMap(Map<? extends Character, ? extends Boolean> m2) {
        this();
        this.putAll(m2);
    }

    public Char2BooleanRBTreeMap(SortedMap<Character, Boolean> m2) {
        this(m2.comparator());
        this.putAll((Map<? extends Character, ? extends Boolean>)m2);
    }

    public Char2BooleanRBTreeMap(Char2BooleanMap m2) {
        this();
        this.putAll(m2);
    }

    public Char2BooleanRBTreeMap(Char2BooleanSortedMap m2) {
        this(m2.comparator());
        this.putAll(m2);
    }

    public Char2BooleanRBTreeMap(char[] k2, boolean[] v2, Comparator<? super Character> c2) {
        this(c2);
        if (k2.length != v2.length) {
            throw new IllegalArgumentException("The key array and the value array have different lengths (" + k2.length + " and " + v2.length + ")");
        }
        for (int i2 = 0; i2 < k2.length; ++i2) {
            this.put(k2[i2], v2[i2]);
        }
    }

    public Char2BooleanRBTreeMap(char[] k2, boolean[] v2) {
        this(k2, v2, null);
    }

    final int compare(char k1, char k2) {
        return this.actualComparator == null ? Character.compare(k1, k2) : this.actualComparator.compare(k1, k2);
    }

    final Entry findKey(char k2) {
        int cmp;
        Entry e2 = this.tree;
        while (e2 != null && (cmp = this.compare(k2, e2.key)) != 0) {
            e2 = cmp < 0 ? e2.left() : e2.right();
        }
        return e2;
    }

    final Entry locateKey(char k2) {
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

    @Override
    public boolean put(char k2, boolean v2) {
        Entry e2 = this.add(k2);
        boolean oldValue = e2.value;
        e2.value = v2;
        return oldValue;
    }

    private Entry add(char k2) {
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
    public boolean remove(char k) {
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
    public Boolean put(Character ok2, Boolean ov) {
        boolean oldValue = this.put(ok2.charValue(), (boolean)ov);
        return this.modified ? null : Boolean.valueOf(oldValue);
    }

    @Override
    @Deprecated
    public Boolean remove(Object ok2) {
        boolean oldValue = this.remove(((Character)ok2).charValue());
        return this.modified ? Boolean.valueOf(oldValue) : null;
    }

    @Override
    public boolean containsValue(boolean v2) {
        ValueIterator i2 = new ValueIterator();
        int j2 = this.count;
        while (j2-- != 0) {
            boolean ev2 = i2.nextBoolean();
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
    public boolean containsKey(char k2) {
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
    public boolean get(char k2) {
        Entry e2 = this.findKey(k2);
        return e2 == null ? this.defRetValue : e2.value;
    }

    @Override
    public char firstCharKey() {
        if (this.tree == null) {
            throw new NoSuchElementException();
        }
        return this.firstEntry.key;
    }

    @Override
    public char lastCharKey() {
        if (this.tree == null) {
            throw new NoSuchElementException();
        }
        return this.lastEntry.key;
    }

    @Override
    public ObjectSortedSet<Char2BooleanMap.Entry> char2BooleanEntrySet() {
        if (this.entries == null) {
            this.entries = new AbstractObjectSortedSet<Char2BooleanMap.Entry>(){
                final Comparator<? super Char2BooleanMap.Entry> comparator = new Comparator<Char2BooleanMap.Entry>(){

                    @Override
                    public int compare(Char2BooleanMap.Entry x2, Char2BooleanMap.Entry y2) {
                        return Char2BooleanRBTreeMap.this.actualComparator.compare(x2.getCharKey(), y2.getCharKey());
                    }
                };

                @Override
                public Comparator<? super Char2BooleanMap.Entry> comparator() {
                    return this.comparator;
                }

                @Override
                public ObjectBidirectionalIterator<Char2BooleanMap.Entry> iterator() {
                    return new EntryIterator();
                }

                @Override
                public ObjectBidirectionalIterator<Char2BooleanMap.Entry> iterator(Char2BooleanMap.Entry from) {
                    return new EntryIterator(from.getCharKey());
                }

                @Override
                public boolean contains(Object o2) {
                    if (!(o2 instanceof Map.Entry)) {
                        return false;
                    }
                    Map.Entry e2 = (Map.Entry)o2;
                    if (e2.getKey() == null || !(e2.getKey() instanceof Character)) {
                        return false;
                    }
                    if (e2.getValue() == null || !(e2.getValue() instanceof Boolean)) {
                        return false;
                    }
                    Entry f2 = Char2BooleanRBTreeMap.this.findKey(((Character)e2.getKey()).charValue());
                    return e2.equals(f2);
                }

                @Override
                public boolean remove(Object o2) {
                    if (!(o2 instanceof Map.Entry)) {
                        return false;
                    }
                    Map.Entry e2 = (Map.Entry)o2;
                    if (e2.getKey() == null || !(e2.getKey() instanceof Character)) {
                        return false;
                    }
                    if (e2.getValue() == null || !(e2.getValue() instanceof Boolean)) {
                        return false;
                    }
                    Entry f2 = Char2BooleanRBTreeMap.this.findKey(((Character)e2.getKey()).charValue());
                    if (f2 != null) {
                        Char2BooleanRBTreeMap.this.remove(f2.key);
                    }
                    return f2 != null;
                }

                @Override
                public int size() {
                    return Char2BooleanRBTreeMap.this.count;
                }

                @Override
                public void clear() {
                    Char2BooleanRBTreeMap.this.clear();
                }

                @Override
                public Char2BooleanMap.Entry first() {
                    return Char2BooleanRBTreeMap.this.firstEntry;
                }

                @Override
                public Char2BooleanMap.Entry last() {
                    return Char2BooleanRBTreeMap.this.lastEntry;
                }

                @Override
                public ObjectSortedSet<Char2BooleanMap.Entry> subSet(Char2BooleanMap.Entry from, Char2BooleanMap.Entry to2) {
                    return Char2BooleanRBTreeMap.this.subMap(from.getCharKey(), to2.getCharKey()).char2BooleanEntrySet();
                }

                @Override
                public ObjectSortedSet<Char2BooleanMap.Entry> headSet(Char2BooleanMap.Entry to2) {
                    return Char2BooleanRBTreeMap.this.headMap(to2.getCharKey()).char2BooleanEntrySet();
                }

                @Override
                public ObjectSortedSet<Char2BooleanMap.Entry> tailSet(Char2BooleanMap.Entry from) {
                    return Char2BooleanRBTreeMap.this.tailMap(from.getCharKey()).char2BooleanEntrySet();
                }
            };
        }
        return this.entries;
    }

    @Override
    public CharSortedSet keySet() {
        if (this.keys == null) {
            this.keys = new KeySet();
        }
        return this.keys;
    }

    @Override
    public BooleanCollection values() {
        if (this.values == null) {
            this.values = new AbstractBooleanCollection(){

                @Override
                public BooleanIterator iterator() {
                    return new ValueIterator();
                }

                @Override
                public boolean contains(boolean k2) {
                    return Char2BooleanRBTreeMap.this.containsValue(k2);
                }

                @Override
                public int size() {
                    return Char2BooleanRBTreeMap.this.count;
                }

                @Override
                public void clear() {
                    Char2BooleanRBTreeMap.this.clear();
                }
            };
        }
        return this.values;
    }

    @Override
    public CharComparator comparator() {
        return this.actualComparator;
    }

    @Override
    public Char2BooleanSortedMap headMap(char to2) {
        return new Submap('\u0000', true, to2, false);
    }

    @Override
    public Char2BooleanSortedMap tailMap(char from) {
        return new Submap(from, false, '\u0000', true);
    }

    @Override
    public Char2BooleanSortedMap subMap(char from, char to2) {
        return new Submap(from, false, to2, false);
    }

    public Char2BooleanRBTreeMap clone() {
        Char2BooleanRBTreeMap c2;
        try {
            c2 = (Char2BooleanRBTreeMap)super.clone();
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
            s2.writeChar(e2.key);
            s2.writeBoolean(e2.value);
        }
    }

    private Entry readTree(ObjectInputStream s2, int n2, Entry pred, Entry succ) throws IOException, ClassNotFoundException {
        if (n2 == 1) {
            Entry top = new Entry(s2.readChar(), s2.readBoolean());
            top.pred(pred);
            top.succ(succ);
            top.black(true);
            return top;
        }
        if (n2 == 2) {
            Entry top = new Entry(s2.readChar(), s2.readBoolean());
            top.black(true);
            top.right(new Entry(s2.readChar(), s2.readBoolean()));
            top.right.pred(top);
            top.pred(pred);
            top.right.succ(succ);
            return top;
        }
        int rightN = n2 / 2;
        int leftN = n2 - rightN - 1;
        Entry top = new Entry();
        top.left(this.readTree(s2, leftN, pred, top));
        top.key = s2.readChar();
        top.value = s2.readBoolean();
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
    extends AbstractChar2BooleanSortedMap
    implements Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        char from;
        char to;
        boolean bottom;
        boolean top;
        protected transient ObjectSortedSet<Char2BooleanMap.Entry> entries;
        protected transient CharSortedSet keys;
        protected transient BooleanCollection values;

        public Submap(char from, boolean bottom, char to2, boolean top) {
            if (!bottom && !top && Char2BooleanRBTreeMap.this.compare(from, to2) > 0) {
                throw new IllegalArgumentException("Start key (" + from + ") is larger than end key (" + to2 + ")");
            }
            this.from = from;
            this.bottom = bottom;
            this.to = to2;
            this.top = top;
            this.defRetValue = Char2BooleanRBTreeMap.this.defRetValue;
        }

        @Override
        public void clear() {
            SubmapIterator i2 = new SubmapIterator();
            while (i2.hasNext()) {
                i2.nextEntry();
                i2.remove();
            }
        }

        final boolean in(char k2) {
            return !(!this.bottom && Char2BooleanRBTreeMap.this.compare(k2, this.from) < 0 || !this.top && Char2BooleanRBTreeMap.this.compare(k2, this.to) >= 0);
        }

        @Override
        public ObjectSortedSet<Char2BooleanMap.Entry> char2BooleanEntrySet() {
            if (this.entries == null) {
                this.entries = new AbstractObjectSortedSet<Char2BooleanMap.Entry>(){

                    @Override
                    public ObjectBidirectionalIterator<Char2BooleanMap.Entry> iterator() {
                        return new SubmapEntryIterator();
                    }

                    @Override
                    public ObjectBidirectionalIterator<Char2BooleanMap.Entry> iterator(Char2BooleanMap.Entry from) {
                        return new SubmapEntryIterator(from.getCharKey());
                    }

                    @Override
                    public Comparator<? super Char2BooleanMap.Entry> comparator() {
                        return Char2BooleanRBTreeMap.this.char2BooleanEntrySet().comparator();
                    }

                    @Override
                    public boolean contains(Object o2) {
                        if (!(o2 instanceof Map.Entry)) {
                            return false;
                        }
                        Map.Entry e2 = (Map.Entry)o2;
                        if (e2.getKey() == null || !(e2.getKey() instanceof Character)) {
                            return false;
                        }
                        if (e2.getValue() == null || !(e2.getValue() instanceof Boolean)) {
                            return false;
                        }
                        Entry f2 = Char2BooleanRBTreeMap.this.findKey(((Character)e2.getKey()).charValue());
                        return f2 != null && Submap.this.in(f2.key) && e2.equals(f2);
                    }

                    @Override
                    public boolean remove(Object o2) {
                        if (!(o2 instanceof Map.Entry)) {
                            return false;
                        }
                        Map.Entry e2 = (Map.Entry)o2;
                        if (e2.getKey() == null || !(e2.getKey() instanceof Character)) {
                            return false;
                        }
                        if (e2.getValue() == null || !(e2.getValue() instanceof Boolean)) {
                            return false;
                        }
                        Entry f2 = Char2BooleanRBTreeMap.this.findKey(((Character)e2.getKey()).charValue());
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
                    public Char2BooleanMap.Entry first() {
                        return Submap.this.firstEntry();
                    }

                    @Override
                    public Char2BooleanMap.Entry last() {
                        return Submap.this.lastEntry();
                    }

                    @Override
                    public ObjectSortedSet<Char2BooleanMap.Entry> subSet(Char2BooleanMap.Entry from, Char2BooleanMap.Entry to2) {
                        return Submap.this.subMap(from.getCharKey(), to2.getCharKey()).char2BooleanEntrySet();
                    }

                    @Override
                    public ObjectSortedSet<Char2BooleanMap.Entry> headSet(Char2BooleanMap.Entry to2) {
                        return Submap.this.headMap(to2.getCharKey()).char2BooleanEntrySet();
                    }

                    @Override
                    public ObjectSortedSet<Char2BooleanMap.Entry> tailSet(Char2BooleanMap.Entry from) {
                        return Submap.this.tailMap(from.getCharKey()).char2BooleanEntrySet();
                    }
                };
            }
            return this.entries;
        }

        @Override
        public CharSortedSet keySet() {
            if (this.keys == null) {
                this.keys = new KeySet();
            }
            return this.keys;
        }

        @Override
        public BooleanCollection values() {
            if (this.values == null) {
                this.values = new AbstractBooleanCollection(){

                    @Override
                    public BooleanIterator iterator() {
                        return new SubmapValueIterator();
                    }

                    @Override
                    public boolean contains(boolean k2) {
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
        public boolean containsKey(char k2) {
            return this.in(k2) && Char2BooleanRBTreeMap.this.containsKey(k2);
        }

        @Override
        public boolean containsValue(boolean v2) {
            SubmapIterator i2 = new SubmapIterator();
            while (i2.hasNext()) {
                boolean ev2 = i2.nextEntry().value;
                if (ev2 != v2) continue;
                return true;
            }
            return false;
        }

        @Override
        public boolean get(char k2) {
            Entry e2;
            char kk2 = k2;
            return this.in(kk2) && (e2 = Char2BooleanRBTreeMap.this.findKey(kk2)) != null ? e2.value : this.defRetValue;
        }

        @Override
        public boolean put(char k2, boolean v2) {
            Char2BooleanRBTreeMap.this.modified = false;
            if (!this.in(k2)) {
                throw new IllegalArgumentException("Key (" + k2 + ") out of range [" + (this.bottom ? "-" : String.valueOf(this.from)) + ", " + (this.top ? "-" : String.valueOf(this.to)) + ")");
            }
            boolean oldValue = Char2BooleanRBTreeMap.this.put(k2, v2);
            return Char2BooleanRBTreeMap.this.modified ? this.defRetValue : oldValue;
        }

        @Override
        @Deprecated
        public Boolean put(Character ok2, Boolean ov) {
            boolean oldValue = this.put(ok2.charValue(), (boolean)ov);
            return Char2BooleanRBTreeMap.this.modified ? null : Boolean.valueOf(oldValue);
        }

        @Override
        public boolean remove(char k2) {
            Char2BooleanRBTreeMap.this.modified = false;
            if (!this.in(k2)) {
                return this.defRetValue;
            }
            boolean oldValue = Char2BooleanRBTreeMap.this.remove(k2);
            return Char2BooleanRBTreeMap.this.modified ? oldValue : this.defRetValue;
        }

        @Override
        @Deprecated
        public Boolean remove(Object ok2) {
            boolean oldValue = this.remove(((Character)ok2).charValue());
            return Char2BooleanRBTreeMap.this.modified ? Boolean.valueOf(oldValue) : null;
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
        public CharComparator comparator() {
            return Char2BooleanRBTreeMap.this.actualComparator;
        }

        @Override
        public Char2BooleanSortedMap headMap(char to2) {
            if (this.top) {
                return new Submap(this.from, this.bottom, to2, false);
            }
            return Char2BooleanRBTreeMap.this.compare(to2, this.to) < 0 ? new Submap(this.from, this.bottom, to2, false) : this;
        }

        @Override
        public Char2BooleanSortedMap tailMap(char from) {
            if (this.bottom) {
                return new Submap(from, false, this.to, this.top);
            }
            return Char2BooleanRBTreeMap.this.compare(from, this.from) > 0 ? new Submap(from, false, this.to, this.top) : this;
        }

        @Override
        public Char2BooleanSortedMap subMap(char from, char to2) {
            if (this.top && this.bottom) {
                return new Submap(from, false, to2, false);
            }
            if (!this.top) {
                char c2 = to2 = Char2BooleanRBTreeMap.this.compare(to2, this.to) < 0 ? to2 : this.to;
            }
            if (!this.bottom) {
                char c3 = from = Char2BooleanRBTreeMap.this.compare(from, this.from) > 0 ? from : this.from;
            }
            if (!this.top && !this.bottom && from == this.from && to2 == this.to) {
                return this;
            }
            return new Submap(from, false, to2, false);
        }

        public Entry firstEntry() {
            Entry e2;
            if (Char2BooleanRBTreeMap.this.tree == null) {
                return null;
            }
            if (this.bottom) {
                e2 = Char2BooleanRBTreeMap.this.firstEntry;
            } else {
                e2 = Char2BooleanRBTreeMap.this.locateKey(this.from);
                if (Char2BooleanRBTreeMap.this.compare(e2.key, this.from) < 0) {
                    e2 = e2.next();
                }
            }
            if (e2 == null || !this.top && Char2BooleanRBTreeMap.this.compare(e2.key, this.to) >= 0) {
                return null;
            }
            return e2;
        }

        public Entry lastEntry() {
            Entry e2;
            if (Char2BooleanRBTreeMap.this.tree == null) {
                return null;
            }
            if (this.top) {
                e2 = Char2BooleanRBTreeMap.this.lastEntry;
            } else {
                e2 = Char2BooleanRBTreeMap.this.locateKey(this.to);
                if (Char2BooleanRBTreeMap.this.compare(e2.key, this.to) >= 0) {
                    e2 = e2.prev();
                }
            }
            if (e2 == null || !this.bottom && Char2BooleanRBTreeMap.this.compare(e2.key, this.from) < 0) {
                return null;
            }
            return e2;
        }

        @Override
        public char firstCharKey() {
            Entry e2 = this.firstEntry();
            if (e2 == null) {
                throw new NoSuchElementException();
            }
            return e2.key;
        }

        @Override
        public char lastCharKey() {
            Entry e2 = this.lastEntry();
            if (e2 == null) {
                throw new NoSuchElementException();
            }
            return e2.key;
        }

        @Override
        @Deprecated
        public Character firstKey() {
            Entry e2 = this.firstEntry();
            if (e2 == null) {
                throw new NoSuchElementException();
            }
            return e2.getKey();
        }

        @Override
        @Deprecated
        public Character lastKey() {
            Entry e2 = this.lastEntry();
            if (e2 == null) {
                throw new NoSuchElementException();
            }
            return e2.getKey();
        }

        private final class SubmapValueIterator
        extends SubmapIterator
        implements BooleanListIterator {
            private SubmapValueIterator() {
            }

            @Override
            public boolean nextBoolean() {
                return this.nextEntry().value;
            }

            @Override
            public boolean previousBoolean() {
                return this.previousEntry().value;
            }

            @Override
            public void set(boolean v2) {
                throw new UnsupportedOperationException();
            }

            @Override
            public void add(boolean v2) {
                throw new UnsupportedOperationException();
            }

            @Override
            public Boolean next() {
                return this.nextEntry().value;
            }

            @Override
            public Boolean previous() {
                return this.previousEntry().value;
            }

            @Override
            public void set(Boolean ok2) {
                throw new UnsupportedOperationException();
            }

            @Override
            public void add(Boolean ok2) {
                throw new UnsupportedOperationException();
            }
        }

        private final class SubmapKeyIterator
        extends SubmapIterator
        implements CharListIterator {
            public SubmapKeyIterator() {
            }

            public SubmapKeyIterator(char from) {
                super(from);
            }

            @Override
            public char nextChar() {
                return this.nextEntry().key;
            }

            @Override
            public char previousChar() {
                return this.previousEntry().key;
            }

            @Override
            public void set(char k2) {
                throw new UnsupportedOperationException();
            }

            @Override
            public void add(char k2) {
                throw new UnsupportedOperationException();
            }

            @Override
            public Character next() {
                return Character.valueOf(this.nextEntry().key);
            }

            @Override
            public Character previous() {
                return Character.valueOf(this.previousEntry().key);
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

        private class SubmapEntryIterator
        extends SubmapIterator
        implements ObjectListIterator<Char2BooleanMap.Entry> {
            SubmapEntryIterator() {
            }

            SubmapEntryIterator(char k2) {
                super(k2);
            }

            @Override
            public Char2BooleanMap.Entry next() {
                return this.nextEntry();
            }

            @Override
            public Char2BooleanMap.Entry previous() {
                return this.previousEntry();
            }

            @Override
            public void set(Char2BooleanMap.Entry ok2) {
                throw new UnsupportedOperationException();
            }

            @Override
            public void add(Char2BooleanMap.Entry ok2) {
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
            SubmapIterator(char k2) {
                this();
                if (this.next == null) return;
                if (!submap.bottom && submap.Char2BooleanRBTreeMap.this.compare(k2, this.next.key) < 0) {
                    this.prev = null;
                    return;
                }
                if (!submap.top) {
                    this.prev = submap.lastEntry();
                    if (submap.Char2BooleanRBTreeMap.this.compare(k2, this.prev.key) >= 0) {
                        this.next = null;
                        return;
                    }
                }
                this.next = submap.Char2BooleanRBTreeMap.this.locateKey(k2);
                if (submap.Char2BooleanRBTreeMap.this.compare(this.next.key, k2) <= 0) {
                    this.prev = this.next;
                    this.next = this.next.next();
                    return;
                }
                this.prev = this.next.prev();
            }

            @Override
            void updatePrevious() {
                this.prev = this.prev.prev();
                if (!Submap.this.bottom && this.prev != null && Char2BooleanRBTreeMap.this.compare(this.prev.key, Submap.this.from) < 0) {
                    this.prev = null;
                }
            }

            @Override
            void updateNext() {
                this.next = this.next.next();
                if (!Submap.this.top && this.next != null && Char2BooleanRBTreeMap.this.compare(this.next.key, Submap.this.to) >= 0) {
                    this.next = null;
                }
            }
        }

        private class KeySet
        extends AbstractChar2BooleanSortedMap.KeySet {
            private KeySet() {
            }

            @Override
            public CharBidirectionalIterator iterator() {
                return new SubmapKeyIterator();
            }

            @Override
            public CharBidirectionalIterator iterator(char from) {
                return new SubmapKeyIterator(from);
            }
        }
    }

    private final class ValueIterator
    extends TreeIterator
    implements BooleanListIterator {
        private ValueIterator() {
        }

        @Override
        public boolean nextBoolean() {
            return this.nextEntry().value;
        }

        @Override
        public boolean previousBoolean() {
            return this.previousEntry().value;
        }

        @Override
        public void set(boolean v2) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void add(boolean v2) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Boolean next() {
            return this.nextEntry().value;
        }

        @Override
        public Boolean previous() {
            return this.previousEntry().value;
        }

        @Override
        public void set(Boolean ok2) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void add(Boolean ok2) {
            throw new UnsupportedOperationException();
        }
    }

    private class KeySet
    extends AbstractChar2BooleanSortedMap.KeySet {
        private KeySet() {
        }

        @Override
        public CharBidirectionalIterator iterator() {
            return new KeyIterator();
        }

        @Override
        public CharBidirectionalIterator iterator(char from) {
            return new KeyIterator(from);
        }
    }

    private final class KeyIterator
    extends TreeIterator
    implements CharListIterator {
        public KeyIterator() {
        }

        public KeyIterator(char k2) {
            super(k2);
        }

        @Override
        public char nextChar() {
            return this.nextEntry().key;
        }

        @Override
        public char previousChar() {
            return this.previousEntry().key;
        }

        @Override
        public void set(char k2) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void add(char k2) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Character next() {
            return Character.valueOf(this.nextEntry().key);
        }

        @Override
        public Character previous() {
            return Character.valueOf(this.previousEntry().key);
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

    private class EntryIterator
    extends TreeIterator
    implements ObjectListIterator<Char2BooleanMap.Entry> {
        EntryIterator() {
        }

        EntryIterator(char k2) {
            super(k2);
        }

        @Override
        public Char2BooleanMap.Entry next() {
            return this.nextEntry();
        }

        @Override
        public Char2BooleanMap.Entry previous() {
            return this.previousEntry();
        }

        @Override
        public void set(Char2BooleanMap.Entry ok2) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void add(Char2BooleanMap.Entry ok2) {
            throw new UnsupportedOperationException();
        }
    }

    private class TreeIterator {
        Entry prev;
        Entry next;
        Entry curr;
        int index = 0;

        TreeIterator() {
            this.next = Char2BooleanRBTreeMap.this.firstEntry;
        }

        TreeIterator(char k2) {
            this.next = Char2BooleanRBTreeMap.this.locateKey(k2);
            if (this.next != null) {
                if (Char2BooleanRBTreeMap.this.compare(this.next.key, k2) <= 0) {
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
            Char2BooleanRBTreeMap.this.remove(this.curr.key);
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
    Char2BooleanMap.Entry {
        private static final int BLACK_MASK = 1;
        private static final int SUCC_MASK = Integer.MIN_VALUE;
        private static final int PRED_MASK = 0x40000000;
        char key;
        boolean value;
        Entry left;
        Entry right;
        int info;

        Entry() {
        }

        Entry(char k2, boolean v2) {
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
        public Character getKey() {
            return Character.valueOf(this.key);
        }

        @Override
        public char getCharKey() {
            return this.key;
        }

        @Override
        @Deprecated
        public Boolean getValue() {
            return this.value;
        }

        @Override
        public boolean getBooleanValue() {
            return this.value;
        }

        @Override
        public boolean setValue(boolean value) {
            boolean oldValue = this.value;
            this.value = value;
            return oldValue;
        }

        @Override
        public Boolean setValue(Boolean value) {
            return this.setValue((boolean)value);
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
            return this.key == ((Character)e2.getKey()).charValue() && this.value == (Boolean)e2.getValue();
        }

        @Override
        public int hashCode() {
            return this.key ^ (this.value ? 1231 : 1237);
        }

        public String toString() {
            return this.key + "=>" + this.value;
        }
    }
}

