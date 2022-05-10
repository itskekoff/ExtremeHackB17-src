package io.netty.handler.codec.compression;

final class Bzip2DivSufSort {
    private static final int STACK_SIZE = 64;
    private static final int BUCKET_A_SIZE = 256;
    private static final int BUCKET_B_SIZE = 65536;
    private static final int SS_BLOCKSIZE = 1024;
    private static final int INSERTIONSORT_THRESHOLD = 8;
    private static final int[] LOG_2_TABLE = new int[]{-1, 0, 1, 1, 2, 2, 2, 2, 3, 3, 3, 3, 3, 3, 3, 3, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7};
    private final int[] SA;
    private final byte[] T;
    private final int n;

    Bzip2DivSufSort(byte[] block, int[] bwtBlock, int blockLength) {
        this.T = block;
        this.SA = bwtBlock;
        this.n = blockLength;
    }

    private static void swapElements(int[] array1, int idx1, int[] array2, int idx2) {
        int temp = array1[idx1];
        array1[idx1] = array2[idx2];
        array2[idx2] = temp;
    }

    private int ssCompare(int p1, int p2, int depth) {
        int U2;
        int[] SA = this.SA;
        byte[] T = this.T;
        int U1n = SA[p1 + 1] + 2;
        int U2n = SA[p2 + 1] + 2;
        int U1 = depth + SA[p1];
        for (U2 = depth + SA[p2]; U1 < U1n && U2 < U2n && T[U1] == T[U2]; ++U1, ++U2) {
        }
        return U1 < U1n ? (U2 < U2n ? (T[U1] & 0xFF) - (T[U2] & 0xFF) : 1) : (U2 < U2n ? -1 : 0);
    }

    private int ssCompareLast(int pa2, int p1, int p2, int depth, int size) {
        int U2;
        int[] SA = this.SA;
        byte[] T = this.T;
        int U1 = depth + SA[p1];
        int U1n = size;
        int U2n = SA[p2 + 1] + 2;
        for (U2 = depth + SA[p2]; U1 < U1n && U2 < U2n && T[U1] == T[U2]; ++U1, ++U2) {
        }
        if (U1 < U1n) {
            return U2 < U2n ? (T[U1] & 0xFF) - (T[U2] & 0xFF) : 1;
        }
        if (U2 == U2n) {
            return 1;
        }
        U1 %= size;
        U1n = SA[pa2] + 2;
        while (U1 < U1n && U2 < U2n && T[U1] == T[U2]) {
            ++U1;
            ++U2;
        }
        return U1 < U1n ? (U2 < U2n ? (T[U1] & 0xFF) - (T[U2] & 0xFF) : 1) : (U2 < U2n ? -1 : 0);
    }

    private void ssInsertionSort(int pa2, int first, int last, int depth) {
        int[] SA = this.SA;
        for (int i2 = last - 2; first <= i2; --i2) {
            int r2;
            int t2 = SA[i2];
            int j2 = i2 + 1;
            while (0 < (r2 = this.ssCompare(pa2 + t2, pa2 + SA[j2], depth))) {
                do {
                    SA[j2 - 1] = SA[j2];
                } while (++j2 < last && SA[j2] < 0);
                if (last > j2) continue;
            }
            if (r2 == 0) {
                SA[j2] = ~SA[j2];
            }
            SA[j2 - 1] = t2;
        }
    }

    private void ssFixdown(int td2, int pa2, int sa2, int i2, int size) {
        int j2;
        int[] SA = this.SA;
        byte[] T = this.T;
        int v2 = SA[sa2 + i2];
        int c2 = T[td2 + SA[pa2 + v2]] & 0xFF;
        while ((j2 = 2 * i2 + 1) < size) {
            int e2;
            int k2;
            int d2;
            if ((d2 = T[td2 + SA[pa2 + SA[sa2 + (k2 = j2++)]]] & 0xFF) < (e2 = T[td2 + SA[pa2 + SA[sa2 + j2]]] & 0xFF)) {
                k2 = j2;
                d2 = e2;
            }
            if (d2 <= c2) break;
            SA[sa2 + i2] = SA[sa2 + k2];
            i2 = k2;
        }
        SA[sa2 + i2] = v2;
    }

    private void ssHeapSort(int td2, int pa2, int sa2, int size) {
        int i2;
        int[] SA = this.SA;
        byte[] T = this.T;
        int m2 = size;
        if (size % 2 == 0 && (T[td2 + SA[pa2 + SA[sa2 + --m2 / 2]]] & 0xFF) < (T[td2 + SA[pa2 + SA[sa2 + m2]]] & 0xFF)) {
            Bzip2DivSufSort.swapElements(SA, sa2 + m2, SA, sa2 + m2 / 2);
        }
        for (i2 = m2 / 2 - 1; 0 <= i2; --i2) {
            this.ssFixdown(td2, pa2, sa2, i2, m2);
        }
        if (size % 2 == 0) {
            Bzip2DivSufSort.swapElements(SA, sa2, SA, sa2 + m2);
            this.ssFixdown(td2, pa2, sa2, 0, m2);
        }
        for (i2 = m2 - 1; 0 < i2; --i2) {
            int t2 = SA[sa2];
            SA[sa2] = SA[sa2 + i2];
            this.ssFixdown(td2, pa2, sa2, 0, i2);
            SA[sa2 + i2] = t2;
        }
    }

    private int ssMedian3(int td2, int pa2, int v1, int v2, int v3) {
        int[] SA = this.SA;
        byte[] T = this.T;
        int T_v1 = T[td2 + SA[pa2 + SA[v1]]] & 0xFF;
        int T_v2 = T[td2 + SA[pa2 + SA[v2]]] & 0xFF;
        int T_v3 = T[td2 + SA[pa2 + SA[v3]]] & 0xFF;
        if (T_v1 > T_v2) {
            int temp = v1;
            v1 = v2;
            v2 = temp;
            int T_vtemp = T_v1;
            T_v1 = T_v2;
            T_v2 = T_vtemp;
        }
        if (T_v2 > T_v3) {
            if (T_v1 > T_v3) {
                return v1;
            }
            return v3;
        }
        return v2;
    }

    private int ssMedian5(int td2, int pa2, int v1, int v2, int v3, int v4, int v5) {
        int T_vtemp;
        int temp;
        int[] SA = this.SA;
        byte[] T = this.T;
        int T_v1 = T[td2 + SA[pa2 + SA[v1]]] & 0xFF;
        int T_v2 = T[td2 + SA[pa2 + SA[v2]]] & 0xFF;
        int T_v3 = T[td2 + SA[pa2 + SA[v3]]] & 0xFF;
        int T_v4 = T[td2 + SA[pa2 + SA[v4]]] & 0xFF;
        int T_v5 = T[td2 + SA[pa2 + SA[v5]]] & 0xFF;
        if (T_v2 > T_v3) {
            temp = v2;
            v2 = v3;
            v3 = temp;
            T_vtemp = T_v2;
            T_v2 = T_v3;
            T_v3 = T_vtemp;
        }
        if (T_v4 > T_v5) {
            temp = v4;
            v4 = v5;
            v5 = temp;
            T_vtemp = T_v4;
            T_v4 = T_v5;
            T_v5 = T_vtemp;
        }
        if (T_v2 > T_v4) {
            v4 = temp = v2;
            T_v4 = T_vtemp = T_v2;
            temp = v3;
            v3 = v5;
            v5 = temp;
            T_vtemp = T_v3;
            T_v3 = T_v5;
            T_v5 = T_vtemp;
        }
        if (T_v1 > T_v3) {
            temp = v1;
            v1 = v3;
            v3 = temp;
            T_vtemp = T_v1;
            T_v1 = T_v3;
            T_v3 = T_vtemp;
        }
        if (T_v1 > T_v4) {
            v4 = temp = v1;
            T_v4 = T_vtemp = T_v1;
            v3 = v5;
            T_v3 = T_v5;
        }
        if (T_v3 > T_v4) {
            return v4;
        }
        return v3;
    }

    private int ssPivot(int td2, int pa2, int first, int last) {
        int t2 = last - first;
        int middle = first + t2 / 2;
        if (t2 <= 512) {
            if (t2 <= 32) {
                return this.ssMedian3(td2, pa2, first, middle, last - 1);
            }
            return this.ssMedian5(td2, pa2, first, first + (t2 >>= 2), middle, last - 1 - t2, last - 1);
        }
        return this.ssMedian3(td2, pa2, this.ssMedian3(td2, pa2, first, first + (t2 >>= 3), first + (t2 << 1)), this.ssMedian3(td2, pa2, middle - t2, middle, middle + t2), this.ssMedian3(td2, pa2, last - 1 - (t2 << 1), last - 1 - t2, last - 1));
    }

