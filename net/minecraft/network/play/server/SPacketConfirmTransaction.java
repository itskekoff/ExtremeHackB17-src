package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class SPacketConfirmTransaction
implements Packet<INetHandlerPlayClient> {
    private int windowId;
    private short actionNumber;
    private boolean accepted;

    public SPacketConfirmTransaction() {
    }

    public SPacketConfirmTransaction(int windowIdIn, short actionNumberIn, boolean acceptedIn) {
        this.windowId = windowIdIn;
        this.actionNumber = actionNumberIn;
        this.accepted = acceptedIn;
    }

    @Override
    public void processPacket(INetHandlerPlayClient handler) {
        handler.handleConfirmTransaction(this);
    }

    @Override
    public void readPacketData(PacketBuffer buf2) throws IOException {
        this.windowId = buf2.readUnsignedByte();
        this.actionNumber = buf2.readShort();
        this.accepted = buf2.readBoolean();
    }

    @Override
    public void writePacketData(PacketBuffer buf2) throws IOException {
        buf2.writeByte(this.windowId);
        buf2.writeShort(this.actionNumber);
        buf2.writeBoolean(this.accepted);
    }

    public int getWindowId() {
        return this.windowId;
    }

    public short getActionNumber() {
        return this.actionNumber;
    }

    public boolean wasAccepted() {
        return this.accepted;
    }
}

