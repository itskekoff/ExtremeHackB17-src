package ShwepSS.B17.modules.hacks;

import ShwepSS.B17.modules.Category;
import ShwepSS.B17.modules.Module;

public class AutoUnban
extends Module {
    public static boolean autoUnban;

    public AutoUnban() {
        super("AutoUnban", "Auto unban", 0, Category.Player, true);
    }

    @Override
    public void onEnable() {
        autoUnban = true;
    }

    @Override
    public void onDisable() {
        autoUnban = false;
    }
}

