package net.minecraft.client.gui;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.BossInfoClient;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.network.play.server.SPacketUpdateBossInfo;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.BossInfo;

public class GuiBossOverlay
extends Gui {
    private static final ResourceLocation GUI_BARS_TEXTURES = new ResourceLocation("textures/gui/bars.png");
    private final Minecraft client;
    private final Map<UUID, BossInfoClient> mapBossInfos = Maps.newLinkedHashMap();

    public GuiBossOverlay(Minecraft clientIn) {
        this.client = clientIn;
    }

    public void renderBossHealth() {
        if (!this.mapBossInfos.isEmpty()) {
            ScaledResolution scaledresolution = new ScaledResolution(this.client);
            int i2 = scaledresolution.getScaledWidth();
            int j2 = 12;
            for (BossInfoClient bossinfoclient : this.mapBossInfos.values()) {
                int k2 = i2 / 2 - 91;
                GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
                this.client.getTextureManager().bindTexture(GUI_BARS_TEXTURES);
                this.render(k2, j2, bossinfoclient);
                String s2 = bossinfoclient.getName().getFormattedText();
                this.client.fontRendererObj.drawStringWithShadow(s2, i2 / 2 - this.client.fontRendererObj.getStringWidth(s2) / 2, j2 - 9, 0xFFFFFF);
                if ((j2 += 10 + this.client.fontRendererObj.FONT_HEIGHT) >= scaledresolution.getScaledHeight() / 3) break;
            }
        }
    }

    private void render(int x2, int y2, BossInfo info) {
        int i2;
        this.drawTexturedModalRect(x2, y2, 0, info.getColor().ordinal() * 5 * 2, 182, 5);
        if (info.getOverlay() != BossInfo.Overlay.PROGRESS) {
            this.drawTexturedModalRect(x2, y2, 0, 80 + (info.getOverlay().ordinal() - 1) * 5 * 2, 182, 5);
        }
        if ((i2 = (int)(info.getPercent() * 183.0f)) > 0) {
            this.drawTexturedModalRect(x2, y2, 0, info.getColor().ordinal() * 5 * 2 + 5, i2, 5);
            if (info.getOverlay() != BossInfo.Overlay.PROGRESS) {
                this.drawTexturedModalRect(x2, y2, 0, 80 + (info.getOverlay().ordinal() - 1) * 5 * 2 + 5, i2, 5);
            }
        }
    }

    public void read(SPacketUpdateBossInfo packetIn) {
        if (packetIn.getOperation() == SPacketUpdateBossInfo.Operation.ADD) {
            this.mapBossInfos.put(packetIn.getUniqueId(), new BossInfoClient(packetIn));
        } else if (packetIn.getOperation() == SPacketUpdateBossInfo.Operation.REMOVE) {
            this.mapBossInfos.remove(packetIn.getUniqueId());
        } else {
            this.mapBossInfos.get(packetIn.getUniqueId()).updateFromPacket(packetIn);
        }
    }

    public void clearBossInfos() {
        this.mapBossInfos.clear();
    }

    public boolean shouldPlayEndBossMusic() {
        if (!this.mapBossInfos.isEmpty()) {
            for (BossInfo bossInfo : this.mapBossInfos.values()) {
                if (!bossInfo.shouldPlayEndBossMusic()) continue;
                return true;
            }
        }
        return false;
    }

    public boolean shouldDarkenSky() {
        if (!this.mapBossInfos.isEmpty()) {
            for (BossInfo bossInfo : this.mapBossInfos.values()) {
                if (!bossInfo.shouldDarkenSky()) continue;
                return true;
            }
        }
        return false;
    }

    public boolean shouldCreateFog() {
        if (!this.mapBossInfos.isEmpty()) {
            for (BossInfo bossInfo : this.mapBossInfos.values()) {
                if (!bossInfo.shouldCreateFog()) continue;
                return true;
            }
        }
        return false;
    }
}

