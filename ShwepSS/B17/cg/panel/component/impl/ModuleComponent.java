package ShwepSS.B17.cg.panel.component.impl;

import ShwepSS.B17.ExtremeHack;
import ShwepSS.B17.HackConfigs;
import ShwepSS.B17.cg.AnimationUtil;
import ShwepSS.B17.cg.ClickGuiScreen;
import ShwepSS.B17.cg.ColorUtils;
import ShwepSS.B17.cg.RenderUtil;
import ShwepSS.B17.cg.font.FontUtil;
import ShwepSS.B17.cg.panel.AnimationState;
import ShwepSS.B17.cg.panel.Panel;
import ShwepSS.B17.cg.panel.component.Component;
import ShwepSS.B17.cg.panel.component.impl.BoolOptionComponent;
import ShwepSS.B17.cg.panel.component.impl.EnumOptionComponent;
import ShwepSS.B17.cg.panel.component.impl.NumberOptionComponent;
import ShwepSS.B17.cg.panel.component.impl.VisibleComponent;
import ShwepSS.B17.cg.settings.Setting;
import ShwepSS.B17.modules.Module;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import org.lwjgl.input.Keyboard;

public final class ModuleComponent
extends Component {
    public final List components = new ArrayList();
    private final ArrayList<Component> children = new ArrayList();
    private static final Color BACKGROUND_COLOR = new Color(23, 23, 23);
    private final Module module;
    private int opacity = 120;
    private int childrenHeight;
    private double scissorBoxHeight;
    private AnimationState state = AnimationState.STATIC;
    private boolean binding;
    private float activeRectAnimate = 0.0f;
    public float animation = 0.0f;
    int onlySettingsY = 0;

    public ModuleComponent(Module module, Panel parent, int x2, int y2, int width, int height) {
        super(parent, x2, y2, width, height);
        this.module = module;
        int y22 = height;
        boolean i2 = false;
        if (ExtremeHack.instance.getSetmgr().getSettingsByMod(module) != null) {
            for (Setting s2 : ExtremeHack.instance.getSetmgr().getSettingsByMod(module)) {
                if (s2.isCombo()) {
                    this.children.add(new EnumOptionComponent(s2, this.getPanel(), x2, y2 + y22, width, height));
                    y22 += height + 20;
                }
                if (s2.isSlider()) {
                    this.children.add(new NumberOptionComponent(s2, this.getPanel(), x2, y2, width, 16));
                    y22 += height + 20;
                    y22 += height + 20;
                }
                if (!s2.isCheck()) continue;
                this.children.add(new BoolOptionComponent(s2, this.getPanel(), x2, y2 + y22, width, height));
                y22 += height + 20;
            }
        }
        this.children.add(new VisibleComponent(module, this.getPanel(), x2, y2, width, height));
        this.calculateChildrenHeight();
    }

    @Override
    public double getOffset() {
        return this.scissorBoxHeight;
    }

    private void drawChildren(int mouseX, int mouseY) {
        int childY = 15;
        ArrayList<Component> children = this.children;
        int componentListSize = children.size();
        for (int i2 = 0; i2 < componentListSize; ++i2) {
            Component child = children.get(i2);
            if (child.isHidden()) continue;
            child.setY(this.getY() + childY);
            child.onDraw(mouseX, mouseY);
            childY += 15;
        }
    }

    private int calculateChildrenHeight() {
        int height = 0;
        ArrayList<Component> children = this.children;
        int childrenSize = children.size();
        for (int i2 = 0; i2 < childrenSize; ++i2) {
            Component component = children.get(i2);
            if (component.isHidden()) continue;
            height = (int)((double)(height + component.getHeight()) + component.getOffset());
        }
        return height;
    }

    @Override
    public void onDraw(int mouseX, int mouseY) {
        int color;
        boolean hover;
        Panel parent = this.getPanel();
        int x2 = parent.getX() + this.getX();
        int y2 = parent.getY() + this.getY();
        int height = this.getHeight();
        int width = this.getWidth();
        boolean hovered = this.isMouseOver(mouseX, mouseY);
        this.handleScissorBox();
        this.childrenHeight = this.calculateChildrenHeight();
        if (hovered) {
            if (this.opacity < 200) {
                this.opacity += 5;
            }
        } else if (this.opacity > 120) {
            this.opacity -= 5;
        }
        this.activeRectAnimate = AnimationUtil.animation(this.activeRectAnimate, (hover = hovered) ? 4.0f : 2.0f, 0.001f);
        int opacity = this.opacity;
        RenderUtil.drawRect(x2, y2, x2 + width, (float)((double)(y2 + height) + this.getOffset()), ColorUtils.getColorWithOpacity(BACKGROUND_COLOR, 255 - opacity).getRGB());
        int n2 = color = this.module.isEnabled() ? HackConfigs.ThemeColor : -1;
        if (this.module.name.chars().mapToObj(Character.UnicodeBlock::of).anyMatch(b2 -> b2.equals(Character.UnicodeBlock.CYRILLIC))) {
            Minecraft.getMinecraft().fontRendererObj.drawStringWithShadow(this.binding ? "Binding... Key:" + Keyboard.getKeyName(this.module.getKey()) : this.module.getName(), (float)x2 + 34.0f + this.activeRectAnimate, (float)y2 + (float)height / 2.5f - 4.0f, color);
        } else {
            FontUtil.elegant_16.drawCenteredString(this.binding ? "Binding... Key:" + Keyboard.getKeyName(this.module.getKey()) : this.module.getName(), (float)x2 + 48.0f + this.activeRectAnimate, (float)y2 + (float)height / 1.5f - 4.0f, color);
        }
        if (this.scissorBoxHeight > 0.0) {
            if (parent.state != AnimationState.RETRACTING) {
                RenderUtil.prepareScissorBox(x2, y2, x2 + width, (float)((double)y2 + Math.min(this.scissorBoxHeight, parent.scissorBoxHeight) + (double)height));
            }
            this.drawChildren(mouseX, mouseY);
        }
        if (hovered) {
            ClickGuiScreen.description = this.module.desc;
        }
    }

    @Override
    public void onMouseClick(int mouseX, int mouseY, int mouseButton) {
        if (this.scissorBoxHeight > 0.0) {
            ArrayList<Component> componentList = this.children;
            int componentListSize = componentList.size();
            for (int i2 = 0; i2 < componentListSize; ++i2) {
                componentList.get(i2).onMouseClick(mouseX, mouseY, mouseButton);
            }
        }
        if (this.isMouseOver(mouseX, mouseY) && mouseButton == 2) {
            this.binding = !this.binding;
            boolean bl2 = this.binding;
        }
        if (this.isMouseOver(mouseX, mouseY)) {
            if (mouseButton == 0) {
                this.module.toggle();
            } else if (mouseButton == 1 && !this.children.isEmpty()) {
                if (this.scissorBoxHeight > 0.0 && (this.state == AnimationState.EXPANDING || this.state == AnimationState.STATIC)) {
                    this.state = AnimationState.RETRACTING;
                } else if (this.scissorBoxHeight < (double)this.childrenHeight && (this.state == AnimationState.EXPANDING || this.state == AnimationState.STATIC)) {
                    this.state = AnimationState.EXPANDING;
                }
            }
        }
    }

    @Override
    public void onMouseRelease(int mouseX, int mouseY, int mouseButton) {
        if (this.scissorBoxHeight > 0.0) {
            ArrayList<Component> componentList = this.children;
            int componentListSize = componentList.size();
            for (int i2 = 0; i2 < componentListSize; ++i2) {
                componentList.get(i2).onMouseRelease(mouseX, mouseY, mouseButton);
            }
        }
    }

    @Override
    public void onKeyPress(int typedChar, int keyCode) {
        if (this.binding) {
            this.module.setKey(keyCode);
            this.binding = false;
            if (keyCode == 211) {
                this.module.setKey(0);
            } else if (keyCode == 1) {
                this.setBinding(false);
            }
        }
        if (this.scissorBoxHeight > 0.0) {
            ArrayList<Component> componentList = this.children;
            int componentListSize = componentList.size();
            for (int i2 = 0; i2 < componentListSize; ++i2) {
                componentList.get(i2).onKeyPress(typedChar, keyCode);
            }
        }
    }

    public void setBinding(boolean binding) {
        this.binding = binding;
    }

    private void handleScissorBox() {
        int childrenHeight = this.childrenHeight;
        switch (this.state) {
            case EXPANDING: {
                if (this.scissorBoxHeight < (double)childrenHeight) {
                    this.scissorBoxHeight = AnimationUtil.animate(childrenHeight, this.scissorBoxHeight, 0.06);
                } else if (this.scissorBoxHeight >= (double)childrenHeight) {
                    this.state = AnimationState.STATIC;
                }
                this.scissorBoxHeight = this.clamp(this.scissorBoxHeight, childrenHeight);
                break;
            }
            case RETRACTING: {
                if (this.scissorBoxHeight > 0.0) {
                    this.scissorBoxHeight = AnimationUtil.animate(0.0, this.scissorBoxHeight, 0.06);
                } else if (this.scissorBoxHeight <= 0.0) {
                    this.state = AnimationState.STATIC;
                }
                this.scissorBoxHeight = this.clamp(this.scissorBoxHeight, childrenHeight);
                break;
            }
            case STATIC: {
                if (this.scissorBoxHeight > 0.0 && this.scissorBoxHeight != (double)childrenHeight) {
                    this.scissorBoxHeight = AnimationUtil.animate(childrenHeight, this.scissorBoxHeight, 0.06);
                }
                this.scissorBoxHeight = this.clamp(this.scissorBoxHeight, childrenHeight);
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