    private static int ssLog(int n2) {
        return (n2 & 0xFF00) != 0 ? 8 + LOG_2_TABLE[n2 >> 8 & 0xFF] : LOG_2_TABLE[n2 & 0xFF];
    }

    private int ssSubstringPartition(int pa2, int first, int last, int depth) {
        int[] SA = this.SA;
        int a2 = first - 1;
        int b2 = last;
        while (true) {
            if (++a2 < b2 && SA[pa2 + SA[a2]] + depth >= SA[pa2 + SA[a2] + 1] + 1) {
                SA[a2] = ~SA[a2];
                continue;
            }
            --b2;
            while (a2 < b2 && SA[pa2 + SA[b2]] + depth < SA[pa2 + SA[b2] + 1] + 1) {
                --b2;
            }
            if (b2 <= a2) break;
            int t2 = ~SA[b2];
            SA[b2] = SA[a2];
            SA[a2] = t2;
        }
        if (first < a2) {
            SA[first] = ~SA[first];
        }
        return a2;
    }

    private void ssMultiKeyIntroSort(int pa2, int first, int last, int depth) {
        int[] SA = this.SA;
        byte[] T = this.T;
        StackEntry[] stack = new StackEntry[64];
        int x2 = 0;
        int ssize = 0;
        int limit = Bzip2DivSufSort.ssLog(last - first);
        while (true) {
            int c2;
            int b2;
            int a2;
            int v2;
            if (last - first <= 8) {
                if (1 < last - first) {
                    this.ssInsertionSort(pa2, first, last, depth);
                }
                if (ssize == 0) {
                    return;
                }
                StackEntry entry = stack[--ssize];
                first = entry.a;
                last = entry.b;
                depth = entry.c;
                limit = entry.d;
                continue;
            }
            int Td = depth;
            if (limit-- == 0) {
                this.ssHeapSort(Td, pa2, first, last - first);
            }
            if (limit < 0) {
                v2 = T[Td + SA[pa2 + SA[first]]] & 0xFF;
                for (a2 = first + 1; a2 < last; ++a2) {
                    x2 = T[Td + SA[pa2 + SA[a2]]] & 0xFF;
                    if (x2 == v2) continue;
                    if (1 < a2 - first) break;
                    v2 = x2;
                    first = a2;
                }
                if ((T[Td + SA[pa2 + SA[first]] - 1] & 0xFF) < v2) {
                    first = this.ssSubstringPartition(pa2, first, a2, depth);
                }
                if (a2 - first <= last - a2) {
                    if (1 < a2 - first) {
                        stack[ssize++] = new StackEntry(a2, last, depth, -1);
                        last = a2;
                        ++depth;
                        limit = Bzip2DivSufSort.ssLog(a2 - first);
                        continue;
                    }
                    first = a2;
                    limit = -1;
                    continue;
                }
                if (1 < last - a2) {
                    stack[ssize++] = new StackEntry(first, a2, depth + 1, Bzip2DivSufSort.ssLog(a2 - first));
                    first = a2;
                    limit = -1;
                    continue;
                }
                last = a2;
                ++depth;
                limit = Bzip2DivSufSort.ssLog(a2 - first);
                continue;
            }
            a2 = this.ssPivot(Td, pa2, first, last);
            v2 = T[Td + SA[pa2 + SA[a2]]] & 0xFF;
            Bzip2DivSufSort.swapElements(SA, first, SA, a2);
            for (b2 = first + 1; b2 < last && (x2 = T[Td + SA[pa2 + SA[b2]]] & 0xFF) == v2; ++b2) {
            }
            a2 = b2;
            if (a2 < last && x2 < v2) {
                while (++b2 < last && (x2 = T[Td + SA[pa2 + SA[b2]]] & 0xFF) <= v2) {
                    if (x2 != v2) continue;
                    Bzip2DivSufSort.swapElements(SA, b2, SA, a2);
                    ++a2;
                }
            }
            for (c2 = last - 1; b2 < c2 && (x2 = T[Td + SA[pa2 + SA[c2]]] & 0xFF) == v2; --c2) {
            }
            int d2 = c2;
            if (b2 < d2 && x2 > v2) {
                while (b2 < --c2 && (x2 = T[Td + SA[pa2 + SA[c2]]] & 0xFF) >= v2) {
                    if (x2 != v2) continue;
                    Bzip2DivSufSort.swapElements(SA, c2, SA, d2);
                    --d2;
                }
            }
            while (b2 < c2) {
                Bzip2DivSufSort.swapElements(SA, b2, SA, c2);
                while (++b2 < c2 && (x2 = T[Td + SA[pa2 + SA[b2]]] & 0xFF) <= v2) {
                    if (x2 != v2) continue;
                    Bzip2DivSufSort.swapElements(SA, b2, SA, a2);
                    ++a2;
                }
                while (b2 < --c2 && (x2 = T[Td + SA[pa2 + SA[c2]]] & 0xFF) >= v2) {
                    if (x2 != v2) continue;
                    Bzip2DivSufSort.swapElements(SA, c2, SA, d2);
                    --d2;
                }
            }
            if (a2 <= d2) {
                c2 = b2 - 1;
                int s2 = a2 - first;
                int t2 = b2 - a2;
                if (s2 > t2) {
                    s2 = t2;
                }
                int e2 = first;
                int f2 = b2 - s2;
                while (0 < s2) {
                    Bzip2DivSufSort.swapElements(SA, e2, SA, f2);
                    --s2;
                    ++e2;
                    ++f2;
                }
                s2 = d2 - c2;
                t2 = last - d2 - 1;
                if (s2 > t2) {
                    s2 = t2;
                }
                e2 = b2;
                f2 = last - s2;
                while (0 < s2) {
                    Bzip2DivSufSort.swapElements(SA, e2, SA, f2);
                    --s2;
                    ++e2;
                    ++f2;
                }
                a2 = first + (b2 - a2);
                c2 = last - (d2 - c2);
                int n2 = b2 = v2 <= (T[Td + SA[pa2 + SA[a2]] - 1] & 0xFF) ? a2 : this.ssSubstringPartition(pa2, a2, c2, depth);
                if (a2 - first <= last - c2) {
                    if (last - c2 <= c2 - b2) {
                        stack[ssize++] = new StackEntry(b2, c2, depth + 1, Bzip2DivSufSort.ssLog(c2 - b2));
                        stack[ssize++] = new StackEntry(c2, last, depth, limit);
                        last = a2;
                        continue;
                    }
                    if (a2 - first <= c2 - b2) {
                        stack[ssize++] = new StackEntry(c2, last, depth, limit);
                        stack[ssize++] = new StackEntry(b2, c2, depth + 1, Bzip2DivSufSort.ssLog(c2 - b2));
                        last = a2;
                        continue;
                    }
                    stack[ssize++] = new StackEntry(c2, last, depth, limit);
                    stack[ssize++] = new StackEntry(first, a2, depth, limit);
                    first = b2;
                    last = c2;
                    ++depth;
                    limit = Bzip2DivSufSort.ssLog(c2 - b2);
                    continue;
                }
                if (a2 - first <= c2 - b2) {
                    stack[ssize++] = new StackEntry(b2, c2, depth + 1, Bzip2DivSufSort.ssLog(c2 - b2));
                    stack[ssize++] = new StackEntry(first, a2, depth, limit);
                    first = c2;
                    continue;
                }
                if (last - c2 <= c2 - b2) {
                    stack[ssize++] = new StackEntry(first, a2, depth, limit);
                    stack[ssize++] = new StackEntry(b2, c2, depth + 1, Bzip2DivSufSort.ssLog(c2 - b2));
                    first = c2;
                    continue;
                }
                stack[ssize++] = new StackEntry(first, a2, depth, limit);
                stack[ssize++] = new StackEntry(c2, last, depth, limit);
                first = b2;
                last = c2;
                ++depth;
                limit = Bzip2DivSufSort.ssLog(c2 - b2);
                continue;
            }
            ++limit;
            if ((T[Td + SA[pa2 + SA[first]] - 1] & 0xFF) < v2) {
                first = this.ssSubstringPartition(pa2, first, last, depth);
                limit = Bzip2DivSufSort.ssLog(last - first);
            }
            ++depth;
        }
    }

    private static void ssBlockSwap(int[] array1, int first1, int[] array2, int first2, int size) {
        int i2 = size;
        int a2 = first1;
        int b2 = first2;
        while (0 < i2) {
            Bzip2DivSufSort.swapElements(array1, a2, array2, b2);
            --i2;
            ++a2;
            ++b2;
        }
    }

