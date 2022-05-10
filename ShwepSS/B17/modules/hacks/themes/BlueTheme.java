package ShwepSS.B17.modules.hacks.themes;

import ShwepSS.B17.HackConfigs;
import ShwepSS.B17.modules.Category;
import ShwepSS.B17.modules.Module;
import java.awt.Color;

public class BlueTheme
extends Module {
    public static Module instance;

    public BlueTheme() {
        super("BlueTheme", "\u0441\u0438\u043d\u044f\u044f \u0442\u0435\u043c\u0430", 0, Category.Theme, false);
        instance = this;
    }

    @Override
    public void onEnable() {
        HackConfigs.ThemeColor = new Color(0, 0, 100).getRGB() * 10;
        HackConfigs.ThemeColorGui = new Color(0, 20, 60).getRGB();
        this.toggle();
    }

    @Override
    public void onDisable() {
    }
}

