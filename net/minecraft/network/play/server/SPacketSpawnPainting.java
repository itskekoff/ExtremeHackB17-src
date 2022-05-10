package net.minecraft.network.play.server;

import java.io.IOException;
import java.util.UUID;
import net.minecraft.entity.item.EntityPainting;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

public class SPacketSpawnPainting
implements Packet<INetHandlerPlayClient> {
    private int entityID;
    private UUID uniqueId;
    private BlockPos position;
    private EnumFacing facing;
    private String title;

    public SPacketSpawnPainting() {
    }

    public SPacketSpawnPainting(EntityPainting painting) {
        this.entityID = painting.getEntityId();
        this.uniqueId = painting.getUniqueID();
        this.position = painting.getHangingPosition();
        this.facing = painting.facingDirection;
        this.title = painting.art.title;
    }

    @Override
    public void readPacketData(PacketBuffer buf2) throws IOException {
        this.entityID = buf2.readVarIntFromBuffer();
        this.uniqueId = buf2.readUuid();
        this.title = buf2.readStringFromBuffer(EntityPainting.EnumArt.MAX_NAME_LENGTH);
        this.position = buf2.readBlockPos();
        this.facing = EnumFacing.getHorizontal(buf2.readUnsignedByte());
    }

    @Override
    public void writePacketData(PacketBuffer buf2) throws IOException {
        buf2.writeVarIntToBuffer(this.entityID);
        buf2.writeUuid(this.uniqueId);
        buf2.writeString(this.title);
        buf2.writeBlockPos(this.position);
        buf2.writeByte(this.facing.getHorizontalIndex());
    }

    @Override
    public void processPacket(INetHandlerPlayClient handler) {
        handler.handleSpawnPainting(this);
    }

    public int getEntityID() {
        return this.entityID;
    }

    public UUID getUniqueId() {
        return this.uniqueId;
    }

    public BlockPos getPosition() {
        return this.position;
    }

    public EnumFacing getFacing() {
        return this.facing;
    }

    public String getTitle() {
        return this.title;
    }
}

