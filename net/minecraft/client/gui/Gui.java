package net.minecraft.client.gui;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;

public class Gui {
    public static final ResourceLocation OPTIONS_BACKGROUND = new ResourceLocation("textures/gui/options_background.png");
    public static final ResourceLocation STAT_ICONS = new ResourceLocation("textures/gui/container/stats_icons.png");
    public static final ResourceLocation ICONS = new ResourceLocation("textures/gui/icons.png");
    public static float zLevel;

    public void drawHorizontalLine(int startX, int endX, int y2, int color) {
        if (endX < startX) {
            int i2 = startX;
            startX = endX;
            endX = i2;
        }
        Gui.drawRect(startX, y2, endX + 1, y2 + 1, color);
    }

    public void drawVerticalLine(int x2, int startY, int endY, int color) {
        if (endY < startY) {
            int i2 = startY;
            startY = endY;
            endY = i2;
        }
        Gui.drawRect(x2, startY + 1, x2 + 1, endY, color);
    }

    public static void drawRect(double d2, double e2, double g2, double h2, int color) {
        if (d2 < g2) {
            double i2 = d2;
            d2 = g2;
            g2 = i2;
        }
        if (e2 < h2) {
            double j2 = e2;
            e2 = h2;
            h2 = j2;
        }
        float f3 = (float)(color >> 24 & 0xFF) / 255.0f;
        float f2 = (float)(color >> 16 & 0xFF) / 255.0f;
        float f1 = (float)(color >> 8 & 0xFF) / 255.0f;
        float f22 = (float)(color & 0xFF) / 255.0f;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.color(f2, f1, f22, f3);
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION);
        bufferbuilder.pos(d2, h2, 0.0).endVertex();
        bufferbuilder.pos(g2, h2, 0.0).endVertex();
        bufferbuilder.pos(g2, e2, 0.0).endVertex();
        bufferbuilder.pos(d2, e2, 0.0).endVertex();
        tessellator.draw();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }

    public static void drawGradientRect(int left, int top, int right, int bottom, int startColor, int endColor) {
        float f2 = (float)(startColor >> 24 & 0xFF) / 255.0f;
        float f1 = (float)(startColor >> 16 & 0xFF) / 255.0f;
        float f22 = (float)(startColor >> 8 & 0xFF) / 255.0f;
        float f3 = (float)(startColor & 0xFF) / 255.0f;
        float f4 = (float)(endColor >> 24 & 0xFF) / 255.0f;
        float f5 = (float)(endColor >> 16 & 0xFF) / 255.0f;
        float f6 = (float)(endColor >> 8 & 0xFF) / 255.0f;
        float f7 = (float)(endColor & 0xFF) / 255.0f;
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.shadeModel(7425);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos(right, top, zLevel).color(f1, f22, f3, f2).endVertex();
        bufferbuilder.pos(left, top, zLevel).color(f1, f22, f3, f2).endVertex();
        bufferbuilder.pos(left, bottom, zLevel).color(f5, f6, f7, f4).endVertex();
        bufferbuilder.pos(right, bottom, zLevel).color(f5, f6, f7, f4).endVertex();
        tessellator.draw();
        GlStateManager.shadeModel(7424);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
    }

    public void drawCenteredString(FontRenderer fontRendererIn, String text, int x2, int y2, int color) {
        fontRendererIn.drawStringWithShadow(text, x2 - fontRendererIn.getStringWidth(text) / 2, y2, color);
    }

    public void drawString(FontRenderer fontRendererIn, String text, int x2, int y2, int color) {
        fontRendererIn.drawStringWithShadow(text, x2, y2, color);
    }

    public void drawTexturedModalRect(int x2, int y2, int textureX, int textureY, int width, int height) {
        float f2 = 0.00390625f;
        float f1 = 0.00390625f;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
        bufferbuilder.pos(x2 + 0, y2 + height, zLevel).tex((float)(textureX + 0) * 0.00390625f, (float)(textureY + height) * 0.00390625f).endVertex();
        bufferbuilder.pos(x2 + width, y2 + height, zLevel).tex((float)(textureX + width) * 0.00390625f, (float)(textureY + height) * 0.00390625f).endVertex();
        bufferbuilder.pos(x2 + width, y2 + 0, zLevel).tex((float)(textureX + width) * 0.00390625f, (float)(textureY + 0) * 0.00390625f).endVertex();
        bufferbuilder.pos(x2 + 0, y2 + 0, zLevel).tex((float)(textureX + 0) * 0.00390625f, (float)(textureY + 0) * 0.00390625f).endVertex();
        tessellator.draw();
    }

    public void drawTexturedModalRect(float xCoord, float yCoord, int minU, int minV, int maxU, int maxV) {
        float f2 = 0.00390625f;
        float f1 = 0.00390625f;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
        bufferbuilder.pos(xCoord + 0.0f, yCoord + (float)maxV, zLevel).tex((float)(minU + 0) * 0.00390625f, (float)(minV + maxV) * 0.00390625f).endVertex();
        bufferbuilder.pos(xCoord + (float)maxU, yCoord + (float)maxV, zLevel).tex((float)(minU + maxU) * 0.00390625f, (float)(minV + maxV) * 0.00390625f).endVertex();
        bufferbuilder.pos(xCoord + (float)maxU, yCoord + 0.0f, zLevel).tex((float)(minU + maxU) * 0.00390625f, (float)(minV + 0) * 0.00390625f).endVertex();
        bufferbuilder.pos(xCoord + 0.0f, yCoord + 0.0f, zLevel).tex((float)(minU + 0) * 0.00390625f, (float)(minV + 0) * 0.00390625f).endVertex();
        tessellator.draw();
    }

    public void drawTexturedModalRect(int xCoord, int yCoord, TextureAtlasSprite textureSprite, int widthIn, int heightIn) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
        bufferbuilder.pos(xCoord + 0, yCoord + heightIn, zLevel).tex(textureSprite.getMinU(), textureSprite.getMaxV()).endVertex();
        bufferbuilder.pos(xCoord + widthIn, yCoord + heightIn, zLevel).tex(textureSprite.getMaxU(), textureSprite.getMaxV()).endVertex();
        bufferbuilder.pos(xCoord + widthIn, yCoord + 0, zLevel).tex(textureSprite.getMaxU(), textureSprite.getMinV()).endVertex();
        bufferbuilder.pos(xCoord + 0, yCoord + 0, zLevel).tex(textureSprite.getMinU(), textureSprite.getMinV()).endVertex();
        tessellator.draw();
    }

    public static void drawModalRectWithCustomSizedTexture(int x2, int y2, float u2, float v2, int width, int height, float textureWidth, float textureHeight) {
        float f2 = 1.0f / textureWidth;
        float f1 = 1.0f / textureHeight;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
        bufferbuilder.pos(x2, y2 + height, 0.0).tex(u2 * f2, (v2 + (float)height) * f1).endVertex();
        bufferbuilder.pos(x2 + width, y2 + height, 0.0).tex((u2 + (float)width) * f2, (v2 + (float)height) * f1).endVertex();
        bufferbuilder.pos(x2 + width, y2, 0.0).tex((u2 + (float)width) * f2, v2 * f1).endVertex();
        bufferbuilder.pos(x2, y2, 0.0).tex(u2 * f2, v2 * f1).endVertex();
        tessellator.draw();
    }

    public static void drawScaledCustomSizeModalRect(float g2, float h2, float u2, float v2, int uWidth, int vHeight, double d2, double e2, float tileWidth, float tileHeight) {
        float f2 = 1.0f / tileWidth;
        float f1 = 1.0f / tileHeight;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
        bufferbuilder.pos(g2, (double)h2 + e2, 0.0).tex(u2 * f2, (v2 + (float)vHeight) * f1).endVertex();
        bufferbuilder.pos((double)g2 + d2, (double)h2 + e2, 0.0).tex((u2 + (float)uWidth) * f2, (v2 + (float)vHeight) * f1).endVertex();
        bufferbuilder.pos((double)g2 + d2, h2, 0.0).tex((u2 + (float)uWidth) * f2, v2 * f1).endVertex();
        bufferbuilder.pos(g2, h2, 0.0).tex(u2 * f2, v2 * f1).endVertex();
        tessellator.draw();
    }
}

