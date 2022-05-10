package net.minecraft.util.datafix.fixes;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.datafix.IFixableData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AddBedTileEntity
implements IFixableData {
    private static final Logger field_193842_a = LogManager.getLogger();

    @Override
    public int getFixVersion() {
        return 1125;
    }

    @Override
    public NBTTagCompound fixTagCompound(NBTTagCompound compound) {
        int i2 = 416;
        try {
            NBTTagCompound nbttagcompound = compound.getCompoundTag("Level");
            int j2 = nbttagcompound.getInteger("xPos");
            int k2 = nbttagcompound.getInteger("zPos");
            NBTTagList nbttaglist = nbttagcompound.getTagList("TileEntities", 10);
            NBTTagList nbttaglist1 = nbttagcompound.getTagList("Sections", 10);
            for (int l2 = 0; l2 < nbttaglist1.tagCount(); ++l2) {
                NBTTagCompound nbttagcompound1 = nbttaglist1.getCompoundTagAt(l2);
                byte i1 = nbttagcompound1.getByte("Y");
                byte[] abyte = nbttagcompound1.getByteArray("Blocks");
                for (int j1 = 0; j1 < abyte.length; ++j1) {
                    if (416 != (abyte[j1] & 0xFF) << 4) continue;
                    int k1 = j1 & 0xF;
                    int l1 = j1 >> 8 & 0xF;
                    int i22 = j1 >> 4 & 0xF;
                    NBTTagCompound nbttagcompound2 = new NBTTagCompound();
                    nbttagcompound2.setString("id", "bed");
                    nbttagcompound2.setInteger("x", k1 + (j2 << 4));
                    nbttagcompound2.setInteger("y", l1 + (i1 << 4));
                    nbttagcompound2.setInteger("z", i22 + (k2 << 4));
                    nbttaglist.appendTag(nbttagcompound2);
                }
            }
        }
        catch (Exception var17) {
            field_193842_a.warn("Unable to datafix Bed blocks, level format may be missing tags.");
        }
        return compound;
    }
}