    private void ssMergeForward(int pa2, int[] buf2, int bufoffset, int first, int middle, int last, int depth) {
        int[] SA = this.SA;
        int bufend = bufoffset + (middle - first) - 1;
        Bzip2DivSufSort.ssBlockSwap(buf2, bufoffset, SA, first, middle - first);
        int t2 = SA[first];
        int i2 = first;
        int j2 = bufoffset;
        int k2 = middle;
        while (true) {
            int r2;
            if ((r2 = this.ssCompare(pa2 + buf2[j2], pa2 + SA[k2], depth)) < 0) {
                do {
                    SA[i2++] = buf2[j2];
                    if (bufend <= j2) {
                        buf2[j2] = t2;
                        return;
                    }
                    buf2[j2++] = SA[i2];
                } while (buf2[j2] < 0);
                continue;
            }
            if (r2 > 0) {
                do {
                    SA[i2++] = SA[k2];
                    SA[k2++] = SA[i2];
                    if (last > k2) continue;
                    while (j2 < bufend) {
                        SA[i2++] = buf2[j2];
                        buf2[j2++] = SA[i2];
                    }
                    SA[i2] = buf2[j2];
                    buf2[j2] = t2;
                    return;
                } while (SA[k2] < 0);
                continue;
            }
            SA[k2] = ~SA[k2];
            do {
                SA[i2++] = buf2[j2];
                if (bufend <= j2) {
                    buf2[j2] = t2;
                    return;
                }
                buf2[j2++] = SA[i2];
            } while (buf2[j2] < 0);
            do {
                SA[i2++] = SA[k2];
                SA[k2++] = SA[i2];
                if (last > k2) continue;
                while (j2 < bufend) {
                    SA[i2++] = buf2[j2];
                    buf2[j2++] = SA[i2];
                }
                SA[i2] = buf2[j2];
                buf2[j2] = t2;
                return;
            } while (SA[k2] < 0);
        }
    }

    private void ssMergeBackward(int pa2, int[] buf2, int bufoffset, int first, int middle, int last, int depth) {
        int p2;
        int p1;
        int[] SA = this.SA;
        int bufend = bufoffset + (last - middle);
        Bzip2DivSufSort.ssBlockSwap(buf2, bufoffset, SA, middle, last - middle);
        int x2 = 0;
        if (buf2[bufend - 1] < 0) {
            x2 |= 1;
            p1 = pa2 + ~buf2[bufend - 1];
        } else {
            p1 = pa2 + buf2[bufend - 1];
        }
        if (SA[middle - 1] < 0) {
            x2 |= 2;
            p2 = pa2 + ~SA[middle - 1];
        } else {
            p2 = pa2 + SA[middle - 1];
        }
        int t2 = SA[last - 1];
        int i2 = last - 1;
        int j2 = bufend - 1;
        int k2 = middle - 1;
        while (true) {
            int r2;
            if ((r2 = this.ssCompare(p1, p2, depth)) > 0) {
                if ((x2 & 1) != 0) {
                    do {
                        SA[i2--] = buf2[j2];
                        buf2[j2--] = SA[i2];
                    } while (buf2[j2] < 0);
                    x2 ^= 1;
                }
                SA[i2--] = buf2[j2];
                if (j2 <= bufoffset) {
                    buf2[j2] = t2;
                    return;
                }
                buf2[j2--] = SA[i2];
                if (buf2[j2] < 0) {
                    x2 |= 1;
                    p1 = pa2 + ~buf2[j2];
                    continue;
                }
                p1 = pa2 + buf2[j2];
                continue;
            }
            if (r2 < 0) {
                if ((x2 & 2) != 0) {
                    do {
                        SA[i2--] = SA[k2];
                        SA[k2--] = SA[i2];
                    } while (SA[k2] < 0);
                    x2 ^= 2;
                }
                SA[i2--] = SA[k2];
                SA[k2--] = SA[i2];
                if (k2 < first) {
                    while (bufoffset < j2) {
                        SA[i2--] = buf2[j2];
                        buf2[j2--] = SA[i2];
                    }
                    SA[i2] = buf2[j2];
                    buf2[j2] = t2;
                    return;
                }
                if (SA[k2] < 0) {
                    x2 |= 2;
                    p2 = pa2 + ~SA[k2];
                    continue;
                }
                p2 = pa2 + SA[k2];
                continue;
            }
            if ((x2 & 1) != 0) {
                do {
                    SA[i2--] = buf2[j2];
                    buf2[j2--] = SA[i2];
                } while (buf2[j2] < 0);
                x2 ^= 1;
            }
            SA[i2--] = ~buf2[j2];
            if (j2 <= bufoffset) {
                buf2[j2] = t2;
                return;
            }
            buf2[j2--] = SA[i2];
            if ((x2 & 2) != 0) {
                do {
                    SA[i2--] = SA[k2];
                    SA[k2--] = SA[i2];
                } while (SA[k2] < 0);
                x2 ^= 2;
            }
            SA[i2--] = SA[k2];
            SA[k2--] = SA[i2];
            if (k2 < first) {
                while (bufoffset < j2) {
                    SA[i2--] = buf2[j2];
                    buf2[j2--] = SA[i2];
                }
                SA[i2] = buf2[j2];
                buf2[j2] = t2;
                return;
            }
            if (buf2[j2] < 0) {
                x2 |= 1;
                p1 = pa2 + ~buf2[j2];
            } else {
                p1 = pa2 + buf2[j2];
            }
            if (SA[k2] < 0) {
                x2 |= 2;
                p2 = pa2 + ~SA[k2];
                continue;
            }
            p2 = pa2 + SA[k2];
        }
    }

    private static int getIDX(int a2) {
        return 0 <= a2 ? a2 : ~a2;
    }

    private void ssMergeCheckEqual(int pa2, int depth, int a2) {
        int[] SA = this.SA;
        if (0 <= SA[a2] && this.ssCompare(pa2 + Bzip2DivSufSort.getIDX(SA[a2 - 1]), pa2 + SA[a2], depth) == 0) {
            SA[a2] = ~SA[a2];
        }
    }

    private void ssMerge(int pa2, int first, int middle, int last, int[] buf2, int bufoffset, int bufsize, int depth) {
        int[] SA = this.SA;
        StackEntry[] stack = new StackEntry[64];
        int check = 0;
        int ssize = 0;
        while (true) {
            StackEntry entry;
            if (last - middle <= bufsize) {
                if (first < middle && middle < last) {
                    this.ssMergeBackward(pa2, buf2, bufoffset, first, middle, last, depth);
                }
                if (check & true) {
                    this.ssMergeCheckEqual(pa2, depth, first);
                }
                if ((check & 2) != 0) {
                    this.ssMergeCheckEqual(pa2, depth, last);
                }
                if (ssize == 0) {
                    return;
                }
                entry = stack[--ssize];
                first = entry.a;
                middle = entry.b;
                last = entry.c;
                check = entry.d;
                continue;
            }
            if (middle - first <= bufsize) {
                if (first < middle) {
                    this.ssMergeForward(pa2, buf2, bufoffset, first, middle, last, depth);
                }
                if ((check & 1) != 0) {
                    this.ssMergeCheckEqual(pa2, depth, first);
                }
                if ((check & 2) != 0) {
                    this.ssMergeCheckEqual(pa2, depth, last);
                }
                if (ssize == 0) {
                    return;
                }
                entry = stack[--ssize];
                first = entry.a;
                middle = entry.b;
                last = entry.c;
                check = entry.d;
                continue;
            }
            int m2 = 0;
            int len = Math.min(middle - first, last - middle);
            int half = len >> 1;
            while (0 < len) {
                if (this.ssCompare(pa2 + Bzip2DivSufSort.getIDX(SA[middle + m2 + half]), pa2 + Bzip2DivSufSort.getIDX(SA[middle - m2 - half - 1]), depth) < 0) {
                    m2 += half + 1;
                    half -= len & 1 ^ 1;
                }
                len = half;
                half >>= 1;
            }
            if (0 < m2) {
                int j2;
                Bzip2DivSufSort.ssBlockSwap(SA, middle - m2, SA, middle, m2);
                int i2 = j2 = middle;
                int next = 0;
                if (middle + m2 < last) {
                    if (SA[middle + m2] < 0) {
                        while (SA[i2 - 1] < 0) {
                            --i2;
                        }
                        SA[middle + m2] = ~SA[middle + m2];
                    }
                    j2 = middle;
                    while (SA[j2] < 0) {
                        ++j2;
                    }
                    next = 1;
                }
                if (i2 - first <= last - j2) {
                    stack[ssize++] = new StackEntry(j2, middle + m2, last, check & 2 | next & 1);
                    middle -= m2;
                    last = i2;
                    check &= 1;
                    continue;
                }
                if (i2 == middle && middle == j2) {
                    next <<= 1;
                }
                stack[ssize++] = new StackEntry(first, middle - m2, i2, check & 1 | next & 2);
                first = j2;
                middle += m2;
                check = check & 2 | next & 1;
                continue;
            }
            if ((check & 1) != 0) {
                this.ssMergeCheckEqual(pa2, depth, first);
            }
            this.ssMergeCheckEqual(pa2, depth, middle);
            if ((check & 2) != 0) {
                this.ssMergeCheckEqual(pa2, depth, last);
            }
            if (ssize == 0) {
                return;
            }
            entry = stack[--ssize];
            first = entry.a;
            middle = entry.b;
            last = entry.c;
            check = entry.d;
        }
    }

