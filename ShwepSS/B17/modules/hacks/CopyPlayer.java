package ShwepSS.B17.modules.hacks;

import ShwepSS.B17.ChatUtils;
import ShwepSS.B17.Utils.TimerUtils;
import ShwepSS.B17.modules.Category;
import ShwepSS.B17.modules.Module;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.RayTraceResult;

public class CopyPlayer
extends Module {
    public TimerUtils timer = new TimerUtils();
    public EntityOtherPlayerMP fakePlayer;

    public CopyPlayer() {
        super("\u041a\u043e\u043f\u0438\u044f \u043f\u043b\u0435\u0435\u0440\u0430", "\u043c\u043e\u0436\u043d\u043e \u0441\u043a\u043e\u043f\u0438\u0440\u043e\u0432\u0430\u0442\u044c \u0448\u043b*\u0445\u0443 \u0441\u0435\u0431\u0435 \u0432 \u0434\u043e\u043c", 0, Category.MISC, true);
    }

    @Override
    public void onEnable() {
    }

    @Override
    public void onTick() {
        Minecraft mc = Minecraft.getMinecraft();
        if (this.timer.check(100.0f)) {
            Entity entity;
            if (mc.gameSettings.keyBindAttack.pressed && mc.objectMouseOver != null && mc.objectMouseOver.typeOfHit == RayTraceResult.Type.ENTITY && (entity = mc.objectMouseOver.entityHit) instanceof EntityOtherPlayerMP) {
                EntityOtherPlayerMP other;
                this.fakePlayer = other = (EntityOtherPlayerMP)entity;
                this.fakePlayer.inventory = other.inventory;
                this.fakePlayer.setPositionAndRotation(mc.player.posX, mc.player.posY, mc.player.posZ, mc.player.rotationYaw, mc.player.rotationPitch);
                mc.world.addEntityToWorld(-1, this.fakePlayer);
                ChatUtils.emessage("\u0418\u0433\u0440\u043e\u043a \u0441\u043a\u043e\u043f\u0438\u0440\u043e\u0432\u0430\u043d");
            }
            if (mc.gameSettings.keyBindUseItem.pressed) {
                this.fakePlayer.setPositionAndRotation(mc.player.posX, mc.player.posY, mc.player.posZ, mc.player.rotationYaw, mc.player.rotationPitch);
                this.fakePlayer.posX = mc.player.posX;
                this.fakePlayer.posY = mc.player.posY;
                this.fakePlayer.posZ = mc.player.posZ;
                this.fakePlayer.prevPosX = mc.player.prevPosX;
                this.fakePlayer.prevPosY = mc.player.prevPosY;
                this.fakePlayer.prevPosZ = mc.player.prevPosZ;
                this.fakePlayer.prevChasingPosX = mc.player.prevChasingPosX;
                this.fakePlayer.prevChasingPosY = mc.player.prevChasingPosY;
                this.fakePlayer.prevChasingPosZ = mc.player.prevChasingPosZ;
            }
            this.timer.reset();
        }
    }

    @Override
    public void onDisable() {
    }
}

