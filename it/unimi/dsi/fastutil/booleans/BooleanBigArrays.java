package it.unimi.dsi.fastutil.booleans;

import it.unimi.dsi.fastutil.BigArrays;
import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.booleans.BooleanArrays;
import it.unimi.dsi.fastutil.booleans.BooleanComparator;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Random;

public class BooleanBigArrays {
    public static final boolean[][] EMPTY_BIG_ARRAY = new boolean[0][];
    public static final Hash.Strategy HASH_STRATEGY = new BigArrayHashStrategy();
    private static final int SMALL = 7;
    private static final int MEDIUM = 40;

    private BooleanBigArrays() {
    }

    public static boolean get(boolean[][] array, long index) {
        return array[BigArrays.segment(index)][BigArrays.displacement(index)];
    }

    public static void set(boolean[][] array, long index, boolean value) {
        array[BigArrays.segment((long)index)][BigArrays.displacement((long)index)] = value;
    }

    public static void swap(boolean[][] array, long first, long second) {
        boolean t2 = array[BigArrays.segment(first)][BigArrays.displacement(first)];
        array[BigArrays.segment((long)first)][BigArrays.displacement((long)first)] = array[BigArrays.segment(second)][BigArrays.displacement(second)];
        array[BigArrays.segment((long)second)][BigArrays.displacement((long)second)] = t2;
    }

    public static long length(boolean[][] array) {
        int length = array.length;
        return length == 0 ? 0L : BigArrays.start(length - 1) + (long)array[length - 1].length;
    }

