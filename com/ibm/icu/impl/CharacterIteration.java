package com.ibm.icu.impl;

import com.ibm.icu.text.UTF16;
import java.text.CharacterIterator;

public final class CharacterIteration {
    public static final int DONE32 = Integer.MAX_VALUE;

    private CharacterIteration() {
    }

    public static int next32(CharacterIterator ci2) {
        int c2 = ci2.current();
        if (c2 >= 55296 && c2 <= 56319 && ((c2 = ci2.next()) < 56320 || c2 > 57343)) {
            c2 = ci2.previous();
        }
        if ((c2 = ci2.next()) >= 55296) {
            c2 = CharacterIteration.nextTrail32(ci2, c2);
        }
        if (c2 >= 65536 && c2 != Integer.MAX_VALUE) {
            ci2.previous();
        }
        return c2;
    }

    public static int nextTrail32(CharacterIterator ci2, int lead) {
        if (lead == 65535 && ci2.getIndex() >= ci2.getEndIndex()) {
            return Integer.MAX_VALUE;
        }
        int retVal = lead;
        if (lead <= 56319) {
            char cTrail = ci2.next();
            if (UTF16.isTrailSurrogate(cTrail)) {
                retVal = (lead - 55296 << 10) + (cTrail - 56320) + 65536;
            } else {
                ci2.previous();
            }
        }
        return retVal;
    }

    public static int previous32(CharacterIterator ci2) {
        int trail;
        if (ci2.getIndex() <= ci2.getBeginIndex()) {
            return Integer.MAX_VALUE;
        }
        int retVal = trail = ci2.previous();
        if (UTF16.isTrailSurrogate((char)trail) && ci2.getIndex() > ci2.getBeginIndex()) {
            char lead = ci2.previous();
            if (UTF16.isLeadSurrogate(lead)) {
                retVal = (lead - 55296 << 10) + (trail - 56320) + 65536;
            } else {
                ci2.next();
            }
        }
        return retVal;
    }

    public static int current32(CharacterIterator ci2) {
        int lead = ci2.current();
        int retVal = lead;
        if (retVal < 55296) {
            return retVal;
        }
        if (UTF16.isLeadSurrogate((char)lead)) {
            char trail = ci2.next();
            ci2.previous();
            if (UTF16.isTrailSurrogate(trail)) {
                retVal = (lead - 55296 << 10) + (trail - 56320) + 65536;
            }
        } else if (lead == 65535 && ci2.getIndex() >= ci2.getEndIndex()) {
            retVal = Integer.MAX_VALUE;
        }
        return retVal;
    }
}

