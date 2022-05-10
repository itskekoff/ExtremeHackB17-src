package net.minecraft.util.math;

import java.util.Random;
import java.util.UUID;
import net.minecraft.util.math.Vec3i;

public class MathHelper {
    public static final float SQRT_2 = MathHelper.sqrt(2.0f);
    private static final int SIN_BITS = 12;
    private static final int SIN_MASK = 4095;
    private static final int SIN_COUNT = 4096;
    public static final float PI = (float)Math.PI;
    public static final float PI2 = (float)Math.PI * 2;
    public static final float PId2 = 1.5707964f;
    private static final float radFull = (float)Math.PI * 2;
    private static final float degFull = 360.0f;
    private static final float radToIndex = 651.8986f;
    private static final float degToIndex = 11.377778f;
    public static final float deg2Rad = (float)Math.PI / 180;
    private static final float[] SIN_TABLE_FAST = new float[4096];
    public static boolean fastMath = false;
    private static final float[] SIN_TABLE = new float[65536];
    private static final Random RANDOM = new Random();
    private static final int[] MULTIPLY_DE_BRUIJN_BIT_POSITION;
    private static final double FRAC_BIAS;
    private static final double[] ASINE_TAB;
    private static final double[] COS_TAB;

    static {
        for (int i2 = 0; i2 < 65536; ++i2) {
            MathHelper.SIN_TABLE[i2] = (float)Math.sin((double)i2 * Math.PI * 2.0 / 65536.0);
        }
        for (int j2 = 0; j2 < 4096; ++j2) {
            MathHelper.SIN_TABLE_FAST[j2] = (float)Math.sin(((float)j2 + 0.5f) / 4096.0f * ((float)Math.PI * 2));
        }
        for (int k2 = 0; k2 < 360; k2 += 90) {
            MathHelper.SIN_TABLE_FAST[(int)((float)k2 * 11.377778f) & 4095] = (float)Math.sin((float)k2 * ((float)Math.PI / 180));
        }
        int[] arrn = new int[32];
        arrn[1] = 1;
        arrn[2] = 28;
        arrn[3] = 2;
        arrn[4] = 29;
        arrn[5] = 14;
        arrn[6] = 24;
        arrn[7] = 3;
        arrn[8] = 30;
        arrn[9] = 22;
        arrn[10] = 20;
        arrn[11] = 15;
        arrn[12] = 25;
        arrn[13] = 17;
        arrn[14] = 4;
        arrn[15] = 8;
        arrn[16] = 31;
        arrn[17] = 27;
        arrn[18] = 13;
        arrn[19] = 23;
        arrn[20] = 21;
        arrn[21] = 19;
        arrn[22] = 16;
        arrn[23] = 7;
        arrn[24] = 26;
        arrn[25] = 12;
        arrn[26] = 18;
        arrn[27] = 6;
        arrn[28] = 11;
        arrn[29] = 5;
        arrn[30] = 10;
        arrn[31] = 9;
        MULTIPLY_DE_BRUIJN_BIT_POSITION = arrn;
        FRAC_BIAS = Double.longBitsToDouble(4805340802404319232L);
        ASINE_TAB = new double[257];
        COS_TAB = new double[257];
        for (int l2 = 0; l2 < 257; ++l2) {
            double d0 = (double)l2 / 256.0;
            double d1 = Math.asin(d0);
            MathHelper.COS_TAB[l2] = Math.cos(d1);
            MathHelper.ASINE_TAB[l2] = d1;
        }
    }

    public static float sin(float value) {
        return fastMath ? SIN_TABLE_FAST[(int)(value * 651.8986f) & 0xFFF] : SIN_TABLE[(int)(value * 10430.378f) & 0xFFFF];
    }

    public static float cos(float value) {
        return fastMath ? SIN_TABLE_FAST[(int)((value + 1.5707964f) * 651.8986f) & 0xFFF] : SIN_TABLE[(int)(value * 10430.378f + 16384.0f) & 0xFFFF];
    }

    public static float sqrt(float value) {
        return (float)Math.sqrt(value);
    }

    public static float sqrt(double value) {
        return (float)Math.sqrt(value);
    }

