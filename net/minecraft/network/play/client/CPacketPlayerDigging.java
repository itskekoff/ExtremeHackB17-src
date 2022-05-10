package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

public class CPacketPlayerDigging
implements Packet<INetHandlerPlayServer> {
    private BlockPos position;
    private EnumFacing facing;
    private Action action;

    public CPacketPlayerDigging() {
    }

    public CPacketPlayerDigging(Action actionIn, BlockPos posIn, EnumFacing facingIn) {
        this.action = actionIn;
        this.position = posIn;
        this.facing = facingIn;
    }

    @Override
    public void readPacketData(PacketBuffer buf2) throws IOException {
        this.action = buf2.readEnumValue(Action.class);
        this.position = buf2.readBlockPos();
        this.facing = EnumFacing.getFront(buf2.readUnsignedByte());
    }

    @Override
    public void writePacketData(PacketBuffer buf2) throws IOException {
        buf2.writeEnumValue(this.action);
        buf2.writeBlockPos(this.position);
        buf2.writeByte(this.facing.getIndex());
    }

    @Override
    public void processPacket(INetHandlerPlayServer handler) {
        handler.processPlayerDigging(this);
    }

    public BlockPos getPosition() {
        return this.position;
    }

    public EnumFacing getFacing() {
        return this.facing;
    }

    public Action getAction() {
        return this.action;
    }

    public static enum Action {
        START_DESTROY_BLOCK,
        ABORT_DESTROY_BLOCK,
        STOP_DESTROY_BLOCK,
        DROP_ALL_ITEMS,
        DROP_ITEM,
        RELEASE_USE_ITEM,
        SWAP_HELD_ITEMS;

    }
}

