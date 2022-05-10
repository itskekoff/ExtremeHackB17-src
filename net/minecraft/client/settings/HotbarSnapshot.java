package net.minecraft.client.settings;

import java.util.ArrayList;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class HotbarSnapshot
extends ArrayList<ItemStack> {
    public static final int field_192835_a = InventoryPlayer.getHotbarSize();

    public HotbarSnapshot() {
        this.ensureCapacity(field_192835_a);
        for (int i2 = 0; i2 < field_192835_a; ++i2) {
            this.add(ItemStack.field_190927_a);
        }
    }

    public NBTTagList func_192834_a() {
        NBTTagList nbttaglist = new NBTTagList();
        for (int i2 = 0; i2 < field_192835_a; ++i2) {
            nbttaglist.appendTag(((ItemStack)this.get(i2)).writeToNBT(new NBTTagCompound()));
        }
        return nbttaglist;
    }

    public void func_192833_a(NBTTagList p_192833_1_) {
        for (int i2 = 0; i2 < field_192835_a; ++i2) {
            this.set(i2, new ItemStack(p_192833_1_.getCompoundTagAt(i2)));
        }
    }

    @Override
    public boolean isEmpty() {
        for (int i2 = 0; i2 < field_192835_a; ++i2) {
            if (((ItemStack)this.get(i2)).func_190926_b()) continue;
            return false;
        }
        return true;
    }
}

