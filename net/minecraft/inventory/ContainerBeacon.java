package net.minecraft.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ContainerBeacon
extends Container {
    private final IInventory tileBeacon;
    private final BeaconSlot beaconSlot;

    public ContainerBeacon(IInventory playerInventory, IInventory tileBeaconIn) {
        this.tileBeacon = tileBeaconIn;
        this.beaconSlot = new BeaconSlot(tileBeaconIn, 0, 136, 110);
        this.addSlotToContainer(this.beaconSlot);
        int i2 = 36;
        int j2 = 137;
        for (int k2 = 0; k2 < 3; ++k2) {
            for (int l2 = 0; l2 < 9; ++l2) {
                this.addSlotToContainer(new Slot(playerInventory, l2 + k2 * 9 + 9, 36 + l2 * 18, 137 + k2 * 18));
            }
        }
        for (int i1 = 0; i1 < 9; ++i1) {
            this.addSlotToContainer(new Slot(playerInventory, i1, 36 + i1 * 18, 195));
        }
    }

    @Override
    public void addListener(IContainerListener listener) {
        super.addListener(listener);
        listener.sendAllWindowProperties(this, this.tileBeacon);
    }

    @Override
    public void updateProgressBar(int id2, int data) {
        this.tileBeacon.setField(id2, data);
    }

    public IInventory getTileEntity() {
        return this.tileBeacon;
    }

    @Override
    public void onContainerClosed(EntityPlayer playerIn) {
        ItemStack itemstack;
        super.onContainerClosed(playerIn);
        if (!playerIn.world.isRemote && !(itemstack = this.beaconSlot.decrStackSize(this.beaconSlot.getSlotStackLimit())).func_190926_b()) {
            playerIn.dropItem(itemstack, false);
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        return this.tileBeacon.isUsableByPlayer(playerIn);
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
        ItemStack itemstack = ItemStack.field_190927_a;
        Slot slot = (Slot)this.inventorySlots.get(index);
        if (slot != null && slot.getHasStack()) {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();
            if (index == 0) {
                if (!this.mergeItemStack(itemstack1, 1, 37, true)) {
                    return ItemStack.field_190927_a;
                }
                slot.onSlotChange(itemstack1, itemstack);
            } else if (!this.beaconSlot.getHasStack() && this.beaconSlot.isItemValid(itemstack1) && itemstack1.func_190916_E() == 1 ? !this.mergeItemStack(itemstack1, 0, 1, false) : (index >= 1 && index < 28 ? !this.mergeItemStack(itemstack1, 28, 37, false) : (index >= 28 && index < 37 ? !this.mergeItemStack(itemstack1, 1, 28, false) : !this.mergeItemStack(itemstack1, 1, 37, false)))) {
                return ItemStack.field_190927_a;
            }
            if (itemstack1.func_190926_b()) {
                slot.putStack(ItemStack.field_190927_a);
            } else {
                slot.onSlotChanged();
            }
            if (itemstack1.func_190916_E() == itemstack.func_190916_E()) {
                return ItemStack.field_190927_a;
            }
            slot.func_190901_a(playerIn, itemstack1);
        }
        return itemstack;
    }

    class BeaconSlot
    extends Slot {
        public BeaconSlot(IInventory inventoryIn, int index, int xIn, int yIn) {
            super(inventoryIn, index, xIn, yIn);
        }

        @Override
        public boolean isItemValid(ItemStack stack) {
            Item item = stack.getItem();
            return item == Items.EMERALD || item == Items.DIAMOND || item == Items.GOLD_INGOT || item == Items.IRON_INGOT;
        }

        @Override
        public int getSlotStackLimit() {
            return 1;
        }
    }
}

