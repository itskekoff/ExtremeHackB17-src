package net.minecraft.network.play.server;

import java.io.IOException;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class SPacketEntityAttach
implements Packet<INetHandlerPlayClient> {
    private int entityId;
    private int vehicleEntityId;

    public SPacketEntityAttach() {
    }

    public SPacketEntityAttach(Entity entityIn, @Nullable Entity vehicleIn) {
        this.entityId = entityIn.getEntityId();
        this.vehicleEntityId = vehicleIn != null ? vehicleIn.getEntityId() : -1;
    }

    @Override
    public void readPacketData(PacketBuffer buf2) throws IOException {
        this.entityId = buf2.readInt();
        this.vehicleEntityId = buf2.readInt();
    }

    @Override
    public void writePacketData(PacketBuffer buf2) throws IOException {
        buf2.writeInt(this.entityId);
        buf2.writeInt(this.vehicleEntityId);
    }

    @Override
    public void processPacket(INetHandlerPlayClient handler) {
        handler.handleEntityAttach(this);
    }

    public int getEntityId() {
        return this.entityId;
    }

    public int getVehicleEntityId() {
        return this.vehicleEntityId;
    }
}

