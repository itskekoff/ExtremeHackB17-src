package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;

public class CPacketPlayerTryUseItemOnBlock
implements Packet<INetHandlerPlayServer> {
    private BlockPos position;
    private EnumFacing placedBlockDirection;
    private EnumHand hand;
    private float facingX;
    private float facingY;
    private float facingZ;

    public CPacketPlayerTryUseItemOnBlock() {
    }

    public CPacketPlayerTryUseItemOnBlock(BlockPos posIn, EnumFacing placedBlockDirectionIn, EnumHand handIn, float facingXIn, float facingYIn, float facingZIn) {
        this.position = posIn;
        this.placedBlockDirection = placedBlockDirectionIn;
        this.hand = handIn;
        this.facingX = facingXIn;
        this.facingY = facingYIn;
        this.facingZ = facingZIn;
    }

    @Override
    public void readPacketData(PacketBuffer buf2) throws IOException {
        this.position = buf2.readBlockPos();
        this.placedBlockDirection = buf2.readEnumValue(EnumFacing.class);
        this.hand = buf2.readEnumValue(EnumHand.class);
        this.facingX = buf2.readFloat();
        this.facingY = buf2.readFloat();
        this.facingZ = buf2.readFloat();
    }

    @Override
    public void writePacketData(PacketBuffer buf2) throws IOException {
        buf2.writeBlockPos(this.position);
        buf2.writeEnumValue(this.placedBlockDirection);
        buf2.writeEnumValue(this.hand);
        buf2.writeFloat(this.facingX);
        buf2.writeFloat(this.facingY);
        buf2.writeFloat(this.facingZ);
    }

    @Override
    public void processPacket(INetHandlerPlayServer handler) {
        handler.processRightClickBlock(this);
    }

    public BlockPos getPos() {
        return this.position;
    }

    public EnumFacing getDirection() {
        return this.placedBlockDirection;
    }

    public EnumHand getHand() {
        return this.hand;
    }

    public float getFacingX() {
        return this.facingX;
    }

    public float getFacingY() {
        return this.facingY;
    }

    public float getFacingZ() {
        return this.facingZ;
    }
}

