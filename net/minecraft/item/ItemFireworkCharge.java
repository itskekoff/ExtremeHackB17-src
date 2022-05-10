package net.minecraft.item;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;

public class ItemFireworkCharge
extends Item {
    public static NBTBase getExplosionTag(ItemStack stack, String key) {
        NBTTagCompound nbttagcompound;
        if (stack.hasTagCompound() && (nbttagcompound = stack.getTagCompound().getCompoundTag("Explosion")) != null) {
            return nbttagcompound.getTag(key);
        }
        return null;
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World playerIn, List<String> tooltip, ITooltipFlag advanced) {
        NBTTagCompound nbttagcompound;
        if (stack.hasTagCompound() && (nbttagcompound = stack.getTagCompound().getCompoundTag("Explosion")) != null) {
            ItemFireworkCharge.addExplosionInfo(nbttagcompound, tooltip);
        }
    }

    public static void addExplosionInfo(NBTTagCompound nbt, List<String> tooltip) {
        boolean flag4;
        boolean flag3;
        int[] aint1;
        int n2;
        byte b0 = nbt.getByte("Type");
        if (b0 >= 0 && b0 <= 4) {
            tooltip.add(I18n.translateToLocal("item.fireworksCharge.type." + b0).trim());
        } else {
            tooltip.add(I18n.translateToLocal("item.fireworksCharge.type").trim());
        }
        int[] aint = nbt.getIntArray("Colors");
        if (aint.length > 0) {
            boolean flag = true;
            String s2 = "";
            int[] arrn = aint;
            n2 = aint.length;
            for (int i2 = 0; i2 < n2; ++i2) {
                int i3 = arrn[i2];
                if (!flag) {
                    s2 = String.valueOf(s2) + ", ";
                }
                flag = false;
                boolean flag1 = false;
                for (int j2 = 0; j2 < ItemDye.DYE_COLORS.length; ++j2) {
                    if (i3 != ItemDye.DYE_COLORS[j2]) continue;
                    flag1 = true;
                    s2 = String.valueOf(s2) + I18n.translateToLocal("item.fireworksCharge." + EnumDyeColor.byDyeDamage(j2).getUnlocalizedName());
                    break;
                }
                if (flag1) continue;
                s2 = String.valueOf(s2) + I18n.translateToLocal("item.fireworksCharge.customColor");
            }
            tooltip.add(s2);
        }
        if ((aint1 = nbt.getIntArray("FadeColors")).length > 0) {
            boolean flag2 = true;
            String s1 = String.valueOf(I18n.translateToLocal("item.fireworksCharge.fadeTo")) + " ";
            int[] arrn = aint1;
            int n3 = aint1.length;
            for (n2 = 0; n2 < n3; ++n2) {
                int l2 = arrn[n2];
                if (!flag2) {
                    s1 = String.valueOf(s1) + ", ";
                }
                flag2 = false;
                boolean flag5 = false;
                for (int k2 = 0; k2 < 16; ++k2) {
                    if (l2 != ItemDye.DYE_COLORS[k2]) continue;
                    flag5 = true;
                    s1 = String.valueOf(s1) + I18n.translateToLocal("item.fireworksCharge." + EnumDyeColor.byDyeDamage(k2).getUnlocalizedName());
                    break;
                }
                if (flag5) continue;
                s1 = String.valueOf(s1) + I18n.translateToLocal("item.fireworksCharge.customColor");
            }
            tooltip.add(s1);
        }
        if (flag3 = nbt.getBoolean("Trail")) {
            tooltip.add(I18n.translateToLocal("item.fireworksCharge.trail"));
        }
        if (flag4 = nbt.getBoolean("Flicker")) {
            tooltip.add(I18n.translateToLocal("item.fireworksCharge.flicker"));
        }
    }
}

