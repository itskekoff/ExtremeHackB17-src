package net.minecraft.network.handshake.client;

import ShwepSS.B17.modules.HackPack;
import java.io.IOException;
import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.handshake.INetHandlerHandshakeServer;

public class C00Handshake
implements Packet<INetHandlerHandshakeServer> {
    private int protocolVersion;
    private String ip;
    private int port;
    private EnumConnectionState requestedState;

    public C00Handshake() {
    }

    public C00Handshake(String p_i47613_1_, int p_i47613_2_, EnumConnectionState p_i47613_3_) {
        this.protocolVersion = 340;
        if (!HackPack.getFakeIp().isEmpty()) {
            StringBuilder append = new StringBuilder(String.valueOf(p_i47613_1_)).append("\u0000");
            StringBuilder append2 = append.append(HackPack.getFakeIp()).append("\u0000");
            this.ip = append2.append(HackPack.getFakeUUID().replace("-", "")).toString();
        } else {
            this.ip = p_i47613_1_;
        }
        this.port = p_i47613_2_;
        this.requestedState = p_i47613_3_;
    }

    @Override
    public void readPacketData(PacketBuffer buf2) throws IOException {
        this.protocolVersion = buf2.readVarIntFromBuffer();
        this.ip = buf2.readStringFromBuffer(255);
        this.port = buf2.readUnsignedShort();
        this.requestedState = EnumConnectionState.getById(buf2.readVarIntFromBuffer());
    }

    @Override
    public void writePacketData(PacketBuffer buf2) throws IOException {
        buf2.writeVarIntToBuffer(this.protocolVersion);
        buf2.writeString(this.ip);
        buf2.writeShort(this.port);
        buf2.writeVarIntToBuffer(this.requestedState.getId());
    }

    @Override
    public void processPacket(INetHandlerHandshakeServer handler) {
        handler.processHandshake(this);
    }

    public EnumConnectionState getRequestedState() {
        return this.requestedState;
    }

    public int getProtocolVersion() {
        return this.protocolVersion;
    }
}

