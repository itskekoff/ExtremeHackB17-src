package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;

public class CPacketResourcePackStatus
implements Packet<INetHandlerPlayServer> {
    private Action action;

    public CPacketResourcePackStatus() {
    }

    public CPacketResourcePackStatus(Action p_i47156_1_) {
        this.action = p_i47156_1_;
    }

    @Override
    public void readPacketData(PacketBuffer buf2) throws IOException {
        this.action = buf2.readEnumValue(Action.class);
    }

    @Override
    public void writePacketData(PacketBuffer buf2) throws IOException {
        buf2.writeEnumValue(this.action);
    }

    @Override
    public void processPacket(INetHandlerPlayServer handler) {
        handler.handleResourcePackStatus(this);
    }

    public static enum Action {
        SUCCESSFULLY_LOADED,
        DECLINED,
        FAILED_DOWNLOAD,
        ACCEPTED;

    }
}

