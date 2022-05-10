package net.minecraft.network.play.server;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.play.INetHandlerPlayClient;

public class SPacketSpawnPlayer
implements Packet<INetHandlerPlayClient> {
    private int entityId;
    private UUID uniqueId;
    private double x;
    private double y;
    private double z;
    private byte yaw;
    private byte pitch;
    private EntityDataManager watcher;
    private List<EntityDataManager.DataEntry<?>> dataManagerEntries;

    public SPacketSpawnPlayer() {
    }

    public SPacketSpawnPlayer(EntityPlayer player) {
        this.entityId = player.getEntityId();
        this.uniqueId = player.getGameProfile().getId();
        this.x = player.posX;
        this.y = player.posY;
        this.z = player.posZ;
        this.yaw = (byte)(player.rotationYaw * 256.0f / 360.0f);
        this.pitch = (byte)(player.rotationPitch * 256.0f / 360.0f);
        this.watcher = player.getDataManager();
    }

    @Override
    public void readPacketData(PacketBuffer buf2) throws IOException {
        this.entityId = buf2.readVarIntFromBuffer();
        this.uniqueId = buf2.readUuid();
        this.x = buf2.readDouble();
        this.y = buf2.readDouble();
        this.z = buf2.readDouble();
        this.yaw = buf2.readByte();
        this.pitch = buf2.readByte();
        this.dataManagerEntries = EntityDataManager.readEntries(buf2);
    }

    @Override
    public void writePacketData(PacketBuffer buf2) throws IOException {
        buf2.writeVarIntToBuffer(this.entityId);
        buf2.writeUuid(this.uniqueId);
        buf2.writeDouble(this.x);
        buf2.writeDouble(this.y);
        buf2.writeDouble(this.z);
        buf2.writeByte(this.yaw);
        buf2.writeByte(this.pitch);
        this.watcher.writeEntries(buf2);
    }

    @Override
    public void processPacket(INetHandlerPlayClient handler) {
        handler.handleSpawnPlayer(this);
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

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    public double getZ() {
        return this.z;
    }

    public byte getYaw() {
        return this.yaw;
    }

    public byte getPitch() {
        return this.pitch;
    }
}

