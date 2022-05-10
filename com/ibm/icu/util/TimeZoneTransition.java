package com.ibm.icu.util;

import com.ibm.icu.util.TimeZoneRule;

public class TimeZoneTransition {
    private final TimeZoneRule from;
    private final TimeZoneRule to;
    private final long time;

    public TimeZoneTransition(long time, TimeZoneRule from, TimeZoneRule to2) {
        this.time = time;
        this.from = from;
        this.to = to2;
    }

    public long getTime() {
        return this.time;
    }

    public TimeZoneRule getTo() {
        return this.to;
    }

    public TimeZoneRule getFrom() {
        return this.from;
    }

    public String toString() {
        StringBuilder buf2 = new StringBuilder();
        buf2.append("time=" + this.time);
        buf2.append(", from={" + this.from + "}");
        buf2.append(", to={" + this.to + "}");
        return buf2.toString();
    }
}

