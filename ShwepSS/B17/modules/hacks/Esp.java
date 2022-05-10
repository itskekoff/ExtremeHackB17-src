package ShwepSS.B17.modules.hacks;

import ShwepSS.B17.modules.Category;
import ShwepSS.B17.modules.Module;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

public class Esp
extends Module {
    public Esp() {
        super("SimpleESP", "\u043f\u0440\u043e\u0441\u0442\u043e\u0435 \u0433\u043b\u043e\u0443 \u0435\u0441\u043f", 0, Category.Visuals, true);
    }

    @Override
    public void onTick() {
        Minecraft mc = Minecraft.getMinecraft();
        for (Entity ent : mc.world.loadedEntityList) {
            if (!(ent instanceof EntityPlayer)) continue;
            ent.setGlowing(true);
        }
    }

    @Override
    public void onDisable() {
        Minecraft mc = Minecraft.getMinecraft();
        for (Entity ent : mc.world.loadedEntityList) {
            if (!(ent instanceof EntityPlayer)) continue;
            ent.setGlowing(false);
        }
    }
}

