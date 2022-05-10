package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;

public class CPacketConfirmTransaction
implements Packet<INetHandlerPlayServer> {
    private int windowId;
    private short uid;
    private boolean accepted;

    public CPacketConfirmTransaction() {
    }

    public CPacketConfirmTransaction(int windowIdIn, short uidIn, boolean acceptedIn) {
        this.windowId = windowIdIn;
        this.uid = uidIn;
        this.accepted = acceptedIn;
    }

    @Override
    public void processPacket(INetHandlerPlayServer handler) {
        handler.processConfirmTransaction(this);
    }

    @Override
    public void readPacketData(PacketBuffer buf2) throws IOException {
        this.windowId = buf2.readByte();
        this.uid = buf2.readShort();
        this.accepted = buf2.readByte() != 0;
    }

    @Override
    public void writePacketData(PacketBuffer buf2) throws IOException {
        buf2.writeByte(this.windowId);
        buf2.writeShort(this.uid);
        buf2.writeByte(this.accepted ? 1 : 0);
    }

    public int getWindowId() {
        return this.windowId;
    }

    public short getUid() {
        return this.uid;
    }
}

