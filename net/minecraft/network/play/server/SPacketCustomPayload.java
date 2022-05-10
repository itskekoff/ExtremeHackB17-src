package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class SPacketCustomPayload
implements Packet<INetHandlerPlayClient> {
    private String channel;
    private PacketBuffer data;

    public SPacketCustomPayload() {
    }

    public SPacketCustomPayload(String channelIn, PacketBuffer bufIn) {
        this.channel = channelIn;
        this.data = bufIn;
        if (bufIn.writerIndex() > 0x100000) {
            throw new IllegalArgumentException("Payload may not be larger than 1048576 bytes");
        }
    }

    @Override
    public void readPacketData(PacketBuffer buf2) throws IOException {
        this.channel = buf2.readStringFromBuffer(20);
        int i2 = buf2.readableBytes();
        if (i2 < 0 || i2 > 0x100000) {
            throw new IOException("Payload may not be larger than 1048576 bytes");
        }
        this.data = new PacketBuffer(buf2.readBytes(i2));
    }

    @Override
    public void writePacketData(PacketBuffer buf2) throws IOException {
        buf2.writeString(this.channel);
        buf2.writeBytes(this.data);
    }

    @Override
    public void processPacket(INetHandlerPlayClient handler) {
        handler.handleCustomPayload(this);
    }

    public String getChannelName() {
        return this.channel;
    }

    public PacketBuffer getBufferData() {
        return this.data;
    }
}

