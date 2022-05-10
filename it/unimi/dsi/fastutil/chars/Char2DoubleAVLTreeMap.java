package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.chars.AbstractChar2DoubleSortedMap;
import it.unimi.dsi.fastutil.chars.Char2DoubleMap;
import it.unimi.dsi.fastutil.chars.Char2DoubleSortedMap;
import it.unimi.dsi.fastutil.chars.CharBidirectionalIterator;
import it.unimi.dsi.fastutil.chars.CharComparator;
import it.unimi.dsi.fastutil.chars.CharListIterator;
import it.unimi.dsi.fastutil.chars.CharSortedSet;
import it.unimi.dsi.fastutil.doubles.AbstractDoubleCollection;
import it.unimi.dsi.fastutil.doubles.DoubleCollection;
import it.unimi.dsi.fastutil.doubles.DoubleIterator;
import it.unimi.dsi.fastutil.doubles.DoubleListIterator;
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

public class Char2DoubleAVLTreeMap
extends AbstractChar2DoubleSortedMap
implements Serializable,
Cloneable {
    protected transient Entry tree;
    protected int count;
    protected transient Entry firstEntry;
    protected transient Entry lastEntry;
    protected transient ObjectSortedSet<Char2DoubleMap.Entry> entries;
    protected transient CharSortedSet keys;
    protected transient DoubleCollection values;
    protected transient boolean modified;
    protected Comparator<? super Character> storedComparator;
    protected transient CharComparator actualComparator;
    private static final long serialVersionUID = -7046029254386353129L;
    private static final boolean ASSERTS = false;
    private transient boolean[] dirPath;

    public Char2DoubleAVLTreeMap() {
        this.allocatePaths();
        this.tree = null;
        this.count = 0;
    }

    private void setActualComparator() {
        this.actualComparator = this.storedComparator == null || this.storedComparator instanceof CharComparator ? (CharComparator)this.storedComparator : new CharComparator(){

            @Override
            public int compare(char k1, char k2) {
                return Char2DoubleAVLTreeMap.this.storedComparator.compare(Character.valueOf(k1), Character.valueOf(k2));
            }

            @Override
            public int compare(Character ok1, Character ok2) {
                return Char2DoubleAVLTreeMap.this.storedComparator.compare(ok1, ok2);
            }
        };
    }

    public Char2DoubleAVLTreeMap(Comparator<? super Character> c2) {
        this();
        this.storedComparator = c2;
        this.setActualComparator();
    }

    public Char2DoubleAVLTreeMap(Map<? extends Character, ? extends Double> m2) {
        this();
        this.putAll(m2);
    }

    public Char2DoubleAVLTreeMap(SortedMap<Character, Double> m2) {
        this(m2.comparator());
        this.putAll((Map<? extends Character, ? extends Double>)m2);
    }

    public Char2DoubleAVLTreeMap(Char2DoubleMap m2) {
        this();
        this.putAll(m2);
    }

    public Char2DoubleAVLTreeMap(Char2DoubleSortedMap m2) {
        this(m2.comparator());
        this.putAll(m2);
    }

    public Char2DoubleAVLTreeMap(char[] k2, double[] v2, Comparator<? super Character> c2) {
        this(c2);
        if (k2.length != v2.length) {
            throw new IllegalArgumentException("The key array and the value array have different lengths (" + k2.length + " and " + v2.length + ")");
        }
        for (int i2 = 0; i2 < k2.length; ++i2) {
            this.put(k2[i2], v2[i2]);
        }
    }

    public Char2DoubleAVLTreeMap(char[] k2, double[] v2) {
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
        this.dirPath = new boolean[48];
    }

    public double addTo(char k2, double incr) {
        Entry e2 = this.add(k2);
        double oldValue = e2.value;
        e2.value += incr;
        return oldValue;
    }

    @Override
    public double put(char k2, double v2) {
        Entry e2 = this.add(k2);
        double oldValue = e2.value;
        e2.value = v2;
        return oldValue;
    }

    private Entry add(char k2) {
        this.modified = false;
        Entry e2 = null;
        if (this.tree == null) {
            ++this.count;
            this.lastEntry = this.firstEntry = new Entry(k2, this.defRetValue);
            this.tree = this.firstEntry;
            e2 = this.firstEntry;
            this.modified = true;
        } else {
            Entry p2 = this.tree;
            Entry q2 = null;
            Entry y2 = this.tree;
            Entry z2 = null;
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
                        e2 = new Entry(k2, this.defRetValue);
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
                    e2 = new Entry(k2, this.defRetValue);
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

    private Entry parent(Entry e2) {
        Entry y2;
        if (e2 == this.tree) {
            return null;
        }
        Entry x2 = y2 = e2;
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
    public double remove(char k2) {
        int cmp;
        this.modified = false;
        if (this.tree == null) {
            return this.defRetValue;
        }
        Entry p2 = this.tree;
        Entry q2 = null;
        boolean dir = false;
        char kk2 = k2;
        while ((cmp = this.compare(kk2, p2.key)) != 0) {
            dir = cmp > 0;
            if (dir) {
                q2 = p2;
                if ((p2 = p2.right()) != null) continue;
                return this.defRetValue;
            }
            q2 = p2;
            if ((p2 = p2.left()) != null) continue;
            return this.defRetValue;
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
    public Double put(Character ok2, Double ov) {
        double oldValue = this.put(ok2.charValue(), (double)ov);
        return this.modified ? null : Double.valueOf(oldValue);
    }

    @Override
    @Deprecated
    public Double remove(Object ok2) {
        double oldValue = this.remove(((Character)ok2).charValue());
        return this.modified ? Double.valueOf(oldValue) : null;
    }

    @Override
    public boolean containsValue(double v2) {
        ValueIterator i2 = new ValueIterator();
        int j2 = this.count;
        while (j2-- != 0) {
            double ev2 = i2.nextDouble();
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
    public double get(char k2) {
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
    public ObjectSortedSet<Char2DoubleMap.Entry> char2DoubleEntrySet() {
        if (this.entries == null) {
            this.entries = new AbstractObjectSortedSet<Char2DoubleMap.Entry>(){
                final Comparator<? super Char2DoubleMap.Entry> comparator = new Comparator<Char2DoubleMap.Entry>(){

                    @Override
                    public int compare(Char2DoubleMap.Entry x2, Char2DoubleMap.Entry y2) {
                        return Char2DoubleAVLTreeMap.this.actualComparator.compare(x2.getCharKey(), y2.getCharKey());
                    }
                };

                @Override
                public Comparator<? super Char2DoubleMap.Entry> comparator() {
                    return this.comparator;
                }

                @Override
                public ObjectBidirectionalIterator<Char2DoubleMap.Entry> iterator() {
                    return new EntryIterator();
                }

                @Override
                public ObjectBidirectionalIterator<Char2DoubleMap.Entry> iterator(Char2DoubleMap.Entry from) {
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
                    if (e2.getValue() == null || !(e2.getValue() instanceof Double)) {
                        return false;
                    }
                    Entry f2 = Char2DoubleAVLTreeMap.this.findKey(((Character)e2.getKey()).charValue());
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
                    if (e2.getValue() == null || !(e2.getValue() instanceof Double)) {
                        return false;
                    }
                    Entry f2 = Char2DoubleAVLTreeMap.this.findKey(((Character)e2.getKey()).charValue());
                    if (f2 != null) {
                        Char2DoubleAVLTreeMap.this.remove(f2.key);
                    }
                    return f2 != null;
                }

                @Override
                public int size() {
                    return Char2DoubleAVLTreeMap.this.count;
                }

                @Override
                public void clear() {
                    Char2DoubleAVLTreeMap.this.clear();
                }

                @Override
                public Char2DoubleMap.Entry first() {
                    return Char2DoubleAVLTreeMap.this.firstEntry;
                }

                @Override
                public Char2DoubleMap.Entry last() {
                    return Char2DoubleAVLTreeMap.this.lastEntry;
                }

                @Override
                public ObjectSortedSet<Char2DoubleMap.Entry> subSet(Char2DoubleMap.Entry from, Char2DoubleMap.Entry to2) {
                    return Char2DoubleAVLTreeMap.this.subMap(from.getCharKey(), to2.getCharKey()).char2DoubleEntrySet();
                }

                @Override
                public ObjectSortedSet<Char2DoubleMap.Entry> headSet(Char2DoubleMap.Entry to2) {
                    return Char2DoubleAVLTreeMap.this.headMap(to2.getCharKey()).char2DoubleEntrySet();
                }

                @Override
                public ObjectSortedSet<Char2DoubleMap.Entry> tailSet(Char2DoubleMap.Entry from) {
                    return Char2DoubleAVLTreeMap.this.tailMap(from.getCharKey()).char2DoubleEntrySet();
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
    public DoubleCollection values() {
        if (this.values == null) {
            this.values = new AbstractDoubleCollection(){

                @Override
                public DoubleIterator iterator() {
                    return new ValueIterator();
                }

                @Override
                public boolean contains(double k2) {
                    return Char2DoubleAVLTreeMap.this.containsValue(k2);
                }

                @Override
                public int size() {
                    return Char2DoubleAVLTreeMap.this.count;
                }

                @Override
                public void clear() {
                    Char2DoubleAVLTreeMap.this.clear();
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
    public Char2DoubleSortedMap headMap(char to2) {
        return new Submap('\u0000', true, to2, false);
    }

    @Override
    public Char2DoubleSortedMap tailMap(char from) {
        return new Submap(from, false, '\u0000', true);
    }

    @Override
    public Char2DoubleSortedMap subMap(char from, char to2) {
        return new Submap(from, false, to2, false);
    }

    public Char2DoubleAVLTreeMap clone() {
        Char2DoubleAVLTreeMap c2;
        try {
            c2 = (Char2DoubleAVLTreeMap)super.clone();
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
            s2.writeDouble(e2.value);
        }
    }

    private Entry readTree(ObjectInputStream s2, int n2, Entry pred, Entry succ) throws IOException, ClassNotFoundException {
        if (n2 == 1) {
            Entry top = new Entry(s2.readChar(), s2.readDouble());
            top.pred(pred);
            top.succ(succ);
            return top;
        }
        if (n2 == 2) {
            Entry top = new Entry(s2.readChar(), s2.readDouble());
            top.right(new Entry(s2.readChar(), s2.readDouble()));
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
        top.key = s2.readChar();
        top.value = s2.readDouble();
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

    private static int checkTree(Entry e2) {
        return 0;
    }

    private final class Submap
    extends AbstractChar2DoubleSortedMap
    implements Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        char from;
        char to;
        boolean bottom;
        boolean top;
        protected transient ObjectSortedSet<Char2DoubleMap.Entry> entries;
        protected transient CharSortedSet keys;
        protected transient DoubleCollection values;

        public Submap(char from, boolean bottom, char to2, boolean top) {
            if (!bottom && !top && Char2DoubleAVLTreeMap.this.compare(from, to2) > 0) {
                throw new IllegalArgumentException("Start key (" + from + ") is larger than end key (" + to2 + ")");
            }
            this.from = from;
            this.bottom = bottom;
            this.to = to2;
            this.top = top;
            this.defRetValue = Char2DoubleAVLTreeMap.this.defRetValue;
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
            return !(!this.bottom && Char2DoubleAVLTreeMap.this.compare(k2, this.from) < 0 || !this.top && Char2DoubleAVLTreeMap.this.compare(k2, this.to) >= 0);
        }

        @Override
        public ObjectSortedSet<Char2DoubleMap.Entry> char2DoubleEntrySet() {
            if (this.entries == null) {
                this.entries = new AbstractObjectSortedSet<Char2DoubleMap.Entry>(){

                    @Override
                    public ObjectBidirectionalIterator<Char2DoubleMap.Entry> iterator() {
                        return new SubmapEntryIterator();
                    }

                    @Override
                    public ObjectBidirectionalIterator<Char2DoubleMap.Entry> iterator(Char2DoubleMap.Entry from) {
                        return new SubmapEntryIterator(from.getCharKey());
                    }

                    @Override
                    public Comparator<? super Char2DoubleMap.Entry> comparator() {
                        return Char2DoubleAVLTreeMap.this.entrySet().comparator();
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
                        if (e2.getValue() == null || !(e2.getValue() instanceof Double)) {
                            return false;
                        }
                        Entry f2 = Char2DoubleAVLTreeMap.this.findKey(((Character)e2.getKey()).charValue());
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
                        if (e2.getValue() == null || !(e2.getValue() instanceof Double)) {
                            return false;
                        }
                        Entry f2 = Char2DoubleAVLTreeMap.this.findKey(((Character)e2.getKey()).charValue());
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
                    public Char2DoubleMap.Entry first() {
                        return Submap.this.firstEntry();
                    }

                    @Override
                    public Char2DoubleMap.Entry last() {
                        return Submap.this.lastEntry();
                    }

                    @Override
                    public ObjectSortedSet<Char2DoubleMap.Entry> subSet(Char2DoubleMap.Entry from, Char2DoubleMap.Entry to2) {
                        return Submap.this.subMap(from.getCharKey(), to2.getCharKey()).char2DoubleEntrySet();
                    }

                    @Override
                    public ObjectSortedSet<Char2DoubleMap.Entry> headSet(Char2DoubleMap.Entry to2) {
                        return Submap.this.headMap(to2.getCharKey()).char2DoubleEntrySet();
                    }

                    @Override
                    public ObjectSortedSet<Char2DoubleMap.Entry> tailSet(Char2DoubleMap.Entry from) {
                        return Submap.this.tailMap(from.getCharKey()).char2DoubleEntrySet();
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
        public DoubleCollection values() {
            if (this.values == null) {
                this.values = new AbstractDoubleCollection(){

                    @Override
                    public DoubleIterator iterator() {
                        return new SubmapValueIterator();
                    }

                    @Override
                    public boolean contains(double k2) {
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
            return this.in(k2) && Char2DoubleAVLTreeMap.this.containsKey(k2);
        }

        @Override
        public boolean containsValue(double v2) {
            SubmapIterator i2 = new SubmapIterator();
            while (i2.hasNext()) {
                double ev2 = i2.nextEntry().value;
                if (ev2 != v2) continue;
                return true;
            }
            return false;
        }

        @Override
        public double get(char k2) {
            Entry e2;
            char kk2 = k2;
            return this.in(kk2) && (e2 = Char2DoubleAVLTreeMap.this.findKey(kk2)) != null ? e2.value : this.defRetValue;
        }

        @Override
        public double put(char k2, double v2) {
            Char2DoubleAVLTreeMap.this.modified = false;
            if (!this.in(k2)) {
                throw new IllegalArgumentException("Key (" + k2 + ") out of range [" + (this.bottom ? "-" : String.valueOf(this.from)) + ", " + (this.top ? "-" : String.valueOf(this.to)) + ")");
            }
            double oldValue = Char2DoubleAVLTreeMap.this.put(k2, v2);
            return Char2DoubleAVLTreeMap.this.modified ? this.defRetValue : oldValue;
        }

        @Override
        @Deprecated
        public Double put(Character ok2, Double ov) {
            double oldValue = this.put(ok2.charValue(), (double)ov);
            return Char2DoubleAVLTreeMap.this.modified ? null : Double.valueOf(oldValue);
        }

        @Override
        public double remove(char k2) {
            Char2DoubleAVLTreeMap.this.modified = false;
            if (!this.in(k2)) {
                return this.defRetValue;
            }
            double oldValue = Char2DoubleAVLTreeMap.this.remove(k2);
            return Char2DoubleAVLTreeMap.this.modified ? oldValue : this.defRetValue;
        }

        @Override
        @Deprecated
        public Double remove(Object ok2) {
            double oldValue = this.remove(((Character)ok2).charValue());
            return Char2DoubleAVLTreeMap.this.modified ? Double.valueOf(oldValue) : null;
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
            return Char2DoubleAVLTreeMap.this.actualComparator;
        }

        @Override
        public Char2DoubleSortedMap headMap(char to2) {
            if (this.top) {
                return new Submap(this.from, this.bottom, to2, false);
            }
            return Char2DoubleAVLTreeMap.this.compare(to2, this.to) < 0 ? new Submap(this.from, this.bottom, to2, false) : this;
        }

        @Override
        public Char2DoubleSortedMap tailMap(char from) {
            if (this.bottom) {
                return new Submap(from, false, this.to, this.top);
            }
            return Char2DoubleAVLTreeMap.this.compare(from, this.from) > 0 ? new Submap(from, false, this.to, this.top) : this;
        }

        @Override
        public Char2DoubleSortedMap subMap(char from, char to2) {
            if (this.top && this.bottom) {
                return new Submap(from, false, to2, false);
            }
            if (!this.top) {
                char c2 = to2 = Char2DoubleAVLTreeMap.this.compare(to2, this.to) < 0 ? to2 : this.to;
            }
            if (!this.bottom) {
                char c3 = from = Char2DoubleAVLTreeMap.this.compare(from, this.from) > 0 ? from : this.from;
            }
            if (!this.top && !this.bottom && from == this.from && to2 == this.to) {
                return this;
            }
            return new Submap(from, false, to2, false);
        }

        public Entry firstEntry() {
            Entry e2;
            if (Char2DoubleAVLTreeMap.this.tree == null) {
                return null;
            }
            if (this.bottom) {
                e2 = Char2DoubleAVLTreeMap.this.firstEntry;
            } else {
                e2 = Char2DoubleAVLTreeMap.this.locateKey(this.from);
                if (Char2DoubleAVLTreeMap.this.compare(e2.key, this.from) < 0) {
                    e2 = e2.next();
                }
            }
            if (e2 == null || !this.top && Char2DoubleAVLTreeMap.this.compare(e2.key, this.to) >= 0) {
                return null;
            }
            return e2;
        }

        public Entry lastEntry() {
            Entry e2;
            if (Char2DoubleAVLTreeMap.this.tree == null) {
                return null;
            }
            if (this.top) {
                e2 = Char2DoubleAVLTreeMap.this.lastEntry;
            } else {
                e2 = Char2DoubleAVLTreeMap.this.locateKey(this.to);
                if (Char2DoubleAVLTreeMap.this.compare(e2.key, this.to) >= 0) {
                    e2 = e2.prev();
                }
            }
            if (e2 == null || !this.bottom && Char2DoubleAVLTreeMap.this.compare(e2.key, this.from) < 0) {
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
        implements DoubleListIterator {
            private SubmapValueIterator() {
            }

            @Override
            public double nextDouble() {
                return this.nextEntry().value;
            }

            @Override
            public double previousDouble() {
                return this.previousEntry().value;
            }

            @Override
            public void set(double v2) {
                throw new UnsupportedOperationException();
            }

            @Override
            public void add(double v2) {
                throw new UnsupportedOperationException();
            }

            @Override
            public Double next() {
                return this.nextEntry().value;
            }

            @Override
            public Double previous() {
                return this.previousEntry().value;
            }

            @Override
            public void set(Double ok2) {
                throw new UnsupportedOperationException();
            }

            @Override
            public void add(Double ok2) {
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
        implements ObjectListIterator<Char2DoubleMap.Entry> {
            SubmapEntryIterator() {
            }

            SubmapEntryIterator(char k2) {
                super(k2);
            }

            @Override
            public Char2DoubleMap.Entry next() {
                return this.nextEntry();
            }

            @Override
            public Char2DoubleMap.Entry previous() {
                return this.previousEntry();
            }

            @Override
            public void set(Char2DoubleMap.Entry ok2) {
                throw new UnsupportedOperationException();
            }

            @Override
            public void add(Char2DoubleMap.Entry ok2) {
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
                if (!submap.bottom && submap.Char2DoubleAVLTreeMap.this.compare(k2, this.next.key) < 0) {
                    this.prev = null;
                    return;
                }
                if (!submap.top) {
                    this.prev = submap.lastEntry();
                    if (submap.Char2DoubleAVLTreeMap.this.compare(k2, this.prev.key) >= 0) {
                        this.next = null;
                        return;
                    }
                }
                this.next = submap.Char2DoubleAVLTreeMap.this.locateKey(k2);
                if (submap.Char2DoubleAVLTreeMap.this.compare(this.next.key, k2) <= 0) {
                    this.prev = this.next;
                    this.next = this.next.next();
                    return;
                }
                this.prev = this.next.prev();
            }

            @Override
            void updatePrevious() {
                this.prev = this.prev.prev();
                if (!Submap.this.bottom && this.prev != null && Char2DoubleAVLTreeMap.this.compare(this.prev.key, Submap.this.from) < 0) {
                    this.prev = null;
                }
            }

            @Override
            void updateNext() {
                this.next = this.next.next();
                if (!Submap.this.top && this.next != null && Char2DoubleAVLTreeMap.this.compare(this.next.key, Submap.this.to) >= 0) {
                    this.next = null;
                }
            }
        }

        private class KeySet
        extends AbstractChar2DoubleSortedMap.KeySet {
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
    implements DoubleListIterator {
        private ValueIterator() {
        }

        @Override
        public double nextDouble() {
            return this.nextEntry().value;
        }

        @Override
        public double previousDouble() {
            return this.previousEntry().value;
        }

        @Override
        public void set(double v2) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void add(double v2) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Double next() {
            return this.nextEntry().value;
        }

        @Override
        public Double previous() {
            return this.previousEntry().value;
        }

        @Override
        public void set(Double ok2) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void add(Double ok2) {
            throw new UnsupportedOperationException();
        }
    }

    private class KeySet
    extends AbstractChar2DoubleSortedMap.KeySet {
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
    implements ObjectListIterator<Char2DoubleMap.Entry> {
        EntryIterator() {
        }

        EntryIterator(char k2) {
            super(k2);
        }

        @Override
        public Char2DoubleMap.Entry next() {
            return this.nextEntry();
        }

        @Override
        public Char2DoubleMap.Entry previous() {
            return this.previousEntry();
        }

        @Override
        public void set(Char2DoubleMap.Entry ok2) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void add(Char2DoubleMap.Entry ok2) {
            throw new UnsupportedOperationException();
        }
    }

    private class TreeIterator {
        Entry prev;
        Entry next;
        Entry curr;
        int index = 0;

        TreeIterator() {
            this.next = Char2DoubleAVLTreeMap.this.firstEntry;
        }

        TreeIterator(char k2) {
            this.next = Char2DoubleAVLTreeMap.this.locateKey(k2);
            if (this.next != null) {
                if (Char2DoubleAVLTreeMap.this.compare(this.next.key, k2) <= 0) {
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
            Char2DoubleAVLTreeMap.this.remove(this.curr.key);
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
    Char2DoubleMap.Entry {
        private static final int SUCC_MASK = Integer.MIN_VALUE;
        private static final int PRED_MASK = 0x40000000;
        private static final int BALANCE_MASK = 255;
        char key;
        double value;
        Entry left;
        Entry right;
        int info;

        Entry() {
        }

        Entry(char k2, double v2) {
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
        public Double getValue() {
            return this.value;
        }

        @Override
        public double getDoubleValue() {
            return this.value;
        }

        @Override
        public double setValue(double value) {
            double oldValue = this.value;
            this.value = value;
            return oldValue;
        }

        @Override
        public Double setValue(Double value) {
            return this.setValue((double)value);
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
            return this.key == ((Character)e2.getKey()).charValue() && this.value == (Double)e2.getValue();
        }

        @Override
        public int hashCode() {
            return this.key ^ HashCommon.double2int(this.value);
        }

        public String toString() {
            return this.key + "=>" + this.value;
        }
    }
}

