package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class SPacketAnimation
implements Packet<INetHandlerPlayClient> {
    private int entityId;
    private int type;

    public SPacketAnimation() {
    }

    public SPacketAnimation(Entity entityIn, int typeIn) {
        this.entityId = entityIn.getEntityId();
        this.type = typeIn;
    }

    @Override
    public void readPacketData(PacketBuffer buf2) throws IOException {
        this.entityId = buf2.readVarIntFromBuffer();
        this.type = buf2.readUnsignedByte();
    }

    @Override
    public void writePacketData(PacketBuffer buf2) throws IOException {
        buf2.writeVarIntToBuffer(this.entityId);
        buf2.writeByte(this.type);
    }

    @Override
    public void processPacket(INetHandlerPlayClient handler) {
        handler.handleAnimation(this);
    }

    public int getEntityID() {
        return this.entityId;
    }

    public int getAnimationType() {
        return this.type;
    }
}

