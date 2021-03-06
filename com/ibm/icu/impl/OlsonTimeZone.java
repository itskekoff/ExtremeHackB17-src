package com.ibm.icu.impl;

import com.ibm.icu.impl.Grego;
import com.ibm.icu.impl.ICUDebug;
import com.ibm.icu.impl.ICUResourceBundle;
import com.ibm.icu.impl.Utility;
import com.ibm.icu.impl.ZoneMeta;
import com.ibm.icu.util.AnnualTimeZoneRule;
import com.ibm.icu.util.BasicTimeZone;
import com.ibm.icu.util.DateTimeRule;
import com.ibm.icu.util.InitialTimeZoneRule;
import com.ibm.icu.util.SimpleTimeZone;
import com.ibm.icu.util.TimeArrayTimeZoneRule;
import com.ibm.icu.util.TimeZone;
import com.ibm.icu.util.TimeZoneRule;
import com.ibm.icu.util.TimeZoneTransition;
import com.ibm.icu.util.UResourceBundle;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Arrays;
import java.util.Date;
import java.util.MissingResourceException;

public class OlsonTimeZone
extends BasicTimeZone {
    static final long serialVersionUID = -6281977362477515376L;
    private int transitionCount;
    private int typeCount;
    private long[] transitionTimes64;
    private int[] typeOffsets;
    private byte[] typeMapData;
    private int finalStartYear = Integer.MAX_VALUE;
    private double finalStartMillis = Double.MAX_VALUE;
    private SimpleTimeZone finalZone = null;
    private volatile String canonicalID = null;
    private static final String ZONEINFORES = "zoneinfo64";
    private static final boolean DEBUG = ICUDebug.enabled("olson");
    private static final int SECONDS_PER_DAY = 86400;
    private transient InitialTimeZoneRule initialRule;
    private transient TimeZoneTransition firstTZTransition;
    private transient int firstTZTransitionIdx;
    private transient TimeZoneTransition firstFinalTZTransition;
    private transient TimeArrayTimeZoneRule[] historicRules;
    private transient SimpleTimeZone finalZoneWithStartYear;
    private transient boolean transitionRulesInitialized;
    private static final int currentSerialVersion = 1;
    private int serialVersionOnStream = 1;
    private transient boolean isFrozen = false;

    public int getOffset(int era, int year, int month, int day, int dayOfWeek, int milliseconds) {
        if (month < 0 || month > 11) {
            throw new IllegalArgumentException("Month is not in the legal range: " + month);
        }
        return this.getOffset(era, year, month, day, dayOfWeek, milliseconds, Grego.monthLength(year, month));
    }

    public int getOffset(int era, int year, int month, int dom, int dow, int millis, int monthLength) {
        if (era != 1 && era != 0 || month < 0 || month > 11 || dom < 1 || dom > monthLength || dow < 1 || dow > 7 || millis < 0 || millis >= 86400000 || monthLength < 28 || monthLength > 31) {
            throw new IllegalArgumentException();
        }
        if (era == 0) {
            year = -year;
        }
        if (this.finalZone != null && year >= this.finalStartYear) {
            return this.finalZone.getOffset(era, year, month, dom, dow, millis);
        }
        long time = Grego.fieldsToDay(year, month, dom) * 86400000L + (long)millis;
        int[] offsets = new int[2];
        this.getHistoricalOffset(time, true, 3, 1, offsets);
        return offsets[0] + offsets[1];
    }

    public void setRawOffset(int offsetMillis) {
        if (this.isFrozen()) {
            throw new UnsupportedOperationException("Attempt to modify a frozen OlsonTimeZone instance.");
        }
        if (this.getRawOffset() == offsetMillis) {
            return;
        }
        long current = System.currentTimeMillis();
        if ((double)current < this.finalStartMillis) {
            SimpleTimeZone stz = new SimpleTimeZone(offsetMillis, this.getID());
            boolean bDst = this.useDaylightTime();
            if (bDst) {
                TimeZoneTransition tzt;
                TimeZoneRule[] currentRules = this.getSimpleTimeZoneRulesNear(current);
                if (currentRules.length != 3 && (tzt = this.getPreviousTransition(current, false)) != null) {
                    currentRules = this.getSimpleTimeZoneRulesNear(tzt.getTime() - 1L);
                }
                if (currentRules.length == 3 && currentRules[1] instanceof AnnualTimeZoneRule && currentRules[2] instanceof AnnualTimeZoneRule) {
                    int sav;
                    DateTimeRule end;
                    DateTimeRule start;
                    int offset2;
                    AnnualTimeZoneRule r1 = (AnnualTimeZoneRule)currentRules[1];
                    AnnualTimeZoneRule r2 = (AnnualTimeZoneRule)currentRules[2];
                    int offset1 = r1.getRawOffset() + r1.getDSTSavings();
                    if (offset1 > (offset2 = r2.getRawOffset() + r2.getDSTSavings())) {
                        start = r1.getRule();
                        end = r2.getRule();
                        sav = offset1 - offset2;
                    } else {
                        start = r2.getRule();
                        end = r1.getRule();
                        sav = offset2 - offset1;
                    }
                    stz.setStartRule(start.getRuleMonth(), start.getRuleWeekInMonth(), start.getRuleDayOfWeek(), start.getRuleMillisInDay());
                    stz.setEndRule(end.getRuleMonth(), end.getRuleWeekInMonth(), end.getRuleDayOfWeek(), end.getRuleMillisInDay());
                    stz.setDSTSavings(sav);
                } else {
                    stz.setStartRule(0, 1, 0);
                    stz.setEndRule(11, 31, 86399999);
                }
            }
            int[] fields = Grego.timeToFields(current, null);
            this.finalStartYear = fields[0];
            this.finalStartMillis = Grego.fieldsToDay(fields[0], 0, 1);
            if (bDst) {
                stz.setStartYear(this.finalStartYear);
            }
            this.finalZone = stz;
        } else {
            this.finalZone.setRawOffset(offsetMillis);
        }
        this.transitionRulesInitialized = false;
    }

    public Object clone() {
        if (this.isFrozen()) {
            return this;
        }
        return this.cloneAsThawed();
    }

    public void getOffset(long date, boolean local, int[] offsets) {
        if (this.finalZone != null && (double)date >= this.finalStartMillis) {
            this.finalZone.getOffset(date, local, offsets);
        } else {
            this.getHistoricalOffset(date, local, 4, 12, offsets);
        }
    }

    public void getOffsetFromLocal(long date, int nonExistingTimeOpt, int duplicatedTimeOpt, int[] offsets) {
        if (this.finalZone != null && (double)date >= this.finalStartMillis) {
            this.finalZone.getOffsetFromLocal(date, nonExistingTimeOpt, duplicatedTimeOpt, offsets);
        } else {
            this.getHistoricalOffset(date, true, nonExistingTimeOpt, duplicatedTimeOpt, offsets);
        }
    }

    public int getRawOffset() {
        int[] ret = new int[2];
        this.getOffset(System.currentTimeMillis(), false, ret);
        return ret[0];
    }

    public boolean useDaylightTime() {
        long current = System.currentTimeMillis();
        if (this.finalZone != null && (double)current >= this.finalStartMillis) {
            return this.finalZone != null && this.finalZone.useDaylightTime();
        }
        int[] fields = Grego.timeToFields(current, null);
        long start = Grego.fieldsToDay(fields[0], 0, 1) * 86400L;
        long limit = Grego.fieldsToDay(fields[0] + 1, 0, 1) * 86400L;
        for (int i2 = 0; i2 < this.transitionCount && this.transitionTimes64[i2] < limit; ++i2) {
            if ((this.transitionTimes64[i2] < start || this.dstOffsetAt(i2) == 0) && (this.transitionTimes64[i2] <= start || i2 <= 0 || this.dstOffsetAt(i2 - 1) == 0)) continue;
            return true;
        }
        return false;
    }

    public boolean observesDaylightTime() {
        long current = System.currentTimeMillis();
        if (this.finalZone != null) {
            if (this.finalZone.useDaylightTime()) {
                return true;
            }
            if ((double)current >= this.finalStartMillis) {
                return false;
            }
        }
        long currentSec = Grego.floorDivide(current, 1000L);
        int trsIdx = this.transitionCount - 1;
        if (this.dstOffsetAt(trsIdx) != 0) {
            return true;
        }
        while (trsIdx >= 0 && this.transitionTimes64[trsIdx] > currentSec) {
            if (this.dstOffsetAt(trsIdx - 1) == 0) continue;
            return true;
        }
        return false;
    }

    public int getDSTSavings() {
        if (this.finalZone != null) {
            return this.finalZone.getDSTSavings();
        }
        return super.getDSTSavings();
    }

    public boolean inDaylightTime(Date date) {
        int[] temp = new int[2];
        this.getOffset(date.getTime(), false, temp);
        return temp[1] != 0;
    }

    public boolean hasSameRules(TimeZone other) {
        if (this == other) {
            return true;
        }
        if (!super.hasSameRules(other)) {
            return false;
        }
        if (!(other instanceof OlsonTimeZone)) {
            return false;
        }
        OlsonTimeZone o2 = (OlsonTimeZone)other;
        if (this.finalZone == null ? o2.finalZone != null : o2.finalZone == null || this.finalStartYear != o2.finalStartYear || !this.finalZone.hasSameRules(o2.finalZone)) {
            return false;
        }
        return this.transitionCount == o2.transitionCount && Arrays.equals(this.transitionTimes64, o2.transitionTimes64) && this.typeCount == o2.typeCount && Arrays.equals(this.typeMapData, o2.typeMapData) && Arrays.equals(this.typeOffsets, o2.typeOffsets);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public String getCanonicalID() {
        if (this.canonicalID == null) {
            OlsonTimeZone olsonTimeZone = this;
            synchronized (olsonTimeZone) {
                if (this.canonicalID == null) {
                    this.canonicalID = OlsonTimeZone.getCanonicalID(this.getID());
                    assert (this.canonicalID != null);
                    if (this.canonicalID == null) {
                        this.canonicalID = this.getID();
                    }
                }
            }
        }
        return this.canonicalID;
    }

    private void constructEmpty() {
        this.transitionCount = 0;
        this.transitionTimes64 = null;
        this.typeMapData = null;
        this.typeCount = 1;
        this.typeOffsets = new int[]{0, 0};
        this.finalZone = null;
        this.finalStartYear = Integer.MAX_VALUE;
        this.finalStartMillis = Double.MAX_VALUE;
        this.transitionRulesInitialized = false;
    }

    public OlsonTimeZone(UResourceBundle top, UResourceBundle res, String id2) {
        super(id2);
        this.construct(top, res);
    }

    private void construct(UResourceBundle top, UResourceBundle res) {
        block25: {
            UResourceBundle r2;
            if (top == null || res == null) {
                throw new IllegalArgumentException();
            }
            if (DEBUG) {
                System.out.println("OlsonTimeZone(" + res.getKey() + ")");
            }
            int[] transPost32 = null;
            int[] trans32 = null;
            int[] transPre32 = null;
            this.transitionCount = 0;
            try {
                r2 = res.get("transPre32");
                transPre32 = r2.getIntVector();
                if (transPre32.length % 2 != 0) {
                    throw new IllegalArgumentException("Invalid Format");
                }
                this.transitionCount += transPre32.length / 2;
            }
            catch (MissingResourceException e2) {
                // empty catch block
            }
            try {
                r2 = res.get("trans");
                trans32 = r2.getIntVector();
                this.transitionCount += trans32.length;
            }
            catch (MissingResourceException e3) {
                // empty catch block
            }
            try {
                r2 = res.get("transPost32");
                transPost32 = r2.getIntVector();
                if (transPost32.length % 2 != 0) {
                    throw new IllegalArgumentException("Invalid Format");
                }
                this.transitionCount += transPost32.length / 2;
            }
            catch (MissingResourceException e4) {
                // empty catch block
            }
            if (this.transitionCount > 0) {
                int i2;
                this.transitionTimes64 = new long[this.transitionCount];
                int idx = 0;
                if (transPre32 != null) {
                    i2 = 0;
                    while (i2 < transPre32.length / 2) {
                        this.transitionTimes64[idx] = ((long)transPre32[i2 * 2] & 0xFFFFFFFFL) << 32 | (long)transPre32[i2 * 2 + 1] & 0xFFFFFFFFL;
                        ++i2;
                        ++idx;
                    }
                }
                if (trans32 != null) {
                    i2 = 0;
                    while (i2 < trans32.length) {
                        this.transitionTimes64[idx] = trans32[i2];
                        ++i2;
                        ++idx;
                    }
                }
                if (transPost32 != null) {
                    i2 = 0;
                    while (i2 < transPost32.length / 2) {
                        this.transitionTimes64[idx] = ((long)transPost32[i2 * 2] & 0xFFFFFFFFL) << 32 | (long)transPost32[i2 * 2 + 1] & 0xFFFFFFFFL;
                        ++i2;
                        ++idx;
                    }
                }
            } else {
                this.transitionTimes64 = null;
            }
            r2 = res.get("typeOffsets");
            this.typeOffsets = r2.getIntVector();
            if (this.typeOffsets.length < 2 || this.typeOffsets.length > 32766 || this.typeOffsets.length % 2 != 0) {
                throw new IllegalArgumentException("Invalid Format");
            }
            this.typeCount = this.typeOffsets.length / 2;
            if (this.transitionCount > 0) {
                r2 = res.get("typeMap");
                this.typeMapData = r2.getBinary(null);
                if (this.typeMapData.length != this.transitionCount) {
                    throw new IllegalArgumentException("Invalid Format");
                }
            } else {
                this.typeMapData = null;
            }
            this.finalZone = null;
            this.finalStartYear = Integer.MAX_VALUE;
            this.finalStartMillis = Double.MAX_VALUE;
            String ruleID = null;
            try {
                ruleID = res.getString("finalRule");
                r2 = res.get("finalRaw");
                int ruleRaw = r2.getInt() * 1000;
                r2 = OlsonTimeZone.loadRule(top, ruleID);
                int[] ruleData = r2.getIntVector();
                if (ruleData == null || ruleData.length != 11) {
                    throw new IllegalArgumentException("Invalid Format");
                }
                this.finalZone = new SimpleTimeZone(ruleRaw, "", ruleData[0], ruleData[1], ruleData[2], ruleData[3] * 1000, ruleData[4], ruleData[5], ruleData[6], ruleData[7], ruleData[8] * 1000, ruleData[9], ruleData[10] * 1000);
                r2 = res.get("finalYear");
                this.finalStartYear = r2.getInt();
                this.finalStartMillis = Grego.fieldsToDay(this.finalStartYear, 0, 1) * 86400000L;
            }
            catch (MissingResourceException e5) {
                if (ruleID == null) break block25;
                throw new IllegalArgumentException("Invalid Format");
            }
        }
    }

    public OlsonTimeZone(String id2) {
        super(id2);
        UResourceBundle top = UResourceBundle.getBundleInstance("com/ibm/icu/impl/data/icudt51b", ZONEINFORES, ICUResourceBundle.ICU_DATA_CLASS_LOADER);
        UResourceBundle res = ZoneMeta.openOlsonResource(top, id2);
        this.construct(top, res);
        if (this.finalZone != null) {
            this.finalZone.setID(id2);
        }
    }

    public void setID(String id2) {
        if (this.isFrozen()) {
            throw new UnsupportedOperationException("Attempt to modify a frozen OlsonTimeZone instance.");
        }
        if (this.canonicalID == null) {
            this.canonicalID = OlsonTimeZone.getCanonicalID(this.getID());
            assert (this.canonicalID != null);
            if (this.canonicalID == null) {
                this.canonicalID = this.getID();
            }
        }
        if (this.finalZone != null) {
            this.finalZone.setID(id2);
        }
        super.setID(id2);
        this.transitionRulesInitialized = false;
    }

    private void getHistoricalOffset(long date, boolean local, int NonExistingTimeOpt, int DuplicatedTimeOpt, int[] offsets) {
        if (this.transitionCount != 0) {
            long sec = Grego.floorDivide(date, 1000L);
            if (!local && sec < this.transitionTimes64[0]) {
                offsets[0] = this.initialRawOffset() * 1000;
                offsets[1] = this.initialDstOffset() * 1000;
            } else {
                int transIdx;
                for (transIdx = this.transitionCount - 1; transIdx >= 0; --transIdx) {
                    long transition = this.transitionTimes64[transIdx];
                    if (local) {
                        boolean stdToDst;
                        int offsetBefore = this.zoneOffsetAt(transIdx - 1);
                        boolean dstBefore = this.dstOffsetAt(transIdx - 1) != 0;
                        int offsetAfter = this.zoneOffsetAt(transIdx);
                        boolean dstAfter = this.dstOffsetAt(transIdx) != 0;
                        boolean dstToStd = dstBefore && !dstAfter;
                        boolean bl2 = stdToDst = !dstBefore && dstAfter;
                        transition = offsetAfter - offsetBefore >= 0 ? ((NonExistingTimeOpt & 3) == 1 && dstToStd || (NonExistingTimeOpt & 3) == 3 && stdToDst ? (transition += (long)offsetBefore) : ((NonExistingTimeOpt & 3) == 1 && stdToDst || (NonExistingTimeOpt & 3) == 3 && dstToStd ? (transition += (long)offsetAfter) : ((NonExistingTimeOpt & 0xC) == 12 ? (transition += (long)offsetBefore) : (transition += (long)offsetAfter)))) : ((DuplicatedTimeOpt & 3) == 1 && dstToStd || (DuplicatedTimeOpt & 3) == 3 && stdToDst ? (transition += (long)offsetAfter) : ((DuplicatedTimeOpt & 3) == 1 && stdToDst || (DuplicatedTimeOpt & 3) == 3 && dstToStd ? (transition += (long)offsetBefore) : ((DuplicatedTimeOpt & 0xC) == 4 ? (transition += (long)offsetBefore) : (transition += (long)offsetAfter))));
                    }
                    if (sec >= transition) break;
                }
                offsets[0] = this.rawOffsetAt(transIdx) * 1000;
                offsets[1] = this.dstOffsetAt(transIdx) * 1000;
            }
        } else {
            offsets[0] = this.initialRawOffset() * 1000;
            offsets[1] = this.initialDstOffset() * 1000;
        }
    }

    private int getInt(byte val) {
        return val & 0xFF;
    }

    private int zoneOffsetAt(int transIdx) {
        int typeIdx = transIdx >= 0 ? this.getInt(this.typeMapData[transIdx]) * 2 : 0;
        return this.typeOffsets[typeIdx] + this.typeOffsets[typeIdx + 1];
    }

    private int rawOffsetAt(int transIdx) {
        int typeIdx = transIdx >= 0 ? this.getInt(this.typeMapData[transIdx]) * 2 : 0;
        return this.typeOffsets[typeIdx];
    }

    private int dstOffsetAt(int transIdx) {
        int typeIdx = transIdx >= 0 ? this.getInt(this.typeMapData[transIdx]) * 2 : 0;
        return this.typeOffsets[typeIdx + 1];
    }

    private int initialRawOffset() {
        return this.typeOffsets[0];
    }

    private int initialDstOffset() {
        return this.typeOffsets[1];
    }

    public String toString() {
        int i2;
        StringBuilder buf2 = new StringBuilder();
        buf2.append(super.toString());
        buf2.append('[');
        buf2.append("transitionCount=" + this.transitionCount);
        buf2.append(",typeCount=" + this.typeCount);
        buf2.append(",transitionTimes=");
        if (this.transitionTimes64 != null) {
            buf2.append('[');
            for (i2 = 0; i2 < this.transitionTimes64.length; ++i2) {
                if (i2 > 0) {
                    buf2.append(',');
                }
                buf2.append(Long.toString(this.transitionTimes64[i2]));
            }
            buf2.append(']');
        } else {
            buf2.append("null");
        }
        buf2.append(",typeOffsets=");
        if (this.typeOffsets != null) {
            buf2.append('[');
            for (i2 = 0; i2 < this.typeOffsets.length; ++i2) {
                if (i2 > 0) {
                    buf2.append(',');
                }
                buf2.append(Integer.toString(this.typeOffsets[i2]));
            }
            buf2.append(']');
        } else {
            buf2.append("null");
        }
        buf2.append(",typeMapData=");
        if (this.typeMapData != null) {
            buf2.append('[');
            for (i2 = 0; i2 < this.typeMapData.length; ++i2) {
                if (i2 > 0) {
                    buf2.append(',');
                }
                buf2.append(Byte.toString(this.typeMapData[i2]));
            }
        } else {
            buf2.append("null");
        }
        buf2.append(",finalStartYear=" + this.finalStartYear);
        buf2.append(",finalStartMillis=" + this.finalStartMillis);
        buf2.append(",finalZone=" + this.finalZone);
        buf2.append(']');
        return buf2.toString();
    }

    private static UResourceBundle loadRule(UResourceBundle top, String ruleid) {
        UResourceBundle r2 = top.get("Rules");
        r2 = r2.get(ruleid);
        return r2;
    }

    public boolean equals(Object obj) {
        if (!super.equals(obj)) {
            return false;
        }
        OlsonTimeZone z2 = (OlsonTimeZone)obj;
        return Utility.arrayEquals(this.typeMapData, (Object)z2.typeMapData) || this.finalStartYear == z2.finalStartYear && (this.finalZone == null && z2.finalZone == null || this.finalZone != null && z2.finalZone != null && this.finalZone.equals(z2.finalZone) && this.transitionCount == z2.transitionCount && this.typeCount == z2.typeCount && Utility.arrayEquals((Object)this.transitionTimes64, (Object)z2.transitionTimes64) && Utility.arrayEquals(this.typeOffsets, (Object)z2.typeOffsets) && Utility.arrayEquals(this.typeMapData, (Object)z2.typeMapData));
    }

    public int hashCode() {
        int i2;
        int ret = (int)((long)(this.finalStartYear ^ (this.finalStartYear >>> 4) + this.transitionCount ^ (this.transitionCount >>> 6) + this.typeCount) ^ (long)(this.typeCount >>> 8) + Double.doubleToLongBits(this.finalStartMillis) + (long)(this.finalZone == null ? 0 : this.finalZone.hashCode()) + (long)super.hashCode());
        if (this.transitionTimes64 != null) {
            for (i2 = 0; i2 < this.transitionTimes64.length; ++i2) {
                ret = (int)((long)ret + (this.transitionTimes64[i2] ^ this.transitionTimes64[i2] >>> 8));
            }
        }
        for (i2 = 0; i2 < this.typeOffsets.length; ++i2) {
            ret += this.typeOffsets[i2] ^ this.typeOffsets[i2] >>> 8;
        }
        if (this.typeMapData != null) {
            for (i2 = 0; i2 < this.typeMapData.length; ++i2) {
                ret += this.typeMapData[i2] & 0xFF;
            }
        }
        return ret;
    }

    public TimeZoneTransition getNextTransition(long base, boolean inclusive) {
        this.initTransitionRules();
        if (this.finalZone != null) {
            if (inclusive && base == this.firstFinalTZTransition.getTime()) {
                return this.firstFinalTZTransition;
            }
            if (base >= this.firstFinalTZTransition.getTime()) {
                if (this.finalZone.useDaylightTime()) {
                    return this.finalZoneWithStartYear.getNextTransition(base, inclusive);
                }
                return null;
            }
        }
        if (this.historicRules != null) {
            long t2;
            int ttidx;
            for (ttidx = this.transitionCount - 1; ttidx >= this.firstTZTransitionIdx && base <= (t2 = this.transitionTimes64[ttidx] * 1000L) && (inclusive || base != t2); --ttidx) {
            }
            if (ttidx == this.transitionCount - 1) {
                return this.firstFinalTZTransition;
            }
            if (ttidx < this.firstTZTransitionIdx) {
                return this.firstTZTransition;
            }
            TimeArrayTimeZoneRule to2 = this.historicRules[this.getInt(this.typeMapData[ttidx + 1])];
            TimeArrayTimeZoneRule from = this.historicRules[this.getInt(this.typeMapData[ttidx])];
            long startTime = this.transitionTimes64[ttidx + 1] * 1000L;
            if (from.getName().equals(to2.getName()) && from.getRawOffset() == to2.getRawOffset() && from.getDSTSavings() == to2.getDSTSavings()) {
                return this.getNextTransition(startTime, false);
            }
            return new TimeZoneTransition(startTime, from, to2);
        }
        return null;
    }

    public TimeZoneTransition getPreviousTransition(long base, boolean inclusive) {
        this.initTransitionRules();
        if (this.finalZone != null) {
            if (inclusive && base == this.firstFinalTZTransition.getTime()) {
                return this.firstFinalTZTransition;
            }
            if (base > this.firstFinalTZTransition.getTime()) {
                if (this.finalZone.useDaylightTime()) {
                    return this.finalZoneWithStartYear.getPreviousTransition(base, inclusive);
                }
                return this.firstFinalTZTransition;
            }
        }
        if (this.historicRules != null) {
            long t2;
            int ttidx;
            for (ttidx = this.transitionCount - 1; !(ttidx < this.firstTZTransitionIdx || base > (t2 = this.transitionTimes64[ttidx] * 1000L) || inclusive && base == t2); --ttidx) {
            }
            if (ttidx < this.firstTZTransitionIdx) {
                return null;
            }
            if (ttidx == this.firstTZTransitionIdx) {
                return this.firstTZTransition;
            }
            TimeArrayTimeZoneRule to2 = this.historicRules[this.getInt(this.typeMapData[ttidx])];
            TimeArrayTimeZoneRule from = this.historicRules[this.getInt(this.typeMapData[ttidx - 1])];
            long startTime = this.transitionTimes64[ttidx] * 1000L;
            if (from.getName().equals(to2.getName()) && from.getRawOffset() == to2.getRawOffset() && from.getDSTSavings() == to2.getDSTSavings()) {
                return this.getPreviousTransition(startTime, false);
            }
            return new TimeZoneTransition(startTime, from, to2);
        }
        return null;
    }

    public TimeZoneRule[] getTimeZoneRules() {
        this.initTransitionRules();
        int size = 1;
        if (this.historicRules != null) {
            for (int i2 = 0; i2 < this.historicRules.length; ++i2) {
                if (this.historicRules[i2] == null) continue;
                ++size;
            }
        }
        if (this.finalZone != null) {
            size = this.finalZone.useDaylightTime() ? (size += 2) : ++size;
        }
        TimeZoneRule[] rules = new TimeZoneRule[size];
        int idx = 0;
        rules[idx++] = this.initialRule;
        if (this.historicRules != null) {
            for (int i3 = 0; i3 < this.historicRules.length; ++i3) {
                if (this.historicRules[i3] == null) continue;
                rules[idx++] = this.historicRules[i3];
            }
        }
        if (this.finalZone != null) {
            if (this.finalZone.useDaylightTime()) {
                TimeZoneRule[] stzr = this.finalZoneWithStartYear.getTimeZoneRules();
                rules[idx++] = stzr[1];
                rules[idx++] = stzr[2];
            } else {
                rules[idx++] = new TimeArrayTimeZoneRule(this.getID() + "(STD)", this.finalZone.getRawOffset(), 0, new long[]{(long)this.finalStartMillis}, 2);
            }
        }
        return rules;
    }

    private synchronized void initTransitionRules() {
        if (this.transitionRulesInitialized) {
            return;
        }
        this.initialRule = null;
        this.firstTZTransition = null;
        this.firstFinalTZTransition = null;
        this.historicRules = null;
        this.firstTZTransitionIdx = 0;
        this.finalZoneWithStartYear = null;
        String stdName = this.getID() + "(STD)";
        String dstName = this.getID() + "(DST)";
        int raw = this.initialRawOffset() * 1000;
        int dst = this.initialDstOffset() * 1000;
        this.initialRule = new InitialTimeZoneRule(dst == 0 ? stdName : dstName, raw, dst);
        if (this.transitionCount > 0) {
            int transitionIdx;
            for (transitionIdx = 0; transitionIdx < this.transitionCount && this.getInt(this.typeMapData[transitionIdx]) == 0; ++transitionIdx) {
                ++this.firstTZTransitionIdx;
            }
            if (transitionIdx != this.transitionCount) {
                int typeIdx;
                long[] times = new long[this.transitionCount];
                for (typeIdx = 0; typeIdx < this.typeCount; ++typeIdx) {
                    int nTimes = 0;
                    for (transitionIdx = this.firstTZTransitionIdx; transitionIdx < this.transitionCount; ++transitionIdx) {
                        long tt2;
                        if (typeIdx != this.getInt(this.typeMapData[transitionIdx]) || !((double)(tt2 = this.transitionTimes64[transitionIdx] * 1000L) < this.finalStartMillis)) continue;
                        times[nTimes++] = tt2;
                    }
                    if (nTimes <= 0) continue;
                    long[] startTimes = new long[nTimes];
                    System.arraycopy(times, 0, startTimes, 0, nTimes);
                    raw = this.typeOffsets[typeIdx * 2] * 1000;
                    dst = this.typeOffsets[typeIdx * 2 + 1] * 1000;
                    if (this.historicRules == null) {
                        this.historicRules = new TimeArrayTimeZoneRule[this.typeCount];
                    }
                    this.historicRules[typeIdx] = new TimeArrayTimeZoneRule(dst == 0 ? stdName : dstName, raw, dst, startTimes, 2);
                }
                typeIdx = this.getInt(this.typeMapData[this.firstTZTransitionIdx]);
                this.firstTZTransition = new TimeZoneTransition(this.transitionTimes64[this.firstTZTransitionIdx] * 1000L, this.initialRule, this.historicRules[typeIdx]);
            }
        }
        if (this.finalZone != null) {
            TimeZoneRule firstFinalRule;
            long startTime = (long)this.finalStartMillis;
            if (this.finalZone.useDaylightTime()) {
                this.finalZoneWithStartYear = (SimpleTimeZone)this.finalZone.clone();
                this.finalZoneWithStartYear.setStartYear(this.finalStartYear);
                TimeZoneTransition tzt = this.finalZoneWithStartYear.getNextTransition(startTime, false);
                firstFinalRule = tzt.getTo();
                startTime = tzt.getTime();
            } else {
                this.finalZoneWithStartYear = this.finalZone;
                firstFinalRule = new TimeArrayTimeZoneRule(this.finalZone.getID(), this.finalZone.getRawOffset(), 0, new long[]{startTime}, 2);
            }
            TimeZoneRule prevRule = null;
            if (this.transitionCount > 0) {
                prevRule = this.historicRules[this.getInt(this.typeMapData[this.transitionCount - 1])];
            }
            if (prevRule == null) {
                prevRule = this.initialRule;
            }
            this.firstFinalTZTransition = new TimeZoneTransition(startTime, prevRule, firstFinalRule);
        }
        this.transitionRulesInitialized = true;
    }

    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        if (this.serialVersionOnStream < 1) {
            boolean initialized = false;
            String tzid = this.getID();
            if (tzid != null) {
                try {
                    UResourceBundle top = UResourceBundle.getBundleInstance("com/ibm/icu/impl/data/icudt51b", ZONEINFORES, ICUResourceBundle.ICU_DATA_CLASS_LOADER);
                    UResourceBundle res = ZoneMeta.openOlsonResource(top, tzid);
                    this.construct(top, res);
                    if (this.finalZone != null) {
                        this.finalZone.setID(tzid);
                    }
                    initialized = true;
                }
                catch (Exception e2) {
                    // empty catch block
                }
            }
            if (!initialized) {
                this.constructEmpty();
            }
        }
        this.transitionRulesInitialized = false;
    }

    public boolean isFrozen() {
        return this.isFrozen;
    }

    public TimeZone freeze() {
        this.isFrozen = true;
        return this;
    }

    public TimeZone cloneAsThawed() {
        OlsonTimeZone tz2 = (OlsonTimeZone)super.cloneAsThawed();
        if (this.finalZone != null) {
            this.finalZone.setID(this.getID());
            tz2.finalZone = (SimpleTimeZone)this.finalZone.clone();
        }
        tz2.isFrozen = false;
        return tz2;
    }
}

