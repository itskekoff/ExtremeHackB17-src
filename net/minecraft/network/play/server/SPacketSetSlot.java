package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class SPacketSetSlot
implements Packet<INetHandlerPlayClient> {
    private int windowId;
    private int slot;
    private ItemStack item = ItemStack.field_190927_a;

    public SPacketSetSlot() {
    }

    public SPacketSetSlot(int windowIdIn, int slotIn, ItemStack itemIn) {
        this.windowId = windowIdIn;
        this.slot = slotIn;
        this.item = itemIn.copy();
    }

    @Override
    public void processPacket(INetHandlerPlayClient handler) {
        handler.handleSetSlot(this);
    }

    @Override
    public void readPacketData(PacketBuffer buf2) throws IOException {
        this.windowId = buf2.readByte();
        this.slot = buf2.readShort();
        this.item = buf2.readItemStackFromBuffer();
    }

    @Override
    public void writePacketData(PacketBuffer buf2) throws IOException {
        buf2.writeByte(this.windowId);
        buf2.writeShort(this.slot);
        buf2.writeItemStackToBuffer(this.item);
    }

    public int getWindowId() {
        return this.windowId;
    }

    public int getSlot() {
        return this.slot;
    }

    public ItemStack getStack() {
        return this.item;
    }
}

