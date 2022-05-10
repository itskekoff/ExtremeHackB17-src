package ShwepSS.B17.modules.hacks;

import ShwepSS.B17.modules.Category;
import ShwepSS.B17.modules.Module;
import net.minecraft.client.Minecraft;

public class RandomSwitch
extends Module {
    public RandomSwitch() {
        super("RandomSwitch", "\u0430\u0432\u0442\u043e\u0441\u043a\u0440\u043e\u043b\u043b", 0, Category.Player, true);
    }

    @Override
    public void onTick() {
        try {
            Minecraft.getMinecraft().player.inventory.changeCurrentItem(-1);
        }
        catch (Exception e2) {
            e2.printStackTrace();
        }
    }
}

