package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.bytes.AbstractByteBigListIterator;
import it.unimi.dsi.fastutil.bytes.ByteBigListIterator;
import it.unimi.dsi.fastutil.bytes.ByteListIterator;
import java.io.Serializable;
import java.util.NoSuchElementException;

public class ByteBigListIterators {
    public static final EmptyBigListIterator EMPTY_BIG_LIST_ITERATOR = new EmptyBigListIterator();

    private ByteBigListIterators() {
    }

    public static ByteBigListIterator singleton(byte element) {
        return new SingletonBigListIterator(element);
    }

    public static ByteBigListIterator unmodifiable(ByteBigListIterator i2) {
        return new UnmodifiableBigListIterator(i2);
    }

    public static ByteBigListIterator asBigListIterator(ByteListIterator i2) {
        return new BigListIteratorListIterator(i2);
    }

    public static class BigListIteratorListIterator
    extends AbstractByteBigListIterator {
        protected final ByteListIterator i;

        protected BigListIteratorListIterator(ByteListIterator i2) {
            this.i = i2;
        }

        private int intDisplacement(long n2) {
            if (n2 < Integer.MIN_VALUE || n2 > Integer.MAX_VALUE) {
                throw new IndexOutOfBoundsException("This big iterator is restricted to 32-bit displacements");
            }
            return (int)n2;
        }

        @Override
        public void set(byte ok2) {
            this.i.set(ok2);
        }

        @Override
        public void add(byte ok2) {
            this.i.add(ok2);
        }

        @Override
        public int back(int n2) {
            return this.i.back(n2);
        }

        @Override
        public long back(long n2) {
            return this.i.back(this.intDisplacement(n2));
        }

        @Override
        public void remove() {
            this.i.remove();
        }

        @Override
        public int skip(int n2) {
            return this.i.skip(n2);
        }

        @Override
        public long skip(long n2) {
            return this.i.skip(this.intDisplacement(n2));
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
        public long nextIndex() {
            return this.i.nextIndex();
        }

        @Override
        public long previousIndex() {
            return this.i.previousIndex();
        }
    }

    public static class UnmodifiableBigListIterator
    extends AbstractByteBigListIterator {
        protected final ByteBigListIterator i;

        public UnmodifiableBigListIterator(ByteBigListIterator i2) {
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
        public long nextIndex() {
            return this.i.nextIndex();
        }

        @Override
        public long previousIndex() {
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

    private static class SingletonBigListIterator
    extends AbstractByteBigListIterator {
        private final byte element;
        private int curr;

        public SingletonBigListIterator(byte element) {
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
        public long nextIndex() {
            return this.curr;
        }

        @Override
        public long previousIndex() {
            return this.curr - 1;
        }
    }

    public static class EmptyBigListIterator
    extends AbstractByteBigListIterator
    implements Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;

        protected EmptyBigListIterator() {
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
        public long nextIndex() {
            return 0L;
        }

        @Override
        public long previousIndex() {
            return -1L;
        }

        @Override
        public long skip(long n2) {
            return 0L;
        }

        @Override
        public long back(long n2) {
            return 0L;
        }

        public Object clone() {
            return EMPTY_BIG_LIST_ITERATOR;
        }

        private Object readResolve() {
            return EMPTY_BIG_LIST_ITERATOR;
        }
    }
}

