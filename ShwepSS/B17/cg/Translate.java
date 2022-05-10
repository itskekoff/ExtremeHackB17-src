package ShwepSS.B17.cg;

import ShwepSS.B17.cg.AnimationUtil;

public final class Translate {
    private double x;
    private double y;

    public Translate(float x2, float y2) {
        this.x = x2;
        this.y = y2;
    }

    public final void interpolate(double targetX, double targetY, double smoothing) {
        this.x = AnimationUtil.animate(targetX, this.x, smoothing);
        this.y = AnimationUtil.animate(targetY, this.y, smoothing);
    }

    public void animate(double newX, double newY) {
        this.x = AnimationUtil.animate(this.x, newX, 1.0);
        this.y = AnimationUtil.animate(this.y, newY, 1.0);
    }

    public double getX() {
        return this.x;
    }

    public void setX(double x2) {
        this.x = x2;
    }

    public double getY() {
        return this.y;
    }

    public void setY(double y2) {
        this.y = y2;
    }
}

