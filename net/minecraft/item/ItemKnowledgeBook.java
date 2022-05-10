package net.minecraft.item;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ItemKnowledgeBook
extends Item {
    private static final Logger field_194126_a = LogManager.getLogger();

    public ItemKnowledgeBook() {
        this.setMaxStackSize(1);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World itemStackIn, EntityPlayer worldIn, EnumHand playerIn) {
        ItemStack itemstack = worldIn.getHeldItem(playerIn);
        NBTTagCompound nbttagcompound = itemstack.getTagCompound();
        if (!worldIn.capabilities.isCreativeMode) {
            worldIn.setHeldItem(playerIn, ItemStack.field_190927_a);
        }
        if (nbttagcompound != null && nbttagcompound.hasKey("Recipes", 9)) {
            if (!itemStackIn.isRemote) {
                NBTTagList nbttaglist = nbttagcompound.getTagList("Recipes", 8);
                ArrayList<IRecipe> list = Lists.newArrayList();
                for (int i2 = 0; i2 < nbttaglist.tagCount(); ++i2) {
                    String s2 = nbttaglist.getStringTagAt(i2);
                    IRecipe irecipe = CraftingManager.func_193373_a(new ResourceLocation(s2));
                    if (irecipe == null) {
                        field_194126_a.error("Invalid recipe: " + s2);
                        return new ActionResult<ItemStack>(EnumActionResult.FAIL, itemstack);
                    }
                    list.add(irecipe);
                }
                worldIn.func_192021_a(list);
                worldIn.addStat(StatList.getObjectUseStats(this));
            }
            return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, itemstack);
        }
        field_194126_a.error("Tag not valid: " + nbttagcompound);
        return new ActionResult<ItemStack>(EnumActionResult.FAIL, itemstack);
    }
}

