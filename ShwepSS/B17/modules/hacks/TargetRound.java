package ShwepSS.B17.modules.hacks;

import ShwepSS.B17.HackConfigs;
import ShwepSS.B17.modules.Category;
import ShwepSS.B17.modules.Module;
import ShwepSS.event.Event3DRender;
import ShwepSS.eventapi.EventManager;
import ShwepSS.eventapi.EventTarget;
import java.awt.Color;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.opengl.GL11;

public class TargetRound
extends Module {
    public double radius = 3.0;
    public double distance = 5.0;
    private int dissCount;
    private float diss;
    private boolean dissAt;
    public static List<EntityLivingBase> targets;
    public static int index;
    private Minecraft mc;
    private double time;
    private boolean down;
    private float s;

    public TargetRound() {
        super("TargetRound", "", 19, Category.Visuals, true);
    }

    @Override
    public void onEnable() {
        EventManager.register(this);
    }

    @Override
    public void onDisable() {
        EventManager.unregister(this);
    }

    public static void startSmooth() {
        GL11.glEnable(2848);
        GL11.glEnable(2881);
        GL11.glEnable(2832);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glHint(3154, 4354);
        GL11.glHint(3155, 4354);
        GL11.glHint(3153, 4354);
    }

    public static void endSmooth() {
        GL11.glDisable(2848);
        GL11.glDisable(2881);
        GL11.glEnable(2832);
    }

    @EventTarget
    public void onkeke(Event3DRender event) {
        int j2;
        EntityPlayer attackedEntity = (EntityPlayer)HackConfigs.Mudila;
        Minecraft mc = Minecraft.getMinecraft();
        if (attackedEntity.getHealth() <= 0.0f) {
            return;
        }
        if (attackedEntity == Minecraft.getMinecraft().player) {
            return;
        }
        if ((double)Minecraft.getMinecraft().player.getDistanceToEntity(attackedEntity) > this.distance) {
            return;
        }
        this.time += 0.005;
        double height = 0.5 * (1.0 + Math.sin(Math.PI * 2 * (this.time * 0.3)));
        if (height > 0.995) {
            this.down = true;
        } else if (height < 0.1) {
            this.down = false;
        }
        double x2 = attackedEntity.lastTickPosX + (attackedEntity.posX - attackedEntity.lastTickPosX) * (double)event.pticks - this.mc.getRenderManager().viewerPosX;
        double y2 = attackedEntity.lastTickPosY + (attackedEntity.posY - attackedEntity.lastTickPosY) * (double)event.pticks - this.mc.getRenderManager().viewerPosY;
        double z2 = attackedEntity.lastTickPosZ + (attackedEntity.posZ - attackedEntity.lastTickPosZ) * (double)event.pticks - this.mc.getRenderManager().viewerPosZ;
        GlStateManager.enableBlend();
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(2848);
        GlStateManager.disableDepth();
        GlStateManager.disableTexture2D();
        GlStateManager.disableAlpha();
        GL11.glLineWidth(1.5f);
        GL11.glShadeModel(7425);
        GL11.glDisable(2884);
        double size = (double)attackedEntity.width * this.radius;
        double yOffset = height * 2.0;
        GL11.glBegin(5);
        for (j2 = 0; j2 < 361; ++j2) {
            GL11.glColor4f(new Color(HackConfigs.ThemeColor).getRed(), new Color(HackConfigs.ThemeColor).getGreen(), new Color(HackConfigs.ThemeColor).getBlue(), 1.0f);
            GL11.glVertex3d(x2 + Math.cos(Math.toRadians(j2)) * size, y2 + yOffset, z2 - Math.sin(Math.toRadians(j2)) * size);
            GL11.glColor4f(new Color(HackConfigs.ThemeColorGui).getRed(), new Color(HackConfigs.ThemeColorGui).getGreen(), new Color(HackConfigs.ThemeColorGui).getBlue(), 1.0f);
            GL11.glVertex3d(x2 + Math.cos(Math.toRadians(j2)) * size, y2 + yOffset + (this.down ? -0.5 * (1.0 - height) : 0.5 * height), z2 - Math.sin(Math.toRadians(j2)) * size);
        }
        GL11.glEnd();
        GL11.glBegin(2);
        for (j2 = 0; j2 < 361; ++j2) {
            GL11.glColor4f(new Color(HackConfigs.ThemeColor).getRed(), new Color(HackConfigs.ThemeColor).getGreen(), new Color(HackConfigs.ThemeColor).getBlue(), 1.0f);
            GL11.glVertex3d(x2 + Math.cos(Math.toRadians(j2)) * size, y2 + yOffset, z2 - Math.sin(Math.toRadians(j2)) * size);
        }
        GL11.glEnd();
        GlStateManager.enableAlpha();
        GL11.glShadeModel(7424);
        GL11.glDisable(2848);
        GL11.glEnable(2884);
        GlStateManager.enableAlpha();
        GL11.glShadeModel(7424);
        GL11.glDisable(2848);
        GL11.glEnable(2884);
        GlStateManager.enableTexture2D();
        GlStateManager.enableDepth();
        GlStateManager.disableBlend();
        GlStateManager.resetColor();
    }
}

