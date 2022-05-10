package net.minecraft.network.play.client;

import java.io.IOException;
import javax.annotation.Nullable;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;
import net.minecraft.util.math.BlockPos;
import org.apache.commons.lang3.StringUtils;

public class CPacketTabComplete
implements Packet<INetHandlerPlayServer> {
    private String message;
    private boolean hasTargetBlock;
    @Nullable
    private BlockPos targetBlock;

    public CPacketTabComplete() {
    }

    public CPacketTabComplete(String messageIn, @Nullable BlockPos targetBlockIn, boolean hasTargetBlockIn) {
        this.message = messageIn;
        this.targetBlock = targetBlockIn;
        this.hasTargetBlock = hasTargetBlockIn;
    }

    @Override
    public void readPacketData(PacketBuffer buf2) throws IOException {
        this.message = buf2.readStringFromBuffer(32767);
        this.hasTargetBlock = buf2.readBoolean();
        boolean flag = buf2.readBoolean();
        if (flag) {
            this.targetBlock = buf2.readBlockPos();
        }
    }

    @Override
    public void writePacketData(PacketBuffer buf2) throws IOException {
        buf2.writeString(StringUtils.substring(this.message, 0, 32767));
        buf2.writeBoolean(this.hasTargetBlock);
        boolean flag = this.targetBlock != null;
        buf2.writeBoolean(flag);
        if (flag) {
            buf2.writeBlockPos(this.targetBlock);
        }
    }

    @Override
    public void processPacket(INetHandlerPlayServer handler) {
        handler.processTabComplete(this);
    }

    public String getMessage() {
        return this.message;
    }

    @Nullable
    public BlockPos getTargetBlock() {
        return this.targetBlock;
    }

    public boolean hasTargetBlock() {
        return this.hasTargetBlock;
    }
}

