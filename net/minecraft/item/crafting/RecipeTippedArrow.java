package net.minecraft.item.crafting;

import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;

public class RecipeTippedArrow
implements IRecipe {
    @Override
    public boolean matches(InventoryCrafting inv, World worldIn) {
        if (inv.getWidth() == 3 && inv.getHeight() == 3) {
            for (int i2 = 0; i2 < inv.getWidth(); ++i2) {
                for (int j2 = 0; j2 < inv.getHeight(); ++j2) {
                    ItemStack itemstack = inv.getStackInRowAndColumn(i2, j2);
                    if (itemstack.func_190926_b()) {
                        return false;
                    }
                    Item item = itemstack.getItem();
                    if (!(i2 == 1 && j2 == 1 ? item != Items.LINGERING_POTION : item != Items.ARROW)) continue;
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting inv) {
        ItemStack itemstack = inv.getStackInRowAndColumn(1, 1);
        if (itemstack.getItem() != Items.LINGERING_POTION) {
            return ItemStack.field_190927_a;
        }
        ItemStack itemstack1 = new ItemStack(Items.TIPPED_ARROW, 8);
        PotionUtils.addPotionToItemStack(itemstack1, PotionUtils.getPotionFromItem(itemstack));
        PotionUtils.appendEffects(itemstack1, PotionUtils.getFullEffectsFromItem(itemstack));
        return itemstack1;
    }

    @Override
    public ItemStack getRecipeOutput() {
        return ItemStack.field_190927_a;
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(InventoryCrafting inv) {
        return NonNullList.func_191197_a(inv.getSizeInventory(), ItemStack.field_190927_a);
    }

    @Override
    public boolean func_192399_d() {
        return true;
    }

    @Override
    public boolean func_194133_a(int p_194133_1_, int p_194133_2_) {
        return p_194133_1_ >= 2 && p_194133_2_ >= 2;
    }
}

