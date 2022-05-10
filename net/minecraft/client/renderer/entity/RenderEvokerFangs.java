package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.ModelEvokerFangs;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.projectile.EntityEvokerFangs;
import net.minecraft.util.ResourceLocation;

public class RenderEvokerFangs
extends Render<EntityEvokerFangs> {
    private static final ResourceLocation field_191329_a = new ResourceLocation("textures/entity/illager/fangs.png");
    private final ModelEvokerFangs field_191330_f = new ModelEvokerFangs();

    public RenderEvokerFangs(RenderManager p_i47208_1_) {
        super(p_i47208_1_);
    }

    @Override
    public void doRender(EntityEvokerFangs entity, double x2, double y2, double z2, float entityYaw, float partialTicks) {
        float f2 = entity.func_190550_a(partialTicks);
        if (f2 != 0.0f) {
            float f1 = 2.0f;
            if (f2 > 0.9f) {
                f1 = (float)((double)f1 * ((1.0 - (double)f2) / (double)0.1f));
            }
            GlStateManager.pushMatrix();
            GlStateManager.disableCull();
            GlStateManager.enableAlpha();
            this.bindEntityTexture(entity);
            GlStateManager.translate((float)x2, (float)y2, (float)z2);
            GlStateManager.rotate(90.0f - entity.rotationYaw, 0.0f, 1.0f, 0.0f);
            GlStateManager.scale(-f1, -f1, f1);
            float f22 = 0.03125f;
            GlStateManager.translate(0.0f, -0.626f, 0.0f);
            this.field_191330_f.render(entity, f2, 0.0f, 0.0f, entity.rotationYaw, entity.rotationPitch, 0.03125f);
            GlStateManager.popMatrix();
            GlStateManager.enableCull();
            super.doRender(entity, x2, y2, z2, entityYaw, partialTicks);
        }
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityEvokerFangs entity) {
        return field_191329_a;
    }
}

