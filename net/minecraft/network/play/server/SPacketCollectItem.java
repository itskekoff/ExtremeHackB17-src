package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class SPacketCollectItem
implements Packet<INetHandlerPlayClient> {
    private int collectedItemEntityId;
    private int entityId;
    private int field_191209_c;

    public SPacketCollectItem() {
    }

    public SPacketCollectItem(int p_i47316_1_, int p_i47316_2_, int p_i47316_3_) {
        this.collectedItemEntityId = p_i47316_1_;
        this.entityId = p_i47316_2_;
        this.field_191209_c = p_i47316_3_;
    }

    @Override
    public void readPacketData(PacketBuffer buf2) throws IOException {
        this.collectedItemEntityId = buf2.readVarIntFromBuffer();
        this.entityId = buf2.readVarIntFromBuffer();
        this.field_191209_c = buf2.readVarIntFromBuffer();
    }

    @Override
    public void writePacketData(PacketBuffer buf2) throws IOException {
        buf2.writeVarIntToBuffer(this.collectedItemEntityId);
        buf2.writeVarIntToBuffer(this.entityId);
        buf2.writeVarIntToBuffer(this.field_191209_c);
    }

    @Override
    public void processPacket(INetHandlerPlayClient handler) {
        handler.handleCollectItem(this);
    }

    public int getCollectedItemEntityID() {
        return this.collectedItemEntityId;
    }

    public int getEntityID() {
        return this.entityId;
    }

    public int func_191208_c() {
        return this.field_191209_c;
    }
}

