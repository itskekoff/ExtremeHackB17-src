package ShwepSS.B17.modules.hacks;

import ShwepSS.B17.modules.Category;
import ShwepSS.B17.modules.Module;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;

public class Parkour
extends Module {
    public Parkour() {
        super("Parkour", "\u041f\u0440\u044b\u0433\u0430\u0435\u0442 \u0441 \u043a\u0440\u0430\u044f \u0431\u043b\u043e\u043a\u0430", 33, Category.Movement, true);
    }

    @Override
    public void onEnable() {
    }

    @Override
    public void onTick() {
        EntityPlayerSP player;
        WorldClient world;
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.player.onGround && !mc.player.isSneaking() && !mc.gameSettings.keyBindJump.isPressed() && (world = mc.world).getCollisionBoxes(player = mc.player, mc.player.getEntityBoundingBox().offset(0.0, -0.5, 0.0).expand(-1.0E-28, 0.0, -1.0E-29)).isEmpty()) {
            mc.player.jump();
        }
    }

    @Override
    public void onDisable() {
    }
}

