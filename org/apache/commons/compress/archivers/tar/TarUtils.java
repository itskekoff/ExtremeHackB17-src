package org.apache.commons.compress.archivers.tar;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import org.apache.commons.compress.archivers.zip.ZipEncoding;
import org.apache.commons.compress.archivers.zip.ZipEncodingHelper;

public class TarUtils {
    private static final int BYTE_MASK = 255;
    static final ZipEncoding DEFAULT_ENCODING = ZipEncodingHelper.getZipEncoding(null);
    static final ZipEncoding FALLBACK_ENCODING = new ZipEncoding(){

        public boolean canEncode(String name) {
            return true;
        }

        public ByteBuffer encode(String name) {
            int length = name.length();
            byte[] buf2 = new byte[length];
            for (int i2 = 0; i2 < length; ++i2) {
                buf2[i2] = (byte)name.charAt(i2);
            }
            return ByteBuffer.wrap(buf2);
        }

        public String decode(byte[] buffer) {
            byte b2;
            int length = buffer.length;
            StringBuilder result = new StringBuilder(length);
            for (int i2 = 0; i2 < length && (b2 = buffer[i2]) != 0; ++i2) {
                result.append((char)(b2 & 0xFF));
            }
            return result.toString();
        }
    };

    private TarUtils() {
    }

    public static long parseOctal(byte[] buffer, int offset, int length) {
        int start;
        long result = 0L;
        int end = offset + length;
        if (length < 2) {
            throw new IllegalArgumentException("Length " + length + " must be at least 2");
        }
        if (buffer[start] == 0) {
            return 0L;
        }
        for (start = offset; start < end && buffer[start] == 32; ++start) {
        }
        byte trailer = buffer[end - 1];
        while (start < end && (trailer == 0 || trailer == 32)) {
            trailer = buffer[--end - 1];
        }
        while (start < end) {
            byte currentByte = buffer[start];
            if (currentByte < 48 || currentByte > 55) {
                throw new IllegalArgumentException(TarUtils.exceptionMessage(buffer, offset, length, start, currentByte));
            }
            result = (result << 3) + (long)(currentByte - 48);
            ++start;
        }
        return result;
    }

    public static long parseOctalOrBinary(byte[] buffer, int offset, int length) {
        boolean negative;
        if ((buffer[offset] & 0x80) == 0) {
            return TarUtils.parseOctal(buffer, offset, length);
        }
        boolean bl2 = negative = buffer[offset] == -1;
        if (length < 9) {
            return TarUtils.parseBinaryLong(buffer, offset, length, negative);
        }
        return TarUtils.parseBinaryBigInteger(buffer, offset, length, negative);
    }

    private static long parseBinaryLong(byte[] buffer, int offset, int length, boolean negative) {
        if (length >= 9) {
            throw new IllegalArgumentException("At offset " + offset + ", " + length + " byte binary number" + " exceeds maximum signed long" + " value");
        }
        long val = 0L;
        for (int i2 = 1; i2 < length; ++i2) {
            val = (val << 8) + (long)(buffer[offset + i2] & 0xFF);
        }
        if (negative) {
            --val;
            val ^= (long)Math.pow(2.0, (length - 1) * 8) - 1L;
        }
        return negative ? -val : val;
    }

    private static long parseBinaryBigInteger(byte[] buffer, int offset, int length, boolean negative) {
        byte[] remainder = new byte[length - 1];
        System.arraycopy(buffer, offset + 1, remainder, 0, length - 1);
        BigInteger val = new BigInteger(remainder);
        if (negative) {
            val = val.add(BigInteger.valueOf(-1L)).not();
        }
        if (val.bitLength() > 63) {
            throw new IllegalArgumentException("At offset " + offset + ", " + length + " byte binary number" + " exceeds maximum signed long" + " value");
        }
        return negative ? -val.longValue() : val.longValue();
    }

    public static boolean parseBoolean(byte[] buffer, int offset) {
        return buffer[offset] == 1;
    }

    private static String exceptionMessage(byte[] buffer, int offset, int length, int current, byte currentByte) {
        String string = new String(buffer, offset, length);
        string = string.replaceAll("\u0000", "{NUL}");
        String s2 = "Invalid byte " + currentByte + " at offset " + (current - offset) + " in '" + string + "' len=" + length;
        return s2;
    }

    public static String parseName(byte[] buffer, int offset, int length) {
        try {
            return TarUtils.parseName(buffer, offset, length, DEFAULT_ENCODING);
        }
        catch (IOException ex2) {
            try {
                return TarUtils.parseName(buffer, offset, length, FALLBACK_ENCODING);
            }
            catch (IOException ex22) {
                throw new RuntimeException(ex22);
            }
        }
    }

    public static String parseName(byte[] buffer, int offset, int length, ZipEncoding encoding) throws IOException {
        int len;
        for (len = length; len > 0 && buffer[offset + len - 1] == 0; --len) {
        }
        if (len > 0) {
            byte[] b2 = new byte[len];
            System.arraycopy(buffer, offset, b2, 0, len);
            return encoding.decode(b2);
        }
        return "";
    }

