package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.BigArrays;
import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.bytes.ByteArrays;
import it.unimi.dsi.fastutil.bytes.ByteComparator;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Random;

public class ByteBigArrays {
    public static final byte[][] EMPTY_BIG_ARRAY = new byte[0][];
    public static final Hash.Strategy HASH_STRATEGY = new BigArrayHashStrategy();
    private static final int SMALL = 7;
    private static final int MEDIUM = 40;
    private static final int DIGIT_BITS = 8;
    private static final int DIGIT_MASK = 255;
    private static final int DIGITS_PER_ELEMENT = 1;

    private ByteBigArrays() {
    }

    public static byte get(byte[][] array, long index) {
        return array[BigArrays.segment(index)][BigArrays.displacement(index)];
    }

    public static void set(byte[][] array, long index, byte value) {
        array[BigArrays.segment((long)index)][BigArrays.displacement((long)index)] = value;
    }

    public static void swap(byte[][] array, long first, long second) {
        byte t2 = array[BigArrays.segment(first)][BigArrays.displacement(first)];
        array[BigArrays.segment((long)first)][BigArrays.displacement((long)first)] = array[BigArrays.segment(second)][BigArrays.displacement(second)];
        array[BigArrays.segment((long)second)][BigArrays.displacement((long)second)] = t2;
    }

    public static void add(byte[][] array, long index, byte incr) {
        byte[] arrby = array[BigArrays.segment(index)];
        int n2 = BigArrays.displacement(index);
        arrby[n2] = (byte)(arrby[n2] + incr);
    }

    public static void mul(byte[][] array, long index, byte factor) {
        byte[] arrby = array[BigArrays.segment(index)];
        int n2 = BigArrays.displacement(index);
        arrby[n2] = (byte)(arrby[n2] * factor);
    }

    public static void incr(byte[][] array, long index) {
        byte[] arrby = array[BigArrays.segment(index)];
        int n2 = BigArrays.displacement(index);
        arrby[n2] = (byte)(arrby[n2] + 1);
    }

    public static void decr(byte[][] array, long index) {
        byte[] arrby = array[BigArrays.segment(index)];
        int n2 = BigArrays.displacement(index);
        arrby[n2] = (byte)(arrby[n2] - 1);
    }

    public static long length(byte[][] array) {
        int length = array.length;
        return length == 0 ? 0L : BigArrays.start(length - 1) + (long)array[length - 1].length;
    }

    public static void copy(byte[][] srcArray, long srcPos, byte[][] destArray, long destPos, long length) {
        if (destPos <= srcPos) {
            int srcSegment = BigArrays.segment(srcPos);
            int destSegment = BigArrays.segment(destPos);
            int srcDispl = BigArrays.displacement(srcPos);
            int destDispl = BigArrays.displacement(destPos);
            while (length > 0L) {
                int l2 = (int)Math.min(length, (long)Math.min(srcArray[srcSegment].length - srcDispl, destArray[destSegment].length - destDispl));
                System.arraycopy(srcArray[srcSegment], srcDispl, destArray[destSegment], destDispl, l2);
                if ((srcDispl += l2) == 0x8000000) {
                    srcDispl = 0;
                    ++srcSegment;
                }
                if ((destDispl += l2) == 0x8000000) {
                    destDispl = 0;
                    ++destSegment;
                }
                length -= (long)l2;
            }
        } else {
            int srcSegment = BigArrays.segment(srcPos + length);
            int destSegment = BigArrays.segment(destPos + length);
            int srcDispl = BigArrays.displacement(srcPos + length);
            int destDispl = BigArrays.displacement(destPos + length);
            while (length > 0L) {
                if (srcDispl == 0) {
                    srcDispl = 0x8000000;
                    --srcSegment;
                }
                if (destDispl == 0) {
                    destDispl = 0x8000000;
                    --destSegment;
                }
                int l3 = (int)Math.min(length, (long)Math.min(srcDispl, destDispl));
                System.arraycopy(srcArray[srcSegment], srcDispl - l3, destArray[destSegment], destDispl - l3, l3);
                srcDispl -= l3;
                destDispl -= l3;
                length -= (long)l3;
            }
        }
    }

