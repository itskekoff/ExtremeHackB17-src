package ShwepSS.B17.modules.hacks;

import ShwepSS.B17.ExtremeHack;
import ShwepSS.B17.Utils.MovementUtil;
import ShwepSS.B17.cg.settings.Setting;
import ShwepSS.B17.modules.Category;
import ShwepSS.B17.modules.Module;

public class SpeedHack
extends Module {
    public Setting speed = new Setting("Speed", this, 0.6, 0.0, 10.0, false);

    public SpeedHack() {
        super("SpeedHack", "\u0441\u043f\u0438\u0434\u044b", 46, Category.Movement, true);
        ExtremeHack.instance.getSetmgr().rSetting(this.speed);
    }

    @Override
    public void onEnable() {
    }

    @Override
    public void onTick() {
        MovementUtil.setSpeed(this.speed.getValFloat());
    }

    @Override
    public void onDisable() {
    }
}

