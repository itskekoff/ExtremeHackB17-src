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

public class ByteAVLTreeSet
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

    public ByteAVLTreeSet() {
        this.allocatePaths();
        this.tree = null;
        this.count = 0;
    }

    private void setActualComparator() {
        this.actualComparator = this.storedComparator == null || this.storedComparator instanceof ByteComparator ? (ByteComparator)this.storedComparator : new ByteComparator(){

            @Override
            public int compare(byte k1, byte k2) {
                return ByteAVLTreeSet.this.storedComparator.compare((Byte)k1, (Byte)k2);
            }

            @Override
            public int compare(Byte ok1, Byte ok2) {
                return ByteAVLTreeSet.this.storedComparator.compare(ok1, ok2);
            }
        };
    }

    public ByteAVLTreeSet(Comparator<? super Byte> c2) {
        this();
        this.storedComparator = c2;
        this.setActualComparator();
    }

    public ByteAVLTreeSet(Collection<? extends Byte> c2) {
        this();
        this.addAll(c2);
    }

    public ByteAVLTreeSet(SortedSet<Byte> s2) {
        this(s2.comparator());
        this.addAll((Collection<? extends Byte>)s2);
    }

    public ByteAVLTreeSet(ByteCollection c2) {
        this();
        this.addAll(c2);
    }

    public ByteAVLTreeSet(ByteSortedSet s2) {
        this(s2.comparator());
        this.addAll(s2);
    }

    public ByteAVLTreeSet(ByteIterator i2) {
        this.allocatePaths();
        while (i2.hasNext()) {
            this.add(i2.nextByte());
        }
    }

    public ByteAVLTreeSet(Iterator<?> i2) {
        this(ByteIterators.asByteIterator(i2));
    }

    public ByteAVLTreeSet(byte[] a2, int offset, int length, Comparator<? super Byte> c2) {
        this(c2);
        ByteArrays.ensureOffsetLength(a2, offset, length);
        for (int i2 = 0; i2 < length; ++i2) {
            this.add(a2[offset + i2]);
        }
    }

    public ByteAVLTreeSet(byte[] a2, int offset, int length) {
        this(a2, offset, length, null);
    }

    public ByteAVLTreeSet(byte[] a2) {
        this();
        int i2 = a2.length;
        while (i2-- != 0) {
            this.add(a2[i2]);
        }
    }

    public ByteAVLTreeSet(byte[] a2, Comparator<? super Byte> c2) {
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
        this.dirPath = new boolean[48];
    }

    @Override
    public boolean add(byte k2) {
        if (this.tree == null) {
            ++this.count;
            this.lastEntry = this.firstEntry = new Entry(k2);
            this.tree = this.firstEntry;
        } else {
            Entry p2 = this.tree;
            Entry q2 = null;
            Entry y2 = this.tree;
            Entry z2 = null;
            Entry e2 = null;
            Entry w2 = null;
            int i2 = 0;
            while (true) {
                int cmp;
                if ((cmp = this.compare(k2, p2.key)) == 0) {
                    return false;
                }
                if (p2.balance() != 0) {
                    i2 = 0;
                    z2 = q2;
                    y2 = p2;
                }
                if (this.dirPath[i2++] = cmp > 0) {
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
                    q2 = p2;
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
                return true;
            }
            if (z2 == null) {
                this.tree = w2;
            } else if (z2.left == y2) {
                z2.left = w2;
            } else {
                z2.right = w2;
            }
        }
        return true;
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
    public boolean rem(byte k2) {
        int cmp;
        if (this.tree == null) {
            return false;
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
                return false;
            }
            q2 = p2;
            if ((p2 = p2.left()) != null) continue;
            return false;
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
        --this.count;
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
        ByteAVLTreeSet c2;
        try {
            c2 = (ByteAVLTreeSet)super.clone();
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
            return top;
        }
        if (n2 == 2) {
            Entry top = new Entry(s2.readByte());
            top.right(new Entry(s2.readByte()));
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
            if (!bottom && !top && ByteAVLTreeSet.this.compare(from, to2) > 0) {
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
            return !(!this.bottom && ByteAVLTreeSet.this.compare(k2, this.from) < 0 || !this.top && ByteAVLTreeSet.this.compare(k2, this.to) >= 0);
        }

        @Override
        public boolean contains(byte k2) {
            return this.in(k2) && ByteAVLTreeSet.this.contains(k2);
        }

        @Override
        public boolean add(byte k2) {
            if (!this.in(k2)) {
                throw new IllegalArgumentException("Element (" + k2 + ") out of range [" + (this.bottom ? "-" : String.valueOf(this.from)) + ", " + (this.top ? "-" : String.valueOf(this.to)) + ")");
            }
            return ByteAVLTreeSet.this.add(k2);
        }

        @Override
        public boolean rem(byte k2) {
            if (!this.in(k2)) {
                return false;
            }
            return ByteAVLTreeSet.this.rem(k2);
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
            return ByteAVLTreeSet.this.actualComparator;
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
            return ByteAVLTreeSet.this.compare(to2, this.to) < 0 ? new Subset(this.from, this.bottom, to2, false) : this;
        }

        @Override
        public ByteSortedSet tailSet(byte from) {
            if (this.bottom) {
                return new Subset(from, false, this.to, this.top);
            }
            return ByteAVLTreeSet.this.compare(from, this.from) > 0 ? new Subset(from, false, this.to, this.top) : this;
        }

        @Override
        public ByteSortedSet subSet(byte from, byte to2) {
            if (this.top && this.bottom) {
                return new Subset(from, false, to2, false);
            }
            if (!this.top) {
                byte by2 = to2 = ByteAVLTreeSet.this.compare(to2, this.to) < 0 ? to2 : this.to;
            }
            if (!this.bottom) {
                byte by3 = from = ByteAVLTreeSet.this.compare(from, this.from) > 0 ? from : this.from;
            }
            if (!this.top && !this.bottom && from == this.from && to2 == this.to) {
                return this;
            }
            return new Subset(from, false, to2, false);
        }

        public Entry firstEntry() {
            Entry e2;
            if (ByteAVLTreeSet.this.tree == null) {
                return null;
            }
            if (this.bottom) {
                e2 = ByteAVLTreeSet.this.firstEntry;
            } else {
                e2 = ByteAVLTreeSet.this.locateKey(this.from);
                if (ByteAVLTreeSet.this.compare(e2.key, this.from) < 0) {
                    e2 = e2.next();
                }
            }
            if (e2 == null || !this.top && ByteAVLTreeSet.this.compare(e2.key, this.to) >= 0) {
                return null;
            }
            return e2;
        }

        public Entry lastEntry() {
            Entry e2;
            if (ByteAVLTreeSet.this.tree == null) {
                return null;
            }
            if (this.top) {
                e2 = ByteAVLTreeSet.this.lastEntry;
            } else {
                e2 = ByteAVLTreeSet.this.locateKey(this.to);
                if (ByteAVLTreeSet.this.compare(e2.key, this.to) >= 0) {
                    e2 = e2.prev();
                }
            }
            if (e2 == null || !this.bottom && ByteAVLTreeSet.this.compare(e2.key, this.from) < 0) {
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
                if (!subset.bottom && subset.ByteAVLTreeSet.this.compare(k2, this.next.key) < 0) {
                    this.prev = null;
                    return;
                }
                if (!subset.top) {
                    this.prev = subset.lastEntry();
                    if (subset.ByteAVLTreeSet.this.compare(k2, this.prev.key) >= 0) {
                        this.next = null;
                        return;
                    }
                }
                this.next = subset.ByteAVLTreeSet.this.locateKey(k2);
                if (subset.ByteAVLTreeSet.this.compare(this.next.key, k2) <= 0) {
                    this.prev = this.next;
                    this.next = this.next.next();
                    return;
                }
                this.prev = this.next.prev();
            }

            @Override
            void updatePrevious() {
                this.prev = this.prev.prev();
                if (!Subset.this.bottom && this.prev != null && ByteAVLTreeSet.this.compare(this.prev.key, Subset.this.from) < 0) {
                    this.prev = null;
                }
            }

            @Override
            void updateNext() {
                this.next = this.next.next();
                if (!Subset.this.top && this.next != null && ByteAVLTreeSet.this.compare(this.next.key, Subset.this.to) >= 0) {
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
            this.next = ByteAVLTreeSet.this.firstEntry;
        }

        SetIterator(byte k2) {
            this.next = ByteAVLTreeSet.this.locateKey(k2);
            if (this.next != null) {
                if (ByteAVLTreeSet.this.compare(this.next.key, k2) <= 0) {
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
            ByteAVLTreeSet.this.rem(this.curr.key);
            this.curr = null;
        }
    }

    private static final class Entry
    implements Cloneable {
        private static final int SUCC_MASK = Integer.MIN_VALUE;
        private static final int PRED_MASK = 0x40000000;
        private static final int BALANCE_MASK = 255;
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

