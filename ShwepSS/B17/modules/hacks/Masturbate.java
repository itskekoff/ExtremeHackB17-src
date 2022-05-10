package ShwepSS.B17.modules.hacks;

import ShwepSS.B17.Utils.TimerUtils;
import ShwepSS.B17.modules.Category;
import ShwepSS.B17.modules.Module;
import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumHand;
import org.apache.commons.lang3.RandomUtils;

public class Masturbate
extends Module {
    TimerUtils time = new TimerUtils();

    public Masturbate() {
        super("Handjob", "\u043b\u043e\u043b", 0, Category.MISC, true);
    }

    @Override
    public void onEnable() {
    }

    @Override
    public void onTick() {
        if (this.time.check(100.0f)) {
            if (RandomUtils.nextBoolean()) {
                Minecraft.getMinecraft().player.swingArm(EnumHand.MAIN_HAND);
            } else {
                Minecraft.getMinecraft().player.swingArm(EnumHand.OFF_HAND);
            }
            this.time.reset();
        }
    }

    @Override
    public void onDisable() {
    }
}

