package net.minecraft.inventory;

import java.util.Map;
import net.minecraft.block.BlockAnvil;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.InventoryCraftResult;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemEnchantedBook;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ContainerRepair
extends Container {
    private static final Logger LOGGER = LogManager.getLogger();
    private final IInventory outputSlot = new InventoryCraftResult();
    private final IInventory inputSlots = new InventoryBasic("Repair", true, 2){

        @Override
        public void markDirty() {
            super.markDirty();
            ContainerRepair.this.onCraftMatrixChanged(this);
        }
    };
    private final World theWorld;
    private final BlockPos selfPosition;
    public int maximumCost;
    private int materialCost;
    private String repairedItemName;
    private final EntityPlayer thePlayer;

    public ContainerRepair(InventoryPlayer playerInventory, World worldIn, EntityPlayer player) {
        this(playerInventory, worldIn, BlockPos.ORIGIN, player);
    }

    public ContainerRepair(InventoryPlayer playerInventory, final World worldIn, final BlockPos blockPosIn, EntityPlayer player) {
        this.selfPosition = blockPosIn;
        this.theWorld = worldIn;
        this.thePlayer = player;
        this.addSlotToContainer(new Slot(this.inputSlots, 0, 27, 47));
        this.addSlotToContainer(new Slot(this.inputSlots, 1, 76, 47));
        this.addSlotToContainer(new Slot(this.outputSlot, 2, 134, 47){

            @Override
            public boolean isItemValid(ItemStack stack) {
                return false;
            }

            @Override
            public boolean canTakeStack(EntityPlayer playerIn) {
                return (playerIn.capabilities.isCreativeMode || playerIn.experienceLevel >= ContainerRepair.this.maximumCost) && ContainerRepair.this.maximumCost > 0 && this.getHasStack();
            }

            @Override
            public ItemStack func_190901_a(EntityPlayer p_190901_1_, ItemStack p_190901_2_) {
                if (!p_190901_1_.capabilities.isCreativeMode) {
                    p_190901_1_.addExperienceLevel(-ContainerRepair.this.maximumCost);
                }
                ContainerRepair.this.inputSlots.setInventorySlotContents(0, ItemStack.field_190927_a);
                if (ContainerRepair.this.materialCost > 0) {
                    ItemStack itemstack = ContainerRepair.this.inputSlots.getStackInSlot(1);
                    if (!itemstack.func_190926_b() && itemstack.func_190916_E() > ContainerRepair.this.materialCost) {
                        itemstack.func_190918_g(ContainerRepair.this.materialCost);
                        ContainerRepair.this.inputSlots.setInventorySlotContents(1, itemstack);
                    } else {
                        ContainerRepair.this.inputSlots.setInventorySlotContents(1, ItemStack.field_190927_a);
                    }
                } else {
                    ContainerRepair.this.inputSlots.setInventorySlotContents(1, ItemStack.field_190927_a);
                }
                ContainerRepair.this.maximumCost = 0;
                IBlockState iblockstate = worldIn.getBlockState(blockPosIn);
                if (!p_190901_1_.capabilities.isCreativeMode && !worldIn.isRemote && iblockstate.getBlock() == Blocks.ANVIL && p_190901_1_.getRNG().nextFloat() < 0.12f) {
                    int l2 = iblockstate.getValue(BlockAnvil.DAMAGE);
                    if (++l2 > 2) {
                        worldIn.setBlockToAir(blockPosIn);
                        worldIn.playEvent(1029, blockPosIn, 0);
                    } else {
                        worldIn.setBlockState(blockPosIn, iblockstate.withProperty(BlockAnvil.DAMAGE, l2), 2);
                        worldIn.playEvent(1030, blockPosIn, 0);
                    }
                } else if (!worldIn.isRemote) {
                    worldIn.playEvent(1030, blockPosIn, 0);
                }
                return p_190901_2_;
            }
        });
        for (int i2 = 0; i2 < 3; ++i2) {
            for (int j2 = 0; j2 < 9; ++j2) {
                this.addSlotToContainer(new Slot(playerInventory, j2 + i2 * 9 + 9, 8 + j2 * 18, 84 + i2 * 18));
            }
        }
        for (int k2 = 0; k2 < 9; ++k2) {
            this.addSlotToContainer(new Slot(playerInventory, k2, 8 + k2 * 18, 142));
        }
    }

    @Override
    public void onCraftMatrixChanged(IInventory inventoryIn) {
        super.onCraftMatrixChanged(inventoryIn);
        if (inventoryIn == this.inputSlots) {
            this.updateRepairOutput();
        }
    }

    public void updateRepairOutput() {
        ItemStack itemstack = this.inputSlots.getStackInSlot(0);
        this.maximumCost = 1;
        int i2 = 0;
        int j2 = 0;
        int k2 = 0;
        if (itemstack.func_190926_b()) {
            this.outputSlot.setInventorySlotContents(0, ItemStack.field_190927_a);
            this.maximumCost = 0;
        } else {
            ItemStack itemstack1 = itemstack.copy();
            ItemStack itemstack2 = this.inputSlots.getStackInSlot(1);
            Map<Enchantment, Integer> map = EnchantmentHelper.getEnchantments(itemstack1);
            j2 = j2 + itemstack.getRepairCost() + (itemstack2.func_190926_b() ? 0 : itemstack2.getRepairCost());
            this.materialCost = 0;
            if (!itemstack2.func_190926_b()) {
                boolean flag;
                boolean bl2 = flag = itemstack2.getItem() == Items.ENCHANTED_BOOK && !ItemEnchantedBook.getEnchantments(itemstack2).hasNoTags();
                if (itemstack1.isItemStackDamageable() && itemstack1.getItem().getIsRepairable(itemstack, itemstack2)) {
                    int i3;
                    int l2 = Math.min(itemstack1.getItemDamage(), itemstack1.getMaxDamage() / 4);
                    if (l2 <= 0) {
                        this.outputSlot.setInventorySlotContents(0, ItemStack.field_190927_a);
                        this.maximumCost = 0;
                        return;
                    }
                    for (i3 = 0; l2 > 0 && i3 < itemstack2.func_190916_E(); ++i3) {
                        int j3 = itemstack1.getItemDamage() - l2;
                        itemstack1.setItemDamage(j3);
                        ++i2;
                        l2 = Math.min(itemstack1.getItemDamage(), itemstack1.getMaxDamage() / 4);
                    }
                    this.materialCost = i3;
                } else {
                    if (!(flag || itemstack1.getItem() == itemstack2.getItem() && itemstack1.isItemStackDamageable())) {
                        this.outputSlot.setInventorySlotContents(0, ItemStack.field_190927_a);
                        this.maximumCost = 0;
                        return;
                    }
                    if (itemstack1.isItemStackDamageable() && !flag) {
                        int l2 = itemstack.getMaxDamage() - itemstack.getItemDamage();
                        int i1 = itemstack2.getMaxDamage() - itemstack2.getItemDamage();
                        int j1 = i1 + itemstack1.getMaxDamage() * 12 / 100;
                        int k1 = l2 + j1;
                        int l1 = itemstack1.getMaxDamage() - k1;
                        if (l1 < 0) {
                            l1 = 0;
                        }
                        if (l1 < itemstack1.getMetadata()) {
                            itemstack1.setItemDamage(l1);
                            i2 += 2;
                        }
                    }
                    Map<Enchantment, Integer> map1 = EnchantmentHelper.getEnchantments(itemstack2);
                    boolean flag2 = false;
                    boolean flag3 = false;
                    for (Enchantment enchantment1 : map1.keySet()) {
                        if (enchantment1 == null) continue;
                        int i22 = map.containsKey(enchantment1) ? map.get(enchantment1) : 0;
                        int j22 = map1.get(enchantment1);
                        j22 = i22 == j22 ? j22 + 1 : Math.max(j22, i22);
                        boolean flag1 = enchantment1.canApply(itemstack);
                        if (this.thePlayer.capabilities.isCreativeMode || itemstack.getItem() == Items.ENCHANTED_BOOK) {
                            flag1 = true;
                        }
                        for (Enchantment enchantment : map.keySet()) {
                            if (enchantment == enchantment1 || enchantment1.func_191560_c(enchantment)) continue;
                            flag1 = false;
                            ++i2;
                        }
                        if (!flag1) {
                            flag3 = true;
                            continue;
                        }
                        flag2 = true;
                        if (j22 > enchantment1.getMaxLevel()) {
                            j22 = enchantment1.getMaxLevel();
                        }
                        map.put(enchantment1, j22);
                        int k3 = 0;
                        switch (enchantment1.getRarity()) {
                            case COMMON: {
                                k3 = 1;
                                break;
                            }
                            case UNCOMMON: {
                                k3 = 2;
                                break;
                            }
                            case RARE: {
                                k3 = 4;
                                break;
                            }
                            case VERY_RARE: {
                                k3 = 8;
                            }
                        }
                        if (flag) {
                            k3 = Math.max(1, k3 / 2);
                        }
                        i2 += k3 * j22;
                        if (itemstack.func_190916_E() <= 1) continue;
                        i2 = 40;
                    }
                    if (flag3 && !flag2) {
                        this.outputSlot.setInventorySlotContents(0, ItemStack.field_190927_a);
                        this.maximumCost = 0;
                        return;
                    }
                }
            }
            if (StringUtils.isBlank(this.repairedItemName)) {
                if (itemstack.hasDisplayName()) {
                    k2 = 1;
                    i2 += k2;
                    itemstack1.clearCustomName();
                }
            } else if (!this.repairedItemName.equals(itemstack.getDisplayName())) {
                k2 = 1;
                i2 += k2;
                itemstack1.setStackDisplayName(this.repairedItemName);
            }
            this.maximumCost = j2 + i2;
            if (i2 <= 0) {
                itemstack1 = ItemStack.field_190927_a;
            }
            if (k2 == i2 && k2 > 0 && this.maximumCost >= 40) {
                this.maximumCost = 39;
            }
            if (this.maximumCost >= 40 && !this.thePlayer.capabilities.isCreativeMode) {
                itemstack1 = ItemStack.field_190927_a;
            }
            if (!itemstack1.func_190926_b()) {
                int k22 = itemstack1.getRepairCost();
                if (!itemstack2.func_190926_b() && k22 < itemstack2.getRepairCost()) {
                    k22 = itemstack2.getRepairCost();
                }
                if (k2 != i2 || k2 == 0) {
                    k22 = k22 * 2 + 1;
                }
                itemstack1.setRepairCost(k22);
                EnchantmentHelper.setEnchantments(map, itemstack1);
            }
            this.outputSlot.setInventorySlotContents(0, itemstack1);
            this.detectAndSendChanges();
        }
    }

    @Override
    public void addListener(IContainerListener listener) {
        super.addListener(listener);
        listener.sendProgressBarUpdate(this, 0, this.maximumCost);
    }

    @Override
    public void updateProgressBar(int id2, int data) {
        if (id2 == 0) {
            this.maximumCost = data;
        }
    }

    @Override
    public void onContainerClosed(EntityPlayer playerIn) {
        super.onContainerClosed(playerIn);
        if (!this.theWorld.isRemote) {
            this.func_193327_a(playerIn, this.theWorld, this.inputSlots);
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        if (this.theWorld.getBlockState(this.selfPosition).getBlock() != Blocks.ANVIL) {
            return false;
        }
        return playerIn.getDistanceSq((double)this.selfPosition.getX() + 0.5, (double)this.selfPosition.getY() + 0.5, (double)this.selfPosition.getZ() + 0.5) <= 64.0;
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
        ItemStack itemstack = ItemStack.field_190927_a;
        Slot slot = (Slot)this.inventorySlots.get(index);
        if (slot != null && slot.getHasStack()) {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();
            if (index == 2) {
                if (!this.mergeItemStack(itemstack1, 3, 39, true)) {
                    return ItemStack.field_190927_a;
                }
                slot.onSlotChange(itemstack1, itemstack);
            } else if (index != 0 && index != 1 ? index >= 3 && index < 39 && !this.mergeItemStack(itemstack1, 0, 2, false) : !this.mergeItemStack(itemstack1, 3, 39, false)) {
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

    public void updateItemName(String newName) {
        this.repairedItemName = newName;
        if (this.getSlot(2).getHasStack()) {
            ItemStack itemstack = this.getSlot(2).getStack();
            if (StringUtils.isBlank(newName)) {
                itemstack.clearCustomName();
            } else {
                itemstack.setStackDisplayName(this.repairedItemName);
            }
        }
        this.updateRepairOutput();
    }
}

