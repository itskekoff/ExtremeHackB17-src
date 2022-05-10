package com.ibm.icu.impl.duration.impl;

import java.util.Locale;

public class Utils {
    public static final Locale localeFromString(String s2) {
        String language = s2;
        String region = "";
        String variant = "";
        int x2 = language.indexOf("_");
        if (x2 != -1) {
            region = language.substring(x2 + 1);
            language = language.substring(0, x2);
        }
        if ((x2 = region.indexOf("_")) != -1) {
            variant = region.substring(x2 + 1);
            region = region.substring(0, x2);
        }
        return new Locale(language, region, variant);
    }

    public static String chineseNumber(long n2, ChineseDigits zh2) {
        if (n2 < 0L) {
            n2 = -n2;
        }
        if (n2 <= 10L) {
            if (n2 == 2L) {
                return String.valueOf(zh2.liang);
            }
            return String.valueOf(zh2.digits[(int)n2]);
        }
        char[] buf2 = new char[40];
        char[] digits = String.valueOf(n2).toCharArray();
        boolean inZero = true;
        boolean forcedZero = false;
        int x2 = buf2.length;
        int i2 = digits.length;
        int u2 = -1;
        int l2 = -1;
        while (--i2 >= 0) {
            if (u2 == -1) {
                if (l2 != -1) {
                    buf2[--x2] = zh2.levels[l2];
                    inZero = true;
                    forcedZero = false;
                }
                ++u2;
            } else {
                buf2[--x2] = zh2.units[u2++];
                if (u2 == 3) {
                    u2 = -1;
                    ++l2;
                }
            }
            int d2 = digits[i2] - 48;
            if (d2 == 0) {
                if (x2 < buf2.length - 1 && u2 != 0) {
                    buf2[x2] = 42;
                }
                if (inZero || forcedZero) {
                    buf2[--x2] = 42;
                    continue;
                }
                buf2[--x2] = zh2.digits[0];
                inZero = true;
                forcedZero = u2 == 1;
                continue;
            }
            inZero = false;
            buf2[--x2] = zh2.digits[d2];
        }
        if (n2 > 1000000L) {
            boolean last = true;
            int i3 = buf2.length - 3;
            while (buf2[i3] != '0') {
                boolean bl2 = last = !last;
                if ((i3 -= 8) > x2) continue;
            }
            i3 = buf2.length - 7;
            do {
                if (buf2[i3] == zh2.digits[0] && !last) {
                    buf2[i3] = 42;
                }
                boolean bl3 = last = !last;
            } while ((i3 -= 8) > x2);
            if (n2 >= 100000000L) {
                i3 = buf2.length - 8;
                do {
                    boolean empty = true;
                    int e2 = Math.max(x2 - 1, i3 - 8);
                    for (int j2 = i3 - 1; j2 > e2; --j2) {
                        if (buf2[j2] == '*') continue;
                        empty = false;
                        break;
                    }
                    if (!empty) continue;
                    buf2[i3] = buf2[i3 + 1] != '*' && buf2[i3 + 1] != zh2.digits[0] ? zh2.digits[0] : 42;
                } while ((i3 -= 8) > x2);
            }
        }
        for (i2 = x2; i2 < buf2.length; ++i2) {
            if (buf2[i2] != zh2.digits[2] || i2 < buf2.length - 1 && buf2[i2 + 1] == zh2.units[0] || i2 > x2 && (buf2[i2 - 1] == zh2.units[0] || buf2[i2 - 1] == zh2.digits[0] || buf2[i2 - 1] == '*')) continue;
            buf2[i2] = zh2.liang;
        }
        if (buf2[x2] == zh2.digits[1] && (zh2.ko || buf2[x2 + 1] == zh2.units[0])) {
            ++x2;
        }
        int w2 = x2;
        for (int r2 = x2; r2 < buf2.length; ++r2) {
            if (buf2[r2] == '*') continue;
            buf2[w2++] = buf2[r2];
        }
        return new String(buf2, x2, w2 - x2);
    }

    public static class ChineseDigits {
        final char[] digits;
        final char[] units;
        final char[] levels;
        final char liang;
        final boolean ko;
        public static final ChineseDigits DEBUG = new ChineseDigits("0123456789s", "sbq", "WYZ", 'L', false);
        public static final ChineseDigits TRADITIONAL = new ChineseDigits("\u96f6\u4e00\u4e8c\u4e09\u56db\u4e94\u516d\u4e03\u516b\u4e5d\u5341", "\u5341\u767e\u5343", "\u842c\u5104\u5146", '\u5169', false);
        public static final ChineseDigits SIMPLIFIED = new ChineseDigits("\u96f6\u4e00\u4e8c\u4e09\u56db\u4e94\u516d\u4e03\u516b\u4e5d\u5341", "\u5341\u767e\u5343", "\u4e07\u4ebf\u5146", '\u4e24', false);
        public static final ChineseDigits KOREAN = new ChineseDigits("\uc601\uc77c\uc774\uc0bc\uc0ac\uc624\uc721\uce60\ud314\uad6c\uc2ed", "\uc2ed\ubc31\ucc9c", "\ub9cc\uc5b5?", '\uc774', true);

        ChineseDigits(String digits, String units, String levels, char liang, boolean ko2) {
            this.digits = digits.toCharArray();
            this.units = units.toCharArray();
            this.levels = levels.toCharArray();
            this.liang = liang;
            this.ko = ko2;
        }
    }
}

