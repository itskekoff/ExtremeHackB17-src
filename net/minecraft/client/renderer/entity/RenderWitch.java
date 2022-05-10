package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.ModelWitch;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.layers.LayerHeldItemWitch;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.util.ResourceLocation;

public class RenderWitch
extends RenderLiving<EntityWitch> {
    private static final ResourceLocation WITCH_TEXTURES = new ResourceLocation("textures/entity/witch.png");

    public RenderWitch(RenderManager renderManagerIn) {
        super(renderManagerIn, new ModelWitch(0.0f), 0.5f);
        this.addLayer(new LayerHeldItemWitch(this));
    }

    @Override
    public ModelWitch getMainModel() {
        return (ModelWitch)super.getMainModel();
    }

    @Override
    public void doRender(EntityWitch entity, double x2, double y2, double z2, float entityYaw, float partialTicks) {
        ((ModelWitch)this.mainModel).holdingItem = !entity.getHeldItemMainhand().func_190926_b();
        super.doRender(entity, x2, y2, z2, entityYaw, partialTicks);
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityWitch entity) {
        return WITCH_TEXTURES;
    }

    @Override
    public void transformHeldFull3DItemLayer() {
        GlStateManager.translate(0.0f, 0.1875f, 0.0f);
    }

    @Override
    protected void preRenderCallback(EntityWitch entitylivingbaseIn, float partialTickTime) {
        float f2 = 0.9375f;
        GlStateManager.scale(0.9375f, 0.9375f, 0.9375f);
    }
}

