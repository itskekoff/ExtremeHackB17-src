package it.unimi.dsi.fastutil.booleans;

import it.unimi.dsi.fastutil.booleans.AbstractBooleanBigListIterator;
import it.unimi.dsi.fastutil.booleans.BooleanBigListIterator;
import it.unimi.dsi.fastutil.booleans.BooleanListIterator;
import java.io.Serializable;
import java.util.NoSuchElementException;

public class BooleanBigListIterators {
    public static final EmptyBigListIterator EMPTY_BIG_LIST_ITERATOR = new EmptyBigListIterator();

    private BooleanBigListIterators() {
    }

    public static BooleanBigListIterator singleton(boolean element) {
        return new SingletonBigListIterator(element);
    }

    public static BooleanBigListIterator unmodifiable(BooleanBigListIterator i2) {
        return new UnmodifiableBigListIterator(i2);
    }

    public static BooleanBigListIterator asBigListIterator(BooleanListIterator i2) {
        return new BigListIteratorListIterator(i2);
    }

    public static class BigListIteratorListIterator
    extends AbstractBooleanBigListIterator {
        protected final BooleanListIterator i;

        protected BigListIteratorListIterator(BooleanListIterator i2) {
            this.i = i2;
        }

        private int intDisplacement(long n2) {
            if (n2 < Integer.MIN_VALUE || n2 > Integer.MAX_VALUE) {
                throw new IndexOutOfBoundsException("This big iterator is restricted to 32-bit displacements");
            }
            return (int)n2;
        }

        @Override
        public void set(boolean ok2) {
            this.i.set(ok2);
        }

        @Override
        public void add(boolean ok2) {
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
        public boolean nextBoolean() {
            return this.i.nextBoolean();
        }

        @Override
        public boolean previousBoolean() {
            return this.i.previousBoolean();
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
    extends AbstractBooleanBigListIterator {
        protected final BooleanBigListIterator i;

        public UnmodifiableBigListIterator(BooleanBigListIterator i2) {
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
        public long nextIndex() {
            return this.i.nextIndex();
        }

        @Override
        public long previousIndex() {
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

    private static class SingletonBigListIterator
    extends AbstractBooleanBigListIterator {
        private final boolean element;
        private int curr;

        public SingletonBigListIterator(boolean element) {
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
        public long nextIndex() {
            return this.curr;
        }

        @Override
        public long previousIndex() {
            return this.curr - 1;
        }
    }

    public static class EmptyBigListIterator
    extends AbstractBooleanBigListIterator
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
        public boolean nextBoolean() {
            throw new NoSuchElementException();
        }

        @Override
        public boolean previousBoolean() {
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

