package net.minecraft.entity.player;

import java.util.Arrays;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.RecipeItemHelper;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ICrashReportDetail;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.play.server.SPacketSetSlot;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ReportedException;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

public class InventoryPlayer
implements IInventory {
    public final NonNullList<ItemStack> mainInventory = NonNullList.func_191197_a(36, ItemStack.field_190927_a);
    public final NonNullList<ItemStack> armorInventory = NonNullList.func_191197_a(4, ItemStack.field_190927_a);
    public final NonNullList<ItemStack> offHandInventory = NonNullList.func_191197_a(1, ItemStack.field_190927_a);
    private final List<NonNullList<ItemStack>> allInventories = Arrays.asList(this.mainInventory, this.armorInventory, this.offHandInventory);
    public int currentItem;
    public EntityPlayer player;
    private ItemStack itemStack = ItemStack.field_190927_a;
    private int field_194017_h;

    public InventoryPlayer(EntityPlayer playerIn) {
        this.player = playerIn;
    }

    public ItemStack getCurrentItem() {
        return InventoryPlayer.isHotbar(this.currentItem) ? this.mainInventory.get(this.currentItem) : ItemStack.field_190927_a;
    }

    public static int getHotbarSize() {
        return 9;
    }

    private boolean canMergeStacks(ItemStack stack1, ItemStack stack2) {
        return !stack1.func_190926_b() && this.stackEqualExact(stack1, stack2) && stack1.isStackable() && stack1.func_190916_E() < stack1.getMaxStackSize() && stack1.func_190916_E() < this.getInventoryStackLimit();
    }

    private boolean stackEqualExact(ItemStack stack1, ItemStack stack2) {
        return stack1.getItem() == stack2.getItem() && (!stack1.getHasSubtypes() || stack1.getMetadata() == stack2.getMetadata()) && ItemStack.areItemStackTagsEqual(stack1, stack2);
    }

    public int getFirstEmptyStack() {
        for (int i2 = 0; i2 < this.mainInventory.size(); ++i2) {
            if (!this.mainInventory.get(i2).func_190926_b()) continue;
            return i2;
        }
        return -1;
    }

    public void setPickedItemStack(ItemStack stack) {
        int i2 = this.getSlotFor(stack);
        if (InventoryPlayer.isHotbar(i2)) {
            this.currentItem = i2;
        } else if (i2 == -1) {
            int j2;
            this.currentItem = this.getBestHotbarSlot();
            if (!this.mainInventory.get(this.currentItem).func_190926_b() && (j2 = this.getFirstEmptyStack()) != -1) {
                this.mainInventory.set(j2, this.mainInventory.get(this.currentItem));
            }
            this.mainInventory.set(this.currentItem, stack);
        } else {
            this.pickItem(i2);
        }
    }

    public void pickItem(int index) {
        this.currentItem = this.getBestHotbarSlot();
        ItemStack itemstack = this.mainInventory.get(this.currentItem);
        this.mainInventory.set(this.currentItem, this.mainInventory.get(index));
        this.mainInventory.set(index, itemstack);
    }

    public static boolean isHotbar(int index) {
        return index >= 0 && index < 9;
    }

    public int getSlotFor(ItemStack stack) {
        for (int i2 = 0; i2 < this.mainInventory.size(); ++i2) {
            if (this.mainInventory.get(i2).func_190926_b() || !this.stackEqualExact(stack, this.mainInventory.get(i2))) continue;
            return i2;
        }
        return -1;
    }

    public int func_194014_c(ItemStack p_194014_1_) {
        for (int i2 = 0; i2 < this.mainInventory.size(); ++i2) {
            ItemStack itemstack = this.mainInventory.get(i2);
            if (this.mainInventory.get(i2).func_190926_b() || !this.stackEqualExact(p_194014_1_, this.mainInventory.get(i2)) || this.mainInventory.get(i2).isItemDamaged() || itemstack.isItemEnchanted() || itemstack.hasDisplayName()) continue;
            return i2;
        }
        return -1;
    }

    public int getBestHotbarSlot() {
        for (int i2 = 0; i2 < 9; ++i2) {
            int j2 = (this.currentItem + i2) % 9;
            if (!this.mainInventory.get(j2).func_190926_b()) continue;
            return j2;
        }
        for (int k2 = 0; k2 < 9; ++k2) {
            int l2 = (this.currentItem + k2) % 9;
            if (this.mainInventory.get(l2).isItemEnchanted()) continue;
            return l2;
        }
        return this.currentItem;
    }

    public void changeCurrentItem(int direction) {
        if (direction > 0) {
            direction = 1;
        }
        if (direction < 0) {
            direction = -1;
        }
        this.currentItem -= direction;
        while (this.currentItem < 0) {
            this.currentItem += 9;
        }
        while (this.currentItem >= 9) {
            this.currentItem -= 9;
        }
    }

    public int clearMatchingItems(@Nullable Item itemIn, int metadataIn, int removeCount, @Nullable NBTTagCompound itemNBT) {
        int i2 = 0;
        for (int j2 = 0; j2 < this.getSizeInventory(); ++j2) {
            ItemStack itemstack = this.getStackInSlot(j2);
            if (itemstack.func_190926_b() || itemIn != null && itemstack.getItem() != itemIn || metadataIn > -1 && itemstack.getMetadata() != metadataIn || itemNBT != null && !NBTUtil.areNBTEquals(itemNBT, itemstack.getTagCompound(), true)) continue;
            int k2 = removeCount <= 0 ? itemstack.func_190916_E() : Math.min(removeCount - i2, itemstack.func_190916_E());
            i2 += k2;
            if (removeCount == 0) continue;
            itemstack.func_190918_g(k2);
            if (itemstack.func_190926_b()) {
                this.setInventorySlotContents(j2, ItemStack.field_190927_a);
            }
            if (removeCount <= 0 || i2 < removeCount) continue;
            return i2;
        }
        if (!this.itemStack.func_190926_b()) {
            if (itemIn != null && this.itemStack.getItem() != itemIn) {
                return i2;
            }
            if (metadataIn > -1 && this.itemStack.getMetadata() != metadataIn) {
                return i2;
            }
            if (itemNBT != null && !NBTUtil.areNBTEquals(itemNBT, this.itemStack.getTagCompound(), true)) {
                return i2;
            }
            int l2 = removeCount <= 0 ? this.itemStack.func_190916_E() : Math.min(removeCount - i2, this.itemStack.func_190916_E());
            i2 += l2;
            if (removeCount != 0) {
                this.itemStack.func_190918_g(l2);
                if (this.itemStack.func_190926_b()) {
                    this.itemStack = ItemStack.field_190927_a;
                }
                if (removeCount > 0 && i2 >= removeCount) {
                    return i2;
                }
            }
        }
        return i2;
    }

    private int storePartialItemStack(ItemStack itemStackIn) {
        int i2 = this.storeItemStack(itemStackIn);
        if (i2 == -1) {
            i2 = this.getFirstEmptyStack();
        }
        return i2 == -1 ? itemStackIn.func_190916_E() : this.func_191973_d(i2, itemStackIn);
    }

    private int func_191973_d(int p_191973_1_, ItemStack p_191973_2_) {
        Item item = p_191973_2_.getItem();
        int i2 = p_191973_2_.func_190916_E();
        ItemStack itemstack = this.getStackInSlot(p_191973_1_);
        if (itemstack.func_190926_b()) {
            itemstack = new ItemStack(item, 0, p_191973_2_.getMetadata());
            if (p_191973_2_.hasTagCompound()) {
                itemstack.setTagCompound(p_191973_2_.getTagCompound().copy());
            }
            this.setInventorySlotContents(p_191973_1_, itemstack);
        }
        int j2 = i2;
        if (i2 > itemstack.getMaxStackSize() - itemstack.func_190916_E()) {
            j2 = itemstack.getMaxStackSize() - itemstack.func_190916_E();
        }
        if (j2 > this.getInventoryStackLimit() - itemstack.func_190916_E()) {
            j2 = this.getInventoryStackLimit() - itemstack.func_190916_E();
        }
        if (j2 == 0) {
            return i2;
        }
        itemstack.func_190917_f(j2);
        itemstack.func_190915_d(5);
        return i2 -= j2;
    }

    public int storeItemStack(ItemStack itemStackIn) {
        if (this.canMergeStacks(this.getStackInSlot(this.currentItem), itemStackIn)) {
            return this.currentItem;
        }
        if (this.canMergeStacks(this.getStackInSlot(40), itemStackIn)) {
            return 40;
        }
        for (int i2 = 0; i2 < this.mainInventory.size(); ++i2) {
            if (!this.canMergeStacks(this.mainInventory.get(i2), itemStackIn)) continue;
            return i2;
        }
        return -1;
    }

    public void decrementAnimations() {
        for (NonNullList<ItemStack> nonnulllist : this.allInventories) {
            for (int i2 = 0; i2 < nonnulllist.size(); ++i2) {
                if (nonnulllist.get(i2).func_190926_b()) continue;
                nonnulllist.get(i2).updateAnimation(this.player.world, this.player, i2, this.currentItem == i2);
            }
        }
    }

    public boolean addItemStackToInventory(ItemStack itemStackIn) {
        return this.func_191971_c(-1, itemStackIn);
    }

    public boolean func_191971_c(int p_191971_1_, final ItemStack p_191971_2_) {
        int i2;
        block12: {
            block10: {
                block11: {
                    if (p_191971_2_.func_190926_b()) {
                        return false;
                    }
                    if (!p_191971_2_.isItemDamaged()) break block10;
                    if (p_191971_1_ == -1) {
                        p_191971_1_ = this.getFirstEmptyStack();
                    }
                    if (p_191971_1_ < 0) break block11;
                    this.mainInventory.set(p_191971_1_, p_191971_2_.copy());
                    this.mainInventory.get(p_191971_1_).func_190915_d(5);
                    p_191971_2_.func_190920_e(0);
                    return true;
                }
                if (this.player.capabilities.isCreativeMode) {
                    p_191971_2_.func_190920_e(0);
                    return true;
                }
                return false;
            }
            try {
                do {
                    i2 = p_191971_2_.func_190916_E();
                    if (p_191971_1_ == -1) {
                        p_191971_2_.func_190920_e(this.storePartialItemStack(p_191971_2_));
                        continue;
                    }
                    p_191971_2_.func_190920_e(this.func_191973_d(p_191971_1_, p_191971_2_));
                } while (!p_191971_2_.func_190926_b() && p_191971_2_.func_190916_E() < i2);
                if (p_191971_2_.func_190916_E() != i2 || !this.player.capabilities.isCreativeMode) break block12;
                p_191971_2_.func_190920_e(0);
                return true;
            }
            catch (Throwable throwable) {
                CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Adding item to inventory");
                CrashReportCategory crashreportcategory = crashreport.makeCategory("Item being added");
                crashreportcategory.addCrashSection("Item ID", Item.getIdFromItem(p_191971_2_.getItem()));
                crashreportcategory.addCrashSection("Item data", p_191971_2_.getMetadata());
                crashreportcategory.setDetail("Item name", new ICrashReportDetail<String>(){

                    @Override
                    public String call() throws Exception {
                        return p_191971_2_.getDisplayName();
                    }
                });
                throw new ReportedException(crashreport);
            }
        }
        return p_191971_2_.func_190916_E() < i2;
    }

    public void func_191975_a(World p_191975_1_, ItemStack p_191975_2_) {
        if (!p_191975_1_.isRemote) {
            while (!p_191975_2_.func_190926_b()) {
                int i2 = this.storeItemStack(p_191975_2_);
                if (i2 == -1) {
                    i2 = this.getFirstEmptyStack();
                }
                if (i2 == -1) {
                    this.player.dropItem(p_191975_2_, false);
                    break;
                }
                int j2 = p_191975_2_.getMaxStackSize() - this.getStackInSlot(i2).func_190916_E();
                if (!this.func_191971_c(i2, p_191975_2_.splitStack(j2))) continue;
                ((EntityPlayerMP)this.player).connection.sendPacket(new SPacketSetSlot(-2, i2, this.getStackInSlot(i2)));
            }
        }
    }

    @Override
    public ItemStack decrStackSize(int index, int count) {
        NonNullList<ItemStack> list = null;
        for (NonNullList<ItemStack> nonnulllist : this.allInventories) {
            if (index < nonnulllist.size()) {
                list = nonnulllist;
                break;
            }
            index -= nonnulllist.size();
        }
        return list != null && !((ItemStack)list.get(index)).func_190926_b() ? ItemStackHelper.getAndSplit(list, index, count) : ItemStack.field_190927_a;
    }

    public void deleteStack(ItemStack stack) {
        block0: for (NonNullList<ItemStack> nonnulllist : this.allInventories) {
            for (int i2 = 0; i2 < nonnulllist.size(); ++i2) {
                if (nonnulllist.get(i2) != stack) continue;
                nonnulllist.set(i2, ItemStack.field_190927_a);
                continue block0;
            }
        }
    }

    @Override
    public ItemStack removeStackFromSlot(int index) {
        NonNullList<ItemStack> nonnulllist = null;
        for (NonNullList<ItemStack> nonnulllist1 : this.allInventories) {
            if (index < nonnulllist1.size()) {
                nonnulllist = nonnulllist1;
                break;
            }
            index -= nonnulllist1.size();
        }
        if (nonnulllist != null && !((ItemStack)nonnulllist.get(index)).func_190926_b()) {
            ItemStack itemstack = nonnulllist.get(index);
            nonnulllist.set(index, ItemStack.field_190927_a);
            return itemstack;
        }
        return ItemStack.field_190927_a;
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        NonNullList<ItemStack> nonnulllist = null;
        for (NonNullList<ItemStack> nonnulllist1 : this.allInventories) {
            if (index < nonnulllist1.size()) {
                nonnulllist = nonnulllist1;
                break;
            }
            index -= nonnulllist1.size();
        }
        if (nonnulllist != null) {
            nonnulllist.set(index, stack);
        }
    }

    public float getStrVsBlock(IBlockState state) {
        float f2 = 1.0f;
        if (!this.mainInventory.get(this.currentItem).func_190926_b()) {
            f2 *= this.mainInventory.get(this.currentItem).getStrVsBlock(state);
        }
        return f2;
    }

    public NBTTagList writeToNBT(NBTTagList nbtTagListIn) {
        for (int i2 = 0; i2 < this.mainInventory.size(); ++i2) {
            if (this.mainInventory.get(i2).func_190926_b()) continue;
            NBTTagCompound nbttagcompound = new NBTTagCompound();
            nbttagcompound.setByte("Slot", (byte)i2);
            this.mainInventory.get(i2).writeToNBT(nbttagcompound);
            nbtTagListIn.appendTag(nbttagcompound);
        }
        for (int j2 = 0; j2 < this.armorInventory.size(); ++j2) {
            if (this.armorInventory.get(j2).func_190926_b()) continue;
            NBTTagCompound nbttagcompound1 = new NBTTagCompound();
            nbttagcompound1.setByte("Slot", (byte)(j2 + 100));
            this.armorInventory.get(j2).writeToNBT(nbttagcompound1);
            nbtTagListIn.appendTag(nbttagcompound1);
        }
        for (int k2 = 0; k2 < this.offHandInventory.size(); ++k2) {
            if (this.offHandInventory.get(k2).func_190926_b()) continue;
            NBTTagCompound nbttagcompound2 = new NBTTagCompound();
            nbttagcompound2.setByte("Slot", (byte)(k2 + 150));
            this.offHandInventory.get(k2).writeToNBT(nbttagcompound2);
            nbtTagListIn.appendTag(nbttagcompound2);
        }
        return nbtTagListIn;
    }

    public void readFromNBT(NBTTagList nbtTagListIn) {
        this.mainInventory.clear();
        this.armorInventory.clear();
        this.offHandInventory.clear();
        for (int i2 = 0; i2 < nbtTagListIn.tagCount(); ++i2) {
            NBTTagCompound nbttagcompound = nbtTagListIn.getCompoundTagAt(i2);
            int j2 = nbttagcompound.getByte("Slot") & 0xFF;
            ItemStack itemstack = new ItemStack(nbttagcompound);
            if (itemstack.func_190926_b()) continue;
            if (j2 >= 0 && j2 < this.mainInventory.size()) {
                this.mainInventory.set(j2, itemstack);
                continue;
            }
            if (j2 >= 100 && j2 < this.armorInventory.size() + 100) {
                this.armorInventory.set(j2 - 100, itemstack);
                continue;
            }
            if (j2 < 150 || j2 >= this.offHandInventory.size() + 150) continue;
            this.offHandInventory.set(j2 - 150, itemstack);
        }
    }

    @Override
    public int getSizeInventory() {
        return this.mainInventory.size() + this.armorInventory.size() + this.offHandInventory.size();
    }

    @Override
    public boolean func_191420_l() {
        for (ItemStack itemstack : this.mainInventory) {
            if (itemstack.func_190926_b()) continue;
            return false;
        }
        for (ItemStack itemstack1 : this.armorInventory) {
            if (itemstack1.func_190926_b()) continue;
            return false;
        }
        for (ItemStack itemstack2 : this.offHandInventory) {
            if (itemstack2.func_190926_b()) continue;
            return false;
        }
        return true;
    }

    @Override
    public ItemStack getStackInSlot(int index) {
        NonNullList<ItemStack> list = null;
        for (NonNullList<ItemStack> nonnulllist : this.allInventories) {
            if (index < nonnulllist.size()) {
                list = nonnulllist;
                break;
            }
            index -= nonnulllist.size();
        }
        return list == null ? ItemStack.field_190927_a : (ItemStack)list.get(index);
    }

    @Override
    public String getName() {
        return "container.inventory";
    }

    @Override
    public boolean hasCustomName() {
        return false;
    }

    @Override
    public ITextComponent getDisplayName() {
        return this.hasCustomName() ? new TextComponentString(this.getName()) : new TextComponentTranslation(this.getName(), new Object[0]);
    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    public boolean canHarvestBlock(IBlockState state) {
        if (state.getMaterial().isToolNotRequired()) {
            return true;
        }
        ItemStack itemstack = this.getStackInSlot(this.currentItem);
        return !itemstack.func_190926_b() ? itemstack.canHarvestBlock(state) : false;
    }

    public ItemStack armorItemInSlot(int slotIn) {
        return this.armorInventory.get(slotIn);
    }

    public void damageArmor(float damage) {
        if ((damage /= 4.0f) < 1.0f) {
            damage = 1.0f;
        }
        for (int i2 = 0; i2 < this.armorInventory.size(); ++i2) {
            ItemStack itemstack = this.armorInventory.get(i2);
            if (!(itemstack.getItem() instanceof ItemArmor)) continue;
            itemstack.damageItem((int)damage, this.player);
        }
    }

    public void dropAllItems() {
        for (List list : this.allInventories) {
            for (int i2 = 0; i2 < list.size(); ++i2) {
                ItemStack itemstack = (ItemStack)list.get(i2);
                if (itemstack.func_190926_b()) continue;
                this.player.dropItem(itemstack, true, false);
                list.set(i2, ItemStack.field_190927_a);
            }
        }
    }

    @Override
    public void markDirty() {
        ++this.field_194017_h;
    }

    public int func_194015_p() {
        return this.field_194017_h;
    }

    public void setItemStack(ItemStack itemStackIn) {
        this.itemStack = itemStackIn;
    }

    public ItemStack getItemStack() {
        return this.itemStack;
    }

    @Override
    public boolean isUsableByPlayer(EntityPlayer player) {
        if (this.player.isDead) {
            return false;
        }
        return player.getDistanceSqToEntity(this.player) <= 64.0;
    }

    public boolean hasItemStack(ItemStack itemStackIn) {
        for (List list : this.allInventories) {
            for (ItemStack itemstack : list) {
                if (itemstack.func_190926_b() || !itemstack.isItemEqual(itemStackIn)) continue;
                return true;
            }
        }
        return false;
    }

    @Override
    public void openInventory(EntityPlayer player) {
    }

    @Override
    public void closeInventory(EntityPlayer player) {
    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        return true;
    }

    public void copyInventory(InventoryPlayer playerInventory) {
        for (int i2 = 0; i2 < this.getSizeInventory(); ++i2) {
            this.setInventorySlotContents(i2, playerInventory.getStackInSlot(i2));
        }
        this.currentItem = playerInventory.currentItem;
    }

    @Override
    public int getField(int id2) {
        return 0;
    }

    @Override
    public void setField(int id2, int value) {
    }

    @Override
    public int getFieldCount() {
        return 0;
    }

    @Override
    public void clear() {
        for (List list : this.allInventories) {
            list.clear();
        }
    }

    public void func_194016_a(RecipeItemHelper p_194016_1_, boolean p_194016_2_) {
        for (ItemStack itemstack : this.mainInventory) {
            p_194016_1_.func_194112_a(itemstack);
        }
        if (p_194016_2_) {
            p_194016_1_.func_194112_a(this.offHandInventory.get(0));
        }
    }
}

