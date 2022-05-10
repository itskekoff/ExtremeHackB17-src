package ShwepSS.B17.modules.hacks;

import ShwepSS.B17.ChatUtils;
import ShwepSS.B17.ExtremeHack;
import ShwepSS.B17.Utils.RandomUtils;
import ShwepSS.B17.cg.settings.Setting;
import ShwepSS.B17.modules.Category;
import ShwepSS.B17.modules.Module;
import java.util.ArrayList;
import net.minecraft.client.Minecraft;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketCreativeInventoryAction;
import org.lwjgl.input.Keyboard;

public class Dropper
extends Module {
    ArrayList<String> options = new ArrayList();

    public Dropper() {
        super("Dropper", "\u0414\u0440\u043e\u043f\u0430\u0435\u0442 \u0432\u0435\u0449\u0438 \u0438\u0437 \u0440\u0443\u043a\u0438, \u043e\u0447 \u043c\u043d\u043e\u0433\u043e", 0, Category.MISC, true);
        this.options.add("Mega - all slots");
        this.options.add("Full - hotbar");
        this.options.add("Lite - hand slot");
        this.options.add("Random drop");
        ExtremeHack.instance.getSetmgr().rSetting(new Setting("Dropper Modes:", this, "Lite - hand slot", this.options));
    }

    public String getMode() {
        return ExtremeHack.instance.getSetmgr().getSettingByName("Dropper Modes:").getValString();
    }

    public Setting getModeSetting() {
        return ExtremeHack.instance.getSetmgr().getSettingByName("Dropper Modes:");
    }

    public ArrayList<String> getSettingMode() {
        return this.options;
    }

    @Override
    public void onEnable() {
        ChatUtils.message(String.valueOf(ChatUtils.ehack) + "Press 'q' to dropping///");
    }

    @Override
    public void onTick() {
        int i2;
        ItemStack stack;
        Minecraft mc = Minecraft.getMinecraft();
        if (this.getMode().equalsIgnoreCase("Mega - all slots") && mc.playerController.isInCreativeMode() && Keyboard.isKeyDown(16)) {
            stack = mc.player.inventory.getCurrentItem().copy();
            stack.stackSize = 64;
            for (i2 = 5; i2 < 44; ++i2) {
                mc.player.connection.sendPacket(new CPacketCreativeInventoryAction(i2, stack));
                mc.playerController.windowClick(0, i2, 1, ClickType.THROW, mc.player);
            }
        }
        if (this.getMode().equalsIgnoreCase("Full - hotbar") && mc.playerController.isInCreativeMode() && Keyboard.isKeyDown(16)) {
            stack = mc.player.inventory.getCurrentItem().copy();
            stack.stackSize = 64;
            for (i2 = 36; i2 < 45; ++i2) {
                mc.player.connection.sendPacket(new CPacketCreativeInventoryAction(i2, stack));
                mc.playerController.windowClick(0, i2, 1, ClickType.THROW, mc.player);
            }
        }
        if (this.getMode().equalsIgnoreCase("Lite - hand slot") && mc.playerController.isInCreativeMode() && Keyboard.isKeyDown(16)) {
            stack = mc.player.inventory.getCurrentItem().copy();
            stack.stackSize = 64;
            for (i2 = 0; i2 < 5; ++i2) {
                mc.player.connection.sendPacket(new CPacketCreativeInventoryAction(36, stack));
                mc.playerController.windowClick(0, 36, 1, ClickType.THROW, mc.player);
            }
        }
        if (this.getMode().equalsIgnoreCase("Random govno") && mc.playerController.isInCreativeMode() && Keyboard.isKeyDown(16)) {
            stack = new ItemStack(Item.getItemById(RandomUtils.nextInt(1, 300)));
            stack.stackSize = 64;
            for (i2 = 0; i2 < 40; ++i2) {
                mc.player.connection.sendPacket(new CPacketCreativeInventoryAction(36, stack));
                mc.playerController.windowClick(0, 36, 1, ClickType.THROW, mc.player);
            }
        }
    }

    @Override
    public void onDisable() {
    }
}

