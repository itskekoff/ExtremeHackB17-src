package ShwepSS.B17.modules.hacks;

import ShwepSS.B17.HackConfigs;
import ShwepSS.B17.Utils.GuiRenderUtils;
import ShwepSS.B17.modules.Category;
import ShwepSS.B17.modules.Module;
import ShwepSS.event.Event3DRender;
import ShwepSS.eventapi.EventManager;
import ShwepSS.eventapi.EventTarget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;

public class NameTags
extends Module {
    public NameTags() {
        super("NameTags", "\u0421\u043c\u043e\u0442\u0440\u0435\u0442\u044c \u043d\u0430 \u043d\u0438\u043a\u0438 \u043c\u0443\u0434\u0438\u043b \u0435\u0431\u0430\u043d\u044b\u0445", 0, Category.Visuals, true);
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
    public void onDrow(Event3DRender ev2) {
        Minecraft mc = Minecraft.getMinecraft();
        if (this.isEnabled()) {
            for (EntityPlayer player : mc.world.playerEntities) {
                if (player == null || player.deathTime > 0 || player == mc.player) continue;
                double x2 = player.lastTickPosX + (player.posX - player.lastTickPosX) - RenderManager.renderPosX;
                double y2 = player.lastTickPosY + (player.posY - player.lastTickPosY) - RenderManager.renderPosY;
                double z2 = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) - RenderManager.renderPosZ;
                this.renderTag(player, x2, y2, z2);
            }
        }
    }

    private void renderTag(Entity entity, double x2, double y2, double z2) {
        Minecraft mc = Minecraft.getMinecraft();
        String name = entity.getName();
        if (entity instanceof EntityLivingBase) {
            name = String.valueOf(name) + " \u00a7a" + (double)Math.round(((EntityLivingBase)entity).getHealth() * 100.0f / 100.0f) / 2.0;
        }
        float var13 = 1.6f;
        float var14 = 0.016666668f * mc.player.getDistanceToEntity(entity) / 2.0f;
        GL11.glPushMatrix();
        GL11.glTranslatef((float)x2, (float)y2 + entity.height + 0.5f, (float)z2);
        GL11.glNormal3f(0.0f, 1.0f, 0.0f);
        GL11.glRotatef(-RenderManager.playerViewY, 0.0f, 1.0f, 0.0f);
        GL11.glRotatef(RenderManager.playerViewX, 1.0f, 0.0f, 0.0f);
        GL11.glScalef(-var14, -var14, var14);
        GL11.glDepthMask(false);
        GL11.glDisable(2896);
        Tessellator var15 = Tessellator.getInstance();
        BufferBuilder vertexbuffer = var15.getBuffer();
        int var16 = (int)(-mc.player.getDistanceToEntity(entity)) / (int)var13;
        if (entity.isSneaking()) {
            var16 += 4;
        } else if (var16 < -14) {
            var16 = -14;
        }
        GL11.glDisable(3553);
        GL11.glDisable(2929);
        int var17 = mc.fontRendererObj.getStringWidth(name) / 2;
        GuiRenderUtils.drawBorderedRect2(-var17 - 2, var16, var17 + 2, 11 + var16, 0.5f, HackConfigs.ThemeColor, Integer.MIN_VALUE);
        mc.fontRendererObj.drawStringWithShadow(name, -var17, var16, HackConfigs.ThemeColor);
        mc.entityRenderer.disableLightmap();
        GL11.glLineWidth(1.0f);
        GL11.glEnable(3553);
        GL11.glDisable(3042);
        GL11.glDisable(2896);
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(2929);
        GL11.glDepthMask(true);
        GL11.glPopMatrix();
    }

    public static void drawItem(ItemStack itemstack, int i2, int j2) {
        Minecraft mc = Minecraft.getMinecraft();
        RenderItem itemRenderer = mc.getRenderItem();
        itemRenderer.renderItemIntoGUI(itemstack, i2, j2);
        itemRenderer.renderItemOverlayIntoGUI(mc.fontRendererObj, itemstack, i2, j2, null);
        GL11.glDisable(2884);
        GL11.glEnable(3008);
        GL11.glDisable(3042);
        GL11.glDisable(2896);
        GL11.glDisable(2884);
        GL11.glClear(256);
    }
}

