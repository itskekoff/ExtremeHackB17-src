package net.minecraft.network.play.server;

import java.io.IOException;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.potion.Potion;
import net.minecraft.world.World;

public class SPacketRemoveEntityEffect
implements Packet<INetHandlerPlayClient> {
    private int entityId;
    private Potion effectId;

    public SPacketRemoveEntityEffect() {
    }

    public SPacketRemoveEntityEffect(int entityIdIn, Potion potionIn) {
        this.entityId = entityIdIn;
        this.effectId = potionIn;
    }

    @Override
    public void readPacketData(PacketBuffer buf2) throws IOException {
        this.entityId = buf2.readVarIntFromBuffer();
        this.effectId = Potion.getPotionById(buf2.readUnsignedByte());
    }

    @Override
    public void writePacketData(PacketBuffer buf2) throws IOException {
        buf2.writeVarIntToBuffer(this.entityId);
        buf2.writeByte(Potion.getIdFromPotion(this.effectId));
    }

    @Override
    public void processPacket(INetHandlerPlayClient handler) {
        handler.handleRemoveEntityEffect(this);
    }

    @Nullable
    public Entity getEntity(World worldIn) {
        return worldIn.getEntityByID(this.entityId);
    }

    @Nullable
    public Potion getPotion() {
        return this.effectId;
    }
}

