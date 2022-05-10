package it.unimi.dsi.fastutil.booleans;

import it.unimi.dsi.fastutil.booleans.AbstractBooleanBidirectionalIterator;
import it.unimi.dsi.fastutil.booleans.AbstractBooleanIterator;
import it.unimi.dsi.fastutil.booleans.AbstractBooleanListIterator;
import it.unimi.dsi.fastutil.booleans.BooleanArrayList;
import it.unimi.dsi.fastutil.booleans.BooleanArrays;
import it.unimi.dsi.fastutil.booleans.BooleanBidirectionalIterator;
import it.unimi.dsi.fastutil.booleans.BooleanCollection;
import it.unimi.dsi.fastutil.booleans.BooleanIterator;
import it.unimi.dsi.fastutil.booleans.BooleanList;
import it.unimi.dsi.fastutil.booleans.BooleanListIterator;
import java.io.Serializable;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.NoSuchElementException;

public class BooleanIterators {
    public static final EmptyIterator EMPTY_ITERATOR = new EmptyIterator();

    private BooleanIterators() {
    }

    public static BooleanListIterator singleton(boolean element) {
        return new SingletonIterator(element);
    }

    public static BooleanListIterator wrap(boolean[] array, int offset, int length) {
        BooleanArrays.ensureOffsetLength(array, offset, length);
        return new ArrayIterator(array, offset, length);
    }

    public static BooleanListIterator wrap(boolean[] array) {
        return new ArrayIterator(array, 0, array.length);
    }

    public static int unwrap(BooleanIterator i2, boolean[] array, int offset, int max) {
        if (max < 0) {
            throw new IllegalArgumentException("The maximum number of elements (" + max + ") is negative");
        }
        if (offset < 0 || offset + max > array.length) {
            throw new IllegalArgumentException();
        }
        int j2 = max;
        while (j2-- != 0 && i2.hasNext()) {
            array[offset++] = i2.nextBoolean();
        }
        return max - j2 - 1;
    }

    public static int unwrap(BooleanIterator i2, boolean[] array) {
        return BooleanIterators.unwrap(i2, array, 0, array.length);
    }

    public static boolean[] unwrap(BooleanIterator i2, int max) {
        if (max < 0) {
            throw new IllegalArgumentException("The maximum number of elements (" + max + ") is negative");
        }
        boolean[] array = new boolean[16];
        int j2 = 0;
        while (max-- != 0 && i2.hasNext()) {
            if (j2 == array.length) {
                array = BooleanArrays.grow(array, j2 + 1);
            }
            array[j2++] = i2.nextBoolean();
        }
        return BooleanArrays.trim(array, j2);
    }

    public static boolean[] unwrap(BooleanIterator i2) {
        return BooleanIterators.unwrap(i2, Integer.MAX_VALUE);
    }

    public static int unwrap(BooleanIterator i2, BooleanCollection c2, int max) {
        if (max < 0) {
            throw new IllegalArgumentException("The maximum number of elements (" + max + ") is negative");
        }
        int j2 = max;
        while (j2-- != 0 && i2.hasNext()) {
            c2.add(i2.nextBoolean());
        }
        return max - j2 - 1;
    }

    public static long unwrap(BooleanIterator i2, BooleanCollection c2) {
        long n2 = 0L;
        while (i2.hasNext()) {
            c2.add(i2.nextBoolean());
            ++n2;
        }
        return n2;
    }

    public static int pour(BooleanIterator i2, BooleanCollection s2, int max) {
        if (max < 0) {
            throw new IllegalArgumentException("The maximum number of elements (" + max + ") is negative");
        }
        int j2 = max;
        while (j2-- != 0 && i2.hasNext()) {
            s2.add(i2.nextBoolean());
        }
        return max - j2 - 1;
    }

    public static int pour(BooleanIterator i2, BooleanCollection s2) {
        return BooleanIterators.pour(i2, s2, Integer.MAX_VALUE);
    }

    public static BooleanList pour(BooleanIterator i2, int max) {
        BooleanArrayList l2 = new BooleanArrayList();
        BooleanIterators.pour(i2, l2, max);
        l2.trim();
        return l2;
    }

    public static BooleanList pour(BooleanIterator i2) {
        return BooleanIterators.pour(i2, Integer.MAX_VALUE);
    }

    public static BooleanIterator asBooleanIterator(Iterator i2) {
        if (i2 instanceof BooleanIterator) {
            return (BooleanIterator)i2;
        }
        return new IteratorWrapper(i2);
    }

    public static BooleanListIterator asBooleanIterator(ListIterator i2) {
        if (i2 instanceof BooleanListIterator) {
            return (BooleanListIterator)i2;
        }
        return new ListIteratorWrapper(i2);
    }

