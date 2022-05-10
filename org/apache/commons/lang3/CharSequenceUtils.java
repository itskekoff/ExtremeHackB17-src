package org.apache.commons.lang3;

public class CharSequenceUtils {
    private static final int NOT_FOUND = -1;

    public static CharSequence subSequence(CharSequence cs2, int start) {
        return cs2 == null ? null : cs2.subSequence(start, cs2.length());
    }

    static int indexOf(CharSequence cs2, int searchChar, int start) {
        if (cs2 instanceof String) {
            return ((String)cs2).indexOf(searchChar, start);
        }
        int sz2 = cs2.length();
        if (start < 0) {
            start = 0;
        }
        for (int i2 = start; i2 < sz2; ++i2) {
            if (cs2.charAt(i2) != searchChar) continue;
            return i2;
        }
        return -1;
    }

    static int indexOf(CharSequence cs2, CharSequence searchChar, int start) {
        return cs2.toString().indexOf(searchChar.toString(), start);
    }

    static int lastIndexOf(CharSequence cs2, int searchChar, int start) {
        if (cs2 instanceof String) {
            return ((String)cs2).lastIndexOf(searchChar, start);
        }
        int sz2 = cs2.length();
        if (start < 0) {
            return -1;
        }
        if (start >= sz2) {
            start = sz2 - 1;
        }
        for (int i2 = start; i2 >= 0; --i2) {
            if (cs2.charAt(i2) != searchChar) continue;
            return i2;
        }
        return -1;
    }

    static int lastIndexOf(CharSequence cs2, CharSequence searchChar, int start) {
        return cs2.toString().lastIndexOf(searchChar.toString(), start);
    }

    static char[] toCharArray(CharSequence cs2) {
        if (cs2 instanceof String) {
            return ((String)cs2).toCharArray();
        }
        int sz2 = cs2.length();
        char[] array = new char[cs2.length()];
        for (int i2 = 0; i2 < sz2; ++i2) {
            array[i2] = cs2.charAt(i2);
        }
        return array;
    }

    static boolean regionMatches(CharSequence cs2, boolean ignoreCase, int thisStart, CharSequence substring, int start, int length) {
        if (cs2 instanceof String && substring instanceof String) {
            return ((String)cs2).regionMatches(ignoreCase, thisStart, (String)substring, start, length);
        }
        int index1 = thisStart;
        int index2 = start;
        int tmpLen = length;
        int srcLen = cs2.length() - thisStart;
        int otherLen = substring.length() - start;
        if (thisStart < 0 || start < 0 || length < 0) {
            return false;
        }
        if (srcLen < length || otherLen < length) {
            return false;
        }
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

