package ShwepSS.B17.modules.hacks;

import ShwepSS.B17.modules.Category;
import ShwepSS.B17.modules.Module;
import net.minecraft.client.Minecraft;

public class AutoSprint
extends Module {
    public AutoSprint() {
        super("AutoSprint", "\u0410\u0432\u0442\u043e\u043c\u0430\u0442\u0438\u0447\u0435\u0441\u043a\u0438 \u043f\u0435\u0440\u0435\u0445\u043e\u0434\u0438\u0442 \u043d\u0430 \u0441\u043f\u0440\u0438\u043d\u0442", 0, Category.Movement, true);
    }

    @Override
    public void onEnable() {
    }

    @Override
    public void onTick() {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.gameSettings.keyBindForward.isKeyDown() || mc.gameSettings.keyBindLeft.isKeyDown() || mc.gameSettings.keyBindRight.isKeyDown() || mc.gameSettings.keyBindBack.isKeyDown()) {
            mc.player.setSprinting(true);
        }
    }

    @Override
    public void onDisable() {
    }
}

