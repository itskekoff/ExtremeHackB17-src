package ShwepSS.B17.modules.hacks;

import ShwepSS.B17.modules.Category;
import ShwepSS.B17.modules.Module;
import net.minecraft.client.Minecraft;

public class AutoPKM
extends Module {
    public AutoPKM() {
        super("AutoPKM", "\u0430\u0432\u0442\u043e \u043f\u0440\u0430\u0432\u0430\u044f \u043a\u043d\u043e\u043f\u043a\u0430 \u043c\u044b\u0448\u0438 \u0451\u043f\u0442\u0430", 0, Category.MISC, true);
    }

    @Override
    public void onEnable() {
    }

    @Override
    public void onTick() {
        Minecraft.getMinecraft().rightClickMouse();
    }

    @Override
    public void onDisable() {
    }
}

