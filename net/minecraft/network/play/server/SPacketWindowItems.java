package net.minecraft.network.play.server;

import java.io.IOException;
import java.util.List;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.util.NonNullList;

public class SPacketWindowItems
implements Packet<INetHandlerPlayClient> {
    private int windowId;
    private List<ItemStack> itemStacks;

    public SPacketWindowItems() {
    }

    public SPacketWindowItems(int p_i47317_1_, NonNullList<ItemStack> p_i47317_2_) {
        this.windowId = p_i47317_1_;
        this.itemStacks = NonNullList.func_191197_a(p_i47317_2_.size(), ItemStack.field_190927_a);
        for (int i2 = 0; i2 < this.itemStacks.size(); ++i2) {
            ItemStack itemstack = p_i47317_2_.get(i2);
            this.itemStacks.set(i2, itemstack.copy());
        }
    }

    @Override
    public void readPacketData(PacketBuffer buf2) throws IOException {
        this.windowId = buf2.readUnsignedByte();
        int i2 = buf2.readShort();
        this.itemStacks = NonNullList.func_191197_a(i2, ItemStack.field_190927_a);
        for (int j2 = 0; j2 < i2; ++j2) {
            this.itemStacks.set(j2, buf2.readItemStackFromBuffer());
        }
    }

    @Override
    public void writePacketData(PacketBuffer buf2) throws IOException {
        buf2.writeByte(this.windowId);
        buf2.writeShort(this.itemStacks.size());
        for (ItemStack itemstack : this.itemStacks) {
            buf2.writeItemStackToBuffer(itemstack);
        }
    }

    @Override
    public void processPacket(INetHandlerPlayClient handler) {
        handler.handleWindowItems(this);
    }

    public int getWindowId() {
        return this.windowId;
    }

    public List<ItemStack> getItemStacks() {
        return this.itemStacks;
    }
}

