package net.minecraft.pathfinding;

import net.minecraft.network.PacketBuffer;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.util.math.MathHelper;

public class PathPoint {
    public final int xCoord;
    public final int yCoord;
    public final int zCoord;
    private final int hash;
    public int index = -1;
    public float totalPathDistance;
    public float distanceToNext;
    public float distanceToTarget;
    public PathPoint previous;
    public boolean visited;
    public float distanceFromOrigin;
    public float cost;
    public float costMalus;
    public PathNodeType nodeType = PathNodeType.BLOCKED;

    public PathPoint(int x2, int y2, int z2) {
        this.xCoord = x2;
        this.yCoord = y2;
        this.zCoord = z2;
        this.hash = PathPoint.makeHash(x2, y2, z2);
    }

    public PathPoint cloneMove(int x2, int y2, int z2) {
        PathPoint pathpoint = new PathPoint(x2, y2, z2);
        pathpoint.index = this.index;
        pathpoint.totalPathDistance = this.totalPathDistance;
        pathpoint.distanceToNext = this.distanceToNext;
        pathpoint.distanceToTarget = this.distanceToTarget;
        pathpoint.previous = this.previous;
        pathpoint.visited = this.visited;
        pathpoint.distanceFromOrigin = this.distanceFromOrigin;
        pathpoint.cost = this.cost;
        pathpoint.costMalus = this.costMalus;
        pathpoint.nodeType = this.nodeType;
        return pathpoint;
    }

    public static int makeHash(int x2, int y2, int z2) {
        return y2 & 0xFF | (x2 & 0x7FFF) << 8 | (z2 & 0x7FFF) << 24 | (x2 < 0 ? Integer.MIN_VALUE : 0) | (z2 < 0 ? 32768 : 0);
    }

    public float distanceTo(PathPoint pathpointIn) {
        float f2 = pathpointIn.xCoord - this.xCoord;
        float f1 = pathpointIn.yCoord - this.yCoord;
        float f22 = pathpointIn.zCoord - this.zCoord;
        return MathHelper.sqrt(f2 * f2 + f1 * f1 + f22 * f22);
    }

    public float distanceToSquared(PathPoint pathpointIn) {
        float f2 = pathpointIn.xCoord - this.xCoord;
        float f1 = pathpointIn.yCoord - this.yCoord;
        float f22 = pathpointIn.zCoord - this.zCoord;
        return f2 * f2 + f1 * f1 + f22 * f22;
    }

    public float distanceManhattan(PathPoint p_186281_1_) {
        float f2 = Math.abs(p_186281_1_.xCoord - this.xCoord);
        float f1 = Math.abs(p_186281_1_.yCoord - this.yCoord);
        float f22 = Math.abs(p_186281_1_.zCoord - this.zCoord);
        return f2 + f1 + f22;
    }

    public boolean equals(Object p_equals_1_) {
        if (!(p_equals_1_ instanceof PathPoint)) {
            return false;
        }
        PathPoint pathpoint = (PathPoint)p_equals_1_;
        return this.hash == pathpoint.hash && this.xCoord == pathpoint.xCoord && this.yCoord == pathpoint.yCoord && this.zCoord == pathpoint.zCoord;
    }

    public int hashCode() {
        return this.hash;
    }

    public boolean isAssigned() {
        return this.index >= 0;
    }

    public String toString() {
        return String.valueOf(this.xCoord) + ", " + this.yCoord + ", " + this.zCoord;
    }

    public static PathPoint createFromBuffer(PacketBuffer buf2) {
        PathPoint pathpoint = new PathPoint(buf2.readInt(), buf2.readInt(), buf2.readInt());
        pathpoint.distanceFromOrigin = buf2.readFloat();
        pathpoint.cost = buf2.readFloat();
        pathpoint.costMalus = buf2.readFloat();
        pathpoint.visited = buf2.readBoolean();
        pathpoint.nodeType = PathNodeType.values()[buf2.readInt()];
        pathpoint.distanceToTarget = buf2.readFloat();
        return pathpoint;
    }
}

