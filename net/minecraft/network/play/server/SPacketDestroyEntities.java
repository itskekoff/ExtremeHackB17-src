package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class SPacketDestroyEntities
implements Packet<INetHandlerPlayClient> {
    private int[] entityIDs;

    public SPacketDestroyEntities() {
    }

    public SPacketDestroyEntities(int ... entityIdsIn) {
        this.entityIDs = entityIdsIn;
    }

    @Override
    public void readPacketData(PacketBuffer buf2) throws IOException {
        this.entityIDs = new int[buf2.readVarIntFromBuffer()];
        for (int i2 = 0; i2 < this.entityIDs.length; ++i2) {
            this.entityIDs[i2] = buf2.readVarIntFromBuffer();
        }
    }

    @Override
    public void writePacketData(PacketBuffer buf2) throws IOException {
        buf2.writeVarIntToBuffer(this.entityIDs.length);
        int[] arrn = this.entityIDs;
        int n2 = this.entityIDs.length;
        for (int i2 = 0; i2 < n2; ++i2) {
            int i3 = arrn[i2];
            buf2.writeVarIntToBuffer(i3);
        }
    }

    @Override
    public void processPacket(INetHandlerPlayClient handler) {
        handler.handleDestroyEntities(this);
    }

    public int[] getEntityIDs() {
        return this.entityIDs;
    }
}

