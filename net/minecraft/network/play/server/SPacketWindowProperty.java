package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class SPacketWindowProperty
implements Packet<INetHandlerPlayClient> {
    private int windowId;
    private int property;
    private int value;

    public SPacketWindowProperty() {
    }

    public SPacketWindowProperty(int windowIdIn, int propertyIn, int valueIn) {
        this.windowId = windowIdIn;
        this.property = propertyIn;
        this.value = valueIn;
    }

    @Override
    public void processPacket(INetHandlerPlayClient handler) {
        handler.handleWindowProperty(this);
    }

    @Override
    public void readPacketData(PacketBuffer buf2) throws IOException {
        this.windowId = buf2.readUnsignedByte();
        this.property = buf2.readShort();
        this.value = buf2.readShort();
    }

    @Override
    public void writePacketData(PacketBuffer buf2) throws IOException {
        buf2.writeByte(this.windowId);
        buf2.writeShort(this.property);
        buf2.writeShort(this.value);
    }

    public int getWindowId() {
        return this.windowId;
    }

    public int getProperty() {
        return this.property;
    }

    public int getValue() {
        return this.value;
    }
}

