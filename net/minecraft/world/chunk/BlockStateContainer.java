package net.minecraft.world.chunk;

import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.BitArray;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.chunk.BlockStatePaletteHashMap;
import net.minecraft.world.chunk.BlockStatePaletteLinear;
import net.minecraft.world.chunk.BlockStatePaletteRegistry;
import net.minecraft.world.chunk.IBlockStatePalette;
import net.minecraft.world.chunk.IBlockStatePaletteResizer;
import net.minecraft.world.chunk.NibbleArray;

public class BlockStateContainer
implements IBlockStatePaletteResizer {
    private static final IBlockStatePalette REGISTRY_BASED_PALETTE = new BlockStatePaletteRegistry();
    protected static final IBlockState AIR_BLOCK_STATE = Blocks.AIR.getDefaultState();
    protected BitArray storage;
    protected IBlockStatePalette palette;
    private int bits;

    public BlockStateContainer() {
        this.setBits(4);
    }

    private static int getIndex(int x2, int y2, int z2) {
        return y2 << 8 | z2 << 4 | x2;
    }

    private void setBits(int bitsIn) {
        if (bitsIn != this.bits) {
            this.bits = bitsIn;
            if (this.bits <= 4) {
                this.bits = 4;
                this.palette = new BlockStatePaletteLinear(this.bits, this);
            } else if (this.bits <= 8) {
                this.palette = new BlockStatePaletteHashMap(this.bits, this);
            } else {
                this.palette = REGISTRY_BASED_PALETTE;
                this.bits = MathHelper.log2DeBruijn(Block.BLOCK_STATE_IDS.size());
            }
            this.palette.idFor(AIR_BLOCK_STATE);
            this.storage = new BitArray(this.bits, 4096);
        }
    }

    @Override
    public int onResize(int p_186008_1_, IBlockState state) {
        BitArray bitarray = this.storage;
        IBlockStatePalette iblockstatepalette = this.palette;
        this.setBits(p_186008_1_);
        for (int i2 = 0; i2 < bitarray.size(); ++i2) {
            IBlockState iblockstate = iblockstatepalette.getBlockState(bitarray.getAt(i2));
            if (iblockstate == null) continue;
            this.set(i2, iblockstate);
        }
        return this.palette.idFor(state);
    }

    public void set(int x2, int y2, int z2, IBlockState state) {
        this.set(BlockStateContainer.getIndex(x2, y2, z2), state);
    }

    protected void set(int index, IBlockState state) {
        int i2 = this.palette.idFor(state);
        this.storage.setAt(index, i2);
    }

    public IBlockState get(int x2, int y2, int z2) {
        return this.get(BlockStateContainer.getIndex(x2, y2, z2));
    }

    protected IBlockState get(int index) {
        IBlockState iblockstate = this.palette.getBlockState(this.storage.getAt(index));
        return iblockstate == null ? AIR_BLOCK_STATE : iblockstate;
    }

    public void read(PacketBuffer buf2) {
        byte i2 = buf2.readByte();
        if (this.bits != i2) {
            this.setBits(i2);
        }
        this.palette.read(buf2);
        buf2.readLongArray(this.storage.getBackingLongArray());
    }

    public void write(PacketBuffer buf2) {
        buf2.writeByte(this.bits);
        this.palette.write(buf2);
        buf2.writeLongArray(this.storage.getBackingLongArray());
    }

    @Nullable
    public NibbleArray getDataForNBT(byte[] p_186017_1_, NibbleArray p_186017_2_) {
        NibbleArray nibblearray = null;
        for (int i2 = 0; i2 < 4096; ++i2) {
            int j2 = Block.BLOCK_STATE_IDS.get(this.get(i2));
            int k2 = i2 & 0xF;
            int l2 = i2 >> 8 & 0xF;
            int i1 = i2 >> 4 & 0xF;
            if ((j2 >> 12 & 0xF) != 0) {
                if (nibblearray == null) {
                    nibblearray = new NibbleArray();
                }
                nibblearray.set(k2, l2, i1, j2 >> 12 & 0xF);
            }
            p_186017_1_[i2] = (byte)(j2 >> 4 & 0xFF);
            p_186017_2_.set(k2, l2, i1, j2 & 0xF);
        }
        return nibblearray;
    }

    public void setDataFromNBT(byte[] p_186019_1_, NibbleArray p_186019_2_, @Nullable NibbleArray p_186019_3_) {
        for (int i2 = 0; i2 < 4096; ++i2) {
            int j2 = i2 & 0xF;
            int k2 = i2 >> 8 & 0xF;
            int l2 = i2 >> 4 & 0xF;
            int i1 = p_186019_3_ == null ? 0 : p_186019_3_.get(j2, k2, l2);
            int j1 = i1 << 12 | (p_186019_1_[i2] & 0xFF) << 4 | p_186019_2_.get(j2, k2, l2);
            this.set(i2, Block.BLOCK_STATE_IDS.getByValue(j1));
        }
    }

    public int getSerializedSize() {
        return 1 + this.palette.getSerializedState() + PacketBuffer.getVarIntSize(this.storage.size()) + this.storage.getBackingLongArray().length * 8;
    }
}

