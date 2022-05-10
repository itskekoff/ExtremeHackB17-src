package ShwepSS.B17.modules.hacks;

import ShwepSS.B17.ExtremeHack;
import ShwepSS.B17.modules.Category;
import ShwepSS.B17.modules.Module;

public class OpenGui
extends Module {
    public OpenGui() {
        super("ClickGui", "gui", 0, Category.Visuals, false);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        ExtremeHack.mc.displayGuiScreen(ExtremeHack.instance.cg);
        ExtremeHack.mc.gameSettings.guiScale = 2;
        this.toggle();
    }
}

