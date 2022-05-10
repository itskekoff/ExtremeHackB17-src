package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;

public class CPacketPlayer
implements Packet<INetHandlerPlayServer> {
    protected double x;
    protected double y;
    protected double z;
    protected float yaw;
    protected float pitch;
    protected boolean onGround;
    protected boolean moving;
    protected boolean rotating;

    public CPacketPlayer() {
    }

    public CPacketPlayer(boolean onGroundIn) {
        this.onGround = onGroundIn;
    }

    @Override
    public void processPacket(INetHandlerPlayServer handler) {
        handler.processPlayer(this);
    }

    @Override
    public void readPacketData(PacketBuffer buf2) throws IOException {
        this.onGround = buf2.readUnsignedByte() != 0;
    }

    @Override
    public void writePacketData(PacketBuffer buf2) throws IOException {
        buf2.writeByte(this.onGround ? 1 : 0);
    }

    public double getX(double defaultValue) {
        return this.moving ? this.x : defaultValue;
    }

    public double getY(double defaultValue) {
        return this.moving ? this.y : defaultValue;
    }

    public double getZ(double defaultValue) {
        return this.moving ? this.z : defaultValue;
    }

    public float getYaw(float defaultValue) {
        return this.rotating ? this.yaw : defaultValue;
    }

    public float getPitch(float defaultValue) {
        return this.rotating ? this.pitch : defaultValue;
    }

    public boolean isOnGround() {
        return this.onGround;
    }

    public static class Position
    extends CPacketPlayer {
        public Position() {
            this.moving = true;
        }

        public Position(double xIn, double yIn, double zIn, boolean onGroundIn) {
            this.x = xIn;
            this.y = yIn;
            this.z = zIn;
            this.onGround = onGroundIn;
            this.moving = true;
        }

        @Override
        public void readPacketData(PacketBuffer buf2) throws IOException {
            this.x = buf2.readDouble();
            this.y = buf2.readDouble();
            this.z = buf2.readDouble();
            super.readPacketData(buf2);
        }

        @Override
        public void writePacketData(PacketBuffer buf2) throws IOException {
            buf2.writeDouble(this.x);
            buf2.writeDouble(this.y);
            buf2.writeDouble(this.z);
            super.writePacketData(buf2);
        }
    }

    public static class PositionRotation
    extends CPacketPlayer {
        public PositionRotation() {
            this.moving = true;
            this.rotating = true;
        }

        public PositionRotation(double xIn, double yIn, double zIn, float yawIn, float pitchIn, boolean onGroundIn) {
            this.x = xIn;
            this.y = yIn;
            this.z = zIn;
            this.yaw = yawIn;
            this.pitch = pitchIn;
            this.onGround = onGroundIn;
            this.rotating = true;
            this.moving = true;
        }

        @Override
        public void readPacketData(PacketBuffer buf2) throws IOException {
            this.x = buf2.readDouble();
            this.y = buf2.readDouble();
            this.z = buf2.readDouble();
            this.yaw = buf2.readFloat();
            this.pitch = buf2.readFloat();
            super.readPacketData(buf2);
        }

        @Override
        public void writePacketData(PacketBuffer buf2) throws IOException {
            buf2.writeDouble(this.x);
            buf2.writeDouble(this.y);
            buf2.writeDouble(this.z);
            buf2.writeFloat(this.yaw);
            buf2.writeFloat(this.pitch);
            super.writePacketData(buf2);
        }
    }

    public static class Rotation
    extends CPacketPlayer {
        public Rotation() {
            this.rotating = true;
        }

        public Rotation(float yawIn, float pitchIn, boolean onGroundIn) {
            this.yaw = yawIn;
            this.pitch = pitchIn;
            this.onGround = onGroundIn;
            this.rotating = true;
        }

        @Override
        public void readPacketData(PacketBuffer buf2) throws IOException {
            this.yaw = buf2.readFloat();
            this.pitch = buf2.readFloat();
            super.readPacketData(buf2);
        }

        @Override
        public void writePacketData(PacketBuffer buf2) throws IOException {
            buf2.writeFloat(this.yaw);
            buf2.writeFloat(this.pitch);
            super.writePacketData(buf2);
        }
    }
}

