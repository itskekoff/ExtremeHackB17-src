package ShwepSS.B17.modules.hacks;

import ShwepSS.B17.modules.Category;
import ShwepSS.B17.modules.Module;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.CPacketCreativeInventoryAction;
import net.minecraft.util.math.RayTraceResult;

public class KrazhaItems
extends Module {
    public KrazhaItems() {
        super("\u041a\u0440\u0430\u0436\u0430", "\u041c\u043e\u0436\u043d\u043e \u0443\u043a\u0440\u0430\u0441\u0442\u044c \u043f\u0440\u0435\u0434\u043c\u0435\u0442 \u0443 \u0438\u0433\u0440\u043e\u043a\u0430 \u0432 \u0440\u0443\u043a\u0435", 0, Category.MISC, true);
    }

    @Override
    public void onEnable() {
    }

    @Override
    public void onTick() {
        Minecraft mc = Minecraft.getMinecraft();
        try {
            Entity entity;
            if (mc.gameSettings.keyBindAttack.pressed && mc.objectMouseOver != null && mc.objectMouseOver.typeOfHit == RayTraceResult.Type.ENTITY && (entity = mc.objectMouseOver.entityHit) instanceof EntityPlayer) {
                EntityPlayer ent = (EntityPlayer)entity;
                mc.player.connection.sendPacket(new CPacketCreativeInventoryAction(5, ent.inventory.getStackInSlot(5).copy()));
                mc.player.connection.sendPacket(new CPacketCreativeInventoryAction(6, ent.inventory.getStackInSlot(6).copy()));
                mc.player.connection.sendPacket(new CPacketCreativeInventoryAction(mc.player.inventory.currentItem + 36, ent.getHeldItemMainhand().copy()));
                mc.player.connection.sendPacket(new CPacketCreativeInventoryAction(45, ent.getHeldItemOffhand().copy()));
            }
        }
        catch (Exception eg2) {
            eg2.printStackTrace();
        }
    }

    @Override
    public void onDisable() {
    }
}

