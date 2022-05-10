package it.unimi.dsi.fastutil.booleans;

import it.unimi.dsi.fastutil.Arrays;
import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.booleans.BooleanComparator;
import it.unimi.dsi.fastutil.ints.IntArrays;
import java.io.Serializable;
import java.util.Random;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

public class BooleanArrays {
    public static final boolean[] EMPTY_ARRAY = new boolean[0];
    private static final int QUICKSORT_NO_REC = 16;
    private static final int PARALLEL_QUICKSORT_NO_FORK = 8192;
    private static final int QUICKSORT_MEDIAN_OF_9 = 128;
    private static final int MERGESORT_NO_REC = 16;
    public static final Hash.Strategy<boolean[]> HASH_STRATEGY = new ArrayHashStrategy();

    private BooleanArrays() {
    }

    public static boolean[] ensureCapacity(boolean[] array, int length) {
        if (length > array.length) {
            boolean[] t2 = new boolean[length];
            System.arraycopy(array, 0, t2, 0, array.length);
            return t2;
        }
        return array;
    }

    public static boolean[] ensureCapacity(boolean[] array, int length, int preserve) {
        if (length > array.length) {
            boolean[] t2 = new boolean[length];
            System.arraycopy(array, 0, t2, 0, preserve);
            return t2;
        }
        return array;
    }

    public static boolean[] grow(boolean[] array, int length) {
        if (length > array.length) {
            int newLength = (int)Math.max(Math.min(2L * (long)array.length, 0x7FFFFFF7L), (long)length);
            boolean[] t2 = new boolean[newLength];
            System.arraycopy(array, 0, t2, 0, array.length);
            return t2;
        }
        return array;
    }

    public static boolean[] grow(boolean[] array, int length, int preserve) {
        if (length > array.length) {
            int newLength = (int)Math.max(Math.min(2L * (long)array.length, 0x7FFFFFF7L), (long)length);
            boolean[] t2 = new boolean[newLength];
            System.arraycopy(array, 0, t2, 0, preserve);
            return t2;
        }
        return array;
    }

    public static boolean[] trim(boolean[] array, int length) {
        if (length >= array.length) {
            return array;
        }
        boolean[] t2 = length == 0 ? EMPTY_ARRAY : new boolean[length];
        System.arraycopy(array, 0, t2, 0, length);
        return t2;
    }

    public static boolean[] setLength(boolean[] array, int length) {
        if (length == array.length) {
            return array;
        }
        if (length < array.length) {
            return BooleanArrays.trim(array, length);
        }
        return BooleanArrays.ensureCapacity(array, length);
    }

    public static boolean[] copy(boolean[] array, int offset, int length) {
        BooleanArrays.ensureOffsetLength(array, offset, length);
        boolean[] a2 = length == 0 ? EMPTY_ARRAY : new boolean[length];
        System.arraycopy(array, offset, a2, 0, length);
        return a2;
    }

    public static boolean[] copy(boolean[] array) {
        return (boolean[])array.clone();
    }

    @Deprecated
    public static void fill(boolean[] array, boolean value) {
        int i2 = array.length;
        while (i2-- != 0) {
            array[i2] = value;
        }
    }