    public static void copyFromBig(byte[][] srcArray, long srcPos, byte[] destArray, int destPos, int length) {
        int srcSegment = BigArrays.segment(srcPos);
        int srcDispl = BigArrays.displacement(srcPos);
        while (length > 0) {
            int l2 = Math.min(srcArray[srcSegment].length - srcDispl, length);
            System.arraycopy(srcArray[srcSegment], srcDispl, destArray, destPos, l2);
            if ((srcDispl += l2) == 0x8000000) {
                srcDispl = 0;
                ++srcSegment;
            }
            destPos += l2;
            length -= l2;
        }
    }

    public static void copyToBig(byte[] srcArray, int srcPos, byte[][] destArray, long destPos, long length) {
        int destSegment = BigArrays.segment(destPos);
        int destDispl = BigArrays.displacement(destPos);
        while (length > 0L) {
            int l2 = (int)Math.min((long)(destArray[destSegment].length - destDispl), length);
            System.arraycopy(srcArray, srcPos, destArray[destSegment], destDispl, l2);
            if ((destDispl += l2) == 0x8000000) {
                destDispl = 0;
                ++destSegment;
            }
            srcPos += l2;
            length -= (long)l2;
        }
    }

    public static byte[][] newBigArray(long length) {
        if (length == 0L) {
            return EMPTY_BIG_ARRAY;
        }
        BigArrays.ensureLength(length);
        int baseLength = (int)(length + 0x7FFFFFFL >>> 27);
        byte[][] base = new byte[baseLength][];
        int residual = (int)(length & 0x7FFFFFFL);
        if (residual != 0) {
            for (int i2 = 0; i2 < baseLength - 1; ++i2) {
                base[i2] = new byte[0x8000000];
            }
            base[baseLength - 1] = new byte[residual];
        } else {
            for (int i3 = 0; i3 < baseLength; ++i3) {
                base[i3] = new byte[0x8000000];
            }
        }
        return base;
    }

    public static byte[][] wrap(byte[] array) {
        if (array.length == 0) {
            return EMPTY_BIG_ARRAY;
        }
        if (array.length <= 0x8000000) {
            return new byte[][]{array};
        }
        byte[][] bigArray = ByteBigArrays.newBigArray(array.length);
        for (int i2 = 0; i2 < bigArray.length; ++i2) {
            System.arraycopy(array, (int)BigArrays.start(i2), bigArray[i2], 0, bigArray[i2].length);
        }
        return bigArray;
    }

    public static byte[][] ensureCapacity(byte[][] array, long length) {
        return ByteBigArrays.ensureCapacity(array, length, ByteBigArrays.length(array));
    }

    public static byte[][] ensureCapacity(byte[][] array, long length, long preserve) {
        long oldLength = ByteBigArrays.length(array);
        if (length > oldLength) {
            BigArrays.ensureLength(length);
            int valid = array.length - (array.length == 0 || array.length > 0 && array[array.length - 1].length == 0x8000000 ? 0 : 1);
            int baseLength = (int)(length + 0x7FFFFFFL >>> 27);
            byte[][] base = (byte[][])Arrays.copyOf(array, baseLength);
            int residual = (int)(length & 0x7FFFFFFL);
            if (residual != 0) {
                for (int i2 = valid; i2 < baseLength - 1; ++i2) {
                    base[i2] = new byte[0x8000000];
                }
                base[baseLength - 1] = new byte[residual];
            } else {
                for (int i3 = valid; i3 < baseLength; ++i3) {
                    base[i3] = new byte[0x8000000];
                }
            }
            if (preserve - (long)valid * 0x8000000L > 0L) {
                ByteBigArrays.copy(array, (long)valid * 0x8000000L, base, (long)valid * 0x8000000L, preserve - (long)valid * 0x8000000L);
            }
            return base;
        }
        return array;
    }

    public static byte[][] grow(byte[][] array, long length) {
        long oldLength = ByteBigArrays.length(array);
        return length > oldLength ? ByteBigArrays.grow(array, length, oldLength) : array;
    }

    public static byte[][] grow(byte[][] array, long length, long preserve) {
        long oldLength = ByteBigArrays.length(array);
        return length > oldLength ? ByteBigArrays.ensureCapacity(array, Math.max(2L * oldLength, length), preserve) : array;
    }

