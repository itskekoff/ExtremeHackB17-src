package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTSizeTracker;

public class NBTTagIntArray
extends NBTBase {
    private int[] intArray;

    NBTTagIntArray() {
    }

    public NBTTagIntArray(int[] p_i45132_1_) {
        this.intArray = p_i45132_1_;
    }

    public NBTTagIntArray(List<Integer> p_i47528_1_) {
        this(NBTTagIntArray.func_193584_a(p_i47528_1_));
    }

    private static int[] func_193584_a(List<Integer> p_193584_0_) {
        int[] aint = new int[p_193584_0_.size()];
        for (int i2 = 0; i2 < p_193584_0_.size(); ++i2) {
            Integer integer = p_193584_0_.get(i2);
            aint[i2] = integer == null ? 0 : integer;
        }
        return aint;
    }

    @Override
    void write(DataOutput output) throws IOException {
        output.writeInt(this.intArray.length);
        int[] arrn = this.intArray;
        int n2 = this.intArray.length;
        for (int i2 = 0; i2 < n2; ++i2) {
            int i3 = arrn[i2];
            output.writeInt(i3);
        }
    }

    @Override
    void read(DataInput input, int depth, NBTSizeTracker sizeTracker) throws IOException {
        sizeTracker.read(192L);
        int i2 = input.readInt();
        sizeTracker.read(32 * i2);
        this.intArray = new int[i2];
        for (int j2 = 0; j2 < i2; ++j2) {
            this.intArray[j2] = input.readInt();
        }
    }

    @Override
    public byte getId() {
        return 11;
    }

    @Override
    public String toString() {
        StringBuilder stringbuilder = new StringBuilder("[I;");
        for (int i2 = 0; i2 < this.intArray.length; ++i2) {
            if (i2 != 0) {
                stringbuilder.append(',');
            }
            stringbuilder.append(this.intArray[i2]);
        }
        return stringbuilder.append(']').toString();
    }

    @Override
    public NBTTagIntArray copy() {
        int[] aint = new int[this.intArray.length];
        System.arraycopy(this.intArray, 0, aint, 0, this.intArray.length);
        return new NBTTagIntArray(aint);
    }

    @Override
    public boolean equals(Object p_equals_1_) {
        return super.equals(p_equals_1_) && Arrays.equals(this.intArray, ((NBTTagIntArray)p_equals_1_).intArray);
    }

    @Override
    public int hashCode() {
        return super.hashCode() ^ Arrays.hashCode(this.intArray);
    }

    public int[] getIntArray() {
        return this.intArray;
    }
}

