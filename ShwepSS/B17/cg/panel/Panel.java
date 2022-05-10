package ShwepSS.B17.cg.panel;

import ShwepSS.B17.ExtremeHack;
import ShwepSS.B17.HackConfigs;
import ShwepSS.B17.cg.AnimationUtil;
import ShwepSS.B17.cg.RectHelper;
import ShwepSS.B17.cg.RenderUtil;
import ShwepSS.B17.cg.font.FontUtil;
import ShwepSS.B17.cg.panel.AnimationState;
import ShwepSS.B17.cg.panel.component.Component;
import ShwepSS.B17.cg.panel.component.impl.ModuleComponent;
import ShwepSS.B17.modules.Category;
import ShwepSS.B17.modules.Module;
import ShwepSS.B17.modules.hacks.themes.QuadTheme;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.lwjgl.opengl.GL11;

public final class Panel {
    public static final int HEADER_SIZE = 20;
    public static final int HEADER_OFFSET = 2;
    public final Category category;
    public final List components = new ArrayList();
    public final int width;
    public double scissorBoxHeight;
    public int x;
    public int lastX;
    public int y;
    public int lastY;
    public int height;
    public AnimationState state = AnimationState.STATIC;
    public boolean dragging;
    public int activeRectAnimate;
    public double scalling;