    public static byte[][] trim(byte[][] array, long length) {
        BigArrays.ensureLength(length);
        long oldLength = ByteBigArrays.length(array);
        if (length >= oldLength) {
            return array;
        }
        int baseLength = (int)(length + 0x7FFFFFFL >>> 27);
        byte[][] base = (byte[][])Arrays.copyOf(array, baseLength);
        int residual = (int)(length & 0x7FFFFFFL);
        if (residual != 0) {
            base[baseLength - 1] = ByteArrays.trim(base[baseLength - 1], residual);
        }
        return base;
    }

    public static byte[][] setLength(byte[][] array, long length) {
        long oldLength = ByteBigArrays.length(array);
        if (length == oldLength) {
            return array;
        }
        if (length < oldLength) {
            return ByteBigArrays.trim(array, length);
        }
        return ByteBigArrays.ensureCapacity(array, length);
    }

    public static byte[][] copy(byte[][] array, long offset, long length) {
        ByteBigArrays.ensureOffsetLength(array, offset, length);
        byte[][] a2 = ByteBigArrays.newBigArray(length);
        ByteBigArrays.copy(array, offset, a2, 0L, length);
        return a2;
    }

    public static byte[][] copy(byte[][] array) {
        byte[][] base = (byte[][])array.clone();
        int i2 = base.length;
        while (i2-- != 0) {
            base[i2] = (byte[])array[i2].clone();
        }
        return base;
    }

    public static void fill(byte[][] array, byte value) {
        int i2 = array.length;
        while (i2-- != 0) {
            Arrays.fill(array[i2], value);
        }
    }

    public static void fill(byte[][] array, long from, long to2, byte value) {
        long length = ByteBigArrays.length(array);
        BigArrays.ensureFromTo(length, from, to2);
        int fromSegment = BigArrays.segment(from);
        int toSegment = BigArrays.segment(to2);
        int fromDispl = BigArrays.displacement(from);
        int toDispl = BigArrays.displacement(to2);
        if (fromSegment == toSegment) {
            Arrays.fill(array[fromSegment], fromDispl, toDispl, value);
            return;
        }
        if (toDispl != 0) {
            Arrays.fill(array[toSegment], 0, toDispl, value);
        }
        while (--toSegment > fromSegment) {
            Arrays.fill(array[toSegment], value);
        }
        Arrays.fill(array[fromSegment], fromDispl, 0x8000000, value);
    }

    public static boolean equals(byte[][] a1, byte[][] a2) {
        if (ByteBigArrays.length(a1) != ByteBigArrays.length(a2)) {
            return false;
        }
        int i2 = a1.length;
        while (i2-- != 0) {
            byte[] t2 = a1[i2];
            byte[] u2 = a2[i2];
            int j2 = t2.length;
            while (j2-- != 0) {
                if (t2[j2] == u2[j2]) continue;
                return false;
            }
        }
        return true;
    }

    public static String toString(byte[][] a2) {
        if (a2 == null) {
            return "null";
        }
        long last = ByteBigArrays.length(a2) - 1L;
        if (last == -1L) {
            return "[]";
        }
        StringBuilder b2 = new StringBuilder();
        b2.append('[');
        long i2 = 0L;
        while (true) {
            b2.append(String.valueOf(ByteBigArrays.get(a2, i2)));
            if (i2 == last) {
                return b2.append(']').toString();
            }
            b2.append(", ");
            ++i2;
        }
    }

    public static void ensureFromTo(byte[][] a2, long from, long to2) {
        BigArrays.ensureFromTo(ByteBigArrays.length(a2), from, to2);
    }

    public static void ensureOffsetLength(byte[][] a2, long offset, long length) {
        BigArrays.ensureOffsetLength(ByteBigArrays.length(a2), offset, length);
    }

    private static void vecSwap(byte[][] x2, long a2, long b2, long n2) {
        int i2 = 0;
        while ((long)i2 < n2) {
            ByteBigArrays.swap(x2, a2, b2);
            ++i2;
            ++a2;
            ++b2;
        }
    }

    private static long med3(byte[][] x2, long a2, long b2, long c2, ByteComparator comp) {
        int ab2 = comp.compare(ByteBigArrays.get(x2, a2), ByteBigArrays.get(x2, b2));
        int ac2 = comp.compare(ByteBigArrays.get(x2, a2), ByteBigArrays.get(x2, c2));
        int bc2 = comp.compare(ByteBigArrays.get(x2, b2), ByteBigArrays.get(x2, c2));
        return ab2 < 0 ? (bc2 < 0 ? b2 : (ac2 < 0 ? c2 : a2)) : (bc2 > 0 ? b2 : (ac2 > 0 ? c2 : a2));
    }

