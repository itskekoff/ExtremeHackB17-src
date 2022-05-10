package ShwepSS.B17.modules.hacks;

import ShwepSS.B17.modules.Category;
import ShwepSS.B17.modules.Module;
import net.minecraft.client.Minecraft;

public class MurderBug
extends Module {
    public MurderBug() {
        super("MurderBug", "\u0443\u043c\u0440\u0438 \u0438 \u043f\u0438\u0437\u0434\u0438 \u0432\u0441\u0435\u0445 \u043d\u0430 MurderMystery", 0, Category.MISC, true);
    }

    @Override
    public void onEnable() {
    }

    @Override
    public void onTick() {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.player.capabilities.allowFlying) {
            mc.player.sendChatMessage("/mm leave");
            this.toggle();
        }
        if (mc.player.isInvisible()) {
            mc.player.sendChatMessage("/mm leave");
            this.toggle();
        }
    }

    @Override
    public void onDisable() {
    }
}

