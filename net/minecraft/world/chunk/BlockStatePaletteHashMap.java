package net.minecraft.world.chunk;

import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.IntIdentityHashBiMap;
import net.minecraft.world.chunk.IBlockStatePalette;
import net.minecraft.world.chunk.IBlockStatePaletteResizer;

public class BlockStatePaletteHashMap
implements IBlockStatePalette {
    private final IntIdentityHashBiMap<IBlockState> statePaletteMap;
    private final IBlockStatePaletteResizer paletteResizer;
    private final int bits;

    public BlockStatePaletteHashMap(int bitsIn, IBlockStatePaletteResizer p_i47089_2_) {
        this.bits = bitsIn;
        this.paletteResizer = p_i47089_2_;
        this.statePaletteMap = new IntIdentityHashBiMap(1 << bitsIn);
    }

    @Override
    public int idFor(IBlockState state) {
        int i2 = this.statePaletteMap.getId(state);
        if (i2 == -1 && (i2 = this.statePaletteMap.add(state)) >= 1 << this.bits) {
            i2 = this.paletteResizer.onResize(this.bits + 1, state);
        }
        return i2;
    }

    @Override
    @Nullable
    public IBlockState getBlockState(int indexKey) {
        return this.statePaletteMap.get(indexKey);
    }

    @Override
    public void read(PacketBuffer buf2) {
        this.statePaletteMap.clear();
        int i2 = buf2.readVarIntFromBuffer();
        for (int j2 = 0; j2 < i2; ++j2) {
            this.statePaletteMap.add(Block.BLOCK_STATE_IDS.getByValue(buf2.readVarIntFromBuffer()));
        }
    }

    @Override
    public void write(PacketBuffer buf2) {
        int i2 = this.statePaletteMap.size();
        buf2.writeVarIntToBuffer(i2);
        for (int j2 = 0; j2 < i2; ++j2) {
            buf2.writeVarIntToBuffer(Block.BLOCK_STATE_IDS.get(this.statePaletteMap.get(j2)));
        }
    }

    @Override
    public int getSerializedState() {
        int i2 = PacketBuffer.getVarIntSize(this.statePaletteMap.size());
        for (int j2 = 0; j2 < this.statePaletteMap.size(); ++j2) {
            i2 += PacketBuffer.getVarIntSize(Block.BLOCK_STATE_IDS.get(this.statePaletteMap.get(j2)));
        }
        return i2;
    }
}

