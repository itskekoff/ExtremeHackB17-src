package net.minecraft.item.crafting;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;

public class RecipesArmorDyes
implements IRecipe {
    @Override
    public boolean matches(InventoryCrafting inv, World worldIn) {
        ItemStack itemstack = ItemStack.field_190927_a;
        ArrayList<ItemStack> list = Lists.newArrayList();
        for (int i2 = 0; i2 < inv.getSizeInventory(); ++i2) {
            ItemStack itemstack1 = inv.getStackInSlot(i2);
            if (itemstack1.func_190926_b()) continue;
            if (itemstack1.getItem() instanceof ItemArmor) {
                ItemArmor itemarmor = (ItemArmor)itemstack1.getItem();
                if (itemarmor.getArmorMaterial() != ItemArmor.ArmorMaterial.LEATHER || !itemstack.func_190926_b()) {
                    return false;
                }
                itemstack = itemstack1;
                continue;
            }
            if (itemstack1.getItem() != Items.DYE) {
                return false;
            }
            list.add(itemstack1);
        }
        return !itemstack.func_190926_b() && !list.isEmpty();
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting inv) {
        ItemStack itemstack = ItemStack.field_190927_a;
        int[] aint = new int[3];
        int i2 = 0;
        int j2 = 0;
        ItemArmor itemarmor = null;
        for (int k2 = 0; k2 < inv.getSizeInventory(); ++k2) {
            ItemStack itemstack1 = inv.getStackInSlot(k2);
            if (itemstack1.func_190926_b()) continue;
            if (itemstack1.getItem() instanceof ItemArmor) {
                itemarmor = (ItemArmor)itemstack1.getItem();
                if (itemarmor.getArmorMaterial() != ItemArmor.ArmorMaterial.LEATHER || !itemstack.func_190926_b()) {
                    return ItemStack.field_190927_a;
                }
                itemstack = itemstack1.copy();
                itemstack.func_190920_e(1);
                if (!itemarmor.hasColor(itemstack1)) continue;
                int l2 = itemarmor.getColor(itemstack);
                float f2 = (float)(l2 >> 16 & 0xFF) / 255.0f;
                float f1 = (float)(l2 >> 8 & 0xFF) / 255.0f;
                float f22 = (float)(l2 & 0xFF) / 255.0f;
                i2 = (int)((float)i2 + Math.max(f2, Math.max(f1, f22)) * 255.0f);
                aint[0] = (int)((float)aint[0] + f2 * 255.0f);
                aint[1] = (int)((float)aint[1] + f1 * 255.0f);
                aint[2] = (int)((float)aint[2] + f22 * 255.0f);
                ++j2;
                continue;
            }
            if (itemstack1.getItem() != Items.DYE) {
                return ItemStack.field_190927_a;
            }
            float[] afloat = EnumDyeColor.byDyeDamage(itemstack1.getMetadata()).func_193349_f();
            int l1 = (int)(afloat[0] * 255.0f);
            int i22 = (int)(afloat[1] * 255.0f);
            int j22 = (int)(afloat[2] * 255.0f);
            i2 += Math.max(l1, Math.max(i22, j22));
            aint[0] = aint[0] + l1;
            aint[1] = aint[1] + i22;
            aint[2] = aint[2] + j22;
            ++j2;
        }
        if (itemarmor == null) {
            return ItemStack.field_190927_a;
        }
        int i1 = aint[0] / j2;
        int j1 = aint[1] / j2;
        int k1 = aint[2] / j2;
        float f3 = (float)i2 / (float)j2;
        float f4 = Math.max(i1, Math.max(j1, k1));
        i1 = (int)((float)i1 * f3 / f4);
        j1 = (int)((float)j1 * f3 / f4);
        k1 = (int)((float)k1 * f3 / f4);
        int k2 = (i1 << 8) + j1;
        k2 = (k2 << 8) + k1;
        itemarmor.setColor(itemstack, k2);
        return itemstack;
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

