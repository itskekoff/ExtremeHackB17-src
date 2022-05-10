package net.minecraft.client.renderer.entity.layers;

import net.minecraft.client.model.ModelSheep1;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderSheep;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.util.ResourceLocation;
import optifine.Config;
import optifine.CustomColors;

public class LayerSheepWool
implements LayerRenderer<EntitySheep> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("textures/entity/sheep/sheep_fur.png");
    private final RenderSheep sheepRenderer;
    public ModelSheep1 sheepModel = new ModelSheep1();

    public LayerSheepWool(RenderSheep sheepRendererIn) {
        this.sheepRenderer = sheepRendererIn;
    }

    @Override
    public void doRenderLayer(EntitySheep entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        if (!entitylivingbaseIn.getSheared() && !entitylivingbaseIn.isInvisible()) {
            this.sheepRenderer.bindTexture(TEXTURE);
            if (entitylivingbaseIn.hasCustomName() && "jeb_".equals(entitylivingbaseIn.getCustomNameTag())) {
                int i1 = 25;
                int i2 = entitylivingbaseIn.ticksExisted / 25 + entitylivingbaseIn.getEntityId();
                int j2 = EnumDyeColor.values().length;
                int k2 = i2 % j2;
                int l2 = (i2 + 1) % j2;
                float f2 = ((float)(entitylivingbaseIn.ticksExisted % 25) + partialTicks) / 25.0f;
                float[] afloat1 = EntitySheep.getDyeRgb(EnumDyeColor.byMetadata(k2));
                float[] afloat2 = EntitySheep.getDyeRgb(EnumDyeColor.byMetadata(l2));
                if (Config.isCustomColors()) {
                    afloat1 = CustomColors.getSheepColors(EnumDyeColor.byMetadata(k2), afloat1);
                    afloat2 = CustomColors.getSheepColors(EnumDyeColor.byMetadata(l2), afloat2);
                }
                GlStateManager.color(afloat1[0] * (1.0f - f2) + afloat2[0] * f2, afloat1[1] * (1.0f - f2) + afloat2[1] * f2, afloat1[2] * (1.0f - f2) + afloat2[2] * f2);
            } else {
                float[] afloat = EntitySheep.getDyeRgb(entitylivingbaseIn.getFleeceColor());
                if (Config.isCustomColors()) {
                    afloat = CustomColors.getSheepColors(entitylivingbaseIn.getFleeceColor(), afloat);
                }
                GlStateManager.color(afloat[0], afloat[1], afloat[2]);
            }
            this.sheepModel.setModelAttributes(this.sheepRenderer.getMainModel());
            this.sheepModel.setLivingAnimations(entitylivingbaseIn, limbSwing, limbSwingAmount, partialTicks);
            this.sheepModel.render(entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
        }
    }

    @Override
    public boolean shouldCombineTextures() {
        return true;
    }
}

