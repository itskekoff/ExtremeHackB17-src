package it.unimi.dsi.fastutil.booleans;

import it.unimi.dsi.fastutil.booleans.AbstractBooleanCollection;
import it.unimi.dsi.fastutil.booleans.AbstractBooleanListIterator;
import it.unimi.dsi.fastutil.booleans.BooleanCollection;
import it.unimi.dsi.fastutil.booleans.BooleanList;
import it.unimi.dsi.fastutil.booleans.BooleanListIterator;
import it.unimi.dsi.fastutil.booleans.BooleanStack;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;

public abstract class AbstractBooleanList
extends AbstractBooleanCollection
implements BooleanList,
BooleanStack {
    protected AbstractBooleanList() {
    }

    protected void ensureIndex(int index) {
        if (index < 0) {
            throw new IndexOutOfBoundsException("Index (" + index + ") is negative");
        }
        if (index > this.size()) {
            throw new IndexOutOfBoundsException("Index (" + index + ") is greater than list size (" + this.size() + ")");
        }
    }

    protected void ensureRestrictedIndex(int index) {
        if (index < 0) {
            throw new IndexOutOfBoundsException("Index (" + index + ") is negative");
        }
        if (index >= this.size()) {
            throw new IndexOutOfBoundsException("Index (" + index + ") is greater than or equal to list size (" + this.size() + ")");
        }
    }

    @Override
    public void add(int index, boolean k2) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean add(boolean k2) {
        this.add(this.size(), k2);
        return true;
    }

    @Override
    public boolean removeBoolean(int i2) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean set(int index, boolean k2) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(int index, Collection<? extends Boolean> c2) {
        this.ensureIndex(index);
        int n2 = c2.size();
        if (n2 == 0) {
            return false;
        }
        Iterator<? extends Boolean> i2 = c2.iterator();
        while (n2-- != 0) {
            this.add(index++, i2.next());
        }
        return true;
    }

    @Override
    public boolean addAll(Collection<? extends Boolean> c2) {
        return this.addAll(this.size(), c2);
    }

    @Override
    @Deprecated
    public BooleanListIterator booleanListIterator() {
        return this.listIterator();
    }

    @Override
    @Deprecated
    public BooleanListIterator booleanListIterator(int index) {
        return this.listIterator(index);
    }

    @Override
    public BooleanListIterator iterator() {
        return this.listIterator();
    }

    @Override
    public BooleanListIterator listIterator() {
        return this.listIterator(0);
    }

    @Override
    public BooleanListIterator listIterator(final int index) {
        this.ensureIndex(index);
        return new AbstractBooleanListIterator(){
            int pos;
            int last;
            {
                this.pos = index;
                this.last = -1;
            }

            @Override
            public boolean hasNext() {
                return this.pos < AbstractBooleanList.this.size();
            }

            @Override
            public boolean hasPrevious() {
                return this.pos > 0;
            }

            @Override
            public boolean nextBoolean() {
                if (!this.hasNext()) {
                    throw new NoSuchElementException();
                }
                this.last = this.pos++;
                return AbstractBooleanList.this.getBoolean(this.last);
            }

            @Override
            public boolean previousBoolean() {
                if (!this.hasPrevious()) {
                    throw new NoSuchElementException();
                }
                this.last = --this.pos;
                return AbstractBooleanList.this.getBoolean(this.pos);
            }

            @Override
            public int nextIndex() {
                return this.pos;
            }

            @Override
            public int previousIndex() {
                return this.pos - 1;
            }

            @Override
            public void add(boolean k2) {
                AbstractBooleanList.this.add(this.pos++, k2);
                this.last = -1;
            }

            @Override
            public void set(boolean k2) {
                if (this.last == -1) {
                    throw new IllegalStateException();
                }
                AbstractBooleanList.this.set(this.last, k2);
            }

            @Override
            public void remove() {
                if (this.last == -1) {
                    throw new IllegalStateException();
                }
                AbstractBooleanList.this.removeBoolean(this.last);
                if (this.last < this.pos) {
                    --this.pos;
                }
                this.last = -1;
            }
        };
    }

    @Override
    public boolean contains(boolean k2) {
        return this.indexOf(k2) >= 0;
    }

    @Override
    public int indexOf(boolean k2) {
        BooleanListIterator i2 = this.listIterator();
        while (i2.hasNext()) {
            boolean e2 = i2.nextBoolean();
            if (k2 != e2) continue;
            return i2.previousIndex();
        }
        return -1;
    }

    @Override
    public int lastIndexOf(boolean k2) {
        BooleanListIterator i2 = this.listIterator(this.size());
        while (i2.hasPrevious()) {
            boolean e2 = i2.previousBoolean();
            if (k2 != e2) continue;
            return i2.nextIndex();
        }
        return -1;
    }

    @Override
    public void size(int size) {
        int i2 = this.size();
        if (size > i2) {
            while (i2++ < size) {
                this.add(false);
            }
        } else {
            while (i2-- != size) {
                this.remove(i2);
            }
        }
    }

    @Override
    public BooleanList subList(int from, int to2) {
        this.ensureIndex(from);
        this.ensureIndex(to2);
        if (from > to2) {
            throw new IndexOutOfBoundsException("Start index (" + from + ") is greater than end index (" + to2 + ")");
        }
        return new BooleanSubList(this, from, to2);
    }

    @Override
    @Deprecated
    public BooleanList booleanSubList(int from, int to2) {
        return this.subList(from, to2);
    }

    @Override
    public void removeElements(int from, int to2) {
        this.ensureIndex(to2);
        BooleanListIterator i2 = this.listIterator(from);
        int n2 = to2 - from;
        if (n2 < 0) {
            throw new IllegalArgumentException("Start index (" + from + ") is greater than end index (" + to2 + ")");
        }
        while (n2-- != 0) {
            i2.nextBoolean();
            i2.remove();
        }
    }

    @Override
    public void addElements(int index, boolean[] a2, int offset, int length) {
        this.ensureIndex(index);
        if (offset < 0) {
            throw new ArrayIndexOutOfBoundsException("Offset (" + offset + ") is negative");
        }
        if (offset + length > a2.length) {
            throw new ArrayIndexOutOfBoundsException("End index (" + (offset + length) + ") is greater than array length (" + a2.length + ")");
        }
        while (length-- != 0) {
            this.add(index++, a2[offset++]);
        }
    }

    @Override
    public void addElements(int index, boolean[] a2) {
        this.addElements(index, a2, 0, a2.length);
    }

    @Override
    public void getElements(int from, boolean[] a2, int offset, int length) {
        BooleanListIterator i2 = this.listIterator(from);
        if (offset < 0) {
            throw new ArrayIndexOutOfBoundsException("Offset (" + offset + ") is negative");
        }
        if (offset + length > a2.length) {
            throw new ArrayIndexOutOfBoundsException("End index (" + (offset + length) + ") is greater than array length (" + a2.length + ")");
        }
        if (from + length > this.size()) {
            throw new IndexOutOfBoundsException("End index (" + (from + length) + ") is greater than list size (" + this.size() + ")");
        }
        while (length-- != 0) {
            a2[offset++] = i2.nextBoolean();
        }
    }

    private boolean valEquals(Object a2, Object b2) {
        return a2 == null ? b2 == null : a2.equals(b2);
    }

    @Override
    public boolean equals(Object o2) {
        if (o2 == this) {
            return true;
        }
        if (!(o2 instanceof List)) {
            return false;
        }
        List l2 = (List)o2;
        int s2 = this.size();
        if (s2 != l2.size()) {
            return false;
        }
        if (l2 instanceof BooleanList) {
            BooleanListIterator i1 = this.listIterator();
            BooleanListIterator i2 = ((BooleanList)l2).listIterator();
            while (s2-- != 0) {
                if (i1.nextBoolean() == i2.nextBoolean()) continue;
                return false;
            }
            return true;
        }
        BooleanListIterator i1 = this.listIterator();
        ListIterator i2 = l2.listIterator();
        while (s2-- != 0) {
            if (this.valEquals(i1.next(), i2.next())) continue;
            return false;
        }
        return true;
    }

    @Override
    public int compareTo(List<? extends Boolean> l2) {
        if (l2 == this) {
            return 0;
        }
        if (l2 instanceof BooleanList) {
            BooleanListIterator i1 = this.listIterator();
            BooleanListIterator i2 = ((BooleanList)l2).listIterator();
            while (i1.hasNext() && i2.hasNext()) {
                boolean e2;
                boolean e1 = i1.nextBoolean();
                int r2 = Boolean.compare(e1, e2 = i2.nextBoolean());
                if (r2 == 0) continue;
                return r2;
            }
            return i2.hasNext() ? -1 : (i1.hasNext() ? 1 : 0);
        }
        BooleanListIterator i1 = this.listIterator();
        ListIterator<? extends Boolean> i2 = l2.listIterator();
        while (i1.hasNext() && i2.hasNext()) {
            int r3 = ((Comparable)i1.next()).compareTo(i2.next());
            if (r3 == 0) continue;
            return r3;
        }
        return i2.hasNext() ? -1 : (i1.hasNext() ? 1 : 0);
    }

    @Override
    public int hashCode() {
        BooleanListIterator i2 = this.iterator();
        int h2 = 1;
        int s2 = this.size();
        while (s2-- != 0) {
            boolean k2 = i2.nextBoolean();
            h2 = 31 * h2 + (k2 ? 1231 : 1237);
        }
        return h2;
    }

    @Override
    public void push(boolean o2) {
        this.add(o2);
    }

    @Override
    public boolean popBoolean() {
        if (this.isEmpty()) {
            throw new NoSuchElementException();
        }
        return this.removeBoolean(this.size() - 1);
    }

    @Override
    public boolean topBoolean() {
        if (this.isEmpty()) {
            throw new NoSuchElementException();
        }
        return this.getBoolean(this.size() - 1);
    }

    @Override
    public boolean peekBoolean(int i2) {
        return this.getBoolean(this.size() - 1 - i2);
    }

    @Override
    public boolean rem(boolean k2) {
        int index = this.indexOf(k2);
        if (index == -1) {
            return false;
        }
        this.removeBoolean(index);
        return true;
    }

    @Override
    public boolean remove(Object o2) {
        return this.rem((Boolean)o2);
    }

    @Override
    public boolean addAll(int index, BooleanCollection c2) {
        return this.addAll(index, (Collection<? extends Boolean>)c2);
    }

    @Override
    public boolean addAll(int index, BooleanList l2) {
        return this.addAll(index, (BooleanCollection)l2);
    }

    @Override
    public boolean addAll(BooleanCollection c2) {
        return this.addAll(this.size(), c2);
    }

    @Override
    public boolean addAll(BooleanList l2) {
        return this.addAll(this.size(), l2);
    }

    @Override
    public void add(int index, Boolean ok2) {
        this.add(index, (boolean)ok2);
    }

    @Override
    @Deprecated
    public Boolean set(int index, Boolean ok2) {
        return this.set(index, (boolean)ok2);
    }

    @Override
    @Deprecated
    public Boolean get(int index) {
        return this.getBoolean(index);
    }

    @Override
    public int indexOf(Object ok2) {
        return this.indexOf((Boolean)ok2);
    }

    @Override
    public int lastIndexOf(Object ok2) {
        return this.lastIndexOf((Boolean)ok2);
    }

    @Override
    @Deprecated
    public Boolean remove(int index) {
        return this.removeBoolean(index);
    }

    @Override
    public void push(Boolean o2) {
        this.push((boolean)o2);
    }

    @Override
    @Deprecated
    public Boolean pop() {
        return this.popBoolean();
    }

    @Override
    @Deprecated
    public Boolean top() {
        return this.topBoolean();
    }

    @Override
    @Deprecated
    public Boolean peek(int i2) {
        return this.peekBoolean(i2);
    }

    @Override
    public String toString() {
        StringBuilder s2 = new StringBuilder();
        BooleanListIterator i2 = this.iterator();
        int n2 = this.size();
        boolean first = true;
        s2.append("[");
        while (n2-- != 0) {
            if (first) {
                first = false;
            } else {
                s2.append(", ");
            }
            boolean k2 = i2.nextBoolean();
            s2.append(String.valueOf(k2));
        }
        s2.append("]");
        return s2.toString();
    }

    public static class BooleanSubList
    extends AbstractBooleanList
    implements Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final BooleanList l;
        protected final int from;
        protected int to;
        private static final boolean ASSERTS = false;

        public BooleanSubList(BooleanList l2, int from, int to2) {
            this.l = l2;
            this.from = from;
            this.to = to2;
        }

        private void assertRange() {
        }

        @Override
        public boolean add(boolean k2) {
            this.l.add(this.to, k2);
            ++this.to;
            return true;
        }

        @Override
        public void add(int index, boolean k2) {
            this.ensureIndex(index);
            this.l.add(this.from + index, k2);
            ++this.to;
        }

        @Override
        public boolean addAll(int index, Collection<? extends Boolean> c2) {
            this.ensureIndex(index);
            this.to += c2.size();
            return this.l.addAll(this.from + index, c2);
        }

        @Override
        public boolean getBoolean(int index) {
            this.ensureRestrictedIndex(index);
            return this.l.getBoolean(this.from + index);
        }

        @Override
        public boolean removeBoolean(int index) {
            this.ensureRestrictedIndex(index);
            --this.to;
            return this.l.removeBoolean(this.from + index);
        }

        @Override
        public boolean set(int index, boolean k2) {
            this.ensureRestrictedIndex(index);
            return this.l.set(this.from + index, k2);
        }

        @Override
        public void clear() {
            this.removeElements(0, this.size());
        }

        @Override
        public int size() {
            return this.to - this.from;
        }

        @Override
        public void getElements(int from, boolean[] a2, int offset, int length) {
            this.ensureIndex(from);
            if (from + length > this.size()) {
                throw new IndexOutOfBoundsException("End index (" + from + length + ") is greater than list size (" + this.size() + ")");
            }
            this.l.getElements(this.from + from, a2, offset, length);
        }

        @Override
        public void removeElements(int from, int to2) {
            this.ensureIndex(from);
            this.ensureIndex(to2);
            this.l.removeElements(this.from + from, this.from + to2);
            this.to -= to2 - from;
        }

        @Override
        public void addElements(int index, boolean[] a2, int offset, int length) {
            this.ensureIndex(index);
            this.l.addElements(this.from + index, a2, offset, length);
            this.to += length;
        }

        @Override
        public BooleanListIterator listIterator(final int index) {
            this.ensureIndex(index);
            return new AbstractBooleanListIterator(){
                int pos;
                int last;
                {
                    this.pos = index;
                    this.last = -1;
                }

                @Override
                public boolean hasNext() {
                    return this.pos < BooleanSubList.this.size();
                }

                @Override
                public boolean hasPrevious() {
                    return this.pos > 0;
                }

                @Override
                public boolean nextBoolean() {
                    if (!this.hasNext()) {
                        throw new NoSuchElementException();
                    }
                    this.last = this.pos++;
                    return BooleanSubList.this.l.getBoolean(BooleanSubList.this.from + this.last);
                }

                @Override
                public boolean previousBoolean() {
                    if (!this.hasPrevious()) {
                        throw new NoSuchElementException();
                    }
                    this.last = --this.pos;
                    return BooleanSubList.this.l.getBoolean(BooleanSubList.this.from + this.pos);
                }

                @Override
                public int nextIndex() {
                    return this.pos;
                }

                @Override
                public int previousIndex() {
                    return this.pos - 1;
                }

                @Override
                public void add(boolean k2) {
                    if (this.last == -1) {
                        throw new IllegalStateException();
                    }
                    BooleanSubList.this.add(this.pos++, k2);
                    this.last = -1;
                }

                @Override
                public void set(boolean k2) {
                    if (this.last == -1) {
                        throw new IllegalStateException();
                    }
                    BooleanSubList.this.set(this.last, k2);
                }

                @Override
                public void remove() {
                    if (this.last == -1) {
                        throw new IllegalStateException();
                    }
                    BooleanSubList.this.removeBoolean(this.last);
                    if (this.last < this.pos) {
                        --this.pos;
                    }
                    this.last = -1;
                }
            };
        }

        @Override
        public BooleanList subList(int from, int to2) {
            this.ensureIndex(from);
            this.ensureIndex(to2);
            if (from > to2) {
                throw new IllegalArgumentException("Start index (" + from + ") is greater than end index (" + to2 + ")");
            }
            return new BooleanSubList(this, from, to2);
        }

        @Override
        public boolean rem(boolean k2) {
            int index = this.indexOf(k2);
            if (index == -1) {
                return false;
            }
            --this.to;
            this.l.removeBoolean(this.from + index);
            return true;
        }

        @Override
        public boolean remove(Object o2) {
            return this.rem((Boolean)o2);
        }

        @Override
        public boolean addAll(int index, BooleanCollection c2) {
            this.ensureIndex(index);
            this.to += c2.size();
            return this.l.addAll(this.from + index, c2);
        }

        @Override
        public boolean addAll(int index, BooleanList l2) {
            this.ensureIndex(index);
            this.to += l2.size();
            return this.l.addAll(this.from + index, l2);
        }
    }
}

