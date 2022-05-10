package net.minecraft.client.renderer.block.model;

import java.util.Locale;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.StringUtils;

public class ModelResourceLocation
extends ResourceLocation {
    private final String variant;

    protected ModelResourceLocation(int unused, String ... resourceName) {
        super(0, resourceName[0], resourceName[1]);
        this.variant = StringUtils.isEmpty(resourceName[2]) ? "normal" : resourceName[2].toLowerCase(Locale.ROOT);
    }

    public ModelResourceLocation(String pathIn) {
        this(0, ModelResourceLocation.parsePathString(pathIn));
    }

    public ModelResourceLocation(ResourceLocation location, String variantIn) {
        this(location.toString(), variantIn);
    }

    public ModelResourceLocation(String location, String variantIn) {
        this(0, ModelResourceLocation.parsePathString(String.valueOf(location) + '#' + (variantIn == null ? "normal" : variantIn)));
    }

    protected static String[] parsePathString(String pathIn) {
        String[] arrstring = new String[3];
        arrstring[1] = pathIn;
        String[] astring = arrstring;
        int i2 = pathIn.indexOf(35);
        String s2 = pathIn;
        if (i2 >= 0) {
            astring[2] = pathIn.substring(i2 + 1, pathIn.length());
            if (i2 > 1) {
                s2 = pathIn.substring(0, i2);
            }
        }
        System.arraycopy(ResourceLocation.splitObjectName(s2), 0, astring, 0, 2);
        return astring;
    }

    public String getVariant() {
        return this.variant;
    }

    @Override
    public boolean equals(Object p_equals_1_) {
        if (this == p_equals_1_) {
            return true;
        }
        if (p_equals_1_ instanceof ModelResourceLocation && super.equals(p_equals_1_)) {
            ModelResourceLocation modelresourcelocation = (ModelResourceLocation)p_equals_1_;
            return this.variant.equals(modelresourcelocation.variant);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return 31 * super.hashCode() + this.variant.hashCode();
    }

    @Override
    public String toString() {
        return String.valueOf(super.toString()) + '#' + this.variant;
    }
}

