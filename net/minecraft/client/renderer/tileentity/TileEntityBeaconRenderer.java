package net.minecraft.client.renderer.tileentity;

import java.util.List;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.tileentity.TileEntityBeacon;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import optifine.Config;
import shadersmod.client.Shaders;

public class TileEntityBeaconRenderer
extends TileEntitySpecialRenderer<TileEntityBeacon> {
    public static final ResourceLocation TEXTURE_BEACON_BEAM = new ResourceLocation("textures/entity/beacon_beam.png");

    @Override
    public void func_192841_a(TileEntityBeacon p_192841_1_, double p_192841_2_, double p_192841_4_, double p_192841_6_, float p_192841_8_, int p_192841_9_, float p_192841_10_) {
        this.renderBeacon(p_192841_2_, p_192841_4_, p_192841_6_, p_192841_8_, p_192841_1_.shouldBeamRender(), p_192841_1_.getBeamSegments(), p_192841_1_.getWorld().getTotalWorldTime());
    }

    public void renderBeacon(double p_188206_1_, double p_188206_3_, double p_188206_5_, double p_188206_7_, double p_188206_9_, List<TileEntityBeacon.BeamSegment> p_188206_11_, double p_188206_12_) {
        if (p_188206_9_ > 0.0 && p_188206_11_.size() > 0) {
            if (Config.isShaders()) {
                Shaders.beginBeacon();
            }
            GlStateManager.alphaFunc(516, 0.1f);
            this.bindTexture(TEXTURE_BEACON_BEAM);
            if (p_188206_9_ > 0.0) {
                GlStateManager.disableFog();
                int i2 = 0;
                for (int j2 = 0; j2 < p_188206_11_.size(); ++j2) {
                    TileEntityBeacon.BeamSegment tileentitybeacon$beamsegment = p_188206_11_.get(j2);
                    TileEntityBeaconRenderer.renderBeamSegment(p_188206_1_, p_188206_3_, p_188206_5_, p_188206_7_, p_188206_9_, p_188206_12_, i2, tileentitybeacon$beamsegment.getHeight(), tileentitybeacon$beamsegment.getColors());
                    i2 += tileentitybeacon$beamsegment.getHeight();
                }
                GlStateManager.enableFog();
            }
            if (Config.isShaders()) {
                Shaders.endBeacon();
            }
        }
    }

    public static void renderBeamSegment(double x2, double y2, double z2, double partialTicks, double textureScale, double totalWorldTime, int yOffset, int height, float[] colors) {
        TileEntityBeaconRenderer.renderBeamSegment(x2, y2, z2, partialTicks, textureScale, totalWorldTime, yOffset, height, colors, 0.2, 0.25);
    }

    public static void renderBeamSegment(double x2, double y2, double z2, double partialTicks, double textureScale, double totalWorldTime, int yOffset, int height, float[] colors, double beamRadius, double glowRadius) {
        int i2 = yOffset + height;
        GlStateManager.glTexParameteri(3553, 10242, 10497);
        GlStateManager.glTexParameteri(3553, 10243, 10497);
        GlStateManager.disableLighting();
        GlStateManager.disableCull();
        GlStateManager.disableBlend();
        GlStateManager.depthMask(true);
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        double d0 = totalWorldTime + partialTicks;
        double d1 = height < 0 ? d0 : -d0;
        double d2 = MathHelper.frac(d1 * 0.2 - (double)MathHelper.floor(d1 * 0.1));
        float f2 = colors[0];
        float f1 = colors[1];
        float f22 = colors[2];
        double d3 = d0 * 0.025 * -1.5;
        double d4 = 0.5 + Math.cos(d3 + 2.356194490192345) * beamRadius;
        double d5 = 0.5 + Math.sin(d3 + 2.356194490192345) * beamRadius;
        double d6 = 0.5 + Math.cos(d3 + 0.7853981633974483) * beamRadius;
        double d7 = 0.5 + Math.sin(d3 + 0.7853981633974483) * beamRadius;
        double d8 = 0.5 + Math.cos(d3 + 3.9269908169872414) * beamRadius;
        double d9 = 0.5 + Math.sin(d3 + 3.9269908169872414) * beamRadius;
        double d10 = 0.5 + Math.cos(d3 + 5.497787143782138) * beamRadius;
        double d11 = 0.5 + Math.sin(d3 + 5.497787143782138) * beamRadius;
        double d12 = 0.0;
        double d13 = 1.0;
        double d14 = -1.0 + d2;
        double d15 = (double)height * textureScale * (0.5 / beamRadius) + d14;
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
        bufferbuilder.pos(x2 + d4, y2 + (double)i2, z2 + d5).tex(1.0, d15).color(f2, f1, f22, 1.0f).endVertex();
        bufferbuilder.pos(x2 + d4, y2 + (double)yOffset, z2 + d5).tex(1.0, d14).color(f2, f1, f22, 1.0f).endVertex();
        bufferbuilder.pos(x2 + d6, y2 + (double)yOffset, z2 + d7).tex(0.0, d14).color(f2, f1, f22, 1.0f).endVertex();
        bufferbuilder.pos(x2 + d6, y2 + (double)i2, z2 + d7).tex(0.0, d15).color(f2, f1, f22, 1.0f).endVertex();
        bufferbuilder.pos(x2 + d10, y2 + (double)i2, z2 + d11).tex(1.0, d15).color(f2, f1, f22, 1.0f).endVertex();
        bufferbuilder.pos(x2 + d10, y2 + (double)yOffset, z2 + d11).tex(1.0, d14).color(f2, f1, f22, 1.0f).endVertex();
        bufferbuilder.pos(x2 + d8, y2 + (double)yOffset, z2 + d9).tex(0.0, d14).color(f2, f1, f22, 1.0f).endVertex();
        bufferbuilder.pos(x2 + d8, y2 + (double)i2, z2 + d9).tex(0.0, d15).color(f2, f1, f22, 1.0f).endVertex();
        bufferbuilder.pos(x2 + d6, y2 + (double)i2, z2 + d7).tex(1.0, d15).color(f2, f1, f22, 1.0f).endVertex();
        bufferbuilder.pos(x2 + d6, y2 + (double)yOffset, z2 + d7).tex(1.0, d14).color(f2, f1, f22, 1.0f).endVertex();
        bufferbuilder.pos(x2 + d10, y2 + (double)yOffset, z2 + d11).tex(0.0, d14).color(f2, f1, f22, 1.0f).endVertex();
        bufferbuilder.pos(x2 + d10, y2 + (double)i2, z2 + d11).tex(0.0, d15).color(f2, f1, f22, 1.0f).endVertex();
        bufferbuilder.pos(x2 + d8, y2 + (double)i2, z2 + d9).tex(1.0, d15).color(f2, f1, f22, 1.0f).endVertex();
        bufferbuilder.pos(x2 + d8, y2 + (double)yOffset, z2 + d9).tex(1.0, d14).color(f2, f1, f22, 1.0f).endVertex();
        bufferbuilder.pos(x2 + d4, y2 + (double)yOffset, z2 + d5).tex(0.0, d14).color(f2, f1, f22, 1.0f).endVertex();
        bufferbuilder.pos(x2 + d4, y2 + (double)i2, z2 + d5).tex(0.0, d15).color(f2, f1, f22, 1.0f).endVertex();
        tessellator.draw();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.depthMask(false);
        d3 = 0.5 - glowRadius;
        d4 = 0.5 - glowRadius;
        d5 = 0.5 + glowRadius;
        d6 = 0.5 - glowRadius;
        d7 = 0.5 - glowRadius;
        d8 = 0.5 + glowRadius;
        d9 = 0.5 + glowRadius;
        d10 = 0.5 + glowRadius;
        d11 = 0.0;
        d12 = 1.0;
        d13 = -1.0 + d2;
        d14 = (double)height * textureScale + d13;
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
        bufferbuilder.pos(x2 + d3, y2 + (double)i2, z2 + d4).tex(1.0, d14).color(f2, f1, f22, 0.125f).endVertex();
        bufferbuilder.pos(x2 + d3, y2 + (double)yOffset, z2 + d4).tex(1.0, d13).color(f2, f1, f22, 0.125f).endVertex();
        bufferbuilder.pos(x2 + d5, y2 + (double)yOffset, z2 + d6).tex(0.0, d13).color(f2, f1, f22, 0.125f).endVertex();
        bufferbuilder.pos(x2 + d5, y2 + (double)i2, z2 + d6).tex(0.0, d14).color(f2, f1, f22, 0.125f).endVertex();
        bufferbuilder.pos(x2 + d9, y2 + (double)i2, z2 + d10).tex(1.0, d14).color(f2, f1, f22, 0.125f).endVertex();
        bufferbuilder.pos(x2 + d9, y2 + (double)yOffset, z2 + d10).tex(1.0, d13).color(f2, f1, f22, 0.125f).endVertex();
        bufferbuilder.pos(x2 + d7, y2 + (double)yOffset, z2 + d8).tex(0.0, d13).color(f2, f1, f22, 0.125f).endVertex();
        bufferbuilder.pos(x2 + d7, y2 + (double)i2, z2 + d8).tex(0.0, d14).color(f2, f1, f22, 0.125f).endVertex();
        bufferbuilder.pos(x2 + d5, y2 + (double)i2, z2 + d6).tex(1.0, d14).color(f2, f1, f22, 0.125f).endVertex();
        bufferbuilder.pos(x2 + d5, y2 + (double)yOffset, z2 + d6).tex(1.0, d13).color(f2, f1, f22, 0.125f).endVertex();
        bufferbuilder.pos(x2 + d9, y2 + (double)yOffset, z2 + d10).tex(0.0, d13).color(f2, f1, f22, 0.125f).endVertex();
        bufferbuilder.pos(x2 + d9, y2 + (double)i2, z2 + d10).tex(0.0, d14).color(f2, f1, f22, 0.125f).endVertex();
        bufferbuilder.pos(x2 + d7, y2 + (double)i2, z2 + d8).tex(1.0, d14).color(f2, f1, f22, 0.125f).endVertex();
        bufferbuilder.pos(x2 + d7, y2 + (double)yOffset, z2 + d8).tex(1.0, d13).color(f2, f1, f22, 0.125f).endVertex();
        bufferbuilder.pos(x2 + d3, y2 + (double)yOffset, z2 + d4).tex(0.0, d13).color(f2, f1, f22, 0.125f).endVertex();
        bufferbuilder.pos(x2 + d3, y2 + (double)i2, z2 + d4).tex(0.0, d14).color(f2, f1, f22, 0.125f).endVertex();
        tessellator.draw();
        GlStateManager.enableLighting();
        GlStateManager.enableTexture2D();
        GlStateManager.depthMask(true);
    }

    @Override
    public boolean isGlobalRenderer(TileEntityBeacon te2) {
        return true;
    }
}