    public static int floor(float value) {
        int i2 = (int)value;
        return value < (float)i2 ? i2 - 1 : i2;
    }

    public static int fastFloor(double value) {
        return (int)(value + 1024.0) - 1024;
    }

    public static int floor(double value) {
        int i2 = (int)value;
        return value < (double)i2 ? i2 - 1 : i2;
    }

    public static long lFloor(double value) {
        long i2 = (long)value;
        return value < (double)i2 ? i2 - 1L : i2;
    }

    public static int absFloor(double value) {
        return (int)(value >= 0.0 ? value : -value + 1.0);
    }

    public static float abs(float value) {
        return value >= 0.0f ? value : -value;
    }

    public static int abs(int value) {
        return value >= 0 ? value : -value;
    }

    public static int ceil(float value) {
        int i2 = (int)value;
        return value > (float)i2 ? i2 + 1 : i2;
    }

    public static int ceil(double value) {
        int i2 = (int)value;
        return value > (double)i2 ? i2 + 1 : i2;
    }

    public static int clamp(int num, int min, int max) {
        if (num < min) {
            return min;
        }
        return num > max ? max : num;
    }

    public static float clamp(float num, float min, float max) {
        if (num < min) {
            return min;
        }
        return num > max ? max : num;
    }

    public static double clamp(double num, double min, double max) {
        if (num < min) {
            return min;
        }
        return num > max ? max : num;
    }

    public static double clampedLerp(double lowerBnd, double upperBnd, double slide) {
        if (slide < 0.0) {
            return lowerBnd;
        }
        return slide > 1.0 ? upperBnd : lowerBnd + (upperBnd - lowerBnd) * slide;
    }

    public static double absMax(double p_76132_0_, double p_76132_2_) {
        if (p_76132_0_ < 0.0) {
            p_76132_0_ = -p_76132_0_;
        }
        if (p_76132_2_ < 0.0) {
            p_76132_2_ = -p_76132_2_;
        }
        return p_76132_0_ > p_76132_2_ ? p_76132_0_ : p_76132_2_;
    }

    public static int intFloorDiv(int p_76137_0_, int p_76137_1_) {
        return p_76137_0_ < 0 ? -((-p_76137_0_ - 1) / p_76137_1_) - 1 : p_76137_0_ / p_76137_1_;
    }

    public static int getInt(Random random, int minimum, int maximum) {
        return minimum >= maximum ? minimum : random.nextInt(maximum - minimum + 1) + minimum;
    }

    public static float nextFloat(Random random, float minimum, float maximum) {
        return minimum >= maximum ? minimum : random.nextFloat() * (maximum - minimum) + minimum;
    }

    public static double nextDouble(Random random, double minimum, double maximum) {
        return minimum >= maximum ? minimum : random.nextDouble() * (maximum - minimum) + minimum;
    }

    public static double average(long[] values) {
        long i2 = 0L;
        long[] arrl = values;
        int n2 = values.length;
        for (int i3 = 0; i3 < n2; ++i3) {
            long j2 = arrl[i3];
            i2 += j2;
        }
        return (double)i2 / (double)values.length;
    }

    public static boolean epsilonEquals(float p_180185_0_, float p_180185_1_) {
        return MathHelper.abs(p_180185_1_ - p_180185_0_) < 1.0E-5f;
    }

    public static int normalizeAngle(int p_180184_0_, int p_180184_1_) {
        return (p_180184_0_ % p_180184_1_ + p_180184_1_) % p_180184_1_;
    }

    public static float positiveModulo(float numerator, float denominator) {
        return (numerator % denominator + denominator) % denominator;
    }

    public static double func_191273_b(double p_191273_0_, double p_191273_2_) {
        return (p_191273_0_ % p_191273_2_ + p_191273_2_) % p_191273_2_;
    }

    public static float wrapDegrees(float value) {
        if ((value %= 360.0f) >= 180.0f) {
            value -= 360.0f;
        }
        if (value < -180.0f) {
            value += 360.0f;
        }
        return value;
    }

    public static double wrapDegrees(double value) {
        if ((value %= 360.0) >= 180.0) {
            value -= 360.0;
        }
        if (value < -180.0) {
            value += 360.0;
        }
        return value;
    }

