package it.unimi.dsi.fastutil;

import it.unimi.dsi.fastutil.BigSwapper;
import it.unimi.dsi.fastutil.ints.IntBigArrays;
import it.unimi.dsi.fastutil.longs.LongComparator;

public class BigArrays {
    public static final int SEGMENT_SHIFT = 27;
    public static final int SEGMENT_SIZE = 0x8000000;
    public static final int SEGMENT_MASK = 0x7FFFFFF;
    private static final int SMALL = 7;
    private static final int MEDIUM = 40;

    protected BigArrays() {
    }

    public static int segment(long index) {
        return (int)(index >>> 27);
    }

    public static int displacement(long index) {
        return (int)(index & 0x7FFFFFFL);
    }

    public static long start(int segment) {
        return (long)segment << 27;
    }

    public static long index(int segment, int displacement) {
        return BigArrays.start(segment) + (long)displacement;
    }

    public static void ensureFromTo(long bigArrayLength, long from, long to2) {
        if (from < 0L) {
            throw new ArrayIndexOutOfBoundsException("Start index (" + from + ") is negative");
        }
        if (from > to2) {
            throw new IllegalArgumentException("Start index (" + from + ") is greater than end index (" + to2 + ")");
        }
        if (to2 > bigArrayLength) {
            throw new ArrayIndexOutOfBoundsException("End index (" + to2 + ") is greater than big-array length (" + bigArrayLength + ")");
        }
    }

    public static void ensureOffsetLength(long bigArrayLength, long offset, long length) {
        if (offset < 0L) {
            throw new ArrayIndexOutOfBoundsException("Offset (" + offset + ") is negative");
        }
        if (length < 0L) {
            throw new IllegalArgumentException("Length (" + length + ") is negative");
        }
        if (offset + length > bigArrayLength) {
            throw new ArrayIndexOutOfBoundsException("Last index (" + (offset + length) + ") is greater than big-array length (" + bigArrayLength + ")");
        }
    }

    public static void ensureLength(long bigArrayLength) {
        if (bigArrayLength < 0L) {
            throw new IllegalArgumentException("Negative big-array size: " + bigArrayLength);
        }
        if (bigArrayLength >= 288230376017494016L) {
            throw new IllegalArgumentException("Big-array size too big: " + bigArrayLength);
        }
    }

    private static void inPlaceMerge(long from, long mid, long to2, LongComparator comp, BigSwapper swapper) {
        long secondCut;
        long firstCut;
        if (from >= mid || mid >= to2) {
            return;
        }
        if (to2 - from == 2L) {
            if (comp.compare(mid, from) < 0) {
                swapper.swap(from, mid);
            }
            return;
        }
        if (mid - from > to2 - mid) {
            firstCut = from + (mid - from) / 2L;
            secondCut = BigArrays.lowerBound(mid, to2, firstCut, comp);
        } else {
            secondCut = mid + (to2 - mid) / 2L;
            firstCut = BigArrays.upperBound(from, mid, secondCut, comp);
        }
        long first2 = firstCut;
        long middle2 = mid;
        long last2 = secondCut;
        if (middle2 != first2 && middle2 != last2) {
            long first1 = first2;
            long last1 = middle2;
            while (first1 < --last1) {
                swapper.swap(first1++, last1);
            }
            first1 = middle2;
            last1 = last2;
            while (first1 < --last1) {
                swapper.swap(first1++, last1);
            }
            first1 = first2;
            last1 = last2;
            while (first1 < --last1) {
                swapper.swap(first1++, last1);
            }
        }
        mid = firstCut + (secondCut - mid);
        BigArrays.inPlaceMerge(from, firstCut, mid, comp, swapper);
        BigArrays.inPlaceMerge(mid, secondCut, to2, comp, swapper);
    }

    private static long lowerBound(long mid, long to2, long firstCut, LongComparator comp) {
        long len = to2 - mid;
        while (len > 0L) {
            long half = len / 2L;
            long middle = mid + half;
            if (comp.compare(middle, firstCut) < 0) {
                mid = middle + 1L;
                len -= half + 1L;
                continue;
            }
            len = half;
        }
        return mid;
    }

    private static long med3(long a2, long b2, long c2, LongComparator comp) {
        int ab2 = comp.compare(a2, b2);
        int ac2 = comp.compare(a2, c2);
        int bc2 = comp.compare(b2, c2);
        return ab2 < 0 ? (bc2 < 0 ? b2 : (ac2 < 0 ? c2 : a2)) : (bc2 > 0 ? b2 : (ac2 > 0 ? c2 : a2));
    }

    public static void mergeSort(long from, long to2, LongComparator comp, BigSwapper swapper) {
        long length = to2 - from;
        if (length < 7L) {
            for (long i2 = from; i2 < to2; ++i2) {
                for (long j2 = i2; j2 > from && comp.compare(j2 - 1L, j2) > 0; --j2) {
                    swapper.swap(j2, j2 - 1L);
                }
            }
            return;
        }
        long mid = from + to2 >>> 1;
        BigArrays.mergeSort(from, mid, comp, swapper);
        BigArrays.mergeSort(mid, to2, comp, swapper);
        if (comp.compare(mid - 1L, mid) <= 0) {
            return;
        }
        BigArrays.inPlaceMerge(from, mid, to2, comp, swapper);
    }

