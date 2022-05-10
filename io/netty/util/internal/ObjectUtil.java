package io.netty.util.internal;

public final class ObjectUtil {
    private ObjectUtil() {
    }

    public static <T> T checkNotNull(T arg2, String text) {
        if (arg2 == null) {
            throw new NullPointerException(text);
        }
        return arg2;
    }

    public static int checkPositive(int i2, String name) {
        if (i2 <= 0) {
            throw new IllegalArgumentException(name + ": " + i2 + " (expected: > 0)");
        }
        return i2;
    }

    public static long checkPositive(long i2, String name) {
        if (i2 <= 0L) {
            throw new IllegalArgumentException(name + ": " + i2 + " (expected: > 0)");
        }
        return i2;
    }

    public static int checkPositiveOrZero(int i2, String name) {
        if (i2 < 0) {
            throw new IllegalArgumentException(name + ": " + i2 + " (expected: >= 0)");
        }
        return i2;
    }

    public static long checkPositiveOrZero(long i2, String name) {
        if (i2 < 0L) {
            throw new IllegalArgumentException(name + ": " + i2 + " (expected: >= 0)");
        }
        return i2;
    }

    public static <T> T[] checkNonEmpty(T[] array, String name) {
        ObjectUtil.checkNotNull(array, name);
        ObjectUtil.checkPositive(array.length, name + ".length");
        return array;
    }

    public static int intValue(Integer wrapper, int defaultValue) {
        return wrapper != null ? wrapper : defaultValue;
    }

    public static long longValue(Long wrapper, long defaultValue) {
        return wrapper != null ? wrapper : defaultValue;
    }
}

