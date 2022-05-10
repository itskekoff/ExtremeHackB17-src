package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.entity.player.PlayerCapabilities;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class SPacketPlayerAbilities
implements Packet<INetHandlerPlayClient> {
    private boolean invulnerable;
    private boolean flying;
    private boolean allowFlying;
    private boolean creativeMode;
    private float flySpeed;
    private float walkSpeed;

    public SPacketPlayerAbilities() {
    }

    public SPacketPlayerAbilities(PlayerCapabilities capabilities) {
        this.setInvulnerable(capabilities.disableDamage);
        this.setFlying(capabilities.isFlying);
        this.setAllowFlying(capabilities.allowFlying);
        this.setCreativeMode(capabilities.isCreativeMode);
        this.setFlySpeed(capabilities.getFlySpeed());
        this.setWalkSpeed(capabilities.getWalkSpeed());
    }

    @Override
    public void readPacketData(PacketBuffer buf2) throws IOException {
        byte b0 = buf2.readByte();
        this.setInvulnerable((b0 & 1) > 0);
        this.setFlying((b0 & 2) > 0);
        this.setAllowFlying((b0 & 4) > 0);
        this.setCreativeMode((b0 & 8) > 0);
        this.setFlySpeed(buf2.readFloat());
        this.setWalkSpeed(buf2.readFloat());
    }

    @Override
    public void writePacketData(PacketBuffer buf2) throws IOException {
        byte b0 = 0;
        if (this.isInvulnerable()) {
            b0 = (byte)(b0 | true ? 1 : 0);
        }
        if (this.isFlying()) {
            b0 = (byte)(b0 | 2);
        }
        if (this.isAllowFlying()) {
            b0 = (byte)(b0 | 4);
        }
        if (this.isCreativeMode()) {
            b0 = (byte)(b0 | 8);
        }
        buf2.writeByte(b0);
        buf2.writeFloat(this.flySpeed);
        buf2.writeFloat(this.walkSpeed);
    }

    @Override
    public void processPacket(INetHandlerPlayClient handler) {
        handler.handlePlayerAbilities(this);
    }

    public boolean isInvulnerable() {
        return this.invulnerable;
    }

    public void setInvulnerable(boolean isInvulnerable) {
        this.invulnerable = isInvulnerable;
    }

    public boolean isFlying() {
        return this.flying;
    }

    public void setFlying(boolean isFlying) {
        this.flying = isFlying;
    }

    public boolean isAllowFlying() {
        return this.allowFlying;
    }

    public void setAllowFlying(boolean isAllowFlying) {
        this.allowFlying = isAllowFlying;
    }

    public boolean isCreativeMode() {
        return this.creativeMode;
    }

    public void setCreativeMode(boolean isCreativeMode) {
        this.creativeMode = isCreativeMode;
    }

    public float getFlySpeed() {
        return this.flySpeed;
    }

    public void setFlySpeed(float flySpeedIn) {
        this.flySpeed = flySpeedIn;
    }

    public float getWalkSpeed() {
        return this.walkSpeed;
    }

    public void setWalkSpeed(float walkSpeedIn) {
        this.walkSpeed = walkSpeedIn;
    }
}

