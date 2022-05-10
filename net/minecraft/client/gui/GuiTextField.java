package net.minecraft.client.gui;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiPageButtonList;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ChatAllowedCharacters;
import net.minecraft.util.math.MathHelper;

public class GuiTextField
extends Gui {
    private final int id;
    private final FontRenderer fontRendererInstance;
    public int xPosition;
    public int yPosition;
    private final int width;
    private final int height;
    public String text = "";
    private int maxStringLength = 32;
    private int cursorCounter;
    private boolean enableBackgroundDrawing = true;
    private boolean canLoseFocus = true;
    private boolean isFocused;
    private boolean isEnabled = true;
    private int lineScrollOffset;
    private int cursorPosition;
    private int selectionEnd;
    private int enabledColor = 0xE0E0E0;
    private int disabledColor = 0x707070;
    private boolean visible = true;
    private GuiPageButtonList.GuiResponder guiResponder;
    private Predicate<String> validator = Predicates.alwaysTrue();

    public GuiTextField(int componentId, FontRenderer fontrendererObj, int x2, int y2, int par5Width, int par6Height) {
        this.id = componentId;
        this.fontRendererInstance = fontrendererObj;
        this.xPosition = x2;
        this.yPosition = y2;
        this.width = par5Width;
        this.height = par6Height;
    }

    public void setGuiResponder(GuiPageButtonList.GuiResponder guiResponderIn) {
        this.guiResponder = guiResponderIn;
    }

    public void updateCursorCounter() {
        ++this.cursorCounter;
    }

    public void setText(String textIn) {
        if (this.validator.apply(textIn)) {
            this.text = textIn.length() > this.maxStringLength ? textIn.substring(0, this.maxStringLength) : textIn;
            this.setCursorPositionEnd();
        }
    }

    public String getText() {
        return this.text;
    }

    public String getSelectedText() {
        int i2 = this.cursorPosition < this.selectionEnd ? this.cursorPosition : this.selectionEnd;
        int j2 = this.cursorPosition < this.selectionEnd ? this.selectionEnd : this.cursorPosition;
        return this.text.substring(i2, j2);
    }

    public void setValidator(Predicate<String> theValidator) {
        this.validator = theValidator;
    }

    public void writeText(String textToWrite) {
        int l2;
        String s2 = "";
        String s1 = ChatAllowedCharacters.filterAllowedCharacters(textToWrite);
        int i2 = this.cursorPosition < this.selectionEnd ? this.cursorPosition : this.selectionEnd;
        int j2 = this.cursorPosition < this.selectionEnd ? this.selectionEnd : this.cursorPosition;
        int k2 = this.maxStringLength - this.text.length() - (i2 - j2);
        if (!this.text.isEmpty()) {
            s2 = String.valueOf(s2) + this.text.substring(0, i2);
        }
        if (k2 < s1.length()) {
            s2 = String.valueOf(s2) + s1.substring(0, k2);
            l2 = k2;
        } else {
            s2 = String.valueOf(s2) + s1;
            l2 = s1.length();
        }
        if (!this.text.isEmpty() && j2 < this.text.length()) {
            s2 = String.valueOf(s2) + this.text.substring(j2);
        }
        if (this.validator.apply(s2)) {
            this.text = s2;
            this.moveCursorBy(i2 - this.selectionEnd + l2);
            this.func_190516_a(this.id, this.text);
        }
    }

    public void func_190516_a(int p_190516_1_, String p_190516_2_) {
        if (this.guiResponder != null) {
            this.guiResponder.setEntryValue(p_190516_1_, p_190516_2_);
        }
    }

    public void deleteWords(int num) {
        if (!this.text.isEmpty()) {
            if (this.selectionEnd != this.cursorPosition) {
                this.writeText("");
            } else {
                this.deleteFromCursor(this.getNthWordFromCursor(num) - this.cursorPosition);
            }
        }
    }

    public void deleteFromCursor(int num) {
        if (!this.text.isEmpty()) {
            if (this.selectionEnd != this.cursorPosition) {
                this.writeText("");
            } else {
                boolean flag = num < 0;
                int i2 = flag ? this.cursorPosition + num : this.cursorPosition;
                int j2 = flag ? this.cursorPosition : this.cursorPosition + num;
                String s2 = "";
                if (i2 >= 0) {
                    s2 = this.text.substring(0, i2);
                }
                if (j2 < this.text.length()) {
                    s2 = String.valueOf(s2) + this.text.substring(j2);
                }
                if (this.validator.apply(s2)) {
                    this.text = s2;
                    if (flag) {
                        this.moveCursorBy(num);
                    }
                    this.func_190516_a(this.id, this.text);
                }
            }
        }
    }

    public int getId() {
        return this.id;
    }

    public int getNthWordFromCursor(int numWords) {
        return this.getNthWordFromPos(numWords, this.getCursorPosition());
    }

    public int getNthWordFromPos(int n2, int pos) {
        return this.getNthWordFromPosWS(n2, pos, true);
    }

    /*
     * Unable to fully structure code
     * Enabled aggressive block sorting
     * Lifted jumps to return sites
     */
    public int getNthWordFromPosWS(int n, int pos, boolean skipWs) {
        i = pos;
        flag = n < 0;
        j = Math.abs(n);
        for (k = 0; k < j; ++k) {
            if (flag) ** GOTO lbl14
            l = this.text.length();
            if ((i = this.text.indexOf(32, i)) != -1) ** GOTO lbl11
            i = l;
            continue;
lbl-1000:
            // 1 sources

            {
                ++i;
lbl11:
                // 2 sources

                ** while (skipWs && i < l && this.text.charAt((int)i) == ' ')
            }
lbl12:
            // 1 sources

            continue;
lbl-1000:
            // 1 sources

            {
                --i;
lbl14:
                // 2 sources

                ** while (skipWs && i > 0 && this.text.charAt((int)(i - 1)) == ' ')
            }
lbl15:
            // 2 sources

            while (i > 0 && this.text.charAt(i - 1) != ' ') {
                --i;
            }
        }
        return i;
    }

    public void moveCursorBy(int num) {
        this.setCursorPosition(this.selectionEnd + num);
    }

    public void setCursorPosition(int pos) {
        this.cursorPosition = pos;
        int i2 = this.text.length();
        this.cursorPosition = MathHelper.clamp(this.cursorPosition, 0, i2);
        this.setSelectionPos(this.cursorPosition);
    }

    public void setCursorPositionZero() {
        this.setCursorPosition(0);
    }

    public void setCursorPositionEnd() {
        this.setCursorPosition(this.text.length());
    }

    public boolean textboxKeyTyped(char typedChar, int keyCode) {
        if (!this.isFocused) {
            return false;
        }
        if (GuiScreen.isKeyComboCtrlA(keyCode)) {
            this.setCursorPositionEnd();
            this.setSelectionPos(0);
            return true;
        }
        if (GuiScreen.isKeyComboCtrlC(keyCode)) {
            GuiScreen.setClipboardString(this.getSelectedText());
            return true;
        }
        if (GuiScreen.isKeyComboCtrlV(keyCode)) {
            if (this.isEnabled) {
                this.writeText(GuiScreen.getClipboardString());
            }
            return true;
        }
        if (GuiScreen.isKeyComboCtrlX(keyCode)) {
            GuiScreen.setClipboardString(this.getSelectedText());
            if (this.isEnabled) {
                this.writeText("");
            }
            return true;
        }
        switch (keyCode) {
            case 14: {
                if (GuiScreen.isCtrlKeyDown()) {
                    if (this.isEnabled) {
                        this.deleteWords(-1);
                    }
                } else if (this.isEnabled) {
                    this.deleteFromCursor(-1);
                }
                return true;
            }
            case 199: {
                if (GuiScreen.isShiftKeyDown()) {
                    this.setSelectionPos(0);
                } else {
                    this.setCursorPositionZero();
                }
                return true;
            }
            case 203: {
                if (GuiScreen.isShiftKeyDown()) {
                    if (GuiScreen.isCtrlKeyDown()) {
                        this.setSelectionPos(this.getNthWordFromPos(-1, this.getSelectionEnd()));
                    } else {
                        this.setSelectionPos(this.getSelectionEnd() - 1);
                    }
                } else if (GuiScreen.isCtrlKeyDown()) {
                    this.setCursorPosition(this.getNthWordFromCursor(-1));
                } else {
                    this.moveCursorBy(-1);
                }
                return true;
            }
            case 205: {
                if (GuiScreen.isShiftKeyDown()) {
                    if (GuiScreen.isCtrlKeyDown()) {
                        this.setSelectionPos(this.getNthWordFromPos(1, this.getSelectionEnd()));
                    } else {
                        this.setSelectionPos(this.getSelectionEnd() + 1);
                    }
                } else if (GuiScreen.isCtrlKeyDown()) {
                    this.setCursorPosition(this.getNthWordFromCursor(1));
                } else {
                    this.moveCursorBy(1);
                }
                return true;
            }
            case 207: {
                if (GuiScreen.isShiftKeyDown()) {
                    this.setSelectionPos(this.text.length());
                } else {
                    this.setCursorPositionEnd();
                }
                return true;
            }
            case 211: {
                if (GuiScreen.isCtrlKeyDown()) {
                    if (this.isEnabled) {
                        this.deleteWords(1);
                    }
                } else if (this.isEnabled) {
                    this.deleteFromCursor(1);
                }
                return true;
            }
        }
        if (ChatAllowedCharacters.isAllowedCharacter(typedChar)) {
            if (this.isEnabled) {
                this.writeText(Character.toString(typedChar));
            }
            return true;
        }
        return false;
    }

    public boolean mouseClicked(int mouseX, int mouseY, int mouseButton) {
        boolean flag;
        boolean bl2 = flag = mouseX >= this.xPosition && mouseX < this.xPosition + this.width && mouseY >= this.yPosition && mouseY < this.yPosition + this.height;
        if (this.canLoseFocus) {
            this.setFocused(flag);
        }
        if (this.isFocused && flag && mouseButton == 0) {
            int i2 = mouseX - this.xPosition;
            if (this.enableBackgroundDrawing) {
                i2 -= 4;
            }
            String s2 = this.fontRendererInstance.trimStringToWidth(this.text.substring(this.lineScrollOffset), this.getWidth());
            this.setCursorPosition(this.fontRendererInstance.trimStringToWidth(s2, i2).length() + this.lineScrollOffset);
            return true;
        }
        return false;
    }

    public void drawTextBox() {
        if (this.getVisible()) {
            if (this.getEnableBackgroundDrawing()) {
                GuiTextField.drawRect(this.xPosition - 1, this.yPosition - 1, this.xPosition + this.width + 1, this.yPosition + this.height + 1, -6250336);
                GuiTextField.drawRect(this.xPosition, this.yPosition, this.xPosition + this.width, this.yPosition + this.height, -16777216);
            }
            int i2 = this.isEnabled ? this.enabledColor : this.disabledColor;
            int j2 = this.cursorPosition - this.lineScrollOffset;
            int k2 = this.selectionEnd - this.lineScrollOffset;
            String s2 = this.fontRendererInstance.trimStringToWidth(this.text.substring(this.lineScrollOffset), this.getWidth());
            boolean flag = j2 >= 0 && j2 <= s2.length();
            boolean flag1 = this.isFocused && this.cursorCounter / 6 % 2 == 0 && flag;
            int l2 = this.enableBackgroundDrawing ? this.xPosition + 4 : this.xPosition;
            int i1 = this.enableBackgroundDrawing ? this.yPosition + (this.height - 8) / 2 : this.yPosition;
            int j1 = l2;
            if (k2 > s2.length()) {
                k2 = s2.length();
            }
            if (!s2.isEmpty()) {
                String s1 = flag ? s2.substring(0, j2) : s2;
                j1 = this.fontRendererInstance.drawStringWithShadow(s1, l2, i1, i2);
            }
            boolean flag2 = this.cursorPosition < this.text.length() || this.text.length() >= this.getMaxStringLength();
            int k1 = j1;
            if (!flag) {
                k1 = j2 > 0 ? l2 + this.width : l2;
            } else if (flag2) {
                k1 = j1 - 1;
                --j1;
            }
            if (!s2.isEmpty() && flag && j2 < s2.length()) {
                j1 = this.fontRendererInstance.drawStringWithShadow(s2.substring(j2), j1, i1, i2);
            }
            if (flag1) {
                if (flag2) {
                    Gui.drawRect(k1, i1 - 1, k1 + 1, i1 + 1 + this.fontRendererInstance.FONT_HEIGHT, -3092272);
                } else {
                    this.fontRendererInstance.drawStringWithShadow("_", k1, i1, i2);
                }
            }
            if (k2 != j2) {
                int l1 = l2 + this.fontRendererInstance.getStringWidth(s2.substring(0, k2));
                this.drawCursorVertical(k1, i1 - 1, l1 - 1, i1 + 1 + this.fontRendererInstance.FONT_HEIGHT);
            }
        }
    }

    private void drawCursorVertical(int startX, int startY, int endX, int endY) {
        if (startX < endX) {
            int i2 = startX;
            startX = endX;
            endX = i2;
        }
        if (startY < endY) {
            int j2 = startY;
            startY = endY;
            endY = j2;
        }
        if (endX > this.xPosition + this.width) {
            endX = this.xPosition + this.width;
        }
        if (startX > this.xPosition + this.width) {
            startX = this.xPosition + this.width;
        }
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        GlStateManager.color(0.0f, 0.0f, 255.0f, 255.0f);
        GlStateManager.disableTexture2D();
        GlStateManager.enableColorLogic();
        GlStateManager.colorLogicOp(GlStateManager.LogicOp.OR_REVERSE);
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION);
        bufferbuilder.pos(startX, endY, 0.0).endVertex();
        bufferbuilder.pos(endX, endY, 0.0).endVertex();
        bufferbuilder.pos(endX, startY, 0.0).endVertex();
        bufferbuilder.pos(startX, startY, 0.0).endVertex();
        tessellator.draw();
        GlStateManager.disableColorLogic();
        GlStateManager.enableTexture2D();
    }

    public void setMaxStringLength(int length) {
        this.maxStringLength = length;
        if (this.text.length() > length) {
            this.text = this.text.substring(0, length);
        }
    }

    public int getMaxStringLength() {
        return this.maxStringLength;
    }

    public int getCursorPosition() {
        return this.cursorPosition;
    }

    public boolean getEnableBackgroundDrawing() {
        return this.enableBackgroundDrawing;
    }

    public void setEnableBackgroundDrawing(boolean enableBackgroundDrawingIn) {
        this.enableBackgroundDrawing = enableBackgroundDrawingIn;
    }

    public void setTextColor(int color) {
        this.enabledColor = color;
    }

    public void setDisabledTextColour(int color) {
        this.disabledColor = color;
    }

    public void setFocused(boolean isFocusedIn) {
        if (isFocusedIn && !this.isFocused) {
            this.cursorCounter = 0;
        }
        this.isFocused = isFocusedIn;
        if (Minecraft.getMinecraft().currentScreen != null) {
            Minecraft.getMinecraft().currentScreen.func_193975_a(isFocusedIn);
        }
    }

    public boolean isFocused() {
        return this.isFocused;
    }

    public void setEnabled(boolean enabled) {
        this.isEnabled = enabled;
    }

    public int getSelectionEnd() {
        return this.selectionEnd;
    }

    public int getWidth() {
        return this.getEnableBackgroundDrawing() ? this.width - 8 : this.width;
    }

    public void setSelectionPos(int position) {
        int i2 = this.text.length();
        if (position > i2) {
            position = i2;
        }
        if (position < 0) {
            position = 0;
        }
        this.selectionEnd = position;
        if (this.fontRendererInstance != null) {
            if (this.lineScrollOffset > i2) {
                this.lineScrollOffset = i2;
            }
            int j2 = this.getWidth();
            String s2 = this.fontRendererInstance.trimStringToWidth(this.text.substring(this.lineScrollOffset), j2);
            int k2 = s2.length() + this.lineScrollOffset;
            if (position == this.lineScrollOffset) {
                this.lineScrollOffset -= this.fontRendererInstance.trimStringToWidth(this.text, j2, true).length();
            }
            if (position > k2) {
                this.lineScrollOffset += position - k2;
            } else if (position <= this.lineScrollOffset) {
                this.lineScrollOffset -= this.lineScrollOffset - position;
            }
            this.lineScrollOffset = MathHelper.clamp(this.lineScrollOffset, 0, i2);
        }
    }

    public void setCanLoseFocus(boolean canLoseFocusIn) {
        this.canLoseFocus = canLoseFocusIn;
    }

    public boolean getVisible() {
        return this.visible;
    }

    public void setVisible(boolean isVisible) {
        this.visible = isVisible;
    }
}

