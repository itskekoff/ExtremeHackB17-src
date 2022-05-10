package org.apache.commons.lang3;

import org.apache.commons.lang3.StringUtils;

public class CharUtils {
    private static final String[] CHAR_STRING_ARRAY = new String[128];
    private static final char[] HEX_DIGITS = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
    public static final char LF = '\n';
    public static final char CR = '\r';

    @Deprecated
    public static Character toCharacterObject(char ch2) {
        return Character.valueOf(ch2);
    }

    public static Character toCharacterObject(String str) {
        if (StringUtils.isEmpty(str)) {
            return null;
        }
        return Character.valueOf(str.charAt(0));
    }

    public static char toChar(Character ch2) {
        if (ch2 == null) {
            throw new IllegalArgumentException("The Character must not be null");
        }
        return ch2.charValue();
    }

    public static char toChar(Character ch2, char defaultValue) {
        if (ch2 == null) {
            return defaultValue;
        }
        return ch2.charValue();
    }

    public static char toChar(String str) {
        if (StringUtils.isEmpty(str)) {
            throw new IllegalArgumentException("The String must not be empty");
        }
        return str.charAt(0);
    }

    public static char toChar(String str, char defaultValue) {
        if (StringUtils.isEmpty(str)) {
            return defaultValue;
        }
        return str.charAt(0);
    }

    public static int toIntValue(char ch2) {
        if (!CharUtils.isAsciiNumeric(ch2)) {
            throw new IllegalArgumentException("The character " + ch2 + " is not in the range '0' - '9'");
        }
        return ch2 - 48;
    }

    public static int toIntValue(char ch2, int defaultValue) {
        if (!CharUtils.isAsciiNumeric(ch2)) {
            return defaultValue;
        }
        return ch2 - 48;
    }

    public static int toIntValue(Character ch2) {
        if (ch2 == null) {
            throw new IllegalArgumentException("The character must not be null");
        }
        return CharUtils.toIntValue(ch2.charValue());
    }

    public static int toIntValue(Character ch2, int defaultValue) {
        if (ch2 == null) {
            return defaultValue;
        }
        return CharUtils.toIntValue(ch2.charValue(), defaultValue);
    }

    public static String toString(char ch2) {
        if (ch2 < '\u0080') {
            return CHAR_STRING_ARRAY[ch2];
        }
        return new String(new char[]{ch2});
    }

    public static String toString(Character ch2) {
        if (ch2 == null) {
            return null;
        }
        return CharUtils.toString(ch2.charValue());
    }

    public static String unicodeEscaped(char ch2) {
        StringBuilder sb2 = new StringBuilder(6);
        sb2.append("\\u");
        sb2.append(HEX_DIGITS[ch2 >> 12 & 0xF]);
        sb2.append(HEX_DIGITS[ch2 >> 8 & 0xF]);
        sb2.append(HEX_DIGITS[ch2 >> 4 & 0xF]);
        sb2.append(HEX_DIGITS[ch2 & 0xF]);
        return sb2.toString();
    }

    public static String unicodeEscaped(Character ch2) {
        if (ch2 == null) {
            return null;
        }
        return CharUtils.unicodeEscaped(ch2.charValue());
    }

    public static boolean isAscii(char ch2) {
        return ch2 < '\u0080';
    }

    public static boolean isAsciiPrintable(char ch2) {
        return ch2 >= ' ' && ch2 < '\u007f';
    }

    public static boolean isAsciiControl(char ch2) {
        return ch2 < ' ' || ch2 == '\u007f';
    }

    public static boolean isAsciiAlpha(char ch2) {
        return CharUtils.isAsciiAlphaUpper(ch2) || CharUtils.isAsciiAlphaLower(ch2);
    }

    public static boolean isAsciiAlphaUpper(char ch2) {
        return ch2 >= 'A' && ch2 <= 'Z';
    }

    public static boolean isAsciiAlphaLower(char ch2) {
        return ch2 >= 'a' && ch2 <= 'z';
    }

    public static boolean isAsciiNumeric(char ch2) {
        return ch2 >= '0' && ch2 <= '9';
    }

    public static boolean isAsciiAlphanumeric(char ch2) {
        return CharUtils.isAsciiAlpha(ch2) || CharUtils.isAsciiNumeric(ch2);
    }

    public static int compare(char x2, char y2) {
        return x2 - y2;
    }

    static {
        for (char c2 = '\u0000'; c2 < CHAR_STRING_ARRAY.length; c2 = (char)(c2 + '\u0001')) {
            CharUtils.CHAR_STRING_ARRAY[c2] = String.valueOf(c2);
        }
    }
}

