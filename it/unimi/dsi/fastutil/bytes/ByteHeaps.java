package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.bytes.ByteComparator;

public class ByteHeaps {
    private ByteHeaps() {
    }

    public static int downHeap(byte[] heap, int size, int i2, ByteComparator c2) {
        assert (i2 < size);
        byte e2 = heap[i2];
        if (c2 == null) {
            int child;
            while ((child = (i2 << 1) + 1) < size) {
                byte t2 = heap[child];
                int right = child + 1;
                if (right < size && heap[right] < t2) {
                    child = right;
                    t2 = heap[child];
                }
                if (e2 > t2) {
                    heap[i2] = t2;
                    i2 = child;
                    continue;
                }
                break;
            }
        } else {
            int child;
            while ((child = (i2 << 1) + 1) < size) {
                byte t3 = heap[child];
                int right = child + 1;
                if (right < size && c2.compare(heap[right], t3) < 0) {
                    child = right;
                    t3 = heap[child];
                }
                if (c2.compare(e2, t3) > 0) {
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

    public static int upHeap(byte[] heap, int size, int i2, ByteComparator c2) {
        assert (i2 < size);
        byte e2 = heap[i2];
        if (c2 == null) {
            int parent;
            byte t2;
            while (i2 != 0 && (t2 = heap[parent = i2 - 1 >>> 1]) > e2) {
                heap[i2] = t2;
                i2 = parent;
            }
        } else {
            int parent;
            byte t3;
            while (i2 != 0 && c2.compare(t3 = heap[parent = i2 - 1 >>> 1], e2) > 0) {
                heap[i2] = t3;
                i2 = parent;
            }
        }
        heap[i2] = e2;
        return i2;
    }

    public static void makeHeap(byte[] heap, int size, ByteComparator c2) {
        int i2 = size >>> 1;
        while (i2-- != 0) {
            ByteHeaps.downHeap(heap, size, i2, c2);
        }
    }
}

