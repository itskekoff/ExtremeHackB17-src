package ShwepSS.B17.modules.hacks.themes;

import ShwepSS.B17.HackConfigs;
import ShwepSS.B17.Utils.ColorUtil;
import ShwepSS.B17.modules.Category;
import ShwepSS.B17.modules.Module;
import ShwepSS.B17.modules.hacks.themes.ExtremeTheme;
import java.awt.Color;

public class Rainbow
extends Module {
    public static Module instance;

    public Rainbow() {
        super("RainbowTheme", "\u0420\u0430\u0434\u0443\u0436\u043d\u0430\u044f \u0442\u0435\u043c\u0430", 0, Category.Theme, false);
        instance = this;
    }

    @Override
    public void onEnable() {
    }

    @Override
    public void onDisable() {
        ExtremeTheme.instance.toggle();
    }

    @Override
    public void onTick() {
        HackConfigs.ThemeColor = ColorUtil.rainbow(20000000L, 1.0f).getRGB();
        HackConfigs.ThemeColorGui = new Color(0, 10, 10).getRGB();
    }
}

