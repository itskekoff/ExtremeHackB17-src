package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.bytes.ByteArrays;
import it.unimi.dsi.fastutil.bytes.ByteComparator;
import java.util.Arrays;

public class ByteIndirectHeaps {
    private ByteIndirectHeaps() {
    }

    public static int downHeap(byte[] refArray, int[] heap, int[] inv, int size, int i2, ByteComparator c2) {
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
                    inv[heap[i2]] = i2;
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
                    inv[heap[i2]] = i2;
                    i2 = child;
                    continue;
                }
                break;
            }
        }
        heap[i2] = e2;
        inv[e2] = i2;
        return i2;
    }

    public static int upHeap(byte[] refArray, int[] heap, int[] inv, int size, int i2, ByteComparator c2) {
        assert (i2 < size);
        int e2 = heap[i2];
        byte E = refArray[e2];
        if (c2 == null) {
            int parent;
            int t2;
            while (i2 != 0 && refArray[t2 = heap[parent = i2 - 1 >>> 1]] > E) {
                heap[i2] = t2;
                inv[heap[i2]] = i2;
                i2 = parent;
            }
        } else {
            int parent;
            int t3;
            while (i2 != 0 && c2.compare(refArray[t3 = heap[parent = i2 - 1 >>> 1]], E) > 0) {
                heap[i2] = t3;
                inv[heap[i2]] = i2;
                i2 = parent;
            }
        }
        heap[i2] = e2;
        inv[e2] = i2;
        return i2;
    }

    public static void makeHeap(byte[] refArray, int offset, int length, int[] heap, int[] inv, ByteComparator c2) {
        ByteArrays.ensureOffsetLength(refArray, offset, length);
        if (heap.length < length) {
            throw new IllegalArgumentException("The heap length (" + heap.length + ") is smaller than the number of elements (" + length + ")");
        }
        if (inv.length < refArray.length) {
            throw new IllegalArgumentException("The inversion array length (" + heap.length + ") is smaller than the length of the reference array (" + refArray.length + ")");
        }
        Arrays.fill(inv, 0, refArray.length, -1);
        int i2 = length;
        while (i2-- != 0) {
            heap[i2] = offset + i2;
            inv[heap[i2]] = i2;
        }
        i2 = length >>> 1;
        while (i2-- != 0) {
            ByteIndirectHeaps.downHeap(refArray, heap, inv, length, i2, c2);
        }
    }

    public static void makeHeap(byte[] refArray, int[] heap, int[] inv, int size, ByteComparator c2) {
        int i2 = size >>> 1;
        while (i2-- != 0) {
            ByteIndirectHeaps.downHeap(refArray, heap, inv, size, i2, c2);
        }
    }
}

