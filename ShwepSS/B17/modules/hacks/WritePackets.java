package ShwepSS.B17.modules.hacks;

import ShwepSS.B17.ChatUtils;
import ShwepSS.B17.modules.Category;
import ShwepSS.B17.modules.Module;
import ShwepSS.event.EventPacketSend;
import ShwepSS.eventapi.EventManager;
import ShwepSS.eventapi.EventTarget;
import java.util.ArrayList;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketKeepAlive;
import net.minecraft.util.math.BlockPos;

public class WritePackets
extends Module {
    public static ArrayList<Packet> macropacket;
    public static ArrayList<BlockPos> pos;
    public static int packet;

    static {
        pos = new ArrayList();
        macropacket = new ArrayList();
        packet = 0;
    }

    public WritePackets() {
        super("WritePackets", "\u0417\u0430\u043f\u0438\u0441\u044c \u043f\u0430\u043a\u0435\u0442\u043e\u0432 \u0434\u043b\u044f \u043a\u043b\u0438\u0435\u043d\u0442\u0430", 0, Category.MISC, true);
    }

    @Override
    public void onEnable() {
        EventManager.register(this);
        try {
            macropacket.clear();
            pos.clear();
        }
        catch (Exception eg2) {
            eg2.printStackTrace();
        }
    }

    @Override
    public void onDisable() {
        EventManager.unregister(this);
        ChatUtils.success("Packets writen!");
    }

    @EventTarget
    public void onPreUpdate(EventPacketSend e2) {
        if (e2.packet instanceof CPacketKeepAlive) {
            return;
        }
        macropacket.add(e2.packet);
    }
}

