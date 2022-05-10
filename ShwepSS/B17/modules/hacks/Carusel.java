package ShwepSS.B17.modules.hacks;

import ShwepSS.B17.Utils.MovementUtil;
import ShwepSS.B17.modules.Category;
import ShwepSS.B17.modules.Module;
import net.minecraft.client.Minecraft;
import org.apache.commons.lang3.RandomUtils;

public class Carusel
extends Module {
    public Carusel() {
        super("\u041a\u0430\u0440\u0443\u0441\u0435\u043b\u044c", "\u043b\u0430\u0434\u043d\u043e, \u044f \u0441\u0435\u0439\u0447\u0430\u0441 \u044f\u0432\u043d\u043e \u043d\u0435 \u0432 \u0445\u043e\u0440\u043e\u0448\u0435\u043c \u043d\u0430\u0441\u0442\u0440\u043e\u0435\u043d\u0438\u0438.", 0, Category.MISC, true);
    }

    @Override
    public void onEnable() {
    }

    @Override
    public void onTick() {
        Minecraft.getMinecraft().player.rotationYaw += RandomUtils.nextFloat(5.0f, 10.0f);
        MovementUtil.setSpeed2(0.4);
    }

    @Override
    public void onDisable() {
    }
}

