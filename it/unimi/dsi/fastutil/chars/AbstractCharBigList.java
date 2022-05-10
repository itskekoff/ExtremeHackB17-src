package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.BigList;
import it.unimi.dsi.fastutil.BigListIterator;
import it.unimi.dsi.fastutil.chars.AbstractCharBigListIterator;
import it.unimi.dsi.fastutil.chars.AbstractCharCollection;
import it.unimi.dsi.fastutil.chars.CharBigArrays;
import it.unimi.dsi.fastutil.chars.CharBigList;
import it.unimi.dsi.fastutil.chars.CharBigListIterator;
import it.unimi.dsi.fastutil.chars.CharCollection;
import it.unimi.dsi.fastutil.chars.CharList;
import it.unimi.dsi.fastutil.chars.CharStack;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

public abstract class AbstractCharBigList
extends AbstractCharCollection
implements CharBigList,
CharStack {
    protected AbstractCharBigList() {
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
    public void add(long index, char k2) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean add(char k2) {
        this.add(this.size64(), k2);
        return true;
    }

    @Override
    public char removeChar(long i2) {
        throw new UnsupportedOperationException();
    }

    public char removeChar(int i2) {
        return this.removeChar((long)i2);
    }

    @Override
    public char set(long index, char k2) {
        throw new UnsupportedOperationException();
    }

    public char set(int index, char k2) {
        return this.set((long)index, k2);
    }

    @Override
    public boolean addAll(long index, Collection<? extends Character> c2) {
        this.ensureIndex(index);
        int n2 = c2.size();
        if (n2 == 0) {
            return false;
        }
        Iterator<? extends Character> i2 = c2.iterator();
        while (n2-- != 0) {
            this.add(index++, i2.next());
        }
        return true;
    }

    public boolean addAll(int index, Collection<? extends Character> c2) {
        return this.addAll((long)index, c2);
    }

    @Override
    public boolean addAll(Collection<? extends Character> c2) {
        return this.addAll(this.size64(), c2);
    }

    @Override
    public CharBigListIterator iterator() {
        return this.listIterator();
    }

    @Override
    public CharBigListIterator listIterator() {
        return this.listIterator(0L);
    }

    @Override
    public CharBigListIterator listIterator(final long index) {
        this.ensureIndex(index);
        return new AbstractCharBigListIterator(){
            long pos;
            long last;
            {
                this.pos = index;
                this.last = -1L;
            }

            @Override
            public boolean hasNext() {
                return this.pos < AbstractCharBigList.this.size64();
            }

            @Override
            public boolean hasPrevious() {
                return this.pos > 0L;
            }

            @Override
            public char nextChar() {
                if (!this.hasNext()) {
                    throw new NoSuchElementException();
                }
                this.last = this.pos++;
                return AbstractCharBigList.this.getChar(this.last);
            }

            @Override
            public char previousChar() {
                if (!this.hasPrevious()) {
                    throw new NoSuchElementException();
                }
                this.last = --this.pos;
                return AbstractCharBigList.this.getChar(this.pos);
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
            public void add(char k2) {
                AbstractCharBigList.this.add(this.pos++, k2);
                this.last = -1L;
            }

            @Override
            public void set(char k2) {
                if (this.last == -1L) {
                    throw new IllegalStateException();
                }
                AbstractCharBigList.this.set(this.last, k2);
            }

            @Override
            public void remove() {
                if (this.last == -1L) {
                    throw new IllegalStateException();
                }
                AbstractCharBigList.this.removeChar(this.last);
                if (this.last < this.pos) {
                    --this.pos;
                }
                this.last = -1L;
            }
        };
    }

    public CharBigListIterator listIterator(int index) {
        return this.listIterator((long)index);
    }

    @Override
    public boolean contains(char k2) {
        return this.indexOf(k2) >= 0L;
    }

    @Override
    public long indexOf(char k2) {
        CharBigListIterator i2 = this.listIterator();
        while (i2.hasNext()) {
            char e2 = i2.nextChar();
            if (k2 != e2) continue;
            return i2.previousIndex();
        }
        return -1L;
    }

    @Override
    public long lastIndexOf(char k2) {
        CharBigListIterator i2 = this.listIterator(this.size64());
        while (i2.hasPrevious()) {
            char e2 = i2.previousChar();
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
                this.add('\u0000');
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
    public CharBigList subList(long from, long to2) {
        this.ensureIndex(from);
        this.ensureIndex(to2);
        if (from > to2) {
            throw new IndexOutOfBoundsException("Start index (" + from + ") is greater than end index (" + to2 + ")");
        }
        return new CharSubList(this, from, to2);
    }

    @Override
    public void removeElements(long from, long to2) {
        this.ensureIndex(to2);
        CharBigListIterator i2 = this.listIterator(from);
        long n2 = to2 - from;
        if (n2 < 0L) {
            throw new IllegalArgumentException("Start index (" + from + ") is greater than end index (" + to2 + ")");
        }
        while (n2-- != 0L) {
            i2.nextChar();
            i2.remove();
        }
    }

    @Override
    public void addElements(long index, char[][] a2, long offset, long length) {
        this.ensureIndex(index);
        CharBigArrays.ensureOffsetLength(a2, offset, length);
        while (length-- != 0L) {
            this.add(index++, CharBigArrays.get(a2, offset++));
        }
    }

    @Override
    public void addElements(long index, char[][] a2) {
        this.addElements(index, a2, 0L, CharBigArrays.length(a2));
    }

    @Override
    public void getElements(long from, char[][] a2, long offset, long length) {
        CharBigListIterator i2 = this.listIterator(from);
        CharBigArrays.ensureOffsetLength(a2, offset, length);
        if (from + length > this.size64()) {
            throw new IndexOutOfBoundsException("End index (" + (from + length) + ") is greater than list size (" + this.size64() + ")");
        }
        while (length-- != 0L) {
            CharBigArrays.set(a2, offset++, i2.nextChar());
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
        if (l2 instanceof CharBigList) {
            CharBigListIterator i1 = this.listIterator();
            CharBigListIterator i2 = ((CharBigList)l2).listIterator();
            while (s2-- != 0L) {
                if (i1.nextChar() == i2.nextChar()) continue;
                return false;
            }
            return true;
        }
        CharBigListIterator i1 = this.listIterator();
        BigListIterator i2 = l2.listIterator();
        while (s2-- != 0L) {
            if (this.valEquals(i1.next(), i2.next())) continue;
            return false;
        }
        return true;
    }

    @Override
    public int compareTo(BigList<? extends Character> l2) {
        if (l2 == this) {
            return 0;
        }
        if (l2 instanceof CharBigList) {
            CharBigListIterator i1 = this.listIterator();
            CharBigListIterator i2 = ((CharBigList)l2).listIterator();
            while (i1.hasNext() && i2.hasNext()) {
                char e2;
                char e1 = i1.nextChar();
                int r2 = Character.compare(e1, e2 = i2.nextChar());
                if (r2 == 0) continue;
                return r2;
            }
            return i2.hasNext() ? -1 : (i1.hasNext() ? 1 : 0);
        }
        CharBigListIterator i1 = this.listIterator();
        BigListIterator<? extends Character> i2 = l2.listIterator();
        while (i1.hasNext() && i2.hasNext()) {
            int r3 = ((Comparable)i1.next()).compareTo(i2.next());
            if (r3 == 0) continue;
            return r3;
        }
        return i2.hasNext() ? -1 : (i1.hasNext() ? 1 : 0);
    }

    @Override
    public int hashCode() {
        CharBigListIterator i2 = this.iterator();
        int h2 = 1;
        long s2 = this.size64();
        while (s2-- != 0L) {
            char k2 = i2.nextChar();
            h2 = 31 * h2 + k2;
        }
        return h2;
    }

    @Override
    public void push(char o2) {
        this.add(o2);
    }

    @Override
    public char popChar() {
        if (this.isEmpty()) {
            throw new NoSuchElementException();
        }
        return this.removeChar(this.size64() - 1L);
    }

    @Override
    public char topChar() {
        if (this.isEmpty()) {
            throw new NoSuchElementException();
        }
        return this.getChar(this.size64() - 1L);
    }

    @Override
    public char peekChar(int i2) {
        return this.getChar(this.size64() - 1L - (long)i2);
    }

    public char getChar(int index) {
        return this.getChar((long)index);
    }

    @Override
    public boolean rem(char k2) {
        long index = this.indexOf(k2);
        if (index == -1L) {
            return false;
        }
        this.removeChar(index);
        return true;
    }

    @Override
    public boolean addAll(long index, CharCollection c2) {
        return this.addAll(index, (Collection<? extends Character>)c2);
    }

    @Override
    public boolean addAll(long index, CharBigList l2) {
        return this.addAll(index, (CharCollection)l2);
    }

    @Override
    public boolean addAll(CharCollection c2) {
        return this.addAll(this.size64(), c2);
    }

    @Override
    public boolean addAll(CharBigList l2) {
        return this.addAll(this.size64(), l2);
    }

    @Override
    @Deprecated
    public void add(long index, Character ok2) {
        this.add(index, ok2.charValue());
    }

    @Override
    @Deprecated
    public Character set(long index, Character ok2) {
        return Character.valueOf(this.set(index, ok2.charValue()));
    }

    @Override
    @Deprecated
    public Character get(long index) {
        return Character.valueOf(this.getChar(index));
    }

    @Override
    @Deprecated
    public long indexOf(Object ok2) {
        return this.indexOf(((Character)ok2).charValue());
    }

    @Override
    @Deprecated
    public long lastIndexOf(Object ok2) {
        return this.lastIndexOf(((Character)ok2).charValue());
    }

    @Deprecated
    public Character remove(int index) {
        return Character.valueOf(this.removeChar(index));
    }

    @Override
    @Deprecated
    public Character remove(long index) {
        return Character.valueOf(this.removeChar(index));
    }

    @Override
    @Deprecated
    public void push(Character o2) {
        this.push(o2.charValue());
    }

    @Override
    @Deprecated
    public Character pop() {
        return Character.valueOf(this.popChar());
    }

    @Override
    @Deprecated
    public Character top() {
        return Character.valueOf(this.topChar());
    }

    @Override
    @Deprecated
    public Character peek(int i2) {
        return Character.valueOf(this.peekChar(i2));
    }

    @Override
    public String toString() {
        StringBuilder s2 = new StringBuilder();
        CharBigListIterator i2 = this.iterator();
        long n2 = this.size64();
        boolean first = true;
        s2.append("[");
        while (n2-- != 0L) {
            if (first) {
                first = false;
            } else {
                s2.append(", ");
            }
            char k2 = i2.nextChar();
            s2.append(String.valueOf(k2));
        }
        s2.append("]");
        return s2.toString();
    }

    public static class CharSubList
    extends AbstractCharBigList
    implements Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final CharBigList l;
        protected final long from;
        protected long to;
        private static final boolean ASSERTS = false;

        public CharSubList(CharBigList l2, long from, long to2) {
            this.l = l2;
            this.from = from;
            this.to = to2;
        }

        private void assertRange() {
        }

        @Override
        public boolean add(char k2) {
            this.l.add(this.to, k2);
            ++this.to;
            return true;
        }

        @Override
        public void add(long index, char k2) {
            this.ensureIndex(index);
            this.l.add(this.from + index, k2);
            ++this.to;
        }

        @Override
        public boolean addAll(long index, Collection<? extends Character> c2) {
            this.ensureIndex(index);
            this.to += (long)c2.size();
            return this.l.addAll(this.from + index, c2);
        }

        @Override
        public char getChar(long index) {
            this.ensureRestrictedIndex(index);
            return this.l.getChar(this.from + index);
        }

        @Override
        public char removeChar(long index) {
            this.ensureRestrictedIndex(index);
            --this.to;
            return this.l.removeChar(this.from + index);
        }

        @Override
        public char set(long index, char k2) {
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
        public void getElements(long from, char[][] a2, long offset, long length) {
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
        public void addElements(long index, char[][] a2, long offset, long length) {
            this.ensureIndex(index);
            this.l.addElements(this.from + index, a2, offset, length);
            this.to += length;
        }

        @Override
        public CharBigListIterator listIterator(final long index) {
            this.ensureIndex(index);
            return new AbstractCharBigListIterator(){
                long pos;
                long last;
                {
                    this.pos = index;
                    this.last = -1L;
                }

                @Override
                public boolean hasNext() {
                    return this.pos < CharSubList.this.size64();
                }

                @Override
                public boolean hasPrevious() {
                    return this.pos > 0L;
                }

                @Override
                public char nextChar() {
                    if (!this.hasNext()) {
                        throw new NoSuchElementException();
                    }
                    this.last = this.pos++;
                    return CharSubList.this.l.getChar(CharSubList.this.from + this.last);
                }

                @Override
                public char previousChar() {
                    if (!this.hasPrevious()) {
                        throw new NoSuchElementException();
                    }
                    this.last = --this.pos;
                    return CharSubList.this.l.getChar(CharSubList.this.from + this.pos);
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
                public void add(char k2) {
                    if (this.last == -1L) {
                        throw new IllegalStateException();
                    }
                    CharSubList.this.add(this.pos++, k2);
                    this.last = -1L;
                }

                @Override
                public void set(char k2) {
                    if (this.last == -1L) {
                        throw new IllegalStateException();
                    }
                    CharSubList.this.set(this.last, k2);
                }

                @Override
                public void remove() {
                    if (this.last == -1L) {
                        throw new IllegalStateException();
                    }
                    CharSubList.this.removeChar(this.last);
                    if (this.last < this.pos) {
                        --this.pos;
                    }
                    this.last = -1L;
                }
            };
        }

        @Override
        public CharBigList subList(long from, long to2) {
            this.ensureIndex(from);
            this.ensureIndex(to2);
            if (from > to2) {
                throw new IllegalArgumentException("Start index (" + from + ") is greater than end index (" + to2 + ")");
            }
            return new CharSubList(this, from, to2);
        }

        @Override
        public boolean rem(char k2) {
            long index = this.indexOf(k2);
            if (index == -1L) {
                return false;
            }
            --this.to;
            this.l.removeChar(this.from + index);
            return true;
        }

        @Override
        public boolean remove(Object o2) {
            return this.rem(((Character)o2).charValue());
        }

        @Override
        public boolean addAll(long index, CharCollection c2) {
            this.ensureIndex(index);
            this.to += (long)c2.size();
            return this.l.addAll(this.from + index, c2);
        }

        public boolean addAll(long index, CharList l2) {
            this.ensureIndex(index);
            this.to += (long)l2.size();
            return this.l.addAll(this.from + index, l2);
        }
    }
}

