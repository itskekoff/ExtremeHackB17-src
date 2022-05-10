package net.minecraft.item.crafting;

import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemWrittenBook;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;

public class RecipeBookCloning
implements IRecipe {
    @Override
    public boolean matches(InventoryCrafting inv, World worldIn) {
        int i2 = 0;
        ItemStack itemstack = ItemStack.field_190927_a;
        for (int j2 = 0; j2 < inv.getSizeInventory(); ++j2) {
            ItemStack itemstack1 = inv.getStackInSlot(j2);
            if (itemstack1.func_190926_b()) continue;
            if (itemstack1.getItem() == Items.WRITTEN_BOOK) {
                if (!itemstack.func_190926_b()) {
                    return false;
                }
                itemstack = itemstack1;
                continue;
            }
            if (itemstack1.getItem() != Items.WRITABLE_BOOK) {
                return false;
            }
            ++i2;
        }
        return !itemstack.func_190926_b() && itemstack.hasTagCompound() && i2 > 0;
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting inv) {
        int i2 = 0;
        ItemStack itemstack = ItemStack.field_190927_a;
        for (int j2 = 0; j2 < inv.getSizeInventory(); ++j2) {
            ItemStack itemstack1 = inv.getStackInSlot(j2);
            if (itemstack1.func_190926_b()) continue;
            if (itemstack1.getItem() == Items.WRITTEN_BOOK) {
                if (!itemstack.func_190926_b()) {
                    return ItemStack.field_190927_a;
                }
                itemstack = itemstack1;
                continue;
            }
            if (itemstack1.getItem() != Items.WRITABLE_BOOK) {
                return ItemStack.field_190927_a;
            }
            ++i2;
        }
        if (!itemstack.func_190926_b() && itemstack.hasTagCompound() && i2 >= 1 && ItemWrittenBook.getGeneration(itemstack) < 2) {
            ItemStack itemstack2 = new ItemStack(Items.WRITTEN_BOOK, i2);
            itemstack2.setTagCompound(itemstack.getTagCompound().copy());
            itemstack2.getTagCompound().setInteger("generation", ItemWrittenBook.getGeneration(itemstack) + 1);
            if (itemstack.hasDisplayName()) {
                itemstack2.setStackDisplayName(itemstack.getDisplayName());
            }
            return itemstack2;
        }
        return ItemStack.field_190927_a;
    }

    @Override
    public ItemStack getRecipeOutput() {
        return ItemStack.field_190927_a;
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(InventoryCrafting inv) {
        NonNullList<ItemStack> nonnulllist = NonNullList.func_191197_a(inv.getSizeInventory(), ItemStack.field_190927_a);
        for (int i2 = 0; i2 < nonnulllist.size(); ++i2) {
            ItemStack itemstack = inv.getStackInSlot(i2);
            if (!(itemstack.getItem() instanceof ItemWrittenBook)) continue;
            ItemStack itemstack1 = itemstack.copy();
            itemstack1.func_190920_e(1);
            nonnulllist.set(i2, itemstack1);
            break;
        }
        return nonnulllist;
    }

    @Override
    public boolean func_192399_d() {
        return true;
    }

    @Override
    public boolean func_194133_a(int p_194133_1_, int p_194133_2_) {
        return p_194133_1_ >= 3 && p_194133_2_ >= 3;
    }
}

