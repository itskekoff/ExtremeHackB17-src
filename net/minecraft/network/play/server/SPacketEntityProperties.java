package net.minecraft.network.play.server;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class SPacketEntityProperties
implements Packet<INetHandlerPlayClient> {
    private int entityId;
    private final List<Snapshot> snapshots = Lists.newArrayList();

    public SPacketEntityProperties() {
    }

    public SPacketEntityProperties(int entityIdIn, Collection<IAttributeInstance> instances) {
        this.entityId = entityIdIn;
        for (IAttributeInstance iattributeinstance : instances) {
            this.snapshots.add(new Snapshot(iattributeinstance.getAttribute().getAttributeUnlocalizedName(), iattributeinstance.getBaseValue(), iattributeinstance.getModifiers()));
        }
    }

    @Override
    public void readPacketData(PacketBuffer buf2) throws IOException {
        this.entityId = buf2.readVarIntFromBuffer();
        int i2 = buf2.readInt();
        for (int j2 = 0; j2 < i2; ++j2) {
            String s2 = buf2.readStringFromBuffer(64);
            double d0 = buf2.readDouble();
            ArrayList<AttributeModifier> list = Lists.newArrayList();
            int k2 = buf2.readVarIntFromBuffer();
            for (int l2 = 0; l2 < k2; ++l2) {
                UUID uuid = buf2.readUuid();
                list.add(new AttributeModifier(uuid, "Unknown synced attribute modifier", buf2.readDouble(), buf2.readByte()));
            }
            this.snapshots.add(new Snapshot(s2, d0, list));
        }
    }

    @Override
    public void writePacketData(PacketBuffer buf2) throws IOException {
        buf2.writeVarIntToBuffer(this.entityId);
        buf2.writeInt(this.snapshots.size());
        for (Snapshot spacketentityproperties$snapshot : this.snapshots) {
            buf2.writeString(spacketentityproperties$snapshot.getName());
            buf2.writeDouble(spacketentityproperties$snapshot.getBaseValue());
            buf2.writeVarIntToBuffer(spacketentityproperties$snapshot.getModifiers().size());
            for (AttributeModifier attributemodifier : spacketentityproperties$snapshot.getModifiers()) {
                buf2.writeUuid(attributemodifier.getID());
                buf2.writeDouble(attributemodifier.getAmount());
                buf2.writeByte(attributemodifier.getOperation());
            }
        }
    }

    @Override
    public void processPacket(INetHandlerPlayClient handler) {
        handler.handleEntityProperties(this);
    }

    public int getEntityId() {
        return this.entityId;
    }

    public List<Snapshot> getSnapshots() {
        return this.snapshots;
    }

    public class Snapshot {
        private final String name;
        private final double baseValue;
        private final Collection<AttributeModifier> modifiers;

        public Snapshot(String nameIn, double baseValueIn, Collection<AttributeModifier> modifiersIn) {
            this.name = nameIn;
            this.baseValue = baseValueIn;
            this.modifiers = modifiersIn;
        }

        public String getName() {
            return this.name;
        }

        public double getBaseValue() {
            return this.baseValue;
        }

        public Collection<AttributeModifier> getModifiers() {
            return this.modifiers;
        }
    }
}