    public static BooleanIterator concat(BooleanIterator[] a2) {
        return BooleanIterators.concat(a2, 0, a2.length);
    }

    public static BooleanIterator concat(BooleanIterator[] a2, int offset, int length) {
        return new IteratorConcatenator(a2, offset, length);
    }

    public static BooleanIterator unmodifiable(BooleanIterator i2) {
        return new UnmodifiableIterator(i2);
    }

    public static BooleanBidirectionalIterator unmodifiable(BooleanBidirectionalIterator i2) {
        return new UnmodifiableBidirectionalIterator(i2);
    }

    public static BooleanListIterator unmodifiable(BooleanListIterator i2) {
        return new UnmodifiableListIterator(i2);
    }

    public static class UnmodifiableListIterator
    extends AbstractBooleanListIterator {
        protected final BooleanListIterator i;

        public UnmodifiableListIterator(BooleanListIterator i2) {
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
        public boolean nextBoolean() {
            return this.i.nextBoolean();
        }

        @Override
        public boolean previousBoolean() {
            return this.i.previousBoolean();
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
        public Boolean next() {
            return (Boolean)this.i.next();
        }

        @Override
        @Deprecated
        public Boolean previous() {
            return (Boolean)this.i.previous();
        }
    }

    public static class UnmodifiableBidirectionalIterator
    extends AbstractBooleanBidirectionalIterator {
        protected final BooleanBidirectionalIterator i;

        public UnmodifiableBidirectionalIterator(BooleanBidirectionalIterator i2) {
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
        public boolean nextBoolean() {
            return this.i.nextBoolean();
        }

        @Override
        public boolean previousBoolean() {
            return this.i.previousBoolean();
        }

        @Override
        @Deprecated
        public Boolean next() {
            return (Boolean)this.i.next();
        }

        @Override
        @Deprecated
        public Boolean previous() {
            return (Boolean)this.i.previous();
        }
    }

    public static class UnmodifiableIterator
    extends AbstractBooleanIterator {
        protected final BooleanIterator i;

        public UnmodifiableIterator(BooleanIterator i2) {
            this.i = i2;
        }

        @Override
        public boolean hasNext() {
            return this.i.hasNext();
        }

        @Override
        public boolean nextBoolean() {
            return this.i.nextBoolean();
        }

        @Override
        @Deprecated
        public Boolean next() {
            return (Boolean)this.i.next();
        }
    }

    private static class IteratorConcatenator
    extends AbstractBooleanIterator {
        final BooleanIterator[] a;
        int offset;
        int length;
        int lastOffset = -1;

        public IteratorConcatenator(BooleanIterator[] a2, int offset, int length) {
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
        public boolean nextBoolean() {
            if (!this.hasNext()) {
                throw new NoSuchElementException();
            }
            this.lastOffset = this.offset;
            boolean next = this.a[this.lastOffset].nextBoolean();
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

    private static class ListIteratorWrapper
    extends AbstractBooleanListIterator {
        final ListIterator<Boolean> i;

        public ListIteratorWrapper(ListIterator<Boolean> i2) {
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
        public void set(boolean k2) {
            this.i.set(k2);
        }

        @Override
        public void add(boolean k2) {
            this.i.add(k2);
        }

        @Override
        public void remove() {
            this.i.remove();
        }

        @Override
        public boolean nextBoolean() {
            return this.i.next();
        }

        @Override
        public boolean previousBoolean() {
            return this.i.previous();
        }
    }

    private static class IteratorWrapper
    extends AbstractBooleanIterator {
        final Iterator<Boolean> i;

        public IteratorWrapper(Iterator<Boolean> i2) {
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
        public boolean nextBoolean() {
            return this.i.next();
        }
    }

    private static class ArrayIterator
    extends AbstractBooleanListIterator {
        private final boolean[] array;
        private final int offset;
        private final int length;
        private int curr;

        public ArrayIterator(boolean[] array, int offset, int length) {
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
        public boolean nextBoolean() {
            if (!this.hasNext()) {
                throw new NoSuchElementException();
            }
            return this.array[this.offset + this.curr++];
        }

        @Override
        public boolean previousBoolean() {
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
    extends AbstractBooleanListIterator {
        private final boolean element;
        private int curr;

        public SingletonIterator(boolean element) {
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
        public boolean nextBoolean() {
            if (!this.hasNext()) {
                throw new NoSuchElementException();
            }
            this.curr = 1;
            return this.element;
        }

        @Override
        public boolean previousBoolean() {
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
    extends AbstractBooleanListIterator
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
        public boolean nextBoolean() {
            throw new NoSuchElementException();
        }

        @Override
        public boolean previousBoolean() {
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

