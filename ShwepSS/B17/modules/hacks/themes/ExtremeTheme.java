package ShwepSS.B17.modules.hacks.themes;

import ShwepSS.B17.HackConfigs;
import ShwepSS.B17.cg.ColorUtils;
import ShwepSS.B17.modules.Category;
import ShwepSS.B17.modules.Module;
import java.awt.Color;

public class ExtremeTheme
extends Module {
    public static Module instance;

    public ExtremeTheme() {
        super("ExtremeTheme", "\u0422\u0435\u043c\u0430 \u044d\u043a\u0441\u0442\u0440\u0438\u043c\u0430", 0, Category.Theme, false);
        instance = this;
    }

    @Override
    public void onEnable() {
        HackConfigs.ThemeColor = ColorUtils.astolfo(3, 20.0f) * 10;
        HackConfigs.ThemeColorGui = new Color(0, 45, 55).getRGB();
        this.toggle();
    }

    @Override
    public void onDisable() {
    }
}

