package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class SPacketEntityEquipment
implements Packet<INetHandlerPlayClient> {
    private int entityID;
    private EntityEquipmentSlot equipmentSlot;
    private ItemStack itemStack = ItemStack.field_190927_a;

    public SPacketEntityEquipment() {
    }

    public SPacketEntityEquipment(int entityIdIn, EntityEquipmentSlot equipmentSlotIn, ItemStack itemStackIn) {
        this.entityID = entityIdIn;
        this.equipmentSlot = equipmentSlotIn;
        this.itemStack = itemStackIn.copy();
    }

    @Override
    public void readPacketData(PacketBuffer buf2) throws IOException {
        this.entityID = buf2.readVarIntFromBuffer();
        this.equipmentSlot = buf2.readEnumValue(EntityEquipmentSlot.class);
        this.itemStack = buf2.readItemStackFromBuffer();
    }

    @Override
    public void writePacketData(PacketBuffer buf2) throws IOException {
        buf2.writeVarIntToBuffer(this.entityID);
        buf2.writeEnumValue(this.equipmentSlot);
        buf2.writeItemStackToBuffer(this.itemStack);
    }

    @Override
    public void processPacket(INetHandlerPlayClient handler) {
        handler.handleEntityEquipment(this);
    }

    public ItemStack getItemStack() {
        return this.itemStack;
    }

    public int getEntityID() {
        return this.entityID;
    }

    public EntityEquipmentSlot getEquipmentSlot() {
        return this.equipmentSlot;
    }
}

