package net.minecraft.util.datafix.fixes;

import com.google.gson.JsonParseException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.StringUtils;
import net.minecraft.util.datafix.IFixableData;
import net.minecraft.util.datafix.fixes.SignStrictJSON;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

public class BookPagesStrictJSON
implements IFixableData {
    @Override
    public int getFixVersion() {
        return 165;
    }

    @Override
    public NBTTagCompound fixTagCompound(NBTTagCompound compound) {
        NBTTagCompound nbttagcompound;
        if ("minecraft:written_book".equals(compound.getString("id")) && (nbttagcompound = compound.getCompoundTag("tag")).hasKey("pages", 9)) {
            NBTTagList nbttaglist = nbttagcompound.getTagList("pages", 8);
            for (int i2 = 0; i2 < nbttaglist.tagCount(); ++i2) {
                String s2 = nbttaglist.getStringTagAt(i2);
                ITextComponent itextcomponent = null;
                if (!"null".equals(s2) && !StringUtils.isNullOrEmpty(s2)) {
                    if (s2.charAt(0) == '\"' && s2.charAt(s2.length() - 1) == '\"' || s2.charAt(0) == '{' && s2.charAt(s2.length() - 1) == '}') {
                        try {
                            itextcomponent = JsonUtils.gsonDeserialize(SignStrictJSON.GSON_INSTANCE, s2, ITextComponent.class, true);
                            if (itextcomponent == null) {
                                itextcomponent = new TextComponentString("");
                            }
                        }
                        catch (JsonParseException jsonParseException) {
                            // empty catch block
                        }
                        if (itextcomponent == null) {
                            try {
                                itextcomponent = ITextComponent.Serializer.jsonToComponent(s2);
                            }
                            catch (JsonParseException jsonParseException) {
                                // empty catch block
                            }
                        }
                        if (itextcomponent == null) {
                            try {
                                itextcomponent = ITextComponent.Serializer.fromJsonLenient(s2);
                            }
                            catch (JsonParseException jsonParseException) {
                                // empty catch block
                            }
                        }
                        if (itextcomponent == null) {
                            itextcomponent = new TextComponentString(s2);
                        }
                    } else {
                        itextcomponent = new TextComponentString(s2);
                    }
                } else {
                    itextcomponent = new TextComponentString("");
                }
                nbttaglist.set(i2, new NBTTagString(ITextComponent.Serializer.componentToJson(itextcomponent)));
            }
            nbttagcompound.setTag("pages", nbttaglist);
        }
        return compound;
    }
}

