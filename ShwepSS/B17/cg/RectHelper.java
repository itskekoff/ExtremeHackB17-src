package ShwepSS.B17.cg;

import java.awt.Color;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;

public class RectHelper {
    public static long delta = 0L;

    public static void drawRoundedRect(double x2, double y2, double width, double height, float radius, Color color) {
        float x22 = (float)(x2 + (double)(radius / 2.0f + 0.5f));
        float y22 = (float)(y2 + (double)(radius / 2.0f + 0.5f));
        float width2 = (float)(width - (double)(radius / 2.0f + 0.5f));
        float height2 = (float)(height - (double)(radius / 2.0f + 0.5f));
        RectHelper.drawRect(x22, y22, x22 + width2, y22 + height2, color.getRGB());
        RectHelper.polygon(x2, y2, radius * 2.0f, 360.0, true, color);
        RectHelper.polygon(x2 + (double)width2 - (double)radius + 1.2, y2, radius * 2.0f, 360.0, true, color);
        RectHelper.polygon(x2 + (double)width2 - (double)radius + 1.2, y2 + (double)height2 - (double)radius + 1.0, radius * 2.0f, 360.0, true, color);
        RectHelper.polygon(x2, y2 + (double)height2 - (double)radius + 1.0, radius * 2.0f, 360.0, true, color);
        GL11.glColor4f((float)color.getRed() / 255.0f, (float)color.getGreen() / 255.0f, (float)color.getBlue() / 255.0f, (float)color.getAlpha() / 255.0f);
        RectHelper.drawRect(x22 - radius / 2.0f - 0.5f, y22 + radius / 2.0f, x22 + width2, y22 + height2 - radius / 2.0f, color.getRGB());
        RectHelper.drawRect(x22, y22 + radius / 2.0f, x22 + width2 + radius / 2.0f + 0.5f, y22 + height2 - radius / 2.0f, color.getRGB());
        RectHelper.drawRect(x22 + radius / 2.0f, y22 - radius / 2.0f - 0.5f, x22 + width2 - radius / 2.0f, y2 + (double)height2 - (double)(radius / 2.0f), color.getRGB());
        RectHelper.drawRect(x22 + radius / 2.0f, y22, x22 + width2 - radius / 2.0f, y22 + height2 + radius / 2.0f + 0.5f, color.getRGB());
    }

