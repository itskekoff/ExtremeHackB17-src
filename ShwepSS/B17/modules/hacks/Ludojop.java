package ShwepSS.B17.modules.hacks;

import ShwepSS.B17.modules.Category;
import ShwepSS.B17.modules.Module;
import net.minecraft.client.Minecraft;

public class Ludojop
extends Module {
    public Ludojop() {
        super("\u041b\u0443\u0434\u043e\u0436\u043e\u043f", "\u041b\u0443\u043d\u043e\u0445\u043e\u0434", 0, Category.MISC, true);
    }

    @Override
    public void onEnable() {
    }

    @Override
    public void onTick() {
        Minecraft.getMinecraft().player.motionY += (double)0.06f;
    }

    @Override
    public void onDisable() {
    }
}

