package ShwepSS.B17.cg.panel.component.impl;

import ShwepSS.B17.HackConfigs;
import ShwepSS.B17.cg.AnimationUtil;
import ShwepSS.B17.cg.ColorUtils;
import ShwepSS.B17.cg.RenderUtil;
import ShwepSS.B17.cg.panel.Panel;
import ShwepSS.B17.cg.panel.component.Component;
import ShwepSS.B17.cg.settings.Setting;
import java.awt.Color;
import java.math.BigDecimal;
import java.math.RoundingMode;
import net.minecraft.client.Minecraft;

public final class NumberOptionComponent
extends Component {
    private boolean dragging = false;
    private int opacity = 120;
    private float animation = 0.0f;
    private float textHoverAnimate = 0.0f;
    private float currentValueAnimate = 0.0f;

    public NumberOptionComponent(Setting option, Panel panel, int x2, int y2, int width, int height) {
        super(panel, x2, y2, width, height);
        this.option = option;
    }

    @Override
    public void onDraw(int mouseX, int mouseY) {
        Panel parent = this.getPanel();
        int x2 = parent.getX() + this.getX();
        int y2 = parent.getY() + this.getY() + -1;
        boolean hovered = this.isMouseOver(mouseX, mouseY);
        int height = this.getHeight();
        int width = this.getWidth();
        Setting option = this.option;
        double min = option.getMin();
        double max = option.getMax();
        if (this.dragging) {
            option.setValDouble(this.round((double)(mouseX - x2) * (max - min) / (double)width + min, 0.01));
            if (Double.valueOf(option.getValDouble()) > max) {
                option.setValDouble(max);
            } else if (Double.valueOf(option.getValDouble()) < min) {
                option.setValDouble(min);
            }
        }
        double optionValue = this.round(option.getValDouble(), 0.01);
        String optionValueStr = String.valueOf(optionValue);
        int color = Color.WHITE.getRGB();
        double kak = (option.getValDouble() - option.getMin()) / (option.getMax() - option.getMin());
        this.currentValueAnimate = AnimationUtil.animation(this.currentValueAnimate, (float)kak, 1.0E-9f);
        double renderPerc = (double)(width - 2) / (max - min);
        double barWidth = renderPerc * optionValue - renderPerc * min;
        if (hovered) {
            if (this.opacity < 200) {
                this.opacity += 5;
            }
        } else if (this.opacity > 120) {
            this.opacity -= 5;
        }
        this.textHoverAnimate = AnimationUtil.animation(this.textHoverAnimate, hovered ? 2.3f : 2.0f, 1.0E-12f);
        this.animation = AnimationUtil.animation(this.animation, this.dragging ? y2 + height - 6 : y2 + height - 5, 1.0E-6f);
        RenderUtil.drawRect(x2, y2, x2 + width, y2 + height, parent.dragging ? 0x9000000 : ColorUtils.getColorWithOpacity(BACKGROUND, 255 - this.opacity).getRGB());
        RenderUtil.drawRect(x2 + 3, this.animation, x2 + (width - 3), y2 + height - 2, new Color(45, 44, 44).getRGB());
        RenderUtil.drawGradientSideways(x2 + 3, y2 + height - 5, (float)x2 + (float)width * this.currentValueAnimate, y2 + height - 2, HackConfigs.ThemeColor, parent.category.getColor2());
        Minecraft.getMinecraft().fontRendererObj.drawStringWithShadow(option.getName(), (float)x2 + 2.0f, (float)y2 + (float)height / this.textHoverAnimate - 4.0f, color);
        Minecraft.getMinecraft().fontRendererObj.drawStringWithShadow(optionValueStr, x2 + width - Minecraft.getMinecraft().fontRendererObj.getStringWidth(optionValueStr) - 3, (float)y2 + (float)height / this.textHoverAnimate - 4.0f, color);
    }

    @Override
    public void onMouseClick(int mouseX, int mouseY, int mouseButton) {
        if (this.isMouseOver(mouseX, mouseY)) {
            this.dragging = true;
        }
    }

    @Override
    public void onMouseRelease(int mouseX, int mouseY, int mouseButton) {
        this.dragging = false;
    }

    private double round(double num, double increment) {
        double v2 = (double)Math.round(num / increment) * increment;
        BigDecimal bd2 = new BigDecimal(v2);
        bd2 = bd2.setScale(2, RoundingMode.HALF_UP);
        return bd2.doubleValue();
    }
}

