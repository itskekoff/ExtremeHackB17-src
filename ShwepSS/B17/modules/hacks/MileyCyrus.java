package ShwepSS.B17.modules.hacks;

import ShwepSS.B17.modules.Category;
import ShwepSS.B17.modules.Module;
import ShwepSS.eventapi.EventManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;

public class MileyCyrus
extends Module {
    static int delay;
    private int Delaytimer;

    public MileyCyrus() {
        super("MileyCyrus", "\u0448\u0438\u0444\u0442\u0438\u043b\u043a\u0430", 0, Category.Player, true);
    }

    @Override
    public void onEnable() {
        EventManager.register(this);
    }

    @Override
    public void onDisable() {
        EventManager.unregister(this);
    }

    @Override
    public void onTick() {
        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayerSP p2 = mc.player;
        int timer = 1;
        if (++timer >= 1) {
            mc.gameSettings.keyBindSneak.pressed = !mc.gameSettings.keyBindSneak.pressed;
            timer = 1;
        }
    }
}

