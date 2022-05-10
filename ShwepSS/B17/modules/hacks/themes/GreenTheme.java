package ShwepSS.B17.modules.hacks.themes;

import ShwepSS.B17.HackConfigs;
import ShwepSS.B17.modules.Category;
import ShwepSS.B17.modules.Module;
import java.awt.Color;

public class GreenTheme
extends Module {
    public static Module instance;

    public GreenTheme() {
        super("GreenTheme", "\u0437\u0435\u043b\u0451\u043d\u0430\u044f \u0442\u0435\u043c\u0430", -1, Category.Theme, false);
        instance = this;
    }

    @Override
    public void onEnable() {
        HackConfigs.ThemeColor = new Color(0, 100, 0).getRGB() * 10;
        HackConfigs.ThemeColorGui = new Color(0, 20, 4).getRGB();
        this.toggle();
    }

    @Override
    public void onDisable() {
    }
}

