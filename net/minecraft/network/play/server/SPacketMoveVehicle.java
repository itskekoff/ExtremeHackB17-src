package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class SPacketMoveVehicle
implements Packet<INetHandlerPlayClient> {
    private double x;
    private double y;
    private double z;
    private float yaw;
    private float pitch;

    public SPacketMoveVehicle() {
    }

    public SPacketMoveVehicle(Entity entityIn) {
        this.x = entityIn.posX;
        this.y = entityIn.posY;
        this.z = entityIn.posZ;
        this.yaw = entityIn.rotationYaw;
        this.pitch = entityIn.rotationPitch;
    }

    @Override
    public void readPacketData(PacketBuffer buf2) throws IOException {
        this.x = buf2.readDouble();
        this.y = buf2.readDouble();
        this.z = buf2.readDouble();
        this.yaw = buf2.readFloat();
        this.pitch = buf2.readFloat();
    }

    @Override
    public void writePacketData(PacketBuffer buf2) throws IOException {
        buf2.writeDouble(this.x);
        buf2.writeDouble(this.y);
        buf2.writeDouble(this.z);
        buf2.writeFloat(this.yaw);
        buf2.writeFloat(this.pitch);
    }

    @Override
    public void processPacket(INetHandlerPlayClient handler) {
        handler.handleMoveVehicle(this);
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    public double getZ() {
        return this.z;
    }

    public float getYaw() {
        return this.yaw;
    }

    public float getPitch() {
        return this.pitch;
    }
}

