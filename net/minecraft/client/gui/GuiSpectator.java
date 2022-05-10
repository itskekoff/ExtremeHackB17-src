package net.minecraft.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.spectator.ISpectatorMenuObject;
import net.minecraft.client.gui.spectator.ISpectatorMenuRecipient;
import net.minecraft.client.gui.spectator.SpectatorMenu;
import net.minecraft.client.gui.spectator.categories.SpectatorDetails;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

public class GuiSpectator
extends Gui
implements ISpectatorMenuRecipient {
    private static final ResourceLocation WIDGETS = new ResourceLocation("textures/gui/widgets.png");
    public static final ResourceLocation SPECTATOR_WIDGETS = new ResourceLocation("textures/gui/spectator_widgets.png");
    private final Minecraft mc;
    private long lastSelectionTime;
    private SpectatorMenu menu;

    public GuiSpectator(Minecraft mcIn) {
        this.mc = mcIn;
    }

    public void onHotbarSelected(int p_175260_1_) {
        this.lastSelectionTime = Minecraft.getSystemTime();
        if (this.menu != null) {
            this.menu.selectSlot(p_175260_1_);
        } else {
            this.menu = new SpectatorMenu(this);
        }
    }

    private float getHotbarAlpha() {
        long i2 = this.lastSelectionTime - Minecraft.getSystemTime() + 5000L;
        return MathHelper.clamp((float)i2 / 2000.0f, 0.0f, 1.0f);
    }

    public void renderTooltip(ScaledResolution p_175264_1_, float p_175264_2_) {
        if (this.menu != null) {
            float f2 = this.getHotbarAlpha();
            if (f2 <= 0.0f) {
                this.menu.exit();
            } else {
                int i2 = p_175264_1_.getScaledWidth() / 2;
                float f1 = zLevel;
                zLevel = -90.0f;
                float f22 = (float)p_175264_1_.getScaledHeight() - 22.0f * f2;
                SpectatorDetails spectatordetails = this.menu.getCurrentPage();
                this.renderPage(p_175264_1_, f2, i2, f22, spectatordetails);
                zLevel = f1;
            }
        }
    }

    protected void renderPage(ScaledResolution p_175258_1_, float p_175258_2_, int p_175258_3_, float p_175258_4_, SpectatorDetails p_175258_5_) {
        GlStateManager.enableRescaleNormal();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.color(1.0f, 1.0f, 1.0f, p_175258_2_);
        this.mc.getTextureManager().bindTexture(WIDGETS);
        this.drawTexturedModalRect((float)(p_175258_3_ - 91), p_175258_4_, 0, 0, 182, 22);
        if (p_175258_5_.getSelectedSlot() >= 0) {
            this.drawTexturedModalRect((float)(p_175258_3_ - 91 - 1 + p_175258_5_.getSelectedSlot() * 20), p_175258_4_ - 1.0f, 0, 22, 24, 22);
        }
        RenderHelper.enableGUIStandardItemLighting();
        for (int i2 = 0; i2 < 9; ++i2) {
            this.renderSlot(i2, p_175258_1_.getScaledWidth() / 2 - 90 + i2 * 20 + 2, p_175258_4_ + 3.0f, p_175258_2_, p_175258_5_.getObject(i2));
        }
        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableRescaleNormal();
        GlStateManager.disableBlend();
    }

    private void renderSlot(int p_175266_1_, int p_175266_2_, float p_175266_3_, float p_175266_4_, ISpectatorMenuObject p_175266_5_) {
        this.mc.getTextureManager().bindTexture(SPECTATOR_WIDGETS);
        if (p_175266_5_ != SpectatorMenu.EMPTY_SLOT) {
            int i2 = (int)(p_175266_4_ * 255.0f);
            GlStateManager.pushMatrix();
            GlStateManager.translate(p_175266_2_, p_175266_3_, 0.0f);
            float f2 = p_175266_5_.isEnabled() ? 1.0f : 0.25f;
            GlStateManager.color(f2, f2, f2, p_175266_4_);
            p_175266_5_.renderIcon(f2, i2);
            GlStateManager.popMatrix();
            String s2 = String.valueOf(GameSettings.getKeyDisplayString(this.mc.gameSettings.keyBindsHotbar[p_175266_1_].getKeyCode()));
            if (i2 > 3 && p_175266_5_.isEnabled()) {
                this.mc.fontRendererObj.drawStringWithShadow(s2, p_175266_2_ + 19 - 2 - this.mc.fontRendererObj.getStringWidth(s2), p_175266_3_ + 6.0f + 3.0f, 0xFFFFFF + (i2 << 24));
            }
        }
    }

    public void renderSelectedItem(ScaledResolution p_175263_1_) {
        int i2 = (int)(this.getHotbarAlpha() * 255.0f);
        if (i2 > 3 && this.menu != null) {
            String s2;
            ISpectatorMenuObject ispectatormenuobject = this.menu.getSelectedItem();
            String string = s2 = ispectatormenuobject == SpectatorMenu.EMPTY_SLOT ? this.menu.getSelectedCategory().getPrompt().getFormattedText() : ispectatormenuobject.getSpectatorName().getFormattedText();
            if (s2 != null) {
                int j2 = (p_175263_1_.getScaledWidth() - this.mc.fontRendererObj.getStringWidth(s2)) / 2;
                int k2 = p_175263_1_.getScaledHeight() - 35;
                GlStateManager.pushMatrix();
                GlStateManager.enableBlend();
                GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
                this.mc.fontRendererObj.drawStringWithShadow(s2, j2, k2, 0xFFFFFF + (i2 << 24));
                GlStateManager.disableBlend();
                GlStateManager.popMatrix();
            }
        }
    }

    @Override
    public void onSpectatorMenuClosed(SpectatorMenu p_175257_1_) {
        this.menu = null;
        this.lastSelectionTime = 0L;
    }

    public boolean isMenuActive() {
        return this.menu != null;
    }

    public void onMouseScroll(int p_175259_1_) {
        int i2;
        for (i2 = this.menu.getSelectedSlot() + p_175259_1_; !(i2 < 0 || i2 > 8 || this.menu.getItem(i2) != SpectatorMenu.EMPTY_SLOT && this.menu.getItem(i2).isEnabled()); i2 += p_175259_1_) {
        }
        if (i2 >= 0 && i2 <= 8) {
            this.menu.selectSlot(i2);
            this.lastSelectionTime = Minecraft.getSystemTime();
        }
    }

    public void onMiddleClick() {
        this.lastSelectionTime = Minecraft.getSystemTime();
        if (this.isMenuActive()) {
            int i2 = this.menu.getSelectedSlot();
            if (i2 != -1) {
                this.menu.selectSlot(i2);
            }
        } else {
            this.menu = new SpectatorMenu(this);
        }
    }
}

