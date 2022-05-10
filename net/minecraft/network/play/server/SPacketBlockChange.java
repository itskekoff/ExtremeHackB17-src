package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SPacketBlockChange
implements Packet<INetHandlerPlayClient> {
    private BlockPos blockPosition;
    private IBlockState blockState;

    public SPacketBlockChange() {
    }

    public SPacketBlockChange(World worldIn, BlockPos posIn) {
        this.blockPosition = posIn;
        this.blockState = worldIn.getBlockState(posIn);
    }

    @Override
    public void readPacketData(PacketBuffer buf2) throws IOException {
        this.blockPosition = buf2.readBlockPos();
        this.blockState = Block.BLOCK_STATE_IDS.getByValue(buf2.readVarIntFromBuffer());
    }

    @Override
    public void writePacketData(PacketBuffer buf2) throws IOException {
        buf2.writeBlockPos(this.blockPosition);
        buf2.writeVarIntToBuffer(Block.BLOCK_STATE_IDS.get(this.blockState));
    }

    @Override
    public void processPacket(INetHandlerPlayClient handler) {
        handler.handleBlockChange(this);
    }

    public IBlockState getBlockState() {
        return this.blockState;
    }

    public BlockPos getBlockPosition() {
        return this.blockPosition;
    }
}

