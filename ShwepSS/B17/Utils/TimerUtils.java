package ShwepSS.B17.Utils;

public class TimerUtils {
    private long previousTime = -1L;

    public boolean check(float milliseconds) {
        return (float)(this.getCurrentTime() - this.getPreviousTime()) >= milliseconds;
    }

    public void reset() {
        this.previousTime = this.getCurrentTime();
    }

    public short convert(float perSecond) {
        return (short)(1000.0f / perSecond);
    }

    public long get() {
        return this.getPreviousTime();
    }

    public long getCurrentMS() {
        return System.nanoTime() / 1000000L;
    }

    public boolean hasReached(long milliseconds) {
        return this.getCurrentMS() - this.getPreviousTime() >= milliseconds;
    }

    public long getCurrentTime() {
        return System.currentTimeMillis();
    }

    public long getTime() {
        return System.currentTimeMillis() - this.getPreviousTime();
    }

    public long getPreviousTime() {
        return this.previousTime;
    }
}

