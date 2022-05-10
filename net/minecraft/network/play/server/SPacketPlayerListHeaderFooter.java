package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.util.text.ITextComponent;

public class SPacketPlayerListHeaderFooter
implements Packet<INetHandlerPlayClient> {
    private ITextComponent header;
    private ITextComponent footer;

    @Override
    public void readPacketData(PacketBuffer buf2) throws IOException {
        this.header = buf2.readTextComponent();
        this.footer = buf2.readTextComponent();
    }

    @Override
    public void writePacketData(PacketBuffer buf2) throws IOException {
        buf2.writeTextComponent(this.header);
        buf2.writeTextComponent(this.footer);
    }

    @Override
    public void processPacket(INetHandlerPlayClient handler) {
        handler.handlePlayerListHeaderFooter(this);
    }

    public ITextComponent getHeader() {
        return this.header;
    }

    public ITextComponent getFooter() {
        return this.footer;
    }
}

