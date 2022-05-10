package net.minecraft.item.crafting;

import javax.annotation.Nullable;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemBanner;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.BannerPattern;
import net.minecraft.tileentity.TileEntityBanner;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;

public class RecipesBanners {

    public static class RecipeAddPattern
    implements IRecipe {
        @Override
        public boolean matches(InventoryCrafting inv, World worldIn) {
            boolean flag = false;
            for (int i2 = 0; i2 < inv.getSizeInventory(); ++i2) {
                ItemStack itemstack = inv.getStackInSlot(i2);
                if (itemstack.getItem() != Items.BANNER) continue;
                if (flag) {
                    return false;
                }
                if (TileEntityBanner.getPatterns(itemstack) >= 6) {
                    return false;
                }
                flag = true;
            }
            if (!flag) {
                return false;
            }
            return this.func_190933_c(inv) != null;
        }

        @Override
        public ItemStack getCraftingResult(InventoryCrafting inv) {
            ItemStack itemstack = ItemStack.field_190927_a;
            for (int i2 = 0; i2 < inv.getSizeInventory(); ++i2) {
                ItemStack itemstack1 = inv.getStackInSlot(i2);
                if (itemstack1.func_190926_b() || itemstack1.getItem() != Items.BANNER) continue;
                itemstack = itemstack1.copy();
                itemstack.func_190920_e(1);
                break;
            }
            BannerPattern bannerpattern = this.func_190933_c(inv);
            if (bannerpattern != null) {
                NBTTagList nbttaglist;
                int k2 = 0;
                for (int j2 = 0; j2 < inv.getSizeInventory(); ++j2) {
                    ItemStack itemstack2 = inv.getStackInSlot(j2);
                    if (itemstack2.getItem() != Items.DYE) continue;
                    k2 = itemstack2.getMetadata();
                    break;
                }
                NBTTagCompound nbttagcompound1 = itemstack.func_190925_c("BlockEntityTag");
                if (nbttagcompound1.hasKey("Patterns", 9)) {
                    nbttaglist = nbttagcompound1.getTagList("Patterns", 10);
                } else {
                    nbttaglist = new NBTTagList();
                    nbttagcompound1.setTag("Patterns", nbttaglist);
                }
                NBTTagCompound nbttagcompound = new NBTTagCompound();
                nbttagcompound.setString("Pattern", bannerpattern.func_190993_b());
                nbttagcompound.setInteger("Color", k2);
                nbttaglist.appendTag(nbttagcompound);
            }
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

        /*
         * Unable to fully structure code
         * Enabled aggressive block sorting
         * Lifted jumps to return sites
         */
        @Nullable
        private BannerPattern func_190933_c(InventoryCrafting p_190933_1_) {
            for (BannerPattern bannerpattern : BannerPattern.values()) {
                block19: {
                    block18: {
                        block17: {
                            if (!bannerpattern.func_191000_d()) continue;
                            flag = true;
                            if (!bannerpattern.func_190999_e()) break block17;
                            flag1 = false;
                            flag2 = false;
                            i = 0;
                            ** GOTO lbl30
                        }
                        if (p_190933_1_.getSizeInventory() != bannerpattern.func_190996_c().length * bannerpattern.func_190996_c()[0].length()) break block18;
                        j = -1;
                        k = 0;
                        ** GOTO lbl53
                    }
                    flag = false;
                    break block19;
lbl-1000:
                    // 1 sources

                    {
                        itemstack = p_190933_1_.getStackInSlot(i);
                        if (!itemstack.func_190926_b() && itemstack.getItem() != Items.BANNER) {
                            if (itemstack.getItem() == Items.DYE) {
                                if (flag2) {
                                    flag = false;
                                    break;
                                }
                                flag2 = true;
                            } else {
                                if (flag1 || !itemstack.isItemEqual(bannerpattern.func_190998_f())) {
                                    flag = false;
                                    break;
                                }
                                flag1 = true;
                            }
                        }
                        ++i;
lbl30:
                        // 2 sources

                        ** while (i < p_190933_1_.getSizeInventory() && flag)
                    }
lbl31:
                    // 3 sources

                    if (flag1 && flag2) break block19;
                    flag = false;
                    break block19;
lbl-1000:
                    // 1 sources

                    {
                        l = k / 3;
                        i1 = k % 3;
                        itemstack1 = p_190933_1_.getStackInSlot(k);
                        if (!itemstack1.func_190926_b() && itemstack1.getItem() != Items.BANNER) {
                            if (itemstack1.getItem() != Items.DYE) {
                                flag = false;
                                break;
                            }
                            if (j != -1 && j != itemstack1.getMetadata()) {
                                flag = false;
                                break;
                            }
                            if (bannerpattern.func_190996_c()[l].charAt(i1) == ' ') {
                                flag = false;
                                break;
                            }
                            j = itemstack1.getMetadata();
                        } else if (bannerpattern.func_190996_c()[l].charAt(i1) != ' ') {
                            flag = false;
                            break;
                        }
                        ++k;
lbl53:
                        // 2 sources

                        ** while (k < p_190933_1_.getSizeInventory() && flag)
                    }
                }
                if (!flag) continue;
                return bannerpattern;
            }
            return null;
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

    public static class RecipeDuplicatePattern
    implements IRecipe {
        @Override
        public boolean matches(InventoryCrafting inv, World worldIn) {
            ItemStack itemstack = ItemStack.field_190927_a;
            ItemStack itemstack1 = ItemStack.field_190927_a;
            for (int i2 = 0; i2 < inv.getSizeInventory(); ++i2) {
                boolean flag;
                ItemStack itemstack2 = inv.getStackInSlot(i2);
                if (itemstack2.func_190926_b()) continue;
                if (itemstack2.getItem() != Items.BANNER) {
                    return false;
                }
                if (!itemstack.func_190926_b() && !itemstack1.func_190926_b()) {
                    return false;
                }
                EnumDyeColor enumdyecolor = ItemBanner.getBaseColor(itemstack2);
                boolean bl2 = flag = TileEntityBanner.getPatterns(itemstack2) > 0;
                if (!itemstack.func_190926_b()) {
                    if (flag) {
                        return false;
                    }
                    if (enumdyecolor != ItemBanner.getBaseColor(itemstack)) {
                        return false;
                    }
                    itemstack1 = itemstack2;
                    continue;
                }
                if (!itemstack1.func_190926_b()) {
                    if (!flag) {
                        return false;
                    }
                    if (enumdyecolor != ItemBanner.getBaseColor(itemstack1)) {
                        return false;
                    }
                    itemstack = itemstack2;
                    continue;
                }
                if (flag) {
                    itemstack = itemstack2;
                    continue;
                }
                itemstack1 = itemstack2;
            }
            return !itemstack.func_190926_b() && !itemstack1.func_190926_b();
        }

        @Override
        public ItemStack getCraftingResult(InventoryCrafting inv) {
            for (int i2 = 0; i2 < inv.getSizeInventory(); ++i2) {
                ItemStack itemstack = inv.getStackInSlot(i2);
                if (itemstack.func_190926_b() || TileEntityBanner.getPatterns(itemstack) <= 0) continue;
                ItemStack itemstack1 = itemstack.copy();
                itemstack1.func_190920_e(1);
                return itemstack1;
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
                if (itemstack.func_190926_b()) continue;
                if (itemstack.getItem().hasContainerItem()) {
                    nonnulllist.set(i2, new ItemStack(itemstack.getItem().getContainerItem()));
                    continue;
                }
                if (!itemstack.hasTagCompound() || TileEntityBanner.getPatterns(itemstack) <= 0) continue;
                ItemStack itemstack1 = itemstack.copy();
                itemstack1.func_190920_e(1);
                nonnulllist.set(i2, itemstack1);
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

