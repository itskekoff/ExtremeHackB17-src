package net.minecraft.network.login.server;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.login.INetHandlerLoginClient;

public class SPacketEnableCompression
implements Packet<INetHandlerLoginClient> {
    private int compressionThreshold;

    public SPacketEnableCompression() {
    }

    public SPacketEnableCompression(int thresholdIn) {
        this.compressionThreshold = thresholdIn;
    }

    @Override
    public void readPacketData(PacketBuffer buf2) throws IOException {
        this.compressionThreshold = buf2.readVarIntFromBuffer();
    }

    @Override
    public void writePacketData(PacketBuffer buf2) throws IOException {
        buf2.writeVarIntToBuffer(this.compressionThreshold);
    }

    @Override
    public void processPacket(INetHandlerLoginClient handler) {
        handler.handleEnableCompression(this);
    }

    public int getCompressionThreshold() {
        return this.compressionThreshold;
    }
}

