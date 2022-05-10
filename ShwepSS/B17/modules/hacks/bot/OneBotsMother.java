package ShwepSS.B17.modules.hacks.bot;

import ShwepSS.B17.modules.Category;
import ShwepSS.B17.modules.Module;
import ShwepSS.eventapi.EventManager;

public class OneBotsMother
extends Module {
    public static Module get;

    public OneBotsMother() {
        super("+1Bot mother", "\u0411\u0444 \u0431\u043e\u0442\u044b \u043f\u043e\u0432\u0442\u043e\u0440\u044f\u044e\u0442 \u0437\u0430 \u0432\u0430\u043c\u0438", 0, Category.BOTS, true);
        get = this;
    }

    @Override
    public void onEnable() {
        EventManager.register(this);
    }

    @Override
    public void onDisable() {
        EventManager.unregister(this);
    }
}

