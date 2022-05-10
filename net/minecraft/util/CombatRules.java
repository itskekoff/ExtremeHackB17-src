package net.minecraft.util;

import net.minecraft.util.math.MathHelper;

public class CombatRules {
    public static float getDamageAfterAbsorb(float damage, float totalArmor, float toughnessAttribute) {
        float f2 = 2.0f + toughnessAttribute / 4.0f;
        float f1 = MathHelper.clamp(totalArmor - damage / f2, totalArmor * 0.2f, 20.0f);
        return damage * (1.0f - f1 / 25.0f);
    }

    public static float getDamageAfterMagicAbsorb(float p_188401_0_, float p_188401_1_) {
        float f2 = MathHelper.clamp(p_188401_1_, 0.0f, 20.0f);
        return p_188401_0_ * (1.0f - f2 / 25.0f);
    }
}

