package net.minecraft.entity;

import java.util.UUID;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public abstract class EntityCreature
extends EntityLiving {
    public static final UUID FLEEING_SPEED_MODIFIER_UUID = UUID.fromString("E199AD21-BA8A-4C53-8D13-6182D5C69D3A");
    public static final AttributeModifier FLEEING_SPEED_MODIFIER = new AttributeModifier(FLEEING_SPEED_MODIFIER_UUID, "Fleeing speed bonus", 2.0, 2).setSaved(false);
    private BlockPos homePosition = BlockPos.ORIGIN;
    private float maximumHomeDistance = -1.0f;
    private final float restoreWaterCost = PathNodeType.WATER.getPriority();

    public EntityCreature(World worldIn) {
        super(worldIn);
    }

    public float getBlockPathWeight(BlockPos pos) {
        return 0.0f;
    }

    @Override
    public boolean getCanSpawnHere() {
        if (super.getCanSpawnHere()) {
            BlockPos blockPos = new BlockPos(this.posX, this.getEntityBoundingBox().minY, this.posZ);
            if (this.getBlockPathWeight(blockPos) >= 0.0f) {
                return true;
            }
        }
        return false;
    }

    public boolean hasPath() {
        return !this.navigator.noPath();
    }

    public boolean isWithinHomeDistanceCurrentPosition() {
        return this.isWithinHomeDistanceFromPosition(new BlockPos(this));
    }

    public boolean isWithinHomeDistanceFromPosition(BlockPos pos) {
        if (this.maximumHomeDistance == -1.0f) {
            return true;
        }
        return this.homePosition.distanceSq(pos) < (double)(this.maximumHomeDistance * this.maximumHomeDistance);
    }

    public void setHomePosAndDistance(BlockPos pos, int distance) {
        this.homePosition = pos;
        this.maximumHomeDistance = distance;
    }

    public BlockPos getHomePosition() {
        return this.homePosition;
    }

    public float getMaximumHomeDistance() {
        return this.maximumHomeDistance;
    }

    public void detachHome() {
        this.maximumHomeDistance = -1.0f;
    }

    public boolean hasHome() {
        return this.maximumHomeDistance != -1.0f;
    }

    @Override
    protected void updateLeashedState() {
        super.updateLeashedState();
        if (this.getLeashed() && this.getLeashedToEntity() != null && this.getLeashedToEntity().world == this.world) {
            Entity entity = this.getLeashedToEntity();
            this.setHomePosAndDistance(new BlockPos((int)entity.posX, (int)entity.posY, (int)entity.posZ), 5);
            float f2 = this.getDistanceToEntity(entity);
            if (this instanceof EntityTameable && ((EntityTameable)this).isSitting()) {
                if (f2 > 10.0f) {
                    this.clearLeashed(true, true);
                }
                return;
            }
            this.onLeashDistance(f2);
            if (f2 > 10.0f) {
                this.clearLeashed(true, true);
                this.tasks.disableControlFlag(1);
            } else if (f2 > 6.0f) {
                double d0 = (entity.posX - this.posX) / (double)f2;
                double d1 = (entity.posY - this.posY) / (double)f2;
                double d2 = (entity.posZ - this.posZ) / (double)f2;
                this.motionX += d0 * Math.abs(d0) * 0.4;
                this.motionY += d1 * Math.abs(d1) * 0.4;
                this.motionZ += d2 * Math.abs(d2) * 0.4;
            } else {
                this.tasks.enableControlFlag(1);
                float f1 = 2.0f;
                Vec3d vec3d = new Vec3d(entity.posX - this.posX, entity.posY - this.posY, entity.posZ - this.posZ).normalize().scale(Math.max(f2 - 2.0f, 0.0f));
                this.getNavigator().tryMoveToXYZ(this.posX + vec3d.xCoord, this.posY + vec3d.yCoord, this.posZ + vec3d.zCoord, this.func_190634_dg());
            }
        }
    }

    protected double func_190634_dg() {
        return 1.0;
    }

    protected void onLeashDistance(float p_142017_1_) {
    }
}

