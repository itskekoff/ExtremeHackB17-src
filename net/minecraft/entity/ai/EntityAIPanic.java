package net.minecraft.entity.ai;

import javax.annotation.Nullable;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class EntityAIPanic
extends EntityAIBase {
    protected final EntityCreature theEntityCreature;
    protected double speed;
    protected double randPosX;
    protected double randPosY;
    protected double randPosZ;

    public EntityAIPanic(EntityCreature creature, double speedIn) {
        this.theEntityCreature = creature;
        this.speed = speedIn;
        this.setMutexBits(1);
    }

    @Override
    public boolean shouldExecute() {
        BlockPos blockpos;
        if (this.theEntityCreature.getAITarget() == null && !this.theEntityCreature.isBurning()) {
            return false;
        }
        if (this.theEntityCreature.isBurning() && (blockpos = this.getRandPos(this.theEntityCreature.world, this.theEntityCreature, 5, 4)) != null) {
            this.randPosX = blockpos.getX();
            this.randPosY = blockpos.getY();
            this.randPosZ = blockpos.getZ();
            return true;
        }
        return this.func_190863_f();
    }

    protected boolean func_190863_f() {
        Vec3d vec3d = RandomPositionGenerator.findRandomTarget(this.theEntityCreature, 5, 4);
        if (vec3d == null) {
            return false;
        }
        this.randPosX = vec3d.xCoord;
        this.randPosY = vec3d.yCoord;
        this.randPosZ = vec3d.zCoord;
        return true;
    }

    @Override
    public void startExecuting() {
        this.theEntityCreature.getNavigator().tryMoveToXYZ(this.randPosX, this.randPosY, this.randPosZ, this.speed);
    }

    @Override
    public boolean continueExecuting() {
        return !this.theEntityCreature.getNavigator().noPath();
    }

    @Nullable
    private BlockPos getRandPos(World worldIn, Entity entityIn, int horizontalRange, int verticalRange) {
        BlockPos blockpos = new BlockPos(entityIn);
        int i2 = blockpos.getX();
        int j2 = blockpos.getY();
        int k2 = blockpos.getZ();
        float f2 = horizontalRange * horizontalRange * verticalRange * 2;
        BlockPos blockpos1 = null;
        BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();
        for (int l2 = i2 - horizontalRange; l2 <= i2 + horizontalRange; ++l2) {
            for (int i1 = j2 - verticalRange; i1 <= j2 + verticalRange; ++i1) {
                for (int j1 = k2 - horizontalRange; j1 <= k2 + horizontalRange; ++j1) {
                    float f1;
                    blockpos$mutableblockpos.setPos(l2, i1, j1);
                    IBlockState iblockstate = worldIn.getBlockState(blockpos$mutableblockpos);
                    if (iblockstate.getMaterial() != Material.WATER || !((f1 = (float)((l2 - i2) * (l2 - i2) + (i1 - j2) * (i1 - j2) + (j1 - k2) * (j1 - k2))) < f2)) continue;
                    f2 = f1;
                    blockpos1 = new BlockPos(blockpos$mutableblockpos);
                }
            }
        }
        return blockpos1;
    }
}

