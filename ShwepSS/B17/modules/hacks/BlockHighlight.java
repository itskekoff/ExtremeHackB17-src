package ShwepSS.B17.modules.hacks;

import ShwepSS.B17.HackConfigs;
import ShwepSS.B17.Utils.GuiRenderUtils;
import ShwepSS.B17.modules.Category;
import ShwepSS.B17.modules.Module;
import ShwepSS.event.Event3DRender;
import ShwepSS.eventapi.EventManager;
import ShwepSS.eventapi.EventTarget;
import java.awt.Color;
import net.minecraft.block.Block;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import org.lwjgl.opengl.GL11;

public class BlockHighlight
extends Module {
    public BlockHighlight() {
        super("BlockHighlight", "\u041f\u043e\u0434\u0441\u0432\u0435\u0447\u0438\u0432\u0430\u0435\u0442 \u0431\u043b\u043e\u043a", 0, Category.Visuals, true);
    }

    @Override
    public void onEnable() {
        EventManager.register(this);
    }

    @Override
    public void onRender() {
        Minecraft mc = Minecraft.getMinecraft();
        try {
            if (mc.objectMouseOver.typeOfHit != RayTraceResult.Type.BLOCK) {
                return;
            }
            BlockPos pos = mc.objectMouseOver.getBlockPos();
            Block block = mc.world.getBlockState(pos).getBlock();
            int id2 = Block.getIdFromBlock(block);
            String s2 = String.valueOf(String.valueOf(block.getLocalizedName())) + " ID: " + id2;
            String s1 = block.getLocalizedName();
            String s22 = " ID: " + id2;
            if (mc.objectMouseOver != null && mc.objectMouseOver.typeOfHit == RayTraceResult.Type.BLOCK) {
                ScaledResolution res = new ScaledResolution(mc);
                int x2 = res.getScaledWidth() / 2 + 7;
                int y2 = res.getScaledHeight() / 2;
                GuiRenderUtils.drawRoundedRect((double)x2 / 1.1 - 5.0, 20.0, mc.fontRendererObj.getStringWidth(s2) + 10, (float)mc.fontRendererObj.FONT_HEIGHT + 0.5f, 2.0, -1677721600);
                mc.fontRendererObj.drawString(s1, (int)((double)x2 / 1.1 + 1.0), 20, -1);
                mc.fontRendererObj.drawString(s22, (int)((double)x2 / 1.1 + (double)mc.fontRendererObj.getStringWidth(s1) + 2.0), 20, -1);
            }
        }
        catch (Exception eg2) {
            eg2.printStackTrace();
        }
    }

    @EventTarget
    public void onKek(Event3DRender ev2) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.objectMouseOver != null && mc.objectMouseOver.typeOfHit == RayTraceResult.Type.BLOCK) {
            BlockPos pos = mc.objectMouseOver.getBlockPos();
            Block block = mc.world.getBlockState(pos).getBlock();
            String s2 = block.getLocalizedName();
            mc.getRenderManager();
            double x2 = (double)pos.getX() - RenderManager.renderPosX;
            mc.getRenderManager();
            double y2 = (double)pos.getY() - RenderManager.renderPosY;
            mc.getRenderManager();
            double z2 = (double)pos.getZ() - RenderManager.renderPosZ;
            GL11.glPushMatrix();
            GL11.glEnable(3042);
            GL11.glBlendFunc(770, 771);
            GL11.glDisable(3553);
            GL11.glEnable(2848);
            GL11.glDisable(2929);
            GL11.glDepthMask(false);
            GL11.glColor4f(0.0f, 0.5f, 1.0f, 0.25f);
            double minX = block instanceof BlockStairs || Block.getIdFromBlock(block) == 134 ? 0.0 : block.getBoundingBox((IBlockState)block.getDefaultState(), (IBlockAccess)mc.world, (BlockPos)pos).minX;
            double minY = block instanceof BlockStairs || Block.getIdFromBlock(block) == 134 ? 0.0 : block.getBoundingBox((IBlockState)block.getDefaultState(), (IBlockAccess)mc.world, (BlockPos)pos).minY;
            double minZ = block instanceof BlockStairs || Block.getIdFromBlock(block) == 134 ? 0.0 : block.getBoundingBox((IBlockState)block.getDefaultState(), (IBlockAccess)mc.world, (BlockPos)pos).minZ;
            GL11.glColor4f(0.0f, 0.5f, 1.0f, 1.0f);
            GL11.glLineWidth(0.5f);
            int customColorValue = HackConfigs.ThemeColor;
            Color top = new Color(customColorValue);
            BlockHighlight.drawBlockOutline(new AxisAlignedBB(x2 + minX, y2 + minY, z2 + minZ, x2 + block.getBoundingBox((IBlockState)block.getDefaultState(), (IBlockAccess)mc.world, (BlockPos)pos).maxX, y2 + block.getBoundingBox((IBlockState)block.getDefaultState(), (IBlockAccess)mc.world, (BlockPos)pos).maxY, z2 + block.getBoundingBox((IBlockState)block.getDefaultState(), (IBlockAccess)mc.world, (BlockPos)pos).maxZ), top, 1.5f);
            GL11.glDisable(2848);
            GL11.glEnable(3553);
            GL11.glEnable(2929);
            GL11.glDepthMask(true);
            GL11.glDisable(3042);
            GL11.glPopMatrix();
        }
    }

    public static Color rainbow(int delay, float saturation, float brightness) {
        double rainbow = Math.ceil((System.currentTimeMillis() + (long)delay) / 16L);
        return Color.getHSBColor((float)((rainbow %= 360.0) / 360.0), saturation, brightness);
    }

    public static void drawBlockOutline(AxisAlignedBB bb2, Color color, float linewidth) {
        float red = (float)color.getRed() / 255.0f;
        float green = (float)color.getGreen() / 255.0f;
        float blue = (float)color.getBlue() / 255.0f;
        float alpha = (float)color.getAlpha() / 255.0f;
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.disableDepth();
        GlStateManager.tryBlendFuncSeparate(770, 771, 0, 1);
        GlStateManager.disableTexture2D();
        GlStateManager.depthMask(false);
        GL11.glEnable(2848);
        GL11.glHint(3154, 4354);
        GL11.glLineWidth(linewidth);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(3, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos(bb2.minX, bb2.minY, bb2.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb2.minX, bb2.minY, bb2.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb2.maxX, bb2.minY, bb2.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb2.maxX, bb2.minY, bb2.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb2.minX, bb2.minY, bb2.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb2.minX, bb2.maxY, bb2.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb2.minX, bb2.maxY, bb2.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb2.minX, bb2.minY, bb2.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb2.maxX, bb2.minY, bb2.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb2.maxX, bb2.maxY, bb2.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb2.minX, bb2.maxY, bb2.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb2.maxX, bb2.maxY, bb2.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb2.maxX, bb2.maxY, bb2.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb2.maxX, bb2.minY, bb2.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb2.maxX, bb2.maxY, bb2.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb2.minX, bb2.maxY, bb2.minZ).color(red, green, blue, alpha).endVertex();
        tessellator.draw();
        GL11.glDisable(2848);
        GlStateManager.depthMask(true);
        GlStateManager.enableDepth();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    @Override
    public void onTick() {
    }

    @Override
    public void onDisable() {
        EventManager.unregister(this);
    }
}

