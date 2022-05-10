package ShwepSS.B17.modules.hacks;

import ShwepSS.B17.modules.Category;
import ShwepSS.B17.modules.Module;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;

public class Jesus
extends Module {
    public Jesus() {
        super("AutoSprint", "\u0410\u0432\u0442\u043e\u043c\u0430\u0442\u0438\u0447\u0435\u0441\u043a\u0438 \u043f\u0435\u0440\u0435\u0445\u043e\u0434\u0438\u0442 \u043d\u0430 \u0441\u043f\u0440\u0438\u043d\u0442", 0, Category.Movement, true);
    }

    @Override
    public void onEnable() {
    }

    @Override
    public void onTick() {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.world.getBlockState(new BlockPos(mc.player.posX - 0.0, mc.player.posY - 0.0, mc.player.posZ)).getBlock() == Blocks.WATER) {
            mc.player.posY = mc.player.posY += 0.0;
            mc.player.onGround = true;
        }
    }

    @Override
    public void onDisable() {
    }
}

