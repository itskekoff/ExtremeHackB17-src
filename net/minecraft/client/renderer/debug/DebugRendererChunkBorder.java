package net.minecraft.client.renderer.debug;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.debug.DebugRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;

public class DebugRendererChunkBorder
implements DebugRenderer.IDebugRenderer {
    private final Minecraft minecraft;

    public DebugRendererChunkBorder(Minecraft minecraftIn) {
        this.minecraft = minecraftIn;
    }

    @Override
    public void render(float p_190060_1_, long p_190060_2_) {
        EntityPlayerSP entityplayer = this.minecraft.player;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        double d0 = entityplayer.lastTickPosX + (entityplayer.posX - entityplayer.lastTickPosX) * (double)p_190060_1_;
        double d1 = entityplayer.lastTickPosY + (entityplayer.posY - entityplayer.lastTickPosY) * (double)p_190060_1_;
        double d2 = entityplayer.lastTickPosZ + (entityplayer.posZ - entityplayer.lastTickPosZ) * (double)p_190060_1_;
        double d3 = 0.0 - d1;
        double d4 = 256.0 - d1;
        GlStateManager.disableTexture2D();
        GlStateManager.disableBlend();
        double d5 = (double)(entityplayer.chunkCoordX << 4) - d0;
        double d6 = (double)(entityplayer.chunkCoordZ << 4) - d2;
        GlStateManager.glLineWidth(1.0f);
        bufferbuilder.begin(3, DefaultVertexFormats.POSITION_COLOR);
        for (int i2 = -16; i2 <= 32; i2 += 16) {
            for (int j2 = -16; j2 <= 32; j2 += 16) {
                bufferbuilder.pos(d5 + (double)i2, d3, d6 + (double)j2).color(1.0f, 0.0f, 0.0f, 0.0f).endVertex();
                bufferbuilder.pos(d5 + (double)i2, d3, d6 + (double)j2).color(1.0f, 0.0f, 0.0f, 0.5f).endVertex();
                bufferbuilder.pos(d5 + (double)i2, d4, d6 + (double)j2).color(1.0f, 0.0f, 0.0f, 0.5f).endVertex();
                bufferbuilder.pos(d5 + (double)i2, d4, d6 + (double)j2).color(1.0f, 0.0f, 0.0f, 0.0f).endVertex();
            }
        }
        for (int k2 = 2; k2 < 16; k2 += 2) {
            bufferbuilder.pos(d5 + (double)k2, d3, d6).color(1.0f, 1.0f, 0.0f, 0.0f).endVertex();
            bufferbuilder.pos(d5 + (double)k2, d3, d6).color(1.0f, 1.0f, 0.0f, 1.0f).endVertex();
            bufferbuilder.pos(d5 + (double)k2, d4, d6).color(1.0f, 1.0f, 0.0f, 1.0f).endVertex();
            bufferbuilder.pos(d5 + (double)k2, d4, d6).color(1.0f, 1.0f, 0.0f, 0.0f).endVertex();
            bufferbuilder.pos(d5 + (double)k2, d3, d6 + 16.0).color(1.0f, 1.0f, 0.0f, 0.0f).endVertex();
            bufferbuilder.pos(d5 + (double)k2, d3, d6 + 16.0).color(1.0f, 1.0f, 0.0f, 1.0f).endVertex();
            bufferbuilder.pos(d5 + (double)k2, d4, d6 + 16.0).color(1.0f, 1.0f, 0.0f, 1.0f).endVertex();
            bufferbuilder.pos(d5 + (double)k2, d4, d6 + 16.0).color(1.0f, 1.0f, 0.0f, 0.0f).endVertex();
        }
        for (int l2 = 2; l2 < 16; l2 += 2) {
            bufferbuilder.pos(d5, d3, d6 + (double)l2).color(1.0f, 1.0f, 0.0f, 0.0f).endVertex();
            bufferbuilder.pos(d5, d3, d6 + (double)l2).color(1.0f, 1.0f, 0.0f, 1.0f).endVertex();
            bufferbuilder.pos(d5, d4, d6 + (double)l2).color(1.0f, 1.0f, 0.0f, 1.0f).endVertex();
            bufferbuilder.pos(d5, d4, d6 + (double)l2).color(1.0f, 1.0f, 0.0f, 0.0f).endVertex();
            bufferbuilder.pos(d5 + 16.0, d3, d6 + (double)l2).color(1.0f, 1.0f, 0.0f, 0.0f).endVertex();
            bufferbuilder.pos(d5 + 16.0, d3, d6 + (double)l2).color(1.0f, 1.0f, 0.0f, 1.0f).endVertex();
            bufferbuilder.pos(d5 + 16.0, d4, d6 + (double)l2).color(1.0f, 1.0f, 0.0f, 1.0f).endVertex();
            bufferbuilder.pos(d5 + 16.0, d4, d6 + (double)l2).color(1.0f, 1.0f, 0.0f, 0.0f).endVertex();
        }
        for (int i1 = 0; i1 <= 256; i1 += 2) {
            double d7 = (double)i1 - d1;
            bufferbuilder.pos(d5, d7, d6).color(1.0f, 1.0f, 0.0f, 0.0f).endVertex();
            bufferbuilder.pos(d5, d7, d6).color(1.0f, 1.0f, 0.0f, 1.0f).endVertex();
            bufferbuilder.pos(d5, d7, d6 + 16.0).color(1.0f, 1.0f, 0.0f, 1.0f).endVertex();
            bufferbuilder.pos(d5 + 16.0, d7, d6 + 16.0).color(1.0f, 1.0f, 0.0f, 1.0f).endVertex();
            bufferbuilder.pos(d5 + 16.0, d7, d6).color(1.0f, 1.0f, 0.0f, 1.0f).endVertex();
            bufferbuilder.pos(d5, d7, d6).color(1.0f, 1.0f, 0.0f, 1.0f).endVertex();
            bufferbuilder.pos(d5, d7, d6).color(1.0f, 1.0f, 0.0f, 0.0f).endVertex();
        }
        tessellator.draw();
        GlStateManager.glLineWidth(2.0f);
        bufferbuilder.begin(3, DefaultVertexFormats.POSITION_COLOR);
        for (int j1 = 0; j1 <= 16; j1 += 16) {
            for (int l1 = 0; l1 <= 16; l1 += 16) {
                bufferbuilder.pos(d5 + (double)j1, d3, d6 + (double)l1).color(0.25f, 0.25f, 1.0f, 0.0f).endVertex();
                bufferbuilder.pos(d5 + (double)j1, d3, d6 + (double)l1).color(0.25f, 0.25f, 1.0f, 1.0f).endVertex();
                bufferbuilder.pos(d5 + (double)j1, d4, d6 + (double)l1).color(0.25f, 0.25f, 1.0f, 1.0f).endVertex();
                bufferbuilder.pos(d5 + (double)j1, d4, d6 + (double)l1).color(0.25f, 0.25f, 1.0f, 0.0f).endVertex();
            }
        }
        for (int k1 = 0; k1 <= 256; k1 += 16) {
            double d8 = (double)k1 - d1;
            bufferbuilder.pos(d5, d8, d6).color(0.25f, 0.25f, 1.0f, 0.0f).endVertex();
            bufferbuilder.pos(d5, d8, d6).color(0.25f, 0.25f, 1.0f, 1.0f).endVertex();
            bufferbuilder.pos(d5, d8, d6 + 16.0).color(0.25f, 0.25f, 1.0f, 1.0f).endVertex();
            bufferbuilder.pos(d5 + 16.0, d8, d6 + 16.0).color(0.25f, 0.25f, 1.0f, 1.0f).endVertex();
            bufferbuilder.pos(d5 + 16.0, d8, d6).color(0.25f, 0.25f, 1.0f, 1.0f).endVertex();
            bufferbuilder.pos(d5, d8, d6).color(0.25f, 0.25f, 1.0f, 1.0f).endVertex();
            bufferbuilder.pos(d5, d8, d6).color(0.25f, 0.25f, 1.0f, 0.0f).endVertex();
        }
        tessellator.draw();
        GlStateManager.glLineWidth(1.0f);
        GlStateManager.enableBlend();
        GlStateManager.enableTexture2D();
    }
}

