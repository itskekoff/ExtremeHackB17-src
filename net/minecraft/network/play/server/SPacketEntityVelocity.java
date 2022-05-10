package net.minecraft.network.play.server;

import ShwepSS.B17.modules.hacks.AntiVelocity;
import java.io.IOException;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class SPacketEntityVelocity
implements Packet<INetHandlerPlayClient> {
    private int entityID;
    private int motionX;
    private int motionY;
    private int motionZ;

    public SPacketEntityVelocity() {
    }

    public SPacketEntityVelocity(Entity entityIn) {
        this(entityIn.getEntityId(), entityIn.motionX, entityIn.motionY, entityIn.motionZ);
    }

    public SPacketEntityVelocity(int entityIdIn, double motionXIn, double motionYIn, double motionZIn) {
        this.entityID = entityIdIn;
        double d0 = 3.9;
        if (motionXIn < -3.9) {
            motionXIn = -3.9;
        }
        if (motionYIn < -3.9) {
            motionYIn = -3.9;
        }
        if (motionZIn < -3.9) {
            motionZIn = -3.9;
        }
        if (motionXIn > 3.9) {
            motionXIn = 3.9;
        }
        if (motionYIn > 3.9) {
            motionYIn = 3.9;
        }
        if (motionZIn > 3.9) {
            motionZIn = 3.9;
        }
        this.motionX = (int)(motionXIn * 8000.0);
        this.motionY = (int)(motionYIn * 8000.0);
        this.motionZ = (int)(motionZIn * 8000.0);
    }

    @Override
    public void readPacketData(PacketBuffer buf2) throws IOException {
        this.entityID = buf2.readVarIntFromBuffer();
        this.motionX = buf2.readShort();
        this.motionY = buf2.readShort();
        this.motionZ = buf2.readShort();
    }

    @Override
    public void writePacketData(PacketBuffer buf2) throws IOException {
        buf2.writeVarIntToBuffer(this.entityID);
        buf2.writeShort(this.motionX);
        buf2.writeShort(this.motionY);
        buf2.writeShort(this.motionZ);
    }

    @Override
    public void processPacket(INetHandlerPlayClient handler) {
        if (AntiVelocity.instance.isEnabled()) {
            return;
        }
        handler.handleEntityVelocity(this);
    }

    public int getEntityID() {
        return this.entityID;
    }

    public int getMotionX() {
        return this.motionX;
    }

    public int getMotionY() {
        return this.motionY;
    }

    public int getMotionZ() {
        return this.motionZ;
    }
}

