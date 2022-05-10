package net.minecraft.network.play.server;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.play.INetHandlerPlayClient;

public class SPacketSpawnMob
implements Packet<INetHandlerPlayClient> {
    private int entityId;
    private UUID uniqueId;
    private int type;
    private double x;
    private double y;
    private double z;
    private int velocityX;
    private int velocityY;
    private int velocityZ;
    private byte yaw;
    private byte pitch;
    private byte headPitch;
    private EntityDataManager dataManager;
    private List<EntityDataManager.DataEntry<?>> dataManagerEntries;

    public SPacketSpawnMob() {
    }

    public SPacketSpawnMob(EntityLivingBase entityIn) {
        this.entityId = entityIn.getEntityId();
        this.uniqueId = entityIn.getUniqueID();
        this.type = EntityList.field_191308_b.getIDForObject(entityIn.getClass());
        this.x = entityIn.posX;
        this.y = entityIn.posY;
        this.z = entityIn.posZ;
        this.yaw = (byte)(entityIn.rotationYaw * 256.0f / 360.0f);
        this.pitch = (byte)(entityIn.rotationPitch * 256.0f / 360.0f);
        this.headPitch = (byte)(entityIn.rotationYawHead * 256.0f / 360.0f);
        double d0 = 3.9;
        double d1 = entityIn.motionX;
        double d2 = entityIn.motionY;
        double d3 = entityIn.motionZ;
        if (d1 < -3.9) {
            d1 = -3.9;
        }
        if (d2 < -3.9) {
            d2 = -3.9;
        }
        if (d3 < -3.9) {
            d3 = -3.9;
        }
        if (d1 > 3.9) {
            d1 = 3.9;
        }
        if (d2 > 3.9) {
            d2 = 3.9;
        }
        if (d3 > 3.9) {
            d3 = 3.9;
        }
        this.velocityX = (int)(d1 * 8000.0);
        this.velocityY = (int)(d2 * 8000.0);
        this.velocityZ = (int)(d3 * 8000.0);
        this.dataManager = entityIn.getDataManager();
    }

    @Override
    public void readPacketData(PacketBuffer buf2) throws IOException {
        this.entityId = buf2.readVarIntFromBuffer();
        this.uniqueId = buf2.readUuid();
        this.type = buf2.readVarIntFromBuffer();
        this.x = buf2.readDouble();
        this.y = buf2.readDouble();
        this.z = buf2.readDouble();
        this.yaw = buf2.readByte();
        this.pitch = buf2.readByte();
        this.headPitch = buf2.readByte();
        this.velocityX = buf2.readShort();
        this.velocityY = buf2.readShort();
        this.velocityZ = buf2.readShort();
        this.dataManagerEntries = EntityDataManager.readEntries(buf2);
    }

    @Override
    public void writePacketData(PacketBuffer buf2) throws IOException {
        buf2.writeVarIntToBuffer(this.entityId);
        buf2.writeUuid(this.uniqueId);
        buf2.writeVarIntToBuffer(this.type);
        buf2.writeDouble(this.x);
        buf2.writeDouble(this.y);
        buf2.writeDouble(this.z);
        buf2.writeByte(this.yaw);
        buf2.writeByte(this.pitch);
        buf2.writeByte(this.headPitch);
        buf2.writeShort(this.velocityX);
        buf2.writeShort(this.velocityY);
        buf2.writeShort(this.velocityZ);
        this.dataManager.writeEntries(buf2);
    }

    @Override
    public void processPacket(INetHandlerPlayClient handler) {
        handler.handleSpawnMob(this);
    }

    @Nullable
    public List<EntityDataManager.DataEntry<?>> getDataManagerEntries() {
        return this.dataManagerEntries;
    }

    public int getEntityID() {
        return this.entityId;
    }

    public UUID getUniqueId() {
        return this.uniqueId;
    }

    public int getEntityType() {
        return this.type;
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

    public int getVelocityX() {
        return this.velocityX;
    }

    public int getVelocityY() {
        return this.velocityY;
    }

    public int getVelocityZ() {
        return this.velocityZ;
    }

    public byte getYaw() {
        return this.yaw;
    }

    public byte getPitch() {
        return this.pitch;
    }

    public byte getHeadPitch() {
        return this.headPitch;
    }
}

