package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class SPacketKeepAlive
implements Packet<INetHandlerPlayClient> {
    private long id;

    public SPacketKeepAlive() {
    }

    public SPacketKeepAlive(long idIn) {
        this.id = idIn;
    }

    @Override
    public void processPacket(INetHandlerPlayClient handler) {
        handler.handleKeepAlive(this);
    }

    @Override
    public void readPacketData(PacketBuffer buf2) throws IOException {
        this.id = buf2.readLong();
    }

    @Override
    public void writePacketData(PacketBuffer buf2) throws IOException {
        buf2.writeLong(this.id);
    }

    public long getId() {
        return this.id;
    }
}

