package net.minecraft.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

public class ModelSilverfish
extends ModelBase {
    private final ModelRenderer[] silverfishBodyParts = new ModelRenderer[7];
    private final ModelRenderer[] silverfishWings;
    private final float[] zPlacement = new float[7];
    private static final int[][] SILVERFISH_BOX_LENGTH = new int[][]{{3, 2, 2}, {4, 3, 2}, {6, 4, 3}, {3, 3, 3}, {2, 2, 3}, {2, 1, 2}, {1, 1, 2}};
    private static final int[][] SILVERFISH_TEXTURE_POSITIONS;

    static {
        int[][] arrarrn = new int[7][];
        arrarrn[0] = new int[2];
        int[] arrn = new int[2];
        arrn[1] = 4;
        arrarrn[1] = arrn;
        int[] arrn2 = new int[2];
        arrn2[1] = 9;
        arrarrn[2] = arrn2;
        int[] arrn3 = new int[2];
        arrn3[1] = 16;
        arrarrn[3] = arrn3;
        int[] arrn4 = new int[2];
        arrn4[1] = 22;
        arrarrn[4] = arrn4;
        int[] arrn5 = new int[2];
        arrn5[0] = 11;
        arrarrn[5] = arrn5;
        arrarrn[6] = new int[]{13, 4};
        SILVERFISH_TEXTURE_POSITIONS = arrarrn;
    }

    public ModelSilverfish() {
        float f2 = -3.5f;
        for (int i2 = 0; i2 < this.silverfishBodyParts.length; ++i2) {
            this.silverfishBodyParts[i2] = new ModelRenderer(this, SILVERFISH_TEXTURE_POSITIONS[i2][0], SILVERFISH_TEXTURE_POSITIONS[i2][1]);
            this.silverfishBodyParts[i2].addBox((float)SILVERFISH_BOX_LENGTH[i2][0] * -0.5f, 0.0f, (float)SILVERFISH_BOX_LENGTH[i2][2] * -0.5f, SILVERFISH_BOX_LENGTH[i2][0], SILVERFISH_BOX_LENGTH[i2][1], SILVERFISH_BOX_LENGTH[i2][2]);
            this.silverfishBodyParts[i2].setRotationPoint(0.0f, 24 - SILVERFISH_BOX_LENGTH[i2][1], f2);
            this.zPlacement[i2] = f2;
            if (i2 >= this.silverfishBodyParts.length - 1) continue;
            f2 += (float)(SILVERFISH_BOX_LENGTH[i2][2] + SILVERFISH_BOX_LENGTH[i2 + 1][2]) * 0.5f;
        }
        this.silverfishWings = new ModelRenderer[3];
        this.silverfishWings[0] = new ModelRenderer(this, 20, 0);
        this.silverfishWings[0].addBox(-5.0f, 0.0f, (float)SILVERFISH_BOX_LENGTH[2][2] * -0.5f, 10, 8, SILVERFISH_BOX_LENGTH[2][2]);
        this.silverfishWings[0].setRotationPoint(0.0f, 16.0f, this.zPlacement[2]);
        this.silverfishWings[1] = new ModelRenderer(this, 20, 11);
        this.silverfishWings[1].addBox(-3.0f, 0.0f, (float)SILVERFISH_BOX_LENGTH[4][2] * -0.5f, 6, 4, SILVERFISH_BOX_LENGTH[4][2]);
        this.silverfishWings[1].setRotationPoint(0.0f, 20.0f, this.zPlacement[4]);
        this.silverfishWings[2] = new ModelRenderer(this, 20, 18);
        this.silverfishWings[2].addBox(-3.0f, 0.0f, (float)SILVERFISH_BOX_LENGTH[4][2] * -0.5f, 6, 5, SILVERFISH_BOX_LENGTH[1][2]);
        this.silverfishWings[2].setRotationPoint(0.0f, 19.0f, this.zPlacement[1]);
    }

    @Override
    public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        int n2;
        this.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entityIn);
        ModelRenderer[] arrmodelRenderer = this.silverfishBodyParts;
        int n3 = this.silverfishBodyParts.length;
        for (n2 = 0; n2 < n3; ++n2) {
            ModelRenderer modelrenderer = arrmodelRenderer[n2];
            modelrenderer.render(scale);
        }
        arrmodelRenderer = this.silverfishWings;
        n3 = this.silverfishWings.length;
        for (n2 = 0; n2 < n3; ++n2) {
            ModelRenderer modelrenderer1 = arrmodelRenderer[n2];
            modelrenderer1.render(scale);
        }
    }

    @Override
    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn) {
        for (int i2 = 0; i2 < this.silverfishBodyParts.length; ++i2) {
            this.silverfishBodyParts[i2].rotateAngleY = MathHelper.cos(ageInTicks * 0.9f + (float)i2 * 0.15f * (float)Math.PI) * (float)Math.PI * 0.05f * (float)(1 + Math.abs(i2 - 2));
            this.silverfishBodyParts[i2].rotationPointX = MathHelper.sin(ageInTicks * 0.9f + (float)i2 * 0.15f * (float)Math.PI) * (float)Math.PI * 0.2f * (float)Math.abs(i2 - 2);
        }
        this.silverfishWings[0].rotateAngleY = this.silverfishBodyParts[2].rotateAngleY;
        this.silverfishWings[1].rotateAngleY = this.silverfishBodyParts[4].rotateAngleY;
        this.silverfishWings[1].rotationPointX = this.silverfishBodyParts[4].rotationPointX;
        this.silverfishWings[2].rotateAngleY = this.silverfishBodyParts[1].rotateAngleY;
        this.silverfishWings[2].rotationPointX = this.silverfishBodyParts[1].rotationPointX;
    }
}

