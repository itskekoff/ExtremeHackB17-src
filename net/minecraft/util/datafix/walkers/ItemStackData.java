package net.minecraft.util.datafix.walkers;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.datafix.DataFixesManager;
import net.minecraft.util.datafix.IDataFixer;
import net.minecraft.util.datafix.walkers.Filtered;

public class ItemStackData
extends Filtered {
    private final String[] matchingTags;

    public ItemStackData(Class<?> p_i47311_1_, String ... p_i47311_2_) {
        super(p_i47311_1_);
        this.matchingTags = p_i47311_2_;
    }

    @Override
    NBTTagCompound filteredProcess(IDataFixer fixer, NBTTagCompound compound, int versionIn) {
        String[] arrstring = this.matchingTags;
        int n2 = this.matchingTags.length;
        for (int i2 = 0; i2 < n2; ++i2) {
            String s2 = arrstring[i2];
            compound = DataFixesManager.processItemStack(fixer, compound, versionIn, s2);
        }
        return compound;
    }
}

