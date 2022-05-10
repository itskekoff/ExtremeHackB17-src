package org.apache.commons.codec.binary;

public class CharSequenceUtils {
    static boolean regionMatches(CharSequence cs2, boolean ignoreCase, int thisStart, CharSequence substring, int start, int length) {
        if (cs2 instanceof String && substring instanceof String) {
            return ((String)cs2).regionMatches(ignoreCase, thisStart, (String)substring, start, length);
        }
        int index1 = thisStart;
        int index2 = start;
        int tmpLen = length;
        while (tmpLen-- > 0) {
            char c2;
            char c1;
            if ((c1 = cs2.charAt(index1++)) == (c2 = substring.charAt(index2++))) continue;
            if (!ignoreCase) {
                return false;
            }
            if (Character.toUpperCase(c1) == Character.toUpperCase(c2) || Character.toLowerCase(c1) == Character.toLowerCase(c2)) continue;
            return false;
        }
        return true;
    }
}