    private static void selectionSort(byte[][] a2, long from, long to2, ByteComparator comp) {
        for (long i2 = from; i2 < to2 - 1L; ++i2) {
            long m2 = i2;
            for (long j2 = i2 + 1L; j2 < to2; ++j2) {
                if (comp.compare(ByteBigArrays.get(a2, j2), ByteBigArrays.get(a2, m2)) >= 0) continue;
                m2 = j2;
            }
            if (m2 == i2) continue;
            ByteBigArrays.swap(a2, i2, m2);
        }
    }

    public static void quickSort(byte[][] x2, long from, long to2, ByteComparator comp) {
        long c2;
        long a2;
        long len = to2 - from;
        if (len < 7L) {
            ByteBigArrays.selectionSort(x2, from, to2, comp);
            return;
        }
        long m2 = from + len / 2L;
        if (len > 7L) {
            long l2 = from;
            long n2 = to2 - 1L;
            if (len > 40L) {
                long s2 = len / 8L;
                l2 = ByteBigArrays.med3(x2, l2, l2 + s2, l2 + 2L * s2, comp);
                m2 = ByteBigArrays.med3(x2, m2 - s2, m2, m2 + s2, comp);
                n2 = ByteBigArrays.med3(x2, n2 - 2L * s2, n2 - s2, n2, comp);
            }
            m2 = ByteBigArrays.med3(x2, l2, m2, n2, comp);
        }
        byte v2 = ByteBigArrays.get(x2, m2);
        long b2 = a2 = from;
        long d2 = c2 = to2 - 1L;
        while (true) {
            int comparison;
            if (b2 <= c2 && (comparison = comp.compare(ByteBigArrays.get(x2, b2), v2)) <= 0) {
                if (comparison == 0) {
                    ByteBigArrays.swap(x2, a2++, b2);
                }
                ++b2;
                continue;
            }
            while (c2 >= b2 && (comparison = comp.compare(ByteBigArrays.get(x2, c2), v2)) >= 0) {
                if (comparison == 0) {
                    ByteBigArrays.swap(x2, c2, d2--);
                }
                --c2;
            }
            if (b2 > c2) break;
            ByteBigArrays.swap(x2, b2++, c2--);
        }
        long n3 = to2;
        long s3 = Math.min(a2 - from, b2 - a2);
        ByteBigArrays.vecSwap(x2, from, b2 - s3, s3);
        s3 = Math.min(d2 - c2, n3 - d2 - 1L);
        ByteBigArrays.vecSwap(x2, b2, n3 - s3, s3);
        s3 = b2 - a2;
        if (s3 > 1L) {
            ByteBigArrays.quickSort(x2, from, from + s3, comp);
        }
        if ((s3 = d2 - c2) > 1L) {
            ByteBigArrays.quickSort(x2, n3 - s3, n3, comp);
        }
    }

    private static long med3(byte[][] x2, long a2, long b2, long c2) {
        int ab2 = Byte.compare(ByteBigArrays.get(x2, a2), ByteBigArrays.get(x2, b2));
        int ac2 = Byte.compare(ByteBigArrays.get(x2, a2), ByteBigArrays.get(x2, c2));
        int bc2 = Byte.compare(ByteBigArrays.get(x2, b2), ByteBigArrays.get(x2, c2));
        return ab2 < 0 ? (bc2 < 0 ? b2 : (ac2 < 0 ? c2 : a2)) : (bc2 > 0 ? b2 : (ac2 > 0 ? c2 : a2));
    }

    private static void selectionSort(byte[][] a2, long from, long to2) {
        for (long i2 = from; i2 < to2 - 1L; ++i2) {
            long m2 = i2;
            for (long j2 = i2 + 1L; j2 < to2; ++j2) {
                if (ByteBigArrays.get(a2, j2) >= ByteBigArrays.get(a2, m2)) continue;
                m2 = j2;
            }
            if (m2 == i2) continue;
            ByteBigArrays.swap(a2, i2, m2);
        }
    }

    public static void quickSort(byte[][] x2, ByteComparator comp) {
        ByteBigArrays.quickSort(x2, 0L, ByteBigArrays.length(x2), comp);
    }

