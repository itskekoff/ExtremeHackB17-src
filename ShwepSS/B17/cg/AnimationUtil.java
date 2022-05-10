package ShwepSS.B17.cg;

import net.minecraft.client.Minecraft;

public class AnimationUtil {
    public static float speedTarget = 0.125f;

    public static float animation(float current, float targetAnimation, float speed) {
        return AnimationUtil.animation(current, targetAnimation, speedTarget, speed);
    }

    public static float animation(float animation, float target, float poxyi, float speedTarget) {
        float da2 = (target - animation) / Math.max((float)Minecraft.getDebugFPS(), 5.0f) * 15.0f;
        if (da2 > 0.0f) {
            da2 = Math.max(speedTarget, da2);
            da2 = Math.min(target - animation, da2);
        } else if (da2 < 0.0f) {
            da2 = Math.min(-speedTarget, da2);
            da2 = Math.max(target - animation, da2);
        }
        return animation + da2;
    }

    public static double animate(double target, double current, double speed) {
        boolean larger = target > current;
        boolean bl2 = larger;
        if (speed < 0.0) {
            speed = 0.0;
        } else if (speed > 1.0) {
            speed = 1.0;
        }
        double dif = Math.max(target, current) - Math.min(target, current);
        double factor = dif * speed;
        if (factor < 0.1) {
            factor = 0.1;
        }
        current = larger ? (current = current + factor) : (current = current - factor);
        return current;
    }
}

