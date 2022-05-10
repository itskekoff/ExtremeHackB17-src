package net.minecraft.world.chunk.storage;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.world.chunk.BlockStateContainer;
import net.minecraft.world.chunk.NibbleArray;
import optifine.Reflector;

public class ExtendedBlockStorage {
    private final int yBase;
    private int blockRefCount;
    private int tickRefCount;
    private final BlockStateContainer data;
    private NibbleArray blocklightArray;
    private NibbleArray skylightArray;

    public ExtendedBlockStorage(int y2, boolean storeSkylight) {
        this.yBase = y2;
        this.data = new BlockStateContainer();
        this.blocklightArray = new NibbleArray();
        if (storeSkylight) {
            this.skylightArray = new NibbleArray();
        }
    }

    public IBlockState get(int x2, int y2, int z2) {
        return this.data.get(x2, y2, z2);
    }

    public void set(int x2, int y2, int z2, IBlockState state) {
        if (Reflector.IExtendedBlockState.isInstance(state)) {
            state = (IBlockState)Reflector.call(state, Reflector.IExtendedBlockState_getClean, new Object[0]);
        }
        IBlockState iblockstate = this.get(x2, y2, z2);
        Block block = iblockstate.getBlock();
        Block block1 = state.getBlock();
        if (block != Blocks.AIR) {
            --this.blockRefCount;
            if (block.getTickRandomly()) {
                --this.tickRefCount;
            }
        }
        if (block1 != Blocks.AIR) {
            ++this.blockRefCount;
            if (block1.getTickRandomly()) {
                ++this.tickRefCount;
            }
        }
        this.data.set(x2, y2, z2, state);
    }

    public boolean isEmpty() {
        return this.blockRefCount == 0;
    }

    public boolean getNeedsRandomTick() {
        return this.tickRefCount > 0;
    }

    public int getYLocation() {
        return this.yBase;
    }

    public void setExtSkylightValue(int x2, int y2, int z2, int value) {
        this.skylightArray.set(x2, y2, z2, value);
    }

    public int getExtSkylightValue(int x2, int y2, int z2) {
        return this.skylightArray.get(x2, y2, z2);
    }

    public void setExtBlocklightValue(int x2, int y2, int z2, int value) {
        this.blocklightArray.set(x2, y2, z2, value);
    }

    public int getExtBlocklightValue(int x2, int y2, int z2) {
        return this.blocklightArray.get(x2, y2, z2);
    }

    public void removeInvalidBlocks() {
        IBlockState iblockstate = Blocks.AIR.getDefaultState();
        int i2 = 0;
        int j2 = 0;
        for (int k2 = 0; k2 < 16; ++k2) {
            for (int l2 = 0; l2 < 16; ++l2) {
                for (int i1 = 0; i1 < 16; ++i1) {
                    IBlockState iblockstate1 = this.data.get(i1, k2, l2);
                    if (iblockstate1 == iblockstate) continue;
                    ++i2;
                    Block block = iblockstate1.getBlock();
                    if (!block.getTickRandomly()) continue;
                    ++j2;
                }
            }
        }
        this.blockRefCount = i2;
        this.tickRefCount = j2;
    }

    public BlockStateContainer getData() {
        return this.data;
    }

    public NibbleArray getBlocklightArray() {
        return this.blocklightArray;
    }

    public NibbleArray getSkylightArray() {
        return this.skylightArray;
    }

    public void setBlocklightArray(NibbleArray newBlocklightArray) {
        this.blocklightArray = newBlocklightArray;
    }

    public void setSkylightArray(NibbleArray newSkylightArray) {
        this.skylightArray = newSkylightArray;
    }
}