    public static int formatNameBytes(String name, byte[] buf2, int offset, int length) {
        try {
            return TarUtils.formatNameBytes(name, buf2, offset, length, DEFAULT_ENCODING);
        }
        catch (IOException ex2) {
            try {
                return TarUtils.formatNameBytes(name, buf2, offset, length, FALLBACK_ENCODING);
            }
            catch (IOException ex22) {
                throw new RuntimeException(ex22);
            }
        }
    }

    public static int formatNameBytes(String name, byte[] buf2, int offset, int length, ZipEncoding encoding) throws IOException {
        int len = name.length();
        ByteBuffer b2 = encoding.encode(name);
        while (b2.limit() > length && len > 0) {
            b2 = encoding.encode(name.substring(0, --len));
        }
        int limit = b2.limit() - b2.position();
        System.arraycopy(b2.array(), b2.arrayOffset(), buf2, offset, limit);
        for (int i2 = limit; i2 < length; ++i2) {
            buf2[offset + i2] = 0;
        }
        return offset + length;
    }

    public static void formatUnsignedOctalString(long value, byte[] buffer, int offset, int length) {
        int remaining = length;
        --remaining;
        if (value == 0L) {
            buffer[offset + remaining--] = 48;
        } else {
            long val;
            for (val = value; remaining >= 0 && val != 0L; val >>>= 3, --remaining) {
                buffer[offset + remaining] = (byte)(48 + (byte)(val & 7L));
            }
            if (val != 0L) {
                throw new IllegalArgumentException(value + "=" + Long.toOctalString(value) + " will not fit in octal number buffer of length " + length);
            }
        }
        while (remaining >= 0) {
            buffer[offset + remaining] = 48;
            --remaining;
        }
    }

    public static int formatOctalBytes(long value, byte[] buf2, int offset, int length) {
        int idx = length - 2;
        TarUtils.formatUnsignedOctalString(value, buf2, offset, idx);
        buf2[offset + idx++] = 32;
        buf2[offset + idx] = 0;
        return offset + length;
    }

    public static int formatLongOctalBytes(long value, byte[] buf2, int offset, int length) {
        int idx = length - 1;
        TarUtils.formatUnsignedOctalString(value, buf2, offset, idx);
        buf2[offset + idx] = 32;
        return offset + length;
    }

    public static int formatLongOctalOrBinaryBytes(long value, byte[] buf2, int offset, int length) {
        boolean negative;
        long maxAsOctalChar = length == 8 ? 0x1FFFFFL : 0x1FFFFFFFFL;
        boolean bl2 = negative = value < 0L;
        if (!negative && value <= maxAsOctalChar) {
            return TarUtils.formatLongOctalBytes(value, buf2, offset, length);
        }
        if (length < 9) {
            TarUtils.formatLongBinary(value, buf2, offset, length, negative);
        }
        TarUtils.formatBigIntegerBinary(value, buf2, offset, length, negative);
        buf2[offset] = (byte)(negative ? 255 : 128);
        return offset + length;
    }

    private static void formatLongBinary(long value, byte[] buf2, int offset, int length, boolean negative) {
        int bits = (length - 1) * 8;
        long max = 1L << bits;
        long val = Math.abs(value);
        if (val >= max) {
            throw new IllegalArgumentException("Value " + value + " is too large for " + length + " byte field.");
        }
        if (negative) {
            val ^= max - 1L;
            val |= (long)(255 << bits);
            ++val;
        }
        for (int i2 = offset + length - 1; i2 >= offset; --i2) {
            buf2[i2] = (byte)val;
            val >>= 8;
        }
    }

    private static void formatBigIntegerBinary(long value, byte[] buf2, int offset, int length, boolean negative) {
        BigInteger val = BigInteger.valueOf(value);
        byte[] b2 = val.toByteArray();
        int len = b2.length;
        int off = offset + length - len;
        System.arraycopy(b2, 0, buf2, off, len);
        byte fill = (byte)(negative ? 255 : 0);
        for (int i2 = offset + 1; i2 < off; ++i2) {
            buf2[i2] = fill;
        }
    }

    public static int formatCheckSumOctalBytes(long value, byte[] buf2, int offset, int length) {
        int idx = length - 2;
        TarUtils.formatUnsignedOctalString(value, buf2, offset, idx);
        buf2[offset + idx++] = 0;
        buf2[offset + idx] = 32;
        return offset + length;
    }

    public static long computeCheckSum(byte[] buf2) {
        long sum = 0L;
        for (byte element : buf2) {
            sum += (long)(0xFF & element);
        }
        return sum;
    }

    public static boolean verifyCheckSum(byte[] header) {
        long storedSum = 0L;
        long unsignedSum = 0L;
        long signedSum = 0L;
        int digits = 0;
        for (int i2 = 0; i2 < header.length; ++i2) {
            int b2 = header[i2];
            if (148 <= i2 && i2 < 156) {
                if (48 <= b2 && b2 <= 55 && digits++ < 6) {
                    storedSum = storedSum * 8L + (long)b2 - 48L;
                } else if (digits > 0) {
                    digits = 6;
                }
                b2 = 32;
            }
            unsignedSum += (long)(0xFF & b2);
            signedSum += (long)b2;
        }
        return storedSum == unsignedSum || storedSum == signedSum || storedSum > unsignedSum;
    }
}

