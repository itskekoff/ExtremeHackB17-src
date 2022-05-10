package net.minecraft.network.play.server;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.List;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class SPacketRecipeBook
implements Packet<INetHandlerPlayClient> {
    private State field_193646_a;
    private List<IRecipe> field_192596_a;
    private List<IRecipe> field_193647_c;
    private boolean field_192598_c;
    private boolean field_192599_d;

    public SPacketRecipeBook() {
    }

    public SPacketRecipeBook(State p_i47597_1_, List<IRecipe> p_i47597_2_, List<IRecipe> p_i47597_3_, boolean p_i47597_4_, boolean p_i47597_5_) {
        this.field_193646_a = p_i47597_1_;
        this.field_192596_a = p_i47597_2_;
        this.field_193647_c = p_i47597_3_;
        this.field_192598_c = p_i47597_4_;
        this.field_192599_d = p_i47597_5_;
    }

    @Override
    public void processPacket(INetHandlerPlayClient handler) {
        handler.func_191980_a(this);
    }

    @Override
    public void readPacketData(PacketBuffer buf2) throws IOException {
        this.field_193646_a = buf2.readEnumValue(State.class);
        this.field_192598_c = buf2.readBoolean();
        this.field_192599_d = buf2.readBoolean();
        int i2 = buf2.readVarIntFromBuffer();
        this.field_192596_a = Lists.newArrayList();
        for (int j2 = 0; j2 < i2; ++j2) {
            this.field_192596_a.add(CraftingManager.func_193374_a(buf2.readVarIntFromBuffer()));
        }
        if (this.field_193646_a == State.INIT) {
            i2 = buf2.readVarIntFromBuffer();
            this.field_193647_c = Lists.newArrayList();
            for (int k2 = 0; k2 < i2; ++k2) {
                this.field_193647_c.add(CraftingManager.func_193374_a(buf2.readVarIntFromBuffer()));
            }
        }
    }

    @Override
    public void writePacketData(PacketBuffer buf2) throws IOException {
        buf2.writeEnumValue(this.field_193646_a);
        buf2.writeBoolean(this.field_192598_c);
        buf2.writeBoolean(this.field_192599_d);
        buf2.writeVarIntToBuffer(this.field_192596_a.size());
        for (IRecipe irecipe : this.field_192596_a) {
            buf2.writeVarIntToBuffer(CraftingManager.func_193375_a(irecipe));
        }
        if (this.field_193646_a == State.INIT) {
            buf2.writeVarIntToBuffer(this.field_193647_c.size());
            for (IRecipe irecipe1 : this.field_193647_c) {
                buf2.writeVarIntToBuffer(CraftingManager.func_193375_a(irecipe1));
            }
        }
    }

    public List<IRecipe> func_192595_a() {
        return this.field_192596_a;
    }

    public List<IRecipe> func_193644_b() {
        return this.field_193647_c;
    }

    public boolean func_192593_c() {
        return this.field_192598_c;
    }

    public boolean func_192594_d() {
        return this.field_192599_d;
    }

    public State func_194151_e() {
        return this.field_193646_a;
    }

    public static enum State {
        INIT,
        ADD,
        REMOVE;

    }
}

