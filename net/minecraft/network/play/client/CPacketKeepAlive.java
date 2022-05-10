package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;

public class CPacketKeepAlive
implements Packet<INetHandlerPlayServer> {
    private long key;

    public CPacketKeepAlive() {
    }

    public CPacketKeepAlive(long idIn) {
        this.key = idIn;
    }

    @Override
    public void processPacket(INetHandlerPlayServer handler) {
        handler.processKeepAlive(this);
    }

    @Override
    public void readPacketData(PacketBuffer buf2) throws IOException {
        this.key = buf2.readLong();
    }

    @Override
    public void writePacketData(PacketBuffer buf2) throws IOException {
        buf2.writeLong(this.key);
    }

    public long getKey() {
        return this.key;
    }
}

