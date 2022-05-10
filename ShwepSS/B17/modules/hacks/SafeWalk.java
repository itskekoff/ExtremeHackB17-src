package ShwepSS.B17.modules.hacks;

import ShwepSS.B17.ExtremeHack;
import ShwepSS.B17.cg.settings.Setting;
import ShwepSS.B17.modules.Category;
import ShwepSS.B17.modules.Module;
import ShwepSS.event.EventSafeWalk;
import ShwepSS.eventapi.EventManager;
import ShwepSS.eventapi.EventTarget;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;

public class SafeWalk
extends Module {
    Setting nolegit = new Setting("Legit?", this, true);

    public SafeWalk() {
        super("SafeWalk", "\u041d\u0435 \u0434\u0430\u0451\u0442 \u0443\u043f\u0430\u0441\u0442\u044c \u0441 \u0431\u043b\u043e\u043a\u0430", 0, Category.Movement, true);
        ExtremeHack.instance.getSetmgr().rSetting(this.nolegit);
    }

    @Override
    public void onEnable() {
        EventManager.register(this);
    }

    @EventTarget
    public void onKek(EventSafeWalk ev2) {
        if (!this.nolegit.getValue()) {
            ev2.setCancelled(true);
        }
    }

    @Override
    public void onTick() {
        if (!this.nolegit.getValue()) {
            return;
        }
        Minecraft mc = Minecraft.getMinecraft();
        BlockPos blockPos = new BlockPos(mc.player.posX, mc.player.posY - 1.0, mc.player.posZ);
        if (mc.player.fallDistance <= 4.0f) {
            mc.gameSettings.keyBindSneak.pressed = mc.world.getBlockState(blockPos).getBlock() == Blocks.AIR;
        }
    }

    @Override
    public void onDisable() {
        EventManager.unregister(this);
    }
}

