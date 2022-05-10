package ShwepSS.B17.modules.hacks;

import ShwepSS.B17.modules.Category;
import ShwepSS.B17.modules.Module;
import ShwepSS.event.EventPacketRecieve;
import ShwepSS.eventapi.EventManager;
import ShwepSS.eventapi.EventTarget;
import net.minecraft.client.Minecraft;

public class Slezhka
extends Module {
    public Slezhka() {
        super("\u0421\u043b\u0435\u0436\u043a\u0430 ", "\u0421\u043b\u0435\u0434\u0438\u0442\u044c \u0437\u0430 \u043f\u0440\u043e\u0438\u0441\u0445\u043e\u0434\u044f\u0449\u0438\u043c \u043d\u0430 \u0441\u0435\u0440\u0432\u0435\u0440\u0435", 0, Category.MISC, false);
    }

    @Override
    public void onEnable() {
        EventManager.register(this);
    }

    @Override
    public void onDisable() {
        EventManager.unregister(this);
    }

    @EventTarget
    public void onkek(EventPacketRecieve ev2) {
        Minecraft mc = Minecraft.getMinecraft();
    }
}

