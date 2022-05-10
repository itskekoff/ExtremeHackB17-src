package net.minecraft.client.gui;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;

public class GuiLabel
extends Gui {
    protected int width = 200;
    protected int height = 20;
    public int x;
    public int y;
    private final List<String> labels;
    public int id;
    private boolean centered;
    public boolean visible = true;
    private boolean labelBgEnabled;
    private final int textColor;
    private int backColor;
    private int ulColor;
    private int brColor;
    private final FontRenderer fontRenderer;
    private int border;

    public GuiLabel(FontRenderer fontRendererObj, int p_i45540_2_, int p_i45540_3_, int p_i45540_4_, int p_i45540_5_, int p_i45540_6_, int p_i45540_7_) {
        this.fontRenderer = fontRendererObj;
        this.id = p_i45540_2_;
        this.x = p_i45540_3_;
        this.y = p_i45540_4_;
        this.width = p_i45540_5_;
        this.height = p_i45540_6_;
        this.labels = Lists.newArrayList();
        this.centered = false;
        this.labelBgEnabled = false;
        this.textColor = p_i45540_7_;
        this.backColor = -1;
        this.ulColor = -1;
        this.brColor = -1;
        this.border = 0;
    }

    public void addLine(String p_175202_1_) {
        this.labels.add(I18n.format(p_175202_1_, new Object[0]));
    }

    public GuiLabel setCentered() {
        this.centered = true;
        return this;
    }

    public void drawLabel(Minecraft mc, int mouseX, int mouseY) {
        if (this.visible) {
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            this.drawLabelBackground(mc, mouseX, mouseY);
            int i2 = this.y + this.height / 2 + this.border / 2;
            int j2 = i2 - this.labels.size() * 10 / 2;
            for (int k2 = 0; k2 < this.labels.size(); ++k2) {
                if (this.centered) {
                    this.drawCenteredString(this.fontRenderer, this.labels.get(k2), this.x + this.width / 2, j2 + k2 * 10, this.textColor);
                    continue;
                }
                this.drawString(this.fontRenderer, this.labels.get(k2), this.x, j2 + k2 * 10, this.textColor);
            }
        }
    }

    protected void drawLabelBackground(Minecraft mcIn, int p_146160_2_, int p_146160_3_) {
        if (this.labelBgEnabled) {
            int i2 = this.width + this.border * 2;
            int j2 = this.height + this.border * 2;
            int k2 = this.x - this.border;
            int l2 = this.y - this.border;
            GuiLabel.drawRect(k2, l2, k2 + i2, l2 + j2, this.backColor);
            this.drawHorizontalLine(k2, k2 + i2, l2, this.ulColor);
            this.drawHorizontalLine(k2, k2 + i2, l2 + j2, this.brColor);
            this.drawVerticalLine(k2, l2, l2 + j2, this.ulColor);
            this.drawVerticalLine(k2 + i2, l2, l2 + j2, this.brColor);
        }
    }
}

