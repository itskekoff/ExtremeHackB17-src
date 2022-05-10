package ShwepSS.B17.modules.hacks;

import ShwepSS.B17.ChatUtils;
import ShwepSS.B17.modules.Category;
import ShwepSS.B17.modules.Module;
import ShwepSS.B17.modules.hacks.WritePackets;
import ShwepSS.eventapi.EventManager;
import net.minecraft.client.Minecraft;

public class RepeatPackets
extends Module {
    public RepeatPackets() {
        super("RepeatPackets", "\u041f\u043e\u0432\u0442\u043e\u0440\u0435\u043d\u0438\u0435 \u043f\u0430\u043a\u0435\u0442\u043e\u0432 \u0434\u043b\u044f \u043a\u043b\u0438\u0435\u043d\u0442\u0430", 0, Category.MISC, true);
    }

    @Override
    public void onEnable() {
        EventManager.register(this);
        ChatUtils.success("Start repeater!");
    }

    @Override
    public void onDisable() {
        EventManager.unregister(this);
        WritePackets.packet = 0;
    }

    @Override
    public void onTick() {
        Minecraft mc = Minecraft.getMinecraft();
        try {
            if (WritePackets.packet != WritePackets.macropacket.size()) {
                mc.player.connection.sendPacket(WritePackets.macropacket.get(WritePackets.packet));
            }
            ++WritePackets.packet;
        }
        catch (Exception exception) {
            // empty catch block
        }
    }
}

