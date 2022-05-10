package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.bytes.ByteCollection;
import it.unimi.dsi.fastutil.bytes.ByteIterator;
import it.unimi.dsi.fastutil.bytes.ByteIterators;
import it.unimi.dsi.fastutil.objects.ObjectIterators;
import java.lang.reflect.Array;
import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Iterator;

public abstract class AbstractByteCollection
extends AbstractCollection<Byte>
implements ByteCollection {
    protected AbstractByteCollection() {
    }

    @Override
    public byte[] toArray(byte[] a2) {
        return this.toByteArray(a2);
    }

    @Override
    public byte[] toByteArray() {
        return this.toByteArray(null);
    }

    @Override
    public byte[] toByteArray(byte[] a2) {
        if (a2 == null || a2.length < this.size()) {
            a2 = new byte[this.size()];
        }
        ByteIterators.unwrap(this.iterator(), a2);
        return a2;
    }

    @Override
    public boolean addAll(ByteCollection c2) {
        boolean retVal = false;
        ByteIterator i2 = c2.iterator();
        int n2 = c2.size();
        while (n2-- != 0) {
            if (!this.add(i2.nextByte())) continue;
            retVal = true;
        }
        return retVal;
    }

    @Override
    public boolean containsAll(ByteCollection c2) {
        ByteIterator i2 = c2.iterator();
        int n2 = c2.size();
        while (n2-- != 0) {
            if (this.contains(i2.nextByte())) continue;
            return false;
        }
        return true;
    }

    @Override
    public boolean retainAll(ByteCollection c2) {
        boolean retVal = false;
        int n2 = this.size();
        ByteIterator i2 = this.iterator();
        while (n2-- != 0) {
            if (c2.contains(i2.nextByte())) continue;
            i2.remove();
            retVal = true;
        }
        return retVal;
    }

    @Override
    public boolean removeAll(ByteCollection c2) {
        boolean retVal = false;
        int n2 = c2.size();
        ByteIterator i2 = c2.iterator();
        while (n2-- != 0) {
            if (!this.rem(i2.nextByte())) continue;
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
    public boolean addAll(Collection<? extends Byte> c2) {
        boolean retVal = false;
        Iterator<? extends Byte> i2 = c2.iterator();
        int n2 = c2.size();
        while (n2-- != 0) {
            if (!this.add(i2.next())) continue;
            retVal = true;
        }
        return retVal;
    }

    @Override
    public boolean add(byte k2) {
        throw new UnsupportedOperationException();
    }

    @Override
    @Deprecated
    public ByteIterator byteIterator() {
        return this.iterator();
    }

    @Override
    public abstract ByteIterator iterator();

    @Override
    public boolean remove(Object ok2) {
        if (ok2 == null) {
            return false;
        }
        return this.rem((Byte)ok2);
    }

    @Override
    public boolean add(Byte o2) {
        return this.add((byte)o2);
    }

    public boolean rem(Object o2) {
        if (o2 == null) {
            return false;
        }
        return this.rem((Byte)o2);
    }

    @Override
    public boolean contains(Object o2) {
        if (o2 == null) {
            return false;
        }
        return this.contains((Byte)o2);
    }

    @Override
    public boolean contains(byte k2) {
        ByteIterator iterator = this.iterator();
        while (iterator.hasNext()) {
            if (k2 != iterator.nextByte()) continue;
            return true;
        }
        return false;
    }

    @Override
    public boolean rem(byte k2) {
        ByteIterator iterator = this.iterator();
        while (iterator.hasNext()) {
            if (k2 != iterator.nextByte()) continue;
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
        ByteIterator i2 = this.iterator();
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
        ByteIterator i2 = this.iterator();
        int n2 = this.size();
        boolean first = true;
        s2.append("{");
        while (n2-- != 0) {
            if (first) {
                first = false;
            } else {
                s2.append(", ");
            }
            byte k2 = i2.nextByte();
            s2.append(String.valueOf(k2));
        }
        s2.append("}");
        return s2.toString();
    }
}

