package ShwepSS.B17.modules.hacks;

import ShwepSS.B17.ChatUtils;
import ShwepSS.B17.Utils.TimerUtils;
import ShwepSS.B17.modules.Category;
import ShwepSS.B17.modules.Module;
import ShwepSS.event.EventPacketRecieve;
import ShwepSS.eventapi.EventManager;
import ShwepSS.eventapi.EventTarget;
import net.minecraft.client.Minecraft;

public class Laggometer
extends Module {
    public static TimerUtils time;

    public Laggometer() {
        super("Laggometer", "\u0423\u0432\u0435\u0434\u043e\u043c\u043b\u044f\u0435\u0442 \u043a\u043e\u0433\u0434\u0430 \u0441\u0435\u0440\u0432\u0430\u043a \u0437\u0430\u043b\u0430\u0433\u0430\u043b", 0, Category.MISC, true);
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

    @EventTarget
    public void onLag(EventPacketRecieve ev2) {
    }

    @Override
    public void onRender() {
        Minecraft mc = Minecraft.getMinecraft();
        if (time.hasReached(1000L)) {
            mc.getConnection().gameController.ingameGUI.displayTitle(String.valueOf(ChatUtils.pink) + "\u0421\u0435\u0440\u0432\u0430\u043a \u043d\u0435 \u043e\u0442\u0432\u0435\u0447\u0430\u0435\u0442!" + ChatUtils.white + " (" + ChatUtils.green + (time.getCurrentMS() - time.getPreviousTime()) + ChatUtils.white + ")", "", 1, 3, 1);
        }
    }
}

