package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.util.EnumParticleTypes;

public class SPacketParticles
implements Packet<INetHandlerPlayClient> {
    private EnumParticleTypes particleType;
    private float xCoord;
    private float yCoord;
    private float zCoord;
    private float xOffset;
    private float yOffset;
    private float zOffset;
    private float particleSpeed;
    private int particleCount;
    private boolean longDistance;
    private int[] particleArguments;

    public SPacketParticles() {
    }

    public SPacketParticles(EnumParticleTypes particleIn, boolean longDistanceIn, float xIn, float yIn, float zIn, float xOffsetIn, float yOffsetIn, float zOffsetIn, float speedIn, int countIn, int ... argumentsIn) {
        this.particleType = particleIn;
        this.longDistance = longDistanceIn;
        this.xCoord = xIn;
        this.yCoord = yIn;
        this.zCoord = zIn;
        this.xOffset = xOffsetIn;
        this.yOffset = yOffsetIn;
        this.zOffset = zOffsetIn;
        this.particleSpeed = speedIn;
        this.particleCount = countIn;
        this.particleArguments = argumentsIn;
    }

    @Override
    public void readPacketData(PacketBuffer buf2) throws IOException {
        this.particleType = EnumParticleTypes.getParticleFromId(buf2.readInt());
        if (this.particleType == null) {
            this.particleType = EnumParticleTypes.BARRIER;
        }
        this.longDistance = buf2.readBoolean();
        this.xCoord = buf2.readFloat();
        this.yCoord = buf2.readFloat();
        this.zCoord = buf2.readFloat();
        this.xOffset = buf2.readFloat();
        this.yOffset = buf2.readFloat();
        this.zOffset = buf2.readFloat();
        this.particleSpeed = buf2.readFloat();
        this.particleCount = buf2.readInt();
        int i2 = this.particleType.getArgumentCount();
        this.particleArguments = new int[i2];
        for (int j2 = 0; j2 < i2; ++j2) {
            this.particleArguments[j2] = buf2.readVarIntFromBuffer();
        }
    }

    @Override
    public void writePacketData(PacketBuffer buf2) throws IOException {
        buf2.writeInt(this.particleType.getParticleID());
        buf2.writeBoolean(this.longDistance);
        buf2.writeFloat(this.xCoord);
        buf2.writeFloat(this.yCoord);
        buf2.writeFloat(this.zCoord);
        buf2.writeFloat(this.xOffset);
        buf2.writeFloat(this.yOffset);
        buf2.writeFloat(this.zOffset);
        buf2.writeFloat(this.particleSpeed);
        buf2.writeInt(this.particleCount);
        int i2 = this.particleType.getArgumentCount();
        for (int j2 = 0; j2 < i2; ++j2) {
            buf2.writeVarIntToBuffer(this.particleArguments[j2]);
        }
    }

    public EnumParticleTypes getParticleType() {
        return this.particleType;
    }

    public boolean isLongDistance() {
        return this.longDistance;
    }

    public double getXCoordinate() {
        return this.xCoord;
    }

    public double getYCoordinate() {
        return this.yCoord;
    }

    public double getZCoordinate() {
        return this.zCoord;
    }

    public float getXOffset() {
        return this.xOffset;
    }

    public float getYOffset() {
        return this.yOffset;
    }

    public float getZOffset() {
        return this.zOffset;
    }

    public float getParticleSpeed() {
        return this.particleSpeed;
    }

    public int getParticleCount() {
        return this.particleCount;
    }

    public int[] getParticleArgs() {
        return this.particleArguments;
    }

    @Override
    public void processPacket(INetHandlerPlayClient handler) {
        handler.handleParticles(this);
    }
}

