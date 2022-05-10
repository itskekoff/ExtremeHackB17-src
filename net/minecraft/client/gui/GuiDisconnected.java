package net.minecraft.client.gui;

import java.io.IOException;
import java.util.List;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.ITextComponent;

public class GuiDisconnected
extends GuiScreen {
    public final String reason;
    public final ITextComponent message;
    private List<String> multilineMessage;
    public final GuiScreen parentScreen;
    private int textHeight;

    public GuiDisconnected(GuiScreen screen, String reasonLocalizationKey, ITextComponent chatComp) {
        this.parentScreen = screen;
        this.reason = I18n.format(reasonLocalizationKey, new Object[0]);
        this.message = chatComp;
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
    }

    @Override
    public void initGui() {
        this.buttonList.clear();
        this.multilineMessage = this.fontRendererObj.listFormattedStringToWidth(this.message.getFormattedText(), width - 50);
        this.textHeight = this.multilineMessage.size() * this.fontRendererObj.FONT_HEIGHT;
        this.buttonList.add(new GuiButton(0, width / 2 - 100, Math.min(height / 2 + this.textHeight / 2 + this.fontRendererObj.FONT_HEIGHT, height - 30), I18n.format("gui.toMenu", new Object[0])));
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.id == 0) {
            this.mc.displayGuiScreen(this.parentScreen);
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        GuiDisconnected.drawDefaultBackground();
        this.drawCenteredString(this.fontRendererObj, this.reason, width / 2, height / 2 - this.textHeight / 2 - this.fontRendererObj.FONT_HEIGHT * 2, 0xAAAAAA);
        int i2 = height / 2 - this.textHeight / 2;
        if (this.multilineMessage != null) {
            for (String s2 : this.multilineMessage) {
                this.drawCenteredString(this.fontRendererObj, s2, width / 2, i2, 0xFFFFFF);
                i2 += this.fontRendererObj.FONT_HEIGHT;
            }
        }
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}

