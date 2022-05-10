package net.minecraft.util.math;

import net.minecraft.nbt.NBTTagFloat;
import net.minecraft.nbt.NBTTagList;

public class Rotations {
    protected final float x;
    protected final float y;
    protected final float z;

    public Rotations(float x2, float y2, float z2) {
        this.x = !Float.isInfinite(x2) && !Float.isNaN(x2) ? x2 % 360.0f : 0.0f;
        this.y = !Float.isInfinite(y2) && !Float.isNaN(y2) ? y2 % 360.0f : 0.0f;
        this.z = !Float.isInfinite(z2) && !Float.isNaN(z2) ? z2 % 360.0f : 0.0f;
    }

    public Rotations(NBTTagList nbt) {
        this(nbt.getFloatAt(0), nbt.getFloatAt(1), nbt.getFloatAt(2));
    }

    public NBTTagList writeToNBT() {
        NBTTagList nbttaglist = new NBTTagList();
        nbttaglist.appendTag(new NBTTagFloat(this.x));
        nbttaglist.appendTag(new NBTTagFloat(this.y));
        nbttaglist.appendTag(new NBTTagFloat(this.z));
        return nbttaglist;
    }

    public boolean equals(Object p_equals_1_) {
        if (!(p_equals_1_ instanceof Rotations)) {
            return false;
        }
        Rotations rotations = (Rotations)p_equals_1_;
        return this.x == rotations.x && this.y == rotations.y && this.z == rotations.z;
    }

    public float getX() {
        return this.x;
    }

    public float getY() {
        return this.y;
    }

    public float getZ() {
        return this.z;
    }
}

