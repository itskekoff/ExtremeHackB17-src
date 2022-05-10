package ShwepSS.B17.modules.hacks;

import ShwepSS.B17.ChatUtils;
import ShwepSS.B17.modules.Category;
import ShwepSS.B17.modules.Module;

public class PlugFinder
extends Module {
    public PlugFinder() {
        super("===PlugFinder===", "\u041f\u043e\u0438\u0441\u043a \u043f\u043b\u0430\u0433\u0438\u043d\u043e\u0432 \u043d\u0430 \u0441\u0435\u0440\u0432\u0435\u0440\u0435", 0, Category.MISC, true);
    }

    @Override
    public void onEnable() {
        ChatUtils.emessage("\u0412\u0432\u0435\u0434\u0438\u0442\u0435 \u0432 \u0447\u0430\u0442 '/' \u0438 \u043d\u0430\u0436\u043c\u0438\u0442\u0435 \u0442\u0430\u0431!");
    }
}

