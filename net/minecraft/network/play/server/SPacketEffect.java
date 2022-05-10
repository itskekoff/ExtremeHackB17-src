package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.util.math.BlockPos;

public class SPacketEffect
implements Packet<INetHandlerPlayClient> {
    private int soundType;
    private BlockPos soundPos;
    private int soundData;
    private boolean serverWide;

    public SPacketEffect() {
    }

    public SPacketEffect(int soundTypeIn, BlockPos soundPosIn, int soundDataIn, boolean serverWideIn) {
        this.soundType = soundTypeIn;
        this.soundPos = soundPosIn;
        this.soundData = soundDataIn;
        this.serverWide = serverWideIn;
    }

    @Override
    public void readPacketData(PacketBuffer buf2) throws IOException {
        this.soundType = buf2.readInt();
        this.soundPos = buf2.readBlockPos();
        this.soundData = buf2.readInt();
        this.serverWide = buf2.readBoolean();
    }

    @Override
    public void writePacketData(PacketBuffer buf2) throws IOException {
        buf2.writeInt(this.soundType);
        buf2.writeBlockPos(this.soundPos);
        buf2.writeInt(this.soundData);
        buf2.writeBoolean(this.serverWide);
    }

    @Override
    public void processPacket(INetHandlerPlayClient handler) {
        handler.handleEffect(this);
    }

    public boolean isSoundServerwide() {
        return this.serverWide;
    }

    public int getSoundType() {
        return this.soundType;
    }

    public int getSoundData() {
        return this.soundData;
    }

    public BlockPos getSoundPos() {
        return this.soundPos;
    }
}

