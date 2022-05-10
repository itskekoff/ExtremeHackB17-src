package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelEnderCrystal;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderDragon;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

public class RenderEnderCrystal
extends Render<EntityEnderCrystal> {
    private static final ResourceLocation ENDER_CRYSTAL_TEXTURES = new ResourceLocation("textures/entity/endercrystal/endercrystal.png");
    private final ModelBase modelEnderCrystal = new ModelEnderCrystal(0.0f, true);
    private final ModelBase modelEnderCrystalNoBase = new ModelEnderCrystal(0.0f, false);

    public RenderEnderCrystal(RenderManager renderManagerIn) {
        super(renderManagerIn);
        this.shadowSize = 0.5f;
    }

    @Override
    public void doRender(EntityEnderCrystal entity, double x2, double y2, double z2, float entityYaw, float partialTicks) {
        float f2 = (float)entity.innerRotation + partialTicks;
        GlStateManager.pushMatrix();
        GlStateManager.translate((float)x2, (float)y2, (float)z2);
        this.bindTexture(ENDER_CRYSTAL_TEXTURES);
        float f1 = MathHelper.sin(f2 * 0.2f) / 2.0f + 0.5f;
        f1 = f1 * f1 + f1;
        if (this.renderOutlines) {
            GlStateManager.enableColorMaterial();
            GlStateManager.enableOutlineMode(this.getTeamColor(entity));
        }
        if (entity.shouldShowBottom()) {
            this.modelEnderCrystal.render(entity, 0.0f, f2 * 3.0f, f1 * 0.2f, 0.0f, 0.0f, 0.0625f);
        } else {
            this.modelEnderCrystalNoBase.render(entity, 0.0f, f2 * 3.0f, f1 * 0.2f, 0.0f, 0.0f, 0.0625f);
        }
        if (this.renderOutlines) {
            GlStateManager.disableOutlineMode();
            GlStateManager.disableColorMaterial();
        }
        GlStateManager.popMatrix();
        BlockPos blockpos = entity.getBeamTarget();
        if (blockpos != null) {
            this.bindTexture(RenderDragon.ENDERCRYSTAL_BEAM_TEXTURES);
            float f22 = (float)blockpos.getX() + 0.5f;
            float f3 = (float)blockpos.getY() + 0.5f;
            float f4 = (float)blockpos.getZ() + 0.5f;
            double d0 = (double)f22 - entity.posX;
            double d1 = (double)f3 - entity.posY;
            double d2 = (double)f4 - entity.posZ;
            RenderDragon.renderCrystalBeams(x2 + d0, y2 - 0.3 + (double)(f1 * 0.4f) + d1, z2 + d2, partialTicks, f22, f3, f4, entity.innerRotation, entity.posX, entity.posY, entity.posZ);
        }
        super.doRender(entity, x2, y2, z2, entityYaw, partialTicks);
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityEnderCrystal entity) {
        return ENDER_CRYSTAL_TEXTURES;
    }

    @Override
    public boolean shouldRender(EntityEnderCrystal livingEntity, ICamera camera, double camX, double camY, double camZ) {
        return super.shouldRender(livingEntity, camera, camX, camY, camZ) || livingEntity.getBeamTarget() != null;
    }
}

