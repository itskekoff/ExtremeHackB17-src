package ShwepSS.event;

import ShwepSS.eventapi.events.Event;

public class MoveEvent
implements Event {
    public double x;
    public double y;
    public double z;
    private boolean safeWalk;

    public MoveEvent(double x2, double y2, double z2) {
        this.x = x2;
        this.y = y2;
        this.z = z2;
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    public double getZ() {
        return this.z;
    }

    public boolean isSafeWalk() {
        return this.safeWalk;
    }

    public void setX(double x2) {
        this.x = x2;
    }

    public void setY(double y2) {
        this.y = y2;
    }

    public void setZ(double z2) {
        this.z = z2;
    }

    public void setSafeWalk(boolean value) {
        this.safeWalk = value;
    }
}

