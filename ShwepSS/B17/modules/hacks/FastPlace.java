package ShwepSS.B17.modules.hacks;

import ShwepSS.B17.modules.Category;
import ShwepSS.B17.modules.Module;
import net.minecraft.client.Minecraft;

public class FastPlace
extends Module {
    public FastPlace() {
        super("FastPlace", "Place blocks even faster!", 25, Category.Player, true);
    }

    @Override
    public void onTick() {
        Minecraft.getMinecraft().playerController.blockHitDelay = 0;
        Minecraft.getMinecraft().rightClickDelayTimer = 0;
    }

    @Override
    public void onDisable() {
        Minecraft.getMinecraft().rightClickDelayTimer = 4;
    }
}

