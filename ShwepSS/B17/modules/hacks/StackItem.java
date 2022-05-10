package ShwepSS.B17.modules.hacks;

import ShwepSS.B17.ChatUtils;
import ShwepSS.B17.modules.Category;
import ShwepSS.B17.modules.Module;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketCreativeInventoryAction;

public class StackItem
extends Module {
    public StackItem() {
        super("StackItem", "\u0421\u0442\u0430\u043a\u043d\u0435\u0442 \u043f\u0440\u0435\u0434\u043c\u0435\u0442 \u0432 \u0440\u0443\u043a\u0435", 0, Category.MISC, true);
    }

    @Override
    public void onEnable() {
        Minecraft mc = Minecraft.getMinecraft();
        ItemStack stack = mc.player.getHeldItemMainhand().copy();
        stack.stackSize = 64;
        mc.player.connection.sendPacket(new CPacketCreativeInventoryAction(mc.player.inventory.currentItem + 36, stack));
        ChatUtils.message(String.valueOf(ChatUtils.ehack) + "\u0413\u043e\u0442\u0438\u0432\u0430 \u0435\u043f\u0442\u0430!");
        this.toggle();
    }
}

