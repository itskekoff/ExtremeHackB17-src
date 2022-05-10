package io.netty.handler.codec.http.cookie;

import io.netty.util.internal.InternalThreadLocalMap;
import java.util.BitSet;

final class CookieUtil {
    private static final BitSet VALID_COOKIE_NAME_OCTETS = CookieUtil.validCookieNameOctets();
    private static final BitSet VALID_COOKIE_VALUE_OCTETS = CookieUtil.validCookieValueOctets();
    private static final BitSet VALID_COOKIE_ATTRIBUTE_VALUE_OCTETS = CookieUtil.validCookieAttributeValueOctets();

    private static BitSet validCookieNameOctets() {
        int[] separators;
        BitSet bits = new BitSet();
        for (int i2 = 32; i2 < 127; ++i2) {
            bits.set(i2);
        }
        for (int separator : separators = new int[]{40, 41, 60, 62, 64, 44, 59, 58, 92, 34, 47, 91, 93, 63, 61, 123, 125, 32, 9}) {
            bits.set(separator, false);
        }
        return bits;
    }

    private static BitSet validCookieValueOctets() {
        int i2;
        BitSet bits = new BitSet();
        bits.set(33);
        for (i2 = 35; i2 <= 43; ++i2) {
            bits.set(i2);
        }
        for (i2 = 45; i2 <= 58; ++i2) {
            bits.set(i2);
        }
        for (i2 = 60; i2 <= 91; ++i2) {
            bits.set(i2);
        }
        for (i2 = 93; i2 <= 126; ++i2) {
            bits.set(i2);
        }
        return bits;
    }

    private static BitSet validCookieAttributeValueOctets() {
        BitSet bits = new BitSet();
        for (int i2 = 32; i2 < 127; ++i2) {
            bits.set(i2);
        }
        bits.set(59, false);
        return bits;
    }

    static StringBuilder stringBuilder() {
        return InternalThreadLocalMap.get().stringBuilder();
    }

    static String stripTrailingSeparatorOrNull(StringBuilder buf2) {
        return buf2.length() == 0 ? null : CookieUtil.stripTrailingSeparator(buf2);
    }

    static String stripTrailingSeparator(StringBuilder buf2) {
        if (buf2.length() > 0) {
            buf2.setLength(buf2.length() - 2);
        }
        return buf2.toString();
    }

    static void add(StringBuilder sb2, String name, long val) {
        sb2.append(name);
        sb2.append('=');
        sb2.append(val);
        sb2.append(';');
        sb2.append(' ');
    }

    static void add(StringBuilder sb2, String name, String val) {
        sb2.append(name);
        sb2.append('=');
        sb2.append(val);
        sb2.append(';');
        sb2.append(' ');
    }

    static void add(StringBuilder sb2, String name) {
        sb2.append(name);
        sb2.append(';');
        sb2.append(' ');
    }

    static void addQuoted(StringBuilder sb2, String name, String val) {
        if (val == null) {
            val = "";
        }
        sb2.append(name);
        sb2.append('=');
        sb2.append('\"');
        sb2.append(val);
        sb2.append('\"');
        sb2.append(';');
        sb2.append(' ');
    }

    static int firstInvalidCookieNameOctet(CharSequence cs2) {
        return CookieUtil.firstInvalidOctet(cs2, VALID_COOKIE_NAME_OCTETS);
    }

    static int firstInvalidCookieValueOctet(CharSequence cs2) {
        return CookieUtil.firstInvalidOctet(cs2, VALID_COOKIE_VALUE_OCTETS);
    }

    static int firstInvalidOctet(CharSequence cs2, BitSet bits) {
        for (int i2 = 0; i2 < cs2.length(); ++i2) {
            char c2 = cs2.charAt(i2);
            if (bits.get(c2)) continue;
            return i2;
        }
        return -1;
    }

    static CharSequence unwrapValue(CharSequence cs2) {
        int len = cs2.length();
        if (len > 0 && cs2.charAt(0) == '\"') {
            if (len >= 2 && cs2.charAt(len - 1) == '\"') {
                return len == 2 ? "" : cs2.subSequence(1, len - 1);
            }
            return null;
        }
        return cs2;
    }

    static String validateAttributeValue(String name, String value) {
        if (value == null) {
            return null;
        }
        if ((value = value.trim()).isEmpty()) {
            return null;
        }
        int i2 = CookieUtil.firstInvalidOctet(value, VALID_COOKIE_ATTRIBUTE_VALUE_OCTETS);
        if (i2 != -1) {
            throw new IllegalArgumentException(name + " contains the prohibited characters: " + value.charAt(i2));
        }
        return value;
    }

    private CookieUtil() {
    }
}

