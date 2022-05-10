package io.netty.handler.codec;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.util.AsciiString;
import java.util.Map;

public final class AsciiHeadersEncoder {
    private final ByteBuf buf;
    private final SeparatorType separatorType;
    private final NewlineType newlineType;

    public AsciiHeadersEncoder(ByteBuf buf2) {
        this(buf2, SeparatorType.COLON_SPACE, NewlineType.CRLF);
    }

    public AsciiHeadersEncoder(ByteBuf buf2, SeparatorType separatorType, NewlineType newlineType) {
        if (buf2 == null) {
            throw new NullPointerException("buf");
        }
        if (separatorType == null) {
            throw new NullPointerException("separatorType");
        }
        if (newlineType == null) {
            throw new NullPointerException("newlineType");
        }
        this.buf = buf2;
        this.separatorType = separatorType;
        this.newlineType = newlineType;
    }

    public void encode(Map.Entry<CharSequence, CharSequence> entry) {
        CharSequence name = entry.getKey();
        CharSequence value = entry.getValue();
        ByteBuf buf2 = this.buf;
        int nameLen = name.length();
        int valueLen = value.length();
        int entryLen = nameLen + valueLen + 4;
        int offset = buf2.writerIndex();
        buf2.ensureWritable(entryLen);
        AsciiHeadersEncoder.writeAscii(buf2, offset, name, nameLen);
        offset += nameLen;
        switch (this.separatorType) {
            case COLON: {
                buf2.setByte(offset++, 58);
                break;
            }
            case COLON_SPACE: {
                buf2.setByte(offset++, 58);
                buf2.setByte(offset++, 32);
                break;
            }
            default: {
                throw new Error();
            }
        }
        AsciiHeadersEncoder.writeAscii(buf2, offset, value, valueLen);
        offset += valueLen;
        switch (this.newlineType) {
            case LF: {
                buf2.setByte(offset++, 10);
                break;
            }
            case CRLF: {
                buf2.setByte(offset++, 13);
                buf2.setByte(offset++, 10);
                break;
            }
            default: {
                throw new Error();
            }
        }
        buf2.writerIndex(offset);
    }

    private static void writeAscii(ByteBuf buf2, int offset, CharSequence value, int valueLen) {
        if (value instanceof AsciiString) {
            AsciiHeadersEncoder.writeAsciiString(buf2, offset, (AsciiString)value, valueLen);
        } else {
            AsciiHeadersEncoder.writeCharSequence(buf2, offset, value, valueLen);
        }
    }

    private static void writeAsciiString(ByteBuf buf2, int offset, AsciiString value, int valueLen) {
        ByteBufUtil.copy(value, 0, buf2, offset, valueLen);
    }

    private static void writeCharSequence(ByteBuf buf2, int offset, CharSequence value, int valueLen) {
        for (int i2 = 0; i2 < valueLen; ++i2) {
            buf2.setByte(offset++, AsciiHeadersEncoder.c2b(value.charAt(i2)));
        }
    }

    private static int c2b(char ch2) {
        return ch2 < '\u0100' ? (int)ch2 : 63;
    }

    public static enum NewlineType {
        LF,
        CRLF;

    }

    public static enum SeparatorType {
        COLON,
        COLON_SPACE;

    }
}

