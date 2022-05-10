package net.minecraft.util;

public class IntegerCache {
    private static final Integer[] CACHE = new Integer[65535];

    static {
        int j2 = CACHE.length;
        for (int i2 = 0; i2 < j2; ++i2) {
            IntegerCache.CACHE[i2] = i2;
        }
    }

    public static Integer getInteger(int value) {
        return value > 0 && value < CACHE.length ? CACHE[value] : value;
    }
}

