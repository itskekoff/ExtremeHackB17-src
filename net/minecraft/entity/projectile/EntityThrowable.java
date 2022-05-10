package net.minecraft.entity.projectile;

import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.datafix.DataFixer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public abstract class EntityThrowable
extends Entity
implements IProjectile {
    private int xTile = -1;
    private int yTile = -1;
    private int zTile = -1;
    private Block inTile;
    protected boolean inGround;
    public int throwableShake;
    protected EntityLivingBase thrower;
    private String throwerName;
    private int ticksInGround;
    private int ticksInAir;
    public Entity ignoreEntity;
    private int ignoreTime;

    public EntityThrowable(World worldIn) {
        super(worldIn);
        this.setSize(0.25f, 0.25f);
    }

    public EntityThrowable(World worldIn, double x2, double y2, double z2) {
        this(worldIn);
        this.setPosition(x2, y2, z2);
    }

    public EntityThrowable(World worldIn, EntityLivingBase throwerIn) {
        this(worldIn, throwerIn.posX, throwerIn.posY + (double)throwerIn.getEyeHeight() - (double)0.1f, throwerIn.posZ);
        this.thrower = throwerIn;
    }

    @Override
    protected void entityInit() {
    }

    @Override
    public boolean isInRangeToRenderDist(double distance) {
        double d0 = this.getEntityBoundingBox().getAverageEdgeLength() * 4.0;
        if (Double.isNaN(d0)) {
            d0 = 4.0;
        }
        return distance < (d0 *= 64.0) * d0;
    }

    public void setHeadingFromThrower(Entity entityThrower, float rotationPitchIn, float rotationYawIn, float pitchOffset, float velocity, float inaccuracy) {
        float f2 = -MathHelper.sin(rotationYawIn * ((float)Math.PI / 180)) * MathHelper.cos(rotationPitchIn * ((float)Math.PI / 180));
        float f1 = -MathHelper.sin((rotationPitchIn + pitchOffset) * ((float)Math.PI / 180));
        float f22 = MathHelper.cos(rotationYawIn * ((float)Math.PI / 180)) * MathHelper.cos(rotationPitchIn * ((float)Math.PI / 180));
        this.setThrowableHeading(f2, f1, f22, velocity, inaccuracy);
        this.motionX += entityThrower.motionX;
        this.motionZ += entityThrower.motionZ;
        if (!entityThrower.onGround) {
            this.motionY += entityThrower.motionY;
        }
    }

    @Override
    public void setThrowableHeading(double x2, double y2, double z2, float velocity, float inaccuracy) {
        float f2 = MathHelper.sqrt(x2 * x2 + y2 * y2 + z2 * z2);
        x2 /= (double)f2;
        y2 /= (double)f2;
        z2 /= (double)f2;
        x2 += this.rand.nextGaussian() * (double)0.0075f * (double)inaccuracy;
        y2 += this.rand.nextGaussian() * (double)0.0075f * (double)inaccuracy;
        z2 += this.rand.nextGaussian() * (double)0.0075f * (double)inaccuracy;
        this.motionX = x2 *= (double)velocity;
        this.motionY = y2 *= (double)velocity;
        this.motionZ = z2 *= (double)velocity;
        float f1 = MathHelper.sqrt(x2 * x2 + z2 * z2);
        this.rotationYaw = (float)(MathHelper.atan2(x2, z2) * 57.29577951308232);
        this.rotationPitch = (float)(MathHelper.atan2(y2, f1) * 57.29577951308232);
        this.prevRotationYaw = this.rotationYaw;
        this.prevRotationPitch = this.rotationPitch;
        this.ticksInGround = 0;
    }

    @Override
    public void setVelocity(double x2, double y2, double z2) {
        this.motionX = x2;
        this.motionY = y2;
        this.motionZ = z2;
        if (this.prevRotationPitch == 0.0f && this.prevRotationYaw == 0.0f) {
            float f2 = MathHelper.sqrt(x2 * x2 + z2 * z2);
            this.rotationYaw = (float)(MathHelper.atan2(x2, z2) * 57.29577951308232);
            this.rotationPitch = (float)(MathHelper.atan2(y2, f2) * 57.29577951308232);
            this.prevRotationYaw = this.rotationYaw;
            this.prevRotationPitch = this.rotationPitch;
        }
    }

    @Override
    public void onUpdate() {
        this.lastTickPosX = this.posX;
        this.lastTickPosY = this.posY;
        this.lastTickPosZ = this.posZ;
        super.onUpdate();
        if (this.throwableShake > 0) {
            --this.throwableShake;
        }
        if (this.inGround) {
            if (this.world.getBlockState(new BlockPos(this.xTile, this.yTile, this.zTile)).getBlock() == this.inTile) {
                ++this.ticksInGround;
                if (this.ticksInGround == 1200) {
                    this.setDead();
                }
                return;
            }
            this.inGround = false;
            this.motionX *= (double)(this.rand.nextFloat() * 0.2f);
            this.motionY *= (double)(this.rand.nextFloat() * 0.2f);
            this.motionZ *= (double)(this.rand.nextFloat() * 0.2f);
            this.ticksInGround = 0;
            this.ticksInAir = 0;
        } else {
            ++this.ticksInAir;
        }
        Vec3d vec3d = new Vec3d(this.posX, this.posY, this.posZ);
        Vec3d vec3d1 = new Vec3d(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
        RayTraceResult raytraceresult = this.world.rayTraceBlocks(vec3d, vec3d1);
        vec3d = new Vec3d(this.posX, this.posY, this.posZ);
        vec3d1 = new Vec3d(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
        if (raytraceresult != null) {
            vec3d1 = new Vec3d(raytraceresult.hitVec.xCoord, raytraceresult.hitVec.yCoord, raytraceresult.hitVec.zCoord);
        }
        Entity entity = null;
        List<Entity> list = this.world.getEntitiesWithinAABBExcludingEntity(this, this.getEntityBoundingBox().addCoord(this.motionX, this.motionY, this.motionZ).expandXyz(1.0));
        double d0 = 0.0;
        boolean flag = false;
        for (int i2 = 0; i2 < list.size(); ++i2) {
            double d1;
            Entity entity1 = list.get(i2);
            if (!entity1.canBeCollidedWith()) continue;
            if (entity1 == this.ignoreEntity) {
                flag = true;
                continue;
            }
            if (this.thrower != null && this.ticksExisted < 2 && this.ignoreEntity == null) {
                this.ignoreEntity = entity1;
                flag = true;
                continue;
            }
            flag = false;
            AxisAlignedBB axisalignedbb = entity1.getEntityBoundingBox().expandXyz(0.3f);
            RayTraceResult raytraceresult1 = axisalignedbb.calculateIntercept(vec3d, vec3d1);
            if (raytraceresult1 == null || !((d1 = vec3d.squareDistanceTo(raytraceresult1.hitVec)) < d0) && d0 != 0.0) continue;
            entity = entity1;
            d0 = d1;
        }
        if (this.ignoreEntity != null) {
            if (flag) {
                this.ignoreTime = 2;
            } else if (this.ignoreTime-- <= 0) {
                this.ignoreEntity = null;
            }
        }
        if (entity != null) {
            raytraceresult = new RayTraceResult(entity);
        }
        if (raytraceresult != null) {
            if (raytraceresult.typeOfHit == RayTraceResult.Type.BLOCK && this.world.getBlockState(raytraceresult.getBlockPos()).getBlock() == Blocks.PORTAL) {
                this.setPortal(raytraceresult.getBlockPos());
            } else {
                this.onImpact(raytraceresult);
            }
        }
        this.posX += this.motionX;
        this.posY += this.motionY;
        this.posZ += this.motionZ;
        float f2 = MathHelper.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
        this.rotationYaw = (float)(MathHelper.atan2(this.motionX, this.motionZ) * 57.29577951308232);
        this.rotationPitch = (float)(MathHelper.atan2(this.motionY, f2) * 57.29577951308232);
        while (this.rotationPitch - this.prevRotationPitch < -180.0f) {
            this.prevRotationPitch -= 360.0f;
        }
        while (this.rotationPitch - this.prevRotationPitch >= 180.0f) {
            this.prevRotationPitch += 360.0f;
        }
        while (this.rotationYaw - this.prevRotationYaw < -180.0f) {
            this.prevRotationYaw -= 360.0f;
        }
        while (this.rotationYaw - this.prevRotationYaw >= 180.0f) {
            this.prevRotationYaw += 360.0f;
        }
        this.rotationPitch = this.prevRotationPitch + (this.rotationPitch - this.prevRotationPitch) * 0.2f;
        this.rotationYaw = this.prevRotationYaw + (this.rotationYaw - this.prevRotationYaw) * 0.2f;
        float f1 = 0.99f;
        float f22 = this.getGravityVelocity();
        if (this.isInWater()) {
            for (int j2 = 0; j2 < 4; ++j2) {
                float f3 = 0.25f;
                this.world.spawnParticle(EnumParticleTypes.WATER_BUBBLE, this.posX - this.motionX * 0.25, this.posY - this.motionY * 0.25, this.posZ - this.motionZ * 0.25, this.motionX, this.motionY, this.motionZ, new int[0]);
            }
            f1 = 0.8f;
        }
        this.motionX *= (double)f1;
        this.motionY *= (double)f1;
        this.motionZ *= (double)f1;
        if (!this.hasNoGravity()) {
            this.motionY -= (double)f22;
        }
        this.setPosition(this.posX, this.posY, this.posZ);
    }

    protected float getGravityVelocity() {
        return 0.03f;
    }

    protected abstract void onImpact(RayTraceResult var1);

    public static void registerFixesThrowable(DataFixer fixer, String name) {
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        compound.setInteger("xTile", this.xTile);
        compound.setInteger("yTile", this.yTile);
        compound.setInteger("zTile", this.zTile);
        ResourceLocation resourcelocation = Block.REGISTRY.getNameForObject(this.inTile);
        compound.setString("inTile", resourcelocation == null ? "" : resourcelocation.toString());
        compound.setByte("shake", (byte)this.throwableShake);
        compound.setByte("inGround", (byte)(this.inGround ? 1 : 0));
        if ((this.throwerName == null || this.throwerName.isEmpty()) && this.thrower instanceof EntityPlayer) {
            this.throwerName = this.thrower.getName();
        }
        compound.setString("ownerName", this.throwerName == null ? "" : this.throwerName);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        this.xTile = compound.getInteger("xTile");
        this.yTile = compound.getInteger("yTile");
        this.zTile = compound.getInteger("zTile");
        this.inTile = compound.hasKey("inTile", 8) ? Block.getBlockFromName(compound.getString("inTile")) : Block.getBlockById(compound.getByte("inTile") & 0xFF);
        this.throwableShake = compound.getByte("shake") & 0xFF;
        this.inGround = compound.getByte("inGround") == 1;
        this.thrower = null;
        this.throwerName = compound.getString("ownerName");
        if (this.throwerName != null && this.throwerName.isEmpty()) {
            this.throwerName = null;
        }
        this.thrower = this.getThrower();
    }

    @Nullable
    public EntityLivingBase getThrower() {
        if (this.thrower == null && this.throwerName != null && !this.throwerName.isEmpty()) {
            this.thrower = this.world.getPlayerEntityByName(this.throwerName);
            if (this.thrower == null && this.world instanceof WorldServer) {
                try {
                    Entity entity = ((WorldServer)this.world).getEntityFromUuid(UUID.fromString(this.throwerName));
                    if (entity instanceof EntityLivingBase) {
                        this.thrower = (EntityLivingBase)entity;
                    }
                }
                catch (Throwable var2) {
                    this.thrower = null;
                }
            }
        }
        return this.thrower;
    }
}

