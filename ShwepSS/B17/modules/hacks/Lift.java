package ShwepSS.B17.modules.hacks;

import ShwepSS.B17.modules.Category;
import ShwepSS.B17.modules.Module;
import net.minecraft.client.Minecraft;

public class Lift
extends Module {
    public Lift() {
        super("Lift", "\u041f\u043e\u0434\u043d\u0438\u043c\u0430\u0435\u0442 \u0432\u0430\u0441 \u0432\u0432\u0435\u0440\u0445", 33, Category.Movement, true);
    }

    @Override
    public void onEnable() {
    }

    @Override
    public void onTick() {
        Minecraft.getMinecraft().player.jump();
    }

    @Override
    public void onDisable() {
    }
}

