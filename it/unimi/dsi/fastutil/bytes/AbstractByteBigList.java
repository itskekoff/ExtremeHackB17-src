package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.BigList;
import it.unimi.dsi.fastutil.BigListIterator;
import it.unimi.dsi.fastutil.bytes.AbstractByteBigListIterator;
import it.unimi.dsi.fastutil.bytes.AbstractByteCollection;
import it.unimi.dsi.fastutil.bytes.ByteBigArrays;
import it.unimi.dsi.fastutil.bytes.ByteBigList;
import it.unimi.dsi.fastutil.bytes.ByteBigListIterator;
import it.unimi.dsi.fastutil.bytes.ByteCollection;
import it.unimi.dsi.fastutil.bytes.ByteList;
import it.unimi.dsi.fastutil.bytes.ByteStack;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

public abstract class AbstractByteBigList
extends AbstractByteCollection
implements ByteBigList,
ByteStack {
    protected AbstractByteBigList() {
    }

    protected void ensureIndex(long index) {
        if (index < 0L) {
            throw new IndexOutOfBoundsException("Index (" + index + ") is negative");
        }
        if (index > this.size64()) {
            throw new IndexOutOfBoundsException("Index (" + index + ") is greater than list size (" + this.size64() + ")");
        }
    }

    protected void ensureRestrictedIndex(long index) {
        if (index < 0L) {
            throw new IndexOutOfBoundsException("Index (" + index + ") is negative");
        }
        if (index >= this.size64()) {
            throw new IndexOutOfBoundsException("Index (" + index + ") is greater than or equal to list size (" + this.size64() + ")");
        }
    }

    @Override
    public void add(long index, byte k2) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean add(byte k2) {
        this.add(this.size64(), k2);
        return true;
    }

    @Override
    public byte removeByte(long i2) {
        throw new UnsupportedOperationException();
    }

    public byte removeByte(int i2) {
        return this.removeByte((long)i2);
    }

    @Override
    public byte set(long index, byte k2) {
        throw new UnsupportedOperationException();
    }

    public byte set(int index, byte k2) {
        return this.set((long)index, k2);
    }

    @Override
    public boolean addAll(long index, Collection<? extends Byte> c2) {
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

    public boolean addAll(int index, Collection<? extends Byte> c2) {
        return this.addAll((long)index, c2);
    }

    @Override
    public boolean addAll(Collection<? extends Byte> c2) {
        return this.addAll(this.size64(), c2);
    }

    @Override
    public ByteBigListIterator iterator() {
        return this.listIterator();
    }

    @Override
    public ByteBigListIterator listIterator() {
        return this.listIterator(0L);
    }

    @Override
    public ByteBigListIterator listIterator(final long index) {
        this.ensureIndex(index);
        return new AbstractByteBigListIterator(){
            long pos;
            long last;
            {
                this.pos = index;
                this.last = -1L;
            }

            @Override
            public boolean hasNext() {
                return this.pos < AbstractByteBigList.this.size64();
            }

            @Override
            public boolean hasPrevious() {
                return this.pos > 0L;
            }

            @Override
            public byte nextByte() {
                if (!this.hasNext()) {
                    throw new NoSuchElementException();
                }
                this.last = this.pos++;
                return AbstractByteBigList.this.getByte(this.last);
            }

            @Override
            public byte previousByte() {
                if (!this.hasPrevious()) {
                    throw new NoSuchElementException();
                }
                this.last = --this.pos;
                return AbstractByteBigList.this.getByte(this.pos);
            }

            @Override
            public long nextIndex() {
                return this.pos;
            }

            @Override
            public long previousIndex() {
                return this.pos - 1L;
            }

            @Override
            public void add(byte k2) {
                AbstractByteBigList.this.add(this.pos++, k2);
                this.last = -1L;
            }

            @Override
            public void set(byte k2) {
                if (this.last == -1L) {
                    throw new IllegalStateException();
                }
                AbstractByteBigList.this.set(this.last, k2);
            }

            @Override
            public void remove() {
                if (this.last == -1L) {
                    throw new IllegalStateException();
                }
                AbstractByteBigList.this.removeByte(this.last);
                if (this.last < this.pos) {
                    --this.pos;
                }
                this.last = -1L;
            }
        };
    }

    public ByteBigListIterator listIterator(int index) {
        return this.listIterator((long)index);
    }

    @Override
    public boolean contains(byte k2) {
        return this.indexOf(k2) >= 0L;
    }

    @Override
    public long indexOf(byte k2) {
        ByteBigListIterator i2 = this.listIterator();
        while (i2.hasNext()) {
            byte e2 = i2.nextByte();
            if (k2 != e2) continue;
            return i2.previousIndex();
        }
        return -1L;
    }

    @Override
    public long lastIndexOf(byte k2) {
        ByteBigListIterator i2 = this.listIterator(this.size64());
        while (i2.hasPrevious()) {
            byte e2 = i2.previousByte();
            if (k2 != e2) continue;
            return i2.nextIndex();
        }
        return -1L;
    }

    @Override
    public void size(long size) {
        long i2 = this.size64();
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

    public void size(int size) {
        this.size((long)size);
    }

    @Override
    public ByteBigList subList(long from, long to2) {
        this.ensureIndex(from);
        this.ensureIndex(to2);
        if (from > to2) {
            throw new IndexOutOfBoundsException("Start index (" + from + ") is greater than end index (" + to2 + ")");
        }
        return new ByteSubList(this, from, to2);
    }

    @Override
    public void removeElements(long from, long to2) {
        this.ensureIndex(to2);
        ByteBigListIterator i2 = this.listIterator(from);
        long n2 = to2 - from;
        if (n2 < 0L) {
            throw new IllegalArgumentException("Start index (" + from + ") is greater than end index (" + to2 + ")");
        }
        while (n2-- != 0L) {
            i2.nextByte();
            i2.remove();
        }
    }

    @Override
    public void addElements(long index, byte[][] a2, long offset, long length) {
        this.ensureIndex(index);
        ByteBigArrays.ensureOffsetLength(a2, offset, length);
        while (length-- != 0L) {
            this.add(index++, ByteBigArrays.get(a2, offset++));
        }
    }

    @Override
    public void addElements(long index, byte[][] a2) {
        this.addElements(index, a2, 0L, ByteBigArrays.length(a2));
    }

    @Override
    public void getElements(long from, byte[][] a2, long offset, long length) {
        ByteBigListIterator i2 = this.listIterator(from);
        ByteBigArrays.ensureOffsetLength(a2, offset, length);
        if (from + length > this.size64()) {
            throw new IndexOutOfBoundsException("End index (" + (from + length) + ") is greater than list size (" + this.size64() + ")");
        }
        while (length-- != 0L) {
            ByteBigArrays.set(a2, offset++, i2.nextByte());
        }
    }

    @Override
    @Deprecated
    public int size() {
        return (int)Math.min(Integer.MAX_VALUE, this.size64());
    }

    private boolean valEquals(Object a2, Object b2) {
        return a2 == null ? b2 == null : a2.equals(b2);
    }

    @Override
    public boolean equals(Object o2) {
        if (o2 == this) {
            return true;
        }
        if (!(o2 instanceof BigList)) {
            return false;
        }
        BigList l2 = (BigList)o2;
        long s2 = this.size64();
        if (s2 != l2.size64()) {
            return false;
        }
        if (l2 instanceof ByteBigList) {
            ByteBigListIterator i1 = this.listIterator();
            ByteBigListIterator i2 = ((ByteBigList)l2).listIterator();
            while (s2-- != 0L) {
                if (i1.nextByte() == i2.nextByte()) continue;
                return false;
            }
            return true;
        }
        ByteBigListIterator i1 = this.listIterator();
        BigListIterator i2 = l2.listIterator();
        while (s2-- != 0L) {
            if (this.valEquals(i1.next(), i2.next())) continue;
            return false;
        }
        return true;
    }

    @Override
    public int compareTo(BigList<? extends Byte> l2) {
        if (l2 == this) {
            return 0;
        }
        if (l2 instanceof ByteBigList) {
            ByteBigListIterator i1 = this.listIterator();
            ByteBigListIterator i2 = ((ByteBigList)l2).listIterator();
            while (i1.hasNext() && i2.hasNext()) {
                byte e2;
                byte e1 = i1.nextByte();
                int r2 = Byte.compare(e1, e2 = i2.nextByte());
                if (r2 == 0) continue;
                return r2;
            }
            return i2.hasNext() ? -1 : (i1.hasNext() ? 1 : 0);
        }
        ByteBigListIterator i1 = this.listIterator();
        BigListIterator<? extends Byte> i2 = l2.listIterator();
        while (i1.hasNext() && i2.hasNext()) {
            int r3 = ((Comparable)i1.next()).compareTo(i2.next());
            if (r3 == 0) continue;
            return r3;
        }
        return i2.hasNext() ? -1 : (i1.hasNext() ? 1 : 0);
    }

    @Override
    public int hashCode() {
        ByteBigListIterator i2 = this.iterator();
        int h2 = 1;
        long s2 = this.size64();
        while (s2-- != 0L) {
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
        return this.removeByte(this.size64() - 1L);
    }

    @Override
    public byte topByte() {
        if (this.isEmpty()) {
            throw new NoSuchElementException();
        }
        return this.getByte(this.size64() - 1L);
    }

    @Override
    public byte peekByte(int i2) {
        return this.getByte(this.size64() - 1L - (long)i2);
    }

    public byte getByte(int index) {
        return this.getByte((long)index);
    }

    @Override
    public boolean rem(byte k2) {
        long index = this.indexOf(k2);
        if (index == -1L) {
            return false;
        }
        this.removeByte(index);
        return true;
    }

    @Override
    public boolean addAll(long index, ByteCollection c2) {
        return this.addAll(index, (Collection<? extends Byte>)c2);
    }

    @Override
    public boolean addAll(long index, ByteBigList l2) {
        return this.addAll(index, (ByteCollection)l2);
    }

    @Override
    public boolean addAll(ByteCollection c2) {
        return this.addAll(this.size64(), c2);
    }

    @Override
    public boolean addAll(ByteBigList l2) {
        return this.addAll(this.size64(), l2);
    }

    @Override
    @Deprecated
    public void add(long index, Byte ok2) {
        this.add(index, (byte)ok2);
    }

    @Override
    @Deprecated
    public Byte set(long index, Byte ok2) {
        return this.set(index, (byte)ok2);
    }

    @Override
    @Deprecated
    public Byte get(long index) {
        return this.getByte(index);
    }

    @Override
    @Deprecated
    public long indexOf(Object ok2) {
        return this.indexOf((Byte)ok2);
    }

    @Override
    @Deprecated
    public long lastIndexOf(Object ok2) {
        return this.lastIndexOf((Byte)ok2);
    }

    @Deprecated
    public Byte remove(int index) {
        return this.removeByte(index);
    }

    @Override
    @Deprecated
    public Byte remove(long index) {
        return this.removeByte(index);
    }

    @Override
    @Deprecated
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
        ByteBigListIterator i2 = this.iterator();
        long n2 = this.size64();
        boolean first = true;
        s2.append("[");
        while (n2-- != 0L) {
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
    extends AbstractByteBigList
    implements Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final ByteBigList l;
        protected final long from;
        protected long to;
        private static final boolean ASSERTS = false;

        public ByteSubList(ByteBigList l2, long from, long to2) {
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
        public void add(long index, byte k2) {
            this.ensureIndex(index);
            this.l.add(this.from + index, k2);
            ++this.to;
        }

        @Override
        public boolean addAll(long index, Collection<? extends Byte> c2) {
            this.ensureIndex(index);
            this.to += (long)c2.size();
            return this.l.addAll(this.from + index, c2);
        }

        @Override
        public byte getByte(long index) {
            this.ensureRestrictedIndex(index);
            return this.l.getByte(this.from + index);
        }

        @Override
        public byte removeByte(long index) {
            this.ensureRestrictedIndex(index);
            --this.to;
            return this.l.removeByte(this.from + index);
        }

        @Override
        public byte set(long index, byte k2) {
            this.ensureRestrictedIndex(index);
            return this.l.set(this.from + index, k2);
        }

        @Override
        public void clear() {
            this.removeElements(0L, this.size64());
        }

        @Override
        public long size64() {
            return this.to - this.from;
        }

        @Override
        public void getElements(long from, byte[][] a2, long offset, long length) {
            this.ensureIndex(from);
            if (from + length > this.size64()) {
                throw new IndexOutOfBoundsException("End index (" + from + length + ") is greater than list size (" + this.size64() + ")");
            }
            this.l.getElements(this.from + from, a2, offset, length);
        }

        @Override
        public void removeElements(long from, long to2) {
            this.ensureIndex(from);
            this.ensureIndex(to2);
            this.l.removeElements(this.from + from, this.from + to2);
            this.to -= to2 - from;
        }

        @Override
        public void addElements(long index, byte[][] a2, long offset, long length) {
            this.ensureIndex(index);
            this.l.addElements(this.from + index, a2, offset, length);
            this.to += length;
        }

        @Override
        public ByteBigListIterator listIterator(final long index) {
            this.ensureIndex(index);
            return new AbstractByteBigListIterator(){
                long pos;
                long last;
                {
                    this.pos = index;
                    this.last = -1L;
                }

                @Override
                public boolean hasNext() {
                    return this.pos < ByteSubList.this.size64();
                }

                @Override
                public boolean hasPrevious() {
                    return this.pos > 0L;
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
                public long nextIndex() {
                    return this.pos;
                }

                @Override
                public long previousIndex() {
                    return this.pos - 1L;
                }

                @Override
                public void add(byte k2) {
                    if (this.last == -1L) {
                        throw new IllegalStateException();
                    }
                    ByteSubList.this.add(this.pos++, k2);
                    this.last = -1L;
                }

                @Override
                public void set(byte k2) {
                    if (this.last == -1L) {
                        throw new IllegalStateException();
                    }
                    ByteSubList.this.set(this.last, k2);
                }

                @Override
                public void remove() {
                    if (this.last == -1L) {
                        throw new IllegalStateException();
                    }
                    ByteSubList.this.removeByte(this.last);
                    if (this.last < this.pos) {
                        --this.pos;
                    }
                    this.last = -1L;
                }
            };
        }

        @Override
        public ByteBigList subList(long from, long to2) {
            this.ensureIndex(from);
            this.ensureIndex(to2);
            if (from > to2) {
                throw new IllegalArgumentException("Start index (" + from + ") is greater than end index (" + to2 + ")");
            }
            return new ByteSubList(this, from, to2);
        }

        @Override
        public boolean rem(byte k2) {
            long index = this.indexOf(k2);
            if (index == -1L) {
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
        public boolean addAll(long index, ByteCollection c2) {
            this.ensureIndex(index);
            this.to += (long)c2.size();
            return this.l.addAll(this.from + index, c2);
        }

        public boolean addAll(long index, ByteList l2) {
            this.ensureIndex(index);
            this.to += (long)l2.size();
            return this.l.addAll(this.from + index, l2);
        }
    }
}

