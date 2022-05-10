package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.Arrays;
import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.bytes.ByteComparator;
import it.unimi.dsi.fastutil.ints.IntArrays;
import java.io.Serializable;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.atomic.AtomicInteger;

public class ByteArrays {
    public static final byte[] EMPTY_ARRAY = new byte[0];
    private static final int QUICKSORT_NO_REC = 16;
    private static final int PARALLEL_QUICKSORT_NO_FORK = 8192;
    private static final int QUICKSORT_MEDIAN_OF_9 = 128;
    private static final int MERGESORT_NO_REC = 16;
    private static final int DIGIT_BITS = 8;
    private static final int DIGIT_MASK = 255;
    private static final int DIGITS_PER_ELEMENT = 1;
    private static final int RADIXSORT_NO_REC = 1024;
    private static final int PARALLEL_RADIXSORT_NO_FORK = 1024;
    protected static final Segment POISON_PILL = new Segment(-1, -1, -1);
    public static final Hash.Strategy<byte[]> HASH_STRATEGY = new ArrayHashStrategy();

    private ByteArrays() {
    }

    public static byte[] ensureCapacity(byte[] array, int length) {
        if (length > array.length) {
            byte[] t2 = new byte[length];
            System.arraycopy(array, 0, t2, 0, array.length);
            return t2;
        }
        return array;
    }

    public static byte[] ensureCapacity(byte[] array, int length, int preserve) {
        if (length > array.length) {
            byte[] t2 = new byte[length];
            System.arraycopy(array, 0, t2, 0, preserve);
            return t2;
        }
        return array;
    }

    public static byte[] grow(byte[] array, int length) {
        if (length > array.length) {
            int newLength = (int)Math.max(Math.min(2L * (long)array.length, 0x7FFFFFF7L), (long)length);
            byte[] t2 = new byte[newLength];
            System.arraycopy(array, 0, t2, 0, array.length);
            return t2;
        }
        return array;
    }

    public static byte[] grow(byte[] array, int length, int preserve) {
        if (length > array.length) {
            int newLength = (int)Math.max(Math.min(2L * (long)array.length, 0x7FFFFFF7L), (long)length);
            byte[] t2 = new byte[newLength];
            System.arraycopy(array, 0, t2, 0, preserve);
            return t2;
        }
        return array;
    }

    public static byte[] trim(byte[] array, int length) {
        if (length >= array.length) {
            return array;
        }
        byte[] t2 = length == 0 ? EMPTY_ARRAY : new byte[length];
        System.arraycopy(array, 0, t2, 0, length);
        return t2;
    }

    public static byte[] setLength(byte[] array, int length) {
        if (length == array.length) {
            return array;
        }
        if (length < array.length) {
            return ByteArrays.trim(array, length);
        }
        return ByteArrays.ensureCapacity(array, length);
    }

    public static byte[] copy(byte[] array, int offset, int length) {
        ByteArrays.ensureOffsetLength(array, offset, length);
        byte[] a2 = length == 0 ? EMPTY_ARRAY : new byte[length];
        System.arraycopy(array, offset, a2, 0, length);
        return a2;
    }

    public static byte[] copy(byte[] array) {
        return (byte[])array.clone();
    }

    @Deprecated
    public static void fill(byte[] array, byte value) {
        int i2 = array.length;
        while (i2-- != 0) {
            array[i2] = value;
        }
    }

    @Deprecated
    public static void fill(byte[] array, int from, int to2, byte value) {
        ByteArrays.ensureFromTo(array, from, to2);
        if (from == 0) {
            while (to2-- != 0) {
                array[to2] = value;
            }
        } else {
            for (int i2 = from; i2 < to2; ++i2) {
                array[i2] = value;
            }
        }
    }

    @Deprecated
    public static boolean equals(byte[] a1, byte[] a2) {
        int i2 = a1.length;
        if (i2 != a2.length) {
            return false;
        }
        while (i2-- != 0) {
            if (a1[i2] == a2[i2]) continue;
            return false;
        }
        return true;
    }

    public static void ensureFromTo(byte[] a2, int from, int to2) {
        Arrays.ensureFromTo(a2.length, from, to2);
    }

    public static void ensureOffsetLength(byte[] a2, int offset, int length) {
        Arrays.ensureOffsetLength(a2.length, offset, length);
    }

    public static void ensureSameLength(byte[] a2, byte[] b2) {
        if (a2.length != b2.length) {
            throw new IllegalArgumentException("Array size mismatch: " + a2.length + " != " + b2.length);
        }
    }

    public static void swap(byte[] x2, int a2, int b2) {
        byte t2 = x2[a2];
        x2[a2] = x2[b2];
        x2[b2] = t2;
    }

    public static void swap(byte[] x2, int a2, int b2, int n2) {
        int i2 = 0;
        while (i2 < n2) {
            ByteArrays.swap(x2, a2, b2);
            ++i2;
            ++a2;
            ++b2;
        }
    }

    private static int med3(byte[] x2, int a2, int b2, int c2, ByteComparator comp) {
        int ab2 = comp.compare(x2[a2], x2[b2]);
        int ac2 = comp.compare(x2[a2], x2[c2]);
        int bc2 = comp.compare(x2[b2], x2[c2]);
        return ab2 < 0 ? (bc2 < 0 ? b2 : (ac2 < 0 ? c2 : a2)) : (bc2 > 0 ? b2 : (ac2 > 0 ? c2 : a2));
    }

    private static void selectionSort(byte[] a2, int from, int to2, ByteComparator comp) {
        for (int i2 = from; i2 < to2 - 1; ++i2) {
            int m2 = i2;
            for (int j2 = i2 + 1; j2 < to2; ++j2) {
                if (comp.compare(a2[j2], a2[m2]) >= 0) continue;
                m2 = j2;
            }
            if (m2 == i2) continue;
            byte u2 = a2[i2];
            a2[i2] = a2[m2];
            a2[m2] = u2;
        }
    }

    private static void insertionSort(byte[] a2, int from, int to2, ByteComparator comp) {
        int i2 = from;
        while (++i2 < to2) {
            byte t2 = a2[i2];
            int j2 = i2;
            byte u2 = a2[j2 - 1];
            while (comp.compare(t2, u2) < 0) {
                a2[j2] = u2;
                if (from == j2 - 1) {
                    --j2;
                    break;
                }
                u2 = a2[--j2 - 1];
            }
            a2[j2] = t2;
        }
    }

    public static void quickSort(byte[] x2, int from, int to2, ByteComparator comp) {
        int c2;
        int a2;
        int len = to2 - from;
        if (len < 16) {
            ByteArrays.selectionSort(x2, from, to2, comp);
            return;
        }
        int m2 = from + len / 2;
        int l2 = from;
        int n2 = to2 - 1;
        if (len > 128) {
            int s2 = len / 8;
            l2 = ByteArrays.med3(x2, l2, l2 + s2, l2 + 2 * s2, comp);
            m2 = ByteArrays.med3(x2, m2 - s2, m2, m2 + s2, comp);
            n2 = ByteArrays.med3(x2, n2 - 2 * s2, n2 - s2, n2, comp);
        }
        m2 = ByteArrays.med3(x2, l2, m2, n2, comp);
        byte v2 = x2[m2];
        int b2 = a2 = from;
        int d2 = c2 = to2 - 1;
        while (true) {
            int comparison;
            if (b2 <= c2 && (comparison = comp.compare(x2[b2], v2)) <= 0) {
                if (comparison == 0) {
                    ByteArrays.swap(x2, a2++, b2);
                }
                ++b2;
                continue;
            }
            while (c2 >= b2 && (comparison = comp.compare(x2[c2], v2)) >= 0) {
                if (comparison == 0) {
                    ByteArrays.swap(x2, c2, d2--);
                }
                --c2;
            }
            if (b2 > c2) break;
            ByteArrays.swap(x2, b2++, c2--);
        }
        int s3 = Math.min(a2 - from, b2 - a2);
        ByteArrays.swap(x2, from, b2 - s3, s3);
        s3 = Math.min(d2 - c2, to2 - d2 - 1);
        ByteArrays.swap(x2, b2, to2 - s3, s3);
        s3 = b2 - a2;
        if (s3 > 1) {
            ByteArrays.quickSort(x2, from, from + s3, comp);
        }
        if ((s3 = d2 - c2) > 1) {
            ByteArrays.quickSort(x2, to2 - s3, to2, comp);
        }
    }

    public static void quickSort(byte[] x2, ByteComparator comp) {
        ByteArrays.quickSort(x2, 0, x2.length, comp);
    }