    public static Color setAlpha(Color color, int alpha) {
        alpha = MathHelper.clamp(alpha, 0, 255);
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha);
    }

    public static void drawPolygonPart(double x2, double y2, int radius, int part, int color, int endcolor) {
        float alpha = (float)(color >> 24 & 0xFF) / 255.0f;
        float red = (float)(color >> 16 & 0xFF) / 255.0f;
        float green = (float)(color >> 8 & 0xFF) / 255.0f;
        float blue = (float)(color & 0xFF) / 255.0f;
        float alpha2 = (float)(endcolor >> 24 & 0xFF) / 255.0f;
        float red2 = (float)(endcolor >> 16 & 0xFF) / 255.0f;
        float green2 = (float)(endcolor >> 8 & 0xFF) / 255.0f;
        float blue2 = (float)(endcolor & 0xFF) / 255.0f;
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.shadeModel(7425);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(6, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos(x2, y2, 0.0).color(red, green, blue, alpha).endVertex();
        double TWICE_PI = Math.PI * 2;
        for (int i2 = part * 90; i2 <= part * 90 + 90; ++i2) {
            double angle = Math.PI * 2 * (double)i2 / 360.0 + Math.toRadians(180.0);
            bufferbuilder.pos(x2 + Math.sin(angle) * (double)radius, y2 + Math.cos(angle) * (double)radius, 0.0).color(red2, green2, blue2, alpha2).endVertex();
        }
        tessellator.draw();
        GlStateManager.shadeModel(7424);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
    }

    public static void drawGlow(double x2, double y2, double x1, double y1, int color) {
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.shadeModel(7425);
        RectHelper.drawVerticalGradientRect((int)x2, (int)y2, (int)x1, (int)(y2 + (y1 - y2) / 2.0), RectHelper.setAlpha(new Color(color), 0).getRGB(), color);
        RectHelper.drawVerticalGradientRect((int)x2, (int)(y2 + (y1 - y2) / 2.0), (int)x1, (int)y1, color, RectHelper.setAlpha(new Color(color), 0).getRGB());
        int radius = (int)((y1 - y2) / 2.0);
        RectHelper.drawPolygonPart(x2, y2 + (y1 - y2) / 2.0, radius, 0, color, RectHelper.setAlpha(new Color(color), 0).getRGB());
        RectHelper.drawPolygonPart(x2, y2 + (y1 - y2) / 2.0, radius, 1, color, RectHelper.setAlpha(new Color(color), 0).getRGB());
        RectHelper.drawPolygonPart(x1, y2 + (y1 - y2) / 2.0, radius, 2, color, RectHelper.setAlpha(new Color(color), 0).getRGB());
        RectHelper.drawPolygonPart(x1, y2 + (y1 - y2) / 2.0, radius, 3, color, RectHelper.setAlpha(new Color(color), 0).getRGB());
        GlStateManager.shadeModel(7424);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
    }

    public static void drawVerticalGradientSmoothRect(float left, float top, float right, float bottom, int color, int color2) {
        GL11.glEnable(3042);
        GL11.glEnable(2848);
        Gui.drawGradientRect((int)left, (int)top, (int)right, (int)bottom, color, color2);
        GL11.glScalef(0.5f, 0.5f, 0.5f);
        Gui.drawGradientRect((int)left * 2 - 1, (int)top * 2, (int)left * 2, (int)bottom * 2 - 1, color, color2);
        Gui.drawGradientRect((int)left * 2, (int)top * 2 - 1, (int)right * 2, (int)top * 2, color, color2);
        Gui.drawGradientRect((int)right * 2, (int)top * 2, (int)right * 2 + 1, (int)bottom * 2 - 1, color, color2);
        Gui.drawGradientRect((int)left * 2, (int)bottom * 2 - 1, (int)right * 2, (int)bottom * 2, color, color2);
        GL11.glDisable(2848);
        GL11.glDisable(3042);
        GL11.glScalef(2.0f, 2.0f, 2.0f);
    }

    public static void drawVerticalGradientRect(float left, float top, float right, float bottom, int color, int color2) {
        Gui.drawGradientRect((int)left, (int)top, (int)right, (int)bottom, color, color2);
    }

    public static void drawVerticalGradientBetterRect(float x2, float y2, float width, float height, int color, int color2) {
        Gui.drawGradientRect((int)x2, (int)y2, (int)x2 + (int)width, (int)y2 + (int)height, color, color2);
    }

    public static void polygon(double x2, double y2, double sideLength, double amountOfSides, boolean filled, Color color) {
        sideLength /= 2.0;
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glDisable(3553);
        GL11.glDisable(2884);
        GlStateManager.disableAlpha();
        GL11.glColor4f((float)color.getRed() / 255.0f, (float)color.getGreen() / 255.0f, (float)color.getBlue() / 255.0f, (float)color.getAlpha() / 255.0f);
        if (!filled) {
            GL11.glLineWidth(1.0f);
        }
        GL11.glEnable(2848);
        GL11.glBegin(filled ? 6 : 3);
        for (double i2 = 0.0; i2 <= amountOfSides; i2 += 1.0) {
            double angle = i2 * (Math.PI * 2) / amountOfSides;
            GL11.glVertex2d(x2 + sideLength * Math.cos(angle) + sideLength, y2 + sideLength * Math.sin(angle) + sideLength);
        }
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GL11.glEnd();
        GL11.glDisable(2848);
        GlStateManager.enableAlpha();
        GL11.glEnable(2884);
        GL11.glEnable(3553);
        GL11.glDisable(3042);
    }

    public static void drawRectBetter(double x2, double y2, double width, double height, int color) {
        RectHelper.drawRect(x2, y2, x2 + width, y2 + height, color);
    }

    public static void drawGradientRectBetter(float x2, float y2, float width, float height, int color, int color2) {
        RectHelper.drawGradientRect(x2, y2, x2 + width, y2 + height, color, color2);
    }

    public static void drawSmoothGradientRect(double left, double top, double right, double bottom, int color, int color2) {
        GL11.glEnable(3042);
        GL11.glEnable(2848);
        RectHelper.drawGradientRect(left, top, right, bottom, color, color2);
        GL11.glScalef(0.5f, 0.5f, 0.5f);
        RectHelper.drawGradientRect(left * 2.0 - 1.0, top * 2.0, left * 2.0, bottom * 2.0 - 1.0, color, color2);
        RectHelper.drawGradientRect(left * 2.0, top * 2.0 - 1.0, right * 2.0, top * 2.0, color, color2);
        RectHelper.drawGradientRect(right * 2.0, top * 2.0, right * 2.0 + 1.0, bottom * 2.0 - 1.0, color, color2);
        RectHelper.drawGradientRect(left * 2.0, bottom * 2.0 - 1.0, right * 2.0, bottom * 2.0, color, color2);
        GL11.glDisable(2848);
        GL11.glDisable(3042);
        GL11.glScalef(2.0f, 2.0f, 2.0f);
    }

    public static void drawSmoothRectBetter(float x2, float y2, float width, float height, int color) {
        RectHelper.drawSmoothRect(x2, y2, x2 + width, y2 + height, color);
    }

    public static void drawRect(double left, double top, double right, double bottom, int color) {
        if (left < right) {
            double i2 = left;
            left = right;
            right = i2;
        }
        if (top < bottom) {
            double j2 = top;
            top = bottom;
            bottom = j2;
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
        bufferbuilder.pos(left, bottom, 0.0).endVertex();
        bufferbuilder.pos(right, bottom, 0.0).endVertex();
        bufferbuilder.pos(right, top, 0.0).endVertex();
        bufferbuilder.pos(left, top, 0.0).endVertex();
        tessellator.draw();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }

    public static void drawSmoothRect(float left, float top, float right, float bottom, int color) {
        GL11.glEnable(3042);
        GL11.glEnable(2848);
        RectHelper.drawRect(left, top, right, bottom, color);
        GL11.glScalef(0.5f, 0.5f, 0.5f);
        RectHelper.drawRect(left * 2.0f - 1.0f, top * 2.0f, left * 2.0f, bottom * 2.0f - 1.0f, color);
        RectHelper.drawRect(left * 2.0f, top * 2.0f - 1.0f, right * 2.0f, top * 2.0f, color);
        RectHelper.drawRect(right * 2.0f, top * 2.0f, right * 2.0f + 1.0f, bottom * 2.0f - 1.0f, color);
        RectHelper.drawRect(left * 2.0f, bottom * 2.0f - 1.0f, right * 2.0f, bottom * 2.0f, color);
        GL11.glDisable(2848);
        GL11.glDisable(3042);
        GL11.glScalef(2.0f, 2.0f, 2.0f);
    }

    public static void drawGradientRect(double left, double top, double right, double bottom, int color, int color2) {
        float f2 = (float)(color >> 24 & 0xFF) / 255.0f;
        float f1 = (float)(color >> 16 & 0xFF) / 255.0f;
        float f22 = (float)(color >> 8 & 0xFF) / 255.0f;
        float f3 = (float)(color & 0xFF) / 255.0f;
        float f4 = (float)(color2 >> 24 & 0xFF) / 255.0f;
        float f5 = (float)(color2 >> 16 & 0xFF) / 255.0f;
        float f6 = (float)(color2 >> 8 & 0xFF) / 255.0f;
        float f7 = (float)(color2 & 0xFF) / 255.0f;
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.shadeModel(7425);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos(left, top, Gui.zLevel).color(f1, f22, f3, f2).endVertex();
        bufferbuilder.pos(left, bottom, Gui.zLevel).color(f1, f22, f3, f2).endVertex();
        bufferbuilder.pos(right, bottom, Gui.zLevel).color(f5, f6, f7, f4).endVertex();
        bufferbuilder.pos(right, top, Gui.zLevel).color(f5, f6, f7, f4).endVertex();
        tessellator.draw();
        GlStateManager.shadeModel(7424);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
    }

    public static void drawSkeetButton(float x2, float y2, float right, float bottom) {
        RectHelper.drawSmoothRect(x2 - 31.0f, y2 - 43.0f, right + 31.0f, bottom - 30.0f, new Color(0, 0, 0, 255).getRGB());
        RectHelper.drawSmoothRect(x2 - 30.5f, y2 - 42.5f, right + 30.5f, bottom - 30.5f, new Color(45, 45, 45, 255).getRGB());
        Gui.drawGradientRect((int)x2 - 30, (int)y2 - 42, (int)right + 30, (int)bottom - 31, new Color(48, 48, 48, 255).getRGB(), new Color(19, 19, 19, 255).getRGB());
    }

    public static void drawSkeetRectWithoutBorder(float x2, float y2, float right, float bottom) {
        RectHelper.drawSmoothRect(x2 - 41.0f, y2 - 61.0f, right + 41.0f, bottom + 61.0f, new Color(48, 48, 48, 255).getRGB());
        RectHelper.drawSmoothRect(x2 - 40.0f, y2 - 60.0f, right + 40.0f, bottom + 60.0f, new Color(17, 17, 17, 255).getRGB());
    }

    public static void drawSkeetRect(float x2, float y2, float right, float bottom) {
        RectHelper.drawRect(x2 - 46.5f, y2 - 66.5f, right + 46.5f, bottom + 66.5f, new Color(0, 0, 0, 255).getRGB());
        RectHelper.drawRect(x2 - 46.0f, y2 - 66.0f, right + 46.0f, bottom + 66.0f, new Color(48, 48, 48, 255).getRGB());
        RectHelper.drawRect(x2 - 44.5f, y2 - 64.5f, right + 44.5f, bottom + 64.5f, new Color(33, 33, 33, 255).getRGB());
        RectHelper.drawRect(x2 - 43.5f, y2 - 63.5f, right + 43.5f, bottom + 63.5f, new Color(0, 0, 0, 255).getRGB());
        RectHelper.drawRect(x2 - 43.0f, y2 - 63.0f, right + 43.0f, bottom + 63.0f, new Color(9, 9, 9, 255).getRGB());
        RectHelper.drawRect(x2 - 40.5f, y2 - 60.5f, right + 40.5f, bottom + 60.5f, new Color(48, 48, 48, 255).getRGB());
        RectHelper.drawRect(x2 - 40.0f, y2 - 60.0f, right + 40.0f, bottom + 60.0f, new Color(17, 17, 17, 255).getRGB());
    }

    public static void drawBorderedRect(float left, float top, float right, float bottom, float borderWidth, int insideColor, int borderColor, boolean borderIncludedInBounds) {
        RectHelper.drawRect(left - (!borderIncludedInBounds ? borderWidth : 0.0f), top - (!borderIncludedInBounds ? borderWidth : 0.0f), right + (!borderIncludedInBounds ? borderWidth : 0.0f), bottom + (!borderIncludedInBounds ? borderWidth : 0.0f), borderColor);
        RectHelper.drawRect(left + (borderIncludedInBounds ? borderWidth : 0.0f), top + (borderIncludedInBounds ? borderWidth : 0.0f), right - (borderIncludedInBounds ? borderWidth : 0.0f), bottom - (borderIncludedInBounds ? borderWidth : 0.0f), insideColor);
    }

    public static void drawBorder(float left, float top, float right, float bottom, float borderWidth, int insideColor, int borderColor, boolean borderIncludedInBounds) {
        RectHelper.drawRect(left - (!borderIncludedInBounds ? borderWidth : 0.0f), top - (!borderIncludedInBounds ? borderWidth : 0.0f), right + (!borderIncludedInBounds ? borderWidth : 0.0f), bottom + (!borderIncludedInBounds ? borderWidth : 0.0f), borderColor);
        RectHelper.drawRect(left + (borderIncludedInBounds ? borderWidth : 0.0f), top + (borderIncludedInBounds ? borderWidth : 0.0f), right - (borderIncludedInBounds ? borderWidth : 0.0f), bottom - (borderIncludedInBounds ? borderWidth : 0.0f), insideColor);
    }

    public static void drawOutlineRect(float x2, float y2, float width, float height, Color color, Color colorTwo) {
        RectHelper.drawRect(x2, y2, x2 + width, y2 + height, color.getRGB());
        int colorRgb = colorTwo.getRGB();
        RectHelper.drawRect(x2 - 1.0f, y2, x2, y2 + height, colorRgb);
        RectHelper.drawRect(x2 + width, y2, x2 + width + 1.0f, y2 + height, colorRgb);
        RectHelper.drawRect(x2 - 1.0f, y2 - 1.0f, x2 + width + 1.0f, y2, colorRgb);
        RectHelper.drawRect(x2 - 1.0f, y2 + height, x2 + width + 1.0f, y2 + height + 1.0f, colorRgb);
    }
}

