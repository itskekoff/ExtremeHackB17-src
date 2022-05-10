package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.bytes.AbstractByteBidirectionalIterator;
import it.unimi.dsi.fastutil.bytes.AbstractByteIterator;
import it.unimi.dsi.fastutil.bytes.AbstractByteListIterator;
import it.unimi.dsi.fastutil.bytes.ByteArrayList;
import it.unimi.dsi.fastutil.bytes.ByteArrays;
import it.unimi.dsi.fastutil.bytes.ByteBidirectionalIterator;
import it.unimi.dsi.fastutil.bytes.ByteCollection;
import it.unimi.dsi.fastutil.bytes.ByteIterator;
import it.unimi.dsi.fastutil.bytes.ByteList;
import it.unimi.dsi.fastutil.bytes.ByteListIterator;
import java.io.Serializable;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.NoSuchElementException;

public class ByteIterators {
    public static final EmptyIterator EMPTY_ITERATOR = new EmptyIterator();

    private ByteIterators() {
    }

    public static ByteListIterator singleton(byte element) {
        return new SingletonIterator(element);
    }

    public static ByteListIterator wrap(byte[] array, int offset, int length) {
        ByteArrays.ensureOffsetLength(array, offset, length);
        return new ArrayIterator(array, offset, length);
    }

    public static ByteListIterator wrap(byte[] array) {
        return new ArrayIterator(array, 0, array.length);
    }

    public static int unwrap(ByteIterator i2, byte[] array, int offset, int max) {
        if (max < 0) {
            throw new IllegalArgumentException("The maximum number of elements (" + max + ") is negative");
        }
        if (offset < 0 || offset + max > array.length) {
            throw new IllegalArgumentException();
        }
        int j2 = max;
        while (j2-- != 0 && i2.hasNext()) {
            array[offset++] = i2.nextByte();
        }
        return max - j2 - 1;
    }

    public static int unwrap(ByteIterator i2, byte[] array) {
        return ByteIterators.unwrap(i2, array, 0, array.length);
    }

    public static byte[] unwrap(ByteIterator i2, int max) {
        if (max < 0) {
            throw new IllegalArgumentException("The maximum number of elements (" + max + ") is negative");
        }
        byte[] array = new byte[16];
        int j2 = 0;
        while (max-- != 0 && i2.hasNext()) {
            if (j2 == array.length) {
                array = ByteArrays.grow(array, j2 + 1);
            }
            array[j2++] = i2.nextByte();
        }
        return ByteArrays.trim(array, j2);
    }

    public static byte[] unwrap(ByteIterator i2) {
        return ByteIterators.unwrap(i2, Integer.MAX_VALUE);
    }

    public static int unwrap(ByteIterator i2, ByteCollection c2, int max) {
        if (max < 0) {
            throw new IllegalArgumentException("The maximum number of elements (" + max + ") is negative");
        }
        int j2 = max;
        while (j2-- != 0 && i2.hasNext()) {
            c2.add(i2.nextByte());
        }
        return max - j2 - 1;
    }

    public static long unwrap(ByteIterator i2, ByteCollection c2) {
        long n2 = 0L;
        while (i2.hasNext()) {
            c2.add(i2.nextByte());
            ++n2;
        }
        return n2;
    }

    public static int pour(ByteIterator i2, ByteCollection s2, int max) {
        if (max < 0) {
            throw new IllegalArgumentException("The maximum number of elements (" + max + ") is negative");
        }
        int j2 = max;
        while (j2-- != 0 && i2.hasNext()) {
            s2.add(i2.nextByte());
        }
        return max - j2 - 1;
    }

    public static int pour(ByteIterator i2, ByteCollection s2) {
        return ByteIterators.pour(i2, s2, Integer.MAX_VALUE);
    }

    public static ByteList pour(ByteIterator i2, int max) {
        ByteArrayList l2 = new ByteArrayList();
        ByteIterators.pour(i2, l2, max);
        l2.trim();
        return l2;
    }

    public static ByteList pour(ByteIterator i2) {
        return ByteIterators.pour(i2, Integer.MAX_VALUE);
    }

    public static ByteIterator asByteIterator(Iterator i2) {
        if (i2 instanceof ByteIterator) {
            return (ByteIterator)i2;
        }
        return new IteratorWrapper(i2);
    }

    public static ByteListIterator asByteIterator(ListIterator i2) {
        if (i2 instanceof ByteListIterator) {
            return (ByteListIterator)i2;
        }
        return new ListIteratorWrapper(i2);
    }

    public static ByteListIterator fromTo(byte from, byte to2) {
        return new IntervalIterator(from, to2);
    }

    public static ByteIterator concat(ByteIterator[] a2) {
        return ByteIterators.concat(a2, 0, a2.length);
    }

    public static ByteIterator concat(ByteIterator[] a2, int offset, int length) {
        return new IteratorConcatenator(a2, offset, length);
    }