    public static void quickSort(long from, long to2, LongComparator comp, BigSwapper swapper) {
        long c2;
        long a2;
        long len = to2 - from;
        if (len < 7L) {
            for (long i2 = from; i2 < to2; ++i2) {
                for (long j2 = i2; j2 > from && comp.compare(j2 - 1L, j2) > 0; --j2) {
                    swapper.swap(j2, j2 - 1L);
                }
            }
            return;
        }
        long m2 = from + len / 2L;
        if (len > 7L) {
            long l2 = from;
            long n2 = to2 - 1L;
            if (len > 40L) {
                long s2 = len / 8L;
                l2 = BigArrays.med3(l2, l2 + s2, l2 + 2L * s2, comp);
                m2 = BigArrays.med3(m2 - s2, m2, m2 + s2, comp);
                n2 = BigArrays.med3(n2 - 2L * s2, n2 - s2, n2, comp);
            }
            m2 = BigArrays.med3(l2, m2, n2, comp);
        }
        long b2 = a2 = from;
        long d2 = c2 = to2 - 1L;
        while (true) {
            int comparison;
            if (b2 <= c2 && (comparison = comp.compare(b2, m2)) <= 0) {
                if (comparison == 0) {
                    if (a2 == m2) {
                        m2 = b2;
                    } else if (b2 == m2) {
                        m2 = a2;
                    }
                    swapper.swap(a2++, b2);
                }
                ++b2;
                continue;
            }
            while (c2 >= b2 && (comparison = comp.compare(c2, m2)) >= 0) {
                if (comparison == 0) {
                    if (c2 == m2) {
                        m2 = d2;
                    } else if (d2 == m2) {
                        m2 = c2;
                    }
                    swapper.swap(c2, d2--);
                }
                --c2;
            }
            if (b2 > c2) break;
            if (b2 == m2) {
                m2 = d2;
            } else if (c2 == m2) {
                m2 = c2;
            }
            swapper.swap(b2++, c2--);
        }
        long n3 = from + len;
        long s3 = Math.min(a2 - from, b2 - a2);
        BigArrays.vecSwap(swapper, from, b2 - s3, s3);
        s3 = Math.min(d2 - c2, n3 - d2 - 1L);
        BigArrays.vecSwap(swapper, b2, n3 - s3, s3);
        s3 = b2 - a2;
        if (s3 > 1L) {
            BigArrays.quickSort(from, from + s3, comp, swapper);
        }
        if ((s3 = d2 - c2) > 1L) {
            BigArrays.quickSort(n3 - s3, n3, comp, swapper);
        }
    }

    private static long upperBound(long from, long mid, long secondCut, LongComparator comp) {
        long len = mid - from;
        while (len > 0L) {
            long half = len / 2L;
            long middle = from + half;
            if (comp.compare(secondCut, middle) < 0) {
                len = half;
                continue;
            }
            from = middle + 1L;
            len -= half + 1L;
        }
        return from;
    }

    private static void vecSwap(BigSwapper swapper, long from, long l2, long s2) {
        int i2 = 0;
        while ((long)i2 < s2) {
            swapper.swap(from, l2);
            ++i2;
            ++from;
            ++l2;
        }
    }

    public static void main(String[] arg2) {
        int[][] a2 = IntBigArrays.newBigArray(1L << Integer.parseInt(arg2[0]));
        int k2 = 10;
        while (k2-- != 0) {
            long start = -System.currentTimeMillis();
            long x2 = 0L;
            long i2 = IntBigArrays.length(a2);
            while (i2-- != 0L) {
                x2 ^= i2 ^ (long)IntBigArrays.get(a2, i2);
            }
            if (x2 == 0L) {
                System.err.println();
            }
            System.out.println("Single loop: " + (start + System.currentTimeMillis()) + "ms");
            start = -System.currentTimeMillis();
            long y2 = 0L;
            int i22 = a2.length;
            while (i22-- != 0) {
                int[] t2 = a2[i22];
                int d2 = t2.length;
                while (d2-- != 0) {
                    y2 ^= (long)t2[d2] ^ BigArrays.index(i22, d2);
                }
            }
            if (y2 == 0L) {
                System.err.println();
            }
            if (x2 != y2) {
                throw new AssertionError();
            }
            System.out.println("Double loop: " + (start + System.currentTimeMillis()) + "ms");
            long z2 = 0L;
            long j2 = IntBigArrays.length(a2);
            int i3 = a2.length;
            while (i3-- != 0) {
                int[] t3 = a2[i3];
                int d3 = t3.length;
                while (d3-- != 0) {
                    y2 ^= (long)t3[d3] ^ --j2;
                }
            }
            if (z2 == 0L) {
                System.err.println();
            }
            if (x2 != z2) {
                throw new AssertionError();
            }
            System.out.println("Double loop (with additional index): " + (start + System.currentTimeMillis()) + "ms");
        }
    }
}

