package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.block.Block;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.util.math.BlockPos;

public class SPacketBlockAction
implements Packet<INetHandlerPlayClient> {
    private BlockPos blockPosition;
    private int instrument;
    private int pitch;
    private Block block;

    public SPacketBlockAction() {
    }

    public SPacketBlockAction(BlockPos pos, Block blockIn, int instrumentIn, int pitchIn) {
        this.blockPosition = pos;
        this.instrument = instrumentIn;
        this.pitch = pitchIn;
        this.block = blockIn;
    }

    @Override
    public void readPacketData(PacketBuffer buf2) throws IOException {
        this.blockPosition = buf2.readBlockPos();
        this.instrument = buf2.readUnsignedByte();
        this.pitch = buf2.readUnsignedByte();
        this.block = Block.getBlockById(buf2.readVarIntFromBuffer() & 0xFFF);
    }

    @Override
    public void writePacketData(PacketBuffer buf2) throws IOException {
        buf2.writeBlockPos(this.blockPosition);
        buf2.writeByte(this.instrument);
        buf2.writeByte(this.pitch);
        buf2.writeVarIntToBuffer(Block.getIdFromBlock(this.block) & 0xFFF);
    }

    @Override
    public void processPacket(INetHandlerPlayClient handler) {
        handler.handleBlockAction(this);
    }

    public BlockPos getBlockPosition() {
        return this.blockPosition;
    }

    public int getData1() {
        return this.instrument;
    }

    public int getData2() {
        return this.pitch;
    }

    public Block getBlockType() {
        return this.block;
    }
}

