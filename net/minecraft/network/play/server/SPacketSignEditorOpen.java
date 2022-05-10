package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.util.math.BlockPos;

public class SPacketSignEditorOpen
implements Packet<INetHandlerPlayClient> {
    private BlockPos signPosition;

    public SPacketSignEditorOpen() {
    }

    public SPacketSignEditorOpen(BlockPos posIn) {
        this.signPosition = posIn;
    }

    @Override
    public void processPacket(INetHandlerPlayClient handler) {
        handler.handleSignEditorOpen(this);
    }

    @Override
    public void readPacketData(PacketBuffer buf2) throws IOException {
        this.signPosition = buf2.readBlockPos();
    }

    @Override
    public void writePacketData(PacketBuffer buf2) throws IOException {
        buf2.writeBlockPos(this.signPosition);
    }

    public BlockPos getSignPosition() {
        return this.signPosition;
    }
}

