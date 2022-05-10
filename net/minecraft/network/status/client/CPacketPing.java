package net.minecraft.network.status.client;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.status.INetHandlerStatusServer;

public class CPacketPing
implements Packet<INetHandlerStatusServer> {
    private long clientTime;

    public CPacketPing() {
    }

    public CPacketPing(long clientTimeIn) {
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
    public void processPacket(INetHandlerStatusServer handler) {
        handler.processPing(this);
    }

    public long getClientTime() {
        return this.clientTime;
    }
}

