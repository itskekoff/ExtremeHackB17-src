package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.chars.AbstractCharCollection;
import it.unimi.dsi.fastutil.chars.AbstractCharListIterator;
import it.unimi.dsi.fastutil.chars.CharCollection;
import it.unimi.dsi.fastutil.chars.CharList;
import it.unimi.dsi.fastutil.chars.CharListIterator;
import it.unimi.dsi.fastutil.chars.CharStack;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;

public abstract class AbstractCharList
extends AbstractCharCollection
implements CharList,
CharStack {
    protected AbstractCharList() {
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
    public void add(int index, char k2) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean add(char k2) {
        this.add(this.size(), k2);
        return true;
    }

    @Override
    public char removeChar(int i2) {
        throw new UnsupportedOperationException();
    }

    @Override
    public char set(int index, char k2) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(int index, Collection<? extends Character> c2) {
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

    @Override
    public boolean addAll(Collection<? extends Character> c2) {
        return this.addAll(this.size(), c2);
    }

    @Override
    @Deprecated
    public CharListIterator charListIterator() {
        return this.listIterator();
    }

    @Override
    @Deprecated
    public CharListIterator charListIterator(int index) {
        return this.listIterator(index);
    }

    @Override
    public CharListIterator iterator() {
        return this.listIterator();
    }

    @Override
    public CharListIterator listIterator() {
        return this.listIterator(0);
    }

    @Override
    public CharListIterator listIterator(final int index) {
        this.ensureIndex(index);
        return new AbstractCharListIterator(){
            int pos;
            int last;
            {
                this.pos = index;
                this.last = -1;
            }

            @Override
            public boolean hasNext() {
                return this.pos < AbstractCharList.this.size();
            }

            @Override
            public boolean hasPrevious() {
                return this.pos > 0;
            }

            @Override
            public char nextChar() {
                if (!this.hasNext()) {
                    throw new NoSuchElementException();
                }
                this.last = this.pos++;
                return AbstractCharList.this.getChar(this.last);
            }

            @Override
            public char previousChar() {
                if (!this.hasPrevious()) {
                    throw new NoSuchElementException();
                }
                this.last = --this.pos;
                return AbstractCharList.this.getChar(this.pos);
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
            public void add(char k2) {
                AbstractCharList.this.add(this.pos++, k2);
                this.last = -1;
            }

            @Override
            public void set(char k2) {
                if (this.last == -1) {
                    throw new IllegalStateException();
                }
                AbstractCharList.this.set(this.last, k2);
            }

            @Override
            public void remove() {
                if (this.last == -1) {
                    throw new IllegalStateException();
                }
                AbstractCharList.this.removeChar(this.last);
                if (this.last < this.pos) {
                    --this.pos;
                }
                this.last = -1;
            }
        };
    }

    @Override
    public boolean contains(char k2) {
        return this.indexOf(k2) >= 0;
    }

    @Override
    public int indexOf(char k2) {
        CharListIterator i2 = this.listIterator();
        while (i2.hasNext()) {
            char e2 = i2.nextChar();
            if (k2 != e2) continue;
            return i2.previousIndex();
        }
        return -1;
    }

    @Override
    public int lastIndexOf(char k2) {
        CharListIterator i2 = this.listIterator(this.size());
        while (i2.hasPrevious()) {
            char e2 = i2.previousChar();
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
                this.add('\u0000');
            }
        } else {
            while (i2-- != size) {
                this.remove(i2);
            }
        }
    }

    @Override
    public CharList subList(int from, int to2) {
        this.ensureIndex(from);
        this.ensureIndex(to2);
        if (from > to2) {
            throw new IndexOutOfBoundsException("Start index (" + from + ") is greater than end index (" + to2 + ")");
        }
        return new CharSubList(this, from, to2);
    }

    @Override
    @Deprecated
    public CharList charSubList(int from, int to2) {
        return this.subList(from, to2);
    }

    @Override
    public void removeElements(int from, int to2) {
        this.ensureIndex(to2);
        CharListIterator i2 = this.listIterator(from);
        int n2 = to2 - from;
        if (n2 < 0) {
            throw new IllegalArgumentException("Start index (" + from + ") is greater than end index (" + to2 + ")");
        }
        while (n2-- != 0) {
            i2.nextChar();
            i2.remove();
        }
    }

    @Override
    public void addElements(int index, char[] a2, int offset, int length) {
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
    public void addElements(int index, char[] a2) {
        this.addElements(index, a2, 0, a2.length);
    }

    @Override
    public void getElements(int from, char[] a2, int offset, int length) {
        CharListIterator i2 = this.listIterator(from);
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
            a2[offset++] = i2.nextChar();
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
        if (l2 instanceof CharList) {
            CharListIterator i1 = this.listIterator();
            CharListIterator i2 = ((CharList)l2).listIterator();
            while (s2-- != 0) {
                if (i1.nextChar() == i2.nextChar()) continue;
                return false;
            }
            return true;
        }
        CharListIterator i1 = this.listIterator();
        ListIterator i2 = l2.listIterator();
        while (s2-- != 0) {
            if (this.valEquals(i1.next(), i2.next())) continue;
            return false;
        }
        return true;
    }

    @Override
    public int compareTo(List<? extends Character> l2) {
        if (l2 == this) {
            return 0;
        }
        if (l2 instanceof CharList) {
            CharListIterator i1 = this.listIterator();
            CharListIterator i2 = ((CharList)l2).listIterator();
            while (i1.hasNext() && i2.hasNext()) {
                char e2;
                char e1 = i1.nextChar();
                int r2 = Character.compare(e1, e2 = i2.nextChar());
                if (r2 == 0) continue;
                return r2;
            }
            return i2.hasNext() ? -1 : (i1.hasNext() ? 1 : 0);
        }
        CharListIterator i1 = this.listIterator();
        ListIterator<? extends Character> i2 = l2.listIterator();
        while (i1.hasNext() && i2.hasNext()) {
            int r3 = ((Comparable)i1.next()).compareTo(i2.next());
            if (r3 == 0) continue;
            return r3;
        }
        return i2.hasNext() ? -1 : (i1.hasNext() ? 1 : 0);
    }

    @Override
    public int hashCode() {
        CharListIterator i2 = this.iterator();
        int h2 = 1;
        int s2 = this.size();
        while (s2-- != 0) {
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
        return this.removeChar(this.size() - 1);
    }

    @Override
    public char topChar() {
        if (this.isEmpty()) {
            throw new NoSuchElementException();
        }
        return this.getChar(this.size() - 1);
    }

    @Override
    public char peekChar(int i2) {
        return this.getChar(this.size() - 1 - i2);
    }

    @Override
    public boolean rem(char k2) {
        int index = this.indexOf(k2);
        if (index == -1) {
            return false;
        }
        this.removeChar(index);
        return true;
    }

    @Override
    public boolean remove(Object o2) {
        return this.rem(((Character)o2).charValue());
    }

    @Override
    public boolean addAll(int index, CharCollection c2) {
        return this.addAll(index, (Collection<? extends Character>)c2);
    }

    @Override
    public boolean addAll(int index, CharList l2) {
        return this.addAll(index, (CharCollection)l2);
    }

    @Override
    public boolean addAll(CharCollection c2) {
        return this.addAll(this.size(), c2);
    }

    @Override
    public boolean addAll(CharList l2) {
        return this.addAll(this.size(), l2);
    }

    @Override
    public void add(int index, Character ok2) {
        this.add(index, ok2.charValue());
    }

    @Override
    @Deprecated
    public Character set(int index, Character ok2) {
        return Character.valueOf(this.set(index, ok2.charValue()));
    }

    @Override
    @Deprecated
    public Character get(int index) {
        return Character.valueOf(this.getChar(index));
    }

    @Override
    public int indexOf(Object ok2) {
        return this.indexOf(((Character)ok2).charValue());
    }

    @Override
    public int lastIndexOf(Object ok2) {
        return this.lastIndexOf(((Character)ok2).charValue());
    }

    @Override
    @Deprecated
    public Character remove(int index) {
        return Character.valueOf(this.removeChar(index));
    }

    @Override
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
        CharListIterator i2 = this.iterator();
        int n2 = this.size();
        boolean first = true;
        s2.append("[");
        while (n2-- != 0) {
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
    extends AbstractCharList
    implements Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final CharList l;
        protected final int from;
        protected int to;
        private static final boolean ASSERTS = false;

        public CharSubList(CharList l2, int from, int to2) {
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
        public void add(int index, char k2) {
            this.ensureIndex(index);
            this.l.add(this.from + index, k2);
            ++this.to;
        }

        @Override
        public boolean addAll(int index, Collection<? extends Character> c2) {
            this.ensureIndex(index);
            this.to += c2.size();
            return this.l.addAll(this.from + index, c2);
        }

        @Override
        public char getChar(int index) {
            this.ensureRestrictedIndex(index);
            return this.l.getChar(this.from + index);
        }

        @Override
        public char removeChar(int index) {
            this.ensureRestrictedIndex(index);
            --this.to;
            return this.l.removeChar(this.from + index);
        }

        @Override
        public char set(int index, char k2) {
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
        public void getElements(int from, char[] a2, int offset, int length) {
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
        public void addElements(int index, char[] a2, int offset, int length) {
            this.ensureIndex(index);
            this.l.addElements(this.from + index, a2, offset, length);
            this.to += length;
        }

        @Override
        public CharListIterator listIterator(final int index) {
            this.ensureIndex(index);
            return new AbstractCharListIterator(){
                int pos;
                int last;
                {
                    this.pos = index;
                    this.last = -1;
                }

                @Override
                public boolean hasNext() {
                    return this.pos < CharSubList.this.size();
                }

                @Override
                public boolean hasPrevious() {
                    return this.pos > 0;
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
                public int nextIndex() {
                    return this.pos;
                }

                @Override
                public int previousIndex() {
                    return this.pos - 1;
                }

                @Override
                public void add(char k2) {
                    if (this.last == -1) {
                        throw new IllegalStateException();
                    }
                    CharSubList.this.add(this.pos++, k2);
                    this.last = -1;
                }

                @Override
                public void set(char k2) {
                    if (this.last == -1) {
                        throw new IllegalStateException();
                    }
                    CharSubList.this.set(this.last, k2);
                }

                @Override
                public void remove() {
                    if (this.last == -1) {
                        throw new IllegalStateException();
                    }
                    CharSubList.this.removeChar(this.last);
                    if (this.last < this.pos) {
                        --this.pos;
                    }
                    this.last = -1;
                }
            };
        }

        @Override
        public CharList subList(int from, int to2) {
            this.ensureIndex(from);
            this.ensureIndex(to2);
            if (from > to2) {
                throw new IllegalArgumentException("Start index (" + from + ") is greater than end index (" + to2 + ")");
            }
            return new CharSubList(this, from, to2);
        }

        @Override
        public boolean rem(char k2) {
            int index = this.indexOf(k2);
            if (index == -1) {
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
        public boolean addAll(int index, CharCollection c2) {
            this.ensureIndex(index);
            this.to += c2.size();
            return this.l.addAll(this.from + index, c2);
        }

        @Override
        public boolean addAll(int index, CharList l2) {
            this.ensureIndex(index);
            this.to += l2.size();
            return this.l.addAll(this.from + index, l2);
        }
    }
}

