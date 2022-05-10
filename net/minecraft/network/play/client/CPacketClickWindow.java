package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;

public class CPacketClickWindow
implements Packet<INetHandlerPlayServer> {
    private int windowId;
    private int slotId;
    private int usedButton;
    private short actionNumber;
    private ItemStack clickedItem = ItemStack.field_190927_a;
    private ClickType mode;

    public CPacketClickWindow() {
    }

    public CPacketClickWindow(int windowIdIn, int slotIdIn, int usedButtonIn, ClickType modeIn, ItemStack clickedItemIn, short actionNumberIn) {
        this.windowId = windowIdIn;
        this.slotId = slotIdIn;
        this.usedButton = usedButtonIn;
        this.clickedItem = clickedItemIn.copy();
        this.actionNumber = actionNumberIn;
        this.mode = modeIn;
    }

    @Override
    public void processPacket(INetHandlerPlayServer handler) {
        handler.processClickWindow(this);
    }

    @Override
    public void readPacketData(PacketBuffer buf2) throws IOException {
        this.windowId = buf2.readByte();
        this.slotId = buf2.readShort();
        this.usedButton = buf2.readByte();
        this.actionNumber = buf2.readShort();
        this.mode = buf2.readEnumValue(ClickType.class);
        this.clickedItem = buf2.readItemStackFromBuffer();
    }

    @Override
    public void writePacketData(PacketBuffer buf2) throws IOException {
        buf2.writeByte(this.windowId);
        buf2.writeShort(this.slotId);
        buf2.writeByte(this.usedButton);
        buf2.writeShort(this.actionNumber);
        buf2.writeEnumValue(this.mode);
        buf2.writeItemStackToBuffer(this.clickedItem);
    }

    public int getWindowId() {
        return this.windowId;
    }

    public int getSlotId() {
        return this.slotId;
    }

    public int getUsedButton() {
        return this.usedButton;
    }

    public short getActionNumber() {
        return this.actionNumber;
    }

    public ItemStack getClickedItem() {
        return this.clickedItem;
    }

    public ClickType getClickType() {
        return this.mode;
    }
}

