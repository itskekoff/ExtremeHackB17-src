package ShwepSS.B17.modules.hacks;

import ShwepSS.B17.ExtremeHack;
import ShwepSS.B17.cg.settings.Setting;
import ShwepSS.B17.modules.Category;
import ShwepSS.B17.modules.Module;
import net.minecraft.client.Minecraft;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.ContainerShulkerBox;
import net.minecraft.inventory.ContainerWorkbench;

public class NoOpenGui
extends Module {
    public NoOpenGui() {
        super("NoOpenGui", "\u043d\u0435 \u043f\u043e\u0437\u0432\u043e\u043b\u044f\u0435\u0442 \u043e\u043a\u0442\u0440\u044b\u0442\u044c \u0433\u0443\u0438 \u0447\u0435\u0441\u0442\u0430 \u043f\u0435\u0447\u043a\u0438 \u0438 \u0442\u0434", 0, Category.MISC, true);
        ExtremeHack.instance.getSetmgr().rSetting(new Setting("Chest", this, true));
        ExtremeHack.instance.getSetmgr().rSetting(new Setting("Workbench", this, false));
        ExtremeHack.instance.getSetmgr().rSetting(new Setting("ShulkerBox", this, false));
    }

    @Override
    public void onEnable() {
    }

    @Override
    public void onTick() {
        boolean chest = ExtremeHack.instance.getSetmgr().getSettingByName("Chest").getValue();
        boolean workbench = ExtremeHack.instance.getSetmgr().getSettingByName("Workbench").getValue();
        boolean shalker = ExtremeHack.instance.getSetmgr().getSettingByName("ShulkerBox").getValue();
        Minecraft mc = Minecraft.getMinecraft();
        if (chest && mc.player.openContainer != null && mc.player.openContainer instanceof ContainerChest) {
            mc.displayGuiScreen(null);
        }
        if (shalker && mc.player.openContainer != null && mc.player.openContainer instanceof ContainerShulkerBox) {
            mc.displayGuiScreen(null);
        }
        if (workbench && mc.player.openContainer != null && mc.player.openContainer instanceof ContainerWorkbench) {
            mc.displayGuiScreen(null);
        }
    }

    @Override
    public void onDisable() {
    }
}