    public static void parallelQuickSort(byte[] x2, int from, int to2, ByteComparator comp) {
        if (to2 - from < 8192) {
            ByteArrays.quickSort(x2, from, to2, comp);
        } else {
            ForkJoinPool pool = new ForkJoinPool(Runtime.getRuntime().availableProcessors());
            pool.invoke(new ForkJoinQuickSortComp(x2, from, to2, comp));
            pool.shutdown();
        }
    }

    public static void parallelQuickSort(byte[] x2, ByteComparator comp) {
        ByteArrays.parallelQuickSort(x2, 0, x2.length, comp);
    }

    private static int med3(byte[] x2, int a2, int b2, int c2) {
        int ab2 = Byte.compare(x2[a2], x2[b2]);
        int ac2 = Byte.compare(x2[a2], x2[c2]);
        int bc2 = Byte.compare(x2[b2], x2[c2]);
        return ab2 < 0 ? (bc2 < 0 ? b2 : (ac2 < 0 ? c2 : a2)) : (bc2 > 0 ? b2 : (ac2 > 0 ? c2 : a2));
    }

    private static void selectionSort(byte[] a2, int from, int to2) {
        for (int i2 = from; i2 < to2 - 1; ++i2) {
            int m2 = i2;
            for (int j2 = i2 + 1; j2 < to2; ++j2) {
                if (a2[j2] >= a2[m2]) continue;
                m2 = j2;
            }
            if (m2 == i2) continue;
            byte u2 = a2[i2];
            a2[i2] = a2[m2];
            a2[m2] = u2;
        }
    }

    private static void insertionSort(byte[] a2, int from, int to2) {
        int i2 = from;
        while (++i2 < to2) {
            byte t2 = a2[i2];
            int j2 = i2;
            byte u2 = a2[j2 - 1];
            while (t2 < u2) {
                a2[j2] = u2;
                if (from == j2 - 1) {
                    --j2;
                    break;
                }
                u2 = a2[--j2 - 1];
            }
            a2[j2] = t2;
        }
    }

    public static void quickSort(byte[] x2, int from, int to2) {
        int c2;
        int a2;
        int len = to2 - from;
        if (len < 16) {
            ByteArrays.selectionSort(x2, from, to2);
            return;
        }
        int m2 = from + len / 2;
        int l2 = from;
        int n2 = to2 - 1;
        if (len > 128) {
            int s2 = len / 8;
            l2 = ByteArrays.med3(x2, l2, l2 + s2, l2 + 2 * s2);
            m2 = ByteArrays.med3(x2, m2 - s2, m2, m2 + s2);
            n2 = ByteArrays.med3(x2, n2 - 2 * s2, n2 - s2, n2);
        }
        m2 = ByteArrays.med3(x2, l2, m2, n2);
        byte v2 = x2[m2];
        int b2 = a2 = from;
        int d2 = c2 = to2 - 1;
        while (true) {
            int comparison;
            if (b2 <= c2 && (comparison = Byte.compare(x2[b2], v2)) <= 0) {
                if (comparison == 0) {
                    ByteArrays.swap(x2, a2++, b2);
                }
                ++b2;
                continue;
            }
            while (c2 >= b2 && (comparison = Byte.compare(x2[c2], v2)) >= 0) {
                if (comparison == 0) {
                    ByteArrays.swap(x2, c2, d2--);
                }
                --c2;
            }
            if (b2 > c2) break;
            ByteArrays.swap(x2, b2++, c2--);
        }
        int s3 = Math.min(a2 - from, b2 - a2);
        ByteArrays.swap(x2, from, b2 - s3, s3);
        s3 = Math.min(d2 - c2, to2 - d2 - 1);
        ByteArrays.swap(x2, b2, to2 - s3, s3);
        s3 = b2 - a2;
        if (s3 > 1) {
            ByteArrays.quickSort(x2, from, from + s3);
        }
        if ((s3 = d2 - c2) > 1) {
            ByteArrays.quickSort(x2, to2 - s3, to2);
        }
    }

    public static void quickSort(byte[] x2) {
        ByteArrays.quickSort(x2, 0, x2.length);
    }

    public static void parallelQuickSort(byte[] x2, int from, int to2) {
        if (to2 - from < 8192) {
            ByteArrays.quickSort(x2, from, to2);
        } else {
            ForkJoinPool pool = new ForkJoinPool(Runtime.getRuntime().availableProcessors());
            pool.invoke(new ForkJoinQuickSort(x2, from, to2));
            pool.shutdown();
        }
    }

    public static void parallelQuickSort(byte[] x2) {
        ByteArrays.parallelQuickSort(x2, 0, x2.length);
    }

    private static int med3Indirect(int[] perm, byte[] x2, int a2, int b2, int c2) {
        byte aa2 = x2[perm[a2]];
        byte bb2 = x2[perm[b2]];
        byte cc2 = x2[perm[c2]];
        int ab2 = Byte.compare(aa2, bb2);
        int ac2 = Byte.compare(aa2, cc2);
        int bc2 = Byte.compare(bb2, cc2);
        return ab2 < 0 ? (bc2 < 0 ? b2 : (ac2 < 0 ? c2 : a2)) : (bc2 > 0 ? b2 : (ac2 > 0 ? c2 : a2));
    }

    private static void insertionSortIndirect(int[] perm, byte[] a2, int from, int to2) {
        int i2 = from;
        while (++i2 < to2) {
            int t2 = perm[i2];
            int j2 = i2;
            int u2 = perm[j2 - 1];
            while (a2[t2] < a2[u2]) {
                perm[j2] = u2;
                if (from == j2 - 1) {
                    --j2;
                    break;
                }
                u2 = perm[--j2 - 1];
            }
            perm[j2] = t2;
        }
    }

    public static void quickSortIndirect(int[] perm, byte[] x2, int from, int to2) {
        int c2;
        int a2;
        int len = to2 - from;
        if (len < 16) {
            ByteArrays.insertionSortIndirect(perm, x2, from, to2);
            return;
        }
        int m2 = from + len / 2;
        int l2 = from;
        int n2 = to2 - 1;
        if (len > 128) {
            int s2 = len / 8;
            l2 = ByteArrays.med3Indirect(perm, x2, l2, l2 + s2, l2 + 2 * s2);
            m2 = ByteArrays.med3Indirect(perm, x2, m2 - s2, m2, m2 + s2);
            n2 = ByteArrays.med3Indirect(perm, x2, n2 - 2 * s2, n2 - s2, n2);
        }
        m2 = ByteArrays.med3Indirect(perm, x2, l2, m2, n2);
        byte v2 = x2[perm[m2]];
        int b2 = a2 = from;
        int d2 = c2 = to2 - 1;
        while (true) {
            int comparison;
            if (b2 <= c2 && (comparison = Byte.compare(x2[perm[b2]], v2)) <= 0) {
                if (comparison == 0) {
                    IntArrays.swap(perm, a2++, b2);
                }
                ++b2;
                continue;
            }
            while (c2 >= b2 && (comparison = Byte.compare(x2[perm[c2]], v2)) >= 0) {
                if (comparison == 0) {
                    IntArrays.swap(perm, c2, d2--);
                }
                --c2;
            }
            if (b2 > c2) break;
            IntArrays.swap(perm, b2++, c2--);
        }
        int s3 = Math.min(a2 - from, b2 - a2);
        IntArrays.swap(perm, from, b2 - s3, s3);
        s3 = Math.min(d2 - c2, to2 - d2 - 1);
        IntArrays.swap(perm, b2, to2 - s3, s3);
        s3 = b2 - a2;
        if (s3 > 1) {
            ByteArrays.quickSortIndirect(perm, x2, from, from + s3);
        }
        if ((s3 = d2 - c2) > 1) {
            ByteArrays.quickSortIndirect(perm, x2, to2 - s3, to2);
        }
    }

    public static void quickSortIndirect(int[] perm, byte[] x2) {
        ByteArrays.quickSortIndirect(perm, x2, 0, x2.length);
    }

    public static void parallelQuickSortIndirect(int[] perm, byte[] x2, int from, int to2) {
        if (to2 - from < 8192) {
            ByteArrays.quickSortIndirect(perm, x2, from, to2);
        } else {
            ForkJoinPool pool = new ForkJoinPool(Runtime.getRuntime().availableProcessors());
            pool.invoke(new ForkJoinQuickSortIndirect(perm, x2, from, to2));
            pool.shutdown();
        }
    }

    public static void parallelQuickSortIndirect(int[] perm, byte[] x2) {
        ByteArrays.parallelQuickSortIndirect(perm, x2, 0, x2.length);
    }

