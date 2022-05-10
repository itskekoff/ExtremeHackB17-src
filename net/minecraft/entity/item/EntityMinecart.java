package net.minecraft.entity.item;

import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRailBase;
import net.minecraft.block.BlockRailPowered;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.item.EntityMinecartChest;
import net.minecraft.entity.item.EntityMinecartCommandBlock;
import net.minecraft.entity.item.EntityMinecartEmpty;
import net.minecraft.entity.item.EntityMinecartFurnace;
import net.minecraft.entity.item.EntityMinecartHopper;
import net.minecraft.entity.item.EntityMinecartMobSpawner;
import net.minecraft.entity.item.EntityMinecartTNT;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.datafix.DataFixer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IWorldNameable;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public abstract class EntityMinecart
extends Entity
implements IWorldNameable {
    private static final DataParameter<Integer> ROLLING_AMPLITUDE = EntityDataManager.createKey(EntityMinecart.class, DataSerializers.VARINT);
    private static final DataParameter<Integer> ROLLING_DIRECTION = EntityDataManager.createKey(EntityMinecart.class, DataSerializers.VARINT);
    private static final DataParameter<Float> DAMAGE = EntityDataManager.createKey(EntityMinecart.class, DataSerializers.FLOAT);
    private static final DataParameter<Integer> DISPLAY_TILE = EntityDataManager.createKey(EntityMinecart.class, DataSerializers.VARINT);
    private static final DataParameter<Integer> DISPLAY_TILE_OFFSET = EntityDataManager.createKey(EntityMinecart.class, DataSerializers.VARINT);
    private static final DataParameter<Boolean> SHOW_BLOCK = EntityDataManager.createKey(EntityMinecart.class, DataSerializers.BOOLEAN);
    private boolean isInReverse;
    private static final int[][][] MATRIX;
    private int turnProgress;
    private double minecartX;
    private double minecartY;
    private double minecartZ;
    private double minecartYaw;
    private double minecartPitch;
    private double velocityX;
    private double velocityY;
    private double velocityZ;

    static {
        int[][][] arrarrn = new int[10][][];
        int[][] arrarrn2 = new int[2][];
        int[] arrn = new int[3];
        arrn[2] = -1;
        arrarrn2[0] = arrn;
        int[] arrn2 = new int[3];
        arrn2[2] = 1;
        arrarrn2[1] = arrn2;
        arrarrn[0] = arrarrn2;
        int[][] arrarrn3 = new int[2][];
        int[] arrn3 = new int[3];
        arrn3[0] = -1;
        arrarrn3[0] = arrn3;
        int[] arrn4 = new int[3];
        arrn4[0] = 1;
        arrarrn3[1] = arrn4;
        arrarrn[1] = arrarrn3;
        int[][] arrarrn4 = new int[2][];
        int[] arrn5 = new int[3];
        arrn5[0] = -1;
        arrn5[1] = -1;
        arrarrn4[0] = arrn5;
        int[] arrn6 = new int[3];
        arrn6[0] = 1;
        arrarrn4[1] = arrn6;
        arrarrn[2] = arrarrn4;
        int[][] arrarrn5 = new int[2][];
        int[] arrn7 = new int[3];
        arrn7[0] = -1;
        arrarrn5[0] = arrn7;
        int[] arrn8 = new int[3];
        arrn8[0] = 1;
        arrn8[1] = -1;
        arrarrn5[1] = arrn8;
        arrarrn[3] = arrarrn5;
        int[][] arrarrn6 = new int[2][];
        int[] arrn9 = new int[3];
        arrn9[2] = -1;
        arrarrn6[0] = arrn9;
        int[] arrn10 = new int[3];
        arrn10[1] = -1;
        arrn10[2] = 1;
        arrarrn6[1] = arrn10;
        arrarrn[4] = arrarrn6;
        int[][] arrarrn7 = new int[2][];
        int[] arrn11 = new int[3];
        arrn11[1] = -1;
        arrn11[2] = -1;
        arrarrn7[0] = arrn11;
        int[] arrn12 = new int[3];
        arrn12[2] = 1;
        arrarrn7[1] = arrn12;
        arrarrn[5] = arrarrn7;
        int[][] arrarrn8 = new int[2][];
        int[] arrn13 = new int[3];
        arrn13[2] = 1;
        arrarrn8[0] = arrn13;
        int[] arrn14 = new int[3];
        arrn14[0] = 1;
        arrarrn8[1] = arrn14;
        arrarrn[6] = arrarrn8;
        int[][] arrarrn9 = new int[2][];
        int[] arrn15 = new int[3];
        arrn15[2] = 1;
        arrarrn9[0] = arrn15;
        int[] arrn16 = new int[3];
        arrn16[0] = -1;
        arrarrn9[1] = arrn16;
        arrarrn[7] = arrarrn9;
        int[][] arrarrn10 = new int[2][];
        int[] arrn17 = new int[3];
        arrn17[2] = -1;
        arrarrn10[0] = arrn17;
        int[] arrn18 = new int[3];
        arrn18[0] = -1;
        arrarrn10[1] = arrn18;
        arrarrn[8] = arrarrn10;
        int[][] arrarrn11 = new int[2][];
        int[] arrn19 = new int[3];
        arrn19[2] = -1;
        arrarrn11[0] = arrn19;
        int[] arrn20 = new int[3];
        arrn20[0] = 1;
        arrarrn11[1] = arrn20;
        arrarrn[9] = arrarrn11;
        MATRIX = arrarrn;
    }

    public EntityMinecart(World worldIn) {
        super(worldIn);
        this.preventEntitySpawning = true;
        this.setSize(0.98f, 0.7f);
    }

    public static EntityMinecart create(World worldIn, double x2, double y2, double z2, Type typeIn) {
        switch (typeIn) {
            case CHEST: {
                return new EntityMinecartChest(worldIn, x2, y2, z2);
            }
            case FURNACE: {
                return new EntityMinecartFurnace(worldIn, x2, y2, z2);
            }
            case TNT: {
                return new EntityMinecartTNT(worldIn, x2, y2, z2);
            }
            case SPAWNER: {
                return new EntityMinecartMobSpawner(worldIn, x2, y2, z2);
            }
            case HOPPER: {
                return new EntityMinecartHopper(worldIn, x2, y2, z2);
            }
            case COMMAND_BLOCK: {
                return new EntityMinecartCommandBlock(worldIn, x2, y2, z2);
            }
        }
        return new EntityMinecartEmpty(worldIn, x2, y2, z2);
    }

    @Override
    protected boolean canTriggerWalking() {
        return false;
    }

    @Override
    protected void entityInit() {
        this.dataManager.register(ROLLING_AMPLITUDE, 0);
        this.dataManager.register(ROLLING_DIRECTION, 1);
        this.dataManager.register(DAMAGE, Float.valueOf(0.0f));
        this.dataManager.register(DISPLAY_TILE, 0);
        this.dataManager.register(DISPLAY_TILE_OFFSET, 6);
        this.dataManager.register(SHOW_BLOCK, false);
    }

    @Override
    @Nullable
    public AxisAlignedBB getCollisionBox(Entity entityIn) {
        return entityIn.canBePushed() ? entityIn.getEntityBoundingBox() : null;
    }

    @Override
    @Nullable
    public AxisAlignedBB getCollisionBoundingBox() {
        return null;
    }

    @Override
    public boolean canBePushed() {
        return true;
    }

    public EntityMinecart(World worldIn, double x2, double y2, double z2) {
        this(worldIn);
        this.setPosition(x2, y2, z2);
        this.motionX = 0.0;
        this.motionY = 0.0;
        this.motionZ = 0.0;
        this.prevPosX = x2;
        this.prevPosY = y2;
        this.prevPosZ = z2;
    }

    @Override
    public double getMountedYOffset() {
        return 0.0;
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        if (!this.world.isRemote && !this.isDead) {
            boolean flag;
            if (this.isEntityInvulnerable(source)) {
                return false;
            }
            this.setRollingDirection(-this.getRollingDirection());
            this.setRollingAmplitude(10);
            this.setBeenAttacked();
            this.setDamage(this.getDamage() + amount * 10.0f);
            boolean bl2 = flag = source.getEntity() instanceof EntityPlayer && ((EntityPlayer)source.getEntity()).capabilities.isCreativeMode;
            if (flag || this.getDamage() > 40.0f) {
                this.removePassengers();
                if (flag && !this.hasCustomName()) {
                    this.setDead();
                } else {
                    this.killMinecart(source);
                }
            }
            return true;
        }
        return true;
    }

    public void killMinecart(DamageSource source) {
        this.setDead();
        if (this.world.getGameRules().getBoolean("doEntityDrops")) {
            ItemStack itemstack = new ItemStack(Items.MINECART, 1);
            if (this.hasCustomName()) {
                itemstack.setStackDisplayName(this.getCustomNameTag());
            }
            this.entityDropItem(itemstack, 0.0f);
        }
    }

    @Override
    public void performHurtAnimation() {
        this.setRollingDirection(-this.getRollingDirection());
        this.setRollingAmplitude(10);
        this.setDamage(this.getDamage() + this.getDamage() * 10.0f);
    }

    @Override
    public boolean canBeCollidedWith() {
        return !this.isDead;
    }

    @Override
    public EnumFacing getAdjustedHorizontalFacing() {
        return this.isInReverse ? this.getHorizontalFacing().getOpposite().rotateY() : this.getHorizontalFacing().rotateY();
    }

    @Override
    public void onUpdate() {
        if (this.getRollingAmplitude() > 0) {
            this.setRollingAmplitude(this.getRollingAmplitude() - 1);
        }
        if (this.getDamage() > 0.0f) {
            this.setDamage(this.getDamage() - 1.0f);
        }
        if (this.posY < -64.0) {
            this.kill();
        }
        if (!this.world.isRemote && this.world instanceof WorldServer) {
            this.world.theProfiler.startSection("portal");
            MinecraftServer minecraftserver = this.world.getMinecraftServer();
            int i2 = this.getMaxInPortalTime();
            if (this.inPortal) {
                if (minecraftserver.getAllowNether()) {
                    if (!this.isRiding() && this.portalCounter++ >= i2) {
                        this.portalCounter = i2;
                        this.timeUntilPortal = this.getPortalCooldown();
                        int j2 = this.world.provider.getDimensionType().getId() == -1 ? 0 : -1;
                        this.changeDimension(j2);
                    }
                    this.inPortal = false;
                }
            } else {
                if (this.portalCounter > 0) {
                    this.portalCounter -= 4;
                }
                if (this.portalCounter < 0) {
                    this.portalCounter = 0;
                }
            }
            if (this.timeUntilPortal > 0) {
                --this.timeUntilPortal;
            }
            this.world.theProfiler.endSection();
        }
        if (this.world.isRemote) {
            if (this.turnProgress > 0) {
                double d4 = this.posX + (this.minecartX - this.posX) / (double)this.turnProgress;
                double d5 = this.posY + (this.minecartY - this.posY) / (double)this.turnProgress;
                double d6 = this.posZ + (this.minecartZ - this.posZ) / (double)this.turnProgress;
                double d1 = MathHelper.wrapDegrees(this.minecartYaw - (double)this.rotationYaw);
                this.rotationYaw = (float)((double)this.rotationYaw + d1 / (double)this.turnProgress);
                this.rotationPitch = (float)((double)this.rotationPitch + (this.minecartPitch - (double)this.rotationPitch) / (double)this.turnProgress);
                --this.turnProgress;
                this.setPosition(d4, d5, d6);
                this.setRotation(this.rotationYaw, this.rotationPitch);
            } else {
                this.setPosition(this.posX, this.posY, this.posZ);
                this.setRotation(this.rotationYaw, this.rotationPitch);
            }
        } else {
            double d3;
            BlockPos blockpos;
            IBlockState iblockstate;
            int i1;
            int l2;
            int k2;
            this.prevPosX = this.posX;
            this.prevPosY = this.posY;
            this.prevPosZ = this.posZ;
            if (!this.hasNoGravity()) {
                this.motionY -= (double)0.04f;
            }
            if (BlockRailBase.isRailBlock(this.world, new BlockPos(k2 = MathHelper.floor(this.posX), (l2 = MathHelper.floor(this.posY)) - 1, i1 = MathHelper.floor(this.posZ)))) {
                --l2;
            }
            if (BlockRailBase.isRailBlock(iblockstate = this.world.getBlockState(blockpos = new BlockPos(k2, l2, i1)))) {
                this.moveAlongTrack(blockpos, iblockstate);
                if (iblockstate.getBlock() == Blocks.ACTIVATOR_RAIL) {
                    this.onActivatorRailPass(k2, l2, i1, iblockstate.getValue(BlockRailPowered.POWERED));
                }
            } else {
                this.moveDerailedMinecart();
            }
            this.doBlockCollisions();
            this.rotationPitch = 0.0f;
            double d0 = this.prevPosX - this.posX;
            double d2 = this.prevPosZ - this.posZ;
            if (d0 * d0 + d2 * d2 > 0.001) {
                this.rotationYaw = (float)(MathHelper.atan2(d2, d0) * 180.0 / Math.PI);
                if (this.isInReverse) {
                    this.rotationYaw += 180.0f;
                }
            }
            if ((d3 = (double)MathHelper.wrapDegrees(this.rotationYaw - this.prevRotationYaw)) < -170.0 || d3 >= 170.0) {
                this.rotationYaw += 180.0f;
                this.isInReverse = !this.isInReverse;
            }
            this.setRotation(this.rotationYaw, this.rotationPitch);
            if (this.getType() == Type.RIDEABLE && this.motionX * this.motionX + this.motionZ * this.motionZ > 0.01) {
                List<Entity> list = this.world.getEntitiesInAABBexcluding(this, this.getEntityBoundingBox().expand(0.2f, 0.0, 0.2f), EntitySelectors.getTeamCollisionPredicate(this));
                if (!list.isEmpty()) {
                    for (int j1 = 0; j1 < list.size(); ++j1) {
                        Entity entity1 = list.get(j1);
                        if (!(entity1 instanceof EntityPlayer || entity1 instanceof EntityIronGolem || entity1 instanceof EntityMinecart || this.isBeingRidden() || entity1.isRiding())) {
                            entity1.startRiding(this);
                            continue;
                        }
                        entity1.applyEntityCollision(this);
                    }
                }
            } else {
                for (Entity entity : this.world.getEntitiesWithinAABBExcludingEntity(this, this.getEntityBoundingBox().expand(0.2f, 0.0, 0.2f))) {
                    if (this.isPassenger(entity) || !entity.canBePushed() || !(entity instanceof EntityMinecart)) continue;
                    entity.applyEntityCollision(this);
                }
            }
            this.handleWaterMovement();
        }
    }

    protected double getMaximumSpeed() {
        return 0.4;
    }

    public void onActivatorRailPass(int x2, int y2, int z2, boolean receivingPower) {
    }

    protected void moveDerailedMinecart() {
        double d0 = this.getMaximumSpeed();
        this.motionX = MathHelper.clamp(this.motionX, -d0, d0);
        this.motionZ = MathHelper.clamp(this.motionZ, -d0, d0);
        if (this.onGround) {
            this.motionX *= 0.5;
            this.motionY *= 0.5;
            this.motionZ *= 0.5;
        }
        this.moveEntity(MoverType.SELF, this.motionX, this.motionY, this.motionZ);
        if (!this.onGround) {
            this.motionX *= (double)0.95f;
            this.motionY *= (double)0.95f;
            this.motionZ *= (double)0.95f;
        }
    }

    protected void moveAlongTrack(BlockPos pos, IBlockState state) {
        double d10;
        double d6;
        Entity entity;
        double d5;
        this.fallDistance = 0.0f;
        Vec3d vec3d = this.getPos(this.posX, this.posY, this.posZ);
        this.posY = pos.getY();
        boolean flag = false;
        boolean flag1 = false;
        BlockRailBase blockrailbase = (BlockRailBase)state.getBlock();
        if (blockrailbase == Blocks.GOLDEN_RAIL) {
            flag = state.getValue(BlockRailPowered.POWERED);
            flag1 = !flag;
        }
        double d0 = 0.0078125;
        BlockRailBase.EnumRailDirection blockrailbase$enumraildirection = state.getValue(blockrailbase.getShapeProperty());
        switch (blockrailbase$enumraildirection) {
            case ASCENDING_EAST: {
                this.motionX -= 0.0078125;
                this.posY += 1.0;
                break;
            }
            case ASCENDING_WEST: {
                this.motionX += 0.0078125;
                this.posY += 1.0;
                break;
            }
            case ASCENDING_NORTH: {
                this.motionZ += 0.0078125;
                this.posY += 1.0;
                break;
            }
            case ASCENDING_SOUTH: {
                this.motionZ -= 0.0078125;
                this.posY += 1.0;
            }
        }
        int[][] aint = MATRIX[blockrailbase$enumraildirection.getMetadata()];
        double d1 = aint[1][0] - aint[0][0];
        double d2 = aint[1][2] - aint[0][2];
        double d3 = Math.sqrt(d1 * d1 + d2 * d2);
        double d4 = this.motionX * d1 + this.motionZ * d2;
        if (d4 < 0.0) {
            d1 = -d1;
            d2 = -d2;
        }
        if ((d5 = Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ)) > 2.0) {
            d5 = 2.0;
        }
        this.motionX = d5 * d1 / d3;
        this.motionZ = d5 * d2 / d3;
        Entity entity2 = entity = this.getPassengers().isEmpty() ? null : this.getPassengers().get(0);
        if (entity instanceof EntityLivingBase && (d6 = (double)((EntityLivingBase)entity).field_191988_bg) > 0.0) {
            double d7 = -Math.sin(entity.rotationYaw * ((float)Math.PI / 180));
            double d8 = Math.cos(entity.rotationYaw * ((float)Math.PI / 180));
            double d9 = this.motionX * this.motionX + this.motionZ * this.motionZ;
            if (d9 < 0.01) {
                this.motionX += d7 * 0.1;
                this.motionZ += d8 * 0.1;
                flag1 = false;
            }
        }
        if (flag1) {
            double d17 = Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
            if (d17 < 0.03) {
                this.motionX *= 0.0;
                this.motionY *= 0.0;
                this.motionZ *= 0.0;
            } else {
                this.motionX *= 0.5;
                this.motionY *= 0.0;
                this.motionZ *= 0.5;
            }
        }
        double d18 = (double)pos.getX() + 0.5 + (double)aint[0][0] * 0.5;
        double d19 = (double)pos.getZ() + 0.5 + (double)aint[0][2] * 0.5;
        double d20 = (double)pos.getX() + 0.5 + (double)aint[1][0] * 0.5;
        double d21 = (double)pos.getZ() + 0.5 + (double)aint[1][2] * 0.5;
        d1 = d20 - d18;
        d2 = d21 - d19;
        if (d1 == 0.0) {
            this.posX = (double)pos.getX() + 0.5;
            d10 = this.posZ - (double)pos.getZ();
        } else if (d2 == 0.0) {
            this.posZ = (double)pos.getZ() + 0.5;
            d10 = this.posX - (double)pos.getX();
        } else {
            double d11 = this.posX - d18;
            double d12 = this.posZ - d19;
            d10 = (d11 * d1 + d12 * d2) * 2.0;
        }
        this.posX = d18 + d1 * d10;
        this.posZ = d19 + d2 * d10;
        this.setPosition(this.posX, this.posY, this.posZ);
        double d22 = this.motionX;
        double d23 = this.motionZ;
        if (this.isBeingRidden()) {
            d22 *= 0.75;
            d23 *= 0.75;
        }
        double d13 = this.getMaximumSpeed();
        d22 = MathHelper.clamp(d22, -d13, d13);
        d23 = MathHelper.clamp(d23, -d13, d13);
        this.moveEntity(MoverType.SELF, d22, 0.0, d23);
        if (aint[0][1] != 0 && MathHelper.floor(this.posX) - pos.getX() == aint[0][0] && MathHelper.floor(this.posZ) - pos.getZ() == aint[0][2]) {
            this.setPosition(this.posX, this.posY + (double)aint[0][1], this.posZ);
        } else if (aint[1][1] != 0 && MathHelper.floor(this.posX) - pos.getX() == aint[1][0] && MathHelper.floor(this.posZ) - pos.getZ() == aint[1][2]) {
            this.setPosition(this.posX, this.posY + (double)aint[1][1], this.posZ);
        }
        this.applyDrag();
        Vec3d vec3d1 = this.getPos(this.posX, this.posY, this.posZ);
        if (vec3d1 != null && vec3d != null) {
            double d14 = (vec3d.yCoord - vec3d1.yCoord) * 0.05;
            d5 = Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
            if (d5 > 0.0) {
                this.motionX = this.motionX / d5 * (d5 + d14);
                this.motionZ = this.motionZ / d5 * (d5 + d14);
            }
            this.setPosition(this.posX, vec3d1.yCoord, this.posZ);
        }
        int j2 = MathHelper.floor(this.posX);
        int i2 = MathHelper.floor(this.posZ);
        if (j2 != pos.getX() || i2 != pos.getZ()) {
            d5 = Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
            this.motionX = d5 * (double)(j2 - pos.getX());
            this.motionZ = d5 * (double)(i2 - pos.getZ());
        }
        if (flag) {
            double d15 = Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
            if (d15 > 0.01) {
                double d16 = 0.06;
                this.motionX += this.motionX / d15 * 0.06;
                this.motionZ += this.motionZ / d15 * 0.06;
            } else if (blockrailbase$enumraildirection == BlockRailBase.EnumRailDirection.EAST_WEST) {
                if (this.world.getBlockState(pos.west()).isNormalCube()) {
                    this.motionX = 0.02;
                } else if (this.world.getBlockState(pos.east()).isNormalCube()) {
                    this.motionX = -0.02;
                }
            } else if (blockrailbase$enumraildirection == BlockRailBase.EnumRailDirection.NORTH_SOUTH) {
                if (this.world.getBlockState(pos.north()).isNormalCube()) {
                    this.motionZ = 0.02;
                } else if (this.world.getBlockState(pos.south()).isNormalCube()) {
                    this.motionZ = -0.02;
                }
            }
        }
    }

    protected void applyDrag() {
        if (this.isBeingRidden()) {
            this.motionX *= (double)0.997f;
            this.motionY *= 0.0;
            this.motionZ *= (double)0.997f;
        } else {
            this.motionX *= (double)0.96f;
            this.motionY *= 0.0;
            this.motionZ *= (double)0.96f;
        }
    }

    @Override
    public void setPosition(double x2, double y2, double z2) {
        this.posX = x2;
        this.posY = y2;
        this.posZ = z2;
        float f2 = this.width / 2.0f;
        float f1 = this.height;
        this.setEntityBoundingBox(new AxisAlignedBB(x2 - (double)f2, y2, z2 - (double)f2, x2 + (double)f2, y2 + (double)f1, z2 + (double)f2));
    }

    @Nullable
    public Vec3d getPosOffset(double x2, double y2, double z2, double offset) {
        IBlockState iblockstate;
        int k2;
        int j2;
        int i2 = MathHelper.floor(x2);
        if (BlockRailBase.isRailBlock(this.world, new BlockPos(i2, (j2 = MathHelper.floor(y2)) - 1, k2 = MathHelper.floor(z2)))) {
            --j2;
        }
        if (BlockRailBase.isRailBlock(iblockstate = this.world.getBlockState(new BlockPos(i2, j2, k2)))) {
            BlockRailBase.EnumRailDirection blockrailbase$enumraildirection = iblockstate.getValue(((BlockRailBase)iblockstate.getBlock()).getShapeProperty());
            y2 = j2;
            if (blockrailbase$enumraildirection.isAscending()) {
                y2 = j2 + 1;
            }
            int[][] aint = MATRIX[blockrailbase$enumraildirection.getMetadata()];
            double d0 = aint[1][0] - aint[0][0];
            double d1 = aint[1][2] - aint[0][2];
            double d2 = Math.sqrt(d0 * d0 + d1 * d1);
            if (aint[0][1] != 0 && MathHelper.floor(x2 += (d0 /= d2) * offset) - i2 == aint[0][0] && MathHelper.floor(z2 += (d1 /= d2) * offset) - k2 == aint[0][2]) {
                y2 += (double)aint[0][1];
            } else if (aint[1][1] != 0 && MathHelper.floor(x2) - i2 == aint[1][0] && MathHelper.floor(z2) - k2 == aint[1][2]) {
                y2 += (double)aint[1][1];
            }
            return this.getPos(x2, y2, z2);
        }
        return null;
    }

    @Nullable
    public Vec3d getPos(double p_70489_1_, double p_70489_3_, double p_70489_5_) {
        IBlockState iblockstate;
        int k2;
        int j2;
        int i2 = MathHelper.floor(p_70489_1_);
        if (BlockRailBase.isRailBlock(this.world, new BlockPos(i2, (j2 = MathHelper.floor(p_70489_3_)) - 1, k2 = MathHelper.floor(p_70489_5_)))) {
            --j2;
        }
        if (BlockRailBase.isRailBlock(iblockstate = this.world.getBlockState(new BlockPos(i2, j2, k2)))) {
            double d9;
            BlockRailBase.EnumRailDirection blockrailbase$enumraildirection = iblockstate.getValue(((BlockRailBase)iblockstate.getBlock()).getShapeProperty());
            int[][] aint = MATRIX[blockrailbase$enumraildirection.getMetadata()];
            double d0 = (double)i2 + 0.5 + (double)aint[0][0] * 0.5;
            double d1 = (double)j2 + 0.0625 + (double)aint[0][1] * 0.5;
            double d2 = (double)k2 + 0.5 + (double)aint[0][2] * 0.5;
            double d3 = (double)i2 + 0.5 + (double)aint[1][0] * 0.5;
            double d4 = (double)j2 + 0.0625 + (double)aint[1][1] * 0.5;
            double d5 = (double)k2 + 0.5 + (double)aint[1][2] * 0.5;
            double d6 = d3 - d0;
            double d7 = (d4 - d1) * 2.0;
            double d8 = d5 - d2;
            if (d6 == 0.0) {
                d9 = p_70489_5_ - (double)k2;
            } else if (d8 == 0.0) {
                d9 = p_70489_1_ - (double)i2;
            } else {
                double d10 = p_70489_1_ - d0;
                double d11 = p_70489_5_ - d2;
                d9 = (d10 * d6 + d11 * d8) * 2.0;
            }
            p_70489_1_ = d0 + d6 * d9;
            p_70489_3_ = d1 + d7 * d9;
            p_70489_5_ = d2 + d8 * d9;
            if (d7 < 0.0) {
                p_70489_3_ += 1.0;
            }
            if (d7 > 0.0) {
                p_70489_3_ += 0.5;
            }
            return new Vec3d(p_70489_1_, p_70489_3_, p_70489_5_);
        }
        return null;
    }

    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        AxisAlignedBB axisalignedbb = this.getEntityBoundingBox();
        return this.hasDisplayTile() ? axisalignedbb.expandXyz((double)Math.abs(this.getDisplayTileOffset()) / 16.0) : axisalignedbb;
    }

    public static void registerFixesMinecart(DataFixer fixer, Class<?> name) {
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound compound) {
        if (compound.getBoolean("CustomDisplayTile")) {
            Block block = compound.hasKey("DisplayTile", 8) ? Block.getBlockFromName(compound.getString("DisplayTile")) : Block.getBlockById(compound.getInteger("DisplayTile"));
            int i2 = compound.getInteger("DisplayData");
            this.setDisplayTile(block == null ? Blocks.AIR.getDefaultState() : block.getStateFromMeta(i2));
            this.setDisplayTileOffset(compound.getInteger("DisplayOffset"));
        }
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound compound) {
        if (this.hasDisplayTile()) {
            compound.setBoolean("CustomDisplayTile", true);
            IBlockState iblockstate = this.getDisplayTile();
            ResourceLocation resourcelocation = Block.REGISTRY.getNameForObject(iblockstate.getBlock());
            compound.setString("DisplayTile", resourcelocation == null ? "" : resourcelocation.toString());
            compound.setInteger("DisplayData", iblockstate.getBlock().getMetaFromState(iblockstate));
            compound.setInteger("DisplayOffset", this.getDisplayTileOffset());
        }
    }

    @Override
    public void applyEntityCollision(Entity entityIn) {
        double d1;
        double d0;
        double d2;
        if (!(this.world.isRemote || entityIn.noClip || this.noClip || this.isPassenger(entityIn) || !((d2 = (d0 = entityIn.posX - this.posX) * d0 + (d1 = entityIn.posZ - this.posZ) * d1) >= (double)1.0E-4f))) {
            d2 = MathHelper.sqrt(d2);
            d0 /= d2;
            d1 /= d2;
            double d3 = 1.0 / d2;
            if (d3 > 1.0) {
                d3 = 1.0;
            }
            d0 *= d3;
            d1 *= d3;
            d0 *= (double)0.1f;
            d1 *= (double)0.1f;
            d0 *= (double)(1.0f - this.entityCollisionReduction);
            d1 *= (double)(1.0f - this.entityCollisionReduction);
            d0 *= 0.5;
            d1 *= 0.5;
            if (entityIn instanceof EntityMinecart) {
                Vec3d vec3d1;
                double d4 = entityIn.posX - this.posX;
                double d5 = entityIn.posZ - this.posZ;
                Vec3d vec3d = new Vec3d(d4, 0.0, d5).normalize();
                double d6 = Math.abs(vec3d.dotProduct(vec3d1 = new Vec3d(MathHelper.cos(this.rotationYaw * ((float)Math.PI / 180)), 0.0, MathHelper.sin(this.rotationYaw * ((float)Math.PI / 180))).normalize()));
                if (d6 < (double)0.8f) {
                    return;
                }
                double d7 = entityIn.motionX + this.motionX;
                double d8 = entityIn.motionZ + this.motionZ;
                if (((EntityMinecart)entityIn).getType() == Type.FURNACE && this.getType() != Type.FURNACE) {
                    this.motionX *= (double)0.2f;
                    this.motionZ *= (double)0.2f;
                    this.addVelocity(entityIn.motionX - d0, 0.0, entityIn.motionZ - d1);
                    entityIn.motionX *= (double)0.95f;
                    entityIn.motionZ *= (double)0.95f;
                } else if (((EntityMinecart)entityIn).getType() != Type.FURNACE && this.getType() == Type.FURNACE) {
                    entityIn.motionX *= (double)0.2f;
                    entityIn.motionZ *= (double)0.2f;
                    entityIn.addVelocity(this.motionX + d0, 0.0, this.motionZ + d1);
                    this.motionX *= (double)0.95f;
                    this.motionZ *= (double)0.95f;
                } else {
                    this.motionX *= (double)0.2f;
                    this.motionZ *= (double)0.2f;
                    this.addVelocity((d7 /= 2.0) - d0, 0.0, (d8 /= 2.0) - d1);
                    entityIn.motionX *= (double)0.2f;
                    entityIn.motionZ *= (double)0.2f;
                    entityIn.addVelocity(d7 + d0, 0.0, d8 + d1);
                }
            } else {
                this.addVelocity(-d0, 0.0, -d1);
                entityIn.addVelocity(d0 / 4.0, 0.0, d1 / 4.0);
            }
        }
    }

    @Override
    public void setPositionAndRotationDirect(double x2, double y2, double z2, float yaw, float pitch, int posRotationIncrements, boolean teleport) {
        this.minecartX = x2;
        this.minecartY = y2;
        this.minecartZ = z2;
        this.minecartYaw = yaw;
        this.minecartPitch = pitch;
        this.turnProgress = posRotationIncrements + 2;
        this.motionX = this.velocityX;
        this.motionY = this.velocityY;
        this.motionZ = this.velocityZ;
    }

    @Override
    public void setVelocity(double x2, double y2, double z2) {
        this.motionX = x2;
        this.motionY = y2;
        this.motionZ = z2;
        this.velocityX = this.motionX;
        this.velocityY = this.motionY;
        this.velocityZ = this.motionZ;
    }

    public void setDamage(float damage) {
        this.dataManager.set(DAMAGE, Float.valueOf(damage));
    }

    public float getDamage() {
        return this.dataManager.get(DAMAGE).floatValue();
    }

    public void setRollingAmplitude(int rollingAmplitude) {
        this.dataManager.set(ROLLING_AMPLITUDE, rollingAmplitude);
    }

    public int getRollingAmplitude() {
        return this.dataManager.get(ROLLING_AMPLITUDE);
    }

    public void setRollingDirection(int rollingDirection) {
        this.dataManager.set(ROLLING_DIRECTION, rollingDirection);
    }

    public int getRollingDirection() {
        return this.dataManager.get(ROLLING_DIRECTION);
    }

    public abstract Type getType();

    public IBlockState getDisplayTile() {
        return !this.hasDisplayTile() ? this.getDefaultDisplayTile() : Block.getStateById(this.getDataManager().get(DISPLAY_TILE));
    }

    public IBlockState getDefaultDisplayTile() {
        return Blocks.AIR.getDefaultState();
    }

    public int getDisplayTileOffset() {
        return !this.hasDisplayTile() ? this.getDefaultDisplayTileOffset() : this.getDataManager().get(DISPLAY_TILE_OFFSET).intValue();
    }

    public int getDefaultDisplayTileOffset() {
        return 6;
    }

    public void setDisplayTile(IBlockState displayTile) {
        this.getDataManager().set(DISPLAY_TILE, Block.getStateId(displayTile));
        this.setHasDisplayTile(true);
    }

    public void setDisplayTileOffset(int displayTileOffset) {
        this.getDataManager().set(DISPLAY_TILE_OFFSET, displayTileOffset);
        this.setHasDisplayTile(true);
    }

    public boolean hasDisplayTile() {
        return this.getDataManager().get(SHOW_BLOCK);
    }

    public void setHasDisplayTile(boolean showBlock) {
        this.getDataManager().set(SHOW_BLOCK, showBlock);
    }

    public static enum Type {
        RIDEABLE(0, "MinecartRideable"),
        CHEST(1, "MinecartChest"),
        FURNACE(2, "MinecartFurnace"),
        TNT(3, "MinecartTNT"),
        SPAWNER(4, "MinecartSpawner"),
        HOPPER(5, "MinecartHopper"),
        COMMAND_BLOCK(6, "MinecartCommandBlock");

        private static final Map<Integer, Type> BY_ID;
        private final int id;
        private final String name;

        static {
            BY_ID = Maps.newHashMap();
            for (Type entityminecart$type : Type.values()) {
                BY_ID.put(entityminecart$type.getId(), entityminecart$type);
            }
        }

        private Type(int idIn, String nameIn) {
            this.id = idIn;
            this.name = nameIn;
        }

        public int getId() {
            return this.id;
        }

        public String getName() {
            return this.name;
        }

        public static Type getById(int idIn) {
            Type entityminecart$type = BY_ID.get(idIn);
            return entityminecart$type == null ? RIDEABLE : entityminecart$type;
        }
    }
}

