package net.minecraft.network.play.server;

import com.google.common.collect.Lists;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;

public class SPacketChunkData
implements Packet<INetHandlerPlayClient> {
    private int chunkX;
    private int chunkZ;
    private int availableSections;
    private byte[] buffer;
    private List<NBTTagCompound> tileEntityTags;
    private boolean loadChunk;

    public SPacketChunkData() {
    }

    public SPacketChunkData(Chunk p_i47124_1_, int p_i47124_2_) {
        this.chunkX = p_i47124_1_.xPosition;
        this.chunkZ = p_i47124_1_.zPosition;
        this.loadChunk = p_i47124_2_ == 65535;
        boolean flag = p_i47124_1_.getWorld().provider.func_191066_m();
        this.buffer = new byte[this.calculateChunkSize(p_i47124_1_, flag, p_i47124_2_)];
        this.availableSections = this.extractChunkData(new PacketBuffer(this.getWriteBuffer()), p_i47124_1_, flag, p_i47124_2_);
        this.tileEntityTags = Lists.newArrayList();
        for (Map.Entry<BlockPos, TileEntity> entry : p_i47124_1_.getTileEntityMap().entrySet()) {
            BlockPos blockpos = entry.getKey();
            TileEntity tileentity = entry.getValue();
            int i2 = blockpos.getY() >> 4;
            if (!this.doChunkLoad() && (p_i47124_2_ & 1 << i2) == 0) continue;
            NBTTagCompound nbttagcompound = tileentity.getUpdateTag();
            this.tileEntityTags.add(nbttagcompound);
        }
    }

    @Override
    public void readPacketData(PacketBuffer buf2) throws IOException {
        this.chunkX = buf2.readInt();
        this.chunkZ = buf2.readInt();
        this.loadChunk = buf2.readBoolean();
        this.availableSections = buf2.readVarIntFromBuffer();
        int i2 = buf2.readVarIntFromBuffer();
        if (i2 > 0x200000) {
            throw new RuntimeException("Chunk Packet trying to allocate too much memory on read.");
        }
        this.buffer = new byte[i2];
        buf2.readBytes(this.buffer);
        int j2 = buf2.readVarIntFromBuffer();
        this.tileEntityTags = Lists.newArrayList();
        for (int k2 = 0; k2 < j2; ++k2) {
            this.tileEntityTags.add(buf2.readNBTTagCompoundFromBuffer());
        }
    }

    @Override
    public void writePacketData(PacketBuffer buf2) throws IOException {
        buf2.writeInt(this.chunkX);
        buf2.writeInt(this.chunkZ);
        buf2.writeBoolean(this.loadChunk);
        buf2.writeVarIntToBuffer(this.availableSections);
        buf2.writeVarIntToBuffer(this.buffer.length);
        buf2.writeBytes(this.buffer);
        buf2.writeVarIntToBuffer(this.tileEntityTags.size());
        for (NBTTagCompound nbttagcompound : this.tileEntityTags) {
            buf2.writeNBTTagCompoundToBuffer(nbttagcompound);
        }
    }

    @Override
    public void processPacket(INetHandlerPlayClient handler) {
        handler.handleChunkData(this);
    }

    public PacketBuffer getReadBuffer() {
        return new PacketBuffer(Unpooled.wrappedBuffer(this.buffer));
    }

    private ByteBuf getWriteBuffer() {
        ByteBuf bytebuf = Unpooled.wrappedBuffer(this.buffer);
        bytebuf.writerIndex(0);
        return bytebuf;
    }

    public int extractChunkData(PacketBuffer p_189555_1_, Chunk p_189555_2_, boolean p_189555_3_, int p_189555_4_) {
        int i2 = 0;
        ExtendedBlockStorage[] aextendedblockstorage = p_189555_2_.getBlockStorageArray();
        int k2 = aextendedblockstorage.length;
        for (int j2 = 0; j2 < k2; ++j2) {
            ExtendedBlockStorage extendedblockstorage = aextendedblockstorage[j2];
            if (extendedblockstorage == Chunk.NULL_BLOCK_STORAGE || this.doChunkLoad() && extendedblockstorage.isEmpty() || (p_189555_4_ & 1 << j2) == 0) continue;
            i2 |= 1 << j2;
            extendedblockstorage.getData().write(p_189555_1_);
            p_189555_1_.writeBytes(extendedblockstorage.getBlocklightArray().getData());
            if (!p_189555_3_) continue;
            p_189555_1_.writeBytes(extendedblockstorage.getSkylightArray().getData());
        }
        if (this.doChunkLoad()) {
            p_189555_1_.writeBytes(p_189555_2_.getBiomeArray());
        }
        return i2;
    }

    protected int calculateChunkSize(Chunk chunkIn, boolean p_189556_2_, int p_189556_3_) {
        int i2 = 0;
        ExtendedBlockStorage[] aextendedblockstorage = chunkIn.getBlockStorageArray();
        int k2 = aextendedblockstorage.length;
        for (int j2 = 0; j2 < k2; ++j2) {
            ExtendedBlockStorage extendedblockstorage = aextendedblockstorage[j2];
            if (extendedblockstorage == Chunk.NULL_BLOCK_STORAGE || this.doChunkLoad() && extendedblockstorage.isEmpty() || (p_189556_3_ & 1 << j2) == 0) continue;
            i2 += extendedblockstorage.getData().getSerializedSize();
            i2 += extendedblockstorage.getBlocklightArray().getData().length;
            if (!p_189556_2_) continue;
            i2 += extendedblockstorage.getSkylightArray().getData().length;
        }
        if (this.doChunkLoad()) {
            i2 += chunkIn.getBiomeArray().length;
        }
        return i2;
    }

    public int getChunkX() {
        return this.chunkX;
    }

    public int getChunkZ() {
        return this.chunkZ;
    }

    public int getExtractedSize() {
        return this.availableSections;
    }

    public boolean doChunkLoad() {
        return this.loadChunk;
    }

    public List<NBTTagCompound> getTileEntityTags() {
        return this.tileEntityTags;
    }
}

