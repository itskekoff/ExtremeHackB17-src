package ShwepSS.B17.cg.panel.component.impl;

import ShwepSS.B17.HackConfigs;
import ShwepSS.B17.cg.AnimationUtil;
import ShwepSS.B17.cg.ColorUtils;
import ShwepSS.B17.cg.RenderUtil;
import ShwepSS.B17.cg.panel.Panel;
import ShwepSS.B17.cg.panel.component.Component;
import ShwepSS.B17.cg.settings.Setting;
import java.awt.Color;
import net.minecraft.client.Minecraft;

public final class BoolOptionComponent
extends Component {
    private int opacity = 120;
    private int animation = 20;
    float textHoverAnimate = 0.0f;
    float leftRectAnimation = 0.0f;
    double rightRectAnimation = 0.0;

    public BoolOptionComponent(Setting option, Panel panel, int x2, int y2, int width, int height) {
        super(panel, x2, y2, width, height);
        this.option = option;
    }

    @Override
    public void onDraw(int mouseX, int mouseY) {
        Panel parent = this.getPanel();
        int x2 = parent.getX() + this.getX();
        int y2 = parent.getY() + this.getY();
        boolean hovered = this.isMouseOver(mouseX, mouseY);
        if (hovered) {
            if (this.opacity < 200) {
                this.opacity += 5;
            }
        } else if (this.opacity > 120) {
            this.opacity -= 5;
        }
        if (this.option.getValue()) {
            if (this.animation < 30) {
                ++this.animation;
            }
        } else if (this.animation > 20) {
            --this.animation;
        }
        RenderUtil.drawRect(x2, y2, x2 + this.getWidth(), y2 + this.getHeight(), parent.dragging ? 0x9000000 : ColorUtils.getColorWithOpacity(BACKGROUND, 255 - this.opacity).getRGB());
        int color = this.option.getValue() ? -1 : new Color(this.opacity, this.opacity, this.opacity).getRGB();
        this.textHoverAnimate = AnimationUtil.animation(this.textHoverAnimate, hovered ? 2.3f : 2.0f, 0.01f);
        this.leftRectAnimation = AnimationUtil.animation(this.leftRectAnimation, this.option.getValue() ? 10.0f : 17.0f, 1.0E-13f);
        this.rightRectAnimation = AnimationUtil.animation((float)this.rightRectAnimation, this.option.getValue() ? 3 : 10, 1.0E-13f);
        RenderUtil.drawSmoothRect((float)((double)x2 + this.width - 18.0), y2 + 2, (float)((double)x2 + this.width - 2.0), (float)((double)y2 + this.height - 3.0), new Color(14, 14, 14).getRGB());
        RenderUtil.drawSmoothRect((float)((double)x2 + this.width - (double)this.leftRectAnimation), y2 + 3, (float)((double)x2 + this.width - this.rightRectAnimation), y2 + this.getHeight() - 4, this.option.getValue() ? HackConfigs.ThemeColor : new Color(50, 50, 50).getRGB());
        Minecraft.getMinecraft().fontRendererObj.drawStringWithShadow(this.option.getName(), (float)x2 + 4.0f, (float)y2 + (float)this.getHeight() / this.textHoverAnimate - 3.0f, color);
    }

    @Override
    public void onMouseClick(int mouseX, int mouseY, int mouseButton) {
        if (this.isMouseOver(mouseX, mouseY)) {
            this.option.setValue(!this.option.getValue());
        }
    }
}

