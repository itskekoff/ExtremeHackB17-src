package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class SPacketSetExperience
implements Packet<INetHandlerPlayClient> {
    private float experienceBar;
    private int totalExperience;
    private int level;

    public SPacketSetExperience() {
    }

    public SPacketSetExperience(float experienceBarIn, int totalExperienceIn, int levelIn) {
        this.experienceBar = experienceBarIn;
        this.totalExperience = totalExperienceIn;
        this.level = levelIn;
    }

    @Override
    public void readPacketData(PacketBuffer buf2) throws IOException {
        this.experienceBar = buf2.readFloat();
        this.level = buf2.readVarIntFromBuffer();
        this.totalExperience = buf2.readVarIntFromBuffer();
    }

    @Override
    public void writePacketData(PacketBuffer buf2) throws IOException {
        buf2.writeFloat(this.experienceBar);
        buf2.writeVarIntToBuffer(this.level);
        buf2.writeVarIntToBuffer(this.totalExperience);
    }

    @Override
    public void processPacket(INetHandlerPlayClient handler) {
        handler.handleSetExperience(this);
    }

    public float getExperienceBar() {
        return this.experienceBar;
    }

    public int getTotalExperience() {
        return this.totalExperience;
    }

    public int getLevel() {
        return this.level;
    }
}

