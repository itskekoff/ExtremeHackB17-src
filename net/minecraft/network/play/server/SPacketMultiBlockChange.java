package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.Chunk;

public class SPacketMultiBlockChange
implements Packet<INetHandlerPlayClient> {
    private ChunkPos chunkPos;
    private BlockUpdateData[] changedBlocks;

    public SPacketMultiBlockChange() {
    }

    public SPacketMultiBlockChange(int p_i46959_1_, short[] p_i46959_2_, Chunk p_i46959_3_) {
        this.chunkPos = new ChunkPos(p_i46959_3_.xPosition, p_i46959_3_.zPosition);
        this.changedBlocks = new BlockUpdateData[p_i46959_1_];
        for (int i2 = 0; i2 < this.changedBlocks.length; ++i2) {
            this.changedBlocks[i2] = new BlockUpdateData(p_i46959_2_[i2], p_i46959_3_);
        }
    }

    @Override
    public void readPacketData(PacketBuffer buf2) throws IOException {
        this.chunkPos = new ChunkPos(buf2.readInt(), buf2.readInt());
        this.changedBlocks = new BlockUpdateData[buf2.readVarIntFromBuffer()];
        for (int i2 = 0; i2 < this.changedBlocks.length; ++i2) {
            this.changedBlocks[i2] = new BlockUpdateData(buf2.readShort(), Block.BLOCK_STATE_IDS.getByValue(buf2.readVarIntFromBuffer()));
        }
    }

    @Override
    public void writePacketData(PacketBuffer buf2) throws IOException {
        buf2.writeInt(this.chunkPos.chunkXPos);
        buf2.writeInt(this.chunkPos.chunkZPos);
        buf2.writeVarIntToBuffer(this.changedBlocks.length);
        BlockUpdateData[] arrblockUpdateData = this.changedBlocks;
        int n2 = this.changedBlocks.length;
        for (int i2 = 0; i2 < n2; ++i2) {
            BlockUpdateData spacketmultiblockchange$blockupdatedata = arrblockUpdateData[i2];
            buf2.writeShort(spacketmultiblockchange$blockupdatedata.getOffset());
            buf2.writeVarIntToBuffer(Block.BLOCK_STATE_IDS.get(spacketmultiblockchange$blockupdatedata.getBlockState()));
        }
    }

    @Override
    public void processPacket(INetHandlerPlayClient handler) {
        handler.handleMultiBlockChange(this);
    }

    public BlockUpdateData[] getChangedBlocks() {
        return this.changedBlocks;
    }

    public class BlockUpdateData {
        private final short offset;
        private final IBlockState blockState;

        public BlockUpdateData(short p_i46544_2_, IBlockState p_i46544_3_) {
            this.offset = p_i46544_2_;
            this.blockState = p_i46544_3_;
        }

        public BlockUpdateData(short p_i46545_2_, Chunk p_i46545_3_) {
            this.offset = p_i46545_2_;
            this.blockState = p_i46545_3_.getBlockState(this.getPos());
        }

        public BlockPos getPos() {
            return new BlockPos(SPacketMultiBlockChange.this.chunkPos.getBlock(this.offset >> 12 & 0xF, this.offset & 0xFF, this.offset >> 8 & 0xF));
        }

        public short getOffset() {
            return this.offset;
        }

        public IBlockState getBlockState() {
            return this.blockState;
        }
    }
}

