package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.bytes.AbstractByteCollection;
import it.unimi.dsi.fastutil.bytes.AbstractByteListIterator;
import it.unimi.dsi.fastutil.bytes.ByteCollection;
import it.unimi.dsi.fastutil.bytes.ByteList;
import it.unimi.dsi.fastutil.bytes.ByteListIterator;
import it.unimi.dsi.fastutil.bytes.ByteStack;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;

public abstract class AbstractByteList
extends AbstractByteCollection
implements ByteList,
ByteStack {
    protected AbstractByteList() {
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
    public void add(int index, byte k2) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean add(byte k2) {
        this.add(this.size(), k2);
        return true;
    }

    @Override
    public byte removeByte(int i2) {
        throw new UnsupportedOperationException();
    }

    @Override
    public byte set(int index, byte k2) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(int index, Collection<? extends Byte> c2) {
        this.ensureIndex(index);
        int n2 = c2.size();
        if (n2 == 0) {
            return false;
        }
        Iterator<? extends Byte> i2 = c2.iterator();
        while (n2-- != 0) {
            this.add(index++, i2.next());
        }
        return true;
    }

    @Override
    public boolean addAll(Collection<? extends Byte> c2) {
        return this.addAll(this.size(), c2);
    }

    @Override
    @Deprecated
    public ByteListIterator byteListIterator() {
        return this.listIterator();
    }

    @Override
    @Deprecated
    public ByteListIterator byteListIterator(int index) {
        return this.listIterator(index);
    }

    @Override
    public ByteListIterator iterator() {
        return this.listIterator();
    }

    @Override
    public ByteListIterator listIterator() {
        return this.listIterator(0);
    }

    @Override
    public ByteListIterator listIterator(final int index) {
        this.ensureIndex(index);
        return new AbstractByteListIterator(){
            int pos;
            int last;
            {
                this.pos = index;
                this.last = -1;
            }

            @Override
            public boolean hasNext() {
                return this.pos < AbstractByteList.this.size();
            }

            @Override
            public boolean hasPrevious() {
                return this.pos > 0;
            }

            @Override
            public byte nextByte() {
                if (!this.hasNext()) {
                    throw new NoSuchElementException();
                }
                this.last = this.pos++;
                return AbstractByteList.this.getByte(this.last);
            }

            @Override
            public byte previousByte() {
                if (!this.hasPrevious()) {
                    throw new NoSuchElementException();
                }
                this.last = --this.pos;
                return AbstractByteList.this.getByte(this.pos);
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
            public void add(byte k2) {
                AbstractByteList.this.add(this.pos++, k2);
                this.last = -1;
            }

            @Override
            public void set(byte k2) {
                if (this.last == -1) {
                    throw new IllegalStateException();
                }
                AbstractByteList.this.set(this.last, k2);
            }

            @Override
            public void remove() {
                if (this.last == -1) {
                    throw new IllegalStateException();
                }
                AbstractByteList.this.removeByte(this.last);
                if (this.last < this.pos) {
                    --this.pos;
                }
                this.last = -1;
            }
        };
    }

    @Override
    public boolean contains(byte k2) {
        return this.indexOf(k2) >= 0;
    }

    @Override
    public int indexOf(byte k2) {
        ByteListIterator i2 = this.listIterator();
        while (i2.hasNext()) {
            byte e2 = i2.nextByte();
            if (k2 != e2) continue;
            return i2.previousIndex();
        }
        return -1;
    }

    @Override
    public int lastIndexOf(byte k2) {
        ByteListIterator i2 = this.listIterator(this.size());
        while (i2.hasPrevious()) {
            byte e2 = i2.previousByte();
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
                this.add((byte)0);
            }
        } else {
            while (i2-- != size) {
                this.remove(i2);
            }
        }
    }

    @Override
    public ByteList subList(int from, int to2) {
        this.ensureIndex(from);
        this.ensureIndex(to2);
        if (from > to2) {
            throw new IndexOutOfBoundsException("Start index (" + from + ") is greater than end index (" + to2 + ")");
        }
        return new ByteSubList(this, from, to2);
    }

    @Override
    @Deprecated
    public ByteList byteSubList(int from, int to2) {
        return this.subList(from, to2);
    }

    @Override
    public void removeElements(int from, int to2) {
        this.ensureIndex(to2);
        ByteListIterator i2 = this.listIterator(from);
        int n2 = to2 - from;
        if (n2 < 0) {
            throw new IllegalArgumentException("Start index (" + from + ") is greater than end index (" + to2 + ")");
        }
        while (n2-- != 0) {
            i2.nextByte();
            i2.remove();
        }
    }

    @Override
    public void addElements(int index, byte[] a2, int offset, int length) {
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
    public void addElements(int index, byte[] a2) {
        this.addElements(index, a2, 0, a2.length);
    }

    @Override
    public void getElements(int from, byte[] a2, int offset, int length) {
        ByteListIterator i2 = this.listIterator(from);
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
            a2[offset++] = i2.nextByte();
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
        if (l2 instanceof ByteList) {
            ByteListIterator i1 = this.listIterator();
            ByteListIterator i2 = ((ByteList)l2).listIterator();
            while (s2-- != 0) {
                if (i1.nextByte() == i2.nextByte()) continue;
                return false;
            }
            return true;
        }
        ByteListIterator i1 = this.listIterator();
        ListIterator i2 = l2.listIterator();
        while (s2-- != 0) {
            if (this.valEquals(i1.next(), i2.next())) continue;
            return false;
        }
        return true;
    }

    @Override
    public int compareTo(List<? extends Byte> l2) {
        if (l2 == this) {
            return 0;
        }
        if (l2 instanceof ByteList) {
            ByteListIterator i1 = this.listIterator();
            ByteListIterator i2 = ((ByteList)l2).listIterator();
            while (i1.hasNext() && i2.hasNext()) {
                byte e2;
                byte e1 = i1.nextByte();
                int r2 = Byte.compare(e1, e2 = i2.nextByte());
                if (r2 == 0) continue;
                return r2;
            }
            return i2.hasNext() ? -1 : (i1.hasNext() ? 1 : 0);
        }
        ByteListIterator i1 = this.listIterator();
        ListIterator<? extends Byte> i2 = l2.listIterator();
        while (i1.hasNext() && i2.hasNext()) {
            int r3 = ((Comparable)i1.next()).compareTo(i2.next());
            if (r3 == 0) continue;
            return r3;
        }
        return i2.hasNext() ? -1 : (i1.hasNext() ? 1 : 0);
    }

    @Override
    public int hashCode() {
        ByteListIterator i2 = this.iterator();
        int h2 = 1;
        int s2 = this.size();
        while (s2-- != 0) {
            byte k2 = i2.nextByte();
            h2 = 31 * h2 + k2;
        }
        return h2;
    }

    @Override
    public void push(byte o2) {
        this.add(o2);
    }

    @Override
    public byte popByte() {
        if (this.isEmpty()) {
            throw new NoSuchElementException();
        }
        return this.removeByte(this.size() - 1);
    }

    @Override
    public byte topByte() {
        if (this.isEmpty()) {
            throw new NoSuchElementException();
        }
        return this.getByte(this.size() - 1);
    }

    @Override
    public byte peekByte(int i2) {
        return this.getByte(this.size() - 1 - i2);
    }

    @Override
    public boolean rem(byte k2) {
        int index = this.indexOf(k2);
        if (index == -1) {
            return false;
        }
        this.removeByte(index);
        return true;
    }

    @Override
    public boolean remove(Object o2) {
        return this.rem((Byte)o2);
    }

    @Override
    public boolean addAll(int index, ByteCollection c2) {
        return this.addAll(index, (Collection<? extends Byte>)c2);
    }

    @Override
    public boolean addAll(int index, ByteList l2) {
        return this.addAll(index, (ByteCollection)l2);
    }

    @Override
    public boolean addAll(ByteCollection c2) {
        return this.addAll(this.size(), c2);
    }

    @Override
    public boolean addAll(ByteList l2) {
        return this.addAll(this.size(), l2);
    }

    @Override
    public void add(int index, Byte ok2) {
        this.add(index, (byte)ok2);
    }

    @Override
    @Deprecated
    public Byte set(int index, Byte ok2) {
        return this.set(index, (byte)ok2);
    }

    @Override
    @Deprecated
    public Byte get(int index) {
        return this.getByte(index);
    }

    @Override
    public int indexOf(Object ok2) {
        return this.indexOf((Byte)ok2);
    }

    @Override
    public int lastIndexOf(Object ok2) {
        return this.lastIndexOf((Byte)ok2);
    }

    @Override
    @Deprecated
    public Byte remove(int index) {
        return this.removeByte(index);
    }

    @Override
    public void push(Byte o2) {
        this.push((byte)o2);
    }

    @Override
    @Deprecated
    public Byte pop() {
        return this.popByte();
    }

    @Override
    @Deprecated
    public Byte top() {
        return this.topByte();
    }

    @Override
    @Deprecated
    public Byte peek(int i2) {
        return this.peekByte(i2);
    }

    @Override
    public String toString() {
        StringBuilder s2 = new StringBuilder();
        ByteListIterator i2 = this.iterator();
        int n2 = this.size();
        boolean first = true;
        s2.append("[");
        while (n2-- != 0) {
            if (first) {
                first = false;
            } else {
                s2.append(", ");
            }
            byte k2 = i2.nextByte();
            s2.append(String.valueOf(k2));
        }
        s2.append("]");
        return s2.toString();
    }

    public static class ByteSubList
    extends AbstractByteList
    implements Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final ByteList l;
        protected final int from;
        protected int to;
        private static final boolean ASSERTS = false;

        public ByteSubList(ByteList l2, int from, int to2) {
            this.l = l2;
            this.from = from;
            this.to = to2;
        }

        private void assertRange() {
        }

        @Override
        public boolean add(byte k2) {
            this.l.add(this.to, k2);
            ++this.to;
            return true;
        }

        @Override
        public void add(int index, byte k2) {
            this.ensureIndex(index);
            this.l.add(this.from + index, k2);
            ++this.to;
        }

        @Override
        public boolean addAll(int index, Collection<? extends Byte> c2) {
            this.ensureIndex(index);
            this.to += c2.size();
            return this.l.addAll(this.from + index, c2);
        }

        @Override
        public byte getByte(int index) {
            this.ensureRestrictedIndex(index);
            return this.l.getByte(this.from + index);
        }

        @Override
        public byte removeByte(int index) {
            this.ensureRestrictedIndex(index);
            --this.to;
            return this.l.removeByte(this.from + index);
        }

        @Override
        public byte set(int index, byte k2) {
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
        public void getElements(int from, byte[] a2, int offset, int length) {
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
        public void addElements(int index, byte[] a2, int offset, int length) {
            this.ensureIndex(index);
            this.l.addElements(this.from + index, a2, offset, length);
            this.to += length;
        }

        @Override
        public ByteListIterator listIterator(final int index) {
            this.ensureIndex(index);
            return new AbstractByteListIterator(){
                int pos;
                int last;
                {
                    this.pos = index;
                    this.last = -1;
                }

                @Override
                public boolean hasNext() {
                    return this.pos < ByteSubList.this.size();
                }

                @Override
                public boolean hasPrevious() {
                    return this.pos > 0;
                }

                @Override
                public byte nextByte() {
                    if (!this.hasNext()) {
                        throw new NoSuchElementException();
                    }
                    this.last = this.pos++;
                    return ByteSubList.this.l.getByte(ByteSubList.this.from + this.last);
                }

                @Override
                public byte previousByte() {
                    if (!this.hasPrevious()) {
                        throw new NoSuchElementException();
                    }
                    this.last = --this.pos;
                    return ByteSubList.this.l.getByte(ByteSubList.this.from + this.pos);
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
                public void add(byte k2) {
                    if (this.last == -1) {
                        throw new IllegalStateException();
                    }
                    ByteSubList.this.add(this.pos++, k2);
                    this.last = -1;
                }

                @Override
                public void set(byte k2) {
                    if (this.last == -1) {
                        throw new IllegalStateException();
                    }
                    ByteSubList.this.set(this.last, k2);
                }

                @Override
                public void remove() {
                    if (this.last == -1) {
                        throw new IllegalStateException();
                    }
                    ByteSubList.this.removeByte(this.last);
                    if (this.last < this.pos) {
                        --this.pos;
                    }
                    this.last = -1;
                }
            };
        }

        @Override
        public ByteList subList(int from, int to2) {
            this.ensureIndex(from);
            this.ensureIndex(to2);
            if (from > to2) {
                throw new IllegalArgumentException("Start index (" + from + ") is greater than end index (" + to2 + ")");
            }
            return new ByteSubList(this, from, to2);
        }

        @Override
        public boolean rem(byte k2) {
            int index = this.indexOf(k2);
            if (index == -1) {
                return false;
            }
            --this.to;
            this.l.removeByte(this.from + index);
            return true;
        }

        @Override
        public boolean remove(Object o2) {
            return this.rem((Byte)o2);
        }

        @Override
        public boolean addAll(int index, ByteCollection c2) {
            this.ensureIndex(index);
            this.to += c2.size();
            return this.l.addAll(this.from + index, c2);
        }

        @Override
        public boolean addAll(int index, ByteList l2) {
            this.ensureIndex(index);
            this.to += l2.size();
            return this.l.addAll(this.from + index, l2);
        }
    }
}

