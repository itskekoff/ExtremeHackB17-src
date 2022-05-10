package com.google.common.cache;

import com.google.common.annotations.GwtIncompatible;
import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Random;
import sun.misc.Unsafe;

@GwtIncompatible
abstract class Striped64
extends Number {
    static final ThreadLocal<int[]> threadHashCode = new ThreadLocal();
    static final Random rng = new Random();
    static final int NCPU = Runtime.getRuntime().availableProcessors();
    volatile transient Cell[] cells;
    volatile transient long base;
    volatile transient int busy;
    private static final Unsafe UNSAFE;
    private static final long baseOffset;
    private static final long busyOffset;

    Striped64() {
    }

    final boolean casBase(long cmp, long val) {
        return UNSAFE.compareAndSwapLong(this, baseOffset, cmp, val);
    }

    final boolean casBusy() {
        return UNSAFE.compareAndSwapInt(this, busyOffset, 0, 1);
    }

    abstract long fn(long var1, long var3);

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    final void retryUpdate(long x2, int[] hc2, boolean wasUncontended) {
        int h2;
        if (hc2 == null) {
            hc2 = new int[1];
            threadHashCode.set(hc2);
            int r2 = rng.nextInt();
            hc2[0] = r2 == 0 ? 1 : r2;
            h2 = hc2[0];
        } else {
            h2 = hc2[0];
        }
        boolean collide = false;
        while (true) {
            long v2;
            int n2;
            Cell[] as2 = this.cells;
            if (this.cells != null && (n2 = as2.length) > 0) {
                Cell a2 = as2[n2 - 1 & h2];
                if (a2 == null) {
                    if (this.busy == 0) {
                        Cell r3 = new Cell(x2);
                        if (this.busy == 0 && this.casBusy()) {
                            boolean created = false;
                            try {
                                int j2;
                                int m2;
                                Cell[] rs2 = this.cells;
                                if (this.cells != null && (m2 = rs2.length) > 0 && rs2[j2 = m2 - 1 & h2] == null) {
                                    rs2[j2] = r3;
                                    created = true;
                                }
                            }
                            finally {
                                this.busy = 0;
                            }
                            if (!created) continue;
                            return;
                        }
                    }
                    collide = false;
                } else if (!wasUncontended) {
                    wasUncontended = true;
                } else {
                    v2 = a2.value;
                    if (a2.cas(v2, this.fn(v2, x2))) return;
                    if (n2 >= NCPU || this.cells != as2) {
                        collide = false;
                    } else if (!collide) {
                        collide = true;
                    } else if (this.busy == 0 && this.casBusy()) {
                        try {
                            if (this.cells == as2) {
                                Cell[] rs3 = new Cell[n2 << 1];
                                for (int i2 = 0; i2 < n2; ++i2) {
                                    rs3[i2] = as2[i2];
                                }
                                this.cells = rs3;
                            }
                        }
                        finally {
                            this.busy = 0;
                        }
                        collide = false;
                        continue;
                    }
                }
                h2 ^= h2 << 13;
                h2 ^= h2 >>> 17;
                h2 ^= h2 << 5;
                hc2[0] = h2;
                continue;
            }
            if (this.busy == 0 && this.cells == as2 && this.casBusy()) {
                boolean init = false;
                try {
                    if (this.cells == as2) {
                        Cell[] rs4 = new Cell[2];
                        rs4[h2 & 1] = new Cell(x2);
                        this.cells = rs4;
                        init = true;
                    }
                }
                finally {
                    this.busy = 0;
                }
                if (!init) continue;
                return;
            }
            v2 = this.base;
            if (this.casBase(v2, this.fn(v2, x2))) return;
        }
    }

    final void internalReset(long initialValue) {
        Cell[] as2 = this.cells;
        this.base = initialValue;
        if (as2 != null) {
            for (Cell a2 : as2) {
                if (a2 == null) continue;
                a2.value = initialValue;
            }
        }
    }

    private static Unsafe getUnsafe() {
        try {
            return Unsafe.getUnsafe();
        }
        catch (SecurityException securityException) {
            try {
                return AccessController.doPrivileged(new PrivilegedExceptionAction<Unsafe>(){

                    @Override
                    public Unsafe run() throws Exception {
                        Class<Unsafe> k2 = Unsafe.class;
                        for (Field f2 : k2.getDeclaredFields()) {
                            f2.setAccessible(true);
                            Object x2 = f2.get(null);
                            if (!k2.isInstance(x2)) continue;
                            return (Unsafe)k2.cast(x2);
                        }
                        throw new NoSuchFieldError("the Unsafe");
                    }
                });
            }
            catch (PrivilegedActionException e2) {
                throw new RuntimeException("Could not initialize intrinsics", e2.getCause());
            }
        }
    }

    static {
        try {
            UNSAFE = Striped64.getUnsafe();
            Class<Striped64> sk2 = Striped64.class;
            baseOffset = UNSAFE.objectFieldOffset(sk2.getDeclaredField("base"));
            busyOffset = UNSAFE.objectFieldOffset(sk2.getDeclaredField("busy"));
        }
        catch (Exception e2) {
            throw new Error(e2);
        }
    }

    static final class Cell {
        volatile long p0;
        volatile long p1;
        volatile long p2;
        volatile long p3;
        volatile long p4;
        volatile long p5;
        volatile long p6;
        volatile long value;
        volatile long q0;
        volatile long q1;
        volatile long q2;
        volatile long q3;
        volatile long q4;
        volatile long q5;
        volatile long q6;
        private static final Unsafe UNSAFE;
        private static final long valueOffset;

        Cell(long x2) {
            this.value = x2;
        }

        final boolean cas(long cmp, long val) {
            return UNSAFE.compareAndSwapLong(this, valueOffset, cmp, val);
        }

        static {
            try {
                UNSAFE = Striped64.getUnsafe();
                Class<Cell> ak2 = Cell.class;
                valueOffset = UNSAFE.objectFieldOffset(ak2.getDeclaredField("value"));
            }
            catch (Exception e2) {
                throw new Error(e2);
            }
        }
    }
}

