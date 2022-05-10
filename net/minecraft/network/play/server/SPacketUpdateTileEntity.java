package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.util.math.BlockPos;

public class SPacketUpdateTileEntity
implements Packet<INetHandlerPlayClient> {
    private BlockPos blockPos;
    private int metadata;
    private NBTTagCompound nbt;

    public SPacketUpdateTileEntity() {
    }

    public SPacketUpdateTileEntity(BlockPos blockPosIn, int metadataIn, NBTTagCompound compoundIn) {
        this.blockPos = blockPosIn;
        this.metadata = metadataIn;
        this.nbt = compoundIn;
    }

    @Override
    public void readPacketData(PacketBuffer buf2) throws IOException {
        this.blockPos = buf2.readBlockPos();
        this.metadata = buf2.readUnsignedByte();
        this.nbt = buf2.readNBTTagCompoundFromBuffer();
    }

    @Override
    public void writePacketData(PacketBuffer buf2) throws IOException {
        buf2.writeBlockPos(this.blockPos);
        buf2.writeByte((byte)this.metadata);
        buf2.writeNBTTagCompoundToBuffer(this.nbt);
    }

    @Override
    public void processPacket(INetHandlerPlayClient handler) {
        handler.handleUpdateTileEntity(this);
    }

    public BlockPos getPos() {
        return this.blockPos;
    }

    public int getTileEntityType() {
        return this.metadata;
    }

    public NBTTagCompound getNbtCompound() {
        return this.nbt;
    }
}

