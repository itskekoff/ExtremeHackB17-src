package ShwepSS.B17.modules.hacks.themes;

import ShwepSS.B17.ExtremeHack;
import ShwepSS.B17.cg.settings.Setting;
import ShwepSS.B17.modules.Category;
import ShwepSS.B17.modules.Module;

public class ConfGui
extends Module {
    public static Module instance;

    public ConfGui() {
        super("Gui Configs", "\u041a\u043e\u043d\u0444\u0438\u0433\u0443\u0440\u0430\u0446\u0438\u0438 \u043a\u043b\u0438\u043a\u0433\u0443\u0438", 0, Category.Theme, false);
        instance = this;
        ExtremeHack.instance.getSetmgr().rSetting(new Setting("GlowPanels", this, true));
        ExtremeHack.instance.getSetmgr().rSetting(new Setting("Particle1", this, true));
    }

    @Override
    public void onEnable() {
    }

    @Override
    public void onDisable() {
    }
}

