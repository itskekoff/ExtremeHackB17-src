package net.minecraft.item.crafting;

import net.minecraft.block.Block;
import net.minecraft.block.BlockShulkerBox;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;

public class ShulkerBoxRecipes {

    public static class ShulkerBoxColoring
    implements IRecipe {
        @Override
        public boolean matches(InventoryCrafting inv, World worldIn) {
            int i2 = 0;
            int j2 = 0;
            for (int k2 = 0; k2 < inv.getSizeInventory(); ++k2) {
                ItemStack itemstack = inv.getStackInSlot(k2);
                if (itemstack.func_190926_b()) continue;
                if (Block.getBlockFromItem(itemstack.getItem()) instanceof BlockShulkerBox) {
                    ++i2;
                } else {
                    if (itemstack.getItem() != Items.DYE) {
                        return false;
                    }
                    ++j2;
                }
                if (j2 <= 1 && i2 <= 1) continue;
                return false;
            }
            return i2 == 1 && j2 == 1;
        }

        @Override
        public ItemStack getCraftingResult(InventoryCrafting inv) {
            ItemStack itemstack = ItemStack.field_190927_a;
            ItemStack itemstack1 = ItemStack.field_190927_a;
            for (int i2 = 0; i2 < inv.getSizeInventory(); ++i2) {
                ItemStack itemstack2 = inv.getStackInSlot(i2);
                if (itemstack2.func_190926_b()) continue;
                if (Block.getBlockFromItem(itemstack2.getItem()) instanceof BlockShulkerBox) {
                    itemstack = itemstack2;
                    continue;
                }
                if (itemstack2.getItem() != Items.DYE) continue;
                itemstack1 = itemstack2;
            }
            ItemStack itemstack3 = BlockShulkerBox.func_190953_b(EnumDyeColor.byDyeDamage(itemstack1.getMetadata()));
            if (itemstack.hasTagCompound()) {
                itemstack3.setTagCompound(itemstack.getTagCompound().copy());
            }
            return itemstack3;
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
}

