package ShwepSS.B17.Utils;

public final class TimeHelper {
    private long lastMS;

    public TimeHelper() {
        this.reset();
    }

    public long getCurrentMS() {
        return System.nanoTime() / 1000000L;
    }

    public long getLastMS() {
        return this.lastMS;
    }

    public boolean hasReached(double milliseconds) {
        return (double)(this.getCurrentMS() - this.lastMS) >= milliseconds;
    }

    public boolean hasReached(int milliseconds) {
        return this.getCurrentMS() - this.lastMS >= (long)milliseconds;
    }

    public boolean hasReached(long milliseconds) {
        return this.getCurrentMS() - this.lastMS >= milliseconds;
    }

    public boolean hasReached(float milliseconds) {
        return (float)(this.getCurrentMS() - this.lastMS) >= milliseconds;
    }

    public boolean hasReached(Float milliseconds) {
        return (float)(this.getCurrentMS() - this.lastMS) >= milliseconds.floatValue();
    }

    public long getTimeDiff() {
        return this.getCurrentMS() - this.lastMS;
    }

    public void reset() {
        this.lastMS = this.getCurrentMS();
    }

    public void setLastMS(long currentMS) {
        this.lastMS = currentMS;
    }
}

