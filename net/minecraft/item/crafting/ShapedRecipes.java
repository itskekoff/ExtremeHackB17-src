package net.minecraft.item.crafting;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class ShapedRecipes
implements IRecipe {
    private final int recipeWidth;
    private final int recipeHeight;
    private final NonNullList<Ingredient> recipeItems;
    private final ItemStack recipeOutput;
    private final String field_194137_e;

    public ShapedRecipes(String p_i47501_1_, int p_i47501_2_, int p_i47501_3_, NonNullList<Ingredient> p_i47501_4_, ItemStack p_i47501_5_) {
        this.field_194137_e = p_i47501_1_;
        this.recipeWidth = p_i47501_2_;
        this.recipeHeight = p_i47501_3_;
        this.recipeItems = p_i47501_4_;
        this.recipeOutput = p_i47501_5_;
    }

    @Override
    public String func_193358_e() {
        return this.field_194137_e;
    }

    @Override
    public ItemStack getRecipeOutput() {
        return this.recipeOutput;
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
    public NonNullList<Ingredient> func_192400_c() {
        return this.recipeItems;
    }

    @Override
    public boolean func_194133_a(int p_194133_1_, int p_194133_2_) {
        return p_194133_1_ >= this.recipeWidth && p_194133_2_ >= this.recipeHeight;
    }

    @Override
    public boolean matches(InventoryCrafting inv, World worldIn) {
        for (int i2 = 0; i2 <= 3 - this.recipeWidth; ++i2) {
            for (int j2 = 0; j2 <= 3 - this.recipeHeight; ++j2) {
                if (this.checkMatch(inv, i2, j2, true)) {
                    return true;
                }
                if (!this.checkMatch(inv, i2, j2, false)) continue;
                return true;
            }
        }
        return false;
    }

    private boolean checkMatch(InventoryCrafting p_77573_1_, int p_77573_2_, int p_77573_3_, boolean p_77573_4_) {
        for (int i2 = 0; i2 < 3; ++i2) {
            for (int j2 = 0; j2 < 3; ++j2) {
                int k2 = i2 - p_77573_2_;
                int l2 = j2 - p_77573_3_;
                Ingredient ingredient = Ingredient.field_193370_a;
                if (k2 >= 0 && l2 >= 0 && k2 < this.recipeWidth && l2 < this.recipeHeight) {
                    ingredient = p_77573_4_ ? this.recipeItems.get(this.recipeWidth - k2 - 1 + l2 * this.recipeWidth) : this.recipeItems.get(k2 + l2 * this.recipeWidth);
                }
                if (ingredient.apply(p_77573_1_.getStackInRowAndColumn(i2, j2))) continue;
                return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting inv) {
        return this.getRecipeOutput().copy();
    }

    public int func_192403_f() {
        return this.recipeWidth;
    }

    public int func_192404_g() {
        return this.recipeHeight;
    }

    public static ShapedRecipes func_193362_a(JsonObject p_193362_0_) {
        String s2 = JsonUtils.getString(p_193362_0_, "group", "");
        Map<String, Ingredient> map = ShapedRecipes.func_192408_a(JsonUtils.getJsonObject(p_193362_0_, "key"));
        String[] astring = ShapedRecipes.func_194134_a(ShapedRecipes.func_192407_a(JsonUtils.getJsonArray(p_193362_0_, "pattern")));
        int i2 = astring[0].length();
        int j2 = astring.length;
        NonNullList<Ingredient> nonnulllist = ShapedRecipes.func_192402_a(astring, map, i2, j2);
        ItemStack itemstack = ShapedRecipes.func_192405_a(JsonUtils.getJsonObject(p_193362_0_, "result"), true);
        return new ShapedRecipes(s2, i2, j2, nonnulllist, itemstack);
    }

    private static NonNullList<Ingredient> func_192402_a(String[] p_192402_0_, Map<String, Ingredient> p_192402_1_, int p_192402_2_, int p_192402_3_) {
        NonNullList<Ingredient> nonnulllist = NonNullList.func_191197_a(p_192402_2_ * p_192402_3_, Ingredient.field_193370_a);
        HashSet<String> set = Sets.newHashSet(p_192402_1_.keySet());
        set.remove(" ");
        for (int i2 = 0; i2 < p_192402_0_.length; ++i2) {
            for (int j2 = 0; j2 < p_192402_0_[i2].length(); ++j2) {
                String s2 = p_192402_0_[i2].substring(j2, j2 + 1);
                Ingredient ingredient = p_192402_1_.get(s2);
                if (ingredient == null) {
                    throw new JsonSyntaxException("Pattern references symbol '" + s2 + "' but it's not defined in the key");
                }
                set.remove(s2);
                nonnulllist.set(j2 + p_192402_2_ * i2, ingredient);
            }
        }
        if (!set.isEmpty()) {
            throw new JsonSyntaxException("Key defines symbols that aren't used in pattern: " + set);
        }
        return nonnulllist;
    }

    @VisibleForTesting
    static String[] func_194134_a(String ... p_194134_0_) {
        int i2 = Integer.MAX_VALUE;
        int j2 = 0;
        int k2 = 0;
        int l2 = 0;
        for (int i1 = 0; i1 < p_194134_0_.length; ++i1) {
            String s2 = p_194134_0_[i1];
            i2 = Math.min(i2, ShapedRecipes.func_194135_a(s2));
            int j1 = ShapedRecipes.func_194136_b(s2);
            j2 = Math.max(j2, j1);
            if (j1 < 0) {
                if (k2 == i1) {
                    ++k2;
                }
                ++l2;
                continue;
            }
            l2 = 0;
        }
        if (p_194134_0_.length == l2) {
            return new String[0];
        }
        String[] astring = new String[p_194134_0_.length - l2 - k2];
        for (int k1 = 0; k1 < astring.length; ++k1) {
            astring[k1] = p_194134_0_[k1 + k2].substring(i2, j2 + 1);
        }
        return astring;
    }

    private static int func_194135_a(String p_194135_0_) {
        int i2;
        for (i2 = 0; i2 < p_194135_0_.length() && p_194135_0_.charAt(i2) == ' '; ++i2) {
        }
        return i2;
    }

    private static int func_194136_b(String p_194136_0_) {
        int i2;
        for (i2 = p_194136_0_.length() - 1; i2 >= 0 && p_194136_0_.charAt(i2) == ' '; --i2) {
        }
        return i2;
    }

    private static String[] func_192407_a(JsonArray p_192407_0_) {
        String[] astring = new String[p_192407_0_.size()];
        if (astring.length > 3) {
            throw new JsonSyntaxException("Invalid pattern: too many rows, 3 is maximum");
        }
        if (astring.length == 0) {
            throw new JsonSyntaxException("Invalid pattern: empty pattern not allowed");
        }
        for (int i2 = 0; i2 < astring.length; ++i2) {
            String s2 = JsonUtils.getString(p_192407_0_.get(i2), "pattern[" + i2 + "]");
            if (s2.length() > 3) {
                throw new JsonSyntaxException("Invalid pattern: too many columns, 3 is maximum");
            }
            if (i2 > 0 && astring[0].length() != s2.length()) {
                throw new JsonSyntaxException("Invalid pattern: each row must be the same width");
            }
            astring[i2] = s2;
        }
        return astring;
    }

    private static Map<String, Ingredient> func_192408_a(JsonObject p_192408_0_) {
        HashMap<String, Ingredient> map = Maps.newHashMap();
        for (Map.Entry<String, JsonElement> entry : p_192408_0_.entrySet()) {
            if (entry.getKey().length() != 1) {
                throw new JsonSyntaxException("Invalid key entry: '" + entry.getKey() + "' is an invalid symbol (must be 1 character only).");
            }
            if (" ".equals(entry.getKey())) {
                throw new JsonSyntaxException("Invalid key entry: ' ' is a reserved symbol.");
            }
            map.put(entry.getKey(), ShapedRecipes.func_193361_a(entry.getValue()));
        }
        map.put(" ", Ingredient.field_193370_a);
        return map;
    }

    public static Ingredient func_193361_a(@Nullable JsonElement p_193361_0_) {
        if (p_193361_0_ != null && !p_193361_0_.isJsonNull()) {
            if (p_193361_0_.isJsonObject()) {
                return Ingredient.func_193369_a(ShapedRecipes.func_192405_a(p_193361_0_.getAsJsonObject(), false));
            }
            if (!p_193361_0_.isJsonArray()) {
                throw new JsonSyntaxException("Expected item to be object or array of objects");
            }
            JsonArray jsonarray = p_193361_0_.getAsJsonArray();
            if (jsonarray.size() == 0) {
                throw new JsonSyntaxException("Item array cannot be empty, at least one item must be defined");
            }
            ItemStack[] aitemstack = new ItemStack[jsonarray.size()];
            for (int i2 = 0; i2 < jsonarray.size(); ++i2) {
                aitemstack[i2] = ShapedRecipes.func_192405_a(JsonUtils.getJsonObject(jsonarray.get(i2), "item"), false);
            }
            return Ingredient.func_193369_a(aitemstack);
        }
        throw new JsonSyntaxException("Item cannot be null");
    }

    public static ItemStack func_192405_a(JsonObject p_192405_0_, boolean p_192405_1_) {
        String s2 = JsonUtils.getString(p_192405_0_, "item");
        Item item = Item.REGISTRY.getObject(new ResourceLocation(s2));
        if (item == null) {
            throw new JsonSyntaxException("Unknown item '" + s2 + "'");
        }
        if (item.getHasSubtypes() && !p_192405_0_.has("data")) {
            throw new JsonParseException("Missing data for item '" + s2 + "'");
        }
        int i2 = JsonUtils.getInt(p_192405_0_, "data", 0);
        int j2 = p_192405_1_ ? JsonUtils.getInt(p_192405_0_, "count", 1) : 1;
        return new ItemStack(item, j2, i2);
    }
}