    public static void quickSort(byte[][] x2, long from, long to2) {
        long c2;
        long a2;
        long len = to2 - from;
        if (len < 7L) {
            ByteBigArrays.selectionSort(x2, from, to2);
            return;
        }
        long m2 = from + len / 2L;
        if (len > 7L) {
            long l2 = from;
            long n2 = to2 - 1L;
            if (len > 40L) {
                long s2 = len / 8L;
                l2 = ByteBigArrays.med3(x2, l2, l2 + s2, l2 + 2L * s2);
                m2 = ByteBigArrays.med3(x2, m2 - s2, m2, m2 + s2);
                n2 = ByteBigArrays.med3(x2, n2 - 2L * s2, n2 - s2, n2);
            }
            m2 = ByteBigArrays.med3(x2, l2, m2, n2);
        }
        byte v2 = ByteBigArrays.get(x2, m2);
        long b2 = a2 = from;
        long d2 = c2 = to2 - 1L;
        while (true) {
            int comparison;
            if (b2 <= c2 && (comparison = Byte.compare(ByteBigArrays.get(x2, b2), v2)) <= 0) {
                if (comparison == 0) {
                    ByteBigArrays.swap(x2, a2++, b2);
                }
                ++b2;
                continue;
            }
            while (c2 >= b2 && (comparison = Byte.compare(ByteBigArrays.get(x2, c2), v2)) >= 0) {
                if (comparison == 0) {
                    ByteBigArrays.swap(x2, c2, d2--);
                }
                --c2;
            }
            if (b2 > c2) break;
            ByteBigArrays.swap(x2, b2++, c2--);
        }
        long n3 = to2;
        long s3 = Math.min(a2 - from, b2 - a2);
        ByteBigArrays.vecSwap(x2, from, b2 - s3, s3);
        s3 = Math.min(d2 - c2, n3 - d2 - 1L);
        ByteBigArrays.vecSwap(x2, b2, n3 - s3, s3);
        s3 = b2 - a2;
        if (s3 > 1L) {
            ByteBigArrays.quickSort(x2, from, from + s3);
        }
        if ((s3 = d2 - c2) > 1L) {
            ByteBigArrays.quickSort(x2, n3 - s3, n3);
        }
    }

    public static void quickSort(byte[][] x2) {
        ByteBigArrays.quickSort(x2, 0L, ByteBigArrays.length(x2));
    }

    public static long binarySearch(byte[][] a2, long from, long to2, byte key) {
        --to2;
        while (from <= to2) {
            long mid = from + to2 >>> 1;
            byte midVal = ByteBigArrays.get(a2, mid);
            if (midVal < key) {
                from = mid + 1L;
                continue;
            }
            if (midVal > key) {
                to2 = mid - 1L;
                continue;
            }
            return mid;
        }
        return -(from + 1L);
    }

    public static long binarySearch(byte[][] a2, byte key) {
        return ByteBigArrays.binarySearch(a2, 0L, ByteBigArrays.length(a2), key);
    }

    public static long binarySearch(byte[][] a2, long from, long to2, byte key, ByteComparator c2) {
        --to2;
        while (from <= to2) {
            long mid = from + to2 >>> 1;
            byte midVal = ByteBigArrays.get(a2, mid);
            int cmp = c2.compare(midVal, key);
            if (cmp < 0) {
                from = mid + 1L;
                continue;
            }
            if (cmp > 0) {
                to2 = mid - 1L;
                continue;
            }
            return mid;
        }
        return -(from + 1L);
    }

    public static long binarySearch(byte[][] a2, byte key, ByteComparator c2) {
        return ByteBigArrays.binarySearch(a2, 0L, ByteBigArrays.length(a2), key, c2);
    }

    public static void radixSort(byte[][] a2) {
        ByteBigArrays.radixSort(a2, 0L, ByteBigArrays.length(a2));
    }

