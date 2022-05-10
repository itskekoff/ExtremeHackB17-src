package net.minecraft.world;

import net.minecraft.util.text.ITextComponent;

public interface IWorldNameable {
    public String getName();

    public boolean hasCustomName();

    public ITextComponent getDisplayName();
}

