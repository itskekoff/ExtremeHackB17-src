package net.minecraft.inventory;

import java.util.Random;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class InventoryHelper {
    private static final Random RANDOM = new Random();

    public static void dropInventoryItems(World worldIn, BlockPos pos, IInventory inventory) {
        InventoryHelper.dropInventoryItems(worldIn, pos.getX(), pos.getY(), pos.getZ(), inventory);
    }

    public static void dropInventoryItems(World worldIn, Entity entityAt, IInventory inventory) {
        InventoryHelper.dropInventoryItems(worldIn, entityAt.posX, entityAt.posY, entityAt.posZ, inventory);
    }

    private static void dropInventoryItems(World worldIn, double x2, double y2, double z2, IInventory inventory) {
        for (int i2 = 0; i2 < inventory.getSizeInventory(); ++i2) {
            ItemStack itemstack = inventory.getStackInSlot(i2);
            if (itemstack.func_190926_b()) continue;
            InventoryHelper.spawnItemStack(worldIn, x2, y2, z2, itemstack);
        }
    }

    public static void spawnItemStack(World worldIn, double x2, double y2, double z2, ItemStack stack) {
        float f2 = RANDOM.nextFloat() * 0.8f + 0.1f;
        float f1 = RANDOM.nextFloat() * 0.8f + 0.1f;
        float f22 = RANDOM.nextFloat() * 0.8f + 0.1f;
        while (!stack.func_190926_b()) {
            EntityItem entityitem = new EntityItem(worldIn, x2 + (double)f2, y2 + (double)f1, z2 + (double)f22, stack.splitStack(RANDOM.nextInt(21) + 10));
            float f3 = 0.05f;
            entityitem.motionX = RANDOM.nextGaussian() * (double)0.05f;
            entityitem.motionY = RANDOM.nextGaussian() * (double)0.05f + (double)0.2f;
            entityitem.motionZ = RANDOM.nextGaussian() * (double)0.05f;
            worldIn.spawnEntityInWorld(entityitem);
        }
    }
}

