package optifine;

import net.minecraft.util.math.MathHelper;

public class MathUtils {
    public static int getAverage(int[] p_getAverage_0_) {
        if (p_getAverage_0_.length <= 0) {
            return 0;
        }
        int i2 = MathUtils.getSum(p_getAverage_0_);
        int j2 = i2 / p_getAverage_0_.length;
        return j2;
    }

    public static int getSum(int[] p_getSum_0_) {
        if (p_getSum_0_.length <= 0) {
            return 0;
        }
        int i2 = 0;
        for (int j2 = 0; j2 < p_getSum_0_.length; ++j2) {
            int k2 = p_getSum_0_[j2];
            i2 += k2;
        }
        return i2;
    }

    public static int roundDownToPowerOfTwo(int p_roundDownToPowerOfTwo_0_) {
        int i2 = MathHelper.smallestEncompassingPowerOfTwo(p_roundDownToPowerOfTwo_0_);
        return p_roundDownToPowerOfTwo_0_ == i2 ? i2 : i2 / 2;
    }

    public static boolean equalsDelta(float p_equalsDelta_0_, float p_equalsDelta_1_, float p_equalsDelta_2_) {
        return Math.abs(p_equalsDelta_0_ - p_equalsDelta_1_) <= p_equalsDelta_2_;
    }

    public static float toDeg(float p_toDeg_0_) {
        return p_toDeg_0_ * 180.0f / (float)Math.PI;
    }

    public static float toRad(float p_toRad_0_) {
        return p_toRad_0_ / 180.0f * (float)Math.PI;
    }
}

