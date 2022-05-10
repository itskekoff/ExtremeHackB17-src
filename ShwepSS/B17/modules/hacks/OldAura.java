package ShwepSS.B17.modules.hacks;

import ShwepSS.B17.HackConfigs;
import ShwepSS.B17.Utils.TimerUtils;
import ShwepSS.B17.modules.Category;
import ShwepSS.B17.modules.Module;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;

public class OldAura
extends Module {
    TimerUtils timer = new TimerUtils();

    public OldAura() {
        super("1.8 Aura", "\u043a\u0438\u043b\u043a\u0430 \u0434\u043b\u044f 1.8", 0, Category.Combat, true);
    }

    @Override
    public void onEnable() {
    }

    @Override
    public void onTick() {
        Minecraft mc = Minecraft.getMinecraft();
        double x2 = mc.player.posX;
        double y2 = mc.player.posY;
        double z2 = mc.player.posZ;
        for (Entity ent : Minecraft.getMinecraft().world.loadedEntityList) {
            if (!(ent instanceof EntityPlayer) || ent == Minecraft.getMinecraft().player || !(Minecraft.getMinecraft().player.getDistanceToEntity(ent) <= 6.0f) || !this.timer.check(530.0f)) continue;
            if (mc.player.onGround) {
                mc.player.jump();
            }
            if (ent.getName().contains("Shweps")) continue;
            Minecraft.getMinecraft().playerController.attackEntity(Minecraft.getMinecraft().player, ent);
            Minecraft.getMinecraft().player.swingArm(EnumHand.MAIN_HAND);
            Minecraft.getMinecraft().getConnection().getPlayerInfo(ent.getUniqueID());
            if (NetworkPlayerInfo.responseTime >= 0 && ent.getUniqueID() != null) {
                HackConfigs.Mudila = (EntityLivingBase)ent;
            }
            this.timer.reset();
        }
    }

    @Override
    public void onDisable() {
    }
}

