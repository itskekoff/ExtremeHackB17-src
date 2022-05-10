package net.minecraft.entity.ai;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.MathHelper;

public class EntityLookHelper {
    private final EntityLiving entity;
    private float deltaLookYaw;
    private float deltaLookPitch;
    private boolean isLooking;
    private double posX;
    private double posY;
    private double posZ;

    public EntityLookHelper(EntityLiving entitylivingIn) {
        this.entity = entitylivingIn;
    }

    public void setLookPositionWithEntity(Entity entityIn, float deltaYaw, float deltaPitch) {
        this.posX = entityIn.posX;
        this.posY = entityIn instanceof EntityLivingBase ? entityIn.posY + (double)entityIn.getEyeHeight() : (entityIn.getEntityBoundingBox().minY + entityIn.getEntityBoundingBox().maxY) / 2.0;
        this.posZ = entityIn.posZ;
        this.deltaLookYaw = deltaYaw;
        this.deltaLookPitch = deltaPitch;
        this.isLooking = true;
    }

    public void setLookPosition(double x2, double y2, double z2, float deltaYaw, float deltaPitch) {
        this.posX = x2;
        this.posY = y2;
        this.posZ = z2;
        this.deltaLookYaw = deltaYaw;
        this.deltaLookPitch = deltaPitch;
        this.isLooking = true;
    }

    public void onUpdateLook() {
        this.entity.rotationPitch = 0.0f;
        if (this.isLooking) {
            this.isLooking = false;
            double d0 = this.posX - this.entity.posX;
            double d1 = this.posY - (this.entity.posY + (double)this.entity.getEyeHeight());
            double d2 = this.posZ - this.entity.posZ;
            double d3 = MathHelper.sqrt(d0 * d0 + d2 * d2);
            float f2 = (float)(MathHelper.atan2(d2, d0) * 57.29577951308232) - 90.0f;
            float f1 = (float)(-(MathHelper.atan2(d1, d3) * 57.29577951308232));
            this.entity.rotationPitch = this.updateRotation(this.entity.rotationPitch, f1, this.deltaLookPitch);
            this.entity.rotationYawHead = this.updateRotation(this.entity.rotationYawHead, f2, this.deltaLookYaw);
        } else {
            this.entity.rotationYawHead = this.updateRotation(this.entity.rotationYawHead, this.entity.renderYawOffset, 10.0f);
        }
        float f2 = MathHelper.wrapDegrees(this.entity.rotationYawHead - this.entity.renderYawOffset);
        if (!this.entity.getNavigator().noPath()) {
            if (f2 < -75.0f) {
                this.entity.rotationYawHead = this.entity.renderYawOffset - 75.0f;
            }
            if (f2 > 75.0f) {
                this.entity.rotationYawHead = this.entity.renderYawOffset + 75.0f;
            }
        }
    }

    private float updateRotation(float p_75652_1_, float p_75652_2_, float p_75652_3_) {
        float f2 = MathHelper.wrapDegrees(p_75652_2_ - p_75652_1_);
        if (f2 > p_75652_3_) {
            f2 = p_75652_3_;
        }
        if (f2 < -p_75652_3_) {
            f2 = -p_75652_3_;
        }
        return p_75652_1_ + f2;
    }

    public boolean getIsLooking() {
        return this.isLooking;
    }

    public double getLookPosX() {
        return this.posX;
    }

    public double getLookPosY() {
        return this.posY;
    }

    public double getLookPosZ() {
        return this.posZ;
    }
}

