package net.minecraft.world.chunk;

import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.chunk.IBlockStatePalette;
import net.minecraft.world.chunk.IBlockStatePaletteResizer;

public class BlockStatePaletteLinear
implements IBlockStatePalette {
    private final IBlockState[] states;
    private final IBlockStatePaletteResizer resizeHandler;
    private final int bits;
    private int arraySize;

    public BlockStatePaletteLinear(int p_i47088_1_, IBlockStatePaletteResizer p_i47088_2_) {
        this.states = new IBlockState[1 << p_i47088_1_];
        this.bits = p_i47088_1_;
        this.resizeHandler = p_i47088_2_;
    }

    @Override
    public int idFor(IBlockState state) {
        int j2;
        for (int i2 = 0; i2 < this.arraySize; ++i2) {
            if (this.states[i2] != state) continue;
            return i2;
        }
        if ((j2 = this.arraySize++) < this.states.length) {
            this.states[j2] = state;
            return j2;
        }
        return this.resizeHandler.onResize(this.bits + 1, state);
    }

    @Override
    @Nullable
    public IBlockState getBlockState(int indexKey) {
        return indexKey >= 0 && indexKey < this.arraySize ? this.states[indexKey] : null;
    }

    @Override
    public void read(PacketBuffer buf2) {
        this.arraySize = buf2.readVarIntFromBuffer();
        for (int i2 = 0; i2 < this.arraySize; ++i2) {
            this.states[i2] = Block.BLOCK_STATE_IDS.getByValue(buf2.readVarIntFromBuffer());
        }
    }

    @Override
    public void write(PacketBuffer buf2) {
        buf2.writeVarIntToBuffer(this.arraySize);
        for (int i2 = 0; i2 < this.arraySize; ++i2) {
            buf2.writeVarIntToBuffer(Block.BLOCK_STATE_IDS.get(this.states[i2]));
        }
    }

    @Override
    public int getSerializedState() {
        int i2 = PacketBuffer.getVarIntSize(this.arraySize);
        for (int j2 = 0; j2 < this.arraySize; ++j2) {
            i2 += PacketBuffer.getVarIntSize(Block.BLOCK_STATE_IDS.get(this.states[j2]));
        }
        return i2;
    }
}

