package io.netty.util.internal;

public final class ConstantTimeUtils {
    private ConstantTimeUtils() {
    }

    public static int equalsConstantTime(int x2, int y2) {
        int z2 = 0xFFFFFFFF ^ (x2 ^ y2);
        z2 &= z2 >> 16;
        z2 &= z2 >> 8;
        z2 &= z2 >> 4;
        z2 &= z2 >> 2;
        z2 &= z2 >> 1;
        return z2 & 1;
    }

    public static int equalsConstantTime(long x2, long y2) {
        long z2 = 0xFFFFFFFFFFFFFFFFL ^ (x2 ^ y2);
        z2 &= z2 >> 32;
        z2 &= z2 >> 16;
        z2 &= z2 >> 8;
        z2 &= z2 >> 4;
        z2 &= z2 >> 2;
        z2 &= z2 >> 1;
        return (int)(z2 & 1L);
    }

    public static int equalsConstantTime(byte[] bytes1, int startPos1, byte[] bytes2, int startPos2, int length) {
        int b2 = 0;
        int end = startPos1 + length;
        int i2 = startPos1;
        int j2 = startPos2;
        while (i2 < end) {
            b2 |= bytes1[i2] ^ bytes2[j2];
            ++i2;
            ++j2;
        }
        return ConstantTimeUtils.equalsConstantTime(b2, 0);
    }

    public static int equalsConstantTime(CharSequence s1, CharSequence s2) {
        if (s1.length() != s2.length()) {
            return 0;
        }
        int c2 = 0;
        for (int i2 = 0; i2 < s1.length(); ++i2) {
            c2 |= s1.charAt(i2) ^ s2.charAt(i2);
        }
        return ConstantTimeUtils.equalsConstantTime(c2, 0);
    }
}

