package ShwepSS.B17.modules.hacks;

import ShwepSS.B17.modules.Category;
import ShwepSS.B17.modules.Module;

public class AutoLogin
extends Module {
    public static Module get;

    public AutoLogin() {
        super("AutoLogin", "\u0420\u0435\u0430\u043a\u0446\u0438\u044f \u043d\u0430 /reg \u0438 /l (\u043f\u0430\u0441\u0441 qazwsx123", 0, Category.BOTS, true);
        get = this;
    }
}

