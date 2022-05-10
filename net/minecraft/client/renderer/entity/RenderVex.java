package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.ModelVex;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.monster.EntityVex;
import net.minecraft.util.ResourceLocation;

public class RenderVex
extends RenderBiped<EntityVex> {
    private static final ResourceLocation field_191343_a = new ResourceLocation("textures/entity/illager/vex.png");
    private static final ResourceLocation field_191344_j = new ResourceLocation("textures/entity/illager/vex_charging.png");
    private int field_191345_k;

    public RenderVex(RenderManager p_i47190_1_) {
        super(p_i47190_1_, new ModelVex(), 0.3f);
        this.field_191345_k = ((ModelVex)this.mainModel).func_191228_a();
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityVex entity) {
        return entity.func_190647_dj() ? field_191344_j : field_191343_a;
    }

    @Override
    public void doRender(EntityVex entity, double x2, double y2, double z2, float entityYaw, float partialTicks) {
        int i2 = ((ModelVex)this.mainModel).func_191228_a();
        if (i2 != this.field_191345_k) {
            this.mainModel = new ModelVex();
            this.field_191345_k = i2;
        }
        super.doRender(entity, x2, y2, z2, entityYaw, partialTicks);
    }

    @Override
    protected void preRenderCallback(EntityVex entitylivingbaseIn, float partialTickTime) {
        GlStateManager.scale(0.4f, 0.4f, 0.4f);
    }
}
