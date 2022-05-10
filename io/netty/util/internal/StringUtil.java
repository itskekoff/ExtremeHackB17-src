package io.netty.util.internal;

import io.netty.util.internal.InternalThreadLocalMap;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PlatformDependent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public final class StringUtil {
    public static final String EMPTY_STRING = "";
    public static final String NEWLINE;
    public static final char DOUBLE_QUOTE = '\"';
    public static final char COMMA = ',';
    public static final char LINE_FEED = '\n';
    public static final char CARRIAGE_RETURN = '\r';
    public static final char TAB = '\t';
    private static final String[] BYTE2HEX_PAD;
    private static final String[] BYTE2HEX_NOPAD;
    private static final int CSV_NUMBER_ESCAPE_CHARACTERS = 7;
    private static final char PACKAGE_SEPARATOR_CHAR = '.';

    private StringUtil() {
    }

    public static String substringAfter(String value, char delim) {
        int pos = value.indexOf(delim);
        if (pos >= 0) {
            return value.substring(pos + 1);
        }
        return null;
    }

    public static boolean commonSuffixOfLength(String s2, String p2, int len) {
        return s2 != null && p2 != null && len >= 0 && s2.regionMatches(s2.length() - len, p2, p2.length() - len, len);
    }

    public static String byteToHexStringPadded(int value) {
        return BYTE2HEX_PAD[value & 0xFF];
    }

    public static <T extends Appendable> T byteToHexStringPadded(T buf2, int value) {
        try {
            buf2.append(StringUtil.byteToHexStringPadded(value));
        }
        catch (IOException e2) {
            PlatformDependent.throwException(e2);
        }
        return buf2;
    }

    public static String toHexStringPadded(byte[] src) {
        return StringUtil.toHexStringPadded(src, 0, src.length);
    }

    public static String toHexStringPadded(byte[] src, int offset, int length) {
        return StringUtil.toHexStringPadded(new StringBuilder(length << 1), src, offset, length).toString();
    }

    public static <T extends Appendable> T toHexStringPadded(T dst, byte[] src) {
        return StringUtil.toHexStringPadded(dst, src, 0, src.length);
    }

    public static <T extends Appendable> T toHexStringPadded(T dst, byte[] src, int offset, int length) {
        int end = offset + length;
        for (int i2 = offset; i2 < end; ++i2) {
            StringUtil.byteToHexStringPadded(dst, src[i2]);
        }
        return dst;
    }

    public static String byteToHexString(int value) {
        return BYTE2HEX_NOPAD[value & 0xFF];
    }

    public static <T extends Appendable> T byteToHexString(T buf2, int value) {
        try {
            buf2.append(StringUtil.byteToHexString(value));
        }
        catch (IOException e2) {
            PlatformDependent.throwException(e2);
        }
        return buf2;
    }

    public static String toHexString(byte[] src) {
        return StringUtil.toHexString(src, 0, src.length);
    }

    public static String toHexString(byte[] src, int offset, int length) {
        return StringUtil.toHexString(new StringBuilder(length << 1), src, offset, length).toString();
    }

    public static <T extends Appendable> T toHexString(T dst, byte[] src) {
        return StringUtil.toHexString(dst, src, 0, src.length);
    }

    public static <T extends Appendable> T toHexString(T dst, byte[] src, int offset, int length) {
        int i2;
        assert (length >= 0);
        if (length == 0) {
            return dst;
        }
        int end = offset + length;
        int endMinusOne = end - 1;
        for (i2 = offset; i2 < endMinusOne && src[i2] == 0; ++i2) {
        }
        StringUtil.byteToHexString(dst, src[i2++]);
        int remaining = end - i2;
        StringUtil.toHexStringPadded(dst, src, i2, remaining);
        return dst;
    }

    public static String simpleClassName(Object o2) {
        if (o2 == null) {
            return "null_object";
        }
        return StringUtil.simpleClassName(o2.getClass());
    }

    public static String simpleClassName(Class<?> clazz) {
        String className = ObjectUtil.checkNotNull(clazz, "clazz").getName();
        int lastDotIdx = className.lastIndexOf(46);
        if (lastDotIdx > -1) {
            return className.substring(lastDotIdx + 1);
        }
        return className;
    }

    /*
     * Enabled aggressive block sorting
     */
    public static CharSequence escapeCsv(CharSequence value) {
        CharSequence charSequence;
        int length = ObjectUtil.checkNotNull(value, "value").length();
        if (length == 0) {
            return value;
        }
        int last = length - 1;
        boolean quoted = StringUtil.isDoubleQuote(value.charAt(0)) && StringUtil.isDoubleQuote(value.charAt(last)) && length != 1;
        boolean foundSpecialCharacter = false;
        boolean escapedDoubleQuote = false;
        StringBuilder escaped = new StringBuilder(length + 7).append('\"');
        block4: for (int i2 = 0; i2 < length; ++i2) {
            char current = value.charAt(i2);
            switch (current) {
                case '\"': {
                    if (i2 == 0 || i2 == last) {
                        if (quoted) continue block4;
                        escaped.append('\"');
                    } else {
                        boolean isNextCharDoubleQuote = StringUtil.isDoubleQuote(value.charAt(i2 + 1));
                        if (StringUtil.isDoubleQuote(value.charAt(i2 - 1)) || isNextCharDoubleQuote && i2 + 1 != last) break;
                        escaped.append('\"');
                        escapedDoubleQuote = true;
                        break;
                    }
                }
                case '\n': 
                case '\r': 
                case ',': {
                    foundSpecialCharacter = true;
                }
            }
            escaped.append(current);
        }
        if (escapedDoubleQuote || foundSpecialCharacter && !quoted) {
            charSequence = escaped.append('\"');
            return charSequence;
        }
        charSequence = value;
        return charSequence;
    }

    public static CharSequence unescapeCsv(CharSequence value) {
        boolean quoted;
        int length = ObjectUtil.checkNotNull(value, "value").length();
        if (length == 0) {
            return value;
        }
        int last = length - 1;
        boolean bl2 = quoted = StringUtil.isDoubleQuote(value.charAt(0)) && StringUtil.isDoubleQuote(value.charAt(last)) && length != 1;
        if (!quoted) {
            StringUtil.validateCsvFormat(value);
            return value;
        }
        StringBuilder unescaped = InternalThreadLocalMap.get().stringBuilder();
        for (int i2 = 1; i2 < last; ++i2) {
            char current = value.charAt(i2);
            if (current == '\"') {
                if (StringUtil.isDoubleQuote(value.charAt(i2 + 1)) && i2 + 1 != last) {
                    ++i2;
                } else {
                    throw StringUtil.newInvalidEscapedCsvFieldException(value, i2);
                }
            }
            unescaped.append(current);
        }
        return unescaped.toString();
    }

    public static List<CharSequence> unescapeCsvFields(CharSequence value) {
        ArrayList<CharSequence> unescaped = new ArrayList<CharSequence>(2);
        StringBuilder current = InternalThreadLocalMap.get().stringBuilder();
        boolean quoted = false;
        int last = value.length() - 1;
        block8: for (int i2 = 0; i2 <= last; ++i2) {
            char c2 = value.charAt(i2);
            if (quoted) {
                switch (c2) {
                    case '\"': {
                        char next;
                        if (i2 == last) {
                            unescaped.add(current.toString());
                            return unescaped;
                        }
                        if ((next = value.charAt(++i2)) == '\"') {
                            current.append('\"');
                            break;
                        }
                        if (next == ',') {
                            quoted = false;
                            unescaped.add(current.toString());
                            current.setLength(0);
                            break;
                        }
                        throw StringUtil.newInvalidEscapedCsvFieldException(value, i2 - 1);
                    }
                    default: {
                        current.append(c2);
                        break;
                    }
                }
                continue;
            }
            switch (c2) {
                case ',': {
                    unescaped.add(current.toString());
                    current.setLength(0);
                    continue block8;
                }
                case '\"': {
                    if (current.length() == 0) {
                        quoted = true;
                        continue block8;
                    }
                }
                case '\n': 
                case '\r': {
                    throw StringUtil.newInvalidEscapedCsvFieldException(value, i2);
                }
                default: {
                    current.append(c2);
                }
            }
        }
        if (quoted) {
            throw StringUtil.newInvalidEscapedCsvFieldException(value, last);
        }
        unescaped.add(current.toString());
        return unescaped;
    }

    private static void validateCsvFormat(CharSequence value) {
        int length = value.length();
        for (int i2 = 0; i2 < length; ++i2) {
            switch (value.charAt(i2)) {
                case '\n': 
                case '\r': 
                case '\"': 
                case ',': {
                    throw StringUtil.newInvalidEscapedCsvFieldException(value, i2);
                }
            }
        }
    }

    private static IllegalArgumentException newInvalidEscapedCsvFieldException(CharSequence value, int index) {
        return new IllegalArgumentException("invalid escaped CSV field: " + value + " index: " + index);
    }

    public static int length(String s2) {
        return s2 == null ? 0 : s2.length();
    }

    public static boolean isNullOrEmpty(String s2) {
        return s2 == null || s2.isEmpty();
    }

    public static int indexOfNonWhiteSpace(CharSequence seq, int offset) {
        while (offset < seq.length()) {
            if (!Character.isWhitespace(seq.charAt(offset))) {
                return offset;
            }
            ++offset;
        }
        return -1;
    }

    public static boolean isSurrogate(char c2) {
        return c2 >= '\ud800' && c2 <= '\udfff';
    }

    private static boolean isDoubleQuote(char c2) {
        return c2 == '\"';
    }

    public static boolean endsWith(CharSequence s2, char c2) {
        int len = s2.length();
        return len > 0 && s2.charAt(len - 1) == c2;
    }

    static {
        int i2;
        NEWLINE = System.getProperty("line.separator");
        BYTE2HEX_PAD = new String[256];
        BYTE2HEX_NOPAD = new String[256];
        for (i2 = 0; i2 < 10; ++i2) {
            StringUtil.BYTE2HEX_PAD[i2] = "0" + i2;
            StringUtil.BYTE2HEX_NOPAD[i2] = String.valueOf(i2);
        }
        while (i2 < 16) {
            char c2 = (char)(97 + i2 - 10);
            StringUtil.BYTE2HEX_PAD[i2] = "0" + c2;
            StringUtil.BYTE2HEX_NOPAD[i2] = String.valueOf(c2);
            ++i2;
        }
        while (i2 < BYTE2HEX_PAD.length) {
            String str;
            StringUtil.BYTE2HEX_PAD[i2] = str = Integer.toHexString(i2);
            StringUtil.BYTE2HEX_NOPAD[i2] = str;
            ++i2;
        }
    }
}

