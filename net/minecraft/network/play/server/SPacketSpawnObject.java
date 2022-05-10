package net.minecraft.network.play.server;

import java.io.IOException;
import java.util.UUID;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

public class SPacketSpawnObject
implements Packet<INetHandlerPlayClient> {
    private int entityId;
    private UUID uniqueId;
    private double x;
    private double y;
    private double z;
    private int speedX;
    private int speedY;
    private int speedZ;
    private int pitch;
    private int yaw;
    private int type;
    private int data;

    public SPacketSpawnObject() {
    }

    public SPacketSpawnObject(Entity entityIn, int typeIn) {
        this(entityIn, typeIn, 0);
    }

    public SPacketSpawnObject(Entity entityIn, int typeIn, int dataIn) {
        this.entityId = entityIn.getEntityId();
        this.uniqueId = entityIn.getUniqueID();
        this.x = entityIn.posX;
        this.y = entityIn.posY;
        this.z = entityIn.posZ;
        this.pitch = MathHelper.floor(entityIn.rotationPitch * 256.0f / 360.0f);
        this.yaw = MathHelper.floor(entityIn.rotationYaw * 256.0f / 360.0f);
        this.type = typeIn;
        this.data = dataIn;
        double d0 = 3.9;
        this.speedX = (int)(MathHelper.clamp(entityIn.motionX, -3.9, 3.9) * 8000.0);
        this.speedY = (int)(MathHelper.clamp(entityIn.motionY, -3.9, 3.9) * 8000.0);
        this.speedZ = (int)(MathHelper.clamp(entityIn.motionZ, -3.9, 3.9) * 8000.0);
    }

    public SPacketSpawnObject(Entity entityIn, int typeIn, int dataIn, BlockPos pos) {
        this(entityIn, typeIn, dataIn);
        this.x = pos.getX();
        this.y = pos.getY();
        this.z = pos.getZ();
    }

    @Override
    public void readPacketData(PacketBuffer buf2) throws IOException {
        this.entityId = buf2.readVarIntFromBuffer();
        this.uniqueId = buf2.readUuid();
        this.type = buf2.readByte();
        this.x = buf2.readDouble();
        this.y = buf2.readDouble();
        this.z = buf2.readDouble();
        this.pitch = buf2.readByte();
        this.yaw = buf2.readByte();
        this.data = buf2.readInt();
        this.speedX = buf2.readShort();
        this.speedY = buf2.readShort();
        this.speedZ = buf2.readShort();
    }

    @Override
    public void writePacketData(PacketBuffer buf2) throws IOException {
        buf2.writeVarIntToBuffer(this.entityId);
        buf2.writeUuid(this.uniqueId);
        buf2.writeByte(this.type);
        buf2.writeDouble(this.x);
        buf2.writeDouble(this.y);
        buf2.writeDouble(this.z);
        buf2.writeByte(this.pitch);
        buf2.writeByte(this.yaw);
        buf2.writeInt(this.data);
        buf2.writeShort(this.speedX);
        buf2.writeShort(this.speedY);
        buf2.writeShort(this.speedZ);
    }

    @Override
    public void processPacket(INetHandlerPlayClient handler) {
        handler.handleSpawnObject(this);
    }

    public int getEntityID() {
        return this.entityId;
    }

    public UUID getUniqueId() {
        return this.uniqueId;
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

    public int getSpeedX() {
        return this.speedX;
    }

    public int getSpeedY() {
        return this.speedY;
    }

    public int getSpeedZ() {
        return this.speedZ;
    }

    public int getPitch() {
        return this.pitch;
    }

    public int getYaw() {
        return this.yaw;
    }

    public int getType() {
        return this.type;
    }

    public int getData() {
        return this.data;
    }

    public void setSpeedX(int newSpeedX) {
        this.speedX = newSpeedX;
    }

    public void setSpeedY(int newSpeedY) {
        this.speedY = newSpeedY;
    }

    public void setSpeedZ(int newSpeedZ) {
        this.speedZ = newSpeedZ;
    }

    public void setData(int dataIn) {
        this.data = dataIn;
    }
}

