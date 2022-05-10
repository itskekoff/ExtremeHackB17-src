package ShwepSS.B17.modules.hacks;

import ShwepSS.B17.ExtremeHack;
import ShwepSS.B17.cg.settings.Setting;
import ShwepSS.B17.modules.Category;
import ShwepSS.B17.modules.Module;
import ShwepSS.event.EventPacketRecieve;
import ShwepSS.eventapi.EventManager;
import ShwepSS.eventapi.EventTarget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SPacketUpdateHealth;
import net.minecraft.util.NonNullList;

public class AutoGapple
extends Module {
    private boolean healing = false;
    Setting health = new Setting("Health", this, 15.0, 1.0, 20.0, false);

    public AutoGapple() {
        super("AutoGapple", "\u0410\u0432\u0442\u043e\u043c\u0430\u0442\u043e\u043c \u0445\u0430\u0432\u0430\u0435\u0442 \u044f\u0431\u043b\u043e\u043a\u043e", 0, Category.Combat, true);
        ExtremeHack.instance.getSetmgr().rSetting(this.health);
    }

    @Override
    public void onEnable() {
        EventManager.register(this);
    }

    @Override
    public void onDisable() {
        EventManager.unregister(this);
    }

    @Override
    public void onTick() {
        Minecraft mc = Minecraft.getMinecraft();
        if (this.healing) {
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(), true);
        } else {
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(), false);
        }
        ItemStack item = mc.player.getItemStackFromSlot(EntityEquipmentSlot.OFFHAND);
        NonNullList<ItemStack> iventorySlot = mc.player.inventory.mainInventory;
        for (int i2 = 0; i2 < iventorySlot.size(); ++i2) {
            if (iventorySlot.get(i2) == ItemStack.field_190927_a || item != null && item.getItem() == Items.GOLDEN_APPLE || iventorySlot.get(i2).getItem() != Items.GOLDEN_APPLE) continue;
            mc.playerController.windowClick(0, i2 < 9 ? i2 + 36 : i2, 0, ClickType.PICKUP, mc.player);
            mc.playerController.windowClick(0, 45, 0, ClickType.PICKUP, mc.player);
            mc.playerController.windowClick(0, i2 < 9 ? i2 + 36 : i2, 0, ClickType.PICKUP, mc.player);
        }
    }

    @EventTarget
    public void readHealthPacket(EventPacketRecieve ev2) {
        if (ev2.getPacket() instanceof SPacketUpdateHealth) {
            SPacketUpdateHealth health = (SPacketUpdateHealth)ev2.getPacket();
            this.healing = health.getHealth() <= this.health.getValFloat();
        }
    }
}

