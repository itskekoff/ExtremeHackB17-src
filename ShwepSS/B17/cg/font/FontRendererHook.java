package ShwepSS.B17.cg.font;

import ShwepSS.B17.cg.font.FRenderer;
import java.awt.Font;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

public final class FontRendererHook
extends FontRenderer {
    private final FRenderer fontRenderer;

    public FontRendererHook(Font font, boolean antiAliasing, boolean fractionalMetrics) {
        super(Minecraft.getMinecraft().gameSettings, new ResourceLocation("textures/font/ascii.png"), Minecraft.getMinecraft().renderEngine, false);
        this.fontRenderer = new FRenderer(font, antiAliasing, fractionalMetrics);
    }

    protected int renderString(String text, float x2, float y2, int color, boolean dropShadow) {
        if (text == null) {
            return 0;
        }
        if (this.bidiFlag) {
            text = this.bidiReorder(text);
        }
        if ((color & 0xFC000000) == 0) {
            color |= 0xFF000000;
        }
        if (dropShadow) {
            color = (color & 0xFCFCFC) >> 2 | color & 0xFF000000;
        }
        this.red = (float)(color >> 16 & 0xFF) / 255.0f;
        this.blue = (float)(color >> 8 & 0xFF) / 255.0f;
        this.green = (float)(color & 0xFF) / 255.0f;
        this.alpha = (float)(color >> 24 & 0xFF) / 255.0f;
        GlStateManager.color(this.red, this.blue, this.green, this.alpha);
        this.posX = x2;
        this.posY = y2;
        this.fontRenderer.drawString(text, x2, y2, color, dropShadow);
        return (int)this.posX;
    }

    @Override
    public int getStringWidth(String text) {
        return this.fontRenderer.getStringWidth(text);
    }
}

