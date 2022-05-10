package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.ModelWither;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.layers.LayerWitherAura;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.util.ResourceLocation;

public class RenderWither
extends RenderLiving<EntityWither> {
    private static final ResourceLocation INVULNERABLE_WITHER_TEXTURES = new ResourceLocation("textures/entity/wither/wither_invulnerable.png");
    private static final ResourceLocation WITHER_TEXTURES = new ResourceLocation("textures/entity/wither/wither.png");

    public RenderWither(RenderManager renderManagerIn) {
        super(renderManagerIn, new ModelWither(0.0f), 1.0f);
        this.addLayer(new LayerWitherAura(this));
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityWither entity) {
        int i2 = entity.getInvulTime();
        return i2 > 0 && (i2 > 80 || i2 / 5 % 2 != 1) ? INVULNERABLE_WITHER_TEXTURES : WITHER_TEXTURES;
    }

    @Override
    protected void preRenderCallback(EntityWither entitylivingbaseIn, float partialTickTime) {
        float f2 = 2.0f;
        int i2 = entitylivingbaseIn.getInvulTime();
        if (i2 > 0) {
            f2 -= ((float)i2 - partialTickTime) / 220.0f * 0.5f;
        }
        GlStateManager.scale(f2, f2, f2);
    }
}

