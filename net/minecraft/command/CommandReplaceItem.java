package net.minecraft.command;

import com.google.common.collect.Maps;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandResultStats;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.NumberInvalidException;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CommandReplaceItem
extends CommandBase {
    private static final Map<String, Integer> SHORTCUTS = Maps.newHashMap();

    static {
        for (int i2 = 0; i2 < 54; ++i2) {
            SHORTCUTS.put("slot.container." + i2, i2);
        }
        for (int j2 = 0; j2 < 9; ++j2) {
            SHORTCUTS.put("slot.hotbar." + j2, j2);
        }
        for (int k2 = 0; k2 < 27; ++k2) {
            SHORTCUTS.put("slot.inventory." + k2, 9 + k2);
        }
        for (int l2 = 0; l2 < 27; ++l2) {
            SHORTCUTS.put("slot.enderchest." + l2, 200 + l2);
        }
        for (int i1 = 0; i1 < 8; ++i1) {
            SHORTCUTS.put("slot.villager." + i1, 300 + i1);
        }
        for (int j1 = 0; j1 < 15; ++j1) {
            SHORTCUTS.put("slot.horse." + j1, 500 + j1);
        }
        SHORTCUTS.put("slot.weapon", 98);
        SHORTCUTS.put("slot.weapon.mainhand", 98);
        SHORTCUTS.put("slot.weapon.offhand", 99);
        SHORTCUTS.put("slot.armor.head", 100 + EntityEquipmentSlot.HEAD.getIndex());
        SHORTCUTS.put("slot.armor.chest", 100 + EntityEquipmentSlot.CHEST.getIndex());
        SHORTCUTS.put("slot.armor.legs", 100 + EntityEquipmentSlot.LEGS.getIndex());
        SHORTCUTS.put("slot.armor.feet", 100 + EntityEquipmentSlot.FEET.getIndex());
        SHORTCUTS.put("slot.horse.saddle", 400);
        SHORTCUTS.put("slot.horse.armor", 401);
        SHORTCUTS.put("slot.horse.chest", 499);
    }

    @Override
    public String getCommandName() {
        return "replaceitem";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "commands.replaceitem.usage";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        Item item;
        int i2;
        boolean flag;
        if (args.length < 1) {
            throw new WrongUsageException("commands.replaceitem.usage", new Object[0]);
        }
        if ("entity".equals(args[0])) {
            flag = false;
        } else {
            if (!"block".equals(args[0])) {
                throw new WrongUsageException("commands.replaceitem.usage", new Object[0]);
            }
            flag = true;
        }
        if (flag) {
            if (args.length < 6) {
                throw new WrongUsageException("commands.replaceitem.block.usage", new Object[0]);
            }
            i2 = 4;
        } else {
            if (args.length < 4) {
                throw new WrongUsageException("commands.replaceitem.entity.usage", new Object[0]);
            }
            i2 = 2;
        }
        String s2 = args[i2];
        int j2 = this.getSlotForShortcut(args[i2++]);
        try {
            item = CommandReplaceItem.getItemByText(sender, args[i2]);
        }
        catch (NumberInvalidException numberinvalidexception) {
            if (Block.getBlockFromName(args[i2]) != Blocks.AIR) {
                throw numberinvalidexception;
            }
            item = null;
        }
        int k2 = args.length > ++i2 ? CommandReplaceItem.parseInt(args[i2++], 1, item.getItemStackLimit()) : 1;
        int l2 = args.length > i2 ? CommandReplaceItem.parseInt(args[i2++]) : 0;
        ItemStack itemstack = new ItemStack(item, k2, l2);
        if (args.length > i2) {
            String s1 = CommandReplaceItem.buildString(args, i2);
            try {
                itemstack.setTagCompound(JsonToNBT.getTagFromJson(s1));
            }
            catch (NBTException nbtexception) {
                throw new CommandException("commands.replaceitem.tagError", nbtexception.getMessage());
            }
        }
        if (flag) {
            sender.setCommandStat(CommandResultStats.Type.AFFECTED_ITEMS, 0);
            BlockPos blockpos = CommandReplaceItem.parseBlockPos(sender, args, 1, false);
            World world = sender.getEntityWorld();
            TileEntity tileentity = world.getTileEntity(blockpos);
            if (tileentity == null || !(tileentity instanceof IInventory)) {
                throw new CommandException("commands.replaceitem.noContainer", blockpos.getX(), blockpos.getY(), blockpos.getZ());
            }
            IInventory iinventory = (IInventory)((Object)tileentity);
            if (j2 >= 0 && j2 < iinventory.getSizeInventory()) {
                iinventory.setInventorySlotContents(j2, itemstack);
            }
        } else {
            Entity entity = CommandReplaceItem.getEntity(server, sender, args[1]);
            sender.setCommandStat(CommandResultStats.Type.AFFECTED_ITEMS, 0);
            if (entity instanceof EntityPlayer) {
                ((EntityPlayer)entity).inventoryContainer.detectAndSendChanges();
            }
            if (!entity.replaceItemInInventory(j2, itemstack)) {
                throw new CommandException("commands.replaceitem.failed", s2, k2, itemstack.func_190926_b() ? "Air" : itemstack.getTextComponent());
            }
            if (entity instanceof EntityPlayer) {
                ((EntityPlayer)entity).inventoryContainer.detectAndSendChanges();
            }
        }
        sender.setCommandStat(CommandResultStats.Type.AFFECTED_ITEMS, k2);
        CommandReplaceItem.notifyCommandListener(sender, (ICommand)this, "commands.replaceitem.success", s2, k2, itemstack.func_190926_b() ? "Air" : itemstack.getTextComponent());
    }

    private int getSlotForShortcut(String shortcut) throws CommandException {
        if (!SHORTCUTS.containsKey(shortcut)) {
            throw new CommandException("commands.generic.parameter.invalid", shortcut);
        }
        return SHORTCUTS.get(shortcut);
    }

    @Override
    public List<String> getTabCompletionOptions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos pos) {
        if (args.length == 1) {
            return CommandReplaceItem.getListOfStringsMatchingLastWord(args, "entity", "block");
        }
        if (args.length == 2 && "entity".equals(args[0])) {
            return CommandReplaceItem.getListOfStringsMatchingLastWord(args, server.getAllUsernames());
        }
        if (args.length >= 2 && args.length <= 4 && "block".equals(args[0])) {
            return CommandReplaceItem.getTabCompletionCoordinate(args, 1, pos);
        }
        if (!(args.length == 3 && "entity".equals(args[0]) || args.length == 5 && "block".equals(args[0]))) {
            return !(args.length == 4 && "entity".equals(args[0]) || args.length == 6 && "block".equals(args[0])) ? Collections.emptyList() : CommandReplaceItem.getListOfStringsMatchingLastWord(args, Item.REGISTRY.getKeys());
        }
        return CommandReplaceItem.getListOfStringsMatchingLastWord(args, SHORTCUTS.keySet());
    }

    @Override
    public boolean isUsernameIndex(String[] args, int index) {
        return args.length > 0 && "entity".equals(args[0]) && index == 1;
    }
}