    public static int clampAngle(int angle) {
        if ((angle %= 360) >= 180) {
            angle -= 360;
        }
        if (angle < -180) {
            angle += 360;
        }
        return angle;
    }

    public static int getInt(String value, int defaultValue) {
        try {
            return Integer.parseInt(value);
        }
        catch (Throwable var3) {
            return defaultValue;
        }
    }

    public static int getInt(String value, int defaultValue, int max) {
        return Math.max(max, MathHelper.getInt(value, defaultValue));
    }

    public static double getDouble(String value, double defaultValue) {
        try {
            return Double.parseDouble(value);
        }
        catch (Throwable var4) {
            return defaultValue;
        }
    }

    public static double getDouble(String value, double defaultValue, double max) {
        return Math.max(max, MathHelper.getDouble(value, defaultValue));
    }

    public static int smallestEncompassingPowerOfTwo(int value) {
        int i2 = value - 1;
        i2 |= i2 >> 1;
        i2 |= i2 >> 2;
        i2 |= i2 >> 4;
        i2 |= i2 >> 8;
        i2 |= i2 >> 16;
        return i2 + 1;
    }

    private static boolean isPowerOfTwo(int value) {
        return value != 0 && (value & value - 1) == 0;
    }

    public static int log2DeBruijn(int value) {
        value = MathHelper.isPowerOfTwo(value) ? value : MathHelper.smallestEncompassingPowerOfTwo(value);
        return MULTIPLY_DE_BRUIJN_BIT_POSITION[(int)((long)value * 125613361L >> 27) & 0x1F];
    }

    public static int log2(int value) {
        return MathHelper.log2DeBruijn(value) - (MathHelper.isPowerOfTwo(value) ? 0 : 1);
    }

    public static int roundUp(int number, int interval) {
        int i2;
        if (interval == 0) {
            return 0;
        }
        if (number == 0) {
            return interval;
        }
        if (number < 0) {
            interval *= -1;
        }
        return (i2 = number % interval) == 0 ? number : number + interval - i2;
    }

    public static int rgb(float rIn, float gIn, float bIn) {
        return MathHelper.rgb(MathHelper.floor(rIn * 255.0f), MathHelper.floor(gIn * 255.0f), MathHelper.floor(bIn * 255.0f));
    }

    public static int rgb(int rIn, int gIn, int bIn) {
        int i2 = (rIn << 8) + gIn;
        i2 = (i2 << 8) + bIn;
        return i2;
    }

    public static int multiplyColor(int p_180188_0_, int p_180188_1_) {
        int i2 = (p_180188_0_ & 0xFF0000) >> 16;
        int j2 = (p_180188_1_ & 0xFF0000) >> 16;
        int k2 = (p_180188_0_ & 0xFF00) >> 8;
        int l2 = (p_180188_1_ & 0xFF00) >> 8;
        int i1 = (p_180188_0_ & 0xFF) >> 0;
        int j1 = (p_180188_1_ & 0xFF) >> 0;
        int k1 = (int)((float)i2 * (float)j2 / 255.0f);
        int l1 = (int)((float)k2 * (float)l2 / 255.0f);
        int i22 = (int)((float)i1 * (float)j1 / 255.0f);
        return p_180188_0_ & 0xFF000000 | k1 << 16 | l1 << 8 | i22;
    }

    public static double frac(double number) {
        return number - Math.floor(number);
    }

    public static long getPositionRandom(Vec3i pos) {
        return MathHelper.getCoordinateRandom(pos.getX(), pos.getY(), pos.getZ());
    }

    public static long getCoordinateRandom(int x2, int y2, int z2) {
        long i2 = (long)(x2 * 3129871) ^ (long)z2 * 116129781L ^ (long)y2;
        i2 = i2 * i2 * 42317861L + i2 * 11L;
        return i2;
    }

    public static UUID getRandomUUID(Random rand) {
        long i2 = rand.nextLong() & 0xFFFFFFFFFFFF0FFFL | 0x4000L;
        long j2 = rand.nextLong() & 0x3FFFFFFFFFFFFFFFL | Long.MIN_VALUE;
        return new UUID(i2, j2);
    }

    public static UUID getRandomUUID() {
        return MathHelper.getRandomUUID(RANDOM);
    }

