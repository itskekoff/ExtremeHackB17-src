package net.minecraft.item.crafting;

import com.google.common.base.Predicate;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntComparators;
import it.unimi.dsi.fastutil.ints.IntList;
import javax.annotation.Nullable;
import net.minecraft.client.util.RecipeItemHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class Ingredient
implements Predicate<ItemStack> {
    public static final Ingredient field_193370_a = new Ingredient(new ItemStack[0]){

        @Override
        public boolean apply(@Nullable ItemStack p_apply_1_) {
            return p_apply_1_.func_190926_b();
        }
    };
    private final ItemStack[] field_193371_b;
    private IntList field_194140_c;

    private Ingredient(ItemStack ... p_i47503_1_) {
        this.field_193371_b = p_i47503_1_;
    }

    public ItemStack[] func_193365_a() {
        return this.field_193371_b;
    }

    @Override
    public boolean apply(@Nullable ItemStack p_apply_1_) {
        if (p_apply_1_ == null) {
            return false;
        }
        ItemStack[] arritemStack = this.field_193371_b;
        int n2 = this.field_193371_b.length;
        for (int i2 = 0; i2 < n2; ++i2) {
            int i3;
            ItemStack itemstack = arritemStack[i2];
            if (itemstack.getItem() != p_apply_1_.getItem() || (i3 = itemstack.getMetadata()) != 32767 && i3 != p_apply_1_.getMetadata()) continue;
            return true;
        }
        return false;
    }

    public IntList func_194139_b() {
        if (this.field_194140_c == null) {
            this.field_194140_c = new IntArrayList(this.field_193371_b.length);
            ItemStack[] arritemStack = this.field_193371_b;
            int n2 = this.field_193371_b.length;
            for (int i2 = 0; i2 < n2; ++i2) {
                ItemStack itemstack = arritemStack[i2];
                this.field_194140_c.add(RecipeItemHelper.func_194113_b(itemstack));
            }
            this.field_194140_c.sort(IntComparators.NATURAL_COMPARATOR);
        }
        return this.field_194140_c;
    }

    public static Ingredient func_193367_a(Item p_193367_0_) {
        return Ingredient.func_193369_a(new ItemStack(p_193367_0_, 1, 32767));
    }

    public static Ingredient func_193368_a(Item ... p_193368_0_) {
        ItemStack[] aitemstack = new ItemStack[p_193368_0_.length];
        for (int i2 = 0; i2 < p_193368_0_.length; ++i2) {
            aitemstack[i2] = new ItemStack(p_193368_0_[i2]);
        }
        return Ingredient.func_193369_a(aitemstack);
    }

    public static Ingredient func_193369_a(ItemStack ... p_193369_0_) {
        if (p_193369_0_.length > 0) {
            ItemStack[] arritemStack = p_193369_0_;
            int n2 = p_193369_0_.length;
            for (int i2 = 0; i2 < n2; ++i2) {
                ItemStack itemstack = arritemStack[i2];
                if (itemstack.func_190926_b()) continue;
                return new Ingredient(p_193369_0_);
            }
        }
        return field_193370_a;
    }

    /* synthetic */ Ingredient(ItemStack[] arritemStack, Ingredient ingredient) {
        this(arritemStack);
    }
}

