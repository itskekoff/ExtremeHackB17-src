package ShwepSS.B17.cg.panel.component.impl;

import ShwepSS.B17.cg.AnimationUtil;
import ShwepSS.B17.cg.ColorUtils;
import ShwepSS.B17.cg.RenderUtil;
import ShwepSS.B17.cg.panel.Panel;
import ShwepSS.B17.cg.panel.component.Component;
import ShwepSS.B17.modules.Module;
import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.client.Minecraft;

public class VisibleComponent
extends Component {
    private int opacity = 120;
    private final Module mod;
    float hoveredAnimation = 0.0f;

    public VisibleComponent(Module mod, Panel panel, int x2, int y2, int width, int height) {
        super(panel, x2, y2, width, height);
        this.mod = mod;
    }

    @Override
    public void onDraw(int mouseX, int mouseY) {
        Panel parent = this.getPanel();
        int x2 = parent.getX() + this.getX();
        int y2 = parent.getY() + this.getY();
        boolean hovered = this.isMouseOver(mouseX, mouseY);
        int height = this.getHeight();
        int width = this.getWidth();
        if (hovered) {
            if (this.opacity < 200) {
                this.opacity += 5;
            }
        } else if (this.opacity > 120) {
            this.opacity -= 5;
        }
        this.hoveredAnimation = AnimationUtil.animation(this.hoveredAnimation, hovered ? 2.3f : 2.0f, 0.01f);
        RenderUtil.drawRect(x2, y2, x2 + width, y2 + height, parent.dragging ? 0x9000000 : ColorUtils.getColorWithOpacity(BACKGROUND, 255 - this.opacity).getRGB());
        Minecraft.getMinecraft().fontRendererObj.drawStringWithShadow("Visible: " + (Object)((Object)ChatFormatting.GRAY) + this.mod.shown, (float)x2 + 2.0f, (float)y2 + (float)this.getHeight() / this.hoveredAnimation - 2.0f, -1);
        super.onDraw(mouseX, mouseY);
    }

    @Override
    public void onMouseClick(int mouseX, int mouseY, int mouseButton) {
        if (this.isMouseOver(mouseX, mouseY) && mouseButton == 0) {
            this.mod.shown = !this.mod.shown;
        }
    }
}

