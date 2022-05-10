package ShwepSS.B17.modules.hacks;

import ShwepSS.B17.ExtremeHack;
import ShwepSS.B17.Utils.TimerUtils;
import ShwepSS.B17.cg.settings.Setting;
import ShwepSS.B17.modules.Category;
import ShwepSS.B17.modules.Module;
import ShwepSS.B17.modules.hacks.themes.BlueTheme;
import ShwepSS.B17.modules.hacks.themes.ExtremeTheme;
import ShwepSS.B17.modules.hacks.themes.GreenTheme;
import ShwepSS.B17.modules.hacks.themes.RedTheme;
import org.apache.commons.lang.math.RandomUtils;

public class Girlyanda
extends Module {
    public Setting delay = new Setting("Delay", this, 700.0, 100.0, 1000.0, false);
    TimerUtils timer = new TimerUtils();

    public Girlyanda() {
        super("\u0413\u0438\u0440\u043b\u044f\u043d\u0434\u0430", "\u0412\u043a\u043b\u044e\u0447\u0438 \u0443\u0432\u0438\u0434\u0438\u0448\u044c)", 0, Category.Theme, false);
        ExtremeHack.instance.getSetmgr().rSetting(this.delay);
    }

    @Override
    public void onEnable() {
    }

    @Override
    public void onTick() {
        if (this.timer.check(this.delay.getValFloat())) {
            if (RandomUtils.nextBoolean()) {
                RedTheme.instance.toggle();
            } else if (RandomUtils.nextBoolean()) {
                BlueTheme.instance.toggle();
            } else if (RandomUtils.nextBoolean()) {
                ExtremeTheme.instance.toggle();
            } else {
                GreenTheme.instance.toggle();
            }
            this.timer.reset();
        }
    }

    @Override
    public void onDisable() {
    }
}

