package net.minecraft.command;

import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandResultStats;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.NumberInvalidException;
import net.minecraft.command.WrongUsageException;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

public class CommandEnchant
extends CommandBase {
    @Override
    public String getCommandName() {
        return "enchant";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "commands.enchant.usage";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        Enchantment enchantment;
        if (args.length < 2) {
            throw new WrongUsageException("commands.enchant.usage", new Object[0]);
        }
        EntityLivingBase entitylivingbase = CommandEnchant.getEntity(server, sender, args[0], EntityLivingBase.class);
        sender.setCommandStat(CommandResultStats.Type.AFFECTED_ITEMS, 0);
        try {
            enchantment = Enchantment.getEnchantmentByID(CommandEnchant.parseInt(args[1], 0));
        }
        catch (NumberInvalidException var12) {
            enchantment = Enchantment.getEnchantmentByLocation(args[1]);
        }
        if (enchantment == null) {
            throw new NumberInvalidException("commands.enchant.notFound", args[1]);
        }
        int i2 = 1;
        ItemStack itemstack = entitylivingbase.getHeldItemMainhand();
        if (itemstack.func_190926_b()) {
            throw new CommandException("commands.enchant.noItem", new Object[0]);
        }
        if (!enchantment.canApply(itemstack)) {
            throw new CommandException("commands.enchant.cantEnchant", new Object[0]);
        }
        if (args.length >= 3) {
            i2 = CommandEnchant.parseInt(args[2], enchantment.getMinLevel(), enchantment.getMaxLevel());
        }
        if (itemstack.hasTagCompound()) {
            NBTTagList nbttaglist = itemstack.getEnchantmentTagList();
            for (int j2 = 0; j2 < nbttaglist.tagCount(); ++j2) {
                Enchantment enchantment1;
                short k2 = nbttaglist.getCompoundTagAt(j2).getShort("id");
                if (Enchantment.getEnchantmentByID(k2) == null || enchantment.func_191560_c(enchantment1 = Enchantment.getEnchantmentByID(k2))) continue;
                throw new CommandException("commands.enchant.cantCombine", enchantment.getTranslatedName(i2), enchantment1.getTranslatedName(nbttaglist.getCompoundTagAt(j2).getShort("lvl")));
            }
        }
        itemstack.addEnchantment(enchantment, i2);
        CommandEnchant.notifyCommandListener(sender, (ICommand)this, "commands.enchant.success", new Object[0]);
        sender.setCommandStat(CommandResultStats.Type.AFFECTED_ITEMS, 1);
    }

    @Override
    public List<String> getTabCompletionOptions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos pos) {
        if (args.length == 1) {
            return CommandEnchant.getListOfStringsMatchingLastWord(args, server.getAllUsernames());
        }
        return args.length == 2 ? CommandEnchant.getListOfStringsMatchingLastWord(args, Enchantment.REGISTRY.getKeys()) : Collections.emptyList();
    }

    @Override
    public boolean isUsernameIndex(String[] args, int index) {
        return index == 0;
    }
}

