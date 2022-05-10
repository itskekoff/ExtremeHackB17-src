package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;

public class CPacketCreativeInventoryAction
implements Packet<INetHandlerPlayServer> {
    private int slotId;
    private ItemStack stack = ItemStack.field_190927_a;

    public CPacketCreativeInventoryAction() {
    }

    public CPacketCreativeInventoryAction(int slotIdIn, ItemStack stackIn) {
        this.slotId = slotIdIn;
        this.stack = stackIn.copy();
    }

    @Override
    public void processPacket(INetHandlerPlayServer handler) {
        handler.processCreativeInventoryAction(this);
    }

    @Override
    public void readPacketData(PacketBuffer buf2) throws IOException {
        this.slotId = buf2.readShort();
        this.stack = buf2.readItemStackFromBuffer();
    }

    @Override
    public void writePacketData(PacketBuffer buf2) throws IOException {
        buf2.writeShort(this.slotId);
        buf2.writeItemStackToBuffer(this.stack);
    }

    public int getSlotId() {
        return this.slotId;
    }

    public ItemStack getStack() {
        return this.stack;
    }
}

