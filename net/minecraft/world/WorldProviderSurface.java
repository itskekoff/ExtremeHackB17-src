package net.minecraft.world;

import net.minecraft.world.DimensionType;
import net.minecraft.world.WorldProvider;

public class WorldProviderSurface
extends WorldProvider {
    @Override
    public DimensionType getDimensionType() {
        return DimensionType.OVERWORLD;
    }

    @Override
    public boolean canDropChunk(int x2, int z2) {
        return !this.worldObj.isSpawnChunk(x2, z2);
    }
}

