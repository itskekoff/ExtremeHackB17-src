package net.minecraft.util.datafix;

import net.minecraft.nbt.NBTTagCompound;

public interface IFixableData {
    public int getFixVersion();

    public NBTTagCompound fixTagCompound(NBTTagCompound var1);
}

