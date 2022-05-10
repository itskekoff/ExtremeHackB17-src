package net.minecraft.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.network.play.server.SPacketUpdateBossInfo;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.BossInfo;

public class BossInfoClient
extends BossInfo {
    protected float rawPercent;
    protected long percentSetTime;

    public BossInfoClient(SPacketUpdateBossInfo packetIn) {
        super(packetIn.getUniqueId(), packetIn.getName(), packetIn.getColor(), packetIn.getOverlay());
        this.rawPercent = packetIn.getPercent();
        this.percent = packetIn.getPercent();
        this.percentSetTime = Minecraft.getSystemTime();
        this.setDarkenSky(packetIn.shouldDarkenSky());
        this.setPlayEndBossMusic(packetIn.shouldPlayEndBossMusic());
        this.setCreateFog(packetIn.shouldCreateFog());
    }

    @Override
    public void setPercent(float percentIn) {
        this.percent = this.getPercent();
        this.rawPercent = percentIn;
        this.percentSetTime = Minecraft.getSystemTime();
    }

    @Override
    public float getPercent() {
        long i2 = Minecraft.getSystemTime() - this.percentSetTime;
        float f2 = MathHelper.clamp((float)i2 / 100.0f, 0.0f, 1.0f);
        return this.percent + (this.rawPercent - this.percent) * f2;
    }

    public void updateFromPacket(SPacketUpdateBossInfo packetIn) {
        switch (packetIn.getOperation()) {
            case UPDATE_NAME: {
                this.setName(packetIn.getName());
                break;
            }
            case UPDATE_PCT: {
                this.setPercent(packetIn.getPercent());
                break;
            }
            case UPDATE_STYLE: {
                this.setColor(packetIn.getColor());
                this.setOverlay(packetIn.getOverlay());
                break;
            }
            case UPDATE_PROPERTIES: {
                this.setDarkenSky(packetIn.shouldDarkenSky());
                this.setPlayEndBossMusic(packetIn.shouldPlayEndBossMusic());
            }
        }
    }
}

