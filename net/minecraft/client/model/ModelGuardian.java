package net.minecraft.client.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityGuardian;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class ModelGuardian
extends ModelBase {
    private final ModelRenderer guardianBody;
    private final ModelRenderer guardianEye;
    private final ModelRenderer[] guardianSpines;
    private final ModelRenderer[] guardianTail;

    public ModelGuardian() {
        this.textureWidth = 64;
        this.textureHeight = 64;
        this.guardianSpines = new ModelRenderer[12];
        this.guardianBody = new ModelRenderer(this);
        this.guardianBody.setTextureOffset(0, 0).addBox(-6.0f, 10.0f, -8.0f, 12, 12, 16);
        this.guardianBody.setTextureOffset(0, 28).addBox(-8.0f, 10.0f, -6.0f, 2, 12, 12);
        this.guardianBody.setTextureOffset(0, 28).addBox(6.0f, 10.0f, -6.0f, 2, 12, 12, true);
        this.guardianBody.setTextureOffset(16, 40).addBox(-6.0f, 8.0f, -6.0f, 12, 2, 12);
        this.guardianBody.setTextureOffset(16, 40).addBox(-6.0f, 22.0f, -6.0f, 12, 2, 12);
        for (int i2 = 0; i2 < this.guardianSpines.length; ++i2) {
            this.guardianSpines[i2] = new ModelRenderer(this, 0, 0);
            this.guardianSpines[i2].addBox(-1.0f, -4.5f, -1.0f, 2, 9, 2);
            this.guardianBody.addChild(this.guardianSpines[i2]);
        }
        this.guardianEye = new ModelRenderer(this, 8, 0);
        this.guardianEye.addBox(-1.0f, 15.0f, 0.0f, 2, 2, 1);
        this.guardianBody.addChild(this.guardianEye);
        this.guardianTail = new ModelRenderer[3];
        this.guardianTail[0] = new ModelRenderer(this, 40, 0);
        this.guardianTail[0].addBox(-2.0f, 14.0f, 7.0f, 4, 4, 8);
        this.guardianTail[1] = new ModelRenderer(this, 0, 54);
        this.guardianTail[1].addBox(0.0f, 14.0f, 0.0f, 3, 3, 7);
        this.guardianTail[2] = new ModelRenderer(this);
        this.guardianTail[2].setTextureOffset(41, 32).addBox(0.0f, 14.0f, 0.0f, 2, 2, 6);
        this.guardianTail[2].setTextureOffset(25, 19).addBox(1.0f, 10.5f, 3.0f, 1, 9, 9);
        this.guardianBody.addChild(this.guardianTail[0]);
        this.guardianTail[0].addChild(this.guardianTail[1]);
        this.guardianTail[1].addChild(this.guardianTail[2]);
    }

    @Override
    public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        this.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entityIn);
        this.guardianBody.render(scale);
    }

    @Override
    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn) {
        EntityGuardian entityguardian = (EntityGuardian)entityIn;
        float f2 = ageInTicks - (float)entityguardian.ticksExisted;
        this.guardianBody.rotateAngleY = netHeadYaw * ((float)Math.PI / 180);
        this.guardianBody.rotateAngleX = headPitch * ((float)Math.PI / 180);
        float[] afloat = new float[]{1.75f, 0.25f, 0.0f, 0.0f, 0.5f, 0.5f, 0.5f, 0.5f, 1.25f, 0.75f, 0.0f, 0.0f};
        float[] afloat1 = new float[]{0.0f, 0.0f, 0.0f, 0.0f, 0.25f, 1.75f, 1.25f, 0.75f, 0.0f, 0.0f, 0.0f, 0.0f};
        float[] afloat2 = new float[]{0.0f, 0.0f, 0.25f, 1.75f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.75f, 1.25f};
        float[] afloat3 = new float[]{0.0f, 0.0f, 8.0f, -8.0f, -8.0f, 8.0f, 8.0f, -8.0f, 0.0f, 0.0f, 8.0f, -8.0f};
        float[] afloat4 = new float[]{-8.0f, -8.0f, -8.0f, -8.0f, 0.0f, 0.0f, 0.0f, 0.0f, 8.0f, 8.0f, 8.0f, 8.0f};
        float[] afloat5 = new float[]{8.0f, -8.0f, 0.0f, 0.0f, -8.0f, -8.0f, 8.0f, 8.0f, 8.0f, -8.0f, 0.0f, 0.0f};
        float f1 = (1.0f - entityguardian.getSpikesAnimation(f2)) * 0.55f;
        for (int i2 = 0; i2 < 12; ++i2) {
            this.guardianSpines[i2].rotateAngleX = (float)Math.PI * afloat[i2];
            this.guardianSpines[i2].rotateAngleY = (float)Math.PI * afloat1[i2];
            this.guardianSpines[i2].rotateAngleZ = (float)Math.PI * afloat2[i2];
            this.guardianSpines[i2].rotationPointX = afloat3[i2] * (1.0f + MathHelper.cos(ageInTicks * 1.5f + (float)i2) * 0.01f - f1);
            this.guardianSpines[i2].rotationPointY = 16.0f + afloat4[i2] * (1.0f + MathHelper.cos(ageInTicks * 1.5f + (float)i2) * 0.01f - f1);
            this.guardianSpines[i2].rotationPointZ = afloat5[i2] * (1.0f + MathHelper.cos(ageInTicks * 1.5f + (float)i2) * 0.01f - f1);
        }
        this.guardianEye.rotationPointZ = -8.25f;
        Entity entity = Minecraft.getMinecraft().getRenderViewEntity();
        if (entityguardian.hasTargetedEntity()) {
            entity = entityguardian.getTargetedEntity();
        }
        if (entity != null) {
            Vec3d vec3d = entity.getPositionEyes(0.0f);
            Vec3d vec3d1 = entityIn.getPositionEyes(0.0f);
            double d0 = vec3d.yCoord - vec3d1.yCoord;
            this.guardianEye.rotationPointY = d0 > 0.0 ? 0.0f : 1.0f;
            Vec3d vec3d2 = entityIn.getLook(0.0f);
            vec3d2 = new Vec3d(vec3d2.xCoord, 0.0, vec3d2.zCoord);
            Vec3d vec3d3 = new Vec3d(vec3d1.xCoord - vec3d.xCoord, 0.0, vec3d1.zCoord - vec3d.zCoord).normalize().rotateYaw(1.5707964f);
            double d1 = vec3d2.dotProduct(vec3d3);
            this.guardianEye.rotationPointX = MathHelper.sqrt((float)Math.abs(d1)) * 2.0f * (float)Math.signum(d1);
        }
        this.guardianEye.showModel = true;
        float f22 = entityguardian.getTailAnimation(f2);
        this.guardianTail[0].rotateAngleY = MathHelper.sin(f22) * (float)Math.PI * 0.05f;
        this.guardianTail[1].rotateAngleY = MathHelper.sin(f22) * (float)Math.PI * 0.1f;
        this.guardianTail[1].rotationPointX = -1.5f;
        this.guardianTail[1].rotationPointY = 0.5f;
        this.guardianTail[1].rotationPointZ = 14.0f;
        this.guardianTail[2].rotateAngleY = MathHelper.sin(f22) * (float)Math.PI * 0.15f;
        this.guardianTail[2].rotationPointX = 0.5f;
        this.guardianTail[2].rotationPointY = 0.5f;
        this.guardianTail[2].rotationPointZ = 6.0f;
    }
}

