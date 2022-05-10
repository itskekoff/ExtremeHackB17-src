package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class SPacketSpawnExperienceOrb
implements Packet<INetHandlerPlayClient> {
    private int entityID;
    private double posX;
    private double posY;
    private double posZ;
    private int xpValue;

    public SPacketSpawnExperienceOrb() {
    }

    public SPacketSpawnExperienceOrb(EntityXPOrb orb) {
        this.entityID = orb.getEntityId();
        this.posX = orb.posX;
        this.posY = orb.posY;
        this.posZ = orb.posZ;
        this.xpValue = orb.getXpValue();
    }

    @Override
    public void readPacketData(PacketBuffer buf2) throws IOException {
        this.entityID = buf2.readVarIntFromBuffer();
        this.posX = buf2.readDouble();
        this.posY = buf2.readDouble();
        this.posZ = buf2.readDouble();
        this.xpValue = buf2.readShort();
    }

    @Override
    public void writePacketData(PacketBuffer buf2) throws IOException {
        buf2.writeVarIntToBuffer(this.entityID);
        buf2.writeDouble(this.posX);
        buf2.writeDouble(this.posY);
        buf2.writeDouble(this.posZ);
        buf2.writeShort(this.xpValue);
    }

    @Override
    public void processPacket(INetHandlerPlayClient handler) {
        handler.handleSpawnExperienceOrb(this);
    }

    public int getEntityID() {
        return this.entityID;
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

    public int getXPValue() {
        return this.xpValue;
    }
}

