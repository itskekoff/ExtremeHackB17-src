package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class SPacketUnloadChunk
implements Packet<INetHandlerPlayClient> {
    private int x;
    private int z;

    public SPacketUnloadChunk() {
    }

    public SPacketUnloadChunk(int xIn, int zIn) {
        this.x = xIn;
        this.z = zIn;
    }

    @Override
    public void readPacketData(PacketBuffer buf2) throws IOException {
        this.x = buf2.readInt();
        this.z = buf2.readInt();
    }

    @Override
    public void writePacketData(PacketBuffer buf2) throws IOException {
        buf2.writeInt(this.x);
        buf2.writeInt(this.z);
    }

    @Override
    public void processPacket(INetHandlerPlayClient handler) {
        handler.processChunkUnload(this);
    }

    public int getX() {
        return this.x;
    }

    public int getZ() {
        return this.z;
    }
}

