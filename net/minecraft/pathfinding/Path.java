package net.minecraft.pathfinding;

import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.math.Vec3d;

public class Path {
    private final PathPoint[] points;
    private PathPoint[] openSet = new PathPoint[0];
    private PathPoint[] closedSet = new PathPoint[0];
    private PathPoint target;
    private int currentPathIndex;
    private int pathLength;

    public Path(PathPoint[] pathpoints) {
        this.points = pathpoints;
        this.pathLength = pathpoints.length;
    }

    public void incrementPathIndex() {
        ++this.currentPathIndex;
    }

    public boolean isFinished() {
        return this.currentPathIndex >= this.pathLength;
    }

    @Nullable
    public PathPoint getFinalPathPoint() {
        return this.pathLength > 0 ? this.points[this.pathLength - 1] : null;
    }

    public PathPoint getPathPointFromIndex(int index) {
        return this.points[index];
    }

    public void setPoint(int index, PathPoint point) {
        this.points[index] = point;
    }

    public int getCurrentPathLength() {
        return this.pathLength;
    }

    public void setCurrentPathLength(int length) {
        this.pathLength = length;
    }

    public int getCurrentPathIndex() {
        return this.currentPathIndex;
    }

    public void setCurrentPathIndex(int currentPathIndexIn) {
        this.currentPathIndex = currentPathIndexIn;
    }

    public Vec3d getVectorFromIndex(Entity entityIn, int index) {
        double d0 = (double)this.points[index].xCoord + (double)((int)(entityIn.width + 1.0f)) * 0.5;
        double d1 = this.points[index].yCoord;
        double d2 = (double)this.points[index].zCoord + (double)((int)(entityIn.width + 1.0f)) * 0.5;
        return new Vec3d(d0, d1, d2);
    }

    public Vec3d getPosition(Entity entityIn) {
        return this.getVectorFromIndex(entityIn, this.currentPathIndex);
    }

    public Vec3d getCurrentPos() {
        PathPoint pathpoint = this.points[this.currentPathIndex];
        return new Vec3d(pathpoint.xCoord, pathpoint.yCoord, pathpoint.zCoord);
    }

    public boolean isSamePath(Path pathentityIn) {
        if (pathentityIn == null) {
            return false;
        }
        if (pathentityIn.points.length != this.points.length) {
            return false;
        }
        for (int i2 = 0; i2 < this.points.length; ++i2) {
            if (this.points[i2].xCoord == pathentityIn.points[i2].xCoord && this.points[i2].yCoord == pathentityIn.points[i2].yCoord && this.points[i2].zCoord == pathentityIn.points[i2].zCoord) continue;
            return false;
        }
        return true;
    }

    public PathPoint[] getOpenSet() {
        return this.openSet;
    }

    public PathPoint[] getClosedSet() {
        return this.closedSet;
    }

    public PathPoint getTarget() {
        return this.target;
    }

    public static Path read(PacketBuffer buf2) {
        int i2 = buf2.readInt();
        PathPoint pathpoint = PathPoint.createFromBuffer(buf2);
        PathPoint[] apathpoint = new PathPoint[buf2.readInt()];
        for (int j2 = 0; j2 < apathpoint.length; ++j2) {
            apathpoint[j2] = PathPoint.createFromBuffer(buf2);
        }
        PathPoint[] apathpoint1 = new PathPoint[buf2.readInt()];
        for (int k2 = 0; k2 < apathpoint1.length; ++k2) {
            apathpoint1[k2] = PathPoint.createFromBuffer(buf2);
        }
        PathPoint[] apathpoint2 = new PathPoint[buf2.readInt()];
        for (int l2 = 0; l2 < apathpoint2.length; ++l2) {
            apathpoint2[l2] = PathPoint.createFromBuffer(buf2);
        }
        Path path = new Path(apathpoint);
        path.openSet = apathpoint1;
        path.closedSet = apathpoint2;
        path.target = pathpoint;
        path.currentPathIndex = i2;
        return path;
    }
}

