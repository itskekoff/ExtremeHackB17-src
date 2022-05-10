package net.minecraft.util;

import net.minecraft.util.math.MathHelper;
import org.apache.commons.lang3.Validate;

public class BitArray {
    private final long[] longArray;
    private final int bitsPerEntry;
    private final long maxEntryValue;
    private final int arraySize;

    public BitArray(int bitsPerEntryIn, int arraySizeIn) {
        Validate.inclusiveBetween(1L, 32L, bitsPerEntryIn);
        this.arraySize = arraySizeIn;
        this.bitsPerEntry = bitsPerEntryIn;
        this.maxEntryValue = (1L << bitsPerEntryIn) - 1L;
        this.longArray = new long[MathHelper.roundUp(arraySizeIn * bitsPerEntryIn, 64) / 64];
    }

    public void setAt(int index, int value) {
        Validate.inclusiveBetween(0L, this.arraySize - 1, index);
        Validate.inclusiveBetween(0L, this.maxEntryValue, value);
        int i2 = index * this.bitsPerEntry;
        int j2 = i2 / 64;
        int k2 = ((index + 1) * this.bitsPerEntry - 1) / 64;
        int l2 = i2 % 64;
        this.longArray[j2] = this.longArray[j2] & (this.maxEntryValue << l2 ^ 0xFFFFFFFFFFFFFFFFL) | ((long)value & this.maxEntryValue) << l2;
        if (j2 != k2) {
            int i1 = 64 - l2;
            int j1 = this.bitsPerEntry - i1;
            this.longArray[k2] = this.longArray[k2] >>> j1 << j1 | ((long)value & this.maxEntryValue) >> i1;
        }
    }

    public int getAt(int index) {
        Validate.inclusiveBetween(0L, this.arraySize - 1, index);
        int i2 = index * this.bitsPerEntry;
        int j2 = i2 / 64;
        int k2 = ((index + 1) * this.bitsPerEntry - 1) / 64;
        int l2 = i2 % 64;
        if (j2 == k2) {
            return (int)(this.longArray[j2] >>> l2 & this.maxEntryValue);
        }
        int i1 = 64 - l2;
        return (int)((this.longArray[j2] >>> l2 | this.longArray[k2] << i1) & this.maxEntryValue);
    }

    public long[] getBackingLongArray() {
        return this.longArray;
    }

    public int size() {
        return this.arraySize;
    }
}

