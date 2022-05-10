package io.netty.buffer;

import io.netty.buffer.AbstractByteBuf;
import io.netty.buffer.AbstractByteBufAllocator;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.buffer.UnpooledByteBufAllocator;
import io.netty.buffer.UnpooledDirectByteBuf;
import io.netty.buffer.UnpooledUnsafeDirectByteBuf;
import io.netty.buffer.WrappedByteBuf;
import io.netty.util.AsciiString;
import io.netty.util.ByteProcessor;
import io.netty.util.CharsetUtil;
import io.netty.util.Recycler;
import io.netty.util.concurrent.FastThreadLocal;
import io.netty.util.internal.MathUtil;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.StringUtil;
import io.netty.util.internal.SystemPropertyUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;
import java.nio.charset.CodingErrorAction;
import java.util.Arrays;
import java.util.Locale;

public final class ByteBufUtil {
    private static final InternalLogger logger;
    private static final FastThreadLocal<CharBuffer> CHAR_BUFFERS;
    private static final byte WRITE_UTF_UNKNOWN = 63;
    private static final int MAX_CHAR_BUFFER_SIZE;
    private static final int THREAD_LOCAL_BUFFER_SIZE;
    private static final int MAX_BYTES_PER_CHAR_UTF8;
    static final ByteBufAllocator DEFAULT_ALLOCATOR;
    private static final ByteProcessor FIND_NON_ASCII;

    public static String hexDump(ByteBuf buffer) {
        return ByteBufUtil.hexDump(buffer, buffer.readerIndex(), buffer.readableBytes());
    }

    public static String hexDump(ByteBuf buffer, int fromIndex, int length) {
        return HexUtil.hexDump(buffer, fromIndex, length);
    }

    public static String hexDump(byte[] array) {
        return ByteBufUtil.hexDump(array, 0, array.length);
    }

    public static String hexDump(byte[] array, int fromIndex, int length) {
        return HexUtil.hexDump(array, fromIndex, length);
    }

    public static int hashCode(ByteBuf buffer) {
        int i2;
        int aLen = buffer.readableBytes();
        int intCount = aLen >>> 2;
        int byteCount = aLen & 3;
        int hashCode = 1;
        int arrayIndex = buffer.readerIndex();
        if (buffer.order() == ByteOrder.BIG_ENDIAN) {
            for (i2 = intCount; i2 > 0; --i2) {
                hashCode = 31 * hashCode + buffer.getInt(arrayIndex);
                arrayIndex += 4;
            }
        } else {
            for (i2 = intCount; i2 > 0; --i2) {
                hashCode = 31 * hashCode + ByteBufUtil.swapInt(buffer.getInt(arrayIndex));
                arrayIndex += 4;
            }
        }
        for (i2 = byteCount; i2 > 0; --i2) {
            hashCode = 31 * hashCode + buffer.getByte(arrayIndex++);
        }
        if (hashCode == 0) {
            hashCode = 1;
        }
        return hashCode;
    }

    public static int indexOf(ByteBuf needle, ByteBuf haystack) {
        int attempts = haystack.readableBytes() - needle.readableBytes() + 1;
        for (int i2 = 0; i2 < attempts; ++i2) {
            if (!ByteBufUtil.equals(needle, needle.readerIndex(), haystack, haystack.readerIndex() + i2, needle.readableBytes())) continue;
            return haystack.readerIndex() + i2;
        }
        return -1;
    }

    public static boolean equals(ByteBuf a2, int aStartIndex, ByteBuf b2, int bStartIndex, int length) {
        int i2;
        if (aStartIndex < 0 || bStartIndex < 0 || length < 0) {
            throw new IllegalArgumentException("All indexes and lengths must be non-negative");
        }
        if (a2.writerIndex() - length < aStartIndex || b2.writerIndex() - length < bStartIndex) {
            return false;
        }
        int longCount = length >>> 3;
        int byteCount = length & 7;
        if (a2.order() == b2.order()) {
            for (i2 = longCount; i2 > 0; --i2) {
                if (a2.getLong(aStartIndex) != b2.getLong(bStartIndex)) {
                    return false;
                }
                aStartIndex += 8;
                bStartIndex += 8;
            }
        } else {
            for (i2 = longCount; i2 > 0; --i2) {
                if (a2.getLong(aStartIndex) != ByteBufUtil.swapLong(b2.getLong(bStartIndex))) {
                    return false;
                }
                aStartIndex += 8;
                bStartIndex += 8;
            }
        }
        for (i2 = byteCount; i2 > 0; --i2) {
            if (a2.getByte(aStartIndex) != b2.getByte(bStartIndex)) {
                return false;
            }
            ++aStartIndex;
            ++bStartIndex;
        }
        return true;
    }

