package ShwepSS.B17.modules.hacks;

import ShwepSS.B17.ExtremeHack;
import ShwepSS.B17.Utils.TimeHelper;
import ShwepSS.B17.cg.settings.Setting;
import ShwepSS.B17.modules.Category;
import ShwepSS.B17.modules.Module;
import ShwepSS.event.EventOpenScreen;
import ShwepSS.eventapi.EventManager;
import ShwepSS.eventapi.EventTarget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiDisconnected;
import net.minecraft.client.multiplayer.GuiConnecting;
import net.minecraft.client.multiplayer.ServerData;

public class AutoReconnect
extends Module {
    public static Module instance;
    private TimeHelper time;
    private static ServerData serverData;
    Setting delay = new Setting("Delay", this, 3000.0, 1000.0, 10000.0, false);

    public AutoReconnect() {
        super("AutoReconnect", "\u043f\u0435\u0440\u0435\u043f\u043e\u0434\u043a\u043b\u044e\u0447\u0430\u0435\u0442\u0441\u044f \u043a \u0441\u0435\u0440\u0432\u0430\u043a\u0443 \u0435\u0441\u043b\u0438 \u043a\u0438\u043a\u0430\u043d\u0443\u043b\u043e \u0435\u0431\u0430\u0442\u044c", 0, Category.Player, false);
        instance = this;
        ExtremeHack.instance.getSetmgr().rSetting(this.delay);
    }

    @Override
    public void onEnable() {
        EventManager.register(this);
    }

    @Override
    public void onDisable() {
        EventManager.unregister(this);
    }

    @Override
    public void onTick() {
        this.updateLastConnectedServer();
    }

    @EventTarget
    public void onGAGA(EventOpenScreen ev2) {
        if (ev2.getScreen() instanceof GuiDisconnected) {
            this.updateLastConnectedServer();
            GuiDisconnected disconnected = (GuiDisconnected)ev2.getScreen();
            ev2.setScreen(new GuiDisconnectedHook(disconnected));
        }
    }

    public void updateLastConnectedServer() {
        Minecraft mc = Minecraft.getMinecraft();
        ServerData data = mc.getCurrentServerData();
        if (data != null) {
            serverData = data;
        }
    }

    private class GuiDisconnectedHook
    extends GuiDisconnected {
        private final TimeHelper timer;

        public GuiDisconnectedHook(GuiDisconnected disconnected) {
            super(disconnected.parentScreen, disconnected.reason, disconnected.message);
            this.timer = new TimeHelper();
            this.timer.reset();
        }

        @Override
        public void updateScreen() {
            if (this.timer.hasReached(AutoReconnect.this.delay.getValFloat())) {
                this.mc.displayGuiScreen(new GuiConnecting(this.parentScreen, this.mc, serverData == null ? this.mc.currentServerData : serverData));
            }
        }

        @Override
        public void drawScreen(int mouseX, int mouseY, float partialTicks) {
            super.drawScreen(mouseX, mouseY, partialTicks);
            Minecraft mc = Minecraft.getMinecraft();
            String s2 = "Reconnecting in " + (double)this.timer.getLastMS() / 1000.0;
            mc.fontRendererObj.drawString(s2, width / 2 - mc.fontRendererObj.getStringWidth(s2) / 2, height - 16, 0xFFFFFF, true);
        }
    }
}

