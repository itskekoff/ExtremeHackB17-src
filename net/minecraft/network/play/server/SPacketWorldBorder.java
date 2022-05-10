package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.world.border.WorldBorder;

public class SPacketWorldBorder
implements Packet<INetHandlerPlayClient> {
    private Action action;
    private int size;
    private double centerX;
    private double centerZ;
    private double targetSize;
    private double diameter;
    private long timeUntilTarget;
    private int warningTime;
    private int warningDistance;

    public SPacketWorldBorder() {
    }

    public SPacketWorldBorder(WorldBorder border, Action actionIn) {
        this.action = actionIn;
        this.centerX = border.getCenterX();
        this.centerZ = border.getCenterZ();
        this.diameter = border.getDiameter();
        this.targetSize = border.getTargetSize();
        this.timeUntilTarget = border.getTimeUntilTarget();
        this.size = border.getSize();
        this.warningDistance = border.getWarningDistance();
        this.warningTime = border.getWarningTime();
    }

    @Override
    public void readPacketData(PacketBuffer buf2) throws IOException {
        this.action = buf2.readEnumValue(Action.class);
        switch (this.action) {
            case SET_SIZE: {
                this.targetSize = buf2.readDouble();
                break;
            }
            case LERP_SIZE: {
                this.diameter = buf2.readDouble();
                this.targetSize = buf2.readDouble();
                this.timeUntilTarget = buf2.readVarLong();
                break;
            }
            case SET_CENTER: {
                this.centerX = buf2.readDouble();
                this.centerZ = buf2.readDouble();
                break;
            }
            case SET_WARNING_BLOCKS: {
                this.warningDistance = buf2.readVarIntFromBuffer();
                break;
            }
            case SET_WARNING_TIME: {
                this.warningTime = buf2.readVarIntFromBuffer();
                break;
            }
            case INITIALIZE: {
                this.centerX = buf2.readDouble();
                this.centerZ = buf2.readDouble();
                this.diameter = buf2.readDouble();
                this.targetSize = buf2.readDouble();
                this.timeUntilTarget = buf2.readVarLong();
                this.size = buf2.readVarIntFromBuffer();
                this.warningDistance = buf2.readVarIntFromBuffer();
                this.warningTime = buf2.readVarIntFromBuffer();
            }
        }
    }

    @Override
    public void writePacketData(PacketBuffer buf2) throws IOException {
        buf2.writeEnumValue(this.action);
        switch (this.action) {
            case SET_SIZE: {
                buf2.writeDouble(this.targetSize);
                break;
            }
            case LERP_SIZE: {
                buf2.writeDouble(this.diameter);
                buf2.writeDouble(this.targetSize);
                buf2.writeVarLong(this.timeUntilTarget);
                break;
            }
            case SET_CENTER: {
                buf2.writeDouble(this.centerX);
                buf2.writeDouble(this.centerZ);
                break;
            }
            case SET_WARNING_BLOCKS: {
                buf2.writeVarIntToBuffer(this.warningDistance);
                break;
            }
            case SET_WARNING_TIME: {
                buf2.writeVarIntToBuffer(this.warningTime);
                break;
            }
            case INITIALIZE: {
                buf2.writeDouble(this.centerX);
                buf2.writeDouble(this.centerZ);
                buf2.writeDouble(this.diameter);
                buf2.writeDouble(this.targetSize);
                buf2.writeVarLong(this.timeUntilTarget);
                buf2.writeVarIntToBuffer(this.size);
                buf2.writeVarIntToBuffer(this.warningDistance);
                buf2.writeVarIntToBuffer(this.warningTime);
            }
        }
    }

    @Override
    public void processPacket(INetHandlerPlayClient handler) {
        handler.handleWorldBorder(this);
    }

    public void apply(WorldBorder border) {
        switch (this.action) {
            case SET_SIZE: {
                border.setTransition(this.targetSize);
                break;
            }
            case LERP_SIZE: {
                border.setTransition(this.diameter, this.targetSize, this.timeUntilTarget);
                break;
            }
            case SET_CENTER: {
                border.setCenter(this.centerX, this.centerZ);
                break;
            }
            case SET_WARNING_BLOCKS: {
                border.setWarningDistance(this.warningDistance);
                break;
            }
            case SET_WARNING_TIME: {
                border.setWarningTime(this.warningTime);
                break;
            }
            case INITIALIZE: {
                border.setCenter(this.centerX, this.centerZ);
                if (this.timeUntilTarget > 0L) {
                    border.setTransition(this.diameter, this.targetSize, this.timeUntilTarget);
                } else {
                    border.setTransition(this.targetSize);
                }
                border.setSize(this.size);
                border.setWarningDistance(this.warningDistance);
                border.setWarningTime(this.warningTime);
            }
        }
    }

    public static enum Action {
        SET_SIZE,
        LERP_SIZE,
        SET_CENTER,
        INITIALIZE,
        SET_WARNING_TIME,
        SET_WARNING_BLOCKS;

    }
}