    public Panel(Category category, int x2, int y2) {
        this.category = category;
        this.x = x2;
        this.y = y2;
        this.width = 100;
        int componentY = 20;
        List<Module> modulesForCategory = Arrays.asList(ExtremeHack.manager.getModulesInCategory(category));
        int modulesForCategorySize = modulesForCategory.size();
        for (int i2 = 0; i2 < modulesForCategorySize; ++i2) {
            Module module = modulesForCategory.get(i2);
            ModuleComponent component = new ModuleComponent(module, this, 0, componentY, this.width, 15);
            this.components.add(component);
            componentY += 15;
        }
        this.height = componentY - 20;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    private void updateComponentHeight() {
        int componentY = 20;
        List componentList = this.components;
        int componentListSize = componentList.size();
        for (int i2 = 0; i2 < componentListSize; ++i2) {
            Component component = (Component)componentList.get(i2);
            component.setY(componentY);
            componentY = (int)((double)componentY + (double)component.getHeight() + component.getOffset());
        }
        this.height = componentY - 20;
    }

    public final void onDraw(int mouseX, int mouseY) {
        int x2 = this.x;
        int y2 = this.y;
        int width = this.width;
        this.updateComponentHeight();
        this.handleScissorBox();
        this.handleDragging(mouseX, mouseY);
        double scissorBoxHeight = this.scissorBoxHeight;
        int backgroundColor = HackConfigs.ThemeColorGui;
        this.activeRectAnimate = (int)AnimationUtil.animate(this.activeRectAnimate, this.dragging ? -1.879048192E9 : (double)backgroundColor, 0.1f);
        if (QuadTheme.instance.isEnabled()) {
            RenderUtil.drawSmoothRect(x2 - 2, y2, x2 + width + 2, (float)((double)(y2 + 20) + scissorBoxHeight), this.dragging ? HackConfigs.ThemeColorGui : HackConfigs.ThemeColor);
        }
        RenderUtil.drawSmoothRect(x2 - 2, y2, x2 + width + 2, y2 + 20, this.dragging ? -1879048192 : backgroundColor);
        RenderUtil.drawSmoothRect(this.x + -2, this.y + -2, this.x + this.width + 2, this.y, HackConfigs.ThemeColor);
        if (ExtremeHack.instance.getSetmgr().getSettingByName("GlowPanels").getValue()) {
            RectHelper.drawGlow(this.x + -2, this.y + 20, this.x + this.width + 2, this.y, HackConfigs.ThemeColor);
        }
        GL11.glPushMatrix();
        FontUtil.roboto_20.drawCenteredString(this.category.name, x2 + 50, y2 + 7 - 3, -1);
        GL11.glEnable(3089);
        RenderUtil.prepareScissorBox(x2 - 2, y2 + 20 - 2, x2 + width + 2, (float)((double)(y2 + 20) + scissorBoxHeight));
        List components = this.components;
        int componentsSize = components.size();
        for (int i2 = 0; i2 < componentsSize; ++i2) {
            ((Component)components.get(i2)).onDraw(mouseX, mouseY);
            if (i2 == componentsSize - 1) continue;
            RenderUtil.prepareScissorBox(x2 - 2, y2 + 20, x2 + width + 2, (float)((double)(y2 + 20) + scissorBoxHeight));
        }
        GL11.glDisable(3089);
        GL11.glPopMatrix();
    }

    public final void onMouseClick(int mouseX, int mouseY, int mouseButton) {
        int x2 = this.x;
        int y2 = this.y;
        int width = this.width;
        double scissorBoxHeight = this.scissorBoxHeight;
        if (mouseX > x2 - 2 && mouseX < x2 + width + 2 && mouseY > y2 && mouseY < y2 + 20) {
            if (mouseButton == 1) {
                if (scissorBoxHeight > 0.0 && (this.state == AnimationState.EXPANDING || this.state == AnimationState.STATIC)) {
                    this.state = AnimationState.RETRACTING;
                } else if (scissorBoxHeight < (double)(this.height + 2) && (this.state == AnimationState.EXPANDING || this.state == AnimationState.STATIC)) {
                    this.state = AnimationState.EXPANDING;
                }
            } else if (mouseButton == 0 && !this.dragging) {
                this.lastX = x2 - mouseX;
                this.lastY = y2 - mouseY;
                this.dragging = true;
            }
        }
        List components = this.components;
        int componentsSize = components.size();
        for (int i2 = 0; i2 < componentsSize; ++i2) {
            Component component = (Component)components.get(i2);
            int componentY = component.getY();
            if (!((double)componentY < scissorBoxHeight + 20.0)) continue;
            component.onMouseClick(mouseX, mouseY, mouseButton);
        }
    }

    public final void onMouseRelease(int mouseX, int mouseY, int mouseButton) {
        if (this.dragging) {
            this.dragging = false;
        }
        if (this.scissorBoxHeight > 0.0) {
            List components = this.components;
            int componentsSize = components.size();
            for (int i2 = 0; i2 < componentsSize; ++i2) {
                ((Component)components.get(i2)).onMouseRelease(mouseX, mouseY, mouseButton);
            }
        }
    }

    public void setY(int y2) {
        this.y = y2;
    }

    public final void onKeyPress(char typedChar, int keyCode) {
        if (this.scissorBoxHeight > 0.0) {
            List components = this.components;
            int componentsSize = components.size();
            for (int i2 = 0; i2 < componentsSize; ++i2) {
                ((Component)components.get(i2)).onKeyPress(typedChar, keyCode);
            }
        }
    }

    private void handleDragging(int mouseX, int mouseY) {
        if (this.dragging) {
            this.x = mouseX + this.lastX;
            this.y = mouseY + this.lastY;
        }
    }

    private void handleScissorBox() {
        int height = this.height;
        switch (this.state) {
            case EXPANDING: {
                if (this.scissorBoxHeight < (double)(height + 2)) {
                    this.scissorBoxHeight = AnimationUtil.animate(height + 2, this.scissorBoxHeight, 0.07);
                    break;
                }
                if (!(this.scissorBoxHeight >= (double)(height + 2))) break;
                this.state = AnimationState.STATIC;
                break;
            }
            case RETRACTING: {
                if (this.scissorBoxHeight > 0.0) {
                    this.scissorBoxHeight = AnimationUtil.animate(0.0, this.scissorBoxHeight, 0.07);
                    break;
                }
                if (!(this.scissorBoxHeight <= 0.0)) break;
                this.state = AnimationState.STATIC;
                break;
            }
            case STATIC: {
                if (this.scissorBoxHeight > 0.0 && this.scissorBoxHeight != (double)(height + 2)) {
                    this.scissorBoxHeight = AnimationUtil.animate(height + 2, this.scissorBoxHeight, 0.07);
                }
                this.scissorBoxHeight = this.clamp(this.scissorBoxHeight, height + 2);
            }
        }
    }

    private double clamp(double a2, double max) {
        if (a2 < 0.0) {
            return 0.0;
        }
        return Math.min(a2, max);
    }
}

