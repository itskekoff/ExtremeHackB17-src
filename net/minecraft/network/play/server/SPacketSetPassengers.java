package net.minecraft.network.play.server;

import java.io.IOException;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class SPacketSetPassengers
implements Packet<INetHandlerPlayClient> {
    private int entityId;
    private int[] passengerIds;

    public SPacketSetPassengers() {
    }

    public SPacketSetPassengers(Entity entityIn) {
        this.entityId = entityIn.getEntityId();
        List<Entity> list = entityIn.getPassengers();
        this.passengerIds = new int[list.size()];
        for (int i2 = 0; i2 < list.size(); ++i2) {
            this.passengerIds[i2] = list.get(i2).getEntityId();
        }
    }

    @Override
    public void readPacketData(PacketBuffer buf2) throws IOException {
        this.entityId = buf2.readVarIntFromBuffer();
        this.passengerIds = buf2.readVarIntArray();
    }

    @Override
    public void writePacketData(PacketBuffer buf2) throws IOException {
        buf2.writeVarIntToBuffer(this.entityId);
        buf2.writeVarIntArray(this.passengerIds);
    }

    @Override
    public void processPacket(INetHandlerPlayClient handler) {
        handler.handleSetPassengers(this);
    }

    public int[] getPassengerIds() {
        return this.passengerIds;
    }

    public int getEntityId() {
        return this.entityId;
    }
}

