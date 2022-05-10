package net.minecraft.util.datafix.fixes;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.StringUtils;
import net.minecraft.util.datafix.IFixableData;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

public class SignStrictJSON
implements IFixableData {
    public static final Gson GSON_INSTANCE = new GsonBuilder().registerTypeAdapter((Type)((Object)ITextComponent.class), new JsonDeserializer<ITextComponent>(){

        @Override
        public ITextComponent deserialize(JsonElement p_deserialize_1_, Type p_deserialize_2_, JsonDeserializationContext p_deserialize_3_) throws JsonParseException {
            if (p_deserialize_1_.isJsonPrimitive()) {
                return new TextComponentString(p_deserialize_1_.getAsString());
            }
            if (p_deserialize_1_.isJsonArray()) {
                JsonArray jsonarray = p_deserialize_1_.getAsJsonArray();
                ITextComponent itextcomponent = null;
                for (JsonElement jsonelement : jsonarray) {
                    ITextComponent itextcomponent1 = this.deserialize(jsonelement, jsonelement.getClass(), p_deserialize_3_);
                    if (itextcomponent == null) {
                        itextcomponent = itextcomponent1;
                        continue;
                    }
                    itextcomponent.appendSibling(itextcomponent1);
                }
                return itextcomponent;
            }
            throw new JsonParseException("Don't know how to turn " + p_deserialize_1_ + " into a Component");
        }
    }).create();

    @Override
    public int getFixVersion() {
        return 101;
    }

    @Override
    public NBTTagCompound fixTagCompound(NBTTagCompound compound) {
        if ("Sign".equals(compound.getString("id"))) {
            this.updateLine(compound, "Text1");
            this.updateLine(compound, "Text2");
            this.updateLine(compound, "Text3");
            this.updateLine(compound, "Text4");
        }
        return compound;
    }

    private void updateLine(NBTTagCompound compound, String key) {
        String s2 = compound.getString(key);
        ITextComponent itextcomponent = null;
        if (!"null".equals(s2) && !StringUtils.isNullOrEmpty(s2)) {
            if (s2.charAt(0) == '\"' && s2.charAt(s2.length() - 1) == '\"' || s2.charAt(0) == '{' && s2.charAt(s2.length() - 1) == '}') {
                try {
                    itextcomponent = JsonUtils.gsonDeserialize(GSON_INSTANCE, s2, ITextComponent.class, true);
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
        compound.setString(key, ITextComponent.Serializer.componentToJson(itextcomponent));
    }
}

