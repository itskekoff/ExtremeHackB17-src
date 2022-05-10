package net.minecraft.nbt;

public class NBTException
extends Exception {
    public NBTException(String p_i47523_1_, String p_i47523_2_, int p_i47523_3_) {
        super(String.valueOf(p_i47523_1_) + " at: " + NBTException.func_193592_a(p_i47523_2_, p_i47523_3_));
    }

    private static String func_193592_a(String p_193592_0_, int p_193592_1_) {
        StringBuilder stringbuilder = new StringBuilder();
        int i2 = Math.min(p_193592_0_.length(), p_193592_1_);
        if (i2 > 35) {
            stringbuilder.append("...");
        }
        stringbuilder.append(p_193592_0_.substring(Math.max(0, i2 - 35), i2));
        stringbuilder.append("<--[HERE]");
        return stringbuilder.toString();
    }
}

