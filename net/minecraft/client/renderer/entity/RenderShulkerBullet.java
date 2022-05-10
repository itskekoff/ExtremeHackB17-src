package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.ModelShulkerBullet;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.projectile.EntityShulkerBullet;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

public class RenderShulkerBullet
extends Render<EntityShulkerBullet> {
    private static final ResourceLocation SHULKER_SPARK_TEXTURE = new ResourceLocation("textures/entity/shulker/spark.png");
    private final ModelShulkerBullet model = new ModelShulkerBullet();

    public RenderShulkerBullet(RenderManager manager) {
        super(manager);
    }

    private float rotLerp(float p_188347_1_, float p_188347_2_, float p_188347_3_) {
        float f2;
        for (f2 = p_188347_2_ - p_188347_1_; f2 < -180.0f; f2 += 360.0f) {
        }
        while (f2 >= 180.0f) {
            f2 -= 360.0f;
        }
        return p_188347_1_ + p_188347_3_ * f2;
    }

    @Override
    public void doRender(EntityShulkerBullet entity, double x2, double y2, double z2, float entityYaw, float partialTicks) {
        GlStateManager.pushMatrix();
        float f2 = this.rotLerp(entity.prevRotationYaw, entity.rotationYaw, partialTicks);
        float f1 = entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks;
        float f22 = (float)entity.ticksExisted + partialTicks;
        GlStateManager.translate((float)x2, (float)y2 + 0.15f, (float)z2);
        GlStateManager.rotate(MathHelper.sin(f22 * 0.1f) * 180.0f, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotate(MathHelper.cos(f22 * 0.1f) * 180.0f, 1.0f, 0.0f, 0.0f);
        GlStateManager.rotate(MathHelper.sin(f22 * 0.15f) * 360.0f, 0.0f, 0.0f, 1.0f);
        float f3 = 0.03125f;
        GlStateManager.enableRescaleNormal();
        GlStateManager.scale(-1.0f, -1.0f, 1.0f);
        this.bindEntityTexture(entity);
        this.model.render(entity, 0.0f, 0.0f, 0.0f, f2, f1, 0.03125f);
        GlStateManager.enableBlend();
        GlStateManager.color(1.0f, 1.0f, 1.0f, 0.5f);
        GlStateManager.scale(1.5f, 1.5f, 1.5f);
        this.model.render(entity, 0.0f, 0.0f, 0.0f, f2, f1, 0.03125f);
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
        super.doRender(entity, x2, y2, z2, entityYaw, partialTicks);
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityShulkerBullet entity) {
        return SHULKER_SPARK_TEXTURE;
    }
}

