package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.bytes.AbstractByteListIterator;
import it.unimi.dsi.fastutil.bytes.AbstractByteSortedSet;
import it.unimi.dsi.fastutil.bytes.ByteArrays;
import it.unimi.dsi.fastutil.bytes.ByteBidirectionalIterator;
import it.unimi.dsi.fastutil.bytes.ByteCollection;
import it.unimi.dsi.fastutil.bytes.ByteComparator;
import it.unimi.dsi.fastutil.bytes.ByteIterator;
import it.unimi.dsi.fastutil.bytes.ByteIterators;
import it.unimi.dsi.fastutil.bytes.ByteSortedSet;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.SortedSet;

public class ByteRBTreeSet
extends AbstractByteSortedSet
implements Serializable,
Cloneable,
ByteSortedSet {
    protected transient Entry tree;
    protected int count;
    protected transient Entry firstEntry;
    protected transient Entry lastEntry;
    protected Comparator<? super Byte> storedComparator;
    protected transient ByteComparator actualComparator;
    private static final long serialVersionUID = -7046029254386353130L;
    private static final boolean ASSERTS = false;
    private transient boolean[] dirPath;
    private transient Entry[] nodePath;

    public ByteRBTreeSet() {
        this.allocatePaths();
        this.tree = null;
        this.count = 0;
    }

    private void setActualComparator() {
        this.actualComparator = this.storedComparator == null || this.storedComparator instanceof ByteComparator ? (ByteComparator)this.storedComparator : new ByteComparator(){

            @Override
            public int compare(byte k1, byte k2) {
                return ByteRBTreeSet.this.storedComparator.compare((Byte)k1, (Byte)k2);
            }

            @Override
            public int compare(Byte ok1, Byte ok2) {
                return ByteRBTreeSet.this.storedComparator.compare(ok1, ok2);
            }
        };
    }

    public ByteRBTreeSet(Comparator<? super Byte> c2) {
        this();
        this.storedComparator = c2;
        this.setActualComparator();
    }

    public ByteRBTreeSet(Collection<? extends Byte> c2) {
        this();
        this.addAll(c2);
    }

    public ByteRBTreeSet(SortedSet<Byte> s2) {
        this(s2.comparator());
        this.addAll((Collection<? extends Byte>)s2);
    }

    public ByteRBTreeSet(ByteCollection c2) {
        this();
        this.addAll(c2);
    }

    public ByteRBTreeSet(ByteSortedSet s2) {
        this(s2.comparator());
        this.addAll(s2);
    }

    public ByteRBTreeSet(ByteIterator i2) {
        this.allocatePaths();
        while (i2.hasNext()) {
            this.add(i2.nextByte());
        }
    }

    public ByteRBTreeSet(Iterator<?> i2) {
        this(ByteIterators.asByteIterator(i2));
    }

    public ByteRBTreeSet(byte[] a2, int offset, int length, Comparator<? super Byte> c2) {
        this(c2);
        ByteArrays.ensureOffsetLength(a2, offset, length);
        for (int i2 = 0; i2 < length; ++i2) {
            this.add(a2[offset + i2]);
        }
    }

    public ByteRBTreeSet(byte[] a2, int offset, int length) {
        this(a2, offset, length, null);
    }

    public ByteRBTreeSet(byte[] a2) {
        this();
        int i2 = a2.length;
        while (i2-- != 0) {
            this.add(a2[i2]);
        }
    }

    public ByteRBTreeSet(byte[] a2, Comparator<? super Byte> c2) {
        this(c2);
        int i2 = a2.length;
        while (i2-- != 0) {
            this.add(a2[i2]);
        }
    }

    final int compare(byte k1, byte k2) {
        return this.actualComparator == null ? Byte.compare(k1, k2) : this.actualComparator.compare(k1, k2);
    }

    private Entry findKey(byte k2) {
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

    @Override
    public boolean add(byte k2) {
        int maxDepth = 0;
        if (this.tree == null) {
            ++this.count;
            this.lastEntry = this.firstEntry = new Entry(k2);
            this.tree = this.firstEntry;
        } else {
            Entry p2 = this.tree;
            int i2 = 0;
            while (true) {
                Entry e2;
                int cmp;
                if ((cmp = this.compare(k2, p2.key)) == 0) {
                    while (i2-- != 0) {
                        this.nodePath[i2] = null;
                    }
                    return false;
                }
                this.nodePath[i2] = p2;
                this.dirPath[i2++] = cmp > 0;
                if (this.dirPath[i2++]) {
                    if (p2.succ()) {
                        ++this.count;
                        e2 = new Entry(k2);
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
                    e2 = new Entry(k2);
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
        return true;
    }

    /*
     * Unable to fully structure code
     * Enabled aggressive block sorting
     * Lifted jumps to return sites
     */
    @Override
    public boolean rem(byte k) {
        block69: {
            block66: {
                block68: {
                    block64: {
                        block67: {
                            block65: {
                                block62: {
                                    block63: {
                                        if (this.tree == null) {
                                            return false;
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
                                            return false;
                                        } while ((p = p.left()) != null);
                                        while (i-- != 0) {
                                            this.nodePath[i] = null;
                                        }
                                        return false;
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
                if (this.dirPath[i - 1]) ** GOTO lbl159
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
lbl159:
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
        --this.count;
        while (maxDepth-- != 0) {
            this.nodePath[maxDepth] = null;
        }
        return true;
    }

    @Override
    public boolean contains(byte k2) {
        return this.findKey(k2) != null;
    }

    @Override
    public void clear() {
        this.count = 0;
        this.tree = null;
        this.lastEntry = null;
        this.firstEntry = null;
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
    public byte firstByte() {
        if (this.tree == null) {
            throw new NoSuchElementException();
        }
        return this.firstEntry.key;
    }

    @Override
    public byte lastByte() {
        if (this.tree == null) {
            throw new NoSuchElementException();
        }
        return this.lastEntry.key;
    }

    @Override
    public ByteBidirectionalIterator iterator() {
        return new SetIterator();
    }

    @Override
    public ByteBidirectionalIterator iterator(byte from) {
        return new SetIterator(from);
    }

    @Override
    public ByteComparator comparator() {
        return this.actualComparator;
    }

    @Override
    public ByteSortedSet headSet(byte to2) {
        return new Subset(0, true, to2, false);
    }

    @Override
    public ByteSortedSet tailSet(byte from) {
        return new Subset(from, false, 0, true);
    }

    @Override
    public ByteSortedSet subSet(byte from, byte to2) {
        return new Subset(from, false, to2, false);
    }

    public Object clone() {
        ByteRBTreeSet c2;
        try {
            c2 = (ByteRBTreeSet)super.clone();
        }
        catch (CloneNotSupportedException cantHappen) {
            throw new InternalError();
        }
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
        SetIterator i2 = new SetIterator();
        s2.defaultWriteObject();
        while (n2-- != 0) {
            s2.writeByte(i2.nextByte());
        }
    }

    private Entry readTree(ObjectInputStream s2, int n2, Entry pred, Entry succ) throws IOException, ClassNotFoundException {
        if (n2 == 1) {
            Entry top = new Entry(s2.readByte());
            top.pred(pred);
            top.succ(succ);
            top.black(true);
            return top;
        }
        if (n2 == 2) {
            Entry top = new Entry(s2.readByte());
            top.black(true);
            top.right(new Entry(s2.readByte()));
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

    private int checkTree(Entry e2, int d2, int D) {
        return 0;
    }

    private final class Subset
    extends AbstractByteSortedSet
    implements Serializable,
    ByteSortedSet {
        private static final long serialVersionUID = -7046029254386353129L;
        byte from;
        byte to;
        boolean bottom;
        boolean top;

        public Subset(byte from, boolean bottom, byte to2, boolean top) {
            if (!bottom && !top && ByteRBTreeSet.this.compare(from, to2) > 0) {
                throw new IllegalArgumentException("Start element (" + from + ") is larger than end element (" + to2 + ")");
            }
            this.from = from;
            this.bottom = bottom;
            this.to = to2;
            this.top = top;
        }

        @Override
        public void clear() {
            SubsetIterator i2 = new SubsetIterator();
            while (i2.hasNext()) {
                i2.nextByte();
                i2.remove();
            }
        }

        final boolean in(byte k2) {
            return !(!this.bottom && ByteRBTreeSet.this.compare(k2, this.from) < 0 || !this.top && ByteRBTreeSet.this.compare(k2, this.to) >= 0);
        }

        @Override
        public boolean contains(byte k2) {
            return this.in(k2) && ByteRBTreeSet.this.contains(k2);
        }

        @Override
        public boolean add(byte k2) {
            if (!this.in(k2)) {
                throw new IllegalArgumentException("Element (" + k2 + ") out of range [" + (this.bottom ? "-" : String.valueOf(this.from)) + ", " + (this.top ? "-" : String.valueOf(this.to)) + ")");
            }
            return ByteRBTreeSet.this.add(k2);
        }

        @Override
        public boolean rem(byte k2) {
            if (!this.in(k2)) {
                return false;
            }
            return ByteRBTreeSet.this.rem(k2);
        }

        @Override
        public int size() {
            SubsetIterator i2 = new SubsetIterator();
            int n2 = 0;
            while (i2.hasNext()) {
                ++n2;
                i2.nextByte();
            }
            return n2;
        }

        @Override
        public boolean isEmpty() {
            return !new SubsetIterator().hasNext();
        }

        @Override
        public ByteComparator comparator() {
            return ByteRBTreeSet.this.actualComparator;
        }

        @Override
        public ByteBidirectionalIterator iterator() {
            return new SubsetIterator();
        }

        @Override
        public ByteBidirectionalIterator iterator(byte from) {
            return new SubsetIterator(from);
        }

        @Override
        public ByteSortedSet headSet(byte to2) {
            if (this.top) {
                return new Subset(this.from, this.bottom, to2, false);
            }
            return ByteRBTreeSet.this.compare(to2, this.to) < 0 ? new Subset(this.from, this.bottom, to2, false) : this;
        }

        @Override
        public ByteSortedSet tailSet(byte from) {
            if (this.bottom) {
                return new Subset(from, false, this.to, this.top);
            }
            return ByteRBTreeSet.this.compare(from, this.from) > 0 ? new Subset(from, false, this.to, this.top) : this;
        }

        @Override
        public ByteSortedSet subSet(byte from, byte to2) {
            if (this.top && this.bottom) {
                return new Subset(from, false, to2, false);
            }
            if (!this.top) {
                byte by2 = to2 = ByteRBTreeSet.this.compare(to2, this.to) < 0 ? to2 : this.to;
            }
            if (!this.bottom) {
                byte by3 = from = ByteRBTreeSet.this.compare(from, this.from) > 0 ? from : this.from;
            }
            if (!this.top && !this.bottom && from == this.from && to2 == this.to) {
                return this;
            }
            return new Subset(from, false, to2, false);
        }

        public Entry firstEntry() {
            Entry e2;
            if (ByteRBTreeSet.this.tree == null) {
                return null;
            }
            if (this.bottom) {
                e2 = ByteRBTreeSet.this.firstEntry;
            } else {
                e2 = ByteRBTreeSet.this.locateKey(this.from);
                if (ByteRBTreeSet.this.compare(e2.key, this.from) < 0) {
                    e2 = e2.next();
                }
            }
            if (e2 == null || !this.top && ByteRBTreeSet.this.compare(e2.key, this.to) >= 0) {
                return null;
            }
            return e2;
        }

        public Entry lastEntry() {
            Entry e2;
            if (ByteRBTreeSet.this.tree == null) {
                return null;
            }
            if (this.top) {
                e2 = ByteRBTreeSet.this.lastEntry;
            } else {
                e2 = ByteRBTreeSet.this.locateKey(this.to);
                if (ByteRBTreeSet.this.compare(e2.key, this.to) >= 0) {
                    e2 = e2.prev();
                }
            }
            if (e2 == null || !this.bottom && ByteRBTreeSet.this.compare(e2.key, this.from) < 0) {
                return null;
            }
            return e2;
        }

        @Override
        public byte firstByte() {
            Entry e2 = this.firstEntry();
            if (e2 == null) {
                throw new NoSuchElementException();
            }
            return e2.key;
        }

        @Override
        public byte lastByte() {
            Entry e2 = this.lastEntry();
            if (e2 == null) {
                throw new NoSuchElementException();
            }
            return e2.key;
        }

        private final class SubsetIterator
        extends SetIterator {
            SubsetIterator() {
                this.next = Subset.this.firstEntry();
            }

            /*
             * Enabled aggressive block sorting
             */
            SubsetIterator(byte k2) {
                this();
                if (this.next == null) return;
                if (!subset.bottom && subset.ByteRBTreeSet.this.compare(k2, this.next.key) < 0) {
                    this.prev = null;
                    return;
                }
                if (!subset.top) {
                    this.prev = subset.lastEntry();
                    if (subset.ByteRBTreeSet.this.compare(k2, this.prev.key) >= 0) {
                        this.next = null;
                        return;
                    }
                }
                this.next = subset.ByteRBTreeSet.this.locateKey(k2);
                if (subset.ByteRBTreeSet.this.compare(this.next.key, k2) <= 0) {
                    this.prev = this.next;
                    this.next = this.next.next();
                    return;
                }
                this.prev = this.next.prev();
            }

            @Override
            void updatePrevious() {
                this.prev = this.prev.prev();
                if (!Subset.this.bottom && this.prev != null && ByteRBTreeSet.this.compare(this.prev.key, Subset.this.from) < 0) {
                    this.prev = null;
                }
            }

            @Override
            void updateNext() {
                this.next = this.next.next();
                if (!Subset.this.top && this.next != null && ByteRBTreeSet.this.compare(this.next.key, Subset.this.to) >= 0) {
                    this.next = null;
                }
            }
        }
    }

    private class SetIterator
    extends AbstractByteListIterator {
        Entry prev;
        Entry next;
        Entry curr;
        int index = 0;

        SetIterator() {
            this.next = ByteRBTreeSet.this.firstEntry;
        }

        SetIterator(byte k2) {
            this.next = ByteRBTreeSet.this.locateKey(k2);
            if (this.next != null) {
                if (ByteRBTreeSet.this.compare(this.next.key, k2) <= 0) {
                    this.prev = this.next;
                    this.next = this.next.next();
                } else {
                    this.prev = this.next.prev();
                }
            }
        }

        @Override
        public boolean hasNext() {
            return this.next != null;
        }

        @Override
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

        @Override
        public byte nextByte() {
            return this.nextEntry().key;
        }

        @Override
        public byte previousByte() {
            return this.previousEntry().key;
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

        @Override
        public int nextIndex() {
            return this.index;
        }

        @Override
        public int previousIndex() {
            return this.index - 1;
        }

        @Override
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
            ByteRBTreeSet.this.rem(this.curr.key);
            this.curr = null;
        }
    }

    private static final class Entry
    implements Cloneable {
        private static final int BLACK_MASK = 1;
        private static final int SUCC_MASK = Integer.MIN_VALUE;
        private static final int PRED_MASK = 0x40000000;
        byte key;
        Entry left;
        Entry right;
        int info;

        Entry() {
        }

        Entry(byte k2) {
            this.key = k2;
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

        public Entry clone() {
            Entry c2;
            try {
                c2 = (Entry)super.clone();
            }
            catch (CloneNotSupportedException cantHappen) {
                throw new InternalError();
            }
            c2.key = this.key;
            c2.info = this.info;
            return c2;
        }

        public boolean equals(Object o2) {
            if (!(o2 instanceof Entry)) {
                return false;
            }
            Entry e2 = (Entry)o2;
            return this.key == e2.key;
        }

        public int hashCode() {
            return this.key;
        }

        public String toString() {
            return String.valueOf(this.key);
        }
    }
}

