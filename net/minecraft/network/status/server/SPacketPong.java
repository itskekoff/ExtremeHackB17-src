package net.minecraft.network.status.server;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.status.INetHandlerStatusClient;

public class SPacketPong
implements Packet<INetHandlerStatusClient> {
    private long clientTime;

    public SPacketPong() {
    }

    public SPacketPong(long clientTimeIn) {
        this.clientTime = clientTimeIn;
    }

    @Override
    public void readPacketData(PacketBuffer buf2) throws IOException {
        this.clientTime = buf2.readLong();
    }

    @Override
    public void writePacketData(PacketBuffer buf2) throws IOException {
        buf2.writeLong(this.clientTime);
    }

    @Override
    public void processPacket(INetHandlerStatusClient handler) {
        handler.handlePong(this);
    }
}