    public static ByteIterator unmodifiable(ByteIterator i2) {
        return new UnmodifiableIterator(i2);
    }

    public static ByteBidirectionalIterator unmodifiable(ByteBidirectionalIterator i2) {
        return new UnmodifiableBidirectionalIterator(i2);
    }

    public static ByteListIterator unmodifiable(ByteListIterator i2) {
        return new UnmodifiableListIterator(i2);
    }

    public static class UnmodifiableListIterator
    extends AbstractByteListIterator {
        protected final ByteListIterator i;

        public UnmodifiableListIterator(ByteListIterator i2) {
            this.i = i2;
        }

        @Override
        public boolean hasNext() {
            return this.i.hasNext();
        }

        @Override
        public boolean hasPrevious() {
            return this.i.hasPrevious();
        }

        @Override
        public byte nextByte() {
            return this.i.nextByte();
        }

        @Override
        public byte previousByte() {
            return this.i.previousByte();
        }

        @Override
        public int nextIndex() {
            return this.i.nextIndex();
        }

        @Override
        public int previousIndex() {
            return this.i.previousIndex();
        }

        @Override
        @Deprecated
        public Byte next() {
            return (Byte)this.i.next();
        }

        @Override
        @Deprecated
        public Byte previous() {
            return (Byte)this.i.previous();
        }
    }

    public static class UnmodifiableBidirectionalIterator
    extends AbstractByteBidirectionalIterator {
        protected final ByteBidirectionalIterator i;

        public UnmodifiableBidirectionalIterator(ByteBidirectionalIterator i2) {
            this.i = i2;
        }

        @Override
        public boolean hasNext() {
            return this.i.hasNext();
        }

        @Override
        public boolean hasPrevious() {
            return this.i.hasPrevious();
        }

        @Override
        public byte nextByte() {
            return this.i.nextByte();
        }

        @Override
        public byte previousByte() {
            return this.i.previousByte();
        }

        @Override
        @Deprecated
        public Byte next() {
            return (Byte)this.i.next();
        }

        @Override
        @Deprecated
        public Byte previous() {
            return (Byte)this.i.previous();
        }
    }

    public static class UnmodifiableIterator
    extends AbstractByteIterator {
        protected final ByteIterator i;

        public UnmodifiableIterator(ByteIterator i2) {
            this.i = i2;
        }

        @Override
        public boolean hasNext() {
            return this.i.hasNext();
        }

        @Override
        public byte nextByte() {
            return this.i.nextByte();
        }

        @Override
        @Deprecated
        public Byte next() {
            return (Byte)this.i.next();
        }
    }

    private static class IteratorConcatenator
    extends AbstractByteIterator {
        final ByteIterator[] a;
        int offset;
        int length;
        int lastOffset = -1;

        public IteratorConcatenator(ByteIterator[] a2, int offset, int length) {
            this.a = a2;
            this.offset = offset;
            this.length = length;
            this.advance();
        }

        private void advance() {
            while (this.length != 0 && !this.a[this.offset].hasNext()) {
                --this.length;
                ++this.offset;
            }
        }

        @Override
        public boolean hasNext() {
            return this.length > 0;
        }

        @Override
        public byte nextByte() {
            if (!this.hasNext()) {
                throw new NoSuchElementException();
            }
            this.lastOffset = this.offset;
            byte next = this.a[this.lastOffset].nextByte();
            this.advance();
            return next;
        }

        @Override
        public void remove() {
            if (this.lastOffset == -1) {
                throw new IllegalStateException();
            }
            this.a[this.lastOffset].remove();
        }

        @Override
        public int skip(int n2) {
            this.lastOffset = -1;
            int skipped = 0;
            while (skipped < n2 && this.length != 0) {
                skipped += this.a[this.offset].skip(n2 - skipped);
                if (this.a[this.offset].hasNext()) break;
                --this.length;
                ++this.offset;
            }
            return skipped;
        }
    }

    private static class IntervalIterator
    extends AbstractByteListIterator {
        private final byte from;
        private final byte to;
        byte curr;

        public IntervalIterator(byte from, byte to2) {
            this.from = this.curr = from;
            this.to = to2;
        }

        @Override
        public boolean hasNext() {
            return this.curr < this.to;
        }

        @Override
        public boolean hasPrevious() {
            return this.curr > this.from;
        }

        @Override
        public byte nextByte() {
            if (!this.hasNext()) {
                throw new NoSuchElementException();
            }
            byte by2 = this.curr;
            this.curr = (byte)(by2 + 1);
            return by2;
        }

        @Override
        public byte previousByte() {
            if (!this.hasPrevious()) {
                throw new NoSuchElementException();
            }
            this.curr = (byte)(this.curr - 1);
            return this.curr;
        }

        @Override
        public int nextIndex() {
            return this.curr - this.from;
        }

        @Override
        public int previousIndex() {
            return this.curr - this.from - 1;
        }

        @Override
        public int skip(int n2) {
            if (this.curr + n2 <= this.to) {
                this.curr = (byte)(this.curr + n2);
                return n2;
            }
            n2 = this.to - this.curr;
            this.curr = this.to;
            return n2;
        }

        @Override
        public int back(int n2) {
            if (this.curr - n2 >= this.from) {
                this.curr = (byte)(this.curr - n2);
                return n2;
            }
            n2 = this.curr - this.from;
            this.curr = this.from;
            return n2;
        }
    }

