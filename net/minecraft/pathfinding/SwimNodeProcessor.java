package net.minecraft.pathfinding;

import javax.annotation.Nullable;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLiving;
import net.minecraft.pathfinding.NodeProcessor;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;

public class SwimNodeProcessor
extends NodeProcessor {
    @Override
    public PathPoint getStart() {
        return this.openPoint(MathHelper.floor(this.entity.getEntityBoundingBox().minX), MathHelper.floor(this.entity.getEntityBoundingBox().minY + 0.5), MathHelper.floor(this.entity.getEntityBoundingBox().minZ));
    }

    @Override
    public PathPoint getPathPointToCoords(double x2, double y2, double z2) {
        return this.openPoint(MathHelper.floor(x2 - (double)(this.entity.width / 2.0f)), MathHelper.floor(y2 + 0.5), MathHelper.floor(z2 - (double)(this.entity.width / 2.0f)));
    }

    @Override
    public int findPathOptions(PathPoint[] pathOptions, PathPoint currentPoint, PathPoint targetPoint, float maxDistance) {
        int i2 = 0;
        for (EnumFacing enumfacing : EnumFacing.values()) {
            PathPoint pathpoint = this.getWaterNode(currentPoint.xCoord + enumfacing.getFrontOffsetX(), currentPoint.yCoord + enumfacing.getFrontOffsetY(), currentPoint.zCoord + enumfacing.getFrontOffsetZ());
            if (pathpoint == null || pathpoint.visited || !(pathpoint.distanceTo(targetPoint) < maxDistance)) continue;
            pathOptions[i2++] = pathpoint;
        }
        return i2;
    }

    @Override
    public PathNodeType getPathNodeType(IBlockAccess blockaccessIn, int x2, int y2, int z2, EntityLiving entitylivingIn, int xSize, int ySize, int zSize, boolean canBreakDoorsIn, boolean canEnterDoorsIn) {
        return PathNodeType.WATER;
    }

    @Override
    public PathNodeType getPathNodeType(IBlockAccess blockaccessIn, int x2, int y2, int z2) {
        return PathNodeType.WATER;
    }

    @Nullable
    private PathPoint getWaterNode(int p_186328_1_, int p_186328_2_, int p_186328_3_) {
        PathNodeType pathnodetype = this.isFree(p_186328_1_, p_186328_2_, p_186328_3_);
        return pathnodetype == PathNodeType.WATER ? this.openPoint(p_186328_1_, p_186328_2_, p_186328_3_) : null;
    }

    private PathNodeType isFree(int p_186327_1_, int p_186327_2_, int p_186327_3_) {
        BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();
        for (int i2 = p_186327_1_; i2 < p_186327_1_ + this.entitySizeX; ++i2) {
            for (int j2 = p_186327_2_; j2 < p_186327_2_ + this.entitySizeY; ++j2) {
                for (int k2 = p_186327_3_; k2 < p_186327_3_ + this.entitySizeZ; ++k2) {
                    IBlockState iblockstate = this.blockaccess.getBlockState(blockpos$mutableblockpos.setPos(i2, j2, k2));
                    if (iblockstate.getMaterial() == Material.WATER) continue;
                    return PathNodeType.BLOCKED;
                }
            }
        }
        return PathNodeType.WATER;
    }
}

