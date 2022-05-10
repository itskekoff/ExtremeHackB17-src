package net.minecraft.client.model;

import java.util.Random;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

public class ModelGhast
extends ModelBase {
    ModelRenderer body;
    ModelRenderer[] tentacles = new ModelRenderer[9];

    public ModelGhast() {
        int i2 = -16;
        this.body = new ModelRenderer(this, 0, 0);
        this.body.addBox(-8.0f, -8.0f, -8.0f, 16, 16, 16);
        this.body.rotationPointY += 8.0f;
        Random random = new Random(1660L);
        for (int j2 = 0; j2 < this.tentacles.length; ++j2) {
            this.tentacles[j2] = new ModelRenderer(this, 0, 0);
            float f2 = (((float)(j2 % 3) - (float)(j2 / 3 % 2) * 0.5f + 0.25f) / 2.0f * 2.0f - 1.0f) * 5.0f;
            float f1 = ((float)(j2 / 3) / 2.0f * 2.0f - 1.0f) * 5.0f;
            int k2 = random.nextInt(7) + 8;
            this.tentacles[j2].addBox(-1.0f, 0.0f, -1.0f, 2, k2, 2);
            this.tentacles[j2].rotationPointX = f2;
            this.tentacles[j2].rotationPointZ = f1;
            this.tentacles[j2].rotationPointY = 15.0f;
        }
    }

    @Override
    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn) {
        for (int i2 = 0; i2 < this.tentacles.length; ++i2) {
            this.tentacles[i2].rotateAngleX = 0.2f * MathHelper.sin(ageInTicks * 0.3f + (float)i2) + 0.4f;
        }
    }

    @Override
    public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        this.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entityIn);
        GlStateManager.pushMatrix();
        GlStateManager.translate(0.0f, 0.6f, 0.0f);
        this.body.render(scale);
        ModelRenderer[] arrmodelRenderer = this.tentacles;
        int n2 = this.tentacles.length;
        for (int i2 = 0; i2 < n2; ++i2) {
            ModelRenderer modelrenderer = arrmodelRenderer[i2];
            modelrenderer.render(scale);
        }
        GlStateManager.popMatrix();
    }
}

