package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.bytes.ByteArrayList;
import it.unimi.dsi.fastutil.bytes.ByteArrays;
import it.unimi.dsi.fastutil.bytes.ByteBigArrays;
import it.unimi.dsi.fastutil.longs.LongArrays;
import it.unimi.dsi.fastutil.objects.AbstractObjectList;
import it.unimi.dsi.fastutil.objects.AbstractObjectListIterator;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.RandomAccess;

public class ByteArrayFrontCodedList
extends AbstractObjectList<byte[]>
implements Serializable,
Cloneable,
RandomAccess {
    private static final long serialVersionUID = 1L;
    protected final int n;
    protected final int ratio;
    protected final byte[][] array;
    protected transient long[] p;

    public ByteArrayFrontCodedList(Iterator<byte[]> arrays, int ratio) {
        if (ratio < 1) {
            throw new IllegalArgumentException("Illegal ratio (" + ratio + ")");
        }
        byte[][] array = ByteBigArrays.EMPTY_BIG_ARRAY;
        long[] p2 = LongArrays.EMPTY_ARRAY;
        byte[][] a2 = new byte[2][];
        long curSize = 0L;
        int n2 = 0;
        int b2 = 0;
        while (arrays.hasNext()) {
            a2[b2] = arrays.next();
            int length = a2[b2].length;
            if (n2 % ratio == 0) {
                p2 = LongArrays.grow(p2, n2 / ratio + 1);
                p2[n2 / ratio] = curSize;
                array = ByteBigArrays.grow(array, curSize + (long)ByteArrayFrontCodedList.count(length) + (long)length, curSize);
                curSize += (long)ByteArrayFrontCodedList.writeInt(array, length, curSize);
                ByteBigArrays.copyToBig(a2[b2], 0, array, curSize, length);
                curSize += (long)length;
            } else {
                int common;
                int minLength = a2[1 - b2].length;
                if (length < minLength) {
                    minLength = length;
                }
                for (common = 0; common < minLength && a2[0][common] == a2[1][common]; ++common) {
                }
                array = ByteBigArrays.grow(array, curSize + (long)ByteArrayFrontCodedList.count(length -= common) + (long)ByteArrayFrontCodedList.count(common) + (long)length, curSize);
                curSize += (long)ByteArrayFrontCodedList.writeInt(array, length, curSize);
                curSize += (long)ByteArrayFrontCodedList.writeInt(array, common, curSize);
                ByteBigArrays.copyToBig(a2[b2], common, array, curSize, length);
                curSize += (long)length;
            }
            b2 = 1 - b2;
            ++n2;
        }
        this.n = n2;
        this.ratio = ratio;
        this.array = ByteBigArrays.trim(array, curSize);
        this.p = LongArrays.trim(p2, (n2 + ratio - 1) / ratio);
    }

    public ByteArrayFrontCodedList(Collection<byte[]> c2, int ratio) {
        this(c2.iterator(), ratio);
    }

    private static int readInt(byte[][] a2, long pos) {
        byte b0 = ByteBigArrays.get(a2, pos);
        if (b0 >= 0) {
            return b0;
        }
        byte b1 = ByteBigArrays.get(a2, pos + 1L);
        if (b1 >= 0) {
            return -b0 - 1 << 7 | b1;
        }
        byte b2 = ByteBigArrays.get(a2, pos + 2L);
        if (b2 >= 0) {
            return -b0 - 1 << 14 | -b1 - 1 << 7 | b2;
        }
        byte b3 = ByteBigArrays.get(a2, pos + 3L);
        if (b3 >= 0) {
            return -b0 - 1 << 21 | -b1 - 1 << 14 | -b2 - 1 << 7 | b3;
        }
        return -b0 - 1 << 28 | -b1 - 1 << 21 | -b2 - 1 << 14 | -b3 - 1 << 7 | ByteBigArrays.get(a2, pos + 4L);
    }

    private static int count(int length) {
        if (length < 128) {
            return 1;
        }
        if (length < 16384) {
            return 2;
        }
        if (length < 0x200000) {
            return 3;
        }
        if (length < 0x10000000) {
            return 4;
        }
        return 5;
    }

    private static int writeInt(byte[][] a2, int length, long pos) {
        int count = ByteArrayFrontCodedList.count(length);
        ByteBigArrays.set(a2, pos + (long)count - 1L, (byte)(length & 0x7F));
        if (count != 1) {
            int i2 = count - 1;
            while (i2-- != 0) {
                ByteBigArrays.set(a2, pos + (long)i2, (byte)(-((length >>>= 7) & 0x7F) - 1));
            }
        }
        return count;
    }

    public int ratio() {
        return this.ratio;
    }

    private int length(int index) {
        byte[][] array = this.array;
        int delta = index % this.ratio;
        long pos = this.p[index / this.ratio];
        int length = ByteArrayFrontCodedList.readInt(array, pos);
        if (delta == 0) {
            return length;
        }
        pos += (long)(ByteArrayFrontCodedList.count(length) + length);
        length = ByteArrayFrontCodedList.readInt(array, pos);
        int common = ByteArrayFrontCodedList.readInt(array, pos + (long)ByteArrayFrontCodedList.count(length));
        for (int i2 = 0; i2 < delta - 1; ++i2) {
            length = ByteArrayFrontCodedList.readInt(array, pos += (long)(ByteArrayFrontCodedList.count(length) + ByteArrayFrontCodedList.count(common) + length));
            common = ByteArrayFrontCodedList.readInt(array, pos + (long)ByteArrayFrontCodedList.count(length));
        }
        return length + common;
    }

    public int arrayLength(int index) {
        this.ensureRestrictedIndex(index);
        return this.length(index);
    }

    private int extract(int index, byte[] a2, int offset, int length) {
        long startPos;
        int delta = index % this.ratio;
        long pos = startPos = this.p[index / this.ratio];
        int arrayLength = ByteArrayFrontCodedList.readInt(this.array, pos);
        int currLen = 0;
        if (delta == 0) {
            pos = this.p[index / this.ratio] + (long)ByteArrayFrontCodedList.count(arrayLength);
            ByteBigArrays.copyFromBig(this.array, pos, a2, offset, Math.min(length, arrayLength));
            return arrayLength;
        }
        int common = 0;
        for (int i2 = 0; i2 < delta; ++i2) {
            long prevArrayPos = pos + (long)ByteArrayFrontCodedList.count(arrayLength) + (long)(i2 != 0 ? ByteArrayFrontCodedList.count(common) : 0);
            common = ByteArrayFrontCodedList.readInt(this.array, (pos = prevArrayPos + (long)arrayLength) + (long)ByteArrayFrontCodedList.count(arrayLength = ByteArrayFrontCodedList.readInt(this.array, pos)));
            int actualCommon = Math.min(common, length);
            if (actualCommon <= currLen) {
                currLen = actualCommon;
                continue;
            }
            ByteBigArrays.copyFromBig(this.array, prevArrayPos, a2, currLen + offset, actualCommon - currLen);
            currLen = actualCommon;
        }
        if (currLen < length) {
            ByteBigArrays.copyFromBig(this.array, pos + (long)ByteArrayFrontCodedList.count(arrayLength) + (long)ByteArrayFrontCodedList.count(common), a2, currLen + offset, Math.min(arrayLength, length - currLen));
        }
        return arrayLength + common;
    }

    @Override
    public byte[] get(int index) {
        return this.getArray(index);
    }

    public byte[] getArray(int index) {
        this.ensureRestrictedIndex(index);
        int length = this.length(index);
        byte[] a2 = new byte[length];
        this.extract(index, a2, 0, length);
        return a2;
    }

    public int get(int index, byte[] a2, int offset, int length) {
        this.ensureRestrictedIndex(index);
        ByteArrays.ensureOffsetLength(a2, offset, length);
        int arrayLength = this.extract(index, a2, offset, length);
        if (length >= arrayLength) {
            return arrayLength;
        }
        return length - arrayLength;
    }

    public int get(int index, byte[] a2) {
        return this.get(index, a2, 0, a2.length);
    }

    @Override
    public int size() {
        return this.n;
    }

    @Override
    public ObjectListIterator<byte[]> listIterator(final int start) {
        this.ensureIndex(start);
        return new AbstractObjectListIterator<byte[]>(){
            byte[] s = ByteArrays.EMPTY_ARRAY;
            int i = 0;
            long pos = 0L;
            boolean inSync;
            {
                if (start != 0) {
                    if (start == ByteArrayFrontCodedList.this.n) {
                        this.i = start;
                    } else {
                        this.pos = ByteArrayFrontCodedList.this.p[start / ByteArrayFrontCodedList.this.ratio];
                        int j2 = start % ByteArrayFrontCodedList.this.ratio;
                        this.i = start - j2;
                        while (j2-- != 0) {
                            this.next();
                        }
                    }
                }
            }

            @Override
            public boolean hasNext() {
                return this.i < ByteArrayFrontCodedList.this.n;
            }

            @Override
            public boolean hasPrevious() {
                return this.i > 0;
            }

            @Override
            public int previousIndex() {
                return this.i - 1;
            }

            @Override
            public int nextIndex() {
                return this.i;
            }

            @Override
            public byte[] next() {
                int length;
                if (!this.hasNext()) {
                    throw new NoSuchElementException();
                }
                if (this.i % ByteArrayFrontCodedList.this.ratio == 0) {
                    this.pos = ByteArrayFrontCodedList.this.p[this.i / ByteArrayFrontCodedList.this.ratio];
                    length = ByteArrayFrontCodedList.readInt(ByteArrayFrontCodedList.this.array, this.pos);
                    this.s = ByteArrays.ensureCapacity(this.s, length, 0);
                    ByteBigArrays.copyFromBig(ByteArrayFrontCodedList.this.array, this.pos + (long)ByteArrayFrontCodedList.count(length), this.s, 0, length);
                    this.pos += (long)(length + ByteArrayFrontCodedList.count(length));
                    this.inSync = true;
                } else if (this.inSync) {
                    length = ByteArrayFrontCodedList.readInt(ByteArrayFrontCodedList.this.array, this.pos);
                    int common = ByteArrayFrontCodedList.readInt(ByteArrayFrontCodedList.this.array, this.pos + (long)ByteArrayFrontCodedList.count(length));
                    this.s = ByteArrays.ensureCapacity(this.s, length + common, common);
                    ByteBigArrays.copyFromBig(ByteArrayFrontCodedList.this.array, this.pos + (long)ByteArrayFrontCodedList.count(length) + (long)ByteArrayFrontCodedList.count(common), this.s, common, length);
                    this.pos += (long)(ByteArrayFrontCodedList.count(length) + ByteArrayFrontCodedList.count(common) + length);
                    length += common;
                } else {
                    length = ByteArrayFrontCodedList.this.length(this.i);
                    this.s = ByteArrays.ensureCapacity(this.s, length, 0);
                    ByteArrayFrontCodedList.this.extract(this.i, this.s, 0, length);
                }
                ++this.i;
                return ByteArrays.copy(this.s, 0, length);
            }

            @Override
            public byte[] previous() {
                if (!this.hasPrevious()) {
                    throw new NoSuchElementException();
                }
                this.inSync = false;
                return ByteArrayFrontCodedList.this.getArray(--this.i);
            }
        };
    }

    public ByteArrayFrontCodedList clone() {
        return this;
    }

    @Override
    public String toString() {
        StringBuffer s2 = new StringBuffer();
        s2.append("[ ");
        for (int i2 = 0; i2 < this.n; ++i2) {
            if (i2 != 0) {
                s2.append(", ");
            }
            s2.append(ByteArrayList.wrap(this.getArray(i2)).toString());
        }
        s2.append(" ]");
        return s2.toString();
    }

    protected long[] rebuildPointerArray() {
        long[] p2 = new long[(this.n + this.ratio - 1) / this.ratio];
        byte[][] a2 = this.array;
        long pos = 0L;
        int j2 = 0;
        int skip = this.ratio - 1;
        for (int i2 = 0; i2 < this.n; ++i2) {
            int length = ByteArrayFrontCodedList.readInt(a2, pos);
            int count = ByteArrayFrontCodedList.count(length);
            if (++skip == this.ratio) {
                skip = 0;
                p2[j2++] = pos;
                pos += (long)(count + length);
                continue;
            }
            pos += (long)(count + ByteArrayFrontCodedList.count(ByteArrayFrontCodedList.readInt(a2, pos + (long)count)) + length);
        }
        return p2;
    }

    private void readObject(ObjectInputStream s2) throws IOException, ClassNotFoundException {
        s2.defaultReadObject();
        this.p = this.rebuildPointerArray();
    }
}

