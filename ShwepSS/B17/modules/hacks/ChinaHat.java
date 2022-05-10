package ShwepSS.B17.modules.hacks;

import ShwepSS.B17.ExtremeHack;
import ShwepSS.B17.HackConfigs;
import ShwepSS.B17.cg.settings.Setting;
import ShwepSS.B17.modules.Category;
import ShwepSS.B17.modules.Module;
import ShwepSS.event.Event3DRender;
import ShwepSS.eventapi.EventManager;
import ShwepSS.eventapi.EventTarget;
import java.awt.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.Cylinder;

public class ChinaHat
extends Module {
    Setting side = new Setting("Sides", this, 30.0, 10.0, 100.0, true);
    Setting stack = new Setting("Stack", this, 30.0, 10.0, 100.0, true);
    Setting light = new Setting("\u042f\u0440\u043a\u043e\u0441\u0442\u044c", this, 0.2, 0.1, 10.0, false);
    Setting others = new Setting("Others", this, false);

    public ChinaHat() {
        super("ChinaHats", "\u041a\u0438\u0442\u0430\u0439\u0441\u043a\u0430\u044f \u0448\u043b\u044f\u043f\u0430 \u043d\u0430 \u0432\u0430\u0441/\u0438\u0433\u0440\u043e\u043a\u0430\u0445", 0, Category.Visuals, true);
        ExtremeHack.instance.getSetmgr().rSetting(this.side);
        ExtremeHack.instance.getSetmgr().rSetting(this.stack);
        ExtremeHack.instance.getSetmgr().rSetting(this.light);
        ExtremeHack.instance.getSetmgr().rSetting(this.others);
    }

    @Override
    public void onEnable() {
        EventManager.register(this);
    }

    @Override
    public void onDisable() {
        EventManager.unregister(this);
    }

    @EventTarget
    public void onKek(Event3DRender ev2) {
        try {
            this.drawChinaHat(Minecraft.getMinecraft().player);
            if (this.others.getValue()) {
                for (Entity en2 : Minecraft.getMinecraft().world.loadedEntityList) {
                    if (!(en2 instanceof EntityPlayer)) continue;
                    EntityLivingBase base = (EntityLivingBase)en2;
                    this.drawChinaHat(base);
                }
            }
        }
        catch (Exception eg2) {
            eg2.printStackTrace();
        }
    }

    private void drawChinaHat(EntityLivingBase entity) {
        Minecraft mc = Minecraft.getMinecraft();
        double d2 = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * (double)mc.timer.field_194147_b;
        mc.getRenderManager();
        double x2 = d2 - RenderManager.renderPosX;
        double d3 = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * (double)mc.timer.field_194147_b;
        mc.getRenderManager();
        double y2 = d3 - RenderManager.renderPosY;
        double d4 = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * (double)mc.timer.field_194147_b;
        mc.getRenderManager();
        double z2 = d4 - RenderManager.renderPosZ;
        int side = this.side.getValInt();
        int stack = this.stack.getValInt();
        GL11.glPushMatrix();
        GL11.glTranslated(x2, y2 + (mc.player.isSneaking() ? 1.8 : 2.2), z2);
        GL11.glRotatef(-entity.width, 0.0f, 1.0f, 0.0f);
        GL11.glColor4f(new Color(HackConfigs.ThemeColor).getRed() / 100, new Color(HackConfigs.ThemeColor).getGreen() / 100, new Color(HackConfigs.ThemeColor).getBlue() / 100, this.light.getValFloat());
        GL11.glDisable(3008);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glDisable(3553);
        GL11.glDisable(2929);
        GL11.glDepthMask(false);
        GL11.glEnable(2884);
        GL11.glEnable(2848);
        GL11.glHint(3154, 4354);
        GL11.glHint(3155, 4354);
        GL11.glLineWidth(1.0f);
        Cylinder c2 = new Cylinder();
        GL11.glRotatef(90.0f, 1.0f, 0.0f, 0.0f);
        c2.setDrawStyle(100011);
        c2.draw(0.0f, 0.8f, 0.4f, side, stack);
        GL11.glEnable(3553);
        GL11.glEnable(2929);
        GL11.glDisable(3042);
        GL11.glEnable(3008);
        GL11.glDepthMask(true);
        GL11.glCullFace(1029);
        GL11.glDisable(2848);
        GL11.glHint(3154, 4352);
        GL11.glHint(3155, 4352);
        GL11.glPopMatrix();
    }
}

