package net.minecraft.util.datafix.walkers;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.datafix.FixTypes;
import net.minecraft.util.datafix.IDataFixer;
import net.minecraft.util.datafix.IDataWalker;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EntityTag
implements IDataWalker {
    private static final Logger LOGGER = LogManager.getLogger();

    @Override
    public NBTTagCompound process(IDataFixer fixer, NBTTagCompound compound, int versionIn) {
        NBTTagCompound nbttagcompound = compound.getCompoundTag("tag");
        if (nbttagcompound.hasKey("EntityTag", 10)) {
            boolean flag;
            String s1;
            NBTTagCompound nbttagcompound1 = nbttagcompound.getCompoundTag("EntityTag");
            String s2 = compound.getString("id");
            if ("minecraft:armor_stand".equals(s2)) {
                s1 = versionIn < 515 ? "ArmorStand" : "minecraft:armor_stand";
            } else {
                if (!"minecraft:spawn_egg".equals(s2)) {
                    return compound;
                }
                s1 = nbttagcompound1.getString("id");
            }
            if (s1 == null) {
                LOGGER.warn("Unable to resolve Entity for ItemInstance: {}", (Object)s2);
                flag = false;
            } else {
                flag = !nbttagcompound1.hasKey("id", 8);
                nbttagcompound1.setString("id", s1);
            }
            fixer.process(FixTypes.ENTITY, nbttagcompound1, versionIn);
            if (flag) {
                nbttagcompound1.removeTag("id");
            }
        }
        return compound;
    }
}