    public static boolean equals(ByteBuf bufferA, ByteBuf bufferB) {
        int aLen = bufferA.readableBytes();
        if (aLen != bufferB.readableBytes()) {
            return false;
        }
        return ByteBufUtil.equals(bufferA, bufferA.readerIndex(), bufferB, bufferB.readerIndex(), aLen);
    }

    public static int compare(ByteBuf bufferA, ByteBuf bufferB) {
        int aLen = bufferA.readableBytes();
        int bLen = bufferB.readableBytes();
        int minLength = Math.min(aLen, bLen);
        int uintCount = minLength >>> 2;
        int byteCount = minLength & 3;
        int aIndex = bufferA.readerIndex();
        int bIndex = bufferB.readerIndex();
        if (uintCount > 0) {
            long res;
            boolean bufferAIsBigEndian = bufferA.order() == ByteOrder.BIG_ENDIAN;
            int uintCountIncrement = uintCount << 2;
            if (bufferA.order() == bufferB.order()) {
                res = bufferAIsBigEndian ? ByteBufUtil.compareUintBigEndian(bufferA, bufferB, aIndex, bIndex, uintCountIncrement) : ByteBufUtil.compareUintLittleEndian(bufferA, bufferB, aIndex, bIndex, uintCountIncrement);
            } else {
                long l2 = res = bufferAIsBigEndian ? ByteBufUtil.compareUintBigEndianA(bufferA, bufferB, aIndex, bIndex, uintCountIncrement) : ByteBufUtil.compareUintBigEndianB(bufferA, bufferB, aIndex, bIndex, uintCountIncrement);
            }
            if (res != 0L) {
                return (int)Math.min(Integer.MAX_VALUE, Math.max(Integer.MIN_VALUE, res));
            }
            aIndex += uintCountIncrement;
            bIndex += uintCountIncrement;
        }
        int aEnd = aIndex + byteCount;
        while (aIndex < aEnd) {
            int comp = bufferA.getUnsignedByte(aIndex) - bufferB.getUnsignedByte(bIndex);
            if (comp != 0) {
                return comp;
            }
            ++aIndex;
            ++bIndex;
        }
        return aLen - bLen;
    }

    private static long compareUintBigEndian(ByteBuf bufferA, ByteBuf bufferB, int aIndex, int bIndex, int uintCountIncrement) {
        int aEnd = aIndex + uintCountIncrement;
        while (aIndex < aEnd) {
            long comp = bufferA.getUnsignedInt(aIndex) - bufferB.getUnsignedInt(bIndex);
            if (comp != 0L) {
                return comp;
            }
            aIndex += 4;
            bIndex += 4;
        }
        return 0L;
    }

    private static long compareUintLittleEndian(ByteBuf bufferA, ByteBuf bufferB, int aIndex, int bIndex, int uintCountIncrement) {
        int aEnd = aIndex + uintCountIncrement;
        while (aIndex < aEnd) {
            long comp = bufferA.getUnsignedIntLE(aIndex) - bufferB.getUnsignedIntLE(bIndex);
            if (comp != 0L) {
                return comp;
            }
            aIndex += 4;
            bIndex += 4;
        }
        return 0L;
    }

    private static long compareUintBigEndianA(ByteBuf bufferA, ByteBuf bufferB, int aIndex, int bIndex, int uintCountIncrement) {
        int aEnd = aIndex + uintCountIncrement;
        while (aIndex < aEnd) {
            long comp = bufferA.getUnsignedInt(aIndex) - bufferB.getUnsignedIntLE(bIndex);
            if (comp != 0L) {
                return comp;
            }
            aIndex += 4;
            bIndex += 4;
        }
        return 0L;
    }

    private static long compareUintBigEndianB(ByteBuf bufferA, ByteBuf bufferB, int aIndex, int bIndex, int uintCountIncrement) {
        int aEnd = aIndex + uintCountIncrement;
        while (aIndex < aEnd) {
            long comp = bufferA.getUnsignedIntLE(aIndex) - bufferB.getUnsignedInt(bIndex);
            if (comp != 0L) {
                return comp;
            }
            aIndex += 4;
            bIndex += 4;
        }
        return 0L;
    }

    public static int indexOf(ByteBuf buffer, int fromIndex, int toIndex, byte value) {
        if (fromIndex <= toIndex) {
            return ByteBufUtil.firstIndexOf(buffer, fromIndex, toIndex, value);
        }
        return ByteBufUtil.lastIndexOf(buffer, fromIndex, toIndex, value);
    }

    public static short swapShort(short value) {
        return Short.reverseBytes(value);
    }

