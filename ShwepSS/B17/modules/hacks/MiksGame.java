package ShwepSS.B17.modules.hacks;

import ShwepSS.B17.modules.Category;
import ShwepSS.B17.modules.Module;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

public class MiksGame
extends Module {
    public MiksGame() {
        super("MiksGame", "\u0423\u0434\u0430\u043b\u044f\u0435\u0442 \u0432\u0441\u0435\u0445 \u0438\u0433\u0440\u043e\u043a\u043e\u0432", 0, Category.Tesla, true);
    }

    @Override
    public void onTick() {
        Minecraft mc = Minecraft.getMinecraft();
        for (Entity ent : mc.world.loadedEntityList) {
            if (!(ent instanceof EntityPlayer) || ent == mc.player) continue;
            mc.world.removeEntity(ent);
        }
    }
}