    private void subStringSort(int pa2, int first, int last, int[] buf2, int bufoffset, int bufsize, int depth, boolean lastsuffix, int size) {
        int k2;
        int[] SA = this.SA;
        if (lastsuffix) {
            ++first;
        }
        int a2 = first;
        int i2 = 0;
        while (a2 + 1024 < last) {
            this.ssMultiKeyIntroSort(pa2, a2, a2 + 1024, depth);
            int[] curbuf = SA;
            int curbufoffset = a2 + 1024;
            int curbufsize = last - (a2 + 1024);
            if (curbufsize <= bufsize) {
                curbufsize = bufsize;
                curbuf = buf2;
                curbufoffset = bufoffset;
            }
            int b2 = a2;
            k2 = 1024;
            int j2 = i2;
            while ((j2 & 1) != 0) {
                this.ssMerge(pa2, b2 - k2, b2, b2 + k2, curbuf, curbufoffset, curbufsize, depth);
                b2 -= k2;
                k2 <<= 1;
                j2 >>>= 1;
            }
            a2 += 1024;
            ++i2;
        }
        this.ssMultiKeyIntroSort(pa2, a2, last, depth);
        k2 = 1024;
        while (i2 != 0) {
            if (i2 & true) {
                this.ssMerge(pa2, a2 - k2, a2, last, buf2, bufoffset, bufsize, depth);
                a2 -= k2;
            }
            k2 <<= 1;
            i2 >>= 1;
        }
        if (lastsuffix) {
            i2 = SA[first - 1];
            int r2 = 1;
            for (a2 = first; a2 < last && (SA[a2] < 0 || 0 < (r2 = this.ssCompareLast(pa2, pa2 + i2, pa2 + SA[a2], depth, size))); ++a2) {
                SA[a2 - 1] = SA[a2];
            }
            if (r2 == 0) {
                SA[a2] = ~SA[a2];
            }
            SA[a2 - 1] = i2;
        }
    }

    private int trGetC(int isa, int isaD, int isaN, int p2) {
        return isaD + p2 < isaN ? this.SA[isaD + p2] : this.SA[isa + (isaD - isa + p2) % (isaN - isa)];
    }

    private void trFixdown(int isa, int isaD, int isaN, int sa2, int i2, int size) {
        int j2;
        int[] SA = this.SA;
        int v2 = SA[sa2 + i2];
        int c2 = this.trGetC(isa, isaD, isaN, v2);
        while ((j2 = 2 * i2 + 1) < size) {
            int e2;
            int k2;
            int d2;
            if ((d2 = this.trGetC(isa, isaD, isaN, SA[sa2 + (k2 = j2++)])) < (e2 = this.trGetC(isa, isaD, isaN, SA[sa2 + j2]))) {
                k2 = j2;
                d2 = e2;
            }
            if (d2 <= c2) break;
            SA[sa2 + i2] = SA[sa2 + k2];
            i2 = k2;
        }
        SA[sa2 + i2] = v2;
    }

    private void trHeapSort(int isa, int isaD, int isaN, int sa2, int size) {
        int i2;
        int[] SA = this.SA;
        int m2 = size;
        if (size % 2 == 0 && this.trGetC(isa, isaD, isaN, SA[sa2 + --m2 / 2]) < this.trGetC(isa, isaD, isaN, SA[sa2 + m2])) {
            Bzip2DivSufSort.swapElements(SA, sa2 + m2, SA, sa2 + m2 / 2);
        }
        for (i2 = m2 / 2 - 1; 0 <= i2; --i2) {
            this.trFixdown(isa, isaD, isaN, sa2, i2, m2);
        }
        if (size % 2 == 0) {
            Bzip2DivSufSort.swapElements(SA, sa2, SA, sa2 + m2);
            this.trFixdown(isa, isaD, isaN, sa2, 0, m2);
        }
        for (i2 = m2 - 1; 0 < i2; --i2) {
            int t2 = SA[sa2];
            SA[sa2] = SA[sa2 + i2];
            this.trFixdown(isa, isaD, isaN, sa2, 0, i2);
            SA[sa2 + i2] = t2;
        }
    }

    private void trInsertionSort(int isa, int isaD, int isaN, int first, int last) {
        int[] SA = this.SA;
        for (int a2 = first + 1; a2 < last; ++a2) {
            int r2;
            int t2 = SA[a2];
            int b2 = a2 - 1;
            while (0 > (r2 = this.trGetC(isa, isaD, isaN, t2) - this.trGetC(isa, isaD, isaN, SA[b2]))) {
                do {
                    SA[b2 + 1] = SA[b2];
                } while (first <= --b2 && SA[b2] < 0);
                if (b2 >= first) continue;
            }
            if (r2 == 0) {
                SA[b2] = ~SA[b2];
            }
            SA[b2 + 1] = t2;
        }
    }

    private static int trLog(int n2) {
        return (n2 & 0xFFFF0000) != 0 ? ((n2 & 0xFF000000) != 0 ? 24 + LOG_2_TABLE[n2 >> 24 & 0xFF] : LOG_2_TABLE[n2 >> 16 & 0x10F]) : ((n2 & 0xFF00) != 0 ? 8 + LOG_2_TABLE[n2 >> 8 & 0xFF] : LOG_2_TABLE[n2 & 0xFF]);
    }

    private int trMedian3(int isa, int isaD, int isaN, int v1, int v2, int v3) {
        int[] SA = this.SA;
        int SA_v1 = this.trGetC(isa, isaD, isaN, SA[v1]);
        int SA_v2 = this.trGetC(isa, isaD, isaN, SA[v2]);
        int SA_v3 = this.trGetC(isa, isaD, isaN, SA[v3]);
        if (SA_v1 > SA_v2) {
            int temp = v1;
            v1 = v2;
            v2 = temp;
            int SA_vtemp = SA_v1;
            SA_v1 = SA_v2;
            SA_v2 = SA_vtemp;
        }
        if (SA_v2 > SA_v3) {
            if (SA_v1 > SA_v3) {
                return v1;
            }
            return v3;
        }
        return v2;
    }

    private int trMedian5(int isa, int isaD, int isaN, int v1, int v2, int v3, int v4, int v5) {
        int SA_vtemp;
        int temp;
        int[] SA = this.SA;
        int SA_v1 = this.trGetC(isa, isaD, isaN, SA[v1]);
        int SA_v2 = this.trGetC(isa, isaD, isaN, SA[v2]);
        int SA_v3 = this.trGetC(isa, isaD, isaN, SA[v3]);
        int SA_v4 = this.trGetC(isa, isaD, isaN, SA[v4]);
        int SA_v5 = this.trGetC(isa, isaD, isaN, SA[v5]);
        if (SA_v2 > SA_v3) {
            temp = v2;
            v2 = v3;
            v3 = temp;
            SA_vtemp = SA_v2;
            SA_v2 = SA_v3;
            SA_v3 = SA_vtemp;
        }
        if (SA_v4 > SA_v5) {
            temp = v4;
            v4 = v5;
            v5 = temp;
            SA_vtemp = SA_v4;
            SA_v4 = SA_v5;
            SA_v5 = SA_vtemp;
        }
        if (SA_v2 > SA_v4) {
            v4 = temp = v2;
            SA_v4 = SA_vtemp = SA_v2;
            temp = v3;
            v3 = v5;
            v5 = temp;
            SA_vtemp = SA_v3;
            SA_v3 = SA_v5;
            SA_v5 = SA_vtemp;
        }
        if (SA_v1 > SA_v3) {
            temp = v1;
            v1 = v3;
            v3 = temp;
            SA_vtemp = SA_v1;
            SA_v1 = SA_v3;
            SA_v3 = SA_vtemp;
        }
        if (SA_v1 > SA_v4) {
            v4 = temp = v1;
            SA_v4 = SA_vtemp = SA_v1;
            v3 = v5;
            SA_v3 = SA_v5;
        }
        if (SA_v3 > SA_v4) {
            return v4;
        }
        return v3;
    }

    private int trPivot(int isa, int isaD, int isaN, int first, int last) {
        int t2 = last - first;
        int middle = first + t2 / 2;
        if (t2 <= 512) {
            if (t2 <= 32) {
                return this.trMedian3(isa, isaD, isaN, first, middle, last - 1);
            }
            return this.trMedian5(isa, isaD, isaN, first, first + (t2 >>= 2), middle, last - 1 - t2, last - 1);
        }
        return this.trMedian3(isa, isaD, isaN, this.trMedian3(isa, isaD, isaN, first, first + (t2 >>= 3), first + (t2 << 1)), this.trMedian3(isa, isaD, isaN, middle - t2, middle, middle + t2), this.trMedian3(isa, isaD, isaN, last - 1 - (t2 << 1), last - 1 - t2, last - 1));
    }

