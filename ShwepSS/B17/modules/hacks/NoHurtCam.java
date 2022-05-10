package ShwepSS.B17.modules.hacks;

import ShwepSS.B17.modules.Category;
import ShwepSS.B17.modules.Module;
import net.minecraft.client.Minecraft;

public class NoHurtCam
extends Module {
    public NoHurtCam() {
        super("NoHurtCam", "\u043a\u0430\u043c\u0435\u0440\u0430 \u043d\u0435 \u0434\u0451\u0440\u0433\u0430\u0435\u0442\u0441\u044f \u043f\u0440\u0438 \u0443\u0440\u043e\u043d\u0435", 19, Category.Combat, true);
    }

    @Override
    public void onEnable() {
    }

    @Override
    public void onTick() {
        Minecraft.getMinecraft().player.hurtResistantTime = 0;
        Minecraft.getMinecraft().player.hurtTime = 0;
        Minecraft.getMinecraft().player.maxHurtResistantTime = 0;
        Minecraft.getMinecraft().player.maxHurtTime = 0;
    }

    @Override
    public void onDisable() {
    }
}