    @Deprecated
    public static void fill(boolean[] array, int from, int to2, boolean value) {
        BooleanArrays.ensureFromTo(array, from, to2);
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
    public static boolean equals(boolean[] a1, boolean[] a2) {
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

    public static void ensureFromTo(boolean[] a2, int from, int to2) {
        Arrays.ensureFromTo(a2.length, from, to2);
    }

    public static void ensureOffsetLength(boolean[] a2, int offset, int length) {
        Arrays.ensureOffsetLength(a2.length, offset, length);
    }

    public static void ensureSameLength(boolean[] a2, boolean[] b2) {
        if (a2.length != b2.length) {
            throw new IllegalArgumentException("Array size mismatch: " + a2.length + " != " + b2.length);
        }
    }

    public static void swap(boolean[] x2, int a2, int b2) {
        boolean t2 = x2[a2];
        x2[a2] = x2[b2];
        x2[b2] = t2;
    }

    public static void swap(boolean[] x2, int a2, int b2, int n2) {
        int i2 = 0;
        while (i2 < n2) {
            BooleanArrays.swap(x2, a2, b2);
            ++i2;
            ++a2;
            ++b2;
        }
    }

    private static int med3(boolean[] x2, int a2, int b2, int c2, BooleanComparator comp) {
        int ab2 = comp.compare(x2[a2], x2[b2]);
        int ac2 = comp.compare(x2[a2], x2[c2]);
        int bc2 = comp.compare(x2[b2], x2[c2]);
        return ab2 < 0 ? (bc2 < 0 ? b2 : (ac2 < 0 ? c2 : a2)) : (bc2 > 0 ? b2 : (ac2 > 0 ? c2 : a2));
    }

    private static void selectionSort(boolean[] a2, int from, int to2, BooleanComparator comp) {
        for (int i2 = from; i2 < to2 - 1; ++i2) {
            int m2 = i2;
            for (int j2 = i2 + 1; j2 < to2; ++j2) {
                if (comp.compare(a2[j2], a2[m2]) >= 0) continue;
                m2 = j2;
            }
            if (m2 == i2) continue;
            boolean u2 = a2[i2];
            a2[i2] = a2[m2];
            a2[m2] = u2;
        }
    }

    private static void insertionSort(boolean[] a2, int from, int to2, BooleanComparator comp) {
        int i2 = from;
        while (++i2 < to2) {
            boolean t2 = a2[i2];
            int j2 = i2;
            boolean u2 = a2[j2 - 1];
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

    public static void quickSort(boolean[] x2, int from, int to2, BooleanComparator comp) {
        int c2;
        int a2;
        int len = to2 - from;
        if (len < 16) {
            BooleanArrays.selectionSort(x2, from, to2, comp);
            return;
        }
        int m2 = from + len / 2;
        int l2 = from;
        int n2 = to2 - 1;
        if (len > 128) {
            int s2 = len / 8;
            l2 = BooleanArrays.med3(x2, l2, l2 + s2, l2 + 2 * s2, comp);
            m2 = BooleanArrays.med3(x2, m2 - s2, m2, m2 + s2, comp);
            n2 = BooleanArrays.med3(x2, n2 - 2 * s2, n2 - s2, n2, comp);
        }
        m2 = BooleanArrays.med3(x2, l2, m2, n2, comp);
        boolean v2 = x2[m2];
        int b2 = a2 = from;
        int d2 = c2 = to2 - 1;
        while (true) {
            int comparison;
            if (b2 <= c2 && (comparison = comp.compare(x2[b2], v2)) <= 0) {
                if (comparison == 0) {
                    BooleanArrays.swap(x2, a2++, b2);
                }
                ++b2;
                continue;
            }
            while (c2 >= b2 && (comparison = comp.compare(x2[c2], v2)) >= 0) {
                if (comparison == 0) {
                    BooleanArrays.swap(x2, c2, d2--);
                }
                --c2;
            }
            if (b2 > c2) break;
            BooleanArrays.swap(x2, b2++, c2--);
        }
        int s3 = Math.min(a2 - from, b2 - a2);
        BooleanArrays.swap(x2, from, b2 - s3, s3);
        s3 = Math.min(d2 - c2, to2 - d2 - 1);
        BooleanArrays.swap(x2, b2, to2 - s3, s3);
        s3 = b2 - a2;
        if (s3 > 1) {
            BooleanArrays.quickSort(x2, from, from + s3, comp);
        }
        if ((s3 = d2 - c2) > 1) {
            BooleanArrays.quickSort(x2, to2 - s3, to2, comp);
        }
    }

    public static void quickSort(boolean[] x2, BooleanComparator comp) {
        BooleanArrays.quickSort(x2, 0, x2.length, comp);
    }

    public static void parallelQuickSort(boolean[] x2, int from, int to2, BooleanComparator comp) {
        if (to2 - from < 8192) {
            BooleanArrays.quickSort(x2, from, to2, comp);
        } else {
            ForkJoinPool pool = new ForkJoinPool(Runtime.getRuntime().availableProcessors());
            pool.invoke(new ForkJoinQuickSortComp(x2, from, to2, comp));
            pool.shutdown();
        }
    }

    public static void parallelQuickSort(boolean[] x2, BooleanComparator comp) {
        BooleanArrays.parallelQuickSort(x2, 0, x2.length, comp);
    }

    private static int med3(boolean[] x2, int a2, int b2, int c2) {
        int ab2 = Boolean.compare(x2[a2], x2[b2]);
        int ac2 = Boolean.compare(x2[a2], x2[c2]);
        int bc2 = Boolean.compare(x2[b2], x2[c2]);
        return ab2 < 0 ? (bc2 < 0 ? b2 : (ac2 < 0 ? c2 : a2)) : (bc2 > 0 ? b2 : (ac2 > 0 ? c2 : a2));
    }

    private static void selectionSort(boolean[] a2, int from, int to2) {
        for (int i2 = from; i2 < to2 - 1; ++i2) {
            int m2 = i2;
            for (int j2 = i2 + 1; j2 < to2; ++j2) {
                if (a2[j2] || !a2[m2]) continue;
                m2 = j2;
            }
            if (m2 == i2) continue;
            boolean u2 = a2[i2];
            a2[i2] = a2[m2];
            a2[m2] = u2;
        }
    }

    private static void insertionSort(boolean[] a2, int from, int to2) {
        int i2 = from;
        while (++i2 < to2) {
            boolean t2 = a2[i2];
            int j2 = i2;
            boolean u2 = a2[j2 - 1];
            while (!t2 && u2) {
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

    public static void quickSort(boolean[] x2, int from, int to2) {
        int c2;
        int a2;
        int len = to2 - from;
        if (len < 16) {
            BooleanArrays.selectionSort(x2, from, to2);
            return;
        }
        int m2 = from + len / 2;
        int l2 = from;
        int n2 = to2 - 1;
        if (len > 128) {
            int s2 = len / 8;
            l2 = BooleanArrays.med3(x2, l2, l2 + s2, l2 + 2 * s2);
            m2 = BooleanArrays.med3(x2, m2 - s2, m2, m2 + s2);
            n2 = BooleanArrays.med3(x2, n2 - 2 * s2, n2 - s2, n2);
        }
        m2 = BooleanArrays.med3(x2, l2, m2, n2);
        boolean v2 = x2[m2];
        int b2 = a2 = from;
        int d2 = c2 = to2 - 1;
        while (true) {
            int comparison;
            if (b2 <= c2 && (comparison = Boolean.compare(x2[b2], v2)) <= 0) {
                if (comparison == 0) {
                    BooleanArrays.swap(x2, a2++, b2);
                }
                ++b2;
                continue;
            }
            while (c2 >= b2 && (comparison = Boolean.compare(x2[c2], v2)) >= 0) {
                if (comparison == 0) {
                    BooleanArrays.swap(x2, c2, d2--);
                }
                --c2;
            }
            if (b2 > c2) break;
            BooleanArrays.swap(x2, b2++, c2--);
        }
        int s3 = Math.min(a2 - from, b2 - a2);
        BooleanArrays.swap(x2, from, b2 - s3, s3);
        s3 = Math.min(d2 - c2, to2 - d2 - 1);
        BooleanArrays.swap(x2, b2, to2 - s3, s3);
        s3 = b2 - a2;
        if (s3 > 1) {
            BooleanArrays.quickSort(x2, from, from + s3);
        }
        if ((s3 = d2 - c2) > 1) {
            BooleanArrays.quickSort(x2, to2 - s3, to2);
        }
    }

    public static void quickSort(boolean[] x2) {
        BooleanArrays.quickSort(x2, 0, x2.length);
    }

    public static void parallelQuickSort(boolean[] x2, int from, int to2) {
        if (to2 - from < 8192) {
            BooleanArrays.quickSort(x2, from, to2);
        } else {
            ForkJoinPool pool = new ForkJoinPool(Runtime.getRuntime().availableProcessors());
            pool.invoke(new ForkJoinQuickSort(x2, from, to2));
            pool.shutdown();
        }
    }

    public static void parallelQuickSort(boolean[] x2) {
        BooleanArrays.parallelQuickSort(x2, 0, x2.length);
    }

    private static int med3Indirect(int[] perm, boolean[] x2, int a2, int b2, int c2) {
        boolean aa2 = x2[perm[a2]];
        boolean bb2 = x2[perm[b2]];
        boolean cc2 = x2[perm[c2]];
        int ab2 = Boolean.compare(aa2, bb2);
        int ac2 = Boolean.compare(aa2, cc2);
        int bc2 = Boolean.compare(bb2, cc2);
        return ab2 < 0 ? (bc2 < 0 ? b2 : (ac2 < 0 ? c2 : a2)) : (bc2 > 0 ? b2 : (ac2 > 0 ? c2 : a2));
    }

    private static void insertionSortIndirect(int[] perm, boolean[] a2, int from, int to2) {
        int i2 = from;
        while (++i2 < to2) {
            int t2 = perm[i2];
            int j2 = i2;
            int u2 = perm[j2 - 1];
            while (!a2[t2] && a2[u2]) {
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

    public static void quickSortIndirect(int[] perm, boolean[] x2, int from, int to2) {
        int c2;
        int a2;
        int len = to2 - from;
        if (len < 16) {
            BooleanArrays.insertionSortIndirect(perm, x2, from, to2);
            return;
        }
        int m2 = from + len / 2;
        int l2 = from;
        int n2 = to2 - 1;
        if (len > 128) {
            int s2 = len / 8;
            l2 = BooleanArrays.med3Indirect(perm, x2, l2, l2 + s2, l2 + 2 * s2);
            m2 = BooleanArrays.med3Indirect(perm, x2, m2 - s2, m2, m2 + s2);
            n2 = BooleanArrays.med3Indirect(perm, x2, n2 - 2 * s2, n2 - s2, n2);
        }
        m2 = BooleanArrays.med3Indirect(perm, x2, l2, m2, n2);
        boolean v2 = x2[perm[m2]];
        int b2 = a2 = from;
        int d2 = c2 = to2 - 1;
        while (true) {
            int comparison;
            if (b2 <= c2 && (comparison = Boolean.compare(x2[perm[b2]], v2)) <= 0) {
                if (comparison == 0) {
                    IntArrays.swap(perm, a2++, b2);
                }
                ++b2;
                continue;
            }
            while (c2 >= b2 && (comparison = Boolean.compare(x2[perm[c2]], v2)) >= 0) {
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
            BooleanArrays.quickSortIndirect(perm, x2, from, from + s3);
        }
        if ((s3 = d2 - c2) > 1) {
            BooleanArrays.quickSortIndirect(perm, x2, to2 - s3, to2);
        }
    }

    public static void quickSortIndirect(int[] perm, boolean[] x2) {
        BooleanArrays.quickSortIndirect(perm, x2, 0, x2.length);
    }

    public static void parallelQuickSortIndirect(int[] perm, boolean[] x2, int from, int to2) {
        if (to2 - from < 8192) {
            BooleanArrays.quickSortIndirect(perm, x2, from, to2);
        } else {
            ForkJoinPool pool = new ForkJoinPool(Runtime.getRuntime().availableProcessors());
            pool.invoke(new ForkJoinQuickSortIndirect(perm, x2, from, to2));
            pool.shutdown();
        }
    }

    public static void parallelQuickSortIndirect(int[] perm, boolean[] x2) {
        BooleanArrays.parallelQuickSortIndirect(perm, x2, 0, x2.length);
    }

    public static void stabilize(int[] perm, boolean[] x2, int from, int to2) {
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

    public static void stabilize(int[] perm, boolean[] x2) {
        BooleanArrays.stabilize(perm, x2, 0, perm.length);
    }

    private static int med3(boolean[] x2, boolean[] y2, int a2, int b2, int c2) {
        int bc2;
        int t2 = Boolean.compare(x2[a2], x2[b2]);
        int ab2 = t2 == 0 ? Boolean.compare(y2[a2], y2[b2]) : t2;
        t2 = Boolean.compare(x2[a2], x2[c2]);
        int ac2 = t2 == 0 ? Boolean.compare(y2[a2], y2[c2]) : t2;
        t2 = Boolean.compare(x2[b2], x2[c2]);
        int n2 = bc2 = t2 == 0 ? Boolean.compare(y2[b2], y2[c2]) : t2;
        return ab2 < 0 ? (bc2 < 0 ? b2 : (ac2 < 0 ? c2 : a2)) : (bc2 > 0 ? b2 : (ac2 > 0 ? c2 : a2));
    }

    private static void swap(boolean[] x2, boolean[] y2, int a2, int b2) {
        boolean t2 = x2[a2];
        boolean u2 = y2[a2];
        x2[a2] = x2[b2];
        y2[a2] = y2[b2];
        x2[b2] = t2;
        y2[b2] = u2;
    }

    private static void swap(boolean[] x2, boolean[] y2, int a2, int b2, int n2) {
        int i2 = 0;
        while (i2 < n2) {
            BooleanArrays.swap(x2, y2, a2, b2);
            ++i2;
            ++a2;
            ++b2;
        }
    }

    private static void selectionSort(boolean[] a2, boolean[] b2, int from, int to2) {
        for (int i2 = from; i2 < to2 - 1; ++i2) {
            int m2 = i2;
            for (int j2 = i2 + 1; j2 < to2; ++j2) {
                int u2 = Boolean.compare(a2[j2], a2[m2]);
                if (u2 >= 0 && (u2 != 0 || b2[j2] || !b2[m2])) continue;
                m2 = j2;
            }
            if (m2 == i2) continue;
            boolean t2 = a2[i2];
            a2[i2] = a2[m2];
            a2[m2] = t2;
            t2 = b2[i2];
            b2[i2] = b2[m2];
            b2[m2] = t2;
        }
    }

    public static void quickSort(boolean[] x2, boolean[] y2, int from, int to2) {
        int c2;
        int a2;
        int len = to2 - from;
        if (len < 16) {
            BooleanArrays.selectionSort(x2, y2, from, to2);
            return;
        }
        int m2 = from + len / 2;
        int l2 = from;
        int n2 = to2 - 1;
        if (len > 128) {
            int s2 = len / 8;
            l2 = BooleanArrays.med3(x2, y2, l2, l2 + s2, l2 + 2 * s2);
            m2 = BooleanArrays.med3(x2, y2, m2 - s2, m2, m2 + s2);
            n2 = BooleanArrays.med3(x2, y2, n2 - 2 * s2, n2 - s2, n2);
        }
        m2 = BooleanArrays.med3(x2, y2, l2, m2, n2);
        boolean v2 = x2[m2];
        boolean w2 = y2[m2];
        int b2 = a2 = from;
        int d2 = c2 = to2 - 1;
        while (true) {
            int t2;
            int comparison;
            if (b2 <= c2 && (comparison = (t2 = Boolean.compare(x2[b2], v2)) == 0 ? Boolean.compare(y2[b2], w2) : t2) <= 0) {
                if (comparison == 0) {
                    BooleanArrays.swap(x2, y2, a2++, b2);
                }
                ++b2;
                continue;
            }
            while (c2 >= b2 && (comparison = (t2 = Boolean.compare(x2[c2], v2)) == 0 ? Boolean.compare(y2[c2], w2) : t2) >= 0) {
                if (comparison == 0) {
                    BooleanArrays.swap(x2, y2, c2, d2--);
                }
                --c2;
            }
            if (b2 > c2) break;
            BooleanArrays.swap(x2, y2, b2++, c2--);
        }
        int s3 = Math.min(a2 - from, b2 - a2);
        BooleanArrays.swap(x2, y2, from, b2 - s3, s3);
        s3 = Math.min(d2 - c2, to2 - d2 - 1);
        BooleanArrays.swap(x2, y2, b2, to2 - s3, s3);
        s3 = b2 - a2;
        if (s3 > 1) {
            BooleanArrays.quickSort(x2, y2, from, from + s3);
        }
        if ((s3 = d2 - c2) > 1) {
            BooleanArrays.quickSort(x2, y2, to2 - s3, to2);
        }
    }

    public static void quickSort(boolean[] x2, boolean[] y2) {
        BooleanArrays.ensureSameLength(x2, y2);
        BooleanArrays.quickSort(x2, y2, 0, x2.length);
    }

    public static void parallelQuickSort(boolean[] x2, boolean[] y2, int from, int to2) {
        if (to2 - from < 8192) {
            BooleanArrays.quickSort(x2, y2, from, to2);
        }
        ForkJoinPool pool = new ForkJoinPool(Runtime.getRuntime().availableProcessors());
        pool.invoke(new ForkJoinQuickSort2(x2, y2, from, to2));
        pool.shutdown();
    }

    public static void parallelQuickSort(boolean[] x2, boolean[] y2) {
        BooleanArrays.ensureSameLength(x2, y2);
        BooleanArrays.parallelQuickSort(x2, y2, 0, x2.length);
    }

    public static void mergeSort(boolean[] a2, int from, int to2, boolean[] supp) {
        int len = to2 - from;
        if (len < 16) {
            BooleanArrays.insertionSort(a2, from, to2);
            return;
        }
        int mid = from + to2 >>> 1;
        BooleanArrays.mergeSort(supp, from, mid, a2);
        BooleanArrays.mergeSort(supp, mid, to2, a2);
        if (!supp[mid - 1] || supp[mid]) {
            System.arraycopy(supp, from, a2, from, len);
            return;
        }
        int p2 = from;
        int q2 = mid;
        for (int i2 = from; i2 < to2; ++i2) {
            a2[i2] = q2 >= to2 || p2 < mid && (!supp[p2] || supp[q2]) ? supp[p2++] : supp[q2++];
        }
    }

    public static void mergeSort(boolean[] a2, int from, int to2) {
        BooleanArrays.mergeSort(a2, from, to2, (boolean[])a2.clone());
    }

    public static void mergeSort(boolean[] a2) {
        BooleanArrays.mergeSort(a2, 0, a2.length);
    }

    public static void mergeSort(boolean[] a2, int from, int to2, BooleanComparator comp, boolean[] supp) {
        int len = to2 - from;
        if (len < 16) {
            BooleanArrays.insertionSort(a2, from, to2, comp);
            return;
        }
        int mid = from + to2 >>> 1;
        BooleanArrays.mergeSort(supp, from, mid, comp, a2);
        BooleanArrays.mergeSort(supp, mid, to2, comp, a2);
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

    public static void mergeSort(boolean[] a2, int from, int to2, BooleanComparator comp) {
        BooleanArrays.mergeSort(a2, from, to2, comp, (boolean[])a2.clone());
    }

    public static void mergeSort(boolean[] a2, BooleanComparator comp) {
        BooleanArrays.mergeSort(a2, 0, a2.length, comp);
    }

    public static boolean[] shuffle(boolean[] a2, int from, int to2, Random random) {
        int i2 = to2 - from;
        while (i2-- != 0) {
            int p2 = random.nextInt(i2 + 1);
            boolean t2 = a2[from + i2];
            a2[from + i2] = a2[from + p2];
            a2[from + p2] = t2;
        }
        return a2;
    }

    public static boolean[] shuffle(boolean[] a2, Random random) {
        int i2 = a2.length;
        while (i2-- != 0) {
            int p2 = random.nextInt(i2 + 1);
            boolean t2 = a2[i2];
            a2[i2] = a2[p2];
            a2[p2] = t2;
        }
        return a2;
    }

    public static boolean[] reverse(boolean[] a2) {
        int length = a2.length;
        int i2 = length / 2;
        while (i2-- != 0) {
            boolean t2 = a2[length - i2 - 1];
            a2[length - i2 - 1] = a2[i2];
            a2[i2] = t2;
        }
        return a2;
    }

    public static boolean[] reverse(boolean[] a2, int from, int to2) {
        int length = to2 - from;
        int i2 = length / 2;
        while (i2-- != 0) {
            boolean t2 = a2[from + length - i2 - 1];
            a2[from + length - i2 - 1] = a2[from + i2];
            a2[from + i2] = t2;
        }
        return a2;
    }

    private static final class ArrayHashStrategy
    implements Hash.Strategy<boolean[]>,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;

        private ArrayHashStrategy() {
        }

        @Override
        public int hashCode(boolean[] o2) {
            return java.util.Arrays.hashCode(o2);
        }

        @Override
        public boolean equals(boolean[] a2, boolean[] b2) {
            return java.util.Arrays.equals(a2, b2);
        }
    }

    protected static class ForkJoinQuickSort2
    extends RecursiveAction {
        private static final long serialVersionUID = 1L;
        private final int from;
        private final int to;
        private final boolean[] x;
        private final boolean[] y;

        public ForkJoinQuickSort2(boolean[] x2, boolean[] y2, int from, int to2) {
            this.from = from;
            this.to = to2;
            this.x = x2;
            this.y = y2;
        }

        @Override
        protected void compute() {
            int c2;
            int a2;
            boolean[] x2 = this.x;
            boolean[] y2 = this.y;
            int len = this.to - this.from;
            if (len < 8192) {
                BooleanArrays.quickSort(x2, y2, this.from, this.to);
                return;
            }
            int m2 = this.from + len / 2;
            int l2 = this.from;
            int n2 = this.to - 1;
            int s2 = len / 8;
            l2 = BooleanArrays.med3(x2, y2, l2, l2 + s2, l2 + 2 * s2);
            m2 = BooleanArrays.med3(x2, y2, m2 - s2, m2, m2 + s2);
            n2 = BooleanArrays.med3(x2, y2, n2 - 2 * s2, n2 - s2, n2);
            m2 = BooleanArrays.med3(x2, y2, l2, m2, n2);
            boolean v2 = x2[m2];
            boolean w2 = y2[m2];
            int b2 = a2 = this.from;
            int d2 = c2 = this.to - 1;
            while (true) {
                int t2;
                int comparison;
                if (b2 <= c2 && (comparison = (t2 = Boolean.compare(x2[b2], v2)) == 0 ? Boolean.compare(y2[b2], w2) : t2) <= 0) {
                    if (comparison == 0) {
                        BooleanArrays.swap(x2, y2, a2++, b2);
                    }
                    ++b2;
                    continue;
                }
                while (c2 >= b2 && (comparison = (t2 = Boolean.compare(x2[c2], v2)) == 0 ? Boolean.compare(y2[c2], w2) : t2) >= 0) {
                    if (comparison == 0) {
                        BooleanArrays.swap(x2, y2, c2, d2--);
                    }
                    --c2;
                }
                if (b2 > c2) break;
                BooleanArrays.swap(x2, y2, b2++, c2--);
            }
            s2 = Math.min(a2 - this.from, b2 - a2);
            BooleanArrays.swap(x2, y2, this.from, b2 - s2, s2);
            s2 = Math.min(d2 - c2, this.to - d2 - 1);
            BooleanArrays.swap(x2, y2, b2, this.to - s2, s2);
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
        private final boolean[] x;

        public ForkJoinQuickSortIndirect(int[] perm, boolean[] x2, int from, int to2) {
            this.from = from;
            this.to = to2;
            this.x = x2;
            this.perm = perm;
        }

        @Override
        protected void compute() {
            int c2;
            int a2;
            boolean[] x2 = this.x;
            int len = this.to - this.from;
            if (len < 8192) {
                BooleanArrays.quickSortIndirect(this.perm, x2, this.from, this.to);
                return;
            }
            int m2 = this.from + len / 2;
            int l2 = this.from;
            int n2 = this.to - 1;
            int s2 = len / 8;
            l2 = BooleanArrays.med3Indirect(this.perm, x2, l2, l2 + s2, l2 + 2 * s2);
            m2 = BooleanArrays.med3Indirect(this.perm, x2, m2 - s2, m2, m2 + s2);
            n2 = BooleanArrays.med3Indirect(this.perm, x2, n2 - 2 * s2, n2 - s2, n2);
            m2 = BooleanArrays.med3Indirect(this.perm, x2, l2, m2, n2);
            boolean v2 = x2[this.perm[m2]];
            int b2 = a2 = this.from;
            int d2 = c2 = this.to - 1;
            while (true) {
                int comparison;
                if (b2 <= c2 && (comparison = Boolean.compare(x2[this.perm[b2]], v2)) <= 0) {
                    if (comparison == 0) {
                        IntArrays.swap(this.perm, a2++, b2);
                    }
                    ++b2;
                    continue;
                }
                while (c2 >= b2 && (comparison = Boolean.compare(x2[this.perm[c2]], v2)) >= 0) {
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
        private final boolean[] x;

        public ForkJoinQuickSort(boolean[] x2, int from, int to2) {
            this.from = from;
            this.to = to2;
            this.x = x2;
        }

        @Override
        protected void compute() {
            int c2;
            int a2;
            boolean[] x2 = this.x;
            int len = this.to - this.from;
            if (len < 8192) {
                BooleanArrays.quickSort(x2, this.from, this.to);
                return;
            }
            int m2 = this.from + len / 2;
            int l2 = this.from;
            int n2 = this.to - 1;
            int s2 = len / 8;
            l2 = BooleanArrays.med3(x2, l2, l2 + s2, l2 + 2 * s2);
            m2 = BooleanArrays.med3(x2, m2 - s2, m2, m2 + s2);
            n2 = BooleanArrays.med3(x2, n2 - 2 * s2, n2 - s2, n2);
            m2 = BooleanArrays.med3(x2, l2, m2, n2);
            boolean v2 = x2[m2];
            int b2 = a2 = this.from;
            int d2 = c2 = this.to - 1;
            while (true) {
                int comparison;
                if (b2 <= c2 && (comparison = Boolean.compare(x2[b2], v2)) <= 0) {
                    if (comparison == 0) {
                        BooleanArrays.swap(x2, a2++, b2);
                    }
                    ++b2;
                    continue;
                }
                while (c2 >= b2 && (comparison = Boolean.compare(x2[c2], v2)) >= 0) {
                    if (comparison == 0) {
                        BooleanArrays.swap(x2, c2, d2--);
                    }
                    --c2;
                }
                if (b2 > c2) break;
                BooleanArrays.swap(x2, b2++, c2--);
            }
            s2 = Math.min(a2 - this.from, b2 - a2);
            BooleanArrays.swap(x2, this.from, b2 - s2, s2);
            s2 = Math.min(d2 - c2, this.to - d2 - 1);
            BooleanArrays.swap(x2, b2, this.to - s2, s2);
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
        private final boolean[] x;
        private final BooleanComparator comp;

        public ForkJoinQuickSortComp(boolean[] x2, int from, int to2, BooleanComparator comp) {
            this.from = from;
            this.to = to2;
            this.x = x2;
            this.comp = comp;
        }

        @Override
        protected void compute() {
            int c2;
            int a2;
            boolean[] x2 = this.x;
            int len = this.to - this.from;
            if (len < 8192) {
                BooleanArrays.quickSort(x2, this.from, this.to, this.comp);
                return;
            }
            int m2 = this.from + len / 2;
            int l2 = this.from;
            int n2 = this.to - 1;
            int s2 = len / 8;
            l2 = BooleanArrays.med3(x2, l2, l2 + s2, l2 + 2 * s2, this.comp);
            m2 = BooleanArrays.med3(x2, m2 - s2, m2, m2 + s2, this.comp);
            n2 = BooleanArrays.med3(x2, n2 - 2 * s2, n2 - s2, n2, this.comp);
            m2 = BooleanArrays.med3(x2, l2, m2, n2, this.comp);
            boolean v2 = x2[m2];
            int b2 = a2 = this.from;
            int d2 = c2 = this.to - 1;
            while (true) {
                int comparison;
                if (b2 <= c2 && (comparison = this.comp.compare(x2[b2], v2)) <= 0) {
                    if (comparison == 0) {
                        BooleanArrays.swap(x2, a2++, b2);
                    }
                    ++b2;
                    continue;
                }
                while (c2 >= b2 && (comparison = this.comp.compare(x2[c2], v2)) >= 0) {
                    if (comparison == 0) {
                        BooleanArrays.swap(x2, c2, d2--);
                    }
                    --c2;
                }
                if (b2 > c2) break;
                BooleanArrays.swap(x2, b2++, c2--);
            }
            s2 = Math.min(a2 - this.from, b2 - a2);
            BooleanArrays.swap(x2, this.from, b2 - s2, s2);
            s2 = Math.min(d2 - c2, this.to - d2 - 1);
            BooleanArrays.swap(x2, b2, this.to - s2, s2);
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