    private void lsUpdateGroup(int isa, int first, int last) {
        int[] SA = this.SA;
        for (int a2 = first; a2 < last; ++a2) {
            int b2;
            if (0 <= SA[a2]) {
                b2 = a2;
                do {
                    SA[isa + SA[a2]] = a2;
                } while (++a2 < last && 0 <= SA[a2]);
                SA[b2] = b2 - a2;
                if (last <= a2) break;
            }
            b2 = a2;
            do {
                SA[a2] = ~SA[a2];
            } while (SA[++a2] < 0);
            int t2 = a2;
            do {
                SA[isa + SA[b2]] = t2;
            } while (++b2 <= a2);
        }
    }

    private void lsIntroSort(int isa, int isaD, int isaN, int first, int last) {
        int[] SA = this.SA;
        StackEntry[] stack = new StackEntry[64];
        int x2 = 0;
        int ssize = 0;
        int limit = Bzip2DivSufSort.trLog(last - first);
        while (true) {
            int c2;
            int b2;
            int a2;
            StackEntry entry;
            if (last - first <= 8) {
                if (1 < last - first) {
                    this.trInsertionSort(isa, isaD, isaN, first, last);
                    this.lsUpdateGroup(isa, first, last);
                } else if (last - first == 1) {
                    SA[first] = -1;
                }
                if (ssize == 0) {
                    return;
                }
                entry = stack[--ssize];
                first = entry.a;
                last = entry.b;
                limit = entry.c;
                continue;
            }
            if (limit-- == 0) {
                this.trHeapSort(isa, isaD, isaN, first, last - first);
                a2 = last - 1;
                while (first < a2) {
                    x2 = this.trGetC(isa, isaD, isaN, SA[a2]);
                    for (b2 = a2 - 1; first <= b2 && this.trGetC(isa, isaD, isaN, SA[b2]) == x2; --b2) {
                        SA[b2] = ~SA[b2];
                    }
                    a2 = b2;
                }
                this.lsUpdateGroup(isa, first, last);
                if (ssize == 0) {
                    return;
                }
                entry = stack[--ssize];
                first = entry.a;
                last = entry.b;
                limit = entry.c;
                continue;
            }
            a2 = this.trPivot(isa, isaD, isaN, first, last);
            Bzip2DivSufSort.swapElements(SA, first, SA, a2);
            int v2 = this.trGetC(isa, isaD, isaN, SA[first]);
            for (b2 = first + 1; b2 < last && (x2 = this.trGetC(isa, isaD, isaN, SA[b2])) == v2; ++b2) {
            }
            a2 = b2;
            if (a2 < last && x2 < v2) {
                while (++b2 < last && (x2 = this.trGetC(isa, isaD, isaN, SA[b2])) <= v2) {
                    if (x2 != v2) continue;
                    Bzip2DivSufSort.swapElements(SA, b2, SA, a2);
                    ++a2;
                }
            }
            for (c2 = last - 1; b2 < c2 && (x2 = this.trGetC(isa, isaD, isaN, SA[c2])) == v2; --c2) {
            }
            int d2 = c2;
            if (b2 < d2 && x2 > v2) {
                while (b2 < --c2 && (x2 = this.trGetC(isa, isaD, isaN, SA[c2])) >= v2) {
                    if (x2 != v2) continue;
                    Bzip2DivSufSort.swapElements(SA, c2, SA, d2);
                    --d2;
                }
            }
            while (b2 < c2) {
                Bzip2DivSufSort.swapElements(SA, b2, SA, c2);
                while (++b2 < c2 && (x2 = this.trGetC(isa, isaD, isaN, SA[b2])) <= v2) {
                    if (x2 != v2) continue;
                    Bzip2DivSufSort.swapElements(SA, b2, SA, a2);
                    ++a2;
                }
                while (b2 < --c2 && (x2 = this.trGetC(isa, isaD, isaN, SA[c2])) >= v2) {
                    if (x2 != v2) continue;
                    Bzip2DivSufSort.swapElements(SA, c2, SA, d2);
                    --d2;
                }
            }
            if (a2 <= d2) {
                c2 = b2 - 1;
                int s2 = a2 - first;
                int t2 = b2 - a2;
                if (s2 > t2) {
                    s2 = t2;
                }
                int e2 = first;
                int f2 = b2 - s2;
                while (0 < s2) {
                    Bzip2DivSufSort.swapElements(SA, e2, SA, f2);
                    --s2;
                    ++e2;
                    ++f2;
                }
                s2 = d2 - c2;
                t2 = last - d2 - 1;
                if (s2 > t2) {
                    s2 = t2;
                }
                e2 = b2;
                f2 = last - s2;
                while (0 < s2) {
                    Bzip2DivSufSort.swapElements(SA, e2, SA, f2);
                    --s2;
                    ++e2;
                    ++f2;
                }
                a2 = first + (b2 - a2);
                b2 = last - (d2 - c2);
                v2 = a2 - 1;
                for (c2 = first; c2 < a2; ++c2) {
                    SA[isa + SA[c2]] = v2;
                }
                if (b2 < last) {
                    v2 = b2 - 1;
                    for (c2 = a2; c2 < b2; ++c2) {
                        SA[isa + SA[c2]] = v2;
                    }
                }
                if (b2 - a2 == 1) {
                    SA[a2] = -1;
                }
                if (a2 - first <= last - b2) {
                    if (first < a2) {
                        stack[ssize++] = new StackEntry(b2, last, limit, 0);
                        last = a2;
                        continue;
                    }
                    first = b2;
                    continue;
                }
                if (b2 < last) {
                    stack[ssize++] = new StackEntry(first, a2, limit, 0);
                    first = b2;
                    continue;
                }
                last = a2;
                continue;
            }
            if (ssize == 0) {
                return;
            }
            entry = stack[--ssize];
            first = entry.a;
            last = entry.b;
            limit = entry.c;
        }
    }

    private void lsSort(int isa, int n2, int depth) {
        int[] SA = this.SA;
        int isaD = isa + depth;
        while (-n2 < SA[0]) {
            int last;
            int t2;
            int first = 0;
            int skip = 0;
            do {
                if ((t2 = SA[first]) < 0) {
                    first -= t2;
                    skip += t2;
                    continue;
                }
                if (skip != 0) {
                    SA[first + skip] = skip;
                    skip = 0;
                }
                last = SA[isa + t2] + 1;
                this.lsIntroSort(isa, isaD, isa + n2, first, last);
                first = last;
            } while (first < n2);
            if (skip != 0) {
                SA[first + skip] = skip;
            }
            if (n2 < isaD - isa) {
                first = 0;
                do {
                    if ((t2 = SA[first]) < 0) {
                        first -= t2;
                        continue;
                    }
                    last = SA[isa + t2] + 1;
                    for (int i2 = first; i2 < last; ++i2) {
                        SA[isa + SA[i2]] = i2;
                    }
                    first = last;
                } while (first < n2);
                break;
            }
            isaD += isaD - isa;
        }
    }

    private PartitionResult trPartition(int isa, int isaD, int isaN, int first, int last, int v2) {
        int c2;
        int b2;
        int[] SA = this.SA;
        int x2 = 0;
        for (b2 = first; b2 < last && (x2 = this.trGetC(isa, isaD, isaN, SA[b2])) == v2; ++b2) {
        }
        int a2 = b2;
        if (a2 < last && x2 < v2) {
            while (++b2 < last && (x2 = this.trGetC(isa, isaD, isaN, SA[b2])) <= v2) {
                if (x2 != v2) continue;
                Bzip2DivSufSort.swapElements(SA, b2, SA, a2);
                ++a2;
            }
        }
        for (c2 = last - 1; b2 < c2 && (x2 = this.trGetC(isa, isaD, isaN, SA[c2])) == v2; --c2) {
        }
        int d2 = c2;
        if (b2 < d2 && x2 > v2) {
            while (b2 < --c2 && (x2 = this.trGetC(isa, isaD, isaN, SA[c2])) >= v2) {
                if (x2 != v2) continue;
                Bzip2DivSufSort.swapElements(SA, c2, SA, d2);
                --d2;
            }
        }
        while (b2 < c2) {
            Bzip2DivSufSort.swapElements(SA, b2, SA, c2);
            while (++b2 < c2 && (x2 = this.trGetC(isa, isaD, isaN, SA[b2])) <= v2) {
                if (x2 != v2) continue;
                Bzip2DivSufSort.swapElements(SA, b2, SA, a2);
                ++a2;
            }
            while (b2 < --c2 && (x2 = this.trGetC(isa, isaD, isaN, SA[c2])) >= v2) {
                if (x2 != v2) continue;
                Bzip2DivSufSort.swapElements(SA, c2, SA, d2);
                --d2;
            }
        }
        if (a2 <= d2) {
            c2 = b2 - 1;
            int s2 = a2 - first;
            int t2 = b2 - a2;
            if (s2 > t2) {
                s2 = t2;
            }
            int e2 = first;
            int f2 = b2 - s2;
            while (0 < s2) {
                Bzip2DivSufSort.swapElements(SA, e2, SA, f2);
                --s2;
                ++e2;
                ++f2;
            }
            s2 = d2 - c2;
            t2 = last - d2 - 1;
            if (s2 > t2) {
                s2 = t2;
            }
            e2 = b2;
            f2 = last - s2;
            while (0 < s2) {
                Bzip2DivSufSort.swapElements(SA, e2, SA, f2);
                --s2;
                ++e2;
                ++f2;
            }
            first += b2 - a2;
            last -= d2 - c2;
        }
        return new PartitionResult(first, last);
    }

