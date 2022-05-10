package net.minecraft.network.play.server;

import java.io.IOException;
import java.util.EnumSet;
import java.util.Set;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class SPacketPlayerPosLook
implements Packet<INetHandlerPlayClient> {
    private double x;
    private double y;
    private double z;
    private float yaw;
    private float pitch;
    private Set<EnumFlags> flags;
    private int teleportId;

    public SPacketPlayerPosLook() {
    }

    public SPacketPlayerPosLook(double xIn, double yIn, double zIn, float yawIn, float pitchIn, Set<EnumFlags> flagsIn, int teleportIdIn) {
        this.x = xIn;
        this.y = yIn;
        this.z = zIn;
        this.yaw = yawIn;
        this.pitch = pitchIn;
        this.flags = flagsIn;
        this.teleportId = teleportIdIn;
    }

    @Override
    public void readPacketData(PacketBuffer buf2) throws IOException {
        this.x = buf2.readDouble();
        this.y = buf2.readDouble();
        this.z = buf2.readDouble();
        this.yaw = buf2.readFloat();
        this.pitch = buf2.readFloat();
        this.flags = EnumFlags.unpack(buf2.readUnsignedByte());
        this.teleportId = buf2.readVarIntFromBuffer();
    }

    @Override
    public void writePacketData(PacketBuffer buf2) throws IOException {
        buf2.writeDouble(this.x);
        buf2.writeDouble(this.y);
        buf2.writeDouble(this.z);
        buf2.writeFloat(this.yaw);
        buf2.writeFloat(this.pitch);
        buf2.writeByte(EnumFlags.pack(this.flags));
        buf2.writeVarIntToBuffer(this.teleportId);
    }

    @Override
    public void processPacket(INetHandlerPlayClient handler) {
        handler.handlePlayerPosLook(this);
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

    public int getTeleportId() {
        return this.teleportId;
    }

    public Set<EnumFlags> getFlags() {
        return this.flags;
    }

    public static enum EnumFlags {
        X(0),
        Y(1),
        Z(2),
        Y_ROT(3),
        X_ROT(4);

        private final int bit;

        private EnumFlags(int p_i46690_3_) {
            this.bit = p_i46690_3_;
        }

        private int getMask() {
            return 1 << this.bit;
        }

        private boolean isSet(int p_187043_1_) {
            return (p_187043_1_ & this.getMask()) == this.getMask();
        }

        public static Set<EnumFlags> unpack(int flags) {
            EnumSet<EnumFlags> set = EnumSet.noneOf(EnumFlags.class);
            for (EnumFlags spacketplayerposlook$enumflags : EnumFlags.values()) {
                if (!spacketplayerposlook$enumflags.isSet(flags)) continue;
                set.add(spacketplayerposlook$enumflags);
            }
            return set;
        }

        public static int pack(Set<EnumFlags> flags) {
            int i2 = 0;
            for (EnumFlags spacketplayerposlook$enumflags : flags) {
                i2 |= spacketplayerposlook$enumflags.getMask();
            }
            return i2;
        }
    }
}

