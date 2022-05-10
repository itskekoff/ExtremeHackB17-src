package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;

public class CPacketUpdateSign
implements Packet<INetHandlerPlayServer> {
    private BlockPos pos;
    private String[] lines;

    public CPacketUpdateSign() {
    }

    public CPacketUpdateSign(BlockPos posIn, ITextComponent[] linesIn) {
        this.pos = posIn;
        this.lines = new String[]{linesIn[0].getUnformattedText(), linesIn[1].getUnformattedText(), linesIn[2].getUnformattedText(), linesIn[3].getUnformattedText()};
    }

    @Override
    public void readPacketData(PacketBuffer buf2) throws IOException {
        this.pos = buf2.readBlockPos();
        this.lines = new String[4];
        for (int i2 = 0; i2 < 4; ++i2) {
            this.lines[i2] = buf2.readStringFromBuffer(384);
        }
    }

    @Override
    public void writePacketData(PacketBuffer buf2) throws IOException {
        buf2.writeBlockPos(this.pos);
        for (int i2 = 0; i2 < 4; ++i2) {
            buf2.writeString(this.lines[i2]);
        }
    }

    @Override
    public void processPacket(INetHandlerPlayServer handler) {
        handler.processUpdateSign(this);
    }

    public BlockPos getPosition() {
        return this.pos;
    }

    public String[] getLines() {
        return this.lines;
    }
}

