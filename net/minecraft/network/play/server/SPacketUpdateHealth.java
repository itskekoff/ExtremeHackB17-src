package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class SPacketUpdateHealth
implements Packet<INetHandlerPlayClient> {
    private float health;
    private int foodLevel;
    private float saturationLevel;

    public SPacketUpdateHealth() {
    }

    public SPacketUpdateHealth(float healthIn, int foodLevelIn, float saturationLevelIn) {
        this.health = healthIn;
        this.foodLevel = foodLevelIn;
        this.saturationLevel = saturationLevelIn;
    }

    @Override
    public void readPacketData(PacketBuffer buf2) throws IOException {
        this.health = buf2.readFloat();
        this.foodLevel = buf2.readVarIntFromBuffer();
        this.saturationLevel = buf2.readFloat();
    }

    @Override
    public void writePacketData(PacketBuffer buf2) throws IOException {
        buf2.writeFloat(this.health);
        buf2.writeVarIntToBuffer(this.foodLevel);
        buf2.writeFloat(this.saturationLevel);
    }

    @Override
    public void processPacket(INetHandlerPlayClient handler) {
        handler.handleUpdateHealth(this);
    }

    public float getHealth() {
        return this.health;
    }

    public int getFoodLevel() {
        return this.foodLevel;
    }

    public float getSaturationLevel() {
        return this.saturationLevel;
    }
}

