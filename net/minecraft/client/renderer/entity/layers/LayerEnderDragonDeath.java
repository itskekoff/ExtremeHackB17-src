package net.minecraft.client.renderer.entity.layers;

import java.util.Random;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.boss.EntityDragon;

public class LayerEnderDragonDeath
implements LayerRenderer<EntityDragon> {
    @Override
    public void doRenderLayer(EntityDragon entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        if (entitylivingbaseIn.deathTicks > 0) {
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder bufferbuilder = tessellator.getBuffer();
            RenderHelper.disableStandardItemLighting();
            float f2 = ((float)entitylivingbaseIn.deathTicks + partialTicks) / 200.0f;
            float f1 = 0.0f;
            if (f2 > 0.8f) {
                f1 = (f2 - 0.8f) / 0.2f;
            }
            Random random = new Random(432L);
            GlStateManager.disableTexture2D();
            GlStateManager.shadeModel(7425);
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
            GlStateManager.disableAlpha();
            GlStateManager.enableCull();
            GlStateManager.depthMask(false);
            GlStateManager.pushMatrix();
            GlStateManager.translate(0.0f, -1.0f, -2.0f);
            int i2 = 0;
            while ((float)i2 < (f2 + f2 * f2) / 2.0f * 60.0f) {
                GlStateManager.rotate(random.nextFloat() * 360.0f, 1.0f, 0.0f, 0.0f);
                GlStateManager.rotate(random.nextFloat() * 360.0f, 0.0f, 1.0f, 0.0f);
                GlStateManager.rotate(random.nextFloat() * 360.0f, 0.0f, 0.0f, 1.0f);
                GlStateManager.rotate(random.nextFloat() * 360.0f, 1.0f, 0.0f, 0.0f);
                GlStateManager.rotate(random.nextFloat() * 360.0f, 0.0f, 1.0f, 0.0f);
                GlStateManager.rotate(random.nextFloat() * 360.0f + f2 * 90.0f, 0.0f, 0.0f, 1.0f);
                float f22 = random.nextFloat() * 20.0f + 5.0f + f1 * 10.0f;
                float f3 = random.nextFloat() * 2.0f + 1.0f + f1 * 2.0f;
                bufferbuilder.begin(6, DefaultVertexFormats.POSITION_COLOR);
                bufferbuilder.pos(0.0, 0.0, 0.0).color(255, 255, 255, (int)(255.0f * (1.0f - f1))).endVertex();
                bufferbuilder.pos(-0.866 * (double)f3, f22, -0.5f * f3).color(255, 0, 255, 0).endVertex();
                bufferbuilder.pos(0.866 * (double)f3, f22, -0.5f * f3).color(255, 0, 255, 0).endVertex();
                bufferbuilder.pos(0.0, f22, 1.0f * f3).color(255, 0, 255, 0).endVertex();
                bufferbuilder.pos(-0.866 * (double)f3, f22, -0.5f * f3).color(255, 0, 255, 0).endVertex();
                tessellator.draw();
                ++i2;
            }
            GlStateManager.popMatrix();
            GlStateManager.depthMask(true);
            GlStateManager.disableCull();
            GlStateManager.disableBlend();
            GlStateManager.shadeModel(7424);
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
            GlStateManager.enableTexture2D();
            GlStateManager.enableAlpha();
            RenderHelper.enableStandardItemLighting();
        }
    }

    @Override
    public boolean shouldCombineTextures() {
        return false;
    }
}

