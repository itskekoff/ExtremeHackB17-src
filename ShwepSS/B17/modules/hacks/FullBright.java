package ShwepSS.B17.modules.hacks;

import ShwepSS.B17.modules.Category;
import ShwepSS.B17.modules.Module;
import net.minecraft.client.Minecraft;

public class FullBright
extends Module {
    float oldbright;

    public FullBright() {
        super("FullBright", "\u0414\u0435\u043b\u0430\u0435\u0442 \u0432\u0441\u0451 \u044f\u0440\u0447\u0435", 0, Category.Visuals, true);
    }

    @Override
    public void onEnable() {
        this.oldbright = Minecraft.getMinecraft().gameSettings.gammaSetting;
        Minecraft.getMinecraft().gameSettings.gammaSetting = 17000.0f;
    }

    @Override
    public void onTick() {
    }

    @Override
    public void onDisable() {
        Minecraft.getMinecraft().gameSettings.gammaSetting = this.oldbright;
    }
}

