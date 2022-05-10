package ShwepSS.B17.modules.hacks;

import ShwepSS.B17.ExtremeHack;
import ShwepSS.B17.Utils.MovementUtil;
import ShwepSS.B17.cg.settings.Setting;
import ShwepSS.B17.modules.Category;
import ShwepSS.B17.modules.Module;
import ShwepSS.event.EventBB;
import ShwepSS.event.EventPacketSend;
import ShwepSS.eventapi.EventManager;
import ShwepSS.eventapi.EventTarget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.client.CPacketUseEntity;

public class FreeCam
extends Module {
    public Setting speed = new Setting("Speed", this, 1.0, 0.1, 6.0, false);
    private EntityOtherPlayerMP fakePlayer = null;
    private double oldX;
    private double oldY;
    private double oldZ;

    public FreeCam() {
        super("FreeCam", "\u0421\u0432\u043e\u0431\u043e\u0434\u043d\u0430\u044f \u043a\u0430\u043c\u0435\u0440\u0430", 0, Category.Movement, true);
        ExtremeHack.instance.getSetmgr().rSetting(this.speed);
    }

    @Override
    public void onEnable() {
        EventManager.register(this);
        this.oldX = Minecraft.getMinecraft().player.posX;
        this.oldY = Minecraft.getMinecraft().player.posY;
        this.oldZ = Minecraft.getMinecraft().player.posZ;
        Minecraft.getMinecraft().player.noClip = true;
        EntityOtherPlayerMP fakePlayer = new EntityOtherPlayerMP(Minecraft.getMinecraft().world, Minecraft.getMinecraft().player.getGameProfile());
        fakePlayer.copyLocationAndAnglesFrom(Minecraft.getMinecraft().player);
        fakePlayer.posY -= 0.0;
        fakePlayer.rotationYawHead = Minecraft.getMinecraft().player.rotationYawHead;
        Minecraft.getMinecraft().world.addEntityToWorld(-69, fakePlayer);
    }

    @Override
    public void onDisable() {
        EventManager.unregister(this);
        Minecraft.getMinecraft().player.noClip = false;
        Minecraft.getMinecraft().player.setPositionAndRotation(this.oldX, this.oldY, this.oldZ, Minecraft.getMinecraft().player.rotationYaw, Minecraft.getMinecraft().player.rotationPitch);
        Minecraft.getMinecraft().world.removeEntityFromWorld(-69);
        this.fakePlayer = null;
    }

    @EventTarget
    public void onKek(EventPacketSend ev2) {
        if (ev2.packet instanceof CPacketPlayer || ev2.packet instanceof CPacketAnimation || ev2.packet instanceof CPacketEntityAction || ev2.packet instanceof CPacketUseEntity || ev2.packet instanceof CPacketHeldItemChange || ev2.packet instanceof CPacketPlayerDigging) {
            ev2.setCancelled(true);
        }
    }

    @EventTarget
    public void onBB(EventBB ev2) {
        ev2.boundingBox = null;
        ev2.setCancelled(true);
    }

    @Override
    public void onTick() {
        double xz2 = this.speed.getValFloat();
        Minecraft.getMinecraft().player.motionY = 0.0;
        Minecraft.getMinecraft().player.onGround = true;
        if (Minecraft.getMinecraft().gameSettings.keyBindJump.pressed) {
            Minecraft.getMinecraft().player.motionY = 0.9;
        }
        if (Minecraft.getMinecraft().gameSettings.keyBindSneak.pressed) {
            Minecraft.getMinecraft().player.motionY = -0.9;
        }
        if (Minecraft.getMinecraft().gameSettings.keyBindForward.pressed) {
            MovementUtil.setSpeed(xz2);
        }
        if (Minecraft.getMinecraft().gameSettings.keyBindBack.pressed) {
            MovementUtil.setSpeed(xz2);
        }
        if (Minecraft.getMinecraft().gameSettings.keyBindLeft.pressed) {
            MovementUtil.setSpeed(xz2);
        }
        if (Minecraft.getMinecraft().gameSettings.keyBindRight.pressed) {
            MovementUtil.setSpeed(xz2);
        }
    }
}

