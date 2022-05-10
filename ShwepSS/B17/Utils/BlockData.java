package ShwepSS.B17.Utils;

import ShwepSS.B17.Utils.RandomUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

public class BlockData {
    public BlockPos position;
    public EnumFacing face;

    public BlockData(BlockPos position, EnumFacing face) {
        this.position = position;
        this.face = face;
    }

    public static ItemStack makeItemColored(ItemStack item) {
        if (item == null) {
            return null;
        }
        NBTTagCompound nbt = item.stackTagCompound;
        if (nbt == null) {
            nbt = new NBTTagCompound();
            item.setTagCompound(nbt);
        }
        if (nbt != null) {
            NBTTagInt color;
            NBTTagCompound display = (NBTTagCompound)nbt.getTag("display");
            if (display == null) {
                display = new NBTTagCompound();
                nbt.setTag("display", display);
            }
            if (display != null && (color = (NBTTagInt)display.getTag("color")) == null) {
                color = new NBTTagInt(RandomUtils.nextInt(9999, 9999999));
                display.setTag("color", color);
            }
        }
        return item;
    }
}

