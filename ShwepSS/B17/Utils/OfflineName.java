package ShwepSS.B17.Utils;

import ShwepSS.B17.ChatUtils;
import ShwepSS.B17.HackConfigs;
import ShwepSS.B17.Utils.RandomUtils;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.multiplayer.GuiConnecting;
import net.minecraft.client.multiplayer.ServerAddress;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.util.Session;
import org.lwjgl.input.Keyboard;

public class OfflineName
extends GuiScreen {
    private GuiScreen parentScreen;
    private GuiTextField usernameTextField;

    public OfflineName(GuiScreen guiscreen) {
        this.parentScreen = guiscreen;
    }

    @Override
    public void updateScreen() {
        this.usernameTextField.updateCursorCounter();
    }

    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
    }

    @Override
    protected void actionPerformed(GuiButton guibutton) throws IOException {
        if (!guibutton.enabled) {
            return;
        }
        if (guibutton.id == 1) {
            this.mc.displayGuiScreen(this.parentScreen);
        }
        if (guibutton.id == 4) {
            this.mc.session = new Session(this.usernameTextField.getText(), "", "", "");
            GuiConnecting gc2 = new GuiConnecting(this.parentScreen, this.mc, new ServerData("bot" + RandomUtils.randomString(3), this.mc.getCurrentServerData().serverIP, false));
            ServerAddress serveraddress = ServerAddress.fromString(this.mc.getCurrentServerData().serverIP);
            gc2.connect(serveraddress.getIP(), serveraddress.getPort());
        } else if (guibutton.id == 0 && !this.usernameTextField.getText().isEmpty()) {
            this.mc.session = new Session(this.usernameTextField.getText(), "", "", "");
            this.mc.displayGuiScreen(this.parentScreen);
        }
        if (guibutton.id == 2) {
            this.mc.session = new Session(RandomUtils.randomString(10), "", "", "");
            this.mc.displayGuiScreen(this.parentScreen);
        }
        if (guibutton.id == 3) {
            File file = new File("ExtremeHack/username.txt");
            File folder = new File("ExtremeHack");
            if (!folder.exists()) {
                folder.mkdir();
            }
            if (!file.exists()) {
                file.createNewFile();
                try {
                    Throwable throwable = null;
                    Object var5_7 = null;
                    try (FileWriter writer = new FileWriter(file.getAbsolutePath(), false);){
                        String text = "savedNick - .minecraft/ExtremeHack/username.txt";
                        writer.write(text);
                        writer.append('\n');
                        writer.flush();
                    }
                    catch (Throwable throwable2) {
                        if (throwable == null) {
                            throwable = throwable2;
                        } else if (throwable != throwable2) {
                            throwable.addSuppressed(throwable2);
                        }
                        throw throwable;
                    }
                }
                catch (IOException ex2) {
                    System.out.println(ex2.getMessage());
                }
            }
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            String readString = "";
            while ((readString = bufferedReader.readLine()) != null) {
                this.mc.session = new Session(readString, "", "", "");
                this.mc.displayGuiScreen(this.parentScreen);
            }
        }
    }

    @Override
    protected void keyTyped(char c2, int i2) throws IOException {
        this.usernameTextField.textboxKeyTyped(c2, i2);
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
    }

    @Override
    public void initGui() {
        Keyboard.enableRepeatEvents(true);
        this.buttonList.clear();
        if (this.mc.world != null) {
            this.buttonList.add(new GuiButton(4, width / 2 - 100, height / 4 - 20 - 12, "Connect bot!"));
        }
        this.buttonList.add(new GuiButton(0, width / 2 - 100, height / 4 + 97 + 12, "Done"));
        this.buttonList.add(new GuiButton(1, width / 2 - 100, height / 4 + 120 + 12, "Cancel"));
        this.buttonList.add(new GuiButton(2, width / 2 - 100, height / 4 + 143 + 12, "Random"));
        this.buttonList.add(new GuiButton(3, width / 2 - 100, height / 4 + 166 + 12, String.valueOf(ChatUtils.cyan) + ChatUtils.l + "Restore from text document"));
        this.usernameTextField = new GuiTextField(2, this.fontRendererObj, width / 2 - 100, 146, 200, 20);
        this.usernameTextField.setText(this.mc.session.getUsername());
    }

    @Override
    public void drawScreen(int i2, int j2, float f2) {
        if (Keyboard.isKeyDown(1)) {
            this.mc.displayGuiScreen(this.parentScreen);
        }
        OfflineName.drawDefaultBackground();
        OfflineName.drawGradientRect(0, height / 2 + 40, width, height, 111111111, HackConfigs.ThemeColor);
        this.drawCenteredString(this.fontRendererObj, "Change nick", width / 2, height / 4 - 60 + 20, 0xFFFFFF);
        this.drawString(this.fontRendererObj, "Nick", width / 2 - 100, 134, 0xA0A0A0);
        this.usernameTextField.drawTextBox();
        super.drawScreen(i2, j2, f2);
    }
}

