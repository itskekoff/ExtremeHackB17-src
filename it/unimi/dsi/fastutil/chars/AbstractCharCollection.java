package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.chars.CharCollection;
import it.unimi.dsi.fastutil.chars.CharIterator;
import it.unimi.dsi.fastutil.chars.CharIterators;
import it.unimi.dsi.fastutil.objects.ObjectIterators;
import java.lang.reflect.Array;
import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Iterator;

public abstract class AbstractCharCollection
extends AbstractCollection<Character>
implements CharCollection {
    protected AbstractCharCollection() {
    }

    @Override
    public char[] toArray(char[] a2) {
        return this.toCharArray(a2);
    }

    @Override
    public char[] toCharArray() {
        return this.toCharArray(null);
    }

    @Override
    public char[] toCharArray(char[] a2) {
        if (a2 == null || a2.length < this.size()) {
            a2 = new char[this.size()];
        }
        CharIterators.unwrap(this.iterator(), a2);
        return a2;
    }

    @Override
    public boolean addAll(CharCollection c2) {
        boolean retVal = false;
        CharIterator i2 = c2.iterator();
        int n2 = c2.size();
        while (n2-- != 0) {
            if (!this.add(i2.nextChar())) continue;
            retVal = true;
        }
        return retVal;
    }

    @Override
    public boolean containsAll(CharCollection c2) {
        CharIterator i2 = c2.iterator();
        int n2 = c2.size();
        while (n2-- != 0) {
            if (this.contains(i2.nextChar())) continue;
            return false;
        }
        return true;
    }

    @Override
    public boolean retainAll(CharCollection c2) {
        boolean retVal = false;
        int n2 = this.size();
        CharIterator i2 = this.iterator();
        while (n2-- != 0) {
            if (c2.contains(i2.nextChar())) continue;
            i2.remove();
            retVal = true;
        }
        return retVal;
    }

    @Override
    public boolean removeAll(CharCollection c2) {
        boolean retVal = false;
        int n2 = c2.size();
        CharIterator i2 = c2.iterator();
        while (n2-- != 0) {
            if (!this.rem(i2.nextChar())) continue;
            retVal = true;
        }
        return retVal;
    }

    @Override
    public Object[] toArray() {
        Object[] a2 = new Object[this.size()];
        ObjectIterators.unwrap(this.iterator(), a2);
        return a2;
    }

    @Override
    public <T> T[] toArray(T[] a2) {
        int size = this.size();
        if (a2.length < size) {
            a2 = (Object[])Array.newInstance(a2.getClass().getComponentType(), size);
        }
        ObjectIterators.unwrap(this.iterator(), a2);
        if (size < a2.length) {
            a2[size] = null;
        }
        return a2;
    }

    @Override
    public boolean addAll(Collection<? extends Character> c2) {
        boolean retVal = false;
        Iterator<? extends Character> i2 = c2.iterator();
        int n2 = c2.size();
        while (n2-- != 0) {
            if (!this.add(i2.next())) continue;
            retVal = true;
        }
        return retVal;
    }

    @Override
    public boolean add(char k2) {
        throw new UnsupportedOperationException();
    }

    @Override
    @Deprecated
    public CharIterator charIterator() {
        return this.iterator();
    }

    @Override
    public abstract CharIterator iterator();

    @Override
    public boolean remove(Object ok2) {
        if (ok2 == null) {
            return false;
        }
        return this.rem(((Character)ok2).charValue());
    }

    @Override
    public boolean add(Character o2) {
        return this.add(o2.charValue());
    }

    public boolean rem(Object o2) {
        if (o2 == null) {
            return false;
        }
        return this.rem(((Character)o2).charValue());
    }

    @Override
    public boolean contains(Object o2) {
        if (o2 == null) {
            return false;
        }
        return this.contains(((Character)o2).charValue());
    }

    @Override
    public boolean contains(char k2) {
        CharIterator iterator = this.iterator();
        while (iterator.hasNext()) {
            if (k2 != iterator.nextChar()) continue;
            return true;
        }
        return false;
    }

    @Override
    public boolean rem(char k2) {
        CharIterator iterator = this.iterator();
        while (iterator.hasNext()) {
            if (k2 != iterator.nextChar()) continue;
            iterator.remove();
            return true;
        }
        return false;
    }

    @Override
    public boolean containsAll(Collection<?> c2) {
        int n2 = c2.size();
        Iterator<?> i2 = c2.iterator();
        while (n2-- != 0) {
            if (this.contains(i2.next())) continue;
            return false;
        }
        return true;
    }

    @Override
    public boolean retainAll(Collection<?> c2) {
        boolean retVal = false;
        int n2 = this.size();
        CharIterator i2 = this.iterator();
        while (n2-- != 0) {
            if (c2.contains(i2.next())) continue;
            i2.remove();
            retVal = true;
        }
        return retVal;
    }

    @Override
    public boolean removeAll(Collection<?> c2) {
        boolean retVal = false;
        int n2 = c2.size();
        Iterator<?> i2 = c2.iterator();
        while (n2-- != 0) {
            if (!this.remove(i2.next())) continue;
            retVal = true;
        }
        return retVal;
    }

    @Override
    public boolean isEmpty() {
        return this.size() == 0;
    }

    @Override
    public String toString() {
        StringBuilder s2 = new StringBuilder();
        CharIterator i2 = this.iterator();
        int n2 = this.size();
        boolean first = true;
        s2.append("{");
        while (n2-- != 0) {
            if (first) {
                first = false;
            } else {
                s2.append(", ");
            }
            char k2 = i2.nextChar();
            s2.append(String.valueOf(k2));
        }
        s2.append("}");
        return s2.toString();
    }
}

