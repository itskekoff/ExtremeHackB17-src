package ShwepSS.B17.modules.hacks;

import ShwepSS.B17.HackConfigs;
import ShwepSS.B17.Utils.RenderUtils;
import ShwepSS.B17.modules.Category;
import ShwepSS.B17.modules.Module;
import ShwepSS.event.Event3DRender;
import ShwepSS.eventapi.EventManager;
import ShwepSS.eventapi.EventTarget;
import java.awt.Color;
import java.util.ArrayList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.AxisAlignedBB;
import org.lwjgl.opengl.GL11;

public class ESPBox
extends Module {
    private final ArrayList<Entity> players = new ArrayList();
    private final ArrayList<EntityPlayer> players2 = new ArrayList();
    private final ArrayList<Blocks> chests = new ArrayList();
    int playerBox;

    public ESPBox() {
        super("Korobka-ESP", "\u0421\u0442\u0430\u043d\u0434\u0430\u0440\u0442\u043d\u044b\u0439 \u044d\u043a\u0441\u0442\u0440\u0438\u043c\u043e\u0432\u0441\u043a\u0438\u0439 \u0415\u0421\u041f", 45, Category.Visuals, true);
    }

    @Override
    public void onEnable() {
        this.playerBox = GL11.glGenLists(1);
        GL11.glNewList(this.playerBox, 4864);
        AxisAlignedBB bb2 = new AxisAlignedBB(-0.5, 0.0, -0.5, 0.5, 1.0, 0.5);
        RenderUtils.drawOutlinedBox(bb2);
        GL11.glEndList();
        EventManager.register(this);
        super.onEnable();
    }

    @Override
    public void onDisable() {
        EventManager.unregister(this);
        super.onDisable();
        this.players.clear();
        this.disabler();
    }

    @EventTarget
    public void on3D(Event3DRender e2) {
        for (Entity en2 : Minecraft.getMinecraft().world.loadedEntityList) {
            if (!(en2 instanceof EntityLivingBase) || !(en2 instanceof EntityPlayer) || en2.getName() == Minecraft.getMinecraft().player.getName()) continue;
            this.players.add(en2);
        }
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(2848);
        GL11.glLineWidth(20.0f);
        GL11.glDisable(3553);
        GL11.glDisable(2929);
        GL11.glPushMatrix();
        Minecraft.getMinecraft().getRenderManager();
        double d2 = -RenderManager.renderPosX;
        Minecraft.getMinecraft().getRenderManager();
        double d3 = -RenderManager.renderPosY;
        Minecraft.getMinecraft().getRenderManager();
        GL11.glTranslated(d2, d3, -RenderManager.renderPosZ);
        this.renderBoxes(e2.pticks());
        GL11.glLineWidth(1.0f);
        this.renderBoxes2(e2.pticks(), 0.19f, new Color(HackConfigs.ThemeColorGui).getRed(), new Color(HackConfigs.ThemeColorGui).getGreen(), new Color(HackConfigs.ThemeColorGui).getBlue());
        GL11.glLineWidth(4.0f);
        this.renderBoxes2(e2.pticks(), 0.18f, 0.1f, 0.6f, 0.7f);
        GL11.glLineWidth(3.0f);
        this.renderBoxes2(e2.pticks(), 0.17f, 0.0f, 0.0f, 1.0f);
        this.players.clear();
        GL11.glPopMatrix();
        GL11.glEnable(2929);
        GL11.glEnable(3553);
        GL11.glDisable(3042);
        GL11.glDisable(2848);
    }

    private void renderBoxes(double partialTicks) {
        for (Entity e2 : this.players) {
            EntityLivingBase en2 = (EntityLivingBase)e2;
            GL11.glPushMatrix();
            GL11.glTranslated(e2.prevPosX + (e2.posX - e2.prevPosX) * partialTicks, e2.prevPosY + (e2.posY - e2.prevPosY) * partialTicks, e2.prevPosZ + (e2.posZ - e2.prevPosZ) * partialTicks);
            GL11.glScaled((double)e2.width + 0.2, (double)e2.height + 0.2, (double)e2.width + 0.2);
            float red = 0.0f;
            if (!(e2 instanceof EntityPlayer)) continue;
            GL11.glRotatef(-e2.rotationYaw, 0.0f, -e2.rotationYaw, 0.0f);
            GL11.glColor4f(new Color(HackConfigs.ThemeColor).getRed(), new Color(HackConfigs.ThemeColor).getGreen(), new Color(HackConfigs.ThemeColor).getBlue(), 0.4f);
            GL11.glCallList(this.playerBox);
            GL11.glPopMatrix();
        }
    }

    private void renderBoxes2(double partialTicks, float hui, float pizda, float damazda, float jigurda) {
        for (Entity e2 : this.players) {
            EntityLivingBase en2 = (EntityLivingBase)e2;
            GL11.glPushMatrix();
            GL11.glTranslated(e2.prevPosX + (e2.posX - e2.prevPosX) * partialTicks, e2.prevPosY + (e2.posY - e2.prevPosY) * partialTicks, e2.prevPosZ + (e2.posZ - e2.prevPosZ) * partialTicks);
            GL11.glScaled(e2.width + hui, e2.height + hui, e2.width + hui);
            float red = 0.0f;
            if (!(e2 instanceof EntityPlayer)) continue;
            GL11.glRotatef(-e2.rotationYaw, 0.0f, -e2.rotationYaw, 0.0f);
            GL11.glColor4f(pizda, damazda, jigurda, 0.8f);
            GL11.glCallList(this.playerBox);
            GL11.glPopMatrix();
        }
    }

    public void disabler() {
        for (Entity en2 : Minecraft.getMinecraft().world.loadedEntityList) {
            if (!(en2 instanceof EntityLivingBase) || en2.getName() == Minecraft.getMinecraft().player.getName()) continue;
            this.players.add(en2);
        }
    }
}

