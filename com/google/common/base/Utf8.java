package com.google.common.base;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;

@Beta
@GwtCompatible(emulated=true)
public final class Utf8 {
    public static int encodedLength(CharSequence sequence) {
        int i2;
        int utf16Length;
        int utf8Length = utf16Length = sequence.length();
        for (i2 = 0; i2 < utf16Length && sequence.charAt(i2) < '\u0080'; ++i2) {
        }
        while (i2 < utf16Length) {
            char c2 = sequence.charAt(i2);
            if (c2 < '\u0800') {
                utf8Length += 127 - c2 >>> 31;
            } else {
                utf8Length += Utf8.encodedLengthGeneral(sequence, i2);
                break;
            }
            ++i2;
        }
        if (utf8Length < utf16Length) {
            throw new IllegalArgumentException("UTF-8 length does not fit in int: " + ((long)utf8Length + 0x100000000L));
        }
        return utf8Length;
    }

    private static int encodedLengthGeneral(CharSequence sequence, int start) {
        int utf16Length = sequence.length();
        int utf8Length = 0;
        for (int i2 = start; i2 < utf16Length; ++i2) {
            char c2 = sequence.charAt(i2);
            if (c2 < '\u0800') {
                utf8Length += 127 - c2 >>> 31;
                continue;
            }
            utf8Length += 2;
            if ('\ud800' > c2 || c2 > '\udfff') continue;
            if (Character.codePointAt(sequence, i2) == c2) {
                throw new IllegalArgumentException(Utf8.unpairedSurrogateMsg(i2));
            }
            ++i2;
        }
        return utf8Length;
    }

    public static boolean isWellFormed(byte[] bytes) {
        return Utf8.isWellFormed(bytes, 0, bytes.length);
    }

    public static boolean isWellFormed(byte[] bytes, int off, int len) {
        int end = off + len;
        Preconditions.checkPositionIndexes(off, end, bytes.length);
        for (int i2 = off; i2 < end; ++i2) {
            if (bytes[i2] >= 0) continue;
            return Utf8.isWellFormedSlowPath(bytes, i2, end);
        }
        return true;
    }

    /*
     * Unable to fully structure code
     * Enabled aggressive block sorting
     * Lifted jumps to return sites
     */
    private static boolean isWellFormedSlowPath(byte[] bytes, int off, int end) {
        index = off;
        do lbl-1000:
        // 5 sources

        {
            block8: {
                block7: {
                    if (index >= end) {
                        return true;
                    }
                    if ((byte1 = bytes[index++]) >= 0) ** GOTO lbl-1000
                    if (byte1 >= -32) break block7;
                    if (index == end) {
                        return false;
                    }
                    if (byte1 < -62) return false;
                    if (bytes[index++] <= -65) ** GOTO lbl-1000
                    return false;
                }
                if (byte1 >= -16) break block8;
                if (index + 1 >= end) {
                    return false;
                }
                if ((byte2 = bytes[index++]) > -65) return false;
                if (byte1 == -32) {
                    if (byte2 < -96) return false;
                }
                if (byte1 == -19) {
                    if (-96 <= byte2) return false;
                }
                if (bytes[index++] <= -65) ** GOTO lbl-1000
                return false;
            }
            if (index + 2 >= end) {
                return false;
            }
            if ((byte2 = bytes[index++]) > -65) return false;
            if ((byte1 << 28) + (byte2 - -112) >> 30 != 0) return false;
            if (bytes[index++] > -65) return false;
        } while (bytes[index++] <= -65);
        return false;
    }

    private static String unpairedSurrogateMsg(int i2) {
        return "Unpaired surrogate at index " + i2;
    }

    private Utf8() {
    }
}

