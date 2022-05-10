package ShwepSS.B17.Utils;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;

public class GuiRenderUtils {
    public static void drawBorderedRect2(float x2, float y2, float x22, float y22, float l1, int col1, int col2) {
        GuiRenderUtils.drawRect(x2, y2, x22, y22, col2);
        GL11.glEnable(3042);
        GL11.glDisable(3553);
        GL11.glBlendFunc(770, 771);
        GL11.glLineWidth(l1);
        GL11.glShadeModel(7425);
        GuiRenderUtils.glColor(col1);
        GL11.glBegin(3);
        GL11.glVertex2f(x2, y2 - 0.5f);
        GL11.glVertex2f(x2, y22);
        GL11.glVertex2f(x22, y22);
        GL11.glVertex2f(x22, y2);
        GL11.glVertex2f(x2, y2);
        GL11.glEnd();
        GL11.glShadeModel(7424);
        GL11.glEnable(3553);
        GL11.glDisable(3042);
        GL11.glDisable(2848);
    }

    public static void drawRoundedRect(double x2, double y2, double width, double height, double radius, int color) {
        int i2;
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        double x1 = x2 + width;
        double y1 = y2 + height;
        float f2 = (float)(color >> 24 & 0xFF) / 255.0f;
        float f1 = (float)(color >> 16 & 0xFF) / 255.0f;
        float f22 = (float)(color >> 8 & 0xFF) / 255.0f;
        float f3 = (float)(color & 0xFF) / 255.0f;
        GL11.glPushAttrib(0);
        GL11.glScaled(0.5, 0.5, 0.5);
        GL11.glEnable(3042);
        x2 *= 2.0;
        y2 *= 2.0;
        x1 *= 2.0;
        y1 *= 2.0;
        GlStateManager.enableAlpha();
        GL11.glDisable(3553);
        GL11.glColor4f(f1, f22, f3, f2);
        GL11.glEnable(2848);
        GL11.glBegin(9);
        for (i2 = 0; i2 <= 90; i2 += 3) {
            GL11.glVertex2d(x2 + radius + (double)MathHelper.sin((float)i2 * (float)Math.PI / 180.0f) * (radius * -1.0), y2 + radius + (double)MathHelper.cos((float)i2 * (float)Math.PI / 180.0f) * (radius * -1.0));
        }
        for (i2 = 90; i2 <= 180; i2 += 3) {
            GL11.glVertex2d(x2 + radius + (double)MathHelper.sin((float)i2 * (float)Math.PI / 180.0f) * (radius * -1.0), y1 - radius + (double)MathHelper.cos((float)i2 * (float)Math.PI / 180.0f) * (radius * -1.0));
        }
        for (i2 = 0; i2 <= 90; i2 += 3) {
            GL11.glVertex2d(x1 - radius + (double)MathHelper.sin((float)i2 * (float)Math.PI / 180.0f) * radius, y1 - radius + (double)MathHelper.cos((float)i2 * (float)Math.PI / 180.0f) * radius);
        }
        for (i2 = 90; i2 <= 180; i2 += 3) {
            GL11.glVertex2d(x1 - radius + (double)MathHelper.sin((float)i2 * (float)Math.PI / 180.0f) * radius, y2 + radius + (double)MathHelper.cos((float)i2 * (float)Math.PI / 180.0f) * radius);
        }
        GL11.glEnd();
        GL11.glEnable(3553);
        GL11.glDisable(2848);
        GL11.glEnable(3553);
        GL11.glScaled(2.0, 2.0, 2.0);
        GL11.glPopAttrib();
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }

    public static void drawRect(float x2, float y2, float x1, float y1, int color) {
        GL11.glEnable(3042);
        GL11.glDisable(3553);
        GL11.glBlendFunc(770, 771);
        GuiRenderUtils.glColor(color);
        GL11.glBegin(7);
        GL11.glVertex2f(x2, y1);
        GL11.glVertex2f(x1, y1);
        GL11.glVertex2f(x1, y2);
        GL11.glVertex2f(x2, y2);
        GL11.glEnd();
        GL11.glEnable(3553);
        GL11.glDisable(3042);
    }

    public static void glColor(int hex) {
        float f2 = (float)(hex >> 24 & 0xFF) / 255.0f;
        float f1 = (float)(hex >> 16 & 0xFF) / 255.0f;
        float f22 = (float)(hex >> 8 & 0xFF) / 255.0f;
        float f3 = (float)(hex & 0xFF) / 255.0f;
        GL11.glColor4f(f1, f22, f3, f2);
    }
}