    public static void radixSort(byte[][] a2, long from, long to2) {
        boolean maxLevel = false;
        boolean stackSize = true;
        long[] offsetStack = new long[1];
        int offsetPos = 0;
        long[] lengthStack = new long[1];
        int lengthPos = 0;
        int[] levelStack = new int[1];
        int levelPos = 0;
        offsetStack[offsetPos++] = from;
        lengthStack[lengthPos++] = to2 - from;
        levelStack[levelPos++] = 0;
        long[] count = new long[256];
        long[] pos = new long[256];
        byte[][] digit = ByteBigArrays.newBigArray(to2 - from);
        while (offsetPos > 0) {
            int level;
            int signMask;
            long first = offsetStack[--offsetPos];
            long length = lengthStack[--lengthPos];
            int n2 = signMask = (level = levelStack[--levelPos]) % 1 == 0 ? 128 : 0;
            if (length < 40L) {
                ByteBigArrays.selectionSort(a2, first, first + length);
                continue;
            }
            int shift = (0 - level % 1) * 8;
            long i2 = length;
            while (i2-- != 0L) {
                ByteBigArrays.set(digit, i2, (byte)(ByteBigArrays.get(a2, first + i2) >>> shift & 0xFF ^ signMask));
            }
            i2 = length;
            while (i2-- != 0L) {
                int n3 = ByteBigArrays.get(digit, i2) & 0xFF;
                count[n3] = count[n3] + 1L;
            }
            int lastUsed = -1;
            long p2 = 0L;
            for (int i3 = 0; i3 < 256; ++i3) {
                if (count[i3] != 0L) {
                    lastUsed = i3;
                    if (level < 0 && count[i3] > 1L) {
                        offsetStack[offsetPos++] = p2 + first;
                        lengthStack[lengthPos++] = count[i3];
                        levelStack[levelPos++] = level + 1;
                    }
                }
                pos[i3] = p2 += count[i3];
            }
            long end = length - count[lastUsed];
            count[lastUsed] = 0L;
            int c2 = -1;
            for (long i4 = 0L; i4 < end; i4 += count[c2]) {
                byte t2 = ByteBigArrays.get(a2, i4 + first);
                c2 = ByteBigArrays.get(digit, i4) & 0xFF;
                while (true) {
                    int n4 = c2;
                    long l2 = pos[n4] - 1L;
                    pos[n4] = l2;
                    long d2 = l2;
                    if (l2 <= i4) break;
                    byte z2 = t2;
                    int zz2 = c2;
                    t2 = ByteBigArrays.get(a2, d2 + first);
                    c2 = ByteBigArrays.get(digit, d2) & 0xFF;
                    ByteBigArrays.set(a2, d2 + first, z2);
                    ByteBigArrays.set(digit, d2, (byte)zz2);
                }
                ByteBigArrays.set(a2, i4 + first, t2);
                count[c2] = 0L;
            }
        }
    }

    private static void selectionSort(byte[][] a2, byte[][] b2, long from, long to2) {
        for (long i2 = from; i2 < to2 - 1L; ++i2) {
            long m2 = i2;
            for (long j2 = i2 + 1L; j2 < to2; ++j2) {
                if (ByteBigArrays.get(a2, j2) >= ByteBigArrays.get(a2, m2) && (ByteBigArrays.get(a2, j2) != ByteBigArrays.get(a2, m2) || ByteBigArrays.get(b2, j2) >= ByteBigArrays.get(b2, m2))) continue;
                m2 = j2;
            }
            if (m2 == i2) continue;
            byte t2 = ByteBigArrays.get(a2, i2);
            ByteBigArrays.set(a2, i2, ByteBigArrays.get(a2, m2));
            ByteBigArrays.set(a2, m2, t2);
            t2 = ByteBigArrays.get(b2, i2);
            ByteBigArrays.set(b2, i2, ByteBigArrays.get(b2, m2));
            ByteBigArrays.set(b2, m2, t2);
        }
    }

    public static void radixSort(byte[][] a2, byte[][] b2) {
        ByteBigArrays.radixSort(a2, b2, 0L, ByteBigArrays.length(a2));
    }

