package ShwepSS.B17.modules.hacks;

import ShwepSS.B17.HackConfigs;
import ShwepSS.B17.Utils.RenderUtils;
import ShwepSS.B17.modules.Category;
import ShwepSS.B17.modules.Module;
import ShwepSS.event.Event3DRender;
import ShwepSS.eventapi.EventManager;
import ShwepSS.eventapi.EventTarget;
import java.awt.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;

public class Trajectories
extends Module {
    public static Module instance;
    public Minecraft mc = Minecraft.getMinecraft();
    private double x;
    private double y;
    private double z;
    private double motionX;
    private double motionY;
    private double motionZ;
    private boolean hitEntity = false;
    private double r;
    private double g;
    private double b;
    public double pX = -9000.0;
    public double pY = -9000.0;
    public double pZ = -9000.0;
    private EntityLivingBase entity;
    private RayTraceResult blockCollision;
    private RayTraceResult entityCollision;
    private static AxisAlignedBB aim;

    public Trajectories() {
        super("Trajectories", "\u0420\u0435\u043d\u0434\u0435\u0440 \u0442\u0440\u0430\u044d\u043a\u0442\u043e\u0440\u0438\u0439", 0, Category.Visuals, true);
        instance = this;
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
    public void onRender3D(Event3DRender event) {
        try {
            if (this.mc.player.inventory.getCurrentItem() != null) {
                EntityPlayerSP player = this.mc.player;
                ItemStack stack = player.inventory.getCurrentItem();
                int itemMain = Item.getIdFromItem(this.mc.player.getHeldItem(EnumHand.MAIN_HAND).getItem());
                int itemOff = Item.getIdFromItem(this.mc.player.getHeldItem(EnumHand.OFF_HAND).getItem());
                if (itemMain == 261 || itemOff == 261 || itemMain == 368 || itemOff == 368 || itemMain == 332 || itemOff == 332 || itemMain == 344 || itemOff == 344) {
                    double posX = player.lastTickPosX + (player.posX - player.lastTickPosX) * (double)this.mc.timer.field_194147_b - Math.cos(Math.toRadians(player.rotationYaw)) * (double)0.16f;
                    double posY = player.lastTickPosY + (player.posY - player.lastTickPosY) * (double)this.mc.timer.field_194147_b + (double)player.getEyeHeight() - 0.1;
                    double posZ = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * (double)this.mc.timer.field_194147_b - Math.sin(Math.toRadians(player.rotationYaw)) * (double)0.16f;
                    double itemBow = stack.getItem() instanceof ItemBow ? 1.0f : 0.4f;
                    double yaw = Math.toRadians(player.rotationYaw);
                    double pitch = Math.toRadians(player.rotationPitch);
                    double trajectoryX = -Math.sin(yaw) * Math.cos(pitch) * itemBow;
                    double trajectoryY = -Math.sin(pitch) * itemBow;
                    double trajectoryZ = Math.cos(yaw) * Math.cos(pitch) * itemBow;
                    double trajectory = Math.sqrt(trajectoryX * trajectoryX + trajectoryY * trajectoryY + trajectoryZ * trajectoryZ);
                    trajectoryX /= trajectory;
                    trajectoryY /= trajectory;
                    trajectoryZ /= trajectory;
                    if (stack.getItem() instanceof ItemBow) {
                        float bowPower = (float)(72000 - player.getItemInUseCount()) / 20.0f;
                        if ((bowPower = (bowPower * bowPower + bowPower * 2.0f) / 3.0f) > 1.0f) {
                            bowPower = 1.0f;
                        }
                        trajectoryX *= (double)(bowPower *= 3.0f);
                        trajectoryY *= (double)bowPower;
                        trajectoryZ *= (double)bowPower;
                    } else {
                        trajectoryX *= 1.5;
                        trajectoryY *= 1.5;
                        trajectoryZ *= 1.5;
                    }
                    GL11.glPushMatrix();
                    GL11.glDisable(3553);
                    GL11.glEnable(3042);
                    GL11.glBlendFunc(770, 771);
                    GL11.glDisable(2929);
                    GL11.glDepthMask(false);
                    GL11.glEnable(2848);
                    GL11.glLineWidth(2.0f);
                    double gravity = stack.getItem() instanceof ItemBow ? 0.05 : 0.03;
                    GL11.glColor4f(new Color(HackConfigs.ThemeColor).getRed(), new Color(HackConfigs.ThemeColor).getGreen(), new Color(HackConfigs.ThemeColor).getBlue(), 1.0f);
                    GL11.glBegin(3);
                    for (int i2 = 0; i2 < 2000; ++i2) {
                        this.mc.getRenderManager();
                        this.mc.getRenderManager();
                        this.mc.getRenderManager();
                        GL11.glVertex3d(posX - RenderManager.renderPosX, posY - RenderManager.renderPosY, posZ - RenderManager.renderPosZ);
                        trajectoryY *= 0.999;
                        Vec3d vec = new Vec3d(player.posX, player.posY + (double)player.getEyeHeight(), player.posZ);
                        this.blockCollision = this.mc.world.rayTraceBlocks(vec, new Vec3d(posX += (trajectoryX *= 0.999) * 0.1, posY += (trajectoryY -= gravity * 0.1) * 0.1, posZ += (trajectoryZ *= 0.999) * 0.1));
                        for (Entity o2 : this.mc.world.getLoadedEntityList()) {
                            if (!(o2 instanceof EntityLivingBase) || o2 instanceof EntityPlayerSP) continue;
                            this.entity = (EntityLivingBase)o2;
                            AxisAlignedBB entityBoundingBox = this.entity.getEntityBoundingBox().expand(0.3, 0.3, 0.3);
                            this.entityCollision = entityBoundingBox.calculateIntercept(vec, new Vec3d(posX, posY, posZ));
                            if (this.entityCollision != null) {
                                this.blockCollision = this.entityCollision;
                            }
                            if (this.entityCollision != null) {
                                GL11.glColor4f(1.0f, 0.0f, 0.2f, 0.5f);
                            }
                            if (this.entityCollision == null) continue;
                            this.blockCollision = this.entityCollision;
                        }
                        if (this.blockCollision != null) break;
                    }
                    GL11.glEnd();
                    this.mc.getRenderManager();
                    double renderX = posX - RenderManager.renderPosX;
                    this.mc.getRenderManager();
                    double renderY = posY - RenderManager.renderPosY;
                    this.mc.getRenderManager();
                    double renderZ = posZ - RenderManager.renderPosZ;
                    GL11.glPushMatrix();
                    GL11.glTranslated(renderX - 0.5, renderY - 0.5, renderZ - 0.5);
                    switch (this.blockCollision.sideHit.getIndex()) {
                        case 2: 
                        case 3: {
                            GlStateManager.rotate(90.0f, 1.0f, 0.0f, 0.0f);
                            aim = new AxisAlignedBB(0.0, 0.5, -1.0, 1.0, 0.45, 0.0);
                            break;
                        }
                        case 4: 
                        case 5: {
                            GlStateManager.rotate(90.0f, 0.0f, 0.0f, 1.0f);
                            aim = new AxisAlignedBB(0.0, -0.5, 0.0, 1.0, -0.45, 1.0);
                            break;
                        }
                        default: {
                            aim = new AxisAlignedBB(0.0, 0.5, 0.0, 1.0, 0.45, 1.0);
                        }
                    }
                    Trajectories.func_181561_a(aim);
                    GL11.glPopMatrix();
                    GL11.glDisable(3042);
                    GL11.glEnable(3553);
                    GL11.glEnable(2929);
                    GL11.glDepthMask(true);
                    GL11.glDisable(2848);
                    GL11.glPopMatrix();
                }
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    public static void func_181561_a(AxisAlignedBB p_181561_0_) {
        GL11.glPushMatrix();
        GL11.glTranslated(0.0, 1.0, 0.0);
        RenderUtils.drawCrossBox();
        GL11.glTranslated(1.0, 0.0, 0.0);
        RenderUtils.drawCrossBox();
        GL11.glTranslated(0.0, 0.0, 1.0);
        RenderUtils.drawCrossBox();
        GL11.glTranslated(-1.0, 0.0, 0.0);
        RenderUtils.drawCrossBox();
        GL11.glPopMatrix();
    }

    public static void drawBox(AxisAlignedBB bb2) {
        GL11.glBegin(7);
        GL11.glVertex3d(bb2.minX, bb2.minY, bb2.minZ);
        GL11.glVertex3d(bb2.maxX, bb2.minY, bb2.minZ);
        GL11.glVertex3d(bb2.maxX, bb2.minY, bb2.maxZ);
        GL11.glVertex3d(bb2.minX, bb2.minY, bb2.maxZ);
        GL11.glVertex3d(bb2.minX, bb2.maxY, bb2.minZ);
        GL11.glVertex3d(bb2.minX, bb2.maxY, bb2.maxZ);
        GL11.glVertex3d(bb2.maxX, bb2.maxY, bb2.maxZ);
        GL11.glVertex3d(bb2.maxX, bb2.maxY, bb2.minZ);
        GL11.glVertex3d(bb2.minX, bb2.minY, bb2.minZ);
        GL11.glVertex3d(bb2.minX, bb2.maxY, bb2.minZ);
        GL11.glVertex3d(bb2.maxX, bb2.maxY, bb2.minZ);
        GL11.glVertex3d(bb2.maxX, bb2.minY, bb2.minZ);
        GL11.glVertex3d(bb2.maxX, bb2.minY, bb2.minZ);
        GL11.glVertex3d(bb2.maxX, bb2.maxY, bb2.minZ);
        GL11.glVertex3d(bb2.maxX, bb2.maxY, bb2.maxZ);
        GL11.glVertex3d(bb2.maxX, bb2.minY, bb2.maxZ);
        GL11.glVertex3d(bb2.minX, bb2.minY, bb2.maxZ);
        GL11.glVertex3d(bb2.maxX, bb2.minY, bb2.maxZ);
        GL11.glVertex3d(bb2.maxX, bb2.maxY, bb2.maxZ);
        GL11.glVertex3d(bb2.minX, bb2.maxY, bb2.maxZ);
        GL11.glVertex3d(bb2.minX, bb2.minY, bb2.minZ);
        GL11.glVertex3d(bb2.minX, bb2.minY, bb2.maxZ);
        GL11.glVertex3d(bb2.minX, bb2.maxY, bb2.maxZ);
        GL11.glVertex3d(bb2.minX, bb2.maxY, bb2.minZ);
        GL11.glEnd();
    }
}

