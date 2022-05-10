package net.minecraft.client.gui.inventory;

import java.io.IOException;
import net.minecraft.block.Block;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.CPacketUpdateSign;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.ChatAllowedCharacters;
import net.minecraft.util.text.TextComponentString;
import org.lwjgl.input.Keyboard;

public class GuiEditSign
extends GuiScreen {
    private final TileEntitySign tileSign;
    private int updateCounter;
    private int editLine;
    private GuiButton doneBtn;

    public GuiEditSign(TileEntitySign teSign) {
        this.tileSign = teSign;
    }

    @Override
    public void initGui() {
        this.buttonList.clear();
        Keyboard.enableRepeatEvents(true);
        this.doneBtn = this.addButton(new GuiButton(0, width / 2 - 100, height / 4 + 120, I18n.format("gui.done", new Object[0])));
        this.tileSign.setEditable(false);
    }

    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
        NetHandlerPlayClient nethandlerplayclient = this.mc.getConnection();
        if (nethandlerplayclient != null) {
            nethandlerplayclient.sendPacket(new CPacketUpdateSign(this.tileSign.getPos(), this.tileSign.signText));
        }
        this.tileSign.setEditable(true);
    }

    @Override
    public void updateScreen() {
        ++this.updateCounter;
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.enabled && button.id == 0) {
            this.tileSign.markDirty();
            this.mc.displayGuiScreen(null);
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (keyCode == 200) {
            this.editLine = this.editLine - 1 & 3;
        }
        if (keyCode == 208 || keyCode == 28 || keyCode == 156) {
            this.editLine = this.editLine + 1 & 3;
        }
        String s2 = this.tileSign.signText[this.editLine].getUnformattedText();
        if (keyCode == 14 && !s2.isEmpty()) {
            s2 = s2.substring(0, s2.length() - 1);
        }
        if (ChatAllowedCharacters.isAllowedCharacter(typedChar) && this.fontRendererObj.getStringWidth(String.valueOf(s2) + typedChar) <= 90) {
            s2 = String.valueOf(s2) + typedChar;
        }
        this.tileSign.signText[this.editLine] = new TextComponentString(s2);
        if (keyCode == 1) {
            this.actionPerformed(this.doneBtn);
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawCenteredString(this.fontRendererObj, I18n.format("sign.edit", new Object[0]), width / 2, 40, 0xFFFFFF);
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.pushMatrix();
        GlStateManager.translate(width / 2, 0.0f, 50.0f);
        float f2 = 93.75f;
        GlStateManager.scale(-93.75f, -93.75f, -93.75f);
        GlStateManager.rotate(180.0f, 0.0f, 1.0f, 0.0f);
        Block block = this.tileSign.getBlockType();
        if (block == Blocks.STANDING_SIGN) {
            float f1 = (float)(this.tileSign.getBlockMetadata() * 360) / 16.0f;
            GlStateManager.rotate(f1, 0.0f, 1.0f, 0.0f);
            GlStateManager.translate(0.0f, -1.0625f, 0.0f);
        } else {
            int i2 = this.tileSign.getBlockMetadata();
            float f22 = 0.0f;
            if (i2 == 2) {
                f22 = 180.0f;
            }
            if (i2 == 4) {
                f22 = 90.0f;
            }
            if (i2 == 5) {
                f22 = -90.0f;
            }
            GlStateManager.rotate(f22, 0.0f, 1.0f, 0.0f);
            GlStateManager.translate(0.0f, -1.0625f, 0.0f);
        }
        if (this.updateCounter / 6 % 2 == 0) {
            this.tileSign.lineBeingEdited = this.editLine;
        }
        TileEntityRendererDispatcher.instance.renderTileEntityAt(this.tileSign, -0.5, -0.75, -0.5, 0.0f);
        this.tileSign.lineBeingEdited = -1;
        GlStateManager.popMatrix();
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}

