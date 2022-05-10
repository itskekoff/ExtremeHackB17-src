package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.world.World;

public class SPacketEntityHeadLook
implements Packet<INetHandlerPlayClient> {
    private int entityId;
    private byte yaw;

    public SPacketEntityHeadLook() {
    }

    public SPacketEntityHeadLook(Entity entityIn, byte yawIn) {
        this.entityId = entityIn.getEntityId();
        this.yaw = yawIn;
    }

    @Override
    public void readPacketData(PacketBuffer buf2) throws IOException {
        this.entityId = buf2.readVarIntFromBuffer();
        this.yaw = buf2.readByte();
    }

    @Override
    public void writePacketData(PacketBuffer buf2) throws IOException {
        buf2.writeVarIntToBuffer(this.entityId);
        buf2.writeByte(this.yaw);
    }

    @Override
    public void processPacket(INetHandlerPlayClient handler) {
        handler.handleEntityHeadLook(this);
    }

    public Entity getEntity(World worldIn) {
        return worldIn.getEntityByID(this.entityId);
    }

    public byte getYaw() {
        return this.yaw;
    }
}

