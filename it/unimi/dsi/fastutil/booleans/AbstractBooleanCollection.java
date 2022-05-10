package it.unimi.dsi.fastutil.booleans;

import it.unimi.dsi.fastutil.booleans.BooleanCollection;
import it.unimi.dsi.fastutil.booleans.BooleanIterator;
import it.unimi.dsi.fastutil.booleans.BooleanIterators;
import it.unimi.dsi.fastutil.objects.ObjectIterators;
import java.lang.reflect.Array;
import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Iterator;

public abstract class AbstractBooleanCollection
extends AbstractCollection<Boolean>
implements BooleanCollection {
    protected AbstractBooleanCollection() {
    }

    @Override
    public boolean[] toArray(boolean[] a2) {
        return this.toBooleanArray(a2);
    }

    @Override
    public boolean[] toBooleanArray() {
        return this.toBooleanArray(null);
    }

    @Override
    public boolean[] toBooleanArray(boolean[] a2) {
        if (a2 == null || a2.length < this.size()) {
            a2 = new boolean[this.size()];
        }
        BooleanIterators.unwrap(this.iterator(), a2);
        return a2;
    }

    @Override
    public boolean addAll(BooleanCollection c2) {
        boolean retVal = false;
        BooleanIterator i2 = c2.iterator();
        int n2 = c2.size();
        while (n2-- != 0) {
            if (!this.add(i2.nextBoolean())) continue;
            retVal = true;
        }
        return retVal;
    }

    @Override
    public boolean containsAll(BooleanCollection c2) {
        BooleanIterator i2 = c2.iterator();
        int n2 = c2.size();
        while (n2-- != 0) {
            if (this.contains(i2.nextBoolean())) continue;
            return false;
        }
        return true;
    }

    @Override
    public boolean retainAll(BooleanCollection c2) {
        boolean retVal = false;
        int n2 = this.size();
        BooleanIterator i2 = this.iterator();
        while (n2-- != 0) {
            if (c2.contains(i2.nextBoolean())) continue;
            i2.remove();
            retVal = true;
        }
        return retVal;
    }

    @Override
    public boolean removeAll(BooleanCollection c2) {
        boolean retVal = false;
        int n2 = c2.size();
        BooleanIterator i2 = c2.iterator();
        while (n2-- != 0) {
            if (!this.rem(i2.nextBoolean())) continue;
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
    public boolean addAll(Collection<? extends Boolean> c2) {
        boolean retVal = false;
        Iterator<? extends Boolean> i2 = c2.iterator();
        int n2 = c2.size();
        while (n2-- != 0) {
            if (!this.add(i2.next())) continue;
            retVal = true;
        }
        return retVal;
    }

    @Override
    public boolean add(boolean k2) {
        throw new UnsupportedOperationException();
    }

    @Override
    @Deprecated
    public BooleanIterator booleanIterator() {
        return this.iterator();
    }

    @Override
    public abstract BooleanIterator iterator();

    @Override
    public boolean remove(Object ok2) {
        if (ok2 == null) {
            return false;
        }
        return this.rem((Boolean)ok2);
    }

    @Override
    public boolean add(Boolean o2) {
        return this.add((boolean)o2);
    }

    public boolean rem(Object o2) {
        if (o2 == null) {
            return false;
        }
        return this.rem((Boolean)o2);
    }

    @Override
    public boolean contains(Object o2) {
        if (o2 == null) {
            return false;
        }
        return this.contains((Boolean)o2);
    }

    @Override
    public boolean contains(boolean k2) {
        BooleanIterator iterator = this.iterator();
        while (iterator.hasNext()) {
            if (k2 != iterator.nextBoolean()) continue;
            return true;
        }
        return false;
    }

    @Override
    public boolean rem(boolean k2) {
        BooleanIterator iterator = this.iterator();
        while (iterator.hasNext()) {
            if (k2 != iterator.nextBoolean()) continue;
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
        BooleanIterator i2 = this.iterator();
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
        BooleanIterator i2 = this.iterator();
        int n2 = this.size();
        boolean first = true;
        s2.append("{");
        while (n2-- != 0) {
            if (first) {
                first = false;
            } else {
                s2.append(", ");
            }
            boolean k2 = i2.nextBoolean();
            s2.append(String.valueOf(k2));
        }
        s2.append("}");
        return s2.toString();
    }
}

