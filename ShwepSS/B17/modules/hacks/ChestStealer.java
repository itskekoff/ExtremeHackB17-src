package ShwepSS.B17.modules.hacks;

import ShwepSS.B17.ExtremeHack;
import ShwepSS.B17.Utils.TimerUtils;
import ShwepSS.B17.cg.settings.Setting;
import ShwepSS.B17.modules.Category;
import ShwepSS.B17.modules.Module;
import net.minecraft.client.Minecraft;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.item.Item;

public class ChestStealer
extends Module {
    Setting delay = new Setting("Delay", this, 120.0, 10.0, 600.0, false);
    TimerUtils time = new TimerUtils();

    public ChestStealer() {
        super("ChestStealer", "\u0422\u0430\u0449\u0438\u0442 \u0432\u0441\u0451 \u0441 \u0441\u0443\u043d\u0434\u0443\u043a\u0430", 19, Category.Player, true);
        ExtremeHack.instance.getSetmgr().rSetting(this.delay);
    }

    @Override
    public void onEnable() {
    }

    @Override
    public void onTick() {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.player.openContainer != null && mc.player.openContainer instanceof ContainerChest) {
            ContainerChest containerchest = (ContainerChest)mc.player.openContainer;
            for (int i2 = 0; i2 < containerchest.getLowerChestInventory().getSizeInventory(); ++i2) {
                if (containerchest.getLowerChestInventory().getStackInSlot(i2).getItem() == Item.getItemById(0) || !this.time.check(this.delay.getValFloat())) continue;
                mc.playerController.windowClick(containerchest.windowId, i2, 0, ClickType.QUICK_MOVE, mc.player);
                this.time.reset();
            }
        }
    }

    @Override
    public void onDisable() {
    }
}

