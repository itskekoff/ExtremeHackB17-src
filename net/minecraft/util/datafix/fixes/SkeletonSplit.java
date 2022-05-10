package net.minecraft.util.datafix.fixes;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.datafix.IFixableData;

public class SkeletonSplit
implements IFixableData {
    @Override
    public int getFixVersion() {
        return 701;
    }

    @Override
    public NBTTagCompound fixTagCompound(NBTTagCompound compound) {
        String s2 = compound.getString("id");
        if ("Skeleton".equals(s2)) {
            int i2 = compound.getInteger("SkeletonType");
            if (i2 == 1) {
                compound.setString("id", "WitherSkeleton");
            } else if (i2 == 2) {
                compound.setString("id", "Stray");
            }
            compound.removeTag("SkeletonType");
        }
        return compound;
    }
}

