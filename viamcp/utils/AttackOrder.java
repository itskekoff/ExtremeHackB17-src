package viamcp.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.RayTraceResult;
import viamcp.ViaMCP;
import viamcp.protocols.ProtocolCollection;

public class AttackOrder {
    private static final Minecraft mc = Minecraft.getMinecraft();
    private static final int VER_1_8_ID = 47;

    public static void sendConditionalSwing(RayTraceResult ray, EnumHand enumHand) {
        if (ray != null && ray.typeOfHit != RayTraceResult.Type.ENTITY) {
            AttackOrder.mc.player.swingArm(enumHand);
        }
    }

    public static void sendFixedAttack(EntityPlayer entityIn, Entity target, EnumHand enumHand) {
        if (ViaMCP.getInstance().getVersion() <= ProtocolCollection.getProtocolById(47).getVersion()) {
            AttackOrder.send1_8Attack(entityIn, target, enumHand);
        } else {
            AttackOrder.send1_9Attack(entityIn, target, enumHand);
        }
    }

    private static void send1_8Attack(EntityPlayer entityIn, Entity target, EnumHand enumHand) {
        AttackOrder.mc.player.swingArm(enumHand);
        AttackOrder.mc.playerController.attackEntity(entityIn, target);
    }

    private static void send1_9Attack(EntityPlayer entityIn, Entity target, EnumHand enumHand) {
        AttackOrder.mc.playerController.attackEntity(entityIn, target);
        AttackOrder.mc.player.swingArm(enumHand);
    }
}

