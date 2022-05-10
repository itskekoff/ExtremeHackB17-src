package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

public class SPacketEntityEffect
implements Packet<INetHandlerPlayClient> {
    private int entityId;
    private byte effectId;
    private byte amplifier;
    private int duration;
    private byte flags;

    public SPacketEntityEffect() {
    }

    public SPacketEntityEffect(int entityIdIn, PotionEffect effect) {
        this.entityId = entityIdIn;
        this.effectId = (byte)(Potion.getIdFromPotion(effect.getPotion()) & 0xFF);
        this.amplifier = (byte)(effect.getAmplifier() & 0xFF);
        this.duration = effect.getDuration() > 32767 ? 32767 : effect.getDuration();
        this.flags = 0;
        if (effect.getIsAmbient()) {
            this.flags = (byte)(this.flags | 1);
        }
        if (effect.doesShowParticles()) {
            this.flags = (byte)(this.flags | 2);
        }
    }

    @Override
    public void readPacketData(PacketBuffer buf2) throws IOException {
        this.entityId = buf2.readVarIntFromBuffer();
        this.effectId = buf2.readByte();
        this.amplifier = buf2.readByte();
        this.duration = buf2.readVarIntFromBuffer();
        this.flags = buf2.readByte();
    }

    @Override
    public void writePacketData(PacketBuffer buf2) throws IOException {
        buf2.writeVarIntToBuffer(this.entityId);
        buf2.writeByte(this.effectId);
        buf2.writeByte(this.amplifier);
        buf2.writeVarIntToBuffer(this.duration);
        buf2.writeByte(this.flags);
    }

    public boolean isMaxDuration() {
        return this.duration == 32767;
    }

    @Override
    public void processPacket(INetHandlerPlayClient handler) {
        handler.handleEntityEffect(this);
    }

    public int getEntityId() {
        return this.entityId;
    }

    public byte getEffectId() {
        return this.effectId;
    }

    public byte getAmplifier() {
        return this.amplifier;
    }

    public int getDuration() {
        return this.duration;
    }

    public boolean doesShowParticles() {
        return (this.flags & 2) == 2;
    }

    public boolean getIsAmbient() {
        return (this.flags & 1) == 1;
    }
}