    public static void copy(boolean[][] srcArray, long srcPos, boolean[][] destArray, long destPos, long length) {
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

    public static void copyFromBig(boolean[][] srcArray, long srcPos, boolean[] destArray, int destPos, int length) {
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

    public static void copyToBig(boolean[] srcArray, int srcPos, boolean[][] destArray, long destPos, long length) {
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

    public static boolean[][] newBigArray(long length) {
        if (length == 0L) {
            return EMPTY_BIG_ARRAY;
        }
        BigArrays.ensureLength(length);
        int baseLength = (int)(length + 0x7FFFFFFL >>> 27);
        boolean[][] base = new boolean[baseLength][];
        int residual = (int)(length & 0x7FFFFFFL);
        if (residual != 0) {
            for (int i2 = 0; i2 < baseLength - 1; ++i2) {
                base[i2] = new boolean[0x8000000];
            }
            base[baseLength - 1] = new boolean[residual];
        } else {
            for (int i3 = 0; i3 < baseLength; ++i3) {
                base[i3] = new boolean[0x8000000];
            }
        }
        return base;
    }

    public static boolean[][] wrap(boolean[] array) {
        if (array.length == 0) {
            return EMPTY_BIG_ARRAY;
        }
        if (array.length <= 0x8000000) {
            return new boolean[][]{array};
        }
        boolean[][] bigArray = BooleanBigArrays.newBigArray(array.length);
        for (int i2 = 0; i2 < bigArray.length; ++i2) {
            System.arraycopy(array, (int)BigArrays.start(i2), bigArray[i2], 0, bigArray[i2].length);
        }
        return bigArray;
    }

    public static boolean[][] ensureCapacity(boolean[][] array, long length) {
        return BooleanBigArrays.ensureCapacity(array, length, BooleanBigArrays.length(array));
    }

    public static boolean[][] ensureCapacity(boolean[][] array, long length, long preserve) {
        long oldLength = BooleanBigArrays.length(array);
        if (length > oldLength) {
            BigArrays.ensureLength(length);
            int valid = array.length - (array.length == 0 || array.length > 0 && array[array.length - 1].length == 0x8000000 ? 0 : 1);
            int baseLength = (int)(length + 0x7FFFFFFL >>> 27);
            boolean[][] base = (boolean[][])Arrays.copyOf(array, baseLength);
            int residual = (int)(length & 0x7FFFFFFL);
            if (residual != 0) {
                for (int i2 = valid; i2 < baseLength - 1; ++i2) {
                    base[i2] = new boolean[0x8000000];
                }
                base[baseLength - 1] = new boolean[residual];
            } else {
                for (int i3 = valid; i3 < baseLength; ++i3) {
                    base[i3] = new boolean[0x8000000];
                }
            }
            if (preserve - (long)valid * 0x8000000L > 0L) {
                BooleanBigArrays.copy(array, (long)valid * 0x8000000L, base, (long)valid * 0x8000000L, preserve - (long)valid * 0x8000000L);
            }
            return base;
        }
        return array;
    }

    public static boolean[][] grow(boolean[][] array, long length) {
        long oldLength = BooleanBigArrays.length(array);
        return length > oldLength ? BooleanBigArrays.grow(array, length, oldLength) : array;
    }

    public static boolean[][] grow(boolean[][] array, long length, long preserve) {
        long oldLength = BooleanBigArrays.length(array);
        return length > oldLength ? BooleanBigArrays.ensureCapacity(array, Math.max(2L * oldLength, length), preserve) : array;
    }

    public static boolean[][] trim(boolean[][] array, long length) {
        BigArrays.ensureLength(length);
        long oldLength = BooleanBigArrays.length(array);
        if (length >= oldLength) {
            return array;
        }
        int baseLength = (int)(length + 0x7FFFFFFL >>> 27);
        boolean[][] base = (boolean[][])Arrays.copyOf(array, baseLength);
        int residual = (int)(length & 0x7FFFFFFL);
        if (residual != 0) {
            base[baseLength - 1] = BooleanArrays.trim(base[baseLength - 1], residual);
        }
        return base;
    }

    public static boolean[][] setLength(boolean[][] array, long length) {
        long oldLength = BooleanBigArrays.length(array);
        if (length == oldLength) {
            return array;
        }
        if (length < oldLength) {
            return BooleanBigArrays.trim(array, length);
        }
        return BooleanBigArrays.ensureCapacity(array, length);
    }

    public static boolean[][] copy(boolean[][] array, long offset, long length) {
        BooleanBigArrays.ensureOffsetLength(array, offset, length);
        boolean[][] a2 = BooleanBigArrays.newBigArray(length);
        BooleanBigArrays.copy(array, offset, a2, 0L, length);
        return a2;
    }

    public static boolean[][] copy(boolean[][] array) {
        boolean[][] base = (boolean[][])array.clone();
        int i2 = base.length;
        while (i2-- != 0) {
            base[i2] = (boolean[])array[i2].clone();
        }
        return base;
    }

    public static void fill(boolean[][] array, boolean value) {
        int i2 = array.length;
        while (i2-- != 0) {
            Arrays.fill(array[i2], value);
        }
    }

    public static void fill(boolean[][] array, long from, long to2, boolean value) {
        long length = BooleanBigArrays.length(array);
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

    public static boolean equals(boolean[][] a1, boolean[][] a2) {
        if (BooleanBigArrays.length(a1) != BooleanBigArrays.length(a2)) {
            return false;
        }
        int i2 = a1.length;
        while (i2-- != 0) {
            boolean[] t2 = a1[i2];
            boolean[] u2 = a2[i2];
            int j2 = t2.length;
            while (j2-- != 0) {
                if (t2[j2] == u2[j2]) continue;
                return false;
            }
        }
        return true;
    }

    public static String toString(boolean[][] a2) {
        if (a2 == null) {
            return "null";
        }
        long last = BooleanBigArrays.length(a2) - 1L;
        if (last == -1L) {
            return "[]";
        }
        StringBuilder b2 = new StringBuilder();
        b2.append('[');
        long i2 = 0L;
        while (true) {
            b2.append(String.valueOf(BooleanBigArrays.get(a2, i2)));
            if (i2 == last) {
                return b2.append(']').toString();
            }
            b2.append(", ");
            ++i2;
        }
    }

    public static void ensureFromTo(boolean[][] a2, long from, long to2) {
        BigArrays.ensureFromTo(BooleanBigArrays.length(a2), from, to2);
    }

    public static void ensureOffsetLength(boolean[][] a2, long offset, long length) {
        BigArrays.ensureOffsetLength(BooleanBigArrays.length(a2), offset, length);
    }

    private static void vecSwap(boolean[][] x2, long a2, long b2, long n2) {
        int i2 = 0;
        while ((long)i2 < n2) {
            BooleanBigArrays.swap(x2, a2, b2);
            ++i2;
            ++a2;
            ++b2;
        }
    }

    private static long med3(boolean[][] x2, long a2, long b2, long c2, BooleanComparator comp) {
        int ab2 = comp.compare(BooleanBigArrays.get(x2, a2), BooleanBigArrays.get(x2, b2));
        int ac2 = comp.compare(BooleanBigArrays.get(x2, a2), BooleanBigArrays.get(x2, c2));
        int bc2 = comp.compare(BooleanBigArrays.get(x2, b2), BooleanBigArrays.get(x2, c2));
        return ab2 < 0 ? (bc2 < 0 ? b2 : (ac2 < 0 ? c2 : a2)) : (bc2 > 0 ? b2 : (ac2 > 0 ? c2 : a2));
    }

    private static void selectionSort(boolean[][] a2, long from, long to2, BooleanComparator comp) {
        for (long i2 = from; i2 < to2 - 1L; ++i2) {
            long m2 = i2;
            for (long j2 = i2 + 1L; j2 < to2; ++j2) {
                if (comp.compare(BooleanBigArrays.get(a2, j2), BooleanBigArrays.get(a2, m2)) >= 0) continue;
                m2 = j2;
            }
            if (m2 == i2) continue;
            BooleanBigArrays.swap(a2, i2, m2);
        }
    }

    public static void quickSort(boolean[][] x2, long from, long to2, BooleanComparator comp) {
        long c2;
        long a2;
        long len = to2 - from;
        if (len < 7L) {
            BooleanBigArrays.selectionSort(x2, from, to2, comp);
            return;
        }
        long m2 = from + len / 2L;
        if (len > 7L) {
            long l2 = from;
            long n2 = to2 - 1L;
            if (len > 40L) {
                long s2 = len / 8L;
                l2 = BooleanBigArrays.med3(x2, l2, l2 + s2, l2 + 2L * s2, comp);
                m2 = BooleanBigArrays.med3(x2, m2 - s2, m2, m2 + s2, comp);
                n2 = BooleanBigArrays.med3(x2, n2 - 2L * s2, n2 - s2, n2, comp);
            }
            m2 = BooleanBigArrays.med3(x2, l2, m2, n2, comp);
        }
        boolean v2 = BooleanBigArrays.get(x2, m2);
        long b2 = a2 = from;
        long d2 = c2 = to2 - 1L;
        while (true) {
            int comparison;
            if (b2 <= c2 && (comparison = comp.compare(BooleanBigArrays.get(x2, b2), v2)) <= 0) {
                if (comparison == 0) {
                    BooleanBigArrays.swap(x2, a2++, b2);
                }
                ++b2;
                continue;
            }
            while (c2 >= b2 && (comparison = comp.compare(BooleanBigArrays.get(x2, c2), v2)) >= 0) {
                if (comparison == 0) {
                    BooleanBigArrays.swap(x2, c2, d2--);
                }
                --c2;
            }
            if (b2 > c2) break;
            BooleanBigArrays.swap(x2, b2++, c2--);
        }
        long n3 = to2;
        long s3 = Math.min(a2 - from, b2 - a2);
        BooleanBigArrays.vecSwap(x2, from, b2 - s3, s3);
        s3 = Math.min(d2 - c2, n3 - d2 - 1L);
        BooleanBigArrays.vecSwap(x2, b2, n3 - s3, s3);
        s3 = b2 - a2;
        if (s3 > 1L) {
            BooleanBigArrays.quickSort(x2, from, from + s3, comp);
        }
        if ((s3 = d2 - c2) > 1L) {
            BooleanBigArrays.quickSort(x2, n3 - s3, n3, comp);
        }
    }

    private static long med3(boolean[][] x2, long a2, long b2, long c2) {
        int ab2 = Boolean.compare(BooleanBigArrays.get(x2, a2), BooleanBigArrays.get(x2, b2));
        int ac2 = Boolean.compare(BooleanBigArrays.get(x2, a2), BooleanBigArrays.get(x2, c2));
        int bc2 = Boolean.compare(BooleanBigArrays.get(x2, b2), BooleanBigArrays.get(x2, c2));
        return ab2 < 0 ? (bc2 < 0 ? b2 : (ac2 < 0 ? c2 : a2)) : (bc2 > 0 ? b2 : (ac2 > 0 ? c2 : a2));
    }

    private static void selectionSort(boolean[][] a2, long from, long to2) {
        for (long i2 = from; i2 < to2 - 1L; ++i2) {
            long m2 = i2;
            for (long j2 = i2 + 1L; j2 < to2; ++j2) {
                if (BooleanBigArrays.get(a2, j2) || !BooleanBigArrays.get(a2, m2)) continue;
                m2 = j2;
            }
            if (m2 == i2) continue;
            BooleanBigArrays.swap(a2, i2, m2);
        }
    }

    public static void quickSort(boolean[][] x2, BooleanComparator comp) {
        BooleanBigArrays.quickSort(x2, 0L, BooleanBigArrays.length(x2), comp);
    }

    public static void quickSort(boolean[][] x2, long from, long to2) {
        long c2;
        long a2;
        long len = to2 - from;
        if (len < 7L) {
            BooleanBigArrays.selectionSort(x2, from, to2);
            return;
        }
        long m2 = from + len / 2L;
        if (len > 7L) {
            long l2 = from;
            long n2 = to2 - 1L;
            if (len > 40L) {
                long s2 = len / 8L;
                l2 = BooleanBigArrays.med3(x2, l2, l2 + s2, l2 + 2L * s2);
                m2 = BooleanBigArrays.med3(x2, m2 - s2, m2, m2 + s2);
                n2 = BooleanBigArrays.med3(x2, n2 - 2L * s2, n2 - s2, n2);
            }
            m2 = BooleanBigArrays.med3(x2, l2, m2, n2);
        }
        boolean v2 = BooleanBigArrays.get(x2, m2);
        long b2 = a2 = from;
        long d2 = c2 = to2 - 1L;
        while (true) {
            int comparison;
            if (b2 <= c2 && (comparison = Boolean.compare(BooleanBigArrays.get(x2, b2), v2)) <= 0) {
                if (comparison == 0) {
                    BooleanBigArrays.swap(x2, a2++, b2);
                }
                ++b2;
                continue;
            }
            while (c2 >= b2 && (comparison = Boolean.compare(BooleanBigArrays.get(x2, c2), v2)) >= 0) {
                if (comparison == 0) {
                    BooleanBigArrays.swap(x2, c2, d2--);
                }
                --c2;
            }
            if (b2 > c2) break;
            BooleanBigArrays.swap(x2, b2++, c2--);
        }
        long n3 = to2;
        long s3 = Math.min(a2 - from, b2 - a2);
        BooleanBigArrays.vecSwap(x2, from, b2 - s3, s3);
        s3 = Math.min(d2 - c2, n3 - d2 - 1L);
        BooleanBigArrays.vecSwap(x2, b2, n3 - s3, s3);
        s3 = b2 - a2;
        if (s3 > 1L) {
            BooleanBigArrays.quickSort(x2, from, from + s3);
        }
        if ((s3 = d2 - c2) > 1L) {
            BooleanBigArrays.quickSort(x2, n3 - s3, n3);
        }
    }

    public static void quickSort(boolean[][] x2) {
        BooleanBigArrays.quickSort(x2, 0L, BooleanBigArrays.length(x2));
    }

    public static boolean[][] shuffle(boolean[][] a2, long from, long to2, Random random) {
        long i2 = to2 - from;
        while (i2-- != 0L) {
            long p2 = (random.nextLong() & Long.MAX_VALUE) % (i2 + 1L);
            boolean t2 = BooleanBigArrays.get(a2, from + i2);
            BooleanBigArrays.set(a2, from + i2, BooleanBigArrays.get(a2, from + p2));
            BooleanBigArrays.set(a2, from + p2, t2);
        }
        return a2;
    }

    public static boolean[][] shuffle(boolean[][] a2, Random random) {
        long i2 = BooleanBigArrays.length(a2);
        while (i2-- != 0L) {
            long p2 = (random.nextLong() & Long.MAX_VALUE) % (i2 + 1L);
            boolean t2 = BooleanBigArrays.get(a2, i2);
            BooleanBigArrays.set(a2, i2, BooleanBigArrays.get(a2, p2));
            BooleanBigArrays.set(a2, p2, t2);
        }
        return a2;
    }

    private static final class BigArrayHashStrategy
    implements Hash.Strategy<boolean[][]>,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;

        private BigArrayHashStrategy() {
        }

        @Override
        public int hashCode(boolean[][] o2) {
            return Arrays.deepHashCode((Object[])o2);
        }

        @Override
        public boolean equals(boolean[][] a2, boolean[][] b2) {
            return BooleanBigArrays.equals(a2, b2);
        }
    }
}

