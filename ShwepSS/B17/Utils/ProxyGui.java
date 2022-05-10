package ShwepSS.B17.Utils;

import ShwepSS.B17.ChatUtils;
import ShwepSS.B17.ExtremeHack;
import ShwepSS.B17.HackConfigs;
import ShwepSS.B17.Utils.EnumProxyType;
import ShwepSS.B17.Utils.GuiRenderUtils;
import ShwepSS.B17.Utils.RandomUtils;
import ShwepSS.B17.modules.hacks.bot.ProxyManager;
import java.io.IOException;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import org.lwjgl.input.Keyboard;

public class ProxyGui
extends GuiScreen {
    private GuiScreen parentScreen;
    private GuiTextField ipPortTextField;
    public static String strIpPort = "";

    public ProxyGui(GuiScreen guiscreen) {
        this.parentScreen = guiscreen;
    }

    @Override
    public void updateScreen() {
        this.ipPortTextField.updateCursorCounter();
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
        if (guibutton.id == 0) {
            ExtremeHack.instance.proxyType = EnumProxyType.SOCKS5;
            ExtremeHack.proxyIP = this.ipPortTextField.getText();
            this.mc.displayGuiScreen(new GuiMultiplayer(this.parentScreen));
        } else if (guibutton.id == 1) {
            ExtremeHack.instance.proxyType = EnumProxyType.SOCKS4;
            ExtremeHack.proxyIP = this.ipPortTextField.getText();
            this.mc.displayGuiScreen(new GuiMultiplayer(this.parentScreen));
        } else if (guibutton.id == 2) {
            this.mc.displayGuiScreen(new GuiMultiplayer(this.parentScreen));
        } else if (guibutton.id == 3) {
            ExtremeHack.instance.proxyType = EnumProxyType.SOCKS5;
            ExtremeHack.proxyIP = ProxyManager.stringProxy.get(RandomUtils.nextInt(1, ProxyManager.stringProxy.size()));
            System.out.println("[ExtremeHack] Setted proxy socks5 " + ExtremeHack.proxyIP);
            this.mc.displayGuiScreen(new GuiMultiplayer(this.parentScreen));
        }
    }

    @Override
    protected void keyTyped(char c2, int i2) {
        this.ipPortTextField.textboxKeyTyped(c2, i2);
        if (c2 == '\t' && this.ipPortTextField.isFocused()) {
            this.ipPortTextField.setFocused(false);
        }
        if (c2 == '\r') {
            this.actionPerformed((GuiButton)this.buttonList.get(0));
        }
    }

    @Override
    protected void mouseClicked(int i2, int j2, int k2) throws IOException {
        super.mouseClicked(i2, j2, k2);
        this.ipPortTextField.mouseClicked(i2, j2, k2);
    }

    @Override
    public void initGui() {
        Keyboard.enableRepeatEvents(true);
        this.buttonList.clear();
        this.buttonList.add(new GuiButton(3, width / 2 - 100, height / 4 + 72, "Random Socks5"));
        this.buttonList.add(new GuiButton(0, width / 2 - 100, height / 4 + 74 + 20, "Connect Socks5"));
        this.buttonList.add(new GuiButton(1, width / 2 - 100, height / 4 + 74 + 42, "Connect Socks4"));
        this.buttonList.add(new GuiButton(2, width / 2 - 100, height / 4 + 85 + 62, "Cancel"));
        this.ipPortTextField = new GuiTextField(2, this.fontRendererObj, width / 2 - 100, 116, 200, 20);
    }

    @Override
    public void drawScreen(int i2, int j2, float f2) {
        ProxyGui.drawDefaultBackground();
        GuiRenderUtils.drawBorderedRect2(width / 2 - 150, height / 2 + 60, width / 2 + 150, height / 2 - 170, 2.0f, HackConfigs.ThemeColor, -1747786192);
        this.drawCenteredString(this.fontRendererObj, String.valueOf(ChatUtils.pink) + ChatUtils.l + "\u041f\u043e\u0434\u043a\u043b\u044e\u0447\u0435\u043d\u0438\u0435 \u0447\u0435\u0440\u0435\u0437 \u043f\u0440\u043e\u043a\u0441\u0438", width / 2, height / 4 - 60 + 20, 0xFFFFFF);
        this.drawString(this.fontRendererObj, String.valueOf(ChatUtils.cyan) + ChatUtils.l + "\u0432\u0432\u0435\u0434\u0438\u0442\u0435 ip:port", width / 2 - 100, 104, 0xA0A0A0);
        this.ipPortTextField.drawTextBox();
        super.drawScreen(i2, j2, f2);
    }
}