    private static class ListIteratorWrapper
    extends AbstractByteListIterator {
        final ListIterator<Byte> i;

        public ListIteratorWrapper(ListIterator<Byte> i2) {
            this.i = i2;
        }

        @Override
        public boolean hasNext() {
            return this.i.hasNext();
        }

        @Override
        public boolean hasPrevious() {
            return this.i.hasPrevious();
        }

        @Override
        public int nextIndex() {
            return this.i.nextIndex();
        }

        @Override
        public int previousIndex() {
            return this.i.previousIndex();
        }

        @Override
        public void set(byte k2) {
            this.i.set(k2);
        }

        @Override
        public void add(byte k2) {
            this.i.add(k2);
        }

        @Override
        public void remove() {
            this.i.remove();
        }

        @Override
        public byte nextByte() {
            return this.i.next();
        }

        @Override
        public byte previousByte() {
            return this.i.previous();
        }
    }

    private static class IteratorWrapper
    extends AbstractByteIterator {
        final Iterator<Byte> i;

        public IteratorWrapper(Iterator<Byte> i2) {
            this.i = i2;
        }

        @Override
        public boolean hasNext() {
            return this.i.hasNext();
        }

        @Override
        public void remove() {
            this.i.remove();
        }

        @Override
        public byte nextByte() {
            return this.i.next();
        }
    }

    private static class ArrayIterator
    extends AbstractByteListIterator {
        private final byte[] array;
        private final int offset;
        private final int length;
        private int curr;

        public ArrayIterator(byte[] array, int offset, int length) {
            this.array = array;
            this.offset = offset;
            this.length = length;
        }

        @Override
        public boolean hasNext() {
            return this.curr < this.length;
        }

        @Override
        public boolean hasPrevious() {
            return this.curr > 0;
        }

        @Override
        public byte nextByte() {
            if (!this.hasNext()) {
                throw new NoSuchElementException();
            }
            return this.array[this.offset + this.curr++];
        }

        @Override
        public byte previousByte() {
            if (!this.hasPrevious()) {
                throw new NoSuchElementException();
            }
            return this.array[this.offset + --this.curr];
        }

        @Override
        public int skip(int n2) {
            if (n2 <= this.length - this.curr) {
                this.curr += n2;
                return n2;
            }
            n2 = this.length - this.curr;
            this.curr = this.length;
            return n2;
        }

        @Override
        public int back(int n2) {
            if (n2 <= this.curr) {
                this.curr -= n2;
                return n2;
            }
            n2 = this.curr;
            this.curr = 0;
            return n2;
        }

        @Override
        public int nextIndex() {
            return this.curr;
        }

        @Override
        public int previousIndex() {
            return this.curr - 1;
        }
    }

    private static class SingletonIterator
    extends AbstractByteListIterator {
        private final byte element;
        private int curr;

        public SingletonIterator(byte element) {
            this.element = element;
        }

        @Override
        public boolean hasNext() {
            return this.curr == 0;
        }

        @Override
        public boolean hasPrevious() {
            return this.curr == 1;
        }

        @Override
        public byte nextByte() {
            if (!this.hasNext()) {
                throw new NoSuchElementException();
            }
            this.curr = 1;
            return this.element;
        }

        @Override
        public byte previousByte() {
            if (!this.hasPrevious()) {
                throw new NoSuchElementException();
            }
            this.curr = 0;
            return this.element;
        }

        @Override
        public int nextIndex() {
            return this.curr;
        }

        @Override
        public int previousIndex() {
            return this.curr - 1;
        }
    }

    public static class EmptyIterator
    extends AbstractByteListIterator
    implements Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;

        protected EmptyIterator() {
        }

        @Override
        public boolean hasNext() {
            return false;
        }

        @Override
        public boolean hasPrevious() {
            return false;
        }

        @Override
        public byte nextByte() {
            throw new NoSuchElementException();
        }

        @Override
        public byte previousByte() {
            throw new NoSuchElementException();
        }

        @Override
        public int nextIndex() {
            return 0;
        }

        @Override
        public int previousIndex() {
            return -1;
        }

        @Override
        public int skip(int n2) {
            return 0;
        }

        @Override
        public int back(int n2) {
            return 0;
        }

        public Object clone() {
            return EMPTY_ITERATOR;
        }

        private Object readResolve() {
            return EMPTY_ITERATOR;
        }
    }
}

