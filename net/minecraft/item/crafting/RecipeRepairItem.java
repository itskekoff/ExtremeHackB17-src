package net.minecraft.item.crafting;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;

public class RecipeRepairItem
implements IRecipe {
    @Override
    public boolean matches(InventoryCrafting inv, World worldIn) {
        ArrayList<ItemStack> list = Lists.newArrayList();
        for (int i2 = 0; i2 < inv.getSizeInventory(); ++i2) {
            ItemStack itemstack = inv.getStackInSlot(i2);
            if (itemstack.func_190926_b()) continue;
            list.add(itemstack);
            if (list.size() <= 1) continue;
            ItemStack itemstack1 = (ItemStack)list.get(0);
            if (itemstack.getItem() == itemstack1.getItem() && itemstack1.func_190916_E() == 1 && itemstack.func_190916_E() == 1 && itemstack1.getItem().isDamageable()) continue;
            return false;
        }
        return list.size() == 2;
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting inv) {
        ArrayList<ItemStack> list = Lists.newArrayList();
        for (int i2 = 0; i2 < inv.getSizeInventory(); ++i2) {
            ItemStack itemstack = inv.getStackInSlot(i2);
            if (itemstack.func_190926_b()) continue;
            list.add(itemstack);
            if (list.size() <= 1) continue;
            ItemStack itemstack1 = (ItemStack)list.get(0);
            if (itemstack.getItem() == itemstack1.getItem() && itemstack1.func_190916_E() == 1 && itemstack.func_190916_E() == 1 && itemstack1.getItem().isDamageable()) continue;
            return ItemStack.field_190927_a;
        }
        if (list.size() == 2) {
            ItemStack itemstack2 = (ItemStack)list.get(0);
            ItemStack itemstack3 = (ItemStack)list.get(1);
            if (itemstack2.getItem() == itemstack3.getItem() && itemstack2.func_190916_E() == 1 && itemstack3.func_190916_E() == 1 && itemstack2.getItem().isDamageable()) {
                Item item = itemstack2.getItem();
                int j2 = item.getMaxDamage() - itemstack2.getItemDamage();
                int k2 = item.getMaxDamage() - itemstack3.getItemDamage();
                int l2 = j2 + k2 + item.getMaxDamage() * 5 / 100;
                int i1 = item.getMaxDamage() - l2;
                if (i1 < 0) {
                    i1 = 0;
                }
                return new ItemStack(itemstack2.getItem(), 1, i1);
            }
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
        return p_194133_1_ * p_194133_2_ >= 2;
    }
}

