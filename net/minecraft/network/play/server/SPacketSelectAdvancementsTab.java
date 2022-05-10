package net.minecraft.network.play.server;

import java.io.IOException;
import javax.annotation.Nullable;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.util.ResourceLocation;

public class SPacketSelectAdvancementsTab
implements Packet<INetHandlerPlayClient> {
    @Nullable
    private ResourceLocation field_194155_a;

    public SPacketSelectAdvancementsTab() {
    }

    public SPacketSelectAdvancementsTab(@Nullable ResourceLocation p_i47596_1_) {
        this.field_194155_a = p_i47596_1_;
    }

    @Override
    public void processPacket(INetHandlerPlayClient handler) {
        handler.func_194022_a(this);
    }

    @Override
    public void readPacketData(PacketBuffer buf2) throws IOException {
        if (buf2.readBoolean()) {
            this.field_194155_a = buf2.func_192575_l();
        }
    }

    @Override
    public void writePacketData(PacketBuffer buf2) throws IOException {
        buf2.writeBoolean(this.field_194155_a != null);
        if (this.field_194155_a != null) {
            buf2.func_192572_a(this.field_194155_a);
        }
    }

    @Nullable
    public ResourceLocation func_194154_a() {
        return this.field_194155_a;
    }
}

