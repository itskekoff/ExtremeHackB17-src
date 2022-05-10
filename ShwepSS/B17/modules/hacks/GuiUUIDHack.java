package ShwepSS.B17.modules.hacks;

import ShwepSS.B17.modules.HackPack;
import com.google.common.base.Charsets;
import java.io.IOException;
import java.util.UUID;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import org.lwjgl.input.Keyboard;

public class GuiUUIDHack
extends GuiScreen {
    private GuiScreen parentScreen;
    private GuiTextField usernameTextField;
    private GuiTextField usernameTextField2;
    private String error;

    public GuiUUIDHack(GuiScreen parentScreen2) {
        this.parentScreen = parentScreen2;
    }

    @Override
    public void updateScreen() {
        this.usernameTextField.updateCursorCounter();
        this.usernameTextField2.updateCursorCounter();
    }

    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
    }

    @Override
    protected void actionPerformed(GuiButton guibutton) {
        if (!guibutton.enabled) {
            return;
        }
        if (guibutton.id == 1) {
            this.mc.displayGuiScreen(this.parentScreen);
        } else if (guibutton.id == 0) {
            HackPack.setFakeUUID(UUID.nameUUIDFromBytes(("OfflinePlayer:" + this.usernameTextField2.getText()).getBytes(Charsets.UTF_8)).toString());
            HackPack.setFakeIP(this.usernameTextField.getText());
        }
        this.mc.displayGuiScreen(this.parentScreen);
    }

    @Override
    protected void keyTyped(char c2, int i2) {
        this.usernameTextField.textboxKeyTyped(c2, i2);
        this.usernameTextField2.textboxKeyTyped(c2, i2);
        if (c2 == '\t' && this.usernameTextField.isFocused()) {
            this.usernameTextField.setFocused(false);
        }
        if (c2 == '\r') {
            this.actionPerformed((GuiButton)this.buttonList.get(0));
        }
    }

    @Override
    protected void mouseClicked(int i2, int j2, int k2) throws IOException {
        super.mouseClicked(i2, j2, k2);
        this.usernameTextField.mouseClicked(i2, j2, k2);
        this.usernameTextField2.mouseClicked(i2, j2, k2);
    }

    @Override
    public void initGui() {
        Keyboard.enableRepeatEvents(true);
        this.buttonList.clear();
        this.buttonList.add(new GuiButton(0, width / 2 - 100, height / 4 + 96 + 12, "Done"));
        this.buttonList.add(new GuiButton(1, width / 2 - 100, height / 4 + 120 + 12, "Cancel"));
        this.usernameTextField = new GuiTextField(2, this.fontRendererObj, width / 2 - 100, 116, 200, 20);
        this.usernameTextField2 = new GuiTextField(3, this.fontRendererObj, width / 2 - 100, 96, 200, 20);
        this.usernameTextField.setMaxStringLength(500);
        this.usernameTextField2.setMaxStringLength(500);
    }

    @Override
    public void drawScreen(int i2, int j2, float f2) {
        GuiUUIDHack.drawDefaultBackground();
        this.usernameTextField.drawTextBox();
        this.drawCenteredString(this.fontRendererObj, "\u2191 Nick, \u2193 IP.", width / 2, height / 4 - 60 + 20, 0xFFFFFF);
        this.usernameTextField2.drawTextBox();
        super.drawScreen(i2, j2, f2);
        if (Keyboard.isKeyDown(1)) {
            this.mc.displayGuiScreen(this.parentScreen);
        }
    }
}