    private void trCopy(int isa, int isaN, int first, int a2, int b2, int last, int depth) {
        int s2;
        int c2;
        int[] SA = this.SA;
        int v2 = b2 - 1;
        int d2 = a2 - 1;
        for (c2 = first; c2 <= d2; ++c2) {
            s2 = SA[c2] - depth;
            if (s2 < 0) {
                s2 += isaN - isa;
            }
            if (SA[isa + s2] != v2) continue;
            SA[d2] = s2;
            SA[isa + s2] = ++d2;
        }
        c2 = last - 1;
        int e2 = d2 + 1;
        d2 = b2;
        while (e2 < d2) {
            s2 = SA[c2] - depth;
            if (s2 < 0) {
                s2 += isaN - isa;
            }
            if (SA[isa + s2] == v2) {
                SA[d2] = s2;
                SA[isa + s2] = --d2;
            }
            --c2;
        }
    }

    private void trIntroSort(int isa, int isaD, int isaN, int first, int last, TRBudget budget, int size) {
        int s2;
        int[] SA = this.SA;
        StackEntry[] stack = new StackEntry[64];
        int x2 = 0;
        int ssize = 0;
        int limit = Bzip2DivSufSort.trLog(last - first);
        while (true) {
            int next;
            StackEntry entry;
            int c2;
            int v2;
            int b2;
            int a2;
            if (limit < 0) {
                if (limit == -1) {
                    StackEntry entry2;
                    if (!budget.update(size, last - first)) break;
                    PartitionResult result = this.trPartition(isa, isaD - 1, isaN, first, last, last - 1);
                    a2 = result.first;
                    b2 = result.last;
                    if (first < a2 || b2 < last) {
                        if (a2 < last) {
                            v2 = a2 - 1;
                            for (c2 = first; c2 < a2; ++c2) {
                                SA[isa + SA[c2]] = v2;
                            }
                        }
                        if (b2 < last) {
                            v2 = b2 - 1;
                            for (c2 = a2; c2 < b2; ++c2) {
                                SA[isa + SA[c2]] = v2;
                            }
                        }
                        stack[ssize++] = new StackEntry(0, a2, b2, 0);
                        stack[ssize++] = new StackEntry(isaD - 1, first, last, -2);
                        if (a2 - first <= last - b2) {
                            if (1 < a2 - first) {
                                stack[ssize++] = new StackEntry(isaD, b2, last, Bzip2DivSufSort.trLog(last - b2));
                                last = a2;
                                limit = Bzip2DivSufSort.trLog(a2 - first);
                                continue;
                            }
                            if (1 < last - b2) {
                                first = b2;
                                limit = Bzip2DivSufSort.trLog(last - b2);
                                continue;
                            }
                            if (ssize == 0) {
                                return;
                            }
                            entry2 = stack[--ssize];
                            isaD = entry2.a;
                            first = entry2.b;
                            last = entry2.c;
                            limit = entry2.d;
                            continue;
                        }
                        if (1 < last - b2) {
                            stack[ssize++] = new StackEntry(isaD, first, a2, Bzip2DivSufSort.trLog(a2 - first));
                            first = b2;
                            limit = Bzip2DivSufSort.trLog(last - b2);
                            continue;
                        }
                        if (1 < a2 - first) {
                            last = a2;
                            limit = Bzip2DivSufSort.trLog(a2 - first);
                            continue;
                        }
                        if (ssize == 0) {
                            return;
                        }
                        entry2 = stack[--ssize];
                        isaD = entry2.a;
                        first = entry2.b;
                        last = entry2.c;
                        limit = entry2.d;
                        continue;
                    }
                    for (c2 = first; c2 < last; ++c2) {
                        SA[isa + SA[c2]] = c2;
                    }
                    if (ssize == 0) {
                        return;
                    }
                    entry2 = stack[--ssize];
                    isaD = entry2.a;
                    first = entry2.b;
                    last = entry2.c;
                    limit = entry2.d;
                    continue;
                }
                if (limit == -2) {
                    a2 = stack[--ssize].b;
                    b2 = stack[ssize].c;
                    this.trCopy(isa, isaN, first, a2, b2, last, isaD - isa);
                    if (ssize == 0) {
                        return;
                    }
                    entry = stack[--ssize];
                    isaD = entry.a;
                    first = entry.b;
                    last = entry.c;
                    limit = entry.d;
                    continue;
                }
                if (0 <= SA[first]) {
                    a2 = first;
                    do {
                        SA[isa + SA[a2]] = a2;
                    } while (++a2 < last && 0 <= SA[a2]);
                    first = a2;
                }
                if (first < last) {
                    a2 = first;
                    do {
                        SA[a2] = ~SA[a2];
                    } while (SA[++a2] < 0);
                    int n2 = next = SA[isa + SA[a2]] != SA[isaD + SA[a2]] ? Bzip2DivSufSort.trLog(a2 - first + 1) : -1;
                    if (++a2 < last) {
                        v2 = a2 - 1;
                        for (b2 = first; b2 < a2; ++b2) {
                            SA[isa + SA[b2]] = v2;
                        }
                    }
                    if (a2 - first <= last - a2) {
                        stack[ssize++] = new StackEntry(isaD, a2, last, -3);
                        ++isaD;
                        last = a2;
                        limit = next;
                        continue;
                    }
                    if (1 < last - a2) {
                        stack[ssize++] = new StackEntry(isaD + 1, first, a2, next);
                        first = a2;
                        limit = -3;
                        continue;
                    }
                    ++isaD;
                    last = a2;
                    limit = next;
                    continue;
                }
                if (ssize == 0) {
                    return;
                }
                entry = stack[--ssize];
                isaD = entry.a;
                first = entry.b;
                last = entry.c;
                limit = entry.d;
                continue;
            }
            if (last - first <= 8) {
                if (!budget.update(size, last - first)) break;
                this.trInsertionSort(isa, isaD, isaN, first, last);
                limit = -3;
                continue;
            }
            if (limit-- == 0) {
                if (!budget.update(size, last - first)) break;
                this.trHeapSort(isa, isaD, isaN, first, last - first);
                a2 = last - 1;
                while (first < a2) {
                    x2 = this.trGetC(isa, isaD, isaN, SA[a2]);
                    for (b2 = a2 - 1; first <= b2 && this.trGetC(isa, isaD, isaN, SA[b2]) == x2; --b2) {
                        SA[b2] = ~SA[b2];
                    }
                    a2 = b2;
                }
                limit = -3;
                continue;
            }
            a2 = this.trPivot(isa, isaD, isaN, first, last);
            Bzip2DivSufSort.swapElements(SA, first, SA, a2);
            v2 = this.trGetC(isa, isaD, isaN, SA[first]);
            for (b2 = first + 1; b2 < last && (x2 = this.trGetC(isa, isaD, isaN, SA[b2])) == v2; ++b2) {
            }
            a2 = b2;
            if (a2 < last && x2 < v2) {
                while (++b2 < last && (x2 = this.trGetC(isa, isaD, isaN, SA[b2])) <= v2) {
                    if (x2 != v2) continue;
                    Bzip2DivSufSort.swapElements(SA, b2, SA, a2);
                    ++a2;
                }
            }
            for (c2 = last - 1; b2 < c2 && (x2 = this.trGetC(isa, isaD, isaN, SA[c2])) == v2; --c2) {
            }
            int d2 = c2;
            if (b2 < d2 && x2 > v2) {
                while (b2 < --c2 && (x2 = this.trGetC(isa, isaD, isaN, SA[c2])) >= v2) {
                    if (x2 != v2) continue;
                    Bzip2DivSufSort.swapElements(SA, c2, SA, d2);
                    --d2;
                }
            }
            while (b2 < c2) {
                Bzip2DivSufSort.swapElements(SA, b2, SA, c2);
                while (++b2 < c2 && (x2 = this.trGetC(isa, isaD, isaN, SA[b2])) <= v2) {
                    if (x2 != v2) continue;
                    Bzip2DivSufSort.swapElements(SA, b2, SA, a2);
                    ++a2;
                }
                while (b2 < --c2 && (x2 = this.trGetC(isa, isaD, isaN, SA[c2])) >= v2) {
                    if (x2 != v2) continue;
                    Bzip2DivSufSort.swapElements(SA, c2, SA, d2);
                    --d2;
                }
            }
            if (a2 <= d2) {
                c2 = b2 - 1;
                s2 = a2 - first;
                int t2 = b2 - a2;
                if (s2 > t2) {
                    s2 = t2;
                }
                int e2 = first;
                int f2 = b2 - s2;
                while (0 < s2) {
                    Bzip2DivSufSort.swapElements(SA, e2, SA, f2);
                    --s2;
                    ++e2;
                    ++f2;
                }
                s2 = d2 - c2;
                t2 = last - d2 - 1;
                if (s2 > t2) {
                    s2 = t2;
                }
                e2 = b2;
                f2 = last - s2;
                while (0 < s2) {
                    Bzip2DivSufSort.swapElements(SA, e2, SA, f2);
                    --s2;
                    ++e2;
                    ++f2;
                }
                a2 = first + (b2 - a2);
                b2 = last - (d2 - c2);
                next = SA[isa + SA[a2]] != v2 ? Bzip2DivSufSort.trLog(b2 - a2) : -1;
                v2 = a2 - 1;
                for (c2 = first; c2 < a2; ++c2) {
                    SA[isa + SA[c2]] = v2;
                }
                if (b2 < last) {
                    v2 = b2 - 1;
                    for (c2 = a2; c2 < b2; ++c2) {
                        SA[isa + SA[c2]] = v2;
                    }
                }
                if (a2 - first <= last - b2) {
                    if (last - b2 <= b2 - a2) {
                        if (1 < a2 - first) {
                            stack[ssize++] = new StackEntry(isaD + 1, a2, b2, next);
                            stack[ssize++] = new StackEntry(isaD, b2, last, limit);
                            last = a2;
                            continue;
                        }
                        if (1 < last - b2) {
                            stack[ssize++] = new StackEntry(isaD + 1, a2, b2, next);
                            first = b2;
                            continue;
                        }
                        if (1 < b2 - a2) {
                            ++isaD;
                            first = a2;
                            last = b2;
                            limit = next;
                            continue;
                        }
                        if (ssize == 0) {
                            return;
                        }
                        entry = stack[--ssize];
                        isaD = entry.a;
                        first = entry.b;
                        last = entry.c;
                        limit = entry.d;
                        continue;
                    }
                    if (a2 - first <= b2 - a2) {
                        if (1 < a2 - first) {
                            stack[ssize++] = new StackEntry(isaD, b2, last, limit);
                            stack[ssize++] = new StackEntry(isaD + 1, a2, b2, next);
                            last = a2;
                            continue;
                        }
                        if (1 < b2 - a2) {
                            stack[ssize++] = new StackEntry(isaD, b2, last, limit);
                            ++isaD;
                            first = a2;
                            last = b2;
                            limit = next;
                            continue;
                        }
                        first = b2;
                        continue;
                    }
                    if (1 < b2 - a2) {
                        stack[ssize++] = new StackEntry(isaD, b2, last, limit);
                        stack[ssize++] = new StackEntry(isaD, first, a2, limit);
                        ++isaD;
                        first = a2;
                        last = b2;
                        limit = next;
                        continue;
                    }
                    stack[ssize++] = new StackEntry(isaD, b2, last, limit);
                    last = a2;
                    continue;
                }
                if (a2 - first <= b2 - a2) {
                    if (1 < last - b2) {
                        stack[ssize++] = new StackEntry(isaD + 1, a2, b2, next);
                        stack[ssize++] = new StackEntry(isaD, first, a2, limit);
                        first = b2;
                        continue;
                    }
                    if (1 < a2 - first) {
                        stack[ssize++] = new StackEntry(isaD + 1, a2, b2, next);
                        last = a2;
                        continue;
                    }
                    if (1 < b2 - a2) {
                        ++isaD;
                        first = a2;
                        last = b2;
                        limit = next;
                        continue;
                    }
                    stack[ssize++] = new StackEntry(isaD, first, last, limit);
                    continue;
                }
                if (last - b2 <= b2 - a2) {
                    if (1 < last - b2) {
                        stack[ssize++] = new StackEntry(isaD, first, a2, limit);
                        stack[ssize++] = new StackEntry(isaD + 1, a2, b2, next);
                        first = b2;
                        continue;
                    }
                    if (1 < b2 - a2) {
                        stack[ssize++] = new StackEntry(isaD, first, a2, limit);
                        ++isaD;
                        first = a2;
                        last = b2;
                        limit = next;
                        continue;
                    }
                    last = a2;
                    continue;
                }
                if (1 < b2 - a2) {
                    stack[ssize++] = new StackEntry(isaD, first, a2, limit);
                    stack[ssize++] = new StackEntry(isaD, b2, last, limit);
                    ++isaD;
                    first = a2;
                    last = b2;
                    limit = next;
                    continue;
                }
                stack[ssize++] = new StackEntry(isaD, first, a2, limit);
                first = b2;
                continue;
            }
            if (!budget.update(size, last - first)) break;
            ++limit;
            ++isaD;
        }
        for (s2 = 0; s2 < ssize; ++s2) {
            if (stack[s2].d != -3) continue;
            this.lsUpdateGroup(isa, stack[s2].b, stack[s2].c);
        }
    }

