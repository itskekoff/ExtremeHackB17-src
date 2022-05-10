package ShwepSS.B17.cg.util;

import ShwepSS.B17.cg.util.GLUtils;
import ShwepSS.B17.cg.util.Particle;
import com.google.common.collect.Lists;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;

public class ParticleEngine {
    public CopyOnWriteArrayList<Particle> particles = Lists.newCopyOnWriteArrayList();
    public float lastMouseX;
    public float lastMouseY;

    public static void drawCircle(double x2, double y2, float radius, int color) {
        float alpha = (float)(color >> 24 & 0xFF) / 255.0f;
        float red = (float)(color >> 16 & 0xFF) / 255.0f;
        float green = (float)(color >> 8 & 0xFF) / 255.0f;
        float blue = (float)(color & 0xFF) / 255.0f;
        GL11.glColor4f(red, green, blue, alpha);
        GL11.glBegin(9);
        for (int i2 = 0; i2 <= 360; ++i2) {
            GL11.glVertex2d(x2 + Math.sin((double)i2 * 3.141526 / 180.0) * (double)radius, y2 + Math.cos((double)i2 * 3.141526 / 180.0) * (double)radius);
        }
        GL11.glEnd();
    }

    public static void disableRender2D() {
        GL11.glDisable(3042);
        GL11.glEnable(2884);
        GL11.glEnable(3553);
        GL11.glDisable(2848);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.shadeModel(7424);
        GlStateManager.disableBlend();
        GlStateManager.enableTexture2D();
    }

    public static void enableRender2D() {
        GL11.glEnable(3042);
        GL11.glDisable(2884);
        GL11.glDisable(3553);
        GL11.glEnable(2848);
        GL11.glBlendFunc(770, 771);
        GL11.glLineWidth(1.0f);
    }

    public static void setColor(int colorHex) {
        float alpha = (float)(colorHex >> 24 & 0xFF) / 255.0f;
        float red = (float)(colorHex >> 16 & 0xFF) / 255.0f;
        float green = (float)(colorHex >> 8 & 0xFF) / 255.0f;
        float blue = (float)(colorHex & 0xFF) / 255.0f;
        GL11.glColor4f(red, green, blue, alpha == 0.0f ? 1.0f : alpha);
    }

    public static void drawLine(double startX, double startY, double endX, double endY, float thickness, int color) {
        ParticleEngine.enableRender2D();
        ParticleEngine.setColor(color);
        GL11.glLineWidth(thickness);
        GL11.glBegin(1);
        GL11.glVertex2d(startX, startY);
        GL11.glVertex2d(endX, endY);
        GL11.glEnd();
        ParticleEngine.disableRender2D();
    }

    public void render(float mouseX, float mouseY) {
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.color(1.0f, 1.0f, 1.0f, 0.2f);
        ScaledResolution sr2 = new ScaledResolution(Minecraft.getMinecraft());
        float xOffset = 0.0f;
        float yOffset = 0.0f;
        while ((float)this.particles.size() < (float)sr2.getScaledWidth() / 8.0f) {
            this.particles.add(new Particle(sr2, new Random().nextFloat() + 1.0f, new Random().nextFloat() * 5.0f + 5.0f));
        }
        ArrayList<Particle> toRemove = Lists.newArrayList();
        int maxOpacity = 52;
        int color = -570425345;
        int mouseRadius = 100;
        for (Particle particle : this.particles) {
            double particleX = (double)particle.x + Math.sin(particle.ticks / 10.0f) * 50.0 + (double)(-xOffset / 5.0f);
            double particleY = particle.ticks * particle.speed * particle.ticks / 10.0f + -yOffset / 5.0f;
            if (particleY < (double)sr2.getScaledHeight()) {
                if (particle.opacity < (float)maxOpacity) {
                    particle.opacity += 2.0f;
                }
                if (particle.opacity > (float)maxOpacity) {
                    particle.opacity = maxOpacity;
                }
                Color c2 = new Color(255, 255, 255, (int)particle.opacity);
                float particle_thickness = 1.0f;
                int line_color = new Color(1.0f, (1.0f - (float)(particleY / (double)sr2.getScaledHeight())) / 2.0f, 1.0f, 1.0f).getRGB();
                GlStateManager.enableBlend();
                this.drawBorderedCircle(particleX, particleY, particle.radius * particle.opacity / (float)maxOpacity, color, color);
            }
            particle.ticks = (float)((double)particle.ticks + 0.05);
            if (!(particleY > (double)sr2.getScaledHeight() || particleY < 0.0 || particleX > (double)sr2.getScaledWidth() || particleX < 0.0)) continue;
            toRemove.add(particle);
        }
        this.particles.removeAll(toRemove);
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        this.lastMouseX = GLUtils.getMouseX();
        this.lastMouseY = GLUtils.getMouseY();
    }

    public void drawBorderedCircle(double x2, double y2, float radius, int outsideC, int insideC) {
        GL11.glDisable(3553);
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(2848);
        GL11.glPushMatrix();
        GL11.glScalef(0.1f, 0.1f, 0.1f);
        ParticleEngine.drawCircle(x2 * 10.0, y2 * 10.0, radius * 10.0f, insideC);
        GL11.glScalef(10.0f, 10.0f, 10.0f);
        GL11.glPopMatrix();
        GL11.glEnable(3553);
        GL11.glDisable(2848);
    }
}

