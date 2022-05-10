package ShwepSS.B17.modules.hacks.themes;

import ShwepSS.B17.modules.Category;
import ShwepSS.B17.modules.Module;

public class QuadTheme
extends Module {
    public static Module instance;

    public QuadTheme() {
        super("QuadTheme", "\u043a\u0432\u0430\u0434\u0440\u0430\u0442\u043d\u0430\u044f \u0442\u0435\u043c\u0430", -1, Category.Theme, false);
        instance = this;
    }

    @Override
    public void onEnable() {
    }

    @Override
    public void onDisable() {
    }
}

