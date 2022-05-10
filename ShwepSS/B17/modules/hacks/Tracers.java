package ShwepSS.B17.modules.hacks;

import ShwepSS.B17.HackConfigs;
import ShwepSS.B17.Utils.RotationUtils;
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
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.Timer;

public class Tracers
extends Module {
    private final ArrayList<Entity> players = new ArrayList();
    private final ArrayList<EntityPlayer> players2 = new ArrayList();
    private final ArrayList<Blocks> chests = new ArrayList();
    int playerBox;

    public Tracers() {
        super("Tracers", "\u041b\u0438\u043d\u0438\u0438 \u043a \u0438\u0433\u0440\u043e\u043a\u0430\u043c", 45, Category.Visuals, true);
    }

    @Override
    public void onEnable() {
        this.playerBox = GL11.glGenLists(1);
        GL11.glNewList(this.playerBox, 4864);
        AxisAlignedBB bb2 = new AxisAlignedBB(-0.5, 0.0, -0.5, 0.5, 1.0, 0.5);
        GL11.glEndList();
        EventManager.register(this);
        super.onEnable();
    }

    @Override
    public void onDisable() {
        EventManager.unregister(this);
        super.onDisable();
        this.players.clear();
    }

    @EventTarget
    public void on3D(Event3DRender e2) {
        for (Entity en2 : Minecraft.getMinecraft().world.loadedEntityList) {
            if (!(en2 instanceof EntityLivingBase) || en2.getName() == Minecraft.getMinecraft().player.getName()) continue;
            this.players.add(en2);
        }
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(2848);
        GL11.glLineWidth(2.0f);
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
        this.renderTracers(e2.pticks());
        GL11.glPopMatrix();
        GL11.glEnable(2929);
        GL11.glEnable(3553);
        GL11.glDisable(3042);
        GL11.glDisable(2848);
    }

    private void renderBoxes(double partialTicks) {
        for (Entity e2 : this.players) {
            float color = 1123.0f;
            float color2 = 2.0f;
            if (e2.getName() == "Jediem150") {
                color2 = 0.0f;
            }
            GL11.glPushMatrix();
            GL11.glTranslated(e2.prevPosX + (e2.posX - e2.prevPosX) * partialTicks, e2.prevPosY + (e2.posY - e2.prevPosY) * partialTicks, e2.prevPosZ + (e2.posZ - e2.prevPosZ) * partialTicks);
            GL11.glScaled((double)e2.width + 0.1, (double)e2.height + 0.1, (double)e2.width + 0.1);
            GL11.glCallList(this.playerBox);
            GL11.glPopMatrix();
        }
    }

    private void renderTracers(double partialTicks) {
        Minecraft mc = Minecraft.getMinecraft();
        Vec3d vec3d = RotationUtils.getClientLookVec().addVector(0.0, Minecraft.getMinecraft().player.getEyeHeight(), 0.0);
        Minecraft.getMinecraft().getRenderManager();
        Minecraft.getMinecraft().getRenderManager();
        Minecraft.getMinecraft().getRenderManager();
        Vec3d start = vec3d.addVector(RenderManager.renderPosX, RenderManager.renderPosY, RenderManager.renderPosZ);
        for (Object o2 : mc.world.loadedEntityList) {
            EntityLivingBase e1;
            if (o2 instanceof EntityLivingBase && (e1 = (EntityLivingBase)o2) != mc.player) {
                if (!(e1 instanceof EntityPlayer)) continue;
                GL11.glBegin(1);
                Vec3d end = e1.getEntityBoundingBox().getCenter().subtract(new Vec3d(e1.posX, e1.posY, e1.posZ).subtract(e1.prevPosX, e1.prevPosY, e1.prevPosZ).scale(1.0 - partialTicks));
                GL11.glColor4f(new Color(HackConfigs.ThemeColor).getRed(), new Color(HackConfigs.ThemeColor).getGreen(), new Color(HackConfigs.ThemeColor).getBlue(), 1.0f);
                GL11.glVertex3d(start.xCoord, start.yCoord, start.zCoord);
                GL11.glVertex3d(end.xCoord, end.yCoord, end.zCoord);
                GL11.glEnd();
            }
            Timer.tick();
            this.players.clear();
        }
    }
}

