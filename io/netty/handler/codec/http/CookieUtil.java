package io.netty.handler.codec.http;

import java.util.BitSet;

@Deprecated
final class CookieUtil {
    private static final BitSet VALID_COOKIE_VALUE_OCTETS = CookieUtil.validCookieValueOctets();
    private static final BitSet VALID_COOKIE_NAME_OCTETS = CookieUtil.validCookieNameOctets(VALID_COOKIE_VALUE_OCTETS);

    private static BitSet validCookieValueOctets() {
        BitSet bits = new BitSet(8);
        for (int i2 = 35; i2 < 127; ++i2) {
            bits.set(i2);
        }
        bits.set(34, false);
        bits.set(44, false);
        bits.set(59, false);
        bits.set(92, false);
        return bits;
    }

    private static BitSet validCookieNameOctets(BitSet validCookieValueOctets) {
        BitSet bits = new BitSet(8);
        bits.or(validCookieValueOctets);
        bits.set(40, false);
        bits.set(41, false);
        bits.set(60, false);
        bits.set(62, false);
        bits.set(64, false);
        bits.set(58, false);
        bits.set(47, false);
        bits.set(91, false);
        bits.set(93, false);
        bits.set(63, false);
        bits.set(61, false);
        bits.set(123, false);
        bits.set(125, false);
        bits.set(32, false);
        bits.set(9, false);
        return bits;
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

    private CookieUtil() {
    }
}

