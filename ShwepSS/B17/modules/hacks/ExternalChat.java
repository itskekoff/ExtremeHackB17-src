package ShwepSS.B17.modules.hacks;

import ShwepSS.B17.Utils.ExtremeChat;
import ShwepSS.B17.modules.Category;
import ShwepSS.B17.modules.Module;

public class ExternalChat
extends Module {
    public ExternalChat() {
        super("ExternalChat", "\u0432\u043d\u0435\u0448\u043d\u0438\u0439 \u0447\u0430\u0442", 0, Category.MISC, true);
    }

    @Override
    public void onEnable() {
        ExtremeChat echat = new ExtremeChat();
        echat.setVisible(true);
        this.toggle();
    }
}