    private void trSort(int isa, int n2, int depth) {
        int[] SA = this.SA;
        int first = 0;
        if (-n2 < SA[0]) {
            TRBudget budget = new TRBudget(n2, Bzip2DivSufSort.trLog(n2) * 2 / 3 + 1);
            do {
                int t2;
                if ((t2 = SA[first]) < 0) {
                    first -= t2;
                    continue;
                }
                int last = SA[isa + t2] + 1;
                if (1 < last - first) {
                    this.trIntroSort(isa, isa + depth, isa + n2, first, last, budget, n2);
                    if (budget.chance == 0) {
                        if (0 < first) {
                            SA[0] = -first;
                        }
                        this.lsSort(isa, n2, depth);
                        break;
                    }
                }
                first = last;
            } while (first < n2);
        }
    }

    private static int BUCKET_B(int c0, int c1) {
        return c1 << 8 | c0;
    }

    private static int BUCKET_BSTAR(int c0, int c1) {
        return c0 << 8 | c1;
    }

    private int sortTypeBstar(int[] bucketA, int[] bucketB) {
        int c1;
        int t2;
        int c0;
        int ti1;
        int i2;
        byte[] T = this.T;
        int[] SA = this.SA;
        int n2 = this.n;
        int[] tempbuf = new int[256];
        boolean flag = true;
        for (i2 = 1; i2 < n2; ++i2) {
            if (T[i2 - 1] == T[i2]) continue;
            if ((T[i2 - 1] & 0xFF) <= (T[i2] & 0xFF)) break;
            flag = false;
            break;
        }
        i2 = n2 - 1;
        int m2 = n2;
        int ti = T[i2] & 0xFF;
        int t0 = T[0] & 0xFF;
        if (ti < t0 || T[i2] == T[0] && flag) {
            if (!flag) {
                int n3 = Bzip2DivSufSort.BUCKET_BSTAR(ti, t0);
                bucketB[n3] = bucketB[n3] + 1;
                SA[--m2] = i2;
            } else {
                int n4 = Bzip2DivSufSort.BUCKET_B(ti, t0);
                bucketB[n4] = bucketB[n4] + 1;
            }
            --i2;
            while (0 <= i2 && (ti = T[i2] & 0xFF) <= (ti1 = T[i2 + 1] & 0xFF)) {
                int n5 = Bzip2DivSufSort.BUCKET_B(ti, ti1);
                bucketB[n5] = bucketB[n5] + 1;
                --i2;
            }
        }
        while (0 <= i2) {
            do {
                int n6 = T[i2] & 0xFF;
                bucketA[n6] = bucketA[n6] + 1;
            } while (0 <= --i2 && (T[i2] & 0xFF) >= (T[i2 + 1] & 0xFF));
            if (0 > i2) continue;
            int n7 = Bzip2DivSufSort.BUCKET_BSTAR(T[i2] & 0xFF, T[i2 + 1] & 0xFF);
            bucketB[n7] = bucketB[n7] + 1;
            SA[--m2] = i2--;
            while (0 <= i2 && (ti = T[i2] & 0xFF) <= (ti1 = T[i2 + 1] & 0xFF)) {
                int n8 = Bzip2DivSufSort.BUCKET_B(ti, ti1);
                bucketB[n8] = bucketB[n8] + 1;
                --i2;
            }
        }
        if ((m2 = n2 - m2) == 0) {
            for (i2 = 0; i2 < n2; ++i2) {
                SA[i2] = i2;
            }
            return 0;
        }
        i2 = -1;
        int j2 = 0;
        for (c0 = 0; c0 < 256; ++c0) {
            t2 = i2 + bucketA[c0];
            bucketA[c0] = i2 + j2;
            i2 = t2 + bucketB[Bzip2DivSufSort.BUCKET_B(c0, c0)];
            for (c1 = c0 + 1; c1 < 256; ++c1) {
                bucketB[c0 << 8 | c1] = j2 += bucketB[Bzip2DivSufSort.BUCKET_BSTAR(c0, c1)];
                i2 += bucketB[Bzip2DivSufSort.BUCKET_B(c0, c1)];
            }
        }
        int PAb = n2 - m2;
        int ISAb = m2;
        i2 = m2 - 2;
        while (0 <= i2) {
            t2 = SA[PAb + i2];
            c0 = T[t2] & 0xFF;
            c1 = T[t2 + 1] & 0xFF;
            int n9 = Bzip2DivSufSort.BUCKET_BSTAR(c0, c1);
            int n10 = bucketB[n9] - 1;
            bucketB[n9] = n10;
            SA[n10] = i2--;
        }
        t2 = SA[PAb + m2 - 1];
        c0 = T[t2] & 0xFF;
        c1 = T[t2 + 1] & 0xFF;
        int n11 = Bzip2DivSufSort.BUCKET_BSTAR(c0, c1);
        int n12 = bucketB[n11] - 1;
        bucketB[n11] = n12;
        SA[n12] = m2 - 1;
        int[] buf2 = SA;
        int bufoffset = m2;
        int bufsize = n2 - 2 * m2;
        if (bufsize <= 256) {
            buf2 = tempbuf;
            bufoffset = 0;
            bufsize = 256;
        }
        c0 = 255;
        j2 = m2;
        while (0 < j2) {
            for (c1 = 255; c0 < c1; --c1) {
                i2 = bucketB[Bzip2DivSufSort.BUCKET_BSTAR(c0, c1)];
                if (1 < j2 - i2) {
                    this.subStringSort(PAb, i2, j2, buf2, bufoffset, bufsize, 2, SA[i2] == m2 - 1, n2);
                }
                j2 = i2;
            }
            --c0;
        }
        for (i2 = m2 - 1; 0 <= i2; --i2) {
            if (0 <= SA[i2]) {
                j2 = i2;
                do {
                    SA[ISAb + SA[i2]] = i2;
                } while (0 <= --i2 && 0 <= SA[i2]);
                SA[i2 + 1] = i2 - j2;
                if (i2 <= 0) break;
            }
            j2 = i2;
            do {
                SA[i2] = ~SA[i2];
                SA[ISAb + SA[i2]] = j2;
            } while (SA[--i2] < 0);
            SA[ISAb + SA[i2]] = j2;
        }
        this.trSort(ISAb, m2, 1);
        i2 = n2 - 1;
        j2 = m2;
        if ((T[i2] & 0xFF) < (T[0] & 0xFF) || T[i2] == T[0] && flag) {
            if (!flag) {
                SA[SA[ISAb + --j2]] = i2;
            }
            --i2;
            while (0 <= i2 && (T[i2] & 0xFF) <= (T[i2 + 1] & 0xFF)) {
                --i2;
            }
        }
        while (0 <= i2) {
            --i2;
            while (0 <= i2 && (T[i2] & 0xFF) >= (T[i2 + 1] & 0xFF)) {
                --i2;
            }
            if (0 > i2) continue;
            SA[SA[ISAb + --j2]] = i2--;
            while (0 <= i2 && (T[i2] & 0xFF) <= (T[i2 + 1] & 0xFF)) {
                --i2;
            }
        }
        i2 = n2 - 1;
        int k2 = m2 - 1;
        for (c0 = 255; 0 <= c0; --c0) {
            for (c1 = 255; c0 < c1; --c1) {
                t2 = i2 - bucketB[Bzip2DivSufSort.BUCKET_B(c0, c1)];
                bucketB[Bzip2DivSufSort.BUCKET_B((int)c0, (int)c1)] = i2 + 1;
                i2 = t2;
                j2 = bucketB[Bzip2DivSufSort.BUCKET_BSTAR(c0, c1)];
                while (j2 <= k2) {
                    SA[i2] = SA[k2];
                    --i2;
                    --k2;
                }
            }
            t2 = i2 - bucketB[Bzip2DivSufSort.BUCKET_B(c0, c0)];
            bucketB[Bzip2DivSufSort.BUCKET_B((int)c0, (int)c0)] = i2 + 1;
            if (c0 < 255) {
                bucketB[Bzip2DivSufSort.BUCKET_BSTAR((int)c0, (int)(c0 + 1))] = t2 + 1;
            }
            i2 = bucketA[c0];
        }
        return m2;
    }

