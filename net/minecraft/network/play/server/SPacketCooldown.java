package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.item.Item;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class SPacketCooldown
implements Packet<INetHandlerPlayClient> {
    private Item item;
    private int ticks;

    public SPacketCooldown() {
    }

    public SPacketCooldown(Item itemIn, int ticksIn) {
        this.item = itemIn;
        this.ticks = ticksIn;
    }

    @Override
    public void readPacketData(PacketBuffer buf2) throws IOException {
        this.item = Item.getItemById(buf2.readVarIntFromBuffer());
        this.ticks = buf2.readVarIntFromBuffer();
    }

    @Override
    public void writePacketData(PacketBuffer buf2) throws IOException {
        buf2.writeVarIntToBuffer(Item.getIdFromItem(this.item));
        buf2.writeVarIntToBuffer(this.ticks);
    }

    @Override
    public void processPacket(INetHandlerPlayClient handler) {
        handler.handleCooldown(this);
    }

    public Item getItem() {
        return this.item;
    }

    public int getTicks() {
        return this.ticks;
    }
}

