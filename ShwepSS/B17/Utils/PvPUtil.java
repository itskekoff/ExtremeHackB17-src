package ShwepSS.B17.Utils;

import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.MathHelper;

public class PvPUtil {
    public static void onRotateToEntity(Entity e2) {
        Minecraft mc = Minecraft.getMinecraft();
        List list = mc.world.playerEntities;
        for (int k2 = 0; k2 < list.size(); ++k2) {
            float f2;
            if (((EntityPlayer)list.get(k2)).getName() == mc.player.getName()) {
                return;
            }
            EntityPlayer entityplayer = (EntityPlayer)list.get(1);
            if (mc.player.getDistanceToEntity(entityplayer) > mc.player.getDistanceToEntity((Entity)list.get(k2))) {
                entityplayer = (EntityPlayer)list.get(k2);
            }
            if (!((f2 = mc.player.getDistanceToEntity(entityplayer)) < 8.0f) || !mc.player.canEntityBeSeen(entityplayer)) continue;
            PvPUtil.faceEntity(entityplayer);
        }
    }

    public static synchronized void faceEntity(EntityLivingBase entity) {
        float[] rotations = PvPUtil.getRotationsNeeded(entity);
        Minecraft mc = Minecraft.getMinecraft();
        if (rotations != null) {
            mc.player.rotationYaw = rotations[0];
            mc.player.rotationPitch = rotations[1] + 1.0f;
        }
    }

    public static float[] getRotationsNeeded(Entity entity) {
        double diffY;
        Minecraft mc = Minecraft.getMinecraft();
        if (entity == null) {
            return null;
        }
        double diffX = entity.posX - mc.player.posX;
        double diffZ = entity.posZ - mc.player.posZ;
        if (entity instanceof EntityLivingBase) {
            EntityLivingBase entityLivingBase = (EntityLivingBase)entity;
            diffY = entityLivingBase.posY + (double)entityLivingBase.getEyeHeight() - (mc.player.posY + (double)mc.player.getEyeHeight());
        } else {
            diffY = (entity.boundingBox.minY + entity.boundingBox.maxY) / 2.0 - (mc.player.posY + (double)mc.player.getEyeHeight());
        }
        double dist = MathHelper.sqrt(diffX * diffX + diffZ * diffZ);
        float yaw = (float)(Math.atan2(diffZ, diffX) * 180.0 / Math.PI) - 90.0f;
        float pitch = (float)(-(Math.atan2(diffY, dist) * 180.0 / Math.PI));
        return new float[]{mc.player.rotationYaw + MathHelper.wrapDegrees(yaw - mc.player.rotationYaw), mc.player.rotationPitch + MathHelper.wrapDegrees(pitch - mc.player.rotationPitch)};
    }
}

