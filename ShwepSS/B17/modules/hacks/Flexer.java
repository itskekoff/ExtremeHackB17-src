package ShwepSS.B17.modules.hacks;

import ShwepSS.B17.ChatUtils;
import ShwepSS.B17.ExtremeHack;
import ShwepSS.B17.Utils.BlockData;
import ShwepSS.B17.Utils.RandomUtils;
import ShwepSS.B17.Utils.TimerUtils;
import ShwepSS.B17.cg.settings.Setting;
import ShwepSS.B17.modules.Category;
import ShwepSS.B17.modules.Module;
import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketCreativeInventoryAction;
import net.minecraft.world.GameType;

public class Flexer
extends Module {
    public Setting delay = new Setting("Delay", this, 200.0, 10.0, 1000.0, false);
    public TimerUtils timer = new TimerUtils();

    public Flexer() {
        super("Flexer", "\u0440\u0430\u0437\u043d\u0430\u044f \u0431\u0440\u043e\u043d\u044f", 0, Category.MISC, true);
        ExtremeHack.instance.getSetmgr().rSetting(this.delay);
        ExtremeHack.instance.getSetmgr().rSetting(new Setting("FlexerV1", this, true));
        ExtremeHack.instance.getSetmgr().rSetting(new Setting("FlexerV2", this, false));
        ExtremeHack.instance.getSetmgr().rSetting(new Setting("MetaHead", this, false));
        ExtremeHack.instance.getSetmgr().rSetting(new Setting("WoolHands", this, false));
        ExtremeHack.instance.getSetmgr().rSetting(new Setting("HandsOnHead", this, false));
    }

    @Override
    public void onEnable() {
    }

    @Override
    public void onTick() {
        Minecraft mc = Minecraft.getMinecraft();
        if (this.timer.check(this.delay.getValFloat())) {
            ItemStack stack;
            if (ExtremeHack.instance.getSetmgr().getSettingByName("FlexerV1").getValue() && mc.playerController.getCurrentGameType().equals((Object)GameType.CREATIVE)) {
                ItemStack hemlet = new ItemStack(Item.getItemById(298));
                ItemStack chestplate = new ItemStack(Item.getItemById(299));
                ItemStack shorts = new ItemStack(Item.getItemById(300));
                ItemStack botinki = new ItemStack(Item.getItemById(301));
                hemlet.addEnchantment(Enchantment.getEnchantmentByID(1), 1);
                chestplate.addEnchantment(Enchantment.getEnchantmentByID(1), 1);
                botinki.addEnchantment(Enchantment.getEnchantmentByID(1), 1);
                shorts.addEnchantment(Enchantment.getEnchantmentByID(1), 1);
                hemlet.stackSize = 64;
                chestplate.stackSize = 64;
                hemlet.setStackDisplayName(String.valueOf(ChatUtils.ehack) + "Subscribe to ShwepSS");
                chestplate.setStackDisplayName(String.valueOf(ChatUtils.ehack) + "Subscribe to ShwepSS");
                botinki.setStackDisplayName(String.valueOf(ChatUtils.ehack) + "Subscribe to ShwepSS");
                shorts.setStackDisplayName(String.valueOf(ChatUtils.ehack) + "Subscribe to ShwepSS");
                hemlet = BlockData.makeItemColored(hemlet);
                botinki = BlockData.makeItemColored(botinki);
                shorts = BlockData.makeItemColored(shorts);
                chestplate = BlockData.makeItemColored(chestplate);
                mc.player.connection.sendPacket(new CPacketCreativeInventoryAction(8, botinki));
                mc.player.connection.sendPacket(new CPacketCreativeInventoryAction(7, shorts));
                mc.player.connection.sendPacket(new CPacketCreativeInventoryAction(6, chestplate));
                mc.player.connection.sendPacket(new CPacketCreativeInventoryAction(5, hemlet));
            }
            if (ExtremeHack.instance.getSetmgr().getSettingByName("FlexerV2").getValue() && mc.playerController.getCurrentGameType().equals((Object)GameType.CREATIVE)) {
                int[] helmets1 = new int[]{298, 302, 306, 310, 314};
                int[] chestplates1 = new int[]{299, 303, 307, 311, 315};
                int[] leggings1 = new int[]{300, 304, 308, 312, 316};
                int[] boots1 = new int[]{301, 305, 309, 313, 317};
                Random random = new Random();
                int hemlet1 = random.nextInt(helmets1.length);
                int chestplate1 = random.nextInt(chestplates1.length);
                int shorts1 = random.nextInt(leggings1.length);
                int boots2 = random.nextInt(boots1.length);
                ItemStack chestplate = new ItemStack(Item.getItemById(chestplates1[chestplate1]));
                ItemStack hemlet = new ItemStack(Item.getItemById(helmets1[hemlet1]));
                ItemStack shorts = new ItemStack(Item.getItemById(leggings1[shorts1]));
                ItemStack boots = new ItemStack(Item.getItemById(boots1[boots2]));
                boots.addEnchantment(Enchantment.getEnchantmentByID(1), 1);
                hemlet.addEnchantment(Enchantment.getEnchantmentByID(1), 1);
                shorts.addEnchantment(Enchantment.getEnchantmentByID(1), 1);
                boots.addEnchantment(Enchantment.getEnchantmentByID(1), 1);
                mc.player.connection.sendPacket(new CPacketCreativeInventoryAction(8, boots));
                mc.player.connection.sendPacket(new CPacketCreativeInventoryAction(7, shorts));
                mc.player.connection.sendPacket(new CPacketCreativeInventoryAction(6, chestplate));
                mc.player.connection.sendPacket(new CPacketCreativeInventoryAction(5, hemlet));
            }
            if (ExtremeHack.instance.getSetmgr().getSettingByName("WoolHands").getValue() && mc.playerController.getCurrentGameType().equals((Object)GameType.CREATIVE)) {
                stack = new ItemStack(Item.getItemById(35));
                stack.setItemDamage(RandomUtils.nextInt(0, 15));
                stack.addEnchantment(Enchantment.getEnchantmentByID(1), 1);
                mc.player.connection.sendPacket(new CPacketCreativeInventoryAction(36, stack));
                mc.player.connection.sendPacket(new CPacketCreativeInventoryAction(45, stack));
            }
            if (ExtremeHack.instance.getSetmgr().getSettingByName("MetaHead").getValue() && mc.playerController.getCurrentGameType().equals((Object)GameType.CREATIVE)) {
                stack = mc.player.getHeldItemHead();
                stack.setItemDamage(RandomUtils.nextInt(0, 15));
                stack.addEnchantment(Enchantment.getEnchantmentByID(1), 1);
                mc.player.connection.sendPacket(new CPacketCreativeInventoryAction(5, stack));
            }
            if (ExtremeHack.instance.getSetmgr().getSettingByName("HandsOnHead").getValue() && mc.playerController.getCurrentGameType().equals((Object)GameType.CREATIVE)) {
                stack = mc.player.getHeldItemMainhand();
                stack.addEnchantment(Enchantment.getEnchantmentByID(1), 1);
                mc.player.connection.sendPacket(new CPacketCreativeInventoryAction(5, stack));
            }
            this.timer.reset();
        }
    }

    @Override
    public void onDisable() {
    }
}

