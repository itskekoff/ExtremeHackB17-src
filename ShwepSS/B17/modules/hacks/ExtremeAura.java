package ShwepSS.B17.modules.hacks;

import ShwepSS.B17.ChatUtils;
import ShwepSS.B17.ExtremeHack;
import ShwepSS.B17.cg.settings.Setting;
import ShwepSS.B17.modules.Category;
import ShwepSS.B17.modules.Module;
import ShwepSS.event.EventPacketRecieve;
import ShwepSS.eventapi.EventManager;
import ShwepSS.eventapi.EventTarget;
import java.util.Iterator;
import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.server.SPacketSpawnPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.MathHelper;

public class ExtremeAura
extends Module {
    public static Module instance;
    Setting speed = new Setting("Speed", this, 1.0, -10.0, 10.0, false);
    Setting distance = new Setting("Speed", this, 5.0, 1.0, 7.0, false);
    public static Entity curTarget;
    private UUID detectedEntity;

    public ExtremeAura() {
        super("ExtremeAura", "\u043a\u0438\u043b\u043a\u0430", 19, Category.Combat, true);
        instance = this;
        curTarget = null;
        ExtremeHack.instance.getSetmgr().rSetting(this.distance);
        ExtremeHack.instance.getSetmgr().rSetting(this.speed);
    }

    @Override
    public void onEnable() {
        EventManager.register(this);
    }

    @Override
    public void onDisable() {
        EventManager.unregister(this);
    }

    @Override
    public void onTick() {
        this.m();
    }

    private void m() {
        Minecraft mc = Minecraft.getMinecraft();
        GameSettings.isKeyDown(mc.gameSettings.keyBindForward);
        for (Entity ent : mc.world.loadedEntityList) {
            if (!this.checks(ent)) continue;
            this.delPingBot();
            EntityLivingBase en2 = (EntityLivingBase)ent;
            ExtremeAura.faceEntity(en2);
            mc.player.setPosition(mc.player.posX, mc.player.posY + 0.04, mc.player.posZ);
            mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.01, mc.player.posZ, true));
            mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY, mc.player.posZ, false));
            mc.playerController.attackEntity(mc.player, en2);
            mc.player.swingArm(EnumHand.MAIN_HAND);
            mc.effectRenderer.emitParticleAtEntity(en2, EnumParticleTypes.CRIT);
            mc.effectRenderer.emitParticleAtEntity(en2, EnumParticleTypes.CRIT_MAGIC);
        }
    }

    @EventTarget
    public void onPacket(EventPacketRecieve event) {
        Minecraft mc = Minecraft.getMinecraft();
        if (event.getPacket() instanceof SPacketSpawnPlayer && mc.player.ticksExisted >= 9 && ((SPacketSpawnPlayer)event.getPacket()).getYaw() != 0 && ((SPacketSpawnPlayer)event.getPacket()).getPitch() != 0) {
            this.detectedEntity = ((SPacketSpawnPlayer)event.getPacket()).getUniqueId();
        }
    }

    boolean checks(Entity en2) {
        Minecraft mc = Minecraft.getMinecraft();
        float speedaura = this.speed.getValFloat();
        double dist = this.distance.getValDouble();
        if (!(en2 instanceof EntityLivingBase)) {
            return false;
        }
        if (en2 == mc.player) {
            return false;
        }
        if (en2.isDead) {
            return false;
        }
        if ((double)en2.getDistanceToEntity(mc.player) > dist) {
            return false;
        }
        if (en2.getName().contains("\u041c\u0410\u0413\u0410\u0417\u0418\u041d")) {
            return false;
        }
        if (mc.player.getCooledAttackStrength(speedaura + 0.0f) < 1.0f) {
            return false;
        }
        return !en2.isInvisibleToPlayer(mc.player);
    }

    public static synchronized void faceEntity(Entity entity) {
        float[] rotations = ExtremeAura.getRotationsNeeded(entity);
        if (rotations != null) {
            Minecraft mc = Minecraft.getMinecraft();
            Minecraft.getMinecraft().player.rotationYaw = rotations[0];
        }
    }

    public void delPingBot() {
        Minecraft mc = Minecraft.getMinecraft();
        Iterator var2 = mc.world.playerEntities.iterator();
        for (Entity e2 : mc.world.loadedEntityList) {
            if (e2.ticksExisted >= 5 || !(e2 instanceof EntityOtherPlayerMP) || ((EntityOtherPlayerMP)e2).hurtTime <= 0 || !(mc.player.getDistanceToEntity(e2) <= 25.0f) || mc.getConnection().getPlayerInfo(e2.getUniqueID()).getResponseTime() == 0) continue;
            mc.world.removeEntity(e2);
            ChatUtils.emessage("Removed bot: " + e2.getName());
        }
    }

    public static float[] getRotationsNeeded(Entity entity) {
        double diffY;
        EntityLivingBase entityLivingBase;
        Minecraft mc = Minecraft.getMinecraft();
        if (entity == null) {
            return null;
        }
        double diffX = entity.posX - Minecraft.getMinecraft().player.posX;
        double diffZ = entity.posZ - Minecraft.getMinecraft().player.posZ;
        if (entity instanceof EntityPlayer) {
            entityLivingBase = (EntityPlayer)entity;
            diffY = entityLivingBase.posY + (double)entityLivingBase.getEyeHeight() - (Minecraft.getMinecraft().player.posY + (double)Minecraft.getMinecraft().player.getEyeHeight());
        } else if (entity instanceof EntityMob) {
            entityLivingBase = (EntityMob)entity;
            diffY = (entity.boundingBox.minY + entity.boundingBox.maxY) / 1.0 - (Minecraft.getMinecraft().player.posY + (double)Minecraft.getMinecraft().player.getEyeHeight());
        } else {
            entityLivingBase = (EntityLivingBase)entity;
            diffY = (entity.boundingBox.minY + entity.boundingBox.maxY) / 1.0 - (Minecraft.getMinecraft().player.posY + (double)Minecraft.getMinecraft().player.getEyeHeight());
        }
        double dist = MathHelper.sqrt(diffX * diffX + diffZ * diffZ);
        float yaw = (float)(Math.atan2(diffZ, diffX) * 180.0 / Math.PI) - 95.0f;
        float pitch = (float)(-(Math.atan2(diffY, dist) * 180.0 / Math.PI));
        return new float[]{Minecraft.getMinecraft().player.rotationYaw + MathHelper.wrapDegrees(yaw - Minecraft.getMinecraft().player.rotationYaw), Minecraft.getMinecraft().player.rotationPitch + MathHelper.wrapDegrees(pitch - Minecraft.getMinecraft().player.rotationPitch)};
    }
}