    public static void stabilize(int[] perm, byte[] x2, int from, int to2) {
        int curr = from;
        for (int i2 = from + 1; i2 < to2; ++i2) {
            if (x2[perm[i2]] == x2[perm[curr]]) continue;
            if (i2 - curr > 1) {
                IntArrays.parallelQuickSort(perm, curr, i2);
            }
            curr = i2;
        }
        if (to2 - curr > 1) {
            IntArrays.parallelQuickSort(perm, curr, to2);
        }
    }

    public static void stabilize(int[] perm, byte[] x2) {
        ByteArrays.stabilize(perm, x2, 0, perm.length);
    }

    private static int med3(byte[] x2, byte[] y2, int a2, int b2, int c2) {
        int bc2;
        int t2 = Byte.compare(x2[a2], x2[b2]);
        int ab2 = t2 == 0 ? Byte.compare(y2[a2], y2[b2]) : t2;
        t2 = Byte.compare(x2[a2], x2[c2]);
        int ac2 = t2 == 0 ? Byte.compare(y2[a2], y2[c2]) : t2;
        t2 = Byte.compare(x2[b2], x2[c2]);
        int n2 = bc2 = t2 == 0 ? Byte.compare(y2[b2], y2[c2]) : t2;
        return ab2 < 0 ? (bc2 < 0 ? b2 : (ac2 < 0 ? c2 : a2)) : (bc2 > 0 ? b2 : (ac2 > 0 ? c2 : a2));
    }

    private static void swap(byte[] x2, byte[] y2, int a2, int b2) {
        byte t2 = x2[a2];
        byte u2 = y2[a2];
        x2[a2] = x2[b2];
        y2[a2] = y2[b2];
        x2[b2] = t2;
        y2[b2] = u2;
    }

    private static void swap(byte[] x2, byte[] y2, int a2, int b2, int n2) {
        int i2 = 0;
        while (i2 < n2) {
            ByteArrays.swap(x2, y2, a2, b2);
            ++i2;
            ++a2;
            ++b2;
        }
    }

    private static void selectionSort(byte[] a2, byte[] b2, int from, int to2) {
        for (int i2 = from; i2 < to2 - 1; ++i2) {
            int m2 = i2;
            for (int j2 = i2 + 1; j2 < to2; ++j2) {
                int u2 = Byte.compare(a2[j2], a2[m2]);
                if (u2 >= 0 && (u2 != 0 || b2[j2] >= b2[m2])) continue;
                m2 = j2;
            }
            if (m2 == i2) continue;
            byte t2 = a2[i2];
            a2[i2] = a2[m2];
            a2[m2] = t2;
            t2 = b2[i2];
            b2[i2] = b2[m2];
            b2[m2] = t2;
        }
    }

    public static void quickSort(byte[] x2, byte[] y2, int from, int to2) {
        int c2;
        int a2;
        int len = to2 - from;
        if (len < 16) {
            ByteArrays.selectionSort(x2, y2, from, to2);
            return;
        }
        int m2 = from + len / 2;
        int l2 = from;
        int n2 = to2 - 1;
        if (len > 128) {
            int s2 = len / 8;
            l2 = ByteArrays.med3(x2, y2, l2, l2 + s2, l2 + 2 * s2);
            m2 = ByteArrays.med3(x2, y2, m2 - s2, m2, m2 + s2);
            n2 = ByteArrays.med3(x2, y2, n2 - 2 * s2, n2 - s2, n2);
        }
        m2 = ByteArrays.med3(x2, y2, l2, m2, n2);
        byte v2 = x2[m2];
        byte w2 = y2[m2];
        int b2 = a2 = from;
        int d2 = c2 = to2 - 1;
        while (true) {
            int t2;
            int comparison;
            if (b2 <= c2 && (comparison = (t2 = Byte.compare(x2[b2], v2)) == 0 ? Byte.compare(y2[b2], w2) : t2) <= 0) {
                if (comparison == 0) {
                    ByteArrays.swap(x2, y2, a2++, b2);
                }
                ++b2;
                continue;
            }
            while (c2 >= b2 && (comparison = (t2 = Byte.compare(x2[c2], v2)) == 0 ? Byte.compare(y2[c2], w2) : t2) >= 0) {
                if (comparison == 0) {
                    ByteArrays.swap(x2, y2, c2, d2--);
                }
                --c2;
            }
            if (b2 > c2) break;
            ByteArrays.swap(x2, y2, b2++, c2--);
        }
        int s3 = Math.min(a2 - from, b2 - a2);
        ByteArrays.swap(x2, y2, from, b2 - s3, s3);
        s3 = Math.min(d2 - c2, to2 - d2 - 1);
        ByteArrays.swap(x2, y2, b2, to2 - s3, s3);
        s3 = b2 - a2;
        if (s3 > 1) {
            ByteArrays.quickSort(x2, y2, from, from + s3);
        }
        if ((s3 = d2 - c2) > 1) {
            ByteArrays.quickSort(x2, y2, to2 - s3, to2);
        }
    }

    public static void quickSort(byte[] x2, byte[] y2) {
        ByteArrays.ensureSameLength(x2, y2);
        ByteArrays.quickSort(x2, y2, 0, x2.length);
    }

    public static void parallelQuickSort(byte[] x2, byte[] y2, int from, int to2) {
        if (to2 - from < 8192) {
            ByteArrays.quickSort(x2, y2, from, to2);
        }
        ForkJoinPool pool = new ForkJoinPool(Runtime.getRuntime().availableProcessors());
        pool.invoke(new ForkJoinQuickSort2(x2, y2, from, to2));
        pool.shutdown();
    }

    public static void parallelQuickSort(byte[] x2, byte[] y2) {
        ByteArrays.ensureSameLength(x2, y2);
        ByteArrays.parallelQuickSort(x2, y2, 0, x2.length);
    }

    public static void mergeSort(byte[] a2, int from, int to2, byte[] supp) {
        int len = to2 - from;
        if (len < 16) {
            ByteArrays.insertionSort(a2, from, to2);
            return;
        }
        int mid = from + to2 >>> 1;
        ByteArrays.mergeSort(supp, from, mid, a2);
        ByteArrays.mergeSort(supp, mid, to2, a2);
        if (supp[mid - 1] <= supp[mid]) {
            System.arraycopy(supp, from, a2, from, len);
            return;
        }
        int p2 = from;
        int q2 = mid;
        for (int i2 = from; i2 < to2; ++i2) {
            a2[i2] = q2 >= to2 || p2 < mid && supp[p2] <= supp[q2] ? supp[p2++] : supp[q2++];
        }
    }

    public static void mergeSort(byte[] a2, int from, int to2) {
        ByteArrays.mergeSort(a2, from, to2, (byte[])a2.clone());
    }

    public static void mergeSort(byte[] a2) {
        ByteArrays.mergeSort(a2, 0, a2.length);
    }

    public static void mergeSort(byte[] a2, int from, int to2, ByteComparator comp, byte[] supp) {
        int len = to2 - from;
        if (len < 16) {
            ByteArrays.insertionSort(a2, from, to2, comp);
            return;
        }
        int mid = from + to2 >>> 1;
        ByteArrays.mergeSort(supp, from, mid, comp, a2);
        ByteArrays.mergeSort(supp, mid, to2, comp, a2);
        if (comp.compare(supp[mid - 1], supp[mid]) <= 0) {
            System.arraycopy(supp, from, a2, from, len);
            return;
        }
        int p2 = from;
        int q2 = mid;
        for (int i2 = from; i2 < to2; ++i2) {
            a2[i2] = q2 >= to2 || p2 < mid && comp.compare(supp[p2], supp[q2]) <= 0 ? supp[p2++] : supp[q2++];
        }
    }

    public static void mergeSort(byte[] a2, int from, int to2, ByteComparator comp) {
        ByteArrays.mergeSort(a2, from, to2, comp, (byte[])a2.clone());
    }

    public static void mergeSort(byte[] a2, ByteComparator comp) {
        ByteArrays.mergeSort(a2, 0, a2.length, comp);
    }

    public static int binarySearch(byte[] a2, int from, int to2, byte key) {
        --to2;
        while (from <= to2) {
            int mid = from + to2 >>> 1;
            byte midVal = a2[mid];
            if (midVal < key) {
                from = mid + 1;
                continue;
            }
            if (midVal > key) {
                to2 = mid - 1;
                continue;
            }
            return mid;
        }
        return -(from + 1);
    }

    public static int binarySearch(byte[] a2, byte key) {
        return ByteArrays.binarySearch(a2, 0, a2.length, key);
    }

    public static int binarySearch(byte[] a2, int from, int to2, byte key, ByteComparator c2) {
        --to2;
        while (from <= to2) {
            int mid = from + to2 >>> 1;
            byte midVal = a2[mid];
            int cmp = c2.compare(midVal, key);
            if (cmp < 0) {
                from = mid + 1;
                continue;
            }
            if (cmp > 0) {
                to2 = mid - 1;
                continue;
            }
            return mid;
        }
        return -(from + 1);
    }

