package net.minecraft.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

public class ModelEnderMite
extends ModelBase {
    private static final int[][] BODY_SIZES = new int[][]{{4, 3, 2}, {6, 4, 5}, {3, 3, 1}, {1, 2, 1}};
    private static final int[][] BODY_TEXS;
    private static final int BODY_COUNT;
    private final ModelRenderer[] bodyParts = new ModelRenderer[BODY_COUNT];

    static {
        int[][] arrarrn = new int[4][];
        arrarrn[0] = new int[2];
        int[] arrn = new int[2];
        arrn[1] = 5;
        arrarrn[1] = arrn;
        int[] arrn2 = new int[2];
        arrn2[1] = 14;
        arrarrn[2] = arrn2;
        int[] arrn3 = new int[2];
        arrn3[1] = 18;
        arrarrn[3] = arrn3;
        BODY_TEXS = arrarrn;
        BODY_COUNT = BODY_SIZES.length;
    }

    public ModelEnderMite() {
        float f2 = -3.5f;
        for (int i2 = 0; i2 < this.bodyParts.length; ++i2) {
            this.bodyParts[i2] = new ModelRenderer(this, BODY_TEXS[i2][0], BODY_TEXS[i2][1]);
            this.bodyParts[i2].addBox((float)BODY_SIZES[i2][0] * -0.5f, 0.0f, (float)BODY_SIZES[i2][2] * -0.5f, BODY_SIZES[i2][0], BODY_SIZES[i2][1], BODY_SIZES[i2][2]);
            this.bodyParts[i2].setRotationPoint(0.0f, 24 - BODY_SIZES[i2][1], f2);
            if (i2 >= this.bodyParts.length - 1) continue;
            f2 += (float)(BODY_SIZES[i2][2] + BODY_SIZES[i2 + 1][2]) * 0.5f;
        }
    }

    @Override
    public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        this.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entityIn);
        ModelRenderer[] arrmodelRenderer = this.bodyParts;
        int n2 = this.bodyParts.length;
        for (int i2 = 0; i2 < n2; ++i2) {
            ModelRenderer modelrenderer = arrmodelRenderer[i2];
            modelrenderer.render(scale);
        }
    }

    @Override
    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn) {
        for (int i2 = 0; i2 < this.bodyParts.length; ++i2) {
            this.bodyParts[i2].rotateAngleY = MathHelper.cos(ageInTicks * 0.9f + (float)i2 * 0.15f * (float)Math.PI) * (float)Math.PI * 0.01f * (float)(1 + Math.abs(i2 - 2));
            this.bodyParts[i2].rotationPointX = MathHelper.sin(ageInTicks * 0.9f + (float)i2 * 0.15f * (float)Math.PI) * (float)Math.PI * 0.1f * (float)Math.abs(i2 - 2);
        }
    }
}

