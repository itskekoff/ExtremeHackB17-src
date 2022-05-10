package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.util.text.ITextComponent;

public class SPacketOpenWindow
implements Packet<INetHandlerPlayClient> {
    private int windowId;
    private String inventoryType;
    private ITextComponent windowTitle;
    private int slotCount;
    private int entityId;

    public SPacketOpenWindow() {
    }

    public SPacketOpenWindow(int windowIdIn, String inventoryTypeIn, ITextComponent windowTitleIn) {
        this(windowIdIn, inventoryTypeIn, windowTitleIn, 0);
    }

    public SPacketOpenWindow(int windowIdIn, String inventoryTypeIn, ITextComponent windowTitleIn, int slotCountIn) {
        this.windowId = windowIdIn;
        this.inventoryType = inventoryTypeIn;
        this.windowTitle = windowTitleIn;
        this.slotCount = slotCountIn;
    }

    public SPacketOpenWindow(int windowIdIn, String inventoryTypeIn, ITextComponent windowTitleIn, int slotCountIn, int entityIdIn) {
        this(windowIdIn, inventoryTypeIn, windowTitleIn, slotCountIn);
        this.entityId = entityIdIn;
    }

    @Override
    public void processPacket(INetHandlerPlayClient handler) {
        handler.handleOpenWindow(this);
    }

    @Override
    public void readPacketData(PacketBuffer buf2) throws IOException {
        this.windowId = buf2.readUnsignedByte();
        this.inventoryType = buf2.readStringFromBuffer(32);
        this.windowTitle = buf2.readTextComponent();
        this.slotCount = buf2.readUnsignedByte();
        if (this.inventoryType.equals("EntityHorse")) {
            this.entityId = buf2.readInt();
        }
    }

    @Override
    public void writePacketData(PacketBuffer buf2) throws IOException {
        buf2.writeByte(this.windowId);
        buf2.writeString(this.inventoryType);
        buf2.writeTextComponent(this.windowTitle);
        buf2.writeByte(this.slotCount);
        if (this.inventoryType.equals("EntityHorse")) {
            buf2.writeInt(this.entityId);
        }
    }

    public int getWindowId() {
        return this.windowId;
    }

    public String getGuiId() {
        return this.inventoryType;
    }

    public ITextComponent getWindowTitle() {
        return this.windowTitle;
    }

    public int getSlotCount() {
        return this.slotCount;
    }

    public int getEntityId() {
        return this.entityId;
    }

    public boolean hasSlots() {
        return this.slotCount > 0;
    }
}