    public static int binarySearch(byte[] a2, byte key, ByteComparator c2) {
        return ByteArrays.binarySearch(a2, 0, a2.length, key, c2);
    }

    public static void radixSort(byte[] a2) {
        ByteArrays.radixSort(a2, 0, a2.length);
    }

    public static void radixSort(byte[] a2, int from, int to2) {
        if (to2 - from < 1024) {
            ByteArrays.quickSort(a2, from, to2);
            return;
        }
        boolean maxLevel = false;
        boolean stackSize = true;
        int stackPos = 0;
        int[] offsetStack = new int[1];
        int[] lengthStack = new int[1];
        int[] levelStack = new int[1];
        offsetStack[stackPos] = from;
        lengthStack[stackPos] = to2 - from;
        levelStack[stackPos++] = 0;
        int[] count = new int[256];
        int[] pos = new int[256];
        while (stackPos > 0) {
            int first = offsetStack[--stackPos];
            int length = lengthStack[stackPos];
            int level = levelStack[stackPos];
            int signMask = level % 1 == 0 ? 128 : 0;
            int shift = (0 - level % 1) * 8;
            int i2 = first + length;
            while (i2-- != first) {
                int n2 = a2[i2] >>> shift & 0xFF ^ signMask;
                count[n2] = count[n2] + 1;
            }
            int lastUsed = -1;
            int p2 = first;
            for (int i3 = 0; i3 < 256; ++i3) {
                if (count[i3] != 0) {
                    lastUsed = i3;
                }
                pos[i3] = p2 += count[i3];
            }
            int end = first + length - count[lastUsed];
            int c2 = -1;
            for (int i4 = first; i4 <= end; i4 += count[c2]) {
                byte t2 = a2[i4];
                c2 = t2 >>> shift & 0xFF ^ signMask;
                if (i4 < end) {
                    while (true) {
                        int n3 = c2;
                        int n4 = pos[n3] - 1;
                        pos[n3] = n4;
                        int d2 = n4;
                        if (n4 <= i4) break;
                        byte z2 = t2;
                        t2 = a2[d2];
                        a2[d2] = z2;
                        c2 = t2 >>> shift & 0xFF ^ signMask;
                    }
                    a2[i4] = t2;
                }
                if (level < 0 && count[c2] > 1) {
                    if (count[c2] < 1024) {
                        ByteArrays.quickSort(a2, i4, i4 + count[c2]);
                    } else {
                        offsetStack[stackPos] = i4;
                        lengthStack[stackPos] = count[c2];
                        levelStack[stackPos++] = level + 1;
                    }
                }
                count[c2] = 0;
            }
        }
    }

