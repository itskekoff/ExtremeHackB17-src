package ShwepSS.B17.modules.hacks;

import ShwepSS.B17.modules.Category;
import ShwepSS.B17.modules.Module;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.InventoryEffectRenderer;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;

public class AutoArmor
extends Module {
    private int timer;
    private int lastUsed;
    private int Delaytimer;
    public static int delay;
    private int HEAD;

    public AutoArmor() {
        super("AutoArmor", "\u0430\u0432\u0442\u043e\u043c\u0430\u0442\u043e\u043c \u043d\u0430\u0434\u0435\u0432\u0430\u0435\u0442 \u0431\u0440\u043e\u043d\u044c", 19, Category.Combat, true);
    }

    @Override
    public void onEnable() {
        this.timer = 0;
    }

    @Override
    public void onTick() {
        int armorType;
        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayerSP player = mc.player;
        if (this.timer > 0) {
            --this.timer;
            return;
        }
        if (mc.currentScreen instanceof GuiContainer && !(mc.currentScreen instanceof InventoryEffectRenderer)) {
            return;
        }
        int[] bestArmorSlots = new int[4];
        int[] bestArmorValues = new int[4];
        for (armorType = 0; armorType < 4; ++armorType) {
            ItemStack oldArmor = mc.player.inventory.armorItemInSlot(armorType);
            if (oldArmor != null && oldArmor.getItem() instanceof ItemArmor) {
                bestArmorValues[armorType] = ((ItemArmor)oldArmor.getItem()).damageReduceAmount;
            }
            bestArmorSlots[armorType] = -1;
        }
        for (int slot = 0; slot < 36; ++slot) {
            int armorType2;
            ItemArmor armor;
            int armorValue;
            ItemStack stack = mc.player.inventory.getStackInSlot(slot);
            if (stack == null || !(stack.getItem() instanceof ItemArmor) || (armorValue = armor.damageReduceAmount) <= bestArmorValues[armorType2 = this.getArmorType(armor = (ItemArmor)stack.getItem())]) continue;
            bestArmorSlots[armorType2] = slot;
            bestArmorValues[armorType2] = armorValue;
        }
        for (armorType = 0; armorType < 4; ++armorType) {
            ItemStack oldArmor;
            int slot = bestArmorSlots[armorType];
            if (slot == -1 || (oldArmor = mc.player.inventory.armorItemInSlot(armorType)) != null && AutoArmor.isEmptySlot(oldArmor) && mc.player.inventory.getFirstEmptyStack() == -1) continue;
            if (slot < 9) {
                slot += 36;
            }
            mc.playerController.windowClick(0, 8 - armorType, 0, ClickType.QUICK_MOVE, mc.player);
            mc.playerController.windowClick(0, slot, 0, ClickType.QUICK_MOVE, mc.player);
            break;
        }
        this.timer = 4;
    }

    public int getArmorType(ItemArmor armor) {
        return armor.armorType.ordinal() - 2;
    }

    public static boolean isEmptySlot(ItemStack slot) {
        return slot == null;
    }

    @Override
    public void onDisable() {
    }
}

