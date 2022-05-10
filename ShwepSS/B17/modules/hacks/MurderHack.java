package ShwepSS.B17.modules.hacks;

import ShwepSS.B17.ChatUtils;
import ShwepSS.B17.HackConfigs;
import ShwepSS.B17.cg.font.FontUtil;
import ShwepSS.B17.modules.Category;
import ShwepSS.B17.modules.Module;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemSword;
import org.lwjgl.opengl.GL11;

public class MurderHack
extends Module {
    public EntityLivingBase holms = null;
    public static String nickKiller = "Empty";
    public static String nickHolms = "Empty";

    public MurderHack() {
        super("MurderHack", "\u041f\u043e\u043a\u0430\u0437\u044b\u0432\u0430\u0435\u0442 \u043c\u0443\u0434\u0438\u043b\u0443 \u0441 \u043c\u0435\u0447\u043e\u043c", -1, Category.Tesla, true);
    }

    @Override
    public void onEnable() {
    }

    @Override
    public void onDisable() {
    }

    @Override
    public void onTick() {
        Minecraft mc = Minecraft.getMinecraft();
        for (Entity ent : mc.world.loadedEntityList) {
            if (!(ent instanceof EntityPlayer) || ent == mc.player || !(((EntityLivingBase)ent).getHeldItemMainhand().getItem() instanceof ItemSword)) continue;
            HackConfigs.Mudila = (EntityLivingBase)ent;
            nickKiller = ent.getName();
            ent.setGlowing(true);
        }
        for (Entity ent : mc.world.loadedEntityList) {
            if (!(ent instanceof EntityPlayer) || !(((EntityLivingBase)ent).getHeldItemMainhand().getItem() instanceof ItemBow)) continue;
            this.holms = (EntityLivingBase)ent;
            nickHolms = ent.getName();
        }
    }

    @Override
    public void onRender() {
        Minecraft mc = Minecraft.getMinecraft();
        try {
            FontUtil.elegant_17.drawStringWithShadow(String.valueOf(ChatUtils.red) + "Killer: " + nickKiller, 3.0f, 55.0f, -1);
            FontUtil.elegant_17.drawStringWithShadow(String.valueOf(ChatUtils.green) + "Holms: " + nickHolms, 3.0f, 65.0f, -1);
            GL11.glPushMatrix();
            GL11.glEnable(2929);
            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
            if (HackConfigs.Mudila != null) {
                GuiInventory.drawEntityOnScreen(30, 165, 40, HackConfigs.Mudila.rotationYaw, HackConfigs.Mudila.rotationPitch, HackConfigs.Mudila);
            } else {
                GuiInventory.drawEntityOnScreen(30, 165, 40, mc.player.rotationYaw, mc.player.rotationPitch, mc.player);
            }
            GL11.glPopMatrix();
        }
        catch (Exception eg2) {
            eg2.printStackTrace();
        }
    }
}

