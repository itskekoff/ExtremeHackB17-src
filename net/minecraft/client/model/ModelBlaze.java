package net.minecraft.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

public class ModelBlaze
extends ModelBase {
    private final ModelRenderer[] blazeSticks = new ModelRenderer[12];
    private final ModelRenderer blazeHead;

    public ModelBlaze() {
        for (int i2 = 0; i2 < this.blazeSticks.length; ++i2) {
            this.blazeSticks[i2] = new ModelRenderer(this, 0, 16);
            this.blazeSticks[i2].addBox(0.0f, 0.0f, 0.0f, 2, 8, 2);
        }
        this.blazeHead = new ModelRenderer(this, 0, 0);
        this.blazeHead.addBox(-4.0f, -4.0f, -4.0f, 8, 8, 8);
    }

    @Override
    public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        this.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entityIn);
        this.blazeHead.render(scale);
        ModelRenderer[] arrmodelRenderer = this.blazeSticks;
        int n2 = this.blazeSticks.length;
        for (int i2 = 0; i2 < n2; ++i2) {
            ModelRenderer modelrenderer = arrmodelRenderer[i2];
            modelrenderer.render(scale);
        }
    }

    @Override
    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn) {
        float f2 = ageInTicks * (float)Math.PI * -0.1f;
        for (int i2 = 0; i2 < 4; ++i2) {
            this.blazeSticks[i2].rotationPointY = -2.0f + MathHelper.cos(((float)(i2 * 2) + ageInTicks) * 0.25f);
            this.blazeSticks[i2].rotationPointX = MathHelper.cos(f2) * 9.0f;
            this.blazeSticks[i2].rotationPointZ = MathHelper.sin(f2) * 9.0f;
            f2 += 1.0f;
        }
        f2 = 0.7853982f + ageInTicks * (float)Math.PI * 0.03f;
        for (int j2 = 4; j2 < 8; ++j2) {
            this.blazeSticks[j2].rotationPointY = 2.0f + MathHelper.cos(((float)(j2 * 2) + ageInTicks) * 0.25f);
            this.blazeSticks[j2].rotationPointX = MathHelper.cos(f2) * 7.0f;
            this.blazeSticks[j2].rotationPointZ = MathHelper.sin(f2) * 7.0f;
            f2 += 1.0f;
        }
        f2 = 0.47123894f + ageInTicks * (float)Math.PI * -0.05f;
        for (int k2 = 8; k2 < 12; ++k2) {
            this.blazeSticks[k2].rotationPointY = 11.0f + MathHelper.cos(((float)k2 * 1.5f + ageInTicks) * 0.5f);
            this.blazeSticks[k2].rotationPointX = MathHelper.cos(f2) * 5.0f;
            this.blazeSticks[k2].rotationPointZ = MathHelper.sin(f2) * 5.0f;
            f2 += 1.0f;
        }
        this.blazeHead.rotateAngleY = netHeadYaw * ((float)Math.PI / 180);
        this.blazeHead.rotateAngleX = headPitch * ((float)Math.PI / 180);
    }
}

