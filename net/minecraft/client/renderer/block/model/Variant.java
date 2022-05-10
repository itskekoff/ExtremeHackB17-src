package net.minecraft.client.renderer.block.model;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import net.minecraft.client.renderer.block.model.ModelRotation;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;

public class Variant {
    private final ResourceLocation modelLocation;
    private final ModelRotation rotation;
    private final boolean uvLock;
    private final int weight;

    public Variant(ResourceLocation modelLocationIn, ModelRotation rotationIn, boolean uvLockIn, int weightIn) {
        this.modelLocation = modelLocationIn;
        this.rotation = rotationIn;
        this.uvLock = uvLockIn;
        this.weight = weightIn;
    }

    public ResourceLocation getModelLocation() {
        return this.modelLocation;
    }

    public ModelRotation getRotation() {
        return this.rotation;
    }

    public boolean isUvLock() {
        return this.uvLock;
    }

    public int getWeight() {
        return this.weight;
    }

    public String toString() {
        return "Variant{modelLocation=" + this.modelLocation + ", rotation=" + this.rotation + ", uvLock=" + this.uvLock + ", weight=" + this.weight + '}';
    }

    public boolean equals(Object p_equals_1_) {
        if (this == p_equals_1_) {
            return true;
        }
        if (!(p_equals_1_ instanceof Variant)) {
            return false;
        }
        Variant variant = (Variant)p_equals_1_;
        return this.modelLocation.equals(variant.modelLocation) && this.rotation == variant.rotation && this.uvLock == variant.uvLock && this.weight == variant.weight;
    }

    public int hashCode() {
        int i2 = this.modelLocation.hashCode();
        i2 = 31 * i2 + this.rotation.hashCode();
        i2 = 31 * i2 + Boolean.valueOf(this.uvLock).hashCode();
        i2 = 31 * i2 + this.weight;
        return i2;
    }

    public static class Deserializer
    implements JsonDeserializer<Variant> {
        @Override
        public Variant deserialize(JsonElement p_deserialize_1_, Type p_deserialize_2_, JsonDeserializationContext p_deserialize_3_) throws JsonParseException {
            JsonObject jsonobject = p_deserialize_1_.getAsJsonObject();
            String s2 = this.getStringModel(jsonobject);
            ModelRotation modelrotation = this.parseModelRotation(jsonobject);
            boolean flag = this.parseUvLock(jsonobject);
            int i2 = this.parseWeight(jsonobject);
            return new Variant(this.getResourceLocationBlock(s2), modelrotation, flag, i2);
        }

        private ResourceLocation getResourceLocationBlock(String p_188041_1_) {
            ResourceLocation resourcelocation = new ResourceLocation(p_188041_1_);
            resourcelocation = new ResourceLocation(resourcelocation.getResourceDomain(), "block/" + resourcelocation.getResourcePath());
            return resourcelocation;
        }

        private boolean parseUvLock(JsonObject json) {
            return JsonUtils.getBoolean(json, "uvlock", false);
        }

        protected ModelRotation parseModelRotation(JsonObject json) {
            int j2;
            int i2 = JsonUtils.getInt(json, "x", 0);
            ModelRotation modelrotation = ModelRotation.getModelRotation(i2, j2 = JsonUtils.getInt(json, "y", 0));
            if (modelrotation == null) {
                throw new JsonParseException("Invalid BlockModelRotation x: " + i2 + ", y: " + j2);
            }
            return modelrotation;
        }

        protected String getStringModel(JsonObject json) {
            return JsonUtils.getString(json, "model");
        }

        protected int parseWeight(JsonObject json) {
            int i2 = JsonUtils.getInt(json, "weight", 1);
            if (i2 < 1) {
                throw new JsonParseException("Invalid weight " + i2 + " found, expected integer >= 1");
            }
            return i2;
        }
    }
}

