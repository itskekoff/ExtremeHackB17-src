package ShwepSS.B17.Utils;

import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class RotationUtils {
    private static float serverYaw;
    private static float serverPitch;

    public static float[] getRotations(Entity ent) {
        double x2 = ent.posX;
        double z2 = ent.posZ;
        double y2 = ent.boundingBox.maxY - 4.0;
        return RotationUtils.getRotationFromPosition(x2, z2, y2);
    }

    public static Vec3d getEyesPos() {
        return new Vec3d(Minecraft.getMinecraft().player.posX, Minecraft.getMinecraft().player.posY + (double)Minecraft.getMinecraft().player.getEyeHeight(), Minecraft.getMinecraft().player.posZ);
    }

    public static Vec3d getClientLookVec() {
        float f2 = RotationUtils.cos(-Minecraft.getMinecraft().player.rotationYaw * ((float)Math.PI / 180) - (float)Math.PI);
        float f1 = RotationUtils.sin(-Minecraft.getMinecraft().player.rotationYaw * ((float)Math.PI / 180) - (float)Math.PI);
        float f22 = -RotationUtils.cos(-Minecraft.getMinecraft().player.rotationPitch * ((float)Math.PI / 180));
        float f3 = RotationUtils.sin(-Minecraft.getMinecraft().player.rotationPitch * ((float)Math.PI / 180));
        return new Vec3d(f1 * f22, f3, f2 * f22);
    }

    public static Vec3d getServerLookVec() {
        float f2 = RotationUtils.cos(-serverYaw * ((float)Math.PI / 180) - (float)Math.PI);
        float f1 = RotationUtils.sin(-serverYaw * ((float)Math.PI / 180) - (float)Math.PI);
        float f22 = -RotationUtils.cos(-serverPitch * ((float)Math.PI / 180));
        float f3 = RotationUtils.sin(-serverPitch * ((float)Math.PI / 180));
        return new Vec3d(f1 * f22, f3, f2 * f22);
    }

    public static float[] getAverageRotations(List<EntityLivingBase> targetList) {
        double posX = 0.0;
        double posY = 0.0;
        double posZ = 0.0;
        for (Entity entity : targetList) {
            posX += entity.posX;
            posY += entity.boundingBox.maxY - 2.0;
            posZ += entity.posZ;
        }
        return new float[]{RotationUtils.getRotationFromPosition(posX /= (double)targetList.size(), posZ /= (double)targetList.size(), posY /= (double)targetList.size())[0], RotationUtils.getRotationFromPosition(posX, posZ, posY)[1]};
    }

    public static float[] getRotationFromPosition(double x2, double z2, double y2) {
        double xDiff = x2 - Minecraft.getMinecraft().player.posX;
        double zDiff = z2 - Minecraft.getMinecraft().player.posZ;
        double yDiff = y2 - Minecraft.getMinecraft().player.posY + (double)Minecraft.getMinecraft().player.getEyeHeight();
        double dist = MathHelper.sqrt(xDiff * xDiff + zDiff * zDiff);
        float yaw = (float)(Math.atan2(zDiff, xDiff) * 180.0 / Math.PI) - 90.0f;
        float pitch = (float)(-(Math.atan2(yDiff, dist) * 180.0 / Math.PI));
        return new float[]{yaw, pitch};
    }

    public static float getTrajAngleSolutionLow(float d3, float d1, float velocity) {
        float g2 = 0.006f;
        float sqrt = velocity * velocity * velocity * velocity - 0.006f * (0.006f * (d3 * d3) + 2.0f * d1 * (velocity * velocity));
        return (float)Math.toDegrees(Math.atan(((double)(velocity * velocity) - Math.sqrt(sqrt)) / (double)(0.006f * d3)));
    }

    public static float getNewAngle(float angle) {
        if ((angle %= 360.0f) >= 180.0f) {
            angle -= 360.0f;
        }
        if (angle < -180.0f) {
            angle += 360.0f;
        }
        return angle;
    }

    public static float getDistanceBetweenAngles(float angle1, float angle2) {
        float angle3 = Math.abs(angle1 - angle2) % 360.0f;
        if (angle3 > 180.0f) {
            angle3 = 360.0f - angle3;
        }
        return angle3;
    }

    public static int clamp(int num, int min, int max) {
        return num < min ? min : (num > max ? max : num);
    }

    public static float clamp(float num, float min, float max) {
        return num < min ? min : (num > max ? max : num);
    }

    public static double clamp(double num, double min, double max) {
        return num < min ? min : (num > max ? max : num);
    }

    public static float sin(float value) {
        return MathHelper.sin(value);
    }

    public static float cos(float value) {
        return MathHelper.cos(value);
    }

    public static float wrapDegrees(float value) {
        return MathHelper.wrapDegrees(value);
    }

    public static double wrapDegrees(double value) {
        return MathHelper.wrapDegrees(value);
    }
}

