package net.minecraft.util.datafix.fixes;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.datafix.IFixableData;

public class MinecartEntityTypes
implements IFixableData {
    private static final List<String> MINECART_TYPE_LIST = Lists.newArrayList("MinecartRideable", "MinecartChest", "MinecartFurnace", "MinecartTNT", "MinecartSpawner", "MinecartHopper", "MinecartCommandBlock");

    @Override
    public int getFixVersion() {
        return 106;
    }

    @Override
    public NBTTagCompound fixTagCompound(NBTTagCompound compound) {
        if ("Minecart".equals(compound.getString("id"))) {
            String s2 = "MinecartRideable";
            int i2 = compound.getInteger("Type");
            if (i2 > 0 && i2 < MINECART_TYPE_LIST.size()) {
                s2 = MINECART_TYPE_LIST.get(i2);
            }
            compound.setString("id", s2);
            compound.removeTag("Type");
        }
        return compound;
    }
}

