package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;
import net.minecraft.util.EnumHand;

public class CPacketAnimation
implements Packet<INetHandlerPlayServer> {
    private EnumHand hand;

    public CPacketAnimation() {
    }

    public CPacketAnimation(EnumHand handIn) {
        this.hand = handIn;
    }

    @Override
    public void readPacketData(PacketBuffer buf2) throws IOException {
        this.hand = buf2.readEnumValue(EnumHand.class);
    }

    @Override
    public void writePacketData(PacketBuffer buf2) throws IOException {
        buf2.writeEnumValue(this.hand);
    }

    @Override
    public void processPacket(INetHandlerPlayServer handler) {
        handler.handleAnimation(this);
    }

    public EnumHand getHand() {
        return this.hand;
    }
}

