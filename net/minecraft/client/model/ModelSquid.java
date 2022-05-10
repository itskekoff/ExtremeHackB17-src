package net.minecraft.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ModelSquid
extends ModelBase {
    ModelRenderer squidBody;
    ModelRenderer[] squidTentacles = new ModelRenderer[8];

    public ModelSquid() {
        int i2 = -16;
        this.squidBody = new ModelRenderer(this, 0, 0);
        this.squidBody.addBox(-6.0f, -8.0f, -6.0f, 12, 16, 12);
        this.squidBody.rotationPointY += 8.0f;
        for (int j2 = 0; j2 < this.squidTentacles.length; ++j2) {
            this.squidTentacles[j2] = new ModelRenderer(this, 48, 0);
            double d0 = (double)j2 * Math.PI * 2.0 / (double)this.squidTentacles.length;
            float f2 = (float)Math.cos(d0) * 5.0f;
            float f1 = (float)Math.sin(d0) * 5.0f;
            this.squidTentacles[j2].addBox(-1.0f, 0.0f, -1.0f, 2, 18, 2);
            this.squidTentacles[j2].rotationPointX = f2;
            this.squidTentacles[j2].rotationPointZ = f1;
            this.squidTentacles[j2].rotationPointY = 15.0f;
            d0 = (double)j2 * Math.PI * -2.0 / (double)this.squidTentacles.length + 1.5707963267948966;
            this.squidTentacles[j2].rotateAngleY = (float)d0;
        }
    }

    @Override
    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn) {
        ModelRenderer[] arrmodelRenderer = this.squidTentacles;
        int n2 = this.squidTentacles.length;
        for (int i2 = 0; i2 < n2; ++i2) {
            ModelRenderer modelrenderer = arrmodelRenderer[i2];
            modelrenderer.rotateAngleX = ageInTicks;
        }
    }

    @Override
    public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        this.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entityIn);
        this.squidBody.render(scale);
        ModelRenderer[] arrmodelRenderer = this.squidTentacles;
        int n2 = this.squidTentacles.length;
        for (int i2 = 0; i2 < n2; ++i2) {
            ModelRenderer modelrenderer = arrmodelRenderer[i2];
            modelrenderer.render(scale);
        }
    }
}

