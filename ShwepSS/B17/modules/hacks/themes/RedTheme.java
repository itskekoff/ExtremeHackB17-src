package ShwepSS.B17.modules.hacks.themes;

import ShwepSS.B17.HackConfigs;
import ShwepSS.B17.modules.Category;
import ShwepSS.B17.modules.Module;
import java.awt.Color;

public class RedTheme
extends Module {
    public static Module instance;

    public RedTheme() {
        super("RedTheme", "\u043a\u0440\u0430\u0441\u043d\u0430\u044f \u0442\u0435\u043c\u0430", 0, Category.Theme, false);
        instance = this;
    }

    @Override
    public void onEnable() {
        HackConfigs.ThemeColor = new Color(100, 0, 0).getRGB() * 10;
        HackConfigs.ThemeColorGui = new Color(0, 1, 4).getRGB();
        this.toggle();
    }

    @Override
    public void onDisable() {
    }
}

