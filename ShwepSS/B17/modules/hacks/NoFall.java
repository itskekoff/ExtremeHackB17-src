package ShwepSS.B17.modules.hacks;

import ShwepSS.B17.modules.Category;
import ShwepSS.B17.modules.Module;
import net.minecraft.client.Minecraft;
import net.minecraft.network.play.client.CPacketPlayer;

public class NoFall
extends Module {
    public NoFall() {
        super("NoFall", "\u043f\u0440\u0435\u0434\u043e\u0442\u0432\u0440\u0430\u0449\u0430\u0435\u0442 \u0443\u0440\u043e\u043d \u043e\u0442 \u043f\u0430\u0434\u0435\u043d\u0438\u044f", 0, Category.Player, true);
    }

    @Override
    public void onEnable() {
    }

    @Override
    public void onTick() {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.player.fallDistance >= 2.0f) {
            for (int i2 = 0; i2 < 10; ++i2) {
                mc.player.connection.sendPacket(new CPacketPlayer(true));
            }
        }
    }

    @Override
    public void onDisable() {
    }
}

