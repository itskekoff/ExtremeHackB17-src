package org.apache.commons.lang3;

import org.apache.commons.lang3.CharSet;
import org.apache.commons.lang3.StringUtils;

public class CharSetUtils {
    public static String squeeze(String str, String ... set) {
        if (StringUtils.isEmpty(str) || CharSetUtils.deepEmpty(set)) {
            return str;
        }
        CharSet chars = CharSet.getInstance(set);
        StringBuilder buffer = new StringBuilder(str.length());
        char[] chrs = str.toCharArray();
        int sz2 = chrs.length;
        char lastChar = chrs[0];
        char ch2 = ' ';
        Character inChars = null;
        Character notInChars = null;
        buffer.append(lastChar);
        for (int i2 = 1; i2 < sz2; ++i2) {
            ch2 = chrs[i2];
            if (ch2 == lastChar) {
                if (inChars != null && ch2 == inChars.charValue()) continue;
                if (notInChars == null || ch2 != notInChars.charValue()) {
                    if (chars.contains(ch2)) {
                        inChars = Character.valueOf(ch2);
                        continue;
                    }
                    notInChars = Character.valueOf(ch2);
                }
            }
            buffer.append(ch2);
            lastChar = ch2;
        }
        return buffer.toString();
    }

    public static boolean containsAny(String str, String ... set) {
        if (StringUtils.isEmpty(str) || CharSetUtils.deepEmpty(set)) {
            return false;
        }
        CharSet chars = CharSet.getInstance(set);
        for (char c2 : str.toCharArray()) {
            if (!chars.contains(c2)) continue;
            return true;
        }
        return false;
    }

    public static int count(String str, String ... set) {
        if (StringUtils.isEmpty(str) || CharSetUtils.deepEmpty(set)) {
            return 0;
        }
        CharSet chars = CharSet.getInstance(set);
        int count = 0;
        for (char c2 : str.toCharArray()) {
            if (!chars.contains(c2)) continue;
            ++count;
        }
        return count;
    }

    public static String keep(String str, String ... set) {
        if (str == null) {
            return null;
        }
        if (str.isEmpty() || CharSetUtils.deepEmpty(set)) {
            return "";
        }
        return CharSetUtils.modify(str, set, true);
    }

    public static String delete(String str, String ... set) {
        if (StringUtils.isEmpty(str) || CharSetUtils.deepEmpty(set)) {
            return str;
        }
        return CharSetUtils.modify(str, set, false);
    }

    private static String modify(String str, String[] set, boolean expect) {
        CharSet chars = CharSet.getInstance(set);
        StringBuilder buffer = new StringBuilder(str.length());
        char[] chrs = str.toCharArray();
        int sz2 = chrs.length;
        for (int i2 = 0; i2 < sz2; ++i2) {
            if (chars.contains(chrs[i2]) != expect) continue;
            buffer.append(chrs[i2]);
        }
        return buffer.toString();
    }

    private static boolean deepEmpty(String[] strings) {
        if (strings != null) {
            for (String s2 : strings) {
                if (!StringUtils.isNotEmpty(s2)) continue;
                return false;
            }
        }
        return true;
    }
}