    private int constructBWT(int[] bucketA, int[] bucketB) {
        int c0;
        int s1;
        int s2;
        int i2;
        byte[] T = this.T;
        int[] SA = this.SA;
        int n2 = this.n;
        int t2 = 0;
        int c2 = 0;
        int orig = -1;
        for (int c1 = 254; 0 <= c1; --c1) {
            i2 = bucketB[Bzip2DivSufSort.BUCKET_BSTAR(c1, c1 + 1)];
            t2 = 0;
            c2 = -1;
            for (int j2 = bucketA[c1 + 1]; i2 <= j2; --j2) {
                s1 = s2 = SA[j2];
                if (0 <= s2) {
                    if (--s2 < 0) {
                        s2 = n2 - 1;
                    }
                    if ((c0 = T[s2] & 0xFF) > c1) continue;
                    SA[j2] = ~s1;
                    if (0 < s2 && (T[s2 - 1] & 0xFF) > c0) {
                        s2 ^= 0xFFFFFFFF;
                    }
                    if (c2 == c0) {
                        SA[--t2] = s2;
                        continue;
                    }
                    if (0 <= c2) {
                        bucketB[Bzip2DivSufSort.BUCKET_B((int)c2, (int)c1)] = t2;
                    }
                    c2 = c0;
                    t2 = bucketB[Bzip2DivSufSort.BUCKET_B(c2, c1)] - 1;
                    SA[t2] = s2;
                    continue;
                }
                SA[j2] = ~s2;
            }
        }
        for (i2 = 0; i2 < n2; ++i2) {
            s1 = s2 = SA[i2];
            if (0 <= s2) {
                if (--s2 < 0) {
                    s2 = n2 - 1;
                }
                if ((c0 = T[s2] & 0xFF) >= (T[s2 + 1] & 0xFF)) {
                    if (0 < s2 && (T[s2 - 1] & 0xFF) < c0) {
                        s2 ^= 0xFFFFFFFF;
                    }
                    if (c0 == c2) {
                        SA[++t2] = s2;
                    } else {
                        if (c2 != -1) {
                            bucketA[c2] = t2;
                        }
                        c2 = c0;
                        t2 = bucketA[c2] + 1;
                        SA[t2] = s2;
                    }
                }
            } else {
                s1 ^= 0xFFFFFFFF;
            }
            if (s1 == 0) {
                SA[i2] = T[n2 - 1];
                orig = i2;
                continue;
            }
            SA[i2] = T[s1 - 1];
        }
        return orig;
    }

    public int bwt() {
        int[] SA = this.SA;
        byte[] T = this.T;
        int n2 = this.n;
        int[] bucketA = new int[256];
        int[] bucketB = new int[65536];
        if (n2 == 0) {
            return 0;
        }
        if (n2 == 1) {
            SA[0] = T[0];
            return 0;
        }
        int m2 = this.sortTypeBstar(bucketA, bucketB);
        if (0 < m2) {
            return this.constructBWT(bucketA, bucketB);
        }
        return 0;
    }

    private static class TRBudget {
        int budget;
        int chance;

        TRBudget(int budget, int chance) {
            this.budget = budget;
            this.chance = chance;
        }

        boolean update(int size, int n2) {
            this.budget -= n2;
            if (this.budget <= 0) {
                if (--this.chance == 0) {
                    return false;
                }
                this.budget += size;
            }
            return true;
        }
    }

    private static class PartitionResult {
        final int first;
        final int last;

        PartitionResult(int first, int last) {
            this.first = first;
            this.last = last;
        }
    }

    private static class StackEntry {
        final int a;
        final int b;
        final int c;
        final int d;

        StackEntry(int a2, int b2, int c2, int d2) {
            this.a = a2;
            this.b = b2;
            this.c = c2;
            this.d = d2;
        }
    }
}

