package net.minecraft.network.play.server;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.List;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class SPacketExplosion
implements Packet<INetHandlerPlayClient> {
    private double posX;
    private double posY;
    private double posZ;
    private float strength;
    private List<BlockPos> affectedBlockPositions;
    private float motionX;
    private float motionY;
    private float motionZ;

    public SPacketExplosion() {
    }

    public SPacketExplosion(double xIn, double yIn, double zIn, float strengthIn, List<BlockPos> affectedBlockPositionsIn, Vec3d motion) {
        this.posX = xIn;
        this.posY = yIn;
        this.posZ = zIn;
        this.strength = strengthIn;
        this.affectedBlockPositions = Lists.newArrayList(affectedBlockPositionsIn);
        if (motion != null) {
            this.motionX = (float)motion.xCoord;
            this.motionY = (float)motion.yCoord;
            this.motionZ = (float)motion.zCoord;
        }
    }

    @Override
    public void readPacketData(PacketBuffer buf2) throws IOException {
        this.posX = buf2.readFloat();
        this.posY = buf2.readFloat();
        this.posZ = buf2.readFloat();
        this.strength = buf2.readFloat();
        int i2 = buf2.readInt();
        this.affectedBlockPositions = Lists.newArrayListWithCapacity(i2);
        int j2 = (int)this.posX;
        int k2 = (int)this.posY;
        int l2 = (int)this.posZ;
        for (int i1 = 0; i1 < i2; ++i1) {
            int j1 = buf2.readByte() + j2;
            int k1 = buf2.readByte() + k2;
            int l1 = buf2.readByte() + l2;
            this.affectedBlockPositions.add(new BlockPos(j1, k1, l1));
        }
        this.motionX = buf2.readFloat();
        this.motionY = buf2.readFloat();
        this.motionZ = buf2.readFloat();
    }

    @Override
    public void writePacketData(PacketBuffer buf2) throws IOException {
        buf2.writeFloat((float)this.posX);
        buf2.writeFloat((float)this.posY);
        buf2.writeFloat((float)this.posZ);
        buf2.writeFloat(this.strength);
        buf2.writeInt(this.affectedBlockPositions.size());
        int i2 = (int)this.posX;
        int j2 = (int)this.posY;
        int k2 = (int)this.posZ;
        for (BlockPos blockpos : this.affectedBlockPositions) {
            int l2 = blockpos.getX() - i2;
            int i1 = blockpos.getY() - j2;
            int j1 = blockpos.getZ() - k2;
            buf2.writeByte(l2);
            buf2.writeByte(i1);
            buf2.writeByte(j1);
        }
        buf2.writeFloat(this.motionX);
        buf2.writeFloat(this.motionY);
        buf2.writeFloat(this.motionZ);
    }

    @Override
    public void processPacket(INetHandlerPlayClient handler) {
        handler.handleExplosion(this);
    }

    public float getMotionX() {
        return this.motionX;
    }

    public float getMotionY() {
        return this.motionY;
    }

    public float getMotionZ() {
        return this.motionZ;
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

    public float getStrength() {
        return this.strength;
    }

    public List<BlockPos> getAffectedBlockPositions() {
        return this.affectedBlockPositions;
    }
}