    public static void parallelRadixSort(final byte[] a2, int from, int to2) {
        if (to2 - from < 1024) {
            ByteArrays.quickSort(a2, from, to2);
            return;
        }
        boolean maxLevel = false;
        final LinkedBlockingQueue<Segment> queue = new LinkedBlockingQueue<Segment>();
        queue.add(new Segment(from, to2 - from, 0));
        final AtomicInteger queueSize = new AtomicInteger(1);
        final int numberOfThreads = Runtime.getRuntime().availableProcessors();
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads, Executors.defaultThreadFactory());
        ExecutorCompletionService<Void> executorCompletionService = new ExecutorCompletionService<Void>(executorService);
        int i2 = numberOfThreads;
        while (i2-- != 0) {
            executorCompletionService.submit(new Callable<Void>(){

                @Override
                public Void call() throws Exception {
                    int[] count = new int[256];
                    int[] pos = new int[256];
                    while (true) {
                        Segment segment;
                        if (queueSize.get() == 0) {
                            int i2 = numberOfThreads;
                            while (i2-- != 0) {
                                queue.add(POISON_PILL);
                            }
                        }
                        if ((segment = (Segment)queue.take()) == POISON_PILL) {
                            return null;
                        }
                        int first = segment.offset;
                        int length = segment.length;
                        int level = segment.level;
                        int signMask = level % 1 == 0 ? 128 : 0;
                        int shift = (0 - level % 1) * 8;
                        int i3 = first + length;
                        while (i3-- != first) {
                            int n2 = a2[i3] >>> shift & 0xFF ^ signMask;
                            count[n2] = count[n2] + 1;
                        }
                        int lastUsed = -1;
                        int p2 = first;
                        for (int i4 = 0; i4 < 256; ++i4) {
                            if (count[i4] != 0) {
                                lastUsed = i4;
                            }
                            pos[i4] = p2 += count[i4];
                        }
                        int end = first + length - count[lastUsed];
                        int c2 = -1;
                        for (int i5 = first; i5 <= end; i5 += count[c2]) {
                            byte t2 = a2[i5];
                            c2 = t2 >>> shift & 0xFF ^ signMask;
                            if (i5 < end) {
                                while (true) {
                                    int n3 = c2;
                                    int n4 = pos[n3] - 1;
                                    pos[n3] = n4;
                                    int d2 = n4;
                                    if (n4 <= i5) break;
                                    byte z2 = t2;
                                    t2 = a2[d2];
                                    a2[d2] = z2;
                                    c2 = t2 >>> shift & 0xFF ^ signMask;
                                }
                                a2[i5] = t2;
                            }
                            if (level < 0 && count[c2] > 1) {
                                if (count[c2] < 1024) {
                                    ByteArrays.quickSort(a2, i5, i5 + count[c2]);
                                } else {
                                    queueSize.incrementAndGet();
                                    queue.add(new Segment(i5, count[c2], level + 1));
                                }
                            }
                            count[c2] = 0;
                        }
                        queueSize.decrementAndGet();
                    }
                }
            });
        }
        Throwable problem = null;
        int i3 = numberOfThreads;
        while (i3-- != 0) {
            try {
                executorCompletionService.take().get();
            }
            catch (Exception e2) {
                problem = e2.getCause();
            }
        }
        executorService.shutdown();
        if (problem != null) {
            throw problem instanceof RuntimeException ? (RuntimeException)problem : new RuntimeException(problem);
        }
    }

    public static void parallelRadixSort(byte[] a2) {
        ByteArrays.parallelRadixSort(a2, 0, a2.length);
    }

    public static void radixSortIndirect(int[] perm, byte[] a2, boolean stable) {
        ByteArrays.radixSortIndirect(perm, a2, 0, perm.length, stable);
    }

    public static void radixSortIndirect(int[] perm, byte[] a2, int from, int to2, boolean stable) {
        int[] support;
        if (to2 - from < 1024) {
            ByteArrays.insertionSortIndirect(perm, a2, from, to2);
            return;
        }
        boolean maxLevel = false;
        boolean stackSize = true;
        int stackPos = 0;
        int[] offsetStack = new int[1];
        int[] lengthStack = new int[1];
        int[] levelStack = new int[1];
        offsetStack[stackPos] = from;
        lengthStack[stackPos] = to2 - from;
        levelStack[stackPos++] = 0;
        int[] count = new int[256];
        int[] pos = new int[256];
        int[] arrn = support = stable ? new int[perm.length] : null;
        while (stackPos > 0) {
            int i2;
            int p2;
            int first = offsetStack[--stackPos];
            int length = lengthStack[stackPos];
            int level = levelStack[stackPos];
            int signMask = level % 1 == 0 ? 128 : 0;
            int shift = (0 - level % 1) * 8;
            int i3 = first + length;
            while (i3-- != first) {
                int n2 = a2[perm[i3]] >>> shift & 0xFF ^ signMask;
                count[n2] = count[n2] + 1;
            }
            int lastUsed = -1;
            int n3 = p2 = stable ? 0 : first;
            for (i2 = 0; i2 < 256; ++i2) {
                if (count[i2] != 0) {
                    lastUsed = i2;
                }
                pos[i2] = p2 += count[i2];
            }
            if (stable) {
                i2 = first + length;
                while (i2-- != first) {
                    int n4 = a2[perm[i2]] >>> shift & 0xFF ^ signMask;
                    int n5 = pos[n4] - 1;
                    pos[n4] = n5;
                    support[n5] = perm[i2];
                }
                System.arraycopy(support, 0, perm, first, length);
                p2 = first;
                for (i2 = 0; i2 <= lastUsed; ++i2) {
                    if (level < 0 && count[i2] > 1) {
                        if (count[i2] < 1024) {
                            ByteArrays.insertionSortIndirect(perm, a2, p2, p2 + count[i2]);
                        } else {
                            offsetStack[stackPos] = p2;
                            lengthStack[stackPos] = count[i2];
                            levelStack[stackPos++] = level + 1;
                        }
                    }
                    p2 += count[i2];
                }
                java.util.Arrays.fill(count, 0);
                continue;
            }
            int end = first + length - count[lastUsed];
            int c2 = -1;
            for (int i4 = first; i4 <= end; i4 += count[c2]) {
                int t2 = perm[i4];
                c2 = a2[t2] >>> shift & 0xFF ^ signMask;
                if (i4 < end) {
                    while (true) {
                        int n6 = c2;
                        int n7 = pos[n6] - 1;
                        pos[n6] = n7;
                        int d2 = n7;
                        if (n7 <= i4) break;
                        int z2 = t2;
                        t2 = perm[d2];
                        perm[d2] = z2;
                        c2 = a2[t2] >>> shift & 0xFF ^ signMask;
                    }
                    perm[i4] = t2;
                }
                if (level < 0 && count[c2] > 1) {
                    if (count[c2] < 1024) {
                        ByteArrays.insertionSortIndirect(perm, a2, i4, i4 + count[c2]);
                    } else {
                        offsetStack[stackPos] = i4;
                        lengthStack[stackPos] = count[c2];
                        levelStack[stackPos++] = level + 1;
                    }
                }
                count[c2] = 0;
            }
        }
    }

    public static void parallelRadixSortIndirect(final int[] perm, final byte[] a2, int from, int to2, final boolean stable) {
        if (to2 - from < 1024) {
            ByteArrays.radixSortIndirect(perm, a2, from, to2, stable);
            return;
        }
        boolean maxLevel = false;
        final LinkedBlockingQueue<Segment> queue = new LinkedBlockingQueue<Segment>();
        queue.add(new Segment(from, to2 - from, 0));
        final AtomicInteger queueSize = new AtomicInteger(1);
        final int numberOfThreads = Runtime.getRuntime().availableProcessors();
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads, Executors.defaultThreadFactory());
        ExecutorCompletionService<Void> executorCompletionService = new ExecutorCompletionService<Void>(executorService);
        final int[] support = stable ? new int[perm.length] : null;
        int i2 = numberOfThreads;
        while (i2-- != 0) {
            executorCompletionService.submit(new Callable<Void>(){

                @Override
                public Void call() throws Exception {
                    int[] count = new int[256];
                    int[] pos = new int[256];
                    while (true) {
                        int i2;
                        Segment segment;
                        if (queueSize.get() == 0) {
                            int i3 = numberOfThreads;
                            while (i3-- != 0) {
                                queue.add(POISON_PILL);
                            }
                        }
                        if ((segment = (Segment)queue.take()) == POISON_PILL) {
                            return null;
                        }
                        int first = segment.offset;
                        int length = segment.length;
                        int level = segment.level;
                        int signMask = level % 1 == 0 ? 128 : 0;
                        int shift = (0 - level % 1) * 8;
                        int i4 = first + length;
                        while (i4-- != first) {
                            int n2 = a2[perm[i4]] >>> shift & 0xFF ^ signMask;
                            count[n2] = count[n2] + 1;
                        }
                        int lastUsed = -1;
                        int p2 = first;
                        for (i2 = 0; i2 < 256; ++i2) {
                            if (count[i2] != 0) {
                                lastUsed = i2;
                            }
                            pos[i2] = p2 += count[i2];
                        }
                        if (stable) {
                            i2 = first + length;
                            while (i2-- != first) {
                                int n3 = a2[perm[i2]] >>> shift & 0xFF ^ signMask;
                                int n4 = pos[n3] - 1;
                                pos[n3] = n4;
                                support[n4] = perm[i2];
                            }
                            System.arraycopy(support, first, perm, first, length);
                            p2 = first;
                            for (i2 = 0; i2 <= lastUsed; ++i2) {
                                if (level < 0 && count[i2] > 1) {
                                    if (count[i2] < 1024) {
                                        ByteArrays.radixSortIndirect(perm, a2, p2, p2 + count[i2], stable);
                                    } else {
                                        queueSize.incrementAndGet();
                                        queue.add(new Segment(p2, count[i2], level + 1));
                                    }
                                }
                                p2 += count[i2];
                            }
                            java.util.Arrays.fill(count, 0);
                        } else {
                            int end = first + length - count[lastUsed];
                            int c2 = -1;
                            for (int i5 = first; i5 <= end; i5 += count[c2]) {
                                int t2 = perm[i5];
                                c2 = a2[t2] >>> shift & 0xFF ^ signMask;
                                if (i5 < end) {
                                    while (true) {
                                        int n5 = c2;
                                        int n6 = pos[n5] - 1;
                                        pos[n5] = n6;
                                        int d2 = n6;
                                        if (n6 <= i5) break;
                                        int z2 = t2;
                                        t2 = perm[d2];
                                        perm[d2] = z2;
                                        c2 = a2[t2] >>> shift & 0xFF ^ signMask;
                                    }
                                    perm[i5] = t2;
                                }
                                if (level < 0 && count[c2] > 1) {
                                    if (count[c2] < 1024) {
                                        ByteArrays.radixSortIndirect(perm, a2, i5, i5 + count[c2], stable);
                                    } else {
                                        queueSize.incrementAndGet();
                                        queue.add(new Segment(i5, count[c2], level + 1));
                                    }
                                }
                                count[c2] = 0;
                            }
                        }
                        queueSize.decrementAndGet();
                    }
                }
            });
        }
        Throwable problem = null;
        int i3 = numberOfThreads;
        while (i3-- != 0) {
            try {
                executorCompletionService.take().get();
            }
            catch (Exception e2) {
                problem = e2.getCause();
            }
        }
        executorService.shutdown();
        if (problem != null) {
            throw problem instanceof RuntimeException ? (RuntimeException)problem : new RuntimeException(problem);
        }
    }

    public static void parallelRadixSortIndirect(int[] perm, byte[] a2, boolean stable) {
        ByteArrays.parallelRadixSortIndirect(perm, a2, 0, a2.length, stable);
    }

    public static void radixSort(byte[] a2, byte[] b2) {
        ByteArrays.ensureSameLength(a2, b2);
        ByteArrays.radixSort(a2, b2, 0, a2.length);
    }

    public static void radixSort(byte[] a2, byte[] b2, int from, int to2) {
        if (to2 - from < 1024) {
            ByteArrays.selectionSort(a2, b2, from, to2);
            return;
        }
        int layers = 2;
        boolean maxLevel = true;
        int stackSize = 256;
        int stackPos = 0;
        int[] offsetStack = new int[256];
        int[] lengthStack = new int[256];
        int[] levelStack = new int[256];
        offsetStack[stackPos] = from;
        lengthStack[stackPos] = to2 - from;
        levelStack[stackPos++] = 0;
        int[] count = new int[256];
        int[] pos = new int[256];
        while (stackPos > 0) {
            int first = offsetStack[--stackPos];
            int length = lengthStack[stackPos];
            int level = levelStack[stackPos];
            int signMask = level % 1 == 0 ? 128 : 0;
            byte[] k2 = level < 1 ? a2 : b2;
            int shift = (0 - level % 1) * 8;
            int i2 = first + length;
            while (i2-- != first) {
                int n2 = k2[i2] >>> shift & 0xFF ^ signMask;
                count[n2] = count[n2] + 1;
            }
            int lastUsed = -1;
            int p2 = first;
            for (int i3 = 0; i3 < 256; ++i3) {
                if (count[i3] != 0) {
                    lastUsed = i3;
                }
                pos[i3] = p2 += count[i3];
            }
            int end = first + length - count[lastUsed];
            int c2 = -1;
            for (int i4 = first; i4 <= end; i4 += count[c2]) {
                byte t2 = a2[i4];
                byte u2 = b2[i4];
                c2 = k2[i4] >>> shift & 0xFF ^ signMask;
                if (i4 < end) {
                    while (true) {
                        int n3 = c2;
                        int n4 = pos[n3] - 1;
                        pos[n3] = n4;
                        int d2 = n4;
                        if (n4 <= i4) break;
                        c2 = k2[d2] >>> shift & 0xFF ^ signMask;
                        byte z2 = t2;
                        t2 = a2[d2];
                        a2[d2] = z2;
                        z2 = u2;
                        u2 = b2[d2];
                        b2[d2] = z2;
                    }
                    a2[i4] = t2;
                    b2[i4] = u2;
                }
                if (level < 1 && count[c2] > 1) {
                    if (count[c2] < 1024) {
                        ByteArrays.selectionSort(a2, b2, i4, i4 + count[c2]);
                    } else {
                        offsetStack[stackPos] = i4;
                        lengthStack[stackPos] = count[c2];
                        levelStack[stackPos++] = level + 1;
                    }
                }
                count[c2] = 0;
            }
        }
    }

    public static void parallelRadixSort(final byte[] a2, final byte[] b2, int from, int to2) {
        if (to2 - from < 1024) {
            ByteArrays.quickSort(a2, b2, from, to2);
            return;
        }
        int layers = 2;
        if (a2.length != b2.length) {
            throw new IllegalArgumentException("Array size mismatch.");
        }
        boolean maxLevel = true;
        final LinkedBlockingQueue<Segment> queue = new LinkedBlockingQueue<Segment>();
        queue.add(new Segment(from, to2 - from, 0));
        final AtomicInteger queueSize = new AtomicInteger(1);
        final int numberOfThreads = Runtime.getRuntime().availableProcessors();
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads, Executors.defaultThreadFactory());
        ExecutorCompletionService<Void> executorCompletionService = new ExecutorCompletionService<Void>(executorService);
        int i2 = numberOfThreads;
        while (i2-- != 0) {
            executorCompletionService.submit(new Callable<Void>(){

                @Override
                public Void call() throws Exception {
                    int[] count = new int[256];
                    int[] pos = new int[256];
                    while (true) {
                        Segment segment;
                        if (queueSize.get() == 0) {
                            int i2 = numberOfThreads;
                            while (i2-- != 0) {
                                queue.add(POISON_PILL);
                            }
                        }
                        if ((segment = (Segment)queue.take()) == POISON_PILL) {
                            return null;
                        }
                        int first = segment.offset;
                        int length = segment.length;
                        int level = segment.level;
                        int signMask = level % 1 == 0 ? 128 : 0;
                        byte[] k2 = level < 1 ? a2 : b2;
                        int shift = (0 - level % 1) * 8;
                        int i3 = first + length;
                        while (i3-- != first) {
                            int n2 = k2[i3] >>> shift & 0xFF ^ signMask;
                            count[n2] = count[n2] + 1;
                        }
                        int lastUsed = -1;
                        int p2 = first;
                        for (int i4 = 0; i4 < 256; ++i4) {
                            if (count[i4] != 0) {
                                lastUsed = i4;
                            }
                            pos[i4] = p2 += count[i4];
                        }
                        int end = first + length - count[lastUsed];
                        int c2 = -1;
                        for (int i5 = first; i5 <= end; i5 += count[c2]) {
                            byte t2 = a2[i5];
                            byte u2 = b2[i5];
                            c2 = k2[i5] >>> shift & 0xFF ^ signMask;
                            if (i5 < end) {
                                while (true) {
                                    int n3 = c2;
                                    int n4 = pos[n3] - 1;
                                    pos[n3] = n4;
                                    int d2 = n4;
                                    if (n4 <= i5) break;
                                    c2 = k2[d2] >>> shift & 0xFF ^ signMask;
                                    byte z2 = t2;
                                    byte w2 = u2;
                                    t2 = a2[d2];
                                    u2 = b2[d2];
                                    a2[d2] = z2;
                                    b2[d2] = w2;
                                }
                                a2[i5] = t2;
                                b2[i5] = u2;
                            }
                            if (level < 1 && count[c2] > 1) {
                                if (count[c2] < 1024) {
                                    ByteArrays.quickSort(a2, b2, i5, i5 + count[c2]);
                                } else {
                                    queueSize.incrementAndGet();
                                    queue.add(new Segment(i5, count[c2], level + 1));
                                }
                            }
                            count[c2] = 0;
                        }
                        queueSize.decrementAndGet();
                    }
                }
            });
        }
        Throwable problem = null;
        int i3 = numberOfThreads;
        while (i3-- != 0) {
            try {
                executorCompletionService.take().get();
            }
            catch (Exception e2) {
                problem = e2.getCause();
            }
        }
        executorService.shutdown();
        if (problem != null) {
            throw problem instanceof RuntimeException ? (RuntimeException)problem : new RuntimeException(problem);
        }
    }

    public static void parallelRadixSort(byte[] a2, byte[] b2) {
        ByteArrays.ensureSameLength(a2, b2);
        ByteArrays.parallelRadixSort(a2, b2, 0, a2.length);
    }

    private static void insertionSortIndirect(int[] perm, byte[] a2, byte[] b2, int from, int to2) {
        int i2 = from;
        while (++i2 < to2) {
            int t2 = perm[i2];
            int j2 = i2;
            int u2 = perm[j2 - 1];
            while (a2[t2] < a2[u2] || a2[t2] == a2[u2] && b2[t2] < b2[u2]) {
                perm[j2] = u2;
                if (from == j2 - 1) {
                    --j2;
                    break;
                }
                u2 = perm[--j2 - 1];
            }
            perm[j2] = t2;
        }
    }

    public static void radixSortIndirect(int[] perm, byte[] a2, byte[] b2, boolean stable) {
        ByteArrays.ensureSameLength(a2, b2);
        ByteArrays.radixSortIndirect(perm, a2, b2, 0, a2.length, stable);
    }

    public static void radixSortIndirect(int[] perm, byte[] a2, byte[] b2, int from, int to2, boolean stable) {
        int[] support;
        if (to2 - from < 1024) {
            ByteArrays.insertionSortIndirect(perm, a2, b2, from, to2);
            return;
        }
        int layers = 2;
        boolean maxLevel = true;
        int stackSize = 256;
        int stackPos = 0;
        int[] offsetStack = new int[256];
        int[] lengthStack = new int[256];
        int[] levelStack = new int[256];
        offsetStack[stackPos] = from;
        lengthStack[stackPos] = to2 - from;
        levelStack[stackPos++] = 0;
        int[] count = new int[256];
        int[] pos = new int[256];
        int[] arrn = support = stable ? new int[perm.length] : null;
        while (stackPos > 0) {
            int i2;
            int p2;
            int first = offsetStack[--stackPos];
            int length = lengthStack[stackPos];
            int level = levelStack[stackPos];
            int signMask = level % 1 == 0 ? 128 : 0;
            byte[] k2 = level < 1 ? a2 : b2;
            int shift = (0 - level % 1) * 8;
            int i3 = first + length;
            while (i3-- != first) {
                int n2 = k2[perm[i3]] >>> shift & 0xFF ^ signMask;
                count[n2] = count[n2] + 1;
            }
            int lastUsed = -1;
            int n3 = p2 = stable ? 0 : first;
            for (i2 = 0; i2 < 256; ++i2) {
                if (count[i2] != 0) {
                    lastUsed = i2;
                }
                pos[i2] = p2 += count[i2];
            }
            if (stable) {
                i2 = first + length;
                while (i2-- != first) {
                    int n4 = k2[perm[i2]] >>> shift & 0xFF ^ signMask;
                    int n5 = pos[n4] - 1;
                    pos[n4] = n5;
                    support[n5] = perm[i2];
                }
                System.arraycopy(support, 0, perm, first, length);
                p2 = first;
                for (i2 = 0; i2 < 256; ++i2) {
                    if (level < 1 && count[i2] > 1) {
                        if (count[i2] < 1024) {
                            ByteArrays.insertionSortIndirect(perm, a2, b2, p2, p2 + count[i2]);
                        } else {
                            offsetStack[stackPos] = p2;
                            lengthStack[stackPos] = count[i2];
                            levelStack[stackPos++] = level + 1;
                        }
                    }
                    p2 += count[i2];
                }
                java.util.Arrays.fill(count, 0);
                continue;
            }
            int end = first + length - count[lastUsed];
            int c2 = -1;
            for (int i4 = first; i4 <= end; i4 += count[c2]) {
                int t2 = perm[i4];
                c2 = k2[t2] >>> shift & 0xFF ^ signMask;
                if (i4 < end) {
                    while (true) {
                        int n6 = c2;
                        int n7 = pos[n6] - 1;
                        pos[n6] = n7;
                        int d2 = n7;
                        if (n7 <= i4) break;
                        int z2 = t2;
                        t2 = perm[d2];
                        perm[d2] = z2;
                        c2 = k2[t2] >>> shift & 0xFF ^ signMask;
                    }
                    perm[i4] = t2;
                }
                if (level < 1 && count[c2] > 1) {
                    if (count[c2] < 1024) {
                        ByteArrays.insertionSortIndirect(perm, a2, b2, i4, i4 + count[c2]);
                    } else {
                        offsetStack[stackPos] = i4;
                        lengthStack[stackPos] = count[c2];
                        levelStack[stackPos++] = level + 1;
                    }
                }
                count[c2] = 0;
            }
        }
    }

    private static void selectionSort(byte[][] a2, int from, int to2, int level) {
        int layers = a2.length;
        int firstLayer = level / 1;
        for (int i2 = from; i2 < to2 - 1; ++i2) {
            int m2 = i2;
            block1: for (int j2 = i2 + 1; j2 < to2; ++j2) {
                for (int p2 = firstLayer; p2 < layers; ++p2) {
                    if (a2[p2][j2] < a2[p2][m2]) {
                        m2 = j2;
                        continue block1;
                    }
                    if (a2[p2][j2] > a2[p2][m2]) continue block1;
                }
            }
            if (m2 == i2) continue;
            int p3 = layers;
            while (p3-- != 0) {
                byte u2 = a2[p3][i2];
                a2[p3][i2] = a2[p3][m2];
                a2[p3][m2] = u2;
            }
        }
    }

    public static void radixSort(byte[][] a2) {
        ByteArrays.radixSort(a2, 0, a2[0].length);
    }

    public static void radixSort(byte[][] a2, int from, int to2) {
        if (to2 - from < 1024) {
            ByteArrays.selectionSort(a2, from, to2, 0);
            return;
        }
        int layers = a2.length;
        int maxLevel = 1 * layers - 1;
        int p2 = layers;
        int l2 = a2[0].length;
        while (p2-- != 0) {
            if (a2[p2].length == l2) continue;
            throw new IllegalArgumentException("The array of index " + p2 + " has not the same length of the array of index 0.");
        }
        int stackSize = 255 * (layers * 1 - 1) + 1;
        int stackPos = 0;
        int[] offsetStack = new int[stackSize];
        int[] lengthStack = new int[stackSize];
        int[] levelStack = new int[stackSize];
        offsetStack[stackPos] = from;
        lengthStack[stackPos] = to2 - from;
        levelStack[stackPos++] = 0;
        int[] count = new int[256];
        int[] pos = new int[256];
        byte[] t2 = new byte[layers];
        while (stackPos > 0) {
            int first = offsetStack[--stackPos];
            int length = lengthStack[stackPos];
            int level = levelStack[stackPos];
            int signMask = level % 1 == 0 ? 128 : 0;
            byte[] k2 = a2[level / 1];
            int shift = (0 - level % 1) * 8;
            int i2 = first + length;
            while (i2-- != first) {
                int n2 = k2[i2] >>> shift & 0xFF ^ signMask;
                count[n2] = count[n2] + 1;
            }
            int lastUsed = -1;
            int p3 = first;
            for (int i3 = 0; i3 < 256; ++i3) {
                if (count[i3] != 0) {
                    lastUsed = i3;
                }
                pos[i3] = p3 += count[i3];
            }
            int end = first + length - count[lastUsed];
            int c2 = -1;
            for (int i4 = first; i4 <= end; i4 += count[c2]) {
                int p4 = layers;
                while (p4-- != 0) {
                    t2[p4] = a2[p4][i4];
                }
                c2 = k2[i4] >>> shift & 0xFF ^ signMask;
                if (i4 < end) {
                    block6: while (true) {
                        int n3 = c2;
                        int n4 = pos[n3] - 1;
                        pos[n3] = n4;
                        int d2 = n4;
                        if (n4 <= i4) break;
                        c2 = k2[d2] >>> shift & 0xFF ^ signMask;
                        p4 = layers;
                        while (true) {
                            if (p4-- == 0) continue block6;
                            byte u2 = t2[p4];
                            t2[p4] = a2[p4][d2];
                            a2[p4][d2] = u2;
                        }
                        break;
                    }
                    p4 = layers;
                    while (p4-- != 0) {
                        a2[p4][i4] = t2[p4];
                    }
                }
                if (level < maxLevel && count[c2] > 1) {
                    if (count[c2] < 1024) {
                        ByteArrays.selectionSort(a2, i4, i4 + count[c2], level + 1);
                    } else {
                        offsetStack[stackPos] = i4;
                        lengthStack[stackPos] = count[c2];
                        levelStack[stackPos++] = level + 1;
                    }
                }
                count[c2] = 0;
            }
        }
    }

    public static byte[] shuffle(byte[] a2, int from, int to2, Random random) {
        int i2 = to2 - from;
        while (i2-- != 0) {
            int p2 = random.nextInt(i2 + 1);
            byte t2 = a2[from + i2];
            a2[from + i2] = a2[from + p2];
            a2[from + p2] = t2;
        }
        return a2;
    }

    public static byte[] shuffle(byte[] a2, Random random) {
        int i2 = a2.length;
        while (i2-- != 0) {
            int p2 = random.nextInt(i2 + 1);
            byte t2 = a2[i2];
            a2[i2] = a2[p2];
            a2[p2] = t2;
        }
        return a2;
    }

    public static byte[] reverse(byte[] a2) {
        int length = a2.length;
        int i2 = length / 2;
        while (i2-- != 0) {
            byte t2 = a2[length - i2 - 1];
            a2[length - i2 - 1] = a2[i2];
            a2[i2] = t2;
        }
        return a2;
    }

    public static byte[] reverse(byte[] a2, int from, int to2) {
        int length = to2 - from;
        int i2 = length / 2;
        while (i2-- != 0) {
            byte t2 = a2[from + length - i2 - 1];
            a2[from + length - i2 - 1] = a2[from + i2];
            a2[from + i2] = t2;
        }
        return a2;
    }

    private static final class ArrayHashStrategy
    implements Hash.Strategy<byte[]>,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;

        private ArrayHashStrategy() {
        }

        @Override
        public int hashCode(byte[] o2) {
            return java.util.Arrays.hashCode(o2);
        }

        @Override
        public boolean equals(byte[] a2, byte[] b2) {
            return java.util.Arrays.equals(a2, b2);
        }
    }

    protected static final class Segment {
        protected final int offset;
        protected final int length;
        protected final int level;

        protected Segment(int offset, int length, int level) {
            this.offset = offset;
            this.length = length;
            this.level = level;
        }

        public String toString() {
            return "Segment [offset=" + this.offset + ", length=" + this.length + ", level=" + this.level + "]";
        }
    }

    protected static class ForkJoinQuickSort2
    extends RecursiveAction {
        private static final long serialVersionUID = 1L;
        private final int from;
        private final int to;
        private final byte[] x;
        private final byte[] y;

        public ForkJoinQuickSort2(byte[] x2, byte[] y2, int from, int to2) {
            this.from = from;
            this.to = to2;
            this.x = x2;
            this.y = y2;
        }

        @Override
        protected void compute() {
            int c2;
            int a2;
            byte[] x2 = this.x;
            byte[] y2 = this.y;
            int len = this.to - this.from;
            if (len < 8192) {
                ByteArrays.quickSort(x2, y2, this.from, this.to);
                return;
            }
            int m2 = this.from + len / 2;
            int l2 = this.from;
            int n2 = this.to - 1;
            int s2 = len / 8;
            l2 = ByteArrays.med3(x2, y2, l2, l2 + s2, l2 + 2 * s2);
            m2 = ByteArrays.med3(x2, y2, m2 - s2, m2, m2 + s2);
            n2 = ByteArrays.med3(x2, y2, n2 - 2 * s2, n2 - s2, n2);
            m2 = ByteArrays.med3(x2, y2, l2, m2, n2);
            byte v2 = x2[m2];
            byte w2 = y2[m2];
            int b2 = a2 = this.from;
            int d2 = c2 = this.to - 1;
            while (true) {
                int t2;
                int comparison;
                if (b2 <= c2 && (comparison = (t2 = Byte.compare(x2[b2], v2)) == 0 ? Byte.compare(y2[b2], w2) : t2) <= 0) {
                    if (comparison == 0) {
                        ByteArrays.swap(x2, y2, a2++, b2);
                    }
                    ++b2;
                    continue;
                }
                while (c2 >= b2 && (comparison = (t2 = Byte.compare(x2[c2], v2)) == 0 ? Byte.compare(y2[c2], w2) : t2) >= 0) {
                    if (comparison == 0) {
                        ByteArrays.swap(x2, y2, c2, d2--);
                    }
                    --c2;
                }
                if (b2 > c2) break;
                ByteArrays.swap(x2, y2, b2++, c2--);
            }
            s2 = Math.min(a2 - this.from, b2 - a2);
            ByteArrays.swap(x2, y2, this.from, b2 - s2, s2);
            s2 = Math.min(d2 - c2, this.to - d2 - 1);
            ByteArrays.swap(x2, y2, b2, this.to - s2, s2);
            s2 = b2 - a2;
            int t3 = d2 - c2;
            if (s2 > 1 && t3 > 1) {
                ForkJoinQuickSort2.invokeAll(new ForkJoinQuickSort2(x2, y2, this.from, this.from + s2), new ForkJoinQuickSort2(x2, y2, this.to - t3, this.to));
            } else if (s2 > 1) {
                ForkJoinQuickSort2.invokeAll(new ForkJoinQuickSort2(x2, y2, this.from, this.from + s2));
            } else {
                ForkJoinQuickSort2.invokeAll(new ForkJoinQuickSort2(x2, y2, this.to - t3, this.to));
            }
        }
    }

    protected static class ForkJoinQuickSortIndirect
    extends RecursiveAction {
        private static final long serialVersionUID = 1L;
        private final int from;
        private final int to;
        private final int[] perm;
        private final byte[] x;

        public ForkJoinQuickSortIndirect(int[] perm, byte[] x2, int from, int to2) {
            this.from = from;
            this.to = to2;
            this.x = x2;
            this.perm = perm;
        }

        @Override
        protected void compute() {
            int c2;
            int a2;
            byte[] x2 = this.x;
            int len = this.to - this.from;
            if (len < 8192) {
                ByteArrays.quickSortIndirect(this.perm, x2, this.from, this.to);
                return;
            }
            int m2 = this.from + len / 2;
            int l2 = this.from;
            int n2 = this.to - 1;
            int s2 = len / 8;
            l2 = ByteArrays.med3Indirect(this.perm, x2, l2, l2 + s2, l2 + 2 * s2);
            m2 = ByteArrays.med3Indirect(this.perm, x2, m2 - s2, m2, m2 + s2);
            n2 = ByteArrays.med3Indirect(this.perm, x2, n2 - 2 * s2, n2 - s2, n2);
            m2 = ByteArrays.med3Indirect(this.perm, x2, l2, m2, n2);
            byte v2 = x2[this.perm[m2]];
            int b2 = a2 = this.from;
            int d2 = c2 = this.to - 1;
            while (true) {
                int comparison;
                if (b2 <= c2 && (comparison = Byte.compare(x2[this.perm[b2]], v2)) <= 0) {
                    if (comparison == 0) {
                        IntArrays.swap(this.perm, a2++, b2);
                    }
                    ++b2;
                    continue;
                }
                while (c2 >= b2 && (comparison = Byte.compare(x2[this.perm[c2]], v2)) >= 0) {
                    if (comparison == 0) {
                        IntArrays.swap(this.perm, c2, d2--);
                    }
                    --c2;
                }
                if (b2 > c2) break;
                IntArrays.swap(this.perm, b2++, c2--);
            }
            s2 = Math.min(a2 - this.from, b2 - a2);
            IntArrays.swap(this.perm, this.from, b2 - s2, s2);
            s2 = Math.min(d2 - c2, this.to - d2 - 1);
            IntArrays.swap(this.perm, b2, this.to - s2, s2);
            s2 = b2 - a2;
            int t2 = d2 - c2;
            if (s2 > 1 && t2 > 1) {
                ForkJoinQuickSortIndirect.invokeAll(new ForkJoinQuickSortIndirect(this.perm, x2, this.from, this.from + s2), new ForkJoinQuickSortIndirect(this.perm, x2, this.to - t2, this.to));
            } else if (s2 > 1) {
                ForkJoinQuickSortIndirect.invokeAll(new ForkJoinQuickSortIndirect(this.perm, x2, this.from, this.from + s2));
            } else {
                ForkJoinQuickSortIndirect.invokeAll(new ForkJoinQuickSortIndirect(this.perm, x2, this.to - t2, this.to));
            }
        }
    }

    protected static class ForkJoinQuickSort
    extends RecursiveAction {
        private static final long serialVersionUID = 1L;
        private final int from;
        private final int to;
        private final byte[] x;

        public ForkJoinQuickSort(byte[] x2, int from, int to2) {
            this.from = from;
            this.to = to2;
            this.x = x2;
        }

        @Override
        protected void compute() {
            int c2;
            int a2;
            byte[] x2 = this.x;
            int len = this.to - this.from;
            if (len < 8192) {
                ByteArrays.quickSort(x2, this.from, this.to);
                return;
            }
            int m2 = this.from + len / 2;
            int l2 = this.from;
            int n2 = this.to - 1;
            int s2 = len / 8;
            l2 = ByteArrays.med3(x2, l2, l2 + s2, l2 + 2 * s2);
            m2 = ByteArrays.med3(x2, m2 - s2, m2, m2 + s2);
            n2 = ByteArrays.med3(x2, n2 - 2 * s2, n2 - s2, n2);
            m2 = ByteArrays.med3(x2, l2, m2, n2);
            byte v2 = x2[m2];
            int b2 = a2 = this.from;
            int d2 = c2 = this.to - 1;
            while (true) {
                int comparison;
                if (b2 <= c2 && (comparison = Byte.compare(x2[b2], v2)) <= 0) {
                    if (comparison == 0) {
                        ByteArrays.swap(x2, a2++, b2);
                    }
                    ++b2;
                    continue;
                }
                while (c2 >= b2 && (comparison = Byte.compare(x2[c2], v2)) >= 0) {
                    if (comparison == 0) {
                        ByteArrays.swap(x2, c2, d2--);
                    }
                    --c2;
                }
                if (b2 > c2) break;
                ByteArrays.swap(x2, b2++, c2--);
            }
            s2 = Math.min(a2 - this.from, b2 - a2);
            ByteArrays.swap(x2, this.from, b2 - s2, s2);
            s2 = Math.min(d2 - c2, this.to - d2 - 1);
            ByteArrays.swap(x2, b2, this.to - s2, s2);
            s2 = b2 - a2;
            int t2 = d2 - c2;
            if (s2 > 1 && t2 > 1) {
                ForkJoinQuickSort.invokeAll(new ForkJoinQuickSort(x2, this.from, this.from + s2), new ForkJoinQuickSort(x2, this.to - t2, this.to));
            } else if (s2 > 1) {
                ForkJoinQuickSort.invokeAll(new ForkJoinQuickSort(x2, this.from, this.from + s2));
            } else {
                ForkJoinQuickSort.invokeAll(new ForkJoinQuickSort(x2, this.to - t2, this.to));
            }
        }
    }

    protected static class ForkJoinQuickSortComp
    extends RecursiveAction {
        private static final long serialVersionUID = 1L;
        private final int from;
        private final int to;
        private final byte[] x;
        private final ByteComparator comp;

        public ForkJoinQuickSortComp(byte[] x2, int from, int to2, ByteComparator comp) {
            this.from = from;
            this.to = to2;
            this.x = x2;
            this.comp = comp;
        }

        @Override
        protected void compute() {
            int c2;
            int a2;
            byte[] x2 = this.x;
            int len = this.to - this.from;
            if (len < 8192) {
                ByteArrays.quickSort(x2, this.from, this.to, this.comp);
                return;
            }
            int m2 = this.from + len / 2;
            int l2 = this.from;
            int n2 = this.to - 1;
            int s2 = len / 8;
            l2 = ByteArrays.med3(x2, l2, l2 + s2, l2 + 2 * s2, this.comp);
            m2 = ByteArrays.med3(x2, m2 - s2, m2, m2 + s2, this.comp);
            n2 = ByteArrays.med3(x2, n2 - 2 * s2, n2 - s2, n2, this.comp);
            m2 = ByteArrays.med3(x2, l2, m2, n2, this.comp);
            byte v2 = x2[m2];
            int b2 = a2 = this.from;
            int d2 = c2 = this.to - 1;
            while (true) {
                int comparison;
                if (b2 <= c2 && (comparison = this.comp.compare(x2[b2], v2)) <= 0) {
                    if (comparison == 0) {
                        ByteArrays.swap(x2, a2++, b2);
                    }
                    ++b2;
                    continue;
                }
                while (c2 >= b2 && (comparison = this.comp.compare(x2[c2], v2)) >= 0) {
                    if (comparison == 0) {
                        ByteArrays.swap(x2, c2, d2--);
                    }
                    --c2;
                }
                if (b2 > c2) break;
                ByteArrays.swap(x2, b2++, c2--);
            }
            s2 = Math.min(a2 - this.from, b2 - a2);
            ByteArrays.swap(x2, this.from, b2 - s2, s2);
            s2 = Math.min(d2 - c2, this.to - d2 - 1);
            ByteArrays.swap(x2, b2, this.to - s2, s2);
            s2 = b2 - a2;
            int t2 = d2 - c2;
            if (s2 > 1 && t2 > 1) {
                ForkJoinQuickSortComp.invokeAll(new ForkJoinQuickSortComp(x2, this.from, this.from + s2, this.comp), new ForkJoinQuickSortComp(x2, this.to - t2, this.to, this.comp));
            } else if (s2 > 1) {
                ForkJoinQuickSortComp.invokeAll(new ForkJoinQuickSortComp(x2, this.from, this.from + s2, this.comp));
            } else {
                ForkJoinQuickSortComp.invokeAll(new ForkJoinQuickSortComp(x2, this.to - t2, this.to, this.comp));
            }
        }
    }
}

