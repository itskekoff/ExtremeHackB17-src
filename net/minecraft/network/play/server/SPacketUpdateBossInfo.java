package net.minecraft.network.play.server;

import java.io.IOException;
import java.util.UUID;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.BossInfo;

public class SPacketUpdateBossInfo
implements Packet<INetHandlerPlayClient> {
    private UUID uniqueId;
    private Operation operation;
    private ITextComponent name;
    private float percent;
    private BossInfo.Color color;
    private BossInfo.Overlay overlay;
    private boolean darkenSky;
    private boolean playEndBossMusic;
    private boolean createFog;

    public SPacketUpdateBossInfo() {
    }

    public SPacketUpdateBossInfo(Operation operationIn, BossInfo data) {
        this.operation = operationIn;
        this.uniqueId = data.getUniqueId();
        this.name = data.getName();
        this.percent = data.getPercent();
        this.color = data.getColor();
        this.overlay = data.getOverlay();
        this.darkenSky = data.shouldDarkenSky();
        this.playEndBossMusic = data.shouldPlayEndBossMusic();
        this.createFog = data.shouldCreateFog();
    }

    @Override
    public void readPacketData(PacketBuffer buf2) throws IOException {
        this.uniqueId = buf2.readUuid();
        this.operation = buf2.readEnumValue(Operation.class);
        switch (this.operation) {
            case ADD: {
                this.name = buf2.readTextComponent();
                this.percent = buf2.readFloat();
                this.color = buf2.readEnumValue(BossInfo.Color.class);
                this.overlay = buf2.readEnumValue(BossInfo.Overlay.class);
                this.setFlags(buf2.readUnsignedByte());
            }
            default: {
                break;
            }
            case UPDATE_PCT: {
                this.percent = buf2.readFloat();
                break;
            }
            case UPDATE_NAME: {
                this.name = buf2.readTextComponent();
                break;
            }
            case UPDATE_STYLE: {
                this.color = buf2.readEnumValue(BossInfo.Color.class);
                this.overlay = buf2.readEnumValue(BossInfo.Overlay.class);
                break;
            }
            case UPDATE_PROPERTIES: {
                this.setFlags(buf2.readUnsignedByte());
            }
        }
    }

    private void setFlags(int flags) {
        this.darkenSky = (flags & 1) > 0;
        this.playEndBossMusic = (flags & 2) > 0;
        this.createFog = (flags & 2) > 0;
    }

    @Override
    public void writePacketData(PacketBuffer buf2) throws IOException {
        buf2.writeUuid(this.uniqueId);
        buf2.writeEnumValue(this.operation);
        switch (this.operation) {
            case ADD: {
                buf2.writeTextComponent(this.name);
                buf2.writeFloat(this.percent);
                buf2.writeEnumValue(this.color);
                buf2.writeEnumValue(this.overlay);
                buf2.writeByte(this.getFlags());
            }
            default: {
                break;
            }
            case UPDATE_PCT: {
                buf2.writeFloat(this.percent);
                break;
            }
            case UPDATE_NAME: {
                buf2.writeTextComponent(this.name);
                break;
            }
            case UPDATE_STYLE: {
                buf2.writeEnumValue(this.color);
                buf2.writeEnumValue(this.overlay);
                break;
            }
            case UPDATE_PROPERTIES: {
                buf2.writeByte(this.getFlags());
            }
        }
    }

    private int getFlags() {
        int i2 = 0;
        if (this.darkenSky) {
            i2 |= 1;
        }
        if (this.playEndBossMusic) {
            i2 |= 2;
        }
        if (this.createFog) {
            i2 |= 2;
        }
        return i2;
    }

    @Override
    public void processPacket(INetHandlerPlayClient handler) {
        handler.handleUpdateEntityNBT(this);
    }

    public UUID getUniqueId() {
        return this.uniqueId;
    }

    public Operation getOperation() {
        return this.operation;
    }

    public ITextComponent getName() {
        return this.name;
    }

    public float getPercent() {
        return this.percent;
    }

    public BossInfo.Color getColor() {
        return this.color;
    }

    public BossInfo.Overlay getOverlay() {
        return this.overlay;
    }

    public boolean shouldDarkenSky() {
        return this.darkenSky;
    }

    public boolean shouldPlayEndBossMusic() {
        return this.playEndBossMusic;
    }

    public boolean shouldCreateFog() {
        return this.createFog;
    }

    public static enum Operation {
        ADD,
        REMOVE,
        UPDATE_PCT,
        UPDATE_NAME,
        UPDATE_STYLE,
        UPDATE_PROPERTIES;

    }
}