    public static int swapMedium(int value) {
        int swapped = value << 16 & 0xFF0000 | value & 0xFF00 | value >>> 16 & 0xFF;
        if ((swapped & 0x800000) != 0) {
            swapped |= 0xFF000000;
        }
        return swapped;
    }

    public static int swapInt(int value) {
        return Integer.reverseBytes(value);
    }

    public static long swapLong(long value) {
        return Long.reverseBytes(value);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static ByteBuf readBytes(ByteBufAllocator alloc, ByteBuf buffer, int length) {
        boolean release = true;
        ByteBuf dst = alloc.buffer(length);
        try {
            buffer.readBytes(dst);
            release = false;
            ByteBuf byteBuf = dst;
            return byteBuf;
        }
        finally {
            if (release) {
                dst.release();
            }
        }
    }

    private static int firstIndexOf(ByteBuf buffer, int fromIndex, int toIndex, byte value) {
        if ((fromIndex = Math.max(fromIndex, 0)) >= toIndex || buffer.capacity() == 0) {
            return -1;
        }
        return buffer.forEachByte(fromIndex, toIndex - fromIndex, new ByteProcessor.IndexOfProcessor(value));
    }

    private static int lastIndexOf(ByteBuf buffer, int fromIndex, int toIndex, byte value) {
        if ((fromIndex = Math.min(fromIndex, buffer.capacity())) < 0 || buffer.capacity() == 0) {
            return -1;
        }
        return buffer.forEachByteDesc(toIndex, fromIndex - toIndex, new ByteProcessor.IndexOfProcessor(value));
    }

    public static ByteBuf writeUtf8(ByteBufAllocator alloc, CharSequence seq) {
        ByteBuf buf2 = alloc.buffer(ByteBufUtil.utf8MaxBytes(seq));
        ByteBufUtil.writeUtf8(buf2, seq);
        return buf2;
    }

    public static int writeUtf8(ByteBuf buf2, CharSequence seq) {
        int len = seq.length();
        buf2.ensureWritable(ByteBufUtil.utf8MaxBytes(seq));
        while (true) {
            if (buf2 instanceof AbstractByteBuf) {
                AbstractByteBuf byteBuf = (AbstractByteBuf)buf2;
                int written = ByteBufUtil.writeUtf8(byteBuf, byteBuf.writerIndex, seq, len);
                byteBuf.writerIndex += written;
                return written;
            }
            if (!(buf2 instanceof WrappedByteBuf)) break;
            buf2 = buf2.unwrap();
        }
        byte[] bytes = seq.toString().getBytes(CharsetUtil.UTF_8);
        buf2.writeBytes(bytes);
        return bytes.length;
    }

    static int writeUtf8(AbstractByteBuf buffer, int writerIndex, CharSequence seq, int len) {
        int oldWriterIndex = writerIndex;
        for (int i2 = 0; i2 < len; ++i2) {
            char c2 = seq.charAt(i2);
            if (c2 < '\u0080') {
                buffer._setByte(writerIndex++, (byte)c2);
                continue;
            }
            if (c2 < '\u0800') {
                buffer._setByte(writerIndex++, (byte)(0xC0 | c2 >> 6));
                buffer._setByte(writerIndex++, (byte)(0x80 | c2 & 0x3F));
                continue;
            }
            if (StringUtil.isSurrogate(c2)) {
                char c22;
                if (!Character.isHighSurrogate(c2)) {
                    buffer._setByte(writerIndex++, 63);
                    continue;
                }
                try {
                    c22 = seq.charAt(++i2);
                }
                catch (IndexOutOfBoundsException e2) {
                    buffer._setByte(writerIndex++, 63);
                    break;
                }
                if (!Character.isLowSurrogate(c22)) {
                    buffer._setByte(writerIndex++, 63);
                    buffer._setByte(writerIndex++, Character.isHighSurrogate(c22) ? 63 : (int)c22);
                    continue;
                }
                int codePoint = Character.toCodePoint(c2, c22);
                buffer._setByte(writerIndex++, (byte)(0xF0 | codePoint >> 18));
                buffer._setByte(writerIndex++, (byte)(0x80 | codePoint >> 12 & 0x3F));
                buffer._setByte(writerIndex++, (byte)(0x80 | codePoint >> 6 & 0x3F));
                buffer._setByte(writerIndex++, (byte)(0x80 | codePoint & 0x3F));
                continue;
            }
            buffer._setByte(writerIndex++, (byte)(0xE0 | c2 >> 12));
            buffer._setByte(writerIndex++, (byte)(0x80 | c2 >> 6 & 0x3F));
            buffer._setByte(writerIndex++, (byte)(0x80 | c2 & 0x3F));
        }
        return writerIndex - oldWriterIndex;
    }

    public static int utf8MaxBytes(CharSequence seq) {
        return seq.length() * MAX_BYTES_PER_CHAR_UTF8;
    }

    public static ByteBuf writeAscii(ByteBufAllocator alloc, CharSequence seq) {
        ByteBuf buf2 = alloc.buffer(seq.length());
        ByteBufUtil.writeAscii(buf2, seq);
        return buf2;
    }

    public static int writeAscii(ByteBuf buf2, CharSequence seq) {
        int len = seq.length();
        buf2.ensureWritable(len);
        if (!(seq instanceof AsciiString)) {
            while (true) {
                if (buf2 instanceof AbstractByteBuf) {
                    AbstractByteBuf byteBuf = (AbstractByteBuf)buf2;
                    int written = ByteBufUtil.writeAscii(byteBuf, byteBuf.writerIndex, seq, len);
                    byteBuf.writerIndex += written;
                    return written;
                }
                if (buf2 instanceof WrappedByteBuf) {
                    buf2 = buf2.unwrap();
                    continue;
                }
                buf2.writeBytes(seq.toString().getBytes(CharsetUtil.US_ASCII));
            }
        }
        AsciiString asciiString = (AsciiString)seq;
        buf2.writeBytes(asciiString.array(), asciiString.arrayOffset(), asciiString.length());
        return len;
    }

    static int writeAscii(AbstractByteBuf buffer, int writerIndex, CharSequence seq, int len) {
        for (int i2 = 0; i2 < len; ++i2) {
            buffer._setByte(writerIndex++, (byte)seq.charAt(i2));
        }
        return len;
    }

    public static ByteBuf encodeString(ByteBufAllocator alloc, CharBuffer src, Charset charset) {
        return ByteBufUtil.encodeString0(alloc, false, src, charset, 0);
    }

    public static ByteBuf encodeString(ByteBufAllocator alloc, CharBuffer src, Charset charset, int extraCapacity) {
        return ByteBufUtil.encodeString0(alloc, false, src, charset, extraCapacity);
    }

    static ByteBuf encodeString0(ByteBufAllocator alloc, boolean enforceHeap, CharBuffer src, Charset charset, int extraCapacity) {
        CharsetEncoder encoder = CharsetUtil.encoder(charset);
        int length = (int)((double)src.remaining() * (double)encoder.maxBytesPerChar()) + extraCapacity;
        boolean release = true;
        ByteBuf dst = enforceHeap ? alloc.heapBuffer(length) : alloc.buffer(length);
        try {
            ByteBuffer dstBuf = dst.internalNioBuffer(dst.readerIndex(), length);
            int pos = dstBuf.position();
            CoderResult cr2 = encoder.encode(src, dstBuf, true);
            if (!cr2.isUnderflow()) {
                cr2.throwException();
            }
            if (!(cr2 = encoder.flush(dstBuf)).isUnderflow()) {
                cr2.throwException();
            }
            dst.writerIndex(dst.writerIndex() + dstBuf.position() - pos);
            release = false;
            ByteBuf byteBuf = dst;
            return byteBuf;
        }
        catch (CharacterCodingException x2) {
            throw new IllegalStateException(x2);
        }
        finally {
            if (release) {
                dst.release();
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    static String decodeString(ByteBuf src, int readerIndex, int len, Charset charset) {
        if (len == 0) {
            return "";
        }
        CharsetDecoder decoder = CharsetUtil.decoder(charset);
        int maxLength = (int)((double)len * (double)decoder.maxCharsPerByte());
        CharBuffer dst = CHAR_BUFFERS.get();
        if (dst.length() < maxLength) {
            dst = CharBuffer.allocate(maxLength);
            if (maxLength <= MAX_CHAR_BUFFER_SIZE) {
                CHAR_BUFFERS.set(dst);
            }
        } else {
            dst.clear();
        }
        if (src.nioBufferCount() == 1) {
            ByteBufUtil.decodeString(decoder, src.internalNioBuffer(readerIndex, len), dst);
        } else {
            ByteBuf buffer = src.alloc().heapBuffer(len);
            try {
                buffer.writeBytes(src, readerIndex, len);
                ByteBufUtil.decodeString(decoder, buffer.internalNioBuffer(buffer.readerIndex(), len), dst);
            }
            finally {
                buffer.release();
            }
        }
        return dst.flip().toString();
    }

    private static void decodeString(CharsetDecoder decoder, ByteBuffer src, CharBuffer dst) {
        try {
            CoderResult cr2 = decoder.decode(src, dst, true);
            if (!cr2.isUnderflow()) {
                cr2.throwException();
            }
            if (!(cr2 = decoder.flush(dst)).isUnderflow()) {
                cr2.throwException();
            }
        }
        catch (CharacterCodingException x2) {
            throw new IllegalStateException(x2);
        }
    }

    public static ByteBuf threadLocalDirectBuffer() {
        if (THREAD_LOCAL_BUFFER_SIZE <= 0) {
            return null;
        }
        if (PlatformDependent.hasUnsafe()) {
            return ThreadLocalUnsafeDirectByteBuf.newInstance();
        }
        return ThreadLocalDirectByteBuf.newInstance();
    }

    public static byte[] getBytes(ByteBuf buf2) {
        return ByteBufUtil.getBytes(buf2, buf2.readerIndex(), buf2.readableBytes());
    }

    public static byte[] getBytes(ByteBuf buf2, int start, int length) {
        return ByteBufUtil.getBytes(buf2, start, length, true);
    }

    public static byte[] getBytes(ByteBuf buf2, int start, int length, boolean copy) {
        if (MathUtil.isOutOfBounds(start, length, buf2.capacity())) {
            throw new IndexOutOfBoundsException("expected: 0 <= start(" + start + ") <= start + length(" + length + ") <= buf.capacity(" + buf2.capacity() + ')');
        }
        if (buf2.hasArray()) {
            if (copy || start != 0 || length != buf2.capacity()) {
                int baseOffset = buf2.arrayOffset() + start;
                return Arrays.copyOfRange(buf2.array(), baseOffset, baseOffset + length);
            }
            return buf2.array();
        }
        byte[] v2 = new byte[length];
        buf2.getBytes(start, v2);
        return v2;
    }

    public static void copy(AsciiString src, int srcIdx, ByteBuf dst, int dstIdx, int length) {
        if (MathUtil.isOutOfBounds(srcIdx, length, src.length())) {
            throw new IndexOutOfBoundsException("expected: 0 <= srcIdx(" + srcIdx + ") <= srcIdx + length(" + length + ") <= srcLen(" + src.length() + ')');
        }
        ObjectUtil.checkNotNull(dst, "dst").setBytes(dstIdx, src.array(), srcIdx + src.arrayOffset(), length);
    }

    public static void copy(AsciiString src, int srcIdx, ByteBuf dst, int length) {
        if (MathUtil.isOutOfBounds(srcIdx, length, src.length())) {
            throw new IndexOutOfBoundsException("expected: 0 <= srcIdx(" + srcIdx + ") <= srcIdx + length(" + length + ") <= srcLen(" + src.length() + ')');
        }
        ObjectUtil.checkNotNull(dst, "dst").writeBytes(src.array(), srcIdx + src.arrayOffset(), length);
    }

    public static String prettyHexDump(ByteBuf buffer) {
        return ByteBufUtil.prettyHexDump(buffer, buffer.readerIndex(), buffer.readableBytes());
    }

    public static String prettyHexDump(ByteBuf buffer, int offset, int length) {
        return HexUtil.prettyHexDump(buffer, offset, length);
    }

    public static void appendPrettyHexDump(StringBuilder dump, ByteBuf buf2) {
        ByteBufUtil.appendPrettyHexDump(dump, buf2, buf2.readerIndex(), buf2.readableBytes());
    }

    public static void appendPrettyHexDump(StringBuilder dump, ByteBuf buf2, int offset, int length) {
        HexUtil.appendPrettyHexDump(dump, buf2, offset, length);
    }

    public static boolean isText(ByteBuf buf2, Charset charset) {
        return ByteBufUtil.isText(buf2, buf2.readerIndex(), buf2.readableBytes(), charset);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static boolean isText(ByteBuf buf2, int index, int length, Charset charset) {
        ObjectUtil.checkNotNull(buf2, "buf");
        ObjectUtil.checkNotNull(charset, "charset");
        int maxIndex = buf2.readerIndex() + buf2.readableBytes();
        if (index < 0 || length < 0 || index > maxIndex - length) {
            throw new IndexOutOfBoundsException("index: " + index + " length: " + length);
        }
        if (charset.equals(CharsetUtil.UTF_8)) {
            return ByteBufUtil.isUtf8(buf2, index, length);
        }
        if (charset.equals(CharsetUtil.US_ASCII)) {
            return ByteBufUtil.isAscii(buf2, index, length);
        }
        CharsetDecoder decoder = CharsetUtil.decoder(charset, CodingErrorAction.REPORT, CodingErrorAction.REPORT);
        try {
            if (buf2.nioBufferCount() == 1) {
                decoder.decode(buf2.internalNioBuffer(index, length));
            } else {
                ByteBuf heapBuffer = buf2.alloc().heapBuffer(length);
                try {
                    heapBuffer.writeBytes(buf2, index, length);
                    decoder.decode(heapBuffer.internalNioBuffer(heapBuffer.readerIndex(), length));
                }
                finally {
                    heapBuffer.release();
                }
            }
            return true;
        }
        catch (CharacterCodingException ignore) {
            return false;
        }
    }

    private static boolean isAscii(ByteBuf buf2, int index, int length) {
        return buf2.forEachByte(index, length, FIND_NON_ASCII) == -1;
    }

    private static boolean isUtf8(ByteBuf buf2, int index, int length) {
        int endIndex = index + length;
        while (index < endIndex) {
            byte b3;
            byte b2;
            byte b1;
            if (((b1 = buf2.getByte(index++)) & 0x80) == 0) continue;
            if ((b1 & 0xE0) == 192) {
                if (index >= endIndex) {
                    return false;
                }
                if (((b2 = buf2.getByte(index++)) & 0xC0) != 128) {
                    return false;
                }
                if ((b1 & 0xFF) >= 194) continue;
                return false;
            }
            if ((b1 & 0xF0) == 224) {
                if (index > endIndex - 2) {
                    return false;
                }
                b2 = buf2.getByte(index++);
                b3 = buf2.getByte(index++);
                if ((b2 & 0xC0) != 128 || (b3 & 0xC0) != 128) {
                    return false;
                }
                if ((b1 & 0xF) == 0 && (b2 & 0xFF) < 160) {
                    return false;
                }
                if ((b1 & 0xF) != 13 || (b2 & 0xFF) <= 159) continue;
                return false;
            }
            if ((b1 & 0xF8) == 240) {
                if (index > endIndex - 3) {
                    return false;
                }
                b2 = buf2.getByte(index++);
                b3 = buf2.getByte(index++);
                byte b4 = buf2.getByte(index++);
                if ((b2 & 0xC0) != 128 || (b3 & 0xC0) != 128 || (b4 & 0xC0) != 128) {
                    return false;
                }
                if ((b1 & 0xFF) <= 244 && ((b1 & 0xFF) != 240 || (b2 & 0xFF) >= 144) && ((b1 & 0xFF) != 244 || (b2 & 0xFF) <= 143)) continue;
                return false;
            }
            return false;
        }
        return true;
    }

    private ByteBufUtil() {
    }

    static {
        AbstractByteBufAllocator alloc;
        logger = InternalLoggerFactory.getInstance(ByteBufUtil.class);
        CHAR_BUFFERS = new FastThreadLocal<CharBuffer>(){

            @Override
            protected CharBuffer initialValue() throws Exception {
                return CharBuffer.allocate(1024);
            }
        };
        MAX_BYTES_PER_CHAR_UTF8 = (int)CharsetUtil.encoder(CharsetUtil.UTF_8).maxBytesPerChar();
        String allocType = SystemPropertyUtil.get("io.netty.allocator.type", PlatformDependent.isAndroid() ? "unpooled" : "pooled");
        if ("unpooled".equals(allocType = allocType.toLowerCase(Locale.US).trim())) {
            alloc = UnpooledByteBufAllocator.DEFAULT;
            logger.debug("-Dio.netty.allocator.type: {}", (Object)allocType);
        } else if ("pooled".equals(allocType)) {
            alloc = PooledByteBufAllocator.DEFAULT;
            logger.debug("-Dio.netty.allocator.type: {}", (Object)allocType);
        } else {
            alloc = PooledByteBufAllocator.DEFAULT;
            logger.debug("-Dio.netty.allocator.type: pooled (unknown: {})", (Object)allocType);
        }
        DEFAULT_ALLOCATOR = alloc;
        THREAD_LOCAL_BUFFER_SIZE = SystemPropertyUtil.getInt("io.netty.threadLocalDirectBufferSize", 65536);
        logger.debug("-Dio.netty.threadLocalDirectBufferSize: {}", (Object)THREAD_LOCAL_BUFFER_SIZE);
        MAX_CHAR_BUFFER_SIZE = SystemPropertyUtil.getInt("io.netty.maxThreadLocalCharBufferSize", 16384);
        logger.debug("-Dio.netty.maxThreadLocalCharBufferSize: {}", (Object)MAX_CHAR_BUFFER_SIZE);
        FIND_NON_ASCII = new ByteProcessor(){

            @Override
            public boolean process(byte value) {
                return value >= 0;
            }
        };
    }

    static final class ThreadLocalDirectByteBuf
    extends UnpooledDirectByteBuf {
        private static final Recycler<ThreadLocalDirectByteBuf> RECYCLER = new Recycler<ThreadLocalDirectByteBuf>(){

            @Override
            protected ThreadLocalDirectByteBuf newObject(Recycler.Handle<ThreadLocalDirectByteBuf> handle) {
                return new ThreadLocalDirectByteBuf(handle);
            }
        };
        private final Recycler.Handle<ThreadLocalDirectByteBuf> handle;

        static ThreadLocalDirectByteBuf newInstance() {
            ThreadLocalDirectByteBuf buf2 = RECYCLER.get();
            buf2.setRefCnt(1);
            return buf2;
        }

        private ThreadLocalDirectByteBuf(Recycler.Handle<ThreadLocalDirectByteBuf> handle) {
            super((ByteBufAllocator)UnpooledByteBufAllocator.DEFAULT, 256, Integer.MAX_VALUE);
            this.handle = handle;
        }

        @Override
        protected void deallocate() {
            if (this.capacity() > THREAD_LOCAL_BUFFER_SIZE) {
                super.deallocate();
            } else {
                this.clear();
                this.handle.recycle(this);
            }
        }
    }

    static final class ThreadLocalUnsafeDirectByteBuf
    extends UnpooledUnsafeDirectByteBuf {
        private static final Recycler<ThreadLocalUnsafeDirectByteBuf> RECYCLER = new Recycler<ThreadLocalUnsafeDirectByteBuf>(){

            @Override
            protected ThreadLocalUnsafeDirectByteBuf newObject(Recycler.Handle<ThreadLocalUnsafeDirectByteBuf> handle) {
                return new ThreadLocalUnsafeDirectByteBuf(handle);
            }
        };
        private final Recycler.Handle<ThreadLocalUnsafeDirectByteBuf> handle;

        static ThreadLocalUnsafeDirectByteBuf newInstance() {
            ThreadLocalUnsafeDirectByteBuf buf2 = RECYCLER.get();
            buf2.setRefCnt(1);
            return buf2;
        }

        private ThreadLocalUnsafeDirectByteBuf(Recycler.Handle<ThreadLocalUnsafeDirectByteBuf> handle) {
            super((ByteBufAllocator)UnpooledByteBufAllocator.DEFAULT, 256, Integer.MAX_VALUE);
            this.handle = handle;
        }

        @Override
        protected void deallocate() {
            if (this.capacity() > THREAD_LOCAL_BUFFER_SIZE) {
                super.deallocate();
            } else {
                this.clear();
                this.handle.recycle(this);
            }
        }
    }

    private static final class HexUtil {
        private static final char[] BYTE2CHAR;
        private static final char[] HEXDUMP_TABLE;
        private static final String[] HEXPADDING;
        private static final String[] HEXDUMP_ROWPREFIXES;
        private static final String[] BYTE2HEX;
        private static final String[] BYTEPADDING;

        private HexUtil() {
        }

        private static String hexDump(ByteBuf buffer, int fromIndex, int length) {
            if (length < 0) {
                throw new IllegalArgumentException("length: " + length);
            }
            if (length == 0) {
                return "";
            }
            int endIndex = fromIndex + length;
            char[] buf2 = new char[length << 1];
            int srcIdx = fromIndex;
            int dstIdx = 0;
            while (srcIdx < endIndex) {
                System.arraycopy(HEXDUMP_TABLE, buffer.getUnsignedByte(srcIdx) << 1, buf2, dstIdx, 2);
                ++srcIdx;
                dstIdx += 2;
            }
            return new String(buf2);
        }

        private static String hexDump(byte[] array, int fromIndex, int length) {
            if (length < 0) {
                throw new IllegalArgumentException("length: " + length);
            }
            if (length == 0) {
                return "";
            }
            int endIndex = fromIndex + length;
            char[] buf2 = new char[length << 1];
            int srcIdx = fromIndex;
            int dstIdx = 0;
            while (srcIdx < endIndex) {
                System.arraycopy(HEXDUMP_TABLE, (array[srcIdx] & 0xFF) << 1, buf2, dstIdx, 2);
                ++srcIdx;
                dstIdx += 2;
            }
            return new String(buf2);
        }

        private static String prettyHexDump(ByteBuf buffer, int offset, int length) {
            if (length == 0) {
                return "";
            }
            int rows = length / 16 + (length % 15 == 0 ? 0 : 1) + 4;
            StringBuilder buf2 = new StringBuilder(rows * 80);
            HexUtil.appendPrettyHexDump(buf2, buffer, offset, length);
            return buf2.toString();
        }

        private static void appendPrettyHexDump(StringBuilder dump, ByteBuf buf2, int offset, int length) {
            if (MathUtil.isOutOfBounds(offset, length, buf2.capacity())) {
                throw new IndexOutOfBoundsException("expected: 0 <= offset(" + offset + ") <= offset + length(" + length + ") <= buf.capacity(" + buf2.capacity() + ')');
            }
            if (length == 0) {
                return;
            }
            dump.append("         +-------------------------------------------------+" + StringUtil.NEWLINE + "         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |" + StringUtil.NEWLINE + "+--------+-------------------------------------------------+----------------+");
            int startIndex = offset;
            int fullRows = length >>> 4;
            int remainder = length & 0xF;
            for (int row = 0; row < fullRows; ++row) {
                int j2;
                int rowStartIndex = (row << 4) + startIndex;
                HexUtil.appendHexDumpRowPrefix(dump, row, rowStartIndex);
                int rowEndIndex = rowStartIndex + 16;
                for (j2 = rowStartIndex; j2 < rowEndIndex; ++j2) {
                    dump.append(BYTE2HEX[buf2.getUnsignedByte(j2)]);
                }
                dump.append(" |");
                for (j2 = rowStartIndex; j2 < rowEndIndex; ++j2) {
                    dump.append(BYTE2CHAR[buf2.getUnsignedByte(j2)]);
                }
                dump.append('|');
            }
            if (remainder != 0) {
                int j3;
                int rowStartIndex = (fullRows << 4) + startIndex;
                HexUtil.appendHexDumpRowPrefix(dump, fullRows, rowStartIndex);
                int rowEndIndex = rowStartIndex + remainder;
                for (j3 = rowStartIndex; j3 < rowEndIndex; ++j3) {
                    dump.append(BYTE2HEX[buf2.getUnsignedByte(j3)]);
                }
                dump.append(HEXPADDING[remainder]);
                dump.append(" |");
                for (j3 = rowStartIndex; j3 < rowEndIndex; ++j3) {
                    dump.append(BYTE2CHAR[buf2.getUnsignedByte(j3)]);
                }
                dump.append(BYTEPADDING[remainder]);
                dump.append('|');
            }
            dump.append(StringUtil.NEWLINE + "+--------+-------------------------------------------------+----------------+");
        }

        private static void appendHexDumpRowPrefix(StringBuilder dump, int row, int rowStartIndex) {
            if (row < HEXDUMP_ROWPREFIXES.length) {
                dump.append(HEXDUMP_ROWPREFIXES[row]);
            } else {
                dump.append(StringUtil.NEWLINE);
                dump.append(Long.toHexString((long)rowStartIndex & 0xFFFFFFFFL | 0x100000000L));
                dump.setCharAt(dump.length() - 9, '|');
                dump.append('|');
            }
        }

        static {
            int j2;
            StringBuilder buf2;
            int i2;
            BYTE2CHAR = new char[256];
            HEXDUMP_TABLE = new char[1024];
            HEXPADDING = new String[16];
            HEXDUMP_ROWPREFIXES = new String[4096];
            BYTE2HEX = new String[256];
            BYTEPADDING = new String[16];
            char[] DIGITS = "0123456789abcdef".toCharArray();
            for (i2 = 0; i2 < 256; ++i2) {
                HexUtil.HEXDUMP_TABLE[i2 << 1] = DIGITS[i2 >>> 4 & 0xF];
                HexUtil.HEXDUMP_TABLE[(i2 << 1) + 1] = DIGITS[i2 & 0xF];
            }
            for (i2 = 0; i2 < HEXPADDING.length; ++i2) {
                int padding = HEXPADDING.length - i2;
                buf2 = new StringBuilder(padding * 3);
                for (j2 = 0; j2 < padding; ++j2) {
                    buf2.append("   ");
                }
                HexUtil.HEXPADDING[i2] = buf2.toString();
            }
            for (i2 = 0; i2 < HEXDUMP_ROWPREFIXES.length; ++i2) {
                StringBuilder buf3 = new StringBuilder(12);
                buf3.append(StringUtil.NEWLINE);
                buf3.append(Long.toHexString((long)(i2 << 4) & 0xFFFFFFFFL | 0x100000000L));
                buf3.setCharAt(buf3.length() - 9, '|');
                buf3.append('|');
                HexUtil.HEXDUMP_ROWPREFIXES[i2] = buf3.toString();
            }
            for (i2 = 0; i2 < BYTE2HEX.length; ++i2) {
                HexUtil.BYTE2HEX[i2] = ' ' + StringUtil.byteToHexStringPadded(i2);
            }
            for (i2 = 0; i2 < BYTEPADDING.length; ++i2) {
                int padding = BYTEPADDING.length - i2;
                buf2 = new StringBuilder(padding);
                for (j2 = 0; j2 < padding; ++j2) {
                    buf2.append(' ');
                }
                HexUtil.BYTEPADDING[i2] = buf2.toString();
            }
            for (i2 = 0; i2 < BYTE2CHAR.length; ++i2) {
                HexUtil.BYTE2CHAR[i2] = i2 <= 31 || i2 >= 127 ? 46 : (char)i2;
            }
        }
    }
}

