package net.minecraft.item;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.item.EntityFireworkRocket;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFireworkCharge;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;

public class ItemFirework
extends Item {
    @Override
    public EnumActionResult onItemUse(EntityPlayer stack, World playerIn, BlockPos worldIn, EnumHand pos, EnumFacing hand, float facing, float hitX, float hitY) {
        if (!playerIn.isRemote) {
            ItemStack itemstack = stack.getHeldItem(pos);
            EntityFireworkRocket entityfireworkrocket = new EntityFireworkRocket(playerIn, (float)worldIn.getX() + facing, (float)worldIn.getY() + hitX, (float)worldIn.getZ() + hitY, itemstack);
            playerIn.spawnEntityInWorld(entityfireworkrocket);
            if (!stack.capabilities.isCreativeMode) {
                itemstack.func_190918_g(1);
            }
        }
        return EnumActionResult.SUCCESS;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World itemStackIn, EntityPlayer worldIn, EnumHand playerIn) {
        if (worldIn.isElytraFlying()) {
            ItemStack itemstack = worldIn.getHeldItem(playerIn);
            if (!itemStackIn.isRemote) {
                EntityFireworkRocket entityfireworkrocket = new EntityFireworkRocket(itemStackIn, itemstack, worldIn);
                itemStackIn.spawnEntityInWorld(entityfireworkrocket);
                if (!worldIn.capabilities.isCreativeMode) {
                    itemstack.func_190918_g(1);
                }
            }
            return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, worldIn.getHeldItem(playerIn));
        }
        return new ActionResult<ItemStack>(EnumActionResult.PASS, worldIn.getHeldItem(playerIn));
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World playerIn, List<String> tooltip, ITooltipFlag advanced) {
        NBTTagCompound nbttagcompound = stack.getSubCompound("Fireworks");
        if (nbttagcompound != null) {
            NBTTagList nbttaglist;
            if (nbttagcompound.hasKey("Flight", 99)) {
                tooltip.add(String.valueOf(I18n.translateToLocal("item.fireworks.flight")) + " " + nbttagcompound.getByte("Flight"));
            }
            if (!(nbttaglist = nbttagcompound.getTagList("Explosions", 10)).hasNoTags()) {
                for (int i2 = 0; i2 < nbttaglist.tagCount(); ++i2) {
                    NBTTagCompound nbttagcompound1 = nbttaglist.getCompoundTagAt(i2);
                    ArrayList<String> list = Lists.newArrayList();
                    ItemFireworkCharge.addExplosionInfo(nbttagcompound1, list);
                    if (list.isEmpty()) continue;
                    for (int j2 = 1; j2 < list.size(); ++j2) {
                        list.set(j2, "  " + (String)list.get(j2));
                    }
                    tooltip.addAll(list);
                }
            }
        }
    }
}

