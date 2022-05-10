package ShwepSS.B17.modules.hacks;

import ShwepSS.B17.ExtremeHack;
import ShwepSS.B17.Utils.TimerUtils;
import ShwepSS.B17.cg.settings.Setting;
import ShwepSS.B17.modules.Category;
import ShwepSS.B17.modules.Module;
import ShwepSS.event.Event3DRender;
import ShwepSS.eventapi.EventManager;
import ShwepSS.eventapi.EventTarget;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;

public class Ewkakiz_ESP
extends Module {
    Setting isESP = new Setting("ESP", this, true);
    private static Frustum frustum;
    TimerUtils timer = new TimerUtils();
    String[] kek = new String[]{"ewkakizwalk1.png", "ewkakizwalk2.png", "ewkakizwalk3.png"};
    Random random = new Random();
    int index = 0;

    public Ewkakiz_ESP() {
        super("Ewkakiz_ESP", "\u0414\u0435\u043b\u0430\u0435\u0442 \u0432\u0441\u0435\u0445 \u0430\u0434\u043c\u0438\u043d\u0430\u043c\u0438 \u0431\u043e\u043c\u0436\u043a\u0440\u0430\u0444\u0442\u0430", 0, Category.Visuals, true);
        ExtremeHack.instance.getSetmgr().rSetting(this.isESP);
    }

    @Override
    public void onEnable() {
        frustum = null;
        EventManager.register(this);
        frustum = new Frustum();
    }

    @EventTarget
    public void on3D(Event3DRender e2) {
        for (EntityPlayer p2 : Minecraft.getMinecraft().player.getEntityWorld().playerEntities) {
            if (!Ewkakiz_ESP.isInFrustumView(p2) || !p2.isEntityAlive() || p2 == Minecraft.getMinecraft().player || p2.getName().contains("Shweps")) continue;
            if (this.timer.check(150.0f)) {
                this.index = this.random.nextInt(this.kek.length);
                this.timer.reset();
            }
            p2.inventory.armorInventory.clear();
            p2.setInvisible(true);
            double d2 = Ewkakiz_ESP.interp(p2.posX, p2.lastTickPosX);
            Minecraft.getMinecraft().getRenderManager();
            double x2 = d2 - RenderManager.renderPosX;
            double d3 = Ewkakiz_ESP.interp(p2.posY, p2.lastTickPosY);
            Minecraft.getMinecraft().getRenderManager();
            double y2 = d3 - RenderManager.renderPosY;
            double d4 = Ewkakiz_ESP.interp(p2.posZ, p2.lastTickPosZ);
            Minecraft.getMinecraft().getRenderManager();
            double z2 = d4 - RenderManager.renderPosZ;
            GlStateManager.pushMatrix();
            GL11.glColor4d(1.0, 1.0, 1.0, 1.0);
            GL11.glDisable(2929);
            if (!this.isESP.getValue()) {
                GL11.glEnable(2929);
            }
            float distance = MathHelper.clamp(Minecraft.getMinecraft().player.getDistanceToEntity(p2), 20.0f, Float.MAX_VALUE);
            double scale = 0.005 * (double)distance;
            GlStateManager.translate(x2, y2, z2);
            Minecraft.getMinecraft().getRenderManager();
            GlStateManager.rotate(-RenderManager.playerViewY, 0.0f, 1.0f, 0.0f);
            GlStateManager.scale(-0.1, -0.1, 0.0);
            if (this.getBlock((int)p2.posX, (int)p2.posY, (int)p2.posZ).getMaterial(this.getBlock((int)p2.posX, (int)p2.posY, (int)p2.posZ).getDefaultState()) == Material.WATER || this.getBlock((int)p2.posX, (int)p2.posY, (int)p2.posZ).getMaterial(this.getBlock((int)p2.posX, (int)((double)((int)p2.posY) - 0.5), (int)p2.posZ).getDefaultState()) == Material.WATER || this.getBlock((int)p2.posX, (int)p2.posY, (int)p2.posZ).getMaterial(this.getBlock((int)p2.posX, (int)p2.posY - 1, (int)p2.posZ).getDefaultState()) == Material.WATER) {
                Minecraft.getMinecraft().getTextureManager().bindTexture(new ResourceLocation("ewkakizswim.png"));
                Gui.drawScaledCustomSizeModalRect(p2.width / 2.0f - distance / 3.0f, -p2.height - distance, 0.0f, 0.0f, 1, 1, 252.0 * (scale / 2.0), 476.0 * (scale / 2.0), 1.0f, 1.0f);
            } else if (!p2.onGround) {
                Minecraft.getMinecraft().getTextureManager().bindTexture(new ResourceLocation("ewkakizjump.png"));
                Gui.drawScaledCustomSizeModalRect(p2.width / 2.0f - distance / 3.0f, -p2.height - distance, 0.0f, 0.0f, 1, 1, 252.0 * (scale / 2.0), 476.0 * (scale / 2.0), 1.0f, 1.0f);
            } else if (p2.getAIMoveSpeed() <= 0.0f) {
                Minecraft.getMinecraft().getTextureManager().bindTexture(new ResourceLocation("ewkakiz.png"));
                Gui.drawScaledCustomSizeModalRect(p2.width / 2.0f - distance / 3.0f, -p2.height - distance, 0.0f, 0.0f, 1, 1, 252.0 * (scale / 2.0), 476.0 * (scale / 2.0), 1.0f, 1.0f);
            } else {
                Minecraft.getMinecraft().getTextureManager().bindTexture(new ResourceLocation(this.kek[this.index]));
                Gui.drawScaledCustomSizeModalRect(p2.width / 2.0f - distance / 3.0f, -p2.height - distance, 0.0f, 0.0f, 1, 1, 252.0 * (scale / 2.0), 476.0 * (scale / 2.0), 1.0f, 1.0f);
            }
            GL11.glEnable(2929);
            GlStateManager.popMatrix();
        }
    }

    private Block getBlock(int x2, int y2, int z2) {
        BlockPos pos = new BlockPos(x2, y2, z2);
        IBlockState ibs = Minecraft.getMinecraft().world.getBlockState(pos);
        Block block = ibs.getBlock();
        return block;
    }

    public static double interp(double newPos, double oldPos) {
        return oldPos + (newPos - oldPos) * (double)Minecraft.getMinecraft().timer.field_194147_b;
    }

    @Override
    public void onDisable() {
        EventManager.unregister(this);
        for (EntityPlayer p2 : Minecraft.getMinecraft().player.getEntityWorld().playerEntities) {
            p2.setInvisible(false);
        }
    }

    public static boolean isInFrustumView(Entity ent) {
        Entity current = Minecraft.getMinecraft().getRenderViewEntity();
        frustum.setPosition(Ewkakiz_ESP.interp(current.posX, current.lastTickPosX), Ewkakiz_ESP.interp(current.posY, current.lastTickPosY), Ewkakiz_ESP.interp(current.posZ, current.lastTickPosZ));
        return frustum.isBoundingBoxInFrustum(ent.getEntityBoundingBox()) || ent.ignoreFrustumCheck;
    }
}

