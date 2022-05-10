package net.minecraft.entity.ai;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityCreature;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class RandomPositionGenerator {
    private static Vec3d staticVector = Vec3d.ZERO;

    @Nullable
    public static Vec3d findRandomTarget(EntityCreature entitycreatureIn, int xz2, int y2) {
        return RandomPositionGenerator.findRandomTargetBlock(entitycreatureIn, xz2, y2, null);
    }

    @Nullable
    public static Vec3d func_191377_b(EntityCreature p_191377_0_, int p_191377_1_, int p_191377_2_) {
        return RandomPositionGenerator.func_191379_a(p_191377_0_, p_191377_1_, p_191377_2_, null, false);
    }

    @Nullable
    public static Vec3d findRandomTargetBlockTowards(EntityCreature entitycreatureIn, int xz2, int y2, Vec3d targetVec3) {
        staticVector = targetVec3.subtract(entitycreatureIn.posX, entitycreatureIn.posY, entitycreatureIn.posZ);
        return RandomPositionGenerator.findRandomTargetBlock(entitycreatureIn, xz2, y2, staticVector);
    }

    @Nullable
    public static Vec3d findRandomTargetBlockAwayFrom(EntityCreature entitycreatureIn, int xz2, int y2, Vec3d targetVec3) {
        staticVector = new Vec3d(entitycreatureIn.posX, entitycreatureIn.posY, entitycreatureIn.posZ).subtract(targetVec3);
        return RandomPositionGenerator.findRandomTargetBlock(entitycreatureIn, xz2, y2, staticVector);
    }

    @Nullable
    private static Vec3d findRandomTargetBlock(EntityCreature entitycreatureIn, int xz2, int y2, @Nullable Vec3d targetVec3) {
        return RandomPositionGenerator.func_191379_a(entitycreatureIn, xz2, y2, targetVec3, true);
    }

    @Nullable
    private static Vec3d func_191379_a(EntityCreature p_191379_0_, int p_191379_1_, int p_191379_2_, @Nullable Vec3d p_191379_3_, boolean p_191379_4_) {
        double d1;
        double d0;
        PathNavigate pathnavigate = p_191379_0_.getNavigator();
        Random random = p_191379_0_.getRNG();
        boolean flag = p_191379_0_.hasHome() ? (d0 = p_191379_0_.getHomePosition().distanceSq(MathHelper.floor(p_191379_0_.posX), MathHelper.floor(p_191379_0_.posY), MathHelper.floor(p_191379_0_.posZ)) + 4.0) < (d1 = (double)(p_191379_0_.getMaximumHomeDistance() + (float)p_191379_1_)) * d1 : false;
        boolean flag1 = false;
        float f2 = -99999.0f;
        int k1 = 0;
        int i2 = 0;
        int j2 = 0;
        for (int k2 = 0; k2 < 10; ++k2) {
            float f1;
            int l2 = random.nextInt(2 * p_191379_1_ + 1) - p_191379_1_;
            int i1 = random.nextInt(2 * p_191379_2_ + 1) - p_191379_2_;
            int j1 = random.nextInt(2 * p_191379_1_ + 1) - p_191379_1_;
            if (p_191379_3_ != null && !((double)l2 * p_191379_3_.xCoord + (double)j1 * p_191379_3_.zCoord >= 0.0)) continue;
            if (p_191379_0_.hasHome() && p_191379_1_ > 1) {
                BlockPos blockpos = p_191379_0_.getHomePosition();
                l2 = p_191379_0_.posX > (double)blockpos.getX() ? (l2 -= random.nextInt(p_191379_1_ / 2)) : (l2 += random.nextInt(p_191379_1_ / 2));
                j1 = p_191379_0_.posZ > (double)blockpos.getZ() ? (j1 -= random.nextInt(p_191379_1_ / 2)) : (j1 += random.nextInt(p_191379_1_ / 2));
            }
            BlockPos blockpos1 = new BlockPos((double)l2 + p_191379_0_.posX, (double)i1 + p_191379_0_.posY, (double)j1 + p_191379_0_.posZ);
            if (flag && !p_191379_0_.isWithinHomeDistanceFromPosition(blockpos1) || !pathnavigate.canEntityStandOnPos(blockpos1) || !p_191379_4_ && RandomPositionGenerator.func_191380_b(blockpos1 = RandomPositionGenerator.func_191378_a(blockpos1, p_191379_0_), p_191379_0_) || !((f1 = p_191379_0_.getBlockPathWeight(blockpos1)) > f2)) continue;
            f2 = f1;
            k1 = l2;
            i2 = i1;
            j2 = j1;
            flag1 = true;
        }
        if (flag1) {
            return new Vec3d((double)k1 + p_191379_0_.posX, (double)i2 + p_191379_0_.posY, (double)j2 + p_191379_0_.posZ);
        }
        return null;
    }

    private static BlockPos func_191378_a(BlockPos p_191378_0_, EntityCreature p_191378_1_) {
        if (!p_191378_1_.world.getBlockState(p_191378_0_).getMaterial().isSolid()) {
            return p_191378_0_;
        }
        BlockPos blockpos = p_191378_0_.up();
        while (blockpos.getY() < p_191378_1_.world.getHeight() && p_191378_1_.world.getBlockState(blockpos).getMaterial().isSolid()) {
            blockpos = blockpos.up();
        }
        return blockpos;
    }

    private static boolean func_191380_b(BlockPos p_191380_0_, EntityCreature p_191380_1_) {
        return p_191380_1_.world.getBlockState(p_191380_0_).getMaterial() == Material.WATER;
    }
}

