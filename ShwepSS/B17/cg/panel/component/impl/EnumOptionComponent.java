package ShwepSS.B17.cg.panel.component.impl;

import ShwepSS.B17.cg.ColorUtils;
import ShwepSS.B17.cg.RenderUtil;
import ShwepSS.B17.cg.panel.Panel;
import ShwepSS.B17.cg.panel.component.Component;
import ShwepSS.B17.cg.settings.Setting;
import com.mojang.realmsclient.gui.ChatFormatting;
import java.awt.Color;
import java.util.ArrayList;
import net.minecraft.client.Minecraft;

public final class EnumOptionComponent
extends Component {
    private int opacity = 120;

    public EnumOptionComponent(Setting option, Panel panel, int x2, int y2, int width, int height) {
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
        RenderUtil.drawRect(x2, y2, x2 + this.getWidth(), y2 + this.getHeight(), parent.dragging ? 0x9000000 : ColorUtils.getColorWithOpacity(BACKGROUND, 255 - this.opacity).getRGB());
        int color = new Color(this.opacity, this.opacity, this.opacity).getRGB();
        Minecraft.getMinecraft().fontRendererObj.drawStringWithShadow(String.format("%s: %s", this.option.getName(), (Object)((Object)ChatFormatting.GRAY) + this.option.getValString()), x2 + 2, (float)y2 + (float)this.getHeight() / 2.0f - 2.0f, -1);
    }

    @Override
    public void onMouseClick(int mouseX, int mouseY, int mouseButton) {
        if (this.isMouseOver(mouseX, mouseY)) {
            ArrayList<String> options = this.option.getOptions();
            int index = options.indexOf(this.option.getValString());
            if (mouseButton == 0) {
                ++index;
            } else if (mouseButton == 1) {
                --index;
            }
            if (index >= options.size()) {
                index = 0;
            } else if (index < 0) {
                index = options.size() - 1;
            }
            this.option.setValString(this.option.getOptions().get(index));
        }
    }
}

