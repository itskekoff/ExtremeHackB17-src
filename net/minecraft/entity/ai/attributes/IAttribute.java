package net.minecraft.entity.ai.attributes;

import javax.annotation.Nullable;

public interface IAttribute {
    public String getAttributeUnlocalizedName();

    public double clampValue(double var1);

    public double getDefaultValue();

    public boolean getShouldWatch();

    @Nullable
    public IAttribute getParent();
}

