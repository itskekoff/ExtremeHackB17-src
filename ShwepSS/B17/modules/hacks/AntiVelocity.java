package ShwepSS.B17.modules.hacks;

import ShwepSS.B17.modules.Category;
import ShwepSS.B17.modules.Module;

public class AntiVelocity
extends Module {
    public static Module instance;

    public AntiVelocity() {
        super("AntiKnockBack", "\u041e\u0442\u0434\u0430\u0447\u0438 \u043e\u0442 \u0443\u0440\u043e\u043d\u0430 \u043d\u0435 \u0431\u0443\u0434\u0435\u0442", 87, Category.Combat, true);
        instance = this;
    }
}

