package net.minecraft.item.crafting;

import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;

public class RecipesMapCloning
implements IRecipe {
    @Override
    public boolean matches(InventoryCrafting inv, World worldIn) {
        int i2 = 0;
        ItemStack itemstack = ItemStack.field_190927_a;
        for (int j2 = 0; j2 < inv.getSizeInventory(); ++j2) {
            ItemStack itemstack1 = inv.getStackInSlot(j2);
            if (itemstack1.func_190926_b()) continue;
            if (itemstack1.getItem() == Items.FILLED_MAP) {
                if (!itemstack.func_190926_b()) {
                    return false;
                }
                itemstack = itemstack1;
                continue;
            }
            if (itemstack1.getItem() != Items.MAP) {
                return false;
            }
            ++i2;
        }
        return !itemstack.func_190926_b() && i2 > 0;
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting inv) {
        int i2 = 0;
        ItemStack itemstack = ItemStack.field_190927_a;
        for (int j2 = 0; j2 < inv.getSizeInventory(); ++j2) {
            ItemStack itemstack1 = inv.getStackInSlot(j2);
            if (itemstack1.func_190926_b()) continue;
            if (itemstack1.getItem() == Items.FILLED_MAP) {
                if (!itemstack.func_190926_b()) {
                    return ItemStack.field_190927_a;
                }
                itemstack = itemstack1;
                continue;
            }
            if (itemstack1.getItem() != Items.MAP) {
                return ItemStack.field_190927_a;
            }
            ++i2;
        }
        if (!itemstack.func_190926_b() && i2 >= 1) {
            ItemStack itemstack2 = new ItemStack(Items.FILLED_MAP, i2 + 1, itemstack.getMetadata());
            if (itemstack.hasDisplayName()) {
                itemstack2.setStackDisplayName(itemstack.getDisplayName());
            }
            if (itemstack.hasTagCompound()) {
                itemstack2.setTagCompound(itemstack.getTagCompound());
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
            if (!itemstack.getItem().hasContainerItem()) continue;
            nonnulllist.set(i2, new ItemStack(itemstack.getItem().getContainerItem()));
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

