package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.world.World;

public class SPacketEntity
implements Packet<INetHandlerPlayClient> {
    protected int entityId;
    protected int posX;
    protected int posY;
    protected int posZ;
    protected byte yaw;
    protected byte pitch;
    protected boolean onGround;
    protected boolean rotating;

    public SPacketEntity() {
    }

    public SPacketEntity(int entityIdIn) {
        this.entityId = entityIdIn;
    }

    @Override
    public void readPacketData(PacketBuffer buf2) throws IOException {
        this.entityId = buf2.readVarIntFromBuffer();
    }

    @Override
    public void writePacketData(PacketBuffer buf2) throws IOException {
        buf2.writeVarIntToBuffer(this.entityId);
    }

    @Override
    public void processPacket(INetHandlerPlayClient handler) {
        handler.handleEntityMovement(this);
    }

    public String toString() {
        return "Entity_" + super.toString();
    }

    public Entity getEntity(World worldIn) {
        return worldIn.getEntityByID(this.entityId);
    }

    public int getX() {
        return this.posX;
    }

    public int getY() {
        return this.posY;
    }

    public int getZ() {
        return this.posZ;
    }

    public byte getYaw() {
        return this.yaw;
    }

    public byte getPitch() {
        return this.pitch;
    }

    public boolean isRotating() {
        return this.rotating;
    }

    public boolean getOnGround() {
        return this.onGround;
    }

    public static class S15PacketEntityRelMove
    extends SPacketEntity {
        public S15PacketEntityRelMove() {
        }

        public S15PacketEntityRelMove(int entityIdIn, long xIn, long yIn, long zIn, boolean onGroundIn) {
            super(entityIdIn);
            this.posX = (int)xIn;
            this.posY = (int)yIn;
            this.posZ = (int)zIn;
            this.onGround = onGroundIn;
        }

        @Override
        public void readPacketData(PacketBuffer buf2) throws IOException {
            super.readPacketData(buf2);
            this.posX = buf2.readShort();
            this.posY = buf2.readShort();
            this.posZ = buf2.readShort();
            this.onGround = buf2.readBoolean();
        }

        @Override
        public void writePacketData(PacketBuffer buf2) throws IOException {
            super.writePacketData(buf2);
            buf2.writeShort(this.posX);
            buf2.writeShort(this.posY);
            buf2.writeShort(this.posZ);
            buf2.writeBoolean(this.onGround);
        }
    }

    public static class S16PacketEntityLook
    extends SPacketEntity {
        public S16PacketEntityLook() {
            this.rotating = true;
        }

        public S16PacketEntityLook(int entityIdIn, byte yawIn, byte pitchIn, boolean onGroundIn) {
            super(entityIdIn);
            this.yaw = yawIn;
            this.pitch = pitchIn;
            this.rotating = true;
            this.onGround = onGroundIn;
        }

        @Override
        public void readPacketData(PacketBuffer buf2) throws IOException {
            super.readPacketData(buf2);
            this.yaw = buf2.readByte();
            this.pitch = buf2.readByte();
            this.onGround = buf2.readBoolean();
        }

        @Override
        public void writePacketData(PacketBuffer buf2) throws IOException {
            super.writePacketData(buf2);
            buf2.writeByte(this.yaw);
            buf2.writeByte(this.pitch);
            buf2.writeBoolean(this.onGround);
        }
    }

    public static class S17PacketEntityLookMove
    extends SPacketEntity {
        public S17PacketEntityLookMove() {
            this.rotating = true;
        }

        public S17PacketEntityLookMove(int entityIdIn, long xIn, long yIn, long zIn, byte yawIn, byte pitchIn, boolean onGroundIn) {
            super(entityIdIn);
            this.posX = (int)xIn;
            this.posY = (int)yIn;
            this.posZ = (int)zIn;
            this.yaw = yawIn;
            this.pitch = pitchIn;
            this.onGround = onGroundIn;
            this.rotating = true;
        }

        @Override
        public void readPacketData(PacketBuffer buf2) throws IOException {
            super.readPacketData(buf2);
            this.posX = buf2.readShort();
            this.posY = buf2.readShort();
            this.posZ = buf2.readShort();
            this.yaw = buf2.readByte();
            this.pitch = buf2.readByte();
            this.onGround = buf2.readBoolean();
        }

        @Override
        public void writePacketData(PacketBuffer buf2) throws IOException {
            super.writePacketData(buf2);
            buf2.writeShort(this.posX);
            buf2.writeShort(this.posY);
            buf2.writeShort(this.posZ);
            buf2.writeByte(this.yaw);
            buf2.writeByte(this.pitch);
            buf2.writeBoolean(this.onGround);
        }
    }
}

