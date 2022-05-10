package ShwepSS.B17.modules.hacks;

import ShwepSS.B17.modules.Category;
import ShwepSS.B17.modules.Module;
import net.minecraft.client.Minecraft;

public class Spider
extends Module {
    public Spider() {
        super("Spider", "\u043a\u0430\u043a\u043e\u0439 \u0436\u0435 \u0447\u0438\u0442 \u0431\u0435\u0437 \u043f\u0430\u0443\u043a\u0430?", 0, Category.Movement, true);
    }

    @Override
    public void onEnable() {
    }

    @Override
    public void onTick() {
        if (Minecraft.getMinecraft().player.isCollidedHorizontally) {
            Minecraft.getMinecraft().player.jump();
        }
    }

    @Override
    public void onDisable() {
    }
}

