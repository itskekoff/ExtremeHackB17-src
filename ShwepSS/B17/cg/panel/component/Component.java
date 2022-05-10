package ShwepSS.B17.cg.panel.component;

import ShwepSS.B17.cg.panel.Panel;
import ShwepSS.B17.cg.settings.Setting;
import java.awt.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;

public abstract class Component {
    public static ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
    protected static final Minecraft MC = Minecraft.getMinecraft();
    protected static final Color BACKGROUND = new Color(26, 26, 26);
    private final Panel panel;
    public double width;
    public double height;
    public double x;
    public double y;
    public static boolean comboextended;
    public Setting option;
    public String setstrg;

    public Component(Panel panel, int x2, int y2, int width, int height) {
        this.panel = panel;
        this.x = x2;
        this.y = y2;
        this.width = width;
        this.height = height;
    }

    public Panel getPanel() {
        return this.panel;
    }

    public void onDraw(int mouseX, int mouseY) {
    }

    public void onMouseClick(int mouseX, int mouseY, int mouseButton) {
    }

    public void onMouseRelease(int mouseX, int mouseY, int mouseButton) {
    }

    public void onKeyPress(int typedChar, int keyCode) {
    }

    public final boolean isMouseOver(int mouseX, int mouseY) {
        int x2 = (int)((double)this.panel.getX() + this.x);
        int y2 = (int)((double)this.panel.getY() + this.y);
        return mouseX > x2 && (double)mouseX < (double)x2 + this.width && mouseY > y2 && (double)mouseY < (double)y2 + this.height;
    }

    public boolean isVisible() {
        return true;
    }

    public double getOffset() {
        return 0.0;
    }

    public int getX() {
        return (int)this.x;
    }

    public int getWidth() {
        return (int)this.width;
    }

    public void setY(int y2) {
        this.y = y2;
    }

    public int getY() {
        return (int)this.y;
    }

    public int getHeight() {
        return (int)this.height;
    }

    public boolean isHidden() {
        return false;
    }
}

