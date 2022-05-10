package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.bytes.ByteArrays;
import it.unimi.dsi.fastutil.bytes.ByteComparator;
import it.unimi.dsi.fastutil.ints.IntArrays;

public class ByteSemiIndirectHeaps {
    private ByteSemiIndirectHeaps() {
    }

    public static int downHeap(byte[] refArray, int[] heap, int size, int i2, ByteComparator c2) {
        assert (i2 < size);
        int e2 = heap[i2];
        byte E = refArray[e2];
        if (c2 == null) {
            int child;
            while ((child = (i2 << 1) + 1) < size) {
                int t2 = heap[child];
                int right = child + 1;
                if (right < size && refArray[heap[right]] < refArray[t2]) {
                    child = right;
                    t2 = heap[child];
                }
                if (E > refArray[t2]) {
                    heap[i2] = t2;
                    i2 = child;
                    continue;
                }
                break;
            }
        } else {
            int child;
            while ((child = (i2 << 1) + 1) < size) {
                int t3 = heap[child];
                int right = child + 1;
                if (right < size && c2.compare(refArray[heap[right]], refArray[t3]) < 0) {
                    child = right;
                    t3 = heap[child];
                }
                if (c2.compare(E, refArray[t3]) > 0) {
                    heap[i2] = t3;
                    i2 = child;
                    continue;
                }
                break;
            }
        }
        heap[i2] = e2;
        return i2;
    }

    public static int upHeap(byte[] refArray, int[] heap, int size, int i2, ByteComparator c2) {
        assert (i2 < size);
        int e2 = heap[i2];
        byte E = refArray[e2];
        if (c2 == null) {
            int parent;
            int t2;
            while (i2 != 0 && refArray[t2 = heap[parent = i2 - 1 >>> 1]] > E) {
                heap[i2] = t2;
                i2 = parent;
            }
        } else {
            int parent;
            int t3;
            while (i2 != 0 && c2.compare(refArray[t3 = heap[parent = i2 - 1 >>> 1]], E) > 0) {
                heap[i2] = t3;
                i2 = parent;
            }
        }
        heap[i2] = e2;
        return i2;
    }

    public static void makeHeap(byte[] refArray, int offset, int length, int[] heap, ByteComparator c2) {
        ByteArrays.ensureOffsetLength(refArray, offset, length);
        if (heap.length < length) {
            throw new IllegalArgumentException("The heap length (" + heap.length + ") is smaller than the number of elements (" + length + ")");
        }
        int i2 = length;
        while (i2-- != 0) {
            heap[i2] = offset + i2;
        }
        i2 = length >>> 1;
        while (i2-- != 0) {
            ByteSemiIndirectHeaps.downHeap(refArray, heap, length, i2, c2);
        }
    }

    public static int[] makeHeap(byte[] refArray, int offset, int length, ByteComparator c2) {
        int[] heap = length <= 0 ? IntArrays.EMPTY_ARRAY : new int[length];
        ByteSemiIndirectHeaps.makeHeap(refArray, offset, length, heap, c2);
        return heap;
    }

    public static void makeHeap(byte[] refArray, int[] heap, int size, ByteComparator c2) {
        int i2 = size >>> 1;
        while (i2-- != 0) {
            ByteSemiIndirectHeaps.downHeap(refArray, heap, size, i2, c2);
        }
    }

    public static int front(byte[] refArray, int[] heap, int size, int[] a2) {
        byte top = refArray[heap[0]];
        int j2 = 0;
        int l2 = 0;
        int r2 = 1;
        int f2 = 0;
        for (int i2 = 0; i2 < r2; ++i2) {
            if (i2 == f2) {
                if (l2 >= r2) break;
                f2 = (f2 << 1) + 1;
                i2 = l2;
                l2 = -1;
            }
            if (top != refArray[heap[i2]]) continue;
            a2[j2++] = heap[i2];
            if (l2 == -1) {
                l2 = i2 * 2 + 1;
            }
            r2 = Math.min(size, i2 * 2 + 3);
        }
        return j2;
    }

    public static int front(byte[] refArray, int[] heap, int size, int[] a2, ByteComparator c2) {
        byte top = refArray[heap[0]];
        int j2 = 0;
        int l2 = 0;
        int r2 = 1;
        int f2 = 0;
        for (int i2 = 0; i2 < r2; ++i2) {
            if (i2 == f2) {
                if (l2 >= r2) break;
                f2 = (f2 << 1) + 1;
                i2 = l2;
                l2 = -1;
            }
            if (c2.compare(top, refArray[heap[i2]]) != 0) continue;
            a2[j2++] = heap[i2];
            if (l2 == -1) {
                l2 = i2 * 2 + 1;
            }
            r2 = Math.min(size, i2 * 2 + 3);
        }
        return j2;
    }
}