    public static void radixSort(byte[][] a2, byte[][] b2, long from, long to2) {
        int layers = 2;
        if (ByteBigArrays.length(a2) != ByteBigArrays.length(b2)) {
            throw new IllegalArgumentException("Array size mismatch.");
        }
        boolean maxLevel = true;
        int stackSize = 256;
        long[] offsetStack = new long[256];
        int offsetPos = 0;
        long[] lengthStack = new long[256];
        int lengthPos = 0;
        int[] levelStack = new int[256];
        int levelPos = 0;
        offsetStack[offsetPos++] = from;
        lengthStack[lengthPos++] = to2 - from;
        levelStack[levelPos++] = 0;
        long[] count = new long[256];
        long[] pos = new long[256];
        byte[][] digit = ByteBigArrays.newBigArray(to2 - from);
        while (offsetPos > 0) {
            int level;
            int signMask;
            long first = offsetStack[--offsetPos];
            long length = lengthStack[--lengthPos];
            int n2 = signMask = (level = levelStack[--levelPos]) % 1 == 0 ? 128 : 0;
            if (length < 40L) {
                ByteBigArrays.selectionSort(a2, b2, first, first + length);
                continue;
            }
            byte[][] k2 = level < 1 ? a2 : b2;
            int shift = (0 - level % 1) * 8;
            long i2 = length;
            while (i2-- != 0L) {
                ByteBigArrays.set(digit, i2, (byte)(ByteBigArrays.get(k2, first + i2) >>> shift & 0xFF ^ signMask));
            }
            i2 = length;
            while (i2-- != 0L) {
                int n3 = ByteBigArrays.get(digit, i2) & 0xFF;
                count[n3] = count[n3] + 1L;
            }
            int lastUsed = -1;
            long p2 = 0L;
            for (int i3 = 0; i3 < 256; ++i3) {
                if (count[i3] != 0L) {
                    lastUsed = i3;
                    if (level < 1 && count[i3] > 1L) {
                        offsetStack[offsetPos++] = p2 + first;
                        lengthStack[lengthPos++] = count[i3];
                        levelStack[levelPos++] = level + 1;
                    }
                }
                pos[i3] = p2 += count[i3];
            }
            long end = length - count[lastUsed];
            count[lastUsed] = 0L;
            int c2 = -1;
            for (long i4 = 0L; i4 < end; i4 += count[c2]) {
                byte t2 = ByteBigArrays.get(a2, i4 + first);
                byte u2 = ByteBigArrays.get(b2, i4 + first);
                c2 = ByteBigArrays.get(digit, i4) & 0xFF;
                while (true) {
                    int n4 = c2;
                    long l2 = pos[n4] - 1L;
                    pos[n4] = l2;
                    long d2 = l2;
                    if (l2 <= i4) break;
                    byte z2 = t2;
                    int zz2 = c2;
                    t2 = ByteBigArrays.get(a2, d2 + first);
                    ByteBigArrays.set(a2, d2 + first, z2);
                    z2 = u2;
                    u2 = ByteBigArrays.get(b2, d2 + first);
                    ByteBigArrays.set(b2, d2 + first, z2);
                    c2 = ByteBigArrays.get(digit, d2) & 0xFF;
                    ByteBigArrays.set(digit, d2, (byte)zz2);
                }
                ByteBigArrays.set(a2, i4 + first, t2);
                ByteBigArrays.set(b2, i4 + first, u2);
                count[c2] = 0L;
            }
        }
    }

    public static byte[][] shuffle(byte[][] a2, long from, long to2, Random random) {
        long i2 = to2 - from;
        while (i2-- != 0L) {
            long p2 = (random.nextLong() & Long.MAX_VALUE) % (i2 + 1L);
            byte t2 = ByteBigArrays.get(a2, from + i2);
            ByteBigArrays.set(a2, from + i2, ByteBigArrays.get(a2, from + p2));
            ByteBigArrays.set(a2, from + p2, t2);
        }
        return a2;
    }

    public static byte[][] shuffle(byte[][] a2, Random random) {
        long i2 = ByteBigArrays.length(a2);
        while (i2-- != 0L) {
            long p2 = (random.nextLong() & Long.MAX_VALUE) % (i2 + 1L);
            byte t2 = ByteBigArrays.get(a2, i2);
            ByteBigArrays.set(a2, i2, ByteBigArrays.get(a2, p2));
            ByteBigArrays.set(a2, p2, t2);
        }
        return a2;
    }

    private static final class BigArrayHashStrategy
    implements Hash.Strategy<byte[][]>,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;

        private BigArrayHashStrategy() {
        }

        @Override
        public int hashCode(byte[][] o2) {
            return Arrays.deepHashCode((Object[])o2);
        }

        @Override
        public boolean equals(byte[][] a2, byte[][] b2) {
            return ByteBigArrays.equals(a2, b2);
        }
    }
}

