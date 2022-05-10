package ShwepSS.B17.modules.hacks;

import ShwepSS.B17.Utils.MovementUtil;
import ShwepSS.B17.Utils.PvPUtil;
import ShwepSS.B17.modules.Category;
import ShwepSS.B17.modules.Module;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;

public class MegaStrafe
extends Module {
    public MegaStrafe() {
        super("MegaStrafe", "\u041d\u0435 \u043b\u0435\u0433\u0438\u0442 \u043c\u0435\u0433\u0430 \u0431\u044b\u0441\u0442\u0440\u044b\u0439 \u0441\u0442\u0440\u0435\u0439\u0444", 0, Category.Combat, true);
    }

    @Override
    public void onEnable() {
    }

    @Override
    public void onTick() {
        Minecraft mc = Minecraft.getMinecraft();
        for (Entity ent : mc.world.loadedEntityList) {
            if (ent == mc.player || !(ent instanceof EntityPlayer) || !(mc.player.getDistanceToEntity(ent) <= 6.0f)) continue;
            MovementUtil.setSpeed2(2.0);
            mc.player.setPosition(mc.player.posX, ent.getPosition().getY(), mc.player.posZ);
            EntityLivingBase en2 = (EntityLivingBase)ent;
            PvPUtil.faceEntity(en2);
            PvPUtil.getRotationsNeeded(en2);
            PvPUtil.onRotateToEntity(en2);
        }
    }

    @Override
    public void onDisable() {
    }
}

