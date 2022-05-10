package net.minecraft.client.gui;

import ShwepSS.B17.HackConfigs;
import ShwepSS.B17.Utils.ColorUtil;
import ShwepSS.B17.Utils.GuiRenderUtils;
import ShwepSS.B17.Utils.TimerUtils;
import ShwepSS.B17.cg.AnimationUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.ResourceLocation;

public class GuiButton
extends Gui {
    protected static final ResourceLocation BUTTON_TEXTURES = new ResourceLocation("textures/gui/widgets.png");
    private static int delay;
    protected int width = 200;
    protected int height = 20;
    public int xPosition;
    public int yPosition;
    public String displayString;
    public int id;
    public boolean enabled = true;
    public boolean visible = true;
    protected boolean hovered;
    private int Delaytimer;

    public GuiButton(int buttonId, int x2, int y2, String buttonText) {
        this(buttonId, x2, y2, 200, 20, buttonText);
    }

    public GuiButton(int buttonId, int x2, int y2, int widthIn, int heightIn, String buttonText) {
        this.id = buttonId;
        this.xPosition = x2;
        this.yPosition = y2;
        this.width = widthIn;
        this.height = heightIn;
        this.displayString = buttonText;
    }

    protected int getHoverState(boolean mouseOver) {
        int i2 = 1;
        if (!this.enabled) {
            i2 = 0;
        } else if (mouseOver) {
            i2 = 2;
        }
        return i2;
    }

    public void func_191745_a(Minecraft p_191745_1_, int p_191745_2_, int p_191745_3_, float p_191745_4_) {
        if (this.visible) {
            FontRenderer var4 = p_191745_1_.fontRendererObj;
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
            this.hovered = p_191745_2_ >= this.xPosition && p_191745_2_ >= this.yPosition && p_191745_2_ < this.xPosition + this.width && p_191745_3_ < this.yPosition + this.height;
            int var5 = this.getHoverState(this.hovered);
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
            GlStateManager.blendFunc(770, 771);
            ColorUtil.rainbow(200000000L, 1.0f).getRGB();
            int var6 = -1879048192;
            if (!this.enabled) {
                var6 = 1231106877;
            }
            TimerUtils timer = new TimerUtils();
            AnimationUtil util = new AnimationUtil();
            if (this.isMouseOver()) {
                GuiRenderUtils.drawBorderedRect2((float)AnimationUtil.animate(this.xPosition - 30, this.xPosition, 0.1), this.yPosition, (float)AnimationUtil.animate(this.xPosition + 30, this.xPosition, 0.1) + (float)this.width, this.yPosition + this.height, 0.1f, HackConfigs.ThemeColor, var6);
            } else {
                GuiRenderUtils.drawBorderedRect2(this.xPosition, this.yPosition, this.xPosition + this.width, this.yPosition + this.height, 0.1f, HackConfigs.ThemeColor, var6);
            }
            this.mouseDragged(p_191745_1_, p_191745_2_, p_191745_3_);
            Minecraft mc = Minecraft.getMinecraft();
            this.drawCenteredString(mc.fontRendererObj, this.displayString, this.xPosition + this.width / 2, this.yPosition + (this.height - 8) / 2, -1);
        }
    }

    protected void mouseDragged(Minecraft mc, int mouseX, int mouseY) {
    }

    public void mouseReleased(int mouseX, int mouseY) {
    }

    public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
        return this.enabled && this.visible && mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
    }

    public boolean isMouseOver() {
        return this.hovered;
    }

    public void drawButtonForegroundLayer(int mouseX, int mouseY) {
    }

    public void playPressSound(SoundHandler soundHandlerIn) {
        soundHandlerIn.playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.BLOCK_ANVIL_FALL, 1.0f));
    }

    public int getButtonWidth() {
        return this.width;
    }

    public void setWidth(int width) {
        this.width = width;
    }
}

