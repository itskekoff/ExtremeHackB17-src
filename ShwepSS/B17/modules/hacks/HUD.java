package ShwepSS.B17.modules.hacks;

import ShwepSS.B17.ChatUtils;
import ShwepSS.B17.ExtremeHack;
import ShwepSS.B17.Utils.LastPacket;
import ShwepSS.B17.Utils.TimerUtils;
import ShwepSS.B17.Utils.WaitTimer;
import ShwepSS.B17.cg.font.FontUtil;
import ShwepSS.B17.modules.Category;
import ShwepSS.B17.modules.Module;
import ShwepSS.event.EventPacketRecieve;
import ShwepSS.eventapi.EventManager;
import ShwepSS.eventapi.EventTarget;
import java.util.ArrayList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.multiplayer.GuiConnecting;
import net.minecraft.network.play.server.SPacketTimeUpdate;
import org.lwjgl.opengl.GL11;

public class HUD
extends Module {
    public static TimerUtils time;
    private static WaitTimer tpsTimer;
    LastPacket lastPacket = new LastPacket();
    public static double lastTps;
    private static ArrayList<Long> times;

    static {
        tpsTimer = new WaitTimer();
        lastTps = 20.0;
        times = new ArrayList();
    }

    public HUD() {
        super("HUD", "\u0438\u043d\u0444\u043e\u0440\u043c\u0430\u0446\u0438\u044f \u043d\u0430 \u044d\u043a\u0440\u0430\u043d\u0435 \u0438\u0433\u0440\u044b", 0, Category.Visuals, false);
        time = new TimerUtils();
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
    public void onRender() {
        Minecraft mc = Minecraft.getMinecraft();
        this.lastPacket.getLastMs();
        this.lastPacket.getReceived();
        Gui.drawRect(0.0, 0.0, 160.0, 40.0, 575083552);
        GL11.glPushMatrix();
        FontUtil.elegant_17.drawStringWithShadow(String.valueOf(ChatUtils.cyan) + "ExtremeHack by ShwepSS .ver B17", 3.0f, 5.0f, -1);
        if (ExtremeHack.mc.isSingleplayer()) {
            FontUtil.elegant_17.drawStringWithShadow(String.valueOf(ChatUtils.gray) + "Server: " + ChatUtils.dred + "NoServer", 3.0f, 15.0f, -1);
        } else {
            FontUtil.elegant_17.drawStringWithShadow(String.valueOf(ChatUtils.gray) + "Server: " + ChatUtils.dred + GuiConnecting.ip + ":" + GuiConnecting.port, 3.0f, 15.0f, -1);
        }
        GL11.glPopMatrix();
        String result = String.format("%.2f", lastTps);
        FontUtil.elegant_17.drawStringWithShadow(String.valueOf(ChatUtils.cyan) + "FPS: " + ChatUtils.green + Minecraft.getDebugFPS() + ChatUtils.cyan + " TPS: " + ChatUtils.red + result, 3.0f, 25.0f, -1);
    }

    @EventTarget
    public void onPacketRecieved(EventPacketRecieve modPacket) {
        this.lastPacket.setSentValue(modPacket.getPacket());
        Minecraft mc = Minecraft.getMinecraft();
        if (modPacket.getPacket() instanceof SPacketTimeUpdate) {
            times.add(Math.max(1000L, tpsTimer.getTime()));
            long timesAdded = 0L;
            if (times.size() > 5) {
                times.remove(0);
            }
            for (long l2 : times) {
                timesAdded += l2;
            }
            long roundedTps = timesAdded / (long)times.size();
            lastTps = 20.0 / (double)roundedTps * 1000.0;
            tpsTimer.reset();
        }
    }
}

