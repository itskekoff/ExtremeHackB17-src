package ShwepSS.B17.modules.hacks.bot;

import ShwepSS.B17.ExtremeHack;
import ShwepSS.B17.HackConfigs;
import ShwepSS.B17.Utils.TimerUtils;
import ShwepSS.B17.cg.settings.Setting;
import ShwepSS.B17.modules.Category;
import ShwepSS.B17.modules.Module;
import ShwepSS.B17.modules.hacks.bot.JoinerBot;

public class JoinerSpam
extends Module {
    public Setting delay = new Setting("Delay", this, 1500.0, 100.0, 10000.0, false);
    TimerUtils time = new TimerUtils();

    public JoinerSpam() {
        super("===JoinerSpam", "\u0421\u043f\u0430\u043c\u0438\u0442 \u0441\u043e\u043e\u0431\u0449\u0435\u043d\u0438\u044f \u043e\u0442 \u0434\u0436\u043e\u0439\u043d\u0435\u0440\u0430", 0, Category.BOTS, true);
        ExtremeHack.instance.getSetmgr().rSetting(this.delay);
    }

    @Override
    public void onEnable() {
    }

    @Override
    public void onTick() {
        if (this.time.check(this.delay.getValFloat())) {
            for (JoinerBot bat2 : JoinerBot.bots) {
                bat2.sendMessage(HackConfigs.spom);
            }
            this.time.reset();
        }
    }

    @Override
    public void onDisable() {
    }
}

