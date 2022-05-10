package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class SPacketEntityTeleport
implements Packet<INetHandlerPlayClient> {
    private int entityId;
    private double posX;
    private double posY;
    private double posZ;
    private byte yaw;
    private byte pitch;
    private boolean onGround;

    public SPacketEntityTeleport() {
    }

    public SPacketEntityTeleport(Entity entityIn) {
        this.entityId = entityIn.getEntityId();
        this.posX = entityIn.posX;
        this.posY = entityIn.posY;
        this.posZ = entityIn.posZ;
        this.yaw = (byte)(entityIn.rotationYaw * 256.0f / 360.0f);
        this.pitch = (byte)(entityIn.rotationPitch * 256.0f / 360.0f);
        this.onGround = entityIn.onGround;
    }

    @Override
    public void readPacketData(PacketBuffer buf2) throws IOException {
        this.entityId = buf2.readVarIntFromBuffer();
        this.posX = buf2.readDouble();
        this.posY = buf2.readDouble();
        this.posZ = buf2.readDouble();
        this.yaw = buf2.readByte();
        this.pitch = buf2.readByte();
        this.onGround = buf2.readBoolean();
    }

    @Override
    public void writePacketData(PacketBuffer buf2) throws IOException {
        buf2.writeVarIntToBuffer(this.entityId);
        buf2.writeDouble(this.posX);
        buf2.writeDouble(this.posY);
        buf2.writeDouble(this.posZ);
        buf2.writeByte(this.yaw);
        buf2.writeByte(this.pitch);
        buf2.writeBoolean(this.onGround);
    }

    @Override
    public void processPacket(INetHandlerPlayClient handler) {
        handler.handleEntityTeleport(this);
    }

    public int getEntityId() {
        return this.entityId;
    }

    public double getX() {
        return this.posX;
    }

    public double getY() {
        return this.posY;
    }

    public double getZ() {
        return this.posZ;
    }

    public byte getYaw() {
        return this.yaw;
    }

    public byte getPitch() {
        return this.pitch;
    }

    public boolean getOnGround() {
        return this.onGround;
    }
}

