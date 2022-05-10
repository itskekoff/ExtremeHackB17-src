package net.minecraft.pathfinding;

import net.minecraft.pathfinding.PathPoint;

public class PathHeap {
    private PathPoint[] pathPoints = new PathPoint[128];
    private int count;

    public PathPoint addPoint(PathPoint point) {
        if (point.index >= 0) {
            throw new IllegalStateException("OW KNOWS!");
        }
        if (this.count == this.pathPoints.length) {
            PathPoint[] apathpoint = new PathPoint[this.count << 1];
            System.arraycopy(this.pathPoints, 0, apathpoint, 0, this.count);
            this.pathPoints = apathpoint;
        }
        this.pathPoints[this.count] = point;
        point.index = this.count;
        this.sortBack(this.count++);
        return point;
    }

    public void clearPath() {
        this.count = 0;
    }

    public PathPoint dequeue() {
        PathPoint pathpoint = this.pathPoints[0];
        this.pathPoints[0] = this.pathPoints[--this.count];
        this.pathPoints[this.count] = null;
        if (this.count > 0) {
            this.sortForward(0);
        }
        pathpoint.index = -1;
        return pathpoint;
    }

    public void changeDistance(PathPoint point, float distance) {
        float f2 = point.distanceToTarget;
        point.distanceToTarget = distance;
        if (distance < f2) {
            this.sortBack(point.index);
        } else {
            this.sortForward(point.index);
        }
    }

    private void sortBack(int index) {
        PathPoint pathpoint = this.pathPoints[index];
        float f2 = pathpoint.distanceToTarget;
        while (index > 0) {
            int i2 = index - 1 >> 1;
            PathPoint pathpoint1 = this.pathPoints[i2];
            if (f2 >= pathpoint1.distanceToTarget) break;
            this.pathPoints[index] = pathpoint1;
            pathpoint1.index = index;
            index = i2;
        }
        this.pathPoints[index] = pathpoint;
        pathpoint.index = index;
    }

    private void sortForward(int index) {
        PathPoint pathpoint = this.pathPoints[index];
        float f2 = pathpoint.distanceToTarget;
        while (true) {
            float f22;
            PathPoint pathpoint2;
            int i2 = 1 + (index << 1);
            int j2 = i2 + 1;
            if (i2 >= this.count) break;
            PathPoint pathpoint1 = this.pathPoints[i2];
            float f1 = pathpoint1.distanceToTarget;
            if (j2 >= this.count) {
                pathpoint2 = null;
                f22 = Float.POSITIVE_INFINITY;
            } else {
                pathpoint2 = this.pathPoints[j2];
                f22 = pathpoint2.distanceToTarget;
            }
            if (f1 < f22) {
                if (f1 >= f2) break;
                this.pathPoints[index] = pathpoint1;
                pathpoint1.index = index;
                index = i2;
                continue;
            }
            if (f22 >= f2) break;
            this.pathPoints[index] = pathpoint2;
            pathpoint2.index = index;
            index = j2;
        }
        this.pathPoints[index] = pathpoint;
        pathpoint.index = index;
    }

    public boolean isPathEmpty() {
        return this.count == 0;
    }
}

