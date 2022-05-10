package ShwepSS.B17.modules.hacks;

import ShwepSS.B17.modules.Category;
import ShwepSS.B17.modules.Module;
import net.minecraft.client.Minecraft;

public class PlayerListPin
extends Module {
    public PlayerListPin() {
        super("PinTAB", "\u0437\u0430\u043a\u0440\u0435\u043f\u043b\u0435\u043d\u0438\u0435 \u0442\u0430\u0431\u0430 (PlayerListPin)", -1, Category.MISC, true);
    }

    @Override
    public void onEnable() {
    }

    @Override
    public void onTick() {
        Minecraft.getMinecraft().gameSettings.keyBindPlayerList.pressed = true;
    }

    @Override
    public void onDisable() {
    }
}

