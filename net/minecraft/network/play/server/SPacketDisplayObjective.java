package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.scoreboard.ScoreObjective;

public class SPacketDisplayObjective
implements Packet<INetHandlerPlayClient> {
    private int position;
    private String scoreName;

    public SPacketDisplayObjective() {
    }

    public SPacketDisplayObjective(int positionIn, ScoreObjective objective) {
        this.position = positionIn;
        this.scoreName = objective == null ? "" : objective.getName();
    }

    @Override
    public void readPacketData(PacketBuffer buf2) throws IOException {
        this.position = buf2.readByte();
        this.scoreName = buf2.readStringFromBuffer(16);
    }

    @Override
    public void writePacketData(PacketBuffer buf2) throws IOException {
        buf2.writeByte(this.position);
        buf2.writeString(this.scoreName);
    }

    @Override
    public void processPacket(INetHandlerPlayClient handler) {
        handler.handleDisplayObjective(this);
    }

    public int getPosition() {
        return this.position;
    }

    public String getName() {
        return this.scoreName;
    }
}