    public static double pct(double p_181160_0_, double p_181160_2_, double p_181160_4_) {
        return (p_181160_0_ - p_181160_2_) / (p_181160_4_ - p_181160_2_);
    }

    public static double atan2(double p_181159_0_, double p_181159_2_) {
        boolean flag2;
        boolean flag1;
        boolean flag;
        double d0 = p_181159_2_ * p_181159_2_ + p_181159_0_ * p_181159_0_;
        if (Double.isNaN(d0)) {
            return Double.NaN;
        }
        boolean bl2 = flag = p_181159_0_ < 0.0;
        if (flag) {
            p_181159_0_ = -p_181159_0_;
        }
        boolean bl3 = flag1 = p_181159_2_ < 0.0;
        if (flag1) {
            p_181159_2_ = -p_181159_2_;
        }
        boolean bl4 = flag2 = p_181159_0_ > p_181159_2_;
        if (flag2) {
            double d1 = p_181159_2_;
            p_181159_2_ = p_181159_0_;
            p_181159_0_ = d1;
        }
        double d9 = MathHelper.fastInvSqrt(d0);
        double d2 = FRAC_BIAS + (p_181159_0_ *= d9);
        int i2 = (int)Double.doubleToRawLongBits(d2);
        double d3 = ASINE_TAB[i2];
        double d4 = COS_TAB[i2];
        double d5 = d2 - FRAC_BIAS;
        double d6 = p_181159_0_ * d4 - (p_181159_2_ *= d9) * d5;
        double d7 = (6.0 + d6 * d6) * d6 * 0.16666666666666666;
        double d8 = d3 + d7;
        if (flag2) {
            d8 = 1.5707963267948966 - d8;
        }
        if (flag1) {
            d8 = Math.PI - d8;
        }
        if (flag) {
            d8 = -d8;
        }
        return d8;
    }

    public static double fastInvSqrt(double p_181161_0_) {
        double d0 = 0.5 * p_181161_0_;
        long i2 = Double.doubleToRawLongBits(p_181161_0_);
        i2 = 6910469410427058090L - (i2 >> 1);
        p_181161_0_ = Double.longBitsToDouble(i2);
        p_181161_0_ *= 1.5 - d0 * p_181161_0_ * p_181161_0_;
        return p_181161_0_;
    }

    public static int hsvToRGB(float hue, float saturation, float value) {
        float f6;
        float f5;
        float f4;
        int i2 = (int)(hue * 6.0f) % 6;
        float f2 = hue * 6.0f - (float)i2;
        float f1 = value * (1.0f - saturation);
        float f22 = value * (1.0f - f2 * saturation);
        float f3 = value * (1.0f - (1.0f - f2) * saturation);
        switch (i2) {
            case 0: {
                f4 = value;
                f5 = f3;
                f6 = f1;
                break;
            }
            case 1: {
                f4 = f22;
                f5 = value;
                f6 = f1;
                break;
            }
            case 2: {
                f4 = f1;
                f5 = value;
                f6 = f3;
                break;
            }
            case 3: {
                f4 = f1;
                f5 = f22;
                f6 = value;
                break;
            }
            case 4: {
                f4 = f3;
                f5 = f1;
                f6 = value;
                break;
            }
            case 5: {
                f4 = value;
                f5 = f1;
                f6 = f22;
                break;
            }
            default: {
                throw new RuntimeException("Something went wrong when converting from HSV to RGB. Input was " + hue + ", " + saturation + ", " + value);
            }
        }
        int j2 = MathHelper.clamp((int)(f4 * 255.0f), 0, 255);
        int k2 = MathHelper.clamp((int)(f5 * 255.0f), 0, 255);
        int l2 = MathHelper.clamp((int)(f6 * 255.0f), 0, 255);
        return j2 << 16 | k2 << 8 | l2;
    }

    public static int hash(int p_188208_0_) {
        p_188208_0_ ^= p_188208_0_ >>> 16;
        p_188208_0_ *= -2048144789;
        p_188208_0_ ^= p_188208_0_ >>> 13;
        p_188208_0_ *= -1028477387;
        p_188208_0_ ^= p_188208_0_ >>> 16;
        return p_188208_0_;
    }
}

