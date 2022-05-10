package ShwepSS.B17.modules.hacks;

import ShwepSS.B17.ExtremeHack;
import ShwepSS.B17.cg.settings.Setting;
import ShwepSS.B17.modules.Category;
import ShwepSS.B17.modules.Module;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

public class TargetStrafe
extends Module {
    public Setting sit = new Setting("AutoSit", this, false);

    public TargetStrafe() {
        super("TargetStrafe", "\u043e\u043a\u0440\u0443\u0436\u0430\u0435\u0442 \u0441\u0432\u043e\u044e \u0446\u0435\u043b\u044c", 0, Category.Combat, true);
        ExtremeHack.instance.getSetmgr().rSetting(this.sit);
    }

    @Override
    public void onEnable() {
    }

    @Override
    public void onTick() {
        Minecraft mc = Minecraft.getMinecraft();
        for (Entity ent : mc.world.loadedEntityList) {
            if (!(ent instanceof EntityPlayer) || Math.sqrt(Math.pow(mc.player.posX - ent.posX, 2.0) + Math.pow(mc.player.posZ - ent.posZ, 2.0)) == 0.0 || !(mc.player.getDistanceToEntity(ent) <= 4.0f)) continue;
            double c1 = (mc.player.posX - ent.posX) / Math.sqrt(Math.pow(mc.player.posX - ent.posX, 2.0) + Math.pow(mc.player.posZ - ent.posZ, 2.0));
            double s1 = (mc.player.posZ - ent.posZ) / Math.sqrt(Math.pow(mc.player.posX - ent.posX, 2.0) + Math.pow(mc.player.posZ - ent.posZ, 2.0));
            if (this.sit.getValue()) {
                mc.gameSettings.keyBindSneak.pressed = true;
            }
            if (mc.gameSettings.keyBindLeft.pressed) {
                mc.player.motionX = -s1 - 0.18 * c1;
                mc.player.motionZ = c1 - 0.18 * s1;
                continue;
            }
            mc.player.motionX = s1 - 0.18 * c1;
            mc.player.motionZ = -c1 - 0.18 * s1;
        }
    }

    @Override
    public void onDisable() {
    }
}

